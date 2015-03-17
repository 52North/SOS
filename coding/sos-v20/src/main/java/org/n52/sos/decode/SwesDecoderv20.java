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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.gml.x32.FeaturePropertyType;
import net.opengis.sos.x20.SosInsertionMetadataPropertyType;
import net.opengis.sos.x20.SosInsertionMetadataType;
import net.opengis.swes.x20.DeleteSensorDocument;
import net.opengis.swes.x20.DeleteSensorType;
import net.opengis.swes.x20.DescribeSensorDocument;
import net.opengis.swes.x20.DescribeSensorType;
import net.opengis.swes.x20.InsertSensorDocument;
import net.opengis.swes.x20.InsertSensorType;
import net.opengis.swes.x20.InsertSensorType.Metadata;
import net.opengis.swes.x20.InsertSensorType.RelatedFeature;
import net.opengis.swes.x20.SensorDescriptionType;
import net.opengis.swes.x20.UpdateSensorDescriptionDocument;
import net.opengis.swes.x20.UpdateSensorDescriptionType;
import net.opengis.swes.x20.UpdateSensorDescriptionType.Description;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DecoderResponseUnsupportedException;
import org.n52.sos.exception.ows.concrete.UnsupportedDecoderInputException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.Sos2Constants.UpdateSensorDescriptionParams;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.ogc.swes.SwesFeatureRelationship;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.DeleteSensorRequest;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.request.UpdateSensorRequest;
import org.n52.sos.service.AbstractServiceCommunicationObject;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * @since 4.0.0
 * 
 */
