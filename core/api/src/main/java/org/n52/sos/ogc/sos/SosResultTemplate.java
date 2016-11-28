/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.util.CodingHelper;
import org.n52.svalbard.decode.exception.DecoderResponseUnsupportedException;
import org.n52.svalbard.decode.exception.DecodingException;

/**
 * @since 4.0.0
 *
 */
public class SosResultTemplate {

    private CodeWithAuthority identifier;
    private String xmlResultStructure;
    private String xmResultEncoding;
    private SweAbstractDataComponent resultStructure;
    private SweAbstractEncoding resultEncoding;

    public CodeWithAuthority getIdentifier() {
        return identifier;
    }

    public void setIdentifier(CodeWithAuthority identifier) {
        this.identifier = identifier;
    }

    public String getXmlResultStructure() {
        return xmlResultStructure;
    }

    public void setXmlResultStructure(String xmlResultStructure) {
        this.xmlResultStructure = xmlResultStructure;
    }
    public String getXmResultEncoding() {
        return xmResultEncoding;
    }

    public void setXmResultEncoding(String xmResultEncoding) {
        this.xmResultEncoding = xmResultEncoding;
    }
    public SweAbstractDataComponent getResultStructure() throws DecodingException {
        if (resultStructure == null) {
            this.resultStructure = parseResultStructure();
        }
        return resultStructure;
    }

    public void setResultStructure(SweAbstractDataComponent resultStructure) {
        this.resultStructure = resultStructure;
    }
    public SweAbstractEncoding getResultEncoding() throws DecodingException {
        if (resultEncoding == null) {
            this.resultEncoding = parseResultEncoding();
        }
        return resultEncoding;
    }

    public void setResultEncoding(SweAbstractEncoding resultEncoding) {
        this.resultEncoding = resultEncoding;
    }

    private SweAbstractDataComponent parseResultStructure() throws DecodingException {
        Object decodedObject = CodingHelper.decodeXmlObject(xmlResultStructure);
        if (decodedObject instanceof SweDataRecord) {
            return (SweDataRecord) decodedObject;
        }
        throw new DecoderResponseUnsupportedException(xmlResultStructure, decodedObject);
    }

    private SweAbstractEncoding parseResultEncoding() throws DecodingException {
        Object decodedObject = CodingHelper.decodeXmlObject(xmResultEncoding);
        if (decodedObject instanceof SweTextEncoding) {
            return (SweTextEncoding) decodedObject;
        }
        throw new DecoderResponseUnsupportedException(xmResultEncoding, decodedObject);
    }
}
