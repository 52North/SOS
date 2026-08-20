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

import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.n52.series.db.beans.Describable;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.i18n.I18nEntity;
import org.n52.series.db.beans.i18n.I18nUnitEntity;
import org.n52.shetland.ogc.UoM;

/**
 * Hibernate data access class for unit
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class UnitDAO {

    /**
     * Get unit object for unit
     *
     * @param unit
     *            Unit
     * @param session
     *            Hibernate session
     * @return Unit object
     */
    public UnitEntity getUnit(String unit, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<UnitEntity> query = cb.createQuery(UnitEntity.class);
        Root<UnitEntity> root = query.from(UnitEntity.class);
        query.where(cb.equal(root.get(UnitEntity.PROPERTY_UNIT), unit));
        return session.createQuery(query)
                .uniqueResult();
    }

    /**
     * Get unit object for unit
     *
     * @param unit
     *            Unit
     * @param session
     *            Hibernate session
     * @return Unit object
     */
    public UnitEntity getUnit(UoM unit, Session session) {
        return getUnit(unit.getUom(), session);
    }

    /**
     * Insert and get unit object
     *
     * @param unit
     *            Unit
     * @param session
     *            Hibernate session
     * @return Unit object
     */
    public UnitEntity getOrInsertUnit(String unit, Session session) {
        return getOrInsertUnit(new UoM(unit), session);
    }

    /**
     * Insert and get unit object
     *
     * @param unit
     *            Unit
     * @param session
     *            Hibernate session
     * @return Unit object
     */
    public UnitEntity getOrInsertUnit(UoM unit, Session session) {
        UnitEntity result = getUnit(unit.getUom(), session);
        if (result == null) {
            result = new UnitEntity();
            result.setUnit(unit.getUom());
            if (unit.isSetName()) {
                result.setName(unit.getName());
            }
            if (unit.isSetLink()) {
                result.setLink(unit.getLink());
            }
            session.persist(result);
            session.flush();
            session.refresh(result);
        }
        return result;
    }

    public UnitEntity getOrInsertUnit(UnitEntity unit, Session session) {
        UnitEntity result = getUnit(unit.getIdentifier(), session);
        if (result == null) {
            result = unit;
            session.persist(result);
            session.flush();
            session.refresh(result);
            if (unit.hasTranslations()) {
                insertTranslations(result, unit.getTranslations(), session);
            }
        }
        return result;
    }

    private void insertTranslations(UnitEntity result, Set<I18nEntity<? extends Describable>> translations,
            Session session) {
        for (I18nEntity<? extends Describable> i18nEntity : translations) {
            ((I18nUnitEntity) i18nEntity).setEntity(result);
            session.persist(i18nEntity);
            session.flush();
            session.refresh(i18nEntity);
        }
    }
}
