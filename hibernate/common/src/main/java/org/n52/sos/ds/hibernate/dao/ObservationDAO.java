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

import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.AbstractObservationTime;
import org.n52.sos.ds.hibernate.entities.BlobObservation;
import org.n52.sos.ds.hibernate.entities.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.CountObservation;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.NumericObservation;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.TextObservation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Hibernate data access class for observation
 * 
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class ObservationDAO extends AbstractObservationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationDAO.class);

    public static final String SQL_QUERY_GET_LATEST_OBSERVATION_TIME = "getLatestObservationTime";

    public static final String SQL_QUERY_GET_FIRST_OBSERVATION_TIME = "getFirstObservationTime";

    @Override
    protected void addObservationIdentifiersToObservation(ObservationIdentifiers observationIdentifiers,
            AbstractObservation observation, Session session) {
        Observation hObservation = (Observation) observation;
        hObservation.setFeatureOfInterest(observationIdentifiers.getFeatureOfInterest());
        hObservation.setObservableProperty(observationIdentifiers.getObservableProperty());
        hObservation.setProcedure(observationIdentifiers.getProcedure());
    }

    @Override
    public Criteria getObservationInfoCriteriaForFeatureOfInterestAndProcedure(String feature, String procedure,
            Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        criteria.createCriteria(AbstractObservation.FEATURE_OF_INTEREST)
                .add(eq(FeatureOfInterest.IDENTIFIER, feature));
        criteria.createCriteria(AbstractObservation.PROCEDURE).add(eq(Procedure.IDENTIFIER, procedure));
        return criteria;
    }

    @Override
    public Criteria getObservationInfoCriteriaForFeatureOfInterestAndOffering(String feature, String offering,
            Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        criteria.createCriteria(AbstractObservation.FEATURE_OF_INTEREST)
                .add(eq(FeatureOfInterest.IDENTIFIER, feature));
        criteria.createCriteria(AbstractObservation.OFFERINGS).add(eq(Offering.IDENTIFIER, offering));
        return criteria;
    }

    /**
     * Update observation by setting deleted flag
     * 
     * @param procedure
     *            Procedure for which the observations should be updated
     * @param deleteFlag
     *            New deleted flag value
     * @param session
     *            Hibernate Session
     */
    public void updateObservationSetAsDeletedForProcedure(String procedure, boolean deleteFlag, Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        criteria.createCriteria(AbstractObservation.PROCEDURE).add(Restrictions.eq(Procedure.IDENTIFIER, procedure));
        ScrollableIterable<AbstractObservation> scroll = ScrollableIterable.fromCriteria(criteria);
        updateObservation(scroll, deleteFlag, session);
    }

    /**
     * Add observableProperty restriction to Hibernate Criteria for Observation
     * 
     * @param criteria
     *            Hibernate Criteria for Observation
     * @param observableProperty
     *            ObservableProperty identifier to add
     */
    public void addObservablePropertyRestrictionToObservationCriteria(Criteria criteria, String observableProperty) {
        criteria.createCriteria(AbstractObservation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, observableProperty));

    }

    /**
     * Add procedure restriction to Hibernate Criteria for Observation
     * 
     * @param criteria
     *            Hibernate Criteria for Observation
     * @param procedure
     *            Procedure identifier to add
     */
    public void addProcedureRestrictionToObservationCriteria(Criteria criteria, String procedure) {
        criteria.createCriteria(AbstractObservation.PROCEDURE).add(Restrictions.eq(Procedure.IDENTIFIER, procedure));

    }

    /**
     * Add featureOfInterest restriction to Hibernate Criteria for Observation
     * 
     * @param criteria
     *            Hibernate Criteria for Observation
     * @param featureOfInterest
     *            FeatureOfInterest identifier to add
     */
    public void addFeatureOfInterestRestrictionToObservationCriteria(Criteria criteria, String featureOfInterest) {
        criteria.createCriteria(AbstractObservation.FEATURE_OF_INTEREST).add(
                eq(FeatureOfInterest.IDENTIFIER, featureOfInterest));
    }

    @Override
    public Criteria getObservationCriteriaForProcedure(String procedure, Session session) {
        Criteria criteria = getDefaultObservationCriteria(session);
        addProcedureRestrictionToObservationCriteria(criteria, procedure);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaForObservableProperty(String observableProperty, Session session) {
        Criteria criteria = getDefaultObservationCriteria(session);
        addObservablePropertyRestrictionToObservationCriteria(criteria, observableProperty);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaForFeatureOfInterest(String featureOfInterest, Session session) {
        Criteria criteria = getDefaultObservationCriteria(session);
        addFeatureOfInterestRestrictionToObservationCriteria(criteria, featureOfInterest);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaFor(String procedure, String observableProperty, Session session) {
        Criteria criteria = getDefaultObservationCriteria(session);
        addProcedureRestrictionToObservationCriteria(criteria, procedure);
        addObservablePropertyRestrictionToObservationCriteria(criteria, observableProperty);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaFor(String procedure, String observableProperty, String featureOfInterest,
            Session session) {
        Criteria criteria = getDefaultObservationCriteria(session);
        addProcedureRestrictionToObservationCriteria(criteria, procedure);
        addObservablePropertyRestrictionToObservationCriteria(criteria, observableProperty);
        addFeatureOfInterestRestrictionToObservationCriteria(criteria, featureOfInterest);
        return criteria;
    }
    @SuppressWarnings("unchecked")
    public Collection<String> getObservationIdentifiers(String procedureIdentifier, Session session) {
        Criteria criteria =
                session.createCriteria(ObservationInfo.class)
                        .setProjection(Projections.distinct(Projections.property(ObservationInfo.IDENTIFIER)))
                        .add(Restrictions.isNotNull(ObservationInfo.IDENTIFIER))
                        .add(Restrictions.eq(ObservationInfo.DELETED, false));
        criteria.createCriteria(ObservationInfo.PROCEDURE).add(
                Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
        LOGGER.debug("QUERY ObservationDAO.getObservationIdentifiers(procedureIdentifier): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    private List<AbstractObservation> getObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        // final Criteria c = getDefaultObservationCriteria(Observation.class,
        // session);
        //
        // checkAndAddSpatialFilteringProfileCriterion(c, request, session);
        //
        // if (CollectionHelper.isNotEmpty(request.getProcedures())) {
        // c.createCriteria(
        // Observation.PROCEDURE).add(Restrictions.in(Procedure.IDENTIFIER,
        // request.getProcedures()));
        // }
        //
        // if (CollectionHelper.isNotEmpty(request.getObservedProperties())) {
        // c.createCriteria(Observation.OBSERVABLE_PROPERTY).add(Restrictions.in(ObservableProperty.IDENTIFIER,
        // request.getObservedProperties()));
        // }
        //
        // if (CollectionHelper.isNotEmpty(features)) {
        // c.createCriteria(Observation.FEATURE_OF_INTEREST).add(Restrictions.in(FeatureOfInterest.IDENTIFIER,
        // features));
        // }
        //
        // if (CollectionHelper.isNotEmpty(request.getOfferings())) {
        // c.createCriteria(Observation.OFFERINGS).add(Restrictions.in(Offering.IDENTIFIER,
        // request.getOfferings()));
        // }
        //
        // String logArgs = "request, features, offerings";
        // if (filterCriterion != null) {
        // logArgs += ", filterCriterion";
        // c.add(filterCriterion);
        // }
        // if (sosIndeterminateTime != null) {
        // logArgs += ", sosIndeterminateTime";
        // addIndeterminateTimeRestriction(c, sosIndeterminateTime);
        // }
        // LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs,
        // HibernateHelper.getSqlString(c));
        return getObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session).list();
    }

    protected Criteria getObservationCriteriaFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        final Criteria c = getDefaultObservationCriteria(session);

        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        if (CollectionHelper.isNotEmpty(request.getProcedures())) {
            c.createCriteria(Observation.PROCEDURE)
                    .add(Restrictions.in(Procedure.IDENTIFIER, request.getProcedures()));
        }

        if (CollectionHelper.isNotEmpty(request.getObservedProperties())) {
            c.createCriteria(Observation.OBSERVABLE_PROPERTY).add(
                    Restrictions.in(ObservableProperty.IDENTIFIER, request.getObservedProperties()));
        }

        if (CollectionHelper.isNotEmpty(features)) {
            c.createCriteria(Observation.FEATURE_OF_INTEREST).add(
                    Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
        }

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            c.createCriteria(Observation.OFFERINGS).add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }

        String logArgs = "request, features, offerings";
        if (filterCriterion != null) {
            logArgs += ", filterCriterion";
            c.add(filterCriterion);
        }
        if (sosIndeterminateTime != null) {
            logArgs += ", sosIndeterminateTime";
            addIndeterminateTimeRestriction(c, sosIndeterminateTime);
        }
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c;
    }

    public Collection<AbstractObservation> getObservationsFor(GetObservationRequest request, Set<String> features,
            Criterion filterCriterion, Session session) throws OwsExceptionReport {
        return getObservationsFor(request, features, filterCriterion, null, session);
    }

    public Collection<AbstractObservation> getObservationsFor(GetObservationRequest request, Set<String> features,
            SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getObservationsFor(request, features, null, sosIndeterminateTime, session);
    }

    public Collection<AbstractObservation> getObservationsFor(GetObservationRequest request, Set<String> features,
            Session session) throws OwsExceptionReport {
        return getObservationsFor(request, features, null, null, session);
    }

    @SuppressWarnings("unchecked")
    public Collection<? extends AbstractObservation> getObservationsFor(ObservationConstellation oc,
            HashSet<String> features, GetObservationRequest request, SosIndeterminateTime sosIndeterminateTime,
            Session session) throws OwsExceptionReport {
        final Criteria c = getDefaultObservationCriteria(session);

        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        c.createCriteria(Observation.PROCEDURE).add(Restrictions.eq(Procedure.ID, oc.getProcedure().getProcedureId()));

        c.createCriteria(Observation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.ID, oc.getObservableProperty().getObservablePropertyId()));

        if (CollectionHelper.isNotEmpty(features)) {
            c.createCriteria(Observation.FEATURE_OF_INTEREST).add(
                    Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
        }

        c.createCriteria(Observation.OFFERINGS).add(Restrictions.eq(Offering.ID, oc.getOffering().getOfferingId()));

        String logArgs = "request, features, offerings";
        logArgs += ", sosIndeterminateTime";
        addIndeterminateTimeRestriction(c, sosIndeterminateTime);
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c.list();
    }

    public ScrollableResults getStreamingObservationsFor(GetObservationRequest request, Set<String> features,
            Criterion temporalFilterCriterion, Session session) throws HibernateException, OwsExceptionReport {
        return getObservationCriteriaFor(request, features, temporalFilterCriterion, null, session).setReadOnly(true)
                .scroll(ScrollMode.FORWARD_ONLY);
    }

    public ScrollableResults getStreamingObservationsFor(GetObservationRequest request, Set<String> features,
            Session session) throws HibernateException, OwsExceptionReport {
        return getObservationCriteriaFor(request, features, null, null, session).setReadOnly(true).scroll(
                ScrollMode.FORWARD_ONLY);
    }

    public ScrollableResults getNotMatchingSeries(Set<Long> procedureIds, Set<Long> observablePropertyIds,
            Set<Long> featureIds, GetObservationRequest request, Set<String> features,
            Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        Criteria c =
                getObservationCriteriaFor(request, features, temporalFilterCriterion, null, session);
        addAliasAndNotRestrictionFor(c, procedureIds, observablePropertyIds, featureIds);
        return c.setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
    }

    public ScrollableResults getNotMatchingSeries(Set<Long> procedureIds, Set<Long> observablePropertyIds,
            Set<Long> featureIds, GetObservationRequest request, Set<String> features, Session session) throws OwsExceptionReport {
        Criteria c = getObservationCriteriaFor(request, features, null, null, session);
        addAliasAndNotRestrictionFor(c, procedureIds, observablePropertyIds, featureIds);
        return c.setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Geometry> getSamplingGeometries(String feature, Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        criteria.createCriteria(AbstractObservation.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        criteria.add(Restrictions.isNotNull(AbstractObservationTime.SAMPLING_GEOMETRY));
        criteria.addOrder(Order.asc(AbstractObservationTime.PHENOMENON_TIME_START));
        criteria.setProjection(Projections.property(AbstractObservationTime.SAMPLING_GEOMETRY));
        return criteria.list();
    }
    
    private void addAliasAndNotRestrictionFor(Criteria c, Set<Long> procedureIds, Set<Long> observablePropertyIds, Set<Long> featureIds) {
        c.createAlias(Observation.PROCEDURE, "p").createAlias(Observation.OBSERVABLE_PROPERTY, "op")
                .createAlias(Observation.FEATURE_OF_INTEREST, "f");
        c.add(Restrictions.not(Restrictions.in("p." + Procedure.ID, procedureIds)));
        c.add(Restrictions.not(Restrictions.in("op." + ObservableProperty.ID, observablePropertyIds)));
        c.add(Restrictions.not(Restrictions.in("f." + FeatureOfInterest.ID, featureIds)));
    }

    @Override
    protected Class<?> getObservationClass() {
        return Observation.class;
    }

    @Override
    protected Class<?> getObservationInfoClass() {
        return ObservationInfo.class;
    }

    @Override
    protected Class<?> getObservationTimeClass() {
        return ObservationInfo.class;
    }

    @Override
    protected Class<?> getBlobObservationClass() {
        return BlobObservation.class;
    }

    @Override
    protected Class<?> getBooleanObservationClass() {
        return BooleanObservation.class;
    }

    @Override
    protected Class<?> getCategoryObservationClass() {
        return CategoryObservation.class;
    }

    @Override
    protected Class<?> getCountObservationClass() {
        return CountObservation.class;
    }

    @Override
    protected Class<?> getGeometryObservationClass() {
        return null;
    }

    @Override
    protected Class<?> getNumericObservationClass() {
        return NumericObservation.class;
    }

    @Override
    protected Class<?> getSweDataArrayObservationClass() {
        return SweDataArrayObservation.class;
    }

    @Override
    protected Class<?> getTextObservationClass() {
        return TextObservation.class;
    } 
}
