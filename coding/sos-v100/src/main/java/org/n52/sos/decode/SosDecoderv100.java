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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.ogc.SpatialOpsType;
import net.opengis.sos.x10.DescribeSensorDocument;
import net.opengis.sos.x10.DescribeSensorDocument.DescribeSensor;
import net.opengis.sos.x10.GetCapabilitiesDocument;
import net.opengis.sos.x10.GetCapabilitiesDocument.GetCapabilities;
import net.opengis.sos.x10.GetFeatureOfInterestDocument;
import net.opengis.sos.x10.GetFeatureOfInterestDocument.GetFeatureOfInterest;
import net.opengis.sos.x10.GetFeatureOfInterestDocument.GetFeatureOfInterest.Location;
import net.opengis.sos.x10.GetObservationByIdDocument;
import net.opengis.sos.x10.GetObservationByIdDocument.GetObservationById;
import net.opengis.sos.x10.GetObservationDocument;
import net.opengis.sos.x10.GetObservationDocument.GetObservation;
import net.opengis.sos.x10.GetObservationDocument.GetObservation.FeatureOfInterest;
import net.opengis.sos.x10.ResponseModeType.Enum;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.NotYetSupportedException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.service.AbstractServiceCommunicationObject;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 * 
 */
public class SosDecoderv100 implements Decoder<AbstractServiceCommunicationObject, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosDecoderv100.class);

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(CodingHelper.decoderKeysForElements(
            Sos1Constants.NS_SOS, GetCapabilitiesDocument.class, DescribeSensorDocument.class,
            GetObservationDocument.class, GetFeatureOfInterestDocument.class, GetObservationByIdDocument.class),
            CodingHelper.xmlDecoderKeysForOperation(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                    SosConstants.Operations.GetCapabilities, SosConstants.Operations.GetObservation,
                    SosConstants.Operations.GetFeatureOfInterest, SosConstants.Operations.GetObservationById,
                    SosConstants.Operations.DescribeSensor));

    public SosDecoderv100() {
        LOGGER.debug("Decoder for the following namespaces initialized successfully: {}!",
                Joiner.on(", ").join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public AbstractServiceCommunicationObject decode(XmlObject xmlObject) throws OwsExceptionReport {
        AbstractServiceCommunicationObject request = null;
        LOGGER.debug("REQUESTTYPE:" + xmlObject.getClass());

        /*
         * Add O&M 1.0.0 namespace to GetObservation document. XmlBeans removes
         * the namespace from the document because there are no om:... elements
         * in the document. But the validation fails if the <resultModel>
         * element is set with e.g. om:Measurement.
         */
        if (xmlObject instanceof GetObservationDocument) {
            XmlCursor cursor = xmlObject.newCursor();
            cursor.toFirstChild();
            cursor.insertNamespace(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM);
            cursor.dispose();
        }
        // validate document
        XmlHelper.validateDocument(xmlObject);

        // getCapabilities request
        if (xmlObject instanceof GetCapabilitiesDocument) {
            GetCapabilitiesDocument getCapsDoc = (GetCapabilitiesDocument) xmlObject;
            request = parseGetCapabilities(getCapsDoc);
        }

        // DescribeSensor request (still SOS 1.0 NS_URI
        else if (xmlObject instanceof DescribeSensorDocument) {
            DescribeSensorDocument descSensorDoc = (DescribeSensorDocument) xmlObject;
            request = parseDescribeSensor(descSensorDoc);
        }

        // getObservation request
        else if (xmlObject instanceof GetObservationDocument) {
            GetObservationDocument getObsDoc = (GetObservationDocument) xmlObject;
            request = parseGetObservation(getObsDoc);
        }

        // getFeatureOfInterest request
        else if (xmlObject instanceof GetFeatureOfInterestDocument) {
            GetFeatureOfInterestDocument getFoiDoc = (GetFeatureOfInterestDocument) xmlObject;
            request = parseGetFeatureOfInterest(getFoiDoc);
        }

        // getObservationById request
        else if (xmlObject instanceof GetObservationByIdDocument) {
            GetObservationByIdDocument getObsByIdDoc = (GetObservationByIdDocument) xmlObject;
            request = parseGetObservationById(getObsByIdDoc);
        }

        else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
        return request;
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
     * @throws OwsExceptionReport
     *             * If parsing the XmlBean failed
     */
    private AbstractServiceRequest<?> parseGetCapabilities(GetCapabilitiesDocument getCapsDoc) throws OwsExceptionReport {
        GetCapabilitiesRequest request = new GetCapabilitiesRequest();

        GetCapabilities getCaps = getCapsDoc.getGetCapabilities();

        request.setService(getCaps.getService());

        if (getCaps.getAcceptFormats() != null && getCaps.getAcceptFormats().sizeOfOutputFormatArray() != 0) {
            request.setAcceptFormats(Arrays.asList(getCaps.getAcceptFormats().getOutputFormatArray()));
        }

        if (getCaps.getAcceptVersions() != null && getCaps.getAcceptVersions().sizeOfVersionArray() != 0) {
            request.setAcceptVersions(Arrays.asList(getCaps.getAcceptVersions().getVersionArray()));
        }

        if (getCaps.getSections() != null && getCaps.getSections().getSectionArray().length != 0) {
            request.setSections(Arrays.asList(getCaps.getSections().getSectionArray()));
        }

        return request;
    }

    /**
     * parses the XmlBean representing the describeSensor request and creates a
     * DescribeSensor request
     * 
     * @param descSensorDoc
     *            XmlBean created from the incoming request stream
     * @return Returns SosDescribeSensorRequest representing the request
     * 
     * 
     * @throws OwsExceptionReport
     *             * If parsing the XmlBean failed
     */
    private AbstractServiceCommunicationObject parseDescribeSensor(DescribeSensorDocument descSensorDoc) {

        DescribeSensorRequest request = new DescribeSensorRequest();
        DescribeSensor descSensor = descSensorDoc.getDescribeSensor();
        request.setService(descSensor.getService());
        request.setVersion(descSensor.getVersion());
        //parse outputFormat through MediaType to ensure it's a mime type and eliminate whitespace variations
        request.setProcedureDescriptionFormat(MediaType.normalizeString(descSensor.getOutputFormat()));
        request.setProcedure(descSensor.getProcedure());
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
     * @throws OwsExceptionReport
     *             * If parsing the XmlBean failed
     */
    private AbstractServiceRequest<?> parseGetObservation(GetObservationDocument getObsDoc) throws OwsExceptionReport {
        GetObservationRequest getObsRequest = new GetObservationRequest();

        GetObservation getObs = getObsDoc.getGetObservation();

        getObsRequest.setService(getObs.getService());
        getObsRequest.setVersion(getObs.getVersion());
        getObsRequest.setOfferings(Arrays.asList(getObs.getOffering()));
        getObsRequest.setObservedProperties(Arrays.asList(getObs.getObservedPropertyArray()));
        getObsRequest.setProcedures(Arrays.asList(getObs.getProcedureArray()));
        getObsRequest.setTemporalFilters(parseTemporalFilters4GetObservation(getObs.getEventTimeArray()));
        getObsRequest.setSrsName(getObs.getSrsName());

        if (getObs.isSetFeatureOfInterest()) {
            FeatureOfInterest featureOfInterest = getObs.getFeatureOfInterest();
            if (featureOfInterest.isSetSpatialOps()) {
                Object filter = CodingHelper.decodeXmlElement(featureOfInterest.getSpatialOps());
                if (filter instanceof SpatialFilter) {
                    getObsRequest.setSpatialFilter((SpatialFilter) filter);
                }
            } else if (featureOfInterest.getObjectIDArray() != null) {
                Set<String> featureIdentifiers = Sets.newHashSet();
                for (String string : featureOfInterest.getObjectIDArray()) {
                   featureIdentifiers.add(string);
                }
                getObsRequest.setFeatureIdentifiers(Lists.newArrayList(featureIdentifiers));
            } 
        }

        // TODO implement result filtering
        if (getObs.isSetResult()) {
            throw new NotYetSupportedException("Result filtering");
        }

        // return error message
        if (getObs.isSetResponseFormat()) {
            try {
                String responseFormat = URLDecoder.decode(getObs.getResponseFormat(), "UTF-8");
                // parse responseFormat through MediaType to ensure it's a mime type and eliminate whitespace variations
                getObsRequest.setResponseFormat(MediaType.normalizeString(responseFormat));
            } catch (UnsupportedEncodingException e) {
                throw new NoApplicableCodeException().causedBy(e).withMessage("Error while decoding response format!");
            }

        } else {
            getObsRequest.setResponseFormat(OmConstants.CONTENT_TYPE_OM.toString());
        }
        if (getObs.isSetResultModel()) {
            getObsRequest.setResultModel(OMHelper.getObservationTypeFor(getObs.getResultModel()));
        }

        return getObsRequest;
    }

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
     * @throws OwsExceptionReport
     *             * if validation of the request failed
     */
    private AbstractServiceRequest<?> parseGetFeatureOfInterest(GetFeatureOfInterestDocument getFoiDoc)
            throws OwsExceptionReport {

        GetFeatureOfInterestRequest getFoiRequest = new GetFeatureOfInterestRequest();
        GetFeatureOfInterest getFoi = getFoiDoc.getGetFeatureOfInterest();
        getFoiRequest.setService(getFoi.getService());
        getFoiRequest.setVersion(getFoi.getVersion());
        getFoiRequest.setFeatureIdentifiers(Arrays.asList(getFoi.getFeatureOfInterestIdArray()));
        getFoiRequest.setSpatialFilters(parseSpatialFilters4GetFeatureOfInterest(getFoi.getLocation()));

        return getFoiRequest;
    }

    private AbstractServiceRequest<?> parseGetObservationById(GetObservationByIdDocument getObsByIdDoc)
            throws OwsExceptionReport {
        GetObservationByIdRequest getObsByIdRequest = new GetObservationByIdRequest();
        GetObservationById getObsById = getObsByIdDoc.getGetObservationById();
        getObsByIdRequest.setService(getObsById.getService());
        getObsByIdRequest.setVersion(getObsById.getVersion());
        if (getObsById.isSetResponseFormat()) {
            try {
                String responseFormat = URLDecoder.decode(getObsById.getResponseFormat(), "UTF-8");
                // parse responseFormat through MediaType to ensure it's a mime type and eliminate whitespace variations
                getObsByIdRequest.setResponseFormat(MediaType.normalizeString(responseFormat));
            } catch (UnsupportedEncodingException e) {
                throw new NoApplicableCodeException().causedBy(e).withMessage("Error while decoding response format!");
            }

        } else {
            getObsByIdRequest.setResponseFormat(OmConstants.CONTENT_TYPE_OM.toString());
        }
        Enum responseMode = getObsById.getResponseMode();
        if (responseMode != null && responseMode.toString().equalsIgnoreCase(SosConstants.RESPONSE_MODE_INLINE)) {
            getObsByIdRequest.setResponseMode(SosConstants.RESPONSE_MODE_INLINE);
        }
        if (getObsById.isSetResultModel()) {
            getObsByIdRequest.setResultModel(OMHelper.getObservationTypeFor(getObsById.getResultModel()));
        }
        getObsByIdRequest.setObservationIdentifier(Arrays.asList(getObsById.getObservationId()));
        return getObsByIdRequest;
    }

    /**
     * Parses the spatial filter of a GetObservation request.
     * 
     * @param spatialOpsType
     *            XmlBean representing the spatial filter parameter of the
     *            request
     * @return Returns SpatialFilter created from the passed foi request
     *         parameter
     * 
     * 
     * @throws OwsExceptionReport
     *             * if creation of the SpatialFilter failed
     */
    @Deprecated
    private SpatialFilter parseSpatialFilter4GetObservation(SpatialOpsType spatialOpsType)
            throws OwsExceptionReport {
//        if (spatialOpsType != null) {
//            if (spatialOpsType.getObjectIDArray() != null && spatialOpsType.getObjectIDArray().length > 0) {
//                throw new NoApplicableCodeException().withMessage("ObjectID filtering in featureOfInterest is "
//                        + "not supported. Only spatial filters are allowed.");
//            }        
//            if (spatialOpsType.getSpatialOps() != null) {
//                Object filter = CodingHelper.decodeXmlElement(spatialOpsType.getSpatialOps());
//                if (filter instanceof SpatialFilter) {
//                    return (SpatialFilter) filter;
//                }
//            }
//        }
        return null;
    }

    /**
     * Parses the spatial filters of a GetFeatureOfInterest request.
     * 
     * @param location
     *            XmlBean representing the spatial filter parameter of the
     *            request
     * @return Returns SpatialFilter created from the passed foi request
     *         parameter
     * 
     * 
     * @throws OwsExceptionReport
     *             * if creation of the SpatialFilter failed
     */
    private List<SpatialFilter> parseSpatialFilters4GetFeatureOfInterest(Location location) throws OwsExceptionReport {

        List<SpatialFilter> sosSpatialFilters = new LinkedList<SpatialFilter>();
        if (location != null && location.getSpatialOps() != null) {
            Object filter = CodingHelper.decodeXmlElement(location.getSpatialOps());
            if (filter instanceof SpatialFilter) {
                sosSpatialFilters.add((SpatialFilter) filter);
            }
        }
        return sosSpatialFilters;
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
     * @throws OwsExceptionReport
     *             * if parsing of the element failed
     */
    private List<TemporalFilter> parseTemporalFilters4GetObservation(GetObservation.EventTime[] temporalFilters)
            throws OwsExceptionReport {

        List<TemporalFilter> sosTemporalFilters = new LinkedList<TemporalFilter>();

        for (GetObservation.EventTime temporalFilter : temporalFilters) {
            Object filter = CodingHelper.decodeXmlElement(temporalFilter.getTemporalOps());
            if (filter instanceof TemporalFilter) {
                sosTemporalFilters.add((TemporalFilter) filter);
            }
        }
        return sosTemporalFilters;
    }

}
