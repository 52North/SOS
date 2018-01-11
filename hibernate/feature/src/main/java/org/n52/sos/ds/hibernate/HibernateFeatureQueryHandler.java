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
package org.n52.sos.ds.hibernate;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
import org.n52.iceland.i18n.I18NDAO;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.iceland.i18n.metadata.I18NFeatureMetadata;
import org.n52.janmayen.i18n.LocalizedString;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.JTSHelper;
import org.n52.shetland.util.JavaHelper;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestTypeDAO;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.SosHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configurable
public class HibernateFeatureQueryHandler implements FeatureQueryHandler, HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateFeatureQueryHandler.class);

    private Locale defaultLocale;
    private boolean showAllLanguages;
    private I18NDAORepository i18NDAORepository;
    private DaoFactory daoFactory;

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setI18NDAORepository(I18NDAORepository i18NDAORepository) {
        this.i18NDAORepository = i18NDAORepository;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = new Locale(defaultLocale);
    }

    @Setting(I18NSettings.I18N_SHOW_ALL_LANGUAGE_VALUES)
    public void setShowAllLanguages(boolean showAllLanguages) {
        this.showAllLanguages = showAllLanguages;
    }

    @Override
    public AbstractFeature getFeatureByID(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        try {
            Criteria q = session.createCriteria(FeatureOfInterest.class)
                    .add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, queryObject.getFeatureIdentifier()));
            return createSosAbstractFeature((FeatureOfInterest) q.uniqueResult(), queryObject);
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "An error occurred while querying feature data for a featureOfInterest identifier!");
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<String> getFeatureIDs(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        try {
            if (getGeometryHandler().isSpatialDatasource()) {
                final Criteria c
                        = session.createCriteria(FeatureOfInterest.class).setProjection(
                        Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
                if (queryObject.isSetSpatialFilters()) {
                    SpatialFilter filter = queryObject.getSpatialFitler();
                    c.add(SpatialRestrictions.filter(FeatureOfInterest.GEOMETRY, filter.getOperator(),
                            getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry())));
                }
                return c.list();
            } else {

                final List<String> identifiers = new LinkedList<>();
                final List<FeatureOfInterest> features = session.createCriteria(FeatureOfInterest.class).list();
                if (queryObject.isSetSpatialFilters()) {
                    SpatialFilter filter = queryObject.getSpatialFitler();
                    final Geometry envelope = getGeometryHandler().getFilterForNonSpatialDatasource(filter);
                    for (final FeatureOfInterest feature : features) {
                        final Geometry geom = getGeomtery(feature, session);
                        if (geom != null && envelope.contains(geom)) {
                            identifiers.add(feature.getIdentifier());
                        }
                    }
                }
                return identifiers;
            }
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "An error occurred while querying feature identifiers for spatial filter!");
        }
    }

    @Override
    public Map<String, AbstractFeature> getFeatures(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport {
        try {
            if (getGeometryHandler().isSpatialDatasource()) {
                return getFeaturesForSpatialDatasource(queryObject);
            } else {
                return getFeaturesForNonSpatialDatasource(queryObject);
            }
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while querying features from data source!");
        }
    }

    @Override
    public ReferencedEnvelope getEnvelopeForFeatureIDs(FeatureQueryHandlerQueryObject queryObject) throws
            OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        if (queryObject.isSetFeatureIdentifiers()) {
            try {
                // XXX workaround for Hibernate Spatial's lack of support for
                // GeoDB's extent aggregate
                // see
                // http://www.hibernatespatial.org/pipermail/hibernatespatial-users/2013-August/000876.html
                Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
                if (getGeometryHandler().isSpatialDatasource()
                        && HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                    // Criteria featureExtentCriteria =
                    // session.createCriteria(FeatureOfInterest.class)
                    // .add(Restrictions.in(FeatureOfInterest.IDENTIFIER,
                    // featureIDs))
                    // .setProjection(SpatialProjections.extent(FeatureOfInterest.GEOMETRY));
                    // LOGGER.debug("QUERY getEnvelopeForFeatureIDs(featureIDs): {}",
                    // HibernateHelper.getSqlString(featureExtentCriteria));
                    // Geometry geom = (Geometry)
                    // featureExtentCriteria.uniqueResult();
                    Geometry geom
                            = (Geometry) session
                            .createCriteria(FeatureOfInterest.class)
                            .add(QueryHelper.getCriterionForFoiIds(FeatureOfInterest.IDENTIFIER,
                                    queryObject.getFeatureIdentifiers()))
                            .setProjection(SpatialProjections.extent(FeatureOfInterest.GEOMETRY))
                            .uniqueResult();
                    if (geom != null) {
                        int srid = geom.getSRID() > 0 ? geom.getSRID() : getStorageEPSG();
                        geom.setSRID(srid);
                        geom = getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geom);
                        return new ReferencedEnvelope(geom.getEnvelopeInternal(), srid);
                    }
                } else {
                    final Envelope envelope = new Envelope();
                    final List<FeatureOfInterest> featuresOfInterest
                            = daoFactory.getFeatureOfInterestDAO().getFeatureOfInterestObject(queryObject
                                    .getFeatureIdentifiers(),
                                    session);
                    for (final FeatureOfInterest feature : featuresOfInterest) {
                        try {
                            // TODO Check if prepareGeometryForResponse required
                            // transform/switch
                            // final Geometry geom =
                            // getGeometryHandler().prepareGeometryForResponse(getGeomtery(feature),
                            // queryObject.getRequestedSrid());
                            final Geometry geom = getGeomtery(feature, session);
                            if (geom != null) {
                                envelope.expandToInclude(geom.getEnvelopeInternal());
                            }
                        } catch (final OwsExceptionReport owse) {
                            LOGGER.warn(
                                    String.format("Error while adding '%s' to envelope!",
                                            feature.getFeatureOfInterestId()), owse);
                        }
                    }
                    if (!envelope.isNull()) {
                        return new ReferencedEnvelope(envelope, getGeometryHandler().getStorageEPSG());
                    }
                }
            } catch (final HibernateException he) {
                throw new NoApplicableCodeException().causedBy(he).withMessage(
                        "Exception thrown while requesting global feature envelope");
            }
        }
        return null;
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * org.n52.sos.ds.FeatureQueryHandler#insertFeature(org.n52.sos.ogc.om.features
     * .samplingFeatures.SamplingFeature, java.lang.Object)
     *
     * FIXME check semantics of this method in respect to its name and the
     * documentation in the super class
     */
    @Override
    public String insertFeature(final SamplingFeature samplingFeature, final Object connection)
            throws OwsExceptionReport {
        if (!Strings.isNullOrEmpty(samplingFeature.getUrl())) {
            if (samplingFeature.isSetIdentifier()) {
                return samplingFeature.getIdentifierCodeWithAuthority().getValue();
            } else {
                return samplingFeature.getUrl();
            }
        } else {
            final Session session = HibernateSessionHolder.getSession(connection);
            String featureIdentifier;
            if (!samplingFeature.isSetIdentifier()) {
                featureIdentifier
                        = SosConstants.GENERATED_IDENTIFIER_PREFIX
                        + JavaHelper.generateID(samplingFeature.getXml());
                samplingFeature.setIdentifier(new CodeWithAuthority(featureIdentifier));
            }
            return insertFeatureOfInterest(samplingFeature, session).getIdentifier();
        }
    }

    @Override
    public int getStorageEPSG() {
        return getGeometryHandler().getStorageEPSG();
    }

    @Override
    public int getStorage3DEPSG() {
        return getGeometryHandler().getStorage3DEPSG();
    }

    @Override
    public int getDefaultResponseEPSG() {
        return getGeometryHandler().getDefaultResponseEPSG();
    }

    @Override
    public int getDefaultResponse3DEPSG() {
        return getGeometryHandler().getDefaultResponse3DEPSG();
    }

    protected GeometryHandler getGeometryHandler() {
        return GeometryHandler.getInstance();
    }

    private boolean isFeatureReferenced(final SamplingFeature samplingFeature) {
        return !Strings.isNullOrEmpty(samplingFeature.getUrl());
    }

    /**
     * Creates a map with FOI identifier and SOS feature
     *
     * @param features FeatureOfInterest objects
     * @param queryObject SOS version
     * @param session
     * @return Map with FOI identifier and SOS feature
     * @throws OwsExceptionReport * If feature type is not supported
     */
    protected Map<String, AbstractFeature> createSosFeatures(final List<FeatureOfInterest> features,
            final FeatureQueryHandlerQueryObject queryObject, Session session) throws OwsExceptionReport {
        final Map<String, AbstractFeature> sosAbstractFois = new HashMap<>(features.size());
        for (final FeatureOfInterest feature : features) {
            final AbstractFeature sosFeature = createSosAbstractFeature(feature, queryObject, session);
            sosAbstractFois.put(feature.getIdentifier(), sosFeature);
        }
        // TODO if sampledFeatures are also in sosAbstractFois, reference them.
        return sosAbstractFois;
    }

    protected FeatureOfInterest getFeatureOfInterest(final String identifier, final Geometry geometry,
            final Session session) throws OwsExceptionReport {
        if (!identifier.startsWith(SosConstants.GENERATED_IDENTIFIER_PREFIX)) {
            return (FeatureOfInterest) session.createCriteria(FeatureOfInterest.class)
                    .add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, identifier)).uniqueResult();
        } else {
            return (FeatureOfInterest) session
                    .createCriteria(FeatureOfInterest.class)
                    .add(SpatialRestrictions.eq(FeatureOfInterest.GEOMETRY, getGeometryHandler()
                            .switchCoordinateAxisFromToDatasourceIfNeeded(geometry))).uniqueResult();
        }
    }

    protected AbstractFeature createSosAbstractFeature(final FeatureOfInterest feature,
            final FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        return createSosAbstractFeature(feature, queryObject, session);
    }

    /**
     * Creates a SOS feature from the FeatureOfInterest object
     *
     * @param feature FeatureOfInterest object
     * @param queryObject
     * @param session
     * @return SOS feature
     * @throws OwsExceptionReport
     */
    protected AbstractFeature createSosAbstractFeature(final FeatureOfInterest feature,
            final FeatureQueryHandlerQueryObject queryObject, Session session) throws OwsExceptionReport {
        if (feature == null) {
            return null;
        }
        FeatureOfInterestDAO featureOfInterestDAO = daoFactory.getFeatureOfInterestDAO();
        final CodeWithAuthority identifier = featureOfInterestDAO.getIdentifier(feature);
        if (!SosHelper.checkFeatureOfInterestIdentifierForSosV2(feature.getIdentifier(), queryObject.getVersion())) {
            identifier.setValue(null);
        }
        final SamplingFeature sampFeat = new SamplingFeature(identifier);
        addNameAndDescription(queryObject, feature, sampFeat, featureOfInterestDAO);
        sampFeat.setGeometry(getGeomtery(feature, session));
        sampFeat.setFeatureType(feature.getFeatureOfInterestType().getFeatureOfInterestType());
        sampFeat.setUrl(feature.getUrl());
        if (feature.isSetDescriptionXml()) {
            sampFeat.setXml(feature.getDescriptionXml());
        }
        final Set<FeatureOfInterest> parentFeatures = feature.getParents();
        if (parentFeatures != null && !parentFeatures.isEmpty()) {
            final List<AbstractFeature> sampledFeatures = new ArrayList<>(parentFeatures.size());
            for (final FeatureOfInterest parentFeature : parentFeatures) {
                sampledFeatures.add(createSosAbstractFeature(parentFeature, queryObject, session));
            }
            sampFeat.setSampledFeatures(sampledFeatures);
        }
        return sampFeat;
    }

    private void addNameAndDescription(FeatureQueryHandlerQueryObject query,
            FeatureOfInterest feature,
            SamplingFeature samplingFeature,
            FeatureOfInterestDAO featureDAO)
            throws OwsExceptionReport {
        I18NDAO<I18NFeatureMetadata> i18nDAO = this.i18NDAORepository.getDAO(I18NFeatureMetadata.class);
        Locale requestedLocale = query.getI18N();
        // set name as human readable identifier if set
        if (feature.isSetName()) {
            samplingFeature.setHumanReadableIdentifier(feature.getName());
        }
        if (i18nDAO == null) {
            // no i18n support
            samplingFeature.addName(featureDAO.getName(feature));
            samplingFeature.setDescription(featureDAO.getDescription(feature));
        } else {
            I18NFeatureMetadata i18n = i18nDAO.getMetadata(feature.getIdentifier());
            if (requestedLocale != null) {
                // specific locale was requested
                Optional<LocalizedString> name = i18n.getName().getLocalizationOrDefault(requestedLocale,
                        this.defaultLocale);
                if (name.isPresent()) {
                    samplingFeature.addName(new CodeType(name.get()));
                }
                Optional<LocalizedString> description
                        = i18n.getDescription().getLocalizationOrDefault(requestedLocale, this.defaultLocale);
                if (description.isPresent()) {
                    samplingFeature.setDescription(description.get().getText());

                }
            } else {
                if (this.showAllLanguages) {
                    for (LocalizedString name : i18n.getName()) {
                        samplingFeature.addName(new CodeType(name));
                    }
                } else {
                    Optional<LocalizedString> name = i18n.getName().getLocalization(this.defaultLocale);
                    if (name.isPresent()) {
                        samplingFeature.addName(new CodeType(name.get()));
                    }
                }
                // choose always the description in the default locale
                Optional<LocalizedString> description = i18n.getDescription().getLocalization(this.defaultLocale);
                if (description.isPresent()) {
                    samplingFeature.setDescription(description.get().getText());
                }
            }
        }
    }

    protected FeatureOfInterest insertFeatureOfInterest(final SamplingFeature samplingFeature, final Session session)
            throws OwsExceptionReport {
        if (!getGeometryHandler().isSpatialDatasource()) {
            throw new NotYetSupportedException("Insertion of full encoded features for non spatial datasources");
        }
        FeatureOfInterestDAO featureOfInterestDAO = daoFactory.getFeatureOfInterestDAO();
        final String newId = samplingFeature.getIdentifierCodeWithAuthority().getValue();
        FeatureOfInterest feature = getFeatureOfInterest(newId, samplingFeature.getGeometry(), session);
        if (feature == null) {
            feature = new FeatureOfInterest();
            featureOfInterestDAO.addIdentifierNameDescription(samplingFeature, feature, session);
            processGeometryPreSave(samplingFeature, feature, session);
            if (samplingFeature.isSetXml()) {
                feature.setDescriptionXml(samplingFeature.getXml());
            }
            if (samplingFeature.isSetFeatureType()) {
                feature.setFeatureOfInterestType(new FeatureOfInterestTypeDAO().getOrInsertFeatureOfInterestType(
                        samplingFeature.getFeatureType(), session));
            }
            if (samplingFeature.isSetSampledFeatures()) {
                Set<FeatureOfInterest> parents
                        = Sets.newHashSetWithExpectedSize(samplingFeature.getSampledFeatures().size());
                for (AbstractFeature sampledFeature : samplingFeature.getSampledFeatures()) {
                    if (!OGCConstants.UNKNOWN.equals(sampledFeature.getIdentifierCodeWithAuthority().getValue())) {
                        if (sampledFeature instanceof SamplingFeature) {
                            parents.add(insertFeatureOfInterest((SamplingFeature) sampledFeature, session));
                        } else {
                            parents.add(insertFeatureOfInterest(new SamplingFeature(sampledFeature
                                    .getIdentifierCodeWithAuthority()), session));
                        }
                    }
                }
                feature.setParents(parents);
            }
            session.save(feature);
            session.flush();
            session.refresh(feature);
            featureOfInterestDAO.insertNameAndDescription(feature, samplingFeature, session);
//            return newId;
//        } else {
//            return feature.getIdentifier();
        }
        return feature;
    }

    protected void processGeometryPreSave(final SamplingFeature ssf, final FeatureOfInterest f, Session session)
            throws OwsExceptionReport {
        f.setGeom(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(ssf.getGeometry()));
    }

    /**
     * Get the geometry from featureOfInterest object.
     *
     * @param feature
     * @param session
     * @return geometry
     * @throws OwsExceptionReport
     */
    protected Geometry getGeomtery(final FeatureOfInterest feature, Session session) throws OwsExceptionReport {
        if (feature.isSetGeometry()) {
            return getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(feature.getGeom());
        } else if (feature.isSetLongLat()) {
            try {
                int epsg = getStorageEPSG();
                if (feature.isSetSrid()) {
                    epsg = feature.getSrid();
                }
                final String wktString
                        = getGeometryHandler().getWktString(feature.getLongitude(), feature.getLatitude(), epsg);
                Geometry geom = JTSHelper.createGeometryFromWKT(wktString, epsg);
                if (feature.isSetAltitude()) {
                    geom.getCoordinate().z = JavaHelper.asDouble(feature.getAltitude());
                    if (geom.getSRID() == getStorage3DEPSG()) {
                        geom.setSRID(getStorage3DEPSG());
                    }
                }
                return geom;
            } catch (ParseException de) {
                throw new NoApplicableCodeException().causedBy(de);
            }
            // return
            // getGeometryHandler().switchCoordinateAxisOrderIfNeeded(geom);
        } else if (session != null) {
            List<Geometry> geometries = daoFactory.getObservationDAO().getSamplingGeometries(feature.getIdentifier(),
                    session);
            int srid = getGeometryHandler().getStorageEPSG();
            if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(geometries)) {
                List<Coordinate> coordinates = Lists.newLinkedList();
                Geometry lastGeoemtry = null;
                for (Geometry geometry : geometries) {
                    if (geometry == null) {
                        continue;
                    }
                    if (lastGeoemtry == null || !geometry.equalsTopo(lastGeoemtry)) {
                        coordinates.add(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geometry)
                                .getCoordinate());
                        lastGeoemtry = geometry;
                        if (geometry.getSRID() != srid) {
                            srid = geometry.getSRID();
                        }
                    }
                    if (geometry.getSRID() != srid) {
                        srid = geometry.getSRID();
                    }
                    if (!geometry.equalsTopo(lastGeoemtry)) {
                        coordinates.add(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geometry)
                                .getCoordinate());
                        lastGeoemtry = geometry;
                    }
                }
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);
                if (coordinates.size() == 1) {
                    return geometryFactory.createPoint(coordinates.iterator().next());
                } else {
                    return geometryFactory.createLineString(coordinates.toArray(new Coordinate[coordinates.size()]));
                }
            }
        }
        return null;
    }

    protected Map<String, AbstractFeature> getFeaturesForNonSpatialDatasource(
            FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        final Map<String, AbstractFeature> featureMap = new HashMap<>(0);
        List<Geometry> envelopes = null;
        boolean hasSpatialFilter = false;
        if (queryObject.isSetSpatialFilters()) {
            hasSpatialFilter = true;
            envelopes = new ArrayList<>(queryObject.getSpatialFilters().size());
            for (final SpatialFilter filter : queryObject.getSpatialFilters()) {
                envelopes.add(getGeometryHandler().getFilterForNonSpatialDatasource(filter));
            }
        }
        final List<FeatureOfInterest> featuresOfInterest
                = daoFactory.getFeatureOfInterestDAO().getFeatureOfInterestObject(queryObject.getFeatureIdentifiers(),
                        session);
        for (final FeatureOfInterest feature : featuresOfInterest) {
            final SamplingFeature sosAbstractFeature
                    = (SamplingFeature) createSosAbstractFeature(feature, queryObject);
            if (!hasSpatialFilter) {
                featureMap.put(sosAbstractFeature.getIdentifierCodeWithAuthority().getValue(), sosAbstractFeature);
            } else if (getGeometryHandler().featureIsInFilter(sosAbstractFeature.getGeometry(), envelopes)) {
                featureMap.put(sosAbstractFeature.getIdentifierCodeWithAuthority().getValue(), sosAbstractFeature);
            }
        }
        return featureMap;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, AbstractFeature> getFeaturesForSpatialDatasource(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        final Criteria c
                = session.createCriteria(FeatureOfInterest.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        boolean filtered = false;
        if (queryObject.isSetFeatureIdentifiers()) {
            c.add(QueryHelper.getCriterionForFoiIds(FeatureOfInterest.IDENTIFIER, queryObject.getFeatureIdentifiers()));
            filtered = true;
        }
        if (queryObject.isSetSpatialFilters()) {
            final Disjunction disjunction = Restrictions.disjunction();
            for (final SpatialFilter filter : queryObject.getSpatialFilters()) {
                disjunction.add(SpatialRestrictions.filter(FeatureOfInterest.GEOMETRY, filter.getOperator(),
                        getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry())));
            }
            c.add(disjunction);
            filtered = true;
        }
        if (filtered) {
            return createSosFeatures(c.list(), queryObject, session);
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    @Deprecated
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }
}
