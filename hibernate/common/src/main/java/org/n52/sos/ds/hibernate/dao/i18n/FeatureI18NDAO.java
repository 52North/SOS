/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Session;
import org.n52.iceland.i18n.I18NDAOKey;
import org.n52.iceland.i18n.metadata.I18NFeatureMetadata;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.i18n.I18nFeatureEntity;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;

public class FeatureI18NDAO
        extends AbstractHibernateI18NDAO<AbstractFeatureEntity, I18NFeatureMetadata, I18nFeatureEntity> {
    private DaoFactory daoFactory;

    @Inject
    public FeatureI18NDAO(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Override
    protected AbstractFeatureEntity getEntity(String id, Session session) {
        return new FeatureOfInterestDAO(daoFactory).get(id, session);
    }

    @Override
    protected Class<I18nFeatureEntity> getHibernateEntityClass() {
        return I18nFeatureEntity.class;
    }

    @Override
    protected I18nFeatureEntity createHibernateObject() {
        return new I18nFeatureEntity();
    }

    @Override
    protected I18NFeatureMetadata createSosObject(String id) {
        return new I18NFeatureMetadata(id);
    }

    @Override
    public Set<I18NDAOKey> getKeys() {
        return Collections.singleton(new I18NDAOKey(I18NFeatureMetadata.class));
    }

}
