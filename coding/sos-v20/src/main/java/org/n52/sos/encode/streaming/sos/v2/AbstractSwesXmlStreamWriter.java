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

import static org.n52.sos.util.CodingHelper.encodeObjectToXml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.exception.swes.SwesStreamingConstants;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.util.Constants;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.base.Strings;

import net.opengis.swes.x20.ExtensibleResponseType;

public abstract class AbstractSwesXmlStreamWriter<T> extends XmlStreamWriter<T> {

    protected void writeExtensions(SwesExtensions extensions) throws XMLStreamException, OwsExceptionReport {
        for (SwesExtension<?> extension : extensions.getExtensions()) {
            if (extension.getValue() instanceof SweAbstractDataComponent) {
                writeExtension((SweAbstractDataComponent) extension.getValue());
                writeNewLine();
            }
        }
    }
    
    protected void writeExtension(SweAbstractDataComponent sweAbstractDataComponent)
            throws OwsExceptionReport, XMLStreamException {
        Map<HelperValues, String> helperValues = new HashMap<>();
        helperValues.put(HelperValues.PROPERTY_TYPE, "true");
        XmlObject extension = encodeSwe(helperValues, sweAbstractDataComponent);
        if (extension.xmlText().contains(XML_FRAGMENT)) {
            XmlObject set =
                    ExtensibleResponseType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions())
                            .addNewExtension().set(extension);
            writeXmlObject(set, SwesStreamingConstants.QN_EXTENSION);
        } else {
            if (checkExtension(extension)) {
                QName name = extension.schemaType().getName();
                String prefix = name.getPrefix();
                if (Strings.isNullOrEmpty(prefix)) {
                    XmlCursor newCursor = extension.newCursor();
                    prefix = newCursor.prefixForNamespace(name.getNamespaceURI());
                    newCursor.setAttributeText(W3CConstants.QN_XSI_TYPE,
                            prefix + ":" + name.getLocalPart());
                    newCursor.dispose();
                }
                writeXmlObject(extension, SwesStreamingConstants.QN_EXTENSION);
            } else {
                start(SwesStreamingConstants.QN_EXTENSION);
                writeNewLine();
                writeXmlObject(extension, SwesStreamingConstants.QN_EXTENSION);
                writeNewLine();
                indent--;
                end(SwesStreamingConstants.QN_EXTENSION);
                indent++;
            }
        }
    }
    
    private boolean checkExtension(XmlObject extension) {
        if (extension.schemaType() != null) {
            SchemaType schemaType = extension.schemaType();
            if (schemaType.getName() != null) {
                QName name = schemaType.getName();
                if (name.getLocalPart() != null && name.getLocalPart().toLowerCase().contains("propertytype")) {
                    return true;
                }
            }
        }
        return false;
    }

    protected XmlObject encodeSwe(Map<HelperValues, String> helperValues, Object o) throws OwsExceptionReport {
        return encodeObjectToXml(SweConstants.NS_SWE_20, o, helperValues);
    }
    
    protected XmlObject encodeGml(Map<HelperValues, String> helperValues, Object o) throws OwsExceptionReport {
        return encodeObjectToXml(GmlConstants.NS_GML_32, o, helperValues);
    }

}
