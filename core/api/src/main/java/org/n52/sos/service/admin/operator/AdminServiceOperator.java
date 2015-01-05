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

import javax.servlet.http.HttpServletRequest;

import org.n52.sos.exception.AdministratorException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;

/**
 * Abstract operator class for the administration interface
 * 
 * @since 4.0.0
 */
public abstract class AdminServiceOperator {
    /**
     * URL pattern for the administration interface
     */
    private static final String URL_PATTERN = "/admin";

    /**
     * HTTP-Get request handling method
     * 
     * @param request
     *            HTTP-Get request
     * @throws OwsExceptionReport
     */
    public abstract ServiceResponse doGetOperation(HttpServletRequest request) throws AdministratorException,
            OwsExceptionReport;

    /**
     * Get URL pattern for the administration interface
     * 
     * @return URL pattern
     */
    public String getUrlPattern() {
        return URL_PATTERN;
    }
}
