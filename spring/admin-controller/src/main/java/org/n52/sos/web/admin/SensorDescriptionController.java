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
package org.n52.sos.web.admin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import org.n52.sos.binding.Binding;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.ds.ProcedureFormatDAO;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.exception.ows.concrete.NoImplementationFoundException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.ServiceLoaderHelper;
import org.n52.sos.util.http.MediaTypes;
import org.n52.sos.web.ControllerConstants;

import com.google.common.collect.Lists;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 */
@Controller
@RequestMapping(ControllerConstants.Paths.ADMIN_SENSORS_DESCRIPTIONS)
public class SensorDescriptionController extends AbstractAdminController {

    private static final Logger log = LoggerFactory.getLogger(SensorDescriptionController.class);

    private static final String SENSORS = "sensors";

    private static final String PROCEDURE_FORMAT_MAP = "procedureFormatMap";

    private static final String IS_UPDATE_SENSOR_SUPPORTED = "isUpdateSensorSupported";

    private static final String IS_DESCRIBE_SENSOR_SUPPORTED = "isDescribeSensorSupported";

    private static final String IS_DELETE_SENSOR_SUPPORTED = "isDeleteSensorSupported";

    private static final String DESCRIBE_SENSOR_REQUEST_METHOD = "describeSensorRequestMethod";

    private ProcedureFormatDAO dao;

    private static final OperationDecoderKey DESCRIBE_SENSOR_DECODER_KEY_SOAP = new OperationDecoderKey(
            SosConstants.SOS,
            Sos2Constants.SERVICEVERSION,
            SosConstants.Operations.DescribeSensor.name(),
            MediaTypes.APPLICATION_SOAP_XML);

    private static final OperationDecoderKey DESCRIBE_SENSOR_DECODER_KEY_KVP = new OperationDecoderKey(
            SosConstants.SOS,
            Sos2Constants.SERVICEVERSION,
            SosConstants.Operations.DescribeSensor.name(),
            MediaTypes.APPLICATION_KVP);

    private static final OperationDecoderKey UPDATE_SENSOR_DECODER_KEY_KVP = new OperationDecoderKey(
            SosConstants.SOS,
            Sos2Constants.SERVICEVERSION,
            Sos2Constants.Operations.UpdateSensorDescription.name(),
            MediaTypes.APPLICATION_KVP);

    private static final OperationDecoderKey DELETE_SENSOR_DECODER_KEY_KVP = new OperationDecoderKey(
            SosConstants.SOS,
            Sos2Constants.SERVICEVERSION,
            Sos2Constants.Operations.DeleteSensor.name(),
            MediaTypes.APPLICATION_KVP);

    private static final OperationDecoderKey UPDATE_SENSOR_DECODER_KEY_SOAP = new OperationDecoderKey(
            SosConstants.SOS,
            Sos2Constants.SERVICEVERSION,
            Sos2Constants.Operations.UpdateSensorDescription.name(),
            MediaTypes.APPLICATION_SOAP_XML);

    private static final OperationDecoderKey DELETE_SENSOR_DECODER_KEY_SOAP = new OperationDecoderKey(
            SosConstants.SOS,
            Sos2Constants.SERVICEVERSION,
            Sos2Constants.Operations.DeleteSensor.name(),
            MediaTypes.APPLICATION_SOAP_XML);

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView view() throws OwsExceptionReport {
        Map<String, Object> model = new HashMap<String, Object>(5);
		boolean getKvp = false, getSoap = false, update = false, delete = false;
        try {
			for (Binding b : BindingRepository.getInstance().getBindings().values()) {
				if (b.checkOperationHttpGetSupported(DESCRIBE_SENSOR_DECODER_KEY_KVP)) {
					getKvp = true;
				}
				if (b.checkOperationHttpPostSupported(UPDATE_SENSOR_DECODER_KEY_KVP)) {
					update = true;
				}
				if (b.checkOperationHttpPostSupported(DELETE_SENSOR_DECODER_KEY_KVP)) {
					delete = true;
				}
				if (b.checkOperationHttpPostSupported(DESCRIBE_SENSOR_DECODER_KEY_SOAP)) {
					getSoap = true;
				}
				if (b.checkOperationHttpPostSupported(UPDATE_SENSOR_DECODER_KEY_SOAP)) {
					update = true;
				}
				if (b.checkOperationHttpPostSupported(DELETE_SENSOR_DECODER_KEY_SOAP)) {
					delete = true;
				}
			}
        } catch (HTTPException ex) {
            log.error("Error requesting DCP for operation.", ex);
        }

        if (getKvp) {
            model.put(DESCRIBE_SENSOR_REQUEST_METHOD, "GET");
        } else if (getSoap) {
            model.put(DESCRIBE_SENSOR_REQUEST_METHOD, "POST");
        }
        model.put(IS_DELETE_SENSOR_SUPPORTED, delete);
        model.put(IS_UPDATE_SENSOR_SUPPORTED, update);
        model.put(IS_DESCRIBE_SENSOR_SUPPORTED, getKvp||getSoap);
        List<String> procedures = Lists.newArrayList(Configurator.getInstance().getCache().getProcedures());
        Collections.sort(procedures);
        model.put(SENSORS, procedures);
        model.put(PROCEDURE_FORMAT_MAP, getProcedureFormatDao().getProcedureFormatMap());
        return new ModelAndView(ControllerConstants.Views.ADMIN_SENSOR_DESCRIPTIONS, model);
    }

    private ProcedureFormatDAO getProcedureFormatDao() throws NoImplementationFoundException {
        if (this.dao == null) {
            this.dao = ServiceLoaderHelper.loadImplementation(ProcedureFormatDAO.class);
        }
        return this.dao;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NoImplementationFoundException.class)
    public String onError(NoImplementationFoundException e) {
        return String.format("No ProcedureFormatDAO implementation found!");
    }
}
