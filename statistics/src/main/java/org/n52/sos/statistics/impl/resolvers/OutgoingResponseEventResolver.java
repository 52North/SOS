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
package org.n52.sos.statistics.impl.resolvers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.n52.iceland.event.events.OutgoingResponseEvent;
import org.n52.sos.statistics.api.AbstractElasticSearchDataHolder;
import org.n52.sos.statistics.api.ServiceEventDataMapping;
import org.n52.sos.statistics.api.interfaces.IStatisticsServiceEventResolver;

public class OutgoingResponseEventResolver extends AbstractElasticSearchDataHolder implements IStatisticsServiceEventResolver {

    private OutgoingResponseEvent response;

    @Override
    public Map<String, Object> resolve() {
        // Objects.requireNonNull(response);
        if (response == null) {
            return null;
        }
        put(ServiceEventDataMapping.ORE_EXEC_TIME, response.getElapsedTime());
        put(ServiceEventDataMapping.ORE_COUNT, response.getRequestNumber());
        if (response.getBytesWritten() != null) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put(ServiceEventDataMapping.ORE_BYTES_WRITTEN_BYTES, response.getBytesWritten());
            data.put(ServiceEventDataMapping.ORE_BYTES_WRITTEN_DISPLAY, FileUtils.byteCountToDisplaySize(response.getBytesWritten()));
        }

        return dataMap;
    }

    public OutgoingResponseEvent getResponse() {
        return response;
    }

    public void setResponse(OutgoingResponseEvent response) {
        this.response = response;
    }

}
