/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.locationtech.jts.geom.Envelope;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.GeometryEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.data.Data;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ExtendedIndeterminateTime;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.n52.sos.ds.hibernate.util.observation.ExtensionFesFilterCriteriaAdder;
import org.n52.sos.util.JTSConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public abstract class AbstractSeriesObservationDAO extends AbstractObservationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeriesObservationDAO.class);

    public AbstractSeriesObservationDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    protected void addObservationContextToObservation(ObservationContext ctx,
            Data<?> observation, Session session) throws OwsExceptionReport {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        DatasetEntity series = seriesDAO.getOrInsertSeries(ctx, observation, session);
        ((DataEntity) observation).setDataset(series);

        OfferingDAO offeringDAO = getDaoFactory().getOfferingDAO();
        offeringDAO.updateOfferingMetadata(series.getOffering(), observation, session);
    }

    @Override
    public Criteria getObservationInfoCriteriaForFeatureOfInterestAndProcedure(String feature, String procedure,
            Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(DataEntity.PROPERTY_DATASET);
        seriesCriteria.createCriteria(DatasetEntity.PROPERTY_FEATURE).add(eq(AbstractFeatureEntity.IDENTIFIER, feature));
        seriesCriteria.createCriteria( DatasetEntity.PROPERTY_PROCEDURE).add(eq(ProcedureEntity.IDENTIFIER, procedure));

//        if (!isIncludeChildObservableProperties()) {
//            seriesCriteria.createCriteria(DataEntity.VALUE).createCriteria(DataEntity.PROPERTY_ID);
//        }

        return criteria;
    }

    @Override
    public Criteria getObservationInfoCriteriaForFeatureOfInterestAndOffering(String feature, String offering,
            Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(DataEntity.PROPERTY_DATASET);
        seriesCriteria.createCriteria(DatasetEntity.PROPERTY_FEATURE).add(eq(AbstractFeatureEntity.IDENTIFIER, feature));
        seriesCriteria.createCriteria(DatasetEntity.PROPERTY_OFFERING).add(eq(OfferingEntity.IDENTIFIER, feature));
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaForProcedure(String procedure, Session session) throws OwsExceptionReport {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesDAO.addProcedureToCriteria(seriesCriteria, procedure);
        return criteria;
    }

    private Criteria getDefaultSeriesObservationCriteria(Criteria criteria) {
        Criteria seriesCriteria = criteria.createCriteria(DataEntity.PROPERTY_DATASET);
        seriesCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, true));
        return seriesCriteria;
    }

    @Override
    public Criteria getObservationCriteriaForObservableProperty(String observableProperty, Session session)
            throws OwsExceptionReport {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesDAO.addObservablePropertyToCriteria(seriesCriteria, observableProperty);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaForFeatureOfInterest(String featureOfInterest, Session session)
            throws OwsExceptionReport {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesDAO.addFeatureOfInterestToCriteria(seriesCriteria, featureOfInterest);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaFor(String procedure, String observableProperty, Session session)
            throws OwsExceptionReport {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesDAO.addProcedureToCriteria(seriesCriteria, procedure);
        seriesDAO.addObservablePropertyToCriteria(seriesCriteria, observableProperty);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaFor(String procedure, String observableProperty, String featureOfInterest,
            Session session) throws OwsExceptionReport {
        Criteria criteria = getDefaultObservationCriteria(session);
        addRestrictionsToCriteria(criteria, procedure, observableProperty, featureOfInterest);
        return criteria;
    }


    @Override
    public Criteria getTemoralReferencedObservationCriteriaFor(OmObservation observation, DatasetEntity oc,
            Session session) throws OwsExceptionReport {
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
     * @throws OwsExceptionReport
     *             If an erro occurs
     */
    private Criteria addRestrictionsToCriteria(Criteria criteria, String procedure, String observableProperty,
            String featureOfInterest) throws OwsExceptionReport {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesDAO.addFeatureOfInterestToCriteria(seriesCriteria, featureOfInterest);
        seriesDAO.addProcedureToCriteria(seriesCriteria, procedure);
        seriesDAO.addObservablePropertyToCriteria(seriesCriteria, observableProperty);
        return seriesCriteria;
    }

    private Criteria addRestrictionsToCriteria(Criteria criteria, String procedure, String observableProperty,
            String featureOfInterest, String offering) throws OwsExceptionReport {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        Criteria seriesCriteria = criteria.createCriteria(DataEntity.PROPERTY_DATASET);
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
                        .setProjection(Projections.distinct(Projections.property(DataEntity.IDENTIFIER)))
                        .add(Restrictions.isNotNull(DataEntity.IDENTIFIER))
                        .add(Restrictions.eq(DataEntity.PROPERTY_DELETED, false));
        Criteria seriesCriteria = getDefaultSeriesObservationCriteria(criteria);
        seriesCriteria.createCriteria( DatasetEntity.PROPERTY_PROCEDURE)
                .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, procedureIdentifier));
        LOGGER.debug("QUERY getObservationIdentifiers(procedureIdentifier): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<org.locationtech.jts.geom.Geometry> getSamplingGeometries(String feature, Session session) throws OwsExceptionReport {
        Criteria criteria = getDefaultObservationTimeCriteria(session).createAlias(DataEntity.PROPERTY_DATASET, "s");
        criteria.createCriteria("s." + DatasetEntity.PROPERTY_FEATURE).add(eq(AbstractFeatureEntity.IDENTIFIER, feature));
        criteria.addOrder(Order.asc(DataEntity.PROPERTY_SAMPLING_TIME_START));
        if (HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), GeometryEntity.PROPERTY_GEOMETRY)) {
            criteria.add(Restrictions.isNotNull(DataEntity.PROPERTY_GEOMETRY_ENTITY));
            criteria.setProjection(Projections.property(DataEntity.PROPERTY_GEOMETRY_ENTITY));
            LOGGER.debug("QUERY getSamplingGeometries(feature): {}", HibernateHelper.getSqlString(criteria));
            return criteria.list();
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), GeometryEntity.PROPERTY_LON)
                && HibernateHelper.isColumnSupported(getObservationFactory().contextualReferencedClass(), GeometryEntity.PROPERTY_LAT)) {
            criteria.add(Restrictions.and(Restrictions.isNotNull(GeometryEntity.PROPERTY_LAT),
                    Restrictions.isNotNull(GeometryEntity.PROPERTY_LON)));
            List<org.locationtech.jts.geom.Geometry> samplingGeometries = new LinkedList<>();
            LOGGER.debug("QUERY getSamplingGeometries(feature): {}", HibernateHelper.getSqlString(criteria));
            for (DataEntity element : (List<DataEntity>)criteria.list()) {
                samplingGeometries.add(JTSConverter.convert(element.getGeometryEntity().getGeometry()));
            }
            return samplingGeometries;
        }
        return Collections.emptyList();
    }

    @Override
    public Long getSamplingGeometriesCount(String feature, Session session) throws OwsExceptionReport {
        Criteria criteria = getDefaultObservationTimeCriteria(session).createAlias(DataEntity.PROPERTY_DATASET, "s");
        criteria.createCriteria("s." + DatasetEntity.PROPERTY_FEATURE).add(eq(AbstractFeatureEntity.IDENTIFIER, feature));
        criteria.setProjection(Projections.count(DataEntity.PROPERTY_ID));
        if (getDaoFactory().getGeometryHandler().isSpatialDatasource()) {
            criteria.add(Restrictions.isNotNull(DataEntity.PROPERTY_GEOMETRY_ENTITY));
            LOGGER.debug("QUERY getSamplingGeometriesCount(feature): {}", HibernateHelper.getSqlString(criteria));
            return (Long)criteria.uniqueResult();
        } else {
            criteria.add(Restrictions.and(Restrictions.isNotNull(GeometryEntity.PROPERTY_ALT),
                    Restrictions.isNotNull(GeometryEntity.PROPERTY_LON)));
            LOGGER.debug("QUERY getSamplingGeometriesCount(feature): {}", HibernateHelper.getSqlString(criteria));
            return (Long)criteria.uniqueResult();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Envelope getBboxFromSamplingGeometries(String feature, Session session) throws OwsExceptionReport {
        Criteria criteria = getDefaultObservationTimeCriteria(session).createAlias(DataEntity.PROPERTY_DATASET, "s");
        criteria.createCriteria("s." + DatasetEntity.PROPERTY_FEATURE).add(eq(AbstractFeatureEntity.IDENTIFIER, feature));
        if (getDaoFactory().getGeometryHandler().isSpatialDatasource()) {
            criteria.add(Restrictions.isNotNull(DataEntity.PROPERTY_GEOMETRY_ENTITY));
            Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getServiceRegistry().getService( JdbcServices.class ).getDialect();
            if (HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                criteria.setProjection(SpatialProjections.extent(DataEntity.PROPERTY_GEOMETRY_ENTITY));
                LOGGER.debug("QUERY getBboxFromSamplingGeometries(feature): {}",
                        HibernateHelper.getSqlString(criteria));
                return JTSConverter.convert((com.vividsolutions.jts.geom.Envelope) criteria.uniqueResult());
            }
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().temporalReferencedClass(), DataEntity.PROPERTY_GEOMETRY_ENTITY)) {
            criteria.add(Restrictions.isNotNull(DataEntity.PROPERTY_GEOMETRY_ENTITY));
            criteria.setProjection(Projections.property(DataEntity.PROPERTY_GEOMETRY_ENTITY));
            LOGGER.debug("QUERY getBboxFromSamplingGeometries(feature): {}",
                    HibernateHelper.getSqlString(criteria));
            Envelope envelope = new Envelope();
            for (com.vividsolutions.jts.geom.Geometry geom : (List<com.vividsolutions.jts.geom.Geometry>) criteria.list()) {
                envelope.expandToInclude(JTSConverter.convert(geom.getEnvelopeInternal()));
            }
            return envelope;
        } else if (HibernateHelper.isColumnSupported(getObservationFactory().temporalReferencedClass(), GeometryEntity.PROPERTY_LAT)
                && HibernateHelper.isColumnSupported(getObservationFactory().temporalReferencedClass(), GeometryEntity.PROPERTY_LON)) {
            criteria.add(Restrictions.and(Restrictions.isNotNull(GeometryEntity.PROPERTY_LAT),
                    Restrictions.isNotNull(GeometryEntity.PROPERTY_LON)));
            criteria.setProjection(Projections.projectionList().add(Projections.min(GeometryEntity.PROPERTY_LAT))
                    .add(Projections.min(GeometryEntity.PROPERTY_LON))
                    .add(Projections.max(GeometryEntity.PROPERTY_LAT))
                    .add(Projections.max(GeometryEntity.PROPERTY_LON)));

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
    protected Criteria createCriteriaFor(Class<?> clazz, DatasetEntity series, List<String> offerings, Session session) {
        final Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(DataEntity.PROPERTY_DATASET)
                .add(Restrictions.eq(DatasetEntity.PROPERTY_ID, series.getId()))
                .add(Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, true));
        if (CollectionHelper.isNotEmpty(offerings)) {
            seriesCriteria.createCriteria(DatasetEntity.PROPERTY_OFFERING).add(Restrictions.in(OfferingEntity.IDENTIFIER, offerings));
        }
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
    protected Criteria createCriteriaFor(Class<?> clazz, DatasetEntity series, Session session) {
        final Criteria criteria = getDefaultObservationCriteria(session);
        criteria.createCriteria(DataEntity.PROPERTY_DATASET)
                .add(Restrictions.eq(DatasetEntity.PROPERTY_ID, series.getId()))
                .add(Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, true));
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
    public List<Date> getResultTimesForSeriesObservation(DatasetEntity series, List<String> offerings, Criterion filter,
            Session session) {
        final Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(DataEntity.PROPERTY_DATASET)
                .add(Restrictions.eq(DatasetEntity.PROPERTY_ID, series.getId()))
                .add(Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, true));
        if (CollectionHelper.isNotEmpty(offerings)) {
            seriesCriteria.createCriteria(DatasetEntity.PROPERTY_OFFERING).add(Restrictions.in(OfferingEntity.IDENTIFIER, offerings));
        }
        if (filter != null) {
            criteria.add(filter);
        }
        criteria.setProjection(Projections.distinct(Projections.property(DataEntity.PROPERTY_RESULT_TIME)));
        criteria.addOrder(Order.asc(DataEntity.PROPERTY_RESULT_TIME));
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
    public Criteria getMinMaxTimeCriteriaForSeriesObservation(DatasetEntity series, Collection<String> offerings,
            Session session) {
        final Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(DataEntity.PROPERTY_DATASET)
                .add(Restrictions.eq(DatasetEntity.PROPERTY_ID, series.getId()))
                .add(Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, true));
        if (CollectionHelper.isNotEmpty(offerings)) {
            seriesCriteria.createCriteria(DatasetEntity.PROPERTY_OFFERING).add(Restrictions.in(OfferingEntity.IDENTIFIER, offerings));
        }
        criteria.setProjection(Projections.projectionList()
                .add(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START))
                .add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END)));
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
    public Criteria getOfferingMinMaxTimeCriteriaForSeriesObservation(DatasetEntity series, Collection<String> offerings,
            Session session) {
        Criteria criteria = createCriteriaFor(getObservationFactory().temporalReferencedClass(), series, session);
        if (CollectionHelper.isNotEmpty(offerings)) {
            criteria.createCriteria(DataEntity.PROPERTY_DATASET).createCriteria( DatasetEntity.PROPERTY_OFFERING, "off").add(
                    Restrictions.in(OfferingEntity.IDENTIFIER, offerings));
        } else {
            criteria.createCriteria(DataEntity.PROPERTY_DATASET).createAlias( DatasetEntity.PROPERTY_OFFERING, "off");
        }
        criteria.setProjection(Projections.projectionList()
                        .add(Projections.groupProperty("off." + OfferingEntity.IDENTIFIER))
                        .add(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START))
                        .add(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END)));
        return criteria;
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
    protected List<DataEntity<?>> getSeriesObservationCriteriaFor(GetObservationRequest request, Collection<String> features,
                Criterion filterCriterion, IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport {

        if (request.hasResultFilter()) {
            List<DataEntity<?>> list = new LinkedList<>();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions.getSubQueryIdentifier(getResultFilterClasses())) {
                String logArgs = new String(identifier + ",");
                Criteria c = getDefaultSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session);
                checkAndAddResultFilterCriterion(c, request, identifier, session);
                LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs,
                        HibernateHelper.getSqlString(c));
                list.addAll(c.list());
            }
            return list;
        }
        String logArgs = new String();
        Criteria c = getDefaultSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session);
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs,
                HibernateHelper.getSqlString(c));
        return c.list();
    }

    private Criteria getDefaultSeriesObservationCriteriaFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, IndeterminateValue sosIndeterminateTime, Session session)
                    throws OwsExceptionReport {
        final Criteria observationCriteria = getDefaultObservationCriteria(session);

        Criteria seriesCriteria = observationCriteria.createCriteria(DataEntity.PROPERTY_DATASET);

        checkAndAddSpatialFilteringProfileCriterion(observationCriteria, request, session);
        StringBuilder logArgs = new StringBuilder("");

        addSpecificRestrictions(seriesCriteria, request, logArgs);
        List<String> params = new LinkedList<>();
        if (CollectionHelper.isNotEmpty(request.getProcedures())) {
            seriesCriteria.createCriteria( DatasetEntity.PROPERTY_PROCEDURE)
                    .add(Restrictions.in(ProcedureEntity.IDENTIFIER, request.getProcedures()));
            params.add("procedure");
        }

        if (CollectionHelper.isNotEmpty(request.getObservedProperties())) {
            seriesCriteria.createCriteria(DatasetEntity.PROPERTY_PHENOMENON)
                    .add(Restrictions.in(PhenomenonEntity.IDENTIFIER, request.getObservedProperties()));
            params.add("phenomenon");
        }

        if (CollectionHelper.isNotEmpty(features)) {
            seriesCriteria.createCriteria(DatasetEntity.PROPERTY_FEATURE)
                    .add(Restrictions.in(AbstractFeatureEntity.IDENTIFIER, features));
            params.add("feature");
        }

        if (CollectionHelper.isNotEmpty(request.getOfferings())) {
            observationCriteria.createCriteria(DatasetEntity.PROPERTY_OFFERING)
                    .add(Restrictions.in(OfferingEntity.IDENTIFIER, request.getOfferings()));
            params.add("offering");
        }

        if (filterCriterion != null) {
            observationCriteria.add(filterCriterion);
            params.add("filterCriterion");
        }
        if (sosIndeterminateTime != null) {
            addIndeterminateTimeRestriction(observationCriteria, sosIndeterminateTime);
            params.add("sosIndeterminateTime");
        }
        logArgs.append(Joiner.on(", ").join(params));
        if (request.isSetFesFilterExtension()) {
            new ExtensionFesFilterCriteriaAdder(observationCriteria, request.getFesFilterExtensions()).add();
        }
        observationCriteria.setFetchMode(DataEntity.PROPERTY_PARAMETERS, FetchMode.JOIN);
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs,
                HibernateHelper.getSqlString(observationCriteria));
        return observationCriteria;
    }


    private String createSeriesAliasAndRestrictions(Criteria c) {
        String alias = "s";
        String aliasWithDot = alias + ".";
        c.createAlias(DataEntity.PROPERTY_DATASET, alias);
        c.add(Restrictions.eq(aliasWithDot + DatasetEntity.PROPERTY_DELETED, false));
        c.add(Restrictions.eq(aliasWithDot + DatasetEntity.PROPERTY_PUBLISHED, true));
        return aliasWithDot;
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
    public void updateObservationSetAsDeletedForSeries(List<DatasetEntity> series, boolean deleteFlag, Session session) {
        if (CollectionHelper.isNotEmpty(series)) {
            Criteria criteria = getDefaultObservationCriteria(session);
            criteria.add(Restrictions.in(DataEntity.PROPERTY_DATASET, series));
            ScrollableIterable<DataEntity<?>> scroll = ScrollableIterable.fromCriteria(criteria);
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
    public DateTime getMinSeriesObservationTime(DatasetEntity series, Session session) {
        Criteria criteria = createCriteriaFor(getObservationFactory().temporalReferencedClass(), series, session);
        criteria.setProjection(Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START));
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
    public DateTime getMaxSeriesObservationTime(DatasetEntity series, Session session) {
        Criteria criteria = createCriteriaFor(getObservationFactory().temporalReferencedClass(), series, session);
        criteria.setProjection(Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END));
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
    public abstract List<DataEntity<?>> getSeriesObservationFor(DatasetEntity series, List<String> offerings, Session session);

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
    public abstract List<DataEntity<?>> getSeriesObservationFor(DatasetEntity series, List<String> offerings, Criterion filterCriterion, Session session);

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
    public abstract List<DataEntity<?>> getSeriesObservationForExtendedIndeterminateTimeFilter(DatasetEntity series, List<String> offerings, IndeterminateValue sosIndeterminateTime, Session session);

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
    public abstract List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features, Session session) throws OwsExceptionReport;

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
    public abstract List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features, Criterion filterCriterion, Session session) throws OwsExceptionReport;

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
    public abstract List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features, IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport;

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
    protected abstract List<? extends DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features, Criterion filterCriterion, IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport;


    public abstract List<DataEntity<?>> getSeriesObservationsFor(DatasetEntity series, GetObservationRequest request, IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport;

    protected abstract void addSpecificRestrictions(Criteria c, GetObservationRequest request, StringBuilder logArgs) throws OwsExceptionReport;

    protected List<DataEntity<?>> getSeriesObservationCriteriaFor(DatasetEntity series, GetObservationRequest request,
            IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            List<DataEntity<?>> list = new LinkedList<>();
            for (SubQueryIdentifier identifier : ResultFilterRestrictions.getSubQueryIdentifier(getResultFilterClasses())) {
                final Criteria c = getDefaultObservationCriteria(session)
                        .add(Restrictions.eq(DataEntity.PROPERTY_DATASET, series));
                checkAndAddSpatialFilteringProfileCriterion(c, request, session);
                checkAndAddResultFilterCriterion(c, request, identifier, session);

                String logArgs = "request, features, offerings";
                logArgs += ", sosIndeterminateTime";
                if (series.isSetFirstValueAt() && sosIndeterminateTime.equals(ExtendedIndeterminateTime.FIRST)) {
                    addIndeterminateTimeRestriction(c, sosIndeterminateTime, series.getFirstValueAt());
                } else if (series.isSetLastValueAt()
                        && sosIndeterminateTime.equals(ExtendedIndeterminateTime.LATEST)) {
                    addIndeterminateTimeRestriction(c, sosIndeterminateTime, series.getLastValueAt());
                } else {
                    addIndeterminateTimeRestriction(c, sosIndeterminateTime);
                }
                LOGGER.debug("QUERY getSeriesObservationFor({}) and result filter sub query '{}': {}", logArgs,
                        identifier.name(), HibernateHelper.getSqlString(c));
                list.addAll(c.list());
            }
            return list;
        }
        final Criteria c =
                getDefaultObservationCriteria(session).add(Restrictions.eq(DataEntity.PROPERTY_DATASET, series));
        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        String logArgs = "request, features, offerings";
        logArgs += ", sosIndeterminateTime";
        if (series.isSetFirstValueAt() && sosIndeterminateTime.equals(ExtendedIndeterminateTime.FIRST)) {
            addIndeterminateTimeRestriction(c, sosIndeterminateTime, series.getFirstValueAt());
        } else if (series.isSetLastValueAt() && sosIndeterminateTime.equals(ExtendedIndeterminateTime.LATEST)) {
            addIndeterminateTimeRestriction(c, sosIndeterminateTime, series.getLastValueAt());
        } else {
            addIndeterminateTimeRestriction(c, sosIndeterminateTime);
        }
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c.list();
    }

    protected Criteria getSeriesObservationCriteriaForIndeterminateTimeFilter(DatasetEntity series,
            List<String> offerings, IndeterminateValue sosIndeterminateTime, Session session) {
        final Criteria criteria = createCriteriaFor(getObservationFactory().observationClass(), series, offerings, session);
        criteria.addOrder(getOrder(sosIndeterminateTime)).setMaxResults(1);
        LOGGER.debug("QUERY getSeriesObservationForExtendedIndeterminateTimeFilter(series, offerings,(first,latest)): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria;
    }

    protected Criteria getSeriesObservationCriteriaFor(DatasetEntity series, List<String> offerings,
            Criterion filterCriterion, Session session) {
        final Criteria criteria = createCriteriaFor(getObservationFactory().observationClass(), series, offerings, session);
        criteria.add(filterCriterion);
        LOGGER.debug("QUERY getSeriesObservationFor(series, offerings, temporalFilter): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria;
    }

    protected Criteria getSeriesObservationCriteriaFor(DatasetEntity series, List<String> offerings,
            Session session) {
        final Criteria criteria = createCriteriaFor(DataEntity.class, series, offerings, session);
        LOGGER.debug("QUERY getSeriesObservationFor(series, offerings): {}", HibernateHelper.getSqlString(criteria));
        return criteria;
    }

    @Override
    public String addProcedureAlias(Criteria criteria) {
        criteria.createAlias(DataEntity.PROPERTY_DATASET, "ds");
        criteria.createAlias("ds." +  DatasetEntity.PROPERTY_PROCEDURE, "proc");
        return "proc.";
    }


    /**
     * Get the first not deleted observation for the {@link DatasetEntity}
     *
     * @param series
     *            Series to get observation for
     * @param session
     *            Hibernate session
     * @return First not deleted observation
     */
    public DataEntity<?> getFirstObservationFor(DatasetEntity series, Session session) {
        Criteria c = getDefaultObservationCriteria(session);
        c.add(Restrictions.eq(DataEntity.PROPERTY_DATASET, series));
        c.addOrder(Order.asc(DataEntity.PROPERTY_SAMPLING_TIME_START));
        c.setMaxResults(1);
         LOGGER.debug("QUERY getFirstObservationFor(series): {}",
                    HibernateHelper.getSqlString(c));
        return (DataEntity)c.uniqueResult();
    }

    /**
     * Get the last not deleted observation for the {@link DatasetEntity}
     *
     * @param series
     *            Series to get observation for
     * @param session
     *            Hibernate session
     * @return Last not deleted observation
     */
    public DataEntity<?> getLastObservationFor(DatasetEntity series, Session session) {
        Criteria c = getDefaultObservationCriteria(session);
        c.add(Restrictions.eq(DataEntity.PROPERTY_DATASET, series));
        c.addOrder(Order.desc(DataEntity.PROPERTY_SAMPLING_TIME_END));
        c.setMaxResults(1);
         LOGGER.debug("QUERY getLastObservationFor(series): {}",
                    HibernateHelper.getSqlString(c));
        return (DataEntity)c.uniqueResult();
    }

    public List<String> getOfferingsForSeries(DatasetEntity series, Session session) {
        return Lists.newArrayList(series.getOffering().getIdentifier());
    }

    public ScrollableResults getObservations(Set<String> procedure, Set<String> observableProperty,
            Set<String> featureOfInterest, Set<String> offering, Criterion filterCriterion, Session session) {
        Criteria c = getDefaultObservationCriteria(session);
        String seriesAliasPrefix = createSeriesAliasAndRestrictions(c);
        if (CollectionHelper.isNotEmpty(procedure)) {
            c.createCriteria(seriesAliasPrefix + DatasetEntity.PROPERTY_PROCEDURE).add(Restrictions.in(ProcedureEntity.IDENTIFIER, procedure));
        }

        if (CollectionHelper.isNotEmpty(observableProperty)) {
            c.createCriteria(seriesAliasPrefix + DatasetEntity.PROPERTY_PHENOMENON).add(Restrictions.in(PhenomenonEntity.IDENTIFIER,
                    observableProperty));
        }

        if (CollectionHelper.isNotEmpty(featureOfInterest)) {
            c.createCriteria(seriesAliasPrefix + DatasetEntity.PROPERTY_FEATURE).add(Restrictions.in(AbstractFeatureEntity.IDENTIFIER, featureOfInterest));
        }

        if (CollectionHelper.isNotEmpty(offering)) {
            c.createCriteria(seriesAliasPrefix + DatasetEntity.PROPERTY_OFFERING).add(Restrictions.in(OfferingEntity.IDENTIFIER, featureOfInterest));
        }
        String logArgs = "request, features, offerings";
        if (filterCriterion != null) {
            logArgs += ", filterCriterion";
            c.add(filterCriterion);
        }
        LOGGER.debug("QUERY getObservations({}): {}", logArgs, HibernateHelper.getSqlString(c));

        return c.scroll(ScrollMode.FORWARD_ONLY);
    }


}
