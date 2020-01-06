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

import static org.n52.sos.util.http.HTTPStatus.BAD_REQUEST;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterestType;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.RelatedFeature;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.feature.Specimen;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.ContextualReferencedLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.ContextualReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.NoopTransformerAdapter;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.NotYetSupportedException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.FeatureWith.FeatureWithFeatureType;
import org.n52.sos.ogc.gml.FeatureWith.FeatureWithGeometry;
import org.n52.sos.ogc.gml.FeatureWith.FeatureWithXmlDescription;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.FeatureOfInterestVisitor;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SfSpecimen;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.series.wml.WmlMonitoringPoint;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.HTTPStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * Hibernate data access class for featureOfInterest
 *
 * @author CarstenHollmann
 * @since 4.0.0
 */
public class FeatureOfInterestDAO extends AbstractFeatureOfInterestDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeatureOfInterestDAO.class);

    private static final String SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING =
            "getFeatureOfInterestIdentifiersForOffering";

    private static final String SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION =
            "getFeatureOfInterestIdentifiersForObservationConstellation";

    @Override
    public AbstractFeatureOfInterest insertFeature(AbstractFeature abstractFeature, Session session)
            throws OwsExceptionReport {
        
        FeatureOfInterestPersister persister = new FeatureOfInterestPersister(
                this,
                session
        );
        return abstractFeature.accept(persister);
    }

    /**
     * Get featureOfInterest object for identifier
     *
     * @param identifier
     *            FeatureOfInterest identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest entity
     */
    public AbstractFeatureOfInterest getFeatureOfInterest(final String identifier, final Session session) {
        Criteria criteria = session.createCriteria(FeatureOfInterest.class)
                .add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, identifier));
        LOGGER.debug("QUERY getFeatureOfInterest(identifier): {}", HibernateHelper.getSqlString(criteria));
        return (FeatureOfInterest) criteria.uniqueResult();
    }

    /**
     * Get featureOfInterest identifiers for observation constellation
     *
     * @param observationConstellation
     *            Observation constellation
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest identifiers for observation constellation
     * @throws CodedException
     */
    @SuppressWarnings("unchecked")
    public List<String> getFeatureOfInterestIdentifiersForObservationConstellation(
            final ObservationConstellation observationConstellation, final Session session) throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(
                SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION, session)) {
            Query namedQuery =
                    session.getNamedQuery(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION);
            namedQuery.setParameter(PROCEDURE, observationConstellation.getProcedure().getIdentifier());
            namedQuery.setParameter(OBSERVABLE_PROPERTY,
                    observationConstellation.getObservableProperty().getIdentifier());
            namedQuery.setParameter(OFFERING, observationConstellation.getOffering().getIdentifier());
            LOGGER.debug(
                    "QUERY getFeatureOfInterestIdentifiersForObservationConstellation(observationConstellation) with NamedQuery: {}",
                    SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OBSERVATION_CONSTELLATION);
            return namedQuery.list();
        } else {
            AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
            Criteria criteria = observationDAO.getDefaultObservationInfoCriteria(session);
            if (observationDAO instanceof SeriesObservationDAO) {
                Criteria seriesCriteria = criteria.createCriteria(ContextualReferencedSeriesObservation.SERIES);
                seriesCriteria.add(Restrictions.eq(Series.PROCEDURE, observationConstellation.getProcedure())).add(
                        Restrictions.eq(Series.OBSERVABLE_PROPERTY, observationConstellation.getObservableProperty()));
                seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST)
                        .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            } else {
                criteria.add(
                        Restrictions.eq(ObservationConstellation.PROCEDURE, observationConstellation.getProcedure()))
                        .add(Restrictions.eq(ObservationConstellation.OBSERVABLE_PROPERTY,
                                observationConstellation.getObservableProperty()));
                criteria.createCriteria(ContextualReferencedLegacyObservation.FEATURE_OF_INTEREST)
                        .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            }
            criteria.createCriteria(AbstractObservation.OFFERINGS)
                    .add(Restrictions.eq(Offering.ID, observationConstellation.getOffering().getOfferingId()));
            LOGGER.debug(
                    "QUERY getFeatureOfInterestIdentifiersForObservationConstellation(observationConstellation): {}",
                    HibernateHelper.getSqlString(criteria));
            return criteria.list();
        }
    }

    /**
     * Get featureOfInterest identifiers for an offering identifier
     *
     * @param offeringIdentifiers
     *            Offering identifier
     * @param session
     *            Hibernate session Hibernate session
     * @return FeatureOfInterest identifiers for offering
     * @throws CodedException
     */
    @SuppressWarnings({ "unchecked" })
    public List<String> getFeatureOfInterestIdentifiersForOffering(final String offeringIdentifiers,
            final Session session) throws OwsExceptionReport {
        if (HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING,
                session)) {
            Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING);
            namedQuery.setParameter(OFFERING, offeringIdentifiers);
            LOGGER.debug("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers) with NamedQuery: {}",
                    SQL_QUERY_GET_FEATURE_OF_INTEREST_IDENTIFIER_FOR_OFFERING);
            return namedQuery.list();
        } else {
            Criteria c = null;
            if (EntitiyHelper.getInstance().isSeriesSupported()) {
                c = session.createCriteria(FeatureOfInterest.class)
                        .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
                c.add(Subqueries.propertyIn(FeatureOfInterest.ID,
                        getDetachedCriteriaSeriesForOffering(offeringIdentifiers, session)));
                LOGGER.debug("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers): {}",
                        HibernateHelper.getSqlString(c));
                List list = c.list();
                if (list == null || (list != null && list.isEmpty())) {
                    c = DaoFactory.getInstance().getObservationDAO().getDefaultObservationInfoCriteria(session);
                    Criteria seriesCriteria = c.createCriteria(AbstractSeriesObservation.SERIES);
                    seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST)
                            .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
                    new OfferingDAO().addOfferingRestricionForObservation(c, offeringIdentifiers);
                    LOGGER.debug("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers): {}",
                            HibernateHelper.getSqlString(c));
                }
                LOGGER.debug("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers): {}",
                        HibernateHelper.getSqlString(c));
                return list;
            } else {
                c = DaoFactory.getInstance().getObservationDAO().getDefaultObservationInfoCriteria(session);
                c.createCriteria(AbstractObservation.FEATURE_OF_INTEREST)
                        .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
                new OfferingDAO().addOfferingRestricionForObservation(c, offeringIdentifiers);
            }
            LOGGER.debug("QUERY getFeatureOfInterestIdentifiersForOffering(offeringIdentifiers): {}",
                    HibernateHelper.getSqlString(c));
            return c.list();
        }
    }

    private DetachedCriteria getDetachedCriteriaSeriesForOffering(String offering, Session session) throws CodedException {
        final DetachedCriteria detachedCriteria = getDetachedCriteriaSeries(session);
        detachedCriteria.createCriteria(Series.OFFERING).add(Restrictions.eq(Offering.IDENTIFIER, offering));
        return detachedCriteria;
    }

    /**
     * Get all featureOfInterest objects
     *
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest objects
     */
    @SuppressWarnings("unchecked")
    public List<FeatureOfInterest> getFeatureOfInterestObjects(final Session session) {
        Criteria criteria = getDefaultCriteria(session);
        LOGGER.debug("QUERY getFeatureOfInterestObjects(identifier): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }
    
    protected Criteria getDefaultCriteria(final Session session) {
        return session.createCriteria(FeatureOfInterest.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * Load FOI identifiers and parent ids for use in the cache. Just loading
     * the ids allows us to not load the geometry columns, XML, etc.
     *
     * @param session
     * @return Map keyed by FOI identifiers, with value collections of parent
     *         FOI identifiers if supported
     */
    public Map<String, Collection<String>> getFeatureOfInterestIdentifiersWithParents(final Session session) {
        Criteria criteria = session.createCriteria(FeatureOfInterest.class);
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property(FeatureOfInterest.IDENTIFIER));
        criteria.createAlias(FeatureOfInterest.PARENTS, "pfoi", JoinType.LEFT_OUTER_JOIN);
        projectionList.add(Projections.property("pfoi." + FeatureOfInterest.IDENTIFIER));
        criteria.setProjection(projectionList);
        // return as List<Object[]> even if there's only one column for
        // consistency
        criteria.setResultTransformer(NoopTransformerAdapter.INSTANCE);

        LOGGER.debug("QUERY getFeatureOfInterestIdentifiersWithParents(): {}", HibernateHelper.getSqlString(criteria));
        @SuppressWarnings("unchecked")
        List<Object[]> results = criteria.list();
        Map<String, Collection<String>> foiMap = Maps.newHashMap();
        for (Object[] result : results) {
            String featureIdentifier = (String) result[0];
            String parentFeatureIdentifier = null;
            parentFeatureIdentifier = (String) result[1];
            if (parentFeatureIdentifier != null) {
                CollectionHelper.addToCollectionMap(featureIdentifier, parentFeatureIdentifier, foiMap);
            } else {
                foiMap.put(featureIdentifier, null);
            }
        }
        return foiMap;
    }

    /**
     * Get all featureOfInterest identifiers
     *
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest identifiers
     */
    @SuppressWarnings("unchecked")
    public List<String> getFeatureOfInterestIdentifiers(Session session) {
        Criteria criteria = session.createCriteria(FeatureOfInterest.class)
                .setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
        LOGGER.debug("QUERY getFeatureOfInterestIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
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
    public AbstractFeatureOfInterest getOrInsertFeatureOfInterest(final String identifier, final String url,
            final Session session) {
        AbstractFeatureOfInterest feature = getFeatureOfInterest(identifier, session);
        if (feature == null) {
            feature = new FeatureOfInterest();
            feature.setIdentifier(identifier);
            if (url != null && !url.isEmpty()) {
                feature.setUrl(url);
            }
            final FeatureOfInterestType featureOfInterestType =
                    new FeatureOfInterestTypeDAO().getOrInsertFeatureOfInterestType(OGCConstants.UNKNOWN, session);
            feature.setFeatureOfInterestType(featureOfInterestType);
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
    public void insertFeatureOfInterestRelationShip(final AbstractFeatureOfInterest parentFeature,
            final AbstractFeatureOfInterest childFeature, final Session session) {
        parentFeature.getChilds().add(childFeature);
        session.saveOrUpdate(parentFeature);
        // don't flush here because we may be batching
    }

    /**
     * Insert featureOfInterest/related feature relations if relatedFeatures
     * exists for offering.
     *
     * @param featureOfInterest
     *            FeatureOfInerest
     * @param offering
     *            Offering
     * @param session
     *            Hibernate session
     */
    public void checkOrInsertFeatureOfInterestRelatedFeatureRelation(final AbstractFeatureOfInterest featureOfInterest,
            final Offering offering, final Session session) {
        final List<RelatedFeature> relatedFeatures =
                new RelatedFeatureDAO().getRelatedFeatureForOffering(offering.getIdentifier(), session);
        if (CollectionHelper.isNotEmpty(relatedFeatures)) {
            for (final RelatedFeature relatedFeature : relatedFeatures) {
                if (!featureOfInterest.getIdentifier().equals(relatedFeature.getFeatureOfInterest().getIdentifier())) {
                    insertFeatureOfInterestRelationShip(relatedFeature.getFeatureOfInterest(), featureOfInterest,
                            session);
                }
            }
        }
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
     *             If SOS feature type is not supported (with status
     *             {@link HTTPStatus}.BAD_REQUEST
     */
    public AbstractFeatureOfInterest checkOrInsertFeatureOfInterest(final AbstractFeature featureOfInterest,
            final Session session) throws OwsExceptionReport {
        if (featureOfInterest instanceof AbstractSamplingFeature) {
            final String featureIdentifier = Configurator.getInstance().getFeatureQueryHandler()
                    .insertFeature((AbstractSamplingFeature) featureOfInterest, session);
            return getOrInsertFeatureOfInterest(featureIdentifier, ((AbstractSamplingFeature) featureOfInterest).getUrl(),
                    session);
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("The used feature type '%s' is not supported.",
                            featureOfInterest != null ? featureOfInterest.getClass().getName() : featureOfInterest)
                    .setStatus(BAD_REQUEST);
        }
    }
    
    public static class FeatureOfInterestPersister implements FeatureOfInterestVisitor<AbstractFeatureOfInterest> {
        
        private FeatureOfInterestDAO dao;
        private Session session;
        
        public FeatureOfInterestPersister(FeatureOfInterestDAO dao, Session sesion) {
           this.dao = dao;
           this.session = sesion;
        }

        @Override
        public AbstractFeatureOfInterest visit(SamplingFeature value) throws OwsExceptionReport {
            AbstractFeatureOfInterest feature = getFeatureOfInterest(value);
            if (feature == null) {
                return persist(new FeatureOfInterest(), value, true);
            }
            return persist(feature, value, false);
        }

        @Override
        public AbstractFeatureOfInterest visit(SfSpecimen value) throws OwsExceptionReport {
            AbstractFeatureOfInterest feature = getFeatureOfInterest(value);
            if (feature == null) {
                Specimen specimen = new Specimen();
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
                    specimen.setSize(value.getSize().getValue());
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
        public AbstractFeatureOfInterest visit(WmlMonitoringPoint monitoringPoint) throws OwsExceptionReport {
           throw new NotYetSupportedException(WmlMonitoringPoint.class.getSimpleName());
//            return null;
        }

        private AbstractFeatureOfInterest persist(AbstractFeatureOfInterest feature, AbstractFeature abstractFeature, boolean add) throws OwsExceptionReport {
            if (add) {
                dao.addIdentifierNameDescription(abstractFeature, feature, session);
                if (abstractFeature instanceof FeatureWithGeometry) {
                    if (((FeatureWithGeometry) abstractFeature).isSetGeometry()) {
                        feature.setGeom(((FeatureWithGeometry) abstractFeature).getGeometry());
                    }
                }
                if (abstractFeature instanceof FeatureWithXmlDescription
                        && ((FeatureWithXmlDescription) abstractFeature).isSetXmlDescription()) {
                    feature.setDescriptionXml(((FeatureWithXmlDescription) abstractFeature).getXmlDescription());
                }
                if (abstractFeature instanceof FeatureWithFeatureType
                        && ((FeatureWithFeatureType) abstractFeature).isSetFeatureType()) {
                    feature.setFeatureOfInterestType(new FeatureOfInterestTypeDAO().getOrInsertFeatureOfInterestType(
                            ((FeatureWithFeatureType) abstractFeature).getFeatureType(), session));
                }
                if (abstractFeature instanceof AbstractSamplingFeature) {
                    AbstractSamplingFeature samplingFeature = (AbstractSamplingFeature) abstractFeature;
                    if (samplingFeature.isSetSampledFeatures()) {
                        Set<AbstractFeatureOfInterest> parents =
                                Sets.newHashSetWithExpectedSize(samplingFeature.getSampledFeatures().size());
                        for (AbstractFeature sampledFeature : samplingFeature.getSampledFeatures()) {
                            if (!OGCConstants.UNKNOWN.equals(sampledFeature.getIdentifierCodeWithAuthority().getValue())) {
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
                session.saveOrUpdate(feature);
                session.flush();
                session.refresh(feature);
            }
            if (abstractFeature instanceof AbstractSamplingFeature && ((AbstractSamplingFeature) abstractFeature).isSetParameter()) {
                Map<UoM, Unit> unitCache = Maps.newHashMap();
                new FeatureParameterDAO().insertParameter(((AbstractSamplingFeature) abstractFeature).getParameters(),
                        feature.getFeatureOfInterestId(), unitCache, session);
            }
            return feature;
        }

        private Unit getUnit(Value<?> value) {
            return value.isSetUnit() ? new UnitDAO().getOrInsertUnit(value.getUnitObject(), session) : null;
        }

        private AbstractFeatureOfInterest getFeatureOfInterest(AbstractSamplingFeature value) throws OwsExceptionReport {
            final String newId = value.getIdentifierCodeWithAuthority().getValue();
            Geometry geom = null;
            if (value instanceof FeatureWithGeometry) {
                geom = ((FeatureWithGeometry) value).getGeometry();
        
            }
            return dao.getFeatureOfInterest(newId, geom, session);
        }
    }

    public void updateFeatureOfInterestGeometry(AbstractFeatureOfInterest featureOfInterest, Geometry geom, Session session) {
        if (featureOfInterest.isSetGeometry()) {
            if (geom instanceof Point) {
                List<Coordinate> coords = Lists.newArrayList();
                if (featureOfInterest.getGeom() instanceof Point) {
                    coords.add(featureOfInterest.getGeom().getCoordinate());
                } else if (featureOfInterest.getGeom() instanceof LineString) {
                    coords.addAll(Lists.newArrayList(featureOfInterest.getGeom().getCoordinates()));
                }
                if (!coords.isEmpty()) {
                    coords.add(geom.getCoordinate());
                    Geometry newGeometry =
                            new GeometryFactory().createLineString(coords.toArray(new Coordinate[coords.size()]));
                    newGeometry.setSRID(featureOfInterest.getGeom().getSRID());
                    featureOfInterest.setGeom(newGeometry);
                }
            }
        } else {
            featureOfInterest.setGeom(geom);
        }
        session.saveOrUpdate(featureOfInterest);
    }

    @SuppressWarnings("unchecked")
    public List<FeatureOfInterest> getPublishedFeatureOfInterest(Session session) throws CodedException {
        Criteria c = getPublishedFeatureOfInterestCriteria(session);
        LOGGER.debug("QUERY getPublishedFeatureOfInterest(): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }

    public Criteria getPublishedFeatureOfInterestCriteria(Session session) throws CodedException {
        Criteria c = getDefaultCriteria(session);
        if (HibernateHelper.isEntitySupported(Series.class)) {
            c.add(Subqueries.propertyNotIn(FeatureOfInterest.ID, getPublishedDetachedCriteriaSeries(session)));
        }
        return c;
    }
    
    private DetachedCriteria getPublishedDetachedCriteriaSeries(Session session) throws CodedException {
        final DetachedCriteria detachedCriteria =
                DetachedCriteria.forClass(DaoFactory.getInstance().getSeriesDAO().getSeriesClass());
        detachedCriteria.add(Restrictions.disjunction(Restrictions.eq(Series.DELETED, true), Restrictions.eq(Series.PUBLISHED, false)));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(Series.FEATURE_OF_INTEREST)));
        return detachedCriteria;
    }

    private DetachedCriteria getDetachedCriteriaSeries(Session session) throws CodedException {
        final DetachedCriteria detachedCriteria =
                DetachedCriteria.forClass(DaoFactory.getInstance().getSeriesDAO().getSeriesClass());
        detachedCriteria.add(Restrictions.eq(Series.DELETED, false)).add(Restrictions.eq(Series.PUBLISHED, true));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(Series.FEATURE_OF_INTEREST)));
        return detachedCriteria;
    }

    @SuppressWarnings("unchecked")
    public List<String> getPublishedFeatureOfInterestIdentifiers(Session session) throws CodedException {
        Criteria c = getPublishedFeatureOfInterestCriteria(session);
        c.setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
        LOGGER.debug("QUERY getPublishedFeatureOfInterestIdentifiers(): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }
}
