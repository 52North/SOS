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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.locationtech.jts.geom.Geometry;
import org.n52.iceland.service.ServiceConfiguration;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.QuantityDatasetEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.data.Data;
import org.n52.series.db.beans.dataset.Dataset;
import org.n52.series.db.beans.dataset.NotInitializedDataset;
import org.n52.shetland.ogc.filter.ComparisonFilter;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.dao.AbstractIdentifierNameDescriptionDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FormatDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationSettingProvider;
import org.n52.sos.ds.hibernate.util.ResultFilterClasses;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.ds.hibernate.util.TimeExtrema;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public abstract class AbstractSeriesDAO
        extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeriesDAO.class);

    public AbstractSeriesDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    public abstract Class<?> getSeriesClass();

    public abstract Class<?> getNotInitializedDatasetClass();

    /**
     * Get series for GetObservation request and featuresOfInterest
     *
     * @param request
     *            GetObservation request to get series for
     * @param features
     *            FeaturesOfInterest to get series for
     * @param session
     *            Hibernate session
     *
     * @return Series that fit
     *
     * @throws OwsExceptionReport
     */
    public abstract List<DatasetEntity> getSeries(GetObservationRequest request, Collection<String> features, Session session)
            throws OwsExceptionReport;

    /**
     * Get series for GetObservationByIdRequest request
     *
     * @param request
     *            GetObservationByIdRequest request to get series for
     * @param session
     *            Hibernate session
     * @return Series that fit
     * @throws CodedException
     */
    public abstract List<DatasetEntity> getSeries(GetObservationByIdRequest request, Session session)
            throws OwsExceptionReport;

    /**
     * Get series for series identifiers
     * @param identifiers Series identifiers to get series for
     * @param session
     *            Hibernate session
     * @return Series that fit
     * @throws CodedException
     */
    public abstract List<DatasetEntity> getSeries(Collection<String> identifiers, Session session)
            throws OwsExceptionReport;

    /**
     * Get series for GetDataAvailability request
     * @param request GetDataAvailability request to get series for
     * @param session Hibernate session
     * @return Series that fit
     * @throws CodedException
     */
    public abstract List<DatasetEntity> getSeries(GetDataAvailabilityRequest request, Session session)
            throws OwsExceptionReport;

    public abstract List<DatasetEntity> getSeries(GetResultRequest request, Collection<String> featureIdentifiers,
            Session session) throws OwsExceptionReport;
    /**
     * Query series for observedProiperty and featuresOfInterest
     *
     * @param observedProperty
     *            ObservedProperty to get series for
     * @param features
     *            FeaturesOfInterest to get series for
     * @param session
     *            Hibernate session
     *
     * @return Series list
     */
    public abstract List<DatasetEntity> getSeries(String observedProperty, Collection<String> features, Session session);

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
    public abstract List<DatasetEntity> getSeries(String procedure, String observedProperty, String offering,
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
     *
     * @return Series that fit
     */
    public abstract List<DatasetEntity> getSeries(Collection<String> procedures, Collection<String> observedProperties,
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
    public abstract List<DatasetEntity> getSeries(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> featuresOfInterest, Collection<String> offerings, Session session)
            throws OwsExceptionReport;

    /**
     * Get series for procedure, observableProperty and featureOfInterest
     *
     * @param procedure
     *            Procedure identifier parameter
     * @param observableProperty
     *            ObservableProperty identifier parameter
     * @param featureOfInterest
     *            AbstractFeatureEntity identifier parameter
     * @param session
     *            Hibernate session
     *
     * @return Matching series
     */
    public abstract DatasetEntity getSeriesFor(String procedure, String observableProperty, String featureOfInterest,
            Session session);

    public abstract List<DatasetEntity> getSeries(String procedure, String observableProperty, Session session);

    /**
     * Insert or update and get series for procedure, observable property and
     * featureOfInterest
     *
     * @param identifiers
     *            identifiers object
     * @param observation
     * @param session
     *            Hibernate session
     *
     * @return Series object
     *
     * @throws OwsExceptionReport
     */
    public abstract DatasetEntity getOrInsertSeries(ObservationContext ctx, Data<?> observation, final Session session)
            throws OwsExceptionReport;

    protected abstract void addSpecificRestrictions(Criteria c, GetObservationRequest request)
            throws OwsExceptionReport;

    public abstract ObservationFactory getObservationFactory();

    public abstract DatasetFactory getDatasetFactory();

    public DatasetEntity getOrInsert(ObservationContext ctx, final Session session) throws OwsExceptionReport {
        return getOrInsert(ctx, null, session);
    }

    protected DatasetEntity getOrInsert(ObservationContext ctx, Data<?> observation, Session session) throws OwsExceptionReport {
        Criteria criteria = getDefaultAllSeriesCriteria(session);
        ctx.addIdentifierRestrictionsToCritera(criteria);
        criteria.setMaxResults(1);
        LOGGER.debug("QUERY getOrInsertSeries(feature, observableProperty, procedure, offering): {}",
                HibernateHelper.getSqlString(criteria));
        DatasetEntity series = (DatasetEntity) criteria.uniqueResult();
        if (series == null || series instanceof NotInitializedDataset) {
            series = preCheckDataset(ctx, observation, series, session);
        }
        if (series == null || (series.isSetFeature() && !series.getFeature().getIdentifier().equals(ctx.getFeatureOfInterest().getIdentifier()))) {
            series = (DatasetEntity) getDatasetFactory().visit(observation);
            ctx.addValuesToSeries(series);
            series.setDeleted(false);
            series.setPublished(ctx.isPublish());
        } else if (!series.isSetFeature()) {
            ctx.addValuesToSeries(series);
            series.setDeleted(false);
            series.setPublished(ctx.isPublish());
        } else if (ctx.isPublish() && !series.isPublished()) {
            series.setPublished(ctx.isPublish());
        } else if (series.isDeleted()) {
            series.setDeleted(false);
        } else {
            return series;
        }
        session.saveOrUpdate(series);
        session.flush();
        return series;
    }

    private DatasetEntity preCheckDataset(ObservationContext ctx, Data<?> observation, DatasetEntity dataset, Session session) throws OwsExceptionReport {
        if (dataset == null) {
            Criteria criteria = getDefaultNotDefinedDatasetCriteria(session);
            ctx.addIdentifierRestrictionsToCritera(criteria, false);
            LOGGER.debug("QUERY preCheckDataset(observableProperty, procedure, offering): {}",
                    HibernateHelper.getSqlString(criteria));
            dataset = (DatasetEntity) criteria.uniqueResult();
        }
        if (dataset != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("update ")
                .append(getSeriesClass().getSimpleName())
                .append(" set valueType = :valueType")
                .append(" where id = :id");
            session.createQuery(builder.toString())
                .setParameter( "valueType", getDatasetFactory().visit(observation).getValueType())
                .setParameter( "id", dataset.getId())
                .executeUpdate();
            session.flush();
        }
        return dataset;
    }

    /**
     * Check and Update and/or get observation constellation objects
     *
     * @param sosOC
     *            SOS observation constellation
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @param parameterName
     *            Parameter name for exception
     * @return Observation constellation object
     * @throws OwsExceptionReport
     *             If the requested observation type is invalid
     */
    public DatasetEntity checkSeries(OmObservationConstellation sosOC, String offering,
            Session session, String parameterName) throws OwsExceptionReport {
        AbstractPhenomenon observableProperty = sosOC.getObservableProperty();
        String observablePropertyIdentifier = observableProperty.getIdentifier();

        Criteria c = session.createCriteria(getSeriesClass())
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        c.createCriteria( DatasetEntity.PROPERTY_OFFERING).add(Restrictions.eq(OfferingEntity.IDENTIFIER, offering));
        c.createCriteria(DatasetEntity.PROPERTY_PHENOMENON)
                .add(Restrictions.eq(PhenomenonEntity.IDENTIFIER, observablePropertyIdentifier));

        if (sosOC.isSetProcedure()) {
            c.createCriteria( DatasetEntity.PROPERTY_PROCEDURE)
                    .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, sosOC.getProcedureIdentifier()));
        }

        LOGGER.debug("QUERY checkObservationConstellation(sosObservationConstellation, offering): {}",
                HibernateHelper.getSqlString(c));
        List<DatasetEntity> hocs = c.list();

        if (hocs == null || hocs.isEmpty()) {
            throw new InvalidParameterValueException().at(Sos2Constants.InsertObservationParams.observation)
                    .withMessage(
                            "The requested observation constellation (procedure=%s, observedProperty=%s and offering=%s) is invalid!",
                            sosOC.getProcedureIdentifier(), observablePropertyIdentifier, sosOC.getOfferings());
        }
        String observationType = sosOC.getObservationType();

        DatasetEntity hObsConst = null;
        for (DatasetEntity hoc : hocs) {
            if (!checkObservationType(hoc, observationType, session)) {
                throw new InvalidParameterValueException().at(parameterName).withMessage(
                        "The requested observationType (%s) is invalid for procedure = %s, observedProperty = %s and offering = %s! The valid observationType is '%s'!",
                        observationType, sosOC.getProcedureIdentifier(), observablePropertyIdentifier,
                        sosOC.getOfferings(), hoc.getObservationType().getFormat());
            }
            if (hObsConst == null) {
                if (sosOC.isSetProcedure()) {
                    if (hoc.getProcedure().getIdentifier().equals(sosOC.getProcedureIdentifier())) {
                        hObsConst = hoc;
                    }
                } else {
                    hObsConst = hoc;
                }
            }

            // add parent/childs
//            if (observableProperty instanceof OmCompositePhenomenon) {
//                OmCompositePhenomenon omCompositePhenomenon = (OmCompositePhenomenon) observableProperty;
//                ObservablePropertyDAO dao = new ObservablePropertyDAO(getDaoFactory());
//                Map<String, PhenomenonEntity> obsprop =
//                        dao.getOrInsertObservablePropertyAsMap(Arrays.asList(observableProperty), false, session);
//                for (OmObservableProperty child : omCompositePhenomenon) {
//                    checkOrInsertSeries(hoc.getProcedure(), obsprop.get(child.getIdentifier()),
//                            hoc.getOffering(), true, session);
//                }
//            }
        }
        return hObsConst;
    }

    private DatasetEntity getSeriesImpl() throws OwsExceptionReport {
        try {
            return (DatasetEntity) getSeriesClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage("Error while creating an instance of %s",
                    getSeriesClass().getCanonicalName());
        }
    }

    public List<DatasetEntity> getSeries(Session session) {
        return getDefaultSeriesCriteria(session).list();
    }

    @SuppressWarnings("unchecked")
    public Set<DatasetEntity> getSeriesSet(GetObservationRequest request, Collection<String> features,
            Session session)
            throws OwsExceptionReport {
        Set<DatasetEntity> set = new LinkedHashSet<>();
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
    protected Set<DatasetEntity> getSeriesCriteria(GetDataAvailabilityRequest request, Session session)
            throws OwsExceptionReport {
        Set<DatasetEntity> set = new LinkedHashSet<>();
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
        c.add(Restrictions.in(DatasetEntity.IDENTIFIER, identifiers));
        LOGGER.debug("QUERY getSeriesCriteria(request): {}", HibernateHelper.getSqlString(c));
        return c;
    }


    protected Criteria getSeriesCriteria(GetResultRequest request, Collection<String> features, Session session) throws OwsExceptionReport {
        final Criteria c = createCriteriaFor(request.getObservedProperty(), request.getOffering(), features,
              session);
//        checkAndAddResultFilterCriterion(c, request, session);
//        checkAndAddSpatialFilterCriterion(c, request, session);
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
        if (!Strings.isNullOrEmpty(observedProperty)) {
            addObservablePropertyToCriteria(c, observedProperty);
        }
        return c;
    }

    public Criteria getSeriesCriteria(String procedure, String observedProperty, String offering,
            Collection<String> features, Session session) {
        final Criteria c = getDefaultSeriesCriteria(session);
        if (CollectionHelper.isNotEmpty(features)) {
            addFeatureOfInterestToCriteria(c, features);
        }
        if (!Strings.isNullOrEmpty(observedProperty)) {
            addObservablePropertyToCriteria(c, observedProperty);
        }
        if (!Strings.isNullOrEmpty(offering)) {
            addOfferingToCriteria(c, offering);
        }
        if (!Strings.isNullOrEmpty(procedure)) {
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

    public Criteria getSeriesCriteriaFor(String procedure, String observableProperty,
            Session session) {
        final Criteria c = createCriteriaFor(procedure, observableProperty, session);
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
     *            AbstractFeatureEntity identifier to add
     */
    public void addFeatureOfInterestToCriteria(Criteria c, String feature) {
        c.createCriteria(DatasetEntity.PROPERTY_FEATURE, "foi").add(Restrictions.eq(AbstractFeatureEntity.IDENTIFIER, feature));

    }

    /**
     * Add featureOfInterest restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param feature
     *            AbstractFeatureEntity to add
     */
    public void addFeatureOfInterestToCriteria(Criteria c, AbstractFeatureEntity feature) {
        c.add(Restrictions.eq(DatasetEntity.PROPERTY_FEATURE, feature));

    }

    /**
     * Add featuresOfInterest restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param features
     *            AbstractFeatureEntity identifiers to add
     */
    public void addFeatureOfInterestToCriteria(Criteria c, Collection<String> features) {
        c.createCriteria(DatasetEntity.PROPERTY_FEATURE, "foi").add(Restrictions.in(AbstractFeatureEntity .IDENTIFIER, features));

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
        c.createCriteria(DatasetEntity.PROPERTY_PHENOMENON)
                .add(Restrictions.eq(PhenomenonEntity.IDENTIFIER, observedProperty));
    }

    /**
     * Add observedProperty restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param observedProperty
     *            ObservableProperty to add
     */
    public void addObservablePropertyToCriteria(Criteria c, PhenomenonEntity observedProperty) {
        c.add(Restrictions.eq(DatasetEntity.PROPERTY_PHENOMENON, observedProperty));
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
        c.createCriteria(DatasetEntity.PROPERTY_PHENOMENON)
                .add(Restrictions.in(PhenomenonEntity.IDENTIFIER, observedProperties));
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
        c.createCriteria( DatasetEntity.PROPERTY_PROCEDURE).add(Restrictions.eq(ProcedureEntity.IDENTIFIER, procedure));
    }

    /**
     * Add procedure restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param procedure
     *            Procedure to add
     */
    public void addProcedureToCriteria(Criteria c, ProcedureEntity procedure) {
        c.add(Restrictions.eq( DatasetEntity.PROPERTY_PROCEDURE, procedure));

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
        c.createCriteria( DatasetEntity.PROPERTY_PROCEDURE).add(Restrictions.in(ProcedureEntity.IDENTIFIER, procedures));

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
        c.createCriteria(DatasetEntity.PROPERTY_OFFERING).add(Restrictions.in(OfferingEntity.IDENTIFIER, offerings));

    }

    public void addOfferingToCriteria(Criteria c, String offering) {
        c.createCriteria(DatasetEntity.PROPERTY_OFFERING).add(Restrictions.eq(OfferingEntity.IDENTIFIER, offering));
    }

    public void addOfferingToCriteria(Criteria c, OfferingEntity offering) {
        c.add(Restrictions.eq( DatasetEntity.PROPERTY_PROCEDURE, offering));
    }

    /**
     * Get default Hibernate Criteria for querying series, deleted flag ==
     * <code>false</code>
     *
     * @param session
     *            Hibernate Session
     *
     * @return Default criteria
     */
    public Criteria getDefaultSeriesCriteria(Session session) {
        Criteria c = session.createCriteria(getSeriesClass()).add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false))
                .add(Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, true)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if (!isIncludeChildObservableProperties()) {
            c.add(Restrictions.eq(DatasetEntity.HIDDEN_CHILD, false));
        }
        return c;
    }

    /**
     * Get default Hibernate Criteria for querying all series
     *
     * @param session
     *            Hibernate Session
     *
     * @return Default criteria
     */
    public Criteria getDefaultAllSeriesCriteria(Session session) {
        return session.createCriteria(getSeriesClass()).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }


    public Criteria getDefaultNotDefinedDatasetCriteria(Session session) {
        return session.createCriteria(getNotInitializedDatasetClass()).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
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
     *
     * @return Updated Series
     */
    @SuppressWarnings("unchecked")
    public List<DatasetEntity> updateSeriesSetAsDeletedForProcedureAndGetSeries(String procedure, boolean deleteFlag,
            Session session) {
        Criteria criteria = getDefaultAllSeriesCriteria(session);
        addProcedureToCriteria(criteria, procedure);
        List<DatasetEntity> hSeries = criteria.list();
        for (DatasetEntity series : hSeries) {
            series.setDeleted(deleteFlag);
            series.setPublished(deleteFlag);
            series.setFirstObservation(null);
            series.setFirstValueAt(null);
            series.setLastObservation(null);
            series.setLastValueAt(null);
            if (series instanceof QuantityDatasetEntity) {
                series.setFirstQuantityValue(null);
                series.setLastQuantityValue(null);
            }
            session.saveOrUpdate(series);
        }
        session.flush();
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
    public void updateSeriesWithFirstLatestValues(DatasetEntity series, DataEntity<?> hObservation, Session session) {
        boolean minChanged = false;
        boolean maxChanged = false;
        if (!series.isSetFirstValueAt() || (series.isSetFirstValueAt()
                && series.getFirstValueAt().after(hObservation.getSamplingTimeStart()))) {
            minChanged = true;
            series.setFirstValueAt(hObservation.getSamplingTimeStart());
            series.setFirstObservation(hObservation);
        }
        if (!series.isSetLastValueAt() || (series.isSetLastValueAt()
                && series.getLastValueAt().before(hObservation.getSamplingTimeEnd()))) {
            maxChanged = true;
            series.setLastValueAt(hObservation.getSamplingTimeEnd());
            series.setLastObservation(hObservation);
        }
        if (hObservation instanceof QuantityDataEntity) {
            if (minChanged) {
                series.setFirstQuantityValue(((QuantityDataEntity) hObservation).getValue());
            }
            if (maxChanged) {
                series.setLastQuantityValue(((QuantityDataEntity) hObservation).getValue());
            }
        }
        session.saveOrUpdate(series);
        session.flush();
    }

    /**
     * Check {@link DatasetEntity} if the deleted observation time stamp corresponds to
     * the first/last series time stamp
     *
     * @param series
     *            Series to update
     * @param observation
     *            Deleted observation
     * @param session
     *            Hibernate session
     */
    public void updateSeriesAfterObservationDeletion(DatasetEntity series, DataEntity<?> observation,
            Session session) {
        SeriesObservationDAO seriesObservationDAO = new SeriesObservationDAO(getDaoFactory());
        if (series.isSetFirstValueAt() && series.getFirstValueAt().equals(observation.getSamplingTimeStart())) {
            DataEntity<?> firstDataEntity = seriesObservationDAO.getFirstObservationFor(series, session);
            if (firstDataEntity != null) {
                series.setFirstValueAt(firstDataEntity.getSamplingTimeStart());
                if (firstDataEntity instanceof QuantityDataEntity) {
                    series.setFirstQuantityValue(((QuantityDataEntity) firstDataEntity).getValue());
                }
                series.setFirstObservation(firstDataEntity);
            } else {
                series.setFirstValueAt(null);
                series.setFirstObservation(null);
                if (observation instanceof QuantityDataEntity) {
                    series.setFirstQuantityValue(null);
                }
            }
        }
        if (series.isSetLastValueAt() && series.getLastValueAt().equals(observation.getSamplingTimeEnd())) {
            DataEntity<?> latestDataEntity = seriesObservationDAO.getLastObservationFor(series, session);
            if (latestDataEntity != null) {
                series.setLastValueAt(latestDataEntity.getSamplingTimeEnd());
                if (latestDataEntity instanceof QuantityDataEntity) {
                    series.setLastQuantityValue(((QuantityDataEntity) latestDataEntity).getValue());
                }
                series.setLastObservation(latestDataEntity);
            } else {
                series.setLastValueAt(null);
                series.setLastObservation(null);
                if (observation instanceof QuantityDataEntity) {
                    series.setLastQuantityValue(null);
                }
            }
        }
        if (!series.isSetFirstValueAt() && !series.isSetLastValueAt()) {
            series.setUnit(null);
        }
        session.saveOrUpdate(series);
    }

    public TimeExtrema getProcedureTimeExtrema(Session session, String procedure) {
        Criteria c = getDefaultSeriesCriteria(session);
        addProcedureToCriteria(c, procedure);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.min(DatasetEntity.PROPERTY_FIRST_VALUE_AT));
        projectionList.add(Projections.max(DatasetEntity.PROPERTY_LAST_VALUE_AT));
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
     *            AbstractFeatureEntity to get series for
     * @param session
     *            Hibernate session
     *
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
        c.setFetchMode(DatasetEntity.PROPERTY_PROCEDURE, FetchMode.JOIN);
        c.setFetchMode(DatasetEntity.PROPERTY_PHENOMENON, FetchMode.JOIN);
        c.setFetchMode(DatasetEntity.PROPERTY_FEATURE, FetchMode.JOIN);
        c.setFetchMode(DatasetEntity.PROPERTY_OFFERING, FetchMode.JOIN);
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
     *            AbstractFeatureEntity to get series for
     * @param session
     *            Hibernate session
     *
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

    private Criteria createCriteriaFor(String procedure, String observedProperty, Session session) {
        final Criteria c = getDefaultSeriesCriteria(session);
        if (Strings.isNullOrEmpty(observedProperty)) {
            addObservablePropertyToCriteria(c, observedProperty);
        }
        if (Strings.isNullOrEmpty(procedure)) {
            addProcedureToCriteria(c, procedure);
        }
        return c;
    }

    private Criteria createCriteriaFor(String observedProperty, String offering, Collection<String> features,
            Session session) {
        final Criteria c = getDefaultSeriesCriteria(session);
        if (CollectionHelper.isNotEmpty(features)) {
            addFeatureOfInterestToCriteria(c, features);
        }
        if (Strings.isNullOrEmpty(observedProperty)) {
            addObservablePropertyToCriteria(c, observedProperty);
        }
        if (Strings.isNullOrEmpty(offering)) {
            addOfferingToCriteria(c, offering);
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
        if (request.hasResultFilter() && request.getResultFilter() instanceof ComparisonFilter) {
            addResultfilter(c, (ComparisonFilter) request.getResultFilter(), identifier);
        }
    }

    private void addResultfilter(Criteria c, ComparisonFilter resultFilter, SubQueryIdentifier identifier) throws CodedException {
        Criterion resultFilterExpression = ResultFilterRestrictions.getResultFilterExpression(resultFilter,
                getResultFilterClasses(), DatasetEntity.PROPERTY_ID, DataEntity.PROPERTY_DATASET, identifier);
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
                dc.add(SpatialRestrictions.filter(DataEntity.PROPERTY_GEOMETRY_ENTITY, filter.getOperator(),
                        geometry));
                dc.setProjection(Projections.property(DataEntity.PROPERTY_DATASET));
                c.add(Subqueries.propertyIn(DatasetEntity.PROPERTY_ID, dc));
            } else {
                if (request.isSetFeaturesOfInterest()) {
                    c.add(SpatialRestrictions.filter("foi." + AbstractFeatureEntity .GEOMETRY, filter.getOperator(), geometry));
                } else {
                    c.createCriteria(DatasetEntity.PROPERTY_FEATURE).add(SpatialRestrictions.filter(AbstractFeatureEntity .GEOMETRY, filter.getOperator(), geometry));
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
                dc.add(SpatialRestrictions.filter(DataEntity.PROPERTY_GEOMETRY_ENTITY, filter.getOperator(),
                        geometry));
                dc.setProjection(Projections.property(DataEntity.PROPERTY_DATASET));
                c.add(Subqueries.propertyIn(DatasetEntity.PROPERTY_ID, dc));
            }
        }
    }

    public ResultFilterClasses getResultFilterClasses() {
        return new ResultFilterClasses(getObservationFactory().numericClass(), getObservationFactory().countClass(),
                getObservationFactory().textClass(), getObservationFactory().categoryClass(),
                getObservationFactory().complexClass(), getObservationFactory().profileClass());
    }

    protected boolean isIncludeChildObservableProperties() {
        if (ObservationSettingProvider.getInstance() != null) {
            return ObservationSettingProvider.getInstance().isIncludeChildObservableProperties();
        }
        return false;
    }

    public DatasetEntity checkOrInsertSeries(ProcedureEntity procedure, PhenomenonEntity observableProperty,
            OfferingEntity offering, CategoryEntity category, AbstractFeatureEntity feature, boolean parentOffering, Session session) throws OwsExceptionReport {
        ObservationContext ctx =
                new ObservationContext().setCategory(category).setOffering(offering)
                        .setPhenomenon(observableProperty).setProcedure(procedure).setFeatureOfInterest(feature);
        return getOrInsert(ctx, session);
    }

    public DatasetEntity checkOrInsertSeries(ProcedureEntity procedure, PhenomenonEntity observableProperty,
            OfferingEntity offering, CategoryEntity category, boolean parentOffering, Session session) throws OwsExceptionReport {
        ObservationContext ctx =
                new ObservationContext().setCategory(category).setOffering(offering)
                        .setPhenomenon(observableProperty).setProcedure(procedure);
        return getOrInsert(ctx, session);
    }

    public DatasetEntity checkOrInsertSeries(ProcedureEntity procedure, PhenomenonEntity observableProperty,
            OfferingEntity offering, boolean hiddenChild, Session session) throws OwsExceptionReport {
        CategoryEntity category = getDaoFactory().getObservablePropertyDAO().getOrInsertCategory(observableProperty, session);
        return checkOrInsertSeries(procedure, observableProperty, offering, category, hiddenChild, session);
    }

    public boolean checkObservationType(DatasetEntity dataset, String observationType, Session session) {
        String hObservationType = dataset.getObservationType() == null ? null : dataset.getObservationType().getFormat();
        if (hObservationType == null || hObservationType.isEmpty() || hObservationType.equals("NOT_DEFINED")) {
            updateSeries(dataset, observationType, session);
        } else  if (!hObservationType.equals(observationType)) {
            return false;
        }
        return true;
    }

    private void updateSeries(DatasetEntity dataset, String observationType, Session session) {
        FormatEntity obsType = new FormatDAO().getFormatEntityObject(observationType, session);
        dataset.setObservationType(obsType);
        session.saveOrUpdate(dataset);

        // update hidden child observation constellations
        // TODO should hidden child observation constellations be restricted to
        // the parent observation type?
        Set<String> offerings =
                dataset.getOffering().getChildren().stream().map(o -> o.getIdentifier()).collect(Collectors.toSet());

        if (CollectionHelper.isNotEmpty(offerings)) {
            Criteria c =
                    session.createCriteria(getSeriesClass())
                            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                            .add(Restrictions.eq(DatasetEntity.PROPERTY_PHENOMENON,
                                    dataset.getObservableProperty()))
                            .add(Restrictions.eq( DatasetEntity.PROPERTY_PROCEDURE,
                                    dataset.getProcedure()))
                            .add(Restrictions.eq(DatasetEntity.HIDDEN_CHILD, true));
            c.createCriteria( DatasetEntity.PROPERTY_OFFERING).add(Restrictions.in(OfferingEntity.IDENTIFIER, offerings));
            LOGGER.debug("QUERY updateSeries(observationConstellation, observationType): {}",
                    HibernateHelper.getSqlString(c));
            List<DatasetEntity> hiddenChildObsConsts = c.list();
            for (DatasetEntity hiddenChildObsConst : hiddenChildObsConsts) {
                hiddenChildObsConst.setObservationType(obsType);
                session.saveOrUpdate(hiddenChildObsConst);
            }
        }

    }

    @SuppressWarnings("unchecked")
    public List<DatasetEntity> getSeriesForOfferings(PhenomenonEntity phenomenon, HashSet<OfferingEntity> offerings,
            Session session) throws OwsExceptionReport {
        return session.createCriteria(getSeriesImpl().getClass())
                .add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false))
                .add(Restrictions.in(DatasetEntity.PROPERTY_OFFERING, offerings))
                .add(Restrictions.eq(DatasetEntity.PROPERTY_PHENOMENON, phenomenon)).list();

    }

    public DatasetEntity getSeries(OmObservationConstellation omObsConst, Session session) throws OwsExceptionReport {
        Criteria criteria =
                session.createCriteria(getSeriesImpl().getClass()).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.createCriteria(DatasetEntity.PROPERTY_PROCEDURE)
                .add(Restrictions.eq(ProcedureEntity.IDENTIFIER, omObsConst.getProcedureIdentifier()));
        criteria.createCriteria(DatasetEntity.PROPERTY_PHENOMENON)
                .add(Restrictions.eq(PhenomenonEntity.IDENTIFIER, omObsConst.getObservablePropertyIdentifier()));
        criteria.createCriteria(DatasetEntity.PROPERTY_OFFERING)
                .add(Restrictions.in(OfferingEntity.IDENTIFIER, omObsConst.getOfferings()));
//        criteria.createAlias(DatasetEntity.PROPERTY_FEATURE, "f");
//        criteria.add(Restrictions.or(Restrictions.isNull(DatasetEntity.PROPERTY_FEATURE), Restrictions
//                .eq("f." + AbstractFeatureEntity.IDENTIFIER, omObsConst.getFeatureOfInterestIdentifier())));
        LOGGER.debug("QUERY getObservationConstellation(omObservationConstellation): {}",
                HibernateHelper.getSqlString(criteria));
        return (DatasetEntity) criteria.uniqueResult();
    }

    /**
     * Query unit for parameter
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param session
     *            Hibernate Session
     * @return Unit or null if no unit is set
     * @throws OwsExceptionReport
     *             If an error occurs when querying the unit
     */
    public String getUnit(long series, Session session) throws OwsExceptionReport {
        StringBuilder logArgs = new StringBuilder();
        DatasetEntity dataset = (DatasetEntity) session.get(getSeriesClass(), series);
        if (dataset != null && dataset.hasUnit()) {
            return dataset.getUnit().getIdentifier();
        }
        return null;
    }

    /**
     * Query unit for parameter
     *
     * @param request
     *            {@link AbstractObservationRequest}
     * @param series
     *            Datasource series id
     * @param session
     *            Hibernate Session
     * @return Unit or null if no unit is set
     * @throws OwsExceptionReport
     *             If an error occurs when querying the unit
     */
    public String getUnit(Set<Long> series, Session session) throws OwsExceptionReport {
        StringBuilder logArgs = new StringBuilder();
        for (Long s : series) {
            DatasetEntity dataset = (DatasetEntity) session.get(getSeriesClass(), s);
            if (dataset != null && dataset.hasUnit()) {
                return dataset.getUnit().getIdentifier();
            }
        }
        return null;
    }

}
