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
package org.n52.sos.ds.hibernate;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.ds.Datasource;
import org.n52.sos.ds.DatasourceCallback;
import org.n52.sos.ds.HibernateDatasourceConstants;

public class MockDatasource implements Datasource {

    @Override
    public String getConnectionProviderIdentifier() {
        return HibernateDatasourceConstants.ORM_CONNECTION_PROVIDER_IDENTIFIER;
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    @Override
    public String getDialectName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<SettingDefinition<?, ?>> getChangableSettingDefinitions(Properties current) {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean checkIfSchemaExists(Properties current, Map<String, Object> newSettings) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean checkSchemaCreation(Map<String, Object> settings) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String[] createSchema(Map<String, Object> settings) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] dropSchema(Map<String, Object> settings) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] updateSchema(Map<String, Object> settings) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
        // TODO Auto-generated method stub
        return null;
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
