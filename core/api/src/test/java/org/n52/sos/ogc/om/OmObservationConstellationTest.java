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
package org.n52.sos.ogc.om;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.series.wml.DefaultPointMetadata;
import org.n52.sos.ogc.series.wml.DefaultTVPMeasurementMetadata;
import org.n52.sos.ogc.series.wml.MeasurementTimeseriesMetadata;
import org.n52.sos.ogc.series.wml.Metadata;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.series.wml.WaterMLConstants;

public class OmObservationConstellationTest {

    private final String PROCEDURE_ID = "http://sensors.portdebarcelona.cat/def/weather/procedure";

    private final SosProcedureDescription PROCEDURE = new SensorML().setIdentifier(PROCEDURE_ID);

    private final String OFFERING = "http://sensors.portdebarcelona.cat/def/weather/offerings#10m";

    private final String FEATURE_1 = "http://sensors.portdebarcelona.cat/def/weather/features#03";

    private final String FEATURE_2 = "http://sensors.portdebarcelona.cat/def/weather/features#P3";

    private final String OBSERVABLE_PROPERTY_1 = "http://sensors.portdebarcelona.cat/def/weather/properties#31N";

    private final String OBSERVABLE_PROPERTY_2 = "http://sensors.portdebarcelona.cat/def/weather/properties#30M";

    private OmObservationConstellation getFirstObservationConstellation() {
        return new OmObservationConstellation().setProcedure(PROCEDURE).addOffering(OFFERING)
                .setFeatureOfInterest(new SamplingFeature(new CodeWithAuthority(FEATURE_1)))
                .setObservableProperty(new OmObservableProperty(OBSERVABLE_PROPERTY_1));

    }

    private OmObservationConstellation getSecondObservationConstellation() {
        return new OmObservationConstellation().setProcedure(PROCEDURE).addOffering(OFFERING)
                .setFeatureOfInterest(new SamplingFeature(new CodeWithAuthority(FEATURE_2)))
                .setObservableProperty(new OmObservableProperty(OBSERVABLE_PROPERTY_2));
    }

    @Test
    public void shouldNotBeEqualHashCode() {
        assertThat(getFirstObservationConstellation().hashCode(), not(getSecondObservationConstellation().hashCode()));
    }

    @Test
    public void testChecheckObservationTypeForMerging() {
        OmObservationConstellation ooc = new OmObservationConstellation();
        ooc.setObservationType(OmConstants.OBS_TYPE_MEASUREMENT);
        assertThat(ooc.checkObservationTypeForMerging(), is(true));
        ooc.setObservationType(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION);
        assertThat(ooc.checkObservationTypeForMerging(), is(true));
        ooc.setObservationType(OmConstants.OBS_TYPE_COUNT_OBSERVATION);
        assertThat(ooc.checkObservationTypeForMerging(), is(true));
        ooc.setObservationType(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION);
        assertThat(ooc.checkObservationTypeForMerging(), is(true));
        ooc.setObservationType(OmConstants.OBS_TYPE_TEXT_OBSERVATION);
        assertThat(ooc.checkObservationTypeForMerging(), is(true));
        ooc.setObservationType(OmConstants.OBS_TYPE_TRUTH_OBSERVATION);
        assertThat(ooc.checkObservationTypeForMerging(), is(true));
        ooc.setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        assertThat(ooc.checkObservationTypeForMerging(), is(false));
        ooc.setObservationType(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION);
        assertThat(ooc.checkObservationTypeForMerging(), is(false));
        ooc.setObservationType(OmConstants.OBS_TYPE_OBSERVATION);
        assertThat(ooc.checkObservationTypeForMerging(), is(false));
        ooc.setObservationType(OmConstants.OBS_TYPE_UNKNOWN);
        assertThat(ooc.checkObservationTypeForMerging(), is(false));

    }
    
    
    @Test
    public void shouldSetInterpolationType() {
        DefaultPointMetadata defaultPointMetadata = new DefaultPointMetadata();
        DefaultTVPMeasurementMetadata defaultTVPMeasurementMetadata = new DefaultTVPMeasurementMetadata();
        defaultTVPMeasurementMetadata.setInterpolationtype(WaterMLConstants.InterpolationType.Continuous);
        defaultPointMetadata.setDefaultTVPMeasurementMetadata(defaultTVPMeasurementMetadata);
        final OmObservationConstellation omObservationConstellation = new OmObservationConstellation();
        omObservationConstellation.setDefaultPointMetadata(defaultPointMetadata);
        Assert.assertThat(omObservationConstellation.isSetDefaultPointMetadata(), Is.is(true));
        Assert.assertThat(omObservationConstellation.getDefaultPointMetadata().isSetDefaultTVPMeasurementMetadata(), Is.is(true));
        Assert.assertThat(omObservationConstellation.getDefaultPointMetadata().getDefaultTVPMeasurementMetadata().isSetInterpolationType(), Is.is(true));
        Assert.assertThat(omObservationConstellation.getDefaultPointMetadata().getDefaultTVPMeasurementMetadata().getInterpolationtype(), Is.is(WaterMLConstants.InterpolationType.Continuous));
    }

    @Test
    public void shouldSetPropertyCumulative() {
        final OmObservationConstellation omObservationConstellation = new OmObservationConstellation();
        Metadata metadata = new Metadata();
        MeasurementTimeseriesMetadata timeseriesMetadata = new MeasurementTimeseriesMetadata();
        timeseriesMetadata.setCumulative(true);
        metadata.setTimeseriesmetadata(timeseriesMetadata);
        omObservationConstellation.setMetadata(metadata);

        Assert.assertThat(omObservationConstellation.isSetMetadata(), Is.is(true));
        Assert.assertThat(omObservationConstellation.getMetadata().isSetTimeseriesMetadata(), Is.is(true));
        Assert.assertThat(omObservationConstellation.getMetadata().getTimeseriesmetadata(), CoreMatchers.instanceOf(MeasurementTimeseriesMetadata.class));
        Assert.assertThat(((MeasurementTimeseriesMetadata)omObservationConstellation.getMetadata().getTimeseriesmetadata()).isCumulative(), Is.is(true));
    }

}
