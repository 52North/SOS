/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.datasource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.boot.Metadata;
import org.n52.faroe.AbstractSettingDefinition;
import org.n52.faroe.ConfigurationError;
import org.n52.faroe.SettingDefinition;
import org.n52.faroe.settings.StringSettingDefinition;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.google.common.collect.Sets;


/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class H2FileDatasource extends AbstractH2Datasource {
    private static final String DIALECT = "H2/GeoDB (file based)";

    private static final Pattern JDBC_URL_PATTERN = Pattern
            .compile("^jdbc:h2:(.+)$");

    private static final String JDBC_URL_FORMAT = "jdbc:h2:%s";

    private static final String USER_HOME = "user.home";

    private static final String SOS = "sos";

    private static final String DESCRIPTION = "Set this to the name/path of the database you want to use for SOS.";

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
        AbstractSettingDefinition<String> h2Database = createDatabaseDefinition().setDescription(
                DESCRIPTION).setDefaultValue(
                System.getProperty(USER_HOME) + File.separator + SOS);
        return Sets.<SettingDefinition<?>> newHashSet(h2Database, getDatabaseConceptDefinition(),
                getDatabaseExtensionDefinition(), getFeatureConceptDefinition());
    }

    private StringSettingDefinition getDatabaseDefinition() {
        StringSettingDefinition def = createDatabaseDefinition();
        def.setDescription(DESCRIPTION);
        def.setDefaultValue(System.getProperty(USER_HOME) + File.separator + SOS);
        return def;
    }

    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
        Properties p = new Properties();
        p.put(HibernateConstants.CONNECTION_URL, toURL(settings));
        p.put(HibernateConstants.DRIVER_CLASS, H2_DRIVER_CLASS);
        p.put(HibernateConstants.DIALECT, H2_DIALECT_CLASS);
        p.put(HibernateConstants.CONNECTION_USERNAME, DEFAULT_USERNAME);
        p.put(HibernateConstants.CONNECTION_PASSWORD, DEFAULT_PASSWORD);
        p.put(HibernateConstants.CONNECTION_POOL_SIZE, "1");
        p.put(HibernateConstants.CONNECTION_RELEASE_MODE, HibernateConstants.CONNECTION_RELEASE_MODE_AFTER_TRANSACTION);
        p.put(HibernateConstants.CURRENT_SESSION_CONTEXT, HibernateConstants.THREAD_LOCAL_SESSION_CONTEXT);
        p.put(DATABASE_CONCEPT_KEY, settings.get(DATABASE_CONCEPT_KEY));
        p.put(DATABASE_EXTENSION_KEY, settings.get(DATABASE_EXTENSION_KEY));
        addMappingFileDirectories(settings, p);
        return p;
    }

    @Override
    public Map<String, Object> parseDatasourceProperties(Properties current) {
        Map<String, Object> settings = new HashMap<>(5);
        Matcher matcher = JDBC_URL_PATTERN.matcher(current.getProperty(HibernateConstants.CONNECTION_URL));
        matcher.find();
        settings.put(DATABASE_KEY, matcher.group(1));
        settings.put(HIBERNATE_DIRECTORY, current.get(HIBERNATE_DIRECTORY));
        settings.put(DATABASE_CONCEPT_KEY,  current.getProperty(DATABASE_CONCEPT_KEY));
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
    protected void validatePrerequisites(Connection con, Metadata metadata, Map<String, Object> settings) {
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
    public void validateSchema(Map<String, Object> settings) {
        /* can not be validated */
    }

    @Override
    public void validateSchema(Properties current,
                               Map<String, Object> changed) {
        /* can not be validated */
    }
}
