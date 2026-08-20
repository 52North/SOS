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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.n52.series.db.beans.FormatEntity;

/**
 * Hibernate data access class for procedure description format
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class FormatDAO {

    public List<String> getFormatEntity(Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<FormatEntity> root = query.from(FormatEntity.class);
        query.select(root.get(FormatEntity.FORMAT))
                .distinct(true);
        return session.createQuery(query)
                .list();
    }

    /**
     * Get procedure description format object
     *
     * @param format
     *            Procedure description format
     * @param session
     *            Hibernate session
     * @return Procedure description format object
     */
    public FormatEntity getFormatEntityObject(String format,
            Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<FormatEntity> query = cb.createQuery(FormatEntity.class);
        Root<FormatEntity> root = query.from(FormatEntity.class);
        query.where(cb.equal(root.get(FormatEntity.FORMAT), format));
        return session.createQuery(query)
                .uniqueResult();
    }

    /**
     * Insert and get procedure description format
     *
     * @param format
     *            Procedure description format
     * @param session
     *            Hibernate session
     * @return Procedure description format object
     */
    public FormatEntity getOrInsertFormatEntity(String format,
            Session session) {
        FormatEntity hFormatEntity =
                getFormatEntityObject(format, session);
        if (hFormatEntity == null) {
            hFormatEntity = new FormatEntity();
            hFormatEntity.setFormat(format);
            session.persist(hFormatEntity);
            session.flush();
        }
        return hFormatEntity;
    }

    /**
     * Insert or/and get observation type objects for observation types
     *
     * @param observationTypes
     *            Observation types
     * @param session
     *            Hibernate session
     * @return Observation type objects
     */
    public List<FormatEntity> getOrInsertFormatEntitys(Set<String> observationTypes, Session session) {
        List<FormatEntity> obsTypes = new LinkedList<>();
        for (String observationType : observationTypes) {
            obsTypes.add(getOrInsertFormatEntity(observationType, session));
        }
        return obsTypes;
    }
}
