/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.aquarius.dao;

import java.util.Locale;

import javax.inject.Inject;

import org.hibernate.Session;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.ds.ConnectionProviderException;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.series.db.old.HibernateSessionStore;
import org.n52.sos.aquarius.ds.AquariusConnectionFactory;
import org.n52.sos.aquarius.ds.AquariusConnector;
import org.n52.sos.ds.dao.DefaultDao;
import org.n52.sos.service.profile.ProfileHandler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public abstract class AbstractAquariusDao implements DefaultDao {

    private AquariusConnectionFactory connectorFactory;

    private HibernateSessionStore sessionStore;

    private ProfileHandler profileHandler;

    private Locale defaultLanguage;

    @Inject
    public void setPegelOnlineConnectionFactory(AquariusConnectionFactory connectorFactory) {
        this.connectorFactory = connectorFactory;
    }

    @Inject
    public void setConnectionProvider(HibernateSessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = LocaleHelper.decode(defaultLanguage);
    }

    protected AquariusConnector getAquariusConnector() throws ConnectionProviderException {
        return connectorFactory.getConnection();
    }

    protected AquariusConnector getAquariusConnector(Object connection) throws ConnectionProviderException {
        return connection != null && connection instanceof AquariusConnector ? (AquariusConnector) connection
                : getAquariusConnector();
    }

    protected HibernateSessionStore getSessionStore() {
        return sessionStore;
    }

    protected boolean checkAquariusConnection(Object connection) {
        return connection != null && connection instanceof AquariusConnector;
    }

    protected boolean checkHibernateConnection(Object connection) {
        return connection != null && connection instanceof Session;
    }

    protected ProfileHandler getProfileHandler() {
        return profileHandler;
    }

    @Override
    public Locale getDefaultLanguage() {
        return defaultLanguage;
    }

}
