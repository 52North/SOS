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
package org.n52.sos.request.operator;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.ds.AbstractDeleteSensorDAO;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.SensorDeletion;
import org.n52.sos.exception.ows.concrete.InvalidProcedureParameterException;
import org.n52.sos.exception.ows.concrete.MissingProcedureParameterException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.DeleteSensorRequest;
import org.n52.sos.response.DeleteSensorResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.wsdl.WSDLConstants;
import org.n52.sos.wsdl.WSDLOperation;

/**
 * @since 4.0.0
 *
 */
public class SosDeleteSensorOperatorV20 extends AbstractV2TransactionalRequestOperator<AbstractDeleteSensorDAO, DeleteSensorRequest, DeleteSensorResponse> {

    private static final String OPERATION_NAME = Sos2Constants.Operations.DeleteSensor.name();
    private static final Set<String> CONFORMANCE_CLASSES = Collections.singleton(ConformanceClasses.SOS_V2_SENSOR_DELETION);

    public SosDeleteSensorOperatorV20() {
        super(OPERATION_NAME, DeleteSensorRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public DeleteSensorResponse receive(DeleteSensorRequest request) throws OwsExceptionReport {
        DeleteSensorResponse response = getDao().deleteSensor(request);
        SosEventBus.fire(new SensorDeletion(request, response));
        return response;
    }

    @Override
    protected void checkParameters(DeleteSensorRequest request) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkServiceParameter(request.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(request);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureIdentifier(request.getProcedureIdentifier());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        exceptions.throwIfNotEmpty();
    }

    private void checkProcedureIdentifier(String procedureIdentifier) throws OwsExceptionReport {
        if (procedureIdentifier != null && !procedureIdentifier.isEmpty()) {
            if (!Configurator.getInstance().getCache().getProcedures().contains(procedureIdentifier)) {
                throw new InvalidProcedureParameterException(procedureIdentifier);
            }
        } else {
            throw new MissingProcedureParameterException();
        }
    }
    
    @Override
    public WSDLOperation getSosOperationDefinition() {
        return WSDLConstants.Operations.DELETE_SENSOR;
    }
}
