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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.convert.AbstractRequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.AbstractStreaming;
import org.n52.sos.ogc.om.ObservationMergeIndicator;
import org.n52.sos.ogc.om.ObservationMerger;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.GetObservationByIdResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.response.InsertObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceSettings;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Configurable
public class SplitMergeObservations
        extends AbstractRequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SplitMergeObservations.class);
    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();
    private boolean includeResultTimeForMerging = false;
    private boolean checkForDuplicity = false;
    
    
    @Setting(ServiceSettings.INCLUDE_RESULT_TIME_FOR_MERGING)
    public void setIncludeResultTimeForMerging(boolean includeResultTimeForMerging) {
        this.includeResultTimeForMerging = includeResultTimeForMerging;
    }
    
    @Setting(ServiceSettings.CHECK_FOR_DUPLICITY)
    public void setCheckForDuplicity(boolean checkForDuplicity) {
        this.checkForDuplicity = checkForDuplicity;
    } 

    private static Set<RequestResponseModifierKeyType> getKeyTypes() {
        Set<String> services = Sets.newHashSet(SosConstants.SOS);
        Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION);
        Map<AbstractServiceRequest<?>, AbstractServiceResponse> requestResponseMap = Maps.newHashMap();

        requestResponseMap.put(new GetObservationRequest(), new GetObservationResponse());
        requestResponseMap.put(new GetObservationByIdRequest(), new GetObservationByIdResponse());
        requestResponseMap.put(new InsertObservationRequest(), new InsertObservationResponse());
        Set<RequestResponseModifierKeyType> keys = Sets.newHashSet();
        for (String service : services) {
            for (String version : versions) {
                for (AbstractServiceRequest<?> request : requestResponseMap.keySet()) {
                    keys.add(new RequestResponseModifierKeyType(service, version, request));
                    keys.add(new RequestResponseModifierKeyType(service, version, request,
                            requestResponseMap.get(request)));
                }
            }
        }
        return keys;
    }

    @Override
    public Set<RequestResponseModifierKeyType> getRequestResponseModifierKeyTypes() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public AbstractServiceRequest<?> modifyRequest(AbstractServiceRequest<?> request) throws OwsExceptionReport {
        if (request instanceof InsertObservationRequest) {
            splitObservations((InsertObservationRequest) request);
        } else if (request instanceof AbstractObservationRequest) {
            checkGetObservationRequest((AbstractObservationRequest) request);
        }
        if (request instanceof AbstractObservationRequest) {
            AbstractObservationRequest req = (AbstractObservationRequest) request;
            if (req.isSetResponseFormat() ) {
                if (checkForDuplicity && (OmConstants.NS_OM_2.equals(req.getResponseFormat())
                        || OmConstants.NS_OM.equals(req.getResponseFormat())
                        || OmConstants.CONTENT_TYPE_OM.toString().equals(req.getResponseFormat())
                        || OmConstants.CONTENT_TYPE_OM_2.toString().equals(req.getResponseFormat()))) {
                    req.setCheckForDuplicity(checkForDuplicity);
                }
            }
        }
        return request;
    }

    private void splitObservations(InsertObservationRequest request) throws OwsExceptionReport {
        if (request.isSetExtensionSplitDataArrayIntoObservations()) {
            splitDataArrayIntoObservations(request);
        }
    }

    private void checkGetObservationRequest(AbstractObservationRequest request) {
        if (request.isSetResultModel()) {
            if (OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(request.getResultModel())) {
                request.addExtension(new SwesExtensionImpl<SweBoolean>()
                        .setDefinition(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name())
                        .setValue((SweBoolean) new SweBoolean().setValue(true)
                                .setDefinition(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name())));
            }
        }
    }

    private void splitDataArrayIntoObservations(final InsertObservationRequest request) throws OwsExceptionReport {
        LOGGER.debug("Start splitting observations. Count: {}", request.getObservations().size());
        final Collection<OmObservation> finalObservationCollection = Sets.newHashSet();
        for (final OmObservation observation : request.getObservations()) {
            if (isSweArrayObservation(observation)) {
                LOGGER.debug("Found SweArrayObservation to split.");
                final SweDataArrayValue sweDataArrayValue = (SweDataArrayValue) observation.getValue().getValue();
                final OmObservationConstellation observationConstellation = observation.getObservationConstellation();
                int counter = 0;
                final int resultTimeIndex =
                        getResultTimeIndex((SweDataRecord) sweDataArrayValue.getValue().getElementType());
                final int phenomenonTimeIndex =
                        getPhenomenonTimeIndex((SweDataRecord) sweDataArrayValue.getValue().getElementType());
                final int resultValueIndex =
                        getResultValueIndex((SweDataRecord) sweDataArrayValue.getValue().getElementType(),
                                observationConstellation.getObservableProperty());
                observationConstellation.setObservationType(getObservationTypeFromElementType(
                        (SweDataRecord) sweDataArrayValue.getValue().getElementType(),
                        observationConstellation.getObservableProperty()));
                // split into single observation
                CodeWithAuthority metaIdentifier = null;
                if (observation.isSetIdentifier()) {
                    metaIdentifier = observation.getIdentifierCodeWithAuthority();
                }

                for (final List<String> block : sweDataArrayValue.getValue().getValues()) {
                    LOGGER.debug("Processing block {}/{}", ++counter, sweDataArrayValue.getValue().getValues().size());
                    final OmObservation newObservation = new OmObservation();
                    newObservation.setObservationConstellation(observationConstellation);
                    // identifier
                    if (observation.isSetIdentifier()) {
                        newObservation.setIdentifier(new CodeWithAuthority(metaIdentifier.getValue() + counter,
                                metaIdentifier.getCodeSpace()));
                    }
                    // phen time
                    Time phenomenonTime;
                    if (phenomenonTimeIndex == -1) {
                        phenomenonTime = observation.getPhenomenonTime();
                    } else {
                        phenomenonTime = DateTimeHelper.parseIsoString2DateTime2Time(block.get(phenomenonTimeIndex));
                    }
                    // result time
                    if (resultTimeIndex == -1) {
                        // use phenomenon time if outer observation's resultTime
                        // value
                        // or nilReason is "template"
                        if ((!observation.isSetResultTime() || observation.isTemplateResultTime())
                                && phenomenonTime instanceof TimeInstant) {
                            newObservation.setResultTime((TimeInstant) phenomenonTime);
                        } else {
                            newObservation.setResultTime(observation.getResultTime());
                        }
                    } else {
                        newObservation.setResultTime(
                                new TimeInstant(DateTimeHelper.parseIsoString2DateTime(block.get(resultTimeIndex))));
                    }
                    if (observation.isSetParameter()) {
                        newObservation.setParameter(observation.getParameter());
                    }
                    // value
                    final ObservationValue<?> value = createObservationResultValue(
                            observationConstellation.getObservationType(), block.get(resultValueIndex), phenomenonTime,
                            ((SweDataRecord) sweDataArrayValue.getValue().getElementType()).getFields()
                                    .get(resultValueIndex));
                    newObservation.setValue(value);
                    finalObservationCollection.add(newObservation);
                }
            } else {
                LOGGER.debug("Found non splittable observation");
                finalObservationCollection.add(observation);
            }
        }
        request.setObservation(Lists.newArrayList(finalObservationCollection));
    }

    private ObservationValue<?> createObservationResultValue(final String observationType, final String valueString,
            final Time phenomenonTime, final SweField resultDefinitionField) throws OwsExceptionReport {
        ObservationValue<?> value = null;

        if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_TRUTH_OBSERVATION)) {
            value = new SingleObservationValue<Boolean>(new BooleanValue(Boolean.parseBoolean(valueString)));
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_COUNT_OBSERVATION)) {
            value = new SingleObservationValue<Integer>(new CountValue(Integer.parseInt(valueString)));
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_MEASUREMENT)) {
            final QuantityValue quantity = new QuantityValue(Double.parseDouble(valueString));
            quantity.setUnit(getUom(resultDefinitionField));
            value = new SingleObservationValue<Double>(quantity);
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION)) {
            final CategoryValue cat = new CategoryValue(valueString);
            cat.setUnit(getUom(resultDefinitionField));
            value = new SingleObservationValue<String>(cat);
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_TEXT_OBSERVATION)) {
            value = new SingleObservationValue<String>(new TextValue(valueString));
        }
        // TODO Check for missing types
        if (value != null) {
            value.setPhenomenonTime(phenomenonTime);
        } else {
            throw new NoApplicableCodeException().withMessage("Observation type '{}' not supported.", observationType)
                    .setStatus(HTTPStatus.BAD_REQUEST);
        }
        return value;
    }

    private String getUom(final SweField resultDefinitionField) {
        return ((SweAbstractUomType<?>) resultDefinitionField.getElement()).getUom();
    }

    private int getResultValueIndex(final SweDataRecord elementTypeDataRecord,
            final AbstractPhenomenon observableProperty) {
        return elementTypeDataRecord.getFieldIndexByIdentifier(observableProperty.getIdentifier());
    }

    private int getPhenomenonTimeIndex(final SweDataRecord elementTypeDataRecord) {
        return elementTypeDataRecord.getFieldIndexByIdentifier(OmConstants.PHENOMENON_TIME);
    }

    private int getResultTimeIndex(final SweDataRecord elementTypeDataRecord) {
        return elementTypeDataRecord.getFieldIndexByIdentifier(OmConstants.PHEN_SAMPLING_TIME);
    }

    private String getObservationTypeFromElementType(final SweDataRecord elementTypeDataRecord,
            final AbstractPhenomenon observableProperty) throws OwsExceptionReport {
        for (final SweField sweField : elementTypeDataRecord.getFields()) {
            if (sweField.getElement() != null && sweField.getElement().isSetDefinition()
                    && sweField.getElement().getDefinition().equalsIgnoreCase(observableProperty.getIdentifier())) {
                return OMHelper.getObservationTypeFrom(sweField.getElement());
            }
        }
        throw new NoApplicableCodeException().withMessage(
                "Not able to derive observation type from elementType element '{}' for observable property '{}'.",
                elementTypeDataRecord, observableProperty).setStatus(HTTPStatus.BAD_REQUEST);
    }

    private boolean isSweArrayObservation(final OmObservation observation) {
        return observation.getObservationConstellation().getObservationType()
                .equalsIgnoreCase(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION)
                && observation.getValue().getValue() instanceof SweDataArrayValue
                && ((SweDataArrayValue) observation.getValue().getValue()).isSetValue();
    }

    @Override
    public AbstractServiceResponse modifyResponse(AbstractServiceRequest<?> request, AbstractServiceResponse response)
            throws OwsExceptionReport {
        if (request instanceof AbstractObservationRequest && response instanceof AbstractObservationResponse) {
            return mergeObservations((AbstractObservationRequest) request, (AbstractObservationResponse) response);
        }
        if (response instanceof AbstractObservationResponse) {
            return mergeObservations((AbstractObservationResponse) response);
        }
        return response;
    }

    private AbstractServiceResponse mergeObservations(AbstractObservationRequest request, AbstractObservationResponse response)
            throws OwsExceptionReport {
        boolean checkForMergeObservationsInResponse = checkForMergeObservationsInResponse(request);
        request.setMergeObservationValues(checkForMergeObservationsInResponse);
        boolean checkEncoderForMergeObservations = checkEncoderForMergeObservations(response);
        if (checkForMergeObservationsInResponse || checkEncoderForMergeObservations) {
            if (!response.hasStreamingData()) {
                mergeObservationsWithSameConstellation(response);
            } else {
                for (OmObservation observation : response.getObservationCollection()) {
                    if (observation.getValue() instanceof AbstractStreaming) {
                        ((AbstractStreaming) observation.getValue()).setObservationMergeIndicator(
                                ObservationMergeIndicator.defaultObservationMergerIndicator()
                                        .setResultTime(includeResultTimeForMerging));
                    }
                }
                response.setObservationMergeIndicator(ObservationMergeIndicator.defaultObservationMergerIndicator()
                        .setResultTime(includeResultTimeForMerging));
            }
            response.setMergeObservations(true);
        }
        return response;
    }

    private void mergeObservationsWithSameConstellation(AbstractObservationResponse response) {
        // TODO merge all observations with the same observationContellation
        // FIXME Failed to set the observation type to sweArrayObservation for
        // the merged Observations
        // (proc, obsProp, foi, off)
        if (response.getObservationCollection() != null) {
            response.setObservationCollection(new ObservationMerger()
                    .mergeObservations(response.getObservationCollection(), ObservationMergeIndicator
                            .defaultObservationMergerIndicator().setResultTime(includeResultTimeForMerging)));
        }
    }

    private boolean checkEncoderForMergeObservations(AbstractObservationResponse response) throws OwsExceptionReport {
        if (response.isSetResponseFormat()) {
            // check for XML encoder
            ObservationEncoder<Object, Object> encoder =
                    (ObservationEncoder<Object, Object>) CodingRepository.getInstance().getEncoder(
                            new XmlEncoderKey(response.getResponseFormat(), OmObservation.class));
            // check for response contentType
            if (encoder == null && response.isSetContentType()) {
                encoder =
                        (ObservationEncoder<Object, Object>) CodingRepository.getInstance().getEncoder(
                                new OperationEncoderKey(response.getService(), response.getVersion(), response
                                        .getOperationName(), response.getContentType()));
            }
            // check for responseFormat as MediaType
            if (encoder == null && response.isSetResponseFormat()) {
                try {
                    encoder =
                            (ObservationEncoder<Object, Object>) CodingRepository.getInstance().getEncoder(
                                    new OperationEncoderKey(response.getService(), response.getVersion(), response
                                            .getOperationName(), MediaType.parse(response.getResponseFormat())));
                } catch (IllegalArgumentException iae) {
                    LOGGER.debug("ResponseFormat isNot a XML response format");
                }
            }

            if (encoder != null && encoder.shouldObservationsWithSameXBeMerged()) {
                if (Sos1Constants.SERVICEVERSION.equals(response.getVersion())) {
                    return checkResultModel(response);
                }
                return true;
            }

        }
        return false;
    }

    private boolean checkResultModel(AbstractObservationResponse response) {
        if (response.isSetResultModel()) {
            if (!OmConstants.OBS_TYPE_OBSERVATION.equals(response.getResultModel())) {
                return false;
            }
        }
        return true;
    }

    private AbstractServiceResponse mergeObservations(AbstractObservationResponse response) throws OwsExceptionReport {
        boolean checkEncoderForMergeObservations = checkEncoderForMergeObservations(response);
        if (checkEncoderForMergeObservations && !response.hasStreamingData()) {
            if (!response.hasStreamingData()) {
                mergeObservationsWithSameConstellation(response);
            }
            response.setMergeObservations(checkEncoderForMergeObservations);
        }
        return response;
    }

    private boolean checkForMergeObservationsInResponse(AbstractObservationRequest sosRequest) {
        if (getActiveProfile().isMergeValues() || isSetExtensionMergeObservationsToSweDataArray(sosRequest)) {
            return true;
        }
        return false;
    }

    private boolean isSetExtensionMergeObservationsToSweDataArray(final AbstractObservationRequest sosRequest) {
        return sosRequest.isSetExtensions() && sosRequest.getExtensions()
                .isBooleanExtensionSet(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name());
    }

    protected Profile getActiveProfile() {
        return Configurator.getInstance().getProfileHandler().getActiveProfile();
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return super.getFacilitator().setMerger(true).setSplitter(true);
    }

}
