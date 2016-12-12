/*
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
package org.n52.sos.decode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.sos.x20.GetCapabilitiesDocument;
import net.opengis.sos.x20.GetCapabilitiesType;
import net.opengis.sos.x20.GetFeatureOfInterestDocument;
import net.opengis.sos.x20.GetFeatureOfInterestType;
import net.opengis.sos.x20.GetObservationByIdDocument;
import net.opengis.sos.x20.GetObservationByIdType;
import net.opengis.sos.x20.GetObservationDocument;
import net.opengis.sos.x20.GetObservationType;
import net.opengis.sos.x20.GetResultDocument;
import net.opengis.sos.x20.GetResultResponseDocument;
import net.opengis.sos.x20.GetResultResponseType;
import net.opengis.sos.x20.GetResultTemplateDocument;
import net.opengis.sos.x20.GetResultTemplateResponseDocument;
import net.opengis.sos.x20.GetResultTemplateResponseType;
import net.opengis.sos.x20.GetResultTemplateType;
import net.opengis.sos.x20.GetResultType;
import net.opengis.sos.x20.InsertObservationDocument;
import net.opengis.sos.x20.InsertObservationType;
import net.opengis.sos.x20.InsertObservationType.Observation;
import net.opengis.sos.x20.InsertResultDocument;
import net.opengis.sos.x20.InsertResultTemplateDocument;
import net.opengis.sos.x20.InsertResultTemplateType;
import net.opengis.sos.x20.InsertResultType;
import net.opengis.sos.x20.ResultTemplateType;
import net.opengis.sos.x20.ResultTemplateType.ObservationTemplate;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.n52.janmayen.exception.CompositeException;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceCommunicationObject;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.request.InsertResultRequest;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.w3c.W3CConstants;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderXmlInputException;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;

import com.google.common.base.Joiner;

/**
 * @since 4.0.0
 *
 */
