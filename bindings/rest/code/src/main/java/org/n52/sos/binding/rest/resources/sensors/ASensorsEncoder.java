/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.binding.rest.encode.ResourceEncoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.util.http.MediaTypes;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public abstract class ASensorsEncoder extends ResourceEncoder {

    /**
     * 
     */
    public ASensorsEncoder() {
        super();
    }

    protected SensorType createRestDefaultRestSensor(SensorResponse sensorsResponse,
            SensorDocument xb_SensorRestDoc) throws OwsExceptionReport
    {
        SensorType xb_SensorRest = xb_SensorRestDoc.addNewSensor();
        // 
        SystemType xb_abstractProcessType = (SystemType) xb_SensorRest.addNewProcess().substitute(SensorMLConstants.SYSTEM_QNAME,SystemType.type);
        xb_abstractProcessType.set(sensorsResponse.getSensorDescriptionXB());
        // sensor links
        addSelfLink(sensorsResponse, xb_SensorRest);
        addDeleteLink(sensorsResponse, xb_SensorRest);
        addUpdateLink(sensorsResponse, xb_SensorRest);
        // add restful links
        String procedureId = sensorsResponse.getProcedureIdentifier();
        // rel:features-get
        setValuesOfLinkToDynamicResource(xb_SensorRest.addNewLink(),
                createQueryStringForProcedureId(procedureId),
                bindingConstants.getResourceRelationFeaturesGet(),
                bindingConstants.getResourceFeatures());
        
        // rel:observations-get
        setValuesOfLinkToDynamicResource(xb_SensorRest.addNewLink(),
                createQueryStringForProcedureId(procedureId),
                bindingConstants.getResourceRelationObservationsGet(),
                bindingConstants.getResourceObservations());

        // rel:observable-property links
        setObservablePropertiesLinks(xb_SensorRest,
                getObservablePropertiesFromSensorDescription(sensorsResponse.getSensorDescriptionXB()));
        return xb_SensorRest;
    }
    
    private Map<String,String> getObservablePropertiesFromSensorDescription(SystemType xb_system) throws OwsExceptionReport
    {
        Map<String,String> observableProperties = new HashMap<String,String>();
        if (xb_system != null && 
                xb_system.isSetInputs() && 
                xb_system.getInputs().isSetInputList() && 
                xb_system.getInputs().getInputList() != null &&
                xb_system.getInputs().getInputList().getInputArray() != null)
        {
            IoComponentPropertyType[] xb_inputs = xb_system.getInputs().getInputList().getInputArray();
            for (IoComponentPropertyType xb_input : xb_inputs)
            {
                if (xb_input != null && xb_input.isSetObservableProperty())
                {
                    String type = MediaTypes.APPLICATION_XML.toString();
                    if (xb_input.getTitle() != null && !xb_input.getTitle().isEmpty())
                    {
                        type = xb_input.getTitle();
                    }
                    ObservableProperty xb_ObservableProperty = xb_input.getObservableProperty();
                    if (xb_ObservableProperty.isSetDefinition() && type != null && !type.isEmpty())
                    {
                        String property = xb_ObservableProperty.getDefinition();
                        observableProperties.put(property, type);
                    }
                }
            }
            if (observableProperties.size() > 0)
            {
                return observableProperties;
            }
        }
        return null;
    }

    private void setObservablePropertiesLinks(SensorType xb_SensorRest,
            Map<String,String> observableProperties)
    {
        LinkType xb_ObservablePropertyLink = null;
        if (observableProperties != null)
        {
            for (String observableProperty : observableProperties.keySet()) {
                xb_ObservablePropertyLink = xb_SensorRest.addNewLink();
                xb_ObservablePropertyLink.setRel(
                        bindingConstants.getEncodingNamespace().concat("/")
                        .concat(bindingConstants.getResourceRelationObservablePropertyGet()));
                if (observableProperty.indexOf("http://") != -1)
                {
                    xb_ObservablePropertyLink.setHref(observableProperty);
                    xb_ObservablePropertyLink.setType(observableProperties.get(observableProperty));
                } 
                else 
                {
                    xb_ObservablePropertyLink.setHref(createHrefForResourceAndIdentifier(
                            bindingConstants.getResourceObservableProperties(),
                            observableProperty));
                    xb_ObservablePropertyLink.setType(bindingConstants.getContentTypeDefault().toString());
                }
            }
        }
    }
    
    private String createQueryStringForProcedureId(String procedureId)
    {
        return bindingConstants.getHttpGetParameterNameProcedure().concat("=").concat(procedureId);
    }
    
    private void addDeleteLink(SensorResponse sensorsPostResponse,
            SensorType xb_SensorRest)
    {
        setValuesOfLinkToUniqueResource(xb_SensorRest.addNewLink(),
                sensorsPostResponse.getProcedureIdentifier(),
                bindingConstants.getResourceRelationSensorDelete(),
                bindingConstants.getResourceSensors());
    }

    private void addUpdateLink(SensorResponse sensorsPostResponse,
            SensorType xb_SensorRest)
    {
        setValuesOfLinkToUniqueResource(xb_SensorRest.addNewLink(),
                sensorsPostResponse.getProcedureIdentifier(),
                bindingConstants.getResourceRelationSensorUpdate(),
                bindingConstants.getResourceSensors());
    }

    private void addSelfLink(SensorResponse sensorsPostResponse,
            SensorType xb_SensorRest)
    {
        setValuesOfLinkToUniqueResource(
                xb_SensorRest.addNewLink(),
                sensorsPostResponse.getProcedureIdentifier(),
                bindingConstants.getResourceRelationSelf(),
                bindingConstants.getResourceSensors());
    }


}
