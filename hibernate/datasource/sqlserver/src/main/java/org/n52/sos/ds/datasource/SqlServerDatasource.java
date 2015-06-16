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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.util.StringHelper;

/**
 * MS SQL Server datasource
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
public class SqlServerDatasource extends AbstractSqlServerDatasource {

    private static final String TN_FEATURE_OF_INTEREST = "featureOfInterest";

    private static final String TN_OBSERVATION = "observation";

    private static final String CN_IDENTIFIER = "identifier";

    private static final String CN_URL = "url";

    private static final String DECLARE_VARIABLE = "DECLARE @ObjectName NVARCHAR(100);";

    private static final String DIALECT_NAME = "SQL Server";

    public SqlServerDatasource() {
        super();
    }

    @Override
    public String getDialectName() {
        return DIALECT_NAME;
    }

    @Override
    public boolean isPostCreateSchema() {
        return true;
    }

    @Override
    public void executePostCreateSchema(Map<String, Object> databaseSettings) {
        List<String> statements = new ArrayList<String>();
        for (TableColumn tableColumn : getTableColumns()) {
            statements.add(getGetAndDropConstraint(tableColumn.getTable(), tableColumn.getColumn(), databaseSettings));
            statements
                    .add(getCreateUniqueConstraint(databaseSettings, tableColumn.getTable(), tableColumn.getColumn()));
            execute(statements.toArray(new String[statements.size()]), databaseSettings);
            statements.clear();
        }
    }

    @Override
    public boolean supportsClear() {
        return false;
    }

    private String getGetAndDropConstraint(String table, String column, Map<String, Object> databaseSettings) {
        StringBuilder builder = new StringBuilder();
        builder.append(DECLARE_VARIABLE);
        builder.append(getSelectConstraintNameToVariable(table, column));
        builder.append(getExecuteDropConstraint(table, databaseSettings));
        return builder.toString();
    }

    private Set<TableColumn> getTableColumns() {
        Set<TableColumn> tableColumns = new HashSet<SqlServerDatasource.TableColumn>();
        tableColumns.add(new TableColumn(TN_FEATURE_OF_INTEREST, CN_IDENTIFIER));
        tableColumns.add(new TableColumn(TN_FEATURE_OF_INTEREST, CN_URL));
        tableColumns.add(new TableColumn(TN_OBSERVATION, CN_IDENTIFIER));
        return tableColumns;
    }

    private String getSelectConstraintNameToVariable(String table, String colum) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT @ObjectName = ccu.CONSTRAINT_NAME ");
        builder.append("FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu, INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc ");
        builder.append("WHERE ccu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME ");
        builder.append("AND ccu.TABLE_NAME=").append("'").append(table).append("'");
        builder.append(" AND ccu.COLUMN_NAME=").append("'").append(colum).append("';");
        return builder.toString();
    }

    private String getExecuteDropConstraint(String table, Map<String, Object> databaseSettings) {
        StringBuilder builder = new StringBuilder();
        builder.append("IF (OBJECT_ID(@ObjectName, 'UQ') IS NOT NULL) ");
        builder.append("BEGIN ");
        builder.append("EXEC('ALTER TABLE ");
        builder.append(getQualifiedTable(getDatabase(databaseSettings), getSchema(databaseSettings), table));
        builder.append(" DROP CONSTRAINT ' + @ObjectName); ");
        builder.append("END ");
        return builder.toString();
    }

    private String getCreateUniqueConstraint(Map<String, Object> databaseSettings, String table, String column) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE UNIQUE NONCLUSTERED INDEX ").append(table).append("_").append(column);
        builder.append(" ON ")
                .append(getQualifiedTable(getDatabase(databaseSettings), getSchema(databaseSettings), table))
                .append("(").append(column).append(")");
        builder.append("WHERE ").append(column).append(" IS NOT NULL");
        return builder.toString();
    }

    private String getQualifiedTable(String database, String schema, String table) {
        StringBuilder builder = new StringBuilder();
        if (StringHelper.isNotEmpty(database)) {
            builder.append(database).append(".");
        }
        if (StringHelper.isNotEmpty(schema)) {
            builder.append(schema);
        }
        builder.append(table);
        return builder.toString();
    }

    protected String getDatabase(Map<String, Object> settings) {
        if (isSetSchema(settings)) {
            return (String) settings.get(DATABASE_KEY);
        }
        return "";
    }

    private class TableColumn {

        private String table;

        private String column;

        public TableColumn(String table, String column) {
            setTable(table);
            setColumn(column);
        }

        /**
         * @return the table
         */
        public String getTable() {
            return table;
        }

        /**
         * @param table
         *            the table to set
         */
        private void setTable(String table) {
            this.table = table;
        }

        /**
         * @return the column
         */
        public String getColumn() {
            return column;
        }

        /**
         * @param column
         *            the column to set
         */
        public void setColumn(String column) {
            this.column = column;
        }
    }
}
