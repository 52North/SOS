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
package org.n52.sos.encode.streaming;

import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import net.opengis.om.x20.OMObservationType;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.convert.Converter;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.convert.ConverterRepository;
import org.n52.sos.encode.AbstractOmEncoderv20;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractObservationValue;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.GmlHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * Abstract implementation of {@link XmlStreamWriter} for writing
 * {@link OmObservation}s to stream
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class AbstractOmV20XmlStreamWriter extends XmlStreamWriter<OmObservation> {

    private OmObservation observation;

    /**
     * constructor
     */
    public AbstractOmV20XmlStreamWriter() {
    }

    /**
     * constructor
     * 
     * @param observation
     *            {@link OmObservation} to write to stream
     */
    public AbstractOmV20XmlStreamWriter(OmObservation observation) {
        setOmObservation(observation);
    }

    @Override
    public void write(OutputStream out) throws XMLStreamException, OwsExceptionReport {
        write(getOmObservation(), out);
    }

    @Override
    public void write(OutputStream out, EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        write(getOmObservation(), out, encodingValues);
    }

    @Override
    public void write(OmObservation response, OutputStream out) throws XMLStreamException, OwsExceptionReport {
        write(response, out, new EncodingValues());
    }

    @Override
    public void write(OmObservation observation, OutputStream out, EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        try {
            setOmObservation(observation);
            init(out, encodingValues);
            start(encodingValues.isEmbedded());
            writeOmObservationDoc(encodingValues);
            end();
            finish();
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse);
        }
    }

    /**
     * Write {@link OmObservation} XML encoded to stream
     * 
     * @param encodingValues
     *            {@link EncodingValues} contains additional information for the
     *            encoding
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     * @throws OwsExceptionReport
     *             If an error occurs when creating elements to be written If an
     *             error occurs when creating elements to be written
     */
    protected void writeOmObservationDoc(EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        start(OmConstants.QN_OM_20_OBSERVATION);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        namespace(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM_2);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        String observationID = addGmlId(observation);
        writeNewLine();
        if (observation.isSetIdentifier()) {
            writeIdentifier(observation.getIdentifierCodeWithAuthority());
            writeNewLine();
        }
        if (observation.isSetDescription()) {
            writeDescription(observation.getDescription());
            writeNewLine();
        }
        if (observation.getObservationConstellation().isSetObservationType()) {
            writeObservationType(observation.getObservationConstellation().getObservationType());
            writeNewLine();
        }
        Time phenomenonTime = observation.getPhenomenonTime();
        if (phenomenonTime.getGmlId() == null) {
            phenomenonTime.setGmlId(OmConstants.PHENOMENON_TIME_NAME + "_" + observationID);
        }
        writePhenomenonTime(phenomenonTime);
        writeNewLine();
        writeResultTime();
        writeNewLine();
        writeProcedure(encodingValues);
        writeNewLine();
        if (observation.isSetParameter()) {
            writeParameter(encodingValues);
        }
        writeObservableProperty();
        writeNewLine();
        writeFeatureOfIntererst(encodingValues);
        writeNewLine();
        writeResult(observation, encodingValues);
        writeNewLine();
        indent--;
        end(OmConstants.QN_OM_20_OBSERVATION);
        indent++;
    }

    /**
     * Write {@link CodeWithAuthority} as gml:identifier to stream
     * 
     * @param identifier
     *            {@link CodeWithAuthority} to write
     * @throws OwsExceptionReport
     *             If an error occurs when creating elements to be written
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    protected void writeIdentifier(CodeWithAuthority identifier) throws OwsExceptionReport, XMLStreamException {
        Encoder<?, CodeWithAuthority> encoder =
                CodingRepository.getInstance().getEncoder(
                        CodingHelper.getEncoderKey(GmlConstants.NS_GML_32, identifier));
        if (encoder != null) {
            writeXmlObject((XmlObject) encoder.encode(identifier), GmlConstants.QN_IDENTIFIER_32);
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Error while encoding geometry value, needed encoder is missing!");
        }
    }

    /**
     * Write description as gml:descritpion to stream
     * 
     * @param description
     *            Description to write
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    protected void writeDescription(String description) throws XMLStreamException {
        start(GmlConstants.QN_DESCRIPTION_32);
        chars(description);
        endInline(GmlConstants.QN_DESCRIPTION_32);
    }

    /**
     * Write observation typ as om:type to stream
     * 
     * @param observationType
     *            Observation type to write
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    protected void writeObservationType(String observationType) throws XMLStreamException {
        empty(OmConstants.QN_OM_20_OBSERVATION_TYPE);
        addXlinkHrefAttr(observationType);
    }

    /**
     * Write {@link Time} as om:phenomenonTime to stream
     * 
     * @param time
     *            {@link Time} to write as om:phenomenonTime to stream
     * @throws OwsExceptionReport
     *             If an error occurs when creating elements to be written
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    protected void writePhenomenonTime(Time time) throws OwsExceptionReport, XMLStreamException {
        start(OmConstants.QN_OM_20_PHENOMENON_TIME);
        writeNewLine();
        writePhenomenonTimeContent(time);
        writeNewLine();
        indent--;
        end(OmConstants.QN_OM_20_PHENOMENON_TIME);
        indent++;
    }

    /**
     * Write om:resultTime to stream
     * 
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     * @throws OwsExceptionReport
     *             If an error occurs when creating elements to be written
     */
    protected void writeResultTime() throws XMLStreamException, OwsExceptionReport {
        TimeInstant resultTime = observation.getResultTime();
        Time phenomenonTime = observation.getPhenomenonTime();
        // get result time from SOS result time representation
        if (observation.getResultTime() != null) {
            if (resultTime.equals(phenomenonTime)) {
                empty(OmConstants.QN_OM_20_RESULT_TIME);
                addXlinkHrefAttr(Constants.NUMBER_SIGN_STRING + phenomenonTime.getGmlId());
            } else {
                addResultTime(resultTime);
            }
        }
        // if result time is not set, get result time from phenomenon time
        // representation
        else {
            if (phenomenonTime instanceof TimeInstant) {
                empty(OmConstants.QN_OM_20_RESULT_TIME);
                addXlinkHrefAttr(Constants.NUMBER_SIGN_STRING + phenomenonTime.getGmlId());
            } else if (phenomenonTime instanceof TimePeriod) {
                TimeInstant rsTime = new TimeInstant(((TimePeriod) observation.getPhenomenonTime()).getEnd());
                addResultTime(rsTime);
            }
        }
    }

    /**
     * Write om:procedure encoded or as xlink:href to stream
     * 
     * @param encodingValues
     *            {@link EncodingValues} contains the required encoder
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     * @throws UnsupportedEncoderInputException
     *             If the procedure could not be encoded
     * @throws OwsExceptionReport
     *             If an error occurs when creating elements to be written
     */
    @SuppressWarnings("unchecked")
    protected void writeProcedure(EncodingValues encodingValues) throws XMLStreamException,
            UnsupportedEncoderInputException, OwsExceptionReport {
//        if (encodingValues.isSetEncoder() && checkEncodProcedureForEncoderKeys(encodingValues.getEncoder())) {
//            SosProcedureDescription procedureToEncode = observation
//                    .getObservationConstellation().getProcedure();
//            // should the procedure be converted
//            if (procedureToEncode.getDescriptionFormat().equals(anObject)) {
//                Converter<SosProcedureDescription, SosProcedureDescription> converter =
//                        ConverterRepository.getInstance().getConverter(procedureDescription.getDescriptionFormat(),
//                                getDefaultProcedureEncodingNamspace());
//                if (converter != null) {
//                    try {
//                        procedureToEncode = converter.convert(procedureDescription);
//                    } catch (ConverterException e) {
//                        throw new NoApplicableCodeException().causedBy(e).withMessage(
//                                "Error while converting procedureDescription!");
//                    }
//                } else {
//                    throw new NoApplicableCodeException().withMessage("No converter (%s -> %s) found!",
//                            procedureDescription.getDescriptionFormat(), getDefaultProcedureEncodingNamspace());
//                }
//            } else {
//                procedureToEncode = procedureDescription;
//            }
//            // encode procedure or add reference
//            XmlObject encodedProcedure =
//                    CodingHelper.encodeObjectToXml(procedureToEncode.getDescriptionFormat(), procedureToEncode);
//            if (encodedProcedure != null) {
//                writeXmlObject(encodedProcedure, OmConstants.QN_OM_20_PROCEDURE);
//            } else {
//                empty(OmConstants.QN_OM_20_PROCEDURE);
//                addXlinkHrefAttr(observation.getObservationConstellation().getProcedure().getIdentifier());
//            }
//        } else {
        empty(OmConstants.QN_OM_20_PROCEDURE);
        addXlinkHrefAttr(observation.getObservationConstellation().getProcedure().getIdentifier());
        if (observation.getObservationConstellation().getProcedure().isSetName()
                && observation.getObservationConstellation().getProcedure().getFirstName().isSetValue()) {
            addXlinkTitleAttr(observation.getObservationConstellation().getProcedure().getFirstName().getValue());
        }
//        }
        
        
        
//        if (encodingValues.isSetEncoder() && encodingValues.getEncoder() instanceof ObservationEncoder) {
//            XmlObject xmlObject =
//                    ((ObservationEncoder<XmlObject, Object>) encodingValues.getEncoder()).encode(observation
//                            .getObservationConstellation().getProcedure(), null);
//            writeXmlObject(xmlObject, OmConstants.QN_OM_20_PROCEDURE);
//        } else {
//            empty(OmConstants.QN_OM_20_PROCEDURE);
//            addXlinkHrefAttr(observation.getObservationConstellation().getProcedure().getIdentifier());
//        }
    }

    /**
     * Write om:parameter to stream
     * 
     * @param encodingValues
     *            {@link EncodingValues} contains the required encoder
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     * @throws OwsExceptionReport
     *             If an error occurs when creating elements to be written
     */
    @SuppressWarnings("unchecked")
    protected void writeParameter(EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        if (encodingValues.isSetEncoder() && encodingValues.getEncoder() instanceof ObservationEncoder) {
            for (NamedValue<?> namedValue : observation.getParameter()) {
                start(OmConstants.QN_OM_20_PARAMETER);
                writeNewLine();
                XmlObject xmlObject =
                        ((ObservationEncoder<XmlObject, Object>) encodingValues.getEncoder()).encode(namedValue);
                writeXmlObject(xmlObject, OmConstants.QN_OM_20_NAMED_VALUE);
                writeNewLine();
                indent--;
                end(OmConstants.QN_OM_20_PARAMETER);
                writeNewLine();
                indent++;
            }
        }
    }

    /**
     * Write om:observedProperty to stream
     * 
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    protected void writeObservableProperty() throws XMLStreamException {
        empty(OmConstants.QN_OM_20_OBSERVED_PROPERTY);
        addXlinkHrefAttr(observation.getObservationConstellation().getObservableProperty().getIdentifier());
        if (observation.getObservationConstellation().getObservableProperty().isSetName()
                && observation.getObservationConstellation().getObservableProperty().getFirstName().isSetValue()) {
            addXlinkTitleAttr(observation.getObservationConstellation().getObservableProperty().getFirstName()
                    .getValue());
        }
    }

    /**
     * Write om:featureOfInterest encoded or as xlink:href to stream
     * 
     * @param encodingValues
     *            {@link EncodingValues} contains the required encoder
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     * @throws OwsExceptionReport
     *             If an error occurs when creating elements to be written
     */
    protected void writeFeatureOfIntererst(EncodingValues encodingValues) throws XMLStreamException,
            OwsExceptionReport {
        if (encodingValues.isSetEncoder() && encodingValues.getEncoder() instanceof AbstractOmEncoderv20) {
            AbstractOmEncoderv20 encoder = (AbstractOmEncoderv20) encodingValues.getEncoder();
            Map<HelperValues, String> additionalValues =
                    new EnumMap<SosConstants.HelperValues, String>(HelperValues.class);
            Profile activeProfile = Configurator.getInstance().getProfileHandler().getActiveProfile();
            additionalValues.put(HelperValues.ENCODE,
                    Boolean.toString(activeProfile.isEncodeFeatureOfInterestInObservations()));
            if (StringHelper.isNotEmpty(activeProfile.getEncodingNamespaceForFeatureOfInterest())) {
                additionalValues.put(HelperValues.ENCODE_NAMESPACE,
                        activeProfile.getEncodingNamespaceForFeatureOfInterest());
            } else {
                additionalValues.put(HelperValues.ENCODE_NAMESPACE, encoder.getDefaultFeatureEncodingNamespace());
            }
            XmlObject xmlObject =
                    CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, observation.getObservationConstellation()
                            .getFeatureOfInterest(), additionalValues);
            writeXmlObject(xmlObject, OmConstants.QN_OM_20_FEATURE_OF_INTEREST);
        } else {
            empty(OmConstants.QN_OM_20_FEATURE_OF_INTEREST);
            addXlinkHrefAttr(observation.getObservationConstellation().getFeatureOfInterest().getIdentifier());
            if (observation.getObservationConstellation().getFeatureOfInterest().isSetName()
                    && observation.getObservationConstellation().getFeatureOfInterest().getFirstName().isSetValue()) {
                addXlinkTitleAttr(observation.getObservationConstellation().getFeatureOfInterest().getFirstName()
                        .getValue());
            }
        }
    }

    /**
     * write om:result to stream
     * 
     * @param observation
     *            {@link OmObservation} with the result to write
     * @param encodingValues
     *            {@link EncodingValues} contains the result element namespace
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     * @throws OwsExceptionReport
     *             If an error occurs when creating elements to be written
     */
    protected void writeResult(OmObservation observation, EncodingValues encodingValues) throws XMLStreamException,
            OwsExceptionReport {
        if (observation.getValue() instanceof AbstractObservationValue<?>) {
            ((AbstractObservationValue<?>) observation.getValue()).setValuesForResultEncoding(observation);
        }
        XmlObject createResult =
                CodingHelper.encodeObjectToXml(encodingValues.getEncodingNamespace(), observation.getValue());
        if (createResult != null) {
            if (createResult.xmlText().contains(XML_FRAGMENT)) {
                XmlObject set =
                        OMObservationType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions())
                                .addNewResult().set(createResult);
                writeXmlObject(set, OmConstants.QN_OM_20_RESULT);
            } else {
                if (checkResult(createResult)) {
                    QName name = createResult.schemaType().getName();
                    String prefix = name.getPrefix();
                    if (Strings.isNullOrEmpty(prefix)) {
                        XmlCursor newCursor = createResult.newCursor();
                        prefix = newCursor.prefixForNamespace(name.getNamespaceURI());
                        newCursor.setAttributeText(W3CConstants.QN_XSI_TYPE,
                                prefix + Constants.COLON_STRING + name.getLocalPart());
                        newCursor.dispose();
                    }
                    writeXmlObject(createResult, OmConstants.QN_OM_20_RESULT);
                } else {
                    start(OmConstants.QN_OM_20_RESULT);
                    writeNewLine();
                    writeXmlObject(createResult, OmConstants.QN_OM_20_RESULT);
                    writeNewLine();
                    indent--;
                    end(OmConstants.QN_OM_20_RESULT);
                    indent++;
                }
            }
        } else {
            empty(OmConstants.QN_OM_20_RESULT);
        }
    }

    /**
     * Get additional values map with document helper value
     * 
     * @return
     */
    protected Map<HelperValues, String> getDocumentAdditionalHelperValues() {
        Map<HelperValues, String> additionalValues = Maps.newHashMap();
        additionalValues.put(HelperValues.DOCUMENT, null);
        return additionalValues;
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
     * Check the encoded om:result content for ...PropertyType
     * 
     * @param result
     *            Encoded om:result content to check
     * @return <code>true</code>, if content contains ...PropertyType
     */
    private boolean checkResult(XmlObject result) {
        if (result.schemaType() != null) {
            SchemaType schemaType = result.schemaType();
            if (schemaType.getName() != null) {
                QName name = schemaType.getName();
                if (name.getLocalPart() != null && name.getLocalPart().toLowerCase().contains("propertytype")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add gml:id to om:OM_Observation element
     * 
     * @param observation
     *            {@link OmObservation} with the GML id
     * @return observation id
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private String addGmlId(OmObservation observation) throws XMLStreamException {
        String observationID = JavaHelper.generateID(Double.toString(System.currentTimeMillis() * Math.random()));
        if (observation.isSetObservationID()) {
            observationID = observation.getObservationID();
        } else {
            observation.setObservationID(observationID);
        }
        attr(GmlConstants.QN_ID_32, "o_" + observationID);
        return observationID;
    }

    /**
     * Write encoded om:phenomenonTime to stream
     * 
     * @param time
     *            {@link Time} to encode and write
     * @throws OwsExceptionReport
     *             If an error occurs when creating elements to be written
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void writePhenomenonTimeContent(Time time) throws OwsExceptionReport, XMLStreamException {
        XmlObject xmlObject =
                CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, time, getDocumentAdditionalHelperValues());
        writeXmlObject(xmlObject, GmlHelper.getGml321QnameForITime(time));
    }

    /**
     * Write encoded om:resultTime to stream
     * 
     * @param time
     *            {@link Time} to encode and write
     * @throws OwsExceptionReport
     *             If an error occurs when creating elements to be written
     * @throws XMLStreamException
     *             If an error occurs when writing to stream
     */
    private void addResultTime(TimeInstant time) throws OwsExceptionReport, XMLStreamException {
        start(OmConstants.QN_OM_20_RESULT_TIME);
        writeNewLine();
        XmlObject xmlObject =
                CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, time, getDocumentAdditionalHelperValues());
        writeXmlObject(xmlObject, GmlConstants.QN_TIME_INSTANT_32);
        writeNewLine();
        indent--;
        end(OmConstants.QN_OM_20_RESULT_TIME);
        indent++;
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

    /**
     * Set {@link OmObservation} which should be written
     * 
     * @param observation
     *            the {@link OmObservation}
     */
    private void setOmObservation(OmObservation observation) {
        this.observation = observation;
    }

    /**
     * Get the {@link OmObservation} which should be written
     * 
     * @return the {@link OmObservation}
     */
    private OmObservation getOmObservation() {
        return observation;
    }

    /**
     * Method to check whether the procedure should be encoded
     * 
     * @return True or false
     */
    private boolean checkEncodProcedureForEncoderKeys(Encoder<?, ?> encoder) {
        Set<EncoderKey> encoderKeyType = encoder.getEncoderKeyType();
        for (EncoderKey encoderKey : encoderKeyType) {
            if (encoderKey instanceof XmlEncoderKey) {
                if (Configurator.getInstance().getProfileHandler().getActiveProfile()
                        .isEncodeProcedureInObservation(((XmlEncoderKey) encoderKey).getNamespace())) {
                    return true;
                }
            }
        }
        return false;
    }
}
