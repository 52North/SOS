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
package org.n52.sos.util;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.exception.ConfigurationException;

/**
 * @since 4.0.0
 * 
 * @param <T>
 */
public class ConfiguringSingletonServiceLoader<T> extends SingletonServiceLoader<T> {
    public static <T> T loadAndConfigure(Class<? extends T> t, boolean required) {
        return new ConfiguringSingletonServiceLoader<T>(t, required).get();
    }

    public static <T> T loadAndConfigure(Class<? extends T> t, boolean required, T defaultImplementation) {
        return new ConfiguringSingletonServiceLoader<T>(t, required, defaultImplementation).get();
    }
    
    public static <T> T loadAndConfigure(Class<? extends T> t, boolean required, String identification) {
        return new ConfiguringSingletonServiceLoader<T>(t, required).get(identification);
    }

    public static <T> T loadAndConfigure(Class<? extends T> t, boolean required, T defaultImplementation, String identification) {
        return new ConfiguringSingletonServiceLoader<T>(t, required, defaultImplementation).get(identification);
    }

    public ConfiguringSingletonServiceLoader(Class<? extends T> c, boolean failIfNotFound) {
        super(c, failIfNotFound);
    }

    public ConfiguringSingletonServiceLoader(Class<? extends T> c, boolean failIfNotFound, T defaultImplementation) {
        super(c, failIfNotFound, defaultImplementation);
    }

    @Override
    protected void processImplementation(T implementation) throws ConfigurationException {
        super.processImplementation(implementation);
        SettingsManager.getInstance().configure(implementation);
    }
}
