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

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.n52.sos.binding.BindingKey;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.encode.ProcedureDescriptionFormatKey;
import org.n52.sos.encode.ResponseFormatKey;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.SettingsChangeEvent;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesKey;
import org.n52.sos.ogc.ows.OwsExtendedCapabilitiesRepository;
import org.n52.sos.ogc.swes.OfferingExtensionKey;
import org.n52.sos.ogc.swes.OfferingExtensionRepository;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceSettings;
import org.n52.sos.util.HashSetMultiMap;
import org.n52.sos.util.SetMultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract {@code SettingsManaeger} implementation that handles the loading of
 * {@link SettingDefinition}s and the configuration of objects.
 * <p/>
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public abstract class AbstractSettingsManager extends SettingsManager {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSettingsManager.class);

    private final SettingDefinitionProviderRepository settingDefinitionRepository;

    private final SetMultiMap<String, ConfigurableObject> configurableObjects =
            new HashSetMultiMap<String, ConfigurableObject>();

    private final ReadWriteLock configurableObjectsLock = new ReentrantReadWriteLock();

    /**
     * Constructs a new instance.
     * <p/>
     * 
     * @throws ConfigurationException
     *             if loading of {@link SettingDefinitionProvider} fails
     */
    protected AbstractSettingsManager() throws ConfigurationException {
        settingDefinitionRepository = new SettingDefinitionProviderRepository();
    }

    /**
     * @return the repository holding the setting definitions
     */
    protected SettingDefinitionProviderRepository getSettingDefinitionRepository() {
        return settingDefinitionRepository;
    }

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return getSettingDefinitionRepository().getSettingDefinitions();
    }

    /**
     * @return the keys for all definiions
     */
    public Set<String> getKeys() {
        Set<SettingDefinition<?, ?>> settings = getSettingDefinitions();
        HashSet<String> keys = new HashSet<String>(settings.size());
        for (SettingDefinition<?, ?> setting : settings) {
            keys.add(setting.getKey());
        }
        return keys;
    }

    @Override
    public void changeSetting(SettingValue<?> newValue) throws ConfigurationException, ConnectionProviderException {
        if (newValue == null) {
            throw new NullPointerException("newValue can not be null");
        }
        if (newValue.getKey() == null) {
            throw new NullPointerException("newValue.key can not be null");
        }
        SettingDefinition<?, ?> def = getDefinitionByKey(newValue.getKey());

        if (def == null) {
            throw new IllegalArgumentException("newValue.key is invalid");
        }

        if (def.getType() != newValue.getType()) {
            throw new IllegalArgumentException(String.format("Invalid type for definition (%s vs. %s)", def.getType(),
                    newValue.getType()));
        }

        SettingValue<?> oldValue = getSettingValue(newValue.getKey());
        if (oldValue == null || !oldValue.equals(newValue)) {
            applySetting(def, oldValue, newValue);
            saveSettingValue(newValue);
            SosEventBus.fire(new SettingsChangeEvent(def, oldValue, newValue));
        }
    }

    @Override
    public void deleteSetting(SettingDefinition<?, ?> setting) throws ConfigurationException,
            ConnectionProviderException {
        SettingValue<?> oldValue = getSettingValue(setting.getKey());
        if (oldValue != null) {
            applySetting(setting, oldValue, null);
            deleteSettingValue(setting.getKey());
            SosEventBus.fire(new SettingsChangeEvent(setting, oldValue, null));
        }
    }

    /**
     * Applies the a new setting to all {@code ConfiguredObject}s. If an error
     * occurs the the old value is reapplied.
     * <p/>
     * 
     * @param setting
     *            the definition
     * @param oldValue
     *            the old value (or {@code null} if there is none)
     * @param newValue
     *            the new value (or {@code null} if there is none)
     *            <p/>
     * @throws ConfigurationException
     *             if there is a error configuring the objects
     */
    private void applySetting(SettingDefinition<?, ?> setting, SettingValue<?> oldValue, SettingValue<?> newValue)
            throws ConfigurationException {
        LinkedList<ConfigurableObject> changed = new LinkedList<ConfigurableObject>();
        ConfigurationException e = null;
        configurableObjectsLock.readLock().lock();
        try {
            Set<ConfigurableObject> cos = configurableObjects.get(setting.getKey());
            if (cos != null) {
                for (ConfigurableObject co : cos) {
                    try {
                        co.configure(newValue.getValue());
                    } catch (ConfigurationException ce) {
                        e = ce;
                        break;
                    } finally {
                        changed.add(co);
                    }
                }
                if (e != null) {
                    LOG.debug("Reverting setting...");
                    for (ConfigurableObject co : changed) {
                        try {
                            co.configure(oldValue.getValue());
                        } catch (ConfigurationException ce) {
                            /* there is nothing we can do... */
                            LOG.error("Error reverting setting!", ce);
                        }
                    }
                    throw e;
                }
            }
        } finally {
            configurableObjectsLock.readLock().unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> SettingValue<T> getSetting(SettingDefinition<?, T> key) throws ConnectionProviderException {
        return (SettingValue<T>) getSettingValue(key.getKey());
    }

    @Override
    public Map<SettingDefinition<?, ?>, SettingValue<?>> getSettings() throws ConnectionProviderException {
        Set<SettingValue<?>> values = getSettingValues();
        Map<SettingDefinition<?, ?>, SettingValue<?>> settingsByDefinition =
                new HashMap<SettingDefinition<?, ?>, SettingValue<?>>(values.size());
        for (SettingValue<?> value : values) {
            final SettingDefinition<?, ?> definition = getSettingDefinitionRepository().getDefinition(value.getKey());
            if (definition == null) {
                LOG.warn("No definition for '{}' found.", value.getKey());
            } else {
                settingsByDefinition.put(definition, value);
            }
        }
        HashSet<SettingDefinition<?, ?>> nullValues = new HashSet<SettingDefinition<?, ?>>(getSettingDefinitions());
        nullValues.removeAll(settingsByDefinition.keySet());
        for (SettingDefinition<?, ?> s : nullValues) {
            if (s.hasDefaultValue()) {
                settingsByDefinition.put(s, getSettingFactory().newSettingValue(s, s.getDefaultValue().toString()));
            } else {
                LOG.warn("No value or default value for '{}' found; using null.", s.getKey());
                settingsByDefinition.put(s, getSettingFactory().newSettingValue(s, null));
            }
        }
        return settingsByDefinition;
    }

    @Override
    public void deleteAdminUser(AdministratorUser user) throws ConnectionProviderException {
        deleteAdminUser(user.getUsername());
    }

    @Override
    public boolean hasAdminUser() throws ConnectionProviderException {
        return !getAdminUsers().isEmpty();
    }

    @Override
    public void configure(Object object) throws ConfigurationException {
        LOG.debug("Configuring {}", object);
        Class<?> clazz = object.getClass();
        Configurable configurable = clazz.getAnnotation(Configurable.class);
        if (configurable == null) {
            return;
        }

        for (Method method : clazz.getMethods()) {
            Setting s = method.getAnnotation(Setting.class);

            if (s != null) {
                String key = s.value();
                if (key == null || key.isEmpty()) {
                    throw new ConfigurationException(String.format("Invalid value for @Setting: '%s'", key));
                }
                if (getSettingDefinitionRepository().getDefinition(key) == null) {
                    throw new ConfigurationException(String.format("No SettingDefinition found for key %s", key));
                }
                if (method.getParameterTypes().length != 1) {
                    throw new ConfigurationException(String.format(
                            "Method %s annotated with @Setting in %s has a invalid method signature", method, clazz));
                }
                if (!Modifier.isPublic(method.getModifiers())) {
                    throw new ConfigurationException(String.format(
                            "Non-public method %s annotated with @Setting in %s", method, clazz));
                }
                configure(new ConfigurableObject(method, object, key));
            }
        }
    }

    private void configure(ConfigurableObject co) throws ConfigurationException {
        LOG.debug("Configuring {}", co);
        configurableObjectsLock.writeLock().lock();
        try {
            configurableObjects.add(co.getKey(), co);
        } finally {
            configurableObjectsLock.writeLock().unlock();
        }
        try {
            co.configure(getNotNullSettingValue(co));
        } catch (ConnectionProviderException cpe) {
            throw new ConfigurationException("Exception configuring " + co.getKey(), cpe);
        } catch (RuntimeException re) {
            throw new ConfigurationException("Exception configuring " + co.getKey(), re);
        }

    }

    @Override
    public SettingDefinition<?, ?> getDefinitionByKey(String key) {
        return getSettingDefinitionRepository().getDefinition(key);
    }

    @SuppressWarnings("unchecked")
    private SettingValue<Object> getNotNullSettingValue(ConfigurableObject co) throws ConnectionProviderException,
            ConfigurationException {
        SettingValue<Object> val = (SettingValue<Object>) getSettingValue(co.getKey());
        if (val == null) {
            SettingDefinition<?, ?> def = getDefinitionByKey(co.getKey());
            if (def == null) {
                throw new ConfigurationException(String.format("No SettingDefinition found for key %s", co.getKey()));
            }
            val = (SettingValue<Object>) getSettingFactory().newSettingValue(def, null);
            if (def.isOptional()) {
                LOG.debug("No value found for optional setting {}", co.getKey());
                saveSettingValue(val);
            } else if (def.hasDefaultValue()) {
                LOG.debug("Using default value '{}' for required setting {}", def.getDefaultValue(), co.getKey());
                saveSettingValue(val.setValue(def.getDefaultValue()));
            } else if (def.equals(ServiceSettings.SERVICE_URL_DEFINITION)) {
                saveSettingValue(val.setValue(URI.create("http://localhost:8080/52n-sos-webapp/sos")));
            } else {
                throw new ConfigurationException(String.format(
                        "No value found for required Setting '%s' with no default value.", co.getKey()));
            }
        }
        return val;
    }

    @Override
    public void setActive(RequestOperatorKey rokt, boolean active) throws ConnectionProviderException {
        LOG.debug("Setting status of {} to {}", rokt, active);
        setOperationStatus(rokt, active);
        if (Configurator.getInstance() != null) {
            RequestOperatorRepository.getInstance().setActive(rokt, active);
        }
    }

    @Override
    public void setActive(ResponseFormatKey rfkt, boolean active) throws ConnectionProviderException {
        LOG.debug("Setting status of {} to {}", rfkt, active);
        setResponseFormatStatus(rfkt, active);
        if (Configurator.getInstance() != null) {
            CodingRepository.getInstance().setActive(rfkt, active);
        }
    }

    @Override
    public void setActive(ProcedureDescriptionFormatKey pdfkt, boolean active) throws ConnectionProviderException {
        LOG.debug("Setting status of {} to {}", pdfkt, active);
        setProcedureDescriptionFormatStatus(pdfkt, active);
        if (Configurator.getInstance() != null) {
            CodingRepository.getInstance().setActive(pdfkt, active);
        }
    }

    @Override
    public void setActive(BindingKey bk, boolean active) throws ConnectionProviderException {
        LOG.debug("Setting status of {} to {}", bk, active);
        setBindingStatus(bk, active);
        if (Configurator.getInstance() != null) {
            BindingRepository.getInstance().setActive(bk, active);
        }
    }
    
    @Override
    public void setActive(OfferingExtensionKey oek, boolean active) throws ConnectionProviderException {
        setActive(oek, active, true);
    }
    
    @Override
    public void setActive(OfferingExtensionKey oek, boolean active, boolean updateRepository) throws ConnectionProviderException {
        LOG.debug("Setting status of {} to {}", oek, active);
        setOfferingExtensionStatus(oek, active);
        if (updateRepository && OfferingExtensionRepository.getInstance() != null) {
            OfferingExtensionRepository.getInstance().setActive(oek, active);
        }
    }

    @Override
    public void setActive(OwsExtendedCapabilitiesKey oeck, boolean active) throws ConnectionProviderException {
        setActive(oeck, active, true);
    }
    
    @Override
    public void setActive(OwsExtendedCapabilitiesKey oeck, boolean active, boolean updateRepository) throws ConnectionProviderException {
        LOG.debug("Setting status of {} to {}", oeck, active);
        setOwsExtendedCapabilitiesStatus(oeck, active);
        if (updateRepository && OwsExtendedCapabilitiesRepository.getInstance() != null) {
            OwsExtendedCapabilitiesRepository.getInstance().setActive(oeck, active);
        }
    }
    
    /**
     * @return all saved setting values
     * 
     * @throws ConnectionProviderException
     */
    protected abstract Set<SettingValue<?>> getSettingValues() throws ConnectionProviderException;

    /**
     * Returns the value of the specified setting or {@code null} if it does not
     * exist.
     * <p/>
     * 
     * @param key
     *            the key
     *            <p/>
     * @return the value
     * 
     * @throws ConnectionProviderException
     */
    protected abstract SettingValue<?> getSettingValue(String key) throws ConnectionProviderException;

    /**
     * Deletes the setting with the specified key.
     * <p/>
     * 
     * @param key
     *            the key
     * 
     * @throws ConnectionProviderException
     */
    protected abstract void deleteSettingValue(String key) throws ConnectionProviderException;

    /**
     * Saves the setting value.
     * <p/>
     * 
     * @param setting
     *            the value
     * 
     * @throws ConnectionProviderException
     */
    protected abstract void saveSettingValue(SettingValue<?> setting) throws ConnectionProviderException;

    /**
     * Sets the status of an operation.
     * <p/>
     * 
     * @param requestOperatorKeyType
     *            the key identifying the operation
     * @param active
     *            whether the operation is active or not
     *            <p/>
     * @throws ConnectionProviderException
     * @see #setActive(RequestOperatorKey, boolean)
     */
    protected abstract void setOperationStatus(RequestOperatorKey requestOperatorKeyType, boolean active)
            throws ConnectionProviderException;

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
     * @see #setActive(ResponseFormatKey, boolean)
     */
    protected abstract void setResponseFormatStatus(ResponseFormatKey rfkt, boolean active)
            throws ConnectionProviderException;

    /**
     * Sets the status of a response format for the specified service and
     * version.
     * 
     * @param pdfkt
     *            the service/version/responseFormat combination
     * @param active
     *            the status
     * 
     * @throws ConnectionProviderException
     * @see #setActive(ProcedureDescriptionFormatKey, boolean)
     */
    protected abstract void setProcedureDescriptionFormatStatus(ProcedureDescriptionFormatKey pdfkt, boolean active)
            throws ConnectionProviderException;

    /**
     * Sets the status of a procedure description format for the specified
     * service and version.
     * 
     * @param pdf
     *            the procedure description format
     * @param active
     *            the status
     * 
     * @throws ConnectionProviderException
     * @see #setProcedureDescriptionFormatStatus(ProcedureDescriptionFormatKey,
     *      boolean)
     */
    @Deprecated
    protected abstract void setProcedureDescriptionFormatStatus(String pdf, boolean active)
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
     * @see #setActive(org.n52.sos.binding.BindingKey, boolean)
     */
    protected abstract void setBindingStatus(BindingKey bk, boolean active) throws ConnectionProviderException;
    
    protected abstract void setOfferingExtensionStatus(OfferingExtensionKey oek, boolean active) throws ConnectionProviderException;
    
    protected abstract void setOwsExtendedCapabilitiesStatus(OwsExtendedCapabilitiesKey oeck, boolean active) throws ConnectionProviderException;

    private class ConfigurableObject {
        private final Method method;

        private final WeakReference<Object> target;

        private final String key;

        /**
         * Constructs a new {@code ConfigurableObject}
         * <p/>
         * 
         * @param method
         *            the method of the target
         * @param target
         *            the target object
         * @param key
         *            the settings key
         */
        ConfigurableObject(Method method, Object target, String key) {
            this.method = method;
            this.target = new WeakReference<Object>(target);
            this.key = key;
        }

        /**
         * @return the method
         */
        public Method getMethod() {
            return method;
        }

        /**
         * @return the target object
         */
        public WeakReference<Object> getTarget() {
            return target;
        }

        /**
         * @return the settings key
         */
        public String getKey() {
            return key;
        }

        /**
         * Configures this object with the specified value.
         * <p/>
         * 
         * @param val
         *            the value
         *            <p/>
         * @throws ConfigurationException
         *             if an error occurs
         */
        public void configure(SettingValue<?> val) throws ConfigurationException {
            configure(val.getValue());
        }

        /**
         * Configures this object with the specified value. Exceptions are
         * wrapped in a {@code ConfigurationException}.
         * <p/>
         * 
         * @param val
         *            the value
         *            <p/>
         * @throws ConfigurationException
         *             if an error occurs
         */
        public void configure(Object val) throws ConfigurationException {

            try {
                if (getTarget().get() != null) {
                    LOG.debug("Setting value '{}' for {}", val, this);
                    getMethod().invoke(getTarget().get(), val);
                }
            } catch (IllegalAccessException ex) {
                logAndThrowError(val, ex);
            } catch (IllegalArgumentException ex) {
                logAndThrowError(val, ex);
            } catch (InvocationTargetException ex) {
                logAndThrowError(val, ex.getTargetException());
            }
        }

        private void logAndThrowError(Object val, Throwable t) throws ConfigurationException {
            String message =
                    String.format("Error while setting value '%s' (%s) for property '%s' with method '%s'", val,
                            val == null ? null : val.getClass(), getKey(), getMethod());
            LOG.error(message);
            throw new ConfigurationException(message, t);
        }

        @Override
        public String toString() {
            return String.format("ConfigurableObject[key=%s, method=%s, target=%s]", getKey(), getMethod(),
                    getTarget().get());
        }

        @Override
        public int hashCode() {
            final int prime = 45;
            int hash = 5;
            hash = prime * hash + (getMethod() != null ? getMethod().hashCode() : 0);
            hash = prime * hash + (getTarget() != null ? getTarget().hashCode() : 0);
            hash = prime * hash + (getKey() != null ? getKey().hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ConfigurableObject other = (ConfigurableObject) obj;
            if (getMethod() != other.getMethod() && (getMethod() == null || !getMethod().equals(other.getMethod()))) {
                return false;
            }
            if (getTarget() != other.getTarget() && (getTarget() == null || !getTarget().equals(other.getTarget()))) {
                return false;
            }
            if ((getKey() == null) ? (other.getKey() != null) : !getKey().equals(other.getKey())) {
                return false;
            }
            return true;
        }
    }
}
