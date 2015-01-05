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

import java.util.List;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetResultRequest;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class GetResultRequestDecoder extends AbstractSosRequestDecoder<GetResultRequest> {

    public GetResultRequestDecoder() {
        super(GetResultRequest.class, SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                SosConstants.Operations.GetResult);
    }

    @Override
    protected String getSchemaURI() {
        return SchemaConstants.Request.GET_RESULT;
    }

    @Override
    protected GetResultRequest decodeRequest(JsonNode node) throws OwsExceptionReport {
        GetResultRequest req = new GetResultRequest();
        req.setFeatureIdentifiers(parseFeatureIdentifiers(node));
        req.setObservationTemplateIdentifier(parseObservationTemplateIdentifier(node));
        req.setObservedProperty(parseObservedProperty(node));
        req.setOffering(parseOffering(node));
        req.setSpatialFilter(parseSpatialFilter(node));
        req.setTemporalFilter(parseTemporalFilters(node));
        return req;
    }

    private List<String> parseFeatureIdentifiers(JsonNode node) {
        return parseStringOrStringList(node.path(JSONConstants.FEATURE_OF_INTEREST));
    }

    private String parseObservationTemplateIdentifier(JsonNode node) {
        return node.path(JSONConstants.OBSERVATION_TEMPLATE).textValue();
    }

    private String parseObservedProperty(JsonNode node) {
        return node.path(JSONConstants.OBSERVED_PROPERTY).textValue();
    }

    private String parseOffering(JsonNode node) {
        return node.path(JSONConstants.OFFERING).textValue();
    }

    private SpatialFilter parseSpatialFilter(JsonNode node) throws OwsExceptionReport {
        return decodeJsonToObject(node.path(JSONConstants.SPATIAL_FILTER), SpatialFilter.class);
    }

    private List<TemporalFilter> parseTemporalFilters(JsonNode node) throws OwsExceptionReport {
        return decodeJsonToObjectList(node.path(JSONConstants.TEMPORAL_FILTER), TemporalFilter.class);
    }

}
