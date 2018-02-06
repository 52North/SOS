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
package org.n52.sos.ds.hibernate.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.UnitEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hibernate data access class for unit
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class UnitDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnitDAO.class);

    public List<UnitEntity> getUnits(Session session) {
        Criteria criteria = session.createCriteria(UnitEntity.class);
        LOGGER.debug("QUERY getUnits(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
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
    public UnitEntity getUnit(String unit, Session session) {
        Criteria criteria = session.createCriteria(UnitEntity.class).add(Restrictions.eq(UnitEntity.PROPERTY_UNIT, unit));
        LOGGER.debug("QUERY getUnit(): {}", HibernateHelper.getSqlString(criteria));
        return (UnitEntity) criteria.uniqueResult();
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
        Criteria criteria = session.createCriteria(UnitEntity.class).add(Restrictions.eq(UnitEntity.PROPERTY_UNIT, unit.getUom()));
        LOGGER.debug("QUERY getUnit(): {}", HibernateHelper.getSqlString(criteria));
        return (UnitEntity) criteria.uniqueResult();
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
            session.save(result);
            session.flush();
            session.refresh(result);
        }
        return result;
    }
}
