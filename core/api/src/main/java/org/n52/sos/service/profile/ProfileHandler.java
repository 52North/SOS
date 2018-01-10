/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.service.profile;

import java.util.Map;

import org.n52.faroe.ConfigurationError;
import org.n52.janmayen.lifecycle.Constructable;

/**
 * @since 4.0.0
 *
 */
//FIXME make this a interface
public abstract class ProfileHandler implements Constructable {

    @Deprecated
    private static ProfileHandler instance;

    @Override
    @Deprecated
    public void init() {
        ProfileHandler.instance = this;
    }

    /**
     * Gets the singleton instance of the ProfileHandler.
     * <p/>
     *
     * @return the profile handler
     *         <p/>
     * @throws ConfigurationError
     *             if no implementation can be found
     */
    @Deprecated
    public static ProfileHandler getInstance() throws ConfigurationError {
        return ProfileHandler.instance;
    }

    public abstract Profile getActiveProfile();

    public abstract Map<String, Profile> getAvailableProfiles();

    public abstract boolean isSetActiveProfile();
}
