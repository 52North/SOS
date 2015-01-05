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
package org.n52.sos.decode;

import static org.n52.sos.ext.deleteobservation.DeleteObservationConstants.PARAMETER_NAME;
import static org.n52.sos.util.KvpHelper.checkParameterSingleValue;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.kvp.AbstractKvpDecoder;
import org.n52.sos.exception.ows.concrete.MissingRequestParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ext.deleteobservation.DeleteObservationConstants;
import org.n52.sos.ext.deleteobservation.DeleteObservationRequest;
import org.n52.sos.ext.deleteobservation.MissingObservationParameterException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.http.MediaTypes;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 1.0.0
 */
public class DeleteObservationKvpDecoder extends AbstractKvpDecoder {

    private static final DecoderKey KVP_DECODER_KEY_TYPE = new OperationDecoderKey(SosConstants.SOS,
            Sos2Constants.SERVICEVERSION, DeleteObservationConstants.Operations.DeleteObservation,
            MediaTypes.APPLICATION_KVP);

    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.singleton(KVP_DECODER_KEY_TYPE);
    }

    public DeleteObservationRequest decode(Map<String, String> objectToDecode) throws OwsExceptionReport {
        if (objectToDecode == null) {
            throw new UnsupportedDecoderInputException(this, objectToDecode);
        }
        DeleteObservationRequest deleteObservationRequest = new DeleteObservationRequest();
        CompositeOwsException exceptions = new CompositeOwsException();
        boolean foundRequest = false, foundService = false, foundVersion = false, foundObservation = false;

        for (String parameterName : objectToDecode.keySet()) {
            String parameterValues = objectToDecode.get(parameterName);
            try {
                if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.service.name())) {
                    deleteObservationRequest.setService(KvpHelper.checkParameterSingleValue(parameterValues,
                            parameterName));
                    foundService = true;
                } else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.version.name())) {
                    deleteObservationRequest.setVersion(KvpHelper.checkParameterSingleValue(parameterValues,
                            parameterName));
                    foundVersion = true;
                } else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.request.name())) {
                    KvpHelper.checkParameterSingleValue(parameterValues, parameterName);
                    foundRequest = true;
                } else if (parameterName.equalsIgnoreCase(PARAMETER_NAME)) {
                    deleteObservationRequest.setObservationIdentifier(checkParameterSingleValue(parameterValues,
                            parameterName));
                    foundObservation = true;
                }
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        }

        if (!foundService) {
            exceptions.add(new MissingServiceParameterException());
        }

        if (!foundVersion) {
            exceptions.add(new MissingVersionParameterException());
        }

        if (!foundRequest) {
            exceptions.add(new MissingRequestParameterException());
        }

        if (!foundObservation) {
            exceptions.add(new MissingObservationParameterException());
        }

        exceptions.throwIfNotEmpty();

        return deleteObservationRequest;
    }

}
