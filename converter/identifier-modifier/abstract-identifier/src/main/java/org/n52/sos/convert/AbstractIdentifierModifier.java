/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.convert;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.convert.RequestResponseModifier;
import org.n52.iceland.convert.RequestResponseModifierFacilitator;
import org.n52.janmayen.function.Functions;
import org.n52.janmayen.function.Predicates;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsCapabilities;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsOperation;
import org.n52.shetland.ogc.ows.OwsOperationsMetadata;
import org.n52.shetland.ogc.ows.OwsPossibleValues;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.GetCapabilitiesResponse;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.ProcessMethod;
import org.n52.shetland.ogc.sensorML.ProcessModel;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.elements.SmlCapabilities;
import org.n52.shetland.ogc.sensorML.elements.SmlIdentifier;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.SosCapabilities;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosObservationOffering;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.shetland.ogc.sos.request.GetFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.AbstractObservationResponse;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;
import org.n52.shetland.ogc.sos.response.GetFeatureOfInterestResponse;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swe.simpleType.SweTime;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public abstract class AbstractIdentifierModifier implements RequestResponseModifier {

    private ContentCacheController contentCacheController;
    private ProfileHandler profileHandler;

    protected ContentCacheController getCacheController() {
        return this.contentCacheController;
    }

    @Inject
    public void setCacheController(ContentCacheController contentCacheController) {
        this.contentCacheController = contentCacheController;
    }

    protected SosContentCache getCache() {
        return (SosContentCache) getCacheController().getCache();
    }

    protected ProfileHandler getProfileHandler() {
        return profileHandler;
    }

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    protected Profile getActiveProfile() {
        return getProfileHandler().getActiveProfile();
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return new RequestResponseModifierFacilitator().setAdderRemover(true);
    }

    protected abstract boolean checkForFlag(OwsServiceRequest request, OwsServiceResponse response) throws
            InvalidParameterValueException;

    protected abstract String checkOfferingParameterValue(String parameterValue);

    protected abstract String checkFeatureOfInterestParameterValue(String parameterValue);

    protected abstract String checkProcedureParameterValue(String parameterValue);

    protected abstract String checkObservablePropertyParameterValue(String parameterValue);

    protected abstract String checkFeatureOfInterestIdentifier(String identifier);

    private Collection<String> checkFeatureOfInterestIdentifier(Collection<String> identifiers) {
        return identifiers.stream().map(this::checkFeatureOfInterestIdentifier).collect(Collectors.toList());
    }

    protected abstract String checkObservablePropertyIdentifier(String identifier);

    private Collection<String> checkObservablePropertyIdentifier(SortedSet<String> identifiers) {
        return identifiers.stream().map(this::checkObservablePropertyIdentifier).collect(Collectors.toList());
    }

    protected abstract String checkProcedureIdentifier(String identifier);

    private Collection<String> checkProcedureIdentifier(Set<String> identifiers) {
        return identifiers.stream().map(this::checkProcedureIdentifier).collect(Collectors.toList());
    }

    protected abstract ReferenceType checkProcedureIdentifier(ReferenceType procedure);

    protected abstract String checkOfferingIdentifier(String identifier);

    protected abstract void checkAndChangeFeatureOfInterestIdentifier(AbstractFeature abstractFeature);

    protected abstract void checkAndChangeProcedureIdentifier(AbstractFeature abstractFeature);

    protected abstract void checkAndChangeObservablePropertyIdentifier(AbstractFeature abstractFeature);

    protected abstract void checkAndChangOfferingIdentifier(SosOffering offering);

    @Override
    public OwsServiceRequest modifyRequest(OwsServiceRequest request) throws OwsExceptionReport {
        if (request instanceof GetObservationRequest) {
            return changeGetObservationRequestParameterValues((GetObservationRequest) request);
        } else if (request instanceof GetFeatureOfInterestRequest) {
            return changeGetFeatureOfInterestRequestParameterValues((GetFeatureOfInterestRequest) request);
        } else if (request instanceof DescribeSensorRequest) {
            return changeDescribeSensorRequestParameterValues((DescribeSensorRequest) request);
        } else if (request instanceof GetDataAvailabilityRequest) {
            return changeGetDataAvailabilityRequestParameterValues((GetDataAvailabilityRequest) request);
        } else if (request instanceof GetResultTemplateRequest) {
            return changeGetResultTemplateRequestParameterValues((GetResultTemplateRequest) request);
        } else if (request instanceof GetResultRequest) {
            return changeGetResultRequestParameterValues((GetResultRequest) request);
        }
        return request;
    }

    protected OwsServiceRequest changeGetObservationRequestParameterValues(GetObservationRequest request) {
        if (request.isSetOffering()) {
            request.setOfferings(checkOfferingParameterValues(request.getOfferings()));
        }
        if (request.isSetFeatureOfInterest()) {
            request.setFeatureIdentifiers(checkFeatureOfInterestParameterValues(request.getFeatureIdentifiers()));
        }
        if (request.isSetObservableProperty()) {
            request.setObservedProperties(checkObservablePropertyParameterValues(request.getObservedProperties()));
        }
        if (request.isSetProcedure()) {
            request.setProcedures(checkProcedureParameterValues(request.getProcedures()));
        }
        return request;
    }

    protected OwsServiceRequest changeGetFeatureOfInterestRequestParameterValues(
            GetFeatureOfInterestRequest request) {
        if (request.isSetFeatureOfInterestIdentifiers()) {
            request.setFeatureIdentifiers(checkFeatureOfInterestParameterValues(request.getFeatureIdentifiers()));
        }
        if (request.isSetObservableProperties()) {
            request.setObservedProperties(checkObservablePropertyParameterValues(request.getObservedProperties()));
        }
        if (request.isSetProcedures()) {
            request.setProcedures(checkProcedureParameterValues(request.getProcedures()));
        }
        return request;
    }

    protected OwsServiceRequest changeDescribeSensorRequestParameterValues(DescribeSensorRequest request) {
        request.setProcedure(checkProcedureParameterValue(request.getProcedure()));
        return request;
    }

    protected OwsServiceRequest changeGetDataAvailabilityRequestParameterValues(
            GetDataAvailabilityRequest request) {
        if (request.isSetOfferings()) {
            request.setOfferings(checkOfferingParameterValues(request.getOfferings()));
        }
        if (request.isSetFeaturesOfInterest()) {
            request.setFeatureOfInterest(checkFeatureOfInterestParameterValues(request.getFeaturesOfInterest()));
        }
        if (request.isSetObservedProperties()) {
            request.setObservedProperty(checkObservablePropertyParameterValues(request.getObservedProperties()));
        }
        if (request.isSetProcedures()) {
            request.setProcedure(checkProcedureParameterValues(request.getProcedures()));
        }
        return request;
    }

    protected OwsServiceRequest changeGetResultTemplateRequestParameterValues(GetResultTemplateRequest request) {
        if (request.isSetOffering()) {
            request.setOffering(checkOfferingParameterValue(request.getOffering()));
        }
        if (request.isSetObservedProperty()) {
            request.setObservedProperty(checkObservablePropertyParameterValue(request.getObservedProperty()));
        }
        return request;
    }

    protected OwsServiceRequest changeGetResultRequestParameterValues(GetResultRequest request) {
        if (request.isSetOffering()) {
            request.setOffering(checkOfferingParameterValue(request.getOffering()));
        }
        if (request.isSetObservedProperty()) {
            request.setObservedProperty(checkObservablePropertyParameterValue(request.getObservedProperty()));
        }
        if (request.isSetFeatureOfInterest()) {
            request.setFeatureIdentifiers(checkFeatureOfInterestParameterValues(request.getFeatureIdentifiers()));
        }
        return request;
    }

    @Override
    public OwsServiceResponse modifyResponse(OwsServiceRequest request, OwsServiceResponse response)
            throws OwsExceptionReport {
        if (checkForFlag(request, response)) {
            if (response instanceof GetCapabilitiesResponse) {
                return changeGetCapabilitiesResponseIdentifier((GetCapabilitiesResponse) response);
            } else if (response instanceof AbstractObservationResponse) {
                return changeAbstractObservationResponseIdentifier((AbstractObservationResponse) response);
            } else if (response instanceof GetFeatureOfInterestResponse) {
                return changeGetFeatureOfInterestResponseIdentifier((GetFeatureOfInterestResponse) response);
            } else if (response instanceof DescribeSensorResponse) {
                return changeDescribeSensorResponseIdentifier((DescribeSensorResponse) response);
            } else if (response instanceof GetDataAvailabilityResponse) {
                return changeGetDataAvailabilityResponseIdentifier((GetDataAvailabilityResponse) response);
            } else if (response instanceof GetResultTemplateResponse) {
                return changeGetResultTemplateResponseIdentifier((GetResultTemplateResponse) response);
            }
        }
        return response;
    }

    protected GetCapabilitiesResponse changeGetCapabilitiesResponseIdentifier(GetCapabilitiesResponse response) {
        Optional<OwsCapabilities> caps = Optional.ofNullable(response.getCapabilities());

        caps.flatMap(OwsCapabilities::getOperationsMetadata)
                .map(OwsOperationsMetadata::getOperations)
                .map(SortedSet::stream)
                .orElseGet(Stream::empty)
                .map(OwsOperation::getParameters)
                .flatMap(SortedSet::stream)
                .forEach(this::checkParameter);

        caps.filter(Predicates.instanceOf(SosCapabilities.class))
                .map(Functions.cast(SosCapabilities.class))
                .flatMap(SosCapabilities::getContents)
                .map(SortedSet::stream)
                .orElseGet(Stream::empty)
                .filter(Predicates.not(SosObservationOffering::isEmpty))
                .forEach(this::checkObservationOffering);

        return response;
    }

    protected void checkObservationOffering(SosObservationOffering offering) {
        checkAndChangOfferingIdentifier(offering.getOffering());
        offering.setFeatureOfInterest(checkFeatureOfInterestIdentifier(offering.getFeatureOfInterest()));
        offering.setProcedures(checkProcedureIdentifier(offering.getProcedures()));
        offering.setObservableProperties(checkObservablePropertyIdentifier(offering.getObservableProperties()));
    }

    private void checkParameter(OwsDomain parameter) {
        if (Stream.of(SosConstants.GetObservationParams.offering,
                      SosConstants.GetObservationParams.featureOfInterest,
                      Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID,
                      SosConstants.GetObservationParams.observedProperty,
                      SosConstants.GetObservationParams.procedure)
                .anyMatch(e -> parameter.getName().equals(e.name()))) {
            checkOwsParameterValues(parameter.getPossibleValues(), parameter.getName());
        }
    }

    protected OwsPossibleValues checkOwsParameterValues(OwsPossibleValues parameter, String name) {
        if (parameter.isAllowedValues()) {
            OwsAllowedValues allowedValues = parameter.asAllowedValues();
            getIdentifierCheckerForName(name)
                    .ifPresent(c -> allowedValues.setRestrictions(new TreeSet<>(allowedValues.getRestrictions())
                            .stream().map(r -> !r.isValue() ? r : new OwsValue(c.apply(r.asValue().getValue())))));
        }
        return parameter;
    }

    protected OwsServiceResponse changeDescribeSensorResponseIdentifier(DescribeSensorResponse response) {
        // TODO check typeOf title
        response.getProcedureDescriptions().stream().forEach(this::checkAndChangeProcedure);
        return response;
    }

    protected OwsServiceResponse changeAbstractObservationResponseIdentifier(AbstractObservationResponse response) {
        response.setObservationCollection(response.getObservationCollection().modify(omObservation -> {
            OmObservationConstellation observationConstellation = omObservation.getObservationConstellation();
            checkAndChangeFeatureOfInterestIdentifier(observationConstellation.getFeatureOfInterest());
            checkAndChangeObservablePropertyIdentifier(observationConstellation.getObservableProperty());
            checkAndChangeProcedure(observationConstellation.getProcedure());
            if (getActiveProfile().isEncodeProcedureInObservation()) {
                checkAndChangeProcedure(observationConstellation.getProcedure());
            }
        }));
        return response;
    }

    protected OwsServiceResponse changeGetFeatureOfInterestResponseIdentifier(GetFeatureOfInterestResponse response) {
        if (response.getAbstractFeature() instanceof FeatureCollection) {
            FeatureCollection featureCollection = (FeatureCollection) response.getAbstractFeature();
            // TODO check if new map with new identifier should be created
            featureCollection.getMembers().values().forEach(this::checkAndChangeFeatureOfInterestIdentifier);
        } else {
            checkAndChangeFeatureOfInterestIdentifier(response.getAbstractFeature());
        }
        return response;
    }

    protected OwsServiceResponse changeGetResultTemplateResponseIdentifier(GetResultTemplateResponse response)
            throws OwsExceptionReport {
        SweAbstractDataComponent resultStructure = response.getResultStructure().get().orElse(null);
        SweDataRecord dataRecord = null;
        if (resultStructure instanceof SweDataArray) {
            SweDataArray dataArray = (SweDataArray) resultStructure;
            if (dataArray.getElementType() instanceof SweDataRecord) {
                dataRecord = (SweDataRecord) dataArray.getElementType();
            }
        } else if (resultStructure instanceof SweDataRecord) {
            dataRecord = (SweDataRecord) resultStructure;
        }
        if (dataRecord != null && dataRecord.isSetFields()) {
            for (SweField field : dataRecord.getFields()) {
                if (!(field.getElement() instanceof SweTime)) {
                    checkAbstractDataComponentForObservableProperty(field.getElement());
                    dataRecord.setXml(null);
                    resultStructure.setXml(null);
                    response.getResultStructure().setXml(null);
                }
            }
        }
        return response;
    }

    protected OwsServiceResponse changeGetDataAvailabilityResponseIdentifier(GetDataAvailabilityResponse response) {
        response.getDataAvailabilities().stream().forEach(this::checkDataAvailability);
        return response;
    }

    private void checkAndChangeProcedure(AbstractFeature abstractFeature) {
        if (abstractFeature instanceof SosProcedureDescription) {
            SosProcedureDescription<?> procedure = (SosProcedureDescription) abstractFeature;
            checkAndChangeProcedureIdentifier(procedure);
            if (procedure.isSetFeaturesOfInterest()) {
                procedure.setFeaturesOfInterest(checkFeatureOfInterestIdentifier(procedure.getFeaturesOfInterest()));
            }
            if (procedure.isSetFeaturesOfInterestMap()) {
                procedure.setFeaturesOfInterestMap(procedure.getFeaturesOfInterestMap().values().stream()
                        .map(Functions.mutate(this::checkAndChangeFeatureOfInterestIdentifier))
                        .collect(Collectors.toMap(AbstractFeature::getIdentifier, Function.identity())));
            }
            if (procedure.isSetOfferings()) {
                procedure.getOfferings().forEach(off -> checkAndChangOfferingIdentifier(off));
            }
            if (procedure.isSetPhenomenon()) {
                procedure.setPhenomenon(procedure.getPhenomenon().values().stream()
                        .map(Functions.mutate(this::checkAndChangeObservablePropertyIdentifier))
                        .collect(Collectors.toMap(AbstractPhenomenon::getIdentifier, Function.identity())));
            }
            if (procedure.isSetParentProcedure()) {
                procedure.setParentProcedure(checkProcedureIdentifier(procedure.getParentProcedure()));
            }
            if (procedure.getProcedureDescription() instanceof AbstractSensorML) {
                AbstractSensorML abstractSensorML = (AbstractSensorML) procedure.getProcedureDescription();
                if (abstractSensorML.isSetKeywords()) {
                    abstractSensorML.setKeywords(checkKeywords(abstractSensorML.getKeywords()));
                }
                if (abstractSensorML instanceof SensorML) {
                    checkSensorML((SensorML) abstractSensorML);

                } else if (abstractSensorML instanceof AbstractProcess) {
                    checkAbstractProcess((AbstractProcess) abstractSensorML);
                }
            }
            if (procedure.isSetChildProcedures()) {
                procedure.getChildProcedures().forEach(this::checkAndChangeProcedure);
            }
        }
    }

    private List<String> checkKeywords(List<String> keywords) {
        return keywords.stream()
                .map(this::checkOfferingIdentifier)
                .map(this::checkObservablePropertyIdentifier)
                .map(this::checkFeatureOfInterestIdentifier)
                .map(this::checkProcedureIdentifier)
                .collect(Collectors.toList());
    }

    private void checkSensorML(SensorML procedure) {
        checkIdentificationCapabilities(procedure);
        if (procedure.isSetMembers()) {
            procedure.getMembers().stream().forEach(this::checkAndChangeProcedure);
        }
    }

    private void checkAbstractProcess(AbstractProcess procedure) {
        checkIdentificationCapabilities(procedure);
        if (procedure.isSetOutputs()) {
            procedure.getOutputs().stream()
                    .map(SmlIo::getIoValue)
                    .forEach(this::checkAbstractDataComponentForObservableProperty);
        }
        checkProcessMethod(procedure);
    }

    private void checkProcessMethod(AbstractProcess procedure) {
        if (procedure instanceof ProcessModel && ((ProcessModel) procedure).isSetMethod()) {
            ProcessMethod method = ((ProcessModel) procedure).getMethod();
            if (method.isSetRulesDefinition() && method.getRulesDefinition().isSetDescription()) {
                String[] split = method.getRulesDefinition().getDescription().split("'");
                if (split.length == 5) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(split[0]).append('\'');
                    builder.append(checkProcedureIdentifier(split[1])).append('\'');
                    builder.append(split[2]).append('\'');
                    Collection<String> obsProps = checkObservablePropertyIdentifier(new TreeSet<>(Arrays
                            .asList(split[3].split(","))));
                    builder.append(Joiner.on(",").join(obsProps));
                    builder.append('\'');
                    builder.append(split[4]);
                    method.getRulesDefinition().setDescription(builder.toString());
                }
            }
        }
    }

    private void checkAbstractDataComponentForObservableProperty(SweAbstractDataComponent value) {
        if (value.isSetDefinition()) {
            value.setDefinition(checkObservablePropertyIdentifier(value.getDefinition()));
        }
        if (value.isSetIdentifier()) {
            value.setIdentifier(checkObservablePropertyIdentifier(value.getIdentifier()));
        }
    }

    private void checkIdentificationCapabilities(AbstractSensorML procedure) {
        if (procedure.isSetIdentifications()) {
            procedure.getIdentifications().stream()
                    .filter(this::isIdentificationProcedureIdentifier)
                    .forEach(id -> id.setValue(checkProcedureIdentifier(id.getValue())));
        }
        if (procedure.isSetCapabilities()) {
            for (SmlCapabilities capabilities : procedure.getCapabilities()) {
                if (null != capabilities.getName()) {
                    switch (capabilities.getName()) {
                        case SensorMLConstants.ELEMENT_NAME_OFFERINGS:
                            capabilities.getDataRecord().getFields().stream()
                                    .map(SweField::getElement)
                                    .filter(Predicates.instanceOf(SweText.class))
                                    .map(Functions.cast(SweText.class))
                                    .forEach(elem -> elem.setValue(checkOfferingIdentifier(elem.getValue())));
                            break;
                        case SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES:
                            capabilities.getDataRecord().getFields().stream()
                                    .map(SweField::getElement)
                                    .filter(Predicates.instanceOf(SweText.class))
                                    .map(Functions.cast(SweText.class))
                                    .forEach(elem -> elem.setValue(checkProcedureIdentifier(elem.getValue())));
                            break;
                        case SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST:
                            capabilities.getDataRecord().getFields().stream()
                                    .map(SweField::getElement)
                                    .filter(Predicates.instanceOf(SweText.class))
                                    .map(Functions.cast(SweText.class))
                                    .forEach(elem -> elem.setValue(checkFeatureOfInterestIdentifier(elem.getValue())));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private boolean isIdentificationProcedureIdentifier(SmlIdentifier identifier) {
        return checkIdentificationNameForProcedureIdentifier(identifier.getName()) ||
               checkIdentificationDefinitionForProcedureIdentifier(identifier.getDefinition());
    }

    private boolean checkIdentificationNameForProcedureIdentifier(String name) {
        return !Strings.isNullOrEmpty(name) && name.equals(OGCConstants.URN_UNIQUE_IDENTIFIER_END);
    }

    private boolean checkIdentificationDefinitionForProcedureIdentifier(String definition) {
        return !Strings.isNullOrEmpty(definition) &&
               (OGCConstants.URN_UNIQUE_IDENTIFIER.equals(definition) ||
                OGCConstants.URN_IDENTIFIER_IDENTIFICATION.equals(definition) ||
                checkDefinitionStartsWithAndContains(definition));
    }

    private boolean checkDefinitionStartsWithAndContains(final String definition) {
        return definition.startsWith(OGCConstants.URN_UNIQUE_IDENTIFIER_START) &&
               definition.contains(OGCConstants.URN_UNIQUE_IDENTIFIER_END);
    }

    private List<String> checkOfferingParameterValues(Collection<String> values) {
        return values.stream().map(this::checkOfferingParameterValue).collect(Collectors.toList());
    }

    private List<String> checkFeatureOfInterestParameterValues(Collection<String> values) {
        return values.stream().map(this::checkFeatureOfInterestParameterValue).collect(Collectors.toList());
    }

    private List<String> checkObservablePropertyParameterValues(Collection<String> values) {
        return values.stream().map(this::checkObservablePropertyParameterValue).collect(Collectors.toList());
    }

    private List<String> checkProcedureParameterValues(Collection<String> values) {
        return values.stream().map(this::checkProcedureParameterValue).collect(Collectors.toList());
    }

    private Optional<Function<String, String>> getIdentifierCheckerForName(String name) {
        if (SosConstants.GetObservationParams.offering.name().equals(name)) {
            return Optional.of(this::checkOfferingIdentifier);
        } else if (SosConstants.GetObservationParams.featureOfInterest.name().equals(name)) {
            return Optional.of(this::checkFeatureOfInterestIdentifier);
        } else if (SosConstants.GetObservationParams.observedProperty.name().equals(name)) {
            return Optional.of(this::checkObservablePropertyIdentifier);
        } else if (SosConstants.GetObservationParams.procedure.name().equals(name)) {
            return Optional.of(this::checkProcedureIdentifier);
        }
        return Optional.empty();
    }

    private void checkDataAvailability(DataAvailability da) {
        da.getFeatureOfInterest().setHref(checkFeatureOfInterestIdentifier(da.getFeatureOfInterest().getHref()));
        da.getProcedure().setHref(checkProcedureIdentifier(da.getProcedure().getHref()));
        da.getObservedProperty().setHref(checkObservablePropertyIdentifier(da.getObservedProperty().getHref()));
    }

}
