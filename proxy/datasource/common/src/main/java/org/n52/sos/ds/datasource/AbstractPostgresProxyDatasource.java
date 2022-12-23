/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.datasource;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.n52.faroe.SettingDefinition;
import org.n52.sos.ds.hibernate.util.HibernateConstants;

import com.google.common.collect.ImmutableSet;

public abstract class AbstractPostgresProxyDatasource extends AbstractPostgresDatasource implements ProxyDatasource {

    private static final Set<String> FILTER_KEYS =
            ImmutableSet.of(BATCH_SIZE_KEY, DATABASE_CONCEPT_KEY, DATABASE_EXTENSION_KEY, FEATURE_CONCEPT_KEY,
                    TIMEZONE_KEY, TIME_STRING_FORMAT_KEY, TIME_STRING_Z_KEY);

    public AbstractPostgresProxyDatasource() {
        super();
    }

    @Override
    public Set<SettingDefinition<?>> getChangableSettingDefinitions(Properties current) {
        return filter(super.getChangableSettingDefinitions(current), FILTER_KEYS);
    }

    @Override
    public Set<SettingDefinition<?>> getSettingDefinitions() {
        return filter(super.getSettingDefinitions(), FILTER_KEYS);
    }

    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
        precheck(settings);
        Properties p = super.getDatasourceProperties(settings);
        p.put(HibernateConstants.CONNECTION_URL, toURL(settings));
        p.put(HibernateConstants.DRIVER_CLASS, POSTGRES_DRIVER_CLASS);
        p.put(HibernateConstants.DIALECT, POSTGRES_DIALECT_CLASS);
        p.put(HibernateConstants.CONNECTION_USERNAME, USERNAME_DEFAULT_VALUE);
        p.put(HibernateConstants.CONNECTION_PASSWORD, PASSWORD_DEFAULT_VALUE);
        p.put(DATABASE_CONCEPT_KEY, settings.get(DATABASE_CONCEPT_KEY));
        p.put(DATABASE_EXTENSION_KEY, settings.get(DATABASE_EXTENSION_KEY));
        p.put(SPRING_PROFILE_KEY, String.join(",", getSpringProfiles()));
        if (settings.containsKey(PROXY_HOST_KEY)) {
            p.put(PROXY_HOST_KEY, settings.get(PROXY_HOST_KEY));
        }
        if (settings.containsKey(PROXY_PATH_KEY)) {
            p.put(PROXY_PATH_KEY, settings.get(PROXY_PATH_KEY));
        }
        addMappingFileDirectories(settings, p);
        return p;
    }

    @Override
    public Map<String, Object> parseDatasourceProperties(Properties current) {
        final Map<String, Object> settings = super.parseDatasourceProperties(current);
        if (current.containsKey(PROXY_HOST_KEY)) {
            settings.put(PROXY_HOST_KEY, current.getProperty(PROXY_HOST_KEY));
        }
        if (current.containsKey(PROXY_PATH_KEY)) {
            settings.put(PROXY_PATH_KEY, current.getProperty(PROXY_PATH_KEY));
        }
        return settings;
    }

    private void precheck(Map<String, Object> settings) {
        settings.put(DATABASE_CONCEPT_KEY, DatabaseConcept.PROXY.name());
        settings.put(FEATURE_CONCEPT_KEY, FeatureConcept.DEFAULT_FEATURE_CONCEPT.name());
        settings.put(DATABASE_EXTENSION_KEY, DatabaseExtension.DATASOURCE.name());
    }

    @Override
    public void validatePrerequisites(Map<String, Object> settings) {
        settings.put(DATABASE_CONCEPT_KEY, DatabaseConcept.PROXY.name());
        settings.put(FEATURE_CONCEPT_KEY, getFeatureConcept());
        settings.put(DATABASE_EXTENSION_KEY, DatabaseExtension.DATASOURCE.name());
        settings.put(HibernateConstants.CONNECTION_USERNAME, USERNAME_DEFAULT_VALUE);
        settings.put(HibernateConstants.CONNECTION_PASSWORD, PASSWORD_DEFAULT_VALUE);
        super.validatePrerequisites(settings);
    }

    @Override
    public void validateConnection(Map<String, Object> settings) {
        super.validateConnection(settings);
    }

    protected String getFeatureConcept() {
        return FeatureConcept.DEFAULT_FEATURE_CONCEPT.name();
    }

}
