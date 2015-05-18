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
package org.n52.iceland.ds;

import java.util.Properties;

import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.util.Cleanupable;

/**
 * Interface for a connection provider that handles the connection to the
 * underlying data source (e.g. database, web service). Implementation can
 * contain a ConnectionPool.
 * 
 * @since 4.0.0
 */
public interface ConnectionProvider extends Cleanupable, ConnectionProviderIdentificator {

    /**
     * Get a data source connection
     * 
     * @return Connection to the data source
     * @throws ConnectionProviderException
     */
    Object getConnection() throws ConnectionProviderException;

    /**
     * Return the connection to the provider
     * 
     * @param connection
     *            Connection
     */
    void returnConnection(Object connection);

    /**
     * Initializes the connection provider.
     * 
     * @param properties
     *            the properties
     * 
     * @throws ConfigurationException
     *             if the initialization failed
     */
    void initialize(Properties properties) throws ConfigurationException;

}
