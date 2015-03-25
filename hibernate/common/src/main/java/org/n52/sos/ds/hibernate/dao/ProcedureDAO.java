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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.n52.sos.ds.hibernate.dao.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ProcedureDescriptionFormat;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationInfo;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.NoopTransformerAdapter;
import org.n52.sos.ds.hibernate.util.TimeExtrema;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;

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
    //public class ProcedureDAO extends TimeCreator implements HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureDAO.class);

    private static final String SQL_QUERY_GET_PROCEDURES_FOR_ALL_FEATURES_OF_INTEREST = "getProceduresForAllFeaturesOfInterest";

    private static final String SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST = "getProceduresForFeatureOfInterest";

    private static final String SQL_QUERY_GET_PROCEDURE_TIME_EXTREMA = "getProcedureTimeExtrema";

    private static final String SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE = "getMinDate4Procedure";

    private static final String SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE = "getMaxDate4Procedure";

    /**
     * Get all procedure objects
     *
     * @param session
     *            Hibernate session
     * @return Procedure objects
     */
    @SuppressWarnings("unchecked")
    public List<Procedure> getProcedureObjects(final Session session) {
        Criteria criteria = session.createCriteria(Procedure.class);
        LOGGER.debug("QUERY getProcedureObjects(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get map keyed by undeleted procedure identifiers with
     * collections of parent procedures (if supported) as values
     * @param session
     * @return Map keyed by procedure identifier with values of parent procedure identifier collections
     */
    public Map<String,Collection<String>> getProcedureIdentifiers(final Session session) {
        boolean tProcedureSupported = HibernateHelper.isEntitySupported(TProcedure.class);
        Criteria criteria = session.createCriteria(Procedure.class)
                .add(Restrictions.eq(Procedure.DELETED, false));
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property(Procedure.IDENTIFIER));
        if (tProcedureSupported) {
            criteria.createAlias(TProcedure.PARENTS, "pp", JoinType.LEFT_OUTER_JOIN);
            projectionList.add(Projections.property("pp." + Procedure.IDENTIFIER));
        }
        criteria.setProjection(projectionList);
        //return as List<Object[]> even if there's only one column for consistency
        criteria.setResultTransformer(NoopTransformerAdapter.INSTANCE);

        LOGGER.debug("QUERY getProcedureIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        @SuppressWarnings("unchecked")
        List<Object[]> results = criteria.list();
        Map<String,Collection<String>> map = Maps.newHashMap();
        for (Object[] result : results) {
            String procedureIdentifier = (String) result[0];
            String parentProcedureIdentifier = null;
            if (tProcedureSupported) {
                parentProcedureIdentifier = (String) result[1];
            }
            if (parentProcedureIdentifier == null) {
                map.put(procedureIdentifier, null);
            } else {
                CollectionHelper.addToCollectionMap(procedureIdentifier, parentProcedureIdentifier, map);
            }
        }
        return map;
    }

    /**
     * Get Procedure object for procedure identifier
     *
     * @param identifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Procedure object
     */
    public Procedure getProcedureForIdentifier(final String identifier, final Session session) {
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        Procedure procedure = (Procedure)criteria.uniqueResult();
        if (procedure instanceof TProcedure && HibernateHelper.isEntitySupported(TProcedure.class)) {
            criteria.createCriteria(TProcedure.VALID_PROCEDURE_TIME).add(Restrictions.isNull(ValidProcedureTime.END_TIME));
            LOGGER.debug("QUERY getProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
            Procedure proc = (Procedure)criteria.uniqueResult();
            if (proc != null) {
                return proc;
            }
        }
        return procedure;

    }
//
//    private Procedure getProcedureWithLatestValidProcedureDescription(String identifier, Session session) {
//        Criteria criteria = getDefaultCriteria(session);
//        criteria.add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
//        criteria.createCriteria(TProcedure.VALID_PROCEDURE_TIME).add(Restrictions.isNull(ValidProcedureTime.END_TIME));
//        LOGGER.debug("QUERY getProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
//        return (Procedure) criteria.uniqueResult();
//    }

    /**
     * Get Procedure object for procedure identifier inclusive deleted procedure
     *
     * @param identifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Procedure object
     */
    public Procedure getProcedureForIdentifierIncludeDeleted(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(Procedure.class).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getProcedureForIdentifierIncludeDeleted(identifier): {}",
                HibernateHelper.getSqlString(criteria));
        return (Procedure) criteria.uniqueResult();
    }

    /**
     * Get Procedure object for procedure identifier
     *
     * @param identifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Procedure object
     */
    public Procedure getProcedureForIdentifier(final String identifier, Time time, final Session session) {
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (Procedure) criteria.uniqueResult();
    }

    /**
     * Get Procedure objects for procedure identifiers
     *
     * @param identifiers
     *            Procedure identifiers
     * @param session
     *            Hibernate session
     * @return Procedure objects
     */
    @SuppressWarnings("unchecked")
    public List<Procedure> getProceduresForIdentifiers(final Collection<String> identifiers, final Session session) {
        if (identifiers == null || identifiers.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.in(Procedure.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getProceduresForIdentifiers(identifiers): {}", HibernateHelper.getSqlString(criteria));
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
     * @throws CodedException
     */
    public Map<String,Collection<String>> getProceduresForAllFeaturesOfInterest(final Session session) {
        List<Object[]> results = getFeatureProcedureResult(session);
        Map<String,Collection<String>> foiProcMap = Maps.newHashMap();
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
     * @throws CodedException
     */
    public Map<String,Collection<String>> getFeaturesOfInterestsForAllProcedures(final Session session) {
        List<Object[]> results = getFeatureProcedureResult(session);
        Map<String,Collection<String>> foiProcMap = Maps.newHashMap();
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
            LOGGER.debug("QUERY getProceduresForAllFeaturesOfInterest(feature) with NamedQuery: {}",
                    SQL_QUERY_GET_PROCEDURES_FOR_ALL_FEATURES_OF_INTEREST);
            results = namedQuery.list();
        } else {
            Criteria c = null;
            if (EntitiyHelper.getInstance().isSeriesSupported()) {
                c = session.createCriteria(EntitiyHelper.getInstance().getSeriesEntityClass())
                    .createAlias(Series.FEATURE_OF_INTEREST, "f")
                    .createAlias(Series.PROCEDURE, "p")
                    .add(Restrictions.eq(Series.DELETED, false))
                    .setProjection(Projections.distinct(Projections.projectionList()
                        .add(Projections.property("f." + FeatureOfInterest.IDENTIFIER))
                        .add(Projections.property("p." + Procedure.IDENTIFIER))));
            } else {
                c = session.createCriteria(Observation.class)
                        .createAlias(Observation.FEATURE_OF_INTEREST, "f")
                        .createAlias(Observation.PROCEDURE, "p")
                        .add(Restrictions.eq(Observation.DELETED, false))
                        .setProjection(Projections.distinct(Projections.projectionList()
                            .add(Projections.property("f." + FeatureOfInterest.IDENTIFIER))
                            .add(Projections.property("p." + Procedure.IDENTIFIER))));
            }
            LOGGER.debug("QUERY getProceduresForAllFeaturesOfInterest(feature): {}", HibernateHelper.getSqlString(c));
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
     */
    @SuppressWarnings("unchecked")
    public List<String> getProceduresForFeatureOfInterest(final Session session, final FeatureOfInterest feature)
            throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST);
            namedQuery.setParameter(FEATURE, feature.getIdentifier());
            LOGGER.debug("QUERY getProceduresForFeatureOfInterest(feature) with NamedQuery: {}",
                    SQL_QUERY_GET_PROCEDURES_FOR_FEATURE_OF_INTEREST);
            return namedQuery.list();
        } else {
            Criteria c = null;
            if (EntitiyHelper.getInstance().isSeriesSupported()) {
                c = getDefaultCriteria(session);
                c.add(Subqueries.propertyIn(Procedure.ID,
                        getDetachedCriteriaProceduresForFeatureOfInterestFromSeries(feature, session)));
                c.setProjection(Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
            } else {
                AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
                c = observationDAO.getDefaultObservationInfoCriteria(session);
                c.createCriteria(AbstractObservation.FEATURE_OF_INTEREST).add(
                        Restrictions.eq(FeatureOfInterest.IDENTIFIER, feature.getIdentifier()));
                c.createCriteria(AbstractObservation.PROCEDURE).setProjection(
                        Projections.distinct(Projections.property(Procedure.IDENTIFIER)));

            }
            LOGGER.debug("QUERY getProceduresForFeatureOfInterest(feature): {}", HibernateHelper.getSqlString(c));
            return (List<String>) c.list();
        }
    }

    /**
     * Get procedure identifiers for offering identifier
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Procedure identifiers
     * @throws CodedException
     *             If an error occurs
     */
    @SuppressWarnings("unchecked")
    public List<String> getProcedureIdentifiersForOffering(final String offeringIdentifier, final Session session)
            throws OwsExceptionReport {
        final boolean obsConstSupported = HibernateHelper.isEntitySupported(ObservationConstellation.class);
        Criteria c = null;

        if (obsConstSupported) {
            c = getDefaultCriteria(session);
            c.add(Subqueries.propertyIn(Procedure.ID,
                    getDetachedCriteriaProceduresForOfferingFromObservationConstellation(offeringIdentifier, session)));
            c.setProjection(Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
        } else {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            c = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                Criteria seriesCriteria = c.createCriteria(SeriesObservationInfo.SERIES);
                seriesCriteria.createCriteria(Series.PROCEDURE).setProjection(
                        Projections.distinct(Projections.property(Procedure.IDENTIFIER)));

            } else {
                c.createCriteria(AbstractObservation.PROCEDURE).setProjection(
                        Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
            }
            new OfferingDAO().addOfferingRestricionForObservation(c, offeringIdentifier);
        }
        LOGGER.debug(
                "QUERY getProcedureIdentifiersForOffering(offeringIdentifier) using ObservationContellation entitiy ({}): {}",
                obsConstSupported, HibernateHelper.getSqlString(c));
        return c.list();
    }

    private Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(Procedure.class).add(Restrictions.eq(Procedure.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    private Criteria getDefaultTProcedureCriteria(Session session) {
        return session.createCriteria(TProcedure.class).add(Restrictions.eq(Procedure.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    private Criteria getDefaultTProcedureCriteriaIncludeDeleted(Session session) {
        return session.createCriteria(TProcedure.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Get procedure identifiers for observable property identifier
     *
     * @param observablePropertyIdentifier
     *            Observable property identifier
     * @param session
     *            Hibernate session
     * @return Procedure identifiers
     * @throws CodedException 
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getProcedureIdentifiersForObservableProperty(final String observablePropertyIdentifier,
            final Session session) throws CodedException {
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class);
        Criteria c = null;
        if (flag) {
            c = getDefaultCriteria(session);
            c.setProjection(Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
            c.add(Subqueries.propertyIn(
                    Procedure.ID,
                    getDetachedCriteriaProceduresForObservablePropertyFromObservationConstellation(
                            observablePropertyIdentifier, session)));
        } else {
            if (EntitiyHelper.getInstance().isSeriesSupported()) {
                c = getDefaultCriteria(session);
                c.setProjection(Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
                c.add(Subqueries.propertyIn(
                        Procedure.ID,
                        getDetachedCriteriaProceduresForObservablePropertyFromSeries(observablePropertyIdentifier,
                                session)));
            } else {
                c = session.createCriteria(ObservationInfo.class).add(Restrictions.eq(ObservationInfo.DELETED, false));
                c.createCriteria(ObservationInfo.PROCEDURE).setProjection(
                        Projections.distinct(Projections.property(Procedure.IDENTIFIER)));
                c.createCriteria(ObservationInfo.OBSERVABLE_PROPERTY).add(
                        Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));
            }
        }
        LOGGER.debug(
                "QUERY getProcedureIdentifiersForObservableProperty(observablePropertyIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get transactional procedure object for procedure identifier
     *
     * @param identifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Transactional procedure object
     */
    public TProcedure getTProcedureForIdentifier(final String identifier, final Session session) {
        Criteria criteria = getDefaultTProcedureCriteria(session).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getTProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (TProcedure) criteria.uniqueResult();
    }

    /**
     * Get transactional procedure object for procedure identifier, include deleted
     *
     * @param identifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Transactional procedure object
     */
    public TProcedure getTProcedureForIdentifierIncludeDeleted(String identifier, Session session) {
        Criteria criteria = getDefaultTProcedureCriteriaIncludeDeleted(session).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getTProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (TProcedure) criteria.uniqueResult();
    }

    /**
     * Get transactional procedure object for procedure identifier and
     * procedureDescriptionFormat
     *
     * @param identifier
     *            Procedure identifier
     * @param procedureDescriptionFormat
     *            ProcedureDescriptionFormat identifier
     * @param session
     *            Hibernate session
     * @return Transactional procedure object
     * @throws UnsupportedOperatorException
     * @throws UnsupportedValueReferenceException
     * @throws UnsupportedTimeException
     */
    public TProcedure getTProcedureForIdentifier(final String identifier, String procedureDescriptionFormat,
            Time validTime, final Session session) throws UnsupportedTimeException,
            UnsupportedValueReferenceException, UnsupportedOperatorException {
        Criteria criteria =
                getDefaultTProcedureCriteria(session).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        Criteria createValidProcedureTime = criteria.createCriteria(TProcedure.VALID_PROCEDURE_TIME);
        Criterion validTimeCriterion = QueryHelper.getValidTimeCriterion(validTime);
        if (validTime == null || validTimeCriterion == null) {
            createValidProcedureTime.add(Restrictions.isNull(ValidProcedureTime.END_TIME));
        } else {
            createValidProcedureTime.add(validTimeCriterion);
        }
        createValidProcedureTime.createCriteria(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT).add(
                Restrictions.eq(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT, procedureDescriptionFormat));
        LOGGER.debug("QUERY getTProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (TProcedure) criteria.uniqueResult();
    }

    /**
     * Get transactional procedure object for procedure identifier and
     * procedureDescriptionFormats
     *
     * @param identifier
     *            Procedure identifier
     * @param procedureDescriptionFormats
     *            ProcedureDescriptionFormat identifiers
     * @param session
     *            Hibernate session
     * @return Transactional procedure object
     */
    public TProcedure getTProcedureForIdentifier(final String identifier, Set<String> procedureDescriptionFormats,
            final Session session) {
        Criteria criteria =
                getDefaultTProcedureCriteria(session).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        criteria.createCriteria(TProcedure.VALID_PROCEDURE_TIME).add(
                Restrictions.in(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT, procedureDescriptionFormats));
        LOGGER.debug("QUERY getTProcedureForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (TProcedure) criteria.uniqueResult();
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
     * @return Procedure entity that match the parameters
     * @throws UnsupportedTimeException
     *             If the time is not supported
     * @throws UnsupportedValueReferenceException
     *             If the valueReference is not supported
     * @throws UnsupportedOperatorException
     *             If the temporal operator is not supported
     */
    public TProcedure getTProcedureForIdentifier(String identifier, Set<String> possibleProcedureDescriptionFormats,
            Time validTime, Session session) throws UnsupportedTimeException, UnsupportedValueReferenceException,
            UnsupportedOperatorException {
        Criteria criteria =
                getDefaultTProcedureCriteria(session).add(Restrictions.eq(Procedure.IDENTIFIER, identifier));
        Criteria createValidProcedureTime = criteria.createCriteria(TProcedure.VALID_PROCEDURE_TIME);
        Criterion validTimeCriterion = QueryHelper.getValidTimeCriterion(validTime);
        if (validTime == null || validTimeCriterion == null) {
            createValidProcedureTime.add(Restrictions.isNull(ValidProcedureTime.END_TIME));
        } else {
            createValidProcedureTime.add(validTimeCriterion);
        }
        createValidProcedureTime.createCriteria(ValidProcedureTime.PROCEDURE_DESCRIPTION_FORMAT).add(
                Restrictions.in(ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT,
                        possibleProcedureDescriptionFormats));
        LOGGER.debug(
                "QUERY getTProcedureForIdentifier(identifier, possibleProcedureDescriptionFormats, validTime): {}",
                HibernateHelper.getSqlString(criteria));
        return (TProcedure) criteria.uniqueResult();
    }
    
    public boolean isProcedureTimeExtremaNamedQuerySupported(Session session) {
        return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_PROCEDURE_TIME_EXTREMA, session);
    }

    public TimeExtrema getProcedureTimeExtremaFromNamedQuery(Session session, String procedureIdentifier) {
        Object[] result = null;
        if (isProcedureTimeExtremaNamedQuerySupported(session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_PROCEDURE_TIME_EXTREMA);
            namedQuery.setParameter(PROCEDURE, procedureIdentifier);
            LOGGER.debug("QUERY getProcedureTimeExtrema(procedure) with NamedQuery: {}",
                    SQL_QUERY_GET_PROCEDURE_TIME_EXTREMA);
            result = (Object[]) namedQuery.uniqueResult();
        }
        return parseProcedureTimeExtremaResult(result);
        
       
    }
    
    private TimeExtrema parseProcedureTimeExtremaResult(Object[] result) {
        TimeExtrema pte = new TimeExtrema();
        if (result != null) {
            pte.setMinTime(DateTimeHelper.makeDateTime(result[1]));
            DateTime maxPhenStart = DateTimeHelper.makeDateTime(result[2]);
            DateTime maxPhenEnd = DateTimeHelper.makeDateTime(result[3]);
            pte.setMaxTime(DateTimeHelper.max(maxPhenStart, maxPhenEnd));
        }
        return pte;
    }

    /**
     * Query procedure time extrema for the provided procedure identifier
     *
     * @param session
     * @param procedureIdentifier
     * @return ProcedureTimeExtrema
     * @throws CodedException
     */
    public TimeExtrema getProcedureTimeExtrema(final Session session, String procedureIdentifier)
            throws OwsExceptionReport {
        Object[] result;
        if (isProcedureTimeExtremaNamedQuerySupported(session)) {
            return getProcedureTimeExtremaFromNamedQuery(session, procedureIdentifier);
        }
        AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
        Criteria criteria = observationDAO.getDefaultObservationInfoCriteria(session);
        if (observationDAO instanceof AbstractSeriesObservationDAO) {
            criteria.createAlias(SeriesObservationInfo.SERIES, "s");
            criteria.createAlias("s." + Series.PROCEDURE, "p");
        } else {
            criteria.createAlias(ObservationInfo.PROCEDURE, "p");
        }
        criteria.add(Restrictions.eq("p." + Procedure.IDENTIFIER, procedureIdentifier));
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.groupProperty("p." + Procedure.IDENTIFIER));
        projectionList.add(Projections.min(AbstractObservation.PHENOMENON_TIME_START));
        projectionList.add(Projections.max(AbstractObservation.PHENOMENON_TIME_START));
        projectionList.add(Projections.max(AbstractObservation.PHENOMENON_TIME_END));
        criteria.setProjection(projectionList);

        LOGGER.debug("QUERY getProcedureTimeExtrema(procedureIdentifier): {}", HibernateHelper.getSqlString(criteria));
        result = (Object[]) criteria.uniqueResult();
        
        return parseProcedureTimeExtremaResult(result);
    }

    /**
     * Get min time from observations for procedure
     *
     * @param procedure
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return min time for procedure
     * @throws CodedException
     */
    public DateTime getMinDate4Procedure(final String procedure, final Session session) throws OwsExceptionReport {
        Object min = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE);
            namedQuery.setParameter(PROCEDURE, procedure);
            LOGGER.debug("QUERY getMinDate4Procedure(procedure) with NamedQuery: {}",
                    SQL_QUERY_GET_MIN_DATE_FOR_PROCEDURE);
            min = namedQuery.uniqueResult();
        } else {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            Criteria criteria = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                addProcedureRestrictionForSeries(criteria, procedure);
            } else {
                addProcedureRestrictionForObservation(criteria, procedure);
            }
            addMinMaxProjection(criteria, MinMax.MIN, AbstractObservation.PHENOMENON_TIME_START);
            LOGGER.debug("QUERY getMinDate4Procedure(procedure): {}", HibernateHelper.getSqlString(criteria));
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
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return max time for procedure
     * @throws CodedException
     */
    public DateTime getMaxDate4Procedure(final String procedure, final Session session) throws OwsExceptionReport {
        Object maxStart = null;
        Object maxEnd = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE);
            namedQuery.setParameter(PROCEDURE, procedure);
            LOGGER.debug("QUERY getMaxDate4Procedure(procedure) with NamedQuery: {}",
                    SQL_QUERY_GET_MAX_DATE_FOR_PROCEDURE);
            maxStart = namedQuery.uniqueResult();
            maxEnd = maxStart;
        } else {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            Criteria cstart = observationDAO.getDefaultObservationInfoCriteria(session);
            Criteria cend = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                addProcedureRestrictionForSeries(cstart, procedure);
                addProcedureRestrictionForSeries(cend, procedure);
            } else {
                addProcedureRestrictionForObservation(cstart, procedure);
                addProcedureRestrictionForObservation(cend, procedure);
            }
            addMinMaxProjection(cstart, MinMax.MAX, AbstractObservation.PHENOMENON_TIME_START);
            addMinMaxProjection(cend, MinMax.MAX, AbstractObservation.PHENOMENON_TIME_END);
            LOGGER.debug("QUERY getMaxDate4Procedure(procedure) start: {}", HibernateHelper.getSqlString(cstart));
            LOGGER.debug("QUERY getMaxDate4Procedure(procedure) end: {}", HibernateHelper.getSqlString(cend));
            if (HibernateHelper.getSqlString(cstart).endsWith(HibernateHelper.getSqlString(cend))) {
                maxStart = cstart.uniqueResult();
                maxEnd = maxStart;
                LOGGER.debug("Max time start and end query are identically, only one query is executed!");
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
     * @param procedureDecriptionFormat
     *            Procedure description format object
     * @param parentProcedures
     *            Parent procedure identifiers
     * @param session
     *            Hibernate session
     * @return Procedure object
     */
    public Procedure getOrInsertProcedure(final String identifier,
            final ProcedureDescriptionFormat procedureDecriptionFormat, final Collection<String> parentProcedures,
            final Session session) {
        Procedure procedure = getProcedureForIdentifierIncludeDeleted(identifier, session);
        if (procedure == null) {
            final TProcedure tProcedure = new TProcedure();
            tProcedure.setProcedureDescriptionFormat(procedureDecriptionFormat);
            tProcedure.setIdentifier(identifier);
            if (CollectionHelper.isNotEmpty(parentProcedures)) {
                tProcedure.setParents(Sets.newHashSet(getProceduresForIdentifiers(parentProcedures, session)));
            }
            procedure = tProcedure;
        }
        procedure.setDeleted(false);
        session.saveOrUpdate(procedure);
        session.flush();
        session.refresh(procedure);
        return procedure;
    }

    /**
     * Get Hibernate Detached Criteria for class Series and featureOfInterest
     * identifier
     *
     * @param featureOfInterest
     *            FeatureOfInterest identifier parameter
     * @param session
     *            Hibernate session
     * @return Hiberante Detached Criteria with Procedure entities
     * @throws CodedException 
     */
    private DetachedCriteria getDetachedCriteriaProceduresForFeatureOfInterestFromSeries(
            FeatureOfInterest featureOfInterest, Session session) throws CodedException {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DaoFactory.getInstance().getSeriesDAO().getClass());
        detachedCriteria.add(Restrictions.eq(Series.DELETED, false));
        detachedCriteria.add(Restrictions.eq(Series.FEATURE_OF_INTEREST, featureOfInterest));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(Series.PROCEDURE)));
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
     * @return Hiberante Detached Criteria with Procedure entities
     */
    private DetachedCriteria getDetachedCriteriaProceduresForObservablePropertyFromObservationConstellation(
            String observablePropertyIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(ObservationConstellation.PROCEDURE)));
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
     * @return Hiberante Detached Criteria with Procedure entities
     * @throws CodedException 
     */
    private DetachedCriteria getDetachedCriteriaProceduresForObservablePropertyFromSeries(
            String observablePropertyIdentifier, Session session) throws CodedException {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DaoFactory.getInstance().getSeriesDAO().getClass());
        detachedCriteria.add(Restrictions.eq(Series.DELETED, false));
        detachedCriteria.createCriteria(Series.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(Series.PROCEDURE)));
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
     * @return Detached Criteria with Procedure entities
     */
    private DetachedCriteria getDetachedCriteriaProceduresForOfferingFromObservationConstellation(
            String offeringIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.OFFERING).add(
                Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(ObservationConstellation.PROCEDURE)));
        return detachedCriteria;
    }

    /**
     * Add procedure identifier restriction to Hibernate Criteria for series
     *
     * @param criteria
     *            Hibernate Criteria for series to add restriction
     * @param procedure
     *            Procedure identifier
     */
    private void addProcedureRestrictionForSeries(Criteria criteria, String procedure) {
        Criteria seriesCriteria = criteria.createCriteria(SeriesObservationInfo.SERIES);
        seriesCriteria.createCriteria(SeriesObservationInfo.PROCEDURE).add(
                Restrictions.eq(Procedure.IDENTIFIER, procedure));
    }

    /**
     * Add procedure identifier restriction to Hibernate Criteria
     *
     * @param criteria
     *            Hibernate Criteria to add restriction
     * @param procedure
     *            Procedure identifier
     */
    private void addProcedureRestrictionForObservation(Criteria criteria, String procedure) {
        criteria.createCriteria(ObservationInfo.PROCEDURE).add(Restrictions.eq(Procedure.IDENTIFIER, procedure));
    }

    @SuppressWarnings("unchecked")
    protected Set<String> getObservationIdentifiers(Session session, String procedureIdentifier) {
        if (EntitiyHelper.getInstance().isSeriesObservationInfoSupported()) {
            Criteria criteria =
                    session.createCriteria(EntitiyHelper.getInstance().getObservationInfoEntityClass())
                            .setProjection(
                                    Projections.distinct(Projections.property(SeriesObservationInfo.IDENTIFIER)))
                            .add(Restrictions.isNotNull(SeriesObservationInfo.IDENTIFIER))
                            .add(Restrictions.eq(SeriesObservationInfo.DELETED, false));
            Criteria seriesCriteria = criteria.createCriteria(SeriesObservationInfo.SERIES);
            seriesCriteria.createCriteria(Series.PROCEDURE).add(
                    Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
            LOGGER.debug("QUERY getObservationIdentifiers(procedureIdentifier): {}",
                    HibernateHelper.getSqlString(criteria));
            return Sets.newHashSet(criteria.list());
        } else {
            Criteria criteria =
                    session.createCriteria(EntitiyHelper.getInstance().getObservationInfoEntityClass())
                            .setProjection(Projections.distinct(Projections.property(ObservationInfo.IDENTIFIER)))
                            .add(Restrictions.isNotNull(ObservationInfo.IDENTIFIER))
                            .add(Restrictions.eq(ObservationInfo.DELETED, false));
            criteria.createCriteria(ObservationInfo.PROCEDURE).add(
                    Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
            LOGGER.debug("QUERY getObservationIdentifiers(procedureIdentifier): {}",
                    HibernateHelper.getSqlString(criteria));
            return Sets.newHashSet(criteria.list());
        }
    }

    public Map<String,String> getProcedureFormatMap(Session session) {
        if (HibernateHelper.isEntitySupported(TProcedure.class)) {
            //get the latest validProcedureTimes' procedureDescriptionFormats
            return new ValidProcedureTimeDAO().getTProcedureFormatMap(session);
        } else {
            Criteria criteria = session.createCriteria(Procedure.class);
            criteria.createAlias(Procedure.PROCEDURE_DESCRIPTION_FORMAT, "pdf");
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.property(Procedure.IDENTIFIER))
                    .add(Projections.property("pdf." + ProcedureDescriptionFormat.PROCEDURE_DESCRIPTION_FORMAT)));
            criteria.addOrder(Order.asc(Procedure.IDENTIFIER));
            LOGGER.debug("QUERY getProcedureFormatMap(): {}", HibernateHelper.getSqlString(criteria));
            @SuppressWarnings("unchecked")
            List<Object[]> results = criteria.list();
            Map<String,String> procedureFormatMap = Maps.newTreeMap();
            for (Object[] result : results) {
                String procedureIdentifier = (String) result[0];
                String format = (String) result[1];
                procedureFormatMap.put(procedureIdentifier, format);
            }
            return procedureFormatMap;
        }
    }
}
