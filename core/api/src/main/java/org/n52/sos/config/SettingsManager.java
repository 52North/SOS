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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.binding.BindingKey;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.encode.ProcedureDescriptionFormatKey;
import org.n52.sos.encode.ResponseFormatKey;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesKey;
import org.n52.sos.ogc.swes.OfferingExtensionKey;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;
import org.n52.sos.util.Comparables;
import org.n52.sos.util.ConfiguringSingletonServiceLoader;

/**
 * Class to handle the settings and configuration of the SOS. Allows other
 * classes to change, delete, and declare settings and to create, modify and
 * delete administrator users. {@code SettingDefinition} are loaded from
 * {@link SettingDefinitionProvider} by the Java {@link ServiceLoader}
 * interface. Classes can subscribe to specific settings using the
 * {@code Configurable} and {@code Setting} annotations. To be recognized by the
 * SettingsManager {@link #configure(java.lang.Object)} has to be called for
 * every object that wants to recieve settings. This is automatically done for
 * all classes loaded by the {@link Configurator}. All other classes have to
 * call {@code configure(java.lang.Object)} manually.
 * <p/>
 *
 * @see AdministratorUser
 * @see SettingDefinition
 * @see SettingDefinitionProvider
 * @see SettingValue
 * @see Configurable
 * @see ConfiguringSingletonServiceLoader
 * @see AbstractConfiguringServiceLoaderRepository
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class SettingsManager implements CapabilitiesExtensionManager{

    private static final Logger LOG = LoggerFactory.getLogger(SettingsManager.class);

    private static final ReentrantLock creationLock = new ReentrantLock();

    private static SettingsManager instance;

    /**
     * Gets the singleton instance of the SettingsManager.
     * <p/>
     *
     * @return the settings manager
     *         <p/>
     * @throws ConfigurationException
     *             if no implementation can be found
     */
    public static SettingsManager getInstance() throws ConfigurationException {
        if (instance == null) {
            creationLock.lock();
            try {
                if (instance == null) {
                    instance = createInstance();
                }
            } finally {
                creationLock.unlock();
            }
        }
        return instance;
    }

    /**
     * Creates a new {@code SettingsManager} with the {@link ServiceLoader}
     * interface.
     * <p/>
     *
     * @return the implementation
     *         <p/>
     * @throws ConfigurationException
     *             if no implementation can be found
     */
    private static SettingsManager createInstance() throws ConfigurationException {
        List<SettingsManager> settingsManagers = new LinkedList<SettingsManager>();
        Iterator<SettingsManager> it = ServiceLoader.load(SettingsManager.class).iterator();
        while(it.hasNext()) {
            try {
                settingsManagers.add(it.next());
            } catch (ServiceConfigurationError e) {
                LOG.error("Could not instantiate SettingsManager", e);
            }
        }
        try {
            return Comparables.inheritance().min(settingsManagers);
        } catch (NoSuchElementException e) {
            throw new ConfigurationException("No SettingsManager implementation loaded", e);
        }
    }


    /**
     * Configure {@code o} with the required settings. All changes to a setting
     * required by the object will be applied.
     * <p/>
     *
     * @param o
     *            the object to configure
     *            <p/>
     * @throws ConfigurationException
     *             if there is a problem configuring the object
     * @see Configurable
     * @see Setting
     */
    public abstract void configure(Object o) throws ConfigurationException;

    /**
     * Get the definition that is defined with the specified key.
     * <p/>
     *
     * @param key
     *            the key
     *            <p/>
     * @return the definition or {@code null} if there is no definition for the
     *         key
     */
    public abstract SettingDefinition<?, ?> getDefinitionByKey(String key);

    /**
     * Gets all {@code SettingDefinition}s known by this class.
     * <p/>
     *
     * @return the definitions
     */
    public abstract Set<SettingDefinition<?, ?>> getSettingDefinitions();

    /**
     * Gets the value of the setting defined by {@code key}.
     * <p/>
     *
     * @param <T>
     *            the type of the setting and value
     * @param key
     *            the definition of the setting
     *            <p/>
     * @return the value of the setting
     *         <p/>
     * @throws ConnectionProviderException
     */
    public abstract <T> SettingValue<T> getSetting(SettingDefinition<?, T> key) throws ConnectionProviderException;

    /**
     * Gets all values for all definitions. If there is no value for a
     * definition {@code null} is added to the map.
     * <p/>
     *
     * @return all values by definition
     *         <p/>
     * @throws ConnectionProviderException
     */
    public abstract Map<SettingDefinition<?, ?>, SettingValue<?>> getSettings() throws ConnectionProviderException;

    /**
     * Deletes the setting defined by {@code setting}.
     * <p/>
     *
     * @param setting
     *            the definition
     *            <p/>
     * @throws ConfigurationException
     *             if there is a problem deleting the setting
     * @throws ConnectionProviderException
     */
    public abstract void deleteSetting(SettingDefinition<?, ?> setting) throws ConfigurationException,
            ConnectionProviderException;

    /**
     * Changes a setting. The change is propagated to all Objects that are
     * configured. If the change fails for one of these objects, the setting is
     * reverted to the old value of the setting for all objects.
     * <p/>
     *
     * @param value
     *            the new value of the setting
     *            <p/>
     * @throws ConfigurationException
     *             if there is a problem changing the setting.
     * @throws ConnectionProviderException
     */
    public abstract void changeSetting(SettingValue<?> value) throws ConfigurationException,
            ConnectionProviderException;

    /**
     * @return the {@link SettingValueFactory} to produce values
     */
    public abstract SettingValueFactory getSettingFactory();

    /**
     * Gets all registered administrator users.
     *
     * @return the users
     *
     * @throws ConnectionProviderException
     */
    public abstract Set<AdministratorUser> getAdminUsers() throws ConnectionProviderException;

    /**
     * Gets the administrator user with the specified user name.
     *
     * @param username
     *            the username
     *
     * @return the administrator user or {@code null} if no user with the
     *         specified name exists
     *
     * @throws ConnectionProviderException
     */
    public abstract AdministratorUser getAdminUser(String username) throws ConnectionProviderException;

    /**
     * Checks if a administrator user exists.
     *
     * @return {@code true} if there is a admin user, otherwise {@code false}.
     *
     * @throws ConnectionProviderException
     */
    public abstract boolean hasAdminUser() throws ConnectionProviderException;

    /**
     * Creates a new {@code AdministratorUser}. This method will fail if the
     * username is already used by another user.
     * <p/>
     *
     * @param username
     *            the proposed username
     * @param password
     *            the proposed (hashed) password
     *            <p/>
     * @return the created user
     *         <p/>
     * @throws ConnectionProviderException
     */
    public abstract AdministratorUser createAdminUser(String username, String password)
            throws ConnectionProviderException;

    /**
     * Saves a user previously returned by
     * {@link #getAdminUser(java.lang.String)} or {@link #getAdminUsers()}.
     * <p/>
     *
     * @param user
     *            the user to change
     *            <p/>
     * @throws ConnectionProviderException
     */
    public abstract void saveAdminUser(AdministratorUser user) throws ConnectionProviderException;

    /**
     * Deletes the user with the specified username.
     * <p/>
     *
     * @param username
     *            the username
     *            <p/>
     * @throws ConnectionProviderException
     */
    public abstract void deleteAdminUser(String username) throws ConnectionProviderException;

    /**
     * Deletes the user previously returned by
     * {@link #getAdminUser(java.lang.String)} or {@link #getAdminUsers()}.
     * <p/>
     *
     * @param user
     *            <p/>
     * @throws ConnectionProviderException
     */
    public abstract void deleteAdminUser(AdministratorUser user) throws ConnectionProviderException;

    /**
     * Deletes all settings and users.
     * <p/>
     *
     * @throws ConnectionProviderException
     */
    public abstract void deleteAll() throws ConnectionProviderException;

    /**
     * Clean up this SettingsManager. All subsequent calls to this class are
     * undefined.
     */
    public abstract void cleanup();

    /**
     * Returns if a operation is active and should be offered by this SOS.
     * <p/>
     *
     * @param rokt
     *            the key identifying the operation
     *            <p/>
     * @return {@code true} if the operation is active in this SOS
     *         <p/>
     * @throws ConnectionProviderException
     */
    public abstract boolean isActive(RequestOperatorKey rokt) throws ConnectionProviderException;

    /**
     * Checks if the response format is active for the specified service and
     * version.
     *
     * @param rfkt
     *            the service/version/responseFormat combination
     *
     * @return if the format is active
     *
     * @throws ConnectionProviderException
     */
    public abstract boolean isActive(ResponseFormatKey rfkt) throws ConnectionProviderException;

    /**
     * Checks if the procedure description format is active for the specified
     * service and version.
     *
     * @param pdfkt
     *            the service/version/procedure description combination
     *
     * @return if the format is active
     *
     * @throws ConnectionProviderException
     */
    public abstract boolean isActive(ProcedureDescriptionFormatKey pdfkt) throws ConnectionProviderException;

    /**
     * Sets the status of an operation.
     * <p/>
     *
     * @param rokt
     *            the key identifying the operation
     * @param active
     *            whether the operation is active or not
     *            <p/>
     * @throws ConnectionProviderException
     */
    public abstract void setActive(RequestOperatorKey rokt, boolean active) throws ConnectionProviderException;

    /**
     * Sets the status of a response format for the specified service and
     * version.
     *
     * @param rfkt
     *            the service/version/responseFormat combination
     * @param active
     *            the status
     *
     * @throws ConnectionProviderException
     */
    public abstract void setActive(ResponseFormatKey rfkt, boolean active) throws ConnectionProviderException;

    /**
     * Sets the status of a procedure description format for the specified
     * service and version.
     *
     * @param pdfkt
     *            the service/version/procedure description combination
     * @param active
     *            the status
     *
     * @throws ConnectionProviderException
     */
    public abstract void setActive(ProcedureDescriptionFormatKey pdfkt, boolean active)
            throws ConnectionProviderException;

    /**
     * Sets the status of a binding.
     *
     * @param bk
     *            the binding
     * @param active
     *            the status
     *
     * @throws ConnectionProviderException
     */
    public abstract void setActive(BindingKey bk, boolean active) throws ConnectionProviderException;

    /**
     * Checks if the binding is active.
     *
     * @param bk
     *            the binding
     *
     * @return if the binding is active
     *
     * @throws ConnectionProviderException
     */
    public abstract boolean isActive(BindingKey bk) throws ConnectionProviderException;

    /**
     * Checks if the offering extension is active.
     *
     * @param oek
     *            the offering extension key
     *
     * @return if the offering extension is active
     *
     * @throws ConnectionProviderException
     */
    public abstract boolean isActive(OfferingExtensionKey oek) throws ConnectionProviderException;

    /**
     * Sets the status of a offering extension.
     *
     * @param oek
     *            the offering extension
     * @param active
     *            the status
     *
     * @throws ConnectionProviderException
     */
    public abstract void setActive(OfferingExtensionKey oek, boolean active) throws ConnectionProviderException;

    /**
     * Sets the status of a offering extension.
     *
     * @param oek
     *            the offering extension
     * @param active
     *            the status
     *  @param updateRepository
     *            indicator if the repository should be updated
     *
     * @throws ConnectionProviderException
     */
    public abstract void setActive(OfferingExtensionKey oek, boolean active, boolean updateRepository) throws ConnectionProviderException;

    /**
     * Checks if the extended capabilities is active.
     *
     * @param oeck
     *            the extended capabilities key
     *
     * @return if the extended capabilities is active
     *
     * @throws ConnectionProviderException
     */
    public abstract boolean isActive(OwsExtendedCapabilitiesKey oeck) throws ConnectionProviderException;

    /**
     * Sets the status of a extended capabilities.
     *
     * @param oeck
     *            the extended capabilities
     * @param active
     *            the status
     *
     * @throws ConnectionProviderException
     */
    public abstract void setActive(OwsExtendedCapabilitiesKey oeck, boolean active) throws ConnectionProviderException;

    /**
     * Sets the status of a extended capabilities.
     *
     * @param oeck
     *            the extended capabilities
     * @param active
     *            the status
     *  @param updateRepository
     *            indicator if the repository should be updated
     *
     * @throws ConnectionProviderException
     */
    public abstract void setActive(OwsExtendedCapabilitiesKey oeck, boolean active, boolean updateRepository)  throws ConnectionProviderException;

}
