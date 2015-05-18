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
package org.n52.iceland.util;

import org.n52.iceland.config.SettingsManager;
import org.n52.iceland.exception.ConfigurationException;

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
