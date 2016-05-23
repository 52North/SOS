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

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.hibernate.spatial.dialect.h2geodb.GeoDBDialect;

import org.n52.sos.config.SettingDefinition;
import org.n52.sos.ds.DatasourceCallback;
import org.n52.sos.exception.ConfigurationException;

import geodb.GeoDB;



/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public abstract class AbstractH2Datasource extends AbstractHibernateDatasource {
    protected static final String H2_DRIVER_CLASS = "org.h2.Driver";

    protected static final String H2_DIALECT_CLASS = GeoDBDialect.class.getName();

    protected static final String DEFAULT_USERNAME = "sa";

    protected static final String DEFAULT_PASSWORD = "";

    protected static final Pattern CREATE_INDEX_PATTERN
                    = Pattern.compile("^create index [a-z]* on observation \\(samplingGeometry\\)$", Pattern.CASE_INSENSITIVE);

    @Override
    protected Dialect createDialect() {
        return new GeoDBDialect();
    }

    @Override
    public boolean supportsClear() {
        return true;
    }

    @Override
    public String[] createSchema(Map<String, Object> settings) {
        String[] createSchema = super.createSchema(settings);
        int index = find(createSchema, CREATE_INDEX_PATTERN);
        return stripIndex(createSchema, index);
    }

    @Override
    public  Set<SettingDefinition<?, ?>> getChangableSettingDefinitions(Properties p) {
        return Collections.emptySet();
    }

    @Override
    public void clear(Properties properties) {
        Map<String, Object> settings = parseDatasourceProperties(properties);
        CustomConfiguration config = getConfig(settings);
        Iterator<Table> tables = config.getTableMappings();

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = openConnection(settings);
            stmt = conn.createStatement();
            stmt.execute("set referential_integrity false");
            while (tables.hasNext()) {
                Table table = tables.next();
                if (table.isPhysicalTable()) {
                    stmt.execute("truncate table " + table.getName());
                }
            }
            stmt.execute("set referential_integrity true");
            GeoDB.InitGeoDB(conn);
        } catch (SQLException ex) {
            throw new ConfigurationException(ex);
        } finally {
            close(stmt);
            close(conn);
        }
    }

    @Override
    protected String getDriverClass() {
        return H2_DRIVER_CLASS;
    }

    @Override
    public DatasourceCallback getCallback() {
        return DatasourceCallback.chain(super.getCallback(),
                                        new DatasourceCallback() {
            @Override
            public Properties onInit(Properties props) {
                initGeoDB(parseDatasourceProperties(props));
                return props;
            }
        });
    }

    protected void initGeoDB(Map<String, Object> settings) throws ConfigurationException {
        try (Connection cx = openConnection(settings)) {
            GeoDB.InitGeoDB(cx);
        } catch (SQLException ex) {
            throw new ConfigurationException("Could not init GeoDB", ex);
        }
    }

    public static int find(String[] array, Pattern pattern) {
        for (int index = 0; index < array.length; ++index) {
            if (pattern.matcher(array[index]).matches()) {
                return index;
            }
        }
        return -1;
    }

    public static <T> T[] stripIndex(T[] array, int idx) {
        int len = array.length;
        if (idx < 0 || len == 0) {
            return array;
        }
        T[] copy = createArray(array.getClass(), len - 1);
        if (idx != 0) {
            System.arraycopy(array, 0, copy, 0, idx);
        }
        if (idx != len - 1) {
            System.arraycopy(array, idx + 1, copy, idx, len - idx - 1);
        }
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] createArray(Class<?> type, int length) {
        return ((Object)type == (Object)Object[].class)
               ? (T[]) new Object[length]
               : (T[]) Array.newInstance(type.getComponentType(), length);
    }
}
