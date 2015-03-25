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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.text.IsEmptyString;
import org.junit.Test;
import org.n52.sos.config.SettingDefinition;


/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class AbstractSqlServerDatasourceTest {
	
	@Test
	public void databaseDescriptionShouldNotBeHostDescription() {
		String databaseDescription = "";
		String hostDescription = "";
		Set<SettingDefinition<?,?>> definitions = new AbstractSqlServerDatasourceSeam().getSettingDefinitions();
		for (Iterator<SettingDefinition<?, ?>> iterator = definitions.iterator(); iterator.hasNext();) {
			SettingDefinition<?, ?> settingDefinition = (SettingDefinition<?, ?>) iterator.next();
			if (settingDefinition.getKey().equalsIgnoreCase(AbstractSqlServerDatasource.DATABASE_KEY)) {
				databaseDescription = settingDefinition.getDescription();
			} else if (settingDefinition.getKey().equalsIgnoreCase(AbstractSqlServerDatasource.HOST_KEY)) {
				hostDescription = settingDefinition.getDescription();
			}
		}
		assertThat(hostDescription, not(isEmptyString()));
		assertThat(databaseDescription, not(isEmptyString()));
		assertThat(hostDescription, is(not(databaseDescription)));
	}
	
	@Test
	public void instanceSettingShouldBeOptional() {
		Set<SettingDefinition<?,?>> definitions = new AbstractSqlServerDatasourceSeam().getSettingDefinitions();
		boolean found = false;
		for (Iterator<SettingDefinition<?, ?>> iterator = definitions.iterator(); iterator.hasNext();) {
			SettingDefinition<?, ?> settingDefinition = (SettingDefinition<?, ?>) iterator.next();
			if (settingDefinition.getKey().equalsIgnoreCase(AbstractSqlServerDatasource.INSTANCE_KEY)) {
				found = true;
				if (!settingDefinition.isOptional()) {
					fail("Instance setting is not optional");
				}
			}
		}
		if (!found) {
			fail("Instance setting not found");
		}
	}
	
	@Test
	public void toUrlShouldEncodeWithoutOptionalInstance() {
		String port = "port";
		String server = "server";
		String database = "database";
		final String expected = "jdbc:sqlserver://" + 
				server + ":" + 
				port + ";databaseName=" + 
				database;
		final Map<String, Object> settings = new LinkedHashMap<>();
		settings.put(AbstractHibernateCoreDatasource.HOST_KEY, server);
		settings.put(AbstractHibernateCoreDatasource.PORT_KEY, port);
		settings.put(AbstractHibernateCoreDatasource.DATABASE_KEY, database);
		final String created = new AbstractSqlServerDatasourceSeam().toURL(settings);
		assertThat(created, is(expected));
	}
	
	@Test
	public void toUrlShouldEncodeServerPortInstanceAndDatabaseName() {
		String instance = "instance";
		String port = "port";
		String server = "server";
		String database = "database";
		final String expected = "jdbc:sqlserver://" + 
				server + ":" + 
				port + ";instance=" + 
				instance + ";databaseName=" + 
				database;
		final Map<String, Object> settings = new LinkedHashMap<>();
		settings.put(AbstractHibernateCoreDatasource.HOST_KEY, server);
		settings.put(AbstractHibernateCoreDatasource.PORT_KEY, port);
		settings.put(AbstractHibernateCoreDatasource.DATABASE_KEY, database);
		settings.put(AbstractSqlServerDatasource.INSTANCE_KEY, instance);
		final String created = new AbstractSqlServerDatasourceSeam().toURL(settings);
		assertThat(created, is(expected));
	}
	
	@Test
	public void parseUrlShouldExtractInstancePortDatabaseNameAndServer(){
		String expectedServer = "server";
		String expectedPort = "1433";
		String expectedInstance = "instance";
		String expectedDatabaseName = "database";
		String[] parsedValues = new AbstractSqlServerDatasourceSeam().parseURL("jdbc:sqlserver://" +
				expectedServer + ":" + 
				expectedPort + ";instance=" + 
				expectedInstance + ";databaseName=" + 
				expectedDatabaseName);
		assertThat(parsedValues[0], is(expectedServer));
		assertThat(parsedValues[1], is(expectedPort));
		assertThat(parsedValues[2], is(expectedDatabaseName));
		assertThat(parsedValues[3], is(expectedInstance));
	}
	
	@Test
	public void parseUrlShouldWorkWithoutOptionalInstance(){
		String expectedServer = "server";
		String expectedPort = "1433";
		String expectedDatabaseName = "database";
		String[] parsedValues = new AbstractSqlServerDatasourceSeam().parseURL("jdbc:sqlserver://" +
				expectedServer + ":" + 
				expectedPort + ";databaseName=" + 
				expectedDatabaseName);
		assertThat(parsedValues[0], is(expectedServer));
		assertThat(parsedValues[1], is(expectedPort));
		assertThat(parsedValues[2], is(expectedDatabaseName));
	}
	
	private class AbstractSqlServerDatasourceSeam extends AbstractSqlServerDatasource {

		@Override
		public String getDialectName() {
			return null;
		}
		
	}

}
