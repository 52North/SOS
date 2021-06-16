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
package org.n52.sos.aquarius.requests;

import java.util.Map;

import org.joda.time.DateTime;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.proxy.request.AbstractGetRequest;

public abstract class AbstractGetTimeSeriesData extends AbstractGetRequest {

    private final String timeSeriesUniqueId;

    private DateTime queryFrom;

    private DateTime queryTo;

    private Boolean applyRounding;

    public AbstractGetTimeSeriesData(String timeSeriesUniqueId) {
        super();
        this.timeSeriesUniqueId = timeSeriesUniqueId;
    }

    @Override
    public Map<String, String> getQueryParameters() {
        Map<String, String> queryParameters = createMap();
        queryParameters.put(AquariusConstants.Parameters.TIME_SERIES_UNIQUE_ID, getTimeSeriesUniqueId());
        if (hasQueryFrom()) {
            queryParameters.put(AquariusConstants.Parameters.QUERY_FROM,
                    DateTimeHelper.formatDateTime2IsoString(getQueryFrom()));
        }
        if (hasQueryTo()) {
            queryParameters.put(AquariusConstants.Parameters.QUERY_TO,
                    DateTimeHelper.formatDateTime2IsoString(getQueryTo()));
        }
        if (hasApplyRounding()) {
            queryParameters.put(AquariusConstants.Parameters.APPLY_ROUNDING, getApplyRounding().toString());
        }
        return queryParameters;
    }

    public String getTimeSeriesUniqueId() {
        return timeSeriesUniqueId;
    }

    public DateTime getQueryFrom() {
        return queryFrom;
    }

    public AbstractGetTimeSeriesData setQueryFrom(DateTime queryFrom) {
        this.queryFrom = queryFrom;
        return this;
    }

    public boolean hasQueryFrom() {
        return getQueryFrom() != null;
    }

    public DateTime getQueryTo() {
        return queryTo;
    }

    public AbstractGetTimeSeriesData setQueryTo(DateTime queryTo) {
        this.queryTo = queryTo;
        return this;
    }

    public boolean hasQueryTo() {
        return getQueryTo() != null;
    }

    public Boolean getApplyRounding() {
        return applyRounding;
    }

    public AbstractGetTimeSeriesData setApplyRounding(Boolean applyRounding) {
        this.applyRounding = applyRounding ? null : applyRounding;
        return this;
    }

    private boolean hasApplyRounding() {
        return getApplyRounding() != null;
    }
}
