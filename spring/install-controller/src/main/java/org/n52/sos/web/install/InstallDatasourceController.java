/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.web.install;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.SettingValueFactory;
import org.n52.iceland.ds.Datasource;
import org.n52.sos.context.ContextSwitcher;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.web.common.ControllerConstants;
import org.n52.sos.web.install.InstallConstants.Step;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.base.Strings;

/**
 * @since 4.0.0
 *
 */
@Controller
@RequestMapping(ControllerConstants.Paths.INSTALL_DATASOURCE)
public class InstallDatasourceController extends AbstractProcessingInstallationController {

    @Inject
    private SettingValueFactory settingValueFactory;

    @Inject
    private Collection<Datasource> datasources;

    @Inject
    private ContextSwitcher contextSwitcher;

    @Inject
    private ConfigurableEnvironment env;

    @Override
    protected Step getStep() {
        return Step.DATASOURCE;
    }

    @Override
    protected void process(Map<String, String> parameters,
                           InstallationConfiguration c)
            throws InstallationSettingsError {
        boolean overwriteTables;
        boolean alreadyExistent;
        boolean createTables;
        boolean forceUpdateTables;
        Datasource datasource;

        try {
            datasource = checkDatasource(parameters, c);
            overwriteTables = checkOverwrite(datasource, parameters, c);
            createTables = checkCreate(datasource, parameters, overwriteTables, c);
            forceUpdateTables = checkUpdate(datasource, parameters, overwriteTables, c);
            c.setDatabaseSettings(parseDatasourceSettings(datasource, parameters));
            setProfiles(c, datasource);
            datasource.validateConnection(c.getDatabaseSettings());
            datasource.validatePrerequisites(c.getDatabaseSettings());

            if (datasource.needsSchema()) {
                alreadyExistent = datasource.checkIfSchemaExists(c.getDatabaseSettings());

                if (createTables) {
                    if (alreadyExistent) {
                        if (!overwriteTables) {
                            throw new InstallationSettingsError(
                                    c, ErrorMessages.TABLES_ALREADY_CREATED_BUT_SHOULD_NOT_OVERWRITE);
                        } else {
                            try {
                                datasource.validateSchema(c.getDatabaseSettings());
                            } catch (ConfigurationError e) {
                                throw new InstallationSettingsError(c,
                                        ErrorMessages.EXISTING_SCHEMA_DIFFERS_DROP_CREATE_SCHEMA, e);
                            }
                        }
                    }
                    if (!datasource.checkSchemaCreation(c.getDatabaseSettings())) {
                        throw new InstallationSettingsError(
                                c, String.format(ErrorMessages.COULD_NOT_CREATE_SOS_TABLES,
                                                 "schema creation test table"));
                    }
                } else if (!alreadyExistent) {
                    throw new InstallationSettingsError(c, ErrorMessages.NO_TABLES_AND_SHOULD_NOT_CREATE);
                } else {
                    try {
                        datasource.validateSchema(c.getDatabaseSettings());
                    } catch (ConfigurationError e) {
                        if (!Strings.isNullOrEmpty(e.getMessage()) &&
                            (e.getMessage().contains(ErrorMessages.TO_CHECK_ERROR_MESSAGE_FOI_COL_IN_OBS_TAB) || e
                             .getMessage().contains(ErrorMessages.TO_CHECK_ERROR_MESSAGE_SERIES_COL_IN_OBS_TAB))) {
                            throw new InstallationSettingsError(
                                    c, ErrorMessages.EXISTING_SCHEMA_DIFFERS_UPDATE_SCHEMA, e);
                        } else if (!forceUpdateTables) {
                            throw new InstallationSettingsError(
                                    c, String.format(ErrorMessages.EXISTING_SCHEMA_REQUIRES_UPDATE, e.getMessage()), e);
                        }
                    }
                }
            }
        } catch (ConfigurationError e) {
            throw new InstallationSettingsError(c, e.getMessage(), e);
        }
    }

