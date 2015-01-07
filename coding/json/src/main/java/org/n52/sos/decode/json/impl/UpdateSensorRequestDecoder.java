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
package org.n52.sos.decode.json.impl;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.XmlNamespaceDecoderKey;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.request.UpdateSensorRequest;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class UpdateSensorRequestDecoder extends AbstractSosRequestDecoder<UpdateSensorRequest> {
    public UpdateSensorRequestDecoder() {
        super(UpdateSensorRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                Sos2Constants.Operations.UpdateSensorDescription);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.UPDATE_SENSOR_DESCRIPTION;
    }

    @Override
    protected UpdateSensorRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        UpdateSensorRequest req = new UpdateSensorRequest();
        req.setProcedureIdentifier(node.path(JSONConstants.PROCEDURE).textValue());
        String pdf = node.path(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT).textValue();
        req.setProcedureDescriptionFormat(pdf);
        JsonNode procedureDescriptionNode = node.path(JSONConstants.PROCEDURE_DESCRIPTION);
        if (procedureDescriptionNode.isArray()) {
            for (JsonNode n : procedureDescriptionNode) {
                req.addProcedureDescriptionString(decodeProcedureDescription(n, pdf));
            }
        } else {
            req.addProcedureDescriptionString(decodeProcedureDescription(procedureDescriptionNode, pdf));
        }
        return req;
    }

    private SosProcedureDescription decodeProcedureDescription(JsonNode node, String pdf) throws OwsExceptionReport {
        if (node.isTextual()) {
            return parseProcedureDesciption(node.textValue(), pdf);
        } else {
            SosProcedureDescription pd =
                    parseProcedureDesciption(node.path(JSONConstants.DESCRIPTION).textValue(), pdf);
            if (node.has(JSONConstants.VALID_TIME)) {
                pd.setValidTime(parseTime(node.path(JSONConstants.VALID_TIME)));
            }
            return pd;
        }
    }

    private SosProcedureDescription parseProcedureDesciption(String xml, String pdf) throws OwsExceptionReport {
        try {
            final XmlObject xb = XmlObject.Factory.parse(xml);
            Decoder<?, XmlObject> decoder =
                    CodingRepository.getInstance().getDecoder(new XmlNamespaceDecoderKey(pdf, xb.getClass()));
            if (decoder == null) {
                throw new InvalidParameterValueException().at(JSONConstants.PROCEDURE_DESCRIPTION_FORMAT).withMessage(
                        "The requested %s is not supported!", JSONConstants.PROCEDURE_DESCRIPTION_FORMAT);
            }
            return (SosProcedureDescription) decoder.decode(xb);
        } catch (final XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                    "Error while parsing procedure description of InsertSensor request!");
        }
    }
}
