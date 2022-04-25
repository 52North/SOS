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
package org.n52.sos.proxy.harvest;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.io.task.ScheduledJob;
import org.n52.janmayen.lifecycle.Constructable;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class DataSourceHarvesterJobFactory implements Constructable {

    public static final String PROXY_FULL_HARVEST_UPDATE = "proxy.harvest.full";
    public static final String PROXY_TEMPORAL_HARVEST_UPDATE = "proxy.harvest.temporal";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceHarvesterJobFactory.class);
    private String cronFullExpression = "0 0 03 * * ?";
    private String cronTemporalExpression = "0 0/5 * * * ?";
    private DataSourceHarvesterScheduler scheduler;
    private Set<String> jobs = new HashSet<>();
    private List<AbstractHarvesterJob> scheduledJobs = new ArrayList<>();
    private boolean initialized;

    @Inject
    public void setDataSourceHarvesterScheduler(DataSourceHarvesterScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Inject
    public void setScheduledJobs(Optional<List<AbstractHarvesterJob>> scheduledJobs) {
        this.scheduledJobs.clear();
        if (scheduledJobs.isPresent()) {
            this.scheduledJobs.addAll(scheduledJobs.get());
        }
    }

    public List<AbstractHarvesterJob> getScheduledJobs() {
        return scheduledJobs;
    }

    /**
     * @return the updateDefinition
     */
    public String getFullCronExpression() {
        return cronFullExpression;
    }

    /**
     * @param updateDefinition
     *            the updateDefinition to set
     */
    @Setting(PROXY_FULL_HARVEST_UPDATE)
    public void setFullCronExpression(String cronExpression) {
        Validation.notNullOrEmpty("Cron expression for full update!", cronExpression);
        validate(cronExpression);
        if (this.cronFullExpression == null) {
            this.cronFullExpression = cronExpression;
            reschedule();
        } else if (!this.cronFullExpression.equalsIgnoreCase(cronExpression)) {
            this.cronFullExpression = cronExpression;
            reschedule();
        }
    }

    /**
     * @return the updateDefinition
     */
    public String getTemporalCronExpression() {
        return cronTemporalExpression;
    }

    /**
     * @param updateDefinition
     *            the updateDefinition to set
     */
    @Setting(PROXY_TEMPORAL_HARVEST_UPDATE)
    public void setTemporalCronExpression(String cronExpression) {
        Validation.notNullOrEmpty("Cron expression for temporal update!", cronExpression);
        validate(cronExpression);
        if (this.cronTemporalExpression == null) {
            this.cronTemporalExpression = cronExpression;
            reschedule();
        } else if (!this.cronTemporalExpression.equalsIgnoreCase(cronExpression)) {
            this.cronTemporalExpression = cronExpression;
            reschedule();
        }
    }

    private void reschedule() {
        reschedule(true);
    }

    private void reschedule(boolean update) {
        if (!initialized && !update || initialized && update) {
            for (ScheduledJob job : getScheduledJobs()) {
                if (jobs.contains(job.getJobName())) {
                    boolean updateJob = false;
                    if (job instanceof FullHarvesterJob) {
                        updateJob = checkCronExpression(job, getFullCronExpression());
                    } else if (job instanceof TemporalHarvesterJob) {
                        updateJob = checkCronExpression(job, getTemporalCronExpression());
                    }
                    if (updateJob) {
                        try {
                            scheduler.updateJob(job);
                        } catch (SchedulerException e) {
                            LOGGER.error("Error while updating a job!", e);
                        }
                    }
                } else {
                    if (job instanceof FullHarvesterJob) {
                        job.setCronExpression(getFullCronExpression());
                    } else if (job instanceof TemporalHarvesterJob) {
                        job.setCronExpression(getTemporalCronExpression());
                    }
                    scheduler.scheduleJob(job);
                }
                jobs.add(job.getJobName());
            }
        }
    }

    private boolean checkCronExpression(ScheduledJob job, String cronExpression) {
        if (job.getCronExpression() == null || job.getCronExpression() != null && !job.getCronExpression()
                .isEmpty() && !job.getCronExpression()
                        .equals(cronExpression)) {
            job.setCronExpression(cronExpression);
            return true;
        }
        return false;
    }

    private void validate(String cronExpression) {
        try {
            CronExpression.validateExpression(cronExpression);
        } catch (ParseException e) {
            throw new ConfigurationError(String.format(
                    "%s is invalid! Please check http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials"
                    + "/tutorial-lesson-06.html",
                    cronExpression));
        }
    }

    @Override
    public void init() {
        reschedule(false);
        this.initialized = true;
    }
}
