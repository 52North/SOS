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
package org.n52.svalbard.ro.encode.streaming;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.RelatedOfferings;

public class RelatedOfferingXmlStreamWriterTest {
    
    private RelatedOfferingXmlStreamWriter writer = new RelatedOfferingXmlStreamWriter();
    
    @Test
    public void should_encode_relatedOfferings() throws XMLStreamException, OwsExceptionReport, XmlException {
        RelatedOfferings ro = new RelatedOfferings();
        ro.addValue("role_1", "offering_1");
        ro.addValue("role_2", "offering_2");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(ro, out);
        XmlObject.Factory.parse(new String(out.toByteArray()));
    }

}
