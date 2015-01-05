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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.dao.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationType;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;
import org.n52.sos.ds.hibernate.entities.TOffering;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationInfo;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Hibernate data access class for offering
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class OfferingDAO extends TimeCreator implements HibernateSqlQueryConstants {

    private static final String SQL_QUERY_OFFERING_TIME_EXTREMA = "getOfferingTimeExtrema";

    private static final String SQL_QUERY_GET_MIN_DATE_FOR_OFFERING = "getMinDate4Offering";

    private static final String SQL_QUERY_GET_MAX_DATE_FOR_OFFERING = "getMaxDate4Offering";

    private static final String SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING = "getMinResultTime4Offering";

    private static final String SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING = "getMaxResultTime4Offering";

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OfferingDAO.class);

    /**
     * Get transactional offering object for identifier
     *
     * @param identifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Transactional offering object
     */
    public TOffering getTOfferingForIdentifier(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(TOffering.class).add(Restrictions.eq(Offering.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getTOfferingForIdentifier(): {}", HibernateHelper.getSqlString(criteria));
        return (TOffering) criteria.uniqueResult();
    }

    /**
     * Get offering objects for cache update
     *
     * @param identifiers
     *            Optional collection of offering identifiers to fetch. If null, all offerings are returned.
     * @param session
     *            Hibernate session
     * @return Offering objects
     */
    @SuppressWarnings("unchecked")
    public List<Offering> getOfferingObjectsForCacheUpdate(final Collection<String> identifiers, final Session session) {
    	 Class<?> clazz = Offering.class;
    	 if (HibernateHelper.isEntitySupported(TOffering.class)) {
    		 clazz = TOffering.class;
    	 }
    	 Criteria criteria = session.createCriteria(clazz);
		if (CollectionHelper.isNotEmpty(identifiers)) {
		    criteria.add(Restrictions.in(Offering.IDENTIFIER, identifiers));
		}
        LOGGER.debug("QUERY getOfferingObjectsForCacheUpdate(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get Offering object for identifier
     *
     * @param identifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Offering object
     */
    public Offering getOfferingForIdentifier(final String identifier, final Session session) {
        Criteria criteria =
                session.createCriteria(Offering.class).add(Restrictions.eq(Offering.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getOfferingForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (Offering) criteria.uniqueResult();
    }

    /**
     * Get Offering objects for identifiers
     *
     * @param identifiers
     *            Offering identifiers
     * @param session
     *            Hibernate session
     * @return Offering objects
     */
    @SuppressWarnings("unchecked")
    public Collection<Offering> getOfferingsForIdentifiers(final Collection<String> identifiers, final Session session) {
        Criteria criteria =
                session.createCriteria(Offering.class).add(Restrictions.in(Offering.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getOfferingsForIdentifiers(identifiers): {}", HibernateHelper.getSqlString(criteria));
        return (List<Offering>) criteria.list();
    }

    /**
     * Get offering identifiers for procedure identifier
     *
     * @param procedureIdentifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Offering identifiers
     * @throws OwsExceptionReport
     */
    @SuppressWarnings("unchecked")
    public List<String> getOfferingIdentifiersForProcedure(final String procedureIdentifier, final Session session) throws OwsExceptionReport {
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class);
        Criteria c = null;
        if (flag) {
            c = session.createCriteria(Offering.class);
            c.add(Subqueries.propertyIn(Offering.ID, getDetachedCriteriaOfferingForProcedureFromObservationConstellation(procedureIdentifier, session)));
            c.setProjection(Projections.distinct(Projections.property(Offering.IDENTIFIER)));
        } else {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            c = observationDAO.getDefaultObservationInfoCriteria(session);
            c.createCriteria(AbstractObservation.OFFERINGS).setProjection(
                    Projections.distinct(Projections.property(Offering.IDENTIFIER)));
            if (observationDAO instanceof SeriesObservationDAO) {
                Criteria seriesCriteria = c.createCriteria(SeriesObservationInfo.SERIES);
                seriesCriteria.createCriteria(Series.PROCEDURE).add(Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));

            } else {
                c.createCriteria(AbstractObservation.PROCEDURE).add(Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
            }
        }
        LOGGER.debug(
                "QUERY getOfferingIdentifiersForProcedure(procedureIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get offering identifiers for observable property identifier
     *
     * @param observablePropertyIdentifier
     *            Observable property identifier
     * @param session
     *            Hibernate session
     * @return Offering identifiers
     * @throws CodedException
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getOfferingIdentifiersForObservableProperty(final String observablePropertyIdentifier,
            final Session session) throws OwsExceptionReport {
        final boolean flag = HibernateHelper.isEntitySupported(ObservationConstellation.class);
        Criteria c = null;
        if (flag) {
            c = session.createCriteria(Offering.class);
            c.add(Subqueries.propertyIn(Offering.ID,
                    getDetachedCriteriaOfferingForObservablePropertyFromObservationConstellation(observablePropertyIdentifier, session)));
            c.setProjection(Projections.distinct(Projections.property(Offering.IDENTIFIER)));
        } else {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            c = observationDAO.getDefaultObservationInfoCriteria(session);
            c.createCriteria(AbstractObservation.OFFERINGS).setProjection(
                    Projections.distinct(Projections.property(Offering.IDENTIFIER)));
            if (observationDAO instanceof SeriesObservationDAO) {
                Criteria seriesCriteria = c.createCriteria(SeriesObservationInfo.SERIES);
                seriesCriteria.createCriteria(Series.OBSERVABLE_PROPERTY).add(Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));

            } else {
                c.createCriteria(AbstractObservation.OBSERVABLE_PROPERTY).add(Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));
            }
        }
        LOGGER.debug(
                "QUERY getOfferingIdentifiersForObservableProperty(observablePropertyIdentifier) using ObservationContellation entitiy ({}): {}",
                flag, HibernateHelper.getSqlString(c));
        return c.list();
    }

    public class OfferingTimeExtrema {
        private DateTime minPhenomenonTime;
        private DateTime maxPhenomenonTime;
        private DateTime minResultTime;
        private DateTime maxResultTime;

        public DateTime getMinPhenomenonTime() {
            return minPhenomenonTime;
        }

        public void setMinPhenomenonTime(DateTime minPhenomenonTime) {
            this.minPhenomenonTime = minPhenomenonTime;
        }

        public DateTime getMaxPhenomenonTime() {
            return maxPhenomenonTime;
        }

        public void setMaxPhenomenonTime(DateTime maxPhenomenonTime) {
            this.maxPhenomenonTime = maxPhenomenonTime;
        }

        public DateTime getMinResultTime() {
            return minResultTime;
        }

        public void setMinResultTime(DateTime minResultTime) {
            this.minResultTime = minResultTime;
        }

        public DateTime getMaxResultTime() {
            return maxResultTime;
        }

        public void setMaxResultTime(DateTime maxResultTime) {
            this.maxResultTime = maxResultTime;
        }
    }

    /**
     * Get offering time extrema
     *
     * @param identifiers
     *            Optional collection of offering identifiers to fetch. If null, all offerings are returned.
     * @param session
     *            Hibernate session Hibernate session
     * @return Map of offering time extrema, keyed by offering identifier
     * @throws CodedException
     */
    @SuppressWarnings("unchecked")
    public Map<String,OfferingTimeExtrema> getOfferingTimeExtrema(final Collection<String> identifiers,
            final Session session) throws OwsExceptionReport {
        List<Object[]> results = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_OFFERING_TIME_EXTREMA, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_OFFERING_TIME_EXTREMA);
            if (CollectionHelper.isNotEmpty(identifiers)) {
                namedQuery.setParameterList("identifiers", identifiers);
            }
            LOGGER.debug("QUERY getOfferingTimeExtrema() with NamedQuery: {}",
                    SQL_QUERY_OFFERING_TIME_EXTREMA);
            results = namedQuery.list();
        } else {
            Criteria criteria = DaoFactory.getInstance().getObservationDAO()
                .getDefaultObservationInfoCriteria(session)
                .createAlias(AbstractObservation.OFFERINGS, "off")
                .setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("off." + Offering.IDENTIFIER))
                    .add(Projections.min(AbstractObservation.PHENOMENON_TIME_START))
                    .add(Projections.max(AbstractObservation.PHENOMENON_TIME_START))
                    .add(Projections.max(AbstractObservation.PHENOMENON_TIME_END))
                    .add(Projections.min(AbstractObservation.RESULT_TIME))
                    .add(Projections.max(AbstractObservation.RESULT_TIME)));
            if (CollectionHelper.isNotEmpty(identifiers)) {
                criteria.add(Restrictions.in(Offering.IDENTIFIER, identifiers));
            }
            LOGGER.debug("QUERY getOfferingTimeExtrema(): {}", HibernateHelper.getSqlString(criteria));
            results = criteria.list();
        }

        Map<String,OfferingTimeExtrema> map = Maps.newHashMap();
        for (Object[] result : results) {
            String offering = (String) result[0];
            OfferingTimeExtrema ote = new OfferingTimeExtrema();
            ote.setMinPhenomenonTime(DateTimeHelper.makeDateTime(result[1]));
            DateTime maxPhenStart = DateTimeHelper.makeDateTime(result[2]);
            DateTime maxPhenEnd = DateTimeHelper.makeDateTime(result[3]);
            ote.setMaxPhenomenonTime(DateTimeHelper.max(maxPhenStart, maxPhenEnd));
            ote.setMinResultTime(DateTimeHelper.makeDateTime(result[4]));
            ote.setMaxResultTime(DateTimeHelper.makeDateTime(result[5]));
            map.put(offering, ote);
        }
        return map;
    }

    /**
     * Get min time from observations for offering
     *
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return min time for offering
     * @throws CodedException
     */
    public DateTime getMinDate4Offering(final String offering, final Session session) throws OwsExceptionReport {
        Object min = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MIN_DATE_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MIN_DATE_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMinDate4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MIN_DATE_FOR_OFFERING);
            min = namedQuery.uniqueResult();
        } else {
            Criteria criteria =
                    DaoFactory.getInstance().getObservationDAO().getDefaultObservationInfoCriteria(session);
            addOfferingRestricionForObservation(criteria, offering);
            addMinMaxProjection(criteria, MinMax.MIN, AbstractObservation.PHENOMENON_TIME_START);
            LOGGER.debug("QUERY Series-getMinDate4Offering(offering): {}", HibernateHelper.getSqlString(criteria));
            min = criteria.uniqueResult();
        }
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Get max time from observations for offering
     *
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return max time for offering
     * @throws CodedException
     */
    public DateTime getMaxDate4Offering(final String offering, final Session session) throws OwsExceptionReport {
        Object maxStart = null;
        Object maxEnd = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MAX_DATE_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MAX_DATE_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMaxDate4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MAX_DATE_FOR_OFFERING);
            maxStart = namedQuery.uniqueResult();
            maxEnd = maxStart;
        } else {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            Criteria cstart = observationDAO.getDefaultObservationInfoCriteria(session);
            Criteria cend = observationDAO.getDefaultObservationInfoCriteria(session);
            addOfferingRestricionForObservation(cstart, offering);
            addOfferingRestricionForObservation(cend, offering);
            addMinMaxProjection(cstart, MinMax.MAX, AbstractObservation.PHENOMENON_TIME_START);
            addMinMaxProjection(cend, MinMax.MAX, AbstractObservation.PHENOMENON_TIME_END);
            LOGGER.debug("QUERY getMaxDate4Offering(offering) start: {}", HibernateHelper.getSqlString(cstart));
            LOGGER.debug("QUERY getMaxDate4Offering(offering) end: {}", HibernateHelper.getSqlString(cend));
            if (HibernateHelper.getSqlString(cstart).equals(HibernateHelper.getSqlString(cend))) {
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
     * Get min result time from observations for offering
     *
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     *
     * @return min result time for offering
     * @throws CodedException
     */
    public DateTime getMinResultTime4Offering(final String offering, final Session session) throws OwsExceptionReport {
        Object min = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMinResultTime4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING);
            min = namedQuery.uniqueResult();
        } else {
            Criteria criteria =
                    DaoFactory.getInstance().getObservationDAO().getDefaultObservationInfoCriteria(session);
            addOfferingRestricionForObservation(criteria, offering);
            addMinMaxProjection(criteria, MinMax.MIN, AbstractObservation.RESULT_TIME);
            LOGGER.debug("QUERY getMinResultTime4Offering(offering): {}", HibernateHelper.getSqlString(criteria));
            min = criteria.uniqueResult();
        }
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Get max result time from observations for offering
     *
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     *
     * @return max result time for offering
     * @throws CodedException
     */
    public DateTime getMaxResultTime4Offering(final String offering, final Session session) throws OwsExceptionReport {
        Object maxStart = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMaxResultTime4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING);
            maxStart = namedQuery.uniqueResult();
        } else {
            Criteria criteria =
                    DaoFactory.getInstance().getObservationDAO().getDefaultObservationInfoCriteria(session);
            addOfferingRestricionForObservation(criteria, offering);
            addMinMaxProjection(criteria, MinMax.MAX, AbstractObservation.RESULT_TIME);
            LOGGER.debug("QUERY getMaxResultTime4Offering(offering): {}", HibernateHelper.getSqlString(criteria));
            maxStart = criteria.uniqueResult();
        }

        if (maxStart == null) {
            return null;
        } else {
            return new DateTime(maxStart, DateTimeZone.UTC);
        }
    }

    /**
     * Get temporal bounding box for each offering
     *
     * @param session
     *            Hibernate session
     * @return a Map containing the temporal bounding box for each offering
     * @throws CodedException
     */
    public Map<String, TimePeriod> getTemporalBoundingBoxesForOfferings(final Session session) throws OwsExceptionReport {
        if (session != null) {
            Criteria criteria =
                    DaoFactory.getInstance().getObservationDAO().getDefaultObservationInfoCriteria(session);
            criteria.createAlias(AbstractObservation.OFFERINGS, "off");
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.min(AbstractObservation.PHENOMENON_TIME_START))
                    .add(Projections.max(AbstractObservation.PHENOMENON_TIME_START))
                    .add(Projections.max(AbstractObservation.PHENOMENON_TIME_END))
                    .add(Projections.groupProperty("off." + Offering.IDENTIFIER)));
            LOGGER.debug("QUERY getTemporalBoundingBoxesForOfferings(): {}", HibernateHelper.getSqlString(criteria));
            final List<?> temporalBoundingBoxes = criteria.list();
            if (!temporalBoundingBoxes.isEmpty()) {
                final HashMap<String, TimePeriod> temporalBBoxMap =
                        new HashMap<String, TimePeriod>(temporalBoundingBoxes.size());
                for (final Object recordObj : temporalBoundingBoxes) {
                    if (recordObj instanceof Object[]) {
                        final Object[] record = (Object[]) recordObj;
                        final TimePeriod value =
                                createTimePeriod((Timestamp) record[0], (Timestamp) record[1], (Timestamp) record[2]);
                        temporalBBoxMap.put((String) record[3], value);
                    }
                }
                LOGGER.debug(temporalBoundingBoxes.toString());
                return temporalBBoxMap;
            }
        }
        return new HashMap<String, TimePeriod>(0);
    }

    /**
     * Insert or update and get offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param offeringName
     *            Offering name
     * @param relatedFeatures
     *            Related feature objects
     * @param observationTypes
     *            Allowed observation type objects
     * @param featureOfInterestTypes
     *            Allowed featureOfInterest type objects
     * @param session
     *            Hibernate session
     * @return Offering object
     */
    public Offering getAndUpdateOrInsertNewOffering(final String offeringIdentifier, final String offeringName,
            final List<RelatedFeature> relatedFeatures, final List<ObservationType> observationTypes,
            final List<FeatureOfInterestType> featureOfInterestTypes, final Session session) {

        TOffering offering = getTOfferingForIdentifier(offeringIdentifier, session);
        if (offering == null) {
            offering = new TOffering();
            offering.setIdentifier(offeringIdentifier);
            if (offeringName != null) {
                offering.setName(offeringName);
            } else {
                offering.setName("Offering for the procedure " + offeringIdentifier);
            }
        }
        if (!relatedFeatures.isEmpty()) {
            offering.setRelatedFeatures(new HashSet<RelatedFeature>(relatedFeatures));
        } else {
            offering.setRelatedFeatures(new HashSet<RelatedFeature>(0));
        }
        if (!observationTypes.isEmpty()) {
            offering.setObservationTypes(new HashSet<ObservationType>(observationTypes));
        } else {
            offering.setObservationTypes(new HashSet<ObservationType>(0));
        }
        if (!featureOfInterestTypes.isEmpty()) {
            offering.setFeatureOfInterestTypes(new HashSet<FeatureOfInterestType>(featureOfInterestTypes));
        } else {
            offering.setFeatureOfInterestTypes(new HashSet<FeatureOfInterestType>(0));
        }
        session.saveOrUpdate(offering);
        session.flush();
        session.refresh(offering);
        return offering;
    }

    /**
     * Get Hibernate Detached Criteria for class ObservationConstellation and
     * observableProperty identifier
     *
     * @param observablePropertyIdentifier
     *            ObservableProperty identifier parameter
     * @param session
     *            Hibernate session
     * @return Detached Criteria with Offering entities as result
     */
    private DetachedCriteria getDetachedCriteriaOfferingForObservablePropertyFromObservationConstellation(String observablePropertyIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observablePropertyIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(ObservationConstellation.OFFERING)));
        return detachedCriteria;
    }

    /**
     * Get Hibernate Detached Criteria for class ObservationConstellation and
     * procedure identifier
     *
     * @param procedureIdentifier
     *            Procedure identifier parameter
     * @param session
     *            Hibernate session
     * @return Detached Criteria with Offering entities as result
     */
    private DetachedCriteria getDetachedCriteriaOfferingForProcedureFromObservationConstellation(String procedureIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(ObservationConstellation.class);
        detachedCriteria.add(Restrictions.eq(ObservationConstellation.DELETED, false));
        detachedCriteria.createCriteria(ObservationConstellation.PROCEDURE).add(
                Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(ObservationConstellation.OFFERING)));
        return detachedCriteria;
    }

    /**
     * Query allowed FeatureOfInterestTypes for offering
     * @param offeringIdentifier Offering identifier
     * @param session
     *            Hibernate session
     * @return Allowed FeatureOfInterestTypes
     */
    public List<String> getAllowedFeatureOfInterestTypes(String offeringIdentifier, Session session) {
        if (HibernateHelper.isEntitySupported(TOffering.class)) {
            Criteria criteria =
                    session.createCriteria(TOffering.class).add(Restrictions.eq(Offering.IDENTIFIER, offeringIdentifier));
            LOGGER.debug("QUERY getAllowedFeatureOfInterestTypes(offering): {}",
                    HibernateHelper.getSqlString(criteria));
            TOffering offering = (TOffering) criteria.uniqueResult();
            if (offering != null) {
                List<String> list = Lists.newArrayList();
                for (FeatureOfInterestType featureOfInterestType : offering.getFeatureOfInterestTypes()) {
                    list.add(featureOfInterestType.getFeatureOfInterestType());
                }
                return list;
            }
        }
        return Lists.newArrayList();
    }

    /**
     * Add offering identifier restriction to Hibernate Criteria
     * @param criteria Hibernate Criteria to add restriction
     * @param offering Offering identifier
     */
    public void addOfferingRestricionForObservation(Criteria criteria, String offering) {
        criteria.createCriteria(AbstractObservation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offering));
    }
}
