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
package org.n52.sos.ds.hibernate.dao;

import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.sta.HistoricalLocationEntity;
import org.n52.series.db.beans.sta.LocationEntity;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlatformDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformDAO.class);
    private static final String ENCODINGTYPE_GEOJSON = "application/vnd.geo+json";

    public PlatformDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    public PlatformEntity getPlatformForIdentifier(String identifier, Session session) {
        Criteria criteria = session.createCriteria(PlatformEntity.class)
                .add(Restrictions.eq(PlatformEntity.IDENTIFIER, identifier));
        LOGGER.trace("QUERY getPlatformForIdentifier(identifier): {}",
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
            platform.setIdentifier(feature.getIdentifier(), getDaoFactory().isStaSupportsUrls());
            platform.setIdentifierCodespace(feature.getIdentifierCodespace());
            platform.setName(feature.getName());
            platform.setNameCodespace(feature.getNameCodespace());
            platform.setDescription(feature.getDescription());
            session.save(platform);
            session.flush();
            session.refresh(platform);
            processSta(platform, feature, session);
        }
        return platform;
    }

    private void processSta(PlatformEntity platform, AbstractFeatureEntity<?> feature, Session session) {
        if (HibernateHelper.isEntitySupported(LocationEntity.class)) {
            LocationEntity location = getOrInsertLocation(feature, session);
            platform.addLocationEntity(location);
            HistoricalLocationEntity historicalLocation = getOrInsertHistoricalLocation(platform, location, session);
            platform.addHistoricalLocation(historicalLocation);

            session.save(platform);
            session.flush();
            session.refresh(platform);
        }
    }

    private LocationEntity getOrInsertLocation(AbstractFeatureEntity<?> feature, Session session) {
        LocationEntity location = new LocationEntity();
        location.setIdentifier(feature.isSetIdentifier() ? feature.getIdentifier()
                : UUID.randomUUID()
                        .toString(),
                getDaoFactory().isStaSupportsUrls());
        location.setIdentifierCodespace(feature.getIdentifierCodespace());
        location.setName(feature.isSetName() ? feature.getName() : location.getIdentifier());
        location.setNameCodespace(feature.getNameCodespace());
        location.setDescription(feature.isSetDescription() ? feature.getDescription() : location.getName());
        location.setLocationEncoding(
                getDaoFactory().getFeatureTypeDAO().getOrInsertFormatEntity(ENCODINGTYPE_GEOJSON, session));
        location.setGeometryEntity(feature.getGeometryEntity());

        session.save(location);
        session.flush();
        session.refresh(location);
        return location;
    }

    private HistoricalLocationEntity getOrInsertHistoricalLocation(PlatformEntity platform, LocationEntity location,
            Session session) {
        HistoricalLocationEntity historicalLocation = new HistoricalLocationEntity();
        historicalLocation.setIdentifier(UUID.randomUUID().toString(), getDaoFactory().isStaSupportsUrls());
        historicalLocation.setThing(platform);
        historicalLocation.setTime(DateTime.now().toDate());

        session.save(historicalLocation);
        session.flush();
        session.refresh(historicalLocation);

        location.addHistoricalLocation(historicalLocation);
        session.saveOrUpdate(location);
        session.flush();
        return historicalLocation;
    }
}
