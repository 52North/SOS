/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.dao.i18n;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Session;
import org.n52.iceland.i18n.I18NDAOKey;
import org.n52.iceland.i18n.metadata.I18NProcedureMetadata;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.i18n.I18nProcedureEntity;
import org.n52.sos.ds.hibernate.dao.DaoFactory;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ProcedureI18NDAO
        extends AbstractHibernateI18NDAO<ProcedureEntity, I18NProcedureMetadata, I18nProcedureEntity> {
    private DaoFactory daoFactory;

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    protected ProcedureEntity getEntity(String id, Session session) {
        return daoFactory.getProcedureDAO().getProcedureForIdentifier(id, session);
    }

    @Override
    protected Class<I18nProcedureEntity> getHibernateEntityClass() {
        return I18nProcedureEntity.class;
    }

    @Override
    protected I18nProcedureEntity createHibernateObject() {
        return new I18nProcedureEntity();
    }

    @Override
    protected I18NProcedureMetadata createSosObject(String id) {
        return new I18NProcedureMetadata(id);
    }

    @Override
    protected void fillHibernateObject(I18NProcedureMetadata i18n, I18nProcedureEntity h18n) {
        super.fillHibernateObject(i18n, h18n);
        Optional<LocalizedString> longName = i18n.getLongName().getLocalization(LocaleHelper.decode(h18n.getLocale()));
        if (longName.isPresent()) {
            h18n.setLongName(longName.get().getText());
        }
        Optional<LocalizedString> shortName =
                i18n.getShortName().getLocalization(LocaleHelper.decode(h18n.getLocale()));
        if (shortName.isPresent()) {
            h18n.setShortName(shortName.get().getText());
        }
    }

    @Override
    protected void fillSosObject(I18nProcedureEntity h18n, I18NProcedureMetadata i18n) {
        super.fillSosObject(h18n, i18n);
        if (h18n.hasLongName()) {
            i18n.getLongName().addLocalization(h18n.getLocale(), h18n.getLongName());
        }
        if (h18n.hasShortName()) {
            i18n.getShortName().addLocalization(h18n.getLocale(), h18n.getShortName());
        }
    }

    @Override
    public Set<I18NDAOKey> getKeys() {
        return Collections.singleton(new I18NDAOKey(I18NProcedureMetadata.class));
    }

}
