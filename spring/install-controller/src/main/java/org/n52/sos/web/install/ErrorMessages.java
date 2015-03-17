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
package org.n52.sos.web.install;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public interface ErrorMessages {
    String INVALID_DATASOURCE = "The datasource %s is invalid!";

    String POST_GIS_IS_NOT_INSTALLED_IN_THE_DATABASE = "PostGIS is not installed in the database.";

    String COULD_NOT_INSERT_TEST_DATA = "Could not insert test data: %s";

    String NO_DRIVER_SPECIFIED = "no driver specified";

    String NO_SCHEMA_SPECIFIED = "No schema specified";

    String NO_JDBC_URL_SPECIFIED = "No JDBC URL specified.";

    String COULD_NOT_WRITE_DATASOURCE_CONFIG = "Could not write datasource config: %s";

    String PASSWORD_IS_INVALID = "Password is invalid.";

    String COULD_NOT_READ_SPATIAL_REF_SYS_TABLE = "Could not read 'spatial_ref_sys' table of PostGIS. "
            + "Please revise your database configuration.";

    String COULD_NOT_LOAD_DIALECT = "Could not load dialect: %s";

    String COULD_NOT_LOAD_CONNECTION_POOL = "Could not load connection pool: %s";

    String COULD_NOT_VALIDATE_PARAMETER = "Could not validate '%s' parameter: %s";

    String COULD_NOT_INSTANTIATE_CONFIGURATOR = "Could not instantiate Configurator: %s";

    String INVALID_JDBC_URL_WITH_ERROR_MESSAGE = "Invalid JDBC URL: %s";

    String CAN_NOT_CREATE_STATEMENT = "Cannot create Statement: %s";

    String COULD_NOT_CONNECT_TO_THE_DATABASE = "Could not connect to the database: %s";

    String COULD_NOT_SAVE_ADMIN_CREDENTIALS = "Could not save admin credentials into the database: %s";

    String INVALID_JDBC_URL = "Invalid JDBC URL.";

    String USERNAME_IS_INVALID = "Username is invalid.";

    String COULD_NOT_LOAD_DRIVER = "Could not load Driver: %s";

    String NO_DIALECT_SPECIFIED = "no dialect specified";

    String TABLES_ALREADY_CREATED_BUT_SHOULD_NOT_OVERWRITE = "Tables already created, but should not overwrite. "
            + "Please take a look at the 'Actions' section.";

    String COULD_NOT_INSERT_SETTINGS = "Could not insert settings into the database: %s";

    String NO_CONNECTION_POOL_SPECIFIED = "no connection pool specified";

    String COULD_NOT_CREATE_SOS_TABLES = "Could not create SOS tables: %s";

    String COULD_NOT_DROP_SOS_TABLES = "Could not drop SOS tables: %s";

    String COULD_NOT_FIND_FILE = "Could not find file '%s'!";

    String COULD_NOT_CONNECT_TO_DATABASE_SERVER = "Could not connect to DB server: %s";

    String COULD_NOT_CREATE_TABLES = "Could not create tables: %s";

    String NO_TABLES_AND_SHOULD_NOT_CREATE = "No tables are present in the database "
            + "and no tables should be created. Enable 'Create tables' or select another datasource.";

    String COULD_NOT_INSTANTIATE_SETTINGS_MANAGER = "Could not instantiate Settings Manager: %s";

    String NO_DEFINITON_FOUND = "No definiton found for setting with key '%s'";

    String COULD_NOT_DELETE_PREVIOUS_SETTINGS = "Could not delete previous settings: %s";

    String COULD_NOT_SET_CATALOG = "Could not set catalog search path";

    String COULD_NOT_CHECK_IF_TABLE_EXISTS = "Could not check if table '%s' exists: %s";

    String COULD_NOT_CHECK_IF_SCHEMA_EXISTS = "Could not check if schema '%s' exists: %s";

    String SCHEMA_DOES_NOT_EXIST = "Schema %s does not exist";
    
    String EXISTING_SCHEMA_DIFFERS_DROP_CREATE_SCHEMA = "The installed schema does not accord to the schema which should be created! Please, check the checkbox 'Old observation concept' or delete existing schema manually with the according drop schema in /misc/db!";
    
    String EXISTING_SCHEMA_REQUIRES_UPDATE = "The installed schema is corrupt/invalid (%s).\nTry to delete the existing tables and check 'Create tables' or check 'Force update tables' (experimental).\nOr check for update scripts in /misc/db!";
    
    String EXISTING_SCHEMA_DIFFERS_UPDATE_SCHEMA = "The installed schema does not accord to the update schema! Please, check the checkbox 'Old observation concept'!";
    
    String TO_CHECK_ERROR_MESSAGE_FOI_COL_IN_OBS_TAB = "Missing column: featureOfInterestId in public.observation";
    
    String TO_CHECK_ERROR_MESSAGE_SERIES_COL_IN_OBS_TAB = "Missing column: seriesId in public.observation";
}
