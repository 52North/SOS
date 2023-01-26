/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesUniqueIdListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesUniqueIdListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.UnitMetadata;
import com.google.common.collect.Lists;

public interface AccessorConnector {

    Set<String> getLocationDescriptions(LocationDescriptionListServiceRequest request) throws OwsExceptionReport;

    default List<LocationDataServiceResponse> getLocations(Set<String> locationIdentifiers) throws OwsExceptionReport {
        List<LocationDataServiceResponse> locations = new LinkedList<>();
        for (String locationIdentifier : locationIdentifiers) {
            LocationDataServiceResponse location =
                    getLocation(new LocationDataServiceRequest().setLocationIdentifier(locationIdentifier));
            if (location != null) {
                locations.add(location);
            }
        }
        return locations;
    }

    default List<LocationDataServiceResponse> getLocations(Set<String> locationIdentifiers,
            SpatialFilter spatialFitler) throws OwsExceptionReport {
        List<LocationDataServiceResponse> locations = new LinkedList<>();
        List<EnvelopeOrGeometry> envelopes = Lists.newArrayList(spatialFitler.getGeometry());
        for (LocationDataServiceResponse location : getLocations(locationIdentifiers)) {
            if (featureIsInFilter(getGeomtery(location), envelopes)) {
                locations.add(location);
            }
        }
        return locations;
    }

    LocationDataServiceResponse getLocation(LocationDataServiceRequest request) throws OwsExceptionReport;

    default LocationDataServiceResponse getLocation(String identifier) throws OwsExceptionReport {
        return getLocation(new LocationDataServiceRequest().setLocationIdentifier(identifier));
    }

    TimeSeriesUniqueIdListServiceResponse getTimeSeriesUniqueIds(TimeSeriesUniqueIdListServiceRequest request);

    List<TimeSeriesDescription> getTimeSeriesDescriptions() throws OwsExceptionReport;

    List<TimeSeriesDescription> getTimeSeriesDescriptions(TimeSeriesDescriptionServiceRequest request)
            throws OwsExceptionReport;

    TimeSeriesDataServiceResponse getTimeSeriesDataFirstPoint(String timeSeriesUniqueId) throws OwsExceptionReport;

    TimeSeriesDataServiceResponse getTimeSeriesDataLastPoint(String timeSeriesUniqueId) throws OwsExceptionReport;

    // getParameterList
    default ParameterListServiceResponse getParameterList() throws OwsExceptionReport {
        return getParameterList(new ParameterListServiceRequest());
    }

    ParameterListServiceResponse getParameterList(ParameterListServiceRequest request) throws OwsExceptionReport;

    default ParameterMetadata getParameter(String parameter) throws OwsExceptionReport {
        ParameterListServiceResponse parameters = getParameterList();
        if (parameters != null && parameters.getParameters() != null) {
            for (ParameterMetadata param : parameters.getParameters()) {
                if (param.getIdentifier().equalsIgnoreCase(parameter)) {
                    return param;
                }
            }
        }
        return null;
    }

    // getUnitList
    default UnitListServiceResponse getUnitList() throws OwsExceptionReport {
        return getUnitList(new UnitListServiceRequest());
    }

    UnitListServiceResponse getUnitList(UnitListServiceRequest request) throws OwsExceptionReport;

    default UnitMetadata getUnit(String unit) throws OwsExceptionReport {
        UnitListServiceResponse units = getUnitList();
        if (units != null && units.getUnits() != null) {
            for (UnitMetadata u : units.getUnits()) {
                if (u.getIdentifier().equalsIgnoreCase(unit)) {
                    return u;
                }
            }
        }
        return null;
    }

    default GradeListServiceResponse getGradeList() throws OwsExceptionReport {
        return getGradeList(new GradeListServiceRequest());
    }

    GradeListServiceResponse getGradeList(GradeListServiceRequest request) throws OwsExceptionReport;

    default QualifierListServiceResponse getQualifierList() throws OwsExceptionReport {
        return getQualifierList(new QualifierListServiceRequest());
    }

    QualifierListServiceResponse getQualifierList(QualifierListServiceRequest request) throws OwsExceptionReport;

    default Map<String, String> createFilterForLocationQuery(Map<String, String> parameter) {
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

    default boolean featureIsInFilter(Geometry geometry, List<EnvelopeOrGeometry> envelopes) {
        return geometry != null && !geometry.isEmpty()
                && envelopes.stream().anyMatch(
                        e -> e.isGeometry() && e.getGeometry().isPresent() && e.getGeometry().get().contains(geometry)
                                || e.isEnvelope() && e.getEnvelope().isPresent()
                                        && e.getEnvelope().get().toGeometry().contains(geometry));
    }

    default Geometry getGeomtery(final LocationDataServiceResponse location) throws OwsExceptionReport {
        if (location.getLongitude() != null && location.getLatitude() != null) {
            try {
                final String wktString = getWktString(location.getLongitude(), location.getLatitude(), true);
                Geometry geom = JTSHelper.createGeometryFromWKT(wktString, location.getSrid().intValue());
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
        builder.append("POINT ").append('(');

        if (northingFirst) {
            builder.append(JavaHelper.asString(latitude)).append(' ');
            builder.append(JavaHelper.asString(longitude));
        } else {
            builder.append(JavaHelper.asString(longitude)).append(' ');
            builder.append(JavaHelper.asString(latitude));
        }
        builder.append(')');
        return builder.toString();
    }

    default URI getURL(String host) throws URISyntaxException {
        // new URIBuilder(host.startsWith("http") ? host : "http://" + host, StandardCharsets.UTF_8).build();
        return new URI(host.startsWith("http") ? host : "http://" + host);
    }

}
