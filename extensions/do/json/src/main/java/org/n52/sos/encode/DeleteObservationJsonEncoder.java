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
package org.n52.sos.encode;

import org.n52.sos.coding.json.JSONConstants;
import org.n52.sos.encode.json.AbstractSosResponseEncoder;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ext.deleteobservation.DeleteObservationConstants;
import org.n52.sos.ext.deleteobservation.DeleteObservationResponse;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class DeleteObservationJsonEncoder extends AbstractSosResponseEncoder<DeleteObservationResponse> {

    public DeleteObservationJsonEncoder() {
        super(DeleteObservationResponse.class,
              DeleteObservationConstants.Operations.DeleteObservation);
    }

    @Override
    protected void encodeResponse(ObjectNode json, DeleteObservationResponse t)
            throws OwsExceptionReport {
        if (t == null) {
            throw new UnsupportedEncoderInputException(this, t);
        }
        final CompositeOwsException exceptions = new CompositeOwsException();
        if (t.getService() == null) {
            exceptions.add(new MissingServiceParameterException());
        }
        if (t.getVersion() == null) {
            exceptions.add(new MissingVersionParameterException());
        }
        if (t.getObservationId() == null || t.getObservationId().isEmpty()) {
            exceptions.add(new MissingParameterValueException(DeleteObservationConstants.PARAMETER_NAME));
        }
        exceptions.throwIfNotEmpty();
        json.put(JSONConstants.DELETED_OBSERVATION, t.getObservationId());
    }

}
