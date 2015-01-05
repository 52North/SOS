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
package org.n52.sos.decode.kvp;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.ParameterNotSupportedException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.http.MediaTypes;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class GetCapabilitiesKvpDecoder extends AbstractKvpDecoder {

    private static final Set<DecoderKey> KVP_DECODER_KEY_TYPE = Sets.<DecoderKey> newHashSet(new OperationDecoderKey(
            SosConstants.SOS, null, SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP),
            new OperationDecoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                    SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP),
            new OperationDecoderKey(null, Sos2Constants.SERVICEVERSION,
                    SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP),
            new OperationDecoderKey(null, null, SosConstants.Operations.GetCapabilities.name(),
                    MediaTypes.APPLICATION_KVP), new OperationDecoderKey(SosConstants.SOS,
                    Sos1Constants.SERVICEVERSION, SosConstants.Operations.GetCapabilities.name(),
                    MediaTypes.APPLICATION_KVP), new OperationDecoderKey(null, Sos1Constants.SERVICEVERSION,
                    SosConstants.Operations.GetCapabilities.name(), MediaTypes.APPLICATION_KVP));

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(KVP_DECODER_KEY_TYPE);
    }

    /**
     * parses the String representing the getCapabilities request and creates a
     * SosGetCapabilities request
     * 
     * @param element
     *            Map with getCapabilities parameters
     * 
     * @return Returns SosGetCapabilitiesRequest representing the request
     * 
     * @throws OwsExceptionReport
     *             If parsing the String failed
     */
    @Override
    public GetCapabilitiesRequest decode(Map<String, String> element) throws OwsExceptionReport {

        GetCapabilitiesRequest request = new GetCapabilitiesRequest();
        CompositeOwsException exceptions = new CompositeOwsException();

        boolean isV100 = false;

        for (String parameterName : element.keySet()) {
            String parameterValues = element.get(parameterName);
            try {
                if (!parseDefaultParameter(request, parameterValues, parameterName)) {
//                // service (mandatory SOS 1.0.0, SOS 2.0 default)
//                if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.service.name())) {
//                    request.setService(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
//                    foundService = true;
//                } // request (mandatory)
//                else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.request.name())) {
//                    KvpHelper.checkParameterSingleValue(parameterValues, parameterName);
//                } // acceptVersions (optional)
//                else 
                  if (parameterName.equalsIgnoreCase(SosConstants.GetCapabilitiesParams.AcceptVersions.name())) {
                        List<String> acceptVersions =
                                KvpHelper.checkParameterMultipleValues(parameterValues, parameterName);
                        request.setAcceptVersions(acceptVersions);
                        if (CollectionHelper.isNotEmpty(acceptVersions)
                                && Sos1Constants.SERVICEVERSION.equals(acceptVersions.get(0))) {
                            isV100 = true;
                        }
                    } // acceptFormats (optional)
                    else if (parameterName.equalsIgnoreCase(SosConstants.GetCapabilitiesParams.AcceptFormats.name())) {
                        request.setAcceptFormats(KvpHelper.checkParameterMultipleValues(parameterValues, parameterName));
                    } // updateSequence (optional)
                    else if (parameterName.equalsIgnoreCase(SosConstants.GetCapabilitiesParams.updateSequence.name())) {
                        request.setUpdateSequence(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
                    } // sections (optional)
                    else if (parameterName.equalsIgnoreCase(SosConstants.GetCapabilitiesParams.Sections.name())) {
                        request.setSections(KvpHelper.checkParameterMultipleValues(parameterValues, parameterName));
                    } // capabilitiesId (optional; non-standard)
                    else if (parameterName.equalsIgnoreCase(SosConstants.GetCapabilitiesParams.CapabilitiesId.name())) {
                         request.setCapabilitiesId(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
    //                } // language (optional)
    //                else if (parameterName.equalsIgnoreCase(SosConstants.InspireParams.language.name())) {
    //                    request.addExtension(getLanguageExtension(KvpHelper.checkParameterSingleValue(parameterValues, parameterName)));
    //                } // CRS (optional) 
    //                else if (parameterName.equalsIgnoreCase(SosConstants.InspireParams.crs.name())) {
    //                    request.addExtension(getCrsExtension(KvpHelper.checkParameterSingleValue(parameterValues, parameterName)));
                    } else {
                        exceptions.add(new ParameterNotSupportedException(parameterName));
                    }
                }
            } catch (OwsExceptionReport owse) {
                exceptions.add(owse);
            }
        }

        if (isV100 && !request.isSetService()) {
            exceptions.add(new MissingServiceParameterException().at(OWSConstants.RequestParams.service));
        }

        exceptions.throwIfNotEmpty();

        return request;

    }
}
