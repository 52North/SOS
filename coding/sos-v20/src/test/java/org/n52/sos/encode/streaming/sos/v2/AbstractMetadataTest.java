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
package org.n52.sos.encode.streaming.sos.v2;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.SweSimpleDataRecord;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.util.SweHelper;

import net.opengis.swe.x20.DataArrayPropertyType;
import net.opengis.swe.x20.DataArrayType;
import net.opengis.swe.x20.DataRecordType;

public abstract class AbstractMetadataTest {

    protected void checkMetadataResponse(XmlObject[] extensionArray) throws XmlException {
        assertThat(extensionArray != null, is(true));
        assertThat(extensionArray.length, is(1));
        XmlObject parse = XmlObject.Factory.parse(extensionArray[0].xmlText());
        assertThat(parse, instanceOf(DataArrayPropertyType.class));
        DataArrayPropertyType dad = (DataArrayPropertyType) parse;
        assertThat(dad.getDataArray1(), instanceOf(DataArrayType.class));
        DataArrayType dat = (DataArrayType) dad.getDataArray1();
        assertThat(dat.getElementType().isSetAbstractDataComponent(), is (true));
        assertThat(dat.getElementType().getAbstractDataComponent(), instanceOf(DataRecordType.class));
    }
    
    
    protected SwesExtension<SweDataArray> createExtension() {
        SweDataArray sweDataArray = new SweDataArray();
        sweDataArray.setElementCount(new SweCount().setValue(2));
        sweDataArray.setEncoding(new SweHelper().createTextEncoding(";", ",", "."));

        SweSimpleDataRecord dataRecord = new SweSimpleDataRecord();
        dataRecord.setDefinition("Components");
        dataRecord.addField(new SweField("test_id"));
        dataRecord.addField(new SweField("test_code"));
        dataRecord.addField(new SweField("test_desc"));
        sweDataArray.setElementType(dataRecord);

        LinkedList<List<String>> values = new LinkedList<List<String>>();
        List<String> blockOfTokens_1 = new LinkedList<>();
        blockOfTokens_1.add("1");
        blockOfTokens_1.add("code_1");
        blockOfTokens_1.add("desc_1");
        values.add(blockOfTokens_1);
        List<String> blockOfTokens_2 = new LinkedList<>();
        blockOfTokens_2.add("2");
        blockOfTokens_2.add("code_2");
        blockOfTokens_2.add("desc_2");
        values.add(blockOfTokens_2);

        sweDataArray.setValues(values);
        return new SwesExtensionImpl<SweDataArray>().setValue(sweDataArray).setIdentifier("test")
                .setDefinition("test");
    }
}
