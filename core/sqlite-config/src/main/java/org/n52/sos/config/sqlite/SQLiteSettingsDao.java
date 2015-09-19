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
import java.util.regex.Pattern;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.config.SettingValue;
import org.n52.iceland.config.SettingsDao;
import org.n52.sos.config.sqlite.entities.AbstractSettingValue;

public class SQLiteSettingsDao
        extends AbstractSQLiteDao
        implements SettingsDao {
    private static final Logger LOG = LoggerFactory.getLogger(SQLiteSettingsDao.class);

    private static final Pattern SETTINGS_TYPE_CHANGED = Pattern
            .compile(".*Abort due to constraint violation \\(column .* is not unique\\)");

    @Override
    public SettingValue<?> getSettingValue(String key) {
        return execute(new GetSettingValueAction(key));
    }

    @Override
    public void saveSettingValue(SettingValue<?> setting) {
        LOG.debug("Saving Setting {}", setting);
        try {
            execute(new SaveSettingValueAction(setting));
        } catch (HibernateException e) {
            if (isSettingsTypeChangeException(e)) {
                LOG.warn("Type of setting {} changed!", setting.getKey());
                execute(new DeleteAndSaveValueAction(setting));
            } else {
                throw e;
            }
        }
    }

    @Override
    public Set<SettingValue<?>> getSettingValues() {
        return execute(new GetSettingValuesAction());
    }

    @Override
    public void deleteSettingValue(String setting) {
        execute(new DeleteSettingValueAction(setting));
    }

    protected boolean isSettingsTypeChangeException(HibernateException e) {
        return e.getMessage() != null && SETTINGS_TYPE_CHANGED.matcher(e.getMessage()).matches();
    }



    @Override
    public void deleteAll() {
        execute(new DeleteAllAction());
    }

    private class DeleteAllAction extends VoidHibernateAction {
        @Override
        @SuppressWarnings("unchecked")
        protected void run(Session session) {
            List<AbstractSettingValue<?>> settings =
                    session.createCriteria(AbstractSettingValue.class)
                            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
            for (SettingValue<?> v : settings) {
                session.delete(v);
            }
        }
    }

    private class DeleteSettingValueAction extends VoidHibernateAction {
        private final String setting;

        DeleteSettingValueAction(String setting) {
            this.setting = setting;
        }

        @Override
        protected void run(Session session) {
            AbstractSettingValue<?> hSetting =
                    (AbstractSettingValue<?>) session.get(AbstractSettingValue.class, setting);
            if (hSetting != null) {
                LOG.debug("Deleting Setting {}", hSetting);
                session.delete(hSetting);
            }
        }
    }

    private class GetSettingValueAction implements HibernateAction<SettingValue<?>> {
        private final String key;

        GetSettingValueAction(String key) {
            this.key = key;
        }

        @Override
        public SettingValue<?> call(Session session) {
            return (SettingValue<?>) session.get(AbstractSettingValue.class, key);
        }
    }

    private class SaveSettingValueAction extends VoidHibernateAction {
        private final SettingValue<?> setting;

        SaveSettingValueAction(SettingValue<?> setting) {
            this.setting = setting;
        }

        @Override
        protected void run(Session session) {
            session.saveOrUpdate(setting);
        }
    }

    private class DeleteAndSaveValueAction extends VoidHibernateAction {
        private final SettingValue<?> setting;

        DeleteAndSaveValueAction(SettingValue<?> setting) {
            this.setting = setting;
        }

        @Override
        protected void run(Session session) {
            AbstractSettingValue<?> hSetting =
                    (AbstractSettingValue<?>) session.get(AbstractSettingValue.class, setting.getKey());
            if (hSetting != null) {
                LOG.debug("Deleting Setting {}", hSetting);
                session.delete(hSetting);
            }
            LOG.debug("Saving Setting {}", setting);
            session.save(setting);
        }
    }

    private class GetSettingValuesAction implements HibernateAction<Set<SettingValue<?>>> {
        @Override
        @SuppressWarnings("unchecked")
        public Set<SettingValue<?>> call(Session session) {
            return new HashSet<>(session.createCriteria(AbstractSettingValue.class)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list());
        }
    }
}
