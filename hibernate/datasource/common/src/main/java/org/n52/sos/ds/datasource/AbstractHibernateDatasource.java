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
package org.n52.sos.ds.datasource;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.BooleanSettingDefinition;
import org.n52.sos.config.settings.ChoiceSettingDefinition;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.ds.DatasourceCallback;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.SessionFactoryProvider;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.SQLConstants;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public abstract class AbstractHibernateDatasource extends AbstractHibernateCoreDatasource implements SQLConstants {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractHibernateDatasource.class);

    // protected static final String USERNAME_TITLE = "User Name";
    //
    // protected static final String PASSWORD_TITLE = "Password";
    //
    // protected static final String DATABASE_KEY = "jdbc.database";
    //
    // protected static final String DATABASE_TITLE = "Database";
    //
    // protected static final String DATABASE_DESCRIPTION =
    // "Set this to the name of the database you want to use for SOS.";
    //
    // protected static final String DATABASE_DEFAULT_VALUE = "sos";
    //
    // protected static final String HOST_KEY = "jdbc.host";
    //
    // protected static final String HOST_TITLE = "Host";
    //
    // protected static final String HOST_DESCRIPTION =
    // "Set this to the IP/net location of the database server. The default value for is \"localhost\".";
    //
    // protected static final String HOST_DEFAULT_VALUE = "localhost";
    //
    // protected static final String PORT_KEY = "jdbc.port";
    //
    // protected static final String PORT_TITLE = "Database Port";

    protected static final String SCHEMA_KEY = HibernateConstants.DEFAULT_SCHEMA;

    protected static final String SCHEMA_TITLE = "Schema";

    protected static final String SCHEMA_DESCRIPTION =
            "Qualifies unqualified table names with the given schema in generated SQL.";

    protected static final String SCHMEA_DEFAULT_VALUE = "public";

//    protected static final String OLD_CONCEPT_TITLE = "Old observation concept";
//
//    protected static final String OLD_CONCEPT_DESCRIPTION =
//            "Should the database support the old observation concept or exists the older database model? The new default concept uses series in the observation table instead of direct relations to procedure, observedProperty and featureOfInterest!";
//
//    protected static final String OLD_CONCEPT_KEY = "sos.oldConcept";
//
//    protected static final boolean OLD_CONCEPT_DEFAULT_VALUE = false;

    protected static final String DATABASE_CONCEPT_TITLE = "Database concept";

    protected static final String DATABASE_CONCEPT_DESCRIPTION = "Select the database concept this SOS should use";

    protected static final String DATABASE_CONCEPT_KEY = "sos.database.concept";

    protected static final String DATABASE_CONCEPT_DEFAULT_VALUE = DatabaseConcept.SERIES_CONCEPT.name();

    protected static final String TRANSACTIONAL_TITLE = "Transactional Profile";

    protected static final String TRANSACTIONAL_DESCRIPTION = "Should the database support the transactional profile?";

    protected static final String TRANSACTIONAL_KEY = "sos.transactional";

    protected static final boolean TRANSACTIONAL_DEFAULT_VALUE = true;

    protected static final String MULTILINGUALISM_TITLE = "Multilingualism support";

    protected static final String MULTILINGUALISM_DESCRIPTION = "Should the database support multilingualism?";

    protected static final String MULTILINGUALISM_KEY = "sos.language";

    protected static final boolean MULTILINGUALISM_DEFAULT_VALUE = false;

    protected static final String USERNAME_KEY = HibernateConstants.CONNECTION_USERNAME;

    protected static final Boolean PROVIDED_JDBC_DRIVER_DEFAULT_VALUE = false;

    protected static final String PROVIDED_JDBC_DRIVER_TITLE = "Provided JDBC driver";

    protected static final String PROVIDED_JDBC_DRIVER_DESCRIPTION =
            "Is the JDBC driver provided and should not be derigistered during shutdown?";

    protected static final String PROVIDED_JDBC_DRIVER_KEY = "sos.jdbc.provided";

    protected static final String BATCH_SIZE_KEY = "jdbc.batch.size";

    protected static final String BATCH_SIZE_TITLE = "Batch size";

    protected static final String BATCH_SIZE_DESCRIPTION = "Database insert/update batch size";

    protected static final Integer BATCH_SIZE_DEFAULT_VALUE = 20;

    private Dialect dialect;

