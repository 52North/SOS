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
package org.n52.sos.ds.datasource;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.text.IsEmptyString;
import org.junit.Assert;
import org.junit.Test;
import org.n52.faroe.SettingDefinition;


/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class AbstractSqlServerDatasourceTest {

    private static final String PORT = "port";
    private static final String SERVER = "server";
    private static final String INSTANCE = "instance";
    private static final String DATABASE = "database";
    private static final String DATABASE_NAME = ";databaseName=";
    private static final String INSTANCE_NAME = ";instance=";
    private static final String JDBC = "jdbc:sqlserver://";
    private static final String PORT_VALUE = "1433";

    @Test
    public void databaseDescriptionShouldNotBeHostDescription() {
        String databaseDescription = "";
        String hostDescription = "";
        Set<SettingDefinition<?>> definitions = new AbstractSqlServerDatasourceSeam().getSettingDefinitions();
        for (Iterator<SettingDefinition<?>> iterator = definitions.iterator(); iterator.hasNext();) {
            SettingDefinition<?> settingDefinition = (SettingDefinition<?>) iterator.next();
            if (settingDefinition.getKey().equalsIgnoreCase(AbstractSqlServerDatasource.DATABASE_KEY)) {
                databaseDescription = settingDefinition.getDescription();
            } else if (settingDefinition.getKey().equalsIgnoreCase(AbstractSqlServerDatasource.HOST_KEY)) {
                hostDescription = settingDefinition.getDescription();
            }
        }
       MatcherAssert.assertThat(hostDescription, CoreMatchers.not(IsEmptyString.isEmptyString()));
       MatcherAssert.assertThat(databaseDescription, CoreMatchers.not(IsEmptyString.isEmptyString()));
       MatcherAssert.assertThat(hostDescription, CoreMatchers.is(CoreMatchers.not(databaseDescription)));
    }

    @Test
    public void instanceSettingShouldBeOptional() {
        Set<SettingDefinition<?>> definitions = new AbstractSqlServerDatasourceSeam().getSettingDefinitions();
        boolean found = false;
        for (Iterator<SettingDefinition<?>> iterator = definitions.iterator(); iterator.hasNext();) {
            SettingDefinition<?> settingDefinition = (SettingDefinition<?>) iterator.next();
            if (settingDefinition.getKey().equalsIgnoreCase(AbstractSqlServerDatasource.INSTANCE_KEY)) {
                found = true;
                if (!settingDefinition.isOptional()) {
                    Assert.fail("Instance setting is not optional");
                }
            }
        }
        if (!found) {
            Assert.fail("Instance setting not found");
        }
    }

    @Test
    public void toUrlShouldEncodeWithoutOptionalInstance() {
        String port = PORT;
        String server = SERVER;
        String database = DATABASE;
        final String expected = JDBC +
                server + ":" +
                port + DATABASE_NAME +
                database;
        final Map<String, Object> settings = new LinkedHashMap<>();
        settings.put(AbstractHibernateCoreDatasource.HOST_KEY, server);
        settings.put(AbstractHibernateCoreDatasource.PORT_KEY, port);
        settings.put(AbstractHibernateCoreDatasource.DATABASE_KEY, database);
        final String created = new AbstractSqlServerDatasourceSeam().toURL(settings);
       MatcherAssert.assertThat(created, CoreMatchers.is(expected));
    }

    @Test
    public void toUrlShouldEncodeServerPortInstanceAndDatabaseName() {
        String instance = INSTANCE;
        String port = PORT;
        String server = SERVER;
        String database = DATABASE;
        final String expected = JDBC +
                server + ":" +
                port + INSTANCE_NAME +
                instance + DATABASE_NAME +
                database;
        final Map<String, Object> settings = new LinkedHashMap<>();
        settings.put(AbstractHibernateCoreDatasource.HOST_KEY, server);
        settings.put(AbstractHibernateCoreDatasource.PORT_KEY, port);
        settings.put(AbstractHibernateCoreDatasource.DATABASE_KEY, database);
        settings.put(AbstractSqlServerDatasource.INSTANCE_KEY, instance);
        final String created = new AbstractSqlServerDatasourceSeam().toURL(settings);
       MatcherAssert.assertThat(created, CoreMatchers.is(expected));
    }

    @Test
    public void parseUrlShouldExtractInstancePortDatabaseNameAndServer() {
        String expectedServer = SERVER;
        String expectedPort = PORT_VALUE;
        String expectedInstance = INSTANCE;
        String expectedDatabaseName = DATABASE;
        String[] parsedValues = new AbstractSqlServerDatasourceSeam().parseURL(JDBC +
                expectedServer + ":" +
                expectedPort + INSTANCE_NAME +
                expectedInstance + DATABASE_NAME +
                expectedDatabaseName);
       MatcherAssert.assertThat(parsedValues[0], CoreMatchers.is(expectedServer));
       MatcherAssert.assertThat(parsedValues[1], CoreMatchers.is(expectedPort));
       MatcherAssert.assertThat(parsedValues[2], CoreMatchers.is(expectedDatabaseName));
       MatcherAssert.assertThat(parsedValues[3], CoreMatchers.is(expectedInstance));
    }

    @Test
    public void parseUrlShouldWorkWithoutOptionalInstance() {
        String expectedServer = SERVER;
        String expectedPort = PORT_VALUE;
        String expectedDatabaseName = DATABASE;
        String[] parsedValues = new AbstractSqlServerDatasourceSeam().parseURL(JDBC +
                expectedServer + ":" +
                expectedPort + DATABASE_NAME +
                expectedDatabaseName);
       MatcherAssert.assertThat(parsedValues[0], CoreMatchers.is(expectedServer));
       MatcherAssert.assertThat(parsedValues[1], CoreMatchers.is(expectedPort));
       MatcherAssert.assertThat(parsedValues[2], CoreMatchers.is(expectedDatabaseName));
    }

    private class AbstractSqlServerDatasourceSeam extends AbstractSqlServerDatasource {

        @Override
        public String getDialectName() {
            return null;
        }

    }

}
