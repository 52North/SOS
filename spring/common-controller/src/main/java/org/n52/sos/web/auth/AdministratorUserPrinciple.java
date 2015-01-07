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
package org.n52.sos.web.auth;

import java.io.Serializable;
import java.security.Principal;

import org.n52.sos.config.AdministratorUser;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class AdministratorUserPrinciple implements Principal, Serializable {
    private static final long serialVersionUID = 8178359938656526381L;

    private String username;

    private boolean defaultAdmin;

    AdministratorUserPrinciple(AdministratorUser user) {
        this(user, false);
    }

    AdministratorUserPrinciple(AdministratorUser user, boolean defaultAdmin) {
        this.username = user.getUsername();
        this.defaultAdmin = defaultAdmin;
    }

    @Override
    public String getName() {
        return this.username;
    }

    public boolean isDefaultAdmin() {
        return defaultAdmin;
    }
}
