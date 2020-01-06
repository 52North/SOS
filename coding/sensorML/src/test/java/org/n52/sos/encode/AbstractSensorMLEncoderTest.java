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
package org.n52.sos.encode;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilitiesPredicates;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweText;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class AbstractSensorMLEncoderTest {
    
    private TestAbstractSensorMLEncoder encoder = new TestAbstractSensorMLEncoder();
    
    private final String CAPABILITIES_NAME = "featuresOfInterest";
    
    private final String DEFINITION = "http://www.opengis.net/def/featureOfInterest/identifier";
    
    private final String FIELD_NAME = "featureOfInterestID";
    
    private final String FEATURE_ID = "http://www.52north.org/test/featureOfInterest/1";
    
    
    @Test
    public void testMergeFeatureCapabilities() {
       AbstractProcess abstractProcess = getAbstractProcess();
       encoder.mergeCapabilities(abstractProcess, CAPABILITIES_NAME, DEFINITION, FIELD_NAME, Sets.newHashSet(getSweText("blabla", FEATURE_ID)));
       assertThat(abstractProcess.isSetCapabilities(), is(true));
       Optional<SmlCapabilities> capabilities =
               abstractProcess.findCapabilities(SmlCapabilitiesPredicates.name(CAPABILITIES_NAME));
       assertThat(capabilities.isPresent(), is(true));
       for (SweField field : capabilities.get().getDataRecord().getFields()) {
           assertThat(FIELD_NAME.equalsIgnoreCase(field.getName().getValue()), is(true));
           assertThat(DEFINITION.equalsIgnoreCase(((SweText)field.getElement()).getDefinition()), is(true));
           assertThat(FEATURE_ID.equalsIgnoreCase(((SweText)field.getElement()).getValue()), is(true));
       }
    }

    private AbstractProcess getAbstractProcess() {
        System system = new System();
        system.addCapabilities(getCapabilites());
        return system;
    }

    private SmlCapabilities getCapabilites() {
        SmlCapabilities caps = new SmlCapabilities(CAPABILITIES_NAME);
        caps.setDataRecord(getDataRecord());
        return caps;
    }

    private SweDataRecord getDataRecord() {
        SweDataRecord record = new SweDataRecord();
        SweField field = new SweField(FIELD_NAME, getSweText(FIELD_NAME, FEATURE_ID));
        record.addField(field);
        return record;
    }

    private SweText getSweText(String name, String value) {
        SweText sweText = new SweText();
        sweText.setValue(value);
        sweText.setName(name);
        return sweText;
    }
}
