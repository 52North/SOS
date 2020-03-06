/**
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
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 * @since 4.0.0
 */
public abstract class AbstractHibernateDatasource extends AbstractHibernateCoreDatasource implements SQLConstants {
	
    private static final Logger LOG = LoggerFactory.getLogger(AbstractHibernateDatasource.class);

    protected static final String SCHEMA_KEY = HibernateConstants.DEFAULT_SCHEMA;

    protected static final String SCHEMA_TITLE = "Schema";

    protected static final String SCHEMA_DESCRIPTION =
            "Qualifies unqualified table names with the given schema in generated SQL.";

    protected static final String SCHMEA_DEFAULT_VALUE = "public";

    protected static final String DATABASE_CONCEPT_TITLE = "Database concept";

    protected static final String DATABASE_CONCEPT_DESCRIPTION = "Select the database concept this SOS should use";

    protected static final String DATABASE_CONCEPT_KEY = "sos.database.concept";

    protected static final String DATABASE_CONCEPT_DEFAULT_VALUE = DatabaseConcept.SERIES_CONCEPT.name();
    
    protected static final String FEATURE_CONCEPT_TITLE = "Feature concept";

    protected static final String FEATURE_CONCEPT_DESCRIPTION = "Select the feature concept this SOS should use";

    protected static final String FEATURE_CONCEPT_KEY = "sos.feature.concept";

    protected static final String FEATURE_CONCEPT_DEFAULT_VALUE = FeatureConcept.DEFAULT_FEATURE_CONCEPT.name();

    protected static final String TRANSACTIONAL_TITLE = "Transactional Profile";

    protected static final String TRANSACTIONAL_DESCRIPTION = "Should the database support the transactional profile?";

    protected static final String TRANSACTIONAL_KEY = "sos.transactional";

    protected static final boolean TRANSACTIONAL_DEFAULT_VALUE = true;
    
    protected static final String MULTILINGUALISM_TITLE = "Multilingualism support";

    protected static final String MULTILINGUALISM_DESCRIPTION = "Should the database support multilingualism?";

    protected static final String MULTILINGUALISM_KEY = "sos.language";

    protected static final boolean MULTILINGUALISM_DEFAULT_VALUE = false;
    
    protected static final String SERIES_METADATA_TITLE = "Series metadata support";

    protected static final String SERIES_METADATA_DESCRIPTION = "Should the database support series metadata? This holds global data for observations of this series.";

    protected static final String SERIES_METADATA_KEY = "sos.series.metadata";

    protected static final boolean SERIES_METADATA_DEFAULT_VALUE = false;
    
    protected static final String CATEGORY_SUPPORT_TITLE = "Category support";

    protected static final String CATEGORY_SUPPORT_DESCRIPTION = "Should the database support category? This is an additional grouping feature to group series.";

    protected static final String CATEGORY_SUPPORT_KEY = "sos.category";

    protected static final boolean CATEGORY_SUPPORT_DEFAULT_VALUE = false;

    protected static final String USERNAME_KEY = HibernateConstants.CONNECTION_USERNAME;

    protected static final Boolean PROVIDED_JDBC_DRIVER_DEFAULT_VALUE = false;

    protected static final String PROVIDED_JDBC_DRIVER_TITLE = "Provided JDBC driver";

    protected static final String PROVIDED_JDBC_DRIVER_DESCRIPTION =
            "Is the JDBC driver provided and should not be deregistered during shutdown?";

    protected static final String PROVIDED_JDBC_DRIVER_KEY = "sos.jdbc.provided";

    protected static final String BATCH_SIZE_KEY = "jdbc.batch.size";

    protected static final String BATCH_SIZE_TITLE = "Batch size";

    protected static final String BATCH_SIZE_DESCRIPTION = "Database insert/update batch size";

    protected static final Integer BATCH_SIZE_DEFAULT_VALUE = 20;

    private Dialect dialect;

    private final ChoiceSettingDefinition databaseConceptDefinition = createDatabaseConceptDefinition();
    
    private final ChoiceSettingDefinition featureConceptDefinition = createFeatureConceptDefinition();

    private final BooleanSettingDefinition transactionalDefiniton = createTransactionalDefinition();

    private boolean transactionalDatasource = true;

    private final BooleanSettingDefinition multilingualismDefinition = createMultilingualismDefinition();

    private boolean multilingualismDatasource = true;
    
    private final BooleanSettingDefinition seriesMetadataDefiniton = createSeriesMetadataDefinition();

