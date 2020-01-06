/**
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
package org.n52.svalbard.inspire.omso.v30.encode;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.DefaultProfileHandler;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.svalbard.inspire.omso.InspireOMSOConstants;
import org.n52.svalbard.inspire.omso.TrajectoryObservation;

import com.vividsolutions.jts.geom.Geometry;

import eu.europa.ec.inspire.schemas.omso.x30.TrajectoryObservationType;

public class TrajectoryObservationTypeEncoderTest {
    
    private static final String PROCEDURE = "procedure";
    private static final String OFFERING = "offering";
    private static final String OBSERVABLE_PROPERTY  = "observableProperty";
    private static final String CODE_SPACE = "codespace";

    @Before
    public void init() {
        Configurator configurator = mock(Configurator.class);
        when(configurator.getProfileHandler()).thenReturn(new DefaultProfileHandler());
        Configurator.setInstance(configurator);
    }
    
    @Test
    public void test_multi_quantity() throws OwsExceptionReport {
        XmlObject encoded = CodingHelper.encodeObjectToXml(InspireOMSOConstants.NS_OMSO_30, getQuantityObservation());
        assertThat(XmlHelper.validateDocument(encoded), is(TRUE));
        System.out.println(encoded.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
        assertThat(encoded, instanceOf(TrajectoryObservationType.class));
    }
    
    @Test
    public void test_multi_count() throws OwsExceptionReport {
        XmlObject encoded = CodingHelper.encodeObjectToXml(InspireOMSOConstants.NS_OMSO_30, getCountObservation());
        assertThat(XmlHelper.validateDocument(encoded), is(TRUE));
        System.out.println(encoded.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
        assertThat(encoded, instanceOf(TrajectoryObservationType.class));
    }
    
    @Test
    public void test_multi_categoy() throws OwsExceptionReport {
        XmlObject encoded = CodingHelper.encodeObjectToXml(InspireOMSOConstants.NS_OMSO_30, getCategoricalObservation());
        assertThat(XmlHelper.validateDocument(encoded), is(TRUE));
        System.out.println(encoded.xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
        assertThat(encoded, instanceOf(TrajectoryObservationType.class));
    }

    private OmObservation createObservation() {
        DateTime now = new DateTime(DateTimeZone.UTC);
        TimeInstant resultTime = new TimeInstant(now);
        TrajectoryObservation observation = new TrajectoryObservation();
        observation.setObservationID("123");
        OmObservationConstellation observationConstellation = new OmObservationConstellation();
        observationConstellation
                .setFeatureOfInterest(new SamplingFeature(new CodeWithAuthority("feature", CODE_SPACE)));
        OmObservableProperty observableProperty = new OmObservableProperty(OBSERVABLE_PROPERTY);
        observationConstellation.setObservableProperty(observableProperty);
        observationConstellation.addOffering(OFFERING);
        SensorML procedure = new SensorML();
        procedure.setIdentifier(new CodeWithAuthority(PROCEDURE, CODE_SPACE));
        observationConstellation.setProcedure(procedure);
        observation.setObservationConstellation(observationConstellation);
        observation.setResultTime(resultTime);
        return observation;
    }
    
    private OmObservation getQuantityObservation() throws OwsExceptionReport {
        MultiObservationValues<List<TimeLocationValueTriple>> multiObservationValues = new MultiObservationValues<List<TimeLocationValueTriple>>();
        TLVTValue tlvtValue = new TLVTValue();
        tlvtValue.addValue(getTimeLocationValueTriple(new QuantityValue(15.6, "C")));
        tlvtValue.addValue(getTimeLocationValueTriple(new QuantityValue(16.5, "C")));
        tlvtValue.addValue(getTimeLocationValueTriple(new QuantityValue(17.6, "C")));
        tlvtValue.addValue(getTimeLocationValueTriple(new QuantityValue(18.7, "C")));
        multiObservationValues.setValue(tlvtValue);
        OmObservation observation = createObservation();
        observation.setValue(multiObservationValues);
        return observation;
    }

    private OmObservation getCountObservation() throws OwsExceptionReport {
        MultiObservationValues<List<TimeLocationValueTriple>> multiObservationValues = new MultiObservationValues<List<TimeLocationValueTriple>>();
        TLVTValue tlvtValue = new TLVTValue();
        tlvtValue.addValue(getTimeLocationValueTriple(new CountValue(15)));
        tlvtValue.addValue(getTimeLocationValueTriple(new CountValue(16)));
        tlvtValue.addValue(getTimeLocationValueTriple(new CountValue(17)));
        tlvtValue.addValue(getTimeLocationValueTriple(new CountValue(18)));
        multiObservationValues.setValue(tlvtValue);
        OmObservation observation = createObservation();
        observation.setValue(multiObservationValues);
        return observation;
    }

    private OmObservation getCategoricalObservation() throws OwsExceptionReport {
        MultiObservationValues<List<TimeLocationValueTriple>> multiObservationValues = new MultiObservationValues<List<TimeLocationValueTriple>>();
        TLVTValue tlvtValue = new TLVTValue();
        tlvtValue.addValue(getTimeLocationValueTriple(new CategoryValue("test_1", "test_voc")));
        tlvtValue.addValue(getTimeLocationValueTriple(new CategoryValue("test_1", "test_voc")));
        tlvtValue.addValue(getTimeLocationValueTriple(new CategoryValue("test_3", "test_voc")));
        tlvtValue.addValue(getTimeLocationValueTriple(new CategoryValue("test_4", "test_voc")));
        multiObservationValues.setValue(tlvtValue);
        OmObservation observation = createObservation();
        observation.setValue(multiObservationValues);
        return observation;
    }
    
    private TimeLocationValueTriple getTimeLocationValueTriple(Value<?> value) throws OwsExceptionReport {
        return new TimeLocationValueTriple(new TimeInstant(new DateTime()), value, getGeometry() );
    }
    
    private Geometry getGeometry() throws OwsExceptionReport {
        final String wktString =
                GeometryHandler.getInstance().getWktString("7.52", "52.7", 4326);
        return JTSHelper.createGeometryFromWKT(wktString, 4326);
    }

}