    private void setProfiles(InstallationConfiguration c, Datasource datasource) {
        String concept = (String) c.getDatabaseSetting(HibernateDatasourceConstants.DATABASE_CONCEPT_KEY);
        boolean set = false;
        if (concept != null && !concept.isEmpty()) {
            env.setActiveProfiles(concept.toLowerCase());
            set = true;
        }
        String extension = (String) c.getDatabaseSetting(HibernateDatasourceConstants.DATABASE_EXTENSION_KEY);
        if (extension != null && !extension.isEmpty()) {
            if (set) {
                env.addActiveProfile(extension.toLowerCase());
            } else {
                env.setActiveProfiles(extension.toLowerCase());
            }
        }
        if (datasource.getSpringProfiles() != null && !datasource.getSpringProfiles().isEmpty()) {
            datasource.getSpringProfiles().forEach(sp -> env.addActiveProfile(sp));
        }
        contextSwitcher.loadSettings();
        contextSwitcher.reloadContext();
    }

    protected boolean checkOverwrite(Datasource datasource, Map<String, String> parameters,
                                     InstallationConfiguration settings) {
        boolean overwriteTables = false;
        if (datasource.needsSchema()) {
            Boolean overwriteTablesParameter = parseBoolean(parameters, InstallConstants.OVERWRITE_TABLES_PARAMETER);
            if (overwriteTablesParameter != null) {
                overwriteTables = overwriteTablesParameter;
            }
        }
        parameters.remove(InstallConstants.OVERWRITE_TABLES_PARAMETER);
        settings.setDropSchema(overwriteTables);
        return overwriteTables;
    }

    protected boolean checkCreate(Datasource datasource, Map<String, String> parameters, boolean overwriteTables,
                                  InstallationConfiguration settings) {
        boolean createTables = false;
        if (datasource.needsSchema()) {
            Boolean createTablesParameter = parseBoolean(parameters, InstallConstants.CREATE_TABLES_PARAMETER);
            if (createTablesParameter != null) {
                createTables = overwriteTables ? overwriteTables : createTablesParameter;
            }
        }
        parameters.remove(InstallConstants.CREATE_TABLES_PARAMETER);
        settings.setCreateSchema(createTables);
        return createTables;
    }

    protected boolean checkUpdate(Datasource datasource, Map<String, String> parameters, boolean createTables,
                                  InstallationConfiguration settings) {
        boolean updateTables = false;
        if (datasource.needsSchema()) {
            Boolean updateTablesParameter = parseBoolean(parameters, InstallConstants.UPDATE_TABLES_PARAMETER);
            if (updateTablesParameter != null) {
                updateTables = createTables ? false : updateTablesParameter;
            }
        }
        parameters.remove(InstallConstants.UPDATE_TABLES_PARAMETER);
        settings.setForceUpdateSchema(updateTables);
        return updateTables;
    }

    protected Map<String, Object> parseDatasourceSettings(Datasource datasource, Map<String, String> parameters) {
        return datasource.getSettingDefinitions().stream().collect(toMap(def -> def.getKey(),
            def -> this.settingValueFactory.newSettingValue(def, parameters.get(def.getKey())).getValue()));

    }

    protected Map<String, Datasource> getDatasources() {
        return this.datasources.stream().collect(toMap(Datasource::getDialectName, Function.identity()));
    }

    protected Datasource checkDatasource(Map<String, String> parameters, InstallationConfiguration settings)
            throws InstallationSettingsError {
        String datasourceName = parameters.get(InstallConstants.DATASOURCE_PARAMETER);
        parameters.remove(InstallConstants.DATASOURCE_PARAMETER);
        Datasource datasource = getDatasources().get(datasourceName);
        if (datasource == null) {
            throw new InstallationSettingsError(settings, String.format(ErrorMessages.INVALID_DATASOURCE,
                                                                        datasourceName));
        } else {
            settings.setDatasource(datasource);
        }
        return datasource;
    }
}
