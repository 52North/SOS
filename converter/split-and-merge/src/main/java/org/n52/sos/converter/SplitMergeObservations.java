/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.RequestResponseModifier;
import org.n52.iceland.convert.RequestResponseModifierFacilitator;
import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.janmayen.http.MediaType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.ObservationMergeIndicator;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.ObservationValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.InsertObservationRequest;
import org.n52.shetland.ogc.sos.response.AbstractObservationResponse;
import org.n52.shetland.ogc.sos.response.AbstractStreaming;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.ogc.sos.response.InsertObservationResponse;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swes.SwesExtension;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.OMHelper;
import org.n52.sos.service.SosSettings;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.ObservationEncoder;
import org.n52.svalbard.encode.OperationResponseEncoderKey;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class SplitMergeObservations
        implements RequestResponseModifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(SplitMergeObservations.class);

    private static final Set<RequestResponseModifierKey> REQUEST_RESPONSE_MODIFIER_KEY_TYPES =
            Stream.of(SosConstants.SOS)
                    .flatMap(service -> Stream.of(Sos1Constants.SERVICEVERSION, Sos2Constants.SERVICEVERSION)
                            .flatMap(version -> Stream.of(
                                    new RequestResponseModifierKey(service, version, new GetObservationRequest()),
                                    new RequestResponseModifierKey(service, version, new GetObservationRequest(),
                                            new GetObservationResponse()),
                                    new RequestResponseModifierKey(service, version, new InsertObservationRequest()),
                                    new RequestResponseModifierKey(service, version, new InsertObservationRequest(),
                                            new InsertObservationResponse()))))
                    .collect(toSet());

    private EncoderRepository encoderRepository;

    private ProfileHandler profileHandler;

    private boolean includeResultTimeForMerging;

    private boolean checkForDuplicity;

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Setting(SosSettings.INCLUDE_RESULT_TIME_FOR_MERGING)
    public void setIncludeResultTimeForMerging(boolean includeResultTimeForMerging) {
        this.includeResultTimeForMerging = includeResultTimeForMerging;
    }

    @Setting(SosSettings.CHECK_FOR_DUPLICITY)
    public void setCheckForDuplicity(boolean checkForDuplicity) {
        this.checkForDuplicity = checkForDuplicity;
    }

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return new RequestResponseModifierFacilitator().setMerger(true).setSplitter(true);
    }

    @Override
    public Set<RequestResponseModifierKey> getKeys() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public OwsServiceRequest modifyRequest(OwsServiceRequest request)
            throws OwsExceptionReport {
        if (request instanceof InsertObservationRequest) {
            splitObservations((InsertObservationRequest) request);
        } else if (request instanceof AbstractObservationRequest) {
            checkGetObservationRequest((AbstractObservationRequest) request);
        }
        if (request instanceof AbstractObservationRequest) {
            AbstractObservationRequest req = (AbstractObservationRequest) request;
            if (req.isSetResponseFormat()) {
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

    private void splitObservations(InsertObservationRequest request)
            throws OwsExceptionReport {
        if (request.isSetExtensionSplitDataArrayIntoObservations()) {
            splitDataArrayIntoObservations(request);
        }
    }

    private void checkGetObservationRequest(AbstractObservationRequest request) {
        if (request.isSetResultModel()) {
            if (OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(request.getResultModel())) {
                request.addExtension(new SwesExtension<SweBoolean>()
                        .setDefinition(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name())
                        .setValue((SweBoolean) new SweBoolean().setValue(true)
                                .setDefinition(Sos2Constants.Extensions.MergeObservationsIntoDataArray.name())));
            }
        }
    }

    private void splitDataArrayIntoObservations(final InsertObservationRequest request)
            throws OwsExceptionReport {
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
                    LOGGER.trace("Processing block {}/{}", ++counter, sweDataArrayValue.getValue().getValues().size());
                    final OmObservation newObservation = new OmObservation();
                    newObservation.setObservationConstellation(observationConstellation);
                    // identifier
                    if (metaIdentifier != null) {
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

    private ObservationValue<?> createObservationResultValue(String observationType, String valueString,
            Time phenomenonTime, SweField resultDefinitionField)
            throws OwsExceptionReport {
        ObservationValue<?> value = null;

        if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_TRUTH_OBSERVATION)) {
            value = new SingleObservationValue<>(new BooleanValue(Boolean.parseBoolean(valueString)));
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_COUNT_OBSERVATION)) {
            value = new SingleObservationValue<>(new CountValue(Integer.parseInt(valueString)));
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_MEASUREMENT)) {
            final QuantityValue quantity = new QuantityValue(Double.parseDouble(valueString));
            quantity.setUnit(getUom(resultDefinitionField));
            value = new SingleObservationValue<>(quantity);
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION)) {
            final CategoryValue cat = new CategoryValue(valueString);
            cat.setUnit(getUom(resultDefinitionField));
            value = new SingleObservationValue<>(cat);
        } else if (observationType.equalsIgnoreCase(OmConstants.OBS_TYPE_TEXT_OBSERVATION)) {
            value = new SingleObservationValue<>(new TextValue(valueString));
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

    private String getUom(SweField resultDefinitionField) {
        return ((SweAbstractUomType<?>) resultDefinitionField.getElement()).getUom();
    }

    private int getResultValueIndex(SweDataRecord elementTypeDataRecord, AbstractPhenomenon observableProperty) {
        return elementTypeDataRecord.getFieldIndexByIdentifier(observableProperty.getIdentifier());
    }

    private int getPhenomenonTimeIndex(SweDataRecord elementTypeDataRecord) {
        return elementTypeDataRecord.getFieldIndexByIdentifier(OmConstants.PHENOMENON_TIME);
    }

    private int getResultTimeIndex(SweDataRecord elementTypeDataRecord) {
        return elementTypeDataRecord.getFieldIndexByIdentifier(OmConstants.PHEN_SAMPLING_TIME);
    }

    private String getObservationTypeFromElementType(SweDataRecord elementTypeDataRecord,
            AbstractPhenomenon observableProperty)
            throws OwsExceptionReport {
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

    private boolean isSweArrayObservation(OmObservation observation) {
        return observation.getObservationConstellation().getObservationType()
                .equalsIgnoreCase(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION)
                && observation.getValue().getValue() instanceof SweDataArrayValue
                && ((SweDataArrayValue) observation.getValue().getValue()).isSetValue();
    }

    @Override
    public OwsServiceResponse modifyResponse(OwsServiceRequest request, OwsServiceResponse response)
            throws OwsExceptionReport {
        if (request instanceof AbstractObservationRequest && response instanceof AbstractObservationResponse) {
            return mergeObservations((AbstractObservationRequest) request, (AbstractObservationResponse) response);
        }
        return response;
    }

    private OwsServiceResponse mergeObservations(AbstractObservationRequest request,
            AbstractObservationResponse response)
            throws OwsExceptionReport {
        boolean checkForMergeObservationsInResponse = checkForMergeObservationsInResponse(request);
        boolean checkEncoderForMergeObservations = checkEncoderForMergeObservations(response);
        ObservationMergeIndicator indicator =
                ObservationMergeIndicator.sameObservationConstellation().setResultTime(includeResultTimeForMerging);
        if (checkForMergeObservationsInResponse || checkEncoderForMergeObservations) {
            ObservationStream observationStream = response.getObservationCollection().merge(indicator);
            List<OmObservation> processed = new LinkedList<>();
            while (observationStream.hasNext()) {
                OmObservation observation = observationStream.next();
                if (observation.getValue() instanceof AbstractStreaming) {
                    ObservationStream valueStream = ((AbstractStreaming) observation.getValue()).merge(indicator);
                    while (valueStream.hasNext()) {
                        processed.add(valueStream.next());
                    }
                } else {
                    processed.add(observation);
                }
            }
            response.setObservationCollection(ObservationStream.of(processed));
        }
        return response;
    }

    private boolean checkEncoderForMergeObservations(AbstractObservationResponse response)
            throws OwsExceptionReport {
        if (response.isSetResponseFormat()) {
            ObservationEncoder<?, ?> encoder = (ObservationEncoder<?, ?>) encoderRepository
                    .getEncoder(new XmlEncoderKey(response.getResponseFormat(), OmObservation.class));
            if (response.isSetContentType()) {
                encoder = (ObservationEncoder<?, ?>) encoderRepository
                        .getEncoder(new OperationResponseEncoderKey(response.getKey(), response.getContentType()));
            }
            // check for responseFormat as MediaType
            if (encoder == null && response.isSetResponseFormat()) {
                try {
                    encoder = (ObservationEncoder<?, ?>) encoderRepository
                            .getEncoder(new OperationResponseEncoderKey(response.getService(), response.getVersion(),
                                    response.getOperationName(), MediaType.parse(response.getResponseFormat())));
                } catch (IllegalArgumentException iae) {
                    LOGGER.debug("ResponseFormat isNot a XML response format");
                }
            }

            if (encoder != null && encoder.shouldObservationsWithSameXBeMerged()
                    && !encoder.supportsResultStreamingForMergedValues()) {
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

    private boolean checkForMergeObservationsInResponse(AbstractObservationRequest sosRequest) {
        return this.profileHandler.getActiveProfile().isMergeValues()
                || sosRequest.getBooleanExtension(Sos2Constants.Extensions.MergeObservationsIntoDataArray);
    }

}
