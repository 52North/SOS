/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.cache.ctrl.persistence;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.GroupedAndNamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Autermann
 */
@Configurable
public class AsyncCachePersistenceStrategy
        extends AbstractPersistingCachePersistenceStrategy {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncCachePersistenceStrategy.class);

    private static final TimeUnit WRITE_DELAY_UNITS = TimeUnit.SECONDS;
    private long writeDelay
            = AsyncCachePersistenceStrategySettings.CACHE_PERSISTENCE_DELAY_DEFINITION
            .getDefaultValue();
    private final ScheduledExecutorService executor = Executors
            .newSingleThreadScheduledExecutor(
                    new GroupedAndNamedThreadFactory("cache-persister"));
    private final AtomicReference<ContentCache> cacheReference
            = new AtomicReference<ContentCache>();
    private Updater updater;

    public AsyncCachePersistenceStrategy() {
        this(null);
    }

    public AsyncCachePersistenceStrategy(File cacheFile) {
        super(cacheFile);
        updater = new Updater();
        this.executor.schedule(updater, writeDelay, WRITE_DELAY_UNITS);
    }

    @Setting(AsyncCachePersistenceStrategySettings.CACHE_PERSISTENCE_DELAY)
    public void setDelay(int delay) {
        if (delay <= 1) {
            throw new ConfigurationException("The write delay has be greater than 1 second.");
        }
        this.writeDelay = delay;
    }

    @Override
    public void persistOnPartialUpdate(ContentCache cache) {
        this.cacheReference.set(cache);
    }

    @Override
    public void persistOnCompleteUpdate(ContentCache cache) {
        this.cacheReference.set(cache);
    }

    @Override
    public void persistOnShutdown(ContentCache cache) {
        updater.setReschedule(false);
        this.executor.shutdown();
        try {
            this.executor.awaitTermination(writeDelay, WRITE_DELAY_UNITS);
        } catch (InterruptedException ie) {
            LOGGER.debug("Executor awaitTermination() was interrupted!", ie);
        }
        this.cacheReference.set(null);
        persistCache(cache);
    }

private class Updater implements Runnable {
        
        private boolean reschedule = true;
        
        /**
         * @return the reschedule
         */
        public boolean isReschedule() {
            return reschedule;
        }
        
        /**
         * @param reschedule the reschedule to set
         */
        public void setReschedule(boolean reschedule) {
            this.reschedule = reschedule;
        }
        
        @Override
        public void run() {
            ContentCache cache = cacheReference.getAndSet(null);
            if (cache != null) {
                persistCache(cache);
            }
            if (isReschedule()) {
                executor.schedule(this, writeDelay, WRITE_DELAY_UNITS);
            }
        }
    }

}