public class SosDecoderv20 extends AbstractSwesDecoderv20<OwsServiceCommunicationObject> implements Decoder<OwsServiceCommunicationObject, XmlObject> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SosDecoderv20.class);

    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(CodingHelper.decoderKeysForElements(
                            Sos2Constants.NS_SOS_20,
                            GetCapabilitiesDocument.class,
                            GetObservationDocument.class,
                            GetFeatureOfInterestDocument.class,
                            GetObservationByIdDocument.class,
                            InsertObservationDocument.class,
                            InsertResultTemplateDocument.class,
                            InsertResultDocument.class,
                            GetResultTemplateDocument.class,
                            GetResultDocument.class,
                            GetResultTemplateResponseDocument.class,
                            GetResultResponseDocument.class),
                   CodingHelper
                   .xmlDecoderKeysForOperation(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                                               SosConstants.Operations.GetCapabilities, SosConstants.Operations.GetObservation,
                                               SosConstants.Operations.GetFeatureOfInterest, SosConstants.Operations.GetObservationById,
                                               SosConstants.Operations.InsertObservation, Sos2Constants.Operations.InsertResultTemplate,
                                               Sos2Constants.Operations.InsertResult, Sos2Constants.Operations.GetResultTemplate,
                                               SosConstants.Operations.GetResult));

    public SosDecoderv20() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getKeys() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public OwsServiceCommunicationObject decode(final XmlObject xml) throws DecodingException {
        LOGGER.debug("REQUESTTYPE:" + xml.getClass());
        // validate document
        XmlHelper.validateDocument(xml);
        if (xml instanceof GetCapabilitiesDocument) {
            return parseGetCapabilities((GetCapabilitiesDocument) xml);
        } else if (xml instanceof GetObservationDocument) {
            return parseGetObservation((GetObservationDocument) xml);
        } else if (xml instanceof GetFeatureOfInterestDocument) {
            return parseGetFeatureOfInterest((GetFeatureOfInterestDocument) xml);
        } else if (xml instanceof GetObservationByIdDocument) {
            return parseGetObservationById((GetObservationByIdDocument) xml);
        } else if (xml instanceof InsertObservationDocument) {
            return parseInsertObservation((InsertObservationDocument) xml);
        } else if (xml instanceof InsertResultTemplateDocument) {
            return parseInsertResultTemplate((InsertResultTemplateDocument) xml);
        } else if (xml instanceof InsertResultDocument) {
            return parseInsertResult((InsertResultDocument) xml);
        } else if (xml instanceof GetResultTemplateDocument) {
            return parseGetResultTemplate((GetResultTemplateDocument) xml);
        } else if (xml instanceof GetResultDocument) {
            return parseGetResult((GetResultDocument) xml);
        } else if (xml instanceof GetResultTemplateResponseDocument) {
            return parseGetResultTemplateResponse((GetResultTemplateResponseDocument) xml);
        } else if (xml instanceof GetResultResponseDocument) {
            return parseGetResultResponse((GetResultResponseDocument) xml);
        } else {
            throw new UnsupportedDecoderXmlInputException(this, xml);
        }
    }

    /**
     * parses the XmlBean representing the getCapabilities request and creates a
     * SosGetCapabilities request
     *
     * @param getCapsDoc
     *            XmlBean created from the incoming request stream
     * @return Returns SosGetCapabilitiesRequest representing the request
     *
     *
     * @throws DecodingException
     *             * If parsing the XmlBean failed
     */
    private OwsServiceRequest parseGetCapabilities(final GetCapabilitiesDocument getCapsDoc)
            throws DecodingException {

        final GetCapabilitiesType getCapsType = getCapsDoc.getGetCapabilities2();
        final GetCapabilitiesRequest request = new GetCapabilitiesRequest(getCapsType.getService());

        if (getCapsType.getAcceptFormats() != null && getCapsType.getAcceptFormats().sizeOfOutputFormatArray() != 0) {
            request.setAcceptFormats(Arrays.asList(getCapsType.getAcceptFormats().getOutputFormatArray()));
        }

        if (getCapsType.getAcceptVersions() != null && getCapsType.getAcceptVersions().sizeOfVersionArray() != 0) {
            request.setAcceptVersions(Arrays.asList(getCapsType.getAcceptVersions().getVersionArray()));
        }

        if (getCapsType.getSections() != null && getCapsType.getSections().getSectionArray().length != 0) {
            request.setSections(Arrays.asList(getCapsType.getSections().getSectionArray()));
        }

        if (getCapsType.getExtensionArray() != null && getCapsType.getExtensionArray().length > 0) {
            request.setExtensions(parseExtensibleRequestExtension(getCapsType.getExtensionArray()));
        }

        return request;
    }

    /**
     * parses the XmlBean representing the getObservation request and creates a
     * SoSGetObservation request
     *
     * @param getObsDoc
     *            XmlBean created from the incoming request stream
     * @return Returns SosGetObservationRequest representing the request
     *
     *
     * @throws DecodingException
     *             * If parsing the XmlBean failed
     */
    private OwsServiceRequest parseGetObservation(final GetObservationDocument getObsDoc)
            throws DecodingException {
        final GetObservationRequest getObsRequest = new GetObservationRequest();
        final GetObservationType getObsType = getObsDoc.getGetObservation();
        // TODO: check
        getObsRequest.setService(getObsType.getService());
        getObsRequest.setVersion(getObsType.getVersion());
        getObsRequest.setOfferings(Arrays.asList(getObsType.getOfferingArray()));
        getObsRequest.setObservedProperties(Arrays.asList(getObsType.getObservedPropertyArray()));
        getObsRequest.setProcedures(Arrays.asList(getObsType.getProcedureArray()));
        getObsRequest.setTemporalFilters(parseTemporalFilters4GetObservation(getObsType.getTemporalFilterArray()));
        if (getObsType.isSetSpatialFilter()) {
            getObsRequest.setSpatialFilter(parseSpatialFilter4GetObservation(getObsType.getSpatialFilter()));
        }
        getObsRequest.setFeatureIdentifiers(Arrays.asList(getObsType.getFeatureOfInterestArray()));
        if (getObsType.isSetResponseFormat()) {
            try {
                final String responseFormat = URLDecoder.decode(getObsType.getResponseFormat(), "UTF-8");
                getObsRequest.setResponseFormat(responseFormat);
            } catch (final UnsupportedEncodingException e) {
                throw new DecodingException(e, "Error while encoding response format!");
            }
        }
        getObsRequest.setExtensions(parseExtensibleRequest(getObsType));
        return getObsRequest;
    }

