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

package org.n52.sos.ds.hibernate.dao.observation;

import com.google.common.collect.Sets;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.joda.time.DateTime;
import org.locationtech.jts.geom.Geometry;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.CodespaceEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.MissingParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.sos.ds.hibernate.dao.AbstractIdentifierNameDescriptionDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.UnitDAO;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.ResultFilterClasses;
import org.n52.sos.ds.hibernate.util.observation.ObservationUnfolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract Hibernate data access class for observations.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.0
 */
public abstract class AbstractObservationDAO extends AbstractIdentifierNameDescriptionDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractObservationDAO.class);

    private static final String ERROR_ADDING_RESULT_TIME_LOG =
        "Error while adding result time to Hibernate Observation entitiy!";

    private static final String ERROR_CREATING_RESULT_TIME_LOG =
        "Error while creating result time filter for querying observations!";

    private static final String INETERMINATE_POSITION_XPATH =
        "gml:TimeInstant/gml:timePosition[@indeterminatePosition]";

    public AbstractObservationDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Add observation identifier (procedure, observableProperty,
     * featureOfInterest) to observation
     *
     * @param observationIdentifiers Observation identifiers
     * @param observation            Observation to add identifiers
     * @param session                the session
     * @return The {@link DatasetEntity}
     * @throws OwsExceptionReport If an error occurs
     */
    protected abstract DatasetEntity addObservationContextToObservation(DatasetEntity dataset,
                                                                        ObservationContext observationIdentifiers,
                                                                        DataEntity<?> observation,
                                                                        Session session) throws OwsExceptionReport;

    /**
     * Get one example observation for a procedure/observableProperty combination, used to
     * derive the O&amp;M result structure for a procedure description.
     *
     * @param procedure          the procedure
     * @param observableProperty the observableProperty
     * @param session            Hibernate session
     * @return An arbitrary matching observation, or {@code null} if none exists
     * @throws OwsExceptionReport If an error occurs
     */
    public abstract DataEntity<?> getExampleObservationFor(String procedure, String observableProperty,
                                                           Session session) throws OwsExceptionReport;

    public ResultFilterClasses getResultFilterClasses() {
        return new ResultFilterClasses(getObservationFactory().numericClass(), getObservationFactory().countClass(),
                                       getObservationFactory().textClass(), getObservationFactory().categoryClass(),
                                       getObservationFactory().complexClass(), getObservationFactory().profileClass());
    }

    /**
     * Get default restrictions for querying observations, deleted flag == <code>false</code>
     *
     * @param cb   CriteriaBuilder
     * @param root Root of the observation query
     * @return Default predicates
     */
    protected List<Predicate> defaultObservationPredicates(CriteriaBuilder cb, Root<? extends DataEntity<?>> root) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isFalse(root.get(DataEntity.PROPERTY_DELETED)));
        if (!getDaoFactory().isIncludeChildObservableProperties()) {
            predicates.add(cb.isNull(root.get(DataEntity.PROPERTY_PARENT)));
        }
        return predicates;
    }

    /**
     * Query observations by identifiers
     *
     * @param identifiers Observation identifiers (gml:identifier)
     * @param session     Hibernate session
     * @return Matching observations
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<DataEntity<?>> getObservationByIdentifiers(Set<String> identifiers, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getObservationFactory().observationClass());
        Root root = query.from(getObservationFactory().observationClass());
        root.fetch(DataEntity.PROPERTY_PARAMETERS, JoinType.LEFT);
        List<Predicate> predicates = defaultObservationPredicates(cb, root);
        predicates.add(QueryHelper.getPredicateForObjects(cb, root.get(DataEntity.IDENTIFIER), identifiers));
        query.where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

    /**
     * Query non-deleted observations of the observation class matching the requested result model
     *
     * @param resultModel Requested result model, determines the queried observation class
     * @param identifiers Observation identifiers (gml:identifier)
     * @param session     Hibernate session
     * @return Matching observations
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<DataEntity<?>> getObservationsForResultModel(String resultModel, Collection<String> identifiers,
                                                             Session session) {
        Class<? extends DataEntity> clazz = getObservationFactory().classForObservationType(resultModel);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(clazz);
        Root root = query.from(clazz);
        query.where(cb.equal(root.get(DataEntity.PROPERTY_DELETED), false),
                    QueryHelper.getPredicateForObjects(cb, root.get(DataEntity.IDENTIFIER), identifiers));
        return session.createQuery(query).list();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public DataEntity<?> getObservationBy(Long dataset, Date samplingTimeStart, Date samplingTimeEnd,
                                          Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getObservationFactory().observationClass());
        Root root = query.from(getObservationFactory().observationClass());
        List<Predicate> predicates = defaultObservationPredicates(cb, root);
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_DATASET_ID), dataset));
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_SAMPLING_TIME_START), samplingTimeStart));
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_SAMPLING_TIME_END), samplingTimeEnd));
        query.where(predicates.toArray(new Predicate[0]));
        return (DataEntity<?>) session.createQuery(query).uniqueResult();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean checkObservationNotUnique(Long dataset,
                                             Date samplingTimeStart,
                                             Date samplingTimeEnd,
                                             Date resultTime,
                                             BigDecimal verticalFrom,
                                             BigDecimal verticalTo,
                                             Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root root = query.from(getObservationFactory().observationClass());
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_DATASET_ID), dataset));
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_SAMPLING_TIME_START), samplingTimeStart));
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_SAMPLING_TIME_END), samplingTimeEnd));
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_RESULT_TIME), resultTime));
        predicates.add(cb.equal(root.get("verticalTo"), verticalTo));
        predicates.add(cb.equal(root.get("verticalFrom"), verticalFrom));
        query.where(predicates.toArray(new Predicate[0]));
        query.select(cb.literal(1));
        return session.createQuery(query).setMaxResults(1).uniqueResultOptional().isPresent();
    }

    /**
     * Insert a multi value observation for observation constellations and
     * featureOfInterest
     *
     * @param observationConstellation Observation constellation objects
     * @param feature                  FeatureOfInterest object
     * @param containerObservation     SOS observation
     * @param codespaceCache           Map based codespace object cache to prevent redundant queries
     * @param unitCache                Map based unit object cache to prevent redundant queries
     * @param formatCache              Map cache for format objects (to prevent redundant querying)
     * @param session                  Hibernate session
     * @return The {@link DatasetEntity}
     * @throws OwsExceptionReport If an error occurs
     */
    public DatasetEntity insertObservationMultiValue(DatasetEntity observationConstellation,
                                                     AbstractFeatureEntity feature,
                                                     OmObservation containerObservation,
                                                     Map<String, CodespaceEntity> codespaceCache,
                                                     Map<UoM, UnitEntity> unitCache,
                                                     Map<String, FormatEntity> formatCache,
                                                     Session session) throws OwsExceptionReport {
        List<OmObservation> unfoldObservations =
            new ObservationUnfolder(containerObservation,
                                    getDaoFactory().getSweHelper(),
                                    getDaoFactory().getGeometryHandler(),
                                    getDaoFactory().getTrajectoryDetectionTimeGap())
                .unfold();
        for (OmObservation sosObservation : unfoldObservations) {
            DatasetEntity dataset = insertObservationSingleValue(observationConstellation, feature, sosObservation,
                                                                 codespaceCache, unitCache, formatCache, session);
            if (!dataset.equals(observationConstellation)) {
                return dataset;
            }
        }
        return observationConstellation;
    }

    /**
     * Insert a single observation for observation constellations and
     * featureOfInterest with local caching for codespaces and units
     *
     * @param hObservationConstellation Observation constellation objects
     * @param hFeature                  FeatureOfInterest object
     * @param sosObservation            SOS observation to insert
     * @param codespaceCache            Map cache for codespace objects (to prevent redundant
     *                                  querying)
     * @param unitCache                 Map cache for unit objects (to prevent redundant querying)
     * @param formatCache               Map cache for format objects (to prevent redundant querying)
     * @param session                   Hibernate session
     * @return The {@link DatasetEntity}
     * @throws OwsExceptionReport If an error occurs
     */
    @SuppressWarnings("rawtypes")
    public DatasetEntity insertObservationSingleValue(DatasetEntity hObservationConstellation,
                                                      AbstractFeatureEntity hFeature,
                                                      OmObservation sosObservation,
                                                      Map<String, CodespaceEntity> codespaceCache,
                                                      Map<UoM, UnitEntity> unitCache,
                                                      Map<String, FormatEntity> formatCache,
                                                      Session session)
        throws OwsExceptionReport {
        SingleObservationValue<?> value = (SingleObservationValue) sosObservation.getValue();
        ObservationPersister persister =
            new ObservationPersister(getDaoFactory(),
                                     this,
                                     sosObservation,
                                     hObservationConstellation,
                                     hFeature,
                                     codespaceCache,
                                     unitCache,
                                     formatCache,
                                     getOfferings(hObservationConstellation),
                                     session);
        return value.getValue().accept(persister).getDataset();
    }

    private Set<OfferingEntity> getOfferings(DatasetEntity hObservationConstellation) {
        Set<OfferingEntity> offerings = Sets.newHashSet();
        offerings.add(hObservationConstellation.getOffering());
        return offerings;
    }

    protected ObservationContext createObservationContext() {
        return new ObservationContext();
    }

    /**
     * If the local unit cache isn't null, use it when retrieving unit.
     *
     * @param unit       Unit
     * @param localCache Cache (possibly null)
     * @param session
     * @return Unit
     */
    protected UnitEntity getUnit(String unit, Map<UoM, UnitEntity> localCache, Session session) {
        return getUnit(new UoM(unit), localCache, session);
    }

    /**
     * If the local unit cache isn't null, use it when retrieving unit.
     *
     * @param unit       Unit
     * @param localCache Cache (possibly null)
     * @param session    the session
     * @return Unit
     */
    protected UnitEntity getUnit(UoM unit, Map<UoM, UnitEntity> localCache, Session session) {
        if (localCache != null && localCache.containsKey(unit)) {
            return localCache.get(unit);
        } else {
            // query unit and set cache
            UnitEntity hUnit = new UnitDAO().getOrInsertUnit(unit, session);
            if (localCache != null) {
                localCache.put(unit, hUnit);
            }
            return hUnit;
        }
    }

    /**
     * Get global temporal bounding box
     *
     * @param session Hibernate session the session
     * @return the global getEqualRestiction bounding box over all observations,
     * or <tt>null</tt>
     */
    @SuppressWarnings("rawtypes")
    public TimePeriod getGlobalTemporalBoundingBox(Session session) {
        if (session == null) {
            return null;
        }
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root root = query.from(getObservationFactory().temporalReferencedClass());
        query.multiselect(cb.least(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_START)),
                          cb.greatest(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_START)),
                          cb.greatest(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_END)))
            .where((Predicate[]) defaultObservationPredicates(cb, root).toArray(new Predicate[0]));
        Object[] temporalBoundingBox = session.createQuery(query).uniqueResult();
        if (temporalBoundingBox != null && temporalBoundingBox[0] != null) {
            return createTimePeriod((Timestamp) temporalBoundingBox[0], (Timestamp) temporalBoundingBox[1],
                                    (Timestamp) temporalBoundingBox[2]);
        }
        return null;
    }

    /**
     * Add phenomenon and result time to observation object
     *
     * @param observation    Observation object
     * @param phenomenonTime SOS phenomenon time
     * @param resultTime     SOS result Time
     * @throws OwsExceptionReport If an error occurs
     */
    protected void addPhenomeonTimeAndResultTimeToObservation(DataEntity<?> observation, Time phenomenonTime,
                                                              TimeInstant resultTime) throws OwsExceptionReport {
        addPhenomenonTimeToObservation(observation, phenomenonTime);
        addResultTimeToObservation(observation, resultTime, phenomenonTime);
    }

    /**
     * Add phenomenon and result time to observation object
     *
     * @param sosObservation the SOS observation
     * @param observation    Observation object
     * @throws OwsExceptionReport If an error occurs
     */
    protected void addTime(OmObservation sosObservation, DataEntity<?> observation) throws OwsExceptionReport {
        addPhenomeonTimeAndResultTimeToObservation(observation, sosObservation.getPhenomenonTime(),
                                                   sosObservation.getResultTime());
        addValidTimeToObservation(observation, sosObservation.getValidTime());
    }

    /**
     * Add phenomenon time to observation object
     *
     * @param observation    Observation object
     * @param phenomenonTime SOS phenomenon time
     * @throws OwsExceptionReport If an error occurs
     */
    public void addPhenomenonTimeToObservation(DataEntity<?> observation, Time phenomenonTime)
        throws OwsExceptionReport {
        if (phenomenonTime instanceof TimeInstant time) {
            if (time.isSetValue()) {
                observation.setSamplingTimeStart(time.getValue().toDate());
                observation.setSamplingTimeEnd(time.getValue().toDate());
            } else if (time.isSetIndeterminateValue()) {
                Date now = getDateForTimeIndeterminateValue(time.getIndeterminateValue(), INETERMINATE_POSITION_XPATH);
                observation.setSamplingTimeStart(now);
                observation.setSamplingTimeEnd(now);
            } else {
                throw new MissingParameterValueException("gml:TimeInstant/gml:timePosition");
            }
        } else if (phenomenonTime instanceof TimePeriod time) {
            if (time.isSetStart()) {
                observation.setSamplingTimeStart(time.getStart().toDate());
            } else if (time.isSetStartIndeterminateValue()) {
                observation.setSamplingTimeStart(getDateForTimeIndeterminateValue(time.getStartIndet(),
                                                                                  "gml:TimePeriod/gml:beginPosition" +
                                                                                      "[@indeterminatePosition]"));
            } else {
                throw new MissingParameterValueException("gml:TimePeriod/gml:beginPosition");
            }
            if (time.isSetEnd()) {
                observation.setSamplingTimeEnd(time.getEnd().toDate());
            } else if (time.isSetEndIndeterminateValue()) {
                observation.setSamplingTimeEnd(getDateForTimeIndeterminateValue(time.getEndIndet(),
                                                                                "gml:TimePeriod/gml:endPosition" +
                                                                                    "[@indeterminatePosition]"));
            } else {
                throw new MissingParameterValueException("gml:TimePeriod/gml:endPosition");
            }

            observation.setSamplingTimeEnd(time.getEnd().toDate());
        }
    }

    /**
     * Add result time to observation object
     *
     * @param observation    Observation object
     * @param resultTime     SOS result time
     * @param phenomenonTime SOS phenomenon time
     * @throws OwsExceptionReport If an error occurs
     */
    public void addResultTimeToObservation(DataEntity<?> observation, TimeInstant resultTime, Time phenomenonTime)
        throws CodedException {
        if (resultTime != null) {
            if (resultTime.isSetValue()) {
                observation.setResultTime(resultTime.getValue().toDate());
            } else if (resultTime.isSetGmlId() && resultTime.getGmlId().contains(Sos2Constants.EN_PHENOMENON_TIME)
                && phenomenonTime instanceof TimeInstant instant) {
                if (instant.isSetValue()) {
                    observation.setResultTime(instant.getValue().toDate());
                } else if (instant.isSetIndeterminateValue()) {
                    observation.setResultTime(getDateForTimeIndeterminateValue(
                        instant.getIndeterminateValue(), INETERMINATE_POSITION_XPATH));
                } else {
                    throw new NoApplicableCodeException().withMessage(ERROR_ADDING_RESULT_TIME_LOG);
                }
            } else if (resultTime.isSetIndeterminateValue()) {
                observation.setResultTime(getDateForTimeIndeterminateValue(resultTime.getIndeterminateValue(),
                                                                           INETERMINATE_POSITION_XPATH));
            } else {
                throw new NoApplicableCodeException().withMessage(ERROR_ADDING_RESULT_TIME_LOG);
            }
        } else if (phenomenonTime instanceof TimeInstant instant1) {
            observation.setResultTime(instant1.getValue().toDate());
        } else {
            throw new NoApplicableCodeException().withMessage(ERROR_ADDING_RESULT_TIME_LOG);
        }
    }

    protected Date getDateForTimeIndeterminateValue(IndeterminateValue indeterminateValue, String parameter)
        throws InvalidParameterValueException {
        if (indeterminateValue.isNow()) {
            return new DateTime().toDate();
        }
        throw new InvalidParameterValueException(parameter, indeterminateValue.getValue());
    }

    /**
     * Add valid time to observation object
     *
     * @param observation Observation object
     * @param validTime   SOS valid time
     */
    protected void addValidTimeToObservation(DataEntity<?> observation, TimePeriod validTime) {
        if (validTime != null) {
            observation.setValidTimeStart(validTime.getStart().toDate());
            observation.setValidTimeEnd(validTime.getEnd().toDate());
        }
    }

    /**
     * Build a database-side bounding-box aggregate expression over a geometry path, equivalent to the
     * pre-Hibernate-6 {@code org.hibernate.spatial.criterion.SpatialProjections.extent(...)}.
     * <p>PostGIS's {@code st_extent(geometry)} returns {@code box2d}, not {@code geometry} -- Hibernate
     * Spatial 5's own {@code extent} projection handled this by appending a {@code ::geometry} cast
     * (see {@code org.hibernate.spatial.dialect.postgis.PostgisFunctions.ExtentFunction}); this does the
     * equivalent via {@link HibernateCriteriaBuilder#cast}, which renders a portable {@code cast(x as T)}.
     * H2GIS's {@code ST_Extent(geometry)} already returns {@code GEOMETRY}, so the added cast there is a
     * redundant (harmless) geometry-to-geometry cast.
     *
     * @param cb   CriteriaBuilder
     * @param root Root of the observation query
     * @return Expression yielding the aggregate envelope geometry over all matching rows
     */
    protected Expression<Geometry> spatialExtent(HibernateCriteriaBuilder cb, Root<? extends DataEntity<?>> root) {
        return cb.cast(cb.function("ST_Extent", Object.class, root.get(DataEntity.PROPERTY_GEOMETRY_ENTITY)),
                       Geometry.class);
    }

    public abstract List<org.locationtech.jts.geom.Geometry> getSamplingGeometries(String feature, Session session)
        throws OwsExceptionReport;

    public abstract Long getSamplingGeometriesCount(String feature, Session session) throws OwsExceptionReport;

    public abstract ObservationFactory getObservationFactory();
}