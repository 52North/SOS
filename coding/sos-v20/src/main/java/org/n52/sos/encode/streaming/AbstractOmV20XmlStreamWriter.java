/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.AbstractOmEncoderv20;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.GmlHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.collect.Maps;

public abstract class AbstractOmV20XmlStreamWriter extends XmlStreamWriter<OmObservation> {

    private OmObservation observation;

    public AbstractOmV20XmlStreamWriter() {
    }

    public AbstractOmV20XmlStreamWriter(OmObservation observation) {
        setOmObservation(observation);
    }

    private void setOmObservation(OmObservation observation) {
        this.observation = observation;
    }

    private OmObservation getOmObservation() {
        return observation;
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

    protected void writeOmObservationDoc(EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        start(OmConstants.QN_OM_20_OBSERVATION);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        namespace(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM_2);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        String observationID = addGmlId(observation);
        writeNewLine();
        if (observation.isSetIdentifier()) {
            writeIdentifier(observation.getIdentifier());
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
            writeParameter();
            writeNewLine();
        }
        writeObservableProperty();
        writeNewLine();
        writeFeatureOfIntererst(encodingValues);
        writeNewLine();
        writeResult();
        writeNewLine();
        indent--;
        end(OmConstants.QN_OM_20_OBSERVATION);
        indent++;
    }

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

    protected void writeDescription(String description) throws XMLStreamException {
        start(GmlConstants.QN_DESCRIPTION_32);
        chars(description);
        endInline(GmlConstants.QN_DESCRIPTION_32);
    }

    protected void writeObservationType(String observationType) throws XMLStreamException {
        start(OmConstants.QN_OM_20_OBSERVATION_TYPE);
        chars(observationType);
        endInline(OmConstants.QN_OM_20_OBSERVATION_TYPE);
    }

    protected void writePhenomenonTime(Time time) throws OwsExceptionReport, XMLStreamException {
        start(OmConstants.QN_OM_20_PHENOMENON_TIME);
        writeNewLine();
        writePhenomenonTimeContent(time);
        writeNewLine();
        indent--;
        end(OmConstants.QN_OM_20_PHENOMENON_TIME);
        indent++;
    }

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

    @SuppressWarnings("unchecked")
    protected void writeProcedure(EncodingValues encodingValues) throws XMLStreamException,
            UnsupportedEncoderInputException, OwsExceptionReport {
        if (encodingValues.isSetEncoder() && encodingValues.getEncoder() instanceof ObservationEncoder) {
            XmlObject xmlObject =
                    ((ObservationEncoder<XmlObject, Object>) encodingValues.getEncoder()).encode(observation
                            .getObservationConstellation().getProcedure(), null);
            writeXmlObject(xmlObject, OmConstants.QN_OM_20_PROCEDURE);
        } else {
            empty(OmConstants.QN_OM_20_PROCEDURE);
            addXlinkHrefAttr(observation.getObservationConstellation().getProcedure().getIdentifier());
        }
    }

    protected void writeParameter() {
        for (NamedValue<?> namedValue : observation.getParameter()) {
            // TODO
        }
    }

    protected void writeObservableProperty() throws XMLStreamException {
        empty(OmConstants.QN_OM_20_OBSERVED_PROPERTY);
        addXlinkHrefAttr(observation.getObservationConstellation().getObservableProperty().getIdentifier());
    }

    @SuppressWarnings("unchecked")
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
            addXlinkHrefAttr(observation.getObservationConstellation().getFeatureOfInterest().getIdentifier()
                    .getValue());
        }
    }

    protected void writeResult() throws XMLStreamException {
        empty(OmConstants.QN_OM_20_RESULT);
        // TODO
    }

    private String addGmlId(OmObservation observation) throws XMLStreamException {
        String observationID = JavaHelper.generateID(Double.toString(System.currentTimeMillis() * Math.random()));
        if (observation.isSetObservationID()) {
            observationID = observation.getObservationID();
        } else {
            observation.setObservationID(observationID);
        }
        attr(GmlConstants.AN_ID, "o_" + observationID);
        return observationID;
    }

    private void writePhenomenonTimeContent(Time time) throws OwsExceptionReport, XMLStreamException {
        XmlObject xmlObject =
                CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, time, getDocumentAdditionalHelperValues());
        writeXmlObject(xmlObject, GmlHelper.getGml321QnameForITime(time));
    }

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

    protected Map<HelperValues, String> getDocumentAdditionalHelperValues() {
        Map<HelperValues, String> additionalValues = Maps.newHashMap();
        additionalValues.put(HelperValues.DOCUMENT, null);
        return additionalValues;
    }

    protected void addXlinkHrefAttr(String value) throws XMLStreamException {
        attr(W3CConstants.QN_XLINK_HREF, value);
    }

    protected void addXlinkTitleAttr(String value) throws XMLStreamException {
        attr(W3CConstants.QN_XLINK_TITLE, value);
    }

}
