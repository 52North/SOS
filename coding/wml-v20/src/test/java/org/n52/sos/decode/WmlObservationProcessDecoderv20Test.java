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
package org.n52.sos.decode;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.series.wml.ObservationProcess;

/**
 * Test class for {@link WmlObservationProcessDecoderv20}
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class WmlObservationProcessDecoderv20Test {

    WmlObservationProcessDecoderv20 decoder = new WmlObservationProcessDecoderv20();

    final String REF_OFFERING = "refOffering";

    final String TEXT_OFFERING = "textOffering";

    @Test
    public void testCheckForOffering() {
        ObservationProcess observationProcess = getObservationProcess();
        decoder.checkForOffering(observationProcess);
        assertThat(observationProcess.isSetOfferings(), is(true));
        assertThat(observationProcess.getOfferings().size(), is(2));
        for (SosOffering offering : observationProcess.getOfferings()) {
            assertThat(offering.getIdentifier(), anyOf(is(REF_OFFERING), is(TEXT_OFFERING)));
        }

    }

    private ObservationProcess getObservationProcess() {
        ObservationProcess op = new ObservationProcess();
        op.addParameter(getReferencedNamedValue());
        op.addParameter(getTextNamedValue());
        return op;
    }

    private NamedValue<ReferenceType> getReferencedNamedValue() {
        NamedValue<ReferenceType> nv = new NamedValue<ReferenceType>();
        nv.setName(getNameValueOfferingName());
        nv.setValue(new ReferenceValue(new ReferenceType(REF_OFFERING)));
        return nv;
    }

    private NamedValue<String> getTextNamedValue() {
        NamedValue<String> nv = new NamedValue<String>();
        nv.setName(getNameValueOfferingName());
        nv.setValue(new TextValue(TEXT_OFFERING));
        return nv;
    }

    private ReferenceType getNameValueOfferingName() {
        return new ReferenceType(SensorMLConstants.ELEMENT_NAME_OFFERINGS);
    }
}
