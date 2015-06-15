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

import java.util.Set;

import net.opengis.sos.x20.GetResultResponseDocument;
import net.opengis.sos.x20.GetResultResponseType;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.response.GetResultResponse;
import org.n52.sos.w3c.SchemaLocation;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetResultResponseEncoder extends AbstractSosResponseEncoder<GetResultResponse> {
    public GetResultResponseEncoder() {
        super(SosConstants.Operations.GetResult.name(), GetResultResponse.class);
    }

    @Override
    protected XmlObject create(GetResultResponse response) throws OwsExceptionReport {
        GetResultResponseDocument doc = GetResultResponseDocument.Factory.newInstance(getXmlOptions());
        GetResultResponseType gtr = doc.addNewGetResultResponse();
        XmlObject resultValues = gtr.addNewResultValues();
        if (response.hasResultValues()) {
            XmlString xmlString = XmlString.Factory.newInstance();
            xmlString.setStringValue(response.getResultValues());
            resultValues.set(xmlString);
        }
        return doc;
    }

    @Override
    public Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_GET_RESULT_SCHEMA_LOCATION);
    }
}
