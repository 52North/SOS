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
package org.n52.sos.ds.hibernate.dao.i18n;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Session;
import org.n52.iceland.i18n.I18NDAOKey;
import org.n52.iceland.i18n.metadata.I18NObservablePropertyMetadata;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.i18n.I18nPhenomenonEntity;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ObservablePropertyI18NDAO extends AbstractHibernateI18NDAO<PhenomenonEntity,
                                                                        I18NObservablePropertyMetadata,
                                                                        I18nPhenomenonEntity> {
    private final DaoFactory daoFactory;

    @Inject
    public ObservablePropertyI18NDAO(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    protected PhenomenonEntity getEntity(String id, Session session) {
        return new ObservablePropertyDAO(daoFactory).getObservablePropertyForIdentifier(id, session);
    }

    @Override
    protected Class<I18nPhenomenonEntity> getHibernateEntityClass() {
        return I18nPhenomenonEntity.class;
    }

    @Override
    protected I18NObservablePropertyMetadata createSosObject(String id) {
        return new I18NObservablePropertyMetadata(id);
    }

    @Override
    protected I18nPhenomenonEntity createHibernateObject() {
        return new I18nPhenomenonEntity();
    }

    @Override
    public Set<I18NDAOKey> getKeys() {
        return Collections.singleton(new I18NDAOKey(I18NObservablePropertyMetadata.class));
    }

}
