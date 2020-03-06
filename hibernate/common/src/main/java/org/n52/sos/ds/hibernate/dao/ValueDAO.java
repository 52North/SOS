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
package org.n52.sos.ds.hibernate.dao;

import java.util.Collection;
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
import org.n52.sos.ds.hibernate.dao.observation.AbstractValueDAO;
import org.n52.sos.ds.hibernate.dao.observation.ValuedObservationFactory;
import org.n52.sos.ds.hibernate.dao.observation.legacy.LegacyValuedObservationFactory;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractValuedLegacyObservation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.observation.ExtensionFesFilterCriteriaAdder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Implementation of {@link AbstractValueDAO} for old concept
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class ValueDAO extends AbstractValueDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValueDAO.class);

    /**
     * Query streaming value for parameter as {@link ScrollableResults}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting {@link ScrollableResults}
     * @throws HibernateException
     *             If an error occurs when querying the
     *             {@link AbstractValuedLegacyObservation}s
     * @throws OwsExceptionReport
     *             If an error occurs when querying the
     *             {@link AbstractValuedLegacyObservation}s
     */
    public ScrollableResults getStreamingValuesFor(GetObservationRequest request, long procedure,
            long observableProperty, long featureOfInterest, Criterion temporalFilterCriterion, Session session)
                    throws HibernateException, OwsExceptionReport {
        return getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, temporalFilterCriterion,
                session, new StringBuilder()).scroll(ScrollMode.FORWARD_ONLY);
    }

    /**
     * Query streaming value for parameter as {@link ScrollableResults}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure ids
     * @param observableProperty
     *            Datasource procedure ids
     * @param featureOfInterest
     *            Datasource procedure ids
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting {@link ScrollableResults}
     * @throws HibernateException
     *             If an error occurs when querying the
     *             {@link AbstractValuedLegacyObservation}s
     * @throws OwsExceptionReport
     *             If an error occurs when querying the
     *             {@link AbstractValuedLegacyObservation}s
     */
    public ScrollableResults getStreamingValuesFor(GetObservationRequest request, Set<Long> procedure,
            Set<Long> observableProperty, Set<Long> featureOfInterest, Criterion temporalFilterCriterion,
            Session session) throws HibernateException, OwsExceptionReport {
        return getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, temporalFilterCriterion,
                session, new StringBuilder()).scroll(ScrollMode.FORWARD_ONLY);
    }

    /**
     * Query streaming value for parameter as {@link ScrollableResults}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param session
     *            Hibernate Session
     * @return Resulting {@link ScrollableResults}
     * @throws OwsExceptionReport
     *             If an error occurs when querying the
     *             {@link AbstractValuedLegacyObservation}s
     */
    public ScrollableResults getStreamingValuesFor(GetObservationRequest request, long procedure,
            long observableProperty, long featureOfInterest, Session session) throws OwsExceptionReport {
        return getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, null, session, new StringBuilder())
                .scroll(ScrollMode.FORWARD_ONLY);
    }

    /**
     * Query streaming value for parameter as {@link ScrollableResults}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure ids
     * @param observableProperty
     *            Datasource procedure ids
     * @param featureOfInterest
     *            Datasource procedure ids
     * @param session
     *            Hibernate Session
     * @return Resulting {@link ScrollableResults}
     * @throws OwsExceptionReport
     *             If an error occurs when querying the
     *             {@link AbstractValuedLegacyObservation}s
     */
    public ScrollableResults getStreamingValuesFor(GetObservationRequest request, Set<Long> procedure,
            Set<Long> observableProperty, Set<Long> featureOfInterest, Session session) throws HibernateException, OwsExceptionReport {
        return getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, null, session, new StringBuilder())
                .scroll(ScrollMode.FORWARD_ONLY);
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param chunkSize
     *            chunk size
     * @param currentRow
     *            Start row
     * @param session
     *            Hibernate Session
     * @return Resulting chunk {@link List}
     * @throws OwsExceptionReport
     *             If an error occurs when querying the
     *             {@link AbstractValuedLegacyObservation}s
     */
    @SuppressWarnings("unchecked")
    public List<ValuedObservation<?>> getStreamingValuesFor(GetObservationRequest request, long procedure,
            long observableProperty, long featureOfInterest, Criterion temporalFilterCriterion, int chunkSize,
            int currentRow, Session session) throws OwsExceptionReport {
        StringBuilder logArgs = new StringBuilder();
        Criteria c = getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest,
                temporalFilterCriterion, session, logArgs);
        addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
        LOGGER.debug("QUERY getStreamingValuesFor({}): {}", logArgs.toString(), HibernateHelper.getSqlString(c));
        return (List<ValuedObservation<?>>) c.list();
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure ids
     * @param observableProperty
     *            Datasource procedure ids
     * @param featureOfInterest
     *            Datasource procedure ids
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param chunkSize
     *            chunk size
     * @param currentRow
     *            Start row
     * @param session
     *            Hibernate Session
     * @return Resulting chunk {@link List}
     * @throws OwsExceptionReport
     *             If an error occurs when querying the
     *             {@link AbstractValuedLegacyObservation}s
     */
    @SuppressWarnings("unchecked")
    public Collection<ValuedObservation<?>> getStreamingValuesFor(GetObservationRequest request, Set<Long> procedure,
            Set<Long> observableProperty, Set<Long> featureOfInterest, Criterion temporalFilterCriterion,
            int chunkSize, int currentRow, Session session) throws OwsExceptionReport {
        StringBuilder logArgs = new StringBuilder();
        Criteria c = getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest,
                temporalFilterCriterion, session, logArgs);
        addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
        LOGGER.debug("QUERY getStreamingValuesFor({}): {}", logArgs.toString(), HibernateHelper.getSqlString(c));
        return (List<ValuedObservation<?>>) c.list();
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param chunkSize
     *            Chunk size
     * @param currentRow
     *            Start row
     * @param session
     *            Hibernate Session
     * @return Resulting chunk {@link List}
     * @throws OwsExceptionReport
     *             If an error occurs when querying the
     *             {@link AbstractValuedLegacyObservation}s
     */
    @SuppressWarnings("unchecked")
    public List<ValuedObservation<?>> getStreamingValuesFor(GetObservationRequest request, long procedure,
            long observableProperty, long featureOfInterest, int chunkSize, int currentRow, Session session)
                    throws OwsExceptionReport {
        StringBuilder logArgs = new StringBuilder();
        Criteria c = getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, null, session, logArgs);
        addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
        LOGGER.debug("QUERY getStreamingValuesFor({}): {}", logArgs.toString(), HibernateHelper.getSqlString(c));
        return (List<ValuedObservation<?>>) c.list();
    }

    /**
     * Query streaming value for parameter as chunk {@link List}
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure ids
     * @param observableProperty
     *            Datasource procedure ids
     * @param featureOfInterest
     *            Datasource procedure ids
     * @param chunkSize
     *            Chunk size
     * @param currentRow
     *            Start row
     * @param session
     *            Hibernate Session
     * @return Resulting chunk {@link List}
     * @throws OwsExceptionReport
     *             If an error occurs when querying the
     *             {@link AbstractValuedLegacyObservation}s
     */
    @SuppressWarnings("unchecked")
    public Collection<ValuedObservation<?>> getStreamingValuesFor(GetObservationRequest request, Set<Long> procedure,
            Set<Long> observableProperty, Set<Long> featureOfInterest, int chunkSize, int currentRow,
            Session session) throws OwsExceptionReport {
        StringBuilder logArgs = new StringBuilder();
        Criteria c = getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, null, session, logArgs);
        addChunkValuesToCriteria(c, chunkSize, currentRow, request, logArgs);
        LOGGER.debug("QUERY getStreamingValuesFor({}): {}", logArgs.toString(), HibernateHelper.getSqlString(c));
        return (List<ValuedObservation<?>>) c.list();
    }

    /**
     * Get {@link Criteria} for parameter
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting {@link Criteria}
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile
     *             restrictions
     */
    private Criteria getValueCriteriaFor(GetObservationRequest request, long procedure, long observableProperty,
            long featureOfInterest, Criterion temporalFilterCriterion, Session session, StringBuilder logArgs) throws OwsExceptionReport {
        return getValueCriteriaFor(request, Sets.newHashSet(procedure), Sets.newHashSet(observableProperty),
                Sets.newHashSet(featureOfInterest), temporalFilterCriterion, session, logArgs);
    }

    /**
     * Get {@link Criteria} for parameter
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure ids
     * @param observableProperty
     *            Datasource procedure ids
     * @param featureOfInterest
     *            Datasource procedure ids
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     * @param session
     *            Hibernate Session
     * @return Resulting {@link Criteria}
     * @throws OwsExceptionReport
     *             If an error occurs when adding Spatial Filtering Profile
     *             restrictions
     */
    private Criteria getValueCriteriaFor(GetObservationRequest request, Set<Long> procedure,
            Set<Long> observableProperty, Set<Long> featureOfInterest, Criterion temporalFilterCriterion,
            Session session, StringBuilder logArgs) throws OwsExceptionReport {
        logArgs.append("request, series, offerings");
        final Criteria c = getDefaultObservationCriteria(AbstractValuedLegacyObservation.class, session);
        c.addOrder(Order.asc(getOrderColumn(request)));
        
        checkAndAddSpatialFilteringProfileCriterion(c, request, session, logArgs);
        checkAndAddResultFilterCriterion(c, request, null, session, logArgs);

        if (CollectionHelper.isNotEmpty(procedure)) {
            c.createAlias(AbstractValuedLegacyObservation.PROCEDURE, "p");
            c.add(Restrictions.in("p." + Procedure.ID, procedure));
        }
        if (CollectionHelper.isNotEmpty(observableProperty)) {
            c.createAlias(AbstractValuedLegacyObservation.OBSERVABLE_PROPERTY, "o");
            c.add(Restrictions.in("o." + ObservableProperty.ID, observableProperty));
        }
        if (CollectionHelper.isNotEmpty(featureOfInterest)) {
            c.createAlias(AbstractValuedLegacyObservation.FEATURE_OF_INTEREST, "f");
            c.add(Restrictions.in("f." + FeatureOfInterest.ID, featureOfInterest));
        }

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            c.createCriteria(AbstractValuedLegacyObservation.OFFERINGS)
                    .add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }

        if (temporalFilterCriterion != null) {
            logArgs.append(", filterCriterion");
            c.add(temporalFilterCriterion);
        }
        addSpecificRestrictions(c, request, logArgs);
        if (request.isSetFesFilterExtension()) {
            new ExtensionFesFilterCriteriaAdder(c, request.getFesFilterExtensions()).add();
        }
        LOGGER.debug("QUERY getValueCriteriaFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c.setReadOnly(true);
    }

    /**
     * Get default {@link Criteria} for {@link Class}
     *
     * @param clazz
     *            {@link Class} to get default {@link Criteria} for
     * @param session
     *            Hibernate Session
     * @return Default {@link Criteria}
     */
    public Criteria getDefaultObservationCriteria(Class<?> clazz, Session session) {
        Criteria criteria = session.createCriteria(clazz)
                .add(Restrictions.eq(Observation.DELETED, false));

        if (!isIncludeChildObservableProperties()) {
            criteria.add(Restrictions.eq(Observation.CHILD, false));
        } else {
            criteria.add(Restrictions.eq(Observation.PARENT, false));
        }
        criteria.setFetchMode("offerings", org.hibernate.FetchMode.JOIN);
        criteria.setFetchMode("parameters", org.hibernate.FetchMode.JOIN);
//        criteria.setFetchMode("value", org.hibernate.FetchMode.JOIN);
        return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Query unit for parameter
     *
     * @param request
     *            {@link GetObservationRequest}
     * @param procedure
     *            Datasource procedure id
     * @param observableProperty
     *            Datasource procedure id
     * @param featureOfInterest
     *            Datasource procedure id
     * @param session
     *            Hibernate Session
     * @return Unit or null if no unit is set
     * @throws OwsExceptionReport
     *             If an error occurs when querying the unit
     */
    public String getUnit(GetObservationRequest request, long procedure, long observableProperty,
            long featureOfInterest, Session session) throws OwsExceptionReport {
        return getUnit(request, Sets.newHashSet(procedure), Sets.newHashSet(observableProperty),
                Sets.newHashSet(featureOfInterest), session);
//        Criteria c = getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, null, session);
//        Unit unit = (Unit) c.setMaxResults(1).setProjection(Projections.property(AbstractValuedLegacyObservation.UNIT))
//                .uniqueResult();
//        if (unit != null && unit.isSetUnit()) {
//            return unit.getUnit();
//        }
//        return null;
    }

    public String getUnit(GetObservationRequest request, Set<Long> procedure, Set<Long> observableProperty,
            Set<Long> featureOfInterest, Session session) throws OwsExceptionReport {
        StringBuilder logArgs = new StringBuilder();
        Criteria c = getValueCriteriaFor(request, procedure, observableProperty, featureOfInterest, null, session, logArgs);
        Unit unit = (Unit) c.setMaxResults(1).setProjection(Projections.property(AbstractValuedLegacyObservation.UNIT))
                .uniqueResult();
        if (unit != null && unit.isSetUnit()) {
            return unit.getUnit();
        }
        return null;
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request, StringBuilder logArgs) throws CodedException {
        // nothing to add
    }

    @Override
    protected ValuedObservationFactory getValuedObservationFactory() {
        return LegacyValuedObservationFactory.getInstance();
    }

}
