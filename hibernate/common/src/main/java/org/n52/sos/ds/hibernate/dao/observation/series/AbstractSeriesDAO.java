/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.locationtech.jts.geom.Geometry;
import org.n52.faroe.annotation.Setting;
import org.n52.series.db.beans.AbstractDatasetEntity;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.CategoryEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetAggregationEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.DescribableEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.GeometryEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.dataset.DatasetType;
import org.n52.series.db.beans.dataset.ValueType;
import org.n52.shetland.ogc.filter.ComparisonFilter;
import org.n52.shetland.ogc.filter.Filter;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.DeleteDataHelper;
import org.n52.sos.ds.hibernate.dao.AbstractIdentifierNameDescriptionDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FormatDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ResultFilterClasses;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public abstract class AbstractSeriesDAO extends AbstractIdentifierNameDescriptionDAO implements DeleteDataHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeriesDAO.class);

    private Boolean deletePhysically;

    public AbstractSeriesDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Setting("service.transactional.DeletePhysically")
    public void setDeletePhysically(Boolean deletePhysically) {
        this.deletePhysically = deletePhysically;
    }

    public Class<?> getSeriesClass() {
        return DatasetEntity.class;
    }

    public Class<?> getNotInitializedDatasetClass() {
        return DatasetEntity.class;
    }

    @SuppressWarnings("unchecked")
    private Class<DatasetEntity> seriesEntityClass() {
        return (Class<DatasetEntity>) getSeriesClass();
    }

    @SuppressWarnings("unchecked")
    private Class<DatasetEntity> notInitializedDatasetEntityClass() {
        return (Class<DatasetEntity>) getNotInitializedDatasetClass();
    }

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
     *             If an error occurs
     */
    public abstract List<DatasetEntity> getSeries(GetObservationRequest request, Collection<String> features,
            Session session) throws OwsExceptionReport;

    /**
     * Get series for GetObservationByIdRequest request
     *
     * @param request
     *            GetObservationByIdRequest request to get series for
     * @param session
     *            Hibernate session
     * @return Series that fit
     * @throws CodedException
     *             If an error occurs
     */
    public abstract List<DatasetEntity> getSeries(GetObservationByIdRequest request, Session session)
            throws OwsExceptionReport;

    /**
     * Get series for series identifiers
     *
     * @param identifiers
     *            Series identifiers to get series for
     * @param session
     *            Hibernate session
     * @return Series that fit
     * @throws CodedException
     *             If an error occurs
     */
    public abstract List<DatasetEntity> getSeries(Collection<String> identifiers, Session session)
            throws OwsExceptionReport;

    /**
     * Get series for GetDataAvailability request
     *
     * @param request
     *            GetDataAvailability request to get series for
     * @param session
     *            Hibernate session
     * @return Series that fit
     * @throws CodedException
     *             If an error occurs
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
    public abstract List<DatasetEntity> getSeries(String observedProperty, Collection<String> features,
            Session session);

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
     * @param featuresOfInterest
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

    public abstract List<DatasetEntity> getSeries(String procedure, String observableProperty, Session session);

    public List<DatasetEntity> getSeries(Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        query.where(defaultSeriesPredicates(cb, root).toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

    public DatasetEntity getSeries(OmObservationConstellation omObsConst, Session session) throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        Join<DatasetEntity, ProcedureEntity> procedure = root.join(DatasetEntity.PROPERTY_PROCEDURE);
        Join<DatasetEntity, PhenomenonEntity> phenomenon = root.join(DatasetEntity.PROPERTY_PHENOMENON);
        Join<DatasetEntity, OfferingEntity> offering = root.join(DatasetEntity.PROPERTY_OFFERING);
        query.where(cb.equal(procedure.get(ProcedureEntity.IDENTIFIER), omObsConst.getProcedureIdentifier()),
                cb.equal(phenomenon.get(PhenomenonEntity.IDENTIFIER), omObsConst.getObservablePropertyIdentifier()),
                offering.get(OfferingEntity.IDENTIFIER).in(omObsConst.getOfferings()));
        if (omObsConst.isSetCategoryParameter()) {
            List<DatasetEntity> datasets = session.createQuery(query).list();
            return datasets.stream()
                    .filter(d -> d.getCategory().getIdentifier()
                            .equals(omObsConst.getCategoryParameter().getValue().getValue()))
                    .findFirst().orElse(datasets.iterator().next());

        }
        return session.createQuery(query).setMaxResults(1).uniqueResult();
    }

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

    /**
     * Insert or update and get series for procedure, observable property and featureOfInterest
     *
     * @param ctx
     *            identifiers object
     * @param observation
     *            the observation
     * @param session
     *            Hibernate session
     *
     * @return Series object
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public abstract DatasetEntity getOrInsertSeries(ObservationContext ctx, DataEntity<?> observation, Session session)
            throws OwsExceptionReport;

    protected abstract Predicate getSpecificRestrictions(CriteriaBuilder cb, Root<DatasetEntity> root,
            GetObservationRequest request) throws OwsExceptionReport;

    public abstract ObservationFactory getObservationFactory();

    public abstract DatasetFactory getDatasetFactory();

    public DatasetEntity getOrInsert(ObservationContext ctx, final Session session) throws OwsExceptionReport {
        return getOrInsert(ctx, null, session);
    }

    @SuppressWarnings("unchecked")
    protected DatasetEntity getOrInsert(ObservationContext ctx, DataEntity<?> observation, Session session)
            throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        List<Predicate> predicates = ctx.getIdentifierRestrictions(cb, root, true, ctx.isIncludeCategory());
        query.where(predicates.toArray(new Predicate[0]));
        // TODO: check for Unit if available!!!
        List<DatasetEntity> datasets = session.createQuery(query).list();
        DatasetEntity dataset = datasets.isEmpty() ? null : checkForCategory(datasets, ctx);
        if (dataset == null || dataset.getDatasetType().equals(DatasetType.not_initialized)) {
            dataset = preCheckDataset(ctx, observation, dataset, session);
            if (dataset != null && dataset.isMobile()) {
                dataset.setDatasetType(DatasetType.trajectory);
                ctx.setMobile(true);
            }
        }
        if (dataset == null || dataset.isSetFeature() && ctx.isSetFeatureOfInterest()
                && !dataset.getFeature().getIdentifier().equals(ctx.getFeatureOfInterest().getIdentifier())) {
            dataset = (DatasetEntity) getDatasetFactory().visit(observation);
            ctx.addValuesToSeries(dataset);
            dataset.setIdentifier(UUID.randomUUID().toString(), getDaoFactory().isStaSupportsUrls());
            addNameDescriptionForDatastream(dataset);
            dataset.setDeleted(false);
            dataset.setPublished(ctx.isPublish());
        } else if (!dataset.isSetFeature()) {
            ctx.addValuesToSeries(dataset);
            addNameDescriptionForDatastream(dataset);
            dataset.setDeleted(false);
            dataset.setPublished(ctx.isPublish());
        } else if (!dataset.isSetUnit() && ctx.isSetUnit()) {
            dataset.setUnit(ctx.getUnit());
            dataset.setDeleted(false);
            dataset.setPublished(ctx.isPublish());
        } else if (ctx.isPublish() && !dataset.isPublished()) {
            dataset.setPublished(ctx.isPublish());
        } else if (dataset.isDeleted()) {
            dataset.setDeleted(false);
        } else {
            return dataset;
        }
        session.saveOrUpdate(dataset);
        session.flush();
        session.refresh(dataset);
        return dataset;
    }

    private DatasetEntity checkForCategory(List<DatasetEntity> datasets, ObservationContext ctx) {
        Optional<DatasetEntity> dataset =
                datasets.stream().filter(d -> checkCategories(d.getCategory(), ctx.getCategory())).findFirst();
        if (dataset.isPresent()) {
            return dataset.get();
        }
        dataset = datasets.stream().filter(d -> checkCategories(d.getCategory(), getDaoFactory().getDefaultCategory()))
                .findFirst();
        if (dataset.isPresent()) {
            DatasetEntity ds = dataset.get();
            ds.setCategory(ctx.getCategory());
            return ds;
        }
        return datasets.iterator().next();
    }

    private boolean checkCategories(CategoryEntity one, CategoryEntity two) {
        if (one != null && two != null) {
            return one.getIdentifier().equals(two.getIdentifier());
        }
        return false;
    }

    private void addNameDescriptionForDatastream(DatasetEntity dataset) {
        dataset.setName(createDatastreamName(dataset));
        dataset.setDescription(createDatastreamDescription(dataset));
    }

    private String createDatastreamName(DatasetEntity dataset) {
        StringBuffer buffer = new StringBuffer("Datastream_");
        buffer.append(getNameOrIdentifier(dataset.getPlatform())).append("_")
                .append(getNameOrIdentifier(dataset.getProcedure())).append("_")
                .append(getNameOrIdentifier(dataset.getPhenomenon())).append("_")
                .append(getNameOrIdentifier(dataset.getFeature()));
        return buffer.toString();
    }

    private String createDatastreamDescription(DatasetEntity dataset) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Datastream for Thing '").append(getNameOrIdentifier(dataset.getPlatform()))
                .append("' and Sensor '").append(getNameOrIdentifier(dataset.getProcedure()))
                .append("' and ObservedProperty '").append(getNameOrIdentifier(dataset.getPhenomenon()))
                .append("' and FeatureOfInterest '").append(getNameOrIdentifier(dataset.getFeature()));
        buffer.append("'.");
        return buffer.toString();
    }

    private String getNameOrIdentifier(DescribableEntity entity) {
        return entity != null ? entity.isSetName() ? entity.getName() : entity.getIdentifier() : "unknown";
    }

    private DatasetEntity preCheckDataset(ObservationContext ctx, DataEntity<?> observation, DatasetEntity dataset,
            Session session) throws OwsExceptionReport {
        DatasetEntity ds = dataset;
        if (ds == null) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<DatasetEntity> query = cb.createQuery(notInitializedDatasetEntityClass());
            Root<DatasetEntity> root = query.from(notInitializedDatasetEntityClass());
            List<Predicate> predicates = ctx.getIdentifierRestrictions(cb, root, false, false);
            query.where(predicates.toArray(new Predicate[0]));
            List<DatasetEntity> datasets = session.createQuery(query).list();
            ds = datasets.isEmpty() ? null : checkForCategory(datasets, ctx);
        }
        if (ds != null) {
            DatasetEntity concrete = getDatasetFactory().visit(observation);
            session.evict(ds);
            ds.setDatasetType(concrete.getDatasetType());
            ds.setObservationType(concrete.getObservationType());
            if (ValueType.not_initialized.equals(concrete.getValueType()) && ctx.isSetValueType()) {
                ds.setValueType(ctx.getValueType());
            } else {
                ds.setValueType(concrete.getValueType());
            }
            ds = (DatasetEntity) session.merge(ds);
            session.flush();
            return ds;
        }
        return ds;
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
    public DatasetEntity checkSeries(OmObservationConstellation sosOC, String offering, Session session,
            String parameterName) throws OwsExceptionReport {
        // TODO: check for Unit if available!!!
        AbstractPhenomenon observableProperty = sosOC.getObservableProperty();
        String observablePropertyIdentifier = observableProperty.getIdentifier();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.join(DatasetEntity.PROPERTY_OFFERING).get(OfferingEntity.IDENTIFIER), offering));
        predicates.add(cb.equal(root.join(DatasetEntity.PROPERTY_PHENOMENON).get(PhenomenonEntity.IDENTIFIER),
                observablePropertyIdentifier));
        if (sosOC.isSetProcedure()) {
            predicates.add(cb.equal(root.join(DatasetEntity.PROPERTY_PROCEDURE).get(ProcedureEntity.IDENTIFIER),
                    sosOC.getProcedureIdentifier()));
        }
        query.where(predicates.toArray(new Predicate[0]));
        List<DatasetEntity> hocs = session.createQuery(query).list();

        if (hocs == null || hocs.isEmpty()) {
            throw new InvalidParameterValueException().at(Sos2Constants.InsertObservationParams.observation)
                    .withMessage(
                            "The requested observation constellation (procedure=%s, "
                                    + "observedProperty=%s and offering=%s) is invalid!",
                            sosOC.getProcedureIdentifier(), observablePropertyIdentifier, sosOC.getOfferings());
        }
        String observationType = sosOC.getObservationType();

        DatasetEntity hObsConst = null;
        for (DatasetEntity hoc : hocs) {
            if (!checkObservationType(hoc, observationType, session)) {
                throw new InvalidParameterValueException().at(parameterName)
                        .withMessage("The requested observationType (%s) is invalid for procedure = %s, "
                                + "observedProperty = %s and offering = %s! The valid observationType is '%s'!",
                                observationType, sosOC.getProcedureIdentifier(), observablePropertyIdentifier,
                                sosOC.getOfferings(), hoc.getOmObservationType().getFormat());
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
            // if (observableProperty instanceof OmCompositePhenomenon) {
            // OmCompositePhenomenon omCompositePhenomenon =
            // (OmCompositePhenomenon) observableProperty;
            // ObservablePropertyDAO dao = new
            // ObservablePropertyDAO(getDaoFactory());
            // Map<String, PhenomenonEntity> obsprop =
            // dao.getOrInsertObservablePropertyAsMap(Arrays.asList(observableProperty),
            // false, session);
            // for (OmObservableProperty child : omCompositePhenomenon) {
            // checkOrInsertSeries(hoc.getProcedure(),
            // obsprop.get(child.getIdentifier()),
            // hoc.getOffering(), true, session);
            // }
            // }
        }
        return hObsConst;
    }

    @SuppressWarnings("unchecked")
    public Set<DatasetEntity> getSeriesSet(GetObservationRequest request, Collection<String> features, Session session)
            throws OwsExceptionReport {
        Set<DatasetEntity> set = new LinkedHashSet<>();
        if (request.hasResultFilter()) {
            for (SubQueryIdentifier identifier : ResultFilterRestrictions
                    .getSubQueryIdentifier(getResultFilterClasses())) {
                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
                Root<DatasetEntity> root = query.from(seriesEntityClass());
                List<Predicate> predicates = seriesPredicatesFor(cb, root, request.getProcedures(),
                        request.getObservedProperties(), features, request.getOfferings());
                Predicate specific = getSpecificRestrictions(cb, root, request);
                if (specific != null) {
                    predicates.add(specific);
                }
                Predicate resultFilter = checkAndAddResultFilterCriterion(cb, query, root, request, identifier,
                        session);
                if (resultFilter != null) {
                    predicates.add(resultFilter);
                }
                Predicate spatialFilter = checkAndAddSpatialFilterCriterion(cb, query, root, request, session);
                if (spatialFilter != null) {
                    predicates.add(spatialFilter);
                }
                query.where(predicates.toArray(new Predicate[0]));
                set.addAll(session.createQuery(query).list());
            }
        } else {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
            Root<DatasetEntity> root = query.from(seriesEntityClass());
            List<Predicate> predicates = seriesPredicatesFor(cb, root, request.getProcedures(),
                    request.getObservedProperties(), features, request.getOfferings());
            Predicate specific = getSpecificRestrictions(cb, root, request);
            if (specific != null) {
                predicates.add(specific);
            }
            Predicate spatialFilter = checkAndAddSpatialFilterCriterion(cb, query, root, request, session);
            if (spatialFilter != null) {
                predicates.add(spatialFilter);
            }
            query.where(predicates.toArray(new Predicate[0]));
            set.addAll(session.createQuery(query).list());
        }
        return set;
    }

    protected Set<DatasetEntity> getSeriesByFilter(GetDataAvailabilityRequest request, Session session)
            throws OwsExceptionReport {
        Set<DatasetEntity> set = new LinkedHashSet<>();
        if (request.hasResultFilter()) {
            for (SubQueryIdentifier identifier : ResultFilterRestrictions
                    .getSubQueryIdentifier(getResultFilterClasses())) {
                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
                Root<DatasetEntity> root = query.from(seriesEntityClass());
                List<Predicate> predicates = seriesPredicatesFor(cb, root, request.getProcedures(),
                        request.getObservedProperties(), request.getFeaturesOfInterest(), request.getOfferings());
                Predicate resultFilter = checkAndAddResultFilterCriterion(cb, query, root, request, identifier,
                        session);
                if (resultFilter != null) {
                    predicates.add(resultFilter);
                }
                Predicate spatialFilter = checkAndAddSpatialFilterCriterion(cb, query, root, request, session);
                if (spatialFilter != null) {
                    predicates.add(spatialFilter);
                }
                query.where(predicates.toArray(new Predicate[0]));
                set.addAll(session.createQuery(query).list());
            }
        } else {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
            Root<DatasetEntity> root = query.from(seriesEntityClass());
            List<Predicate> predicates = seriesPredicatesFor(cb, root, request.getProcedures(),
                    request.getObservedProperties(), request.getFeaturesOfInterest(), request.getOfferings());
            Predicate spatialFilter = checkAndAddSpatialFilterCriterion(cb, query, root, request, session);
            if (spatialFilter != null) {
                predicates.add(spatialFilter);
            }
            query.where(predicates.toArray(new Predicate[0]));
            set.addAll(session.createQuery(query).list());
        }
        return set;
    }

    public List<DatasetEntity> getSeriesByFilter(Collection<String> identifiers, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        List<Predicate> predicates = new ArrayList<>(defaultSeriesPredicates(cb, root));
        predicates.add(root.get(DatasetEntity.IDENTIFIER).in(identifiers));
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

    protected List<DatasetEntity> getSeriesByFilter(GetResultRequest request, Collection<String> features,
            Session session) throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        List<Predicate> predicates =
                seriesPredicatesFor(cb, root, request.getObservedProperty(), request.getOffering(), features);
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

    public List<DatasetEntity> getSeriesByFilter(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        List<Predicate> predicates = seriesPredicatesFor(cb, root, procedures, observedProperties, features);
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

    public List<DatasetEntity> getSeriesByFilter(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Collection<String> offerings, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        List<Predicate> predicates = seriesPredicatesFor(cb, root, procedures, observedProperties, features, offerings);
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

    public List<DatasetEntity> getSeriesByFilter(String observedProperty, Collection<String> features,
            Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        List<Predicate> predicates = new ArrayList<>(defaultSeriesPredicates(cb, root));
        if (CollectionHelper.isNotEmpty(features)) {
            predicates.add(featurePredicate(cb, root, features));
        }
        if (!Strings.isNullOrEmpty(observedProperty)) {
            predicates.add(observablePropertyPredicate(cb, root, observedProperty));
        }
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

    public List<DatasetEntity> getSeriesByFilter(String procedure, String observedProperty, String offering,
            Collection<String> features, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        List<Predicate> predicates = new ArrayList<>(defaultSeriesPredicates(cb, root));
        if (CollectionHelper.isNotEmpty(features)) {
            predicates.add(featurePredicate(cb, root, features));
        }
        if (!Strings.isNullOrEmpty(observedProperty)) {
            predicates.add(observablePropertyPredicate(cb, root, observedProperty));
        }
        if (!Strings.isNullOrEmpty(offering)) {
            predicates.add(offeringPredicate(cb, root, offering));
        }
        if (!Strings.isNullOrEmpty(procedure)) {
            predicates.add(procedurePredicate(cb, root, procedure));
        }
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

    public DatasetEntity getSeriesByFilterFor(String procedure, String observableProperty, String featureOfInterest,
            Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        List<Predicate> predicates = seriesPredicatesFor(cb, root, procedure, observableProperty, featureOfInterest);
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).uniqueResult();
    }

    public List<DatasetEntity> getSeriesByFilterFor(String procedure, String observableProperty, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        List<Predicate> predicates = seriesPredicatesFor(cb, root, procedure, observableProperty);
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

    /**
     * Get a {@link Predicate} restricting the dataset path to a featureOfInterest identifier
     *
     * @param cb
     *            CriteriaBuilder
     * @param dataset
     *            Path to the dataset
     * @param feature
     *            AbstractFeatureEntity identifier to restrict to
     * @return Predicate
     */
    public Predicate featurePredicate(CriteriaBuilder cb, From<?, DatasetEntity> dataset, String feature) {
        Join<DatasetEntity, AbstractFeatureEntity> join = dataset.join(DatasetEntity.PROPERTY_FEATURE);
        return cb.equal(join.get(AbstractFeatureEntity.IDENTIFIER), feature);
    }

    /**
     * Get a {@link Predicate} restricting the dataset path to featureOfInterest identifiers
     *
     * @param cb
     *            CriteriaBuilder
     * @param dataset
     *            Path to the dataset
     * @param features
     *            AbstractFeatureEntity identifiers to restrict to
     * @return Predicate
     */
    public Predicate featurePredicate(CriteriaBuilder cb, From<?, DatasetEntity> dataset, Collection<String> features) {
        Join<DatasetEntity, AbstractFeatureEntity> join = dataset.join(DatasetEntity.PROPERTY_FEATURE);
        return join.get(AbstractFeatureEntity.IDENTIFIER).in(features);
    }

    /**
     * Get a {@link Predicate} restricting the dataset path to an observedProperty identifier
     *
     * @param cb
     *            CriteriaBuilder
     * @param dataset
     *            Path to the dataset
     * @param observedProperty
     *            ObservableProperty identifier to restrict to
     * @return Predicate
     */
    public Predicate observablePropertyPredicate(CriteriaBuilder cb, From<?, DatasetEntity> dataset,
            String observedProperty) {
        Join<DatasetEntity, PhenomenonEntity> join = dataset.join(DatasetEntity.PROPERTY_PHENOMENON);
        return cb.equal(join.get(PhenomenonEntity.IDENTIFIER), observedProperty);
    }

    /**
     * Get a {@link Predicate} restricting the dataset path to observedProperty identifiers
     *
     * @param cb
     *            CriteriaBuilder
     * @param dataset
     *            Path to the dataset
     * @param observedProperties
     *            ObservableProperty identifiers to restrict to
     * @return Predicate
     */
    public Predicate observablePropertyPredicate(CriteriaBuilder cb, From<?, DatasetEntity> dataset,
            Collection<String> observedProperties) {
        Join<DatasetEntity, PhenomenonEntity> join = dataset.join(DatasetEntity.PROPERTY_PHENOMENON);
        return join.get(PhenomenonEntity.IDENTIFIER).in(observedProperties);
    }

    /**
     * Get a {@link Predicate} restricting the dataset path to a procedure identifier
     *
     * @param cb
     *            CriteriaBuilder
     * @param dataset
     *            Path to the dataset
     * @param procedure
     *            Procedure identifier to restrict to
     * @return Predicate
     */
    public Predicate procedurePredicate(CriteriaBuilder cb, From<?, DatasetEntity> dataset, String procedure) {
        Join<DatasetEntity, ProcedureEntity> join = dataset.join(DatasetEntity.PROPERTY_PROCEDURE);
        return cb.equal(join.get(ProcedureEntity.IDENTIFIER), procedure);
    }

    /**
     * Get a {@link Predicate} restricting the dataset path to a procedure
     *
     * @param cb
     *            CriteriaBuilder
     * @param dataset
     *            Path to the dataset
     * @param procedure
     *            Procedure to restrict to
     * @return Predicate
     */
    public Predicate procedurePredicate(CriteriaBuilder cb, From<?, DatasetEntity> dataset, ProcedureEntity procedure) {
        return cb.equal(dataset.get(DatasetEntity.PROPERTY_PROCEDURE), procedure);
    }

    /**
     * Get a {@link Predicate} restricting the dataset path to procedure identifiers
     *
     * @param cb
     *            CriteriaBuilder
     * @param dataset
     *            Path to the dataset
     * @param procedures
     *            Procedure identifiers to restrict to
     * @return Predicate
     */
    public Predicate procedurePredicate(CriteriaBuilder cb, From<?, DatasetEntity> dataset,
            Collection<String> procedures) {
        Join<DatasetEntity, ProcedureEntity> join = dataset.join(DatasetEntity.PROPERTY_PROCEDURE);
        return join.get(ProcedureEntity.IDENTIFIER).in(procedures);
    }

    /**
     * Get a {@link Predicate} restricting the dataset path to offering identifiers
     *
     * @param cb
     *            CriteriaBuilder
     * @param dataset
     *            Path to the dataset
     * @param offerings
     *            Offering identifiers to restrict to
     * @return Predicate
     */
    public Predicate offeringPredicate(CriteriaBuilder cb, From<?, DatasetEntity> dataset,
            Collection<String> offerings) {
        Join<DatasetEntity, OfferingEntity> join = dataset.join(DatasetEntity.PROPERTY_OFFERING);
        return join.get(OfferingEntity.IDENTIFIER).in(offerings);
    }

    /**
     * Get a {@link Predicate} restricting the dataset path to an offering identifier
     *
     * @param cb
     *            CriteriaBuilder
     * @param dataset
     *            Path to the dataset
     * @param offering
     *            Offering identifier to restrict to
     * @return Predicate
     */
    public Predicate offeringPredicate(CriteriaBuilder cb, From<?, DatasetEntity> dataset, String offering) {
        Join<DatasetEntity, OfferingEntity> join = dataset.join(DatasetEntity.PROPERTY_OFFERING);
        return cb.equal(join.get(OfferingEntity.IDENTIFIER), offering);
    }

    /**
     * Get a {@link Predicate} restricting the dataset path to an offering.
     *
     * <p>
     * Note: preserved as ported from the pre-Hibernate-6 code, which compared
     * {@code DatasetEntity.PROPERTY_PROCEDURE} against the supplied offering instead of
     * {@code DatasetEntity.PROPERTY_OFFERING} -- looks like a pre-existing copy/paste bug, not fixed here.
     *
     * @param cb
     *            CriteriaBuilder
     * @param dataset
     *            Path to the dataset
     * @param offering
     *            Offering to restrict to
     * @return Predicate
     */
    public Predicate offeringPredicate(CriteriaBuilder cb, From<?, DatasetEntity> dataset, OfferingEntity offering) {
        return cb.equal(dataset.get(DatasetEntity.PROPERTY_OFFERING), offering);
    }

    /**
     * Get default restrictions for querying series, deleted flag == <code>false</code>
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Root of the dataset query
     *
     * @return Default predicates
     */
    public List<Predicate> defaultSeriesPredicates(CriteriaBuilder cb, Root<DatasetEntity> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(DatasetEntity.PROPERTY_DELETED), false));
        predicates.add(cb.equal(root.get(DatasetEntity.PROPERTY_PUBLISHED), true));
        if (!isIncludeChildObservableProperties()) {
            predicates.add(cb.equal(root.get(DatasetEntity.HIDDEN_CHILD), false));
        }
        return predicates;
    }

    /**
     * Update series values which will be used by the Timeseries API. Can be later used by the SOS.
     *
     * @param dataset
     *            Series object
     * @param observation
     *            Observation object
     * @param session
     *            Hibernate session
     */
    public void updateDatasetWithObservation(AbstractDatasetEntity dataset, DataEntity<?> observation,
            Session session) {
        boolean minChanged = false;
        boolean maxChanged = false;
        if (!dataset.isSetFirstValueAt() || dataset.isSetFirstValueAt()
                && dataset.getFirstValueAt().after(observation.getSamplingTimeStart())) {
            minChanged = true;
            dataset.setFirstValueAt(observation.getSamplingTimeStart());
            dataset.setFirstObservation(observation);
        }
        if (!dataset.isSetLastValueAt()
                || dataset.isSetLastValueAt() && dataset.getLastValueAt().before(observation.getSamplingTimeEnd())) {
            maxChanged = true;
            dataset.setLastValueAt(observation.getSamplingTimeEnd());
            dataset.setLastObservation(observation);
        }
        if (observation instanceof QuantityDataEntity entity) {
            if (minChanged) {
                dataset.setFirstQuantityValue(entity.getValue());
            }
            if (maxChanged) {
                dataset.setLastQuantityValue(entity.getValue());
            }
        }
        if (!dataset.isSetResultTimeStart() || dataset.isSetResultTimeStart()
                && dataset.getResultTimeStart().after(observation.getResultTime())) {
            dataset.setResultTimeStart(observation.getResultTime());
        }
        if (!dataset.isSetResultTimeEnd()
                || dataset.isSetResultTimeEnd() && dataset.getResultTimeEnd().before(observation.getResultTime())) {
            dataset.setResultTimeEnd(observation.getResultTime());
        }
        if (observation.isSetGeometryEntity()) {
            if (dataset.isSetGeometry()) {
                dataset.getGeometryEntity().expand(observation.getGeometryEntity());
            } else {
                GeometryEntity geometryEntity = new GeometryEntity();
                geometryEntity.expand(observation.getGeometryEntity());
                dataset.setGeometryEntity(geometryEntity);
            }
        } else if (observation.getDataset().isSetFeature() && observation.getDataset().getFeature().isSetGeometry()) {
            if (dataset.isSetGeometry()) {
                dataset.getGeometryEntity().expand(observation.getDataset().getFeature().getGeometryEntity());
            } else {
                GeometryEntity geometryEntity = new GeometryEntity();
                geometryEntity.expand(observation.getDataset().getFeature().getGeometryEntity());
                dataset.setGeometryEntity(geometryEntity);
            }
        }
        session.saveOrUpdate(dataset);
        session.flush();
        session.refresh(dataset);
        if (HibernateHelper.isEntitySupported(DatasetAggregationEntity.class)) {
            if (dataset.isSetAggregation()) {
                updateDatasetWithObservation(dataset.getAggregation(), observation, session);
            }
        }
    }

    /**
     * Update Series for procedure by setting deleted flag and return changed series
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
    public List<DatasetEntity> updateSeriesSetAsDeletedForProcedureAndGetSeries(String procedure, boolean deleteFlag,
            Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        query.where(procedurePredicate(cb, root, procedure));
        List<DatasetEntity> datasets = session.createQuery(query).list();
        for (DatasetEntity dataset : datasets) {
            dataset.setDeleted(deleteFlag);
            dataset.setPublished(!deleteFlag);
            dataset.setDisabled(deleteFlag);
            dataset.setFirstObservation(null);
            dataset.setFirstValueAt(null);
            dataset.setLastObservation(null);
            dataset.setLastValueAt(null);
            dataset.setResultTimeStart(null);
            dataset.setResultTimeEnd(null);
            dataset.setGeometryEntity(null);
            if (dataset.getValueType().equals(ValueType.quantity)) {
                dataset.setFirstQuantityValue(null);
                dataset.setLastQuantityValue(null);
            }
            session.saveOrUpdate(dataset);
        }
        session.flush();
        return datasets;
    }

    /**
     * Check {@link DatasetEntity} if the deleted observation time stamp corresponds to the first/last series
     * time stamp
     *
     * @param dataset
     *            Series to update
     * @param observation
     *            Deleted observation
     * @param session
     *            Hibernate session
     */
    public void updateSeriesAfterObservationDeletion(DatasetEntity dataset, DataEntity<?> observation,
            Session session) {
        SeriesObservationDAO seriesObservationDAO = new SeriesObservationDAO(getDaoFactory());
        if (dataset.isSetFirstValueAt() && dataset.getFirstValueAt().equals(observation.getSamplingTimeStart())) {
            DataEntity<?> firstDataEntity = seriesObservationDAO.getFirstObservationFor(dataset, session);
            if (firstDataEntity != null) {
                dataset.setFirstValueAt(firstDataEntity.getSamplingTimeStart());
                if (firstDataEntity instanceof QuantityDataEntity entity) {
                    dataset.setFirstQuantityValue(entity.getValue());
                }
                dataset.setFirstObservation(firstDataEntity);
            } else {
                dataset.setFirstValueAt(null);
                dataset.setFirstObservation(null);
                if (observation instanceof QuantityDataEntity) {
                    dataset.setFirstQuantityValue(null);
                }
            }
        }
        if (dataset.isSetLastValueAt() && dataset.getLastValueAt().equals(observation.getSamplingTimeEnd())) {
            DataEntity<?> latestDataEntity = seriesObservationDAO.getLastObservationFor(dataset, session);
            if (latestDataEntity != null) {
                dataset.setLastValueAt(latestDataEntity.getSamplingTimeEnd());
                if (latestDataEntity instanceof QuantityDataEntity entity1) {
                    dataset.setLastQuantityValue(entity1.getValue());
                }
                dataset.setLastObservation(latestDataEntity);
            } else {
                dataset.setLastValueAt(null);
                dataset.setLastObservation(null);
                if (observation instanceof QuantityDataEntity) {
                    dataset.setLastQuantityValue(null);
                }
            }
        }
        if (!dataset.isSetFirstValueAt() && !dataset.isSetLastValueAt()) {
            dataset.setUnit(null);
            dataset.setGeometry(null);
        }
        // restultTime
        if (dataset.isSetResultTimeStart() && dataset.getResultTimeStart().equals(observation.getResultTime())) {
            DataEntity<?> firstDataEntity = seriesObservationDAO.getFirstObservationFor(dataset, session);
            if (firstDataEntity != null) {
                dataset.setResultTimeStart(firstDataEntity.getResultTime());
            } else {
                dataset.setResultTimeStart(null);
            }
        }
        if (dataset.isSetResultTimeEnd() && dataset.getResultTimeEnd().equals(observation.getResultTime())) {
            DataEntity<?> latestDataEntity = seriesObservationDAO.getLastObservationFor(dataset, session);
            if (latestDataEntity != null) {
                dataset.setResultTimeEnd(latestDataEntity.getSamplingTimeEnd());
            } else {
                dataset.setResultTimeEnd(null);
            }
        }
        session.saveOrUpdate(dataset);
        if (HibernateHelper.isEntitySupported(DatasetAggregationEntity.class)) {
            if (dataset.isSetAggregation()) {
                updateStaAfterObservationDeletion(dataset.getAggregation(), session);
            }
        }
    }

    private void updateStaAfterObservationDeletion(AbstractDatasetEntity dataset, Session session) {
        if (dataset instanceof DatasetAggregationEntity aggregation) {
            Set<Date> samplingTimeStart = new LinkedHashSet<>();
            Set<Date> samplingTimeEnd = new LinkedHashSet<>();
            Set<Date> resultTimeStart = new LinkedHashSet<>();
            Set<Date> resultTimeEnd = new LinkedHashSet<>();
            GeometryEntity geom = new GeometryEntity();
            if (aggregation.isSetDatasets()) {
                for (AbstractDatasetEntity ade : aggregation.getDatasets()) {
                    samplingTimeStart.add(ade.getSamplingTimeStart());
                    samplingTimeEnd.add(ade.getSamplingTimeEnd());
                    resultTimeStart.add(ade.getResultTimeStart());
                    resultTimeEnd.add(ade.getResultTimeEnd());
                    if (ade.getGeometry() != null) {
                        geom.union(ade.getGeometryEntity());
                    }
                }
                aggregation.setSamplingTimeStart(Collections.min(samplingTimeStart));
                aggregation.setSamplingTimeEnd(Collections.max(samplingTimeEnd));
                aggregation.setResultTimeStart(Collections.min(resultTimeStart));
                aggregation.setResultTimeEnd(Collections.max(resultTimeEnd));
                session.saveOrUpdate(aggregation);
            }
        }
    }

    /**
     * Build the default series predicates plus procedure/observedProperty/feature restrictions for the supplied
     * collections
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Root of the dataset query
     * @param procedures
     *            Procedures to get series for
     * @param observedProperties
     *            ObservedProperties to get series for
     * @param features
     *            AbstractFeatureEntity to get series for
     *
     * @return Predicates to query series
     */
    private List<Predicate> seriesPredicatesFor(CriteriaBuilder cb, Root<DatasetEntity> root,
            Collection<String> procedures, Collection<String> observedProperties, Collection<String> features) {
        List<Predicate> predicates = new ArrayList<>(defaultSeriesPredicates(cb, root));
        if (CollectionHelper.isNotEmpty(features)) {
            predicates.add(featurePredicate(cb, root, features));
        }
        if (CollectionHelper.isNotEmpty(observedProperties)) {
            predicates.add(observablePropertyPredicate(cb, root, observedProperties));
        }
        if (CollectionHelper.isNotEmpty(procedures)) {
            predicates.add(procedurePredicate(cb, root, procedures));
        }
        return predicates;
    }

    private List<Predicate> seriesPredicatesFor(CriteriaBuilder cb, Root<DatasetEntity> root,
            Collection<String> procedures, Collection<String> observedProperties, Collection<String> features,
            Collection<String> offerings) {
        List<Predicate> predicates = seriesPredicatesFor(cb, root, procedures, observedProperties, features);
        if (CollectionHelper.isNotEmpty(offerings)) {
            predicates.add(offeringPredicate(cb, root, offerings));
        }
        return predicates;
    }

    /**
     * Build the default series predicates plus procedure/observedProperty/feature restrictions for the supplied
     * identifiers.
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Root of the dataset query
     * @param procedure
     *            Procedure to get series for
     * @param observedProperty
     *            ObservedProperty to get series for
     * @param feature
     *            AbstractFeatureEntity to get series for
     *
     * @return Predicates to query series
     */
    private List<Predicate> seriesPredicatesFor(CriteriaBuilder cb, Root<DatasetEntity> root, String procedure,
            String observedProperty, String feature) {
        List<Predicate> predicates = new ArrayList<>(defaultSeriesPredicates(cb, root));
        if (!Strings.isNullOrEmpty(feature)) {
            predicates.add(featurePredicate(cb, root, feature));
        }
        if (!Strings.isNullOrEmpty(observedProperty)) {
            predicates.add(observablePropertyPredicate(cb, root, observedProperty));
        }
        if (!Strings.isNullOrEmpty(procedure)) {
            predicates.add(procedurePredicate(cb, root, procedure));
        }
        return predicates;
    }

    /**
     * Build the default series predicates plus procedure/observedProperty restrictions.
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Root of the dataset query
     * @param procedure
     *            Procedure to get series for
     * @param observedProperty
     *            ObservedProperty to get series for
     *
     * @return Predicates to query series
     */
    private List<Predicate> seriesPredicatesFor(CriteriaBuilder cb, Root<DatasetEntity> root, String procedure,
            String observedProperty) {
        List<Predicate> predicates = new ArrayList<>(defaultSeriesPredicates(cb, root));
        if (!Strings.isNullOrEmpty(observedProperty)) {
            predicates.add(observablePropertyPredicate(cb, root, observedProperty));
        }
        if (!Strings.isNullOrEmpty(procedure)) {
            predicates.add(procedurePredicate(cb, root, procedure));
        }
        return predicates;
    }

    private List<Predicate> seriesPredicatesFor(CriteriaBuilder cb, Root<DatasetEntity> root, String observedProperty,
            String offering, Collection<String> features) {
        List<Predicate> predicates = new ArrayList<>(defaultSeriesPredicates(cb, root));
        if (CollectionHelper.isNotEmpty(features)) {
            predicates.add(featurePredicate(cb, root, features));
        }
        if (!Strings.isNullOrEmpty(observedProperty)) {
            predicates.add(observablePropertyPredicate(cb, root, observedProperty));
        }
        if (!Strings.isNullOrEmpty(offering)) {
            predicates.add(offeringPredicate(cb, root, offering));
        }
        return predicates;
    }

    protected Predicate checkAndAddResultFilterCriterion(CriteriaBuilder cb, CriteriaQuery<?> query,
            Root<DatasetEntity> root, GetDataAvailabilityRequest request, SubQueryIdentifier identifier,
            Session session) throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            return getResultFilterPredicate(cb, query, root, request.getResultFilter(), identifier);
        }
        return null;
    }

    protected Predicate checkAndAddResultFilterCriterion(CriteriaBuilder cb, CriteriaQuery<?> query,
            Root<DatasetEntity> root, GetObservationRequest request, SubQueryIdentifier identifier, Session session)
            throws OwsExceptionReport {
        if (request.hasResultFilter() && request.getResultFilter() instanceof ComparisonFilter) {
            return getResultFilterPredicate(cb, query, root, (ComparisonFilter) request.getResultFilter(),
                    identifier);
        }
        return null;
    }

    private Predicate getResultFilterPredicate(CriteriaBuilder cb, CriteriaQuery<?> query, Root<DatasetEntity> root,
            Filter<?> resultFilter, SubQueryIdentifier identifier) throws CodedException {
        return ResultFilterRestrictions.getResultFilterExpression(cb, query, root, resultFilter,
                getResultFilterClasses(), DatasetEntity.PROPERTY_ID, DataEntity.PROPERTY_DATASET_ID, identifier);
    }

    protected Predicate checkAndAddSpatialFilterCriterion(CriteriaBuilder cb, CriteriaQuery<?> query,
            Root<DatasetEntity> root, GetDataAvailabilityRequest request, Session session)
            throws OwsExceptionReport {
        if (request.hasSpatialFilter()) {
            SpatialFilter filter = request.getSpatialFilter();
            Geometry geometry = getDaoFactory().getGeometryHandler()
                    .switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry());
            if (filter.getValueReference().equals(Sos2Constants.VALUE_REFERENCE_SPATIAL_FILTERING_PROFILE)) {
                return observationGeometrySubqueryPredicate(cb, query, root, filter.getOperator(), geometry);
            }
            Join<DatasetEntity, AbstractFeatureEntity> featureJoin = root.join(DatasetEntity.PROPERTY_FEATURE);
            return SpatialRestrictions.filter(cb, featureJoin.<Geometry>get(AbstractFeatureEntity.GEOMETRY),
                    filter.getOperator(), geometry);
        }
        return null;
    }

    protected Predicate checkAndAddSpatialFilterCriterion(CriteriaBuilder cb, CriteriaQuery<?> query,
            Root<DatasetEntity> root, GetObservationRequest request, Session session) throws OwsExceptionReport {
        if (request.isSetSpatialFilter()) {
            SpatialFilter filter = request.getSpatialFilter();
            Geometry geometry = getDaoFactory().getGeometryHandler()
                    .switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry());
            if (filter.getValueReference().equals(Sos2Constants.VALUE_REFERENCE_SPATIAL_FILTERING_PROFILE)) {
                return observationGeometrySubqueryPredicate(cb, query, root, filter.getOperator(), geometry);
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private Predicate observationGeometrySubqueryPredicate(CriteriaBuilder cb, CriteriaQuery<?> query,
            Root<DatasetEntity> root, SpatialOperator operator, Geometry geometry) throws OwsExceptionReport {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<? extends DataEntity> dataRoot = subquery.from(getObservationFactory().observationClass());
        subquery.select(dataRoot.<Long>get(DataEntity.PROPERTY_DATASET_ID));
        subquery.where(SpatialRestrictions.filter(cb, dataRoot.<Geometry>get(DataEntity.PROPERTY_GEOMETRY_ENTITY),
                operator, geometry));
        return cb.in(root.get(DatasetEntity.PROPERTY_ID)).value(subquery);
    }

    public ResultFilterClasses getResultFilterClasses() {
        return new ResultFilterClasses(getObservationFactory().numericClass(), getObservationFactory().countClass(),
                getObservationFactory().textClass(), getObservationFactory().categoryClass(),
                getObservationFactory().complexClass(), getObservationFactory().profileClass());
    }

    protected boolean isIncludeChildObservableProperties() {
        return getDaoFactory().isIncludeChildObservableProperties();
    }

    public DatasetEntity checkOrInsertSeries(ProcedureEntity procedure, PhenomenonEntity observableProperty,
            OfferingEntity offering, CategoryEntity category, AbstractFeatureEntity<?> feature,
            PlatformEntity platform, FormatEntity observationType, boolean b, Session session)
            throws OwsExceptionReport {
        ObservationContext ctx = new ObservationContext().setCategory(category).setOffering(offering)
                .setPhenomenon(observableProperty).setProcedure(procedure).setFeatureOfInterest(feature)
                .setPlatform(platform).setObservationType(observationType);
        return getOrInsert(ctx, session);
    }

    public DatasetEntity checkOrInsertSeries(ProcedureEntity procedure, PhenomenonEntity observableProperty,
            OfferingEntity offering, CategoryEntity category, AbstractFeatureEntity feature, PlatformEntity platform,
            boolean parentOffering, Session session) throws OwsExceptionReport {
        ObservationContext ctx =
                new ObservationContext().setCategory(category).setOffering(offering).setPhenomenon(observableProperty)
                        .setProcedure(procedure).setFeatureOfInterest(feature).setPlatform(platform);
        return getOrInsert(ctx, session);
    }

    public DatasetEntity checkOrInsertSeries(ProcedureEntity procedure, PhenomenonEntity observableProperty,
            OfferingEntity offering, CategoryEntity category, AbstractFeatureEntity feature, boolean parentOffering,
            Session session) throws OwsExceptionReport {
        ObservationContext ctx = new ObservationContext().setCategory(category).setOffering(offering)
                .setPhenomenon(observableProperty).setProcedure(procedure).setFeatureOfInterest(feature);
        return getOrInsert(ctx, session);
    }

    public DatasetEntity checkOrInsertSeries(ProcedureEntity procedure, PhenomenonEntity observableProperty,
            OfferingEntity offering, CategoryEntity category, boolean parentOffering, Session session)
            throws OwsExceptionReport {
        ObservationContext ctx = new ObservationContext().setCategory(category).setOffering(offering)
                .setPhenomenon(observableProperty).setProcedure(procedure);
        return getOrInsert(ctx, session);
    }

    public DatasetEntity checkOrInsertSeries(ProcedureEntity procedure, PhenomenonEntity observableProperty,
            OfferingEntity offering, boolean hiddenChild, Session session) throws OwsExceptionReport {
        CategoryEntity category = getDaoFactory().getCategoryDAO().getOrInsertCategory(observableProperty, session);
        return checkOrInsertSeries(procedure, observableProperty, offering, category, hiddenChild, session);
    }

    public boolean checkObservationType(DatasetEntity dataset, String observationType, Session session) {
        String hObservationType = dataset.isSetOMObservationType() ? dataset.getOmObservationType().getFormat() : null;
        if (hObservationType == null || hObservationType.isEmpty() || hObservationType.equals("NOT_DEFINED")) {
            updateSeries(dataset, observationType, session);
        } else if (!hObservationType.equals(observationType)) {
            return false;
        }
        return true;
    }

    private void updateSeries(DatasetEntity dataset, String observationType, Session session) {
        FormatEntity obsType = new FormatDAO().getFormatEntityObject(observationType, session);
        dataset.setOmObservationType(obsType);
        session.saveOrUpdate(dataset);

        // update hidden child observation constellations
        // TODO should hidden child observation constellations be restricted to
        // the parent observation type?
        Set<String> offerings =
                dataset.getOffering().getChildren().stream().map(o -> o.getIdentifier()).collect(Collectors.toSet());

        if (CollectionHelper.isNotEmpty(offerings)) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
            Root<DatasetEntity> root = query.from(seriesEntityClass());
            Join<DatasetEntity, OfferingEntity> offeringJoin = root.join(DatasetEntity.PROPERTY_OFFERING);
            query.where(cb.equal(root.get(DatasetEntity.PROPERTY_PHENOMENON), dataset.getObservableProperty()),
                    cb.equal(root.get(DatasetEntity.PROPERTY_PROCEDURE), dataset.getProcedure()),
                    cb.equal(root.get(DatasetEntity.HIDDEN_CHILD), true),
                    offeringJoin.get(OfferingEntity.IDENTIFIER).in(offerings));
            List<DatasetEntity> hiddenChildObsConsts = session.createQuery(query).list();
            for (DatasetEntity hiddenChildObsConst : hiddenChildObsConsts) {
                hiddenChildObsConst.setOmObservationType(obsType);
                session.saveOrUpdate(hiddenChildObsConst);
            }
        }

    }

    public List<DatasetEntity> getSeriesForOfferings(PhenomenonEntity phenomenon, HashSet<OfferingEntity> offerings,
            Session session) throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        query.where(cb.equal(root.get(DatasetEntity.PROPERTY_DELETED), false),
                root.get(DatasetEntity.PROPERTY_OFFERING).in(offerings),
                cb.equal(root.get(DatasetEntity.PROPERTY_PHENOMENON), phenomenon));
        return session.createQuery(query).list();
    }

    /**
     * Query unit for parameter
     *
     * @param series
     *            Datasource series id
     * @param session
     *            Hibernate Session
     * @return Unit or null if no unit is set
     * @throws OwsExceptionReport
     *             If an error occurs when querying the unit
     */
    public String getUnit(long series, Session session) throws OwsExceptionReport {
        DatasetEntity dataset = (DatasetEntity) session.get(getSeriesClass(), series);
        if (dataset != null && dataset.isSetUnit()) {
            return dataset.getUnit().getIdentifier();
        }
        return null;
    }

    /**
     * Query unit for parameter
     *
     * @param series
     *            Datasource series ids
     * @param session
     *            Hibernate Session
     * @return Unit or null if no unit is set
     * @throws OwsExceptionReport
     *             If an error occurs when querying the unit
     */
    public String getUnit(Set<Long> series, Session session) throws OwsExceptionReport {
        for (Long s : series) {
            DatasetEntity dataset = (DatasetEntity) session.get(getSeriesClass(), s);
            if (dataset != null && dataset.isSetUnit()) {
                return dataset.getUnit().getIdentifier();
            }
        }
        return null;
    }

    public List<DatasetEntity> delete(ProcedureEntity procedure, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<DatasetEntity> query = cb.createQuery(seriesEntityClass());
        Root<DatasetEntity> root = query.from(seriesEntityClass());
        query.where(procedurePredicate(cb, root, procedure));
        List<DatasetEntity> datasets = session.createQuery(query).list();
        if (datasets != null && !datasets.isEmpty()) {
            deleteDatastream(datasets, session);
            StringBuilder builder = new StringBuilder();
            builder.append("delete ");
            builder.append(DatasetEntity.class.getSimpleName());
            builder.append(" where ").append(DatasetEntity.PROPERTY_PROCEDURE).append(" = :")
                    .append(DatasetEntity.PROPERTY_PROCEDURE);
            Query<?> q = session.createQuery(builder.toString());
            q.setParameter(DatasetEntity.PROPERTY_PROCEDURE, procedure);
            int executeUpdate = q.executeUpdate();
            LOGGER.debug("{} datasets were physically deleted!", executeUpdate);
            session.flush();
        }
        return datasets;
    }

    private void deleteDatastream(List<DatasetEntity> datasets, Session session) {
        datasets.forEach(d -> deleteDatastream(d, session));
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public boolean isDeletePhysically() {
        return deletePhysically;
    }

}