/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.dao.series;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.interfaces.NumericObservation;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Hibernate data access class for series
 * 
 * @since 4.0.0
 * 
 */
public class SeriesDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesDAO.class);

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
     */
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(GetObservationRequest request, Collection<String> features, Session session) {
        final Criteria c =
                createCriteriaFor(request.getProcedures(), request.getObservedProperties(), features, session);
        LOGGER.debug("QUERY getSeries(request, features): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }

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
     * @return Series that fir
     */
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Session session) {
        final Criteria c = createCriteriaFor(procedures, observedProperties, features, session);
        LOGGER.debug("QUERY getSeries(proceedures, observableProperteies, features): {}",
                HibernateHelper.getSqlString(c));
        return c.list();
    }

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
    @SuppressWarnings("unchecked")
    public List<Series> getSeries(String observedProperty, Collection<String> features, Session session) {
        final Criteria c = session.createCriteria(Series.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        c.add(Restrictions.eq(Series.DELETED, false));
        if (CollectionHelper.isNotEmpty(features)) {
            addFeatureOfInterestToCriteria(c, features);
        }
        if (StringHelper.isNotEmpty(observedProperty)) {
            addObservablePropertyToCriteria(c, observedProperty);
        }
        return c.list();
    }

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
    public Series getSeriesFor(String procedure, String observableProperty, String featureOfInterest, Session session) {
        final Criteria c = createCriteriaFor(procedure, observableProperty, featureOfInterest, session);
        LOGGER.debug("QUERY getSeriesFor(procedure, observableProperty, featureOfInterest): {}",
                HibernateHelper.getSqlString(c));
        return (Series) c.uniqueResult();
    }

    /**
     * Insert or update and get series for procedure, observable property and
     * featureOfInterest
     * 
     * @param feature
     *            FeatureOfInterest object
     * @param observableProperty
     *            ObservableProperty object
     * @param procedure
     *            Procedure object
     * @param session
     *            Hibernate session
     * @return Series object
     */
    public Series getOrInsertSeries(final FeatureOfInterest feature, final ObservableProperty observableProperty,
            final Procedure procedure, final Session session) {
        Criteria criteria = getDefaultAllSeriesCriteria(session);
        criteria.add(Restrictions.eq(Series.FEATURE_OF_INTEREST, feature))
                .add(Restrictions.eq(Series.OBSERVABLE_PROPERTY, observableProperty))
                .add(Restrictions.eq(Series.PROCEDURE, procedure));
        LOGGER.debug("QUERY getOrInsertSeries(feature, observableProperty, procedure): {}",
                HibernateHelper.getSqlString(criteria));
        Series series = (Series) criteria.uniqueResult();
        if (series == null) {
            series = new Series();
            series.setObservableProperty(observableProperty);
            series.setProcedure(procedure);
            series.setFeatureOfInterest(feature);
            series.setDeleted(false);
            session.save(series);
            session.flush();
            session.refresh(series);
        } else if (series.isDeleted()) {
            series.setDeleted(false);
            session.update(series);
            session.flush();
            session.refresh(series);
        }
        return series;
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

    /**
     * Add featureOfInterest restriction to Hibernate Criteria
     * 
     * @param c
     *            Hibernate Criteria to add restriction
     * @param feature
     *            FeatureOfInterest identifier to add
     */
    public void addFeatureOfInterestToCriteria(Criteria c, String feature) {
        c.createCriteria(Series.FEATURE_OF_INTEREST).add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, feature));

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
        c.createCriteria(Series.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observedProperty));
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
     * Add featuresOfInterest restriction to Hibernate Criteria
     * 
     * @param c
     *            Hibernate Criteria to add restriction
     * @param features
     *            FeatureOfInterest identifiers to add
     */
    public void addFeatureOfInterestToCriteria(Criteria c, Collection<String> features) {
        c.createCriteria(Series.FEATURE_OF_INTEREST).add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));

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
        c.createCriteria(Series.OBSERVABLE_PROPERTY).add(
                Restrictions.in(ObservableProperty.IDENTIFIER, observedProperties));
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
     * Get default Hibernate Criteria for querying series, deleted flag ==
     * <code>false</code>
     * 
     * @param session
     *            Hibernate Session
     * @return Default criteria
     */
    public Criteria getDefaultSeriesCriteria(Session session) {
        return session.createCriteria(Series.class).add(Restrictions.eq(Series.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Get default Hibernate Criteria for querying all series
     * 
     * @param session
     *            Hibernate Session
     * @return Default criteria
     */
    public Criteria getDefaultAllSeriesCriteria(Session session) {
        return session.createCriteria(Series.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
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
     * Update series values which will be used by the Timeseries API.
     * Can be later used by the SOS.
     * 
     * @param series Series object
     * @param hObservation Observation object
     * @param session Hibernate session
     */
    public void updateSeriesWithFirstLatestValues(Series series, AbstractObservation hObservation, Session session) {
        boolean minChanged = false;
        boolean maxChanged = false;
        if (!series.isSetFirstTimeStamp() || (series.isSetFirstTimeStamp() && series.getFirstTimeStamp().after(hObservation.getPhenomenonTimeStart()))) {
            minChanged = true;
            series.setFirstTimeStamp(hObservation.getPhenomenonTimeStart());
        }
        if (!series.isSetLastTimeStamp() || (series.isSetLastTimeStamp() && series.getLastTimeStamp().before(hObservation.getPhenomenonTimeEnd()))) {
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
        }
        session.saveOrUpdate(series);
        session.flush();
    }

}
