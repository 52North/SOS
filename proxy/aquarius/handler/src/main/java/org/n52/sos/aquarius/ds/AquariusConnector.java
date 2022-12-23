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
package org.n52.sos.aquarius.ds;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.aquarius.AquariusConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aquaticinformatics.aquarius.sdk.helpers.SdkServiceClient;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListByUniqueIdServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesUniqueIdListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesUniqueIdListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceResponse;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({ "EI_EXPOSE_REP2" })
public class AquariusConnector implements AccessorConnector, AquariusTimeHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusConnector.class);
    private AquariusHelper aquariusHelper;
    private ClientHandler clientHandler;

    public AquariusConnector(ClientHandler clientHandler, AquariusHelper aquariusHelper) {
        this.aquariusHelper = aquariusHelper;
        this.clientHandler = clientHandler;
    }

    @Override
    public Set<String> getLocationDescriptions(LocationDescriptionListServiceRequest request) {
        LocationDescriptionListServiceResponse response = getClient().get(request);
        if (response != null) {
            if (response.getLocationDescriptions() != null) {
                return response.getLocationDescriptions().stream().map(l -> l.getIdentifier())
                        .collect(Collectors.toSet());
            }
        }
        return Collections.emptySet();
    }

    @Override
    public LocationDataServiceResponse getLocation(LocationDataServiceRequest request) {
        return getClient().get(request);
    }

    public List<TimeSeriesDescription> getTimeSeriesDescriptions() throws OwsExceptionReport {
        return getTimeSeriesDescriptions(aquariusHelper.getGetTimeSeriesDescriptionListRequest());
    }

    @Override
    public List<TimeSeriesDescription> getTimeSeriesDescriptions(TimeSeriesDescriptionServiceRequest request)
            throws OwsExceptionReport {
        TimeSeriesDescriptionListServiceResponse respone = getClient().get(request);
        if (respone != null && respone.getTimeSeriesDescriptions() != null) {
            return respone.getTimeSeriesDescriptions();
        }
        return Collections.emptyList();
    }

    public TimeSeriesDescriptionListByUniqueIdServiceResponse getTimeSeriesDescriptionsByUniqueId(
            TimeSeriesDescriptionListByUniqueIdServiceRequest request) {
        return getClient().get(request);
    }

    @Override
    public TimeSeriesUniqueIdListServiceResponse getTimeSeriesUniqueIds(TimeSeriesUniqueIdListServiceRequest request) {
        return getClient().get(request);
    }

    @Override
    public ParameterListServiceResponse getParameterList(ParameterListServiceRequest request)
            throws OwsExceptionReport {
        return getClient().get(request);
    }

    @Override
    public UnitListServiceResponse getUnitList(UnitListServiceRequest request) throws OwsExceptionReport {
        return getClient().get(request);
    }

    @Override
    public GradeListServiceResponse getGradeList(GradeListServiceRequest request) throws OwsExceptionReport {
        return getClient().get(request);
    }

    @Override
    public QualifierListServiceResponse getQualifierList(QualifierListServiceRequest request)
            throws OwsExceptionReport {
        return getClient().get(request);
    }

    public TimeSeriesDataServiceResponse getTimeSeriesData(String timeSeriesUniqueId, DateTime queryFrom,
            DateTime queryTo) throws OwsExceptionReport {
        LOGGER.debug("Query TimeSeriesData for {} from {}/{}", timeSeriesUniqueId, queryFrom, queryTo);
        return getClient()
                .get(aquariusHelper.getTimeSeriesDataRequest(timeSeriesUniqueId, queryFrom, queryTo));
    }

    public TimeSeriesDataServiceResponse getTimeSeriesData(String timeSeriesUniqueId) throws OwsExceptionReport {
        return getTimeSeriesData(timeSeriesUniqueId, null, null);
    }

    @Override
    public TimeSeriesDataServiceResponse getTimeSeriesDataFirstPoint(String timeSeriesUniqueId)
            throws OwsExceptionReport {
        DateTime dateTime = getQueryToForFirstTimeSeriesData(timeSeriesUniqueId);
        if (dateTime != null) {
            return getClient()
                    .get(aquariusHelper.getTimeSeriesDataRequest(timeSeriesUniqueId, null, dateTime));
        }
        return null;
    }

    @Override
    public TimeSeriesDataServiceResponse getTimeSeriesDataLastPoint(String timeSeriesUniqueId)
            throws OwsExceptionReport {
        DateTime dateTime = getQueryFromForLastTimeSeriesData(timeSeriesUniqueId);
        if (dateTime != null) {
            return getClient()
                    .get(aquariusHelper.getTimeSeriesDataRequest(timeSeriesUniqueId, dateTime, null));
        }
        return null;
    }

    private DateTime getQueryToForFirstTimeSeriesData(String timeSeriesUniqueId) {
        TimeSeriesDescription timeSeries = aquariusHelper.getDataset(timeSeriesUniqueId);
        if (timeSeries != null) {
            switch (aquariusHelper.getDataType()) {
                case CORRECTED:
                    return timeSeries.getCorrectedStartTime() != null
                            ? toDateTime(timeSeries.getCorrectedStartTime())
                            : null;
                default:
                    return timeSeries.getRawStartTime() != null ? toDateTime(timeSeries.getRawStartTime()) : null;
            }
        }
        return null;
    }

    private DateTime getQueryFromForLastTimeSeriesData(String timeSeriesUniqueId) {
        TimeSeriesDescription timeSeries = aquariusHelper.getDataset(timeSeriesUniqueId);
        if (timeSeries != null) {
            switch (aquariusHelper.getDataType()) {
                case CORRECTED:
                    return timeSeries.getCorrectedEndTime() != null ? toDateTime(timeSeries.getCorrectedEndTime())
                            : null;
                default:
                    return timeSeries.getRawEndTime() != null ? toDateTime(timeSeries.getRawEndTime()) : null;
            }
        }
        return null;
    }

    @Override
    public Map<String, String> createFilterForLocationQuery(Map<String, String> parameter) {
        if (parameter != null && !parameter.isEmpty()) {
            HashMap<String, String> filter = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            for (Entry<String, String> entry : parameter.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
            }
            filter.put(AquariusConstants.FILTER, sb.toString().substring(0, sb.toString().length() - 1));
            return filter;
        }
        return new HashMap<>();
    }

    private SdkServiceClient getClient() {
        return clientHandler.getClient().Publish;
    }

}
