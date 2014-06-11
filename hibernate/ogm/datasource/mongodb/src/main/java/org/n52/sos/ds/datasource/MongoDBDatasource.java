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

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.ogm.datastore.mongodb.MongoDBDialect;
import org.n52.sos.config.SettingDefinition;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.ds.DatasourceCallback;

public class MongoDBDatasource extends AbstractHibernateOgmDatasource {
    
    private static final String DIALECT_NAME = "MongoDB";

    protected static final String TIMEOUT_KEY = "ogm.timeout";

    protected static final String TIMEOUT_TITLE = "Connection timeout in ms";

    protected static final String TIMEOUT_DESCRIPTION =
            "The timeout used by the driver when the connection is initiated in ms!";

    protected static final Integer TIMEOUT_DEFAULT_VALUE = 5000;

    protected static final String PORT_DESCRIPTION =
            "Set this to the port number of your MongoDB server. The default value for MongoDB is 27017.";

    protected static final int PORT_DEFAULT_VALUE = 27017;

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        Set<SettingDefinition<?, ?>> set = super.getSettingDefinitions();
        set.add(createTimeoutDefinition());
        return set;
    }

    protected IntegerSettingDefinition createTimeoutDefinition() {
        return new IntegerSettingDefinition().setGroup(BASE_GROUP).setOrder(SettingDefinitionProvider.ORDER_10)
                .setKey(TIMEOUT_KEY).setTitle(TIMEOUT_TITLE).setDescription(TIMEOUT_DESCRIPTION)
                .setDefaultValue(TIMEOUT_DEFAULT_VALUE);
    }

    @Override
    public String getDialectName() {
        return DIALECT_NAME;
    }

    @Override
    public void validateConnection(Map<String, Object> settings) {
        // TODO Auto-generated method stub

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
        return new String[0];
    }

    @Override
    public String[] dropSchema(Map<String, Object> settings) {
        return new String[0];
    }

    @Override
    public String[] updateSchema(Map<String, Object> settings) {
        return new String[0];
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Properties getDatasourceProperties(Properties current, Map<String, Object> newSettings) {
        // TODO Auto-generated method stub
        return null;
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

    @Override
    protected String getProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String[] parseURL(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Map<String, Object> parseDatasourceProperties(Properties current) {
        // TODO Auto-generated method stub
        return null;
    }

}
