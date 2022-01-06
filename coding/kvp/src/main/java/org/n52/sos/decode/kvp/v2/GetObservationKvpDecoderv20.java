/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlObject;
import org.n52.shetland.ogc.ows.extension.Extension;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swes.SwesExtension;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.decode.exception.NoDecoderForKeyException;
import org.n52.svalbard.util.CodingHelper;
import org.n52.svalbard.util.XmlHelper;

/**
 * @since 4.0.0
 *
 */
public class GetObservationKvpDecoderv20 extends AbstractObservationKvpDecoder<GetObservationRequest> {

    private DecoderRepository decoderRepository;

    public GetObservationKvpDecoderv20() {
        super(GetObservationRequest::new, Sos2Constants.SERVICEVERSION, SosConstants.Operations.GetObservation);
    }

    public GetObservationKvpDecoderv20(String version, String operation) {
        this(GetObservationRequest::new, version, operation);
    }

    public GetObservationKvpDecoderv20(String version, Enum<?> operation) {
        this(GetObservationRequest::new, version, operation);
    }

    public GetObservationKvpDecoderv20(DecoderKey... keys) {
        this(GetObservationRequest::new, keys);
    }

    public GetObservationKvpDecoderv20(Collection<? extends DecoderKey> keys) {
        this(GetObservationRequest::new, keys);
    }

    public GetObservationKvpDecoderv20(String service, String version, String operation) {
        super(GetObservationRequest::new, service, version, operation);
    }

    public GetObservationKvpDecoderv20(String service, String version, Enum<?> operation) {
        super(GetObservationRequest::new, service, version, operation);
    }

    public GetObservationKvpDecoderv20(Supplier<? extends GetObservationRequest> supplier, String version,
            String operation) {
        super(supplier, version, operation);
    }

    public GetObservationKvpDecoderv20(Supplier<? extends GetObservationRequest> supplier, String version,
            Enum<?> operation) {
        super(supplier, version, operation);
    }

    public GetObservationKvpDecoderv20(Supplier<? extends GetObservationRequest> supplier, DecoderKey... keys) {
        super(supplier, keys);
    }

    public GetObservationKvpDecoderv20(Supplier<? extends GetObservationRequest> supplier,
            Collection<? extends DecoderKey> keys) {
        super(supplier, keys);
    }

    public GetObservationKvpDecoderv20(Supplier<? extends GetObservationRequest> supplier, String service,
            String version, String operation) {
        super(supplier, service, version, operation);
    }

    public GetObservationKvpDecoderv20(Supplier<? extends GetObservationRequest> supplier, String service,
            String version, Enum<?> operation) {
        super(supplier, service, version, operation);
    }

    @Inject
    public void setDecoderRepository(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    @Override
    protected void getRequestParameterDefinitions(Builder<GetObservationRequest> builder) {
        super.getRequestParameterDefinitions(builder);
        builder.add(SosConstants.GetObservationParams.offering, decodeList(GetObservationRequest::setOfferings));
        builder.add(SosConstants.GetObservationParams.procedure, decodeList(GetObservationRequest::setProcedures));
        builder.add(SosConstants.GetObservationParams.observedProperty,
                decodeList(GetObservationRequest::setObservedProperties));
        builder.add(SosConstants.GetObservationParams.featureOfInterest,
                decodeList(GetObservationRequest::setFeatureIdentifiers));
        builder.add(Sos2Constants.GetObservationParams.temporalFilter,
                decodeList(decodeTemporalFilter(asList(GetObservationRequest::setTemporalFilters))));
        builder.add(Sos2Constants.GetObservationParams.spatialFilter,
                decodeList(decodeSpatialFilter(GetObservationRequest::setSpatialFilter)));
        builder.add(Sos2Constants.GetObservationParams.namespaces,
                decodeNamespaces(GetObservationRequest::setNamespaces));
        builder.add(SosConstants.GetObservationParams.responseFormat, GetObservationRequest::setResponseFormat);
        builder.add(Sos2Constants.Extensions.MergeObservationsIntoDataArray, this::parseMergeObservationIntoDataArray);
        builder.add("extension", decodeList(this::parseExtensionParameter));
        builder.add("$filter", this::parseODataFes);

    }

    protected void parseExtensionParameter(OwsServiceRequest request, String name, List<String> value)
            throws DecodingException {
        try {
            for (String parameterValue : value) {
                request.addExtension(parseExtensionParameter(parameterValue));
            }
        } catch (DecodingException ex) {
            throw new DecodingException(ex, name);
        }
    }

    public Extension<?> parseExtensionParameter(String value) throws DecodingException {
        XmlObject xml = XmlHelper.parseXmlString(value);
        DecoderKey key = CodingHelper.getDecoderKey(xml);
        Decoder<Object, XmlObject> decoder = decoderRepository.getDecoder(key);
        if (decoder == null) {
            throw new NoDecoderForKeyException(key);
        }
        Object obj = decoder.decode(xml);
        if (obj instanceof Extension) {
            return (Extension<?>) obj;
        } else if (obj instanceof SweAbstractDataComponent) {
            return new SwesExtension<>().setValue((SweAbstractDataComponent) obj);
        } else {
            return new SwesExtension<>().setValue(new SweText().setValue(value));
        }
    }

    private void parseMergeObservationIntoDataArray(GetObservationRequest request, String name, String value) {
        request.addSweBooleanExtension(name, value);
    }

}
