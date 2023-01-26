/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.web.common;

/**
 * @since 4.0.0
 *
 */
public interface ControllerConstants {
    String MEDIA_TYPE_APPLICATION_JSON = "application/json; charset=UTF-8";

    String SETTINGS_MODEL_ATTRIBUTE = "settings";

    String DATABASE_SETTINGS_MODEL_ATTRIBUTE = "databaseSettings";

    String ERROR_MODEL_ATTRIBUTE = "error";

    String JDBC_PARAMETER = "jdbc_uri";

    String LIBRARIES_MODEL_ATTRIBUTE = "libs";

    String ROLE_ADMIN = "ROLE_ADMIN";

    /* SQL file paths */
    String DROP_DATAMODEL_SQL_FILE = "/sql/script_20_drop.sql";

    String CREATE_DATAMODEL_SQL_FILE = "/sql/script_20_create.sql";

    String CLEAR_DATABASE_SQL_FILE = "/sql/clear_database.sql";

    String ADMIN_USERNAME_REQUEST_PARAMETER = "admin_username";

    String ADMIN_PASSWORD_REQUEST_PARAMETER = "admin_password";

    String ADMIN_CURRENT_PASSWORD_REQUEST_PARAMETER = "current_password";

    interface Default {
        String SETTINGS = "/settings";

        String EXTENSIONS = "/extensions";

        String PROFILES = "/profiles";

        String DATASOURCE = "/datasource";

        String ADMIN = "admin";

        String ADMIN_ROOT = "/" + ADMIN;

        String INSTALL = "install";

        String INSTALL_ROOT = "/" + INSTALL;

        String INDEX = "index";

        String LOGIN = "/login";

        String FINISH = "/finish";
    }

    /**
     * @since 4.0.0
     *
     */
    interface Views extends Default {

        String DOCUMENTATION = "documentation";

        String CLIENT = "client";

        String LICENSE = "license";

        String GET_INVOLVED = "get-involved";

        String ADMIN_INDEX = ADMIN + "/" + INDEX;

        String ADMIN_DATASOURCE = ADMIN + DATASOURCE;

        String ADMIN_DATASOURCE_SETTINGS = ADMIN + "/datasource-settings";

        String ADMIN_LOGIN = ADMIN + LOGIN;

        String ADMIN_RESET = ADMIN + "/reset";

        String ADMIN_LIBRARY_LIST = ADMIN + "/libs";

        String ADMIN_SETTINGS = ADMIN + SETTINGS;

        String ADMIN_OPERATIONS = ADMIN + "/operations";

        String ADMIN_ENCODINGS = ADMIN + "/encodings";

        String ADMIN_BINDINGS = ADMIN + "/bindings";

        String INSTALL_LOAD_SETTINGS = "install/load";

        String ADMIN_LOGGING = ADMIN + "/logging";

        String ADMIN_CACHE = ADMIN + "/cache";

        String ADMIN_SENSOR_DESCRIPTIONS = ADMIN + "/sensors";

        String ADMIN_CAPABILITIES_SETTINGS = ADMIN + "/capabilities";

        String ADMIN_I18N = ADMIN + "/i18n";

        String ADMIN_I18N_SETTINGS = ADMIN_I18N + SETTINGS;

        String ADMIN_RENAME_OBSERVABLE_PROPERTIES = "admin/observableProperties";

        String ADMIN_EXTENSIONS = ADMIN + EXTENSIONS;

        String ADMIN_PROFILES = ADMIN + PROFILES;

        String INSTALL_INDEX =  INSTALL + "/" + INDEX;

        String INSTALL_DATASOURCE = INSTALL + DATASOURCE;

        String INSTALL_SETTINGS = INSTALL + SETTINGS;

        String INSTALL_FINISH = INSTALL + FINISH;
    }

    /**
     * @since 4.0.0
     *
     */
    interface Paths extends Default {
        String ROOT = "/";

        String WSDL = "/wsdl";

        String INDEX = "/" + Default.INDEX;

        String DOCUMENTATION = "/documentation";

        String LICENSE = "/license";

        String CLIENT = "/client";

        String GET_INVOLVED = "/get-involved";

        String SETTING_DEFINITIONS = "/settingDefinitions.json";

        String ADMIN_INDEX = "/admin/index";

        String ADMIN_SETTINGS = ADMIN_ROOT + SETTINGS;

        String ADMIN_SETTINGS_DUMP = "/admin/settings.json";

        String ADMIN_CONFIGURATION_DUMP = "/admin/configuration.json";

        String ADMIN_SETTINGS_UPDATE = "/admin/settings";

        String ADMIN_DATABASE = ADMIN_ROOT + DATASOURCE;

        String ADMIN_SENSORS_DESCRIPTIONS = "/admin/sensors";

