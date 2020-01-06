/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package basetest;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.mockito.Mock;

import org.n52.iceland.statistics.api.interfaces.geolocation.IStatisticsLocationUtil;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.net.IPAddress;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequestContext;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;
import org.n52.shetland.util.JTSHelper;
import org.n52.svalbard.decode.exception.DecodingException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

public abstract class HandlerBaseTest extends MockitoBaseTest {

    protected static OwsServiceRequestContext requestContext;

    protected static SpatialFilter spatialFilter;

    protected static TemporalFilter temporalFilter;

    protected static OmObservation omObservation;

    protected static OmObservationConstellation omConstellation;

    private static final String ID = "id";

    private static final String WKT_POLYGON = "POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))";

    @Mock
    protected IStatisticsLocationUtil locationUtil;

    @BeforeClass
    public static void beforeClass() throws OwsExceptionReport, DecodingException, ParseException {
        requestContext = new OwsServiceRequestContext();
        requestContext.setContentType("application/json");
        requestContext.setAcceptType(Arrays.asList(new MediaType("*", "*")));
        requestContext.setIPAddress(new IPAddress("123.123.123.123"));

        Geometry geom = JTSHelper.createGeometryFromWKT(WKT_POLYGON, 4326);
        spatialFilter = new SpatialFilter(SpatialOperator.BBOX, geom, "value-ref");

        temporalFilter = new TemporalFilter(TimeOperator.TM_Equals, new TimeInstant(DateTime.now()), "nothing");

        createOmConstellation();
        createOmObservation();
    }

    private static void createOmConstellation() {
        // constellation
        omConstellation = new OmObservationConstellation();
        omConstellation.setProcedure(new SosProcedureDescriptionUnknownType(ID, "format", "xml"));
        omConstellation.setObservableProperty(new OmObservableProperty(ID, "desc", "unit", "value"));
        omConstellation.setFeatureOfInterest(new OmObservation() {
            {
                setIdentifier("foi");
            }
        });
        omConstellation.setObservationType("obstype");
    }

    private static void createOmObservation() throws OwsExceptionReport, DecodingException, ParseException {
        omObservation = new OmObservation();
        omObservation.setIdentifier(ID);

        omObservation.setObservationConstellation(omConstellation);

        // result time
        // valid time
        omObservation.setValidTime(new TimePeriod(DateTime.now(), DateTime.now().plusHours(1)));
        omObservation.setResultTime(new TimeInstant(DateTime.now()));

        // pheomenon time
        SingleObservationValue<String> value = new SingleObservationValue<String>();
        value.setValue(new TextValue("anyadat"));
        value.setPhenomenonTime(new TimeInstant(DateTime.now()));
        omObservation.setValue(value);

        // spatial profile
        NamedValue<Geometry> spatial = new NamedValue<>();
        spatial.setName(new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY));
        GeometryValue geometryValue = new GeometryValue(
                JTSHelper.createGeometryFromWKT(WKT_POLYGON, 4326));
        spatial.setValue(geometryValue);
        omObservation.addParameter(spatial);
    }
}