//    private final BooleanSettingDefinition oldConceptDefiniton = createOldConceptDefinition();

    private final ChoiceSettingDefinition databaseConceptDefinition = createDatabaseConceptDefinition();

    private final BooleanSettingDefinition transactionalDefiniton = createTransactionalDefinition();

    private boolean transactionalDatasource = true;

    private final BooleanSettingDefinition multilingualismDefinition = createMultilingualismDefinition();

    private boolean multilingualismDatasource = true;

    /**
     * Create settings definition for username
     *
     * @return Username settings definition
     */
    protected StringSettingDefinition createUsernameDefinition() {
        return new StringSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_1)
                .setKey(USERNAME_KEY).setTitle(USERNAME_TITLE);
    }

    // /**
    // * Create settings definition for username
    // *
    // * @return Username settings definition
    // */
    // protected StringSettingDefinition createUsernameDefinition() {
    // return new
    // StringSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_1)
    // .setKey(USERNAME_KEY).setTitle(USERNAME_TITLE);
    // }
    //
    // /**
    // * Create settings definition for password
    // *
    // * @return Password settings definition
    // */
    // protected StringSettingDefinition createPasswordDefinition() {
    // return new
    // StringSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_2)
    // .setKey(PASSWORD_KEY).setTitle(PASSWORD_TITLE);
    // }
    //
    // /**
    // * Create settings definition for database name
    // *
    // * @return database name settings definition
    // */
    // protected StringSettingDefinition createDatabaseDefinition() {
    // return new
    // StringSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_3)
    // .setKey(DATABASE_KEY).setTitle(DATABASE_TITLE).setDescription(DATABASE_DESCRIPTION)
    // .setDefaultValue(DATABASE_DEFAULT_VALUE);
    // }
    //
    // /**
    // * Create settings definition for host
    // *
    // * @return Host settings definition
    // */
    // protected StringSettingDefinition createHostDefinition() {
    // return new
    // StringSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_4)
    // .setKey(HOST_KEY).setTitle(HOST_TITLE).setDescription(HOST_DESCRIPTION)
    // .setDefaultValue(HOST_DEFAULT_VALUE);
    // }
    //
    // /**
    // * Create settings definition for port
    // *
    // * @return Port settings definition
    // */
    // protected IntegerSettingDefinition createPortDefinition() {
    // return new
    // IntegerSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_5)
    // .setKey(PORT_KEY).setTitle(PORT_TITLE);
    // }

    /**
     * Create settings definition for database schema
     *
     * @return Database schema settings definition
     */
    protected StringSettingDefinition createSchemaDefinition() {
        return new StringSettingDefinition().setGroup(ADVANCED_GROUP).setOrder(SettingDefinitionProvider.ORDER_1)
                .setKey(SCHEMA_KEY).setTitle(SCHEMA_TITLE).setDescription(SCHEMA_DESCRIPTION)
                .setDefaultValue(SCHMEA_DEFAULT_VALUE);
    }

    // /**
    // * Create settings definition for old concept
    // *
    // * @return Old concept settings definition
    // */
    // protected BooleanSettingDefinition createOldConceptDefinition() {
    // return new
    // BooleanSettingDefinition().setDefaultValue(OLD_CONCEPT_DEFAULT_VALUE).setTitle(OLD_CONCEPT_TITLE)
    // .setDescription(OLD_CONCEPT_DESCRIPTION).setGroup(ADVANCED_GROUP)
    // .setOrder(SettingDefinitionProvider.ORDER_2).setKey(OLD_CONCEPT_KEY);
    // }

    protected ChoiceSettingDefinition createDatabaseConceptDefinition() {
        ChoiceSettingDefinition choiceSettingDefinition = new ChoiceSettingDefinition();
        choiceSettingDefinition.setTitle(DATABASE_CONCEPT_TITLE).setDescription(DATABASE_CONCEPT_DESCRIPTION)
                .setGroup(ADVANCED_GROUP).setOrder(SettingDefinitionProvider.ORDER_2).setKey(DATABASE_CONCEPT_KEY);
        choiceSettingDefinition.addOption(DatabaseConcept.SERIES_CONCEPT.name(),
                DatabaseConcept.SERIES_CONCEPT.getDisplayName());
        choiceSettingDefinition.addOption(DatabaseConcept.EREPORTING_CONCEPT.name(),
                DatabaseConcept.EREPORTING_CONCEPT.getDisplayName());
        choiceSettingDefinition.addOption(DatabaseConcept.OLD_CONCEPT.name(),
                DatabaseConcept.OLD_CONCEPT.getDisplayName());
        choiceSettingDefinition.setDefaultValue(DatabaseConcept.SERIES_CONCEPT.name());
        return choiceSettingDefinition;
    }

    /**
     * Create settings definition for transactional support
     *
     * @return Transactional support settings definition
     */
    protected BooleanSettingDefinition createTransactionalDefinition() {
        return new BooleanSettingDefinition().setDefaultValue(TRANSACTIONAL_DEFAULT_VALUE)
                .setTitle(TRANSACTIONAL_TITLE).setDescription(TRANSACTIONAL_DESCRIPTION).setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_3).setKey(TRANSACTIONAL_KEY);
    }

    protected BooleanSettingDefinition createMultilingualismDefinition() {
        return new BooleanSettingDefinition().setDefaultValue(MULTILINGUALISM_DEFAULT_VALUE)
                .setTitle(MULTILINGUALISM_TITLE).setDescription(MULTILINGUALISM_DESCRIPTION).setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_3).setKey(MULTILINGUALISM_KEY);
    }

    /**
     * Create settings definition for JDBC driver
     *
     * @return JDBC driver settings definition
     */
    protected BooleanSettingDefinition createProvidedJdbcDriverDefinition() {
        return new BooleanSettingDefinition().setDefaultValue(PROVIDED_JDBC_DRIVER_DEFAULT_VALUE)
                .setTitle(PROVIDED_JDBC_DRIVER_TITLE).setDescription(PROVIDED_JDBC_DRIVER_DESCRIPTION)
                .setDefaultValue(PROVIDED_JDBC_DRIVER_DEFAULT_VALUE).setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_5).setKey(PROVIDED_JDBC_DRIVER_KEY);
    }

    // /**
    // * Create settings definition for minimal connection pool size
    // *
    // * @return Minimal connection pool size settings definition
    // */
    // protected IntegerSettingDefinition createMinPoolSizeDefinition() {
    // return new
    // IntegerSettingDefinition().setGroup(ADVANCED_GROUP).setOrder(SettingDefinitionProvider.ORDER_6)
    // .setKey(MIN_POOL_SIZE_KEY).setTitle(MIN_POOL_SIZE_TITLE).setDescription(MIN_POOL_SIZE_DESCRIPTION)
    // .setDefaultValue(MIN_POOL_SIZE_DEFAULT_VALUE);
    // }
    //
    // /**
    // * Create settings definition for maximal connection pool size
    // *
    // * @return Maximal connection pool size settings definition
    // */
    // protected IntegerSettingDefinition createMaxPoolSizeDefinition() {
    // return new
    // IntegerSettingDefinition().setGroup(ADVANCED_GROUP).setOrder(SettingDefinitionProvider.ORDER_7)
    // .setKey(MAX_POOL_SIZE_KEY).setTitle(MAX_POOL_SIZE_TITLE).setDescription(MAX_POOL_SIZE_DESCRIPTION)
    // .setDefaultValue(MAX_POOL_SIZE_DEFAULT_VALUE);
    // }

    /**
     * Create settings definition for JDBC batch size
     *
     * @return JDBC batch size settings definition
     */
    protected IntegerSettingDefinition createBatchSizeDefinition() {
        return new IntegerSettingDefinition().setGroup(ADVANCED_GROUP).setOrder(SettingDefinitionProvider.ORDER_8)
                .setKey(BATCH_SIZE_KEY).setTitle(BATCH_SIZE_TITLE).setDescription(BATCH_SIZE_DESCRIPTION)
                .setDefaultValue(BATCH_SIZE_DEFAULT_VALUE);
    }

    /**
     * Get custom configuration from datasource settings
     *
     * @param settings
     *            Datasource settings to create custom configuration from
     * @return Custom configuration from datasource settings
     */
    public CustomConfiguration getConfig(Map<String, Object> settings) {
        CustomConfiguration config = new CustomConfiguration();
        config.configure("/sos-hibernate.cfg.xml");
        config.addDirectory(resource(HIBERNATE_MAPPING_CORE_PATH));
        config.addDirectory(resource(getDatabaseConceptMappingDirectory(settings)));
        if (isTransactionalDatasource()) {
            Boolean transactional = (Boolean) settings.get(this.transactionalDefiniton.getKey());
            if (transactional != null && transactional.booleanValue()) {
                config.addDirectory(resource(HIBERNATE_MAPPING_TRANSACTIONAL_PATH));
            }
        }
        if (isMultiLanguageDatasource()) {
            Boolean multiLanguage = (Boolean) settings.get(this.multilingualismDefinition.getKey());
            if (multiLanguage != null && multiLanguage.booleanValue()) {
                config.addDirectory(resource(HIBERNATE_MAPPING_I18N_PATH));
            }
        }
        if (isSetSchema(settings)) {
            Properties properties = new Properties();
            properties.put(HibernateConstants.DEFAULT_SCHEMA, settings.get(HibernateConstants.DEFAULT_SCHEMA));
            config.addProperties(properties);
        }
        config.buildMappings();
        return config;
    }

    protected String getDatabaseConceptMappingDirectory(Map<String, Object> settings) {
        String concept = (String)settings.get(this.databaseConceptDefinition.getKey());
        switch (DatabaseConcept.valueOf(concept)) {
        case SERIES_CONCEPT:
            return HIBERNATE_MAPPING_SERIES_CONCEPT_OBSERVATION_PATH;
        case EREPORTING_CONCEPT:
            return HIBERNATE_MAPPING_EREPORTING_CONCEPT_OBSERVATION_PATH;
        case OLD_CONCEPT:
            return HIBERNATE_MAPPING_OLD_CONCEPT_OBSERVATION_PATH;
        default:
            return HIBERNATE_MAPPING_SERIES_CONCEPT_OBSERVATION_PATH;
        }
    }

