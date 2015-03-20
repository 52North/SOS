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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.kvp.AbstractKvpDecoder;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.ParameterNotSupportedException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.http.MediaTypes;

/**
 * @since 4.0.0
 * 
 */
public class GetFeatureOfInterestKvpDecoderv20 extends AbstractKvpDecoder {
    private static final DecoderKey KVP_DECODER_KEY_TYPE = new OperationDecoderKey(SosConstants.SOS,
            Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetFeatureOfInterest, MediaTypes.APPLICATION_KVP);

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.singleton(KVP_DECODER_KEY_TYPE);
    }

    @Override
    public GetFeatureOfInterestRequest decode(Map<String, String> element) throws OwsExceptionReport {

        GetFeatureOfInterestRequest request = new GetFeatureOfInterestRequest();
        CompositeOwsException exceptions = new CompositeOwsException();

        for (String parameterName : element.keySet()) {
            String parameterValues = element.get(parameterName);
            try {
                if (!parseDefaultParameter(request, parameterValues, parameterName)) {
//                    // service (mandatory)
//                    if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.service.name())) {
//                        request.setService(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
//                        foundService = true;
//                    }
//    
//                    // version (mandatory)
//                    else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.version.name())) {
//                        request.setVersion(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
//                        foundVersion = true;
//                    }
//                    // request (mandatory)
//                    else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.request.name())) {
//                        KvpHelper.checkParameterSingleValue(parameterValues, parameterName);
//                    }
//                    // observedProperty (optional)
//                    else 
                        if (parameterName.equalsIgnoreCase(Sos2Constants.GetFeatureOfInterestParams.observedProperty
                            .name())) {
                        request.setObservedProperties(KvpHelper.checkParameterMultipleValues(parameterValues,
                                parameterName));
                    }
    
                    // procedure (optional)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetFeatureOfInterestParams.procedure.name())) {
                        request.setProcedures(KvpHelper.checkParameterMultipleValues(parameterValues, parameterName));
                    }
    
                    // featureOfInterest (optional)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetFeatureOfInterestParams.featureOfInterest
                            .name())) {
                        request.setFeatureIdentifiers(KvpHelper.checkParameterMultipleValues(parameterValues,
                                parameterName));
                    }
    
                    // spatialFilter (optional)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetFeatureOfInterestParams.spatialFilter.name())) {
                        List<SpatialFilter> spatialFilters = new ArrayList<SpatialFilter>(1);
                        List<String> splittedParameterValues = Arrays.asList(parameterValues.split(","));
                        if (CollectionHelper.isEmpty(splittedParameterValues)) {
                            throw new MissingParameterValueException(parameterName);
                        }
                        KvpHelper.checkParameterSingleValue(splittedParameterValues.get(0),
                                SosConstants.Filter.ValueReference);
                        KvpHelper.checkParameterMultipleValues(splittedParameterValues, parameterName);
    
                        spatialFilters.add(parseSpatialFilter(splittedParameterValues, parameterName));
                        request.setSpatialFilters(spatialFilters);
                    }
                    // namespaces (conditional)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetObservationParams.namespaces.name())) {
                        request.setNamespaces(parseNamespaces(parameterValues));
                     // language (optional)
//                    } else if (parameterName.equalsIgnoreCase(SosConstants.InspireParams.language.name())) {
//                        request.addExtension(getLanguageExtension(KvpHelper.checkParameterSingleValue(parameterValues, parameterName)));
//                    // CRS (optional)
//                    } else if (parameterName.equalsIgnoreCase(SosConstants.InspireParams.crs.name())) {
//                        request.addExtension(getCrsExtension(KvpHelper.checkParameterSingleValue(parameterValues, parameterName)));
                    } else {
                        exceptions.add(new ParameterNotSupportedException(parameterName));
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

        exceptions.throwIfNotEmpty();

        return request;
    }

}
