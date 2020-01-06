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
package org.n52.sos.decode;

import static org.n52.sos.coding.json.JSONConstants.FEATURE_OF_INTEREST;
import static org.n52.sos.coding.json.JSONConstants.OBSERVED_PROPERTY;
import static org.n52.sos.coding.json.JSONConstants.OFFERING;
import static org.n52.sos.coding.json.JSONConstants.PROCEDURE;
import static org.n52.sos.coding.json.JSONConstants.TEMPORAL_FILTER;

import java.util.List;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.coding.json.SchemaConstants;
import org.n52.sos.decode.json.AbstractSosRequestDecoder;
import org.n52.sos.ext.deleteobservation.DeleteObservationConstants;
import org.n52.sos.ext.deleteobservation.DeleteObservationRequest;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
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
    protected DeleteObservationRequest decodeRequest(JsonNode node)
            throws OwsExceptionReport {
        DeleteObservationRequest r = new DeleteObservationRequest(DeleteObservationConstants.NS_SOSDO_2_0);
        r.setObservationIdentifiers(parseStringOrStringList(node.path(JSONConstants.OBSERVATION)));
        r.setFeatureIdentifiers(parseStringOrStringList(node.path(FEATURE_OF_INTEREST)));
        r.setObservedProperties(parseStringOrStringList(node.path(OBSERVED_PROPERTY)));
        r.setOfferings(parseStringOrStringList(node.path(OFFERING)));
        r.setProcedures(parseStringOrStringList(node.path(PROCEDURE)));
        r.setTemporalFilters(parseTemporalFilters(node.path(TEMPORAL_FILTER)));
        return r;
    }
    
    private List<TemporalFilter> parseTemporalFilters(JsonNode node) throws OwsExceptionReport {
        return decodeJsonToObjectList(node, TemporalFilter.class);
    }
}
