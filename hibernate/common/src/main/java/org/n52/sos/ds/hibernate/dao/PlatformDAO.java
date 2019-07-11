/*
 * Copyright (C) 2012-2019 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformDAO.class);

    public PlatformDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    public PlatformEntity getPlatformForIdentifier(String identifier, Session session) {
        Criteria criteria = session.createCriteria(PlatformEntity.class)
                .add(Restrictions.eq(PlatformEntity.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getPlatformForIdentifier(identifier): {}",
                HibernateHelper.getSqlString(criteria));
        return (PlatformEntity) criteria.uniqueResult();
    }

    public PlatformEntity getOrInsertPlatform(String value, Session session) {
        PlatformEntity platform = getPlatformForIdentifier(value, session);
        if (platform == null) {
            platform = new PlatformEntity();
            addIdentifier(platform, value, session);
            addName(platform, value, session);
            session.save(platform);
            session.flush();
            session.refresh(platform);
        }
        return platform;
    }

    public PlatformEntity getOrInsertPlatform(SweText sweText, Session session) {
        PlatformEntity platform = getPlatformForIdentifier(sweText.getValue(), session);
        if (platform == null) {
            platform = new PlatformEntity();
            addIdentifier(platform, sweText.getValue(), session);
            addName(platform, sweText.getName(), session);
            addDescription(platform, sweText.getDescription());
            session.save(platform);
            session.flush();
            session.refresh(platform);
        }
        return platform;
    }

    public PlatformEntity getOrInsertPlatform(AbstractFeatureEntity<?> feature, Session session) {
        PlatformEntity platform = getPlatformForIdentifier(feature.getIdentifier(), session);
        if (platform == null) {
            platform = new PlatformEntity();
            platform.setIdentifier(feature.getIdentifier());
            platform.setIdentifierCodespace(feature.getIdentifierCodespace());
            platform.setName(feature.getName());
            platform.setNameCodespace(feature.getNameCodespace());
            platform.setDescription(feature.getDescription());
            session.save(platform);
            session.flush();
            session.refresh(platform);
        }
        return platform;
    }
}
