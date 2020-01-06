/*
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
package org.n52.sos.decode.kvp.v2;

import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationRequest;
import org.n52.sos.decode.kvp.AbstractSosKvpDecoder;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 * @since 1.0.0
 */
public class DeleteObservationKvpDecoder extends AbstractSosKvpDecoder<DeleteObservationRequest> {

    public DeleteObservationKvpDecoder() {
        super(DeleteObservationRequest::new, Sos2Constants.SERVICEVERSION,
                DeleteObservationConstants.Operations.DeleteObservation);
    }

    @Override
    protected void getRequestParameterDefinitions(Builder<DeleteObservationRequest> builder) {
        builder.add(DeleteObservationConstants.PARAM_OBSERVATION,
                decodeList(DeleteObservationRequest::setObservationIdentifiers));
        builder.add(DeleteObservationConstants.PARAM_OFFERING, decodeList(DeleteObservationRequest::setOfferings));
        builder.add(DeleteObservationConstants.PARAM_OBSERVED_PROPERTY,
                decodeList(DeleteObservationRequest::setObservedProperties));
        builder.add(DeleteObservationConstants.PARAM_PROCEDURE, decodeList(DeleteObservationRequest::setProcedures));
        builder.add(DeleteObservationConstants.PARAM_FEATURE_OF_INTEREST,
                decodeList(DeleteObservationRequest::setFeatureIdentifiers));
        builder.add(DeleteObservationConstants.PARAM_TEMPORAL_FILTER,
                decodeList(decodeTemporalFilter(asList(DeleteObservationRequest::setTemporalFilters))));
    }

}
