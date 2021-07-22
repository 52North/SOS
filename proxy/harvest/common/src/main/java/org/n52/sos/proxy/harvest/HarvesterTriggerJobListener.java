/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import java.util.LinkedHashSet;
import java.util.Set;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvesterTriggerJobListener implements JobListener, TriggerListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(HarvesterTriggerJobListener.class);
    private Set<String> fullHarvestingJobs = new LinkedHashSet<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        if (checkForFullHarvesterJob(context)) {
            fullHarvestingJobs.add(getGroup(context));
        }
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        LOGGER.debug("The job '{}' was vetoed!", getJobName(context));
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (checkForFullHarvesterJob(context)) {
           fullHarvestingJobs.remove(getGroup(context));
        }
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        return checkForTemporalHarvesterJob(context) && fullHarvestingJobs.contains(getGroup(context));
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        // TODO Auto-generated method stub
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
            CompletedExecutionInstruction triggerInstructionCode) {
        // TODO Auto-generated method stub
    }

    private String getGroup(JobExecutionContext context) {
        return context.getJobDetail().getKey().getGroup();
    }

    private String getJobName(JobExecutionContext context) {
        return context.getJobDetail().getKey().getName();
    }

    private boolean checkForFullHarvesterJob(JobExecutionContext context) {
        return context.getJobInstance() instanceof FullHarvesterJob;
    }

    private boolean checkForTemporalHarvesterJob(JobExecutionContext context) {
        return context.getJobInstance() instanceof TemporalHarvesterJob;
    }

}