        String ADMIN_LIBRARY_LIST = "/admin/libs";

        String ADMIN_OPERATIONS = "/admin/operations";

        String ADMIN_OPERATIONS_JSON_ENDPOINT = "/admin/operations/json";

        String ADMIN_ENCODINGS = "/admin/encodings";

        String ADMIN_ENCODINGS_JSON_ENDPOINT = "/admin/encodings/json";

        String ADMIN_BINDINGS = "/admin/bindings";

        String ADMIN_BINDINGS_JSON_ENDPOINT = "/admin/bindings/json";

        String ADMIN_DATABASE_EXECUTE = ADMIN_ROOT + DATASOURCE;

        String ADMIN_DATABASE_SETTINGS = "/admin/datasource/settings";

        String ADMIN_DATABASE_ADD_SAMPLEDATA = "/admin/datasource/addSampledata";

        String ADMIN_CACHE = "/admin/cache";

        String ADMIN_CACHE_SUMMARY = "/admin/cache/summary";

        String ADMIN_CACHE_LOADING = "/admin/cache/loading";

        String ADMIN_CACHE_DUMP = "/admin/cache/dump";

        String ADMIN_RELOAD_CAPABILITIES_CACHE = "/admin/cache/reload";

        String ADMIN_DATABASE_UPDATE_SCRIPT = "/admin/datasource/updatescript";

        String ADMIN_DATABASE_REMOVE_TEST_DATA = "/admin/datasource/testdata/remove";

        String ADMIN_DATABASE_CREATE_TEST_DATA = "/admin/datasource/testdata/create";

        String ADMIN_DATABASE_CLEAR = "/admin/datasource/clear";

        String ADMIN_DATABASE_DELETE_DELETED_OBSERVATIONS = "/admin/datasource/deleteDeletedObservations";

        String ADMIN_DATABASE_DELETE_DELETED_DATA = "/admin/datasource/deleteDeletedData";

        String ADMIN_DATABASE_LOAD_PREDEFINED_UNITS = "/admin/datasource/loadPredefinedUnits";

        String ADMIN_DATABASE_LOAD_PREDEFINED_PHENOMENA = "/admin/datasource/loadPredefinedPhenomena";

        String ADMIN_RENAME_OBSERVABLE_PROPERTIES = "/admin/observableProperties";

        String ADMIN_RESET = "/admin/reset";

        String ADMIN_LOGGING = "/admin/logging";

        String ADMIN_LOGGING_FILE_DOWNLOAD = "/admin/logging/file";

        String ADMIN_CAPABILITIES_SETTINGS = "/admin/capabilities";

        String ADMIN_I18N = "/admin/i18n";

        String ADMIN_I18N_SETTINGS = ADMIN_I18N + SETTINGS;

        String INSTALL_INDEX = INSTALL_ROOT + INDEX;

        String INSTALL_DATASOURCE = INSTALL_ROOT + DATASOURCE;

        String INSTALL_SETTINGS = INSTALL_ROOT + SETTINGS;

        String INSTALL_FINISH = INSTALL_ROOT + FINISH;

        String INSTALL_LOAD_CONFIGURATION = INSTALL_ROOT + "/load";

        String INSTALL_DATASOURCE_DIALECTS = INSTALL_ROOT + "/datasource/sources";

        String LOGIN = Default.LOGIN;

        String LOGOUT = "/j_spring_security_logout";

        String OFFERING_EXTENSIONS_AJAX_ENDPOINT = "/admin/capabilities/ajax/offeringExtensions";

        String CAPABILITIES_EXTENSION_AJAX_ENDPOINT = "/admin/capabilities/ajax/capabilitiesExtensions";

        String STATIC_CAPABILITIES_AJAX_ENDPOINT = "/admin/capabilities/ajax/staticCapabilities";

        String VALIDATION_AJAX_ENDPOINT = "/admin/capabilities/ajax/validation";

        String OFFERING_I18N_AJAX_ENDPOINT = "/admin/i18n/ajax/offerings";

        String PROCEDURE_I18N_AJAX_ENDPOINT = "/admin/i18n/ajax/procedures";

        String FEATURE_I18N_AJAX_ENDPOINT = "/admin/i18n/ajax/features";

        String OBSERVABLE_PROPERTY_I18N_AJAX_ENDPOINT = "/admin/i18n/ajax/observableProperties";

        String SETTINGS_I18N_AJAX_ENDPOINT = "/admin/i18n/ajax/settings";

        String ADMIN_EXTENSIONS = ADMIN_ROOT + EXTENSIONS;

        String ADMIN_EXTENSIONS_JSON_ENDPOINT = "/admin/extensions/json";

        String ADMIN_PROFILES = ADMIN_ROOT + PROFILES;
    }

}
