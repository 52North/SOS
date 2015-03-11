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
package org.n52.sos.config;

import java.util.ServiceLoader;
import java.util.Set;

import org.n52.sos.config.annotation.Configurable;

/**
 * Interface to declare dependencies to specific settings. This class should not
 * be implemented by classes that are loaded by the Service (e.g
 * {@link org.n52.sos.binding.Binding}s), as the will be instantiated before the
 * Configurator is present. {@code SettingDefinitionProvider} will be loaded
 * with the {@link ServiceLoader} interface. The setting will be injected in the
 * classes loaded by the service, that are annotated with the
 * <code>&#064;Configurable</code> annotation.
 * <p/>
 * 
 * @see Configurable
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public interface SettingDefinitionProvider {

    float ORDER_0 = 0;

    float ORDER_1 = 1;

    float ORDER_2 = 2;

    float ORDER_3 = 3;

    float ORDER_4 = 4;

    float ORDER_5 = 5;

    float ORDER_6 = 6;

    float ORDER_7 = 7;

    float ORDER_8 = 8;

    float ORDER_9 = 9;

    float ORDER_10 = 10;

    float ORDER_11 = 11;

    float ORDER_12 = 12;

    float ORDER_13 = 13;

    float ORDER_14 = 14;

    float ORDER_15 = 15;

    float ORDER_16 = 16;

    float ORDER_17 = 17;

    float ORDER_18 = 18;

    float ORDER_19 = 19;

    /**
     * @return the declared setting definitons of this provider
     */
    Set<SettingDefinition<?, ?>> getSettingDefinitions();
}
