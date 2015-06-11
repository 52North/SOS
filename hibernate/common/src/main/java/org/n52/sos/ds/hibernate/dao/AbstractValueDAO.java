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

import java.sql.Timestamp;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.values.AbstractValue;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.GeometryHandler;

/**
 * Abstract DAO class for querying {@link AbstractValue}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class AbstractValueDAO extends TimeCreator {

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
            c.add(SpatialRestrictions.filter(
                    AbstractObservation.SAMPLING_GEOMETRY,
                    request.getSpatialFilter().getOperator(),
                    GeometryHandler.getInstance().switchCoordinateAxisFromToDatasourceIfNeeded(
                            request.getSpatialFilter().getGeometry())));
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
    protected Criteria addIndeterminateTimeRestriction(Criteria c, SosIndeterminateTime sosIndeterminateTime) {
        // get extrema indeterminate time
        c.setProjection(getIndeterminateTimeExtremaProjection(sosIndeterminateTime));
        Timestamp indeterminateExtremaTime = (Timestamp) c.uniqueResult();

        // reset criteria
        // see http://stackoverflow.com/a/1472958/193435
        c.setProjection(null);
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        // get observations with exactly the extrema time
        c.add(Restrictions.eq(getIndeterminateTimeFilterProperty(sosIndeterminateTime), indeterminateExtremaTime));

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
            return Projections.min(AbstractValue.PHENOMENON_TIME_START);
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
            return Projections.max(AbstractValue.PHENOMENON_TIME_END);
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
            return AbstractValue.PHENOMENON_TIME_START;
        } else if (indetTime.equals(SosIndeterminateTime.latest)) {
            return AbstractValue.PHENOMENON_TIME_END;
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
            if (filter.getValueReference().contains(AbstractValue.RESULT_TIME)) {
               return AbstractValue.RESULT_TIME;
            }
        }
        return AbstractValue.PHENOMENON_TIME_START;
    }

    protected abstract void addSpecificRestrictions(Criteria c, GetObservationRequest request) throws CodedException;

}
