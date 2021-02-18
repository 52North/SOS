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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.query.Query;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.NoopTransformerAdapter;
import org.n52.sos.ds.hibernate.util.ProcedureTimeExtrema;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.TimeExtrema;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Hibernate data access class for procedure
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ProcedureDAO extends AbstractIdentifierNameDescriptionDAO implements HibernateSqlQueryConstants {
    // public class ProcedureDAO extends TimeCreator implements
    // HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureDAO.class);

    private static final String SQL_QUERY_GET_PROCEDURES_FOR_ALL_FEATURES_OF_INTEREST =
            "getProceduresForAllFeaturesOfInterest";

    private static final String SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST =
            "getProceduresForAbstractFeatureEntity";

    private static final String SQL_QUERY_GET_PROCEDURE_TIME_EXTREMA = "getProcedureTimeExtrema";

    private static final String SQL_QUERY_GET_ALL_PROCEDURE_TIME_EXTREMA = "getAllProcedureTimeExtrema";

    private static final String SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE = "getMinDate4Procedure";

    private static final String SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE = "getMaxDate4Procedure";

    private static final String QUERY_IDENTIFIER_LOG_TEMPLATE = "QUERY getProcedureForIdentifier(identifier): {}";

    private static final String QUERY_TIME_EXTREMA_LOG_TEMPLATE =
            "QUERY getProcedureTimeExtrema(procedureIdentifier): {}";

    private static final String QUERY_FORMAT_MAP_LOG_TEMPLATE = "QUERY getProcedureFormatMap(): {}";

    private static final String P_PREFIX = "p.";

    private static final String PDF = "pdf";

    private static final String PDF_PREFIX = PDF + ".";

    private final ProcedureTimeTransformer transformer = new ProcedureTimeTransformer();

    public ProcedureDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Get all procedure objects
     *
     * @param session
     *            Hibernate session
     * @return ProcedureEntity objects
     */
    @SuppressWarnings("unchecked")
    public List<ProcedureEntity> getProcedureObjects(final Session session) {
        Criteria criteria = getDefaultCriteria(session);
        LOGGER.trace("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get map keyed by undeleted procedure identifiers with collections of
     * parent procedures (if supported) as values
     *
     * @param session
     *            the session
     * @return Map keyed by procedure identifier with values of parent procedure
     *         identifier collections
     */
    public Map<String, Collection<String>> getProcedureIdentifiers(final Session session) {
        Criteria criteria = getDefaultCriteria(session);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property(ProcedureEntity.IDENTIFIER));
        criteria.createAlias(ProcedureEntity.PROPERTY_PARENTS, "pp", JoinType.LEFT_OUTER_JOIN);
        projectionList.add(Projections.property("pp." + ProcedureEntity.IDENTIFIER));
        criteria.setProjection(projectionList);
        // return as List<Object[]> even if there's only one column for
        // consistency
        criteria.setResultTransformer(NoopTransformerAdapter.INSTANCE);

        LOGGER.trace("QUERY getProcedureIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        @SuppressWarnings("unchecked")
        List<Object[]> results = criteria.list();
        Map<String, Collection<String>> map = Maps.newHashMap();
        for (Object[] result : results) {
            String procedureIdentifier = (String) result[0];
            String parentProcedureIdentifier = null;
            parentProcedureIdentifier = (String) result[1];
            if (parentProcedureIdentifier == null) {
                map.put(procedureIdentifier, null);
            } else {
                CollectionHelper.addToCollectionMap(procedureIdentifier, parentProcedureIdentifier, map);
            }
        }
        return map;
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
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.eq(ProcedureEntity.IDENTIFIER, identifier));
        LOGGER.trace(QUERY_IDENTIFIER_LOG_TEMPLATE, HibernateHelper.getSqlString(criteria));
        ProcedureEntity procedure = (ProcedureEntity) criteria.uniqueResult();
        if (HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class)) {
            criteria.createCriteria(ProcedureEntity.PROPERTY_VALID_PROCEDURE_TIME)
                    .add(Restrictions.isNull(ProcedureHistoryEntity.END_TIME));
            LOGGER.trace(QUERY_IDENTIFIER_LOG_TEMPLATE, HibernateHelper.getSqlString(criteria));
            ProcedureEntity proc = (ProcedureEntity) criteria.uniqueResult();
            if (proc != null) {
                return proc;
            }
        }
        return procedure;

    }
    //
    // private ProcedureEntity
    // getProcedureWithLatestValidProcedureDescription(String
    // identifier, Session session) {
    // Criteria criteria = getDefaultCriteria(session);
    // criteria.add(Restrictions.eq(ProcedureEntity.IDENTIFIER, identifier));
    // criteria.createCriteria(TProcedureEntity.VALID_PROCEDURE_TIME).add(
    // Restrictions.isNull(ValidProcedureTime.END_TIME));
    // LOGGER.trace(QUERY_IDENTIFIER_LOG_TEMPLATE,
    // HibernateHelper.getSqlString(criteria));
    // return (ProcedureEntity) criteria.uniqueResult();
    // }

    /**
     * Get ProcedureEntity object for procedure identifier
     *
     * @param identifier
     *            ProcedureEntity identifier
     * @param session
     *            Hibernate session
     * @return ProcedureEntity object
     */
    public ProcedureEntity getProcedureForIdentifier(final String identifier, Time time, final Session session) {
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.eq(ProcedureEntity.IDENTIFIER, identifier));
        LOGGER.trace(QUERY_IDENTIFIER_LOG_TEMPLATE, HibernateHelper.getSqlString(criteria));
        return (ProcedureEntity) criteria.uniqueResult();
    }

    /**
     * Get transactional procedure object for procedure identifier and
     * procedureDescriptionFormat
     *
     * @param identifier
     *            ProcedureEntity identifier
     * @param procedureDescriptionFormat
     *            ProcedureDescriptionFormat identifier
     * @param session
     *            Hibernate session
     * @return Transactional procedure object
     * @throws UnsupportedOperatorException
     *             If an error occurs
     * @throws UnsupportedValueReferenceException
     *             If an error occurs
     * @throws UnsupportedTimeException
     *             If an error occurs
     */
    public ProcedureEntity getProcedureForIdentifier(final String identifier, String procedureDescriptionFormat,
            Time validTime, final Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.eq(ProcedureEntity.IDENTIFIER, identifier));
        Criteria createValidProcedureTime = criteria.createCriteria(ProcedureEntity.PROPERTY_VALID_PROCEDURE_TIME);
        Criterion validTimeCriterion = QueryHelper.getValidTimeCriterion(validTime);
        if (validTime == null || validTimeCriterion == null) {
            createValidProcedureTime.add(Restrictions.isNull(ProcedureHistoryEntity.END_TIME));
        } else {
            createValidProcedureTime.add(validTimeCriterion);
        }
        createValidProcedureTime.createCriteria(ProcedureHistoryEntity.PROCEDURE_DESCRIPTION_FORMAT)
                .add(Restrictions.eq(FormatEntity.FORMAT, procedureDescriptionFormat));
        LOGGER.trace(QUERY_IDENTIFIER_LOG_TEMPLATE, HibernateHelper.getSqlString(criteria));
        return (ProcedureEntity) criteria.uniqueResult();
    }

    /**
     * Get transactional procedure object for procedure identifier and
     * procedureDescriptionFormats
     *
     * @param identifier
     *            ProcedureEntity identifier
     * @param procedureDescriptionFormats
     *            ProcedureDescriptionFormat identifiers
     * @param session
     *            Hibernate session
     * @return Transactional procedure object
     */
    public ProcedureEntity getProcedureForIdentifier(final String identifier, Set<String> procedureDescriptionFormats,
            final Session session) {
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.eq(ProcedureEntity.IDENTIFIER, identifier));
        criteria.createCriteria(ProcedureEntity.PROPERTY_VALID_PROCEDURE_TIME).add(
                Restrictions.in(ProcedureHistoryEntity.PROCEDURE_DESCRIPTION_FORMAT, procedureDescriptionFormats));
        LOGGER.trace(QUERY_IDENTIFIER_LOG_TEMPLATE, HibernateHelper.getSqlString(criteria));
        return (ProcedureEntity) criteria.uniqueResult();
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
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.eq(ProcedureEntity.IDENTIFIER, identifier));
        Criteria createValidProcedureTime = criteria.createCriteria(ProcedureEntity.PROPERTY_VALID_PROCEDURE_TIME);
        Criterion validTimeCriterion = QueryHelper.getValidTimeCriterion(validTime);
        if (validTime == null || validTimeCriterion == null) {
            createValidProcedureTime.add(Restrictions.isNull(ProcedureHistoryEntity.END_TIME));
        } else {
            createValidProcedureTime.add(validTimeCriterion);
        }
        createValidProcedureTime.createCriteria(ProcedureHistoryEntity.PROCEDURE_DESCRIPTION_FORMAT)
                .add(Restrictions.in(FormatEntity.FORMAT, possibleProcedureDescriptionFormats));
        LOGGER.trace("QUERY getProcedureForIdentifier(identifier, possibleProcedureDescriptionFormats, validTime): {}",
                HibernateHelper.getSqlString(criteria));
        return (ProcedureEntity) criteria.uniqueResult();
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
        Criteria criteria = session.createCriteria(ProcedureEntity.class)
                .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, identifier));
        LOGGER.trace("QUERY getProcedureForIdentifierIncludeDeleted(identifier): {}",
                HibernateHelper.getSqlString(criteria));
        return (ProcedureEntity) criteria.uniqueResult();
    }

    /**
     * Get ProcedureEntity objects for procedure identifiers
     *
     * @param identifiers
     *            ProcedureEntity identifiers
     * @param session
     *            Hibernate session
     * @return ProcedureEntity objects
     */
    @SuppressWarnings("unchecked")
    public List<ProcedureEntity> getProceduresForIdentifiers(final Collection<String> identifiers,
            final Session session) {
        if (identifiers == null || identifiers.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.in(ProcedureEntity.IDENTIFIER, identifiers));
        LOGGER.trace("QUERY getProceduresForIdentifiers(identifiers): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get procedure identifiers for all FOIs
     *
     * @param session
     *            Hibernate session
     *
     * @return Map of foi identifier to procedure identifier collection
     * @throws HibernateException
     *             If an error occurs
     */
    public Map<String, Collection<String>> getProceduresForAllFeaturesOfInterest(final Session session) {
        List<Object[]> results = getFeatureProcedureResult(session);
        Map<String, Collection<String>> foiProcMap = Maps.newHashMap();
        if (CollectionHelper.isNotEmpty(results)) {
            for (Object[] result : results) {
                String foi = (String) result[0];
                String proc = (String) result[1];
                Collection<String> foiProcs = foiProcMap.get(foi);
                if (foiProcs == null) {
                    foiProcs = Lists.newArrayList();
                    foiProcMap.put(foi, foiProcs);
                }
                foiProcs.add(proc);
            }
        }
        return foiProcMap;
    }

    /**
     * Get FOIs for all procedure identifiers
     *
     * @param session
     *            Hibernate session
     *
     * @return Map of procedure identifier to foi identifier collection
     */
    public Map<String, Collection<String>> getFeaturesOfInterestsForAllProcedures(final Session session) {
        List<Object[]> results = getFeatureProcedureResult(session);
        Map<String, Collection<String>> foiProcMap = Maps.newHashMap();
        if (CollectionHelper.isNotEmpty(results)) {
            for (Object[] result : results) {
                String foi = (String) result[0];
                String proc = (String) result[1];
                Collection<String> procFois = foiProcMap.get(proc);
                if (procFois == null) {
                    procFois = Lists.newArrayList();
                    foiProcMap.put(proc, procFois);
                }
                procFois.add(foi);
            }
        }
        return foiProcMap;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getFeatureProcedureResult(Session session) {
        List<Object[]> results;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_PROCEDURES_FOR_ALL_FEATURES_OF_INTEREST, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_PROCEDURES_FOR_ALL_FEATURES_OF_INTEREST);
            LOGGER.trace("QUERY getProceduresForAllFeaturesOfInterest(feature) with NamedQuery: {}",
                    SQL_QUERY_GET_PROCEDURES_FOR_ALL_FEATURES_OF_INTEREST);
            results = namedQuery.list();
        } else {
            Criteria c = null;
            c = session.createCriteria(getDaoFactory().getSeriesDAO().getSeriesClass())
                    .createAlias(DatasetEntity.PROPERTY_FEATURE, "f")
                    .createAlias(DatasetEntity.PROPERTY_PROCEDURE, "p")
                    .add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false))
                    .setProjection(Projections.distinct(Projections.projectionList()
                            .add(Projections.property("f." + AbstractFeatureEntity.IDENTIFIER))
                            .add(Projections.property(P_PREFIX + ProcedureEntity.IDENTIFIER))));
            LOGGER.trace("QUERY getProceduresForAllFeaturesOfInterest(feature): {}", HibernateHelper.getSqlString(c));
            results = c.list();
        }
        return results;
    }

    /**
     * Get procedure identifiers for FOI
     *
     * @param session
     *            Hibernate session
     * @param feature
     *            FOI object
     *
     * @return Related procedure identifiers
     * @throws CodedException
     *             If an error occurs
     */
    @SuppressWarnings("unchecked")
    public List<String> getProceduresForAbstractFeatureEntity(final Session session,
            final AbstractFeatureEntity feature) throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST);
            namedQuery.setParameter(FEATURE, feature.getIdentifier());
            LOGGER.trace("QUERY getProceduresForAbstractFeatureEntity(feature) with NamedQuery: {}",
                    SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST);
            return namedQuery.list();
        } else {
            Criteria c = null;
            c = getDefaultCriteria(session);
            c.add(Subqueries.propertyIn(ProcedureEntity.PROPERTY_ID,
                    getDetachedCriteriaProceduresForAbstractFeatureEntityFromSeries(feature, session)));
            c.setProjection(Projections.distinct(Projections.property(ProcedureEntity.IDENTIFIER)));
            LOGGER.trace("QUERY getProceduresForAbstractFeatureEntity(feature): {}", HibernateHelper.getSqlString(c));
            return c.list();
        }
    }

    /**
     * Get procedure identifiers for offering identifier
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return ProcedureEntity identifiers
     * @throws CodedException
     *             If an error occurs
     */
    @SuppressWarnings("unchecked")
    public List<String> getProcedureIdentifiersForOffering(final String offeringIdentifier, final Session session)
            throws OwsExceptionReport {
        Criteria c = getDefaultCriteria(session);
        c.add(Subqueries.propertyIn(ProcedureEntity.PROPERTY_ID,
                getDetachedCriteriaProceduresForOfferingFromObservationConstellation(offeringIdentifier, session)));
        c.setProjection(Projections.distinct(Projections.property(ProcedureEntity.IDENTIFIER)));
        LOGGER.trace("QUERY getProcedureIdentifiersForOffering(offeringIdentifier): {}",
                HibernateHelper.getSqlString(c));
        return c.list();
    }

    private Criteria getDefaultCriteria(Session session) {
        Criteria c = session.createCriteria(ProcedureEntity.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        try {
            c.add(Subqueries.propertyIn(ProcedureEntity.PROPERTY_ID,
                    getDetachedCriteriaProceduresForFromSeries(session)));
        } catch (OwsExceptionReport e) {
            LOGGER.error("Error while creating defaut criteria!");
        }
        return c;
    }

    private Criteria getDefaultProcedureCriteriaIncludeDeleted(Session session) {
        return session.createCriteria(ProcedureEntity.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Get procedure identifiers for observable property identifier
     *
     * @param observablePropertyIdentifier
     *            Observable property identifier
     * @param session
     *            Hibernate session
     * @return ProcedureEntity identifiers
     * @throws CodedException
     *             If an error occurs
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getProcedureIdentifiersForObservableProperty(final String observablePropertyIdentifier,
            final Session session) throws OwsExceptionReport {
        Criteria c = getDefaultCriteria(session);
        c.setProjection(Projections.distinct(Projections.property(ProcedureEntity.IDENTIFIER)));
        c.add(Subqueries.propertyIn(ProcedureEntity.PROPERTY_ID,
                getDetachedCriteriaProceduresForObservablePropertyFromObservationConstellation(
                        observablePropertyIdentifier, session)));
        LOGGER.trace("QUERY getProcedureIdentifiersForObservableProperty(observablePropertyIdentifier): {}",
                HibernateHelper.getSqlString(c));
        return c.list();
    }

    public boolean isProcedureTimeExtremaNamedQuerySupported(Session session) {
        return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_PROCEDURE_TIME_EXTREMA, session);
    }

    public TimeExtrema getProcedureTimeExtremaFromNamedQuery(Session session, String procedureIdentifier) {
        Object[] result = null;
        if (isProcedureTimeExtremaNamedQuerySupported(session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_PROCEDURE_TIME_EXTREMA);
            namedQuery.setParameter(PROCEDURE, procedureIdentifier);
            LOGGER.trace("QUERY getProcedureTimeExtrema({}) with NamedQuery '{}': {}", procedureIdentifier,
                    SQL_QUERY_GET_PROCEDURE_TIME_EXTREMA, namedQuery.getQueryString());
            result = (Object[]) namedQuery.uniqueResult();
        }
        return parseProcedureTimeExtremaResult(result);
    }

    public boolean isAllProcedureTimeExtremaNamedQuerySupported(Session session) {
        return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_ALL_PROCEDURE_TIME_EXTREMA, session);
    }

    private TimeExtrema parseProcedureTimeExtremaResult(Object[] result) {
        TimeExtrema pte = new TimeExtrema();
        if (result != null) {
            pte.setMinPhenomenonTime(DateTimeHelper.makeDateTime(result[1]));
            DateTime maxPhenStart = DateTimeHelper.makeDateTime(result[2]);
            DateTime maxPhenEnd = DateTimeHelper.makeDateTime(result[3]);
            pte.setMaxPhenomenonTime(DateTimeHelper.max(maxPhenStart, maxPhenEnd));
        }
        return pte;
    }

    /**
     * Query procedure time extrema for the provided procedure identifier
     *
     * @param session
     *            the session
     * @param procedureIdentifier
     *            procedure identifier
     * @return ProcedureTimeExtrema
     * @throws CodedException
     *             If an error occurs
     */
    public TimeExtrema getProcedureTimeExtrema(final Session session, String procedureIdentifier)
            throws OwsExceptionReport {
        Object[] result;
        if (isProcedureTimeExtremaNamedQuerySupported(session)) {
            return getProcedureTimeExtremaFromNamedQuery(session, procedureIdentifier);
        }
        AbstractObservationDAO observationDAO = getDaoFactory().getObservationDAO();
        Criteria criteria = observationDAO.getDefaultObservationInfoCriteria(session);
        criteria.createAlias(DataEntity.PROPERTY_DATASET, "s");
        criteria.createAlias("s." + DatasetEntity.PROPERTY_PROCEDURE, "p");
        criteria.add(Restrictions.eq(P_PREFIX + ProcedureEntity.IDENTIFIER, procedureIdentifier));
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty(P_PREFIX + ProcedureEntity.IDENTIFIER));
        projectionList.add(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START));
        projectionList.add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_START));
        projectionList.add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END));
        criteria.setProjection(projectionList);

        LOGGER.trace(QUERY_TIME_EXTREMA_LOG_TEMPLATE, HibernateHelper.getSqlString(criteria));
        result = (Object[]) criteria.uniqueResult();

        return parseProcedureTimeExtremaResult(result);
    }

    @SuppressWarnings("unchecked")
    public Map<String, TimeExtrema> getProcedureTimeExtrema(Session session) throws OwsExceptionReport {
        List<ProcedureTimeExtrema> results = null;
        if (isAllProcedureTimeExtremaNamedQuerySupported(session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_ALL_PROCEDURE_TIME_EXTREMA);
            LOGGER.trace("QUERY getProcedureTimeExtrema() with NamedQuery '{}': {}",
                    SQL_QUERY_GET_ALL_PROCEDURE_TIME_EXTREMA, namedQuery.getQueryString());
            namedQuery.setResultTransformer(transformer);
            results = namedQuery.list();
        } else {
            AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
            if (seriesDAO != null) {
                Criteria c = seriesDAO.getDefaultSeriesCriteria(session);
                c.createAlias(DatasetEntity.PROPERTY_PROCEDURE, "p");
                c.setProjection(Projections.projectionList()
                        .add(Projections.groupProperty(P_PREFIX + ProcedureEntity.IDENTIFIER))
                        .add(Projections.min(DatasetEntity.PROPERTY_FIRST_VALUE_AT))
                        .add(Projections.max(DatasetEntity.PROPERTY_LAST_VALUE_AT)));
                LOGGER.trace(QUERY_TIME_EXTREMA_LOG_TEMPLATE, HibernateHelper.getSqlString(c));
                c.setResultTransformer(transformer);
                results = c.list();
            }
            if (checkHasNoProcedureTimeResult(results)) {
                AbstractObservationDAO observationDAO = getDaoFactory().getObservationDAO();
                Criteria criteria = observationDAO.getDefaultObservationTimeCriteria(session);
                String alias = observationDAO.addProcedureAlias(criteria);
                criteria.setProjection(
                        Projections.projectionList().add(Projections.groupProperty(alias + ProcedureEntity.IDENTIFIER))
                                .add(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START))
                                .add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_START))
                                .add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_START)));

                LOGGER.trace(QUERY_TIME_EXTREMA_LOG_TEMPLATE, HibernateHelper.getSqlString(criteria));
                criteria.setResultTransformer(transformer);
                results = criteria.list();
            }
        }
        Map<String, TimeExtrema> procedureTimeExtrema = Maps.newHashMap();
        if (results != null && !results.isEmpty()) {
            for (ProcedureTimeExtrema pte : results) {
                if (pte != null && pte.isSetProcedure()) {
                    procedureTimeExtrema.put(pte.getProcedure(), pte);
                }
            }
        }
        return procedureTimeExtrema;
    }

    private boolean checkHasNoProcedureTimeResult(List<ProcedureTimeExtrema> results) {
        if (CollectionHelper.isNotEmpty(results)) {
            int noTimeCount = 0;
            for (ProcedureTimeExtrema procedureTimeExtrema : results) {
                if (!procedureTimeExtrema.isSetPhenomenonTimes()) {
                    noTimeCount++;
                }
            }
            return results.size() > 0 && noTimeCount == results.size();
        }
        return true;
    }

    /**
     * Get min time from observations for procedure
     *
     * @param procedure
     *            ProcedureEntity identifier
     * @param session
     *            Hibernate session
     * @return min time for procedure
     * @throws CodedException
     *             If an error occurs
     */
    public DateTime getMinDate4Procedure(final String procedure, final Session session) throws OwsExceptionReport {
        Object min = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE);
            namedQuery.setParameter(PROCEDURE, procedure);
            LOGGER.trace("QUERY getMinDate4Procedure(procedure) with NamedQuery: {}",
                    SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE);
            min = namedQuery.uniqueResult();
        } else {
            AbstractObservationDAO observationDAO = getDaoFactory().getObservationDAO();
            Criteria criteria = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                addProcedureRestrictionForSeries(criteria, procedure);
            } else {
                addProcedureRestrictionForObservation(criteria, procedure);
            }
            addMinMaxProjection(criteria, MinMax.MIN, DataEntity.PROPERTY_SAMPLING_TIME_START);
            LOGGER.trace("QUERY getMinDate4Procedure(procedure): {}", HibernateHelper.getSqlString(criteria));
            min = criteria.uniqueResult();
        }
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Get max time from observations for procedure
     *
     * @param procedure
     *            ProcedureEntity identifier
     * @param session
     *            Hibernate session
     * @return max time for procedure
     * @throws CodedException
     *             If an error occurs
     */
    public DateTime getMaxDate4Procedure(final String procedure, final Session session) throws OwsExceptionReport {
        Object maxStart = null;
        Object maxEnd = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE);
            namedQuery.setParameter(PROCEDURE, procedure);
            LOGGER.trace("QUERY getMaxDate4Procedure(procedure) with NamedQuery: {}",
                    SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE);
            maxStart = namedQuery.uniqueResult();
            maxEnd = maxStart;
        } else {
            AbstractObservationDAO observationDAO = getDaoFactory().getObservationDAO();
            Criteria cstart = observationDAO.getDefaultObservationInfoCriteria(session);
            Criteria cend = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                addProcedureRestrictionForSeries(cstart, procedure);
                addProcedureRestrictionForSeries(cend, procedure);
            } else {
                addProcedureRestrictionForObservation(cstart, procedure);
                addProcedureRestrictionForObservation(cend, procedure);
            }
            addMinMaxProjection(cstart, MinMax.MAX, DataEntity.PROPERTY_SAMPLING_TIME_START);
            addMinMaxProjection(cend, MinMax.MAX, DataEntity.PROPERTY_SAMPLING_TIME_END);
            LOGGER.trace("QUERY getMaxDate4Procedure(procedure) start: {}", HibernateHelper.getSqlString(cstart));
            LOGGER.trace("QUERY getMaxDate4Procedure(procedure) end: {}", HibernateHelper.getSqlString(cend));
            if (HibernateHelper.getSqlString(cstart).endsWith(HibernateHelper.getSqlString(cend))) {
                maxStart = cstart.uniqueResult();
                maxEnd = maxStart;
                LOGGER.trace("Max time start and end query are identically, only one query is executed!");
            } else {
                maxStart = cstart.uniqueResult();
                maxEnd = cend.uniqueResult();
            }
        }
        if (maxStart == null && maxEnd == null) {
            return null;
        } else {
            final DateTime start = new DateTime(maxStart, DateTimeZone.UTC);
            if (maxEnd != null) {
                final DateTime end = new DateTime(maxEnd, DateTimeZone.UTC);
                if (end.isAfter(start)) {
                    return end;
                }
            }
            return start;
        }
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

    private DetachedCriteria getDetachedCriteriaProceduresForFromSeries(Session session) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria =
                DetachedCriteria.forClass(getDaoFactory().getSeriesDAO().getSeriesClass());
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_PROCEDURE)));
        return detachedCriteria;
    }

    /**
     * Get Hibernate Detached Criteria for class Series and featureOfInterest
     * identifier
     *
     * @param featureOfInterest
     *            AbstractFeatureEntity identifier parameter
     * @param session
     *            Hibernate session
     * @return Hiberante Detached Criteria with ProcedureEntity entities
     * @throws CodedException
     *             If an error occurs
     */
    private DetachedCriteria getDetachedCriteriaProceduresForAbstractFeatureEntityFromSeries(
            AbstractFeatureEntity featureOfInterest, Session session) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria =
                DetachedCriteria.forClass(getDaoFactory().getSeriesDAO().getSeriesClass());
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_FEATURE, featureOfInterest));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_PROCEDURE)));
        return detachedCriteria;
    }

    /**
     * Get Hibernate Detached Criteria for class ObservationConstellation and
     * observableProperty identifier
     *
     * @param observablePropertyIdentifier
     *            ObservableProperty identifier parameter
     * @param session
     *            Hibernate session
     * @return Hiberante Detached Criteria with ProcedureEntity entities
     */
    private DetachedCriteria getDetachedCriteriaProceduresForObservablePropertyFromObservationConstellation(
            String observablePropertyIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DatasetEntity.class);
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.createCriteria(DatasetEntity.PROPERTY_PHENOMENON)
                .add(Restrictions.eq(PhenomenonEntity.IDENTIFIER, observablePropertyIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_PROCEDURE)));
        return detachedCriteria;
    }

    /**
     * Get Hibernate Detached Criteria for class Series and observableProperty
     * identifier
     *
     * @param observablePropertyIdentifier
     *            ObservableProperty identifier parameter
     * @param session
     *            Hibernate session
     * @return Hiberante Detached Criteria with ProcedureEntity entities
     * @throws CodedException
     *             If an error occurs
     */
    private DetachedCriteria getDetachedCriteriaProceduresForObservablePropertyFromSeries(
            String observablePropertyIdentifier, Session session) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria =
                DetachedCriteria.forClass(getDaoFactory().getSeriesDAO().getSeriesClass());

        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.createCriteria(DatasetEntity.PROPERTY_PHENOMENON)
                .add(Restrictions.eq(PhenomenonEntity.IDENTIFIER, observablePropertyIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_PROCEDURE)));
        return detachedCriteria;
    }

    /**
     * Get Hibernate Detached Criteria for class ObservationConstellation and
     * offering identifier
     *
     * @param offeringIdentifier
     *            Offering identifier parameter
     * @param session
     *            Hibernate session
     * @return Detached Criteria with ProcedureEntity entities
     */
    private DetachedCriteria getDetachedCriteriaProceduresForOfferingFromObservationConstellation(
            String offeringIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DatasetEntity.class);
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.createCriteria(DatasetEntity.PROPERTY_OFFERING)
                .add(Restrictions.eq(OfferingEntity.IDENTIFIER, offeringIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_PROCEDURE)));
        return detachedCriteria;
    }

    /**
     * Add procedure identifier restriction to Hibernate Criteria for series
     *
     * @param criteria
     *            Hibernate Criteria for series to add restriction
     * @param procedure
     *            ProcedureEntity identifier
     */
    private void addProcedureRestrictionForSeries(Criteria criteria, String procedure) {
        Criteria seriesCriteria = criteria.createCriteria(DataEntity.PROPERTY_DATASET);
        seriesCriteria.createCriteria(DatasetEntity.PROPERTY_PROCEDURE)
                .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, procedure));
    }

    /**
     * Add procedure identifier restriction to Hibernate Criteria
     *
     * @param criteria
     *            Hibernate Criteria to add restriction
     * @param procedure
     *            ProcedureEntity identifier
     */
    private void addProcedureRestrictionForObservation(Criteria criteria, String procedure) {
        criteria.createCriteria(DataEntity.PROPERTY_DATASET).createCriteria(DatasetEntity.PROPERTY_PROCEDURE)
                .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, procedure));
    }

    @SuppressWarnings("unchecked")
    protected Set<String> getObservationIdentifiers(Session session, String procedureIdentifier) {
        Criteria criteria = session.createCriteria(DataEntity.class)
                .setProjection(Projections.distinct(Projections.property(DataEntity.IDENTIFIER)))
                .add(Restrictions.isNotNull(DataEntity.IDENTIFIER))
                .add(Restrictions.eq(DataEntity.PROPERTY_DELETED, false));
        Criteria seriesCriteria = criteria.createCriteria(DataEntity.PROPERTY_DATASET);
        seriesCriteria.createCriteria(DatasetEntity.PROPERTY_PROCEDURE)
                .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, procedureIdentifier));
        LOGGER.trace("QUERY getObservationIdentifiers(procedureIdentifier): {}",
                HibernateHelper.getSqlString(criteria));
        return Sets.newHashSet(criteria.list());
    }

    public Map<String, String> getProcedureFormatMap(Session session) {
        Map<String, String> procedureFormatMap = Maps.newTreeMap();
        if (HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class)) {
            Criteria criteria = session.createCriteria(ProcedureEntity.class);
            criteria.createAlias(ProcedureEntity.PROPERTY_VALID_PROCEDURE_TIME, "vpt");
            criteria.createAlias(ProcedureHistoryEntity.PROCEDURE_DESCRIPTION_FORMAT, PDF);
            criteria.add(Restrictions.isNull("vpt." + ProcedureHistoryEntity.END_TIME));
            criteria.setProjection(Projections.projectionList().add(Projections.property(ProcedureEntity.IDENTIFIER))
                    .add(Projections.property(PDF_PREFIX + FormatEntity.FORMAT)));
            criteria.addOrder(Order.asc(ProcedureEntity.IDENTIFIER));
            LOGGER.trace(QUERY_FORMAT_MAP_LOG_TEMPLATE, HibernateHelper.getSqlString(criteria));
            @SuppressWarnings("unchecked")
            List<Object[]> results = criteria.list();

            for (Object[] result : results) {
                String procedureIdentifier = (String) result[0];
                String format = (String) result[1];
                procedureFormatMap.put(procedureIdentifier, format);
            }
        }
        return procedureFormatMap;
    }

    @SuppressWarnings("unchecked")
    public List<ProcedureEntity> getPublishedProcedure(Session session) throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(DatasetEntity.class)) {
            Criteria c = getDefaultCriteria(session);
            c.add(Subqueries.propertyNotIn(ProcedureEntity.PROPERTY_ID, getDetachedCriteriaSeries(session)));
            return c.list();
        }
        return getProcedureObjects(session);
    }

    private DetachedCriteria getDetachedCriteriaSeries(Session session) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria =
                DetachedCriteria.forClass(getDaoFactory().getSeriesDAO().getSeriesClass());
        detachedCriteria.add(Restrictions.disjunction(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, true),
                Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, false)));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_PROCEDURE)));
        return detachedCriteria;
    }

    public ProcedureEntity updateProcedure(ProcedureEntity procedure, SosProcedureDescription procedureDescription,
            Session session) {
        AbstractFeature af = procedureDescription.getProcedureDescription();
        if (af.isSetName()) {
            if (!procedure.isSetName()
                    || (procedure.isSetName() && !checkForName(af.getName(), procedure.getName()))) {
                procedure.setName(af.getFirstName().getValue());
            }
            if (af.isSetDescription() && !af.getDescription().equals(procedure.getDescription())) {
                procedure.setDescription(af.getDescription());
            }
        }
        session.saveOrUpdate(procedure);
        session.flush();
        session.refresh(procedure);
        return procedure;
    }

    private boolean checkForName(List<CodeType> names, String name) {
        return names.stream().filter(n -> n.getValue().equals(name)).findFirst().isPresent();
    }

    /**
     * ProcedureEntity time extrema {@link ResultTransformer}
     *
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.4.0
     *
     */
    private static class ProcedureTimeTransformer implements ResultTransformer {
        private static final long serialVersionUID = -373512929481519459L;

        @Override
        public ProcedureTimeExtrema transformTuple(Object[] tuple, String[] aliases) {
            ProcedureTimeExtrema procedureTimeExtrema = new ProcedureTimeExtrema();
            if (tuple != null) {
                procedureTimeExtrema.setProcedure(tuple[0].toString());
                procedureTimeExtrema.setMinPhenomenonTime(DateTimeHelper.makeDateTime(tuple[1]));
                if (tuple.length == 4) {
                    DateTime maxPhenStart = DateTimeHelper.makeDateTime(tuple[2]);
                    DateTime maxPhenEnd = DateTimeHelper.makeDateTime(tuple[3]);
                    procedureTimeExtrema.setMaxPhenomenonTime(DateTimeHelper.max(maxPhenStart, maxPhenEnd));
                } else {
                    procedureTimeExtrema.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(tuple[2]));
                }
            }
            return procedureTimeExtrema;
        }

        @Override
        @SuppressWarnings({ "rawtypes" })
        public List transformList(List collection) {
            return collection;
        }
    }

}