//    private SwesExtensions parseSwesExtensions(final XmlObject[] extensionArray) throws OwsExceptionReport
//    {
//        final SwesExtensions extensions = new SwesExtensions();
//        for (final XmlObject xbSwesExtension : extensionArray) {
//
//            final Object obj = CodingHelper.decodeXmlElement(xbSwesExtension);
//            if (obj instanceof SwesExtension<?>) {
//                extensions.addSwesExtension((SwesExtension<?>) obj);
//            }
//        }
//        return extensions;
//    }

    /**
     * parses the passes XmlBeans document and creates a SOS
     * getFeatureOfInterest request
     *
     * @param getFoiDoc
     *            XmlBeans document representing the getFeatureOfInterest
     *            request
     * @return Returns SOS getFeatureOfInterest request
     *
     *
     * @throws DecodingException
     *             * if validation of the request failed
     */
    private OwsServiceRequest parseGetFeatureOfInterest(final GetFeatureOfInterestDocument getFoiDoc)
            throws DecodingException {

        final GetFeatureOfInterestRequest getFoiRequest = new GetFeatureOfInterestRequest();
        final GetFeatureOfInterestType getFoiType = getFoiDoc.getGetFeatureOfInterest();
        getFoiRequest.setService(getFoiType.getService());
        getFoiRequest.setVersion(getFoiType.getVersion());
        getFoiRequest.setFeatureIdentifiers(Arrays.asList(getFoiType.getFeatureOfInterestArray()));
        getFoiRequest.setObservedProperties(Arrays.asList(getFoiType.getObservedPropertyArray()));
        getFoiRequest.setProcedures(Arrays.asList(getFoiType.getProcedureArray()));
        getFoiRequest.setSpatialFilters(parseSpatialFilters4GetFeatureOfInterest(getFoiType.getSpatialFilterArray()));
        getFoiRequest.setExtensions(parseExtensibleRequest(getFoiType));
        return getFoiRequest;
    }

    private OwsServiceRequest parseGetObservationById(final GetObservationByIdDocument getObsByIdDoc)
            throws DecodingException {
        final GetObservationByIdRequest getObsByIdRequest = new GetObservationByIdRequest();
        final GetObservationByIdType getObsByIdType = getObsByIdDoc.getGetObservationById();
        getObsByIdRequest.setService(getObsByIdType.getService());
        getObsByIdRequest.setVersion(getObsByIdType.getVersion());
        getObsByIdRequest.setObservationIdentifier(Arrays.asList(getObsByIdType.getObservationArray()));
        getObsByIdRequest.setExtensions(parseExtensibleRequest(getObsByIdType));
        return getObsByIdRequest;
    }

    private OwsServiceRequest parseInsertObservation(final InsertObservationDocument insertObservationDoc)
            throws DecodingException {
        // set namespace for default XML type (e.g. xs:string, xs:integer,
        // xs:boolean, ...)
        // Fix for problem with XmlBeans: namespace is not set in child elements
        // when defined in root of request (SOAP)
        final XmlCursor cursor = insertObservationDoc.newCursor();
        if (cursor.toFirstChild() && cursor.namespaceForPrefix(W3CConstants.NS_XS_PREFIX) == null) {
            cursor.prefixForNamespace(W3CConstants.NS_XS);
        }
        cursor.dispose();
        final InsertObservationRequest insertObservationRequest = new InsertObservationRequest();
        final InsertObservationType insertObservationType = insertObservationDoc.getInsertObservation();
        insertObservationRequest.setService(insertObservationType.getService());
        insertObservationRequest.setVersion(insertObservationType.getVersion());
        if (insertObservationDoc.getInsertObservation().getOfferingArray() != null) {
            insertObservationRequest.setOfferings(Arrays.asList(insertObservationType.getOfferingArray()));
        }
        insertObservationRequest.setExtensions(parseExtensibleRequest(insertObservationType));

        if (insertObservationType.getObservationArray() != null) {
            final int length = insertObservationType.getObservationArray().length;
            final Map<String, Time> phenomenonTimes = new HashMap<>(length);
            final Map<String, TimeInstant> resultTimes = new HashMap<>(length);
            final Map<String, AbstractFeature> features = new HashMap<>(length);

            CompositeException exceptions = new CompositeException();
            for (final Observation observation : insertObservationType.getObservationArray()) {
                final Object decodedObject = decodeXmlElement(observation.getOMObservation());
                if (decodedObject instanceof OmObservation) {
                    final OmObservation sosObservation = (OmObservation) decodedObject;
                    checkAndAddPhenomenonTime(sosObservation.getPhenomenonTime(), phenomenonTimes);
                    checkAndAddResultTime(sosObservation.getResultTime(), resultTimes);
                    checkAndAddFeatures(sosObservation.getObservationConstellation().getFeatureOfInterest(), features);
                    insertObservationRequest.addObservation(sosObservation);
                } else {
                    exceptions.add(new DecodingException(Sos2Constants.InsertObservationParams.observation,
                                                "The requested observation type (%s) is not supported by this server!",
                                                observation.getOMObservation().getDomNode().getNodeName()));
                }
            }
            checkReferencedElements(insertObservationRequest.getObservations(), phenomenonTimes, resultTimes, features);
            try {
                exceptions.throwIfNotEmpty();
            } catch (CompositeException ex) {
                throw new DecodingException(ex, Sos2Constants.InsertObservationParams.observation);
            }
        } else {
            // TODO MissingMandatoryParameterException?
            throw new DecodingException(Sos2Constants.InsertObservationParams.observation,
                                        "The request does not contain an observation");
        }
        return insertObservationRequest;

    }

    private OwsServiceRequest parseInsertResultTemplate(final InsertResultTemplateDocument insertResultTemplateDoc)
            throws DecodingException {
        InsertResultTemplateRequest sosInsertResultTemplate = new InsertResultTemplateRequest();
        InsertResultTemplateType insertResultTemplate = insertResultTemplateDoc.getInsertResultTemplate();
        sosInsertResultTemplate.setService(insertResultTemplate.getService());
        sosInsertResultTemplate.setVersion(insertResultTemplate.getVersion());
        ResultTemplateType resultTemplate = insertResultTemplate.getProposedTemplate().getResultTemplate();
        sosInsertResultTemplate.setIdentifier(resultTemplate.getIdentifier());
        OmObservationConstellation sosObservationConstellation = parseObservationTemplate(resultTemplate.getObservationTemplate());
        sosObservationConstellation.addOffering(resultTemplate.getOffering());
        sosInsertResultTemplate.setObservationTemplate(sosObservationConstellation);
        sosInsertResultTemplate.setResultStructure(parseResultStructure(resultTemplate.getResultStructure().getAbstractDataComponent()));
        sosInsertResultTemplate.setResultEncoding(parseResultEncoding(resultTemplate.getResultEncoding().getAbstractEncoding()));
        sosInsertResultTemplate.setExtensions(parseExtensibleRequest(insertResultTemplate));
        return sosInsertResultTemplate;
    }

    private OwsServiceRequest parseInsertResult(final InsertResultDocument insertResultDoc)
            throws DecodingException {
        final InsertResultType insertResult = insertResultDoc.getInsertResult();
        final InsertResultRequest sosInsertResultRequest = new InsertResultRequest();
        sosInsertResultRequest.setService(insertResult.getService());
        sosInsertResultRequest.setVersion(insertResult.getVersion());
        sosInsertResultRequest.setTemplateIdentifier(insertResult.getTemplate());
        sosInsertResultRequest.setResultValues(parseResultValues(insertResult.getResultValues()));
        sosInsertResultRequest.setExtensions(parseExtensibleRequest(insertResult));
        return sosInsertResultRequest;
    }

    private OwsServiceRequest parseGetResult(final GetResultDocument getResultDoc) throws DecodingException {
        final GetResultType getResult = getResultDoc.getGetResult();
        final GetResultRequest sosGetResultRequest = new GetResultRequest();
        sosGetResultRequest.setService(getResult.getService());
        sosGetResultRequest.setVersion(getResult.getVersion());
        sosGetResultRequest.setOffering(getResult.getOffering());
        sosGetResultRequest.setObservedProperty(getResult.getObservedProperty());
        sosGetResultRequest.setFeatureIdentifiers(Arrays.asList(getResult.getFeatureOfInterestArray()));
        getResult.getFeatureOfInterestArray();
        if (getResult.isSetSpatialFilter()) {
            sosGetResultRequest.setSpatialFilter(parseSpatialFilter4GetResult(getResult.getSpatialFilter()));
        }
        sosGetResultRequest.setExtensions(parseExtensibleRequest(getResult));
        sosGetResultRequest.setTemporalFilter(parseTemporalFilters4GetResult(getResult.getTemporalFilterArray()));
        return sosGetResultRequest;
    }

    private OwsServiceRequest parseGetResultTemplate(final GetResultTemplateDocument getResultTemplateDoc)
            throws DecodingException {
        final GetResultTemplateType getResultTemplate = getResultTemplateDoc.getGetResultTemplate();
        final GetResultTemplateRequest sosGetResultTemplateRequest = new GetResultTemplateRequest();
        sosGetResultTemplateRequest.setService(getResultTemplate.getService());
        sosGetResultTemplateRequest.setVersion(getResultTemplate.getVersion());
        sosGetResultTemplateRequest.setOffering(getResultTemplate.getOffering());
        sosGetResultTemplateRequest.setObservedProperty(getResultTemplate.getObservedProperty());
        sosGetResultTemplateRequest.setExtensions(parseExtensibleRequest(getResultTemplate));
        return sosGetResultTemplateRequest;
    }

    private OwsServiceResponse parseGetResultTemplateResponse(
            final GetResultTemplateResponseDocument getResultTemplateResponseDoc) throws DecodingException {
        final GetResultTemplateResponse sosGetResultTemplateResponse = new GetResultTemplateResponse();
        final GetResultTemplateResponseType getResultTemplateResponse =
                getResultTemplateResponseDoc.getGetResultTemplateResponse();
        final SosResultEncoding resultEncoding =
                parseResultEncoding(getResultTemplateResponse.getResultEncoding().getAbstractEncoding());
        final SosResultStructure resultStructure =
                parseResultStructure(getResultTemplateResponse.getResultStructure().getAbstractDataComponent());
        sosGetResultTemplateResponse.setResultEncoding(resultEncoding);
        sosGetResultTemplateResponse.setResultStructure(resultStructure);
        return sosGetResultTemplateResponse;
    }

    private OwsServiceResponse parseGetResultResponse(final GetResultResponseDocument getResultResponseDoc)
            throws DecodingException {
        final GetResultResponse sosGetResultResponse = new GetResultResponse();
        final GetResultResponseType getResultResponse = getResultResponseDoc.getGetResultResponse();
        final String resultValues = parseResultValues(getResultResponse.getResultValues());
        // sosGetResultResponse.setResultValues(resultValues);
        return sosGetResultResponse;
    }

    /**
     * Parses the spatial filter of a GetObservation request.
     *
     * @param spatialFilter
     *            XmlBean representing the spatial filter parameter of the
     *            request
     * @return Returns SpatialFilter created from the passed foi request
     *         parameter
     *
     *
     * @throws DecodingException
     *             * if creation of the SpatialFilter failed
     */
    private SpatialFilter parseSpatialFilter4GetObservation(
            final net.opengis.sos.x20.GetObservationType.SpatialFilter spatialFilter) throws DecodingException {
        if (spatialFilter != null && spatialFilter.getSpatialOps() != null) {
            final Object filter = decodeXmlElement(spatialFilter.getSpatialOps());
            if (filter instanceof SpatialFilter) {
                return (SpatialFilter) filter;
            }
        }
        return null;
    }

    /**
     * Parses the spatial filters of a GetFeatureOfInterest request.
     *
     * @param spatialFilters
     *            XmlBean representing the spatial filter parameter of the
     *            request
     * @return Returns SpatialFilter created from the passed foi request
     *         parameter
     *
     *
     * @throws DecodingException
     *             * if creation of the SpatialFilter failed
     */
    private List<SpatialFilter> parseSpatialFilters4GetFeatureOfInterest(
            final net.opengis.sos.x20.GetFeatureOfInterestType.SpatialFilter[] spatialFilters)
            throws DecodingException {
        final List<SpatialFilter> sosSpatialFilters = new ArrayList<>(spatialFilters.length);
        for (final net.opengis.sos.x20.GetFeatureOfInterestType.SpatialFilter spatialFilter : spatialFilters) {
            final Object filter = decodeXmlElement(spatialFilter.getSpatialOps());
            if (filter instanceof SpatialFilter) {
                sosSpatialFilters.add((SpatialFilter) filter);
            }
        }
        return sosSpatialFilters;
    }

    private SpatialFilter parseSpatialFilter4GetResult(
            final net.opengis.sos.x20.GetResultType.SpatialFilter spatialFilter) throws DecodingException {
        if (spatialFilter != null && spatialFilter.getSpatialOps() != null) {
            final Object filter = decodeXmlElement(spatialFilter.getSpatialOps());
            if (filter instanceof SpatialFilter) {
                return (SpatialFilter) filter;
            }
        }
        return null;
    }

    /**
     * parses the Time of the requests and returns an array representing the
     * temporal filters
     *
     * @param temporalFilters
     *            array of XmlObjects representing the Time element in the
     *            request
     * @return Returns array representing the temporal filters
     *
     *
     * @throws DecodingException
     *             * if parsing of the element failed
     */
    private List<TemporalFilter> parseTemporalFilters4GetObservation(
            final net.opengis.sos.x20.GetObservationType.TemporalFilter[] temporalFilters) throws DecodingException {
        final List<TemporalFilter> sosTemporalFilters = new ArrayList<>(temporalFilters.length);
        for (final net.opengis.sos.x20.GetObservationType.TemporalFilter temporalFilter : temporalFilters) {
            final Object filter = decodeXmlElement(temporalFilter.getTemporalOps());
            if (filter instanceof TemporalFilter) {
                sosTemporalFilters.add((TemporalFilter) filter);
            }
        }
        return sosTemporalFilters;
    }

    private List<TemporalFilter> parseTemporalFilters4GetResult(
            final net.opengis.sos.x20.GetResultType.TemporalFilter[] temporalFilters) throws DecodingException {
        final List<TemporalFilter> sosTemporalFilters = new ArrayList<>(temporalFilters.length);
        for (final net.opengis.sos.x20.GetResultType.TemporalFilter temporalFilter : temporalFilters) {
            final Object filter = decodeXmlElement(temporalFilter.getTemporalOps());
            if (filter instanceof TemporalFilter) {
                sosTemporalFilters.add((TemporalFilter) filter);
            }
        }
        return sosTemporalFilters;
    }

    private OmObservationConstellation parseObservationTemplate(final ObservationTemplate observationTemplate)
            throws DecodingException {
        final Object decodedObject = decodeXmlElement(observationTemplate.getOMObservation());
        if (decodedObject instanceof OmObservation) {
            final OmObservation observation = (OmObservation) decodedObject;
            return observation.getObservationConstellation();
        }
        return null;
    }

    private SosResultStructure parseResultStructure(final XmlObject resultStructure) throws DecodingException {
        final Object decodedObject = decodeXmlElement(resultStructure);
        if (decodedObject instanceof SweAbstractDataComponent) {
            final SweAbstractDataComponent sosSweData = (SweAbstractDataComponent) decodedObject;
            return new SosResultStructure(sosSweData);
        } else {
            throw new DecodingException(Sos2Constants.InsertObservationParams.observation,
                                        "The requested result structure (%s) is not supported by this server!",
                                        resultStructure.getDomNode().getNodeName());
        }
    }

    private SosResultEncoding parseResultEncoding(final XmlObject resultEncoding) throws DecodingException {
        final Object decodedObject = decodeXmlElement(resultEncoding);
        if (decodedObject instanceof SweAbstractEncoding) {
            final SweAbstractEncoding sosSweEncoding = (SweAbstractEncoding) decodedObject;
            return new SosResultEncoding(sosSweEncoding);
        } else {
            throw new DecodingException(Sos2Constants.InsertObservationParams.observation,
                                        "The requested result encoding (%s) is not supported by this server!",
                                        resultEncoding.getDomNode().getNodeName());
        }
    }

    private String parseResultValues(final XmlObject resultValues) throws DecodingException {
        if (resultValues.schemaType() == XmlString.type) {
            return ((XmlString) resultValues).getStringValue().trim();
        } else if (resultValues.schemaType() == XmlObject.type) {
            final Node resultValuesNode = resultValues.getDomNode();
            if (resultValuesNode.hasChildNodes()) {
                final NodeList childNodes = resultValuesNode.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    final Node childNode = childNodes.item(i);
                    if (childNode.getNodeType() == Node.TEXT_NODE) {
                        return childNode.getNodeValue().trim();
                    }
                }
            }
            throw new DecodingException(Sos2Constants.InsertResultParams.resultValues,
                                        "The value for the parameter '%s' is missing in the request!",
                                        Sos2Constants.InsertResultParams.resultValues);
        } else {
            throw new DecodingException("The requested resultValue type is not supported");
        }
    }

    private void checkAndAddPhenomenonTime(final Time phenomenonTime, final Map<String, Time> phenomenonTimes) {
        if (!phenomenonTime.isReferenced()) {
            phenomenonTimes.put(phenomenonTime.getGmlId(), phenomenonTime);
        }
    }

    private void checkAndAddResultTime(final TimeInstant resultTime, final Map<String, TimeInstant> resultTimes) {
        if (!resultTime.isReferenced()) {
            resultTimes.put(resultTime.getGmlId(), resultTime);
        }
    }

    private void checkAndAddFeatures(final AbstractFeature featureOfInterest,
            final Map<String, AbstractFeature> features) {
        if (!featureOfInterest.isReferenced()) {
            features.put(featureOfInterest.getGmlId(), featureOfInterest);
        }
    }

    private void checkReferencedElements(final List<OmObservation> observations,
            final Map<String, Time> phenomenonTimes, final Map<String, TimeInstant> resultTimes,
            final Map<String, AbstractFeature> features) throws DecodingException {
        for (final OmObservation observation : observations) {
            // phenomenonTime
            final Time phenomenonTime = observation.getPhenomenonTime();
            if (phenomenonTime.isReferenced()) {
                observation.getValue().setPhenomenonTime(phenomenonTimes.get(phenomenonTime.getGmlId()));
            }
            // resultTime
            final TimeInstant resultTime = observation.getResultTime();
            if (resultTime.isReferenced()) {
                if (resultTimes.containsKey(resultTime.getGmlId())) {
                    observation.setResultTime(resultTimes.get(resultTime.getGmlId()));
                } else if (phenomenonTimes.containsKey(resultTime.getGmlId())) {
                    final Time iTime = phenomenonTimes.get(resultTime.getGmlId());
                    if (iTime instanceof TimeInstant) {
                        observation.setResultTime((TimeInstant) iTime);
                    } else if (iTime instanceof TimePeriod) {
                        final TimePeriod timePeriod = (TimePeriod) iTime;
                        observation.setResultTime(new TimeInstant(timePeriod.getEnd()));
                    } else {
                        throw new DecodingException("observation.resultTime", "The time value type is not supported");
                    }

                }
            }
            // featureOfInterest
            final AbstractFeature featureOfInterest = observation.getObservationConstellation().getFeatureOfInterest();
            if (featureOfInterest.isReferenced()) {
                observation.getObservationConstellation().setFeatureOfInterest(
                        features.get(featureOfInterest.getGmlId()));
            }

        }
    }
}
