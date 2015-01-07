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
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.ogc.gml.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Hibernate data access class for valid procedure time
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ValidProcedureTimeDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidProcedureTimeDAO.class);

    /**
     * Insert valid procedure time for procedrue
     * 
     * @param procedure
     *            Procedure object
     * @param xmlDescription
     *            Procedure XML description
     * @param validStartTime
     *            Valid start time
     * @param session
     *            Hibernate session
     */
    public void insertValidProcedureTime(Procedure procedure, ProcedureDescriptionFormat procedureDescriptionFormat,
            String xmlDescription, DateTime validStartTime, Session session) {
        ValidProcedureTime vpd = new ValidProcedureTime();
        vpd.setProcedure(procedure);
        vpd.setProcedureDescriptionFormat(procedureDescriptionFormat);
        vpd.setDescriptionXml(xmlDescription);
        vpd.setStartTime(validStartTime.toDate());
        session.save(vpd);
        session.flush();
    }

    /**
     * Update valid procedure time object
     * 
     * @param validProcedureTime
     *            Valid procedure time object
     * @param session
     *            Hibernate session
     */
    public void updateValidProcedureTime(ValidProcedureTime validProcedureTime, Session session) {
        session.saveOrUpdate(validProcedureTime);
    }

    /**
     * Set valid end time to valid procedure time object for procedure
     * identifier
     * 
     * @param procedureIdentifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @throws UnsupportedOperatorException
     * @throws UnsupportedValueReferenceException
     * @throws UnsupportedTimeException
     */
    public void setValidProcedureDescriptionEndTime(String procedureIdentifier, String procedureDescriptionFormat,
            Session session) throws UnsupportedTimeException, UnsupportedValueReferenceException,
            UnsupportedOperatorException {
        TProcedure procedure =
                new ProcedureDAO().getTProcedureForIdentifier(procedureIdentifier, procedureDescriptionFormat, null,
                        session);
        Set<ValidProcedureTime> validProcedureTimes = procedure.getValidProcedureTimes();
        for (ValidProcedureTime validProcedureTime : validProcedureTimes) {
            if (validProcedureTime.getEndTime() == null) {
                validProcedureTime.setEndTime(new DateTime(DateTimeZone.UTC).toDate());
            }
        }
    }

    /**
     * Set valid end time to valid procedure time object for procedure
     * identifier
     * 
     * @param procedureIdentifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     */
    public void setValidProcedureDescriptionEndTime(String procedureIdentifier, Session session) {
        TProcedure procedure = new ProcedureDAO().getTProcedureForIdentifierIncludeDeleted(procedureIdentifier, session);
        Set<ValidProcedureTime> validProcedureTimes = procedure.getValidProcedureTimes();
        for (ValidProcedureTime validProcedureTime : validProcedureTimes) {
            if (validProcedureTime.getEndTime() == null) {
                validProcedureTime.setEndTime(new DateTime(DateTimeZone.UTC).toDate());
            }
        }
    }

    /**
     * Get ValidProcedureTimes for requested parameters
     * 
     * @param procedure
     *            Requested Procedure
     * @param procedureDescriptionFormat
     *            Requested procedureDescriptionFormat
     * @param validTime
     *            Requested validTime (optional)
     * @param session
     *            Hibernate session
     * @return List with ValidProcedureTime objects
     * @throws UnsupportedTimeException
     *             If validTime time value is invalid
     * @throws UnsupportedValueReferenceException
     *             If valueReference is not supported
     * @throws UnsupportedOperatorException
     *             If temporal operator is not supported
     */
    @SuppressWarnings("unchecked")
    public List<ValidProcedureTime> getValidProcedureTimes(Procedure procedure, String procedureDescriptionFormat,
            Time validTime, Session session) throws UnsupportedTimeException, UnsupportedValueReferenceException,
            UnsupportedOperatorException {
        Criteria criteria = session.createCriteria(ValidProcedureTime.class);
        criteria.add(Restrictions.eq(ValidProcedureTime.PROCEDURE, procedure));
        criteria.createCriteria(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT).add(
                Restrictions.eq(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT, procedureDescriptionFormat));

        Criterion validTimeCriterion = QueryHelper.getValidTimeCriterion(validTime);
        // if validTime == null or validTimeCriterion == null, query latest
        // valid procedure description
        if (validTime == null || validTimeCriterion == null) {
            criteria.add(Restrictions.isNull(ValidProcedureTime.END_TIME));
        } else {
            criteria.add(validTimeCriterion);
        }
        LOGGER.debug("QUERY getValidProcedureTimes(procedure,procedureDescriptionFormat, validTime): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public List<ValidProcedureTime> getValidProcedureTimes(TProcedure procedure,
            Set<String> possibleProcedureDescriptionFormats, Time validTime, Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Criteria criteria = session.createCriteria(ValidProcedureTime.class);
        criteria.add(Restrictions.eq(ValidProcedureTime.PROCEDURE, procedure));
        criteria.createCriteria(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT).add(
                Restrictions.in(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT,
                        possibleProcedureDescriptionFormats));

        Criterion validTimeCriterion = QueryHelper.getValidTimeCriterion(validTime);
        // if validTime == null or validTimeCriterion == null, query latest
        // valid procedure description
        if (validTime == null || validTimeCriterion == null) {
            criteria.add(Restrictions.isNull(ValidProcedureTime.END_TIME));
        } else {
            criteria.add(validTimeCriterion);
        }
        LOGGER.debug("QUERY getValidProcedureTimes(procedure, possibleProcedureDescriptionFormats, validTime): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    public Map<String,String> getTProcedureFormatMap(Session session) {
        Criteria criteria = session.createCriteria(TProcedure.class);
        criteria.createAlias(TProcedure.VALID_PROCEDURE_TIME, "vpt");
        criteria.createAlias(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT, "pdf");
        criteria.add(Restrictions.isNull("vpt." + ValidProcedureTime.END_TIME));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property(TProcedure.IDENTIFIER))
                .add(Projections.property("pdf." + ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT)));
        criteria.addOrder(Order.asc(TProcedure.IDENTIFIER));
        LOGGER.debug("QUERY getTProcedureFormatMap(): {}", HibernateHelper.getSqlString(criteria));
        @SuppressWarnings("unchecked")
        List<Object[]> results = criteria.list();
        Map<String,String> tProcedureFormatMap = Maps.newTreeMap();
        for (Object[] result : results) {
            String procedureIdentifier = (String) result[0];
            String format = (String) result[1];
            tProcedureFormatMap.put(procedureIdentifier, format);
        }
        return tProcedureFormatMap;
    }
}
