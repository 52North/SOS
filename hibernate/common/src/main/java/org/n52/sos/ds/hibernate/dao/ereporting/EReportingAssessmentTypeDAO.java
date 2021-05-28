/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.dao.ereporting;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.AssessmentTypeEntity;
import org.n52.shetland.aqd.AqdConstants.AssessmentType;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EReportingAssessmentTypeDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(EReportingAssessmentTypeDAO.class);

    private static final String LOG_TEMPLATE = "QUERY getEReportingAssessmentTypes(): {}";

    @SuppressWarnings("unchecked")
    public List<AssessmentTypeEntity> getEReportingAssessmentTypes(Session session) {
        Criteria c = getDefaultCriteria(session);
        LOGGER.trace(LOG_TEMPLATE, HibernateHelper.getSqlString(c));
        return (List<AssessmentTypeEntity>) c.list();
    }

    public AssessmentTypeEntity getEReportingAssessmentType(AssessmentType assessmentType, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(AssessmentTypeEntity.PROPERTY_ID, assessmentType.getId()));
        LOGGER.trace(LOG_TEMPLATE, HibernateHelper.getSqlString(c));
        return (AssessmentTypeEntity) c.uniqueResult();
    }

    public AssessmentTypeEntity getEReportingAssessmentType(String assessmentType, Session session) {
        Criteria c = getDefaultCriteria(session);
        c.add(Restrictions.eq(AssessmentTypeEntity.PROPERTY_ID, assessmentType));
        LOGGER.trace(LOG_TEMPLATE, HibernateHelper.getSqlString(c));
        return (AssessmentTypeEntity) c.uniqueResult();
    }

    private Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(AssessmentTypeEntity.class).setResultTransformer(
                Criteria.DISTINCT_ROOT_ENTITY);
    }

    public AssessmentTypeEntity getOrInsert(AssessmentType assessmentType, Session session) {
        AssessmentTypeEntity eReportingAssessmentType = getEReportingAssessmentType(assessmentType, session);
        if (eReportingAssessmentType == null) {
            eReportingAssessmentType = new AssessmentTypeEntity();
            eReportingAssessmentType.setAssessmentType(assessmentType.getId());
            eReportingAssessmentType.setUri(assessmentType.getConceptURI());
            session.saveOrUpdate(eReportingAssessmentType);
            session.flush();
            session.refresh(eReportingAssessmentType);
        }
        return eReportingAssessmentType;
    }

}
