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
package org.n52.sos.ds.hibernate.admin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.ReturningWork;
import org.n52.sos.ds.GeneralQueryDAO;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.util.SQLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class that deals with crud operations related to SOS DB.
 * 
 * @author Shubham Sachdeva
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 * 
 */
public class HibernateGeneralQueryDAO implements GeneralQueryDAO {
    private static final Logger LOG = LoggerFactory.getLogger(HibernateGeneralQueryDAO.class);

    private static final String[] MODIFY_COMMANDS = { "alter ", "create ", "drop ", "truncate ", "rename " };

    private static final String[] UPDATE_COMMANDS = { "update ", "insert ", "delete " };

    private static boolean contains(String[] commands, String query) {
        for (String command : commands) {
            if (query.contains(command)) {
                return true;
            }
        }
        return false;
    }

    private HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    /**
     * Method which query the SOS DB
     * 
     * @param query
     *            normal sql query concerning any table
     * 
     * @return query result
     * 
     * @throws SQLException
     */
    @Override
    public QueryResult query(final String query) throws SQLException {
        String q = query.toLowerCase();
        if (contains(MODIFY_COMMANDS, q)) {
            return doTransactionalWork(new ModifyWork(), query);
        } else if (contains(UPDATE_COMMANDS, q)) {
            return doTransactionalWork(new UpdateWork(), query);
        } else {
            return doWork(new SelectWork(), query);
        }
    }

    private QueryResult doWork(QueryWork work, String query) {
        Session s = null;
        try {
            s = sessionHolder.getSession();
            return s.doReturningWork(work.setQuery(query));
        } catch (Exception ex) {
            return new ErrorResult(ex);
        } finally {
            sessionHolder.returnSession(s);
        }
    }

    private QueryResult doTransactionalWork(QueryWork work, String query) {
        Session s = null;
        try {
            s = sessionHolder.getSession();
            Transaction t = s.beginTransaction();
            try {
                QueryResult result = s.doReturningWork(work.setQuery(query));
                t.commit();
                return result;
            } catch (HibernateException e) {
                t.rollback();
                throw e;
            }
        } catch (Exception ex) {
            return new ErrorResult(ex);
        } finally {
            sessionHolder.returnSession(s);
        }
    }

    private class ErrorResult extends QueryResult {
        ErrorResult(Exception e) {
            super(String.format("Unable to execute the query. Cause: %s", e.getMessage()), true);
            LOG.error("Unable to execute the query.", e);
        }
    }

    private abstract class QueryWork implements ReturningWork<QueryResult> {
        private String query;

        String getQuery() {
            return query;
        }

        QueryWork setQuery(String query) {
            this.query = query;
            return this;
        }
    }

    private class SelectWork extends QueryWork {
        @Override
        public QueryResult execute(Connection conn) throws SQLException {
            Statement stmt = null;
            ResultSet rset = null;
            try {
                stmt = conn.createStatement();
                rset = stmt.executeQuery(getQuery());
                ResultSetMetaData meta = rset.getMetaData();
                int columnCount = meta.getColumnCount();
                QueryResult q = new QueryResult().setColumnNames(getColumnNames(columnCount, meta));
                while (rset.next()) {
                    q.addRow(getRow(columnCount, rset));
                }
                return q;
            } catch (Exception ex) {
                return new ErrorResult(ex);
            } finally {
                SQLHelper.close(rset);
                SQLHelper.close(stmt);
            }
        }

        protected Row getRow(int columnCount, ResultSet rset) throws SQLException {
            ArrayList<String> values = new ArrayList<String>(columnCount);
            for (int i = 1; i <= columnCount; ++i) {
                values.add(rset.getString(i));
            }
            return new Row().setValues(values);
        }

        protected List<String> getColumnNames(int columnCount, ResultSetMetaData meta) throws SQLException {
            ArrayList<String> names = new ArrayList<String>(columnCount);
            for (int i = 1; i <= columnCount; ++i) {
                names.add(meta.getColumnLabel(i));
            }
            return names;
        }
    }

    private class UpdateWork extends QueryWork {
        @Override
        public QueryResult execute(Connection conn) throws SQLException {
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                int result = stmt.executeUpdate(getQuery());
                return new QueryResult(String.format("%d rows affected!", result));
            } catch (Exception ex) {
                return new ErrorResult(ex);
            } finally {
                SQLHelper.close(stmt);
            }
        }
    }

    private class ModifyWork extends QueryWork {
        @Override
        public QueryResult execute(Connection conn) throws SQLException {
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                stmt.execute(getQuery());
                return new QueryResult("Success!");
            } catch (Exception ex) {
                return new ErrorResult(ex);
            } finally {
                SQLHelper.close(stmt);
            }
        }
    }
}
