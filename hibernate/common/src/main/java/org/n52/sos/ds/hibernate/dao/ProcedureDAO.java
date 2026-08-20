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
import java.util.Map;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.hibernate.Session;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Hibernate data access class for procedure
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ProcedureDAO extends AbstractIdentifierNameDescriptionDAO implements HibernateSqlQueryConstants {

    public ProcedureDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Get ProcedureEntity object for procedure identifier
     *
     * @param identifier
     *            ProcedureEntity identifier
     * @param session
     *            Hibernate session
     * @return ProcedureEntity object
     */
    public ProcedureEntity getProcedureForIdentifier(final String identifier, final Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ProcedureEntity> query = cb.createQuery(ProcedureEntity.class);
        Root<ProcedureEntity> root = query.from(ProcedureEntity.class);
        query.where(hasNonDeletedSeriesPredicate(cb, query, root),
                cb.equal(root.get(ProcedureEntity.IDENTIFIER), identifier));
        ProcedureEntity procedure = session.createQuery(query).uniqueResult();

        if (HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class)) {
            CriteriaQuery<ProcedureEntity> vptQuery = cb.createQuery(ProcedureEntity.class);
            Root<ProcedureEntity> vptRoot = vptQuery.from(ProcedureEntity.class);
            Join<ProcedureEntity, ProcedureHistoryEntity> vpt =
                    vptRoot.join(ProcedureEntity.PROPERTY_VALID_PROCEDURE_TIME);
            vptQuery.where(hasNonDeletedSeriesPredicate(cb, vptQuery, vptRoot),
                    cb.equal(vptRoot.get(ProcedureEntity.IDENTIFIER), identifier),
                    cb.isNull(vpt.get(ProcedureHistoryEntity.END_TIME)));
            ProcedureEntity proc = session.createQuery(vptQuery).uniqueResult();
            if (proc != null) {
                return proc;
            }
        }
        return procedure;
    }

    /**
     * Get procedure for identifier, possible procedureDescriptionFormats and
     * valid time
     *
     * @param identifier
     *            Identifier of the procedure
     * @param possibleProcedureDescriptionFormats
     *            Possible procedureDescriptionFormats
     * @param validTime
     *            Valid time of the procedure
     * @param session
     *            Hibernate Session
     * @return ProcedureEntity entity that match the parameters
     * @throws UnsupportedTimeException
     *             If the time is not supported
     * @throws UnsupportedValueReferenceException
     *             If the valueReference is not supported
     * @throws UnsupportedOperatorException
     *             If the temporal operator is not supported
     */
    public ProcedureEntity getProcedureForIdentifier(String identifier,
            Set<String> possibleProcedureDescriptionFormats, Time validTime, Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ProcedureEntity> query = cb.createQuery(ProcedureEntity.class);
        Root<ProcedureEntity> root = query.from(ProcedureEntity.class);
        Join<ProcedureEntity, ProcedureHistoryEntity> vpt = root.join(ProcedureEntity.PROPERTY_VALID_PROCEDURE_TIME);
        Join<ProcedureHistoryEntity, FormatEntity> pdf = vpt.join(ProcedureHistoryEntity.PROCEDURE_DESCRIPTION_FORMAT);
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(hasNonDeletedSeriesPredicate(cb, query, root));
        predicates.add(cb.equal(root.get(ProcedureEntity.IDENTIFIER), identifier));
        Predicate validTimeCriterion = QueryHelper.getValidTimeCriterion(cb, vpt, validTime);
        if (validTime == null || validTimeCriterion == null) {
            predicates.add(cb.isNull(vpt.get(ProcedureHistoryEntity.END_TIME)));
        } else {
            predicates.add(validTimeCriterion);
        }
        predicates.add(pdf.get(FormatEntity.FORMAT).in(possibleProcedureDescriptionFormats));
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).uniqueResult();
    }

    /**
     * Get ProcedureEntity object for procedure identifier inclusive deleted
     * procedure
     *
     * @param identifier
     *            ProcedureEntity identifier
     * @param session
     *            Hibernate session
     * @return ProcedureEntity object
     */
    public ProcedureEntity getProcedureForIdentifierIncludeDeleted(final String identifier, final Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ProcedureEntity> query = cb.createQuery(ProcedureEntity.class);
        Root<ProcedureEntity> root = query.from(ProcedureEntity.class);
        query.where(cb.equal(root.get(ProcedureEntity.IDENTIFIER), identifier));
        return session.createQuery(query).uniqueResult();
    }

    /**
     * Predicate restricting a {@link ProcedureEntity} query to procedures that have at least one
     * non-deleted dataset/series.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Predicate hasNonDeletedSeriesPredicate(CriteriaBuilder cb, CriteriaQuery<?> query,
            Root<ProcedureEntity> root) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root seriesRoot = subquery.from(getDaoFactory().getSeriesDAO().getSeriesClass());
        subquery.select(seriesRoot.get(DatasetEntity.PROPERTY_PROCEDURE).get(ProcedureEntity.PROPERTY_ID)).distinct(true)
                .where(cb.equal(seriesRoot.get(DatasetEntity.PROPERTY_DELETED), false));
        return cb.in(root.get(ProcedureEntity.PROPERTY_ID)).value(subquery);
    }

    /**
     * Insert and get procedure object
     *
     * @param identifier
     *            Procedure identifier
     * @param procedureDescriptionFormat
     *            Procedure description format object
     * @param procedureDescription
     *            {@link SosProcedureDescription} to insert
     * @param isType
     *            flag if it is a type
     * @param session
     *            Hibernate session
     * @return ProcedureEntity object
     */
    public ProcedureEntity getOrInsertProcedure(String identifier, FormatEntity procedureDescriptionFormat,
            SosProcedureDescription<?> procedureDescription, boolean isType, Session session) {
        ProcedureEntity procedure = getProcedureForIdentifierIncludeDeleted(identifier, session);
        if (procedure == null) {
            procedure = new ProcedureEntity();
            procedure.setFormat(procedureDescriptionFormat);
            procedure.setIdentifier(identifier, getDaoFactory().isStaSupportsUrls());
            AbstractFeature af = procedureDescription.getProcedureDescription();
            if (af.isSetName()) {
                procedure.setName(af.getFirstName().getValue());
            }
            if (af.isSetDescription()) {
                procedure.setDescription(af.getDescription());
            }
            if (procedureDescription.isSetParentProcedure()) {
                ProcedureEntity parent =
                        getProcedureForIdentifier(procedureDescription.getParentProcedure().getHref(), session);
                if (parent != null) {
                    procedure.setParents(Sets.newHashSet(parent));
                }
            }
            if (procedureDescription.getTypeOf() != null && !procedure.isSetTypeOf()) {
                ProcedureEntity typeOfProc =
                        getProcedureForIdentifier(procedureDescription.getTypeOf().getTitle(), session);
                if (typeOfProc != null) {
                    procedure.setTypeOf(typeOfProc);
                }
            }
            procedure.setType(isType);
            procedure.setAggregation(procedureDescription.isAggregation());
            procedure.setReference(procedureDescription.isReference());
        }
        session.saveOrUpdate(procedure);
        session.flush();
        session.refresh(procedure);
        return procedure;
    }

    private record ProcedureFormatRow(String procedure, String format) {
    }

    public Map<String, String> getProcedureFormatMap(Session session) {
        Map<String, String> procedureFormatMap = Maps.newTreeMap();
        if (HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class)) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ProcedureFormatRow> query = cb.createQuery(ProcedureFormatRow.class);
            Root<ProcedureEntity> root = query.from(ProcedureEntity.class);
            Join<ProcedureEntity, ProcedureHistoryEntity> vpt =
                    root.join(ProcedureEntity.PROPERTY_VALID_PROCEDURE_TIME);
            Join<ProcedureHistoryEntity, FormatEntity> pdf =
                    vpt.join(ProcedureHistoryEntity.PROCEDURE_DESCRIPTION_FORMAT);
            query.select(cb.construct(ProcedureFormatRow.class, root.get(ProcedureEntity.IDENTIFIER),
                    pdf.get(FormatEntity.FORMAT)))
                    .where(cb.isNull(vpt.get(ProcedureHistoryEntity.END_TIME)))
                    .orderBy(cb.asc(root.get(ProcedureEntity.IDENTIFIER)));
            for (ProcedureFormatRow row : session.createQuery(query).list()) {
                procedureFormatMap.put(row.procedure(), row.format());
            }
        }
        return procedureFormatMap;
    }
}