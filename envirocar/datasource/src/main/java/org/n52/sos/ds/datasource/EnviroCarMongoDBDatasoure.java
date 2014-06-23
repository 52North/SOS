/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.envirocar.server.mongo.MongoDB;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.ds.Datasource;
import org.n52.sos.ds.DatasourceCallback;
import org.n52.sos.ds.EnviroCarConstants;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Sets;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class EnviroCarMongoDBDatasoure implements Datasource {
    
    private String DIALECT_NAME = "MongoDB4Envirocar";
    
    private String usernameDefault, usernameDescription;

    private String passwordDefault, passwordDescription;

    private String databaseDefault, databaseDescription;

    private String hostDefault, hostDescription;

    private int portDefault;

    private String portDescription;
    
    protected static final String USERNAME_TITLE = "User Name";
    
    protected static final String USERNAME_KEY = "envirocar.username";

    protected static final String PASSWORD_TITLE = "Password";
    
    protected static final String PASSWORD_KEY = "envirocar.password";

    protected static final String DATABASE_KEY = "envirocar.database";

    protected static final String DATABASE_TITLE = "Database";

    protected static final String DATABASE_DESCRIPTION =
            "Set this to the name of the database you want to use for SOS. The default name vor EnviroCar is 'envirocar'";

    protected static final String DATABASE_DEFAULT_VALUE = "envirocar";

    protected static final String HOST_KEY = "envirocar.host";

    protected static final String HOST_TITLE = "Host";

    protected static final String HOST_DESCRIPTION =
            "Set this to the IP/net location of the database server. The default value for is \"localhost\".";

    protected static final String HOST_DEFAULT_VALUE = "localhost";

    protected static final String PORT_KEY = "envirocar.port";

    protected static final String PORT_TITLE = "Database Port";
    
    protected static final String PORT_DESCRIPTION =
            "Set this to the port number of your MongoDB server. The default value for MongoDB is 27017.";

    protected static final int PORT_DEFAULT_VALUE = 27017;
    
    public EnviroCarMongoDBDatasoure() {
        setDatabaseDefault(DATABASE_DEFAULT_VALUE);
        setDatabaseDescription(DATABASE_DESCRIPTION);
        setHostDefault(HOST_DEFAULT_VALUE);
        setHostDescription(HOST_DESCRIPTION);
        setPortDefault(PORT_DEFAULT_VALUE);
        setPortDescription(PORT_DESCRIPTION);
    }
    
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
     * Create settings definition for password
     *
     * @return Password settings definition
     */
    protected StringSettingDefinition createPasswordDefinition() {
        return new StringSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_2)
                .setKey(PASSWORD_KEY).setTitle(PASSWORD_TITLE);
    }

    /**
     * Create settings definition for database name
     *
     * @return database name settings definition
     */
    protected StringSettingDefinition createDatabaseDefinition() {
        return new StringSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_3)
                .setKey(DATABASE_KEY).setTitle(DATABASE_TITLE).setDescription(DATABASE_DESCRIPTION)
                .setDefaultValue(DATABASE_DEFAULT_VALUE);
    }

    /**
     * Create settings definition for host
     *
     * @return Host settings definition
     */
    protected StringSettingDefinition createHostDefinition() {
        return new StringSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_4)
                .setKey(HOST_KEY).setTitle(HOST_TITLE).setDescription(HOST_DESCRIPTION)
                .setDefaultValue(HOST_DEFAULT_VALUE);
    }

    /**
     * Create settings definition for port
     *
     * @return Port settings definition
     */
    protected IntegerSettingDefinition createPortDefinition() {
        return new IntegerSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_5)
                .setKey(PORT_KEY).setTitle(PORT_TITLE);
    }
    
    
    protected StringSettingDefinition createUsernameDefinition(final String defaultValue) {
        return createUsernameDefinition().setDescription(usernameDescription).setDefaultValue(defaultValue);
    }

    protected StringSettingDefinition createPasswordDefinition(final String defaultValue) {
        return createPasswordDefinition().setDescription(passwordDescription).setDefaultValue(defaultValue);
    }

    protected StringSettingDefinition createDatabaseDefinition(final String defaultValue) {
        return createDatabaseDefinition().setDescription(databaseDescription).setDefaultValue(defaultValue);
    }

    protected StringSettingDefinition createHostDefinition(final String defaultValue) {
        return createHostDefinition().setDescription(hostDescription).setDefaultValue(defaultValue);
    }

    protected IntegerSettingDefinition createPortDefinition(final int defaultValue) {
        return createPortDefinition().setDescription(portDescription).setDefaultValue(defaultValue);
    }

    /**
     * @param usernameDefault
     *            the usernameDefault to set
     */
    public void setUsernameDefault(final String usernameDefault) {
        this.usernameDefault = usernameDefault;
    }

    /**
     * @param usernameDescription
     *            the usernameDescription to set
     */
    public void setUsernameDescription(final String usernameDescription) {
        this.usernameDescription = usernameDescription;
    }

    /**
     * @param passwordDefault
     *            the passwordDefault to set
     */
    public void setPasswordDefault(final String passwordDefault) {
        this.passwordDefault = passwordDefault;
    }

    /**
     * @param passwordDescription
     *            the passwordDescription to set
     */
    public void setPasswordDescription(final String passwordDescription) {
        this.passwordDescription = passwordDescription;
    }

    /**
     * @param databaseDefault
     *            the databaseDefault to set
     */
    public void setDatabaseDefault(final String databaseDefault) {
        this.databaseDefault = databaseDefault;
    }

    /**
     * @param databaseDescription
     *            the databaseDescription to set
     */
    public void setDatabaseDescription(final String databaseDescription) {
        this.databaseDescription = databaseDescription;
    }

    /**
     * @param hostDefault
     *            the hostDefault to set
     */
    public void setHostDefault(final String hostDefault) {
        this.hostDefault = hostDefault;
    }

    /**
     * @param hostDescription
     *            the hostDescription to set
     */
    public void setHostDescription(final String hostDescription) {
        this.hostDescription = hostDescription;
    }

    /**
     * @param portDefault
     *            the portDefault to set
     */
    public void setPortDefault(final int portDefault) {
        this.portDefault = portDefault;
    }

    /**
     * @param portDescription
     *            the portDescription to set
     */
    public void setPortDescription(final String portDescription) {
        this.portDescription = portDescription;
    }
    
    
    @Override
    public Properties getDatasourceProperties(Properties current, Map<String, Object> changed) {
        return getDatasourceProperties(mergeProperties(current, changed));
    }
    
    /**
     * Merge current properties with changed settings
     *
     * @param current
     *            Current properties
     * @param changed
     *            Changed settings
     * @return Updated settings
     */
    protected Map<String, Object> mergeProperties(Properties current, Map<String, Object> changed) {
        Map<String, Object> settings = parseDatasourceProperties(current);
        settings.putAll(changed);
        return settings;
    }
    
    
    @Override
    public String getDialectName() {
        return DIALECT_NAME;
    }


    @Override
    public String getConnectionProviderIdentifier() {
        return EnviroCarConstants.ENVIROCAR_CONNECTION_PROVIDER_IDENTIFIER;
    }


    @Override
    public String getDatasourceDaoIdentifier() {
        return EnviroCarConstants.ENVIROCAR_DATASOURCE_DAO_IDENTIFIER;
    }
    


    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        Set<SettingDefinition<?, ?>> set =
                Sets.<SettingDefinition<?, ?>> newHashSet(createUsernameDefinition(usernameDefault),
                        createPasswordDefinition(passwordDefault), createDatabaseDefinition(databaseDefault),
                        createHostDefinition(hostDefault), createPortDefinition(portDefault));
        return set;
    }
    
    @Override
    public Set<SettingDefinition<?, ?>> getChangableSettingDefinitions(final Properties current) {
        final Map<String, Object> settings = parseDatasourceProperties(current);
        return Sets.<SettingDefinition<?, ?>> newHashSet(
                createUsernameDefinition((String) settings.get(USERNAME_KEY)),
                createPasswordDefinition((String) settings.get(PASSWORD_KEY)),
                createDatabaseDefinition((String) settings.get(DATABASE_KEY)),
                createHostDefinition((String) settings.get(HOST_KEY)),
                createPortDefinition(JavaHelper.asInteger(settings.get(PORT_KEY))));
    }


    private Map<String, Object> parseDatasourceProperties(Properties current) {
        final Map<String, Object> settings = new HashMap<String, Object>(current.size());
        settings.put(USERNAME_KEY, current.getProperty(MongoDB.USER_PROPERTY));
        settings.put(PASSWORD_KEY, current.getProperty(MongoDB.PASS_PROPERTY));
        settings.put(HOST_KEY, current.getProperty(MongoDB.HOST_PROPERTY));
        settings.put(PORT_KEY, current.getProperty(MongoDB.PORT_PROPERTY));
        settings.put(DATABASE_KEY, current.getProperty(MongoDB.DATABASE_PROPERTY));
        return settings;
    }

    @Override
    public void validateConnection(Map<String, Object> settings) {
        MongoClient client = null;
        try {
            client = createClient(settings);
            // check if connected
            client.getDatabaseNames();
            String database = (String) settings.get(DATABASE_KEY);
            if (StringHelper.isNotEmpty(database)) {
                DB db = client.getDB(database);
                // check if connected
                db.getCollectionNames();
                String pass = (String) settings.get(PASSWORD_KEY);
                String user = (String) settings.get(USERNAME_KEY);
                if (StringHelper.isNotEmpty(user) && StringHelper.isNotEmpty(pass)) {
                    if (!db.authenticate(user, pass.toCharArray())) {
                        throw new ConfigurationException("Invalid username and/or password!");
                    }
                }
            }
        } catch (UnknownHostException uhe) {
            throw new ConfigurationException(uhe);
        } catch (MongoException me) {
            throw new ConfigurationException(me);
        } finally {
            client.close();
        }
    }

    private MongoClient createClient(Map<String, Object> settings) throws UnknownHostException {
        String host = (String) settings.get(HOST_KEY);
        int port = (Integer) settings.get(PORT_KEY);
        return new MongoClient(host, port);
    }


    @Override
    public void validateConnection(Properties current, Map<String, Object> newSettings) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void validatePrerequisites(Map<String, Object> settings) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void validatePrerequisites(Properties current, Map<String, Object> newSettings) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public boolean needsSchema() {
        return false;
    }


    @Override
    public void validateSchema(Map<String, Object> settings) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void validateSchema(Properties current, Map<String, Object> newSettings) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public boolean checkIfSchemaExists(Map<String, Object> settings) {
        return false;
    }


    @Override
    public boolean checkIfSchemaExists(Properties current, Map<String, Object> newSettings) {
        return false;
    }


    @Override
    public boolean checkSchemaCreation(Map<String, Object> settings) {
        return false;
    }


    @Override
    public String[] createSchema(Map<String, Object> settings) {
        return null;
    }


    @Override
    public String[] dropSchema(Map<String, Object> settings) {
        return null;
    }


    @Override
    public String[] updateSchema(Map<String, Object> settings) {
        return null;
    }


    @Override
    public void execute(String[] sql, Map<String, Object> settings) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void clear(Properties settings) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public boolean supportsClear() {
        return false;
    }


    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
        final Properties p = new Properties();
        if (isSet(settings, USERNAME_KEY)) {
            p.put(MongoDB.USER_PROPERTY, settings.get(USERNAME_KEY));
        }
        if (isSet(settings, PASSWORD_KEY)) {
            p.put(MongoDB.PASS_PROPERTY, settings.get(PASSWORD_KEY));
        }
        p.put(MongoDB.HOST_PROPERTY, settings.get(HOST_KEY));
        p.put(MongoDB.PORT_PROPERTY, settings.get(PORT_KEY));
        p.put(MongoDB.DATABASE_PROPERTY, settings.get(DATABASE_KEY));
        return p;
    }
    
    private boolean isSet(Map<String, Object> settings, String usernameKey) {
        Object object = settings.get(usernameKey);
        if (object != null) {
            if (object instanceof String) {
                return StringHelper.isNotEmpty((String)object);
            }
            return true;
        }
        return false;
    }


    @Override
    public DatasourceCallback getCallback() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void prepare(Map<String, Object> settings) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public boolean isPostCreateSchema() {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void executePostCreateSchema(Map<String, Object> databaseSettings) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void checkPostCreation(Properties properties) {
        // TODO Auto-generated method stub
        
    }
    
}
