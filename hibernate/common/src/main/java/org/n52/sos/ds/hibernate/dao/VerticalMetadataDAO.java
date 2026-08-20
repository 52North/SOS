/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.n52.series.db.beans.VerticalMetadataEntity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class VerticalMetadataDAO {

    private final DaoFactory daoFactory;

    public VerticalMetadataDAO(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public VerticalMetadataEntity getOrInsertVerticalMetadata(VerticalMetadataEntity entity, Session session) {
        if (!session.contains(entity)) {
            VerticalMetadataEntity verticalMetadata = getVerticalMetadataFor(entity, session);
            if (verticalMetadata == null) {
                session.persist(entity);
                session.flush();
                session.refresh(entity);
                return entity;
            }
            return verticalMetadata;
        }
        return entity;
    }

    private VerticalMetadataEntity getVerticalMetadataFor(VerticalMetadataEntity verticalMetadata, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<VerticalMetadataEntity> query = cb.createQuery(VerticalMetadataEntity.class);
        Root<VerticalMetadataEntity> root = query.from(VerticalMetadataEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(VerticalMetadataEntity.PROPERTY_VERTICAL_UNIT),
                verticalMetadata.getVerticalUnit()));
        if (verticalMetadata.isSetOrientation()) {
            predicates.add(cb.equal(root.get(VerticalMetadataEntity.PROPERTY_VERTICAL_ORIENTATION),
                    verticalMetadata.getOrientation()));
        }
        if (verticalMetadata.isSetVerticalOriginName()) {
            predicates.add(cb.equal(root.get(VerticalMetadataEntity.PROPERTY_VERTICAL_ORIGIN_NAME),
                    verticalMetadata.getVerticalOriginName()));
        }
        if (verticalMetadata.isSetVerticalFromName()) {
            predicates.add(cb.equal(root.get(VerticalMetadataEntity.PROPERTY_VERTICAL_FROM_NAME),
                    verticalMetadata.getVerticalFromName()));
        }
        if (verticalMetadata.isSetVerticalToName()) {
            predicates.add(cb.equal(root.get(VerticalMetadataEntity.PROPERTY_VERTICAL_TO_NAME),
                    verticalMetadata.getVerticalToName()));
        }
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query)
                .uniqueResult();
    }

}
