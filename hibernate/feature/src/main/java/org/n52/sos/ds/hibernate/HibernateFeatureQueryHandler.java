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
package org.n52.sos.ds.hibernate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.locationtech.jts.geom.Geometry;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.I18NSettings;
import org.n52.janmayen.i18n.LocaleHelper;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.FeatureEntity;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.util.IdGenerator;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ds.hibernate.create.HibernateFeatureVisitor;
import org.n52.sos.ds.hibernate.create.HibernateFeatureVisitorContext;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.service.SosSettings;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;

import com.google.common.base.Strings;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class HibernateFeatureQueryHandler
        implements FeatureQueryHandler, HibernateSqlQueryConstants {

    private Locale defaultLocale;

    private boolean showAllLanguages;

    private I18NDAORepository i18NDAORepository;

    private GeometryHandler geometryHandler;

    private DaoFactory daoFactory;

    private boolean updateFeatureGeometry;

    private boolean createFeatureGeometryFromSamplingGeometries;

    private ContentCacheController contentCacheController;

    private ProfileHandler profileHandler;

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

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @Setting(I18NSettings.I18N_DEFAULT_LANGUAGE)
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = LocaleHelper.decode(defaultLocale);
    }

    @Setting(I18NSettings.I18N_SHOW_ALL_LANGUAGE_VALUES)
    public void setShowAllLanguages(boolean showAllLanguages) {
        this.showAllLanguages = showAllLanguages;
    }

    @Override
    public AbstractFeature getFeatureByID(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        AbstractFeatureEntity<?> feature = null;
        if (queryObject.isSetFeatureObject() && queryObject.getFeatureObject() instanceof AbstractFeatureEntity) {
            feature = (AbstractFeatureEntity<?>) Hibernate.unproxy(queryObject.getFeatureObject());
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

    @SuppressWarnings("rawtypes")
    @Override
    public Collection<String> getFeatureIDs(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        try {
            HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<String> query = cb.createQuery(String.class);
            Root<AbstractFeatureEntity> root = query.from(AbstractFeatureEntity.class);
            query.select(root.get(AbstractFeatureEntity.IDENTIFIER));
            if (queryObject.isSetSpatialFilters()) {
                SpatialFilter filter = queryObject.getSpatialFitler();
                // NOTE: the identifier restriction below is guarded by isSetSpatialFilters(), not
                // isSetFeatures() -- pre-existing since before the Hibernate 6 migration, preserved
                // as-is rather than silently corrected. It means a spatial-filter-only request is
                // additionally narrowed by (a possibly empty) feature list.
                query.where(SpatialRestrictions.filter(cb, root.<Geometry>get(FeatureEntity.GEOMETRY),
                        filter.getOperator(),
                        getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry())),
                        root.get(FeatureEntity.IDENTIFIER).in(queryObject.getFeatures()));
            }
            return session.createQuery(query).list();
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("An error occurred while querying feature identifiers for spatial filter!");
        }
    }

    @Override
    public Map<String, AbstractFeature> getFeatures(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport {
        try {
            return getFeaturesForSpatialDatasource(queryObject);
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
                HibernateCriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<Geometry> query = cb.createQuery(Geometry.class);
                Root<AbstractFeatureEntity> root = query.from(AbstractFeatureEntity.class);
                query.select(cb.cast(
                        cb.function("ST_Extent", Object.class, root.get(AbstractFeatureEntity.GEOMETRY)),
                        Geometry.class))
                        .where(QueryHelper.getPredicateForObjects(cb, root.get(AbstractFeatureEntity.IDENTIFIER),
                                queryObject.getFeatures()));
                Geometry geometry = session.createQuery(query).uniqueResult();
                if (geometry != null) {
                    int srid = geometry.getSRID() > 0 ? geometry.getSRID() : getStorageEPSG();
                    geometry.setSRID(srid);
                    geometry = getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geometry);
                    return new ReferencedEnvelope(geometry.getEnvelopeInternal(), srid);
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
        HibernateFeatureVisitorContext context = (HibernateFeatureVisitorContext) getDefaultContext()
                .setSession(session)
                .setRequestedLanguage(queryObject.getI18N());
        return new HibernateFeatureVisitor(context).visit(feature);
    }

    private HibernateFeatureVisitorContext getDefaultContext() {
        HibernateFeatureVisitorContext context = new HibernateFeatureVisitorContext()
                .setDaoFactory(daoFactory);
        context.setStorageEPSG(getStorageEPSG())
        .setStorage3DEPSG(getStorage3DEPSG())
        .setGeometryHandler(geometryHandler)
        .setShowAllLanguages(showAllLanguages)
        .setDefaultLanguage(defaultLocale)
        .setUpdateFeatureGeometry(updateFeatureGeometry)
        .setCreateFeatureGeometryFromSamplingGeometries(createFeatureGeometryFromSamplingGeometries)
        .setI18NDAORepository(i18NDAORepository)
        .setCache((SosContentCache) contentCacheController.getCache())
        .setActiveProfile(profileHandler.getActiveProfile());
        return context;
    }

    protected AbstractFeatureEntity insertFeatureOfInterest(AbstractSamplingFeature samplingFeature,
            Session session) throws OwsExceptionReport {
        return daoFactory.getFeatureDAO().insertFeature(samplingFeature, session);
    }

    protected Map<String, AbstractFeature> getFeaturesForSpatialDatasource(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
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
