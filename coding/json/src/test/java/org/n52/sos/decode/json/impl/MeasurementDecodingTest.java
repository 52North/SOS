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
package org.n52.sos.decode.json.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.n52.sos.util.DateTimeHelper.parseIsoString2DateTime;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.sos.ConfiguredSettingsManager;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.decode.json.JSONDecodingException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosProcedureDescription;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.vividsolutions.jts.geom.Point;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class MeasurementDecodingTest {
    @ClassRule
    public static final ConfiguredSettingsManager csm = new ConfiguredSettingsManager();

    public static final String PROCEDURE = "http://52north.org/example/procedure/1";

    public static final String OBSERVED_PROPERTY = "http://52north.org/example/observedProperty/1";

    public static final String UNKNOWN_CODESPACE = "http://www.opengis.net/def/nil/OGC/0/unknown";

    public static final String FEATURE_NAME = "feature1";

    public static final String FEATURE_IDENTIFIER = "feature1";

    public static final String IDENTIFIER = "measurement1";

    private static JsonNode json;

    private static DateTime resultTime;

    private static DateTime validTimeEnd;

    private static DateTime validTimeStart;

    private static DateTime phenomenonTime;

    private ObservationDecoder decoder;

    private OmObservation observation;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        try {
            resultTime = parseIsoString2DateTime("2013-01-01T00:00:00+02:00");
            validTimeStart = parseIsoString2DateTime("2013-01-01T00:00:00+02:00");
            validTimeEnd = parseIsoString2DateTime("2013-01-01T01:00:00+02:00");
            phenomenonTime = parseIsoString2DateTime("2013-01-01T00:00:00+02:00");
            json = JsonLoader.fromResource("/examples/measurement-geometry-inline.json");
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    @Before
    public void before() throws OwsExceptionReport {
        this.decoder = new ObservationDecoder();
        this.observation = decoder.decodeJSON(json, true);
    }

    @Test
    public void testObservation() {
        assertThat(observation, is(notNullValue()));
    }

    @Test
    public void testResultTime() {
        assertThat(observation, is(notNullValue()));
        final TimeInstant rt = observation.getResultTime();
        assertThat(rt, is(notNullValue()));
        assertThat(rt.getValue(), is(equalTo(resultTime)));
    }

    @Test
    public void testIdentifier() {
        assertThat(observation, is(notNullValue()));
        final CodeWithAuthority cwa = observation.getIdentifierCodeWithAuthority();
        assertThat(cwa, is(notNullValue()));
        assertThat(cwa.getValue(), is(equalTo(IDENTIFIER)));
        assertThat(cwa.getCodeSpace(), is(equalTo(UNKNOWN_CODESPACE)));
    }

    @Test
    public void testPhenomenonTime() {
        assertThat(observation, is(notNullValue()));
        final ObservationValue<?> ov = observation.getValue();
        assertThat(ov, is(notNullValue()));
        final Time pt = ov.getPhenomenonTime();
        assertThat(pt, is(notNullValue()));
        assertThat(pt, is(instanceOf(TimeInstant.class)));
        TimeInstant ti = (TimeInstant) pt;
        assertThat(ti.getValue(), is(equalTo(phenomenonTime)));
    }

    @Test
    public void testValue() {
        assertThat(observation, is(notNullValue()));
        final ObservationValue<?> ov = observation.getValue();
        assertThat(ov, is(notNullValue()));
        assertThat(ov.getValue(), is(instanceOf(QuantityValue.class)));
        QuantityValue qv = (QuantityValue) ov.getValue();
        assertThat(qv.getUnit(), is(equalTo("testunit1")));
        assertThat(qv.getValue(), is(equalTo(new Double("123123"))));
    }

    @Test
    public void testValidTime() {
        assertThat(observation, is(notNullValue()));
        final TimePeriod vt = observation.getValidTime();
        assertThat(vt, is(notNullValue()));
        assertThat(vt.getStart(), is(equalTo(validTimeStart)));
        assertThat(vt.getEnd(), is(equalTo(validTimeEnd)));
    }

    @Test
    public void testObservationConstellation() {
        assertThat(observation, is(notNullValue()));
        final OmObservationConstellation oc = observation.getObservationConstellation();
        assertThat(oc, is(notNullValue()));
    }

    @Test
    public void testObservationType() {
        assertThat(observation, is(notNullValue()));
        final OmObservationConstellation oc = observation.getObservationConstellation();
        assertThat(oc, is(notNullValue()));
        assertThat(oc.getObservationType(), is(equalTo(OmConstants.OBS_TYPE_MEASUREMENT)));
    }

    @Test
    public void testProcedure() {
        assertThat(observation, is(notNullValue()));
        final OmObservationConstellation oc = observation.getObservationConstellation();
        assertThat(oc, is(notNullValue()));
        final SosProcedureDescription p = oc.getProcedure();
        assertThat(p, is(notNullValue()));
        assertThat(p.getIdentifier(), is(equalTo(PROCEDURE)));
    }

    @Test
    public void testObservedProperty() {
        assertThat(observation, is(notNullValue()));
        final OmObservationConstellation oc = observation.getObservationConstellation();
        assertThat(oc, is(notNullValue()));
        final AbstractPhenomenon op = oc.getObservableProperty();
        assertThat(op, is(notNullValue()));
        assertThat(op.getIdentifier(), is(equalTo(OBSERVED_PROPERTY)));
    }

    @Test
    public void testFeatureOfInterest() {
        assertThat(observation, is(notNullValue()));
        final OmObservationConstellation oc = observation.getObservationConstellation();
        assertThat(oc, is(notNullValue()));
        final AbstractFeature foi = oc.getFeatureOfInterest();
        assertThat(foi, is(notNullValue()));
    }

    @Test
    public void testFeatureOfInterestSampledFeatures() {
        assertThat(observation, is(notNullValue()));
        final OmObservationConstellation oc = observation.getObservationConstellation();
        assertThat(oc, is(notNullValue()));
        final AbstractFeature foi = oc.getFeatureOfInterest();
        assertThat(foi, is(notNullValue()));
        assertThat(foi, is(instanceOf(SamplingFeature.class)));
        SamplingFeature sf = (SamplingFeature) foi;
        assertThat(sf.getSampledFeatures(), is(notNullValue()));
        assertThat(sf.getSampledFeatures().size(), is(3));

        final AbstractFeature af1 = sf.getSampledFeatures().get(0);
        assertThat(af1, is(notNullValue()));
        assertThat(af1.getIdentifierCodeWithAuthority(), is(notNullValue()));
        assertThat(af1.getIdentifierCodeWithAuthority().getValue(), is(equalTo("sampledFeature1")));

        final AbstractFeature af2 = sf.getSampledFeatures().get(1);
        assertThat(af2, is(notNullValue()));
        assertThat(af2.getIdentifierCodeWithAuthority(), is(notNullValue()));
        assertThat(af2.getIdentifierCodeWithAuthority().getValue(), is(equalTo("sampledFeature2")));
        assertThat(af2.getName(), is(notNullValue()));
        assertThat(af2.getName().size(), is(1));
        assertThat(af2.getName().get(0), is(not(nullValue())));
        assertThat(af2.getName().get(0).getCodeSpace(), is(equalTo(UNKNOWN_CODESPACE)));
        assertThat(af2.getName().get(0).getValue(), is(equalTo("sampledFeature2")));
        assertThat(af2, is(instanceOf(SamplingFeature.class)));
        SamplingFeature sf2 = (SamplingFeature) af2;
        assertThat(sf2.getGeometry(), is(notNullValue()));
        assertThat(sf2.getGeometry().getCoordinate().x, is(51.0));
        assertThat(sf2.getGeometry().getCoordinate().y, is(8.0));

        final AbstractFeature af3 = sf.getSampledFeatures().get(2);
        assertThat(af3, is(notNullValue()));
        assertThat(af3.getIdentifierCodeWithAuthority(), is(notNullValue()));
        assertThat(af3.getIdentifierCodeWithAuthority().getValue(), is(equalTo("sampledFeature3")));
    }

    @Test
    public void testFeatureOfInterestIdentifier() {
        assertThat(observation, is(notNullValue()));
        final OmObservationConstellation oc = observation.getObservationConstellation();
        assertThat(oc, is(notNullValue()));
        final AbstractFeature foi = oc.getFeatureOfInterest();
        assertThat(foi, is(notNullValue()));
        assertThat(foi.getIdentifierCodeWithAuthority(), is(notNullValue()));
        assertThat(foi.getIdentifierCodeWithAuthority().getCodeSpace(), is(equalTo(UNKNOWN_CODESPACE)));
        assertThat(foi.getIdentifierCodeWithAuthority().getValue(), is(equalTo(FEATURE_IDENTIFIER)));
    }

    @Test
    public void testFeatureOfInterestName() {
        assertThat(observation, is(notNullValue()));
        final OmObservationConstellation oc = observation.getObservationConstellation();
        assertThat(oc, is(notNullValue()));
        final AbstractFeature foi = oc.getFeatureOfInterest();
        assertThat(foi, is(notNullValue()));
        final List<CodeType> name = foi.getName();
        assertThat(name, is(notNullValue()));
        assertThat(name.size(), is(3));
        assertThat(name.get(0), is(notNullValue()));
        assertThat(name.get(0).getValue(), is(equalTo(FEATURE_NAME)));
        assertThat(name.get(0).getCodeSpace(), is(equalTo("http://x.y/z")));
        assertThat(name.get(1), is(notNullValue()));
        assertThat(name.get(1).getValue(), is(equalTo("othername1")));
        assertThat(name.get(1).getCodeSpace(), is(equalTo(UNKNOWN_CODESPACE)));
        assertThat(name.get(2), is(notNullValue()));
        assertThat(name.get(2).getValue(), is(equalTo("othername2")));
        assertThat(name.get(2).getCodeSpace(), is(equalTo(UNKNOWN_CODESPACE)));
    }

    @Test
    public void testFeatureOfInterestGeometry() {
        assertThat(observation, is(notNullValue()));
        final OmObservationConstellation oc = observation.getObservationConstellation();
        assertThat(oc, is(notNullValue()));
        assertThat(oc.getFeatureOfInterest(), is(notNullValue()));
        assertThat(oc.getFeatureOfInterest(), is(instanceOf(SamplingFeature.class)));
        SamplingFeature foi = (SamplingFeature) oc.getFeatureOfInterest();
        assertThat(foi.getGeometry(), is(notNullValue()));
        assertThat(foi.getGeometry(), is(instanceOf(Point.class)));
        assertThat(foi.getGeometry().getCoordinate().x, is(52.0));
        assertThat(foi.getGeometry().getCoordinate().y, is(7.0));
    }

    @Test
    public void testNull() throws OwsExceptionReport {
        assertThat(decoder.decode(null), is(nullValue()));
    }

    @Test
    public void testUnknownObservationType() throws OwsExceptionReport {
        final String type = "someType";
        final ObjectNode c = json.deepCopy();
        c.put(JSONConstants.TYPE, type);
        thrown.expect(JSONDecodingException.class);
        thrown.expectMessage(is("Unsupported observationType: " + type));
        decoder.decode(c);
    }
}
