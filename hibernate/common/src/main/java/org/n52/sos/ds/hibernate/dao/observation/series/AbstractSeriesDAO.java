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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.sos.ds.hibernate.dao.AbstractIdentifierNameDescriptionDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ResultFilterClasses;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.ds.hibernate.util.TimeExtrema;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.gda.GetDataAvailabilityRequest;
import org.n52.sos.ogc.filter.Filter;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.vividsolutions.jts.geom.Geometry;

public abstract class AbstractSeriesDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeriesDAO.class);

    public abstract Class<?> getSeriesClass();

    /**
     * Get series for GetObservation request and featuresOfInterest
     *
     * @param request
     *            GetObservation request to get series for
     * @param features
     *            FeaturesOfInterest to get series for
     * @param session
     *            Hibernate session
     * @return Series that fit
     * @throws CodedException
     */
    public abstract List<Series> getSeries(GetObservationRequest request, Collection<String> features, Session session)
            throws OwsExceptionReport;

    /**
     * Get series for GetObservationByIdRequest request
     * @param request GetObservationByIdRequest request to get series for
     * @param session
     *            Hibernate session
     * @return Series that fit
     * @throws CodedException
     */
    public abstract List<Series> getSeries(GetObservationByIdRequest request, Session session)
            throws OwsExceptionReport;
    
    /**
     * Get series for series identifiers
     * @param identifiers Series identifiers to get series for
     * @param session
     *            Hibernate session
     * @return Series that fit
     * @throws CodedException
     */
    public abstract List<Series> getSeries(Collection<String> identifiers, Session session)
            throws OwsExceptionReport;

    /**
     * Get series for GetDataAvailability request
     * @param request GetDataAvailability request to get series for
     * @param session Hibernate session
     * @return Series that fit
     * @throws CodedException
     */
    public abstract List<Series> getSeries(GetDataAvailabilityRequest request, Session session)
            throws OwsExceptionReport;

    /**
     * Query series for observedProiperty and featuresOfInterest
     *
     * @param observedProperty
     *            ObservedProperty to get series for
     * @param features
     *            FeaturesOfInterest to get series for
     * @param session
     *            Hibernate session
     * @return Series list
     */
    public abstract List<Series> getSeries(String observedProperty, Collection<String> features, Session session);

    /**
     * Query series for observedProiperty and featuresOfInterest
     *
     * @param procedure
     *            Procedure to get series for
     * @param observedProperty
     *            ObservedProperty to get series for
     * @param offering
     *            offering to get series for
     * @param features
     *            FeaturesOfInterest to get series for
     * @param session
     *            Hibernate session
     * @return Series list
     */
    public abstract List<Series> getSeries(String procedure, String observedProperty, String offering,
            Collection<String> featureIdentifiers, Session session);

    /**
     * Create series for parameter
     *
     * @param procedures
     *            Procedures to get series for
     * @param observedProperties
     *            ObservedProperties to get series for
     * @param features
     *            FeaturesOfInterest to get series for
     * @param session
     *            Hibernate session
     * @return Series that fit
     */
    public abstract List<Series> getSeries(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Session session);

    /**
     * Create series for parameter
     *
     * @param procedures
     *            Procedures to get series for
     * @param observedProperties
     *            ObservedProperties to get series for
     * @param features
     *            FeaturesOfInterest to get series for
     * @param offerings
     *            Offerings to get series for
     * @param session
     *            Hibernate session
     * @return Series that fit
     */
    public abstract List<Series> getSeries(Collection<String> procedures, Collection<String> observedProperties, Collection<String> featuresOfInterest,
            Collection<String> offerings, Session session) throws OwsExceptionReport;
    /**
     * Get series for procedure, observableProperty and featureOfInterest
     *
     * @param procedure
     *            Procedure identifier parameter
     * @param observableProperty
     *            ObservableProperty identifier parameter
     * @param featureOfInterest
     *            FeatureOfInterest identifier parameter
     * @param session
     *            Hibernate session
     * @return Matching series
     */
    public abstract Series getSeriesFor(String procedure, String observableProperty, String featureOfInterest,
            Session session);

    /**
     * Insert or update and get series for procedure, observable property and
     * featureOfInterest
     *
     * @param identifiers
     *            identifiers object
     * @param session
     *            Hibernate session
     * @return Series object
     * @throws CodedException
     */
    public abstract Series getOrInsertSeries(ObservationContext identifiers, final Session session)
            throws CodedException;

    protected abstract void addSpecificRestrictions(Criteria c, GetObservationRequest request) throws CodedException;
    
    public abstract ObservationFactory getObservationFactory();

    protected Series getOrInsert(ObservationContext ctx, final Session session) throws CodedException {
        Criteria criteria = getDefaultAllSeriesCriteria(session);
        ctx.addIdentifierRestrictionsToCritera(criteria);
        LOGGER.debug("QUERY getOrInsertSeries(feature, observableProperty, procedure): {}",
                HibernateHelper.getSqlString(criteria));
        Series series = (Series) criteria.uniqueResult();
        if (series == null) {
            series = getSeriesImpl();
            ctx.addValuesToSeries(series);
            series.setDeleted(false);
            series.setPublished(ctx.isPublish());
            series.setHiddenChild(ctx.isHiddenChild());
        } else if (series.isDeleted()) {
            series.setDeleted(false);
        } else if (ctx.isSetSeriesType() && !series.isSetSeriesType()) {
            ctx.addValuesToSeries(series);
        } else if (ctx.isPublish() && !series.isPublished()) {
            series.setPublished(true);
        } else {
            return series;
        }
        session.saveOrUpdate(series);
        session.flush();
        session.refresh(series);
        return series;
    }

    private Series getSeriesImpl() throws CodedException {
        try {
            return (Series) getSeriesClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage("Error while creating an instance of %s",
                    getSeriesClass().getCanonicalName());
        }
    }

    @SuppressWarnings("unchecked")
    public Set<Series> getSeriesSet(GetObservationRequest request, Collection<String> features,
            Session session)
            throws OwsExceptionReport {
        Set<Series> set = new LinkedHashSet<>();
        if (request.hasResultFilter()) {
            for (SubQueryIdentifier identifier : ResultFilterRestrictions.getSubQueryIdentifier(getResultFilterClasses())) {
                final Criteria c = createCriteriaFor(request.getProcedures(), request.getObservedProperties(),
                        features, request.getOfferings(), session);
                addSpecificRestrictions(c, request);
                checkAndAddResultFilterCriterion(c, request, identifier, session);
                checkAndAddSpatialFilterCriterion(c, request, session);
                LOGGER.debug("QUERY getSeries(request, features) and result filter sub query '{}': {}",
                        identifier.name(), HibernateHelper.getSqlString(c));
                set.addAll(c.list());
            }
        } else {
            final Criteria c = createCriteriaFor(request.getProcedures(), request.getObservedProperties(), features,
                    request.getOfferings(), session);
            addSpecificRestrictions(c, request);
            checkAndAddSpatialFilterCriterion(c, request, session);
            LOGGER.debug("QUERY getSeries(request, features): {}", HibernateHelper.getSqlString(c));
            set.addAll(c.list());
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    protected Set<Series> getSeriesCriteria(GetDataAvailabilityRequest request, Session session)
            throws OwsExceptionReport {
        Set<Series> set = new LinkedHashSet<>();
        if (request.hasResultFilter()) {
            for (SubQueryIdentifier identifier : ResultFilterRestrictions.getSubQueryIdentifier(getResultFilterClasses())) {
                Criteria c = getSeriesCriteria(request.getProcedures(), request.getObservedProperties(),
                        request.getFeaturesOfInterest(), request.getOfferings(), session);
                checkAndAddResultFilterCriterion(c, request, identifier, session);
                checkAndAddSpatialFilterCriterion(c, request, session);
                LOGGER.debug("QUERY getSeriesCriteria(request) and result filter sub query '{}': {}",
                        identifier.name(), HibernateHelper.getSqlString(c));
                set.addAll(c.list());
            }
        } else {
            Criteria c = getSeriesCriteria(request.getProcedures(), request.getObservedProperties(),
                    request.getFeaturesOfInterest(), request.getOfferings(), session);
            checkAndAddSpatialFilterCriterion(c, request, session);
            LOGGER.debug("QUERY getSeriesCriteria(request): {}", HibernateHelper.getSqlString(c));
            set.addAll(c.list());
        }
        return set;
    }

    public Criteria  getSeriesCriteria(Collection<String> identifiers, Session session) {
        final Criteria c = getDefaultSeriesCriteria(session);
        c.add(Restrictions.in(Series.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getSeriesCriteria(request): {}", HibernateHelper.getSqlString(c));
        return c;
    }
    
    public Criteria getSeriesCriteria(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Session session) {
        final Criteria c = createCriteriaFor(procedures, observedProperties, features, session);
        LOGGER.debug("QUERY getSeries(procedures, observableProperteies, features): {}",
                HibernateHelper.getSqlString(c));
        return c;
    }

    public Criteria getSeriesCriteria(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Collection<String> offerings, Session session) {
        final Criteria c = createCriteriaFor(procedures, observedProperties, features, offerings, session);
        LOGGER.debug("QUERY getSeries(proceedures, observableProperteies, features, offerings): {}",
                HibernateHelper.getSqlString(c));
        return c;
    }

    public Criteria getSeriesCriteria(String observedProperty, Collection<String> features, Session session) {
        final Criteria c = getDefaultSeriesCriteria(session);
        if (CollectionHelper.isNotEmpty(features)) {
            addFeatureOfInterestToCriteria(c, features);
        }
        if (StringHelper.isNotEmpty(observedProperty)) {
            addObservablePropertyToCriteria(c, observedProperty);
        }
        return c;
    }

    public Criteria getSeriesCriteria(String procedure, String observedProperty, String offering, Collection<String> features,
            Session session) {
        final Criteria c = getDefaultSeriesCriteria(session);
        if (CollectionHelper.isNotEmpty(features)) {
            addFeatureOfInterestToCriteria(c, features);
        }
        if (StringHelper.isNotEmpty(observedProperty)) {
            addObservablePropertyToCriteria(c, observedProperty);
        }
        if (StringHelper.isNotEmpty(offering)) {
            addOfferingToCriteria(c, offering);
        }
        if (StringHelper.isNotEmpty(procedure)) {
            addProcedureToCriteria(c, procedure);
        }
        return c;
    }

    public Criteria getSeriesCriteriaFor(String procedure, String observableProperty, String featureOfInterest,
            Session session) {
        final Criteria c = createCriteriaFor(procedure, observableProperty, featureOfInterest, session);
        LOGGER.debug("QUERY getSeriesFor(procedure, observableProperty, featureOfInterest): {}",
                HibernateHelper.getSqlString(c));
        return c;
    }

    /**
     * Add featureOfInterest restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param feature
     *            FeatureOfInterest identifier to add
     */
    public void addFeatureOfInterestToCriteria(Criteria c, String feature) {
        c.createCriteria(Series.FEATURE_OF_INTEREST, "foi").add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, feature));

    }

    /**
     * Add featureOfInterest restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param feature
     *            FeatureOfInterest to add
     */
    public void addFeatureOfInterestToCriteria(Criteria c, FeatureOfInterest feature) {
        c.add(Restrictions.eq(Series.FEATURE_OF_INTEREST, feature));

    }

    /**
     * Add featuresOfInterest restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param features
     *            FeatureOfInterest identifiers to add
     */
    public void addFeatureOfInterestToCriteria(Criteria c, Collection<String> features) {
        c.createCriteria(Series.FEATURE_OF_INTEREST, "foi").add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));

    }
    
    /**
     * Add observedProperty restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param observedProperty
     *            ObservableProperty identifier to add
     */
    public void addObservablePropertyToCriteria(Criteria c, String observedProperty) {
        c.createCriteria(Series.OBSERVABLE_PROPERTY)
                .add(Restrictions.eq(ObservableProperty.IDENTIFIER, observedProperty));
    }

    /**
     * Add observedProperty restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param observedProperty
     *            ObservableProperty to add
     */
    public void addObservablePropertyToCriteria(Criteria c, ObservableProperty observedProperty) {
        c.add(Restrictions.eq(Series.OBSERVABLE_PROPERTY, observedProperty));
    }

    /**
     * Add observedProperties restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param observedProperties
     *            ObservableProperty identifiers to add
     */
    public void addObservablePropertyToCriteria(Criteria c, Collection<String> observedProperties) {
        c.createCriteria(Series.OBSERVABLE_PROPERTY)
                .add(Restrictions.in(ObservableProperty.IDENTIFIER, observedProperties));
    }

    /**
     * Add procedure restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param procedure
     *            Procedure identifier to add
     */
    public void addProcedureToCriteria(Criteria c, String procedure) {
        c.createCriteria(Series.PROCEDURE).add(Restrictions.eq(Procedure.IDENTIFIER, procedure));
    }

    /**
     * Add procedure restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param procedure
     *            Procedure to add
     */
    public void addProcedureToCriteria(Criteria c, Procedure procedure) {
        c.add(Restrictions.eq(Series.PROCEDURE, procedure));

    }

    /**
     * Add procedures restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param procedures
     *            Procedure identifiers to add
     */
    public void addProcedureToCriteria(Criteria c, Collection<String> procedures) {
        c.createCriteria(Series.PROCEDURE).add(Restrictions.in(Procedure.IDENTIFIER, procedures));

    }

    /**
     * Add offering restriction to Hibernate Criteria with LEFT-OUTER-JOIN
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param offerings
     *            Offering identifiers to add
     * @throws OwsExceptionReport
     */
    public void addOfferingToCriteria(Criteria c, Collection<String> offerings) {
        c.createCriteria(Series.OFFERING).add(Restrictions.in(Offering.IDENTIFIER, offerings));

    }

    public void addOfferingToCriteria(Criteria c, String offering) {
        c.createCriteria(Series.OFFERING).add(Restrictions.eq(Offering.IDENTIFIER, offering));
    }

    public void addOfferingToCriteria(Criteria c, Offering offering) {
        c.add(Restrictions.eq(Series.PROCEDURE, offering));
    }

    /**
     * Get default Hibernate Criteria for querying series, deleted flag ==
     * <code>false</code>
     *
     * @param session
     *            Hibernate Session
     * @return Default criteria
     */
    public Criteria getDefaultSeriesCriteria(Session session) {
        Criteria c = session.createCriteria(getSeriesClass()).add(Restrictions.eq(Series.DELETED, false))
                .add(Restrictions.eq(Series.PUBLISHED, true)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if (!isIncludeChildObservableProperties()) {
            c.add(Restrictions.eq(Series.HIDDEN_CHILD, false));
        }
        return c;
    }

    /**
     * Get default Hibernate Criteria for querying all series
     *
     * @param session
     *            Hibernate Session
     * @return Default criteria
     */
    public Criteria getDefaultAllSeriesCriteria(Session session) {
        return session.createCriteria(getSeriesClass()).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Update Series for procedure by setting deleted flag and return changed
     * series
     *
     * @param procedure
     *            Procedure for which the series should be changed
     * @param deleteFlag
     *            New deleted flag value
     * @param session
     *            Hibernate session
     * @return Updated Series
     */
    @SuppressWarnings("unchecked")
    public List<Series> updateSeriesSetAsDeletedForProcedureAndGetSeries(String procedure, boolean deleteFlag,
            Session session) {
        Criteria criteria = getDefaultAllSeriesCriteria(session);
        addProcedureToCriteria(criteria, procedure);
        List<Series> hSeries = criteria.list();
        for (Series series : hSeries) {
            series.setDeleted(deleteFlag);
            session.saveOrUpdate(series);
            session.flush();
        }
        return hSeries;
    }

    /**
     * Update series values which will be used by the Timeseries API. Can be
     * later used by the SOS.
     *
     * @param series
     *            Series object
     * @param hObservation
     *            Observation object
     * @param session
     *            Hibernate session
     */
    public void updateSeriesWithFirstLatestValues(Series series, Observation<?> hObservation, Session session) {
        boolean minChanged = false;
        boolean maxChanged = false;
        if (!series.isSetFirstTimeStamp()
                || series.isSetFirstTimeStamp() && series.getFirstTimeStamp().after(
                        hObservation.getPhenomenonTimeStart())) {
            minChanged = true;
            series.setFirstTimeStamp(hObservation.getPhenomenonTimeStart());
        }
        if (!series.isSetLastTimeStamp()
                || series.isSetLastTimeStamp() && series.getLastTimeStamp().before(
                        hObservation.getPhenomenonTimeEnd())) {
            maxChanged = true;
            series.setLastTimeStamp(hObservation.getPhenomenonTimeEnd());
        }

        if (hObservation instanceof NumericObservation) {
            if (minChanged) {
                series.setFirstNumericValue(((NumericObservation) hObservation).getValue());
            }
            if (maxChanged) {
                series.setLastNumericValue(((NumericObservation) hObservation).getValue());
            }
            if (!series.isSetUnit() && hObservation.isSetUnit()) {
                // TODO check if both unit are equal. If not throw exception?
                series.setUnit(hObservation.getUnit());
            }
        } else if (hObservation instanceof SweDataArrayObservation) {
            if (!series.isSetUnit() && hObservation.isSetUnit()) {
                // TODO check if both unit are equal. If not throw exception?
                series.setUnit(hObservation.getUnit());
            }
        }
        session.saveOrUpdate(series);
        session.flush();
    }

    /**
     * Check {@link Series} if the deleted observation time stamp corresponds to
     * the first/last series time stamp
     *
     * @param series
     *            Series to update
     * @param observation
     *            Deleted observation
     * @param session
     *            Hibernate session
     */
    public void updateSeriesAfterObservationDeletion(Series series, SeriesObservation<?> observation, Session session) {
        SeriesObservationDAO seriesObservationDAO = new SeriesObservationDAO();
        if (series.isSetFirstTimeStamp() && series.getFirstTimeStamp().equals(observation.getPhenomenonTimeStart())) {
            SeriesObservation<?> firstObservation = seriesObservationDAO.getFirstObservationFor(series, session);
            if (firstObservation != null) {
	            series.setFirstTimeStamp(firstObservation.getPhenomenonTimeStart());
	            if (firstObservation instanceof NumericObservation) {
	                series.setFirstNumericValue(((NumericObservation) firstObservation).getValue());
	            }
            } else {
            	series.setFirstTimeStamp(null);
	            if (observation instanceof NumericObservation) {
	                series.setFirstNumericValue(null);
	            }
            }
        }
        if (series.isSetLastTimeStamp() && series.getLastTimeStamp().equals(observation.getPhenomenonTimeEnd())) {
            SeriesObservation<?> latestObservation = seriesObservationDAO.getLastObservationFor(series, session);
            if (latestObservation != null) {
            	series.setLastTimeStamp(latestObservation.getPhenomenonTimeEnd());
                if (latestObservation instanceof NumericObservation) {
                    series.setLastNumericValue(((NumericObservation) latestObservation).getValue());
                }
            } else {
            	series.setLastTimeStamp(null);
                if (observation instanceof NumericObservation) {
                    series.setLastNumericValue(null);
                }
            }
        }
        if (!series.isSetFirstLastTime()) {
        	series.setUnit(null);
        }
        session.saveOrUpdate(series);
    }

    public TimeExtrema getProcedureTimeExtrema(Session session, String procedure) {
        Criteria c = getDefaultSeriesCriteria(session);
        addProcedureToCriteria(c, procedure);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.min(Series.FIRST_TIME_STAMP));
        projectionList.add(Projections.max(Series.LAST_TIME_STAMP));
        c.setProjection(projectionList);
        LOGGER.debug("QUERY getProcedureTimeExtrema(procedureIdentifier): {}", HibernateHelper.getSqlString(c));
        Object[] result = (Object[]) c.uniqueResult();

        TimeExtrema pte = new TimeExtrema();
        if (result != null) {
            pte.setMinPhenomenonTime(DateTimeHelper.makeDateTime(result[0]));
            pte.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(result[1]));
        }
        return pte;
    }
    
    /**
     * Create series query criteria for parameter
     *
     * @param procedures
     *            Procedures to get series for
     * @param observedProperties
     *            ObservedProperties to get series for
     * @param features
     *            FeatureOfInterest to get series for
     * @param session
     *            Hibernate session
     * @return Criteria to query series
     */
    private Criteria createCriteriaFor(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Session session) {
        final Criteria c = getDefaultSeriesCriteria(session);
        if (CollectionHelper.isNotEmpty(features)) {
            addFeatureOfInterestToCriteria(c, features);
        }
        if (CollectionHelper.isNotEmpty(observedProperties)) {
            addObservablePropertyToCriteria(c, observedProperties);
        }
        if (CollectionHelper.isNotEmpty(procedures)) {
            addProcedureToCriteria(c, procedures);
        }
        return c;
    }

    private Criteria createCriteriaFor(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Collection<String> offerings, Session session) {
        final Criteria c = createCriteriaFor(procedures, observedProperties, features, session);
        if (CollectionHelper.isNotEmpty(offerings)) {
            addOfferingToCriteria(c, offerings);
        }
        c.setFetchMode(Series.PROCEDURE, FetchMode.JOIN);
        c.setFetchMode(Series.OBSERVABLE_PROPERTY, FetchMode.JOIN);
        c.setFetchMode(Series.FEATURE_OF_INTEREST, FetchMode.JOIN);
        c.setFetchMode(Series.OFFERING, FetchMode.JOIN);
        return c;
    }

    /**
     * Get series query Hibernate Criteria for procedure, observableProperty and
     * featureOfInterest
     *
     * @param procedure
     *            Procedure to get series for
     * @param observedProperty
     *            ObservedProperty to get series for
     * @param feature
     *            FeatureOfInterest to get series for
     * @param session
     *            Hibernate session
     * @return Criteria to query series
     */
    private Criteria createCriteriaFor(String procedure, String observedProperty, String feature, Session session) {
        final Criteria c = getDefaultSeriesCriteria(session);
        if (Strings.isNullOrEmpty(feature)) {
            addFeatureOfInterestToCriteria(c, feature);
        }
        if (Strings.isNullOrEmpty(observedProperty)) {
            addObservablePropertyToCriteria(c, observedProperty);
        }
        if (Strings.isNullOrEmpty(procedure)) {
            addProcedureToCriteria(c, procedure);
        }
        return c;
    }

    protected void checkAndAddResultFilterCriterion(Criteria c, GetDataAvailabilityRequest request, SubQueryIdentifier identifier, Session session)
            throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            addResultfilter(c, request.getResultFilter(), identifier);
        }
    }

    protected void checkAndAddResultFilterCriterion(Criteria c, GetObservationRequest request, SubQueryIdentifier identifier, Session session)
            throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            addResultfilter(c, request.getResultFilter(), identifier);
        }
    }
    
    private void addResultfilter(Criteria c, Filter<?> resultFilter, SubQueryIdentifier identifier) throws CodedException {
        Criterion resultFilterExpression = ResultFilterRestrictions.getResultFilterExpression(resultFilter,
                getResultFilterClasses(), Series.ID, AbstractSeriesObservation.SERIES, identifier);
        if (resultFilterExpression != null) {
            c.add(resultFilterExpression);
        }
    }
    
    protected void checkAndAddSpatialFilterCriterion(Criteria c, GetDataAvailabilityRequest request,
            Session session) throws OwsExceptionReport {
        if (request.hasSpatialFilter()) {
            SpatialFilter filter = request.getSpatialFilter();
            Geometry geometry = GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry());
            if (filter.getValueReference().equals(Sos2Constants.VALUE_REFERENCE_SPATIAL_FILTERING_PROFILE)) {
                DetachedCriteria dc = DetachedCriteria.forClass(getObservationFactory().observationClass());
                dc.add(SpatialRestrictions.filter(Observation.SAMPLING_GEOMETRY, filter.getOperator(),
                        geometry));
                dc.setProjection(Projections.property(AbstractSeriesObservation.SERIES));
                c.add(Subqueries.propertyIn(Series.ID, dc));
            } else {
                if (request.isSetFeaturesOfInterest()) {
                    c.add(SpatialRestrictions.filter("foi." + FeatureOfInterest.GEOMETRY, filter.getOperator(), geometry));
                } else {
                    c.createCriteria(Series.FEATURE_OF_INTEREST).add(SpatialRestrictions.filter(FeatureOfInterest.GEOMETRY, filter.getOperator(), geometry));
                }
            }
        }
    }
    
    protected void checkAndAddSpatialFilterCriterion(Criteria c, GetObservationRequest request,
            Session session) throws OwsExceptionReport {
        if (request.isSetSpatialFilter()) {
            SpatialFilter filter = request.getSpatialFilter();
            Geometry geometry = GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry());
            if (filter.getValueReference().equals(Sos2Constants.VALUE_REFERENCE_SPATIAL_FILTERING_PROFILE)) {
                DetachedCriteria dc = DetachedCriteria.forClass(getObservationFactory().observationClass());
                dc.add(SpatialRestrictions.filter(Observation.SAMPLING_GEOMETRY, filter.getOperator(),
                        geometry));
                dc.setProjection(Projections.property(AbstractSeriesObservation.SERIES));
                c.add(Subqueries.propertyIn(Series.ID, dc));
            }
        }
    }
    
    public ResultFilterClasses getResultFilterClasses() {
        return new ResultFilterClasses(getObservationFactory().numericClass(), getObservationFactory().countClass(),
                getObservationFactory().textClass(), getObservationFactory().categoryClass(),
                getObservationFactory().complexClass(), getObservationFactory().profileClass());
    }
    
    protected boolean isIncludeChildObservableProperties() {
        return ServiceConfiguration.getInstance().isIncludeChildObservableProperties();
    }
}
