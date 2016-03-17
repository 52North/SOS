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
package org.n52.sos.extensions.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to safely open and clean up after connections.
 */
public class JDBCUtils 
{
    private static final Logger LOG = Logger.getLogger(JDBCUtils.class.toString());
    
    /** No constructor available. */
    protected JDBCUtils()
    {
        super();
    }
    
    /**
     * A utility method for closing a Statement. Wraps and logs any exceptions thrown by the close method.
     */
    public static void close(Statement statement)
    {
        if (statement!=null)
        {
            try
            {
                statement.close();
            }
            catch (SQLException e)
            {
                String msg = "Error closing JDBC Statement";
                LOG.log(Level.WARNING, msg, e);
            }
        }
    }
    
    /**
     * A utility method for closing a ResultSet. Wraps and logs any exceptions thrown by the close method.
     */
    public static void close(ResultSet rs) 
    {
        if (rs!=null)
        {
            try
            {
                rs.close();
            }
            catch (Exception e) 
            {
                String msg = "Error closing JDBC ResultSet";
                LOG.log(Level.WARNING, msg, e);
            }
        }
    }
    
    /**
     * A utility method for closing a Connection. Wraps and logs any exceptions thrown by the close method.
     */
    public static void close(Connection connection)
    {
        if (connection!=null)
        {
            try
            {
                if (!connection.isClosed()) connection.close();
            }
            catch (SQLException e)
            {
                String msg = "Error closing JDBC Connection";
                LOG.log(Level.WARNING, msg, e);
            }
        }
    }
}
