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

/**
 * Interface for users that are allowed to administer the SOS. Implementations
 * are {@link SettingsManager} specific.
 * <p/>
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public interface AdministratorUser {

    /**
     * Get the value of password
     * <p/>
     * 
     * @return the value of password
     */
    String getPassword();

    /**
     * Get the value of username
     * <p/>
     * 
     * @return the value of username
     */
    String getUsername();

    /**
     * Set the value of password
     * <p/>
     * 
     * @param password
     *            new value of password
     *            <p/>
     * @return this
     */
    AdministratorUser setPassword(String password);

    /**
     * Set the value of username
     * <p/>
     * 
     * @param username
     *            new value of username
     *            <p/>
     * @return this
     */
    AdministratorUser setUsername(String username);
}
