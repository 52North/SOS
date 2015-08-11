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
package org.n52.sos.statistics.sos.models;

import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.iceland.ogc.gml.time.TimePeriod;
import org.n52.iceland.ogc.om.OmConstants;
import org.n52.iceland.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.util.JTSHelper;

import com.vividsolutions.jts.geom.Geometry;

public class OmObservationEsModelTest {

    @SuppressWarnings("unchecked")
    @Test
    public void validateAllFields() throws OwsExceptionReport {
        OmObservation obs = new OmObservation();
        obs.setIdentifier("id");

        // constellation
        OmObservationConstellation constellation = new OmObservationConstellation();
        constellation.setProcedure(new SosProcedureDescriptionUnknowType("id", "format", "xml"));
        constellation.setObservableProperty(new OmObservableProperty("id", "desc", "unit", "value"));
        constellation.setFeatureOfInterest(new OmObservation() {
            {
                setIdentifier("foi");
            }
        });
        constellation.setObservationType("obstype");
        obs.setObservationConstellation(constellation);

        // result time
        // valid time
        obs.setValidTime(new TimePeriod(DateTime.now(), DateTime.now().plusHours(1)));
        obs.setResultTime(new TimeInstant(DateTime.now()));

        // pheomenon time
        SingleObservationValue<String> value = new SingleObservationValue<String>();
        value.setValue(new TextValue("anyadat"));
        value.setPhenomenonTime(new TimeInstant(DateTime.now()));
        obs.setValue(value);

        // spatial profile
        NamedValue<Geometry> spatial = new NamedValue<>();
        spatial.setName(new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY));
        GeometryValue geometryValue = new GeometryValue(JTSHelper.createGeometryFromWKT("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))", 4326));
        spatial.setValue(geometryValue);
        obs.addParameter(spatial);

        Map<String, Object> map = OmObservationEsModel.convert(obs);

        Assert.assertNotNull(map.get(ObjectEsParameterFactory.OMOBS_RESULT_TIME.getName()));
        Assert.assertNotNull(map.get(ObjectEsParameterFactory.OMOBS_VALID_TIME.getName()));
        Assert.assertNotNull(map.get(ObjectEsParameterFactory.OMOBS_PHENOMENON_TIME.getName()));

        Map<String, Object> constellationMap = (Map<String, Object>) map.get(ObjectEsParameterFactory.OMOBS_CONSTELLATION.getName());
        Assert.assertEquals(constellationMap.get(ObjectEsParameterFactory.OMOCONSTELL_PROCEDURE.getName()), "id");
        Assert.assertEquals(constellationMap.get(ObjectEsParameterFactory.OMOCONSTELL_OBSERVABLE_PROPERTY.getName()), "id");
        Assert.assertEquals(constellationMap.get(ObjectEsParameterFactory.OMOCONSTELL_OBSERVATION_TYPE.getName()), "obstype");
        Assert.assertEquals(constellationMap.get(ObjectEsParameterFactory.OMOCONSTELL_FEATURE_OF_INTEREST.getName()), "foi");

        Assert.assertNotNull(map.get(ObjectEsParameterFactory.OMOBS_SAMPLING_GEOMETRY.getName()));

    }
}
