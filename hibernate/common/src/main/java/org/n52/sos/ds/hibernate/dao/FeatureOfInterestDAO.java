/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.query.Query;
import org.hibernate.sql.JoinType;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
import org.n52.janmayen.function.Suppliers;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.series.db.beans.FormatEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.feature.SpecimenEntity;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.FeatureWith.FeatureWithFeatureType;
import org.n52.shetland.ogc.gml.FeatureWith.FeatureWithGeometry;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.om.features.samplingFeatures.FeatureOfInterestVisitor;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.om.features.samplingFeatures.SfSpecimen;
import org.n52.shetland.ogc.om.series.tsml.TsmlMonitoringFeature;
import org.n52.shetland.ogc.om.series.wml.WmlMonitoringPoint;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.NoopTransformerAdapter;
import org.n52.sos.ds.hibernate.util.ParameterCreator;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Hibernate data access class for featureOfInterest
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
@SuppressFBWarnings({ "EI_EXPOSE_REP2" })
public class FeatureOfInterestDAO extends AbstractFeatureOfInterestDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureOfInterestDAO.class);

    private static final String SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING =
            "getFeatureOfInterestIdentifiersForOffering";

    public FeatureOfInterestDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    public AbstractFeatureEntity insertFeature(AbstractFeature abstractFeature, Session session)
            throws OwsExceptionReport {

        FeatureOfInterestPersister persister =
                new FeatureOfInterestPersister(this, getDaoFactory().getGeometryHandler(), session);
        return abstractFeature.accept(persister);
    }

    /**
     * /** Get featureOfInterest object for identifier
     *
     * @param identifier
     *            FeatureOfInterest identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest entity
     */
    public AbstractFeatureEntity get(String identifier, Session session) {
        Criteria criteria = session.createCriteria(AbstractFeatureEntity.class)
                .add(Restrictions.eq(AbstractFeatureEntity.IDENTIFIER, identifier));
        LOGGER.trace("QUERY getFeatureOfInterest(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (AbstractFeatureEntity) criteria.uniqueResult();
    }

    /**
     * Get featureOfInterest identifiers for observation constellation
     *
     * @param oc
     *            Observation constellation
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest identifiers for observation constellation
     * @throws CodedException
     *             If an error occurs
     */
    @SuppressWarnings("unchecked")
    public List<String> getIdentifiers(DatasetEntity oc, Session session) throws OwsExceptionReport {
        return Lists.newArrayList(oc.getFeature().getIdentifier());
    }

    /**
     * Get all featureOfInterest identifiers
     *
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getIdentifiers(Session session) {
        Criteria criteria = session.createCriteria(FeatureEntity.class)
                .setProjection(Projections.distinct(Projections.property(AbstractFeatureEntity.IDENTIFIER)));
        LOGGER.trace("QUERY getFeatureOfInterestIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get featureOfInterest identifiers for an offering identifier
     *
     * @param offering
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest identifiers for offering
     * @throws CodedException
     *             If an error occurs
     */
    @SuppressWarnings({ "unchecked" })
    public List<String> getIdentifiersForOffering(String offering, Session session) throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING,
                session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offering);
            LOGGER.trace("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers) with NamedQuery: {}",
                    SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING);
            return namedQuery.list();
        } else {
            AbstractSeriesDAO datasetDAO = getDaoFactory().getSeriesDAO();
            Criteria c = datasetDAO.getDefaultSeriesCriteria(session).createCriteria(DatasetEntity.PROPERTY_FEATURE)
                    .setProjection(Projections.distinct(Projections.property(AbstractFeatureEntity.IDENTIFIER)));

            getDaoFactory().getOfferingDAO().addOfferingRestricionForObservation(c, offering);
            LOGGER.trace("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers): {}",
                    HibernateHelper.getSqlString(c));
            return c.list();
        }
    }

    private DetachedCriteria getDetachedCriteriaSeriesForOffering(String offering, Session session)
            throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria = getDetachedCriteriaSeries(session);
        detachedCriteria.createCriteria(DatasetEntity.PROPERTY_OFFERING)
                .add(Restrictions.eq(OfferingEntity.IDENTIFIER, offering));
        return detachedCriteria;
    }

    /**
     * Get featureOfInterest objects for featureOfInterest identifiers
     *
     * @param identifiers
     *            FeatureOfInterest identifiers
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest objects
     */
    @SuppressWarnings("unchecked")
    public List<FeatureEntity> getFeatureOfInterestObject(Collection<String> identifiers, Session session) {
        if (identifiers == null || identifiers.isEmpty()) {
            return Collections.emptyList();
        }
        Criteria criteria = session.createCriteria(FeatureEntity.class)
                .add(QueryHelper.getCriterionForObjects(AbstractFeatureEntity.IDENTIFIER, identifiers));
        LOGGER.trace("QUERY getFeatureOfInterestObject(identifiers): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Get all featureOfInterest objects
     *
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest objects
     */
    @SuppressWarnings("unchecked")
    public List<FeatureEntity> getFeatureOfInterestObjects(Session session) {
        Criteria criteria = getDefaultCriteria(session);
        LOGGER.trace("QUERY getFeatureOfInterestObjects(identifier): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    protected Criteria getDefaultCriteria(final Session session) {
        return session.createCriteria(FeatureEntity.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Load FOI identifiers and parent ids for use in the cache. Just loading the ids allows us to not load
     * the geometry columns, XML, etc.
     *
     * @param session
     *            the session
     * @return Map keyed by FOI identifiers, with value collections of parent FOI identifiers if supported
     */
    public Map<String, Collection<String>> getIdentifiersWithParents(Session session) {
        Criteria criteria = session.createCriteria(FeatureEntity.class)
                .createAlias(AbstractFeatureEntity.PROPERTY_PARENTS, "pfoi", JoinType.LEFT_OUTER_JOIN)
                .setProjection(Projections.projectionList().add(Projections.property(AbstractFeatureEntity.IDENTIFIER))
                        .add(Projections.property("pfoi." + AbstractFeatureEntity.IDENTIFIER)));
        // return as List<Object[]> even if there's only one column for
        // consistency
        criteria.setResultTransformer(NoopTransformerAdapter.INSTANCE);

        LOGGER.trace("QUERY getFeatureOfInterestIdentifiersWithParents(): {}", HibernateHelper.getSqlString(criteria));
        @SuppressWarnings("unchecked")
        List<Object[]> results = criteria.list();
        Map<String, Collection<String>> foiMap = Maps.newHashMap();
        results.forEach(result -> {
            String featureIdentifier = (String) result[0];
            String parentFeatureIdentifier = (String) result[1];
            if (parentFeatureIdentifier != null) {
                foiMap.computeIfAbsent(featureIdentifier, Suppliers.asFunction(ArrayList::new))
                        .add(parentFeatureIdentifier);
            } else {
                foiMap.put(featureIdentifier, null);
            }
        });
        return foiMap;
    }

    /**
     * Insert and/or get featureOfInterest object for identifier
     *
     * @param identifier
     *            FeatureOfInterest identifier
     * @param url
     *            FeatureOfInterest URL, if defined as link
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest object
     */
    public AbstractFeatureEntity getOrInsert(String identifier, String url, Session session) {
        AbstractFeatureEntity feature = get(identifier, session);
        if (feature == null) {
            feature = new FeatureEntity();
            feature.setIdentifier(identifier, getDaoFactory().isStaSupportsUrls());
            if (url != null && !url.isEmpty()) {
                feature.setUrl(url);
            }
            FormatEntity type = new FormatDAO().getOrInsertFormatEntity(OGCConstants.UNKNOWN, session);
            feature.setFeatureType(type);
            session.save(feature);
        } else if (feature.getUrl() != null && !feature.getUrl().isEmpty() && url != null && !url.isEmpty()) {
            feature.setUrl(url);
            session.saveOrUpdate(feature);
        }
        // don't flush here because we may be batching
        return feature;
    }

    /**
     * Insert featureOfInterest relationship
     *
     * @param parentFeature
     *            Parent featureOfInterest
     * @param childFeature
     *            Child featureOfInterest
     * @param session
     *            Hibernate session
     */
    public void insertRelationship(AbstractFeatureEntity parentFeature, AbstractFeatureEntity childFeature,
            Session session) {
        parentFeature.getChildren().add(childFeature);
        session.saveOrUpdate(parentFeature);
        // don't flush here because we may be batching
    }

    /**
     * Insert featureOfInterest/related feature relations if relatedFeatures exists for offering.
     *
     * @param featureOfInterest
     *            FeatureOfInerest
     * @param offering
     *            Offering
     * @param session
     *            Hibernate session
     */
    public void checkOrInsertRelatedFeatureRelation(AbstractFeatureEntity featureOfInterest, OfferingEntity offering,
            Session session) {
        getDaoFactory().getRelatedFeatureDAO().getRelatedFeatureForOffering(offering.getIdentifier(), session).stream()
                .filter(relatedFeature -> !featureOfInterest.getIdentifier()
                        .equals(relatedFeature.getFeature().getIdentifier()))
                .forEachOrdered(
                        relatedFeature -> insertRelationship(relatedFeature.getFeature(), featureOfInterest, session));

    }

    /**
     * Insert featureOfInterest if it is supported
     *
     * @param featureOfInterest
     *            SOS featureOfInterest to insert
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest object
     * @throws NoApplicableCodeException
     *             If SOS feature type is not supported (with status {@link HTTPStatus}.BAD_REQUEST
     */
    public AbstractFeatureEntity checkOrInsert(AbstractFeature featureOfInterest, Session session)
            throws OwsExceptionReport {
        if (featureOfInterest == null) {
            throw new NoApplicableCodeException().withMessage("The feature to check or insert is null.");
        }
        AbstractFeatureEntity<?> feature = getFeature(featureOfInterest.getIdentifier(), session);
        if (feature != null) {
            return feature;
        }
        if (featureOfInterest instanceof AbstractSamplingFeature) {
            AbstractSamplingFeature sf = (AbstractSamplingFeature) featureOfInterest;
            String featureIdentifier = getFeatureQueryHandler().insertFeature(sf, session);
            return getFeature(featureIdentifier, session);
        } else {
            throw new NoApplicableCodeException().withMessage("The used feature type '%s' is not supported.",
                    featureOfInterest.getClass().getName()).setStatus(HTTPStatus.BAD_REQUEST);
        }
    }

    public void updateFeatureOfInterestGeometry(AbstractFeatureEntity featureOfInterest, Geometry geom,
            Session session) {
        if (featureOfInterest != null) {
            if (featureOfInterest.isSetGeometry()) {
                if (geom instanceof Point) {
                    List<Coordinate> coords = Lists.newArrayList();
                    Geometry convert = featureOfInterest.getGeometry();
                    if (convert instanceof Point) {
                        coords.add(convert.getCoordinate());
                    } else if (convert instanceof LineString) {
                        coords.addAll(Lists.newArrayList(convert.getCoordinates()));
                    }
                    if (!coords.isEmpty()) {
                        coords.add(geom.getCoordinate());
                        Geometry newGeometry =
                                new GeometryFactory().createLineString(coords.toArray(new Coordinate[coords.size()]));
                        newGeometry.setSRID(featureOfInterest.getGeometry().getSRID());
                        featureOfInterest.setGeometry(newGeometry);
                    }
                }
            } else {
                featureOfInterest.setGeometry(geom);
            }
            session.merge(featureOfInterest);
        }
    }

    @SuppressWarnings("unchecked")
    public List<FeatureEntity> getPublishedFeatureOfInterest(Session session) throws OwsExceptionReport {
        Criteria c = getPublishedFeatureOfInterestCriteria(session);
        LOGGER.trace("QUERY getPublishedFeatureOfInterest(): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }

    public Criteria getPublishedFeatureOfInterestCriteria(Session session) throws OwsExceptionReport {
        Criteria c = getDefaultCriteria(session);
        if (HibernateHelper.isEntitySupported(DatasetEntity.class)) {
            c.add(Subqueries.propertyNotIn(AbstractFeatureEntity.PROPERTY_ID,
                    getPublishedDetachedCriteriaSeries(session)));
        }
        return c;
    }

    private DetachedCriteria getPublishedDetachedCriteriaSeries(Session session) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria =
                DetachedCriteria.forClass(getDaoFactory().getSeriesDAO().getSeriesClass());
        detachedCriteria.add(Restrictions.disjunction(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, true),
                Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, false)));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_FEATURE)));
        return detachedCriteria;
    }

    private DetachedCriteria getDetachedCriteriaSeries(Session session) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria =
                DetachedCriteria.forClass(getDaoFactory().getSeriesDAO().getSeriesClass());
        detachedCriteria.add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, false))
                .add(Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, true));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(DatasetEntity.PROPERTY_FEATURE)));
        return detachedCriteria;
    }

    @SuppressWarnings("unchecked")
    public List<String> getPublishedFeatureOfInterestIdentifiers(Session session) throws OwsExceptionReport {
        Criteria c = getPublishedFeatureOfInterestCriteria(session);
        c.setProjection(Projections.distinct(Projections.property(AbstractFeatureEntity.IDENTIFIER)));
        LOGGER.trace("QUERY getPublishedFeatureOfInterestIdentifiers(): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }

    private FeatureQueryHandler getFeatureQueryHandler() {
        return getDaoFactory().getFeatureQueryHandler();
    }

    public static class FeatureOfInterestPersister implements FeatureOfInterestVisitor<AbstractFeatureEntity> {

        private FeatureOfInterestDAO dao;

        private Session session;

        private GeometryHandler geometryHandler;

        public FeatureOfInterestPersister(FeatureOfInterestDAO dao, GeometryHandler geometryHandler, Session sesion) {
            this.dao = dao;
            this.session = sesion;
            this.geometryHandler = geometryHandler;
        }

        @Override
        public AbstractFeatureEntity visit(SamplingFeature value) throws OwsExceptionReport {
            AbstractFeatureEntity feature = getFeatureOfInterest(value);
            if (feature == null) {
                return persist(new FeatureEntity(), value, true);
            }
            return persist(feature, value, false);
        }

        @Override
        public AbstractFeatureEntity visit(SfSpecimen value) throws OwsExceptionReport {
            AbstractFeatureEntity feature = getFeatureOfInterest(value);
            if (feature == null) {
                SpecimenEntity specimen = new SpecimenEntity();
                specimen.setMaterialClass(value.getMaterialClass().getHref());
                if (value.getSamplingTime() instanceof TimeInstant) {
                    TimeInstant time = (TimeInstant) value.getSamplingTime();
                    specimen.setSamplingTimeStart(time.getValue().toDate());
                    specimen.setSamplingTimeEnd(time.getValue().toDate());
                } else if (value.getSamplingTime() instanceof TimePeriod) {
                    TimePeriod time = (TimePeriod) value.getSamplingTime();
                    specimen.setSamplingTimeStart(time.getStart().toDate());
                    specimen.setSamplingTimeEnd(time.getEnd().toDate());
                }
                if (value.isSetSamplingMethod()) {
                    specimen.setSamplingMethod(value.getSamplingMethod().getReference().getHref().toString());
                }
                if (value.isSetSize()) {
                    specimen.setSize(value.getSize().getValue().doubleValue());
                    specimen.setSizeUnit(getUnit(value.getSize()));
                }
                if (value.isSetCurrentLocation()) {
                    specimen.setCurrentLocation(value.getCurrentLocation().getReference().getHref().toString());
                }
                if (value.isSetSpecimenType()) {
                    specimen.setSpecimenType(value.getSpecimenType().getHref());
                }
                return persist(specimen, value, true);
            }
            return persist(feature, value, false);
        }

        @Override
        public AbstractFeatureEntity visit(WmlMonitoringPoint monitoringPoint) throws OwsExceptionReport {
            throw new NotYetSupportedException(WmlMonitoringPoint.class.getSimpleName());
        }

        @Override
        public AbstractFeatureEntity visit(TsmlMonitoringFeature value) throws OwsExceptionReport {
            throw new NotYetSupportedException(TsmlMonitoringFeature.class.getSimpleName());
        }

        private AbstractFeatureEntity persist(AbstractFeatureEntity feature, AbstractFeature abstractFeature,
                boolean add) throws OwsExceptionReport {
            if (add) {
                dao.addIdentifierNameDescription(abstractFeature, feature, session);
                if (abstractFeature instanceof FeatureWithGeometry) {
                    if (((FeatureWithGeometry) abstractFeature).isSetGeometry()) {
                        feature.setGeometry(geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(
                                ((FeatureWithGeometry) abstractFeature).getGeometry()));
                    }
                }
                if (abstractFeature.isSetXml()) {
                    feature.setXml(abstractFeature.getXml());
                }
                if (abstractFeature instanceof FeatureWithFeatureType
                        && ((FeatureWithFeatureType) abstractFeature).isSetFeatureType()) {
                    feature.setFeatureType(new FormatDAO().getOrInsertFormatEntity(
                            ((FeatureWithFeatureType) abstractFeature).getFeatureType(), session));
                }
                if (abstractFeature instanceof AbstractSamplingFeature) {
                    AbstractSamplingFeature samplingFeature = (AbstractSamplingFeature) abstractFeature;
                    if (samplingFeature.isSetSampledFeatures()) {
                        Set<AbstractFeatureEntity> parents =
                                Sets.newHashSetWithExpectedSize(samplingFeature.getSampledFeatures().size());
                        for (AbstractFeature sampledFeature : samplingFeature.getSampledFeatures()) {
                            if (!OGCConstants.UNKNOWN
                                    .equals(sampledFeature.getIdentifierCodeWithAuthority().getValue())) {
                                if (sampledFeature instanceof AbstractSamplingFeature) {
                                    parents.add(dao.insertFeature((AbstractSamplingFeature) sampledFeature, session));
                                } else {
                                    parents.add(dao.insertFeature(
                                            new SamplingFeature(sampledFeature.getIdentifierCodeWithAuthority()),
                                            session));
                                }
                            }
                        }
                        feature.setParents(parents);
                    }
                }
                if (abstractFeature instanceof AbstractSamplingFeature
                        && ((AbstractSamplingFeature) abstractFeature).isSetParameter()) {
                    Map<UoM, UnitEntity> unitCache = Maps.newHashMap();
                    new ParameterCreator().createParameter(
                            ((AbstractSamplingFeature) abstractFeature).getParameters(), unitCache, feature, session);
                }
                session.saveOrUpdate(feature);
                session.flush();
                session.refresh(feature);
            }
            return feature;
        }

        private UnitEntity getUnit(Value<?> value) {
            return value.isSetUnit() ? new UnitDAO().getOrInsertUnit(value.getUnitObject(), session) : null;
        }

        private AbstractFeatureEntity getFeatureOfInterest(AbstractSamplingFeature value) throws OwsExceptionReport {
            final String newId = value.getIdentifierCodeWithAuthority().getValue();
            Geometry geom = ((FeatureWithGeometry) value).getGeometry();
            return dao.getFeatureOfInterest(newId, geom, session);
        }
    }
}
