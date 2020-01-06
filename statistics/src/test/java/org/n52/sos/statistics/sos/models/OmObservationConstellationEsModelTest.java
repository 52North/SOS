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
package org.n52.sos.statistics.sos.models;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.n52.iceland.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;

public class OmObservationConstellationEsModelTest {

    private static final String ID = "id";
    private static final String FOI = "foi";
    private static final String OBS_TYPE = "obstype";

    @Test
    public void validateAllFields() {
        OmObservationConstellation obs = new OmObservationConstellation();
        obs.setProcedure(new SosProcedureDescriptionUnknownType(ID, "format", "xml"));
        obs.setObservableProperty(new OmObservableProperty(ID, "desc", "unit", "value"));
        obs.setFeatureOfInterest(new OmObservation() {
            {
                setIdentifier(FOI);
            }
        });
        obs.setObservationType(OBS_TYPE);

        Map<String, Object> map = OmObservationConstellationEsModel.convert(obs);

        Assert.assertEquals(ID, map.get(ObjectEsParameterFactory.OMOCONSTELL_PROCEDURE.getName()));
        Assert.assertEquals(ID, map.get(ObjectEsParameterFactory.OMOCONSTELL_OBSERVABLE_PROPERTY.getName()));
        Assert.assertEquals(OBS_TYPE, map.get(ObjectEsParameterFactory.OMOCONSTELL_OBSERVATION_TYPE.getName()));
        Assert.assertEquals(FOI, map.get(ObjectEsParameterFactory.OMOCONSTELL_FEATURE_OF_INTEREST.getName()));
    }

    @Test
    public void nullInputValue() {
        Assert.assertNull(OmObservationConstellationEsModel.convert((OmObservationConstellation) null));
        Assert.assertNull(OmObservationConstellationEsModel.convert((List<OmObservationConstellation>) null));
    }
}
