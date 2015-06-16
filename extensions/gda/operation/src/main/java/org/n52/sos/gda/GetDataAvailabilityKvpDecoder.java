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
package org.n52.sos.gda;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.kvp.AbstractKvpDecoder;
import org.n52.sos.exception.ows.concrete.ParameterNotSupportedException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.http.MediaTypes;

/**
 * @since 4.0.0
 * 
 */
public class GetDataAvailabilityKvpDecoder extends AbstractKvpDecoder {
    private static final DecoderKey KVP_DECODER_KEY_TYPE = new OperationDecoderKey(SosConstants.SOS,
            Sos2Constants.SERVICEVERSION, GetDataAvailabilityConstants.OPERATION_NAME, MediaTypes.APPLICATION_KVP);

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.singleton(KVP_DECODER_KEY_TYPE);
    }

    @Override
    public GetDataAvailabilityRequest decode(Map<String, String> element) throws OwsExceptionReport {
        GetDataAvailabilityRequest request = new GetDataAvailabilityRequest();
        CompositeOwsException exceptions = new CompositeOwsException();

        for (String name : element.keySet()) {
            String parameterValues = element.get(name);
            try {
                if (!parseDefaultParameter(request, parameterValues, name)) {
                    if (name.equalsIgnoreCase(GetDataAvailabilityConstants.GetDataAvailabilityParams.observedProperty
                            .name())) {
                        for (String observedProperty : KvpHelper.checkParameterMultipleValues(parameterValues, name)) {
                            request.addObservedProperty(observedProperty);
                        }
                    } else if (name.equalsIgnoreCase(GetDataAvailabilityConstants.GetDataAvailabilityParams.procedure
                            .name())) {
                        for (String procedure : KvpHelper.checkParameterMultipleValues(parameterValues, name)) {
                            request.addProcedure(procedure);
                        }
                    } else if (name
                            .equalsIgnoreCase(GetDataAvailabilityConstants.GetDataAvailabilityParams.featureOfInterest
                                    .name())) {
                        for (String featureOfInterest : KvpHelper.checkParameterMultipleValues(parameterValues, name)) {
                            request.addFeatureOfInterest(featureOfInterest);
                        }
                    } else if (name.equalsIgnoreCase(GetDataAvailabilityConstants.GetDataAvailabilityParams.offering
                            .name())) {
                        for (String offering : KvpHelper.checkParameterMultipleValues(parameterValues, name)) {
                            request.addOffering(offering);
                        }
                    } else {
                        exceptions.add(new ParameterNotSupportedException(name));
                    }
                }
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        }
        
        exceptions.throwIfNotEmpty();

        return request;
    }
}
