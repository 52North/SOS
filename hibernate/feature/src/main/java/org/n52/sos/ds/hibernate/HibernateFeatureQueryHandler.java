/*
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
package org.n52.sos.ds.hibernate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.n52.faroe.ConfigurationError;
import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.exception.ows.concrete.NotYetSupportedException;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.iceland.service.ServiceSettings;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.util.IdGenerator;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ds.hibernate.create.FeatureVisitorContext;
import org.n52.sos.ds.hibernate.create.HibernateFeatureVisitor;
import org.n52.sos.ds.hibernate.create.HibernateGeometryVisitor;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.service.SosSettings;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

@Configurable
public class HibernateFeatureQueryHandler
        implements FeatureQueryHandler, HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateFeatureQueryHandler.class);

    private Locale defaultLocale;

    private boolean showAllLanguages;

    private I18NDAORepository i18NDAORepository;

    private GeometryHandler geometryHandler;

    private DaoFactory daoFactory;

    private boolean updateFeatureGeometry;

    private boolean createFeatureGeometryFromSamplingGeometries;

    private ContentCacheController contentCacheController;

    private String serviceURL;

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setI18NDAORepository(I18NDAORepository i18NDAORepository) {
        this.i18NDAORepository = i18NDAORepository;
    }

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    @Inject
    public void setContentCacheController(ContentCacheController ctrl) {
        this.contentCacheController = ctrl;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = LocaleHelper.decode(defaultLocale);
    }

    @Setting(I18NSettings.I18N_SHOW_ALL_LANGUAGE_VALUES)
    public void setShowAllLanguages(boolean showAllLanguages) {
        this.showAllLanguages = showAllLanguages;
    }

    @Setting(ServiceSettings.SERVICE_URL)
    public void setServiceURL(final URI serviceURL) throws ConfigurationError {
        Validation.notNull("Service URL", serviceURL);
        String url = serviceURL.toString();
        if (url.contains("?")) {
            url = url.split("[?]")[0];
        }
        this.serviceURL = url;
    }

    @Override
    public AbstractFeature getFeatureByID(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        AbstractFeatureEntity<?> feature = null;
        if (queryObject.isSetFeatureObject() && queryObject.getFeatureObject() instanceof AbstractFeatureEntity) {
            feature = (FeatureEntity) queryObject.getFeatureObject();
        } else {
            final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
            try {
                feature = daoFactory.getFeatureDAO().getFeature(queryObject.getFeatureIdentifier(), session);
            } catch (final HibernateException he) {
                throw new NoApplicableCodeException().causedBy(he).withMessage(
                        "An error occurred while querying feature data for a featureOfInterest identifier!");
            }
        }
        return createSosAbstractFeature(feature, queryObject);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<String> getFeatureIDs(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        try {
            if (getGeometryHandler().isSpatialDatasource()) {
                final Criteria c
                        = session.createCriteria(AbstractFeatureEntity.class).setProjection(
                        Projections.distinct(Projections.property(AbstractFeatureEntity.IDENTIFIER)));
                if (queryObject.isSetSpatialFilters()) {
                    SpatialFilter filter = queryObject.getSpatialFitler();
                    c.add(SpatialRestrictions.filter(FeatureEntity.GEOMETRY, filter.getOperator(),
                            getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry())));
                }
                if (queryObject.isSetSpatialFilters()) {
                    c.add(Restrictions.in(FeatureEntity.IDENTIFIER, queryObject.getFeatures()));
                }
                return c.list();
            } else {
                Criteria c = session.createCriteria(FeatureEntity.class);
                if (queryObject.isSetFeatures()) {
                    c.add(Restrictions.in(FeatureEntity.IDENTIFIER, queryObject.getFeatures()));
                }
                List<String> identifiers = new LinkedList<>();
                if (queryObject.isSetSpatialFilters()) {
                    SpatialFilter filter = queryObject.getSpatialFitler();
                    final List<AbstractFeatureEntity> features = daoFactory.getFeatureDAO().getFeatures(session);
                    final Geometry envelope = getGeometryHandler().getFilterForNonSpatialDatasource(filter);
                    FeatureVisitorContext context = getDefaultContext()
                            .setSession(session)
                            .setRequestedLanguage(queryObject.getI18N());
                    for (final AbstractFeatureEntity feature : features) {
                        final Geometry geom = new HibernateGeometryVisitor(context).visit(feature);
                        if (geom != null && !geom.isEmpty() && envelope.contains(geom)) {
                            identifiers.add(feature.getIdentifier());
                        }
                    }
                }
                return identifiers;
            }
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("An error occurred while querying feature identifiers for spatial filter!");
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
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying features from data source!");
        }
    }

    @Override
    public ReferencedEnvelope getEnvelopeForFeatureIDs(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        if (queryObject.isSetFeatures()) {
            try {
                // XXX workaround for Hibernate Spatial's lack of support for
                // GeoDB's extent aggregate
                // see
                // http://www.hibernatespatial.org/pipermail/hibernatespatial-users/2013-August/000876.html
                Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
                if (getGeometryHandler().isSpatialDatasource()
                        && HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                    // Criteria featureExtentCriteria =
                    // session.createCriteria(FeatureEntity.class)
                    // .add(Restrictions.in(FeatureEntity.IDENTIFIER,
                    // featureIDs))
                    // .setProjection(SpatialProjections.extent(FeatureEntity.GEOMETRY));
                    // LOGGER.trace("QUERY getEnvelopeForFeatureIDs(featureIDs):
                    // {}",
                    // HibernateHelper.getSqlString(featureExtentCriteria));
                    // Geometry geom = (Geometry)
                    // featureExtentCriteria.uniqueResult();
                    Geometry geometry
                            = (Geometry) session
                            .createCriteria(AbstractFeatureEntity.class)
                            .add(QueryHelper.getCriterionForObjects(AbstractFeatureEntity.IDENTIFIER,
                                    queryObject.getFeatures()))
                            .setProjection(SpatialProjections.extent(AbstractFeatureEntity.GEOMETRY))
                            .uniqueResult();
                    if (geometry != null) {
                        int srid = geometry.getSRID() > 0 ? geometry.getSRID() : getStorageEPSG();
                        geometry.setSRID(srid);
                        geometry = getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geometry);
                        return new ReferencedEnvelope(geometry.getEnvelopeInternal(), srid);
                    }
                } else {
                    final Envelope envelope = new Envelope();
                    final List<AbstractFeatureEntity> featuresOfInterest =
                            daoFactory.getFeatureDAO().getFeatureOfInterestObjects(queryObject.getFeatures(), session);
                    for (final AbstractFeatureEntity feature : featuresOfInterest) {
                        try {
                            // TODO Check if prepareGeometryForResponse required
                            // transform/switch
                            // final Geometry geom =
                            // getGeometryHandler().prepareGeometryForResponse(getGeomtery(feature),
                            // queryObject.getRequestedSrid());
                            FeatureVisitorContext context = getDefaultContext()
                                    .setSession(session)
                                    .setRequestedLanguage(queryObject.getI18N());
                            final Geometry geom = new HibernateGeometryVisitor(context).visit(feature);
                            if (geom != null && !geom.isEmpty()) {
                                envelope.expandToInclude(geom.getEnvelopeInternal());
                            }
                        } catch (final OwsExceptionReport owse) {
                            LOGGER.warn(String.format("Error while adding '%s' to envelope!",
                                    feature.getId()), owse);
                        }
                    }
                    if (!envelope.isNull()) {
                        return new ReferencedEnvelope(envelope, getGeometryHandler().getStorageEPSG());
                    }
                }
            } catch (final HibernateException he) {
                throw new NoApplicableCodeException().causedBy(he)
                        .withMessage("Exception thrown while requesting global feature envelope");
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.n52.sos.ds.FeatureQueryHandler#insertFeature(org.n52.sos.ogc.om.
     * features .samplingFeatures.SamplingFeature, java.lang.Object)
     *
     * FIXME check semantics of this method in respect to its name and the
     * documentation in the super class
     */
    @Override
    public String insertFeature(final AbstractSamplingFeature samplingFeature, final Object connection)
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
                featureIdentifier =
                        SosConstants.GENERATED_IDENTIFIER_PREFIX + IdGenerator.generate(samplingFeature.getXml());
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

    private boolean isFeatureReferenced(final SamplingFeature samplingFeature) {
        return !Strings.isNullOrEmpty(samplingFeature.getUrl());
    }

    /**
     * Creates a map with FOI identifier and SOS feature
     *
     * @param features FeatureOfInterest objects
     * @param queryObject SOS version
     * @param session the session
     * @return Map with FOI identifier and SOS feature
     * @throws OwsExceptionReport * If feature type is not supported
     */
    protected Map<String, AbstractFeature> createSosFeatures(final List<AbstractFeatureEntity> features,
            final FeatureQueryHandlerQueryObject queryObject, Session session) throws OwsExceptionReport {
        final Map<String, AbstractFeature> sosAbstractFois = new HashMap<>(features.size());
        for (final AbstractFeatureEntity feature : features) {
            final AbstractFeature sosFeature = createSosAbstractFeature(feature, queryObject, session);
            sosAbstractFois.put(feature.getIdentifier(), sosFeature);
        }
        // TODO if sampledFeatures are also in sosAbstractFois, reference them.
        return sosAbstractFois;
    }

    protected FeatureEntity getFeatureOfInterest(final String identifier, final Geometry geometry,
            final Session session) throws OwsExceptionReport {
        if (!identifier.startsWith(SosConstants.GENERATED_IDENTIFIER_PREFIX)) {
            return (FeatureEntity) session.createCriteria(FeatureEntity.class)
                    .add(Restrictions.eq(FeatureEntity.IDENTIFIER, identifier)).uniqueResult();
        } else {
            return (FeatureEntity) session.createCriteria(FeatureEntity.class)
                    .add(SpatialRestrictions.eq(FeatureEntity.GEOMETRY,
                            getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geometry)))
                    .uniqueResult();
        }
    }

    protected AbstractFeature createSosAbstractFeature(final AbstractFeatureEntity feature,
            final FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        return createSosAbstractFeature(feature, queryObject, session);
    }

    /**
     * Creates a SOS feature from the FeatureOfInterest object
     *
     * @param feature FeatureOfInterest object
     * @param queryObject Query object
     * @param session the session
     * @return SOS feature An SOS feature
     * @throws OwsExceptionReport  If an error occurs
     */
    protected AbstractFeature createSosAbstractFeature(final AbstractFeatureEntity feature,
            final FeatureQueryHandlerQueryObject queryObject, Session session) throws OwsExceptionReport {
        if (feature == null) {
            return null;
        }
        FeatureVisitorContext context = getDefaultContext()
                .setSession(session)
                .setRequestedLanguage(queryObject.getI18N());
        return new HibernateFeatureVisitor(context).visit(feature);
    }

    private FeatureVisitorContext getDefaultContext() {
        return new FeatureVisitorContext()
        .setStorageEPSG(getStorageEPSG())
        .setStorage3DEPSG(getStorage3DEPSG())
        .setGeometryHandler(geometryHandler)
        .setDaoFactory(daoFactory)
        .setShowAllLanguages(showAllLanguages)
        .setDefaultLanguage(defaultLocale)
        .setUpdateFeatureGeometry(updateFeatureGeometry)
        .setCreateFeatureGeometryFromSamplingGeometries(createFeatureGeometryFromSamplingGeometries)
        .setI18NDAORepository(i18NDAORepository)
        .setCache((SosContentCache) contentCacheController.getCache())
        .setServiceURL(serviceURL);
    }

    protected AbstractFeatureEntity insertFeatureOfInterest(AbstractSamplingFeature samplingFeature,
            Session session) throws OwsExceptionReport {
        if (!getGeometryHandler().isSpatialDatasource()) {
            throw new NotYetSupportedException("Insertion of full encoded features for non spatial datasources");
        }
        return daoFactory.getFeatureDAO().insertFeature(samplingFeature, session);
    }

    protected Map<String, AbstractFeature> getFeaturesForNonSpatialDatasource(
            FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        final Map<String, AbstractFeature> featureMap = new HashMap<>(0);
        List<org.locationtech.jts.geom.Geometry> envelopes = null;
        boolean hasSpatialFilter = false;
        if (queryObject.isSetSpatialFilters()) {
            hasSpatialFilter = true;
            envelopes = new ArrayList<>(queryObject.getSpatialFilters().size());
            for (final SpatialFilter filter : queryObject.getSpatialFilters()) {
                envelopes.add(getGeometryHandler().getFilterForNonSpatialDatasource(filter));
            }
        }
        final List<AbstractFeatureEntity> featuresOfInterest =
                daoFactory.getFeatureDAO().getFeatureOfInterestObjects(queryObject.getFeatures(), session);
        for (final AbstractFeatureEntity feature : featuresOfInterest) {
            final AbstractSamplingFeature sosAbstractFeature =
                    (AbstractSamplingFeature) createSosAbstractFeature(feature, queryObject, session);
            if (!hasSpatialFilter) {
                featureMap.put(sosAbstractFeature.getIdentifierCodeWithAuthority().getValue(), sosAbstractFeature);
            } else if (getGeometryHandler().featureIsInFilter(sosAbstractFeature.getGeometry(), envelopes)) {
                featureMap.put(sosAbstractFeature.getIdentifierCodeWithAuthority().getValue(), sosAbstractFeature);
            }
        }
        return featureMap;
    }

    protected Map<String, AbstractFeature> getFeaturesForSpatialDatasource(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        final Criteria c =
                session.createCriteria(FeatureEntity.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        boolean filtered = false;
        if (queryObject.isSetFeatures()) {
            c.add(QueryHelper.getCriterionForObjects(FeatureEntity.IDENTIFIER, queryObject.getFeatures()));
            filtered = true;
        }
        if (queryObject.isSetSpatialFilters()) {
            for (final SpatialFilter filter : queryObject.getSpatialFilters()) {
                filter.setGeometry(getGeometryHandler()
                        .switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry().toGeometry()));
            }
        }
        List<AbstractFeatureEntity> features = daoFactory.getFeatureDAO().getFeatures(queryObject.getFeatures(),
                queryObject.getSpatialFilters(), session);
        if (features != null) {
            return createSosFeatures(features, queryObject, session);
        } else {
            return Collections.emptyMap();
        }
    }

    protected GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    @Setting(SosSettings.CREATE_FOI_GEOM_FROM_SAMPLING_GEOMS)
    public void setCreateFeatureGeometryFromSamplingGeometries(boolean createFeatureGeometryFromSamplingGeometries) {
        this.createFeatureGeometryFromSamplingGeometries = createFeatureGeometryFromSamplingGeometries;
    }

    @Setting(SosSettings.UPDATE_FEATURE_GEOMETRY)
    public void setUpdateFeatureGeometry(boolean updateFeatureGeometry) {
        this.updateFeatureGeometry = updateFeatureGeometry;
    }

}
