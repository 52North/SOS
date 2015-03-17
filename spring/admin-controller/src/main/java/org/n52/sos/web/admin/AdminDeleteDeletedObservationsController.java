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

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.n52.sos.ds.DeleteDeletedObservationDAO;
import org.n52.sos.exception.ows.concrete.NoImplementationFoundException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.ServiceLoaderHelper;
import org.n52.sos.web.ControllerConstants;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
@Controller
@RequestMapping(value = ControllerConstants.Paths.ADMIN_DATABASE_DELETE_DELETED_OBSERVATIONS)
public class AdminDeleteDeletedObservationsController extends AbstractAdminController {
    private DeleteDeletedObservationDAO dao;

    private DeleteDeletedObservationDAO getDAO() throws NoImplementationFoundException {
        if (this.dao == null) {
            this.dao = ServiceLoaderHelper.loadImplementation(DeleteDeletedObservationDAO.class);
        }
        return this.dao;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NoImplementationFoundException.class)
    public String onError(NoImplementationFoundException e) {
        return "The operation is not supported by this SOS";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete() throws NoImplementationFoundException, OwsExceptionReport {
        getDAO().deleteDeletedObservations();
    }
}
