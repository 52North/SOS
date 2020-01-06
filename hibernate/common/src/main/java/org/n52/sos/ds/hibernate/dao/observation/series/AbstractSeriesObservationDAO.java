/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.dao.observation.series;

import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.hibernate.transform.ResultTransformer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.AbstractTemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.ContextualReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.TemporalReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateGeometryCreator;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.n52.sos.ds.hibernate.util.observation.ExtensionFesFilterCriteriaAdder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractSeriesObservationDAO extends AbstractObservationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeriesObservationDAO.class);
    private final SeriesTimeTransformer transformer = new SeriesTimeTransformer();
    
    @Override
    protected ObservationContext createObservationContext() {
        return new SeriesObservationContext();
    }
    
    @Override
    protected void addObservationContextToObservation(ObservationContext ctx,
            Observation<?> observation, Session session) throws CodedException {
        AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();
        Series series = seriesDAO.getOrInsertSeries(ctx, session);
        ((AbstractSeriesObservation) observation).setSeries(series);
        seriesDAO.updateSeriesWithFirstLatestValues(series, observation, session);
    }

    @Override
    public Criteria getObservationInfoCriteriaForFeatureOfInterestAndProcedure(String feature, String procedure,
            Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(ContextualReferencedSeriesObservation.SERIES);
        seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        seriesCriteria.createCriteria(Series.PROCEDURE).add(eq(Procedure.IDENTIFIER, procedure));

        if (!isIncludeChildObservableProperties()) {
            seriesCriteria.createCriteria(AbstractObservation.VALUE).createCriteria(AbstractObservation.OBS_ID);
        }

        return criteria;
    }

    @Override
    public Criteria getObservationInfoCriteriaForFeatureOfInterestAndOffering(String feature, String offering,
            Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(ContextualReferencedSeriesObservation.SERIES);
        seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        seriesCriteria.createCriteria(Series.OFFERING).add(eq(Offering.IDENTIFIER, offering));
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaForProcedure(String procedure, Session session) throws CodedException {
        AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesDAO.addProcedureToCriteria(seriesCriteria, procedure);
        return criteria;
    }

    private Criteria getDefaultSeriesObservationCriteria(Criteria criteria) {
        Criteria seriesCriteria = criteria.createCriteria(SeriesObservation.SERIES);
        seriesCriteria.add(Restrictions.eq(Series.PUBLISHED, true));
        return seriesCriteria;
    }

    @Override
    public Criteria getObservationCriteriaForObservableProperty(String observableProperty, Session session)
            throws CodedException {
        AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesDAO.addObservablePropertyToCriteria(seriesCriteria, observableProperty);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaForFeatureOfInterest(String featureOfInterest, Session session)
            throws CodedException {
        AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesDAO.addFeatureOfInterestToCriteria(seriesCriteria, featureOfInterest);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaFor(String procedure, String observableProperty, Session session)
            throws CodedException {
        AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesDAO.addProcedureToCriteria(seriesCriteria, procedure);
        seriesDAO.addObservablePropertyToCriteria(seriesCriteria, observableProperty);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaFor(String procedure, String observableProperty, String featureOfInterest,
            Session session) throws CodedException {
        Criteria criteria = getDefaultObservationCriteria(session);
        addRestrictionsToCriteria(criteria, procedure, observableProperty, featureOfInterest);
        return criteria;
    }
    
    @Override
    public Criteria getTemoralReferencedObservationCriteriaFor(OmObservation observation, ObservationConstellation oc,
            Session session) throws CodedException {
        Criteria criteria = getDefaultObservationTimeCriteria(session);
        Criteria seriesCriteria = addRestrictionsToCriteria(criteria, oc.getProcedure().getIdentifier(),
                oc.getObservableProperty().getIdentifier(),
                observation.getObservationConstellation().getFeatureOfInterestIdentifier(),
                oc.getOffering().getIdentifier());
        addAdditionalObservationIdentification(seriesCriteria, observation);
        return criteria;
    }
    
    /**
     * Add restirction to {@link Criteria
     * 
     * @param criteria
     *            Main {@link Criteria}
     * @param procedure
     *            The procedure restriction
     * @param observableProperty
     *            The observableProperty restriction
     * @param featureOfInterest
     *            The featureOfInterest restriction
     * @return The created series {@link Criteria}
     * @throws CodedException
     *             If an erro occurs
     */
    private Criteria addRestrictionsToCriteria(Criteria criteria, String procedure, String observableProperty,
            String featureOfInterest) throws CodedException {
        AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesDAO.addFeatureOfInterestToCriteria(seriesCriteria, featureOfInterest);
        seriesDAO.addProcedureToCriteria(seriesCriteria, procedure);
        seriesDAO.addObservablePropertyToCriteria(seriesCriteria, observableProperty);
        return seriesCriteria;
    }
    
    private Criteria addRestrictionsToCriteria(Criteria criteria, String procedure, String observableProperty,
            String featureOfInterest, String offering) throws CodedException {
        AbstractSeriesDAO seriesDAO = DaoFactory.getInstance().getSeriesDAO();
        Criteria seriesCriteria = criteria.createCriteria(AbstractSeriesObservation.SERIES);
        seriesDAO.addFeatureOfInterestToCriteria(seriesCriteria, featureOfInterest);
        seriesDAO.addProcedureToCriteria(seriesCriteria, procedure);
        seriesDAO.addObservablePropertyToCriteria(seriesCriteria, observableProperty);
        seriesDAO.addOfferingToCriteria(seriesCriteria, offering);
        return seriesCriteria;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<String> getObservationIdentifiers(String procedureIdentifier, Session session) {
        Criteria criteria =
                getDefaultObservationInfoCriteria(session)
                        .setProjection(Projections.distinct(Projections.property(ContextualReferencedSeriesObservation.IDENTIFIER)))
                        .add(Restrictions.isNotNull(ContextualReferencedSeriesObservation.IDENTIFIER))
                        .add(Restrictions.eq(ContextualReferencedSeriesObservation.DELETED, false));
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesCriteria.createCriteria(Series.PROCEDURE)
                .add(Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
        LOGGER.debug("QUERY getObservationIdentifiers(procedureIdentifier): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }
    
    @Override
    public ScrollableResults getObservations(Set<String> procedure, Set<String> observableProperty,
            Set<String> featureOfInterest, Set<String> offering, Criterion filterCriterion, Session session) {
        Criteria c = getDefaultObservationCriteria(session);
        String seriesAliasPrefix = createSeriesAliasAndRestrictions(c);
        if (CollectionHelper.isNotEmpty(procedure)) {
            c.createCriteria(seriesAliasPrefix + Series.PROCEDURE).add(Restrictions.in(Procedure.IDENTIFIER, procedure));
        }
        
        if (CollectionHelper.isNotEmpty(observableProperty)) {
            c.createCriteria(seriesAliasPrefix + Series.OBSERVABLE_PROPERTY).add(Restrictions.in(ObservableProperty.IDENTIFIER,
                    observableProperty));
        }
        
        if (CollectionHelper.isNotEmpty(featureOfInterest)) {
            c.createCriteria(seriesAliasPrefix + Series.FEATURE_OF_INTEREST).add(Restrictions.in(FeatureOfInterest.IDENTIFIER, featureOfInterest));
        }
        
        if (CollectionHelper.isNotEmpty(offering)) {
            c.createCriteria(seriesAliasPrefix + Series.OFFERING).add(Restrictions.in(Offering.IDENTIFIER, offering));
        }
        String logArgs = "request, features, offerings";
        if (filterCriterion != null) {
            logArgs += ", filterCriterion";
            c.add(filterCriterion);
        }
        LOGGER.debug("QUERY getObservations({}): {}", logArgs, HibernateHelper.getSqlString(c));
        
        return c.scroll(ScrollMode.FORWARD_ONLY);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Geometry> getSamplingGeometries(String feature, Session session) throws OwsExceptionReport {
        Criteria criteria = getDefaultObservationTimeCriteria(session).createAlias(AbstractSeriesObservation.SERIES, "s");
        criteria.createCriteria("s." + Series.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        criteria.addOrder(Order.asc(AbstractTemporalReferencedObservation.PHENOMENON_TIME_START));
        if (HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY)) {
            criteria.add(Restrictions.isNotNull(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            criteria.setProjection(Projections.property(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            LOGGER.debug("QUERY getSamplingGeometries(feature): {}", HibernateHelper.getSqlString(criteria));
            return criteria.list();
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), AbstractTemporalReferencedObservation.LONGITUDE)
                && HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), AbstractTemporalReferencedObservation.LATITUDE)) {
            criteria.add(Restrictions.and(Restrictions.isNotNull(AbstractTemporalReferencedObservation.LATITUDE),
                    Restrictions.isNotNull(AbstractTemporalReferencedObservation.LONGITUDE)));
            List<Geometry> samplingGeometries = Lists.newArrayList();
            LOGGER.debug("QUERY getSamplingGeometries(feature): {}", HibernateHelper.getSqlString(criteria));
            for (AbstractTemporalReferencedObservation element : (List<AbstractTemporalReferencedObservation>)criteria.list()) {
                samplingGeometries.add(new HibernateGeometryCreator().createGeometry(element));
            }
            return samplingGeometries;
        }
        return Collections.emptyList();
    }

    @Override
    public Long getSamplingGeometriesCount(String feature, Session session) throws OwsExceptionReport {
        Criteria criteria = getDefaultObservationTimeCriteria(session).createAlias(SeriesObservation.SERIES, "s");
        criteria.createCriteria("s." + Series.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        criteria.setProjection(Projections.count(AbstractTemporalReferencedObservation.OBS_ID));
        if (GeometryHandler.getInstance().isSpatialDatasource()) {
            criteria.add(Restrictions.isNotNull(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            LOGGER.debug("QUERY getSamplingGeometriesCount(feature): {}", HibernateHelper.getSqlString(criteria));
            return (Long)criteria.uniqueResult();
        } else {
            criteria.add(Restrictions.and(Restrictions.isNotNull(AbstractTemporalReferencedObservation.LATITUDE),
                    Restrictions.isNotNull(AbstractTemporalReferencedObservation.LONGITUDE)));
            LOGGER.debug("QUERY getSamplingGeometriesCount(feature): {}", HibernateHelper.getSqlString(criteria));
            return (Long)criteria.uniqueResult();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Envelope getBboxFromSamplingGeometries(String feature, Session session) throws OwsExceptionReport {
        Criteria criteria = getDefaultObservationTimeCriteria(session).createAlias(SeriesObservation.SERIES, "s");
        criteria.createCriteria("s." + Series.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        if (GeometryHandler.getInstance().isSpatialDatasource()) {
            criteria.add(Restrictions.isNotNull(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
            if (HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                criteria.setProjection(SpatialProjections.extent(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
                LOGGER.debug("QUERY getBboxFromSamplingGeometries(feature): {}",
                        HibernateHelper.getSqlString(criteria));
                return (Envelope) criteria.uniqueResult();
            }
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().temporalReferencedClass(), AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY)) {
            criteria.add(Restrictions.isNotNull(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            criteria.setProjection(Projections.property(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            LOGGER.debug("QUERY getBboxFromSamplingGeometries(feature): {}",
                    HibernateHelper.getSqlString(criteria));
            Envelope envelope = new Envelope();
            for (Geometry geom : (List<Geometry>) criteria.list()) {
                envelope.expandToInclude(geom.getEnvelopeInternal());
            }
            return envelope;
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().temporalReferencedClass(), AbstractTemporalReferencedObservation.LATITUDE)
                && HibernateHelper.isColumnSupported(getObservationFactory().temporalReferencedClass(), AbstractTemporalReferencedObservation.LONGITUDE)) {
            criteria.add(Restrictions.and(Restrictions.isNotNull(AbstractTemporalReferencedObservation.LATITUDE),
                    Restrictions.isNotNull(AbstractTemporalReferencedObservation.LONGITUDE)));
            criteria.setProjection(Projections.projectionList().add(Projections.min(AbstractTemporalReferencedObservation.LATITUDE))
                    .add(Projections.min(AbstractTemporalReferencedObservation.LONGITUDE))
                    .add(Projections.max(AbstractTemporalReferencedObservation.LATITUDE))
                    .add(Projections.max(AbstractTemporalReferencedObservation.LONGITUDE)));

            LOGGER.debug("QUERY getBboxFromSamplingGeometries(feature): {}", HibernateHelper.getSqlString(criteria));
            MinMaxLatLon minMaxLatLon = new MinMaxLatLon((Object[]) criteria.uniqueResult());
            Envelope envelope = new Envelope(minMaxLatLon.getMinLon(), minMaxLatLon.getMaxLon(),
                    minMaxLatLon.getMinLat(), minMaxLatLon.getMaxLat());
            return envelope;
        }
        return null;
    }
    
    /**
     * Create series observation query criteria for series and offerings
     *
     * @param clazz
     *            Class to query
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param session
     *            Hibernate session
     * @return Criteria to query series observations
     */
    protected Criteria createCriteriaFor(Class<?> clazz, Series series, List<String> offerings, Session session) {
        final Criteria criteria = createCriteriaFor(clazz, series, session);
//        if (CollectionHelper.isNotEmpty(offerings)) {
//            criteria.createCriteria(AbstractSeriesObservation.OFFERINGS).add(Restrictions.in(Offering.IDENTIFIER, offerings));
//        }
        return criteria;
    }

    /**
     * Create series observation query criteria for series
     *
     * @param clazz
     *            to query
     * @param series
     *            Series to get values for
     * @param session
     *            Hibernate session
     * @return Criteria to query series observations
     */
    protected Criteria createCriteriaFor(Class<?> clazz, Series series, Session session) {
        final Criteria criteria = getDefaultObservationCriteria(session);
        criteria.createCriteria(AbstractSeriesObservation.SERIES)
                .add(Restrictions.eq(Series.ID, series.getSeriesId()))
                .add(Restrictions.eq(Series.PUBLISHED, true));
        return criteria;
    }

    /**
     * Get the result times for this series, offerings and filters
     *
     * @param series
     *            Time series to get result times for
     * @param offerings
     *            Offerings to restrict matching result times
     * @param filter
     *            Temporal filter to restrict matching result times
     * @param session
     *            Hibernate session
     * @return Matching result times
     */
    @SuppressWarnings("unchecked")
    public List<Date> getResultTimesForSeriesObservation(Series series, List<String> offerings, Criterion filter,
            Session session) {
        Criteria criteria = createCriteriaFor(getObservationFactory().temporalReferencedClass(), series, session);
//        if (CollectionHelper.isNotEmpty(offerings)) {
//            criteria.createCriteria(TemporalReferencedSeriesObservation.OFFERINGS)
//                    .add(Restrictions.in(Offering.IDENTIFIER, offerings));
//        }
        if (filter != null) {
            criteria.add(filter);
        }
        criteria.setProjection(Projections.distinct(Projections.property(TemporalReferencedSeriesObservation.RESULT_TIME)));
        criteria.addOrder(Order.asc(TemporalReferencedSeriesObservation.RESULT_TIME));
        LOGGER.debug("QUERY getResultTimesForSeriesObservation({}): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Create criteria to query min/max time for series from series observation
     *
     * @param series
     *            Series to get values for
     * @param offerings
     * @param session
     *            Hibernate session
     * @return Criteria to get min/max time values for series
     */
    public Criteria getMinMaxTimeCriteriaForSeriesObservation(Series series, Collection<String> offerings,
            Session session) {
        Criteria criteria = createCriteriaFor(getObservationFactory().temporalReferencedClass(), series, session);
//        if (CollectionHelper.isNotEmpty(offerings)) {
//            criteria.createCriteria(TemporalReferencedSeriesObservation.OFFERINGS).add(
//                    Restrictions.in(Offering.IDENTIFIER, offerings));
//        }
        criteria.setProjection(Projections.projectionList()
                .add(Projections.min(TemporalReferencedSeriesObservation.PHENOMENON_TIME_START))
                .add(Projections.max(TemporalReferencedSeriesObservation.PHENOMENON_TIME_END)));
        return criteria;
    }
    
    /**
     * Create criteria to query min/max time of each offering for series from series observation
     *
     * @param series
     *            Series to get values for
     * @param offerings
     * @param session
     *            Hibernate session
     * @return Criteria to get min/max time values for series
     */
    public Criteria getOfferingMinMaxTimeCriteriaForSeriesObservation(Series series, Collection<String> offerings,
            Session session) {
        Criteria criteria = createCriteriaFor(getObservationFactory().temporalReferencedClass(), series, session);
//        if (CollectionHelper.isNotEmpty(offerings)) {
//            criteria.createCriteria(TemporalReferencedSeriesObservation.OFFERINGS, "off").add(
//                    Restrictions.in(Offering.IDENTIFIER, offerings));
//        } else {
//            criteria.createAlias(AbstractObservation.OFFERINGS, "off");
//        }
        criteria.setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("off." + Offering.IDENTIFIER))
                        .add(Projections.min(TemporalReferencedSeriesObservation.PHENOMENON_TIME_START))
                        .add(Projections.max(TemporalReferencedSeriesObservation.PHENOMENON_TIME_END)));
        return criteria;
    }

    public ScrollableResults getSeriesNotMatchingSeries(Set<Long> seriesIDs, GetObservationRequest request,
            Set<String> features, Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        Criteria c = getScrollableSeriesObservationCriteriaFor(request, features, temporalFilterCriterion, null, session);
        c.createCriteria(AbstractSeriesObservation.SERIES)
            .add(Restrictions.not(Restrictions.in(Series.ID, seriesIDs)));
        c.setProjection(Projections.property(AbstractSeriesObservation.SERIES));
        return c.setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
    }

    public ScrollableResults getSeriesNotMatchingSeries(Set<Long> seriesIDs, GetObservationRequest request,
            Set<String> features, Session session) throws OwsExceptionReport {
        return getSeriesNotMatchingSeries(seriesIDs, request, features, null, session);
    }

    /**
     * Create series observations {@link Criteria} for GetObservation request,
     * features, and filter criterion (typically a temporal filter) or an
     * indeterminate time (first/latest). This method is private and accepts all
     * possible arguments for request-based getSeriesObservationFor. Other
     * public methods overload this method with sensible combinations of
     * arguments.
     *
     * @param request
     *            GetObservation request
     * @param features
     *              Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param sosIndeterminateTime
     *            Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     * @return Series observations {@link Criteria}
     * @throws OwsExceptionReport
     */
    protected List<SeriesObservation<?>> getSeriesObservationCriteriaFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session)
                    throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            List<SeriesObservation<?>> list = new LinkedList<>();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions.getSubQueryIdentifier(getResultFilterClasses())) {
                String logArgs = new String(identifier + ",");
                Criteria c = getDefaultSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session, logArgs);
                checkAndAddResultFilterCriterion(c, request, identifier, session);
                LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs,
                        HibernateHelper.getSqlString(c));
                list.addAll(c.list());
            }
            return list;
        }
        String logArgs = new String();
        Criteria c = getDefaultSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session, logArgs);
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs,
                HibernateHelper.getSqlString(c));
        return c.list();
    }
    
    private Criteria getDefaultSeriesObservationCriteriaFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session, String logArgs)
                    throws OwsExceptionReport {
        final Criteria observationCriteria = getDefaultObservationCriteria(session);

        Criteria seriesCriteria = observationCriteria.createCriteria(AbstractSeriesObservation.SERIES);

        checkAndAddSpatialFilteringProfileCriterion(observationCriteria, request, session);
        
        addSpecificRestrictions(seriesCriteria, request);
        if (CollectionHelper.isNotEmpty(request.getProcedures())) {
            seriesCriteria.createCriteria(Series.PROCEDURE)
                    .add(Restrictions.in(Procedure.IDENTIFIER, request.getProcedures()));
        }

        if (CollectionHelper.isNotEmpty(request.getObservedProperties())) {
            seriesCriteria.createCriteria(Series.OBSERVABLE_PROPERTY)
                    .add(Restrictions.in(ObservableProperty.IDENTIFIER, request.getObservedProperties()));
        }

        if (CollectionHelper.isNotEmpty(features)) {
            seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST)
                    .add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
        }

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            seriesCriteria.createCriteria(Series.OFFERING)
            .add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
//            observationCriteria.createCriteria(AbstractSeriesObservation.OFFERINGS)
//                    .add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }

        logArgs = logArgs != null ? logArgs + "request, features, offerings" : new String("request, features, offerings");
        if (filterCriterion != null) {
            logArgs += ", filterCriterion";
            observationCriteria.add(filterCriterion);
        }
        if (sosIndeterminateTime != null) {
            logArgs += ", sosIndeterminateTime";
            addIndeterminateTimeRestriction(observationCriteria, sosIndeterminateTime);
        }
        if (request.isSetFesFilterExtension()) {
            new ExtensionFesFilterCriteriaAdder(observationCriteria, request.getFesFilterExtensions()).add();
        }
        return observationCriteria;
    }
    
    protected Criteria getScrollableSeriesObservationCriteriaFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session)
                    throws OwsExceptionReport {
        final Criteria observationCriteria = getDefaultObservationCriteria(session);

        Criteria seriesCriteria = observationCriteria.createCriteria(AbstractSeriesObservation.SERIES);

        checkAndAddSpatialFilteringProfileCriterion(observationCriteria, request, session);
        checkAndAddResultFilterCriterion(observationCriteria, request, null, session);
        
        addSpecificRestrictions(seriesCriteria, request);
        if (CollectionHelper.isNotEmpty(request.getProcedures())) {
            seriesCriteria.createCriteria(Series.PROCEDURE)
                    .add(Restrictions.in(Procedure.IDENTIFIER, request.getProcedures()));
        }

        if (CollectionHelper.isNotEmpty(request.getObservedProperties())) {
            seriesCriteria.createCriteria(Series.OBSERVABLE_PROPERTY)
                    .add(Restrictions.in(ObservableProperty.IDENTIFIER, request.getObservedProperties()));
        }

        if (CollectionHelper.isNotEmpty(features)) {
            seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST)
                    .add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
        }

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            seriesCriteria.createCriteria(Series.OFFERING)
            .add(Restrictions.in(Offering.IDENTIFIER,  request.getOfferings()));
//            observationCriteria.createCriteria(AbstractSeriesObservation.OFFERINGS)
//                    .add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }

        String logArgs = "request, features, offerings";
        if (filterCriterion != null) {
            logArgs += ", filterCriterion";
            observationCriteria.add(filterCriterion);
        }
        if (sosIndeterminateTime != null) {
            logArgs += ", sosIndeterminateTime";
            addIndeterminateTimeRestriction(observationCriteria, sosIndeterminateTime);
        }
        if (request.isSetFesFilterExtension()) {
            new ExtensionFesFilterCriteriaAdder(observationCriteria, request.getFesFilterExtensions()).add();
        }
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs,
                HibernateHelper.getSqlString(observationCriteria));
        return observationCriteria;
    }

    private String createSeriesAliasAndRestrictions(Criteria c) {
        String alias = "s";
        String aliasWithDot = alias + ".";
        c.createAlias(SeriesObservation.SERIES, alias);
        c.add(Restrictions.eq(aliasWithDot + Series.DELETED, false));
        c.add(Restrictions.eq(aliasWithDot + Series.PUBLISHED, true));
        return aliasWithDot;
    }

    /**
     * Query series observations {@link ScrollableResults} for GetObservation
     * request and features
     *
     * @param request
     *              GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param session
     *            Hibernate session
     * @return {@link ScrollableResults} of Series observations that fit
     * @throws OwsExceptionReport
     */
    public ScrollableResults getStreamingSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Session session) throws OwsExceptionReport {
        return getStreamingSeriesObservationsFor(request, features, null, null, session);
    }

    /**
     * Query series observations {@link ScrollableResults} for GetObservation
     * request, features, and a filter criterion (typically a temporal filter)
     *
     * @param request
     *              GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param session
     *            Hibernate session
     * @return {@link ScrollableResults} of Series observations that fit
     * @throws OwsExceptionReport
     */
    public ScrollableResults getStreamingSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Criterion filterCriterion, Session session) throws OwsExceptionReport {
        return getStreamingSeriesObservationsFor(request, features, filterCriterion, null, session);
    }

    /**
     * Query series observations for GetObservation request, features, and
     * filter criterion (typically a temporal filter) or an indeterminate time
     * (first/latest). This method is private and accepts all possible arguments
     * for request-based getSeriesObservationFor. Other public methods overload
     * this method with sensible combinations of arguments.
     *
     * @param request
     *            GetObservation request
     * @param features
     *              Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param sosIndeterminateTime
     *            Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     * @return {@link ScrollableResults} of Series observations that fits
     * @throws OwsExceptionReport
     */
    protected ScrollableResults getStreamingSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime,
            Session session) throws OwsExceptionReport {
        return getScrollableSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session)
                .setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
    }

    /**
     * Update series observation by setting deleted flag
     *
     * @param series
     *            Series for which the observations should be updated
     * @param deleteFlag
     *            New deleted flag value
     * @param session
     *            Hibernate Session
     */
    public void updateObservationSetAsDeletedForSeries(List<Series> series, boolean deleteFlag, Session session) {
        if (CollectionHelper.isNotEmpty(series)) {
            Criteria criteria = getDefaultObservationCriteria(session);
            criteria.add(Restrictions.in(SeriesObservation.SERIES, series));
            ScrollableIterable<SeriesObservation<?>> scroll = ScrollableIterable.fromCriteria(criteria);
            updateObservation(scroll, deleteFlag, session);
        }
    }

    /**
     * Query the min time from series observations for series
     *
     * @param series
     *            Series to get values for
     * @param session
     *            Hibernate session
     * @return Min time from series observations
     */
    public DateTime getMinSeriesObservationTime(Series series, Session session) {
        Criteria criteria = createCriteriaFor(getObservationFactory().temporalReferencedClass(), series, session);
        criteria.setProjection(Projections.min(TemporalReferencedSeriesObservation.PHENOMENON_TIME_START));
        Object min = criteria.uniqueResult();
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Query the max time from series observations for series
     *
     * @param series
     *            Series to get values for
     * @param session
     *            Hibernate session
     * @return Max time from series observations
     */
    public DateTime getMaxSeriesObservationTime(Series series, Session session) {
        Criteria criteria = createCriteriaFor(getObservationFactory().temporalReferencedClass(), series, session);
        criteria.setProjection(Projections.max(TemporalReferencedSeriesObservation.PHENOMENON_TIME_END));
        Object max = criteria.uniqueResult();
        if (max != null) {
            return new DateTime(max, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Query series observation for series and offerings
     *
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    public abstract List<SeriesObservation<?>> getSeriesObservationFor(Series series, List<String> offerings, Session session);

    /**
     * Query series obserations for series, temporal filter, and offerings
     *
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param filterCriterion
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    public abstract List<SeriesObservation<?>> getSeriesObservationFor(Series series, List<String> offerings, Criterion filterCriterion, Session session);

    /**
     * Query first/latest series obserations for series (and offerings)
     *
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param sosIndeterminateTime
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    public abstract List<SeriesObservation<?>> getSeriesObservationForSosIndeterminateTimeFilter(Series series, List<String> offerings, SosIndeterminateTime sosIndeterminateTime, Session session);

    /**
     * Query series observations for GetObservation request and features
     *
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    public abstract List<SeriesObservation<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features, Session session) throws OwsExceptionReport;

    /**
     * Query series observations for GetObservation request, features, and a
     * filter criterion (typically a temporal filter)
     *
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    public abstract List<SeriesObservation<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features, Criterion filterCriterion, Session session) throws OwsExceptionReport;

    /**
     * Query series observations for GetObservation request, features, and an
     * indeterminate time (first/latest)
     *
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param sosIndeterminateTime
     *            Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    public abstract List<SeriesObservation<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features, SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport;

    /**
     * Query series observations for GetObservation request, features, and
     * filter criterion (typically a temporal filter) or an indeterminate time
     * (first/latest). This method is private and accepts all possible arguments
     * for request-based getSeriesObservationFor. Other public methods overload
     * this method with sensible combinations of arguments.
     *
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param sosIndeterminateTime
     *            Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    protected abstract List<? extends SeriesObservation<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features, Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport;


    public abstract List<SeriesObservation<?>> getSeriesObservationsFor(Series series, GetObservationRequest request, SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport;

    protected abstract void addSpecificRestrictions(Criteria c, GetObservationRequest request) throws CodedException;

    protected List<SeriesObservation<?>> getSeriesObservationCriteriaFor(Series series, GetObservationRequest request,
            SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            List<SeriesObservation<?>> list = new LinkedList<>();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions.getSubQueryIdentifier(getResultFilterClasses())) {
                final Criteria c =
                        getDefaultObservationCriteria(session).add(
                                Restrictions.eq(AbstractSeriesObservation.SERIES, series));
                checkAndAddSpatialFilteringProfileCriterion(c, request, session);
                checkAndAddResultFilterCriterion(c, request, identifier, session);

//                if (request.isSetOffering()) {
//                    c.createCriteria(AbstractSeriesObservation.OFFERINGS).add(
//                            Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
//                }
                String logArgs = "request, features, offerings";
                logArgs += ", sosIndeterminateTime";
                if (series.isSetFirstTimeStamp() && sosIndeterminateTime.equals(SosIndeterminateTime.first)) {
                    addIndeterminateTimeRestriction(c, sosIndeterminateTime, series.getFirstTimeStamp());
                } else if (series.isSetLastTimeStamp() && sosIndeterminateTime.equals(SosIndeterminateTime.latest)) {
                    addIndeterminateTimeRestriction(c, sosIndeterminateTime, series.getLastTimeStamp());
                } else {
                    addIndeterminateTimeRestriction(c, sosIndeterminateTime);
                }
                LOGGER.debug("QUERY getSeriesObservationFor({}) and result filter sub query '{}': {}", logArgs, identifier.name(), HibernateHelper.getSqlString(c));
                list.addAll(c.list());
            }
            return list;
        }
        final Criteria c =
                getDefaultObservationCriteria(session).add(
                        Restrictions.eq(AbstractSeriesObservation.SERIES, series));
        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

//        if (request.isSetOffering()) {
//            c.createCriteria(AbstractSeriesObservation.OFFERINGS).add(
//                    Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
//        }
        String logArgs = "request, features, offerings";
        logArgs += ", sosIndeterminateTime";
        if (series.isSetFirstTimeStamp() && sosIndeterminateTime.equals(SosIndeterminateTime.first)) {
            addIndeterminateTimeRestriction(c, sosIndeterminateTime, series.getFirstTimeStamp());
        } else if (series.isSetLastTimeStamp() && sosIndeterminateTime.equals(SosIndeterminateTime.latest)) {
            addIndeterminateTimeRestriction(c, sosIndeterminateTime, series.getLastTimeStamp());
        } else {
            addIndeterminateTimeRestriction(c, sosIndeterminateTime);
        }
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c.list();

    }

    protected Criteria getSeriesObservationCriteriaForSosIndeterminateTimeFilter(Series series,
            List<String> offerings, SosIndeterminateTime sosIndeterminateTime, Session session) {
        final Criteria criteria = createCriteriaFor(getObservationFactory().observationClass(), series, offerings, session);
        criteria.addOrder(getOrder(sosIndeterminateTime)).setMaxResults(1);
        LOGGER.debug("QUERY getSeriesObservationForSosIndeterminateTimeFilter(series, offerings,(first,latest)): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria;
    }

    protected Criteria getSeriesObservationCriteriaFor(Series series, List<String> offerings,
            Criterion filterCriterion, Session session) {
        final Criteria criteria = createCriteriaFor(getObservationFactory().observationClass(), series, offerings, session);
        criteria.add(filterCriterion);
        LOGGER.debug("QUERY getSeriesObservationFor(series, offerings, temporalFilter): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria;
    }

    protected Criteria getSeriesObservationCriteriaFor(Series series, List<String> offerings,
            Session session) {
        final Criteria criteria = createCriteriaFor(AbstractSeriesObservation.class, series, offerings, session);
        LOGGER.debug("QUERY getSeriesObservationFor(series, offerings): {}", HibernateHelper.getSqlString(criteria));
        return criteria;
    }

    /**
     * Get the first not deleted observation for the {@link Series}
     * 
     * @param series
     *            Series to get observation for
     * @param session
     *            Hibernate session
     * @return First not deleted observation
     */
    @SuppressWarnings("rawtypes")
    public SeriesObservation getFirstObservationFor(Series series, Session session) {
        Criteria c = getDefaultObservationCriteria(session);
        c.add(Restrictions.eq(SeriesObservation.SERIES, series));
        c.addOrder(Order.asc(AbstractObservation.PHENOMENON_TIME_START));
        c.setMaxResults(1);
        LOGGER.debug("QUERY getFirstObservationFor(series): {}", HibernateHelper.getSqlString(c));
        return (SeriesObservation) c.uniqueResult();
    }

    /**
     * Get the last not deleted observation for the {@link Series}
     * 
     * @param series
     *            Series to get observation for
     * @param session
     *            Hibernate session
     * @return Last not deleted observation
     */
    @SuppressWarnings("rawtypes")
    public SeriesObservation getLastObservationFor(Series series, Session session) {
        Criteria c = getDefaultObservationCriteria(session);
        c.add(Restrictions.eq(SeriesObservation.SERIES, series));
        c.addOrder(Order.desc(AbstractObservation.PHENOMENON_TIME_END));
        c.setMaxResults(1);
        LOGGER.debug("QUERY getLastObservationFor(series): {}", HibernateHelper.getSqlString(c));
        return (SeriesObservation) c.uniqueResult();
    }
    
    @Override
    public String addProcedureAlias(Criteria criteria) {
        criteria.createAlias(SeriesObservation.SERIES, Series.ALIAS);
        criteria.createAlias(Series.ALIAS_DOT + Series.PROCEDURE, Procedure.ALIAS);
        return Procedure.ALIAS_DOT;
    }

    @SuppressWarnings("unchecked")
    public List<String> getOfferingsForSeries(Series series, Session session) {
//        Criteria criteria = createCriteriaFor(getObservationFactory().temporalReferencedClass(), series, session);
//        criteria.createAlias(AbstractObservation.OFFERINGS, "off");
//        criteria.setProjection(Projections.distinct(Projections.property("off." + Offering.IDENTIFIER)));
//        LOGGER.debug("QUERY getOfferingsForSeries(series): {}", HibernateHelper.getSqlString(criteria));
        return Lists.newArrayList(series.getOffering().getIdentifier());
    }

    public Map<Long, SeriesTimeExtrema> getMinMaxSeriesTimes(Set<Series> serieses, Session session) {
        Criteria c = getDefaultObservationTimeCriteria(session);
        c.add(Restrictions.in(SeriesObservation.SERIES, serieses));
        c.setProjection(Projections.projectionList()
                .add(Projections.groupProperty(SeriesObservation.SERIES))
                .add(Projections.min(TemporalReferencedSeriesObservation.PHENOMENON_TIME_START))
                .add(Projections.max(TemporalReferencedSeriesObservation.PHENOMENON_TIME_END)));
        c.setResultTransformer(transformer);
        
        Map<Long, SeriesTimeExtrema> map = Maps.newHashMap();
        for (SeriesTimeExtrema result : (List<SeriesTimeExtrema>) c.list()) {
            if (result.isSetSeries()) {
                map.put(result.getSeries(), result);
            }
        }
        return map;
    }
    
    private class SeriesTimeTransformer implements ResultTransformer {
        private static final long serialVersionUID = -373512929481519459L;

        @Override
        public SeriesTimeExtrema transformTuple(Object[] tuple, String[] aliases) {
            SeriesTimeExtrema seriesTimeExtrema = new SeriesTimeExtrema();
            if (tuple != null) {
                seriesTimeExtrema.setSeries(((Series) tuple[0]).getSeriesId());
                seriesTimeExtrema.setMinPhenomenonTime(DateTimeHelper.makeDateTime(tuple[1]));
                seriesTimeExtrema.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(tuple[2]));
            }
            return seriesTimeExtrema;
        }

        @Override
        @SuppressWarnings({ "rawtypes"})
        public List transformList(List collection) {
            return collection;
        }
    }

    public Observation<?> getMinObservation(Series series, DateTime time, Session session) {
        Criteria c = getDefaultObservationCriteria(session);
        c.add(Restrictions.eq(SeriesObservation.SERIES, series));
        c.add(Restrictions.eq(SeriesObservation.PHENOMENON_TIME_START, time.toDate()));
        return (Observation<?>) c.uniqueResult();
    }
    
    public Object getMaxObservation(Series series, DateTime time, Session session) {
        Criteria c = getDefaultObservationCriteria(session);
        c.add(Restrictions.eq(SeriesObservation.SERIES, series));
        c.add(Restrictions.eq(SeriesObservation.PHENOMENON_TIME_END, time.toDate()));
        return (Observation<?>) c.uniqueResult();
    }

}
