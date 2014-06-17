/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.cache.ContentCacheUpdate;
import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.cache.ctrl.action.CompleteCacheUpdate;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;

/**
 * Classes that saves the cache state after each apply. Actual functionality is
 * delegated to subclasses.
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class ContentCacheControllerImpl extends AbstractSchedulingContentCacheController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentCacheControllerImpl.class);

    private static final String CACHE_FILE = "cache.tmp";

    private static final AtomicInteger COMPLETE_UPDATE_COUNT = new AtomicInteger(0);

    private static final AtomicInteger PARTIAL_UPDATE_COUNT = new AtomicInteger(0);

    private String cacheFile;

    private CompleteUpdate current = null;

    private CompleteUpdate next = null;

    private volatile WritableContentCache cache;

    private final ReentrantLock lock = new ReentrantLock();

    public ContentCacheControllerImpl() {
        loadOrCreateCache();
    }

    @Override
    public WritableContentCache getCache() {
        return this.cache;
    }

    protected File getCacheFile() {
        if (this.cacheFile == null) {
            this.cacheFile = new File(Configurator.getInstance().getBasePath(), CACHE_FILE).getAbsolutePath();
        }
        return new File(this.cacheFile);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        persistCache();
    }

    private void loadOrCreateCache() {
        File f = getCacheFile();
        if (f.exists() && f.canRead()) {
            LOGGER.debug("Reading cache from temp file '{}'", f.getAbsolutePath());
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new FileInputStream(f));
                setCache((WritableContentCache) in.readObject());
            } catch (IOException t) {
                LOGGER.error(String.format("Error reading cache file '%s'", f.getAbsolutePath()), t);
            } catch (ClassNotFoundException t) {
                LOGGER.error(String.format("Error reading cache file '%s'", f.getAbsolutePath()), t);
            } finally {
                IOUtils.closeQuietly(in);
            }
            f.delete();
        } else {
            LOGGER.debug("No cache temp file found at '{}'", f.getAbsolutePath());

            // cache file doesn't exist, try to load cache from datasource
            try {
                update();
            } catch (OwsExceptionReport e) {
                LOGGER.warn("Couldn't load cache from datasource, maybe the datasource isn't configured yet?");
            }
        }

        if (getCache() == null) {
            setCache(CacheFactory.getInstance().create());
        } else {
            setInitialized(true);
        }
    }

    private void persistCache() {
        File f = getCacheFile();
        if (!f.exists() || f.delete()) {
            ObjectOutputStream out = null;
            if (getCache() != null) {
                LOGGER.debug("Serializing cache to {}", f.getAbsolutePath());
                try {
                    if (f.createNewFile() && f.canWrite()) {
                        out = new ObjectOutputStream(new FileOutputStream(f));
                        out.writeObject(getCache());
                    } else {
                        LOGGER.error("Can not create writable file {}", f.getAbsolutePath());
                    }
                } catch (IOException t) {
                    LOGGER.error(String.format("Error serializing cache to '%s'", f.getAbsolutePath()), t);
                } finally {
                    IOUtils.closeQuietly(out);
                }
            }
        }
    }

    protected void setCache(WritableContentCache wcc) {
        this.cache = wcc;
    }

    @Override
    public void update(ContentCacheUpdate update) throws OwsExceptionReport {
        if (update != null) {
            if (update.isCompleteUpdate()) {
                executeComplete(new CompleteUpdate(update));
            } else {
                executePartial(new PartialUpdate(update));
            }
        } else {
            throw new IllegalArgumentException("update may not be null");
        }
    }

    private void runCurrent() throws OwsExceptionReport {
        LOGGER.trace("Starting update {}", this.current);
        this.current.execute();
        LOGGER.trace("Finished update {}", this.current);
        lock();
        try {
            persistCache();
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
            persistCache();
            if (this.current != null) {
                this.current.addUpdate(update);
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
                LOGGER.trace("{} waiting for {}", update, waitFor);
                waitFor.waitForCompletion();
                LOGGER.trace("{} stopped waiting for {}", update, waitFor);
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
            LOGGER.trace("{} waiting for {}", update, waitFor);
            waitFor.waitForCompletion();
            LOGGER.trace("{} stopped waiting for {}", update, waitFor);
        }
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
    public void update() throws OwsExceptionReport {
        update(new CompleteCacheUpdate());
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
            LOGGER.trace("Starting Update {}", getUpdate());
            getUpdate().reset();
            getUpdate().setCache(cache);
            getUpdate().execute();
            LOGGER.trace("Finished Update {}", getUpdate());
            if (getUpdate().failed()) {
                LOGGER.warn("Update failed!", getUpdate().getFailureCause());
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
                = new ConcurrentLinkedQueue<PartialUpdate>();

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

        void setState(State state) {
            ContentCacheControllerImpl.this.lock();
            try {
                lock();
                try {
                    LOGGER.debug("State change: {} -> {}", this.state, state);
                    this.state = state;
                } finally {
                    unlock();
                }
            } finally {
                ContentCacheControllerImpl.this.unlock();
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
            LOGGER.trace("Starting update {}", getUpdate());
            getUpdate().execute();
            LOGGER.trace("Finished update {}", getUpdate());
            lock();
            try {
                if (getUpdate().failed()) {
                    setState(State.FAILED);
                    LOGGER.warn("Update failed!", getUpdate().getFailureCause());
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
