/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.binding;

import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.iceland.binding.AbstractXmlBinding;
import org.n52.iceland.binding.BindingKey;
import org.n52.iceland.binding.MediaTypeBindingKey;
import org.n52.iceland.binding.PathBindingKey;
import org.n52.iceland.coding.SupportedTypeRepository;
import org.n52.iceland.exception.HTTPException;
import org.n52.janmayen.http.MediaType;
import org.n52.janmayen.http.MediaTypes;
import org.n52.janmayen.stream.Streams;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceResponse;
import org.n52.shetland.ogc.sensorML.AbstractProcess;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosInsertionMetadata;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweAbstractDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swes.SwesExtension;
import org.n52.shetland.util.OMHelper;
import org.n52.sos.exception.swes.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Binding to register a sensor without using the SOS-InsertSensor operation.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class RegisterBinding extends AbstractXmlBinding<OwsServiceRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterBinding.class);

    private static final String URL_PATTERN = "/register";

    private static final String PROCEDURE = "procedure";

    private static final String OFFERING = "offering";

    private static final String IS_TYPE = "isType";

    private static final Set<BindingKey> KEYS = ImmutableSet.<BindingKey> builder()
            .add(new PathBindingKey(URL_PATTERN)).add(new MediaTypeBindingKey(MediaTypes.APPLICATION_XML)).build();

    @Inject
    private SupportedTypeRepository supportedTypeRepository;

    @Override
    public Set<BindingKey> getKeys() {
        return Collections.unmodifiableSet(KEYS);
    }

    public String getUrlPattern() {
        return URL_PATTERN;
    }

    @Override
    protected MediaType getDefaultContentType() {
        return MediaTypes.APPLICATION_XML;
    }

    @Override
    protected boolean isUseHttpResponseCodes() {
        return false;
    }

    @Override
    public void doPostOperation(HttpServletRequest req, HttpServletResponse res) throws HTTPException, IOException {
        OwsServiceRequest serviceRequest = null;
        try {
            serviceRequest = parseRequest(req);
            // add request context information
            serviceRequest.setRequestContext(getRequestContext(req));
            OwsServiceResponse response = getServiceOperator(serviceRequest).receiveRequest(serviceRequest);
            writeResponse(req, res, response);
        } catch (OwsExceptionReport oer) {
            oer.setVersion(serviceRequest != null ? serviceRequest.getVersion() : null);
            writeOwsExceptionReport(req, res, oer);
        }
    }

    private OwsServiceRequest parseRequest(HttpServletRequest req) throws OwsExceptionReport {
        Map<String, String> parameters = Streams.stream(req.getParameterNames())
                .collect(toMap(name -> name.replace("amp;", "").toLowerCase(Locale.ROOT), req::getParameter));
        Object object = decode(req);
        if (object != null) {
            SosProcedureDescription<?> procDesc = null;
            if (object instanceof SosProcedureDescription<?>) {
                procDesc = (SosProcedureDescription<?>) object;
            } else if (object instanceof AbstractFeature) {
                procDesc = new SosProcedureDescription<AbstractFeature>((AbstractFeature) object);
            } else {
                throw new NoApplicableCodeException().withMessage("The requested type '{}' is not supported!",
                        object.getClass().getName());
            }

            InsertSensorRequest request = new InsertSensorRequest();
            request.setRequestContext(getRequestContext(req));
            // isType extension
            String isType = getParameterValue(IS_TYPE, parameters);
            boolean isTypeRequest = false;
            if (!Strings.isNullOrEmpty(isType) && Boolean.parseBoolean(isType)) {
                SwesExtension<SweBoolean> extension = new SwesExtension<SweBoolean>();
                extension.setDefinition(IS_TYPE).setValue(new SweBoolean().setValue(true));
                request.addExtension(extension);
                isTypeRequest = true;
            }
            // check for procedure and offering identifier
            // parameterValueMap
            checkForProcedureParameter(procDesc, parameters);
            checkForOfferingParameter(procDesc, parameters);

            // sensor description
            request.setProcedureDescription(procDesc);
            // service and version
            request.setService(getServiceParameterValue(parameters));
            request.setVersion(getVersionParameterValue(parameters));
            // format
            request.setProcedureDescriptionFormat(procDesc.getDescriptionFormat());
            // observable properties
            // get from parameter or from sml:output
            List<String> observableProperties = checkForObservablePropertyParameter(procDesc, parameters);
            if (!observableProperties.isEmpty()) {
                request.setObservableProperty(observableProperties);
            } else if (procDesc.getProcedureDescription() instanceof AbstractSensorML) {
                request.setObservableProperty(getObservablePropertyFromAbstractSensorML(
                        (AbstractSensorML) procDesc.getProcedureDescription()));
            } else if (isTypeRequest) {
                request.setObservableProperty(Lists.newArrayList("not_defined"));
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "The sensor description does not contain sml:outputs which is used to "
                        + "fetch the possible observableProperties! "
                                + "Please add an sml:ouput section or define the observableProperties"
                                + " via 'observableProperty' URL parameter!'");
            }
            // metadata
            if (!isTypeRequest) {
                SosInsertionMetadata metadata = new SosInsertionMetadata();
                List<String> featureOfInterestTypes = checkForFeatureOfInterestTypeParameter(procDesc, parameters);
                if (!featureOfInterestTypes.isEmpty()) {
                    metadata.setFeatureOfInterestTypes(featureOfInterestTypes);
                } else {
                    metadata.setFeatureOfInterestTypes(supportedTypeRepository.getFeatureOfInterestTypesAsString());
                }
                List<String> observationTypes = checkForObservationTypeParameter(procDesc, parameters);
                if (!observationTypes.isEmpty()) {
                    metadata.setObservationTypes(observationTypes);
                } else if (procDesc.getProcedureDescription() instanceof AbstractProcess
                        && ((AbstractProcess) procDesc.getProcedureDescription()).isSetOutputs()) {
                    metadata.setObservationTypes(getObservationTypeFrom(
                            ((AbstractProcess) procDesc.getProcedureDescription()).getOutputs()));
                } else if (procDesc.getProcedureDescription() instanceof SensorML
                        && ((SensorML) procDesc.getProcedureDescription()).isWrapper()) {
                    Set<String> obsTyp = Sets.newHashSet();
                    for (AbstractProcess abstractProcess : ((SensorML) procDesc.getProcedureDescription())
                            .getMembers()) {
                        if (abstractProcess.isSetOutputs()) {
                            obsTyp.addAll(getObservationTypeFrom(abstractProcess.getOutputs()));
                        }
                    }
                    metadata.setObservationTypes(obsTyp);
                } else {
                    metadata.setObservationTypes(supportedTypeRepository.getObservationTypesAsString());
                }
                request.setMetadata(metadata);
            }
            return request;
        }
        throw new InvalidRequestException().withMessage("The requested sensor description null is not supported!");
    }

    private String getServiceParameterValue(Map<String, String> map) {
        final String service = getParameterValue(OWSConstants.RequestParams.service, map);
        if (Strings.isNullOrEmpty(service)) {
            return Sos2Constants.SOS;
        }
        return service;
    }

    private String getVersionParameterValue(Map<String, String> map) {
        final String version = getParameterValue(OWSConstants.RequestParams.version, map);
        if (Strings.isNullOrEmpty(version)) {
            return Sos2Constants.SERVICEVERSION;
        }
        return version;
    }

    private void checkForProcedureParameter(SosProcedureDescription<?> procDesc, Map<String, String> map) {
        final String procedure = getParameterValue(PROCEDURE, map);
        if (!Strings.isNullOrEmpty(procedure)) {
            procDesc.setIdentifier(new CodeWithAuthority(procedure));
        }
    }

    private void checkForOfferingParameter(SosProcedureDescription<?> procDesc, Map<String, String> map)
            throws OwsExceptionReport {
        final String offerings = getParameterValue(OFFERING, map);
        if (!Strings.isNullOrEmpty(offerings)) {
            List<String> multipleOfferingValues = checkParameterMultipleValues(offerings, OFFERING);
            for (String offering : multipleOfferingValues) {
                procDesc.addOffering(new SosOffering(offering, offering));
            }
        }
    }

    private List<String> checkForObservablePropertyParameter(SosProcedureDescription<?> procDesc,
            Map<String, String> map) throws OwsExceptionReport {
        final String offering = getParameterValue(Sos2Constants.InsertSensorParams.observableProperty, map);
        if (!Strings.isNullOrEmpty(offering)) {
            return checkParameterMultipleValues(offering, Sos2Constants.InsertSensorParams.observableProperty);
        }
        return Collections.emptyList();
    }

    private List<String> checkForObservationTypeParameter(SosProcedureDescription<?> procDesc, Map<String, String> map)
            throws OwsExceptionReport {
        final String offering = getParameterValue(Sos2Constants.InsertSensorParams.observationType, map);
        if (!Strings.isNullOrEmpty(offering)) {
            return checkParameterMultipleValues(offering, Sos2Constants.InsertSensorParams.observationType);
        }
        return Collections.emptyList();
    }

    private List<String> checkForFeatureOfInterestTypeParameter(SosProcedureDescription<?> procDesc,
            Map<String, String> map) throws OwsExceptionReport {
        final String offering = getParameterValue(Sos2Constants.InsertSensorParams.featureOfInterestType, map);
        if (!Strings.isNullOrEmpty(offering)) {
            return checkParameterMultipleValues(offering, Sos2Constants.InsertSensorParams.featureOfInterestType);
        }
        return Collections.emptyList();
    }

    private String getObservablePropertyIdentifierFrom(SweAbstractDataComponent abstractDataComponent) {
        if (abstractDataComponent.isSetIdentifier()) {
            return abstractDataComponent.getIdentifier();
        } else if (abstractDataComponent.isSetDefinition()) {
            return abstractDataComponent.getDefinition();
        }
        return null;
    }

    private List<String> getObservablePropertyFromAbstractSensorML(AbstractSensorML absSensorML) {
        Set<String> obsProps = Sets.newHashSet();
        if (absSensorML instanceof AbstractProcess && ((AbstractProcess) absSensorML).isSetOutputs()) {
            for (SmlIo smlIo : ((AbstractProcess) absSensorML).getOutputs()) {
                if (smlIo.isSetValue()) {
                    SweAbstractDataComponent abstractDataComponent = smlIo.getIoValue();
                    if (abstractDataComponent instanceof SweAbstractDataRecord) {
                        for (SweField field : ((SweAbstractDataRecord) abstractDataComponent).getFields()) {
                            String identifier = getObservablePropertyIdentifierFrom(field.getElement());
                            if (!Strings.isNullOrEmpty(identifier)) {
                                obsProps.add(identifier);
                            } else {
                                String identifierFromField = getObservablePropertyIdentifierFrom(field.getElement());
                                if (!Strings.isNullOrEmpty(identifierFromField)) {
                                    obsProps.add(identifierFromField);
                                } else {
                                    obsProps.add(field.getName().getValue());
                                }
                            }
                        }
                    }
                    String identifier = getObservablePropertyIdentifierFrom(abstractDataComponent);
                    if (!Strings.isNullOrEmpty(identifier)) {
                        obsProps.add(identifier);
                    }
                } else if (smlIo.isSetName()) {
                    obsProps.add(smlIo.getIoName());
                }
            }
        } else if (absSensorML instanceof SensorML && ((SensorML) absSensorML).isWrapper()) {
            for (AbstractProcess abstractProcess : ((SensorML) absSensorML).getMembers()) {
                obsProps.addAll(getObservablePropertyFromAbstractSensorML(abstractProcess));
            }
        }
        return Lists.newArrayList(obsProps);
    }

    private Set<String> getObservationTypeFrom(List<SmlIo> outputs) {
        Set<String> observationTypes = Sets.newHashSet();
        for (SmlIo smlIo : outputs) {
            if (smlIo.isSetValue()) {
                try {
                    if (smlIo.getIoValue() instanceof SweAbstractDataRecord) {
                        for (SweField field : ((SweAbstractDataRecord) smlIo.getIoValue()).getFields()) {
                            observationTypes.add(OMHelper.getObservationTypeFrom(field.getElement()));
                        }
                    }
                    observationTypes.add(OMHelper.getObservationTypeFrom(smlIo.getIoValue()));
                } catch (OwsExceptionReport e) {
                    observationTypes.add(OmConstants.OBS_TYPE_OBSERVATION);
                    LOGGER.debug("The output element can not be assigned to an observation type", e);
                }
            }
        }
        return observationTypes;
    }

    private String getParameterValue(String name, Map<String, String> map) {
        return map.computeIfAbsent(name, key -> map.entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(key))
                .findFirst().map(Entry::getValue).orElse(null));
    }

    private String getParameterValue(Enum<?> name, Map<String, String> map) {
        return getParameterValue(name.name(), map);
    }

    public List<String> checkParameterMultipleValues(String values, String name) throws OwsExceptionReport {
        if (values.isEmpty()) {
            throw new MissingParameterValueException(name);
        }
        List<String> splittedParameterValues = Arrays.asList(values.split(","));
        for (String parameterValue : splittedParameterValues) {
            if (Strings.isNullOrEmpty(parameterValue)) {
                throw new MissingParameterValueException(name);
            }
        }
        return splittedParameterValues;
    }

    public List<String> checkParameterMultipleValues(String values, Enum<?> name) throws OwsExceptionReport {
        return checkParameterMultipleValues(values, name.name());
    }

}
