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
package org.n52.sos.ds.hibernate.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.Query;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.ResultTransformer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.locationtech.jts.geom.Geometry;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.GeometryEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.RelatedFeatureEntity;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.NoopTransformerAdapter;
import org.n52.sos.ds.hibernate.util.OfferingTimeExtrema;
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
public class OfferingDAO extends AbstractIdentifierNameDescriptionDAO implements HibernateSqlQueryConstants {

    private static final String SQL_QUERY_OFFERING_TIME_EXTREMA = "getOfferingTimeExtrema";

    private static final String SQL_QUERY_GET_MIN_DATE_FOR_OFFERING = "getMinDate4Offering";

    private static final String SQL_QUERY_GET_MAX_DATE_FOR_OFFERING = "getMaxDate4Offering";

    private static final String SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING = "getMinResultTime4Offering";

    private static final String SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING = "getMaxResultTime4Offering";

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferingDAO.class);

    private final OfferingeTimeTransformer transformer = new OfferingeTimeTransformer();

    public OfferingDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Get transactional offering object for identifier
     *
     * @param identifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Transactional offering object
     */
    public OfferingEntity getTOfferingForIdentifier(final String identifier, final Session session) {
        Criteria criteria =
                getDefaultTransactionalCriteria(session).add(Restrictions.eq(OfferingEntity.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getTOfferingForIdentifier(): {}", HibernateHelper.getSqlString(criteria));
        return (OfferingEntity) criteria.uniqueResult();
    }

    /**
     * Get all offering objects
     *
     * @param session
     *            Hibernate session
     * @return Offering objects
     */
    public List<OfferingEntity> getOfferings(final Session session) {
        Criteria criteria = session.createCriteria(OfferingEntity.class);
        LOGGER.debug("QUERY getOfferings(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get offering objects for cache update
     *
     * @param identifiers
     *            Optional collection of offering identifiers to fetch. If null,
     *            all offerings are returned.
     * @param session
     *            Hibernate session
     * @return Offering objects
     */
    @SuppressWarnings("unchecked")
    public List<OfferingEntity> getOfferingObjectsForCacheUpdate(final Collection<String> identifiers,
            final Session session) {
        Criteria criteria = getDefaultCriteria(session);
        if (CollectionHelper.isNotEmpty(identifiers)) {
            criteria.add(Restrictions.in(OfferingEntity.IDENTIFIER, identifiers));
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
    public OfferingEntity getOfferingForIdentifier(final String identifier, final Session session) {
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.eq(OfferingEntity.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getOfferingForIdentifier(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (OfferingEntity) criteria.uniqueResult();
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
    public Collection<OfferingEntity> getOfferingsForIdentifiers(final Collection<String> identifiers,
            final Session session) {
        Criteria criteria = getDefaultCriteria(session).add(Restrictions.in(OfferingEntity.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getOfferingsForIdentifiers(identifiers): {}", HibernateHelper.getSqlString(criteria));
        return (List<OfferingEntity>) criteria.list();
    }

    /**
     * Get offering identifiers for procedure identifier
     *
     * @param procedureIdentifier
     *            Procedure identifier
     * @param session
     *            Hibernate session
     * @return Offering identifiers
     * @throws OwsExceptionReport If an error occurs
     */
    @SuppressWarnings("unchecked")
    public List<String> getOfferingIdentifiersForProcedure(final String procedureIdentifier, final Session session)
            throws OwsExceptionReport {
        Criteria c = getDefaultCriteria(session);
        c.add(Subqueries.propertyIn(OfferingEntity.PROPERTY_ID,
                getDetachedCriteriaOfferingForProcedureFromObservationConstellation(procedureIdentifier, session)));
        c.setProjection(Projections.distinct(Projections.property(OfferingEntity.IDENTIFIER)));
        LOGGER.debug("QUERY getOfferingIdentifiersForProcedure(procedureIdentifier): {}",
                HibernateHelper.getSqlString(c));
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
     * @throws CodedException If an error occurs
     */
    @SuppressWarnings("unchecked")
    public Collection<String> getOfferingIdentifiersForObservableProperty(final String observablePropertyIdentifier,
            final Session session) throws OwsExceptionReport {
        Criteria c = getDefaultCriteria(session);
        c.add(Subqueries.propertyIn(OfferingEntity.PROPERTY_ID,
                getDetachedCriteriaOfferingForObservablePropertyFromObservationConstellation(
                        observablePropertyIdentifier, session)));
        c.setProjection(Projections.distinct(Projections.property(OfferingEntity.IDENTIFIER)));
        LOGGER.debug("QUERY getOfferingIdentifiersForObservableProperty(observablePropertyIdentifier): {}",
                HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get offering time extrema
     *
     * @param identifiers
     *            Optional collection of offering identifiers to fetch. If null,
     *            all offerings are returned.
     * @param session
     *            Hibernate session Hibernate session
     * @return Map of offering time extrema, keyed by offering identifier
     * @throws CodedException If an error occurs
     */
    @SuppressWarnings("unchecked")
    public Map<String, OfferingTimeExtrema> getOfferingTimeExtrema(final Collection<String> identifiers,
            final Session session) throws OwsExceptionReport {
        List<OfferingTimeExtrema> results = null;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_OFFERING_TIME_EXTREMA, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_OFFERING_TIME_EXTREMA);
            if (CollectionHelper.isNotEmpty(identifiers)) {
                namedQuery.setParameterList("identifiers", identifiers);
            }
            LOGGER.debug("QUERY getOfferingTimeExtrema() with NamedQuery: {}", SQL_QUERY_OFFERING_TIME_EXTREMA);
            namedQuery.setResultTransformer(transformer);
            results = namedQuery.list();
        } else {
            Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getJdbcServices().getDialect();
            Criteria criteria = getDaoFactory().getObservationDAO().getDefaultObservationInfoCriteria(session)
                    .createAlias(DataEntity.PROPERTY_DATASET, "ds")
                    .createAlias(DatasetEntity.PROPERTY_OFFERING, "ds.off");
            ProjectionList projectionList =
                    Projections.projectionList().add(Projections.groupProperty("ds.off." + OfferingEntity.IDENTIFIER))
                            .add(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START))
                            .add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_START))
                            .add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END))
                            .add(Projections.min(DataEntity.PROPERTY_RESULT_TIME))
                            .add(Projections.max(DataEntity.PROPERTY_RESULT_TIME));
            if (getDaoFactory().getGeometryHandler().isSpatialDatasource()
                    && HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                projectionList.add(SpatialProjections.extent(DataEntity.PROPERTY_GEOMETRY_ENTITY));
            }
            criteria.setProjection(projectionList);
            if (CollectionHelper.isNotEmpty(identifiers)) {
                criteria.add(Restrictions.in(OfferingEntity.IDENTIFIER, identifiers));
            }
            LOGGER.debug("QUERY getOfferingTimeExtrema(): {}", HibernateHelper.getSqlString(criteria));
            criteria.setResultTransformer(transformer);
            results = criteria.list();
        }

        Map<String, OfferingTimeExtrema> map = Maps.newHashMap();
        for (OfferingTimeExtrema result : results) {
            if (result.isSetOffering()) {
                map.put(result.getOffering(), result);
            }
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
     * @throws CodedException If an error occurs
     */
    public DateTime getMinDate4Offering(final String offering, final Session session) throws OwsExceptionReport {
        Object min;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MIN_DATE_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MIN_DATE_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMinDate4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MIN_DATE_FOR_OFFERING);
            min = namedQuery.uniqueResult();
        } else {
            Criteria criteria = getDaoFactory().getObservationDAO().getDefaultObservationInfoCriteria(session);
            addOfferingRestricionForObservation(criteria, offering);
            addMinMaxProjection(criteria, MinMax.MIN, DataEntity.PROPERTY_SAMPLING_TIME_START);
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
     * @throws CodedException If an error occurs
     */
    public DateTime getMaxDate4Offering(final String offering, final Session session) throws OwsExceptionReport {
        Object maxStart;
        Object maxEnd;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MAX_DATE_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MAX_DATE_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMaxDate4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MAX_DATE_FOR_OFFERING);
            maxStart = namedQuery.uniqueResult();
            maxEnd = maxStart;
        } else {
            AbstractObservationDAO observationDAO = getDaoFactory().getObservationDAO();
            Criteria cstart = observationDAO.getDefaultObservationInfoCriteria(session);
            Criteria cend = observationDAO.getDefaultObservationInfoCriteria(session);
            addOfferingRestricionForObservation(cstart, offering);
            addOfferingRestricionForObservation(cend, offering);
            addMinMaxProjection(cstart, MinMax.MAX, DataEntity.PROPERTY_SAMPLING_TIME_START);
            addMinMaxProjection(cend, MinMax.MAX, DataEntity.PROPERTY_SAMPLING_TIME_END);
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
     * @throws CodedException If an error occurs
     */
    public DateTime getMinResultTime4Offering(final String offering, final Session session) throws OwsExceptionReport {
        Object min;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMinResultTime4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MIN_RESULT_TIME_FOR_OFFERING);
            min = namedQuery.uniqueResult();
        } else {
            Criteria criteria = getDaoFactory().getObservationDAO().getDefaultObservationInfoCriteria(session);
            addOfferingRestricionForObservation(criteria, offering);
            addMinMaxProjection(criteria, MinMax.MIN, DataEntity.PROPERTY_RESULT_TIME);
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
     * @throws CodedException If an error occurs
     */
    public DateTime getMaxResultTime4Offering(final String offering, final Session session) throws OwsExceptionReport {
        Object maxStart;
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING, session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.debug("QUERY getMaxResultTime4Offering(offering) with NamedQuery: {}",
                    SQL_QUERY_GET_MAX_RESULT_TIME_FOR_OFFERING);
            maxStart = namedQuery.uniqueResult();
        } else {
            Criteria criteria = getDaoFactory().getObservationDAO().getDefaultObservationInfoCriteria(session);
            addOfferingRestricionForObservation(criteria, offering);
            addMinMaxProjection(criteria, MinMax.MAX, DataEntity.PROPERTY_RESULT_TIME);
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
     * @throws CodedException If an error occurs
     */
    public Map<String, TimePeriod> getTemporalBoundingBoxesForOfferings(final Session session)
            throws OwsExceptionReport {
        if (session != null) {
            Criteria criteria = getDaoFactory().getSeriesDAO().getDefaultSeriesCriteria(session);
            criteria.createCriteria(DatasetEntity.PROPERTY_OFFERING, "off");
            criteria.setProjection(
                    Projections.projectionList().add(Projections.min(DatasetEntity.PROPERTY_FIRST_VALUE_AT))
                            .add(Projections.max(DatasetEntity.PROPERTY_FIRST_VALUE_AT))
                            .add(Projections.max(DatasetEntity.PROPERTY_LAST_VALUE_AT))
                            .add(Projections.groupProperty("off." + OfferingEntity.IDENTIFIER)));
            LOGGER.debug("QUERY getTemporalBoundingBoxesForOfferings(): {}", HibernateHelper.getSqlString(criteria));
            final List<?> temporalBoundingBoxes = criteria.list();
            if (!temporalBoundingBoxes.isEmpty()) {
                final HashMap<String, TimePeriod> temporalBBoxMap = new HashMap<>(temporalBoundingBoxes.size());
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
        return new HashMap<>(0);
    }

    /**
     * Insert or update and get offering
     *
     * @param assignedOffering
     *            SosOffering to insert, update or get
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
    public OfferingEntity getAndUpdateOrInsert(SosOffering assignedOffering,
            Collection<RelatedFeatureEntity> relatedFeatures, Collection<FormatEntity> observationTypes,
            Collection<FormatEntity> featureOfInterestTypes, Session session) {
        OfferingEntity offering = getTOfferingForIdentifier(assignedOffering.getIdentifier(), session);
        if (offering == null) {
            offering = new OfferingEntity();
            offering.setIdentifier(assignedOffering.getIdentifier(), getDaoFactory().isStaSupportsUrls());
            if (assignedOffering.isSetName()) {
                offering.setName(assignedOffering.getFirstName().getValue());
            } else {
                offering.setName("Offering for the procedure " + assignedOffering.getIdentifier());
            }
            if (assignedOffering.isSetDescription()) {
                offering.setDescription(assignedOffering.getDescription());
            }
        }
        if (!relatedFeatures.isEmpty()) {
            offering.setRelatedFeatures(new HashSet<>(relatedFeatures));
        } else {
            offering.setRelatedFeatures(new HashSet<RelatedFeatureEntity>(0));
        }
        if (!observationTypes.isEmpty()) {
            offering.setObservationTypes(new HashSet<>(observationTypes));
        } else {
            offering.setObservationTypes(new HashSet<FormatEntity>(0));
        }
        if (!featureOfInterestTypes.isEmpty()) {
            offering.setFeatureTypes(new HashSet<>(featureOfInterestTypes));
        } else {
            offering.setFeatureTypes(new HashSet<FormatEntity>(0));
        }
        session.saveOrUpdate(offering);
        session.flush();
        session.refresh(offering);
        return offering;
    }

    public void updateParentOfferings(Set<String> parentOfferings, OfferingEntity hOffering, Session session) {
        for (String identifier : parentOfferings) {
            OfferingEntity offering = getOfferingForIdentifier(identifier, session);
            if (!offering.getChildren().contains(hOffering)) {
                offering.addChild(hOffering);
                session.saveOrUpdate(offering);
                session.flush();
                session.refresh(offering);
            }
        }
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
    private DetachedCriteria getDetachedCriteriaOfferingForObservablePropertyFromObservationConstellation(
            String observablePropertyIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DatasetEntity.class);
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.createCriteria(DatasetEntity.PROPERTY_PHENOMENON)
                .add(Restrictions.eq(PhenomenonEntity.IDENTIFIER, observablePropertyIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_OFFERING)));
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
    private DetachedCriteria getDetachedCriteriaOfferingForProcedureFromObservationConstellation(
            String procedureIdentifier, Session session) {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(DatasetEntity.class);
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false));
        detachedCriteria.createCriteria(DatasetEntity.PROPERTY_PROCEDURE)
                .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, procedureIdentifier));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_OFFERING)));
        return detachedCriteria;
    }

    /**
     * Query allowed FeatureOfInterestTypes for offering
     *
     * @param offeringIdentifier
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Allowed FeatureOfInterestTypes
     */
    public List<String> getAllowedFeatureOfInterestTypes(String offeringIdentifier, Session session) {
        if (HibernateHelper.isEntitySupported(OfferingEntity.class)) {
            Criteria criteria = getDefaultTransactionalCriteria(session)
                    .add(Restrictions.eq(OfferingEntity.IDENTIFIER, offeringIdentifier));
            LOGGER.debug("QUERY getAllowedFeatureOfInterestTypes(offering): {}",
                    HibernateHelper.getSqlString(criteria));
            OfferingEntity offering = (OfferingEntity) criteria.uniqueResult();
            if (offering != null) {
                List<String> list = Lists.newArrayList();
                for (FormatEntity featureOfInterestType : offering.getFeatureTypes()) {
                    list.add(featureOfInterestType.getFormat());
                }
                return list;
            }
        }
        return Lists.newArrayList();
    }

    /**
     * Add offering identifier restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param offering
     *            Offering identifier
     */
    public void addOfferingRestricionForObservation(Criteria c, String offering) {
        c.createCriteria(DataEntity.PROPERTY_DATASET).createCriteria(DatasetEntity.PROPERTY_OFFERING)
                .add(Restrictions.eq(OfferingEntity.IDENTIFIER, offering));
    }

    public void addOfferingRestricionForObservation(DetachedCriteria dc, String offering) {
        dc.createCriteria(DataEntity.PROPERTY_DATASET).createCriteria(DatasetEntity.PROPERTY_OFFERING)
                .add(Restrictions.eq(OfferingEntity.IDENTIFIER, offering));
    }

    public Map<String, Collection<String>> getOfferingIdentifiers(Session session) {
        Criteria criteria = getDefaultCriteria(session);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property(OfferingEntity.IDENTIFIER));
        criteria.createAlias(OfferingEntity.PROPERTY_PARENTS, "po", JoinType.LEFT_OUTER_JOIN);
        projectionList.add(Projections.property("po." + OfferingEntity.IDENTIFIER));
        criteria.setProjection(projectionList);
        // return as List<Object[]> even if there's only one column for
        // consistency
        criteria.setResultTransformer(NoopTransformerAdapter.INSTANCE);

        LOGGER.debug("QUERY getOfferingIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        @SuppressWarnings("unchecked")
        List<Object[]> results = criteria.list();
        Map<String, Collection<String>> map = Maps.newHashMap();
        for (Object[] result : results) {
            String offeringIdentifier = (String) result[0];
            String parentOfferingIdentifier = null;
            parentOfferingIdentifier = (String) result[1];
            if (parentOfferingIdentifier == null) {
                map.put(offeringIdentifier, null);
            } else {
                CollectionHelper.addToCollectionMap(offeringIdentifier, parentOfferingIdentifier, map);
            }
        }
        return map;
    }

    /**
     * Add offering identifier restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param offering
     *            Offering identifier
     */
    public void addOfferingRestricionForSeries(Criteria c, String offering) {
        addOfferingRestrictionFor(c, offering, DatasetEntity.PROPERTY_OFFERING);
    }

    private void addOfferingRestrictionFor(Criteria c, String offering, String associationPath) {
        c.createCriteria(associationPath).add(Restrictions.eq(OfferingEntity.IDENTIFIER, offering));
    }

    @SuppressWarnings("unchecked")
    public List<OfferingEntity> getPublishedOffering(Collection<String> identifiers, Session session)
            throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(DatasetEntity.class)) {
            Criteria c = getDefaultCriteria(session);
            c.add(Subqueries.propertyNotIn(OfferingEntity.PROPERTY_ID, getDetachedCriteriaSeries(session)));
            return c.list();
        }
        return getOfferingObjectsForCacheUpdate(identifiers, session);
    }

    private DetachedCriteria getDetachedCriteriaSeries(Session session) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria =
                DetachedCriteria.forClass(getDaoFactory().getSeriesDAO().getSeriesClass());
        detachedCriteria.add(Restrictions.disjunction(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, true),
                Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, false)));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_OFFERING)));
        return detachedCriteria;
    }

    protected Criteria getDefaultCriteria(Session session) {
        return session.createCriteria(OfferingEntity.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    protected Criteria getDefaultTransactionalCriteria(Session session) {
        return getDefaultCriteria(session);
    }

    public OfferingEntity updateOfferingMetadata(OfferingEntity offering, DataEntity<?> observation, Session session) {
        if (offering.getSamplingTimeStart() == null
                || (offering.getSamplingTimeStart() != null && observation.getSamplingTimeStart() != null
                        && offering.getSamplingTimeStart().after(observation.getSamplingTimeStart()))) {
            offering.setSamplingTimeStart(observation.getSamplingTimeStart());
        }
        if (offering.getSamplingTimeEnd() == null
                || (offering.getSamplingTimeEnd() != null && observation.getSamplingTimeEnd() != null
                        && offering.getSamplingTimeEnd().before(observation.getSamplingTimeEnd()))) {
            offering.setSamplingTimeEnd(observation.getSamplingTimeEnd());
        }
        if (offering.getResultTimeStart() == null
                || (offering.getResultTimeStart() != null && observation.getResultTime() != null
                        && offering.getResultTimeStart().after(observation.getResultTime()))) {
            offering.setResultTimeStart(observation.getResultTime());
        }
        if (offering.getResultTimeEnd() == null
                || (offering.getResultTimeEnd() != null && observation.getResultTime() != null
                        && offering.getResultTimeEnd().before(observation.getResultTime()))) {
            offering.setResultTimeEnd(observation.getResultTime());
        }
        if (offering.getValidTimeStart() == null
                || (offering.getValidTimeStart() != null && observation.getValidTimeStart() != null
                        && offering.getValidTimeStart().after(observation.getValidTimeStart()))) {
            offering.setValidTimeStart(observation.getValidTimeStart());
        }
        if (offering.getValidTimeEnd() == null
                || (offering.getValidTimeEnd() != null && observation.getValidTimeEnd() != null
                        && offering.getValidTimeEnd().before(observation.getValidTimeEnd()))) {
            offering.setValidTimeEnd(observation.getValidTimeEnd());
        }
        if (observation.isSetGeometryEntity()) {
            if (offering.isSetGeometry()) {
                offering.getGeometryEntity()
                        .union(observation.getGeometryEntity());
            } else {
                offering.setGeometryEntity(new GeometryEntity().copy(observation.getGeometryEntity()));
            }
        } else if (observation.getDataset().isSetFeature() && observation.getDataset().getFeature().isSetGeometry()) {
            if (offering.isSetGeometry()) {
                offering.getGeometryEntity()
                        .union(observation.getDataset().getFeature().getGeometryEntity());
            } else {
                offering.setGeometryEntity(new GeometryEntity().copy(observation.getDataset()
                        .getFeature()
                        .getGeometryEntity()));
            }
        }
        session.saveOrUpdate(offering);
        return offering;
    }

    public void updateAfterObservationDeletion(org.n52.series.db.beans.OfferingEntity offering,
            DataEntity<?> observation, Session session) {
        SeriesObservationDAO seriesObservationDAO = new SeriesObservationDAO(getDaoFactory());
        if (offering.hasSamplingTimeStart()
                && offering.getSamplingTimeStart().equals(observation.getSamplingTimeStart())) {
            DataEntity<?> firstDataEntity =
                    seriesObservationDAO.getFirstObservationFor(observation.getDataset(), session);
            if (firstDataEntity != null) {
                offering.setSamplingTimeStart(firstDataEntity.getSamplingTimeStart());
            }
        }
        if (offering.hasSamplingTimeEnd()
                && offering.getSamplingTimeEnd().equals(observation.getSamplingTimeEnd())) {
            DataEntity<?> latestDataEntity =
                    seriesObservationDAO.getLastObservationFor(observation.getDataset(), session);
            if (latestDataEntity != null) {
                offering.setSamplingTimeEnd(latestDataEntity.getSamplingTimeEnd());
            }
        }
    }

    public void delete(Collection<OfferingEntity> offerings, Session session) throws OwsExceptionReport {
        if (offerings != null && !offerings.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("delete ");
            builder.append(OfferingEntity.class.getSimpleName());
            builder.append(" where ").append(OfferingEntity.PROPERTY_ID).append(" in :")
                    .append(OfferingEntity.PROPERTY_ID);
            Query<?> q = session.createQuery(builder.toString());
            q.setParameter(OfferingEntity.PROPERTY_ID,
                    offerings.stream().map(OfferingEntity::getId).collect(Collectors.toSet()));
            int executeUpdate = q.executeUpdate();
            LOGGER.debug("{} offerings were physically deleted!", executeUpdate);
            session.flush();
        }
    }

    /**
     * Offering time extrema {@link ResultTransformer}
     *
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.4.0
     *
     */
    private static class OfferingeTimeTransformer implements ResultTransformer {
        private static final long serialVersionUID = -373512929481519459L;

        @Override
        public OfferingTimeExtrema transformTuple(Object[] tuple, String[] aliases) {
            OfferingTimeExtrema offeringTimeExtrema = new OfferingTimeExtrema();
            if (tuple != null) {
                offeringTimeExtrema.setOffering(tuple[0].toString());
                offeringTimeExtrema.setMinPhenomenonTime(DateTimeHelper.makeDateTime(tuple[1]));
                if (tuple.length == 7) {
                    DateTime maxPhenStart = DateTimeHelper.makeDateTime(tuple[2]);
                    DateTime maxPhenEnd = DateTimeHelper.makeDateTime(tuple[3]);
                    offeringTimeExtrema.setMinPhenomenonTime(DateTimeHelper.max(maxPhenStart, maxPhenEnd));
                    offeringTimeExtrema.setMinResultTime(DateTimeHelper.makeDateTime(tuple[4]));
                    offeringTimeExtrema.setMaxResultTime(DateTimeHelper.makeDateTime(tuple[5]));
                    offeringTimeExtrema.setEnvelope((Geometry) tuple[6]);
                } else {
                    offeringTimeExtrema.setMinPhenomenonTime(DateTimeHelper.makeDateTime(tuple[2]));
                    offeringTimeExtrema.setMinResultTime(DateTimeHelper.makeDateTime(tuple[3]));
                    offeringTimeExtrema.setMaxResultTime(DateTimeHelper.makeDateTime(tuple[4]));
                }
            }
            return offeringTimeExtrema;
        }

        @Override
        @SuppressWarnings({ "rawtypes" })
        public List transformList(List collection) {
            return collection;
        }
    }

}