    private boolean seriesMetadataDatasource = true;
    
    private final BooleanSettingDefinition categorySupportDefiniton = createCategorySupportDefinition();

    /**
     * Create settings definition for username
     *
     * @return Username settings definition
     */
    protected StringSettingDefinition createUsernameDefinition() {
        return new StringSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_1)
                .setKey(USERNAME_KEY).setTitle(USERNAME_TITLE);
    }

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
        choiceSettingDefinition.addOption(DatabaseConcept.GEOLOGY_LOG_CONCEPT.name(),
                DatabaseConcept.GEOLOGY_LOG_CONCEPT.getDisplayName());
        choiceSettingDefinition.setDefaultValue(DatabaseConcept.SERIES_CONCEPT.name());
        return choiceSettingDefinition;
    }
    
    protected ChoiceSettingDefinition createFeatureConceptDefinition() {
        ChoiceSettingDefinition choiceSettingDefinition = new ChoiceSettingDefinition();
        choiceSettingDefinition.setTitle(FEATURE_CONCEPT_TITLE).setDescription(FEATURE_CONCEPT_DESCRIPTION)
                .setGroup(ADVANCED_GROUP).setOrder(SettingDefinitionProvider.ORDER_3).setKey(FEATURE_CONCEPT_KEY);
        choiceSettingDefinition.addOption(FeatureConcept.DEFAULT_FEATURE_CONCEPT.name(),
                FeatureConcept.DEFAULT_FEATURE_CONCEPT.getDisplayName());
        choiceSettingDefinition.addOption(FeatureConcept.EXTENDED_FEATURE_CONCEPT.name(),
                FeatureConcept.EXTENDED_FEATURE_CONCEPT.getDisplayName());
        choiceSettingDefinition.setDefaultValue(FeatureConcept.DEFAULT_FEATURE_CONCEPT.name());
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
                .setOrder(SettingDefinitionProvider.ORDER_4).setKey(TRANSACTIONAL_KEY);
    }
    
    protected BooleanSettingDefinition createMultilingualismDefinition() {
        return new BooleanSettingDefinition().setDefaultValue(MULTILINGUALISM_DEFAULT_VALUE)
                .setTitle(MULTILINGUALISM_TITLE).setDescription(MULTILINGUALISM_DESCRIPTION).setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_5).setKey(MULTILINGUALISM_KEY);
    }
    
    /**
     * Create settings definition for sereis metadata support
     *
     * @return Series metadata support settings definition
     */
    protected BooleanSettingDefinition createSeriesMetadataDefinition() {
        return new BooleanSettingDefinition().setDefaultValue(SERIES_METADATA_DEFAULT_VALUE)
                .setTitle(SERIES_METADATA_TITLE).setDescription(SERIES_METADATA_DESCRIPTION).setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_6).setKey(SERIES_METADATA_KEY);
    }
    
    /**
     * Create settings definition for category support
     *
     * @return Category support settings definition
     */
    protected BooleanSettingDefinition createCategorySupportDefinition() {
        return new BooleanSettingDefinition().setDefaultValue(CATEGORY_SUPPORT_DEFAULT_VALUE)
                .setTitle(CATEGORY_SUPPORT_TITLE).setDescription(CATEGORY_SUPPORT_DESCRIPTION).setGroup(ADVANCED_GROUP)
                .setOrder(SettingDefinitionProvider.ORDER_7).setKey(CATEGORY_SUPPORT_KEY);
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
                .setOrder(SettingDefinitionProvider.ORDER_9).setKey(PROVIDED_JDBC_DRIVER_KEY);
    }
    
    /**
     * Create settings definition for JDBC batch size
     *
     * @return JDBC batch size settings definition
     */
    protected IntegerSettingDefinition createBatchSizeDefinition() {
        return new IntegerSettingDefinition().setGroup(ADVANCED_GROUP).setOrder(SettingDefinitionProvider.ORDER_10)
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
        for (String resource : getDatabaseConceptMappingDirectory(settings)) {
            config.addDirectory(resource(resource));
        }
        String featureConceptMappingDirectory = getFeatureConceptMappingDirectory(settings);
        if (!Strings.isNullOrEmpty(featureConceptMappingDirectory)) {
            config.addDirectory(resource(featureConceptMappingDirectory));
        }
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
        DatabaseConcept databaseConcept = getDatabaseConcept(settings);
        if (isSeriesMetadataDatasource() && (DatabaseConcept.SERIES_CONCEPT.equals(databaseConcept)
                || DatabaseConcept.EREPORTING_CONCEPT.equals(databaseConcept)
                || DatabaseConcept.GEOLOGY_LOG_CONCEPT.equals(databaseConcept))) {
            Boolean t = (Boolean) settings.get(this.seriesMetadataDefiniton.getKey());
            if (t != null && t) {
                config.addDirectory(resource(HIBERNATE_MAPPING_SERIES_METADATA_PATH));
            }
        }
        if (DatabaseConcept.SERIES_CONCEPT.equals(databaseConcept)) {
            Boolean t = (Boolean) settings.get(categorySupportDefiniton.getKey());
            if (t != null && t) {
                config.addDirectory(resource(HIBERNATE_MAPPING_SERIES_CATEGORY_PATH));
            } else {
                config.addDirectory(resource(HIBERNATE_MAPPING_SERIES_DEFAULT_PATH));
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
    
    protected String getFeatureConceptMappingDirectory(Map<String, Object> settings) {
        String concept = (String)settings.get(this.featureConceptDefinition.getKey());
        if (concept == null || concept.isEmpty()) {
            String hibernateDirectories = (String) settings.get(HibernateDatasourceConstants.HIBERNATE_DIRECTORY);
            concept = FeatureConcept.DEFAULT_FEATURE_CONCEPT.name();
            if (hibernateDirectories.contains(HIBERNATE_MAPPING_FEATURE_PATH)) {
                    concept = FeatureConcept.EXTENDED_FEATURE_CONCEPT.name();
            }
            LOG.error("Setting with key '{}' not found in datasource property file! Setting it using '{}' to '{}'."
                    + " If this produces no error, please add the following setting to your datasource properties: '{}={}'\n\n",
                    featureConceptDefinition.getKey(),
                    HibernateDatasourceConstants.HIBERNATE_DIRECTORY,
                    concept,
                    featureConceptDefinition.getKey(),
                    concept);
        }
        switch (FeatureConcept.valueOf(concept)) {
        case EXTENDED_FEATURE_CONCEPT:
            return HIBERNATE_MAPPING_FEATURE_PATH;
        default:
            return null;
        }
    }

    protected Set<String> getDatabaseConceptMappingDirectory(Map<String, Object> settings) {
//        String concept = (String)settings.get(this.databaseConceptDefinition.getKey());
//        if (concept == null || concept.isEmpty()) {
//            String hibernateDirectories = (String) settings.get(HibernateDatasourceConstants.HIBERNATE_DIRECTORY);
//            concept = DatabaseConcept.SERIES_CONCEPT.name();
//            if (hibernateDirectories.contains(HIBERNATE_MAPPING_EREPORTING_CONCEPT_OBSERVATION_PATH)) {
//                concept = DatabaseConcept.EREPORTING_CONCEPT.name();
//            } else if (hibernateDirectories.contains(HIBERNATE_MAPPING_OLD_CONCEPT_OBSERVATION_PATH)) {
//                concept = DatabaseConcept.OLD_CONCEPT.name();
//            }
//            LOG.error("Setting with key '{}' not found in datasource property file! Setting it using '{}' to '{}'."
//                    + " If this produces no error, please add the following setting to your datasource properties: '{}={}'\n\n",
//                    databaseConceptDefinition.getKey(),
//                    HibernateDatasourceConstants.HIBERNATE_DIRECTORY,
//                    concept,
//                    databaseConceptDefinition.getKey(),
//                    concept);
//        }
        HashSet<String> mappings = Sets.newHashSet(HIBERNATE_MAPPING_PARAMETER_FEATURE_PATH);
        switch (getDatabaseConcept(settings)) {
            case SERIES_CONCEPT:
                mappings.add(HIBERNATE_MAPPING_SERIES_CONCEPT_OBSERVATION_PATH);
                break;
            case EREPORTING_CONCEPT:
                mappings.add(HIBERNATE_MAPPING_EREPORTING_CONCEPT_OBSERVATION_PATH);
                break;
            case OLD_CONCEPT:
                mappings.add(HIBERNATE_MAPPING_OLD_CONCEPT_OBSERVATION_PATH);
                break;
            case GEOLOGY_LOG_CONCEPT:
                mappings.add(HIBERNATE_MAPPING_BRGM_CONCEPT_OBSERVATION_PATH);
                break;
            default:
                mappings.add(HIBERNATE_MAPPING_SERIES_CONCEPT_OBSERVATION_PATH);
                break;
        }
        return mappings;
    }
    
    private DatabaseConcept getDatabaseConcept(Map<String, Object> settings) {
        String concept = (String)settings.get(this.databaseConceptDefinition.getKey());
        if (concept == null || concept.isEmpty()) {
            String hibernateDirectories = (String) settings.get(HibernateDatasourceConstants.HIBERNATE_DIRECTORY);
            concept = DatabaseConcept.SERIES_CONCEPT.name();
            if (hibernateDirectories.contains(HIBERNATE_MAPPING_EREPORTING_CONCEPT_OBSERVATION_PATH)) {
                concept = DatabaseConcept.EREPORTING_CONCEPT.name();
            } else if (hibernateDirectories.contains(HIBERNATE_MAPPING_OLD_CONCEPT_OBSERVATION_PATH)) {
                concept = DatabaseConcept.OLD_CONCEPT.name();
            } else if (hibernateDirectories.contains(HIBERNATE_MAPPING_BRGM_CONCEPT_OBSERVATION_PATH)) {
                concept = DatabaseConcept.GEOLOGY_LOG_CONCEPT.name();
            }
            LOG.error("Setting with key '{}' not found in datasource property file! Setting it using '{}' to '{}'."
                    + " If this produces no error, please add the following setting to your datasource properties: '{}={}'\n\n",
                    databaseConceptDefinition.getKey(), HibernateDatasourceConstants.HIBERNATE_DIRECTORY, concept,
                    databaseConceptDefinition.getKey(), concept);
        }
        return DatabaseConcept.valueOf(concept);
    }

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
            Set<String> nonDublicated = Sets.newLinkedHashSet(Arrays.asList(SchemaUpdateScript.toStringArray(upSchema)));
            return nonDublicated.toArray(new String[nonDublicated.size()]);
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

    protected String checkCatalog(Connection conn) throws SQLException {
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
    public void checkPostCreation(Properties properties, Map<String, Object> settings) {
        if (checkIfExtensionDirectoryExists()) {
            StringBuilder builder =
                    new StringBuilder(properties.getProperty(SessionFactoryProvider.HIBERNATE_DIRECTORY));
            if (DatabaseConcept.SERIES_CONCEPT.equals(getDatabaseConcept(settings))
                    || DatabaseConcept.EREPORTING_CONCEPT.equals(getDatabaseConcept(settings))) {
                Boolean t = (Boolean) settings.get(categorySupportDefiniton.getKey());
                if (t != null && t) {
                    builder.append(SessionFactoryProvider.PATH_SEPERATOR)
                            .append(HIBERNATE_MAPPING_EXTENSION_READONLY_CATEGORY_PATH);
                } else {
                    builder.append(SessionFactoryProvider.PATH_SEPERATOR)
                            .append(HIBERNATE_MAPPING_EXTENSION_READONLY_DEFAULT_PATH);
                }
                properties.put(SessionFactoryProvider.HIBERNATE_DIRECTORY, builder.toString());
            }
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
                if (!Strings.isNullOrEmpty(cmd)) {
                    lastCmd = cmd;
                    LOG.debug("Execute: {}", cmd);
                    stmt.execute(cmd);
                }
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
        for (String path : getDatabaseConceptMappingDirectory(settings)) {
            builder.append(SessionFactoryProvider.PATH_SEPERATOR).append(path);
        }
        String featureConceptMappingDirectory = getFeatureConceptMappingDirectory(settings);
        if (!Strings.isNullOrEmpty(featureConceptMappingDirectory)) {
            builder.append(SessionFactoryProvider.PATH_SEPERATOR).append(
                    featureConceptMappingDirectory);
        }
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
        DatabaseConcept databaseConcept = getDatabaseConcept(settings);
        if (isSeriesMetadataDatasource() && (DatabaseConcept.SERIES_CONCEPT.equals(databaseConcept)
                || DatabaseConcept.EREPORTING_CONCEPT.equals(databaseConcept)
                || DatabaseConcept.GEOLOGY_LOG_CONCEPT.equals(databaseConcept))) {
            Boolean t = (Boolean) settings.get(seriesMetadataDefiniton.getKey());
            if (t != null && t) {
                builder.append(SessionFactoryProvider.PATH_SEPERATOR).append(HIBERNATE_MAPPING_SERIES_METADATA_PATH);
            }
        }
        if (DatabaseConcept.SERIES_CONCEPT.equals(databaseConcept)) {
            Boolean t = (Boolean) settings.get(categorySupportDefiniton.getKey());
            if (t != null && t) {
                builder.append(SessionFactoryProvider.PATH_SEPERATOR).append(HIBERNATE_MAPPING_SERIES_CATEGORY_PATH);
            } else {
                builder.append(SessionFactoryProvider.PATH_SEPERATOR).append(HIBERNATE_MAPPING_SERIES_DEFAULT_PATH);
            }
        }
        p.put(SessionFactoryProvider.HIBERNATE_DIRECTORY, builder.toString());
    }
    
    protected ChoiceSettingDefinition getFeatureConceptDefinition() {
        return featureConceptDefinition;
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
     * Check if properties contains series metadata mapping path
     *
     * @param properties
     *            Datasource properties
     * @return <code>true</code>, if properties contains series metadata mapping
     *         path
     */
    protected boolean isSeriesMetadata(Properties properties) {
        String p = properties.getProperty(SessionFactoryProvider.HIBERNATE_DIRECTORY);
        return p == null || p.contains(HIBERNATE_MAPPING_SERIES_METADATA_PATH);
    }

    /**
     * Get series metadata setting definition
     *
     * @return Series metadata setting definition
     */
    protected BooleanSettingDefinition getSeriesMetadataDefiniton() {
        return seriesMetadataDefiniton;
    }
    
    /**
     * Check if properties contains category mapping path
     *
     * @param properties
     *            Datasource properties
     * @return <code>true</code>, if properties contains category mapping
     *         path
     */
    protected boolean isCategory(Properties properties) {
        String p = properties.getProperty(SessionFactoryProvider.HIBERNATE_DIRECTORY);
        return p == null || p.contains(HIBERNATE_MAPPING_SERIES_CATEGORY_PATH);
    }

    /**
     * Get category setting definition
     *
     * @return Category setting definition
     */
    protected BooleanSettingDefinition getCategoryDefiniton() {
        return categorySupportDefiniton;
    }

    private String[] concat(String[] first, String[]... rest) {
		int length = first.length;
        for (int i = 0; i < rest.length; ++i) {
            length += rest[i].length;
        }
        String[] result = Arrays.copyOf(first, length);
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
     * Check if the datasource is series metadata
     *
     * @return <code>true</code>, if it is a seriesMetadataDatasource
     */
    public boolean isSeriesMetadataDatasource() {
        return seriesMetadataDatasource;
    }

    /**
     * Set series metadata datasource flag
     *
     * @param seriesMetadataDatasource
     *            the seriesMetadataDatasource flag to set
     */
    public void setSeriesMetadataDatasource(boolean seriesMetadataDatasource) {
        this.seriesMetadataDatasource = seriesMetadataDatasource;
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
    
    /**
     * Create quoted string with schema.table
     * @param settings Datasource settings
     * @param conn SQL connection
     * @return {@link List} with table names
     * @throws SQLException If an error occurs while checking catalog/schema
     */
    protected List<String> getQuotedSchemaTableNames(Map<String, Object> settings, Connection conn) throws SQLException {
    	String catalog = checkCatalog(conn);
        String schema = checkSchema((String) settings.get(SCHEMA_KEY), catalog, conn);
        CustomConfiguration config = getConfig(settings);
        Iterator<Table> tables = config.getTableMappings();
        List<String> names = new LinkedList<String>();
        while (tables.hasNext()) {
            Table table = tables.next();
            if (table.isPhysicalTable()) {
                names.add(table.getQualifiedName(getDialectInternal(), catalog, schema));
            }
        }
        return names;
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
     * Workaround for Java {@link DriverManager} issue with more than one
     * registered drivers. Only the first {@link SQLException} is catched and
     * thrown instead of the {@link SQLException} related to the driver which is
     * valid for the URL.
     * 
     * @param url
     *            DB connection URL
     * @param user
     *            User name
     * @param password
     *            Password
     * @throws SQLException
     */
    protected void precheckDriver(String url, String user, String password) throws SQLException {
        Driver driver = DriverManager.getDriver(url);
        if (driver != null) {
            java.util.Properties info = new java.util.Properties();
            if (user != null) {
                info.put("user", user);
            }
            if (password != null) {
                info.put("password", password);
            }
            driver.connect(url, info).close();
        }
    }

    /**
     * Gets the qualified name of the driver class.
     *
     * @return the driver class.
     */
    protected abstract String getDriverClass();

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
