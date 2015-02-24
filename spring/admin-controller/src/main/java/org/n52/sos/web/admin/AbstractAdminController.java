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
package org.n52.sos.web.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.n52.sos.ds.ConnectionProvider;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.SQLHelper;
import org.n52.sos.web.AbstractController;

/**
 * @since 4.0.0
 * 
 */
public abstract class AbstractAdminController extends AbstractController {

    private void executeSqlFile(String path) throws SQLException, FileNotFoundException, ConnectionProviderException {
        final File f = new File(getContext().getRealPath(path));
        if (!f.exists()) {
            throw new FileNotFoundException(f.getAbsolutePath() + " not found.");
        }
        ConnectionProvider p = Configurator.getInstance().getDataConnectionProvider();
        Object con = null;
        try {
            con = p.getConnection();
            if (con instanceof Connection) {
                try {
                    SQLHelper.executeSQLFile((Connection) con, f);
                } catch (IOException ex) {
                    throw new SQLException(ex);
                }
            } else if (con instanceof Session) {
                Session s = (Session) con;
                Transaction t = s.beginTransaction();
                try {
                    s.doWork(new Work() {
                        @Override
                        public void execute(Connection connection) throws SQLException {
                            try {
                                SQLHelper.executeSQLFile(connection, f);
                            } catch (IOException ex) {
                                throw new SQLException(ex);
                            }
                        }
                    });
                    t.commit();
                } catch (HibernateException e) {
                    t.rollback();
                }
            } else {
                throw new SQLException("Unknown conncetion type: " + con.getClass());
            }
        } finally {
            p.returnConnection(con);
        }
    }

    protected boolean cacheIsLoading() {
        return Configurator.getInstance().getCacheController().isUpdateInProgress();
    }    
    
    protected void updateCache() throws OwsExceptionReport {
        //don't wait for cache update to complete since it can take much longer than browser timeouts.
        //instead, start it and then check loading status from the client using cacheIsLoading().
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Configurator.getInstance().getCacheController().update();
                } catch (OwsExceptionReport e) {
                    //NOOP
                    //TODO should the last cache loading error be stored and accessible to the client?
                }                
            }            
        }).start();        
    }
}
