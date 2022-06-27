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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.n52.faroe.AbstractSettingDefinition;
import org.n52.faroe.ConfigurationError;
import org.n52.faroe.SettingDefinition;
import org.n52.faroe.settings.StringSettingDefinition;
import org.n52.sos.ds.hibernate.util.HibernateConstants;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class AquariusFileDatasource extends AbstractAquariusH2Datasource {

    private static final long serialVersionUID = 1L;

    private static final String DIALECT = "Proxy Aquarius (file based)";

    private static final Pattern JDBC_URL_PATTERN = Pattern.compile("^jdbc:h2:(.+)$");

    private static final String JDBC_URL_FORMAT = "jdbc:h2:file:%s";

    private static final String USER_HOME = "user.home";

    private static final String AQ = "aq";

    private static final String DESCRIPTION =
            "Set this to the name/path of the database you want to use for SOS Proxy.";

    @Override
    @SuppressFBWarnings("DMI_EMPTY_DB_PASSWORD")
    protected Connection openConnection(Map<String, Object> settings) throws SQLException {
        try {
            String jdbc = toURL(settings);
            Class.forName(H2_DRIVER_CLASS);
            precheckDriver(jdbc, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            return DriverManager.getConnection(jdbc, DEFAULT_USERNAME, DEFAULT_PASSWORD);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getDialectName() {
        return DIALECT;
    }

    @Override
    public Set<SettingDefinition<?>> getSettingDefinitions() {
        AbstractSettingDefinition<String> h2Database = createDatabaseDefinition().setDescription(DESCRIPTION)
                .setDefaultValue(System.getProperty(USER_HOME) + File.separator + AQ);
        Set<SettingDefinition<?>> settingDefinitions = super.getSettingDefinitions();
        settingDefinitions.add(h2Database);
        return settingDefinitions;
    }

    private StringSettingDefinition getDatabaseDefinition() {
        StringSettingDefinition def = createDatabaseDefinition();
        def.setDescription(DESCRIPTION);
        def.setDefaultValue(System.getProperty(USER_HOME) + File.separator + AQ);
        return def;
    }

    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
        Properties p = super.getDatasourceProperties(settings);
        p.put(HibernateConstants.CONNECTION_RELEASE_MODE,
                HibernateConstants.CONNECTION_RELEASE_MODE_AFTER_TRANSACTION);
        p.put(HibernateConstants.CURRENT_SESSION_CONTEXT, HibernateConstants.THREAD_LOCAL_SESSION_CONTEXT);
        return p;
    }

    @Override
    public Map<String, Object> parseDatasourceProperties(Properties current) {
        Map<String, Object> settings = super.parseDatasourceProperties(current);
        Matcher matcher = JDBC_URL_PATTERN.matcher(current.getProperty(HibernateConstants.CONNECTION_URL));
        matcher.find();
        settings.put(DATABASE_KEY, matcher.group(1));
        settings.put(HIBERNATE_DIRECTORY, current.get(HIBERNATE_DIRECTORY));
        settings.put(DATABASE_CONCEPT_KEY, current.getProperty(DATABASE_CONCEPT_KEY));
        settings.put(DATABASE_EXTENSION_KEY, current.getProperty(DATABASE_EXTENSION_KEY));
        return settings;
    }

    @Override
    public boolean checkSchemaCreation(Map<String, Object> settings) {
        String path = (String) settings.get(DATABASE_KEY);
        File f = new File(path + ".h2.db");
        if (f.exists()) {
            return checkTableSize(settings);
        } else {
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean mkdirs = parent.mkdirs();
                if (!mkdirs) {
                    return false;
                }
            }
            try {
                boolean created = f.createNewFile();
                if (created) {
                    return f.delete();
                }
                return created;
            } catch (IOException ex) {
                throw new ConfigurationError(ex);
            }
        }
    }

    private boolean checkTableSize(Map<String, Object> settings) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = openConnection(settings);
            stmt = conn.createStatement();
            stmt.execute("show tables");
            ResultSet resultSet = stmt.getResultSet();
            resultSet.last();
            return resultSet.getRow() <= 1;
        } catch (SQLException ex) {
            throw new ConfigurationError(ex);
        } finally {
            close(conn);
            close(stmt);
        }
    }

    @Override
    public void prepare(Map<String, Object> settings) {
        initGeoDB(settings);
    }

    @Override
    protected String toURL(Map<String, Object> settings) {
        return String.format(JDBC_URL_FORMAT, settings.get(DATABASE_KEY));
    }

    @Override
    protected String[] parseURL(String url) {
        return new String[0];
    }

    @Override
    public boolean needsSchema() {
        return true;
    }

}
