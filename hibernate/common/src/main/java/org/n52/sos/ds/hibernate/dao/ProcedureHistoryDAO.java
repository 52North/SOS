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
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Hibernate data access class for valid procedure time
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class ProcedureHistoryDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureHistoryDAO.class);

    private final DaoFactory daoFactory;

    public ProcedureHistoryDAO(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

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
     * @return The inserted {@link ProcedureHistoryEntity}
     */
    public ProcedureHistoryEntity insert(ProcedureEntity procedure,
            FormatEntity procedureDescriptionFormat, String xmlDescription, DateTime validStartTime, Session session) {
        ProcedureHistoryEntity vpd = new ProcedureHistoryEntity();
        vpd.setProcedure(procedure);
        vpd.setFormat(procedureDescriptionFormat);
        vpd.setXml(xmlDescription);
        vpd.setStartTime(validStartTime.toDate());
        session.save(vpd);
        session.flush();
        session.refresh(vpd);
        return vpd;
    }

    /**
     * Update valid procedure time object
     *
     * @param validProcedureTime
     *            Valid procedure time object
     * @param session
     *            Hibernate session
     */
    public void update(ProcedureHistoryEntity validProcedureTime, Session session) {
        session.saveOrUpdate(validProcedureTime);
    }

    public void delete(ProcedureEntity procedure, Session session) {
        StringBuilder builder = new StringBuilder();
        builder.append("delete ");
        builder.append(ProcedureHistoryEntity.class.getSimpleName());
        builder.append(" where ").append(ProcedureHistoryEntity.PROPERTY_PROCEDURE).append(" = :")
                .append(ProcedureHistoryEntity.PROPERTY_PROCEDURE);
        Query<?> q = session.createQuery(builder.toString());
        q.setParameter(ProcedureHistoryEntity.PROPERTY_PROCEDURE, procedure);
        int executeUpdate = q.executeUpdate();
        LOGGER.debug("{} datasets were physically deleted!", executeUpdate);
        session.flush();
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
    public void setEndTime(String procedureIdentifier, Session session) {
        ProcedureEntity procedure =
                new ProcedureDAO(daoFactory).getProcedureForIdentifierIncludeDeleted(procedureIdentifier, session);
        Set<ProcedureHistoryEntity> validProcedureTimes = procedure.getProcedureHistory();
        Date endTime = new DateTime(DateTimeZone.UTC).toDate();
        validProcedureTimes.stream().filter(validProcedureTime -> validProcedureTime.getEndTime() == null)
                .forEach(validProcedureTime -> validProcedureTime.setEndTime(endTime));
    }

    /**
     * Get ValidProcedureTimes for requested parameters
     *
     * @param procedure
     *            Requested Procedure
     * @param possibleProcedureDescriptionFormats
     *            Requested procedureDescriptionFormats
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
    public List<ProcedureHistoryEntity> get(ProcedureEntity procedure,
            Set<String> possibleProcedureDescriptionFormats, Time validTime, Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ProcedureHistoryEntity> query = cb.createQuery(ProcedureHistoryEntity.class);
        Root<ProcedureHistoryEntity> root = query.from(ProcedureHistoryEntity.class);
        Join<ProcedureHistoryEntity, FormatEntity> pdf = root.join(ProcedureHistoryEntity.PROCEDURE_DESCRIPTION_FORMAT);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(ProcedureHistoryEntity.PROCEDURE), procedure));
        predicates.add(pdf.get(FormatEntity.FORMAT).in(possibleProcedureDescriptionFormats));
        Predicate validTimeCriterion = QueryHelper.getValidTimeCriterion(cb, root, validTime);
        // if validTime == null or validTimeCriterion == null, query latest
        // valid procedure description
        if (validTime == null || validTimeCriterion == null) {
            predicates.add(cb.isNull(root.get(ProcedureHistoryEntity.END_TIME)));
        } else {
            predicates.add(validTimeCriterion);
        }
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

}
