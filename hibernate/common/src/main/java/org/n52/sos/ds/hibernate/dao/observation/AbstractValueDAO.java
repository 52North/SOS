/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.filter.Filter;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ExtendedIndeterminateTime;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.TimeCreator;
import org.n52.sos.ds.hibernate.util.ResultFilterClasses;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions;
import org.n52.sos.ds.hibernate.util.ResultFilterRestrictions.SubQueryIdentifier;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract DAO class for querying {@link DataEntity}
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public abstract class AbstractValueDAO extends TimeCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractValueDAO.class);

    private DaoFactory daoFactory;

    public AbstractValueDAO(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Check if a Spatial Filtering Profile filter is requested and add to
     * criteria
     *
     * @param c
     *            Criteria to add crtierion
     * @param request
     *            GetObservationRequest request
     * @param session
     *            Hiberante Session
     * @param logArgs
     *            log arguments
     * @throws OwsExceptionReport
     *             If Spatial Filteirng Profile is not supported or an error
     *             occurs.
     */
    protected void checkAndAddSpatialFilteringProfileCriterion(Criteria c, GetObservationRequest request,
            Session session, StringBuilder logArgs) throws OwsExceptionReport {
        if (request.hasSpatialFilteringProfileSpatialFilter()) {
            if (getGeometryHandler().isSpatialDatasource()) {
                c.add(SpatialRestrictions.filter(DataEntity.PROPERTY_GEOMETRY_ENTITY,
                        ((GetObservationRequest) request).getSpatialFilter().getOperator(),
                        getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(
                                ((GetObservationRequest) request).getSpatialFilter().getGeometry())));
                logArgs.append(", spatialFilter");
            } else {
                // TODO add filter with lat/lon
                LOGGER.warn("Spatial filtering for lat/lon is not yet implemented!");
            }
        }
    }

    protected void checkAndAddResultFilterCriterion(Criteria c, GetObservationRequest request,
            SubQueryIdentifier identifier, Session session, StringBuilder logArgs) throws OwsExceptionReport {
        if (request.hasResultFilter()) {
            Filter<?> resultFilter = request.getResultFilter();
            Criterion resultFilterExpression = ResultFilterRestrictions.getResultFilterExpression(resultFilter,
                    getResultFilterClasses(), DataEntity.PROPERTY_ID, identifier);
            if (resultFilterExpression != null) {
                c.add(resultFilterExpression);
                logArgs.append(", resultFilter");
            }
        }
    }

    protected ResultFilterClasses getResultFilterClasses() {
        return new ResultFilterClasses(getValuedObservationFactory().numericClass(),
                getValuedObservationFactory().countClass(), getValuedObservationFactory().textClass(),
                getValuedObservationFactory().categoryClass(), getValuedObservationFactory().complexClass(),
                getValuedObservationFactory().profileClass());
    }

    protected void addTemporalFilterCriterion(Criteria c, Criterion temporalFilterCriterion, StringBuilder logArgs) {
        if (temporalFilterCriterion != null) {
            logArgs.append(", filterCriterion");
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
    protected Criteria addIndeterminateTimeRestriction(Criteria c, IndeterminateValue sosIndeterminateTime,
            StringBuilder logArgs) {
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

            logArgs.append(", sosIndeterminateTime");
        }
        return c;
    }

    /**
     * Get projection for {@link IndeterminateValue} value
     *
     * @param indetTime
     *            Value to get projection for
     * @return Projection to use to determine indeterminate time extrema
     */
    protected Projection getIndeterminateTimeExtremaProjection(IndeterminateValue indetTime) {
        if (indetTime.equals(ExtendedIndeterminateTime.FIRST)) {
            return Projections.min(DataEntity.PROPERTY_SAMPLING_TIME_START);
        } else if (indetTime.equals(ExtendedIndeterminateTime.LATEST)) {
            return Projections.max(DataEntity.PROPERTY_SAMPLING_TIME_END);
        }
        return null;
    }

    /**
     * Get the AbstractValue property to filter on for an
     * {@link IndeterminateValue}
     *
     * @param indetTime
     *            Value to get property for
     * @return String property to filter on
     */
    protected String getIndeterminateTimeFilterProperty(IndeterminateValue indetTime) {
        if (indetTime.equals(ExtendedIndeterminateTime.FIRST)) {
            return DataEntity.PROPERTY_SAMPLING_TIME_START;
        } else if (indetTime.equals(ExtendedIndeterminateTime.LATEST)) {
            return DataEntity.PROPERTY_SAMPLING_TIME_END;
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
     *            the request
     * @param logArgs
     *            log arguments
     */
    protected void addChunkValuesToCriteria(Criteria c, int chunkSize, int currentRow,
            AbstractObservationRequest request, StringBuilder logArgs) {
        if (chunkSize > 0) {
            c.setMaxResults(chunkSize).setFirstResult(currentRow);
            logArgs.append(", chunk(" + currentRow + "," + chunkSize + ")");
        }
    }

    protected String getOrderColumn(AbstractObservationRequest request) {
        if (request instanceof GetObservationRequest) {
            if (((GetObservationRequest) request).isSetTemporalFilter()) {
                TemporalFilter filter = ((GetObservationRequest) request).getTemporalFilters().iterator().next();
                if (filter.getValueReference().contains(DataEntity.PROPERTY_RESULT_TIME)) {
                    return DataEntity.PROPERTY_RESULT_TIME;
                }
            }
        }
        return DataEntity.PROPERTY_SAMPLING_TIME_START;
    }

    @SuppressWarnings("rawtypes")
    protected Criteria getDefaultCriteria(Class clazz, Session session) {
        Criteria criteria = session.createCriteria(clazz).add(Restrictions.eq(DataEntity.PROPERTY_DELETED, false));

        // FIXME check if this works
        if (!daoFactory.isIncludeChildObservableProperties()) {
            criteria.add(Restrictions.isNull(DataEntity.PROPERTY_PARENT));
        } else {
            criteria.add(Restrictions.or(Restrictions.isNotNull(DataEntity.PROPERTY_PARENT),
                    Restrictions.and(Restrictions.isNull(DataEntity.PROPERTY_PARENT),
                            Restrictions.sizeEq(DataEntity.PROPERTY_VALUE, 0))));
            // criteria.add(Restrictions.isNotNull(DataEntity.PROPERTY_PARENT));
        }
        criteria.setFetchMode(DataEntity.PROPERTY_PARAMETERS, FetchMode.JOIN);
        return criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    protected abstract void addSpecificRestrictions(Criteria c, GetObservationRequest request, StringBuilder logArgs)
            throws OwsExceptionReport;

    protected abstract ValuedObservationFactory getValuedObservationFactory();

    public GeometryHandler getGeometryHandler() {
        return daoFactory.getGeometryHandler();
    }
}
