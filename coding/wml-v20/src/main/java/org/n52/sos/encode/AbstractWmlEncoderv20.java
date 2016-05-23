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

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.om.x20.OMObservationDocument;
import net.opengis.om.x20.OMObservationType;
import net.opengis.samplingSpatial.x20.ShapeType;
import net.opengis.waterml.x20.CollectionDocument;
import net.opengis.waterml.x20.CollectionType;
import net.opengis.waterml.x20.MonitoringPointDocument;
import net.opengis.waterml.x20.MonitoringPointType;
import net.opengis.waterml.x20.ObservationProcessDocument;
import net.opengis.waterml.x20.ObservationProcessType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.wml.ObservationProcess;
import org.n52.sos.ogc.wml.WaterMLConstants;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.util.http.MediaType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract encoder class for WaterML 2.0
 * 
 * @since 4.0.0
 */
public abstract class AbstractWmlEncoderv20 extends AbstractOmEncoderv20 implements ProcedureEncoder<XmlObject, Object> {

    @SuppressWarnings("unchecked")
    protected static final Set<EncoderKey> DEFAULT_ENCODER_KEYS = CollectionHelper.union(CodingHelper.encoderKeysForElements(
            WaterMLConstants.NS_WML_20, AbstractFeature.class), CodingHelper.encoderKeysForElements(
                    WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING, ObservationProcess.class));
    
    private static final Map<String, ImmutableMap<String, Set<String>>> SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS =
            ImmutableMap.of(
                    SosConstants.SOS,
                    ImmutableMap
                            .<String, Set<String>> builder()
                            .put(Sos2Constants.SERVICEVERSION,
                                    ImmutableSet.of(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING)).build());

    
    protected static Set<EncoderKey> getDefaultEncoderKeys() {
        return Collections.unmodifiableSet(DEFAULT_ENCODER_KEYS);
    }

    @Override
    protected boolean convertEncodedProcedure() {
        return true;
    }

    @Override
    public boolean isObservationAndMeasurmentV20Type() {
        return true;
    }

    @Override
    public boolean shouldObservationsWithSameXBeMerged() {
        return true;
    }
    
    @Override
    public Set<String> getSupportedProcedureDescriptionFormats(String service, String version) {
        if (SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.containsKey(service)
                && SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.get(service).containsKey(version)) {
            return SUPPORTED_PROCEDURE_DESCRIPTION_FORMATS.get(service).get(version);
        }
        return Collections.emptySet();
    }

    @Override
    public MediaType getContentType() {
        return WaterMLConstants.WML_CONTENT_TYPE;
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (element instanceof ObservationProcess) {
            return createObservationProcess((ObservationProcess) element, additionalValues);
        } else if (element instanceof AbstractFeature) {
            return encodeAbstractFeature((AbstractFeature) element, additionalValues);
        } else {
            return super.encode(element, additionalValues);
        }
    }

    private XmlObject encodeAbstractFeature(AbstractFeature abstractFeature, Map<HelperValues, String> additionalValues)
            throws UnsupportedEncoderInputException, OwsExceptionReport {
        if (abstractFeature instanceof OmObservation) {
            return super.encode(abstractFeature, additionalValues);
        } else {
            return createMonitoringPoint(abstractFeature);
        }
    }

    @Override
    public String getDefaultFeatureEncodingNamespace() {
        return WaterMLConstants.NS_WML_20;
    }

    @Override
    protected String getDefaultProcedureEncodingNamspace() {
        return WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING;
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        super.addNamespacePrefixToMap(nameSpacePrefixMap);
        nameSpacePrefixMap.put(WaterMLConstants.NS_WML_20, WaterMLConstants.NS_WML_20_PREFIX);
    }

