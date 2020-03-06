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
package org.n52.sos.converter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.convert.AbstractRequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractStreaming;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.ObservationMergeIndicator;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.ParameterHolder;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.JavaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EprtrConverter
        extends AbstractRequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> {
    
    private static final Logger LOGGER = 
            LoggerFactory.getLogger(EprtrConverter.class);

    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();
    private static final ObservationMergeIndicator indicator = new ObservationMergeIndicator().setFeatureOfInterest(true).setProcedure(true);

    private static Set<RequestResponseModifierKeyType> getKeyTypes() {
        Set<RequestResponseModifierKeyType> keys = Sets.newHashSet();
        keys.add(new RequestResponseModifierKeyType(Sos2Constants.SOS, Sos2Constants.SERVICEVERSION,
                new GetObservationRequest(), new GetObservationResponse()));
        keys.add(new RequestResponseModifierKeyType(Sos2Constants.SOS, Sos2Constants.SERVICEVERSION,
                new GetFeatureOfInterestRequest(), new GetFeatureOfInterestResponse()));
        return keys;
    }

    @Override
    public Set<RequestResponseModifierKeyType> getRequestResponseModifierKeyTypes() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public AbstractServiceRequest<?> modifyRequest(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        return request;
    }

    @Override
    public AbstractServiceResponse modifyResponse(AbstractServiceRequest<?> request, AbstractServiceResponse response)
            throws OwsExceptionReport {
        if (response instanceof GetObservationResponse) {
            if (mergeForEprtr()) {
                return mergeObservations((GetObservationResponse) response);
            } else {
                return checkGetObservationFeatures((GetObservationResponse) response);
            }
        }
        if (response instanceof GetObservationResponse && mergeForEprtr()) {
            return checkFeatures((GetFeatureOfInterestResponse) response);
        }
        return response;
    }

    private AbstractServiceResponse mergeObservations(GetObservationResponse response) throws OwsExceptionReport {
        response.setObservationCollection(mergeObservations(mergeStreamingData(response.getObservationCollection())));
        checkObservationFeatures(response.getObservationCollection());
        return response;
    }
    
    private AbstractServiceResponse checkGetObservationFeatures(GetObservationResponse response) {
        checkObservationFeatures(response.getObservationCollection());
        return response;
    }

    private void checkObservationFeatures(List<OmObservation> observationCollection) {
        for (OmObservation omObservation : observationCollection) {
            checkFeature(omObservation.getObservationConstellation().getFeatureOfInterest());
        }
    }

    private AbstractServiceResponse checkFeatures(GetFeatureOfInterestResponse response) {
        AbstractFeature abstractFeature = response.getAbstractFeature();
        if (abstractFeature instanceof FeatureCollection) {
            for (AbstractFeature feature : ((FeatureCollection) abstractFeature).getMembers().values()) {
                checkFeature(feature);
            }
        } else {
            checkFeature(abstractFeature);
        }
        return response;
    }

    private void checkFeature(AbstractFeature abstractFeature) {
        if (abstractFeature instanceof AbstractSamplingFeature) {
            AbstractSamplingFeature asf = (AbstractSamplingFeature) abstractFeature;
            if (asf.isSetParameter() && isPrtr(asf.getParameters())
                    && !containsConfidentialIndicator(asf.getParameters())) {
                NamedValue<Boolean> confidentialIndicator = new NamedValue<>();
                confidentialIndicator.setName(new ReferenceType("ConfidentialIndicator"));
                if (containsConfidentialCode(asf.getParameters())) {
                    confidentialIndicator.setValue(new BooleanValue(true));
                } else {
                    confidentialIndicator.setValue(new BooleanValue(false));
                }
                asf.addParameter(confidentialIndicator);
            }
        }
    }

    private boolean isPrtr(List<NamedValue<?>> parameters) {
        for (NamedValue<?> namedValue : parameters) {
            if (namedValue.getName().getHref().equalsIgnoreCase("MethodBasisCode") ||
                namedValue.getName().getHref().equalsIgnoreCase("AccidentalQuantity") ||
                namedValue.getName().getHref().equalsIgnoreCase("MediumCode")) {
                return true;
            }
        }
        return false;
    }

    private boolean containsConfidentialCode(List<NamedValue<?>> parameters) {
        for (NamedValue<?> namedValue : parameters) {
            if (namedValue.getName().getHref().equalsIgnoreCase("ConfidentialCode")) {
                return true;
            }
        }
        return false;
    }
    
    private boolean containsConfidentialIndicator(List<NamedValue<?>> parameters) {
        for (NamedValue<?> namedValue : parameters) {
            if (namedValue.getName().getHref().equalsIgnoreCase("ConfidentialIndicator")) {
                return true;
            }
        }
        return false;
    }

    private List<OmObservation> mergeStreamingData(List<OmObservation> obs) throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(obs)) {
            List<OmObservation> observations = Lists.newArrayList();
            if (hasStreamingData(obs.iterator().next())) {
                for (OmObservation observation : obs) {
                    AbstractStreaming values = (AbstractStreaming) observation.getValue();
                    if (values.hasNextValue()) {
                        observations.addAll(values.getObservation());
                    }
                }
            }
            return observations;
        }
        return obs;
    }

    private boolean hasStreamingData(OmObservation omObservation) {
        if (omObservation != null) {
            return omObservation.getValue() instanceof AbstractStreaming;
        }
        return false;
    }

    private List<OmObservation> mergeObservations(List<OmObservation> observations) throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(observations)) {
            final List<OmObservation> mergedObservations = new LinkedList<OmObservation>();
            int obsIdCounter = 1;
            for (final OmObservation sosObservation : observations) {
                if (checkForProcedure(sosObservation)) {
                    if (mergedObservations.isEmpty()) {
                        if (!sosObservation.isSetGmlID()) {
                            sosObservation.setObservationID(Integer.toString(obsIdCounter++));
                        }
                        mergedObservations.add(convertObservation(sosObservation));
                    } else {
                        boolean combined = false;
                        for (final OmObservation combinedSosObs : mergedObservations) {
                            if (checkForMerge(combinedSosObs, sosObservation, indicator)) {
                                mergeValues(combinedSosObs, convertObservation(sosObservation));
                                combined = true;
                                break;
                            }
                        }
                        if (!combined) {
                            mergedObservations.add(convertObservation(sosObservation));
                        }
                    }
                }
            }
            return mergedObservations;
        }
        return Lists.newArrayList(observations);
    }

    private boolean checkForProcedure(OmObservation sosObservation) {
        return "PollutantRelease".equals(sosObservation.getObservationConstellation().getProcedureIdentifier())
                || "PollutantTransfer".equals(sosObservation.getObservationConstellation().getProcedureIdentifier())
                || "WasteTransfer".equals(sosObservation.getObservationConstellation().getProcedureIdentifier());
    }

    private OmObservation convertObservation(OmObservation sosObservation) throws OwsExceptionReport {
        if ("PollutantRelease".equals(sosObservation.getObservationConstellation().getProcedureIdentifier())) {
            SweDataArrayValue value = new SweDataArrayValue();
            value.setValue(getPollutantReleaseArray(sosObservation.getValue().getValue().getUnit()));
            value.addBlock(createPollutantReleaseBlock(sosObservation));
            SingleObservationValue<SweDataArray> singleObservationValue = new SingleObservationValue<>(value);
            singleObservationValue.setPhenomenonTime(sosObservation.getPhenomenonTime());
            sosObservation.setValue(singleObservationValue);
            try {
                OmObservationConstellation obsConst = sosObservation.getObservationConstellation().clone();
                obsConst.setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
                obsConst.setObservableProperty(new OmObservableProperty("pollutants"));
                sosObservation.setObservationConstellation(obsConst);
            } catch (CloneNotSupportedException e) {
                LOGGER.error("Error while merging ePRTR data!", e);
            }
        } else if ("PollutantTransfer".equals(sosObservation.getObservationConstellation().getProcedureIdentifier())) {
            SweDataArrayValue value = new SweDataArrayValue();
            value.setValue(getPollutantTransferArray(sosObservation.getValue().getValue().getUnit()));
            value.addBlock(createPollutantTransferBlock(sosObservation));
            SingleObservationValue<SweDataArray> singleObservationValue = new SingleObservationValue<>(value);
            singleObservationValue.setPhenomenonTime(sosObservation.getPhenomenonTime());
            sosObservation.setValue(singleObservationValue);
            try {
                OmObservationConstellation obsConst = sosObservation.getObservationConstellation().clone();
                obsConst.setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
                obsConst.setObservableProperty(new OmObservableProperty("pollutants"));
                sosObservation.setObservationConstellation(obsConst);
            } catch (CloneNotSupportedException e) {
                LOGGER.error("Error while merging ePRTR data!", e);
            }
        } else if ("WasteTransfer".equals(sosObservation.getObservationConstellation().getProcedureIdentifier())) {
            SweDataArrayValue value = new SweDataArrayValue();
            value.setValue(getWasteTransferArray(sosObservation.getValue().getValue().getUnit()));
            value.addBlock(createWasteTransferBlock(sosObservation));
            SingleObservationValue<SweDataArray> singleObservationValue = new SingleObservationValue<>(value);
            singleObservationValue.setPhenomenonTime(sosObservation.getPhenomenonTime());
            sosObservation.setValue(singleObservationValue);
            try {
                OmObservationConstellation obsConst = sosObservation.getObservationConstellation().clone();
                obsConst.setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
                obsConst.setObservableProperty(new OmObservableProperty("pollutants"));
                sosObservation.setObservationConstellation(obsConst);
            } catch (CloneNotSupportedException e) {
                LOGGER.error("Error while merging ePRTR data!", e);
            }
        }
        return sosObservation;
    }

    private List<String> createPollutantReleaseBlock(OmObservation sosObservation) {
        List<String> values = new LinkedList<>();
        values.add(getYear(sosObservation.getPhenomenonTime()));
        // MediumCode
        values.add(getMediumCode(sosObservation.getObservationConstellation()));
        // PollutantCode
        values.add(sosObservation.getObservationConstellation().getObservablePropertyIdentifier());
        values.add(getParameter(sosObservation.getParameterHolder(), "MethodBasisCode"));
        values.add(getParameter(sosObservation.getParameterHolder(), "MethodUsed"));
        values.add(JavaHelper.asString(sosObservation.getValue().getValue().getValue()));
        values.add(getParameter(sosObservation.getParameterHolder(), "AccidentalQuantity"));
        String confidentialCode = getParameter(sosObservation.getParameterHolder(), "ConfidentialCode");
        values.add(getConfidentialIndicator(confidentialCode, sosObservation.getParameterHolder()));
        values.add(confidentialCode);
        values.add(getParameter(sosObservation.getParameterHolder(), "RemarkText"));
        return values;
    }

    private String getMediumCode(OmObservationConstellation observationConstellation) {
        if (observationConstellation.isSetOfferings()) {
            Optional<String> offering = observationConstellation.getOfferings().stream().findFirst();
            return offering.isPresent() ? offering.get() : "AIR";
        }
        return "AIR";
    }

    private SweDataArray getPollutantReleaseArray(String unit) {
        SweDataRecord record = new SweDataRecord();
        record.addName("pollutants");
        record.addField(new SweField("Year", new SweTime().setUom(OmConstants.PHEN_UOM_ISO8601).setDefinition("Year")));
        record.addField(new SweField("MediumCode", new SweText().setDefinition("MediumCode")));
        record.addField(new SweField("PollutantCode", new SweText().setDefinition("PollutantCode")));
        record.addField(new SweField("MethodBasisCode", new SweText().setDefinition("MethodBasisCode")));
        record.addField(new SweField("MethodUsed", new SweText().setDefinition("MethodUsed")));
        record.addField(new SweField("TotalQuantity", new SweQuantity().setUom(unit).setDefinition("TotalQuantity")));
        record.addField(new SweField("AccidentalQuantity", new SweQuantity().setUom(unit).setDefinition("AccidentalQuantity")));
        record.addField(new SweField("ConfidentialIndicator", new SweBoolean().setDefinition("ConfidentialIndicator")));
        record.addField(new SweField("ConfidentialCode", new SweText().setDefinition("ConfidentialCode")));
        record.addField(new SweField("RemarkText", new SweText().setDefinition("RemarkText")));
        SweDataArray array = new SweDataArray();
        array.setElementType(record);
        array.setEncoding(getEncoding());
        return array;
    }
    
    private List<String> createPollutantTransferBlock(OmObservation sosObservation) {
        List<String> values = new LinkedList<>();
        values.add(getYear(sosObservation.getPhenomenonTime()));
        values.add(sosObservation.getObservationConstellation().getObservablePropertyIdentifier());
        values.add(getParameter(sosObservation.getParameterHolder(), "MethodBasisCode"));
        values.add(getParameter(sosObservation.getParameterHolder(), "MethodUsed"));
        values.add(JavaHelper.asString(sosObservation.getValue().getValue().getValue()));
        String confidentialCode = getParameter(sosObservation.getParameterHolder(), "ConfidentialCode");
        values.add(getConfidentialIndicator(confidentialCode, sosObservation.getParameterHolder()));
        values.add(confidentialCode);
        values.add(getParameter(sosObservation.getParameterHolder(), "RemarkText"));
        return values;
    }
    
    private SweDataArray getPollutantTransferArray(String unit) {
        SweDataRecord record = new SweDataRecord();
        record.addName("pollutants");
        record.addField(new SweField("Year", new SweTime().setUom(OmConstants.PHEN_UOM_ISO8601).setDefinition("Year")));
        record.addField(new SweField("PollutantCode", new SweText().setDefinition("PollutantCode")));
        record.addField(new SweField("MethodBasisCode", new SweText().setDefinition("MethodBasisCode")));
        record.addField(new SweField("MethodUsed", new SweText().setDefinition("MethodUsed")));
        record.addField(new SweField("Quantity", new SweQuantity().setUom(unit).setDefinition("Quantity")));
        record.addField(new SweField("ConfidentialIndicator", new SweBoolean().setDefinition("ConfidentialIndicator")));
        record.addField(new SweField("ConfidentialCode", new SweText().setDefinition("ConfidentialCode")));
        record.addField(new SweField("RemarkText", new SweText().setDefinition("RemarkText")));
        SweDataArray array = new SweDataArray();
        array.setElementType(record);
        array.setEncoding(getEncoding());
        return array;
    }
    
    private List<String> createWasteTransferBlock(OmObservation sosObservation) throws OwsExceptionReport {
        List<String> values = new LinkedList<>();
        values.add(getYear(sosObservation.getPhenomenonTime()));
        values.add(sosObservation.getObservationConstellation().getObservablePropertyIdentifier());
        values.add(getWasteTreatmentCode(sosObservation.getObservationConstellation().getOfferings()));
        values.add(JavaHelper.asString(sosObservation.getValue().getValue().getValue()));
        values.add(getParameter(sosObservation.getParameterHolder(), "MethodBasisCode"));
        values.add(getParameter(sosObservation.getParameterHolder(), "MethodUsed"));
        String confidentialCode = getParameter(sosObservation.getParameterHolder(), "ConfidentialCode");
        values.add(getConfidentialIndicator(confidentialCode, sosObservation.getParameterHolder()));
        values.add(confidentialCode);
        values.add(getParameter(sosObservation.getParameterHolder(), "RemarkText"));
        values.add(getWasteHandlerPartyParameter(sosObservation.getParameterHolder(), "WasteHandlerParty"));
        return values;
    }
    
    private String getConfidentialIndicator(String confidentialCode, ParameterHolder parameterHolder) {
        String confidentialIndicator = getParameter(parameterHolder, "ConfidentialIndicator");
        return confidentialIndicator != null && !confidentialIndicator.isEmpty() 
                ? confidentialIndicator
                : confidentialCode != null && !confidentialCode.isEmpty()
                        ? "true" 
                        : "false";
    }

    private SweDataArray getWasteTransferArray(String unit) {
        SweDataRecord record = new SweDataRecord();
        record.addName("pollutants");
        record.addField(new SweField("Year", new SweTime().setUom(OmConstants.PHEN_UOM_ISO8601).setDefinition("Year")));
        record.addField(new SweField("WasteTypeCode", new SweText().setDefinition("WasteTypeCode")));
        record.addField(new SweField("WasteTreatmentCode", new SweText().setDefinition("WasteTreatmentCode")));
        record.addField(new SweField("Quantity", new SweQuantity().setUom(unit).setDefinition("Quantity")));
        record.addField(new SweField("MethodBasisCode", new SweText().setDefinition("MethodBasisCode")));
        record.addField(new SweField("MethodUsed", new SweText().setDefinition("MethodUsed")));
        record.addField(new SweField("ConfidentialIndicator", new SweBoolean().setDefinition("ConfidentialIndicator")));
        record.addField(new SweField("ConfidentialCode", new SweText().setDefinition("ConfidentialCode")));
        record.addField(new SweField("RemarkText", new SweText().setDefinition("RemarkText")));
        record.addField(new SweField("WasteHandlerParty", createWasteHandlerPary()));
        SweDataArray array = new SweDataArray();
        array.setElementType(record);
        array.setEncoding(getEncoding());
        return array;
    }
    
    private String getWasteTreatmentCode(Set<String> offerings) {
        for (String offering : offerings) {
            if (offering.length() == 1) {
                return offering;
            }
        }
        return "";
    }

    private SweAbstractDataComponent createWasteHandlerPary() {
        SweDataRecord record = new SweDataRecord();
        record.setDefinition("WasteHandlerParty");
        record.addField(new SweField("Name", new SweText().setDefinition("Name")));
        record.addField(new SweField("Address", createAddressRecord("Address")));
        record.addField(new SweField("SiteAddress", createAddressRecord("SiteAddress")));
        return record;
    }

    private SweAbstractDataComponent createAddressRecord(String definition) {
        SweDataRecord record = new SweDataRecord();
        record.setDefinition(definition);
        record.addField(new SweField("StreetName", new SweText().setDefinition("StreetName")));
        record.addField(new SweField("BuildingNumber", new SweText().setDefinition("BuildingNumber")));
        record.addField(new SweField("CityName", new SweText().setDefinition("CityName")));
        record.addField(new SweField("PostcodeCode", new SweText().setDefinition("PostcodeCode")));
        record.addField(new SweField("CountryID", new SweText().setDefinition("CountryID")));
        return record;
    }

    private String getYear(Time phenomenonTime) {
        if (phenomenonTime instanceof TimePeriod) {
            return Integer.toString(((TimePeriod) phenomenonTime).getEnd().getYear());
        }
        return Integer.toString(((TimeInstant) phenomenonTime).getValue().getYear());
    }

    private String getParameter(ParameterHolder holder, String name) {
        Object parameterObject = getParameterObject(holder, name);
        return parameterObject != null ? parameterObject.toString() : "";
    }
    
    private Object getParameterObject(ParameterHolder holder, String name) {
        for (NamedValue<?> namedValue : holder.getParameter()) {
            if (name.equals(namedValue.getName().getHref())) {
                holder.removeParameter(namedValue);
                if(namedValue.getValue().isSetValue()) {
                    return namedValue.getValue().getValue();
                }
            }
        }
        return null;
    }

    private String getWasteHandlerPartyParameter(ParameterHolder parameterHolder, String name) throws OwsExceptionReport {
        Object parameterObject = getParameterObject(parameterHolder, name);
        if (parameterObject != null && parameterObject instanceof XmlObject) {
            Object xml = CodingHelper.decodeXmlObject((XmlObject) parameterObject);
            if (xml instanceof SweDataRecord) {
                List<String> values = getValuesFromRecord((SweDataRecord) xml);
                if (!values.isEmpty()) {
                    return Joiner.on(",").join(values);
                }
            }
        }
        return ",,,,,,,,,,";
    }
    
    private List<String> getValuesFromRecord( SweDataRecord record) {
        List<String> values = new LinkedList<>();
        if (record.isSetFields()) {
            for (SweField field : record.getFields()) {
                if (field.getElement() instanceof SweText) {
                    values.add(((SweText) field.getElement()).getValue());
                } else if (field.getElement() instanceof SweDataRecord) {
                    values.addAll(getValuesFromRecord((SweDataRecord) field.getElement()));
                }
            }
        }
        return values;
    }

    private SweTextEncoding getEncoding() {
        SweTextEncoding encoding = new SweTextEncoding();
        encoding.setBlockSeparator("#");
        encoding.setTokenSeparator(",");
        return encoding;
    }

    private void mergeValues(OmObservation combinedSosObs, OmObservation sosObservation) {
        SweDataArray combinedValue = (SweDataArray) combinedSosObs.getValue().getValue().getValue();
        SweDataArray value = (SweDataArray) sosObservation.getValue().getValue().getValue();
        if (value.isSetValues()) {
            combinedValue.addAll(value.getValues());
            if (combinedSosObs.getPhenomenonTime() instanceof TimePeriod) {
                ((TimePeriod) combinedSosObs.getPhenomenonTime()).extendToContain(sosObservation.getPhenomenonTime());
            }
        }
    }

    protected boolean checkForMerge(OmObservation observation, OmObservation observationToAdd, ObservationMergeIndicator observationMergeIndicator) {
        boolean merge = true;
        if (observation.isSetAdditionalMergeIndicator() && observationToAdd.isSetAdditionalMergeIndicator()) {
            merge = observation.getAdditionalMergeIndicator().equals(observationToAdd.getAdditionalMergeIndicator());
        } else if ((observation.isSetAdditionalMergeIndicator() && !observationToAdd.isSetAdditionalMergeIndicator())
                || (!observation.isSetAdditionalMergeIndicator() && observationToAdd.isSetAdditionalMergeIndicator())) {
            merge = false;
        }
        if (observationMergeIndicator.isProcedure()) {
            merge = merge && checkForProcedure(observation, observationToAdd);
        }
        if (observationMergeIndicator.isFeatureOfInterest()) {
            merge = merge && checkForFeatureOfInterest(observation, observationToAdd);
        }
        return merge;
        
    }
    
    private boolean checkForProcedure(OmObservation observation, OmObservation observationToAdd) {
        return observation.getObservationConstellation().getProcedure().equals(observationToAdd.getObservationConstellation().getProcedure());
    }

    private boolean checkForFeatureOfInterest(OmObservation observation, OmObservation observationToAdd) {
        return observation.getObservationConstellation().getFeatureOfInterest().equals(observationToAdd.getObservationConstellation().getFeatureOfInterest());
    }
    
    private boolean mergeForEprtr() {
        return ServiceConfiguration.getInstance().isMergeForEprtr();
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return super.getFacilitator().setMerger(true).setSplitter(false);
    }

}
