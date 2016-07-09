/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.kvp.AbstractKvpDecoder;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.exception.ows.concrete.ParameterNotSupportedException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.Sos2Constants.Extensions;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.MediaTypes;

/**
 * @since 4.0.0
 * 
 */
public class GetObservationKvpDecoderv20 extends AbstractKvpDecoder {

    private static final DecoderKey KVP_DECODER_KEY_TYPE = new OperationDecoderKey(SosConstants.SOS,
            Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetObservation, MediaTypes.APPLICATION_KVP);

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.singleton(KVP_DECODER_KEY_TYPE);
    }

    @Override
    public GetObservationRequest decode(final Map<String, String> element) throws OwsExceptionReport {

        final GetObservationRequest request = new GetObservationRequest();
        final CompositeOwsException exceptions = new CompositeOwsException();

        for (final String parameterName : element.keySet()) {
            final String parameterValues = element.get(parameterName);
            try {
                if (!parseDefaultParameter(request, parameterValues, parameterName)) {
                    // offering (optional)
                    if (parameterName.equalsIgnoreCase(SosConstants.GetObservationParams.offering.name())) {
                        request.setOfferings(KvpHelper.checkParameterMultipleValues(parameterValues, parameterName));
                    }

                    // observedProperty (optional)
                    else if (parameterName.equalsIgnoreCase(SosConstants.GetObservationParams.observedProperty.name())) {
                        request.setObservedProperties(KvpHelper.checkParameterMultipleValues(parameterValues,
                                parameterName));
                    }

                    // procedure (optional)
                    else if (parameterName.equalsIgnoreCase(SosConstants.GetObservationParams.procedure.name())) {
                        request.setProcedures(KvpHelper.checkParameterMultipleValues(parameterValues, parameterName));
                    }

                    // featureOfInterest (optional)
                    else if (parameterName.equalsIgnoreCase(SosConstants.GetObservationParams.featureOfInterest.name())) {
                        request.setFeatureIdentifiers(KvpHelper.checkParameterMultipleValues(parameterValues,
                                parameterName));
                    }

                    // eventTime (optional)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetObservationParams.temporalFilter.name())) {
                        try {
                            request.setTemporalFilters(parseTemporalFilter(
                                    KvpHelper.checkParameterMultipleValues(parameterValues, parameterName), parameterName));
                        } catch (final OwsExceptionReport e) {
                            exceptions.add(new InvalidParameterValueException(parameterName, parameterValues).causedBy(e));
                        }
                    }

                    // spatialFilter (optional)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetObservationParams.spatialFilter.name())) {
                        List<String> splittedParameterValues = Arrays.asList(parameterValues.split(","));
                        if (CollectionHelper.isEmpty(splittedParameterValues)) {
                            throw new MissingParameterValueException(parameterName);
                        }
                        KvpHelper.checkParameterSingleValue(splittedParameterValues.get(0),
                                SosConstants.Filter.ValueReference);
                        KvpHelper.checkParameterMultipleValues(splittedParameterValues, parameterName);
                        request.setSpatialFilter(parseSpatialFilter(splittedParameterValues, parameterName));
                    }

                    // responseFormat (optional)
                    else if (parameterName.equalsIgnoreCase(SosConstants.GetObservationParams.responseFormat.name())) {
                        request.setResponseFormat(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
                    }
                    // namespaces (conditional)
                    else if (parameterName.equalsIgnoreCase(Sos2Constants.GetObservationParams.namespaces.name())) {
                        request.setNamespaces(parseNamespaces(parameterValues));
                    }
                    /*
                     * EXTENSIONS
                     */
                    // MergeObservationsIntoDataArray
                    else if (parameterName
                            .equalsIgnoreCase(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name())) {
                        request.setExtensions(parseExtension(Sos2Constants.Extensions.MergeObservationsIntoDataArray,
                                parameterValues, request.getExtensions()));
                    } else {
                        exceptions.add(new ParameterNotSupportedException(parameterName));
                    }
                }
            } catch (final OwsExceptionReport owse) {
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
    
    @Override
    protected boolean parseExtensionParameter(AbstractServiceRequest<?> request, String parameterValues,
            String parameterName) throws OwsExceptionReport {
        if ("extension".equalsIgnoreCase(parameterName)) {
            List<String> checkParameterMultipleValues = KvpHelper.checkParameterMultipleValues(parameterValues, parameterName);
            for (String parameterValue : checkParameterMultipleValues) {
                final Object obj = CodingHelper.decodeXmlElement(XmlHelper.parseXmlString(parameterValue));
                if (obj instanceof SwesExtension<?>) {
                    request.addExtension((SwesExtension<?>) obj);
                } else {
                    request.addExtension(new SwesExtensionImpl<Object>().setValue(obj));
                }
            }
            return true;
        }
        return super.parseExtensionParameter(request, parameterValues, parameterName);
    }

    private SwesExtensions parseExtension(final Extensions extension, final String parameterValues,
            SwesExtensions extensions) {
        if (extensions == null || extensions.isEmpty()) {
            extensions = new SwesExtensions();
        }
        switch (extension) {
        case MergeObservationsIntoDataArray:
            extensions.addSwesExtension(
            		new SwesExtensionImpl<SweBoolean>()
            		.setDefinition(extension.name())
            		.setValue(
            				(SweBoolean) new SweBoolean()
            				.setValue(Boolean.parseBoolean(parameterValues))
            		.setDefinition(extension.name())));
            break;
        default:
            break;
        }
        return extensions;
    }
}
