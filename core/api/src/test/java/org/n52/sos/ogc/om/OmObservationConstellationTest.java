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
package org.n52.sos.ogc.om;

import org.junit.Test;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sos.SosProcedureDescription;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

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

}
