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
package org.n52.sos.decode.kvp.v2;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.kvp.AbstractKvpDecoder;
import org.n52.sos.exception.ows.concrete.InvalidTemporalFilterParameterException;
import org.n52.sos.exception.ows.concrete.MissingObservedPropertyParameterException;
import org.n52.sos.exception.ows.concrete.MissingOfferingParameterException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.ParameterNotSupportedException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetResultRequest;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.http.MediaTypes;

/**
 * @since 4.0.0
 * 
 */
public class GetResultKvpDecoderv20 extends AbstractKvpDecoder {
    private static final DecoderKey KVP_DECODER_KEY_TYPE = new OperationDecoderKey(SosConstants.SOS,
            Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetResult, MediaTypes.APPLICATION_KVP);

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.singleton(KVP_DECODER_KEY_TYPE);
    }

    @Override
    public GetResultRequest decode(Map<String, String> element) throws OwsExceptionReport {
        GetResultRequest request = new GetResultRequest();
        CompositeOwsException exceptions = new CompositeOwsException();

        boolean foundOffering = false;
        boolean foundObservedProperty = false;

        for (String parameterName : element.keySet()) {
            String parameterValues = element.get(parameterName);
            try {
                if (!parseDefaultParameter(request, parameterValues, parameterName)) {
//                    // service (mandatory)
//                    if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.service.name())) {
//                        request.setService(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
//                        foundService = true;
//                    } // version (mandatory)
//                    else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.version.name())) {
//                        request.setVersion(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
//                        foundVersion = true;
//                    } // request (mandatory)
//                    else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.request.name())) {
//                        KvpHelper.checkParameterSingleValue(parameterValues, parameterName);
//                    } // offering (mandatory)
//                    else 
                        
                        if (parameterName.equalsIgnoreCase(Sos2Constants.GetResultTemplateParams.offering.name())) {
                        request.setOffering(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
                        foundOffering = true;
                    } // observedProperty (mandatory)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetResultTemplateParams.observedProperty.name())) {
                        request.setObservedProperty(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
                        foundObservedProperty = true;
                    } // featureOfInterest (optional)
                    else if (parameterName.equalsIgnoreCase(SosConstants.GetObservationParams.featureOfInterest.name())) {
                        request.setFeatureIdentifiers(KvpHelper.checkParameterMultipleValues(parameterValues,
                                parameterName));
                    } // eventTime (optional)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetObservationParams.temporalFilter.name())) {
                        try {
                            request.setTemporalFilter(parseTemporalFilter(
                                    KvpHelper.checkParameterMultipleValues(parameterValues, parameterName), parameterName));
                        } catch (OwsExceptionReport e) {
                            throw new InvalidTemporalFilterParameterException(parameterValues).causedBy(e);
                        }
    
                    } // spatialFilter (optional)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetObservationParams.spatialFilter.name())) {
                        request.setSpatialFilter(parseSpatialFilter(
                                KvpHelper.checkParameterMultipleValues(parameterValues, parameterName), parameterName));
                    } // xmlWrapper (default = false) (optional)
                      // namespaces (conditional)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetObservationParams.namespaces.name())) {
                        request.setNamespaces(parseNamespaces(parameterValues));
                    } else {
                        throw new ParameterNotSupportedException(parameterName);
                    }
                }
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        }

        if (!request.isSetService()) {
            exceptions.add(new MissingServiceParameterException());
        }

        if (!request.isSetVersion()) {
            exceptions.add(new MissingVersionParameterException());
        }

        if (!foundOffering) {
            exceptions.add(new MissingOfferingParameterException());
        }

        if (!foundObservedProperty) {
            exceptions.add(new MissingObservedPropertyParameterException());
        }
        exceptions.throwIfNotEmpty();
        return request;
    }
}
