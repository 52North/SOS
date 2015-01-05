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

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;
import net.opengis.swes.x20.DescribeSensorResponseDocument;
import net.opengis.swes.x20.DescribeSensorResponseType.Description;
import net.opengis.swes.x20.SensorDescriptionType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.binding.rest.requests.ResourceNotFoundResponse;
import org.n52.sos.binding.rest.requests.RestRequest;
import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OwsExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 */
public class SensorsGetRequestHandler extends SensorsRequestHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorsGetRequestHandler.class);

    @Override
    public RestResponse handleRequest(final RestRequest sensorsHttpGetRequest) throws OwsExceptionReport, XmlException, IOException
    {
        if (sensorsHttpGetRequest != null) {
            if (sensorsHttpGetRequest instanceof GetSensorByIdRequest)
            {
                // Case A: with ID
                return handleGetSensorByIdRequest((GetSensorByIdRequest)sensorsHttpGetRequest);
            }
            else if (sensorsHttpGetRequest instanceof GetSensorsRequest)
            {
                // Case C: global resource
                return handleGetSensorsRequest((GetSensorsRequest)sensorsHttpGetRequest);
            }
        }
        throw logRequestTypeNotSupportedByThisHandlerAndCreateException(sensorsHttpGetRequest,this.getClass().getName());
    }

    private SensorsGetResponse handleGetSensorsRequest(final GetSensorsRequest getSensorsRequest) throws OwsExceptionReport, XmlException, IOException
    {
        final Set<String> sensorIds = Configurator.getInstance().getCache().getProcedures();
        String[] sensorIDs = sensorIds.toArray(new String[sensorIds.size()]);
        Arrays.sort(sensorIDs);
        return new SensorsGetResponse(sensorIDs);
    }

    private RestResponse handleGetSensorByIdRequest(final GetSensorByIdRequest req) throws OwsExceptionReport, XmlException, IOException
    {
        SystemType xb_system;
        String procedureId;
        final XmlObject xb_describeSensorResponse;

        // 0 submit DescribeSensor (if response is an OWSException report -> cancel whole process and throw it)
        procedureId = req.getDescribeSensorRequest().getProcedure();
        try
        {
            xb_describeSensorResponse = executeSosRequest(req.getDescribeSensorRequest());
        }
        catch (final OwsExceptionReport oer) {
            if (!oer.getExceptions().isEmpty())
            {
                for (final CodedException owsE : oer.getExceptions())
                {
                    if (owsE.getCode().equals(OwsExceptionCode.InvalidParameterValue) &&
                            owsE.getLocator().equals(SosConstants.DescribeSensorParams.procedure.toString()))
                    {
                        return new ResourceNotFoundResponse(bindingConstants.getResourceSensors(), procedureId);
                    }
                }
            }
            throw oer;
        }



        if (xb_describeSensorResponse instanceof DescribeSensorResponseDocument) {

            final DescribeSensorResponseDocument xb_describeSensorResponseDoc = (DescribeSensorResponseDocument) xb_describeSensorResponse;
            final Description xb_descriptionDoc = xb_describeSensorResponseDoc.getDescribeSensorResponse().getDescriptionArray()[0];
            final SensorDescriptionType xb_description = xb_descriptionDoc.getSensorDescription();
            xb_system = getSmlSystemFromSensorDescription(xb_description);
            
            // 1 return result 
            return new GetSensorByIdResponse(xb_system,procedureId);

        } else {
            final String exceptionText = String.format("Processing of SOS core operation 'DescribeSensor' response failed. Type of could not be handled: '%s'",
                    xb_describeSensorResponse.getClass().getName());
            LOGGER.debug(exceptionText);
            throw new NoApplicableCodeException().withMessage(exceptionText);
        }
    }
    
    private SystemType getSmlSystemFromSensorDescription(final SensorDescriptionType sensorDescription) throws OwsExceptionReport
    {
        try {
            final SensorMLDocument xb_sensorML = SensorMLDocument.Factory.parse(sensorDescription.getData().newInputStream());

            final SystemType xb_system = (SystemType) xb_sensorML.getSensorML().getMemberArray()[0].getProcess().substitute(SensorMLConstants.SYSTEM_QNAME,SystemType.type);
            return xb_system;
        } catch (final IOException ioe) {
        	throw logAndCreateException(ioe,String.format("Processing of '%s' failed.",
                    GetSensorByIdResponse.class.getName()));
        } catch (final XmlException xe) {
            throw logAndCreateException(xe,String.format("XML Processing of '%s' failed.",
    		        GetSensorByIdResponse.class.getName()));
        }
    }

	private CodedException logAndCreateException(final Exception e, final String exceptionText) throws CodedException
	{
		LOGGER.debug(exceptionText);
		return new NoApplicableCodeException().causedBy(e).withMessage("%s Exception: %s",exceptionText,e.getMessage());
	}
}
