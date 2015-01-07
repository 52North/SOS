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
package org.n52.sos.ds;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionGroup;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public interface Datasource extends ConnectionProviderIdentificator, DatasourceDaoIdentifier {

    SettingDefinitionGroup BASE_GROUP = new SettingDefinitionGroup().setTitle("Database Configuration").setOrder(1);

    SettingDefinitionGroup ADVANCED_GROUP = new SettingDefinitionGroup().setTitle("Advanced Database Configuration")
            .setOrder(2);

    /**
     * @return the representive name of this dialect
     */
    String getDialectName();

    /**
     * @return the settings needed to connect
     */
    Set<SettingDefinition<?, ?>> getSettingDefinitions();

    /**
     * @param current
     *            the current settings
     *
     * @return the settings that can be newSettings without schema without
     *         reinstallation
     */
    Set<SettingDefinition<?, ?>> getChangableSettingDefinitions(Properties current);

    /**
     * Check if a connection is possible.
     *
     * @param settings
     *            the settings to connect
     */
    void validateConnection(Map<String, Object> settings);

    /**
     * Check if a connection is still possible with the newSettings settings.
     *
     * @param current
     *            the current datasource settings
     * @param newSettings
     *            the newSettings settings
     */
    void validateConnection(Properties current, Map<String, Object> newSettings);

    /**
     * Validate if all prerequisites (e.g. datasource version) are met. Will
     * only be called if
     * {@link #validateConnection(java.util.Properties, java.util.Map)
     * validateConnection()} succeeded.
     *
     * @param settings
     *            the settings to connect
     */
    void validatePrerequisites(Map<String, Object> settings);

    /**
     * Used to validate prerequisites after the connections settings newSettings
     * in the admin interface.
     *
     * @param current
     *            the current datasource settings
     * @param newSettings
     *            the newSettings settings
     */
    void validatePrerequisites(Properties current, Map<String, Object> newSettings);

    /**
     * @return if this datasource needs some kind of schema
     */
    boolean needsSchema();

    /**
     * Validate the existing schema. Will only be called if
     * {@link #needsSchema() needsSchema()} and
     * {@link #checkIfSchemaExists(java.util.Map) checkIfSchemaExists()} return
     * {@code true}.
     *
     * @param settings
     *            the settings to connect
     */
    void validateSchema(Map<String, Object> settings);

    /**
     * Validate the existing schema. Will only be called if
     * {@link #needsSchema() needsSchema()} and
     * {@link #checkIfSchemaExists(java.util.Properties, java.util.Map)
     * checkIfSchemaExists()} return {@code true}.
     *
     * @param current
     *            the current datasource settings
     * @param newSettings
     *            the newSettings settings
     */
    void validateSchema(Properties current, Map<String, Object> newSettings);

    /**
     *
     * Check if the schema exists. Should return {@code true} even if parts are
     * missing. Will only be called if {@link #needsSchema() needsSchema()}
     * returns {@code true}.
     *
     * @param settings
     *            the settings to connect
     *
     * @return if the schema (or parts of it) exists
     */
    boolean checkIfSchemaExists(Map<String, Object> settings);

    /**
     * Check if the schema exists. Should return {@code true} even if parts are
     * missing. Will only be called if {@link #needsSchema() needsSchema()}
     * returns {@code true}.
     *
     * @param current
     *            the current datasource settings
     * @param newSettings
     *            the newSettings settings
     *
     * @return if the schema (or parts of it) exists
     */
    boolean checkIfSchemaExists(Properties current, Map<String, Object> newSettings);

    /**
     * Check if it is possible to create the schema (e.g. test if the privilege
     * are sufficient). Will only be called if {@link #needsSchema()
     * needsSchema()} returns {@code true}.
     *
     * @param settings
     *            the settings to connect
     *
     * @return if the creation if the schema is possible
     */
    boolean checkSchemaCreation(Map<String, Object> settings);

    /**
     * Create the schema for the supplied settings. Will only be called if
     * {@link #needsSchema() needsSchema()} and
     * {@link #checkSchemaCreation(java.util.Map) checkSchemaCreation()} return
     * {@code true}. If {@link #checkIfSchemaExists(java.util.Map)
     * checkIfSchemaExists()} returned {@code true},
     * {@link #dropSchema(java.util.Map) dropSchema()} will be called first.
     *
     * @param settings
     *            the settings to connect
     */
    String[] createSchema(Map<String, Object> settings);

    /**
     * Drop the present schema (or parts of it). Will only be called if
     * {@link #needsSchema() needsSchema()} and
     * {@link #checkIfSchemaExists(java.util.Map) checkIfSchemaExists()} return
     * {@code true}.
     *
     * @param settings
     *            the settings to connect
     */
    String[] dropSchema(Map<String, Object> settings);

    /**
     * Creates an update schema for the supplied settings. Will only be called if
     * {@link #needsSchema() needsSchema()} and
     * {@link #checkSchemaCreation(java.util.Map) checkSchemaCreation()} return
     * {@code true}. If {@link #checkIfSchemaExists(java.util.Map)
     * checkIfSchemaExists()} returned {@code true},
     * {@link #dropSchema(java.util.Map) dropSchema()} will be called first.
     *
     * @param settings
     *            the settings to connect
     * @return
     */
    String[] updateSchema(Map<String, Object> settings);

    void execute(String[] sql, Map<String, Object> settings);

    /**
     * Clear the contents of the datasource. Only called if
     * {@link #supportsClear() supportsClear()} returns {@code true}.
     *
     * @param settings
     *            the settings to connect
     */
    void clear(Properties settings);

    /**
     *
     * @return {@code true}
     */
    boolean supportsClear();

    /**
     * Create the datasource properties used by the {@link ConnectionProvider}
     * to connect.
     *
     * @param settings
     *            the settings to connect
     *
     * @return the datasource properties
     */
    Properties getDatasourceProperties(Map<String, Object> settings);

    /**
     * Create the datasource properties used by the {@link ConnectionProvider}
     * to connect.
     *
     * @param current
     *            the current datasource settings
     * @param newSettings
     *            the newSettings settings
     *
     * @return the new datasource properties
     */
    Properties getDatasourceProperties(Properties current, Map<String, Object> newSettings);

    /**
     * @return the callback used at instantiation time of the connection provider
     */
    DatasourceCallback getCallback();

    /**
     * Called right before a schema is created.
     *
     * @param settings the settings to connect
     */
    void prepare(Map<String, Object> settings);

    boolean isPostCreateSchema();

    void executePostCreateSchema(Map<String, Object> databaseSettings);
    
    void checkPostCreation(Properties properties);
    
}
