/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Hibernate data access class for procedure description format
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ProcedureDescriptionFormatDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureDescriptionFormatDAO.class);

    /**
     * Get procedure description format object
     * 
     * @param procedureDescriptionFormat
     *            Procedure description format
     * @param session
     *            Hibernate session
     * @return Procedure description format object
     */
    public ProcedureDescriptionFormat getProcedureDescriptionFormatObject(String procedureDescriptionFormat,
            Session session) {
        Criteria criteria =
                session.createCriteria(ProcedureDescriptionFormat.class).add(
                        Restrictions.eq(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT,
                                procedureDescriptionFormat));
        LOGGER.debug("QUERY getProcedureDescriptionFormatObject(procedureDescriptionFormat): {}",
                HibernateHelper.getSqlString(criteria));
        return (ProcedureDescriptionFormat) criteria.uniqueResult();
    }

    /**
     * Insert and get procedure description format
     * 
     * @param procedureDescriptionFormat
     *            Procedure description format
     * @param session
     *            Hibernate session
     * @return Procedure description format object
     */
    public ProcedureDescriptionFormat getOrInsertProcedureDescriptionFormat(String procedureDescriptionFormat,
            Session session) {
        ProcedureDescriptionFormat hProcedureDescriptionFormat =
                getProcedureDescriptionFormatObject(procedureDescriptionFormat, session);
        if (hProcedureDescriptionFormat == null) {
            hProcedureDescriptionFormat = new ProcedureDescriptionFormat();
            hProcedureDescriptionFormat.setProcedureDescriptionFormat(procedureDescriptionFormat);
            session.save(hProcedureDescriptionFormat);
            session.flush();
        }
        return hProcedureDescriptionFormat;
    }

    @SuppressWarnings("unchecked")
    public List<String> getProcedureDescriptionFormat(Session session) {
        Criteria c = session.createCriteria(ProcedureDescriptionFormat.class);
        c.setProjection(Projections.distinct(Projections.property(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT)));
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return c.list();
    }
}
