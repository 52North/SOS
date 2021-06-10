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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.EnvelopeOrGeometry;
import org.n52.shetland.util.JTSHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.Parameter;
import org.n52.sos.aquarius.pojo.Parameters;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.pojo.data.Point;
import org.n52.sos.aquarius.requests.AbstractGetTimeSeriesData;
import org.n52.sos.aquarius.requests.GetLocationData;
import org.n52.sos.aquarius.requests.GetLocationDescriptionList;
import org.n52.sos.aquarius.requests.GetParameterList;
import org.n52.sos.aquarius.requests.GetTimeSeriesDescriptionList;

import com.google.common.collect.Lists;

public interface AccessorConnector {

    Set<String> getLocationDescriptions(GetLocationDescriptionList request) throws OwsExceptionReport;

    default List<Location> getLocations(Set<String> locationIdentifiers) throws OwsExceptionReport {
        List<Location> locations = new LinkedList<>();
        for (String locationIdentifier : locationIdentifiers) {
            Location location = getLocation(new GetLocationData(locationIdentifier));
            if (location != null) {
                locations.add(location);
            }
        }
        return locations;
    }

    default List<Location> getLocations(GetLocationDescriptionList request) throws OwsExceptionReport {
        return getLocations(getLocationDescriptions(request));
    }

    default List<Location> getLocations(Set<String> locationIdentifiers, SpatialFilter spatialFitler)
            throws OwsExceptionReport {
        List<Location> locations = new LinkedList<>();
        List<EnvelopeOrGeometry> envelopes = Lists.newArrayList(spatialFitler.getGeometry());
        for (Location location : getLocations(locationIdentifiers)) {
            if (featureIsInFilter(getGeomtery(location), envelopes)) {
                locations.add(location);
            }
        }
        return locations;
    }

    Location getLocation(GetLocationData request) throws OwsExceptionReport;

    default Location getLocation(String identifier) throws OwsExceptionReport {
        return getLocation(new GetLocationData(identifier));
    }

    List<TimeSeriesDescription> getTimeSeriesDescriptions() throws OwsExceptionReport;

    List<TimeSeriesDescription> getTimeSeriesDescriptions(GetTimeSeriesDescriptionList request)
            throws OwsExceptionReport;

    List<Point> getTimeSeriesData(AbstractGetTimeSeriesData request) throws OwsExceptionReport;

    Point getTimeSeriesDataFirstPoint(String timeSeriesUniqueId) throws OwsExceptionReport;

    Point getTimeSeriesDataLastPoint(String timeSeriesUniqueId) throws OwsExceptionReport;

    // getParameterList
    default Parameters getParameterList() throws OwsExceptionReport {
        return getParameterList(new GetParameterList());
    }

    Parameters getParameterList(GetParameterList request) throws OwsExceptionReport;

    default Parameter getParameter(String parameter) throws OwsExceptionReport {
        Parameters parameters = getParameterList();
        if (parameters != null && parameters.hasParameters()) {
            for (Parameter param : parameters.getParameters()) {
                if (param.getIdentifier()
                        .equalsIgnoreCase(parameter)) {
                    return param;
                }
            }
        }
        return null;
    }

    // // getUnitList
    // Set<Object> getUnitList() throws OwsExceptionReport;
    //
    // // getTimeseriesUnitqueIdList
    // Set<Object> getTimeseriesUnitqueIdList() throws OwsExceptionReport;
    //

    default Map<String, String> createFilterForLocationQuery(Map<String, String> parameter) {
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

    default boolean featureIsInFilter(Geometry geometry, List<EnvelopeOrGeometry> envelopes) {
        return geometry != null && !geometry.isEmpty() && envelopes.stream()
                .anyMatch(e -> (e.isGeometry() && e.getGeometry()
                        .isPresent()
                        && e.getGeometry()
                                .get()
                                .contains(geometry))
                        || (e.isEnvelope() && e.getEnvelope()
                                .isPresent()
                                && e.getEnvelope()
                                        .get()
                                        .toGeometry()
                                        .contains(geometry)));
    }

    default Geometry getGeomtery(final Location location) throws OwsExceptionReport {
        if (location.getLongitude() != null && location.getLatitude() != null) {
            try {
                final String wktString = getWktString(location.getLongitude(), location.getLatitude(), true);
                Geometry geom = JTSHelper.createGeometryFromWKT(wktString, location.getSrid()
                        .intValue());
                if (location.getElevation() != null) {
                    geom.getCoordinate().z = JavaHelper.asDouble(location.getElevation());
                }
                return geom;
            } catch (ParseException pe) {
                throw new NoApplicableCodeException().causedBy(pe);
            }
        }
        return null;
    }

    default String getWktString(Object latitude, Object longitude, boolean northingFirst) {
        StringBuilder builder = new StringBuilder();
        builder.append("POINT ")
                .append('(');

        if (northingFirst) {
            builder.append(JavaHelper.asString(latitude))
                    .append(' ');
            builder.append(JavaHelper.asString(longitude));
        } else {
            builder.append(JavaHelper.asString(longitude))
                    .append(' ');
            builder.append(JavaHelper.asString(latitude));
        }
        builder.append(')');
        return builder.toString();
    }

    default URI getURL(Session session) throws URISyntaxException {
        return getURL(session.getConnection()
                .getBasePath());
    }

    default URI getURL(String host) throws URISyntaxException {
        return new URI(host.startsWith("http") ? host : "http://" + host);
    }

}
