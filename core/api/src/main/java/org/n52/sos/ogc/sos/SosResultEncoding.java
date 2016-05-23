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
package org.n52.sos.ogc.sos;

import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject.Factory;
import org.n52.sos.exception.ows.concrete.DecoderResponseUnsupportedException;
import org.n52.sos.exception.ows.concrete.XmlDecodingException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * @since 4.0.0
 * 
 */
public class SosResultEncoding {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosHelper.class);

    private String xml;

    private SweAbstractEncoding encoding;

    public SosResultEncoding() {
    }

    public SosResultEncoding(String resultEncoding) throws OwsExceptionReport {
        this.xml = resultEncoding;
        encoding = parseResultEncoding();
    }

    public String getXml() throws OwsExceptionReport {
        if (!isSetXml() && encoding != null) {
            if (encoding.isSetXml()) {
                setXml(encoding.getXml());
            } else {
                setXml(encodeResultEncoding());
            }
        }
        return xml;
    }

    public SosResultEncoding setEncoding(SweAbstractEncoding encoding) {
        this.encoding = encoding;
        return this;
    }

    public SweAbstractEncoding getEncoding() throws OwsExceptionReport {
        if (encoding == null && xml != null && !xml.isEmpty()) {
            encoding = parseResultEncoding();
        }
        return encoding;
    }

    public SosResultEncoding setXml(String xml) {
        this.xml = xml;
        return this;
    }

    private SweAbstractEncoding parseResultEncoding() throws OwsExceptionReport {
        try {
            Object decodedObject = CodingHelper.decodeXmlObject(Factory.parse(xml));
            if (decodedObject instanceof SweAbstractEncoding) {
                return (SweAbstractEncoding) decodedObject;
            } else {
                throw new DecoderResponseUnsupportedException(xml, decodedObject);
            }
        } catch (XmlException xmle) {
            throw new XmlDecodingException("resultEncoding", xml, xmle);
        }
    }

    private String encodeResultEncoding() throws OwsExceptionReport {
        Map<HelperValues, String> map = Maps.newEnumMap(HelperValues.class);
        map.put(HelperValues.DOCUMENT, null);
        return CodingHelper.encodeObjectToXmlText(SweConstants.NS_SWE_20, getEncoding(), map);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SosResultEncoding other = (SosResultEncoding) obj;
        try {
            if (this.getEncoding() != other.getEncoding()
                    && (this.getEncoding() == null || !this.getEncoding().equals(other.getEncoding()))) {
                return false;
            }
        } catch (OwsExceptionReport ex) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        try {
            return getEncoding().hashCode();
        } catch (OwsExceptionReport e) {
            LOGGER.error("Error while parsing resultStructure", e);
        }
        return super.hashCode();
    }

    public boolean isEmpty() {
        return StringHelper.isNotEmpty(xml);
    }

    public boolean isSetXml() {
        return StringHelper.isNotEmpty(this.xml);
    }

}
