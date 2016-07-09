/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
import java.util.SortedMap;
import java.util.SortedSet;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.convert.RequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.converter.util.FlexibleIdentifierHelper;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.gda.GetDataAvailabilityRequest;
import org.n52.sos.gda.GetDataAvailabilityResponse;
import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.ows.OwsParameterValue;
import org.n52.sos.ogc.ows.OwsParameterValuePossibleValues;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.elements.SmlCapabilities;
import org.n52.sos.ogc.sensorML.elements.SmlIdentifier;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosCapabilities;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosObservationOffering;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.request.GetResultRequest;
import org.n52.sos.request.GetResultTemplateRequest;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.response.GetCapabilitiesResponse;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.response.GetObservationByIdResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.response.GetResultResponse;
import org.n52.sos.response.GetResultTemplateResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class FlexibleIdentifierModifier
		implements
		RequestResponseModifier<AbstractServiceRequest<?>, AbstractServiceResponse> {

	private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = getKeyTypes();

	/**
	 * Get the keys
	 * 
	 * @return Set of keys
	 */
	private static Set<RequestResponseModifierKeyType> getKeyTypes() {
		Set<String> services = Sets.newHashSet(SosConstants.SOS);
		Set<String> versions = Sets.newHashSet(Sos1Constants.SERVICEVERSION,
				Sos2Constants.SERVICEVERSION);
		Map<AbstractServiceRequest<?>, AbstractServiceResponse> requestResponseMap = Maps
				.newHashMap();
		requestResponseMap.put(new GetCapabilitiesRequest(),
				new GetCapabilitiesResponse());
		requestResponseMap.put(new GetObservationRequest(),
				new GetObservationResponse());
		requestResponseMap.put(new GetObservationByIdRequest(),
				new GetObservationByIdResponse());
		requestResponseMap.put(new GetFeatureOfInterestRequest(),
				new GetFeatureOfInterestResponse());
		requestResponseMap.put(new DescribeSensorRequest(),
				new DescribeSensorResponse());
		requestResponseMap.put(new GetDataAvailabilityRequest(),
				new GetDataAvailabilityResponse());
		requestResponseMap.put(new GetResultTemplateRequest(),
				new GetResultTemplateResponse());
		requestResponseMap.put(new GetResultRequest(), new GetResultResponse());
		Set<RequestResponseModifierKeyType> keys = Sets.newHashSet();
		for (String service : services) {
			for (String version : versions) {
				for (AbstractServiceRequest<?> request : requestResponseMap
						.keySet()) {
					keys.add(new RequestResponseModifierKeyType(service,
							version, request));
					keys.add(new RequestResponseModifierKeyType(service,
							version, request, requestResponseMap.get(request)));
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
	public AbstractServiceRequest<?> modifyRequest(
			AbstractServiceRequest<?> request) throws OwsExceptionReport {
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

	private AbstractServiceRequest<?> changeGetObservationRequestParameterValues(
			GetObservationRequest request) {
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

	private AbstractServiceRequest<?> changeGetFeatureOfInterestRequestParameterValues(
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

	private AbstractServiceRequest<?> changeDescribeSensorRequestParameterValues(
			DescribeSensorRequest request) {
		request.setProcedure(checkProcedureParameterValue(request.getProcedure()));
		return request;
	}

	private AbstractServiceRequest<?> changeGetDataAvailabilityRequestParameterValues(
			GetDataAvailabilityRequest request) {
		if (request.isSetOfferings()) {
			request.setOffering(checkOfferingParameterValues(request.getOfferings()));
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

	private AbstractServiceRequest<?> changeGetResultTemplateRequestParameterValues(
			GetResultTemplateRequest request) {
		if (request.isSetOffering()) {
			request.setOffering(checkOfferingParameterValue(request.getOffering()));
		}
		if (request.isSetObservedProperty()) {
			request.setObservedProperty(checkObservablePropertyParameterValue(request.getObservedProperty()));
		}
		return request;
	}

	private AbstractServiceRequest<?> changeGetResultRequestParameterValues(
			GetResultRequest request) {
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
	public AbstractServiceResponse modifyResponse(
			AbstractServiceRequest<?> request, AbstractServiceResponse response)
			throws OwsExceptionReport {
		if (checkForReturnHumanReadableIdentifier(request, response)) {
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

	private GetCapabilitiesResponse changeGetCapabilitiesResponseIdentifier(
			GetCapabilitiesResponse response) {
		SosCapabilities capabilities = response.getCapabilities();
		if (capabilities.isSetOperationsMetadata() && capabilities.getOperationsMetadata().isSetOperations()) {
			for (OwsOperation operation : capabilities.getOperationsMetadata().getOperations()) {
				SortedMap<String, List<OwsParameterValue>> parameterValues = operation
						.getParameterValues();
				if (parameterValues
						.containsKey(SosConstants.GetObservationParams.offering
								.name())) {
					checkOwsParameterValues(
							parameterValues.get(SosConstants.GetObservationParams.offering
									.name()),
							SosConstants.GetObservationParams.offering.name());
				}
				if (parameterValues
						.containsKey(SosConstants.GetObservationParams.featureOfInterest
								.name())) {
					checkOwsParameterValues(
							parameterValues.get(SosConstants.GetObservationParams.featureOfInterest
									.name()),
							SosConstants.GetObservationParams.featureOfInterest
									.name());
				}
				if (parameterValues
						.containsKey(Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID
								.name())) {
					checkOwsParameterValues(
							parameterValues.get(Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID
									.name()),
							SosConstants.GetObservationParams.featureOfInterest
									.name());
				}
				if (parameterValues
						.containsKey(SosConstants.GetObservationParams.observedProperty
								.name())) {
					checkOwsParameterValues(
							parameterValues.get(SosConstants.GetObservationParams.observedProperty
									.name()),
							SosConstants.GetObservationParams.observedProperty
									.name());
				}
				if (parameterValues
						.containsKey(SosConstants.GetObservationParams.procedure
								.name())) {
					checkOwsParameterValues(
							parameterValues.get(SosConstants.GetObservationParams.procedure
									.name()),
							SosConstants.GetObservationParams.procedure.name());
				}
			}
		}
		if (capabilities.isSetContents()) {
			for (SosObservationOffering observationOffering : capabilities.getContents()) {
				checkAndChangOfferingIdentifier(observationOffering.getOffering());
				observationOffering
						.setFeatureOfInterest(checkFeatureOfInterestIdentifier(observationOffering
								.getFeatureOfInterest()));
				observationOffering
						.setProcedures(checkProcedureIdentifier(observationOffering
								.getProcedures()));
				observationOffering
						.setObservableProperties(checkObservablePropertyIdentifier(observationOffering
								.getObservableProperties()));
			}
		}
		return response;
	}

	private void checkOwsParameterValues(
			List<OwsParameterValue> list, String name) {
//		List<OwsParameterValue> checkedList = Lists.newArrayListWithCapacity(list.size());
		for (OwsParameterValue owsParameterValue : list) {
			if (owsParameterValue instanceof OwsParameterValuePossibleValues && CollectionHelper.isNotEmpty(((OwsParameterValuePossibleValues) owsParameterValue).getValues())) {
				OwsParameterValuePossibleValues pvpv = (OwsParameterValuePossibleValues) owsParameterValue;
				SortedSet<String> checkedValues = Sets.<String>newTreeSet();
				for (String identifier : pvpv.getValues()) {
					if (SosConstants.GetObservationParams.offering.name().equals(name)) {
						checkedValues.add(checkOfferingIdentifier(identifier));
					} else if (SosConstants.GetObservationParams.featureOfInterest.name().equals(name)) {
						checkedValues.add(checkFeatureOfInterestIdentifier(identifier));
					} else if (SosConstants.GetObservationParams.observedProperty.name().equals(name)) {
						checkedValues.add(checkObservablePropertyIdentifier(identifier));
					} else if (SosConstants.GetObservationParams.procedure.name().equals(name)) {
						checkedValues.add(checkProcedureIdentifier(identifier));
					} else {
						checkedValues.add(identifier);
					}
				}
				pvpv.setValues(checkedValues);
			}
		}
	}

	private AbstractServiceResponse changeDescribeSensorResponseIdentifier(
			DescribeSensorResponse response) {
		for (SosProcedureDescription procedure : response
				.getProcedureDescriptions()) {
			checkAndChangeProcedure(procedure);
		}
		return response;
	}

	private AbstractServiceResponse changeAbstractObservationResponseIdentifier(
			AbstractObservationResponse response) {
		for (OmObservation omObservation : response.getObservationCollection()) {
			OmObservationConstellation observationConstellation = omObservation
					.getObservationConstellation();
			checkAndChangeFeatureOfInterestIdentifier(observationConstellation.getFeatureOfInterest());
			checkAndChangeObservablePropertyIdentifier(observationConstellation.getObservableProperty());
			checkAndChangeProcedure(observationConstellation.getProcedure());
			if (getActiveProfile().isEncodeProcedureInObservation()) {
				checkAndChangeProcedure(observationConstellation.getProcedure());
			}
		}
		return response;
	}

	private AbstractServiceResponse changeGetFeatureOfInterestResponseIdentifier(
			GetFeatureOfInterestResponse response) {
		if (response.getAbstractFeature() instanceof FeatureCollection) {
			FeatureCollection featureCollection = (FeatureCollection) response
					.getAbstractFeature();
			// TODO check if new map with new identifier should be created
			for (AbstractFeature abstractFeature : featureCollection
					.getMembers().values()) {
				checkAndChangeFeatureOfInterestIdentifier(abstractFeature);
			}
		} else {
			checkAndChangeFeatureOfInterestIdentifier(response.getAbstractFeature());
		}
		return response;
	}

	private AbstractServiceResponse changeGetResultTemplateResponseIdentifier(
			GetResultTemplateResponse response) throws OwsExceptionReport {
		SweAbstractDataComponent resultStructure = response.getResultStructure().getResultStructure();
		SweDataRecord dataRecord = null;
		if (resultStructure instanceof SweDataArray) {
			SweDataArray dataArray = (SweDataArray) resultStructure;
			if (dataArray.getElementType() instanceof SweDataRecord) {
				dataRecord = (SweDataRecord)dataArray.getElementType();
			}
		} else if (resultStructure instanceof SweDataRecord) {
			dataRecord = (SweDataRecord)resultStructure;
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

	private AbstractServiceResponse changeGetDataAvailabilityResponseIdentifier(
			GetDataAvailabilityResponse response) {
		for (DataAvailability dataAvailability : response.getDataAvailabilities()) {
			dataAvailability.getFeatureOfInterest().setHref(
					checkFeatureOfInterestIdentifier(dataAvailability
							.getFeatureOfInterest().getHref()));
			dataAvailability.getProcedure().setHref(
					checkProcedureIdentifier(dataAvailability.getProcedure()
							.getHref()));
			dataAvailability.getObservedProperty().setHref(
					checkObservablePropertyIdentifier(dataAvailability.getObservedProperty().getHref()));
		}
		return response;
	}

	private boolean checkForReturnHumanReadableIdentifier(
			AbstractServiceRequest<?> request, AbstractServiceResponse response)
			throws InvalidParameterValueException {
		if (getFlexibleIdentifierHelper()
				.checkIsReturnHumanReadableIdentifierFlagExtensionSet(
						request.getExtensions()) || getFlexibleIdentifierHelper()
						.checkIsReturnHumanReadableIdentifierFlagExtensionSet(
								response.getExtensions())) {
			return checkResponseForReturnHumanReadableIdentifierFlag(response) || checkRequestForReturnHumanReadableIdentifierFlag(request);
		}
		return FlexibleIdentifierHelper.getInstance()
				.isSetReturnHumanReadableIdentifier();
	}

	private void checkAndChangeProcedure(SosProcedureDescription procedure) {
		checkAndChangeProcedureIdentifier(procedure);
		if (procedure.isSetFeaturesOfInterest()) {
			procedure.setFeaturesOfInterest(checkFeatureOfInterestIdentifier(procedure.getFeaturesOfInterest()));
		}
		if (procedure.isSetFeaturesOfInterestMap()) {
			Map<String, AbstractFeature> checkedFeatures = Maps.newHashMap();
			for (AbstractFeature feature : procedure.getFeaturesOfInterestMap().values()) {
				checkAndChangeFeatureOfInterestIdentifier(feature);
				checkedFeatures.put(feature.getIdentifier(), feature);
			}
			procedure.setFeaturesOfInterest(checkedFeatures);
		}
		if (procedure.isSetOfferings()) {
			for (SosOffering offering : procedure.getOfferings()) {
				checkAndChangOfferingIdentifier(offering);
			}
		}
		if (procedure.isSetPhenomenon()) {
			Map<String, AbstractPhenomenon> checkedPhens = Maps.newHashMap();
			for (AbstractPhenomenon phen : procedure.getPhenomenon().values()) {
				checkAndChangeObservablePropertyIdentifier(phen);
				checkedPhens.put(phen.getIdentifier(), phen);
			}
			procedure.setPhenomenon(checkedPhens);
		}
		if (procedure.isSetParentProcedures()) {
				procedure.setParentProcedures(checkProcedureIdentifier(procedure.getParentProcedures()));
		}
		if (procedure instanceof AbstractSensorML) {
			if (((AbstractSensorML) procedure).isSetKeywords()) {
				((AbstractSensorML) procedure).setKeywords(checkKeywords(((AbstractSensorML) procedure).getKeywords()));
				
			}
			if (procedure instanceof SensorML) {
				checkSensorML((SensorML)procedure);
				
			} else if (procedure instanceof AbstractProcess) {
				checkAbstractProcess((AbstractProcess)procedure);
			}
		}
		if (procedure.isSetChildProcedures()) {
			for (SosProcedureDescription childProcedure : procedure.getChildProcedures()) {
				checkAndChangeProcedure(childProcedure);
			}
		}
	}

	private List<String> checkKeywords(List<String> keywords) {
		List<String> checkedKeyword = Lists.newArrayListWithCapacity(keywords.size());
		for (String keyword : keywords) {
			String checked = checkOfferingIdentifier(keyword);
			checked = checkObservablePropertyIdentifier(checked);
			checked = checkFeatureOfInterestIdentifier(checked);
			checked = checkProcedureIdentifier(checked);
			checkedKeyword.add(checked);
		}
		return checkedKeyword;
	}

	private void checkSensorML(SensorML procedure) {
		checkIdentificationCapabilities((AbstractSensorML)procedure);
		if (procedure.isSetMembers()) {
			for (AbstractProcess member : procedure.getMembers()) {
				checkAndChangeProcedure(member);
			}
		}
	}

	private void checkAbstractProcess(AbstractProcess procedure) {
		checkIdentificationCapabilities((AbstractSensorML)procedure);
		if (procedure.isSetOutputs()) {
			for (SmlIo<?> output : procedure.getOutputs()) {
				checkAbstractDataComponentForObservableProperty(output.getIoValue());
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
			for (SmlIdentifier identifier : procedure.getIdentifications()) {
				if (isIdentificationProcedureIdentifier(identifier)) {
					identifier.setValue(checkProcedureIdentifier(identifier.getValue()));
				}
			}
		}
		if (procedure.isSetCapabilities()) {
			for (SmlCapabilities capabilities : procedure.getCapabilities()) {
				if (SensorMLConstants.ELEMENT_NAME_OFFERINGS.equals(capabilities.getName())) {
					for (SweField field : capabilities.getDataRecord().getFields()) {
						if (field.getElement() instanceof SweText) {
							((SweText)field.getElement()).setValue(checkOfferingIdentifier(((SweText)field.getElement()).getValue()));
						}
					}
                } else if (SensorMLConstants.ELEMENT_NAME_PARENT_PROCEDURES.equals(capabilities.getName())) {
                	for (SweField field : capabilities.getDataRecord().getFields()) {
						if (field.getElement() instanceof SweText) {
							((SweText)field.getElement()).setValue(checkProcedureIdentifier(((SweText)field.getElement()).getValue()));
						}
					}
                } else if (SensorMLConstants.ELEMENT_NAME_FEATURES_OF_INTEREST.equals(capabilities.getName())) {
                	for (SweField field : capabilities.getDataRecord().getFields()) {
						if (field.getElement() instanceof SweText) {
							((SweText)field.getElement()).setValue(checkFeatureOfInterestIdentifier(((SweText)field.getElement()).getValue()));
						}
					}
                }
			}
		}
	}

    private boolean isIdentificationProcedureIdentifier(final SmlIdentifier identifier) {
        return (checkIdentificationNameForProcedureIdentifier(identifier.getName()) || checkIdentificationDefinitionForProcedureIdentifier(identifier
                .getDefinition()));
    }

    private boolean checkIdentificationNameForProcedureIdentifier(final String name) {
        return !Strings.isNullOrEmpty(name) && name.equals(OGCConstants.URN_UNIQUE_IDENTIFIER_END);
    }

    private boolean checkIdentificationDefinitionForProcedureIdentifier(final String definition) {
        if (Strings.isNullOrEmpty(definition)) {
            return false;
        }
        final Set<String> definitionValues =
                Sets.newHashSet(OGCConstants.URN_UNIQUE_IDENTIFIER, OGCConstants.URN_IDENTIFIER_IDENTIFICATION);
        return definitionValues.contains(definition) || checkDefinitionStartsWithAndContains(definition);
    }

    private boolean checkDefinitionStartsWithAndContains(final String definition) {
        return definition.startsWith(OGCConstants.URN_UNIQUE_IDENTIFIER_START)
                && definition.contains(OGCConstants.URN_UNIQUE_IDENTIFIER_END);
    }
		
	private boolean checkResponseForReturnHumanReadableIdentifierFlag(
			AbstractServiceResponse response)
			throws InvalidParameterValueException {
		return getFlexibleIdentifierHelper()
				.checkForReturnHumanReadableIdentifierFlagExtension(
						response.getExtensions());
	}

	private boolean checkRequestForReturnHumanReadableIdentifierFlag(
			AbstractServiceRequest<?> request)
			throws InvalidParameterValueException {
		return getFlexibleIdentifierHelper()
				.checkForReturnHumanReadableIdentifierFlagExtension(
						request.getExtensions());
	}
	
	private List<String> checkOfferingParameterValues(Collection<String> requestedParameterValues) {
		List<String> checkedParameterValues = Lists.newArrayListWithCapacity(requestedParameterValues.size());
		for (String parameterValue : requestedParameterValues) {
			checkedParameterValues.add(checkOfferingParameterValue(parameterValue));
		}
		return checkedParameterValues;
	}

	private String checkOfferingParameterValue(String parameterValue) {
		return getCache().getOfferingIdentifierForHumanReadableName(parameterValue);
	}
	
	private List<String> checkFeatureOfInterestParameterValues(Collection<String> requestedParameterValues) {
		List<String> checkedParameterValues = Lists.newArrayListWithCapacity(requestedParameterValues.size());
		for (String parameterValue : requestedParameterValues) {
			checkedParameterValues.add(checkFeatureOfInterestParameterValue(parameterValue));
		}
		return checkedParameterValues;
	}

	private String checkFeatureOfInterestParameterValue(String parameterValue) {
		return getCache().getFeatureOfInterestIdentifierForHumanReadableName(parameterValue);
	}
	
	private List<String> checkObservablePropertyParameterValues(Collection<String> requestedParameterValues) {
		List<String> checkedParameterValues = Lists.newArrayListWithCapacity(requestedParameterValues.size());
		for (String parameterValue : requestedParameterValues) {
			checkedParameterValues.add(checkObservablePropertyParameterValue(parameterValue));
		}
		return checkedParameterValues;
	}

	private String checkObservablePropertyParameterValue(String parameterValue) {
		return getCache().getObservablePropertyIdentifierForHumanReadableName(parameterValue);
	}
	
	private List<String> checkProcedureParameterValues(Collection<String> requestedParameterValues) {
		List<String> checkedParameterValues = Lists.newArrayListWithCapacity(requestedParameterValues.size());
		for (String parameterValue : requestedParameterValues) {
			checkedParameterValues.add(checkProcedureParameterValue(parameterValue));
		}
		return checkedParameterValues;
	}

	private String checkProcedureParameterValue(String parameterValue) {
		return getCache().getProcedureIdentifierForHumanReadableName(parameterValue);
	}
	
	private Collection<String> checkFeatureOfInterestIdentifier(
			Collection<String> identifiers) {
			List<String> checkedIdentifiers = Lists.newArrayListWithCapacity(identifiers.size());
			for (String identifier : identifiers) {
				checkedIdentifiers.add(checkFeatureOfInterestIdentifier(identifier));
			}	
			return checkedIdentifiers;
	}

	private String checkFeatureOfInterestIdentifier(String identifier) {
		if (getFlexibleIdentifierHelper().isSetIncludeFeatureOfInterest()) {
			return getCache().getFeatureOfInterestHumanReadableNameForIdentifier(identifier);
		}
		return identifier;
	}
	
	private Collection<String> checkObservablePropertyIdentifier(
			SortedSet<String> identifiers) {
		List<String> checkedIdentifiers = Lists.newArrayListWithCapacity(identifiers.size());
		for (String identifier : identifiers) {
			checkedIdentifiers.add(checkObservablePropertyIdentifier(identifier));
		}
		return checkedIdentifiers;
	}

	private String checkObservablePropertyIdentifier(String identifier) {
		if (getFlexibleIdentifierHelper().isSetIncludeObservableProperty()) {
			return getCache().getObservablePropertyHumanReadableNameForIdentifier(identifier);
		}
		return identifier;
	}
	
	private Collection<String> checkProcedureIdentifier(Set<String> identifiers) {
		List<String> checkedIdentifiers = Lists.newArrayListWithCapacity(identifiers.size());
		for (String identifier : identifiers) {
			checkedIdentifiers.add(checkProcedureIdentifier(identifier));
		}
		return checkedIdentifiers;
	}
	
	private Collection<String> checkProcedureIdentifier(
			SortedSet<String> identifiers) {
		List<String> checkedIdentifiers = Lists.newArrayListWithCapacity(identifiers.size());
		for (String identifier : identifiers) {
			checkedIdentifiers.add(checkProcedureIdentifier(identifier));
		}
		return checkedIdentifiers;
	}
	
	private String checkProcedureIdentifier(String identifier) {
		if (getFlexibleIdentifierHelper().isSetIncludeProcedure()) {
			return getCache().getProcedureHumanReadableNameForIdentifier(identifier);
		}
		return identifier;
	}
	
	private String checkOfferingIdentifier(String identifier) {
		if (getFlexibleIdentifierHelper().isSetIncludeOffering()) {
			return getCache().getOfferingHumanReadableNameForIdentifier(identifier);
		}
		return identifier;
	}
	
	private void checkAndChangeFeatureOfInterestIdentifier(AbstractFeature abstractFeature) {
		if (getFlexibleIdentifierHelper().isSetIncludeFeatureOfInterest()) {
			checkAndChangeIdentifierOfAbstractFeature(abstractFeature);
		}
	}
	
	private void checkAndChangeProcedureIdentifier(AbstractFeature abstractFeature) {
		if (getFlexibleIdentifierHelper().isSetIncludeProcedure()) {
			if (!abstractFeature.isSetHumanReadableIdentifier()) {
				abstractFeature.setHumanReadableIdentifier(checkProcedureIdentifier(abstractFeature.getIdentifier()));
			}
			abstractFeature.setHumanReadableIdentifierAsIdentifier();
		}
	}
	
	private void checkAndChangeObservablePropertyIdentifier(AbstractFeature abstractFeature) {
		if (getFlexibleIdentifierHelper().isSetIncludeObservableProperty()) {
			if (!abstractFeature.isSetHumanReadableIdentifier()) {
				abstractFeature.setHumanReadableIdentifier(checkObservablePropertyIdentifier(abstractFeature.getIdentifier()));
			}
			abstractFeature.setHumanReadableIdentifierAsIdentifier();
		}
	}
	
	private void checkAndChangOfferingIdentifier(SosOffering offering) {
		if (getFlexibleIdentifierHelper().isSetIncludeOffering()) {
			if (!offering.isSetHumanReadableIdentifier()) {
				offering.setHumanReadableIdentifier(checkOfferingIdentifier(offering.getIdentifier()));
			}
			offering.setHumanReadableIdentifierAsIdentifier();
		}
	}

	private void checkAndChangeIdentifierOfAbstractFeature(
			AbstractFeature abstractFeature) {
		if (!abstractFeature.isSetHumanReadableIdentifier()) {
			abstractFeature.setHumanReadableIdentifier(checkFeatureOfInterestIdentifier(abstractFeature.getIdentifier()));
		}
		abstractFeature.setHumanReadableIdentifierAsIdentifier();
		if (abstractFeature instanceof SamplingFeature && ((SamplingFeature) abstractFeature).isSetXmlDescription()) {
			((SamplingFeature) abstractFeature).setXmlDescription(null);
		}
		
	}

	protected FlexibleIdentifierHelper getFlexibleIdentifierHelper() {
		return FlexibleIdentifierHelper.getInstance();
	}

	protected Profile getActiveProfile() {
		return Configurator.getInstance().getProfileHandler()
				.getActiveProfile();
	}

	protected ContentCache getCache() {
        return Configurator.getInstance().getCache();
    }

	@Override
	public boolean isMerger() {
		return false;
	}
}
