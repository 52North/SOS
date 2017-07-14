/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import java.util.List;

import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationRequest;
import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.svalbard.decode.exception.DecodingException;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
public class DeleteObservationJsonDecoder
        extends AbstractSosRequestDecoder<DeleteObservationRequest> {

    public DeleteObservationJsonDecoder() {
        super(DeleteObservationRequest.class,
              SosConstants.SOS,
              Sos2Constants.SERVICEVERSION,
              DeleteObservationConstants.Operations.DeleteObservation);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.DELETE_OBSERVATION;
    }

    @Override
    protected DeleteObservationRequest decodeRequest(JsonNode node) throws DecodingException  {
        DeleteObservationRequest r = new DeleteObservationRequest(DeleteObservationConstants.NS_SOSDO_2_0);
        r.setObservationIdentifiers(parseStringOrStringList(node.path(JSONConstants.OBSERVATION)));
        r.setFeatureIdentifiers(parseStringOrStringList(node.path(JSONConstants.FEATURE_OF_INTEREST)));
        r.setObservedProperties(parseStringOrStringList(node.path(JSONConstants.OBSERVED_PROPERTY)));
        r.setOfferings(parseStringOrStringList(node.path(JSONConstants.OFFERING)));
        r.setProcedures(parseStringOrStringList(node.path(JSONConstants.PROCEDURE)));
        r.setTemporalFilters(parseTemporalFilters(node.path(JSONConstants.TEMPORAL_FILTER)));
        return r;
    }

    private List<TemporalFilter> parseTemporalFilters(JsonNode node) throws DecodingException {
        return decodeJsonToObjectList(node, TemporalFilter.class);
    }
}