    /**
     * Encodes a SOS GetObservationResponse to a single WaterML 2.0 observation
     * or to a WaterML 1.0 ObservationCollection
     * 
     * @param getObservationResonse
     *            SOS GetObservationResponse
     * @return Encoded response
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected XmlObject createWmlGetObservationResponse(GetObservationResponse getObservationResonse)
            throws OwsExceptionReport {
        // TODO: set schemaLocation if final
        Map<String, String> gmlID4sfIdentifier = Maps.newHashMap();
        int sfIdCounter = 1;
        if (getObservationResonse.getObservationCollection() != null
                && !getObservationResonse.getObservationCollection().isEmpty()) {
            Collection<OmObservation> sosObservations = getObservationResonse.getObservationCollection();
            if (sosObservations.size() == 1) {
                OMObservationDocument omObservationDoc =
                        OMObservationDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                for (OmObservation sosObservation : sosObservations) {
                    Map<HelperValues, String> foiHelper =
                            new EnumMap<SosConstants.HelperValues, String>(SosConstants.HelperValues.class);
                    String gmlId = "sf_" + sfIdCounter;
                    foiHelper.put(HelperValues.GMLID, gmlId);
                    omObservationDoc.setOMObservation((OMObservationType) createOmObservation(sosObservation,
                            foiHelper));
                }
                return omObservationDoc;
            } else {
                CollectionDocument xmlCollectionDoc =
                        CollectionDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
                CollectionType wmlCollection = xmlCollectionDoc.addNewCollection();
                for (OmObservation sosObservation : sosObservations) {
                    Map<HelperValues, String> foiHelper =
                            new EnumMap<SosConstants.HelperValues, String>(SosConstants.HelperValues.class);
                    String gmlId;
                    // FIXME CodeWithAuthority VS. String keys
                    if (gmlID4sfIdentifier.containsKey(sosObservation.getObservationConstellation()
                            .getFeatureOfInterest().getIdentifierCodeWithAuthority())) {
                        gmlId =
                                gmlID4sfIdentifier.get(sosObservation.getObservationConstellation()
                                        .getFeatureOfInterest().getIdentifierCodeWithAuthority());
                        foiHelper.put(HelperValues.EXIST_FOI_IN_DOC, Boolean.toString(true));
                    } else {
                        gmlId = "sf_" + sfIdCounter;
                        gmlID4sfIdentifier.put(sosObservation.getObservationConstellation().getFeatureOfInterest()
                                .getIdentifierCodeWithAuthority().getValue(), gmlId);
                        foiHelper.put(HelperValues.EXIST_FOI_IN_DOC, Boolean.toString(false));
                    }
                    foiHelper.put(HelperValues.GMLID, gmlId);
                    wmlCollection.addNewObservationMember().setOMObservation(
                            (OMObservationType) createOmObservation(sosObservation, foiHelper));
                }
                return xmlCollectionDoc;
            }

        } else {
            // TODO: HydrologieProfile-Exception
            throw new NoApplicableCodeException().withMessage("Combination does not exists!");
        }
    }

    /**
     * Creates a WaterML 2.0 MonitoringPoint XML object from SOS feature object
     * 
     * @param absFeature
     *            SOS feature
     * @return WaterML 2.0 MonitoringPoint XML object
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected XmlObject createMonitoringPoint(AbstractFeature absFeature) throws OwsExceptionReport {
        if (absFeature instanceof SamplingFeature) {
            SamplingFeature sampFeat = (SamplingFeature) absFeature;
            StringBuilder builder = new StringBuilder();
            builder.append("mp_");
            builder.append(JavaHelper.generateID(absFeature.getIdentifierCodeWithAuthority().getValue()));
            absFeature.setGmlId(builder.toString());

            MonitoringPointDocument monitoringPointDoc =
                    MonitoringPointDocument.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            if (sampFeat.getXmlDescription() != null) {
                try {
                    XmlObject feature = XmlObject.Factory.parse(sampFeat.getXmlDescription());
                    if (XmlHelper.getNamespace(feature).equals(WaterMLConstants.NS_WML_20)) {
                        if (feature instanceof MonitoringPointDocument) {
                            monitoringPointDoc = (MonitoringPointDocument) feature;
                        } else if (feature instanceof MonitoringPointType) {
                            monitoringPointDoc.setSFSpatialSamplingFeature((MonitoringPointType) feature);
                        }
                        XmlHelper.updateGmlIDs(monitoringPointDoc.getDomNode(), absFeature.getGmlId(), null);
                        return monitoringPointDoc;
                    }
                } catch (XmlException xmle) {
                    throw new NoApplicableCodeException()
                            .causedBy(xmle)
                            .withMessage(
                                    "Error while encoding GetFeatureOfInterest response, invalid samplingFeature description!");
                }
            }
            MonitoringPointType monitoringPoint = monitoringPointDoc.addNewMonitoringPoint();
            // set gml:id
            monitoringPoint.setId(absFeature.getGmlId());

            if (sampFeat.isSetIdentifier()
                    && SosHelper.checkFeatureOfInterestIdentifierForSosV2(sampFeat.getIdentifierCodeWithAuthority().getValue(),
                            Sos2Constants.SERVICEVERSION)) {
                XmlObject xmlObject = CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sampFeat.getIdentifierCodeWithAuthority());
                if (xmlObject != null) {
                    monitoringPoint.addNewIdentifier().set(xmlObject);
                }
            }

            if (sampFeat.isSetName()) {
                for (CodeType sosName : sampFeat.getName()) {
                    monitoringPoint.addNewName().set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sosName));
                }
            }

            // set type
            // TODO: check if special definition
            // monitoringPoint.addNewType().setHref(sampFeat.getFeatureType());

            // set sampledFeatures
            // TODO: CHECK
            if (sampFeat.getSampledFeatures() != null && !sampFeat.getSampledFeatures().isEmpty()) {
                if (sampFeat.getSampledFeatures().size() == 1) {
                    XmlObject encodeObjectToXml =
                            CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sampFeat.getSampledFeatures()
                                    .get(0));
                    monitoringPoint.addNewSampledFeature().set(encodeObjectToXml);
                } else {
                    FeatureCollection featureCollection = new FeatureCollection();
                    featureCollection.setGmlId("sampledFeatures_" + absFeature.getGmlId());
                    for (AbstractFeature sampledFeature : sampFeat.getSampledFeatures()) {
                        featureCollection.addMember(sampledFeature);
                    }
                    XmlObject encodeObjectToXml =
                            CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, featureCollection);
                    monitoringPoint.addNewSampledFeature().set(encodeObjectToXml);
                }
            } else {
                monitoringPoint.addNewSampledFeature().setHref(GmlConstants.NIL_UNKNOWN);
            }

            if (sampFeat.isSetParameter()) {
                addParameter(monitoringPoint, sampFeat);
            }

            // set position
            ShapeType xbShape = monitoringPoint.addNewShape();
            Encoder<XmlObject, Geometry> encoder =
                    CodingRepository.getInstance().getEncoder(
                            CodingHelper.getEncoderKey(GmlConstants.NS_GML_32, sampFeat.getGeometry()));
            if (encoder != null) {
                Map<HelperValues, String> gmlAdditionalValues = new EnumMap<HelperValues, String>(HelperValues.class);
                gmlAdditionalValues.put(HelperValues.GMLID, absFeature.getGmlId());
                XmlObject xmlObject = encoder.encode(sampFeat.getGeometry(), gmlAdditionalValues);
                xbShape.addNewAbstractGeometry().set(xmlObject);
                XmlHelper.substituteElement(xbShape.getAbstractGeometry(), xmlObject);
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("Error while encoding geometry for feature, needed encoder is missing!");
            }
            return monitoringPointDoc;
        }
        throw new UnsupportedEncoderInputException(this, absFeature);
    }

    /**
     * Creates an WaterML 2.0 ObservationProcess XML object from SOS
     * ObservationProcess object
     * 
     * @param procedure
     *            SOS ObservationProcess
     * @param additionalValues
     *            Additional values
     * @return WaterML 2.0 ObservationProcess XML object
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected ObservationProcessDocument createObservationProcess(ObservationProcess procedure,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        ObservationProcessDocument observationProcessDoc = ObservationProcessDocument.Factory.newInstance();
        ObservationProcessType observationProcess = observationProcessDoc.addNewObservationProcess();
        if (additionalValues.containsKey(HelperValues.GMLID)) {
            observationProcess.setId("process." + additionalValues.get(HelperValues.GMLID));
        } else {
            observationProcess.setId("process." + JavaHelper.generateID(procedure.toString()));
        }
        if (procedure.isSetIdentifier()) {
            CodeWithAuthority codeWithAuthority = procedure.getIdentifierCodeWithAuthority();
            Encoder<?, CodeWithAuthority> encoder =
                    CodingRepository.getInstance().getEncoder(
                            CodingHelper.getEncoderKey(GmlConstants.NS_GML_32,codeWithAuthority));
            if (encoder != null) {
                XmlObject xmlObject = (XmlObject) encoder.encode(codeWithAuthority);
                observationProcess.addNewIdentifier().set(xmlObject);
            } else {
                throw new NoApplicableCodeException()
                        .withMessage("Error while encoding geometry value, needed encoder is missing!");
            }
        }
        if (procedure.isSetName()) {
            for (final CodeType sosName : procedure.getName()) {
                observationProcess.addNewName().set(CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, sosName));
            }
        }
        addProcessType(observationProcess, procedure);
        addOriginatingProcess(observationProcess, procedure);
        addAggregatingDuration(observationProcess, procedure);
        addVerticalDatum(observationProcess, procedure);
        addComment(observationProcess, procedure);
        addProcessReference(observationProcess, procedure);
        addInput(observationProcess, procedure);
        addParameter(observationProcess, procedure);
        return observationProcessDoc;
    }

    /**
     * Adds processType value to WaterML 2.0 ObservationProcess XML object
     * 
     * @param observationProcess
     *            WaterML 2.0 ObservationProcess XML object
     * @param procedure
     *            SOS ObservationProcess
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addProcessType(ObservationProcessType observationProcess, ObservationProcess procedure)
            throws OwsExceptionReport {
        if (procedure.isSetProcessType() && procedure.getProcessType().isSetHref()) {
            XmlObject referenceType = encodeReferenceType(procedure.getProcessType());
            if (referenceType != null) {
                observationProcess.addNewProcessType().set(referenceType);
            }
        } else {
            throw new NoApplicableCodeException().withMessage("Missing processType definition");
        }
    }

    /**
     * Adds OriginatingProcess value to WaterML 2.0 ObservationProcess XML
     * object
     * 
     * @param observationProcess
     *            WaterML 2.0 ObservationProcess XML object
     * @param procedure
     *            SOS ObservationProcess
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addOriginatingProcess(ObservationProcessType observationProcess, ObservationProcess procedure)
            throws OwsExceptionReport {
        if (procedure.isSetOriginatingProcess()) {
            XmlObject referenceType = encodeReferenceType(procedure.getOriginatingProcess());
            if (referenceType != null) {
                observationProcess.addNewOriginatingProcess().set(referenceType);
            }
        }
    }

    /**
     * Adds AggregatingDuration value to WaterML 2.0 ObservationProcess XML
     * object
     * 
     * @param observationProcess
     *            WaterML 2.0 ObservationProcess XML object
     * @param procedure
     *            SOS ObservationProcess
     */
    private void addAggregatingDuration(ObservationProcessType observationProcess, ObservationProcess procedure) {
    }

