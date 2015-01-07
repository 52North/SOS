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
package org.n52.sos.service.admin.operator;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.n52.sos.exception.AdministratorException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.service.admin.AdministratorConstants.AdministratorParams;
import org.n52.sos.service.admin.request.AdminRequest;
import org.n52.sos.service.admin.request.operator.AdminRequestOperator;
import org.n52.sos.service.admin.request.operator.AdminRequestOperatorRepository;
import org.n52.sos.util.KvpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the different Listeners which are registered through the
 * config file. After parsing the request through the doOperation() method, the
 * request is send up to the specific Listener (e.g. GetCapabilitiesListener)
 * 
 * @since 4.0.0
 */
public class AdminServiceOperatorImpl extends AdminServiceOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceOperatorImpl.class);

    public AdminServiceOperatorImpl() {
        super();
    }

    @Override
    public ServiceResponse doGetOperation(HttpServletRequest req) throws AdministratorException, OwsExceptionReport {
        AdminRequest request;
        if (req.getParameterMap() == null || (req.getParameterMap() != null && req.getParameterMap().isEmpty())) {
            throw new MissingParameterValueException(OWSConstants.RequestParams.request);
        }
        Map<String, String> parameterValueMap = KvpHelper.getKvpParameterValueMap(req);
        request = getRequestFromValues(parameterValueMap);
        AdminRequestOperator requestOperator =
                AdminRequestOperatorRepository.getInstance().getAdminRequestOperator(request.getService());
        if (requestOperator != null) {
            return requestOperator.receiveRequest(request);
        }
        String exceptionText = "The service administrator is not supported!";
        throw new AdministratorException(exceptionText);
    }

    private AdminRequest getRequestFromValues(Map<String, String> kvp) throws AdministratorException {
        if (kvp.isEmpty()) {
            String exceptionText = "The request is empty!";
            LOGGER.debug(exceptionText);
            throw new AdministratorException(exceptionText);
        } else if (!kvp.isEmpty() && !kvp.containsKey(AdministratorParams.request.name())) {
            String exceptionText =
                    "The request does not contain mandatory '" + AdministratorParams.request.name() + "' parameter!";
            LOGGER.debug(exceptionText);
            throw new AdministratorException(exceptionText);
        } else if (!kvp.isEmpty() && !kvp.containsKey(AdministratorParams.service.name())) {
            String exceptionText =
                    "The request does not contain mandatory '" + AdministratorParams.service.name() + "' parameter!";
            LOGGER.debug(exceptionText);
            throw new AdministratorException(exceptionText);
        }
        AdminRequest request = new AdminRequest();
        request.setService(kvp.get(AdministratorParams.service.name()));
        request.setRequest(kvp.get(AdministratorParams.request.name()));
        request.setParameters(kvp.get(AdministratorParams.parameter.name()));
        return request;
    }
}
