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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import net.opengis.sos.x20.GetResultDocument;
import net.opengis.sos.x20.GetResultTemplateDocument;
import net.opengis.sos.x20.GetResultTemplateType;
import net.opengis.sos.x20.GetResultType;
import net.opengis.sos.x20.GetResultType.SpatialFilter;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetResultRequest;
import org.n52.sos.request.GetResultTemplateRequest;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class SosRequestEncoderv20 extends AbstractXmlEncoder<AbstractServiceRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SosRequestEncoderv20.class);

    private static final Set<EncoderKey> ENCODER_KEYS = CodingHelper.encoderKeysForElements(Sos2Constants.NS_SOS_20,
            AbstractServiceRequest.class, GetResultTemplateRequest.class, GetResultRequest.class);

    public SosRequestEncoderv20() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(Sos2Constants.NS_SOS_20, SosConstants.NS_SOS_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(Sos2Constants.SOS_SCHEMA_LOCATION);
    }

    @Override
    public XmlObject encode(final AbstractServiceRequest communicationObject) throws OwsExceptionReport {
        final Map<HelperValues, String> additionalValues = new EnumMap<HelperValues, String>(HelperValues.class);
        additionalValues.put(HelperValues.VERSION, Sos2Constants.SERVICEVERSION);
        return encode(communicationObject, additionalValues);
    }

    @Override
    public XmlObject encode(final AbstractServiceRequest request, final Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        XmlObject encodedObject = encodeRequests(request);
        LOGGER.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    private XmlObject encodeRequests(final AbstractServiceRequest request) throws OwsExceptionReport {
        if (request instanceof GetResultTemplateRequest) {
            return createGetResultTemplateRequest((GetResultTemplateRequest) request);
        } else if (request instanceof GetResultRequest) {
            return createGetResultRequest((GetResultRequest) request);
        }
        throw new UnsupportedEncoderInputException(this, request);
    }

    private XmlObject createGetResultTemplateRequest(final GetResultTemplateRequest request) {
        final GetResultTemplateDocument getResultTemplateDoc =
                GetResultTemplateDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final GetResultTemplateType getResultTemplate = getResultTemplateDoc.addNewGetResultTemplate();
        getResultTemplate.setService(request.getService());
        getResultTemplate.setVersion(request.getVersion());
        getResultTemplate.setOffering(request.getOffering());
        getResultTemplate.setObservedProperty(request.getObservedProperty());
        return getResultTemplateDoc;
    }

    private XmlObject createGetResultRequest(final GetResultRequest request) throws OwsExceptionReport {
        final GetResultDocument getResultDoc =
                GetResultDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
        final GetResultType getResult = getResultDoc.addNewGetResult();
        getResult.setService(request.getService());
        getResult.setVersion(request.getVersion());
        getResult.setOffering(request.getOffering());
        getResult.setObservedProperty(request.getObservedProperty());
        if (request.isSetFeatureOfInterest()) {
            for (final String featureOfInterest : request.getFeatureIdentifiers()) {
                getResult.addFeatureOfInterest(featureOfInterest);
            }
        }
        if (request.hasTemporalFilter()) {
            for (final TemporalFilter temporalFilter : request.getTemporalFilter()) {
                createTemporalFilter(getResult.addNewTemporalFilter(), temporalFilter);
            }
        }
        if (request.isSetSpatialFilter()) {
            createSpatialFilter(getResult.addNewSpatialFilter(), request.getSpatialFilter());
        }

        return getResultDoc;
    }

    private void createTemporalFilter(final net.opengis.sos.x20.GetResultType.TemporalFilter temporalFilter,
            final TemporalFilter sosTemporalFilter) throws OwsExceptionReport {
        final Encoder<XmlObject, TemporalFilter> encoder =
                CodingRepository.getInstance().getEncoder(
                        CodingHelper.getEncoderKey(FilterConstants.NS_FES_2, sosTemporalFilter));
        final XmlObject encodedObject = encoder.encode(sosTemporalFilter);
        temporalFilter.set(encodedObject);
    }

    private void createSpatialFilter(final SpatialFilter spatialFilter,
            final org.n52.sos.ogc.filter.SpatialFilter sosSpatialFilter) throws OwsExceptionReport {
        final Encoder<XmlObject, org.n52.sos.ogc.filter.SpatialFilter> encoder =
                CodingRepository.getInstance().getEncoder(
                        CodingHelper.getEncoderKey(FilterConstants.NS_FES_2, sosSpatialFilter));
        final XmlObject encodedObject = encoder.encode(sosSpatialFilter);
        spatialFilter.set(encodedObject);
    }

}
