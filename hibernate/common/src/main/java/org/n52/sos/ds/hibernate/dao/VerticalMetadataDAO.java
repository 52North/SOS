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
package org.n52.sos.ds.hibernate.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.VerticalMetadataEntity;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticalMetadataDAO {

    private static final Logger LOG = LoggerFactory.getLogger(VerticalMetadataDAO.class);
    private final DaoFactory daoFactory;

    public VerticalMetadataDAO(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public VerticalMetadataEntity getOrInsertVerticalMetadata(VerticalMetadataEntity entity, Session session) {
        VerticalMetadataEntity verticalMetadata = getVerticalMetadataFor(entity, session);
        if (verticalMetadata == null) {
            session.save(entity);
            session.flush();
            session.refresh(entity);
            return entity;
        }
        return verticalMetadata;
    }

    private VerticalMetadataEntity getVerticalMetadataFor(VerticalMetadataEntity verticalMetadata, Session session) {
        Criteria criteria = session.createCriteria(VerticalMetadataEntity.class).add(
                Restrictions.eq(VerticalMetadataEntity.PROPERTY_VERTICAL_UNIT, verticalMetadata.getVerticalUnit()));
        if (verticalMetadata.isSetOrientation()) {
            criteria.add(Restrictions.eq(VerticalMetadataEntity.PROPERTY_VERTICAL_ORIENTATION,
                    verticalMetadata.getOrientation()));
        }
        if (verticalMetadata.isSetVerticalOriginName()) {
            criteria.add(Restrictions.eq(VerticalMetadataEntity.PROPERTY_VERTICAL_ORIGIN_NAME,
                    verticalMetadata.getVerticalOriginName()));
        }
        if (verticalMetadata.isSetVerticalFromName()) {
            criteria.add(Restrictions.eq(VerticalMetadataEntity.PROPERTY_VERTICAL_FROM_NAME,
                    verticalMetadata.getVerticalFromName()));
        }
        if (verticalMetadata.isSetVerticalToName()) {
            criteria.add(Restrictions.eq(VerticalMetadataEntity.PROPERTY_VERTICAL_TO_NAME,
                    verticalMetadata.getVerticalToName()));
        }
        LOG.trace("QUERY getCategoryForIdentifier(identifier): {}",
                HibernateHelper.getSqlString(criteria));
        return (VerticalMetadataEntity) criteria.uniqueResult();
    }

}
