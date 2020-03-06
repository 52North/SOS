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
package org.n52.sos.ds.hibernate.dao.observation.legacy;

import static org.hibernate.criterion.Restrictions.eq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.AbstractTemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.ContextualReferencedLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.LegacyObservation;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateGeometryCreator;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.n52.sos.ds.hibernate.util.observation.ExtensionFesFilterCriteriaAdder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class LegacyObservationDAO
        extends
        AbstractObservationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyObservationDAO.class);

    public static final String SQL_QUERY_GET_LATEST_OBSERVATION_TIME = "getLatestObservationTime";

    public static final String SQL_QUERY_GET_FIRST_OBSERVATION_TIME = "getFirstObservationTime";

    @Override
    protected void addObservationContextToObservation(ObservationContext ctx, Observation<?> observation,
            Session session) {
        LegacyObservation<?> hObservation = (LegacyObservation<?>) observation;
        hObservation.setFeatureOfInterest(ctx.getFeatureOfInterest());
        hObservation.setObservableProperty(ctx.getObservableProperty());
        hObservation.setProcedure(ctx.getProcedure());
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
        ScrollableIterable<Observation<?>> scroll = ScrollableIterable.fromCriteria(criteria);
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
        criteria.createCriteria(AbstractObservation.OBSERVABLE_PROPERTY)
                .add(Restrictions.eq(ObservableProperty.IDENTIFIER, observableProperty));

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
        criteria.createCriteria(AbstractObservation.FEATURE_OF_INTEREST)
                .add(eq(FeatureOfInterest.IDENTIFIER, featureOfInterest));
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

    @Override
    public Criteria getTemoralReferencedObservationCriteriaFor(OmObservation observation,
            ObservationConstellation observationConstellation, Session session)
            throws CodedException {
        OmObservationConstellation oc = observation.getObservationConstellation();
        Criteria criteria = getDefaultObservationTimeCriteria(session);
        addProcedureRestrictionToObservationCriteria(criteria, oc.getProcedureIdentifier());
        addObservablePropertyRestrictionToObservationCriteria(criteria, oc.getObservablePropertyIdentifier());
        addFeatureOfInterestRestrictionToObservationCriteria(criteria, oc.getFeatureOfInterestIdentifier());
        return criteria;
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getObservationIdentifiers(String procedureIdentifier, Session session) {
        Criteria criteria = session.createCriteria(ContextualReferencedLegacyObservation.class)
                .setProjection(
                        Projections.distinct(Projections.property(ContextualReferencedLegacyObservation.IDENTIFIER)))
                .add(Restrictions.isNotNull(ContextualReferencedLegacyObservation.IDENTIFIER))
                .add(Restrictions.eq(ContextualReferencedLegacyObservation.DELETED, false));
        criteria.createCriteria(ContextualReferencedLegacyObservation.PROCEDURE)
                .add(Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
        LOGGER.debug("QUERY ObservationDAO.getObservationIdentifiers(procedureIdentifier): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    @Override
    public ScrollableResults getObservations(Set<String> procedure, Set<String> observableProperty,
            Set<String> featureOfInterest, Set<String> offering, Criterion filterCriterion, Session session) {
        final Criteria c = getDefaultObservationCriteria(session);
        if (CollectionHelper.isNotEmpty(procedure)) {
            c.createCriteria(Observation.PROCEDURE).add(Restrictions.in(Procedure.IDENTIFIER, procedure));
        }

        if (CollectionHelper.isNotEmpty(observableProperty)) {
            c.createCriteria(Observation.OBSERVABLE_PROPERTY)
                    .add(Restrictions.in(ObservableProperty.IDENTIFIER, observableProperty));
        }

        if (CollectionHelper.isNotEmpty(featureOfInterest)) {
            c.createCriteria(Observation.FEATURE_OF_INTEREST)
                    .add(Restrictions.in(FeatureOfInterest.IDENTIFIER, featureOfInterest));
        }

        if (CollectionHelper.isNotEmpty(offering)) {
            c.createCriteria(Observation.OFFERINGS).add(Restrictions.in(Offering.IDENTIFIER, offering));
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
    private Collection<Observation<?>> getObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(features)) {
            List<Observation<?>> observations = new ArrayList<>();
            for (List<String> ids : QueryHelper.getListsForIdentifiers(features)) {
                observations.addAll(getObservationFor(request, ids, filterCriterion, sosIndeterminateTime, session));
            }
            return observations;
        } else {
            return getObservationFor(request, features, filterCriterion, sosIndeterminateTime, session);
        }
    }

    protected Collection<Observation<?>> getObservationFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            List<Observation<?>> list = new LinkedList<>();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions.getSubQueryIdentifier(getResultFilterClasses())) {
                final Criteria c = getDefaultObservationCriteria(session);

                checkAndAddSpatialFilteringProfileCriterion(c, request, session);
                checkAndAddResultFilterCriterion(c, request, identifier, session);

                if (CollectionHelper.isNotEmpty(request.getProcedures())) {
                    c.createCriteria(AbstractLegacyObservation.PROCEDURE)
                            .add(Restrictions.in(Procedure.IDENTIFIER, request.getProcedures()));
                }

                if (CollectionHelper.isNotEmpty(request.getObservedProperties())) {
                    c.createCriteria(AbstractLegacyObservation.OBSERVABLE_PROPERTY)
                            .add(Restrictions.in(ObservableProperty.IDENTIFIER, request.getObservedProperties()));
                }

                if (CollectionHelper.isNotEmpty(features)) {
                    c.createCriteria(AbstractLegacyObservation.FEATURE_OF_INTEREST)
                            .add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
                }

                if (CollectionHelper.isNotEmpty(request.getOfferings())) {
                    c.createCriteria(AbstractLegacyObservation.OFFERINGS)
                            .add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
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
                if (request.isSetFesFilterExtension()) {
                    new ExtensionFesFilterCriteriaAdder(c, request.getFesFilterExtensions()).add();
                }
                LOGGER.debug("QUERY getSeriesObservationFor({}) and result filter sub query '{}': {}", logArgs,
                        identifier, HibernateHelper.getSqlString(c));
                list.addAll(c.list());
            }
            return list;
        }
        final Criteria c = getDefaultObservationCriteria(session);

        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        if (CollectionHelper.isNotEmpty(request.getProcedures())) {
            c.createCriteria(AbstractLegacyObservation.PROCEDURE)
                    .add(Restrictions.in(Procedure.IDENTIFIER, request.getProcedures()));
        }

        if (CollectionHelper.isNotEmpty(request.getObservedProperties())) {
            c.createCriteria(AbstractLegacyObservation.OBSERVABLE_PROPERTY)
                    .add(Restrictions.in(ObservableProperty.IDENTIFIER, request.getObservedProperties()));
        }

        if (CollectionHelper.isNotEmpty(features)) {
            c.createCriteria(AbstractLegacyObservation.FEATURE_OF_INTEREST)
                    .add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
        }

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            c.createCriteria(AbstractLegacyObservation.OFFERINGS)
                    .add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
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
        if (request.isSetFesFilterExtension()) {
            new ExtensionFesFilterCriteriaAdder(c, request.getFesFilterExtensions()).add();
        }
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c.list();
    }

    public Collection<Observation<?>> getObservationsFor(GetObservationRequest request, Set<String> features,
            Criterion filterCriterion, Session session)
            throws OwsExceptionReport {
        return getObservationsFor(request, features, filterCriterion, null, session);
    }

    public Collection<Observation<?>> getObservationsFor(GetObservationRequest request, Set<String> features,
            SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        return getObservationsFor(request, features, null, sosIndeterminateTime, session);
    }

    public Collection<Observation<?>> getObservationsFor(GetObservationRequest request, Set<String> features,
            Session session)
            throws OwsExceptionReport {
        return getObservationsFor(request, features, null, null, session);
    }

    @SuppressWarnings("unchecked")
    public Collection<? extends Observation<?>> getObservationsFor(ObservationConstellation oc,
            HashSet<String> features, GetObservationRequest request, SosIndeterminateTime sosIndeterminateTime,
            Session session)
            throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            Set<Observation<?>> set = new HashSet<>();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions.getSubQueryIdentifier(getResultFilterClasses())) {
                final Criteria c = getDefaultObservationsFor(oc, features, request, sosIndeterminateTime, session);
                checkAndAddResultFilterCriterion(c, request, identifier, session);
                String logArgs = "request, features, offerings";
                logArgs += ", sosIndeterminateTime";
                LOGGER.debug("QUERY getSeriesObservationFor({}) and result filter sub query '{}': {}", logArgs,
                        identifier, HibernateHelper.getSqlString(c));
                set.addAll(c.list());
            }
            return set;
        } else {
            final Criteria c = getDefaultObservationsFor(oc, features, request, sosIndeterminateTime, session);
            String logArgs = "request, features, offerings";
            logArgs += ", sosIndeterminateTime";
            LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
            return c.list();
        }
    }

    private Criteria getDefaultObservationsFor(ObservationConstellation oc, HashSet<String> features,
            GetObservationRequest request, SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        final Criteria c = getDefaultObservationCriteria(session);

        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        c.createCriteria(AbstractLegacyObservation.PROCEDURE)
                .add(Restrictions.eq(Procedure.ID, oc.getProcedure().getProcedureId()));

        c.createCriteria(AbstractLegacyObservation.OBSERVABLE_PROPERTY)
                .add(Restrictions.eq(ObservableProperty.ID, oc.getObservableProperty().getObservablePropertyId()));

        if (CollectionHelper.isNotEmpty(features)) {
            c.createCriteria(AbstractLegacyObservation.FEATURE_OF_INTEREST)
                    .add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
        }

        c.createCriteria(AbstractLegacyObservation.OFFERINGS)
                .add(Restrictions.eq(Offering.ID, oc.getOffering().getOfferingId()));

        addIndeterminateTimeRestriction(c, sosIndeterminateTime);
        return c;
    }

    public ScrollableResults getStreamingObservationsFor(GetObservationRequest request, Set<String> features,
            Criterion temporalFilterCriterion, Session session)
            throws HibernateException,
            OwsExceptionReport {
        return scroll(getScrollableObservationsFor(request, features, temporalFilterCriterion, null, session));
    }

    public ScrollableResults getStreamingObservationsFor(GetObservationRequest request, Set<String> features,
            Session session)
            throws HibernateException,
            OwsExceptionReport {
        return scroll(getScrollableObservationsFor(request, features, null, null, session));
    }

    public ScrollableResults getNotMatchingSeries(Set<Long> procedureIds, Set<Long> observablePropertyIds,
            Set<Long> featureIds, GetObservationRequest request, Set<String> features,
            Criterion temporalFilterCriterion, Session session)
            throws OwsExceptionReport {
        Criteria c = getScrollableObservationsFor(request, features, temporalFilterCriterion, null, session);
        addAliasAndNotRestrictionFor(c, procedureIds, observablePropertyIds, featureIds);
        return scroll(c);
    }

    @Override
    public ObservationFactory getObservationFactory() {
        return LegacyObservationFactory.getInstance();
    }

    protected static ScrollableResults scroll(Criteria c) {
        return c.setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
    }

    public ScrollableResults getNotMatchingSeries(Set<Long> procedureIds, Set<Long> observablePropertyIds,
            Set<Long> featureIds, GetObservationRequest request, Set<String> features, Session session)
            throws OwsExceptionReport {
        Criteria c = getScrollableObservationsFor(request, features, null, null, session);
        addAliasAndNotRestrictionFor(c, procedureIds, observablePropertyIds, featureIds);
        return scroll(c);
    }

    private Criteria getScrollableObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        final Criteria c = getDefaultObservationCriteria(session);

        checkAndAddSpatialFilteringProfileCriterion(c, request, session);
        checkAndAddResultFilterCriterion(c, request, null, session);

        if (CollectionHelper.isNotEmpty(request.getProcedures())) {
            c.createCriteria(AbstractLegacyObservation.PROCEDURE)
                    .add(Restrictions.in(Procedure.IDENTIFIER, request.getProcedures()));
        }

        if (CollectionHelper.isNotEmpty(request.getObservedProperties())) {
            c.createCriteria(AbstractLegacyObservation.OBSERVABLE_PROPERTY)
                    .add(Restrictions.in(ObservableProperty.IDENTIFIER, request.getObservedProperties()));
        }

        if (CollectionHelper.isNotEmpty(features)) {
            c.createCriteria(AbstractLegacyObservation.FEATURE_OF_INTEREST)
                    .add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
        }

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            c.createCriteria(AbstractLegacyObservation.OFFERINGS)
                    .add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
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
        if (request.isSetFesFilterExtension()) {
            new ExtensionFesFilterCriteriaAdder(c, request.getFesFilterExtensions()).add();
        }
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Geometry> getSamplingGeometries(String feature, Session session)
            throws OwsExceptionReport {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        criteria.createCriteria(AbstractObservation.FEATURE_OF_INTEREST)
                .add(eq(FeatureOfInterest.IDENTIFIER, feature));
        criteria.addOrder(Order.asc(AbstractTemporalReferencedObservation.PHENOMENON_TIME_START));
        if (HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(),
                AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY)) {
            criteria.add(Restrictions.isNotNull(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            criteria.setProjection(Projections.property(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            LOGGER.debug("QUERY getSamplingGeometries(feature): {}", HibernateHelper.getSqlString(criteria));
            return criteria.list();
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(),
                AbstractTemporalReferencedObservation.LONGITUDE)
                && HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(),
                        AbstractTemporalReferencedObservation.LATITUDE)) {
            criteria.add(Restrictions.and(Restrictions.isNotNull(AbstractTemporalReferencedObservation.LATITUDE),
                    Restrictions.isNotNull(AbstractTemporalReferencedObservation.LONGITUDE)));
            List<Geometry> samplingGeometries = Lists.newArrayList();
            LOGGER.debug("QUERY getSamplingGeometries(feature): {}", HibernateHelper.getSqlString(criteria));
            for (AbstractTemporalReferencedObservation element : (List<AbstractTemporalReferencedObservation>) criteria
                    .list()) {
                samplingGeometries.add(new HibernateGeometryCreator().createGeometry(element));
            }
            return samplingGeometries;
        }
        return Collections.emptyList();
    }

    @Override
    public Long getSamplingGeometriesCount(String feature, Session session)
            throws OwsExceptionReport {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        criteria.createCriteria(AbstractObservation.FEATURE_OF_INTEREST)
                .add(eq(FeatureOfInterest.IDENTIFIER, feature));
        criteria.setProjection(Projections.count(AbstractTemporalReferencedObservation.OBS_ID));
        if (GeometryHandler.getInstance().isSpatialDatasource()) {
            criteria.add(Restrictions.isNotNull(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            LOGGER.debug("QUERY getSamplingGeometriesCount(feature): {}", HibernateHelper.getSqlString(criteria));
            return (Long) criteria.uniqueResult();
        } else {
            criteria.add(Restrictions.and(Restrictions.isNotNull(AbstractTemporalReferencedObservation.LATITUDE),
                    Restrictions.isNotNull(AbstractTemporalReferencedObservation.LONGITUDE)));
            LOGGER.debug("QUERY getSamplingGeometriesCount(feature): {}", HibernateHelper.getSqlString(criteria));
            return (Long) criteria.uniqueResult();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Envelope getBboxFromSamplingGeometries(String feature, Session session)
            throws OwsExceptionReport {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        criteria.createCriteria(AbstractObservation.FEATURE_OF_INTEREST)
                .add(eq(FeatureOfInterest.IDENTIFIER, feature));
        if (GeometryHandler.getInstance().isSpatialDatasource()) {
            criteria.add(Restrictions.isNotNull(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
            if (HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                criteria.setProjection(
                        SpatialProjections.extent(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
                LOGGER.debug("QUERY getBboxFromSamplingGeometries(feature): {}",
                        HibernateHelper.getSqlString(criteria));
                return (Envelope) criteria.uniqueResult();
            }
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().temporalReferencedClass(),
                AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY)) {
            criteria.add(Restrictions.isNotNull(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            criteria.setProjection(Projections.property(AbstractTemporalReferencedObservation.SAMPLING_GEOMETRY));
            LOGGER.debug("QUERY getBboxFromSamplingGeometries(feature): {}", HibernateHelper.getSqlString(criteria));
            Envelope envelope = new Envelope();
            for (Geometry geom : (List<Geometry>) criteria.list()) {
                envelope.expandToInclude(geom.getEnvelopeInternal());
            }
            return envelope;
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().temporalReferencedClass(),
                AbstractTemporalReferencedObservation.LATITUDE)
                && HibernateHelper.isColumnSupported(getObservationFactory().temporalReferencedClass(),
                        AbstractTemporalReferencedObservation.LONGITUDE)) {
            criteria.add(Restrictions.and(Restrictions.isNotNull(AbstractTemporalReferencedObservation.LATITUDE),
                    Restrictions.isNotNull(AbstractTemporalReferencedObservation.LONGITUDE)));
            criteria.setProjection(
                    Projections.projectionList().add(Projections.min(AbstractTemporalReferencedObservation.LATITUDE))
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

    private void addAliasAndNotRestrictionFor(Criteria c, Set<Long> procedureIds, Set<Long> observablePropertyIds,
            Set<Long> featureIds) {
        c.createAlias(AbstractLegacyObservation.PROCEDURE, "p")
                .createAlias(AbstractLegacyObservation.OBSERVABLE_PROPERTY, "op")
                .createAlias(AbstractLegacyObservation.FEATURE_OF_INTEREST, "f");
        c.add(Restrictions.not(Restrictions.in("p." + Procedure.ID, procedureIds)));
        c.add(Restrictions.not(Restrictions.in("op." + ObservableProperty.ID, observablePropertyIds)));
        c.add(Restrictions.not(Restrictions.in("f." + FeatureOfInterest.ID, featureIds)));
    }

    @Override
    public String addProcedureAlias(Criteria criteria) {
        criteria.createAlias(Observation.PROCEDURE, Procedure.ALIAS);
        return Procedure.ALIAS_DOT;
    }

    @Override
    protected Criteria addAdditionalObservationIdentification(Criteria c, OmObservation sosObservation) {
        return c;
    }
}
