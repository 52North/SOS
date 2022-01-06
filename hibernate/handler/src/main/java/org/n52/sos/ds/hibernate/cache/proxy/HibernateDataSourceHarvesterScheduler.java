/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.cache.proxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.n52.io.task.ScheduledJob;
import org.n52.janmayen.lifecycle.Destroyable;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateDataSourceHarvesterScheduler implements Destroyable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateDataSourceHarvesterScheduler.class);

    private List<ScheduledJob> scheduledJobs = new ArrayList<>();

    private int startupDelayInSeconds = 5;

    private Scheduler scheduler;

    private boolean enabled = true;

    public void init() {
        if (!enabled) {
            LOGGER.info(
                    "Job schedular disabled. No jobs will be triggered. "
                    + "This is also true for particularly enabled jobs.");
            return;
        }

        for (ScheduledJob scheduledJob : scheduledJobs) {
            // LOGGER.info(dataSourceConfig.getItemName() + " " +
            // dataSourceConfig.getUrl());
            // HibernateDataSourceHarvesterJob dataSourceJob = new
            // HibernateDataSourceHarvesterJob();
            // dataSourceJob.init(dataSourceConfig);
            scheduleJob(scheduledJob);
        }

        try {
            scheduler.startDelayed(startupDelayInSeconds);
            LOGGER.info("Scheduler will start jobs in {}s ...", startupDelayInSeconds);
        } catch (SchedulerException e) {
            LOGGER.error("Could not start scheduler.", e);
        }
    }

    public void updateJob(ScheduledJob taskToSchedule) throws SchedulerException {
        JobDetail details = taskToSchedule.createJobDetails();
        Trigger trigger = taskToSchedule.createTrigger(details.getKey());
        Date nextExecution = scheduler.rescheduleJob(trigger.getKey(), trigger);
        LOGGER.debug("Rescheduled job '{}' will be executed at '{}'!", details.getKey(), new DateTime(nextExecution));
    }

    public void scheduleJob(ScheduledJob taskToSchedule) {
        try {
            JobDetail details = taskToSchedule.createJobDetails();
            Trigger trigger = taskToSchedule.createTrigger(details.getKey());
            Date nextExecution = scheduler.scheduleJob(details, trigger);
            LOGGER.debug("Schedule job '{}' will be executed at '{}'!", details.getKey(), new DateTime(nextExecution));
            if (taskToSchedule.isTriggerAtStartup()) {
                LOGGER.debug("Schedule job '{}' to run once at startup.", details.getKey());
                Trigger onceAtStartup = TriggerBuilder.newTrigger().withIdentity(details.getKey() + "_onceAtStartup")
                        .forJob(details.getKey()).build();
                Date startupExecution = scheduler.scheduleJob(onceAtStartup);
                LOGGER.debug("Schedule job '{}' will be executed on startup at '{}'!", details.getKey(),
                        new DateTime(startupExecution));
            }
        } catch (SchedulerException e) {
            LOGGER.warn("Could not schdule Job '{}'.", taskToSchedule.getJobName(), e);
        }
    }

    /**
     * Shuts down the task scheduler without waiting tasks to be finished.
     */
    public void shutdown() {
        try {
            scheduler.shutdown(true);
            LOGGER.info("Shutdown scheduler");
        } catch (SchedulerException e) {
            LOGGER.error("Could not scheduler.", e);
        }
    }

    @Override
    public void destroy() {
        shutdown();
    }

    public List<ScheduledJob> getScheduledJobs() {
        return scheduledJobs;
    }

    public void setScheduledJobs(List<ScheduledJob> scheduledJobs) {
        this.scheduledJobs.clear();
        if (scheduledJobs != null) {
            this.scheduledJobs.addAll(scheduledJobs);
        }
    }

    public int getStartupDelayInSeconds() {
        return startupDelayInSeconds;
    }

    public void setStartupDelayInSeconds(int startupDelayInSeconds) {
        this.startupDelayInSeconds = startupDelayInSeconds;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
