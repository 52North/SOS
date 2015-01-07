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

import org.n52.sos.ds.AbstractDescribeSensorDAO;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.http.MediaType;

/**
 * class handles the DescribeSensor request
 * 
 * @since 4.0.0
 */
public class SosDescribeSensorOperatorV100 extends
        AbstractV1RequestOperator<AbstractDescribeSensorDAO, DescribeSensorRequest, DescribeSensorResponse> {

    private static final String OPERATION_NAME = SosConstants.Operations.DescribeSensor.name();

    // TODO necessary in SOS 1.0.0, different value?
    private static final Set<String> CONFORMANCE_CLASSES = Collections
            .singleton("http://www.opengis.net/spec/SOS/1.0/conf/core");

    public SosDescribeSensorOperatorV100() {
        super(OPERATION_NAME, DescribeSensorRequest.class);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public DescribeSensorResponse receive(DescribeSensorRequest sosRequest) throws OwsExceptionReport {
        DescribeSensorResponse response = getDao().getSensorDescription(sosRequest);
        response.setOutputFormat(MediaType.normalizeString(sosRequest.getProcedureDescriptionFormat()));
        return response;
    }

    @Override
    protected void checkParameters(DescribeSensorRequest sosRequest) throws OwsExceptionReport {
        CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkServiceParameter(sosRequest.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(sosRequest);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureID(sosRequest.getProcedure(), SosConstants.DescribeSensorParams.procedure.name());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkProcedureDescriptionFromat(sosRequest.getProcedureDescriptionFormat(), sosRequest);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        // TODO necessary in SOS 1.0.0, different value?
        // if (sosRequest.getTime() != null && !sosRequest.getTime().isEmpty())
        // {
        // String exceptionText =
        // "The requested parameter is not supported by this server!";
        // exceptions.add(Util4Exceptions.createOptionNotSupportedException(Sos2Constants.DescribeSensorParams.validTime.name(),
        // exceptionText));
        // }
        exceptions.throwIfNotEmpty();
    }
    
    private void checkProcedureDescriptionFromat(String procedureDescriptionFormat, DescribeSensorRequest sosRequest) throws MissingParameterValueException, OwsExceptionReport {
        if (!checkOnlyRequestableProcedureDescriptionFromats(sosRequest.getProcedureDescriptionFormat(), Sos1Constants.DescribeSensorParams.outputFormat)) {
            SosHelper.checkOutputFormat(MediaType.normalizeString(sosRequest.getProcedureDescriptionFormat()),
                    sosRequest.getService(), sosRequest.getVersion());
        }
    }

}
