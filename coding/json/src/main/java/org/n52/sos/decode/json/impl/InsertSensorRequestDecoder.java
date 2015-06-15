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

import static org.n52.sos.coding.json.JSONConstants.FEATURE_OF_INTEREST_TYPE;
import static org.n52.sos.coding.json.JSONConstants.OBSERVABLE_PROPERTY;
import static org.n52.sos.coding.json.JSONConstants.OBSERVATION_TYPE;
import static org.n52.sos.coding.json.JSONConstants.PROCEDURE_DESCRIPTION;
import static org.n52.sos.coding.json.JSONConstants.PROCEDURE_DESCRIPTION_FORMAT;
import static org.n52.sos.coding.json.JSONConstants.RELATED_FEATURE;
import static org.n52.sos.coding.json.JSONConstants.ROLE;
import static org.n52.sos.coding.json.JSONConstants.TARGET;

import java.util.Collections;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.Decoder;
import org.n52.sos.decode.XmlNamespaceDecoderKey;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swes.SwesFeatureRelationship;
import org.n52.sos.request.InsertSensorRequest;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class InsertSensorRequestDecoder extends AbstractSosRequestDecoder<InsertSensorRequest> {
    private final JSONDecoder<AbstractFeature> featureDecoder = new FeatureDecoder();

    public InsertSensorRequestDecoder() {
        super(InsertSensorRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                Sos2Constants.Operations.InsertSensor);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.INSERT_SENSOR;
    }

    @Override
    protected InsertSensorRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        final InsertSensorRequest r = new InsertSensorRequest();
        final SosInsertionMetadata meta = new SosInsertionMetadata();
        meta.setFeatureOfInterestTypes(parseStringOrStringList(node.path(FEATURE_OF_INTEREST_TYPE)));
        meta.setObservationTypes(parseStringOrStringList(node.path(OBSERVATION_TYPE)));
        r.setMetadata(meta);
        r.setObservableProperty(parseStringOrStringList(node.path(OBSERVABLE_PROPERTY)));
        r.setProcedureDescriptionFormat(node.path(PROCEDURE_DESCRIPTION_FORMAT).textValue());
        r.setRelatedFeature(parseFeatureRelationships(node.path(RELATED_FEATURE)));
        r.setProcedureDescription(parseProcedureDescription(node.path(PROCEDURE_DESCRIPTION),
                r.getProcedureDescriptionFormat()));
        return r;
    }

    protected List<SwesFeatureRelationship> parseFeatureRelationships(JsonNode node) throws OwsExceptionReport {
        if (node.isArray()) {
            List<SwesFeatureRelationship> list = Lists.newArrayListWithExpectedSize(node.size());
            for (JsonNode n : node) {
                if (n.isObject()) {
                    list.add(parseFeatureRelationship(n));
                }
            }
            return list;
        } else if (node.isObject()) {
            return Collections.singletonList(parseFeatureRelationship(node));
        } else {
            return null;
        }
    }

    protected SwesFeatureRelationship parseFeatureRelationship(JsonNode node) throws OwsExceptionReport {
        return new SwesFeatureRelationship(node.path(ROLE).textValue(), featureDecoder.decodeJSON(node.path(TARGET),
                false));
    }

    private SosProcedureDescription parseProcedureDescription(JsonNode path, String pdf) throws OwsExceptionReport {
        try {
            final XmlObject xb = XmlObject.Factory.parse(path.textValue());
            Decoder<?, XmlObject> decoder = getProcedureDescriptionDecoder(pdf, xb);
            if (decoder == null) {
                throw new InvalidParameterValueException().at(PROCEDURE_DESCRIPTION_FORMAT).withMessage(
                        "The requested %s is not supported!", PROCEDURE_DESCRIPTION_FORMAT);
            }
            return (SosProcedureDescription) decoder.decode(xb);
        } catch (final XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                    "Error while parsing procedure description of InsertSensor request!");
        }
    }

    protected Decoder<?, XmlObject> getProcedureDescriptionDecoder(String pdf, XmlObject xb) {
        return CodingRepository.getInstance().getDecoder(new XmlNamespaceDecoderKey(pdf, xb.getClass()));
    }
}
