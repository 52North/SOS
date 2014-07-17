/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.spatial.criterion.SpatialProjections;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.AbstractSpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract Hibernate data access class for Spatial Filtering Profile.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 * @param <T>
 *            Concrete SpatialFilteringProfile entity
 */
@Configurable
@Deprecated
public abstract class AbstractSpatialFilteringProfileDAO<T extends AbstractSpatialFilteringProfile> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSpatialFilteringProfileDAO.class);

    /**
     * Get observation ids for SpatialFilteringProfile spatial filter
     * 
     * @param spatialFilter
     *            Spatial filter
     * @param session
     *            Hibernate session
     * @return Observation ids which are valid for spatial filter
     * @throws OwsExceptionReport
     *             If SpatialFilteringProfile is not supported
     */
    public abstract Set<Long> getObservationIdsForSpatialFilter(SpatialFilter spatialFilter, Session session)
            throws OwsExceptionReport;

    /**
     * Insert SpatialFilteringProfile definition into datasource
     * 
     * @param namedValue
     *            SpatialFilteringProfile definition
     * @param observation
     *            Observation entity
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If SpatialFilteringProfile is not supported
     */
    public abstract void insertSpatialfilteringProfile(NamedValue<Geometry> namedValue,
            AbstractObservation observation, Session session) throws OwsExceptionReport;

    /**
     * Get map with observation id key and related SpatialFilteringProfile
     * 
     * @param observationIds
     *            Observation ids to get SpatialFilteringProfile for
     * @param session
     *            Hibernate session
     * @return Map with observation id and related SpatialFilteringProfile
     */
    public abstract Map<Long, AbstractSpatialFilteringProfile> getSpatialFilertingProfiles(Set<Long> observationIds,
            Session session);

    /**
     * Get SpatialFilteringProfile for observation id.
     * 
     * @param observationId
     *            Related observation id
     * @param session
     *            Hibernate session
     * @return SpatialFilteringProfile for observation id
     * @throws CodedException
     *             If SpatialFilteringProfile is not supported
     */
    public abstract AbstractSpatialFilteringProfile getSpatialFilertingProfile(Long observationId, Session session)
            throws CodedException;

    /**
     * Get SpatialFilteringProfile.
     * 
     * @param observationId
     *            Related observation id
     * @param session
     *            Hibernate session
     */
    public abstract List<AbstractSpatialFilteringProfile> getSpatialFileringProfiles(Session session);

    /**
     * Get envelope for offering id
     * 
     * @param offeringID
     *            Offering id
     * @param session
     *            Hibernate session
     * @return SOS envelope
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public abstract SosEnvelope getEnvelopeForOfferingId(String offeringID, Session session) throws OwsExceptionReport;
    
    public abstract DetachedCriteria getDetachedCriteriaFor(SpatialFilter filter) throws OwsExceptionReport; 

    /**
     * Get concrete SpatialFilteringProfile entity object
     * 
     * @return SpatialFilteringProfile object
     */
    protected abstract T getSpatialFilteringProfileImpl();

    /**
     * Query observation ids which are OM_SpatialObservations and correspond to
     * the spatial filter
     * 
     * @param clazz
     *            Entity to create Criteria for
     * @param spatialFilter
     *            Spatial filter
     * @param session
     *            Hibernate session
     * @return Observation ids
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Set<Long> getObservationIdsForSpatialFilter(Class clazz, SpatialFilter spatialFilter, Session session)
            throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(clazz, session)) {
            // TODO check if other parameter (Offering, Procedure, ...) should
            // be set in query to reduce the number of returned observation ids.
            final Criteria criteria =
                    getObservationCriteria(clazz, getDetachedCriteria(clazz, spatialFilter), session);
            LOGGER.debug("QUERY getObservationIdsForSpatialFilter(spatialFilter): {}",
                    HibernateHelper.getSqlString(criteria));
            return Sets.newHashSet(criteria.list());
        }
        return Sets.newHashSet();
    }

    /**
     * Insert a new SpatialFilteringProfile into the database
     * 
     * @param clazz
     *            Entity to create Criteria for
     * @param namedValue
     *            Geometry to insert
     * @param observation
     *            Corresponding observation
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If Spatial Filtering Profile is not supported or an error
     *             occurs
     */
    @SuppressWarnings("rawtypes")
    protected void insertSpatialfilteringProfile(Class clazz, NamedValue<Geometry> namedValue,
            AbstractObservation observation, Session session) throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(clazz, session)) {
            AbstractSpatialFilteringProfile spatialFilteringProfile = getSpatialFilteringProfileImpl();
            spatialFilteringProfile.setObservation(observation);
            spatialFilteringProfile.setDefinition(namedValue.getName().getHref());
            if (namedValue.getName().isSetTitle()) {
                spatialFilteringProfile.setTitle(namedValue.getName().getTitle());
            }

            spatialFilteringProfile.setGeom(GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(
                    namedValue.getValue().getValue()));
            session.saveOrUpdate(spatialFilteringProfile);
            //dont flush here because we may be batching
        } else {
            throw new OptionNotSupportedException().at(Sos2Constants.InsertObservationParams.parameter).withMessage(
                    "The SOS 2.0 Spatial Filtering Profile is not supported by this service!");
        }
    }

    /**
     * Get SpatialFilteringProfiles for observation ids
     * 
     * @param clazz
     *            Entity to create Criteria for
     * @param observationIds
     *            Observation ids
     * @param session
     *            Hibernate session
     * @return Map with observation id as key and corresponding
     *         SpatialFilteringProfile as value
     */
    @SuppressWarnings("rawtypes")
    protected Map<Long, AbstractSpatialFilteringProfile> getSpatialFilertingProfiles(Class clazz,
            Set<Long> observationIds, Session session) {
        Long count = getSpatialFilteringProfileCount(clazz, session);
        if (HibernateHelper.isEntitySupported(clazz, session) && CollectionHelper.isNotEmpty(observationIds)
                && hasSpatialFilteringProfileValues(clazz, count, session)) {
            Map<Long, AbstractSpatialFilteringProfile> spatialFilteringProfilesMap = Maps.newHashMap();
            if (count <= observationIds.size()) {
                List<AbstractSpatialFilteringProfile> spatialFileringProfiles = getSpatialFileringProfiles(session);
                for (AbstractSpatialFilteringProfile spatialFilteringProfile : spatialFileringProfiles) {
                    if (observationIds.contains(spatialFilteringProfile.getObservation().getObservationId())) {
                        spatialFilteringProfilesMap.put(spatialFilteringProfile.getObservation().getObservationId(),
                                spatialFilteringProfile);
                    }
                }
            } else {
                List<AbstractSpatialFilteringProfile> queriedSpatiaFilteringProfiles =
                        querySpatialFilteringProfileCriteria(clazz,
                                HibernateHelper.getValidSizedLists(observationIds), session);
                if (CollectionHelper.isNotEmpty(queriedSpatiaFilteringProfiles)) {
                    for (AbstractSpatialFilteringProfile spatialFilteringProfile : queriedSpatiaFilteringProfiles) {
                        spatialFilteringProfilesMap.put(spatialFilteringProfile.getObservation().getObservationId(),
                                spatialFilteringProfile);
                    }
                }
            }
            return spatialFilteringProfilesMap;
        }
        return Maps.newHashMap();
    }

    /**
     * Query all SpatialFilteringProfiles
     * 
     * @param clazz
     *            Entity to create Criteria for
     * @param session
     *            Hibernate session
     * @return All SpatialFilteringProfiles
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<AbstractSpatialFilteringProfile> getSpatialFileringProfiles(Class clazz, Session session) {
        Criteria criteria = session.createCriteria(clazz);
        LOGGER.debug("QUERY getSpatialFileringProfiles(): {}", HibernateHelper.getSqlString(criteria));
        return (List<AbstractSpatialFilteringProfile>) criteria.list();
    }

    /**
     * Get SpatialFilteringProfile for observation id.
     * 
     * @param observationId
     *            Related observation id
     * @param session
     *            Hibernate session
     * @return SpatialFilteringProfile for observation id
     * @throws CodedException
     *             If SpatialFilteringProfile is not supported
     */
    @SuppressWarnings("rawtypes")
    protected AbstractSpatialFilteringProfile getSpatialFilertingProfile(Class clazz, Long observationId,
            Session session) throws CodedException {
        if (HibernateHelper.isEntitySupported(clazz, session)) {
            Criteria criteria = session.createCriteria(clazz);
            criteria.createCriteria(AbstractSpatialFilteringProfile.OBSERVATION).add(
                    Restrictions.eq(AbstractObservation.ID, observationId));
            LOGGER.debug("QUERY getSpatialFilertingProfile(observationId): {}", HibernateHelper.getSqlString(criteria));
            return (AbstractSpatialFilteringProfile) criteria.uniqueResult();
        }
        throw new OptionNotSupportedException().at("SpatialFilteringProfile").withMessage(
                "SpatialFilteringProfile is not supported! Add mapping to ressources!");
    }

    /**
     * Query all SpatialFilteringProfiles for observation ids
     * 
     * @param clazz
     *            Entity to create Criteria for
     * @param observationIdsList
     *            List with Observation ids list
     * @param session
     *            Hibernate session
     * @return SpatialFilteringProfiles for observation ids
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected List<AbstractSpatialFilteringProfile> querySpatialFilteringProfileCriteria(Class clazz,
            List<List<Long>> observationIdsList, Session session) {
        List<AbstractSpatialFilteringProfile> list = Lists.newArrayList();
        for (List<Long> observationIds : observationIdsList) {
            Criteria criteria = session.createCriteria(clazz);
            criteria.createCriteria(AbstractSpatialFilteringProfile.OBSERVATION).add(
                    Restrictions.in(AbstractObservation.ID, observationIds));
            LOGGER.debug("QUERY querySpatialFilteringProfileCriteria(observationIdsList): {}",
                    HibernateHelper.getSqlString(criteria));
            list.addAll((List<AbstractSpatialFilteringProfile>) criteria.list());
        }
        return list;
    }

    /**
     * Check if SpatialFilteringProfiles are contained in the database
     * 
     * @param clazz
     *            Entity to create Criteria for
     * @param count
     *            Current SpatialFilteringProfile count
     * @param session
     *            Hibernate session
     * @return <code>true</code>, if SpatialFilteringProfiles are contained in
     *         the database
     */
    @SuppressWarnings("rawtypes")
    protected boolean hasSpatialFilteringProfileValues(Class clazz, Long count, Session session) {
        if (count != null) {
            return count == 0 ? false : true;
        } else {
            return getSpatialFilteringProfileCount(clazz, session) == 0 ? false : true;
        }
    }

    /**
     * Query the count of SpatialFilteringProfile contained in the database
     * 
     * @param clazz
     *            Entity to create Criteria for
     * @param session
     *            Hibernate session
     * @return Count of SpatialFilteringProfile contained in the database
     */
    @SuppressWarnings("rawtypes")
    protected Long getSpatialFilteringProfileCount(Class clazz, Session session) {
        Criteria criteria = session.createCriteria(clazz);
        criteria.setProjection(Projections.countDistinct(AbstractSpatialFilteringProfile.OBSERVATION));
        LOGGER.debug("QUERY hasSpatialFilteringProfileValues(): {}", HibernateHelper.getSqlString(criteria));
        return (Long) criteria.uniqueResult();
    }

    /**
     * Get geometry from SpatialFilteringProfile
     * 
     * @param spatialFilteringProfile
     *            SpatialFilteringProfile to get geomnetry from
     * @return Geometry
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    protected Geometry getGeomtery(AbstractSpatialFilteringProfile spatialFilteringProfile) throws OwsExceptionReport {
        if (spatialFilteringProfile.isSetGeometry()) {
            return GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(spatialFilteringProfile.getGeom());
        } else if (spatialFilteringProfile.isSetLongLat()) {
            int epsg = GeometryHandler.getInstance().getDefaultEPSG();
            if (spatialFilteringProfile.isSetSrid()) {
                epsg = spatialFilteringProfile.getSrid();
            }
            final String wktString =
                    GeometryHandler.getInstance().getWktString(spatialFilteringProfile.getLongitude(),
                            spatialFilteringProfile.getLatitude());
            final Geometry geom = JTSHelper.createGeometryFromWKT(wktString, epsg);
            if (spatialFilteringProfile.isSetAltitude()) {
                geom.getCoordinate().z =
                        GeometryHandler.getInstance().getValueAsDouble(spatialFilteringProfile.getAltitude());
                if (geom.getSRID() == GeometryHandler.getInstance().getDefaultEPSG()) {
                    geom.setSRID(GeometryHandler.getInstance().getDefault3DEPSG());
                }
            }
            return GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(geom);
        }
        return null;
    }

    /**
     * Create detached observation criteria for spatial filter
     * 
     * @param clazz
     *            Entity to create Criteria for
     * @param spatialFilter
     * @return Detached criteria
     * @throws OwsExceptionReport
     *             If coordinate switching fails
     */
    @SuppressWarnings("rawtypes")
    protected DetachedCriteria getDetachedCriteria(Class clazz, SpatialFilter spatialFilter) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(clazz);
        if (spatialFilter != null) {
            detachedCriteria.add(SpatialRestrictions.filter(AbstractSpatialFilteringProfile.GEOMETRY,
                    spatialFilter.getOperator(),
                    GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(spatialFilter.getGeometry())));
        }
        detachedCriteria.setProjection(Projections.distinct(Projections
                .property(AbstractSpatialFilteringProfile.OBSERVATION)));
        return detachedCriteria;
    }

    /**
     * Create criteria for entity and detached criteria
     * 
     * @param clazz
     *            Entity to create Criteria for
     * @param detachedCriteria
     * @param session
     *            Hibernate session
     * @return Criteria
     */
    @SuppressWarnings("rawtypes")
    protected Criteria getObservationCriteria(Class clazz, DetachedCriteria detachedCriteria, Session session) {
        final Criteria criteria = session.createCriteria(clazz);
        criteria.add(Subqueries.propertyIn(AbstractSpatialFilteringProfile.OBSERVATION, detachedCriteria));
        criteria.createAlias(AbstractSpatialFilteringProfile.OBSERVATION, "o");
        criteria.setProjection(Projections.distinct(Projections.property("o." + AbstractObservation.ID)));
        return criteria;
    }

    /**
     * Get the Capabilities envelope for offering from Spatial Filtering Profile
     * 
     * @param clazz
     *            Entity to create Criteria for
     * @param offeringID
     *            Offering identifier
     * @param session
     *            Hibernate session
     * @return Envelope
     * @throws OwsExceptionReport
     *             If coordinate switching fails
     */
    @SuppressWarnings("rawtypes")
    protected SosEnvelope getEnvelopeForOfferingId(Class clazz, String offeringID, Session session)
            throws OwsExceptionReport {
        try {
            // XXX workaround for Hibernate Spatial's lack of support for
            // GeoDB's extent aggregate see
            // http://www.hibernatespatial.org/pipermail/hibernatespatial-users/2013-August/000876.html
            Dialect dialect = ((SessionFactoryImplementor) session.getSessionFactory()).getDialect();
            if (GeometryHandler.getInstance().isSpatialDatasource()
                    && HibernateHelper.supportsFunction(dialect, HibernateConstants.FUNC_EXTENT)) {
                Criteria criteria = session.createCriteria(clazz);
                criteria.setProjection(SpatialProjections.extent(FeatureOfInterest.GEOMETRY));
                Criteria createCriteria = criteria.createCriteria(AbstractSpatialFilteringProfile.OBSERVATION);
                createCriteria.createCriteria(AbstractObservation.OFFERINGS).add(
                        Restrictions.eq(Offering.IDENTIFIER, offeringID));
                LOGGER.debug("QUERY getEnvelopeForOfferingId(offeringID): {}", HibernateHelper.getSqlString(criteria));
                Geometry geom = (Geometry) criteria.uniqueResult();
                geom = GeometryHandler.getInstance().switchCoordinateAxisOrderIfNeeded(geom);
                if (geom != null) {
                    return new SosEnvelope(geom.getEnvelopeInternal(), GeometryHandler.getInstance().getDefaultEPSG());
                }
            } else {
                final Envelope envelope = new Envelope();
                Criteria criteria = session.createCriteria(getSpatialFilteringProfileImpl().getClass());
                Criteria createCriteria = criteria.createCriteria(AbstractSpatialFilteringProfile.OBSERVATION);
                createCriteria.createCriteria(AbstractObservation.OFFERINGS).add(
                        Restrictions.eq(Offering.IDENTIFIER, offeringID));
                LOGGER.debug("QUERY getEnvelopeForOfferingId(offeringID): {}", HibernateHelper.getSqlString(criteria));
                @SuppressWarnings("unchecked")
                final List<AbstractSpatialFilteringProfile> spatialFilteringProfiles = criteria.list();
                if (CollectionHelper.isNotEmpty(spatialFilteringProfiles)) {
                    for (final AbstractSpatialFilteringProfile spatialFilteringProfile : spatialFilteringProfiles) {
                        try {
                            final Geometry geom = getGeomtery(spatialFilteringProfile);
                            if (geom != null && geom.getEnvelopeInternal() != null) {
                                envelope.expandToInclude(geom.getEnvelopeInternal());
                            }
                        } catch (final OwsExceptionReport owse) {
                            LOGGER.warn(
                                    String.format("Error while adding '%s' to envelope!",
                                            spatialFilteringProfile.getSpatialFilteringProfileId()), owse);
                        }

                    }
                    if (!envelope.isNull()) {
                        return new SosEnvelope(envelope, GeometryHandler.getInstance().getDefaultEPSG());
                    }
                }
            }
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Exception thrown while requesting feature envelope for observation ids");
        }
        return null;
    }

}
