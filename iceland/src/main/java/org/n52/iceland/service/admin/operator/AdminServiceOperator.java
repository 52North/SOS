/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.service.admin.operator;

import javax.servlet.http.HttpServletRequest;

import org.n52.iceland.exception.AdministratorException;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.response.ServiceResponse;

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
