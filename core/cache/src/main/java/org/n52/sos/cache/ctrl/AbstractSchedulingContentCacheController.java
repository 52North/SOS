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
package org.n52.sos.cache.ctrl;

import java.util.Timer;
import java.util.TimerTask;

import org.joda.time.DateTime;
import org.n52.sos.cache.ContentCacheController;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for capabilities cache controller implementations that
 * schedules a complete cache update at a configured interval.
 * 
 * @since 4.0.0
 */
@Configurable
public abstract class AbstractSchedulingContentCacheController implements ContentCacheController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSchedulingContentCacheController.class);

    private boolean initialized = false;

    private long updateInterval;

    private final Timer timer = new Timer("52n-sos-capabilities-cache-controller", true);

    private TimerTask current = null;

    /**
     * Starts a new timer task
     */
    private void schedule() {
        /*
         * Timers can not be rescheduled. To make the interval changeable
         * reschedule a new timer.
         */
        current = new UpdateTimerTask();
        long delay = getUpdateInterval();
        if (!isInitialized()) {
            delay = 1;
            setInitialized(true);
        }
        if (delay > 0) {
            LOGGER.info("Next CapabilitiesCacheUpdate in {}m: {}", delay / 60000,
                    new DateTime(System.currentTimeMillis() + delay));
            timer.schedule(current, delay);
        }
    }

    @Setting(ScheduledContentCacheControllerSettings.CAPABILITIES_CACHE_UPDATE_INTERVAL)
    public void setUpdateInterval(int interval) throws ConfigurationException {
        Validation.greaterEqualZero("Cache update interval", interval);
        if (this.updateInterval != interval) {
            this.updateInterval = interval;
            reschedule();
        }
    }

    private long getUpdateInterval() {
        return this.updateInterval * 60000;
    }

    /**
     * Stops the current task, if available and starts a new {@link TimerTask}.
     * 
     * @see #schedule()
     */
    private void reschedule() {
        cancelCurrent();
        schedule();
    }

    private void cancelCurrent() {
        if (this.current != null) {
            this.current.cancel();
            LOGGER.debug("Current {} canceled", UpdateTimerTask.class.getSimpleName());
        }
    }

    private void cancelTimer() {
        if (this.timer != null) {
            this.timer.cancel();
            LOGGER.debug("Cache Update timer canceled.");
        }
    }

    @Override
    public void cleanup() {
        cancelCurrent();
        cancelTimer();
    }

    @Override
    protected void finalize() throws Throwable {
        Throwable t = null;
        try {
            cleanup();
        } catch (Throwable e) {
            LOGGER.error("Could not finalize CapabilitiesCacheController! " + e.getMessage(), e);
            t = e;
            throw t;
        } finally {
            super.finalize();
        }

    }

    /**
     * @return the initialized
     */
    protected boolean isInitialized() {
        return initialized;
    }

    /**
     * @param initialized
     *            the initialized to set
     */
    protected void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    private class UpdateTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                update();
                LOGGER.info("Timertask: capabilities cache update successful!");
                schedule();
            } catch (OwsExceptionReport e) {
                LOGGER.error("Fatal error: Timertask couldn't update capabilities cache! Switch log level to DEBUG to get more details.");
                LOGGER.debug("Exception thrown", e);
            }
        }
    }
}
