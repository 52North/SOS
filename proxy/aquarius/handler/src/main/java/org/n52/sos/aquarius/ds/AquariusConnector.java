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
package org.n52.sos.aquarius.ds;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.n52.faroe.ConfigurationError;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.LocationDescriptions;
import org.n52.sos.aquarius.pojo.Parameters;
import org.n52.sos.aquarius.pojo.TimeSeriesData;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.pojo.TimeSeriesDescriptions;
import org.n52.sos.aquarius.requests.AbstractGetTimeSeriesData;
import org.n52.sos.aquarius.requests.GetLocationData;
import org.n52.sos.aquarius.requests.GetLocationDescriptionList;
import org.n52.sos.aquarius.requests.GetParameterList;
import org.n52.sos.aquarius.requests.GetTimeSeriesDescriptionList;
import org.n52.sos.proxy.Response;
import org.n52.sos.proxy.request.AbstractRequest;
import org.n52.sos.web.HttpClientHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

public class AquariusConnector implements AccessorConnector {

    private HttpClientHandler httpClientHandler;

    private ObjectMapper om = new ObjectMapper();

    private AquariusHelper aquariusHelper;

    private SessionHandler sessionHandler;

    public AquariusConnector(SessionHandler sessionHandler, HttpClientHandler httpClientHandler,
            AquariusHelper aquariusHelper) {
        this.httpClientHandler = httpClientHandler;
        this.aquariusHelper = aquariusHelper;
        this.sessionHandler = sessionHandler;
    }

    @Override
    public Set<String> getLocationDescriptions(GetLocationDescriptionList request) throws OwsExceptionReport {
        try {
            Response response = query(request);
            if (response.getEntity() != null) {
                LocationDescriptions locationDescriptions =
                        om.readValue(response.getEntity(), LocationDescriptions.class);
                if (locationDescriptions != null && locationDescriptions.hasLocationDesctiptions()) {
                    return locationDescriptions.getLocationDescriptions()
                            .stream()
                            .map(l -> l.getIdentifier())
                            .collect(Collectors.toSet());
                }
            }
            return Collections.emptySet();
        } catch (URISyntaxException | IOException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying locations");
        }
    }

    @Override
    public Location getLocation(GetLocationData request) throws OwsExceptionReport {
        try {
            Response response = query(request);
            return response.getEntity() != null ? om.readValue(response.getEntity(), Location.class) : null;
        } catch (URISyntaxException | IOException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying location data");
        }
    }

    public List<TimeSeriesDescription> getTimeSeriesDescriptions() throws OwsExceptionReport {
        return getTimeSeriesDescriptions(aquariusHelper.getGetTimeSeriesDescriptionListRequest());
    }

    @Override
    public List<TimeSeriesDescription> getTimeSeriesDescriptions(GetTimeSeriesDescriptionList request)
            throws OwsExceptionReport {
        try {
            Response response = query(request);
            if (response.getEntity() != null) {
                TimeSeriesDescriptions timeSeriesDescriptions =
                        om.readValue(response.getEntity(), TimeSeriesDescriptions.class);
                if (timeSeriesDescriptions.hasTimeSeriesDescriptions()) {
                    return timeSeriesDescriptions.getTimeSeriesDescriptions();
                }
            }
            return Collections.emptyList();
        } catch (URISyntaxException | IOException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying dataset data");
        }
    }

    @Override
    public Parameters getParameterList(GetParameterList request) throws OwsExceptionReport {
        try {
            Response response = query(request);
            return response.getEntity() != null ? om.readValue(response.getEntity(), Parameters.class)
                    : new Parameters();
        } catch (URISyntaxException | IOException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying parameter data");
        }
    }


    public TimeSeriesData getTimeSeriesData(String timeSeriesUniqueId) throws OwsExceptionReport {
        return getTimeSeriesData(aquariusHelper.getTimeSeriesDataRequest(timeSeriesUniqueId));
    }

    @Override
    public TimeSeriesData getTimeSeriesData(AbstractGetTimeSeriesData request) throws OwsExceptionReport {
        try {
            Response response = query(request);
            if (response.getEntity() != null) {
                TimeSeriesData timeSeriesData = om.readValue(response.getEntity(), TimeSeriesData.class);
                if (timeSeriesData != null && timeSeriesData.hasPoints()) {
                    return timeSeriesData;
                }
            }
            return null;
        } catch (URISyntaxException | IOException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying time series data!");
        }
    }

