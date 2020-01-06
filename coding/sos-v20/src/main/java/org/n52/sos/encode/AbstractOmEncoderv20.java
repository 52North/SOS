/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlString;
import org.isotc211.x2005.gmd.AbstractDQElementDocument;
import org.isotc211.x2005.gmd.DQElementPropertyType;
import org.n52.sos.convert.Converter;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.convert.ConverterRepository;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.AbstractGML;
import org.n52.sos.ogc.gml.AbstractMetaData;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmCompositePhenomenon;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationContext;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.quality.OmResultQuality;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.CvDiscretePointCoverage;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.MultiPointCoverage;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.QuantityRangeValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.RectifiedGridCoverage;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.om.values.XmlValue;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.Constants;
import org.n52.sos.util.GmlHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.W3CConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.opengis.om.x20.NamedValueDocument;
import net.opengis.om.x20.NamedValuePropertyType;
import net.opengis.om.x20.NamedValueType;
import net.opengis.om.x20.OMObservationDocument;
import net.opengis.om.x20.OMObservationPropertyType;
import net.opengis.om.x20.OMObservationType;
import net.opengis.om.x20.OMProcessPropertyType;
import net.opengis.om.x20.ObservationContextPropertyType;
import net.opengis.om.x20.ObservationContextType;
import net.opengis.om.x20.TimeObjectPropertyType;


