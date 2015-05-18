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

import java.util.Set;

import org.n52.iceland.config.SettingsManager;
import org.n52.iceland.exception.ConfigurationException;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 * 
 */
public abstract class AbstractConfiguringServiceLoaderRepository<T> extends AbstractServiceLoaderRepository<T> {

    public AbstractConfiguringServiceLoaderRepository(Class<T> type, boolean failIfEmpty)
            throws ConfigurationException {
        super(type, failIfEmpty);
    }

    @Override
    protected final void processImplementations(Set<T> implementations) throws ConfigurationException {
        SettingsManager sm = SettingsManager.getInstance();
        for (T implementation : implementations) {
            sm.configure(implementation);
        }
        processConfiguredImplementations(implementations);
    }
    
    protected abstract void processConfiguredImplementations(Set<T> implementations) throws ConfigurationException;
}
