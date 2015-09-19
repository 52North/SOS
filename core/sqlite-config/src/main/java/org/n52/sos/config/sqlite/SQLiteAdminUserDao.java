/*
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
package org.n52.sos.config.sqlite;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.config.AdminUserDao;
import org.n52.iceland.config.AdministratorUser;
import org.n52.sos.config.sqlite.entities.AdminUser;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SQLiteAdminUserDao extends AbstractSQLiteDao
        implements AdminUserDao {

    private static final Logger LOG = LoggerFactory
            .getLogger(SQLiteAdminUserDao.class);

    @Override
    public AdministratorUser getAdminUser(String username) {
        return execute(new GetAdminUserAction(username));
    }

    @Override
    public AdministratorUser createAdminUser(String username, String password) {
        return execute(new CreateAdminUserAction(username, password));
    }

    @Override
    public void saveAdminUser(AdministratorUser user) {
        execute(new SaveAdminUserAction(user));
    }

    @Override
    public void deleteAdminUser(String username) {
        execute(new DeleteAdminUserAction(username));
    }

    @Override
    public Set<AdministratorUser> getAdminUsers() {
        return execute(new GetAdminUsersAction());
    }

    @Override
    public void deleteAll() {
        execute(new DeleteAllAction());
    }

    private class DeleteAllAction extends VoidHibernateAction {
        @Override
        @SuppressWarnings("unchecked")
        protected void run(Session session) {
            List<AdministratorUser> users = session
                    .createCriteria(AdministratorUser.class)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
            users.forEach(session::delete);
        }
    }

    private class GetAdminUserAction implements HibernateAction<AdminUser> {
        private final String username;

        GetAdminUserAction(String username) {
            this.username = username;
        }

        @Override
        public AdminUser call(Session session) {
            return (AdminUser) session.createCriteria(AdminUser.class)
                    .add(Restrictions.eq(AdminUser.USERNAME_PROPERTY, username))
                    .uniqueResult();
        }
    }

    private class DeleteAdminUserAction extends VoidHibernateAction {
        private final String username;

        DeleteAdminUserAction(String username) {
            this.username = username;
        }

        @Override
        protected void run(Session session) {
            AdministratorUser au = (AdministratorUser) session
                    .createCriteria(AdministratorUser.class)
                    .add(Restrictions.eq(AdminUser.USERNAME_PROPERTY, username))
                    .uniqueResult();
            if (au != null) {
                session.delete(au);
            }
        }
    }

    private class GetAdminUsersAction implements
            HibernateAction<Set<AdministratorUser>> {
        @Override
        @SuppressWarnings("unchecked")
        public Set<AdministratorUser> call(Session session) {
            return new HashSet<>(session.createCriteria(AdministratorUser.class)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list());
        }
    }

    private class SaveAdminUserAction extends VoidHibernateAction {
        private final AdministratorUser user;

        SaveAdminUserAction(AdministratorUser user) {
            this.user = user;
        }

        @Override
        protected void run(Session session) {
            LOG.debug("Updating AdministratorUser {}", user);
            session.update(user);
        }
    }

    private class CreateAdminUserAction implements HibernateAction<AdminUser> {
        private final String username;

        private final String password;

        CreateAdminUserAction(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public AdminUser call(Session session) {
            AdminUser user = new AdminUser().setUsername(username)
                    .setPassword(password);
            LOG.debug("Creating AdministratorUser {}", user);
            session.save(user);
            return user;
        }
    }
}