    /**
     * Adds VerticalDatum value to WaterML 2.0 ObservationProcess XML object
     * 
     * @param observationProcess
     *            WaterML 2.0 ObservationProcess XML object
     * @param procedure
     *            SOS ObservationProcess
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addVerticalDatum(ObservationProcessType observationProcess, ObservationProcess procedure)
            throws OwsExceptionReport {
        if (procedure.isSetVerticalDatum()) {
            XmlObject referenceType = encodeReferenceType(procedure.getVerticalDatum());
            if (referenceType != null) {
                observationProcess.addNewVerticalDatum().set(referenceType);
            }
        }
    }

    /**
     * Adds Comment value to WaterML 2.0 ObservationProcess XML object
     * 
     * @param observationProcess
     *            WaterML 2.0 ObservationProcess XML object
     * @param procedure
     *            SOS ObservationProcess
     */
    private void addComment(ObservationProcessType observationProcess, ObservationProcess procedure) {
        if (procedure.isSetComments()) {
            for (String comment : procedure.getComments()) {
                if (comment != null && !comment.isEmpty()) {
                    observationProcess.addComment(comment);
                }
            }
        }
    }

    /**
     * Adds ProcessReference value to WaterML 2.0 ObservationProcess XML object
     * 
     * @param observationProcess
     *            WaterML 2.0 ObservationProcess XML object
     * @param procedure
     *            SOS ObservationProcess
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addProcessReference(ObservationProcessType observationProcess, ObservationProcess procedure)
            throws OwsExceptionReport {
        if (procedure.isSetProcessReference()) {
            XmlObject referenceType = encodeReferenceType(procedure.getProcessReference());
            if (referenceType != null) {
                observationProcess.addNewProcessReference().set(referenceType);
            }
        }
    }

    /**
     * Adds Input value to WaterML 2.0 ObservationProcess XML object
     * 
     * @param observationProcess
     *            WaterML 2.0 ObservationProcess XML object
     * @param procedure
     *            SOS ObservationProcess
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addInput(ObservationProcessType observationProcess, ObservationProcess procedure)
            throws OwsExceptionReport {
        if (procedure.isSetInputs()) {
            for (org.n52.sos.ogc.gml.ReferenceType sosReferenceType : procedure.getInputs()) {
                XmlObject referenceType = encodeReferenceType(sosReferenceType);
                if (referenceType != null) {
                    observationProcess.addNewInput().set(referenceType);
                }
            }
        }
    }

    /**
     * Adds Parameter value to WaterML 2.0 ObservationProcess XML object
     * 
     * @param observationProcess
     *            WaterML 2.0 ObservationProcess XML object
     * @param procedure
     *            SOS ObservationProcess
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addParameter(ObservationProcessType observationProcess, ObservationProcess procedure)
            throws OwsExceptionReport {
        if (procedure.isSetParameters()) {
            List<NamedValue<?>> parameters = procedure.getParameters();
            for (NamedValue<?> sosNamedValue : parameters) {
                XmlObject namedValue = createNamedValue(sosNamedValue);
                if (namedValue != null) {
                    observationProcess.addNewParameter().addNewNamedValue().set(namedValue);
                }
            }
        }
    }

    /**
     * Creates a XML ReferenceType object from SOS ReferenceType object
     * 
     * @param sosReferenceType
     *            SOS ReferenceType object
     * @return XML ReferenceType object
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private XmlObject encodeReferenceType(ReferenceType sosReferenceType) throws OwsExceptionReport {
        Encoder<XmlObject, ReferenceType> encoder =
                CodingRepository.getInstance().getEncoder(
                        CodingHelper.getEncoderKey(GmlConstants.NS_GML_32, sosReferenceType));
        if (encoder != null) {
            return encoder.encode(sosReferenceType);
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Error while encoding referenceType, needed encoder is missing!");
        }

    }

    /**
     * Adds parameter values to WaterML 2.0 XML MonitoringPoint object from
     * SosSamplingFeature
     * 
     * @param monitoringPoint
     *            WaterML 2.0 XML MonitoringPoint object
     * @param sampFeat
     *            SosSamplingFeature
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addParameter(MonitoringPointType monitoringPoint, SamplingFeature sampFeat) throws OwsExceptionReport {
        for (NamedValue<?> namedValue : sampFeat.getParameters()) {
            XmlObject encodeObjectToXml = createNamedValue(namedValue);
            if (encodeObjectToXml != null) {
                monitoringPoint.addNewParameter().addNewNamedValue().set(encodeObjectToXml);
            }
        }
    }

    /**
     * Parses the ITime object to a time representation as String
     * 
     * @param time
     *            SOS ITime object
     * @return Time as String
     * @throws DateTimeFormatException
     *             If a formatting error occurs
     */
    protected String getTimeString(Time time) throws DateTimeFormatException {
        DateTime dateTime = getTime(time);
        return DateTimeHelper.formatDateTime2String(dateTime, time.getTimeFormat());
    }

    /**
     * Get the time representation from ITime object
     * 
     * @param time
     *            ITime object
     * @return Time as DateTime
     */
    private DateTime getTime(Time time) {
        if (time instanceof TimeInstant) {
            return ((TimeInstant) time).getValue();
        } else if (time instanceof TimePeriod) {
            TimePeriod timePeriod = (TimePeriod) time;
            if (timePeriod.getEnd() != null) {
                return timePeriod.getEnd();
            } else {
                return timePeriod.getStart();
            }
        }
        return new DateTime().minusYears(1000);
    }
}
