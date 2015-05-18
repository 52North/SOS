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
package org.n52.iceland.config;

import java.util.ServiceLoader;
import java.util.Set;

import org.n52.iceland.config.annotation.Configurable;

/**
 * Interface to declare dependencies to specific settings. This class should not
 * be implemented by classes that are loaded by the Service (e.g
 * {@link org.n52.iceland.binding.Binding}s), as the will be instantiated before the
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
