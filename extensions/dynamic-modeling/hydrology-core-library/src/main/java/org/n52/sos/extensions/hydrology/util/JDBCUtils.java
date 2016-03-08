/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.extensions.hydrology.util;

import java.sql.Connection;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;

/**
 * Utility class to safely open and clean up after connections.
 */
public class JDBCUtils extends org.n52.sos.extensions.util.JDBCUtils
{
    /** No constructor available. */
    private JDBCUtils()
    {
        super();
    }
    
    /**
     * Returns a Connection to the specified Spatialite database.
     */
    public static Connection openSpatialiteConnection(String spatialiteFileName) throws RuntimeException
    {
        Connection connection = null;
        Statement stmt = null;
        
        // Load the sqlite-JDBC driver using the current class loader.
        try
        {
            Class.forName("org.sqlite.JDBC");
            
            // Enabling dynamic extension loading absolutely required by SpatiaLite
            SQLiteConfig config = new SQLiteConfig();
            config.enableLoadExtension(true);
            
            // Creates a database connection
            connection = java.sql.DriverManager.getConnection("jdbc:sqlite:"+spatialiteFileName, config.toProperties());
            
            // Loading SpatiaLite extension when available
            stmt = connection.createStatement();
            stmt.setQueryTimeout(10); // set timeout to 10 seconds.
            stmt.execute("SELECT load_extension('mod_spatialite')");
                
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(stmt);
            return connection;
        }
        catch (Exception e)
        {
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(stmt);
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(connection);
            
            throw new RuntimeException(e.getMessage());
        }
    }
    /**
     * Returns a Connection to the specified Sqlite database.
     */
    public static Connection openSqliteConnection(String spatialiteFileName) throws RuntimeException
    {
        Connection connection = null;
        
        // Load the sqlite-JDBC driver using the current class loader.
        try
        {
            Class.forName("org.sqlite.JDBC");
            
            // Enabling dynamic extension loading absolutely required by SpatiaLite
            SQLiteConfig config = new SQLiteConfig();
            config.enableLoadExtension(true);
            
            // Creates a database connection
            connection = java.sql.DriverManager.getConnection("jdbc:sqlite:"+spatialiteFileName, config.toProperties());
            return connection;
        }
        catch (Exception e)
        {
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(connection);
            
            throw new RuntimeException(e.getMessage());
        }
    }    
}