public class SwesDecoderv20 extends AbstractSwesDecoderv20 implements Decoder<AbstractServiceCommunicationObject, XmlObject> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwesDecoderv20.class);

    @SuppressWarnings("unchecked")
    private static final Set<DecoderKey> DECODER_KEYS = CollectionHelper.union(CodingHelper.decoderKeysForElements(
            SwesConstants.NS_SWES_20, DescribeSensorDocument.class, InsertSensorDocument.class,
            UpdateSensorDescriptionDocument.class, DeleteSensorDocument.class), CodingHelper
            .xmlDecoderKeysForOperation(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                    SosConstants.Operations.DescribeSensor, Sos2Constants.Operations.InsertSensor,
                    Sos2Constants.Operations.UpdateSensorDescription, Sos2Constants.Operations.DeleteSensor));

    public SwesDecoderv20() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
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
    public AbstractServiceRequest<?> decode(final XmlObject xmlObject) throws OwsExceptionReport {
        LOGGER.debug("REQUESTTYPE:" + xmlObject.getClass());
        XmlHelper.validateDocument(xmlObject);
        if (xmlObject instanceof DescribeSensorDocument) {
            return parseDescribeSensor((DescribeSensorDocument) xmlObject);
        } else if (xmlObject instanceof InsertSensorDocument) {
            return parseInsertSensor((InsertSensorDocument) xmlObject);
        } else if (xmlObject instanceof UpdateSensorDescriptionDocument) {
            return parseUpdateSensorDescription((UpdateSensorDescriptionDocument) xmlObject);
        } else if (xmlObject instanceof DeleteSensorDocument) {
            return parseDeleteSensor((DeleteSensorDocument) xmlObject);
        } else {
            throw new UnsupportedDecoderInputException(this, xmlObject);
        }
    }

    /**
     * parses the passes XmlBeans document and creates a SOS describeSensor
     * request
     * 
     * @param xbDescSenDoc
     *            XmlBeans document representing the describeSensor request
     * @return Returns SOS describeSensor request
     * 
     * 
     * @throws OwsExceptionReport
     *             * if validation of the request failed
     */
    private AbstractServiceRequest<?> parseDescribeSensor(final DescribeSensorDocument xbDescSenDoc)
            throws OwsExceptionReport {
        final DescribeSensorRequest descSensorRequest = new DescribeSensorRequest();
        final DescribeSensorType xbDescSensor = xbDescSenDoc.getDescribeSensor();
        descSensorRequest.setService(xbDescSensor.getService());
        descSensorRequest.setVersion(xbDescSensor.getVersion());
        descSensorRequest.setProcedure(xbDescSensor.getProcedure());
        descSensorRequest.setProcedureDescriptionFormat(xbDescSensor.getProcedureDescriptionFormat());
        if (xbDescSensor.isSetValidTime()) {
            descSensorRequest.setValidTime(getValidTime(xbDescSensor.getValidTime()));
        }
     // extensions
        descSensorRequest.setExtensions(parseExtensibleRequest(xbDescSensor));
        return descSensorRequest;
    }

    private AbstractServiceRequest<?> parseInsertSensor(final InsertSensorDocument xbInsSensDoc)
            throws OwsExceptionReport {
        final InsertSensorRequest request = new InsertSensorRequest();
        final InsertSensorType xbInsertSensor = xbInsSensDoc.getInsertSensor();
        request.setService(xbInsertSensor.getService());
        request.setVersion(xbInsertSensor.getVersion());
        // format
        request.setProcedureDescriptionFormat(xbInsertSensor.getProcedureDescriptionFormat());
        // observable properties
        if (CollectionHelper.isNotNullOrEmpty(xbInsertSensor.getObservablePropertyArray())) {
            request.setObservableProperty(Arrays.asList(xbInsertSensor.getObservablePropertyArray()));
        }
        // related features
        if (CollectionHelper.isNotNullOrEmpty(xbInsertSensor.getRelatedFeatureArray())) {
            request.setRelatedFeature(parseRelatedFeature(xbInsertSensor.getRelatedFeatureArray()));
        }
        // metadata
        if (CollectionHelper.isNotNullOrEmpty(xbInsertSensor.getMetadataArray())) {
            request.setMetadata(parseMetadata(xbInsertSensor.getMetadataArray()));
        }
        // extensions
        request.setExtensions(parseExtensibleRequest(xbInsertSensor));
        try {
            final XmlObject xbProcedureDescription =
                    XmlObject.Factory.parse(getNodeFromNodeList(xbInsertSensor.getProcedureDescription().getDomNode()
                            .getChildNodes()));

            final Decoder<?, XmlObject> decoder =
                    CodingRepository.getInstance().getDecoder(
                            new XmlNamespaceDecoderKey(xbInsertSensor.getProcedureDescriptionFormat(),
                                    xbProcedureDescription.getClass()));
            if (decoder == null) {
                SosHelper.checkProcedureDescriptionFormat(xbInsertSensor.getProcedureDescriptionFormat(),
                        request.getService(), request.getVersion());
                // if
                // (StringHelper.isNullOrEmpty(xbInsertSensor.getProcedureDescriptionFormat()))
                // {
                //
                // } else {
                // throw new InvalidParameterValueException().at(
                // Sos2Constants.InsertSensorParams.procedureDescriptionFormat).withMessage(
                // "The requested %s is not supported!",
                // Sos2Constants.InsertSensorParams.procedureDescriptionFormat.name());
                // }
            }
            final Object decodedProcedureDescription = decoder.decode(xbProcedureDescription);
            if (decodedProcedureDescription instanceof SosProcedureDescription) {
                request.setProcedureDescription((SosProcedureDescription) decodedProcedureDescription);
            }
        } catch (final XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                    "Error while parsing procedure description of InsertSensor request!");
        }
        return request;
    }

    private AbstractServiceRequest<?> parseDeleteSensor(final DeleteSensorDocument xbDelSenDoc) throws OwsExceptionReport {
        final DeleteSensorRequest request = new DeleteSensorRequest();
        DeleteSensorType deleteSensor = xbDelSenDoc.getDeleteSensor();
        request.setService(deleteSensor.getService());
        request.setVersion(deleteSensor.getVersion());
        request.setProcedureIdentifier(deleteSensor.getProcedure());
     // extensions
        request.setExtensions(parseExtensibleRequest(deleteSensor));
        return request;
    }

    /**
     * parses the Xmlbeans UpdateSensorDescription document to a SOS request.
     * 
     * @param xbUpSenDoc
     *            UpdateSensorDescription document
     * @return SOS UpdateSensor request
     * 
     * 
     * @throws OwsExceptionReport
     *             * if an error occurs.
     */
    private AbstractServiceRequest<?> parseUpdateSensorDescription(final UpdateSensorDescriptionDocument xbUpSenDoc)
            throws OwsExceptionReport {
        final UpdateSensorRequest request = new UpdateSensorRequest();
        final UpdateSensorDescriptionType xbUpdateSensor = xbUpSenDoc.getUpdateSensorDescription();
        request.setService(xbUpdateSensor.getService());
        request.setVersion(xbUpdateSensor.getVersion());
        request.setProcedureIdentifier(xbUpdateSensor.getProcedure());
        request.setProcedureDescriptionFormat(xbUpdateSensor.getProcedureDescriptionFormat());
     // extensions
        request.setExtensions(parseExtensibleRequest(xbUpdateSensor));
        for (final Description description : xbUpdateSensor.getDescriptionArray()) {
            SensorDescriptionType sensorDescription = description.getSensorDescription();

            try {
                // TODO exception if valid time is set
                final XmlObject xmlObject =
                        XmlObject.Factory.parse(getNodeFromNodeList(sensorDescription.getData().getDomNode()
                                .getChildNodes()));
                final Decoder<?, XmlObject> decoder =
                        CodingRepository.getInstance().getDecoder(CodingHelper.getDecoderKey(xmlObject));
                if (decoder == null) {
                    throw new InvalidParameterValueException().at(
                            UpdateSensorDescriptionParams.procedureDescriptionFormat).withMessage(
                            "The requested procedureDescritpionFormat is not supported!");
                }

                final Object decodedObject = decoder.decode(xmlObject);
                if (decodedObject instanceof SosProcedureDescription) {
                    SosProcedureDescription sosProcedureDescription = (SosProcedureDescription) decodedObject;
                    if (sensorDescription.isSetValidTime()) {
                        sosProcedureDescription.setValidTime(getValidTime(sensorDescription.getValidTime()));
                    }
                    request.addProcedureDescriptionString(sosProcedureDescription);
                }
            } catch (final XmlException xmle) {
                throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                        "Error while parsing procedure description of UpdateSensor request!");
            }
        }
        return request;
    }

    private SosInsertionMetadata parseMetadata(final Metadata[] metadataArray) throws OwsExceptionReport {

        final SosInsertionMetadata sosMetadata = new SosInsertionMetadata();
        try {
            for (final Metadata metadata : metadataArray) {
                SosInsertionMetadataType xbSosInsertionMetadata = null;
                if (metadata.getInsertionMetadata() != null
                        && metadata.getInsertionMetadata().schemaType() == SosInsertionMetadataType.type) {
                    xbSosInsertionMetadata = (SosInsertionMetadataType) metadata.getInsertionMetadata();
                } else {
                    if (metadata.getDomNode().hasChildNodes()) {
                        final Node node = getNodeFromNodeList(metadata.getDomNode().getChildNodes());
                        final SosInsertionMetadataPropertyType xbMetadata =
                                SosInsertionMetadataPropertyType.Factory.parse(node);
                        xbSosInsertionMetadata = xbMetadata.getSosInsertionMetadata();
                    }
                }
                if (xbSosInsertionMetadata != null) {
                    // featureOfInterest types
                    if (xbSosInsertionMetadata.getFeatureOfInterestTypeArray() != null) {
                        sosMetadata.setFeatureOfInterestTypes(Arrays.asList(xbSosInsertionMetadata
                                .getFeatureOfInterestTypeArray()));
                    }
                    // observation types
                    if (xbSosInsertionMetadata.getObservationTypeArray() != null) {
                        sosMetadata
                                .setObservationTypes(Arrays.asList(xbSosInsertionMetadata.getObservationTypeArray()));
                    }
                }
            }
        } catch (final XmlException xmle) {
            throw new NoApplicableCodeException().causedBy(xmle).withMessage(
                    "An error occurred while parsing the metadata in the http post request");
        }
        return sosMetadata;
    }

    private List<SwesFeatureRelationship> parseRelatedFeature(final RelatedFeature[] relatedFeatureArray)
            throws OwsExceptionReport {
        final List<SwesFeatureRelationship> sosRelatedFeatures =
                new ArrayList<SwesFeatureRelationship>(relatedFeatureArray.length);
        for (final RelatedFeature relatedFeature : relatedFeatureArray) {
            final SwesFeatureRelationship sosFeatureRelationship = new SwesFeatureRelationship();

            final FeaturePropertyType fpt = relatedFeature.getFeatureRelationship().getTarget();
            if (fpt.getHref() != null && !fpt.getHref().isEmpty()) {
                final String identifier = fpt.getHref();
                final SamplingFeature feature = new SamplingFeature(new CodeWithAuthority(identifier));
                if (fpt.getTitle() != null && !fpt.getTitle().isEmpty()) {
                    feature.setName(Lists.newArrayList(new CodeType(fpt.getTitle())));
                }
                if (checkForRequestUrl(fpt.getHref())) {
                    feature.setUrl(fpt.getHref());
                }
                feature.setFeatureType(OGCConstants.UNKNOWN);
                sosFeatureRelationship.setFeature(feature);
            } else {
                final Object decodedObject = CodingHelper.decodeXmlElement(fpt);
                if (decodedObject instanceof SamplingFeature) {
                    sosFeatureRelationship.setFeature((SamplingFeature) decodedObject);
                } else {
                    throw new DecoderResponseUnsupportedException(fpt.xmlText(), decodedObject);
                }
            }
            sosFeatureRelationship.setRole(relatedFeature.getFeatureRelationship().getRole());
            sosRelatedFeatures.add(sosFeatureRelationship);
        }
        return sosRelatedFeatures;
    }

    private boolean checkForRequestUrl(final String href) {
        return href.toLowerCase().contains("request=");
    }

    private Node getNodeFromNodeList(final NodeList nodeList) {
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    return nodeList.item(i);
                }
            }
        }
        return null;
    }

    private Time getValidTime(net.opengis.swes.x20.DescribeSensorType.ValidTime validTime) throws OwsExceptionReport {
        Object decodeXmlElement = CodingHelper.decodeXmlElement(validTime.getAbstractTimeGeometricPrimitive());
        if (decodeXmlElement instanceof Time) {
            return (Time) decodeXmlElement;
        } else {
            throw new InvalidParameterValueException().at(Sos2Constants.DescribeSensorParams.validTime).withMessage(
                    "The validTime element ({}) is not supported",
                    validTime.getAbstractTimeGeometricPrimitive().schemaType());
        }
    }

    private Time getValidTime(net.opengis.swes.x20.SensorDescriptionType.ValidTime validTime)
            throws OwsExceptionReport {
        Object decodeXmlElement = CodingHelper.decodeXmlElement(validTime.getAbstractTimeGeometricPrimitive());
        if (decodeXmlElement instanceof Time) {
            return (Time) decodeXmlElement;
        } else {
            throw new InvalidParameterValueException().at(Sos2Constants.UpdateSensorDescriptionParams.validTime)
                    .withMessage("The validTime element ({}) is not supported",
                            validTime.getAbstractTimeGeometricPrimitive().schemaType());
        }
    }
}
