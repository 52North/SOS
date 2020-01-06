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
package org.n52.sos.ds.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweTimeRange;

import com.google.common.collect.Sets;

public class InsertResultDAOTest
        extends HibernateTestCase {

    private static final String OBS_PROP_1 = "obsProp_1";
    private static final String OBS_PROP_2 = "obsProp_2";
    private static final String OBS_PROP_3 = "obsProp_3";
    private static final String TEST = "test";
    private static final String COMPLEX_OBSERVATION = "complexObservation";

    private InsertResultHandler insertResultDAO = new InsertResultHandler();

    @Test
    public void test_getIndexForObservedPropertyAndUnit_SimpleObservation()
            throws CodedException {
        final SweDataRecord record = createRecordWithSimpleObservation();
        final Map<Integer, String> observedProperties = new HashMap<Integer, String>(record.getFields().size() - 1);
        final Map<Integer, String> units = new HashMap<Integer, String>(record.getFields().size() - 1);
        final Map<Integer, String> featureOfInterests = new HashMap<Integer, String>(record.getFields().size() - 1);
        final Map<Integer, String> procedures = new HashMap<Integer, String>(record.getFields().size() - 1);
        insertResultDAO.getIndexFor(record, 0, observedProperties, units, featureOfInterests, procedures,
                Sets.newHashSet(0), null);
        assertThat(observedProperties.size(), is(1));
        assertThat(observedProperties.get(1).equals(OBS_PROP_1), is(true));
    }

    @Test
    public void test_getIndexForObservedPropertyAndUnit_ComplexObservation()
            throws CodedException {
        final SweDataRecord record = createRecordWithComplexObservation();
        final Map<Integer, String> observedProperties = new HashMap<Integer, String>(record.getFields().size() - 1);
        final Map<Integer, String> units = new HashMap<Integer, String>(record.getFields().size() - 1);
        final Map<Integer, String> featureOfInterests = new HashMap<Integer, String>(record.getFields().size() - 1);
        final Map<Integer, String> procedures = new HashMap<Integer, String>(record.getFields().size() - 1);
        insertResultDAO.getIndexFor(record, 0, observedProperties, units, featureOfInterests, procedures,
                Sets.newHashSet(0), null);
        assertThat(observedProperties.size(), is(3));
        assertThat(observedProperties.get(1).equals(OBS_PROP_1), is(true));
        assertThat(observedProperties.get(2).equals(OBS_PROP_2), is(true));
        assertThat(observedProperties.get(3).equals(OBS_PROP_3), is(true));
    }

    private SweDataRecord createRecord() {
        SweDataRecord record = new SweDataRecord();
        record.addField(new SweField("phenomenonTime",
                new SweTimeRange().setUom(TEST).setDefinition(OmConstants.PHENOMENON_TIME)));
        return record;
    }

    private SweDataRecord createRecordWithSimpleObservation() {
        SweDataRecord record = createRecord();
        record.addField(new SweField(OBS_PROP_1, new SweQuantity().setUom(TEST).setDefinition(OBS_PROP_1)));
        return record;
    }

    private SweDataRecord createRecordWithComplexObservation() {
        SweDataRecord record = createRecord();
        record.addField(new SweField(COMPLEX_OBSERVATION, createComplexObservationRecord()));
        return record;
    }

    private SweDataRecord createComplexObservationRecord() {
        SweDataRecord record = new SweDataRecord();
        record.setDefinition(COMPLEX_OBSERVATION);
        record.addField(new SweField(OBS_PROP_1, new SweQuantity().setUom(TEST).setDefinition(OBS_PROP_1)));
        record.addField(new SweField(OBS_PROP_2, new SweQuantity().setUom(TEST).setDefinition(OBS_PROP_2)));
        record.addField(new SweField(OBS_PROP_3, new SweQuantity().setUom(TEST).setDefinition(OBS_PROP_3)));
        return record;
    }
}
