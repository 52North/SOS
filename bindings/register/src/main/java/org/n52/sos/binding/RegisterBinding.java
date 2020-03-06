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
package org.n52.sos.binding;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.swes.InvalidRequestException;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OWSConstants.RequestParams;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractProcess;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.elements.SmlIo;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweAbstractDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Binding to register a sensor without using the SOS-InsertSensor operation.
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class RegisterBinding extends SimpleBinding {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterBinding.class);

    private static final String PROCEDURE = "procedure";

    private static final String OFFERING = "offering";

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    protected MediaType getDefaultContentType() {
        return MediaTypes.APPLICATION_XML;
    }

    @Override
    public String getUrlPattern() {
        return "/register";
    }

    @Override
    public Set<MediaType> getSupportedEncodings() {
        return Collections.emptySet();
    }

    @Override
    public void doPostOperation(HttpServletRequest req, HttpServletResponse res) throws HTTPException, IOException {
        AbstractServiceRequest<?> serviceRequest = null;
        try {
            serviceRequest = parseRequest(req);
            // add request context information
            serviceRequest.setRequestContext(getRequestContext(req));
            AbstractServiceResponse response = getServiceOperator(serviceRequest).receiveRequest(serviceRequest);
            writeResponse(req, res, response);
        } catch (OwsExceptionReport oer) {
            oer.setVersion(serviceRequest != null ? serviceRequest.getVersion() : null);
            writeOwsExceptionReport(req, res, oer);
        }
    }

    private AbstractServiceRequest<?> parseRequest(HttpServletRequest req) throws OwsExceptionReport {
        Map<String, String> parameterValueMap = KvpHelper.getKvpParameterValueMap(req);
        XmlObject doc = XmlHelper.parseXmlSosRequest(req);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.debug("REGISTER-REQUEST: {}", doc.xmlText());
        }
        Object object = getDecoder(CodingHelper.getDecoderKey(doc)).decode(doc);
        if (object != null && object instanceof SosProcedureDescription) {

            SosProcedureDescription procDesc = (SosProcedureDescription) object;
            InsertSensorRequest request = new InsertSensorRequest();
            // isType extension
            String isType = KvpHelper.getParameterValue("isType", parameterValueMap);
            boolean isTypeRequest = false;
            if (!Strings.isNullOrEmpty(isType) && Boolean.parseBoolean(isType)) {
                SwesExtensionImpl<SweBoolean> extension = new SwesExtensionImpl<SweBoolean>();
                extension.setDefinition("isType").setValue(new SweBoolean().setValue(true));
                request.addExtension(extension);
                isTypeRequest = true;
            }
            // check for procedure and offering identifier
            // parameterValueMap
            checkForProcedureParameter(procDesc, parameterValueMap);
            checkForOfferingParameter(procDesc, parameterValueMap);

            // sensor description
            request.setProcedureDescription(procDesc);
            // service and version
            request.setService(getServiceParameterValue(parameterValueMap));
            request.setVersion(getVersionParameterValue(parameterValueMap));
            // format
            request.setProcedureDescriptionFormat(procDesc.getDescriptionFormat());
            // observable properties
            // get from parameter or from sml:output
            List<String> observableProperties = checkForObservablePropertyParameter(procDesc, parameterValueMap);
            if (!observableProperties.isEmpty()) {
                request.setObservableProperty(observableProperties);
            } else if (procDesc instanceof AbstractSensorML) {
                request.setObservableProperty(getObservablePropertyFromAbstractSensorML((AbstractSensorML)procDesc));
            } else if (isTypeRequest) {
                request.setObservableProperty(Lists.newArrayList("not_defined"));
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "The sensor description does not contain sml:outputs which is used to fetch the possible observableProperties! "
                        + "Please add an sml:ouput section or define the observableProperties via 'observableProperty' URL parameter!'");
            }
            // metadata
            if (!isTypeRequest) {
                SosInsertionMetadata metadata = new SosInsertionMetadata();
                List<String> featureOfInterestTypes =
                        checkForFeatureOfInterestTypeParameter(procDesc, parameterValueMap);
                if (!featureOfInterestTypes.isEmpty()) {
                    metadata.setFeatureOfInterestTypes(featureOfInterestTypes);
                } else {
                    metadata.setFeatureOfInterestTypes(
                            Configurator.getInstance().getCache().getFeatureOfInterestTypes());
                }
                List<String> observationTypes = checkForObservationTypeParameter(procDesc, parameterValueMap);
                if (!observationTypes.isEmpty()) {
                    metadata.setObservationTypes(observationTypes);
                } else if (procDesc instanceof AbstractProcess && ((AbstractProcess) procDesc).isSetOutputs()) {
                    metadata.setObservationTypes(getObservationTypeFrom(((AbstractProcess) procDesc).getOutputs()));
                } else if (procDesc instanceof SensorML && ((SensorML)procDesc).isWrapper()) {
                    Set<String> obsTyp = Sets.newHashSet();
                    for (AbstractProcess abstractProcess : ((SensorML)procDesc).getMembers()) {
                        if (abstractProcess.isSetOutputs()) {
                            obsTyp.addAll(getObservationTypeFrom(abstractProcess.getOutputs()));
                        }
                    }
                    metadata.setObservationTypes(obsTyp);
                } else {
                    metadata.setObservationTypes(Configurator.getInstance().getCache().getObservationTypes());
                }
                request.setMetadata(metadata);
            }
            return request;
        }
        throw new InvalidRequestException().withMessage("The requested sensor description {} is not supported!",
                object != null ? object.getClass().getName() : "null");
    }

    private String getServiceParameterValue(Map<String, String> map) {
        final String service = KvpHelper.getParameterValue(RequestParams.service, map);
        if (Strings.isNullOrEmpty(service)) {
            return Sos2Constants.SOS;
        }
        return service;
    }

    private String getVersionParameterValue(Map<String, String> map) {
        final String version = KvpHelper.getParameterValue(RequestParams.version, map);
        if (Strings.isNullOrEmpty(version)) {
            return Sos2Constants.SERVICEVERSION;
        }
        return version;
    }

    private void checkForProcedureParameter(SosProcedureDescription procDesc, Map<String, String> map) {
        final String procedure = KvpHelper.getParameterValue(PROCEDURE, map);
        if (!Strings.isNullOrEmpty(procedure)) {
            procDesc.setIdentifier(new CodeWithAuthority(procedure));
        }
    }

    private void checkForOfferingParameter(SosProcedureDescription procDesc, Map<String, String> map)
            throws MissingParameterValueException {
        final String offerings = KvpHelper.getParameterValue(OFFERING, map);
        if (!Strings.isNullOrEmpty(offerings)) {
            List<String> multipleOfferingValues = KvpHelper.checkParameterMultipleValues(offerings, OFFERING);
            for (String offering : multipleOfferingValues) {
                procDesc.addOffering(new SosOffering(offering, offering));
            }
        }
    }

    private List<String> checkForObservablePropertyParameter(SosProcedureDescription procDesc, Map<String, String> map)
            throws MissingParameterValueException {
        final String offering = KvpHelper.getParameterValue(Sos2Constants.InsertSensorParams.observableProperty, map);
        if (!Strings.isNullOrEmpty(offering)) {
            return KvpHelper.checkParameterMultipleValues(offering,
                    Sos2Constants.InsertSensorParams.observableProperty);
        }
        return Collections.emptyList();
    }

    private List<String> checkForObservationTypeParameter(SosProcedureDescription procDesc, Map<String, String> map)
            throws MissingParameterValueException {
        final String offering = KvpHelper.getParameterValue(Sos2Constants.InsertSensorParams.observationType, map);
        if (!Strings.isNullOrEmpty(offering)) {
            return KvpHelper.checkParameterMultipleValues(offering, Sos2Constants.InsertSensorParams.observationType);
        }
        return Collections.emptyList();
    }

    private List<String> checkForFeatureOfInterestTypeParameter(SosProcedureDescription procDesc,
            Map<String, String> map) throws MissingParameterValueException {
        final String offering =
                KvpHelper.getParameterValue(Sos2Constants.InsertSensorParams.featureOfInterestType, map);
        if (!Strings.isNullOrEmpty(offering)) {
            return KvpHelper.checkParameterMultipleValues(offering,
                    Sos2Constants.InsertSensorParams.featureOfInterestType);
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
            for (SmlIo<?> smlIo : ((AbstractProcess) absSensorML).getOutputs()) {
                if (smlIo.isSetValue()) {
                    SweAbstractDataComponent abstractDataComponent = smlIo.getIoValue();
                    if (abstractDataComponent instanceof SweAbstractDataRecord) {
                        for (SweField field : ((SweAbstractDataRecord) abstractDataComponent).getFields()) {
                            String identifier = getObservablePropertyIdentifierFrom(field.getElement());
                            if (!Strings.isNullOrEmpty(identifier)) {
                                obsProps.add(identifier);
                            } else {
                                String identifierFromField =
                                        getObservablePropertyIdentifierFrom(field.getElement());
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
        } else if (absSensorML instanceof SensorML && ((SensorML)absSensorML).isWrapper()) {
            for (AbstractProcess abstractProcess : ((SensorML)absSensorML).getMembers()) {
                obsProps.addAll(getObservablePropertyFromAbstractSensorML(abstractProcess));
            }
        }
        return Lists.newArrayList(obsProps);
    }

    private Set<String> getObservationTypeFrom(List<SmlIo<?>> outputs) {
        Set<String> observationTypes = Sets.newHashSet();
        for (SmlIo<?> smlIo : outputs) {
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
}
