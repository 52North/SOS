/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.cache.ctrl;
/*
 * Copyright 2015-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.cache.ContentCachePersistenceStrategy;
import org.n52.iceland.cache.ContentCacheUpdate;
import org.n52.iceland.cache.WritableContentCache;
import org.n52.iceland.cache.ctrl.CompleteCacheUpdateFactory;
import org.n52.iceland.cache.ctrl.ContentCacheFactory;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.janmayen.lifecycle.Destroyable;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.cache.AbstractStaticSosContentCache;
import org.n52.sos.cache.ContentCacheFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SosContentCacheControllerImpl implements ContentCacheController, Constructable, Destroyable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SosContentCacheControllerImpl.class);

    private static final AtomicInteger COMPLETE_UPDATE_COUNT = new AtomicInteger(0);
    private static final AtomicInteger PARTIAL_UPDATE_COUNT = new AtomicInteger(0);
    private static final String STARTING_UPDATE = "Starting update {}";
    private static final String FINISHED_UPDATE = "Finished update {}";
    private static final String UPDATE_FAILED = "Update failed!";
    private CompleteUpdate current;
    private CompleteUpdate next;
    private volatile WritableContentCache cache;
    private final ReentrantLock lock = new ReentrantLock();

    private ContentCachePersistenceStrategy persistenceStrategy;
    private ContentCacheFactory cacheFactory;
    private CompleteCacheUpdateFactory completeCacheUpdateFactory;

    @Inject
    public void setCacheFactory(ContentCacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    @Inject
    public void setPersistenceStrategy(ContentCachePersistenceStrategy persistenceStrategy) {
        this.persistenceStrategy = persistenceStrategy;
    }

    @Inject
    public void setCompleteCacheUpdateFactory(CompleteCacheUpdateFactory factory) {
        this.completeCacheUpdateFactory = factory;
    }

    @Override
    public void init() {
        loadOrCreateCache();
    }

    private void loadOrCreateCache() {
        Optional<WritableContentCache> optionalCache = persistenceStrategy.load();
        if (optionalCache.isPresent()) {
            setCache(optionalCache.get());
            if (getCache() instanceof AbstractStaticSosContentCache
                    && this.cacheFactory instanceof ContentCacheFactoryImpl) {
                ((AbstractStaticSosContentCache) getCache()).setSupportedTypeRepository(
                        ((ContentCacheFactoryImpl) this.cacheFactory).getSupportedTypeRepository());
            }
        } else {
            // cache file doesn't exist, try to load cache from datasource
            setCache(this.cacheFactory.get());
            try {
                update();
            } catch (OwsExceptionReport e) {
                LOGGER.warn("Couldn't load cache from datasource, maybe the datasource isn't configured yet?", e);
            }
        }
    }


    @Override
    public WritableContentCache getCache() {
        return this.cache;
    }

    protected void setCache(WritableContentCache wcc) {
        this.cache = wcc;
    }

    @Override
    public void destroy() {
        lock();
        try {
            persistenceStrategy.persistOnShutdown(getCache());
        } finally {
            unlock();
        }
    }

    @Override
    public void update(ContentCacheUpdate update) throws OwsExceptionReport {
        if (update != null) {
            try {
                if (update.isCompleteUpdate()) {
                    executeComplete(new CompleteUpdate(update));
                } else {
                    executePartial(new PartialUpdate(update));
                }
                cache.setLastUpdateTime(DateTime.now());
            } finally {
                current = null;
            }
        } else {
            throw new IllegalArgumentException("update may not be null");
        }
    }

    @Override
    public void update() throws OwsExceptionReport {
        update(this.completeCacheUpdateFactory.get());
    }

    private void runCurrent() throws OwsExceptionReport {
        LOGGER.trace(STARTING_UPDATE, this.current);
        this.current.execute();
        LOGGER.trace(FINISHED_UPDATE, this.current);
        lock();
        try {
            persistenceStrategy.persistOnCompleteUpdate(getCache());
            CompleteUpdate u = this.current;
            this.current = null;
            u.signalWaiting();
        } finally {
            unlock();
        }
    }

    private void executePartial(PartialUpdate update) throws OwsExceptionReport {
        update.execute(getCache());
        lock();
        try {
            if (this.current != null) {
                this.current.addUpdate(update);
            } else {
                persistenceStrategy.persistOnPartialUpdate(getCache());
            }
        } finally {
            unlock();
        }
    }

    private void executeComplete(CompleteUpdate update) throws OwsExceptionReport {
        boolean isCurrent = false;
        boolean isNext = false;
        CompleteUpdate waitFor = null;
        lock();
        try {
            if (current == null || current.isFinished()) {
                current = update;
                isCurrent = true;
            } else if (current.isNotYetStarted()) {
                waitFor = current;
            } else if (next == null || next.isFinished()) {
                next = update;
                waitFor = current;
                isNext = true;
            } else {
                waitFor = next;
            }
        } finally {
            unlock();
        }

        if (isCurrent) {
            runCurrent();
        } else if (isNext) {
            if (waitFor != null) {
                logAndWait(update, waitFor);
            }
            lock();
            try {
                current = next;
                next = null;
            } finally {
                unlock();
            }
            runCurrent();
        } else if (waitFor != null) {
            logAndWait(update, waitFor);
        }
    }

    private void logAndWait(CompleteUpdate update, CompleteUpdate waitFor) throws OwsExceptionReport {
        LOGGER.trace("{} waiting for {}", update, waitFor);
        waitFor.waitForCompletion();
        LOGGER.trace("{} stopped waiting for {}", update, waitFor);
    }

    private void lock() {
        lock.lock();
    }

    private void unlock() {
        lock.unlock();
    }

    @Override
    public boolean isUpdateInProgress() {
        return current != null;
    }

    @Override
    public ContentCachePersistenceStrategy getContentCachePersistenceStrategy() {
        return this.persistenceStrategy;
    }

    private enum State {
        WAITING, RUNNING, APPLYING_UPDATES, FINISHED, FAILED
    }

    private abstract class Update {
        private final ContentCacheUpdate update;

        Update(ContentCacheUpdate update) {
            this.update = update;
        }

        ContentCacheUpdate getUpdate() {
            return update;
        }
    }

    private class PartialUpdate extends Update {
        private final int nr = PARTIAL_UPDATE_COUNT.getAndIncrement();

        PartialUpdate(ContentCacheUpdate update) {
            super(update);
        }

        synchronized void execute(WritableContentCache cache) throws OwsExceptionReport {
            LOGGER.trace(STARTING_UPDATE, getUpdate());
            getUpdate().reset();
            getUpdate().setCache(cache);
            getUpdate().execute();
            LOGGER.trace(FINISHED_UPDATE, getUpdate());
            if (getUpdate().failed()) {
                LOGGER.warn(UPDATE_FAILED, getUpdate().getFailureCause());
                throw getUpdate().getFailureCause();
            }
        }

        @Override
        public String toString() {
            return String.format("PartialUpdate[#%d]", nr);
        }
    }

    private class CompleteUpdate extends Update {
        private final ConcurrentLinkedQueue<PartialUpdate> updates
                = new ConcurrentLinkedQueue<>();

        private final Lock lock = new ReentrantLock();
        private final Condition finished = lock.newCondition();
        private State state = State.WAITING;
        private final int nr = COMPLETE_UPDATE_COUNT.getAndIncrement();

        CompleteUpdate(ContentCacheUpdate update) {
            super(update);
        }

        void addUpdate(PartialUpdate update) {
            updates.offer(update);
        }

        State getState() {
            lock();
            try {
                return state;
            } finally {
                unlock();
            }
        }

        void setState(State state) {
            SosContentCacheControllerImpl.this.lock();
            try {
                lock();
                try {
                    LOGGER.debug("State change: {} -> {}", this.state, state);
                    this.state = state;
                } finally {
                    unlock();
                }
            } finally {
                SosContentCacheControllerImpl.this.unlock();
            }
        }

        boolean isFinished() {
            lock();
            try {
                return getState() == State.FINISHED || getState() == State.FAILED;
            } finally {
                unlock();
            }
        }

        boolean isNotYetStarted() {
            lock();
            try {
                return getState() == State.WAITING;
            } finally {
                unlock();
            }
        }

        void execute() throws OwsExceptionReport {
            setCache(execute(getCache()));
        }

        WritableContentCache execute(WritableContentCache cache) throws OwsExceptionReport {
            if (isFinished()) {
                throw new IllegalStateException("already finished");
            }
            setState(State.RUNNING);
            getUpdate().setCache(cache);
            LOGGER.trace(STARTING_UPDATE, getUpdate());
            getUpdate().execute();
            LOGGER.trace(FINISHED_UPDATE, getUpdate());
            lock();
            try {
                if (getUpdate().failed()) {
                    setState(State.FAILED);
                    LOGGER.warn(UPDATE_FAILED, getUpdate().getFailureCause());
                    throw getUpdate().getFailureCause();
                } else {
                    setState(State.APPLYING_UPDATES);
                    PartialUpdate pu;
                    WritableContentCache cc = getUpdate().getCache();
                    while ((pu = updates.poll()) != null) {
                        pu.execute(cc);
                    }
                    setState(State.FINISHED);
                    return cc;
                }
            } finally {
                unlock();
            }
        }

        void waitForCompletion() throws OwsExceptionReport {
            lock();
            try {
                while (!isFinished()) {
                    try {
                        finished.await();
                    } catch (InterruptedException ex) {
                        LOGGER.warn("Error while waiting for finishing!", ex);
                    }
                }
                if (getState() == State.FAILED) {
                    throw getUpdate().getFailureCause();
                }
            } finally {
                unlock();
            }
        }

        void signalWaiting() {
            lock();
            try {
                finished.signalAll();
            } finally {
                unlock();
            }
        }

        @Override
        public String toString() {
            return String.format("CompleteUpdate[#%d]", nr);
        }

        protected void unlock() {
            lock.unlock();
        }

        protected void lock() {
            lock.lock();
        }
    }
}