//    /**
//     * Check if this datasource supported the series concept
//     *
//     * @param settings
//     *            Datasource settings
//     * @return <code>true</code>, if this datasource supported the series
//     *         concept
//     */
//    private boolean isSeriesConceptDatasource(Map<String, Object> settings) {
//        return !isOldConceptDatasource(settings);
//    }
//
//    /**
//     * Check if this datasource supported the old concept
//     *
//     * @param settings
//     *            Datasource settings
//     * @return <code>true</code>, if this datasource supported the old concept
//     */
//    private boolean isOldConceptDatasource(Map<String, Object> settings) {
//        Boolean oldConcept = (Boolean) settings.get(this.oldConceptDefiniton.getKey());
//        return oldConcept != null && oldConcept;
//    }

    /**
     * Get File from resource String
     *
     * @param resource
     *            Resource String
     * @return File from resource String
     */
    protected File resource(String resource) {
        try {
            return new File(getClass().getResource(resource).toURI());
        } catch (URISyntaxException ex) {
            throw new ConfigurationException(ex);
        }
    }

    @Override
    public String[] createSchema(Map<String, Object> settings) {
        String[] script = getConfig(settings).generateSchemaCreationScript(getDialectInternal());
        String[] pre = getPreSchemaScript();
        String[] post = getPostSchemaScript();

        script =
                (pre == null) ? (post == null) ? script : concat(script, post) : (post == null) ? concat(pre, script)
                        : concat(pre, script, post);

        return checkCreateSchema(script);
    }

    @Override
    public String[] dropSchema(Map<String, Object> settings) {
        Connection conn = null;
        try {
            conn = openConnection(settings);
            DatabaseMetadata metadata = getDatabaseMetadata(conn, getConfig(settings));
            String[] dropScript =
                    checkDropSchema(getConfig(settings).generateDropSchemaScript(getDialectInternal(), metadata));
            return dropScript;
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    @Override
    public String[] updateSchema(Map<String, Object> settings) {
        Connection conn = null;
        try {
            conn = openConnection(settings);
            DatabaseMetadata metadata = getDatabaseMetadata(conn, getConfig(settings));
            List<SchemaUpdateScript> upSchema =
                    getConfig(settings).generateSchemaUpdateScriptList(getDialectInternal(), metadata);
            return SchemaUpdateScript.toStringArray(upSchema);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    @Override
    public void validateSchema(Map<String, Object> settings) {
        Connection conn = null;
        try {
            conn = openConnection(settings);
            DatabaseMetadata metadata = getDatabaseMetadata(conn, getConfig(settings));
            getConfig(settings).validateSchema(getDialectInternal(), metadata);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } catch (HibernateException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    protected DatabaseMetadata getDatabaseMetadata(Connection conn, CustomConfiguration customConfiguration)
            throws SQLException {
        return new DatabaseMetadata(conn, getDialectInternal(), customConfiguration, true);
    }

    @Override
    public boolean checkIfSchemaExists(Map<String, Object> settings) {
        Connection conn = null;
        try {
            /* check if any of the needed tables is existing */
            conn = openConnection(settings);
            DatabaseMetadata metadata = getDatabaseMetadata(conn, getConfig(settings));
            Iterator<Table> iter = getConfig(settings).getTableMappings();
            String catalog = checkCatalog(conn);
            String schema = checkSchema((String) settings.get(SCHEMA_KEY), catalog, conn);
            while (iter.hasNext()) {
                Table table = iter.next();
                if (table.isPhysicalTable()
                        && metadata.isTable(table.getQuotedName())
                        && metadata.getTableMetadata(table.getName(), schema, catalog, table.isQuoted()) != null) {
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    protected String checkSchema(String schema, String catalog, Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        if (metaData != null) {
            ResultSet rs = metaData.getSchemas();
            while (rs.next()) {
                if (StringHelper.isNotEmpty(rs.getString("TABLE_SCHEM")) && rs.getString("TABLE_SCHEM").equalsIgnoreCase(schema)) {
                    return rs.getString("TABLE_SCHEM");
                }
            }
            if (StringHelper.isNotEmpty(schema)) {
                throw new ConfigurationException(String.format("Requested schema (%s) is not contained in the database!", schema));
            }
        }
        return null;
    }

    private String checkCatalog(Connection conn) throws SQLException {
        return conn.getCatalog();
    }

    @Override
    public void execute(String[] sql, Map<String, Object> settings) throws HibernateException {
        Connection conn = null;
        try {
            conn = openConnection(settings);
            execute(sql, conn);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    @Override
    public void validateConnection(Map<String, Object> settings) {
        Connection conn = null;
        try {
            conn = openConnection(settings);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    @Override
    public boolean needsSchema() {
        return true;
    }

    @Override
    public void validatePrerequisites(Map<String, Object> settings) {
        Connection conn = null;
        try {
            conn = openConnection(settings);
            DatabaseMetadata metadata = getDatabaseMetadata(conn, getConfig(settings));
            validatePrerequisites(conn, metadata, settings);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(conn);
        }
    }

    @Override
    public void validateConnection(Properties current, Map<String, Object> changed) {
        validateConnection(mergeProperties(current, changed));
    }

    @Override
    public void validatePrerequisites(Properties current, Map<String, Object> changed) {
        validatePrerequisites(mergeProperties(current, changed));
    }

    @Override
    public void validateSchema(Properties current, Map<String, Object> changed) {
        validateSchema(mergeProperties(current, changed));
    }

    @Override
    public boolean checkIfSchemaExists(Properties current, Map<String, Object> changed) {
        return checkIfSchemaExists(mergeProperties(current, changed));
    }

    @Override
    public void checkPostCreation(Properties properties) {
        if (checkIfExtensionDirectoryExists()) {
            StringBuilder builder =
                    new StringBuilder(properties.getProperty(SessionFactoryProvider.HIBERNATE_DIRECTORY));
            builder.append(SessionFactoryProvider.PATH_SEPERATOR).append(HIBERNATE_MAPPING_EXTENSION_READONLY);
            properties.put(SessionFactoryProvider.HIBERNATE_DIRECTORY, builder.toString());
        }
    }

    private boolean checkIfExtensionDirectoryExists() {
        URL dirUrl = Thread.currentThread().getContextClassLoader().getResource(HIBERNATE_MAPPING_EXTENSION_READONLY);
        if (dirUrl != null) {
            try {
                return new File(URLDecoder.decode(dirUrl.getPath(), Charset.defaultCharset().toString())).exists();
            } catch (UnsupportedEncodingException e) {
                throw new ConfigurationException("Unable to encode directory URL " + dirUrl + "!");
            }
        }
        return false;
    }
    
    protected Set<SettingDefinition<?,?>> filter(Set<SettingDefinition<?,?>> definitions, Set<String> keysToExclude) {
        Iterator<SettingDefinition<?, ?>> iterator = definitions.iterator();
        while(iterator.hasNext()) {
            if (keysToExclude.contains(iterator.next().getKey())) {
                iterator.remove();
            }
        }
        return definitions;
    }

    /**
     * Get internal Hibernate dialect
     *
     * @return Hibernate dialect
     */
    protected Dialect getDialectInternal() {
        if (dialect == null) {
            dialect = createDialect();
        }
        return dialect;
    }

    /**
     * Execute SQL script
     *
     * @param sql
     *            SQL script to execute
     * @param conn
     *            SQL connection
     * @throws HibernateException
     *             If an error occurs
     */
    protected void execute(String[] sql, Connection conn) throws HibernateException {
        Statement stmt = null;
        String lastCmd = null;
        try {
            stmt = conn.createStatement();
            LOG.debug("Start executing SQL commands: ");
            for (String cmd : sql) {
                lastCmd = cmd;
                LOG.debug("Execute: {}", cmd);
                stmt.execute(cmd);
            }
        } catch (SQLException ex) {
            if (lastCmd != null) {
                throw new ConfigurationException(ex.getMessage() + ". Command: " + lastCmd, ex);
            } else {
                throw new ConfigurationException(ex);
            }
        } finally {
            close(stmt);
        }
    }

    /**
     * Close SQL connection
     *
     * @param conn
     *            SQL connection to close
     */
    protected void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOG.error("Error closing connection", e);
            }
        }
    }

    /**
     * Close SQL statement
     *
     * @param stmt
     *            SQL statement to close
     */
    protected void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                LOG.error("Error closing statement", e);
            }
        }
    }

    /**
     * Add mapping files directories to properties
     *
     * @param settings
     *            Datasource settings
     * @param p
     *            Datasource properties
     */
    protected void addMappingFileDirectories(Map<String, Object> settings, Properties p) {
        StringBuilder builder = new StringBuilder();
        builder.append(HIBERNATE_MAPPING_CORE_PATH);
        builder.append(SessionFactoryProvider.PATH_SEPERATOR).append(
                getDatabaseConceptMappingDirectory(settings));
        if (isTransactionalDatasource()) {
            Boolean t = (Boolean) settings.get(transactionalDefiniton.getKey());
            if (t != null && t) {
                builder.append(SessionFactoryProvider.PATH_SEPERATOR).append(HIBERNATE_MAPPING_TRANSACTIONAL_PATH);
            }
        }
        if (isMultiLanguageDatasource()) {
            Boolean t = (Boolean) settings.get(multilingualismDefinition.getKey());
            if (t != null && t) {
                builder.append(SessionFactoryProvider.PATH_SEPERATOR).append(HIBERNATE_MAPPING_I18N_PATH);
            }
        }
        p.put(SessionFactoryProvider.HIBERNATE_DIRECTORY, builder.toString());
    }

    protected ChoiceSettingDefinition getDatabaseConceptDefinition() {
        return databaseConceptDefinition;
    }

    /**
     * Check if properties contains transactional mapping path
     *
     * @param properties
     *            Datasource properties
     * @return <code>true</code>, if properties contains transactional mapping
     *         path
     */
    protected boolean isTransactional(Properties properties) {
        String p = properties.getProperty(SessionFactoryProvider.HIBERNATE_DIRECTORY);
        return p == null || p.contains(HIBERNATE_MAPPING_TRANSACTIONAL_PATH);
    }

    /**
     * Get transactional setting definition
     *
     * @return Transactional setting definition
     */
    protected BooleanSettingDefinition getTransactionalDefiniton() {
        return transactionalDefiniton;
    }

    protected boolean isMultiLanguage(Properties properties) {
        String p = properties.getProperty(SessionFactoryProvider.HIBERNATE_DIRECTORY);
        return p == null || p.contains(HIBERNATE_MAPPING_I18N_PATH);
    }

    protected BooleanSettingDefinition getMulitLanguageDefiniton() {
        return multilingualismDefinition;
    }

    /**
     * Concatenate two arrays
     *
     * @param first
     *            First array
     * @param rest
     *            The other array
     * @return Concatenated array
     */
    private <T> T[] concat(T[] first, T[]... rest) {
        int length = first.length;
        for (int i = 0; i < rest.length; ++i) {
            length += rest[i].length;
        }
        T[] result = Arrays.copyOf(first, length);
        int offset = first.length;
        for (int i = 0; i < rest.length; ++i) {
            System.arraycopy(rest[i], 0, result, offset, rest[i].length);
            offset += rest[i].length;
        }
        return result;
    }

    /**
     * Get the schema script before the database schema is created
     *
     * @return script to run before the schema creation
     */
    protected String[] getPreSchemaScript() {
        return null;
    }

    /**
     * Get the schema script after the database schema is created
     *
     * @return script to run after the schema creation
     */
    protected String[] getPostSchemaScript() {
        return null;
    }

    protected boolean isSetSchema(Map<String, Object> settings) {
        if (settings.containsKey(HibernateConstants.DEFAULT_SCHEMA)) {
            return StringHelper.isNotEmpty((String) settings.get(HibernateConstants.DEFAULT_SCHEMA));
        }
        return false;
    }

    protected String getSchema(Map<String, Object> settings) {
        if (isSetSchema(settings)) {
            return (String) settings.get(HibernateConstants.DEFAULT_SCHEMA) + ".";
        }
        return "";
    }

    /**
     * Check if the datasource is transactional
     *
     * @return <code>true</code>, if it is a transactionalDatasource
     */
    public boolean isTransactionalDatasource() {
        return transactionalDatasource;
    }

    /**
     * Set transactional datasource flag
     *
     * @param transactionalDatasource
     *            the transactionalDatasource flag to set
     */
    public void setTransactional(boolean transactionalDatasource) {
        this.transactionalDatasource = transactionalDatasource;
    }

    /**
     * @return the multi language
     */
    public boolean isMultiLanguageDatasource() {
        return multilingualismDatasource;
    }

    /**
     * @param multi
     *            language the multi language to set
     */
    public void setMultiLangugage(boolean multiLanguageDatasource) {
        this.multilingualismDatasource = multiLanguageDatasource;
    }

    /**
     * Remove duplicated foreign key definition for table observationHasOffering
     * otherwise database model creation fails in Oracle
     *
     * @param script
     *            Create and not checked script.
     * @return Checked script without duplicate foreign key for
     *         observationHasOffering
     */
    protected String[] checkCreateSchema(String[] script) {
        return checkScriptForGeneratedAndDuplicatedEntries(script);
    }

    /**
     * Remove generated foreign key definition and duplicated entries.
     *
     * @param script
     *            Not checked script.
     * @return Checked script without duplicate foreign key
     */
    protected String[] checkScriptForGeneratedAndDuplicatedEntries(String[] script) {
        // creates upper case hexStrings from table names hashCode() with prefix
        // 'FK'
        Set<String> generatedForeignKeys =
                Sets.newHashSet(getGeneratedForeignKeyFor("observationHasOffering"),
                        getGeneratedForeignKeyFor("relatedFeatureHasRole"),
                        getGeneratedForeignKeyFor("offeringAllowedFeatureType"),
                        getGeneratedForeignKeyFor("offeringAllowedObservationType"));
        List<String> checkedSchema = Lists.newLinkedList();
        for (String string : script) {
            if (string.startsWith("alter table")) {
                boolean hasNoGeneratedKey = true;
                for (String key : generatedForeignKeys) {
                    if (string.contains(key)) {
                        hasNoGeneratedKey = false;
                    }
                }
                if (hasNoGeneratedKey) {
                    checkedSchema.add(string.trim());
                }
            } else {
                checkedSchema.add(string.trim());
            }
        }
        // eliminate duplicated lines while keeping the order
        Set<String> nonDublicated = Sets.newLinkedHashSet(checkedSchema);
        return nonDublicated.toArray(new String[nonDublicated.size()]);
    }

    /**
     * Create the beginning character of a generated foreign key from a table
     * name hasCode()
     *
     * @param string
     *            Table name
     * @return Beginning characters of a generated foreign key like
     *         "FK + table name hasCode()"
     */
    private String getGeneratedForeignKeyFor(String tableName) {
        return new StringBuilder("FK").append(Integer.toHexString(tableName.hashCode()).toUpperCase()).toString();
    }

    /**
     * Check if drop schema contains alter table ... drop constraint ... . Due
     * to dynamic generation some constraints are generated and differ.
     *
     * @param dropSchema
     *            Schema to check
     * @return Checked schema
     */
    protected String[] checkDropSchema(String[] dropSchema) {
        return checkScriptForGeneratedAndDuplicatedEntries(dropSchema);
    }

    @Override
    public DatasourceCallback getCallback() {
        return DatasourceCallback.nullCallback();
    }

    @Override
    public void prepare(Map<String, Object> settings) {

    }

    @Override
    public boolean isPostCreateSchema() {
        return false;
    }

    @Override
    public void executePostCreateSchema(Map<String, Object> databaseSettings) {
    }

    @Override
    public String getConnectionProviderIdentifier() {
        return HibernateDatasourceConstants.ORM_CONNECTION_PROVIDER_IDENTIFIER;
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    /**
     * Gets the qualified name of the driver class.
     *
     * @return the driver class.
     */
    protected abstract String getDriverClass();

    // /**
    // * Parse datasource properties to map
    // *
    // * @param current
    // * Current datasource properties
    // * @return Map with String key and Object value
    // */
    // protected abstract Map<String, Object>
    // parseDatasourceProperties(Properties current);

    /**
     * Check if the required extensions are available
     *
     * @param con
     *            SQL connection
     * @param metadata
     *            Current database metadata
     * @param settings
     *            Datasource settings
     */
    protected abstract void validatePrerequisites(Connection con, DatabaseMetadata metadata,
            Map<String, Object> settings);

    /**
     * Create a new Hibernate dialect
     *
     * @return Hibernate dialect
     */
    protected abstract Dialect createDialect();

    /**
     * Open a new SQL connection
     *
     * @param settings
     *            Datasource setting: URL, username, passsword, database, ...
     * @return New SQL connection
     * @throws SQLException
     *             If the SQL connection creation fails
     */
    protected abstract Connection openConnection(Map<String, Object> settings) throws SQLException;

}
