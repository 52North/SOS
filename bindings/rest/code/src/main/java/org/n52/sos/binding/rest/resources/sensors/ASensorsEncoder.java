/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.binding.rest.resources.sensors;

import java.util.HashMap;
import java.util.Map;

import net.opengis.sensorML.x101.IoComponentPropertyType;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.sosREST.x10.LinkType;
import net.opengis.sosREST.x10.SensorDocument;
import net.opengis.sosREST.x10.SensorType;
import net.opengis.swe.x101.ObservablePropertyDocument.ObservableProperty;

import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.sos.binding.rest.Constants;
import org.n52.sos.binding.rest.encode.ResourceEncoder;
import org.n52.svalbard.util.XmlOptionsHelper;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public abstract class ASensorsEncoder extends ResourceEncoder {
    public ASensorsEncoder(Constants constants, XmlOptionsHelper xmlOptionsHelper) {
        super(constants, xmlOptionsHelper);
    }

    protected SensorType createRestDefaultRestSensor(SensorResponse sensorsResponse,
                                                     SensorDocument xb_SensorRestDoc) throws OwsExceptionReport {
        SensorType xb_SensorRest = xb_SensorRestDoc.addNewSensor();
        //
        SystemType xb_abstractProcessType = (SystemType) xb_SensorRest.addNewProcess()
                .substitute(SensorMLConstants.SYSTEM_QNAME, SystemType.type);
        xb_abstractProcessType.set(sensorsResponse.getSensorDescriptionXB());
        // sensor links
        addSelfLink(sensorsResponse, xb_SensorRest);
        addDeleteLink(sensorsResponse, xb_SensorRest);
        addUpdateLink(sensorsResponse, xb_SensorRest);
        // add restful links
        String procedureId = sensorsResponse.getProcedureIdentifier();
        // rel:features-get
        setValuesOfLinkToDynamicResource(xb_SensorRest.addNewLink(),
                                         createQueryStringForProcedureId(procedureId), Constants.REST_RESOURCE_RELATION_FEATURES_GET, Constants.REST_RESOURCE_RELATION_FEATURES);

        // rel:observations-get
        setValuesOfLinkToDynamicResource(xb_SensorRest.addNewLink(),
                                         createQueryStringForProcedureId(procedureId),
                                         Constants.REST_RESOURCE_RELATION_OBSERVATIONS_GET, Constants.REST_RESOURCE_RELATION_OBSERVATIONS);

        // rel:observable-property links
        setObservablePropertiesLinks(xb_SensorRest,
                                     getObservablePropertiesFromSensorDescription(sensorsResponse
                                             .getSensorDescriptionXB()));
        return xb_SensorRest;
    }

    private Map<String, String> getObservablePropertiesFromSensorDescription(SystemType xb_system) throws
            OwsExceptionReport {
        Map<String, String> observableProperties = new HashMap<>();
        if (xb_system != null &&
            xb_system.isSetInputs() &&
            xb_system.getInputs().isSetInputList() &&
            xb_system.getInputs().getInputList() != null &&
            xb_system.getInputs().getInputList().getInputArray() != null) {
            IoComponentPropertyType[] xb_inputs = xb_system.getInputs().getInputList().getInputArray();
            for (IoComponentPropertyType xb_input : xb_inputs) {
                if (xb_input != null && xb_input.isSetObservableProperty()) {
                    String type = MediaTypes.APPLICATION_XML.toString();
                    if (xb_input.getTitle() != null && !xb_input.getTitle().isEmpty()) {
                        type = xb_input.getTitle();
                    }
                    ObservableProperty xb_ObservableProperty = xb_input.getObservableProperty();
                    if (xb_ObservableProperty.isSetDefinition() && type != null && !type.isEmpty()) {
                        String property = xb_ObservableProperty.getDefinition();
                        observableProperties.put(property, type);
                    }
                }
            }
            if (observableProperties.size() > 0) {
                return observableProperties;
            }
        }
        return null;
    }

    private void setObservablePropertiesLinks(SensorType xb_SensorRest,
                                              Map<String, String> observableProperties) {
        if (observableProperties != null) {
            for (String observableProperty : observableProperties.keySet()) {
                LinkType xb_ObservablePropertyLink = xb_SensorRest.addNewLink();
                xb_ObservablePropertyLink.setRel(getConstants().getEncodingNamespace().concat("/")
                        .concat(Constants.REST_RESOURCE_RELATION_OBSERVABLEPROPERTY_GET));
                if (observableProperty.contains("http://")) {
                    xb_ObservablePropertyLink.setHref(observableProperty);
                    xb_ObservablePropertyLink.setType(observableProperties.get(observableProperty));
                } else {
                    xb_ObservablePropertyLink
                            .setHref(createHrefForResourceAndIdentifier(Constants.REST_RESOURCE_RELATION_OBSERVABLE_PROPERTIES,
                                                                        observableProperty));
                    xb_ObservablePropertyLink.setType(getConstants().getContentTypeDefault().toString());
                }
            }
        }
    }

    private String createQueryStringForProcedureId(String procedureId) {
        return (Constants.REST_HTTP_GET_PARAMETERNAME_PROCEDURES).concat("=").concat(procedureId);
    }

    private void addDeleteLink(SensorResponse sensorsPostResponse,
                               SensorType xb_SensorRest) {
        setValuesOfLinkToUniqueResource(xb_SensorRest.addNewLink(),
                                        sensorsPostResponse.getProcedureIdentifier(), Constants.REST_RESOURCE_RELATION_SENSOR_DELETE, Constants.REST_RESOURCE_SENSORS);
    }

    private void addUpdateLink(SensorResponse sensorsPostResponse,
                               SensorType xb_SensorRest) {
        setValuesOfLinkToUniqueResource(xb_SensorRest.addNewLink(),
                                        sensorsPostResponse.getProcedureIdentifier(), Constants.REST_RESOURCE_RELATION_SENSOR_UPDATE, Constants.REST_RESOURCE_SENSORS);
    }

    private void addSelfLink(SensorResponse sensorsPostResponse,
                             SensorType xb_SensorRest) {
        setValuesOfLinkToUniqueResource(xb_SensorRest.addNewLink(),
                                        sensorsPostResponse.getProcedureIdentifier(), Constants.REST_RESOURCE_RELATION_SELF, Constants.REST_RESOURCE_SENSORS);
    }

}
