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

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.n52.sos.ds.ConnectionProvider;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.hibernate.SessionFactoryProvider;
import org.n52.sos.service.Configurator;
import org.n52.sos.web.AbstractController;
import org.n52.sos.web.ControllerConstants;

/**
 * @since 4.0.0
 *
 */
@Controller
@RequestMapping({ ControllerConstants.Paths.ADMIN_DATABASE_UPDATE_SCRIPT })
public class AdminDatasourceUpdateScriptController extends AbstractController {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(AdminDatasourceUpdateScriptController.class);

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getChangeScript() throws ConnectionProviderException, SQLException {
        ConnectionProvider connectionProvider = Configurator.getInstance().getDataConnectionProvider();
        if (connectionProvider instanceof SessionFactoryProvider) {
            SessionFactoryProvider sessionFactoryProvider = (SessionFactoryProvider) connectionProvider;
            String updateScript = sessionFactoryProvider.getUpdateScript();
            if (updateScript.isEmpty()) {
                return "The database is current with the data model. No updates necessary.";
            } else {
                return sessionFactoryProvider.getUpdateScript();
            }
        } else {
            return "Couldn't generate update script.";
        }
    }
}