    @Override
    public TimeSeriesData getTimeSeriesDataFirstPoint(String timeSeriesUniqueId) throws OwsExceptionReport {
        try {
            DateTime dateTime = getQueryToForFirstTimeSeriesData(timeSeriesUniqueId);
            if (dateTime != null) {
                Response response = query(aquariusHelper.getTimeSeriesDataRequest(timeSeriesUniqueId)
                        .setQueryTo(dateTime));
                if (response.getEntity() != null) {
                    TimeSeriesData timeSeriesData = om.readValue(response.getEntity(), TimeSeriesData.class);
                    if (timeSeriesData.hasPoints()) {
                        return timeSeriesData;
                    }
                }
            }
            return null;
        } catch (URISyntaxException | IOException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying first time series data");
        }
    }

    @Override
    public TimeSeriesData getTimeSeriesDataLastPoint(String timeSeriesUniqueId) throws OwsExceptionReport {
        try {
            DateTime dateTime = getQueryFromForLastTimeSeriesData(timeSeriesUniqueId);
            if (dateTime != null) {
                Response response = query(aquariusHelper.getTimeSeriesDataRequest(timeSeriesUniqueId)
                        .setQueryFrom(dateTime));
                if (response.getEntity() != null) {
                    TimeSeriesData timeSeriesData = om.readValue(response.getEntity(), TimeSeriesData.class);
                    if (timeSeriesData.hasPoints()) {
                        return timeSeriesData;
                    }
                }
            }
            return null;
        } catch (URISyntaxException | IOException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying last time series data");
        }
    }

    private DateTime getQueryToForFirstTimeSeriesData(String timeSeriesUniqueId) {
        TimeSeriesDescription timeSeries = aquariusHelper.getDataset(timeSeriesUniqueId);
        if (timeSeries != null) {
            switch (aquariusHelper.getDataType()) {
                case CORRECTED:
                    return timeSeries.getCorrectedStartTime() != null
                            ? DateTimeHelper.parseIsoString2DateTime(timeSeries.getCorrectedStartTime())
                            : null;
                default:
                    return timeSeries.getRawStartTime() != null
                            ? DateTimeHelper.parseIsoString2DateTime(timeSeries.getRawStartTime())
                            : null;
            }
        }
        return null;
    }

    private DateTime getQueryFromForLastTimeSeriesData(String timeSeriesUniqueId) {
        TimeSeriesDescription timeSeries = aquariusHelper.getDataset(timeSeriesUniqueId);
        if (timeSeries != null) {
            switch (aquariusHelper.getDataType()) {
                case CORRECTED:
                    return timeSeries.getCorrectedEndTime() != null
                            ? DateTimeHelper.parseIsoString2DateTime(timeSeries.getCorrectedEndTime())
                            : null;
                default:
                    return timeSeries.getRawEndTime() != null
                            ? DateTimeHelper.parseIsoString2DateTime(timeSeries.getRawEndTime())
                            : null;
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
                sb.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append(";");
            }
            filter.put(AquariusConstants.FILTER, sb.toString()
                    .substring(0, sb.toString()
                            .length() - 1));
            return filter;
        }
        return new HashMap<>();
    }

    private Response query(AbstractRequest request) throws OwsExceptionReport, URISyntaxException {
        Response response = httpClientHandler.execute(getURL(sessionHandler.getSession()),
                checkTokenParameter(request, sessionHandler.getSession()));
        if (response != null && response.getStatus() == 401) {
            sessionHandler.keepAlive(sessionHandler.getSession());
            response = httpClientHandler.execute(getURL(sessionHandler.getSession()),
                    checkTokenParameter(request, sessionHandler.getSession()));
        }
        return response;
    }

    private AbstractRequest checkTokenParameter(AbstractRequest request, Session session) {
        if (session == null || Strings.isNullOrEmpty(session.getToken())) {
            String exceptionText = "Error when establishing a connection to Aquarius";
            if (session != null && session.getConnection() != null) {
                exceptionText = String.format("Error when establishing a connection to Aquarius for (%s, %s, %s)",
                        session.getConnection()
                                .getBasePath(),
                        session.getConnection()
                                .getUsername(),
                        session.getConnection()
                                .getPassword());
            }
            throw new ConfigurationError(exceptionText);
        }
        request.addHeader(AquariusConstants.HEADER_AQ_AUTH_TOKEN, session.getToken());
        return request;
    }

}
