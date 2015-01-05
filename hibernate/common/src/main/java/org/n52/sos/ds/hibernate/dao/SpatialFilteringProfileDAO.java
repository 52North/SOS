/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.AbstractSpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.SpatialFilteringProfile;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosEnvelope;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Hibernate DAO class for SOS 2.0 Spatial Filtering Profile
 * 
 * @since 4.0.0
 * 
 */
@Configurable
@Deprecated
public class SpatialFilteringProfileDAO extends AbstractSpatialFilteringProfileDAO<SpatialFilteringProfile> {

    @Override
    public AbstractSpatialFilteringProfile getSpatialFilertingProfile(Long observationId, Session session)
            throws CodedException {
        return getSpatialFilertingProfile(SpatialFilteringProfile.class, observationId, session);
    }

    @Override
    public Set<Long> getObservationIdsForSpatialFilter(SpatialFilter spatialFilter, Session session)
            throws OwsExceptionReport {
        return getObservationIdsForSpatialFilter(SpatialFilteringProfile.class, spatialFilter, session);
    }

    @Override
    public void insertSpatialfilteringProfile(NamedValue<Geometry> namedValue, AbstractObservation observation, Session session)
            throws OwsExceptionReport {
        insertSpatialfilteringProfile(SpatialFilteringProfile.class, namedValue, observation, session);
    }

    @Override
    public Map<Long, AbstractSpatialFilteringProfile> getSpatialFilertingProfiles(Set<Long> observationIds, Session session) {
        return getSpatialFilertingProfiles(SpatialFilteringProfile.class, observationIds, session);

    }

    @Override
    public List<AbstractSpatialFilteringProfile> getSpatialFileringProfiles(Session session) {
        return getSpatialFileringProfiles(SpatialFilteringProfile.class, session);
    }

    @Override
    public SosEnvelope getEnvelopeForOfferingId(String offeringID, Session session) throws OwsExceptionReport {
        return getEnvelopeForOfferingId(SpatialFilteringProfile.class, offeringID, session);
    }

    @Override
    protected SpatialFilteringProfile getSpatialFilteringProfileImpl() {
        return new SpatialFilteringProfile();
    }

    @Override
    public DetachedCriteria getDetachedCriteriaFor(SpatialFilter filter) throws OwsExceptionReport {
        return getDetachedCriteria(SpatialFilteringProfile.class, filter);
    }

}