public abstract class AbstractOmEncoderv20
        extends AbstractXmlEncoder<Object>
        implements ObservationEncoder<XmlObject, Object>,
                   StreamingEncoder<XmlObject, Object> {

    /**
     * Method to create the om:result element content
     *
     * @param sosObservation
     *            SosObservation to be encoded
     * @return XML encoded result object, e.g a gml:MeasureType
     * @throws OwsExceptionReport
     *             if an error occurs
     */
    protected abstract XmlObject createResult(OmObservation sosObservation) throws OwsExceptionReport;

    protected abstract XmlObject encodeResult(ObservationValue<?> observationValue) throws OwsExceptionReport;

    /**
     * Method to add the observation type to the om:Observation. Subclasses
     * should have mappings to set the correct type, e.g. O&M .../Measurement ==
     * .../MeasurementTimeseriesTVPObservation in WaterML 2.0
     *
     * @param xbObservation
     *            XmlBeans object of observation
     * @param observationType
     *            Observation type
     */
    protected abstract void addObservationType(OMObservationType xbObservation, String observationType);

    /**
     * Get the default encoding Namespace for FeatureOfInterest
     *
     * @return Encoding namespace
     */
    public abstract String getDefaultFeatureEncodingNamespace();

    /**
     * Get the default encoding Namespace for Procedures
     *
     * @return Encoding namespace
     */
    protected abstract String getDefaultProcedureEncodingNamspace();

    /**
     * Indicator whether the procedure is to be encoded
     *
     * @return Indicator
     */
    protected abstract boolean convertEncodedProcedure();
    
    protected abstract OMObservationType createOmObservationType(); 
    
    protected abstract void addAddtitionalInformation(OMObservationType omot, OmObservation observation) throws OwsExceptionReport;

    @Override
    public boolean forceStreaming() {
        return false;
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues) throws OwsExceptionReport,
            UnsupportedEncoderInputException {
        XmlObject encodedObject = null;
        if (element instanceof OmObservation) {
            encodedObject = encodeOmObservation((OmObservation) element, additionalValues);
        } else if (element instanceof NamedValue) {
            NamedValueType nvt = createNamedValue((NamedValue<?>) element);
            if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
                NamedValueDocument nvd = NamedValueDocument.Factory.newInstance();
                nvd.setNamedValue(nvt);
                encodedObject = nvd;
            } else if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
                NamedValuePropertyType nvpt = NamedValuePropertyType.Factory.newInstance();
                nvpt.setNamedValue(nvt);
                encodedObject = nvpt;
            } else {
                encodedObject = nvt;
            }
        } else if (element instanceof AbstractFeature) {
            encodedObject = encodeFeatureOfInterest((AbstractFeature) element);
        } else if (element instanceof SosProcedureDescription) {
            encodedObject = encodeProcedureDescription((SosProcedureDescription) element);
        } else {
            throw new UnsupportedEncoderInputException(this, element);
        }
        // LOGGER.debug("Encoded object {} is valid: {}",
        // encodedObject.schemaType().toString(),
        // XmlHelper.validateDocument(encodedObject));
        return encodedObject;
    }

    @Override
    public void encode(Object objectToEncode, OutputStream outputStream) throws OwsExceptionReport {
        encode(objectToEncode, outputStream, new EncodingValues());
    }

    @Override
    public void encode(Object objectToEncode, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        try {
            XmlOptions xmlOptions = getXmlOptions();
            if (encodingValues.isEmbedded()) {
                xmlOptions.setSaveNoXmlDecl();
            }
            // writeIndent(encodingValues.getIndent(), outputStream);
            encode(objectToEncode, encodingValues.getAdditionalValues()).save(outputStream, xmlOptions);
        } catch (IOException ioe) {
            throw new NoApplicableCodeException().causedBy(ioe).withMessage("Error while writing element to stream!");
        } finally {
            if (encodingValues.isEmbedded()) {
                getXmlOptions().remove(XmlOptions.SAVE_NO_XML_DECL);
            }
        }
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(OmConstants.NS_OM_2, OmConstants.NS_OM_PREFIX);
    }

    /**
     * Method to create an O&M 2.0 observation XmlBeans object
     *
     * @param sosObservation
     *            SosObservation to be encoded
     * @param additionalValues
     *            Additional values which are used during the encoding
     * @return XmlBeans representation of O&M 2.0 observation
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    protected XmlObject encodeOmObservation(OmObservation sosObservation, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport {
        OMObservationType xbObservation = createOmObservationType();

        if (!sosObservation.isSetObservationID()) {
            sosObservation.setObservationID(JavaHelper.generateID(Double.toString(System.currentTimeMillis()
                    * Math.random())));
        }
        String observationID = sosObservation.getObservationID();
        if (!sosObservation.isSetGmlID()) {
            sosObservation.setGmlId("o_" + observationID);
        }
        xbObservation.setId(sosObservation.getGmlId());
        setObservationIdentifier(sosObservation, xbObservation);
        setObservationName(sosObservation, xbObservation);
        setDescription(sosObservation, xbObservation);
        setMetaDataProperty(sosObservation, xbObservation);
        setObservationType(sosObservation, xbObservation);
        setRelatedObservations(sosObservation, xbObservation);
        setPhenomenonTime(sosObservation, xbObservation);
        setResultTime(sosObservation, xbObservation);
        setValidTime(sosObservation, xbObservation);
        setProcedure(sosObservation, xbObservation);
        setParameter(sosObservation, xbObservation);
        setObservableProperty(sosObservation, xbObservation);
        setFeatureOfInterest(sosObservation, xbObservation);
        setResultQualities(xbObservation, sosObservation);
        setResult(sosObservation, xbObservation);
        addAddtitionalInformation(xbObservation, sosObservation);
        if (additionalValues.containsKey(HelperValues.PROPERTY_TYPE)) {
            return createObservationPropertyType(xbObservation);
        } else if (additionalValues.containsKey(HelperValues.DOCUMENT)) {
            return createObservationDocument(xbObservation);
        } else {
            return xbObservation;
        }
    }
    
    private XmlObject createObservationDocument(OMObservationType xbObservation) {
        OMObservationDocument doc = createObservationDocument();
        doc.setOMObservation(xbObservation);
        return doc;
    }

    private XmlObject createObservationPropertyType(OMObservationType obs) {
        OMObservationPropertyType opt = createObservationPropertyType();
        opt.setOMObservation(obs);
        return opt;
    }

    private void setDescription(OmObservation observation, OMObservationType xb) {
        // set observation description
        if (observation.isSetDescription()) {
            xb.addNewDescription().setStringValue(observation.getDescription());
        }
    }

    private void setResult(OmObservation observation, OMObservationType xb) throws OwsExceptionReport {
        XmlObject result = createResult(observation);
        if (result != null) {
             xb.addNewResult().set(result);
        } else {
            xb.addNewResult();
        }
    }

    private void setFeatureOfInterest(OmObservation observation, OMObservationType xb) throws OwsExceptionReport {
        AbstractFeature foi = observation.getObservationConstellation().getFeatureOfInterest();
        XmlObject xbFoi = encodeFeatureOfInterest(foi);
        xb.addNewFeatureOfInterest().set(xbFoi);
    }

    private void setObservationIdentifier(OmObservation observation, OMObservationType xb) throws OwsExceptionReport {
        // set observation identifier if available
        if (observation.isSetIdentifier()) {
            XmlObject xbId = encodeGML(observation.getIdentifierCodeWithAuthority());
            xb.addNewIdentifier().set(xbId);
        }
    }
    
    private void setObservationName(OmObservation observation, OMObservationType xb) throws OwsExceptionReport {
        // set observation identifier if available
        if (observation.isSetIdentifier()) {
            for (CodeType name : observation.getName()) {
                XmlObject xbId = encodeGML(name);
                xb.addNewName().set(xbId);
            }
        }
    }

    private void setMetaDataProperty(OmObservation sosObservation, OMObservationType xbObservation) throws OwsExceptionReport {
        if (sosObservation.isSetMetaDataProperty()) {
            for (AbstractMetaData abstractMetaData : sosObservation.getMetaDataProperty()) {
                XmlObject encodeObject = encodeGML32(abstractMetaData);
                XmlObject substituteElement = XmlHelper.substituteElement(xbObservation.addNewMetaDataProperty().addNewAbstractMetaData(), encodeObject);
                substituteElement.set(encodeObject);
            }
        }
    }

    protected void setObservationType(OmObservation observation, OMObservationType xb) {
        // add observationType if set
        addObservationType(xb, observation.getObservationConstellation().getObservationType());
    }

    private void setRelatedObservations(OmObservation sosObservation, OMObservationType omot) throws OwsExceptionReport {
        if (sosObservation.isSetRelatedObservations()) {
            for (OmObservationContext observationContext : sosObservation.getRelatedObservations()) {
                addRelatedObservation(omot.addNewRelatedObservation(), observationContext);
            }
        }
    }

    private void addRelatedObservation(ObservationContextPropertyType ocpt, OmObservationContext observationContext) throws OwsExceptionReport {
        ObservationContextType oct = ocpt.addNewObservationContext();
        oct.addNewRole().set(encodeGML(observationContext.getRole()));
        oct.addNewRelatedObservation().set(encodeGML(observationContext.getRelatedObservation()));
    }

    private void setPhenomenonTime(OmObservation observation, OMObservationType xb) throws OwsExceptionReport {
        // set validTime
        Time phenomenonTime = observation.getPhenomenonTime();
        if (phenomenonTime.getGmlId() == null) {
            phenomenonTime.setGmlId(OmConstants.PHENOMENON_TIME_NAME + "_" + observation.getObservationID());
        }
        addPhenomenonTime(xb.addNewPhenomenonTime(), phenomenonTime);
    }

    private void setResultTime(OmObservation observation, OMObservationType xb)
            throws OwsExceptionReport {
        // set resultTime
        addResultTime(xb, observation);
    }

    private void setProcedure(OmObservation observation, OMObservationType xb) throws OwsExceptionReport {
        // set procedure
        addProcedure(xb.addNewProcedure(), observation
                .getObservationConstellation().getProcedure());
    }

    private void setParameter(OmObservation observation, OMObservationType xb) throws OwsExceptionReport {
        // set parameter
        if (observation.isSetParameter()) {
            addParameter(xb, observation.getParameter());
        }
    }

    private void setObservableProperty(OmObservation observation, OMObservationType xb) {
        // set observedProperty (phenomenon)
        AbstractPhenomenon observableProperty = observation
                .getObservationConstellation().getObservableProperty();
        xb.addNewObservedProperty().setHref(observableProperty.getIdentifier());
        if (observableProperty.isSetName()) {
            xb.getObservedProperty().setTitle(observableProperty.getFirstName().getValue());
        }

        if (observableProperty instanceof OmObservableProperty) {
        } else if (observableProperty instanceof OmCompositePhenomenon) {
        }
    }

    private XmlObject encodeProcedureDescription(SosProcedureDescription procedureDescription) throws OwsExceptionReport {
        OMProcessPropertyType procedure = OMProcessPropertyType.Factory.newInstance();
        addProcedure(procedure, procedureDescription);
        return procedure;
    }

    /**
     * Method that adds the procedure as reference or as encoded object to the
     * XML observation object
     *
     * @param procedure
     *            XML process type
     * @param procedureDescription
     *            SosProcedureDescription to be encoded
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addProcedure(OMProcessPropertyType procedure, SosProcedureDescription procedureDescription)
            throws OwsExceptionReport {
        if (checkEncodProcedureForEncoderKeys()) {
            SosProcedureDescription procedureToEncode = null;
            // should the procedure be converted
            if (convertEncodedProcedure()) {
                Converter<SosProcedureDescription, SosProcedureDescription> converter =
                        ConverterRepository.getInstance().getConverter(procedureDescription.getDescriptionFormat(),
                                                                       getDefaultProcedureEncodingNamspace());
                if (converter != null) {
                    try {
                        procedureToEncode = converter.convert(procedureDescription);
                    } catch (ConverterException e) {
                        throw new NoApplicableCodeException().causedBy(e).withMessage(
                                "Error while converting procedureDescription!");
                    }
                } else {
                    throw new NoApplicableCodeException().withMessage("No converter (%s -> %s) found!",
                                                                      procedureDescription.getDescriptionFormat(), getDefaultProcedureEncodingNamspace());
                }
            } else {
                procedureToEncode = procedureDescription;
            }
            // encode procedure or add reference
            XmlObject encodedProcedure =
                    CodingHelper.encodeObjectToXml(procedureToEncode.getDescriptionFormat(), procedureToEncode);
            if (encodedProcedure != null) {
                procedure.set(encodedProcedure);
            } else {
                procedure.setHref(procedureDescription.getIdentifier());
            }
        } else {
            procedure.setHref(procedureDescription.getIdentifier());
        }
     // set name as xlink:title
        if (procedure.isSetHref() && procedureDescription.isSetName() && procedureDescription.getFirstName().isSetValue()) {
            procedure.setTitle(procedureDescription.getFirstName().getValue());
        }
    }

    /**
     * Method to check whether the procedure should be encoded
     *
     * @return True or false
     */
    private boolean checkEncodProcedureForEncoderKeys() {
         Set<EncoderKey> encoderKeyType = getEncoderKeyType();
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

     /**
     * Method to add the phenomenon time to the XML observation object
     *
     * @param timeObjectPropertyType
     *            XML time object from XML observation object
     * @param time
     *            SOS phenomenon time representation
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void addPhenomenonTime(TimeObjectPropertyType timeObjectPropertyType, Time time) throws OwsExceptionReport {
        XmlObject xmlObject = encodeGML(time);
        XmlObject substitution =
                timeObjectPropertyType.addNewAbstractTimeObject().substitute(
                        GmlHelper.getGml321QnameForITime(time), xmlObject.schemaType());
        substitution.set(xmlObject);
     }

    /**
     * Method to add the result time to the XML observation object
     *
     * @param xbObs
     *            XML observation object
     * @param sosObservation
     *            SOS observation object
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    private void addResultTime(OMObservationType xbObs, OmObservation sosObservation) throws OwsExceptionReport {
         TimeInstant resultTime = sosObservation.getResultTime();
         Time phenomenonTime = sosObservation.getPhenomenonTime();
         // get result time from SOS result time representation
         if (sosObservation.getResultTime() != null) {
             if (resultTime.equals(phenomenonTime)) {
                 xbObs.addNewResultTime().setHref(Constants.NUMBER_SIGN_STRING + phenomenonTime.getGmlId());
             } else {
                 addResultTime(xbObs, resultTime);
             }
         }
         // if result time is not set, get result time from phenomenon time
         // representation
         else {
             if (phenomenonTime instanceof TimeInstant) {
                 xbObs.addNewResultTime().setHref(Constants.NUMBER_SIGN_STRING + phenomenonTime.getGmlId());
             } else if (phenomenonTime instanceof TimePeriod) {
                 TimeInstant rsTime = new TimeInstant(((TimePeriod) sosObservation.getPhenomenonTime()).getEnd());
                 addResultTime(xbObs, rsTime);
             }
         }
     }


    private void setValidTime(OmObservation observation, OMObservationType xb) throws OwsExceptionReport {
        Time validTime = observation.getValidTime();
        if (validTime == null) {
            return;
        }
        if (validTime.getGmlId() == null) {
            validTime.setGmlId(OmConstants.VALID_TIME_NAME + "_" + observation.getObservationID());
        }
        xb.addNewValidTime().addNewTimePeriod().set(encodeGML(validTime));
    }

    /**
     * Method to add the result time to the XML observation object
     *
     * @param xbObs
     *            XML observation object
     * @param time
     *            SOS result time representation
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    private void addResultTime(OMObservationType xbObs, TimeInstant time) throws OwsExceptionReport {
         XmlObject xmlObject = encodeGML(time);
         xbObs.addNewResultTime().addNewTimeInstant().set(xmlObject);
         XmlObject substitution =
                 xbObs.getResultTime().getTimeInstant()
                         .substitute(GmlHelper.getGml321QnameForITime(time), xmlObject.schemaType());
         substitution.set(xmlObject);
     }

    private void addParameter(OMObservationType xbObservation, Collection<NamedValue<?>> parameter)
            throws OwsExceptionReport {
        for (NamedValue<?> namedValue : parameter) {
            xbObservation.addNewParameter().setNamedValue(createNamedValue(namedValue));
        }
    }

    /**
     * Method to add the featureOfInterest to the XML observation object
     *
     * @param feature
     *            SOS feature representation
     * @return Encoded featureOfInterest
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    private XmlObject encodeFeatureOfInterest(AbstractFeature feature) throws OwsExceptionReport {
        Map<HelperValues, String> additionalValues =
                new EnumMap<SosConstants.HelperValues, String>(HelperValues.class);
        Profile activeProfile = Configurator.getInstance().getProfileHandler().getActiveProfile();
        additionalValues.put(HelperValues.ENCODE,
                Boolean.toString(activeProfile.isEncodeFeatureOfInterestInObservations()));
        if (StringHelper.isNotEmpty(activeProfile.getEncodingNamespaceForFeatureOfInterest())) {
            additionalValues.put(HelperValues.ENCODE_NAMESPACE,
                    activeProfile.getEncodingNamespaceForFeatureOfInterest());
        } else if (!Strings.isNullOrEmpty(getDefaultFeatureEncodingNamespace())) {
            additionalValues.put(HelperValues.ENCODE_NAMESPACE, getDefaultFeatureEncodingNamespace());
        } else {
            additionalValues.put(HelperValues.ENCODE_NAMESPACE, feature.getDefaultElementEncoding());
        }
        return encodeGML(feature, additionalValues);
    }

    /**
     * Method to encode a SOS NamedValue to an XmlBeans representation
     *
     * @param sosNamedValue
     *            SOS NamedValue
     * @return XmlBeans object
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
   protected NamedValueType createNamedValue(NamedValue<?> sosNamedValue) throws OwsExceptionReport {
        // encode value (any)
        XmlObject namedValuePropertyValue = getNamedValueValue(sosNamedValue.getValue());
        if (namedValuePropertyValue != null) {
            NamedValueType xbNamedValue =
                    NamedValueType.Factory.newInstance(XmlOptionsHelper.getInstance().getXmlOptions());
            // encode gml:ReferenceType
            XmlObject encodeObjectToXml = encodeGML(sosNamedValue.getName());
            xbNamedValue.addNewName().set(encodeObjectToXml);
            // set value (any)
            xbNamedValue.setValue(namedValuePropertyValue);
            return xbNamedValue;
        }
        return null;
    }

    /**
     * Get the XmlBeans object for SOS value
     *
     * @param value
     *            SOS value object
     * @return XmlBeans object
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    private XmlObject getNamedValueValue(Value<?> value) throws OwsExceptionReport {
        if (value.isSetValue()) {
            return value.accept(new NamedValueValueEncoder());
        }
        return null;
    }

    private void setResultQualities(OMObservationType xbObservation, OmObservation sosObservation)
            throws OwsExceptionReport {
        if (sosObservation.isSetResultQuality()) {
            encodeResultQualities(xbObservation, sosObservation.getResultQuality());
        } else if (sosObservation.getValue() instanceof SingleObservationValue) {
            encodeResultQualities(xbObservation,
                    ((SingleObservationValue<?>) sosObservation.getValue()).getQualityList());
        }
    }

    private void encodeResultQualities(OMObservationType xbObservation, Set<OmResultQuality> resultQuality)
            throws OwsExceptionReport {
        for (OmResultQuality quality : resultQuality) {
            AbstractDQElementDocument encodedQuality = (AbstractDQElementDocument) CodingHelper.encodeObjectToXml(null,
                    quality, ImmutableMap.of(HelperValues.DOCUMENT, "true"));
            DQElementPropertyType addNewResultQuality = xbObservation.addNewResultQuality();
            addNewResultQuality.setAbstractDQElement(encodedQuality.getAbstractDQElement());
            XmlHelper.substituteElement(addNewResultQuality.getAbstractDQElement(),
                    encodedQuality.getAbstractDQElement());
        }
    }

    protected static XmlOptions getXmlOptions() {
        return XmlOptionsHelper.getInstance().getXmlOptions();
    }

    private static OMObservationPropertyType createObservationPropertyType() {
        return OMObservationPropertyType.Factory.newInstance(getXmlOptions());
    }

    private static OMObservationDocument createObservationDocument() {
        return OMObservationDocument.Factory.newInstance(getXmlOptions());
    }

    protected static XmlObject encodeXLINK(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(W3CConstants.NS_XLINK, o);
    }

    protected static XmlObject encodeXLINK(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(W3CConstants.NS_XLINK, o, helperValues);
    }

    protected static XmlObject encodeGML(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o);
    }

    protected static XmlObject encodeGML(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(GmlConstants.NS_GML_32, o, helperValues);
    }

    protected static XmlObject encodeSweCommon(Object o) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, o);
    }

    protected static XmlObject encodeSweCommon(Object o, Map<HelperValues, String> helperValues) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(SweConstants.NS_SWE_20, o, helperValues);
    }

    private static class NamedValueValueEncoder implements ValueVisitor<XmlObject> {
        private static final Logger LOG =  LoggerFactory.getLogger(NamedValueValueEncoder.class);

        @Override
        public XmlObject visit(BooleanValue value) {
            XmlBoolean xbBoolean = XmlBoolean.Factory.newInstance();
            xbBoolean.setBooleanValue(value.getValue());
            return xbBoolean;
        }

        @Override
        public XmlObject visit(CategoryValue value) throws OwsExceptionReport {
            return encodeGML(value, createHelperValues(value));
        }

        @Override
        public XmlObject visit(ComplexValue value) {
            return defaultValue(value);
        }

        @Override
        public XmlObject visit(CountValue value) {
            XmlInteger xmlInteger = XmlInteger.Factory.newInstance();
            xmlInteger.setStringValue(value.getValue().toString());
            return xmlInteger;
        }

        @Override
        public XmlObject visit(GeometryValue value)
                throws OwsExceptionReport {
            return encodeGML(value, createHelperValues(value));
        }

        @Override
        public XmlObject visit(HrefAttributeValue value)
                throws OwsExceptionReport {
            return encodeXLINK(value.getValue(), createHelperValues(value));
        }

        @Override
        public XmlObject visit(NilTemplateValue value) {
            return defaultValue(value);
        }

        @Override
        public XmlObject visit(QuantityValue value) throws OwsExceptionReport {
            return encodeGML(value, createHelperValues(value));
        }

        @Override
        public XmlObject visit(QuantityRangeValue value) throws OwsExceptionReport {
            return defaultValue(value);
        }

        @Override
        public XmlObject visit(ReferenceValue value)
                throws OwsExceptionReport {
            return encodeGML(value.getValue(), createHelperValues(value));
        }

        @Override
        public XmlObject visit(SweDataArrayValue value) {
            return defaultValue(value);
        }

        @Override
        public XmlObject visit(TVPValue value) {
            return defaultValue(value);
        }
        
        @Override
        public XmlObject visit(TLVTValue value) {
            return defaultValue(value);
        }

        @Override
        public XmlObject visit(TextValue value) {
            XmlString xmlString = XmlString.Factory.newInstance();
            xmlString.setStringValue(value.getValue());
            return xmlString;
        }

        @Override
        public XmlObject visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
            return defaultValue(value);
        }

        @Override
        public XmlObject visit(MultiPointCoverage value) throws OwsExceptionReport {
            return defaultValue(value);
        }

        @Override
        public XmlObject visit(RectifiedGridCoverage value) throws OwsExceptionReport {
            return defaultValue(value);
        }

        @Override
        public XmlObject visit(ProfileValue value) throws OwsExceptionReport {
            return defaultValue(value);
        }

        @Override
        public XmlObject visit(UnknownValue value) {
            return defaultValue(value);
        }

        private Map<HelperValues, String> createHelperValues(Value<?> value) {
            Map<SosConstants.HelperValues, String> helperValues = Maps.newHashMap();
            helperValues.put(HelperValues.PROPERTY_TYPE, null);
            if (value instanceof AbstractGML) {
                if (((AbstractGML) value).isSetGmlID()) {
                    helperValues.put(HelperValues.GMLID, ((AbstractGML) value).getGmlId());
                } else {
                    String gmlId = JavaHelper.generateID(value.toString());
                    ((AbstractGML) value).setGmlId(gmlId);
                    helperValues.put(HelperValues.GMLID, gmlId);
                }
            } else {
                helperValues.put(HelperValues.GMLID, JavaHelper.generateID(value.toString()));
            }
            return helperValues;
        }

        @Override
        public XmlObject visit(XmlValue value)
                throws OwsExceptionReport {
            return value.getValue();
        }

        private static XmlObject defaultValue(Value<?> value) {
            LOG.warn("Can not encode named value value {}", value);
            return null;
        }
    }
}
