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
package org.n52.sos.aquarius.harvest;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.proxy.harvest.TemporalHarvesterJob;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AquariusTemporalHarvesterJob extends AbstractAquariusHarvesterJob implements TemporalHarvesterJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusTemporalHarvesterJob.class);

    @Inject
    private AquariusTemporalUpdater updater;

    public AquariusTemporalHarvesterJob() {
        setTriggerAtStartup(isTriggerAtStartup());
    }

    @Override
    protected boolean process(JobExecutionContext context, AquariusConnector connector) {
        JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
        if (!mergedJobDataMap.containsKey(AquariusConstants.LAST_UPDATE_TIME)) {
            mergedJobDataMap.put(AquariusConstants.LAST_UPDATE_TIME, getLastUpdateTime(context));
        }
        DateTime now = DateTime.now();
        TemporalUpdateResponse response = updater.update(mergedJobDataMap, connector);
        context.getJobDetail()
                .getJobDataMap()
                .put(AquariusConstants.LAST_UPDATE_TIME, getNextTime(response.getNextToken(), now));
        return response.isUpdated();
    }

    private DateTime getNextTime(String nextToken, DateTime now) {
        if (nextToken != null && !nextToken.isEmpty()) {
            return new DateTime(nextToken);
        }
        return now;
    }

}
