/*
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
package org.n52.sos.web.admin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.n52.iceland.binding.Binding;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.exception.HTTPException;
import org.n52.iceland.exception.ows.concrete.NoImplementationFoundException;
import org.n52.iceland.request.operator.RequestOperatorKey;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.sos.ds.ProcedureFormatDAO;
import org.n52.sos.web.common.ControllerConstants;
import org.n52.svalbard.decode.OperationDecoderKey;
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

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
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

    @Inject
    private Optional<ProcedureFormatDAO> dao;
    @Inject
    private Optional<RequestOperatorRepository> requestOperatorRepository = Optional.empty();
    @Inject
    private Optional<BindingRepository> bindingRepository = Optional.empty();

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView view() throws OwsExceptionReport {
        Map<String, Object> model = new HashMap<>(5);
        boolean getKvp = false;
        boolean getSoap = false;
        boolean update = false;
        boolean delete = false;
        if (this.requestOperatorRepository.isPresent() && this.bindingRepository.isPresent()) {
            try {
                for (Binding b : this.bindingRepository.get().getBindings().values()) {
                    if (b.checkOperationHttpGetSupported(DESCRIBE_SENSOR_DECODER_KEY_KVP)) {
                        getKvp = true;
                    }
                    if (checkRequestOperator(UPDATE_SENSOR_DECODER_KEY_KVP)
                            && b.checkOperationHttpPostSupported(UPDATE_SENSOR_DECODER_KEY_KVP)) {
                        update = true;
                    }
                    if (checkRequestOperator(DELETE_SENSOR_DECODER_KEY_KVP)
                            && b.checkOperationHttpPostSupported(DELETE_SENSOR_DECODER_KEY_KVP)) {
                        delete = true;
                    }
                    if (b.checkOperationHttpPostSupported(DESCRIBE_SENSOR_DECODER_KEY_SOAP)) {
                        getSoap = true;
                    }
                    if (checkRequestOperator(UPDATE_SENSOR_DECODER_KEY_SOAP)
                            && b.checkOperationHttpPostSupported(UPDATE_SENSOR_DECODER_KEY_SOAP)) {
                        update = true;
                    }
                    if (checkRequestOperator(DELETE_SENSOR_DECODER_KEY_SOAP)
                            && b.checkOperationHttpPostSupported(DELETE_SENSOR_DECODER_KEY_SOAP)) {
                        delete = true;
                    }
                }
            } catch (HTTPException ex) {
                log.error("Error requesting DCP for operation.", ex);
            }
        }

        if (getKvp) {
            model.put(DESCRIBE_SENSOR_REQUEST_METHOD, "GET");
        } else if (getSoap) {
            model.put(DESCRIBE_SENSOR_REQUEST_METHOD, "POST");
        }
        model.put(IS_DELETE_SENSOR_SUPPORTED, delete);
        model.put(IS_UPDATE_SENSOR_SUPPORTED, update);
        model.put(IS_DESCRIBE_SENSOR_SUPPORTED, getKvp || getSoap);
        List<String> procedures = Lists.newArrayList(getCache().getProcedures());
        Collections.sort(procedures);
        model.put(SENSORS, procedures);
        if (this.dao.isPresent()) {
            model.put(PROCEDURE_FORMAT_MAP, this.dao.get().getProcedureFormatMap());
        }
        return new ModelAndView(ControllerConstants.Views.ADMIN_SENSOR_DESCRIPTIONS, model);
    }

    private boolean checkRequestOperator(OperationDecoderKey odk) {
        return requestOperatorRepository.get()
                .isActive(new RequestOperatorKey(odk.getService(), odk.getVersion(), odk.getOperation(), false));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NoImplementationFoundException.class)
    public String onError(NoImplementationFoundException e) {
        return String.format("No ProcedureFormatDAO implementation found!");
    }
}
