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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractServiceLoaderRepository;
import org.n52.sos.util.MultiMaps;
import org.n52.sos.util.SetMultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * Repository for {@code SettingDefinitionProvider} implementations.
 * <p/>
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class SettingDefinitionProviderRepository extends AbstractServiceLoaderRepository<SettingDefinitionProvider> {
    private static final Logger LOG = LoggerFactory.getLogger(SettingDefinitionProviderRepository.class);

    private Map<String, SettingDefinition<?, ?>> definitionsByKey = Collections.emptyMap();

    private Set<SettingDefinition<?, ?>> settingDefinitions = Collections.emptySet();

    private SetMultiMap<SettingDefinition<?, ?>, SettingDefinitionProvider> providersByDefinition = MultiMaps
            .newSetMultiMap();

    /**
     * Constructs a new repository.
     * <p/>
     * 
     * @throws ConfigurationException
     *             if there is a problem while loading implementations
     */
    public SettingDefinitionProviderRepository() throws ConfigurationException {
        super(SettingDefinitionProvider.class, false);
        super.load(false);
    }

    /**
     * @return all setting definitions
     */
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return Collections.unmodifiableSet(this.settingDefinitions);
    }

    /**
     * Returns all providers that declared a specific setting.
     * <p/>
     * 
     * @param setting
     *            the setting
     *            <p/>
     * @return the providers
     */
    public Set<SettingDefinitionProvider> getProviders(SettingDefinition<?, ?> setting) {
        Set<SettingDefinitionProvider> set = this.providersByDefinition.get(setting);
        if (set == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(set);
        }
    }

    /**
     * Gets the definition for the specified key.
     * <p/>
     * 
     * @param key
     *            the key
     *            <p/>
     * @return the definition or {@code null} if none is known
     */
    public SettingDefinition<?, ?> getDefinition(String key) {
        return this.definitionsByKey.get(key);
    }

    @Override
    protected void processImplementations(Set<SettingDefinitionProvider> implementations)
            throws ConfigurationException {
        this.settingDefinitions = new HashSet<SettingDefinition<?, ?>>();
        this.providersByDefinition = MultiMaps.newSetMultiMap();
        this.definitionsByKey = new HashMap<String, SettingDefinition<?, ?>>();

        for (SettingDefinitionProvider provider : implementations) {
            LOG.debug("Processing IDefinitionProvider {}", provider);
            Set<SettingDefinition<?, ?>> requiredSettings = provider.getSettingDefinitions();
            for (SettingDefinition<?, ?> definition : requiredSettings) {
                SettingDefinition<?, ?> prev = definitionsByKey.put(definition.getKey(), definition);
                if (prev != null && !prev.equals(definition)) {
                    LOG.warn("{} overwrites {} requested by [{}]", definition, prev,
                            Joiner.on(", ").join(this.providersByDefinition.get(prev)));
                    this.providersByDefinition.remove(prev);
                }
                LOG.debug("Found Setting definition for key '{}'", definition.getKey());
                if (!definition.isOptional() && !definition.hasDefaultValue()) {
                    LOG.warn("No default value for optional setting {}", definition.getKey());
                }
            }
            this.settingDefinitions.addAll(requiredSettings);
            for (SettingDefinition<?, ?> setting : requiredSettings) {
                this.providersByDefinition.add(setting, provider);
            }
        }
    }
}
