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
package org.n52.sos.ds;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 4.0.0
 * 
 */
public interface GeneralQueryDAO {
    class QueryResult {
        private boolean error;

        private String message;

        private List<String> columnNames = new ArrayList<String>(0);

        private List<Row> columns = new LinkedList<Row>();

        public QueryResult() {
        }

        public QueryResult(String message) {
            this(message, false);
        }

        public QueryResult(String message, boolean isError) {
            this.message = message;
            this.error = isError;
        }

        public List<String> getColumnNames() {
            return Collections.unmodifiableList(this.columnNames);
        }

        public QueryResult setColumnNames(List<String> columnNames) {
            this.columnNames = new ArrayList<String>(columnNames);
            return this;
        }

        public List<Row> getRows() {
            return Collections.unmodifiableList(this.columns);
        }

        public QueryResult addRow(Row column) {
            this.columns.add(column);
            return this;
        }

        public boolean isError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }

    class Row {
        private List<String> values = new LinkedList<String>();

        public List<String> getValues() {
            return Collections.unmodifiableList(this.values);
        }

        public Row addValue(String value) {
            this.values.add(value);
            return this;
        }

        public Row setValues(List<String> values) {
            this.values = values == null ? new LinkedList<String>() : values;
            return this;
        }
    }

    /**
     * Method which query the SOS DB
     * 
     * @param query
     *            normal sql query concerning any table
     * 
     * @return query result
     * 
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    QueryResult query(String query) throws SQLException, FileNotFoundException, IOException;
}
