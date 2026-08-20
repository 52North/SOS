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
package org.n52.sos.ds.hibernate.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
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
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.util.ParameterCreator;
import org.n52.sos.util.GeometryHandler;

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
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AbstractFeatureEntity get(String identifier, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(AbstractFeatureEntity.class);
        Root root = query.from(AbstractFeatureEntity.class);
        query.where(cb.equal(root.get(AbstractFeatureEntity.IDENTIFIER), identifier));
        return (AbstractFeatureEntity) session.createQuery(query).uniqueResult();
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
    public List<String> getIdentifiersForOffering(String offering, Session session) throws OwsExceptionReport {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<DatasetEntity> root = query.from(DatasetEntity.class);
        Join<DatasetEntity, AbstractFeatureEntity> feature = root.join(DatasetEntity.PROPERTY_FEATURE);
        List<Predicate> predicates = new ArrayList<>(seriesDAO.defaultSeriesPredicates(cb, root));
        predicates.add(seriesDAO.offeringPredicate(cb, root, offering));
        query.select(feature.get(AbstractFeatureEntity.IDENTIFIER)).distinct(true)
                .where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).list();
    }

    @Override
    protected Class<? extends AbstractFeatureEntity> getFeatureEntityClass() {
        return FeatureEntity.class;
    }

    /**
     * Load FOI identifiers and parent ids for use in the cache. Just loading the ids allows us to not load
     * the geometry columns, XML, etc.
     *
     * @param session
     *            the session
     * @return Map keyed by FOI identifiers, with value collections of parent FOI identifiers if supported
     */
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
        if (featureOfInterest instanceof AbstractSamplingFeature sf) {
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
                if (abstractFeature instanceof FeatureWithGeometry geometry) {
                    if (geometry.isSetGeometry()) {
                        feature.setGeometry(geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(
                                geometry.getGeometry()));
                    }
                }
                if (abstractFeature.isSetXml()) {
                    feature.setXml(abstractFeature.getXml());
                }
                if (abstractFeature instanceof FeatureWithFeatureType type
                        && type.isSetFeatureType()) {
                    feature.setFeatureType(new FormatDAO().getOrInsertFormatEntity(
                            type.getFeatureType(), session));
                }
                if (abstractFeature instanceof AbstractSamplingFeature samplingFeature) {
                    if (samplingFeature.isSetSampledFeatures()) {
                        Set<AbstractFeatureEntity> parents =
                                Sets.newHashSetWithExpectedSize(samplingFeature.getSampledFeatures().size());
                        for (AbstractFeature sampledFeature : samplingFeature.getSampledFeatures()) {
                            if (!OGCConstants.UNKNOWN
                                    .equals(sampledFeature.getIdentifierCodeWithAuthority().getValue())) {
                                if (sampledFeature instanceof AbstractSamplingFeature abstractSamplingFeature) {
                                    parents.add(dao.insertFeature(abstractSamplingFeature, session));
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
                if (abstractFeature instanceof AbstractSamplingFeature samplingFeature1
                        && samplingFeature1.isSetParameter()) {
                    Map<UoM, UnitEntity> unitCache = Maps.newHashMap();
                    new ParameterCreator().createParameter(
                            samplingFeature1.getParameters(), unitCache, feature, session);
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
