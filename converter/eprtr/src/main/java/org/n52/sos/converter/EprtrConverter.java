/**
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
import java.util.Locale;
import java.util.Set;

import org.n52.sos.convert.AbstractRequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractStreaming;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.ObservationMergeIndicator;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.ParameterHolder;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swe.DataRecord;
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
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.JavaHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.neovisionaries.i18n.CountryCode;

public class EprtrConverter
        extends AbstractRequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> {

    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();
    private static final ObservationMergeIndicator indicator = new ObservationMergeIndicator().setFeatureOfInterest(true).setProcedure(true);

    private static Set<RequestResponseModifierKeyType> getKeyTypes() {
        Set<RequestResponseModifierKeyType> keys = Sets.newHashSet();
        keys.add(new RequestResponseModifierKeyType(Sos2Constants.SOS, Sos2Constants.SERVICEVERSION,
                new GetObservationRequest()));
        keys.add(new RequestResponseModifierKeyType(Sos2Constants.SOS, Sos2Constants.SERVICEVERSION,
                new GetObservationRequest(), new GetObservationResponse()));
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
        if (response instanceof GetObservationResponse && mergeForEprtr()) {
            return mergeObservations((GetObservationResponse) response);
        }
        return response;
    }

    private AbstractServiceResponse mergeObservations(GetObservationResponse response) throws OwsExceptionReport {
        response.setObservationCollection(mergeObservations(mergeStreamingData(response.getObservationCollection())));
        return response;
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
            sosObservation.getObservationConstellation().setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            sosObservation.getObservationConstellation().setObservableProperty(new OmObservableProperty("pollutants"));
        } else if ("PollutantTransfer".equals(sosObservation.getObservationConstellation().getProcedureIdentifier())) {
            SweDataArrayValue value = new SweDataArrayValue();
            value.setValue(getPollutantTransferArray(sosObservation.getValue().getValue().getUnit()));
            value.addBlock(createPollutantTransferBlock(sosObservation));
            SingleObservationValue<SweDataArray> singleObservationValue = new SingleObservationValue<>(value);
            singleObservationValue.setPhenomenonTime(sosObservation.getPhenomenonTime());
            sosObservation.setValue(singleObservationValue);
            sosObservation.getObservationConstellation().setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            sosObservation.getObservationConstellation().setObservableProperty(new OmObservableProperty("pollutants"));
        } else if ("WasteTransfer".equals(sosObservation.getObservationConstellation().getProcedureIdentifier())) {
            SweDataArrayValue value = new SweDataArrayValue();
            value.setValue(getWasteTransferArray(sosObservation.getValue().getValue().getUnit()));
            value.addBlock(createWasteTransferBlock(sosObservation));
            SingleObservationValue<SweDataArray> singleObservationValue = new SingleObservationValue<>(value);
            singleObservationValue.setPhenomenonTime(sosObservation.getPhenomenonTime());
            sosObservation.setValue(singleObservationValue);
            sosObservation.getObservationConstellation().setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            sosObservation.getObservationConstellation().setObservableProperty(new OmObservableProperty("pollutants"));
        }
        return sosObservation;
    }

    private List<String> createPollutantReleaseBlock(OmObservation sosObservation) {
        List<String> values = new LinkedList<>();
        values.add(getYear(sosObservation.getPhenomenonTime()));
        values.add(getParameter(sosObservation.getParameterHolder(), "MediumCode"));
        values.add(sosObservation.getObservationConstellation().getObservablePropertyIdentifier());
        values.add(getParameter(sosObservation.getParameterHolder(), "MethodBasisCode"));
        values.add(getParameter(sosObservation.getParameterHolder(), "MethodUsed"));
        values.add(JavaHelper.asString(sosObservation.getValue().getValue().getValue()));
        values.add(getParameter(sosObservation.getParameterHolder(), "AccidentalQuantity"));
        values.add(getParameter(sosObservation.getParameterHolder(), "ConfidentialIndicator"));
        values.add(getParameter(sosObservation.getParameterHolder(), "ConfidentialCode"));
        values.add(getParameter(sosObservation.getParameterHolder(), "RemarkText"));
        return values;
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
        values.add(getParameter(sosObservation.getParameterHolder(), "ConfidentialIndicator"));
        values.add(getParameter(sosObservation.getParameterHolder(), "ConfidentialCode"));
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
    
    private List<String> createWasteTransferBlock(OmObservation sosObservation) {
        List<String> values = new LinkedList<>();
        values.add(getYear(sosObservation.getPhenomenonTime()));
        values.add(sosObservation.getObservationConstellation().getObservablePropertyIdentifier());
        values.add(getWasteTreatmentCode(sosObservation.getObservationConstellation().getOfferings()));
        values.add(JavaHelper.asString(sosObservation.getValue().getValue().getValue()));
        values.add(getParameter(sosObservation.getParameterHolder(), "MethodBasisCode"));
        values.add(getParameter(sosObservation.getParameterHolder(), "MethodUsed"));
        values.add(getParameter(sosObservation.getParameterHolder(), "ConfidentialIndicator"));
        values.add(getParameter(sosObservation.getParameterHolder(), "ConfidentialCode"));
        values.add(getParameter(sosObservation.getParameterHolder(), "RemarkText"));
        values.add(getParameter(sosObservation.getParameterHolder(), "WasteHandlerParty"));
        return values;
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
        for (NamedValue<?> namedValue : holder.getParameter()) {
            if (name.equals(namedValue.getName().getHref())) {
                holder.removeParameter(namedValue);
                if(namedValue.getValue().isSetValue()) {
                    return namedValue.getValue().getValue().toString();
                }
            }
        }
        return "";
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
