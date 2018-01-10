/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.i18n.HibernateI18NProcedureMetadata;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ProcedureI18NDAO extends AbstractHibernateI18NDAO<Procedure, I18NProcedureMetadata, HibernateI18NProcedureMetadata> {
    private DaoFactory daoFactory;

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    protected Procedure getEntity(String id, Session session) {
        return daoFactory.getProcedureDAO().getProcedureForIdentifier(id, session);
    }

    @Override
    protected Class<HibernateI18NProcedureMetadata> getHibernateEntityClass() {
        return HibernateI18NProcedureMetadata.class;
    }

    @Override
    protected HibernateI18NProcedureMetadata createHibernateObject() {
        return new HibernateI18NProcedureMetadata();
    }

    @Override
    protected I18NProcedureMetadata createSosObject(String id) {
        return new I18NProcedureMetadata(id);
    }

    @Override
    protected void fillHibernateObject(I18NProcedureMetadata i18n,
                                       HibernateI18NProcedureMetadata h18n) {
        super.fillHibernateObject(i18n, h18n);
        Optional<LocalizedString> longName
                = i18n.getLongName().getLocalization(h18n.getLocale());
        if (longName.isPresent()) {
            h18n.setLongname(longName.get().getText());
        }
        Optional<LocalizedString> shortName
                = i18n.getShortName().getLocalization(h18n.getLocale());
        if (shortName.isPresent()) {
            h18n.setShortname(shortName.get().getText());
        }
    }

    @Override
    protected void fillSosObject(HibernateI18NProcedureMetadata h18n,
                                 I18NProcedureMetadata i18n) {
        super.fillSosObject(h18n, i18n);
        if (h18n.isSetLongname()) {
            i18n.getLongName().addLocalization(h18n.getLocale(),
                                               h18n.getLongname());
        }
        if (h18n.isSetShortname()) {
            i18n.getShortName().addLocalization(h18n.getLocale(),
                                                h18n.getShortname());
        }
    }

    @Override
    public Set<I18NDAOKey> getKeys() {
        return Collections.singleton(new I18NDAOKey(I18NProcedureMetadata.class));
    }

}
