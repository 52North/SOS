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
package org.n52.sos.ds.hibernate.dao.observation;

import java.sql.Timestamp;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.dao.TimeCreator;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.AbstractTemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractValuedLegacyObservation;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract DAO class for querying {@link AbstractValuedLegacyObservation}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class AbstractValueDAO extends TimeCreator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractValueDAO.class);

    /**
     * Check if a Spatial Filtering Profile filter is requested and add to
     * criteria
     * 
     * @param c
     *            Criteria to add crtierion
     * @param request
     *            GetObservation request
     * @param session
     *            Hiberante Session
     * @throws OwsExceptionReport
     *             If Spatial Filteirng Profile is not supported or an error
     *             occurs.
     */
    protected void checkAndAddSpatialFilteringProfileCriterion(Criteria c, GetObservationRequest request,
            Session session) throws OwsExceptionReport {
        if (request.hasSpatialFilteringProfileSpatialFilter()) {
            if (GeometryHandler.getInstance().isSpatialDatasource()) {
                c.add(SpatialRestrictions.filter(
                        AbstractObservation.SAMPLING_GEOMETRY,
                        request.getSpatialFilter().getOperator(),
                        GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(
                                request.getSpatialFilter().getGeometry())));
            } else {
                // TODO add filter with lat/lon
                LOGGER.warn("Spatial filtering for lat/lon is not yet implemented!");
            }
        }
    }
    
    protected void addTemporalFilterCriterion(Criteria c, Criterion temporalFilterCriterion, String logArgs) {
        if (temporalFilterCriterion != null) {
            logArgs += ", filterCriterion";
            c.add(temporalFilterCriterion);
        }
    }

    /**
     * Add an indeterminate time restriction to a criteria. This allows for
     * multiple results if more than one observation has the extrema time (max
     * for latest, min for first). Note: use this method *after* adding all
     * other applicable restrictions so that they will apply to the min/max
     * observation time determination.
     * 
     * @param c
     *            Criteria to add the restriction to
     * @param sosIndeterminateTime
     *            Indeterminate time restriction to add
     * @return Modified criteria
     */
    protected Criteria addIndeterminateTimeRestriction(Criteria c, SosIndeterminateTime sosIndeterminateTime, String logArgs) {
        if (sosIndeterminateTime != null) {
            // get extrema indeterminate time
            c.setProjection(getIndeterminateTimeExtremaProjection(sosIndeterminateTime));
            Timestamp indeterminateExtremaTime = (Timestamp) c.uniqueResult();
    
            // reset criteria
            // see http://stackoverflow.com/a/1472958/193435
            c.setProjection(null);
            c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    
            // get observations with exactly the extrema time
            c.add(Restrictions.eq(getIndeterminateTimeFilterProperty(sosIndeterminateTime), indeterminateExtremaTime));
            logArgs += ", sosIndeterminateTime";
        }
        // not really necessary to return the Criteria object, but useful if we
        // want to chain
        return c;
    }

    /**
     * Get projection for {@link SosIndeterminateTime} value
     * 
     * @param indetTime
     *            Value to get projection for
     * @return Projection to use to determine indeterminate time extrema
     */
    protected Projection getIndeterminateTimeExtremaProjection(final SosIndeterminateTime indetTime) {
        if (indetTime.equals(SosIndeterminateTime.first)) {
            return Projections.min(AbstractValuedLegacyObservation.PHENOMENON_TIME_START);
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
            return Projections.max(AbstractValuedLegacyObservation.PHENOMENON_TIME_END);
        }
        return null;
    }

    /**
     * Get the AbstractValue property to filter on for an
     * {@link SosIndeterminateTime}
     * 
     * @param indetTime
     *            Value to get property for
     * @return String property to filter on
     */
    protected String getIndeterminateTimeFilterProperty(final SosIndeterminateTime indetTime) {
        if (indetTime.equals(SosIndeterminateTime.first)) {
            return AbstractValuedLegacyObservation.PHENOMENON_TIME_START;
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
            return AbstractValuedLegacyObservation.PHENOMENON_TIME_END;
        }
        return null;
    }

    /**
     * Add chunk information to {@link Criteria}
     * 
     * @param c
     *            {@link Criteria} to add information
     * @param chunkSize
     *            Chunk size
     * @param currentRow
     *            Start row
     * @param request 
     */
    protected void addChunkValuesToCriteria(Criteria c, int chunkSize, int currentRow, GetObservationRequest request) {
        c.addOrder(Order.asc(getOrderColumn(request)));
        if (chunkSize > 0) {
            c.setMaxResults(chunkSize).setFirstResult(currentRow);
        }
    }
    
    private String getOrderColumn(GetObservationRequest request) {
        if (request.isSetTemporalFilter()) {
            TemporalFilter filter = request.getTemporalFilters().iterator().next();
            if (filter.getValueReference().contains(AbstractTemporalReferencedObservation.RESULT_TIME)) {
               return AbstractTemporalReferencedObservation.RESULT_TIME;
            }
        }
        return AbstractTemporalReferencedObservation.PHENOMENON_TIME_START;
    }
    
    @SuppressWarnings("rawtypes")
    protected Criteria getDefaultCriteria(Class clazz, Session session) {
        Criteria criteria = session.createCriteria(clazz)
                .add(Restrictions.eq(Observation.DELETED, false));

        if (!isIncludeChildObservableProperties()) {
            criteria.add(Restrictions.eq(Observation.CHILD, false));
        } else {
            criteria.add(Restrictions.eq(Observation.PARENT, false));
        }

        return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    protected boolean isIncludeChildObservableProperties() {
        return ServiceConfiguration.getInstance().isIncludeChildObservableProperties();
    }

    protected abstract void addSpecificRestrictions(Criteria c, GetObservationRequest request) throws CodedException;

}
