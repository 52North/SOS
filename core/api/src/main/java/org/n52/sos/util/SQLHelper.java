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
package org.n52.sos.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQL helper class with methods to close statements, connections, ... and to
 * load and execute a SQL script file.
 * 
 * @since 4.0.0
 * 
 */
public final class SQLHelper implements Constants {

    private static final Logger LOG = LoggerFactory.getLogger(SQLHelper.class);

    /*
     * TODO find a working library function that can parse and execute a SQL
     * file...
     */
    public static void executeSQLFile(Connection conn, File path) throws SQLException, IOException {
        FileInputStream in = null;
        Statement st = null;
        BufferedReader br = null;
        try {
            /* FIXME DataInputStream!? */
            in = new FileInputStream(path);
            br = new BufferedReader(new InputStreamReader(new DataInputStream(in)));
            st = conn.createStatement();
            boolean stringLiteral = false;
            String strLine;
            StringBuilder sql = new StringBuilder();
            LOG.debug("Executing SQL file {}", path);
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                if ((strLine.length() > 0) && (!strLine.contains("--"))) {
                    if (strLine.equals("$$")) {
                        stringLiteral = !stringLiteral;
                    }
                    sql.append(BLANK_CHAR).append(strLine).append(BLANK_CHAR);
                    if (!stringLiteral && strLine.substring(strLine.length() - 1).equals(SEMICOLON_CHAR)) {
                        st.execute(sql.substring(0, sql.length() - 1));
                        sql = new StringBuilder();
                    }
                }
            }
        } finally {
            close(st);
            close(in);
            close(br);
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException t) {
                LOG.error(String.format("Error closing %s!", closeable.getClass()), t);
            }
        }
    }

    public static void close(ResultSet closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (SQLException ex) {
                LOG.error("Error closing ResultSet!", ex);
            }
        }
    }

    public static void close(Statement closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (SQLException ex) {
                LOG.error("Error closing Statement!", ex);
            }
        }
    }

    public static void close(Connection closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (SQLException ex) {
                LOG.error("Error closing Connection!", ex);
            }
        }
    }

    private SQLHelper() {
        // private constructor to enforce static access
    }
}
