/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.AbstractFeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.feature.HibernateFeatureConverter;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.exception.ows.concrete.NotYetSupportedException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;


@Configurable
public class HibernateFeatureQueryHandler implements FeatureQueryHandler, HibernateSqlQueryConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateFeatureQueryHandler.class);

    @Deprecated
    @Override
    public AbstractFeature getFeatureByID(String featureID, Object connection, String version)
            throws OwsExceptionReport {
        FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject();
        queryObject.setConnection(connection);
        queryObject.addFeatureIdentifier(featureID);
        queryObject.setVersion(version);
        return getFeatureByID(queryObject);
    }

    @Override
    public AbstractFeature getFeatureByID(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        try {
            FeatureOfInterest feature = getFeatureDAO().getFeature(queryObject.getFeatureIdentifier(), session);
            return createSosAbstractFeature(feature, queryObject);
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "An error occurred while querying feature data for a featureOfInterest identifier!");
        }

    }

    @Deprecated
    @Override
    public Collection<String> getFeatureIDs(final SpatialFilter filter, final Object connection)
            throws OwsExceptionReport {
        return getFeatureIDs(new FeatureQueryHandlerQueryObject().setConnection(connection).addSpatialFilter(filter));
//        final Session session = HibernateSessionHolder.getSession(connection);
//        try {
//            if (GeometryHandler.getInstance().isSpatialDatasource()) {
//                final Criteria c =
//                        session.createCriteria(FeatureOfInterest.class).setProjection(
//                                Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
//                if (filter != null) {
//                    c.add(SpatialRestrictions.filter(FeatureOfInterest.GEOMETRY, filter.getOperator(),
//                            getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry())));
//                }
//                return c.list();
//            } else {
//
//                final List<String> identifiers = new LinkedList<String>();
//                final List<FeatureOfInterest> features = session.createCriteria(FeatureOfInterest.class).list();
//                if (filter != null) {
//                    final Geometry envelope = GeometryHandler.getInstance().getFilterForNonSpatialDatasource(filter);
//                    for (final FeatureOfInterest feature : features) {
//                        final Geometry geom = getGeomtery(feature, session);
//                        if (geom != null && envelope.contains(geom)) {
//                            identifiers.add(feature.getIdentifier());
//                        }
//                    }
//                }
//                return identifiers;
//            }
//        } catch (final HibernateException he) {
//            throw new NoApplicableCodeException().causedBy(he).withMessage(
//                    "An error occurred while querying feature identifiers for spatial filter!");
//        }
    }

    @Override
    public Collection<String> getFeatureIDs(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        try {
            if (getGeometryHandler().isSpatialDatasource()) {
                queryObject.getSpatialFitler().setGeometry(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(queryObject.getSpatialFitler().getGeometry()));
                return getFeatureDAO().getFeatureIdentifiers(queryObject.getSpatialFitler(), session);
            } else {
                final List<String> identifiers = new LinkedList<String>();
                final List<FeatureOfInterest> features = getFeatureDAO().getFeatures(session);
                if (queryObject.getSpatialFitler() != null) {
                    final Geometry envelope = GeometryHandler.getInstance().getFilterForNonSpatialDatasource(queryObject.getSpatialFitler());
                    for (final FeatureOfInterest feature : features) {
                        final Optional<Geometry> geom = new HibernateFeatureConverter(feature, queryObject.getI18N(), queryObject.getVersion(), getStorageEPSG(), getStorage3DEPSG(), session).createGeometry();
                        if (geom != null && geom.isPresent() && envelope.contains(geom.get())) {
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

    @Deprecated
    @Override
    public Map<String, AbstractFeature> getFeatures(Collection<String> featureIDs, List<SpatialFilter> spatialFilters,
            Object connection, String version) throws OwsExceptionReport {
        FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject();
        queryObject.setFeatureIdentifiers(featureIDs);
        queryObject.setSpatialFilters(spatialFilters);
        queryObject.setConnection(connection);
        queryObject.setVersion(version);
        return getFeatures(queryObject);
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

    @Deprecated
    @Override
    public SosEnvelope getEnvelopeForFeatureIDs(Collection<String> featureIDs, Object connection)
            throws OwsExceptionReport {
        return getEnvelopeForFeatureIDs(new FeatureQueryHandlerQueryObject().setFeatureIdentifiers(featureIDs)
                .setConnection(connection));
    }

    @Override
    public SosEnvelope getEnvelopeForFeatureIDs(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
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
                    Geometry geom = getFeatureDAO().getFeatureExtent(queryObject.getFeatureIdentifiers(), session);
                    
                    if (geom != null) {
                        int srid = geom.getSRID() > 0 ? geom.getSRID() : getStorageEPSG();
                        geom.setSRID(srid);
                        geom = getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(geom);
                        return new SosEnvelope(geom.getEnvelopeInternal(), srid);
                    }
                } else {
                    final Envelope envelope = new Envelope();
                    final List<FeatureOfInterest> featuresOfInterest =
                            getFeatureDAO().getFeatureOfInterestObjects(queryObject.getFeatureIdentifiers(),
                                    session);
                    for (final FeatureOfInterest feature : featuresOfInterest) {
                        try {
                            // TODO Check if prepareGeometryForResponse required
                            // transform/switch
                            // final Geometry geom =
                            // getGeometryHandler().prepareGeometryForResponse(getGeomtery(feature),
                            // queryObject.getRequestedSrid());
                            final Optional<Geometry> geom = new HibernateFeatureConverter(feature, queryObject.getI18N(), queryObject.getVersion(), getStorageEPSG(), getStorage3DEPSG(), session).createGeometry();
                            if (geom != null && geom.isPresent()) {
                                envelope.expandToInclude(geom.get().getEnvelopeInternal());
                            }
                        } catch (final OwsExceptionReport owse) {
                            LOGGER.warn(
                                    String.format("Error while adding '%s' to envelope!",
                                            feature.getFeatureOfInterestId()), owse);
                        }
                    }
                    if (!envelope.isNull()) {
                        return new SosEnvelope(envelope, getDefaultEPSG());
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
        if (StringHelper.isNotEmpty(samplingFeature.getUrl())) {
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
                        SosConstants.GENERATED_IDENTIFIER_PREFIX
                                + JavaHelper.generateID(samplingFeature.getXmlDescription());
                samplingFeature.setIdentifier(new CodeWithAuthority(featureIdentifier));
            }
            return insertFeatureOfInterest(samplingFeature, session).getIdentifier();
        }
    }

    @Deprecated
    @Override
    public int getDefaultEPSG() {
        return getStorageEPSG();
    }

    @Deprecated
    @Override
    public int getDefault3DEPSG() {
        return getStorage3DEPSG();
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

    /**
     * Creates a map with FOI identifier and SOS feature
     * <p/>
     *
     * @param features
     *            FeatureOfInterest objects
     * @param queryObject
     *            SOS version
     *            <p/>
     * @return Map with FOI identifier and SOS feature
     *         <p/>
     * @throws OwsExceptionReport
     *             * If feature type is not supported
     */
    protected Map<String, AbstractFeature> createSosFeatures(final List<FeatureOfInterest> features,
            final FeatureQueryHandlerQueryObject queryObject, Session session) throws OwsExceptionReport {
        final Map<String, AbstractFeature> sosAbstractFois = new HashMap<String, AbstractFeature>();
        for (final FeatureOfInterest feature : features) {
            final AbstractFeature sosFeature = createSosAbstractFeature(feature, queryObject, session);
            sosAbstractFois.put(feature.getIdentifier(), sosFeature);
        }
        // TODO if sampledFeatures are also in sosAbstractFois, reference them.
        return sosAbstractFois;
    }

//    protected FeatureOfInterest getFeatureOfInterest(final String identifier, final Geometry geometry,
//            final Session session) throws OwsExceptionReport {
//        if (!identifier.startsWith(SosConstants.GENERATED_IDENTIFIER_PREFIX)) {
//            return (FeatureOfInterest) session.createCriteria(FeatureOfInterest.class)
//                    .add(Restrictions.eq(FeatureOfInterest.IDENTIFIER, identifier)).uniqueResult();
//        } else {
//            return (FeatureOfInterest) session
//                    .createCriteria(FeatureOfInterest.class)
//                    .add(SpatialRestrictions.eq(FeatureOfInterest.GEOMETRY, GeometryHandler.getInstance()
//                            .switchCoordinateAxisFromToDatasourceIfNeeded(geometry))).uniqueResult();
//        }
//    }

    protected AbstractFeature createSosAbstractFeature(final FeatureOfInterest feature,
            final FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        return createSosAbstractFeature(feature, queryObject, session);
    }

    /**
     * Creates a SOS feature from the FeatureOfInterest object
     *
     * @param feature
     *            FeatureOfInterest object
     * @param version
     *            SOS version
     * @return SOS feature
     * @throws OwsExceptionReport
     */
    protected AbstractFeature createSosAbstractFeature(final FeatureOfInterest feature,
            final FeatureQueryHandlerQueryObject queryObject, Session session) throws OwsExceptionReport {
        if (feature == null) {
            return null;
        }
        Optional<AbstractFeature> abstractFeature = new HibernateFeatureConverter(feature, queryObject.getI18N(), queryObject.getVersion(), getStorageEPSG(), getStorage3DEPSG(), session).create();
        if (abstractFeature.isPresent()) {
           return abstractFeature.get();
        }
        return null;
    }

    protected FeatureOfInterest insertFeatureOfInterest(final SamplingFeature samplingFeature, final Session session)
            throws OwsExceptionReport {
        if (!getGeometryHandler().isSpatialDatasource()) {
            throw new NotYetSupportedException("Insertion of full encoded features for non spatial datasources");
        }
        checkForSwitchCoordinateAxis(samplingFeature);
        return getFeatureDAO().insertFeature(samplingFeature, session);
    }

    private void checkForSwitchCoordinateAxis(SamplingFeature samplingFeature) throws InvalidSridException, OwsExceptionReport {
        samplingFeature.setGeometry(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(samplingFeature.getGeometry()));
        for (AbstractFeature sampledFeature : samplingFeature.getSampledFeatures()) {
            if (sampledFeature instanceof SamplingFeature) {
                checkForSwitchCoordinateAxis((SamplingFeature)sampledFeature);
            }
        }
    }
    
    protected Map<String, AbstractFeature> getFeaturesForNonSpatialDatasource(
            FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        final Map<String, AbstractFeature> featureMap = new HashMap<>(0);
        List<Geometry> envelopes = null;
        boolean hasSpatialFilter = false;
        if (queryObject.isSetSpatialFilters()) {
            hasSpatialFilter = true;
            envelopes = new ArrayList<Geometry>(queryObject.getSpatialFilters().size());
            for (final SpatialFilter filter : queryObject.getSpatialFilters()) {
                envelopes.add(getGeometryHandler().getFilterForNonSpatialDatasource(filter));
            }
        }
        final List<FeatureOfInterest> featuresOfInterest =
                new FeatureOfInterestDAO().getFeatureOfInterestObjects(queryObject.getFeatureIdentifiers(), session);
        for (final FeatureOfInterest feature : featuresOfInterest) {
            final SamplingFeature sosAbstractFeature =
                    (SamplingFeature) createSosAbstractFeature(feature, queryObject);
            if (!hasSpatialFilter) {
                featureMap.put(sosAbstractFeature.getIdentifierCodeWithAuthority().getValue(), sosAbstractFeature);
            } else {
                if (getGeometryHandler().featureIsInFilter(sosAbstractFeature.getGeometry(), envelopes)) {
                    featureMap.put(sosAbstractFeature.getIdentifierCodeWithAuthority().getValue(), sosAbstractFeature);
                }
            }
        }
        return featureMap;
    }

    protected Map<String, AbstractFeature> getFeaturesForSpatialDatasource(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport {
        final Session session = HibernateSessionHolder.getSession(queryObject.getConnection());
        if (queryObject.isSetSpatialFilters()) {
            for (final SpatialFilter filter : queryObject.getSpatialFilters()) {
                filter.setGeometry(getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(filter.getGeometry()));
            }
        }
        List<FeatureOfInterest> features = getFeatureDAO().getFeatures(queryObject.getFeatureIdentifiers(), queryObject.getSpatialFilters(), session);
        if (features != null) {
            return createSosFeatures(features, queryObject, session);
        } else {
            return Collections.emptyMap();
        }
    }
    
    protected GeometryHandler getGeometryHandler() {
        return GeometryHandler.getInstance();
    }
    
    protected AbstractFeatureOfInterestDAO getFeatureDAO() throws CodedException {
        return DaoFactory.getInstance().getFeatureDAO();
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }
}
