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
package org.n52.sos.encode.sos.v2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.opengis.sos.x20.GetResultTemplateResponseDocument;
import net.opengis.sos.x20.GetResultTemplateResponseType;
import net.opengis.sos.x20.GetResultTemplateResponseType.ResultEncoding;
import net.opengis.sos.x20.GetResultTemplateResponseType.ResultStructure;
import net.opengis.swe.x20.DataRecordDocument;
import net.opengis.swe.x20.TextEncodingDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosResultEncoding;
import org.n52.sos.ogc.sos.SosResultStructure;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.GetResultTemplateResponse;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetResultTemplateResponseEncoder extends AbstractSosResponseEncoder<GetResultTemplateResponse> {
    public GetResultTemplateResponseEncoder() {
        super(Sos2Constants.Operations.GetResultTemplate.name(), GetResultTemplateResponse.class);
    }

    @Override
    protected XmlObject create(GetResultTemplateResponse response) throws OwsExceptionReport {
        GetResultTemplateResponseDocument doc = GetResultTemplateResponseDocument.Factory.newInstance(getXmlOptions());
        GetResultTemplateResponseType xbResponse = doc.addNewGetResultTemplateResponse();
        xbResponse.setResultEncoding(createResultEncoding(response.getResultEncoding()));
        xbResponse.setResultStructure(createResultStructure(response.getResultStructure()));
        return doc;
    }

    private ResultEncoding createResultEncoding(SosResultEncoding resultEncoding) throws OwsExceptionReport {
        // TODO move encoding to SWECommonEncoder
        final TextEncodingDocument xbEncoding;
        if (resultEncoding.isSetXml()) {
            try {
                xbEncoding = TextEncodingDocument.Factory.parse(resultEncoding.getXml());
            } catch (final XmlException e) {
                throw new NoApplicableCodeException().causedBy(e).withMessage(
                        "ResultEncoding element encoding is not supported!");
            }
        } else {
        	Map<HelperValues, String> helperValues = new HashMap<HelperValues, String>(1);
        	helperValues.put(HelperValues.DOCUMENT, null);
            XmlObject xml = encodeSwe(helperValues, resultEncoding.getEncoding());
            if (xml instanceof TextEncodingDocument) {
                xbEncoding = (TextEncodingDocument) xml;
            } else {
                throw new NoApplicableCodeException().withMessage("ResultEncoding element encoding is not supported!");
            }
        }
        ResultEncoding xbResultEncoding = ResultEncoding.Factory.newInstance(getXmlOptions());
        xbResultEncoding.addNewAbstractEncoding().set(xbEncoding.getTextEncoding());
        XmlHelper.substituteElement(xbResultEncoding.getAbstractEncoding(), xbEncoding.getTextEncoding());
        return xbResultEncoding;
    }

    private ResultStructure createResultStructure(SosResultStructure resultStructure) throws OwsExceptionReport {
        // TODO move encoding to SWECommonEncoder
        final DataRecordDocument dataRecordDoc;
        if (resultStructure.isSetXml()) {
            try {
                dataRecordDoc = DataRecordDocument.Factory.parse(resultStructure.getXml());
            } catch (XmlException e) {
                throw new NoApplicableCodeException()
                        .withMessage("ResultStructure element encoding is not supported!");
            }
        } else {
        	Map<HelperValues, String> helperValues = new HashMap<HelperValues, String>(1);
        	helperValues.put(HelperValues.DOCUMENT, null);
            XmlObject xml = encodeSwe(helperValues, resultStructure.getResultStructure());
            if (xml instanceof DataRecordDocument) {
                dataRecordDoc = (DataRecordDocument) xml;
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("ResultStructure element encoding is not supported!");
            }
        }
        ResultStructure xbResultStructure =
                ResultStructure.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        xbResultStructure.addNewAbstractDataComponent().set(dataRecordDoc.getDataRecord());
        XmlHelper.substituteElement(xbResultStructure.getAbstractDataComponent(), dataRecordDoc.getDataRecord());
        return xbResultStructure;
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_RESULT_TEMPLATE_SCHEMA_LOCATION);
    }
}
