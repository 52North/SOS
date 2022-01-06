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

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.cache.ctrl.ScheduledContentCacheControllerSettings;
import org.quartz.SchedulerException;

@Configurable
public class HibernateDataSourceHarvestJobFactory {

    private String cronExpression;
    private HibernateDataSourceHarvesterScheduler scheduler;
    private Set<String> jobs = new HashSet<>();

    @Inject
    public void setHibernateDataSourceHarvesterScheduler(HibernateDataSourceHarvesterScheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * @return the updateDefinition
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * @param cronExpression the cronExpression to set
     */
    @Setting(ScheduledContentCacheControllerSettings.CAPABILITIES_CACHE_UPDATE)
    public void setCronExpression(String cronExpression) {
        Validation.notNullOrEmpty("Cron expression for cache update", cronExpression);
        if (this.cronExpression == null) {
            this.cronExpression = cronExpression;
            reschedule();
        } else if (!this.cronExpression.equalsIgnoreCase(cronExpression)) {
            this.cronExpression = cronExpression;
            reschedule();
        }
    }

    private void reschedule() {
        HibernateDataSourceHarvesterJob job = new HibernateDataSourceHarvesterJob();
        job.setEnabled(true);
        job.setCronExpression(getCronExpression());
        job.setTriggerAtStartup(true);
        if (jobs.contains(job.getJobName())) {
            try {
                scheduler.updateJob(job);
            } catch (SchedulerException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            scheduler.scheduleJob(job);
        }
        jobs.add(job.getJobName());
    }
}
