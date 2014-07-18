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
package org.n52.sos.ds.hibernate.dao.series;

import static org.hibernate.criterion.Restrictions.eq;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.dao.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.AbstractObservationTime;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesBlobObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesBooleanObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesCategoryObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesCountObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesGeometryObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesNumericObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationInfo;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationTime;
import org.n52.sos.ds.hibernate.entities.series.SeriesSweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesTextObservation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Hibernate data access class for series observations
 * 
 * @since 4.0.0
 * 
 */
public class SeriesObservationDAO extends AbstractObservationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeriesObservationDAO.class);

    /**
     * Create series observation query criteria for series and offerings
     * 
     * @param clazz
     *            Class to query
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param session
     *            Hibernate session
     * @return Criteria to query series observations
     */
    private Criteria createCriteriaFor(Class<?> clazz, Series series, List<String> offerings, Session session) {
        final Criteria criteria = createCriteriaFor(clazz, series, session);
        if (CollectionHelper.isNotEmpty(offerings)) {
            criteria.createCriteria(SeriesObservation.OFFERINGS).add(Restrictions.in(Offering.IDENTIFIER, offerings));
        }
        return criteria;
    }

    /**
     * Create series observation query criteria for series
     * 
     * @param Class
     *            to query
     * @param series
     *            Series to get values for
     * @param session
     *            Hibernate session
     * @return Criteria to query series observations
     */
    private Criteria createCriteriaFor(Class<?> clazz, Series series, Session session) {
        final Criteria criteria = getDefaultObservationCriteria(clazz, session);
        criteria.createCriteria(SeriesObservation.SERIES).add(Restrictions.eq(Series.ID, series.getSeriesId()));
        return criteria;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getObservationIdentifiers(Session session) {
        Criteria criteria =
                session.createCriteria(SeriesObservationInfo.class).add(Restrictions.eq(Observation.DELETED, false))
                        .add(Restrictions.isNotNull(Observation.IDENTIFIER))
                        .setProjection(Projections.distinct(Projections.property(Observation.IDENTIFIER)));
        LOGGER.debug("QUERY getObservationIdentifiers(): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    @Override
    public boolean checkNumericObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(SeriesNumericObservation.class, offeringIdentifier, session);
    }

    @Override
    public boolean checkBooleanObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(SeriesBooleanObservation.class, offeringIdentifier, session);
    }

    @Override
    public boolean checkCountObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(SeriesCountObservation.class, offeringIdentifier, session);
    }

    @Override
    public boolean checkCategoryObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(SeriesCategoryObservation.class, offeringIdentifier, session);
    }

    @Override
    public boolean checkTextObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(SeriesTextObservation.class, offeringIdentifier, session);
    }

    @Override
    public boolean checkBlobObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(SeriesBlobObservation.class, offeringIdentifier, session);
    }

    @Override
    public boolean checkGeometryObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(SeriesGeometryObservation.class, offeringIdentifier, session);
    }

    @Override
    public boolean checkSweDataArrayObservationsFor(String offeringIdentifier, Session session) {
        return checkObservationFor(SeriesSweDataArrayObservation.class, offeringIdentifier, session);
    }

    /**
     * Query series observation for series and offerings
     * 
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    @SuppressWarnings("unchecked")
    public List<SeriesObservation> getSeriesObservationFor(Series series, List<String> offerings, Session session) {
        final Criteria criteria = createCriteriaFor(SeriesObservation.class, series, offerings, session);
        LOGGER.debug("QUERY getSeriesObservationFor(series, offerings): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Query series obserations for series, temporal filter, and offerings
     * 
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param filterCriterion
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    @SuppressWarnings("unchecked")
    public List<SeriesObservation> getSeriesObservationFor(Series series, List<String> offerings,
            Criterion filterCriterion, Session session) {
        final Criteria criteria = createCriteriaFor(SeriesObservation.class, series, offerings, session);
        criteria.add(filterCriterion);
        LOGGER.debug("QUERY getSeriesObservationFor(series, offerings, temporalFilter): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Query first/latest series obserations for series (and offerings)
     * 
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param sosIndeterminateTime
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    @SuppressWarnings("unchecked")
    public List<SeriesObservation> getSeriesObservationForSosIndeterminateTimeFilter(Series series,
            List<String> offerings, SosIndeterminateTime sosIndeterminateTime, Session session) {
        final Criteria criteria = createCriteriaFor(SeriesObservation.class, series, offerings, session);
        criteria.addOrder(getOrder(sosIndeterminateTime)).setMaxResults(1);
        LOGGER.debug("QUERY getSeriesObservationForSosIndeterminateTimeFilter(series, offerings,(first,latest)): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    /**
     * Query series observations for GetObservation request and features
     * 
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    public List<SeriesObservation> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, null, null, session);
    }
    
    /**
     * Query series observations {@link ScrollableResults} for GetObservation request and features
     * 
     * @param request
     *              GetObservation request  
     * @param features
     *              Collection of feature identifiers resolved from the request
     * @param session
     *              Hibernate session
     * @return {@link ScrollableResults} of Series observations that fit
     * @throws OwsExceptionReport 
     */
    public ScrollableResults getStreamingSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Session session) throws OwsExceptionReport {
        return getStreamingSeriesObservationsFor(request, features, null, null, session);
    }

    /**
     * Query series observations for GetObservation request, features, and a
     * filter criterion (typically a temporal filter)
     * 
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    public List<SeriesObservation> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, Criterion filterCriterion, Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, filterCriterion, null, session);
    }
    
    /**
     * Query series observations {@link ScrollableResults} for GetObservation request, features, and a filter criterion (typically a temporal filter)
     * 
     * @param request
     *              GetObservation request  
     * @param features
     *              Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *              Criterion to apply to criteria query (typically a temporal filter)
     * @param session
     *              Hibernate session
     * @return {@link ScrollableResults} of Series observations that fit
     * @throws OwsExceptionReport 
     */
    public ScrollableResults getStreamingSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, Session session) throws OwsExceptionReport {
        return getStreamingSeriesObservationsFor(request, features, filterCriterion, null, session);
    }

    /**
     * Query series observations for GetObservation request, features, and an
     * indeterminate time (first/latest)
     * 
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param sosIndeterminateTime
     *            Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    public List<SeriesObservation> getSeriesObservationsFor(GetObservationRequest request,
            Collection<String> features, SosIndeterminateTime sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, null, sosIndeterminateTime, session);
    }

    /**
     * Query series observations for GetObservation request, features, and
     * filter criterion (typically a temporal filter) or an indeterminate time
     * (first/latest). This method is private and accepts all possible arguments
     * for request-based getSeriesObservationFor. Other public methods overload
     * this method with sensible combinations of arguments.
     * 
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param sosIndeterminateTime
     *            Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     * @return Series observations that fit
     * @throws OwsExceptionReport
     */
    @SuppressWarnings("unchecked")
    private List<SeriesObservation> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session).list();
    }
    
    /**
     * Query series observations for GetObservation request, features, and filter criterion (typically a temporal filter) or
     * an indeterminate time (first/latest). This method is private and accepts all possible arguments for request-based
     * getSeriesObservationFor. Other public methods overload this method with sensible combinations of arguments.
     * 
     * @param request
     *              GetObservation request
     * @param features
     *              Collection of feature identifiers resolved from the request 
     * @param filterCriterion
     *              Criterion to apply to criteria query (typically a temporal filter)
     * @param sosIndeterminateTime
     *              Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     * @return {@link ScrollableResults} of Series observations that fits
     * @throws OwsExceptionReport 
     */
    private ScrollableResults getStreamingSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session).setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
    }
    
    /**
     * Create series observations {@link Criteria} for GetObservation request, features, and filter criterion (typically a temporal filter) or
     * an indeterminate time (first/latest). This method is private and accepts all possible arguments for request-based
     * getSeriesObservationFor. Other public methods overload this method with sensible combinations of arguments.
     * 
     * @param request
     *              GetObservation request
     * @param features
     *              Collection of feature identifiers resolved from the request 
     * @param filterCriterion
     *              Criterion to apply to criteria query (typically a temporal filter)
     * @param sosIndeterminateTime
     *              Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     * @return Series observations {@link Criteria}
     * @throws OwsExceptionReport 
     */
    private Criteria getSeriesObservationCriteriaFor(GetObservationRequest request, Collection<String> features,
                Criterion filterCriterion, SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport {
            final Criteria c = getDefaultObservationCriteria(SeriesObservation.class, session)
                    .createAlias(SeriesObservation.SERIES, "s");
            
            checkAndAddSpatialFilteringProfileCriterion(c, request, session);
            
            if (CollectionHelper.isNotEmpty(request.getProcedures())) {
                c.createCriteria("s." + Series.PROCEDURE).add(Restrictions.in(Procedure.IDENTIFIER, request.getProcedures()));
            }
            
            if (CollectionHelper.isNotEmpty(request.getObservedProperties())) {
                c.createCriteria("s." + Series.OBSERVABLE_PROPERTY).add(Restrictions.in(ObservableProperty.IDENTIFIER,
                        request.getObservedProperties()));
            }
            
            if (CollectionHelper.isNotEmpty(features)) {
                c.createCriteria("s." + Series.FEATURE_OF_INTEREST).add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
            }
            
            if (CollectionHelper.isNotEmpty(request.getOfferings())) {
                c.createCriteria(SeriesObservation.OFFERINGS).add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
            }
            
            String logArgs = "request, features, offerings";
            if (filterCriterion != null) {
                logArgs += ", filterCriterion";
                c.add(filterCriterion);
            }
            if (sosIndeterminateTime != null) {
                logArgs += ", sosIndeterminateTime";
                addIndeterminateTimeRestriction(c, sosIndeterminateTime);
            }
            LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
            return c;
    }

    @SuppressWarnings("unchecked")
    public List<SeriesObservation> getSeriesObservationsFor(Series series, GetObservationRequest request,
            SosIndeterminateTime sosIndeterminateTime, Session session) throws OwsExceptionReport {
        final Criteria c =
                getDefaultObservationCriteria(SeriesObservation.class, session).add(
                        Restrictions.eq(SeriesObservation.SERIES, series));
        checkAndAddSpatialFilteringProfileCriterion(c, request, session);

        if (request.isSetOffering()) {
            c.createCriteria(SeriesObservation.OFFERINGS).add(
                    Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }
        String logArgs = "request, features, offerings";
        logArgs += ", sosIndeterminateTime";
        addIndeterminateTimeRestriction(c, sosIndeterminateTime);
        LOGGER.debug("QUERY getSeriesObservationFor({}): {}", logArgs, HibernateHelper.getSqlString(c));
        return c.list();

    }

    /**
     * Query the min time from series observations for series
     * 
     * @param series
     *            Series to get values for
     * @param session
     *            Hibernate session
     * @return Min time from series observations
     */
    public DateTime getMinSeriesObservationTime(Series series, Session session) {
        Criteria criteria = createCriteriaFor(SeriesObservationTime.class, series, session);
        criteria.setProjection(Projections.min(SeriesObservationTime.PHENOMENON_TIME_START));
        Object min = criteria.uniqueResult();
        if (min != null) {
            return new DateTime(min, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Query the max time from series observations for series
     * 
     * @param series
     *            Series to get values for
     * @param session
     *            Hibernate session
     * @return Max time from series observations
     */
    public DateTime getMaxSeriesObservationTime(Series series, Session session) {
        Criteria criteria = createCriteriaFor(SeriesObservationTime.class, series, session);
        criteria.setProjection(Projections.max(SeriesObservationTime.PHENOMENON_TIME_END));
        Object max = criteria.uniqueResult();
        if (max != null) {
            return new DateTime(max, DateTimeZone.UTC);
        }
        return null;
    }

    /**
     * Create criteria to query min/max time for series from series observation
     * 
     * @param series
     *            Series to get values for
     * @param list
     * @param session
     *            Hibernate session
     * @return Criteria to get min/max time values for series
     */
    public Criteria getMinMaxTimeCriteriaForSeriesObservation(Series series, Collection<String> offerings,
            Session session) {
        Criteria criteria = createCriteriaFor(SeriesObservationTime.class, series, session);
        if (CollectionHelper.isNotEmpty(offerings)) {
            criteria.createCriteria(SeriesObservationTime.OFFERINGS).add(
                    Restrictions.in(Offering.IDENTIFIER, offerings));
        }
        criteria.setProjection(Projections.projectionList()
                .add(Projections.min(SeriesObservationTime.PHENOMENON_TIME_START))
                .add(Projections.max(SeriesObservationTime.PHENOMENON_TIME_END)));
        return criteria;
    }

    /**
     * Get the result times for this series, offerings and filters
     * 
     * @param series
     *            Timeseries to get result times for
     * @param offerings
     *            Offerings to restrict matching result times
     * @param filter
     *            Temporal filter to restrict matching result times
     * @param session
     *            Hibernate session
     * @return Matching result times
     */
    @SuppressWarnings("unchecked")
    public List<Date> getResultTimesForSeriesObservation(Series series, List<String> offerings, Criterion filter,
            Session session) {
        Criteria criteria = createCriteriaFor(SeriesObservationTime.class, series, session);
        if (CollectionHelper.isNotEmpty(offerings)) {
            criteria.createCriteria(SeriesObservationTime.OFFERINGS).add(
                    Restrictions.in(Offering.IDENTIFIER, offerings));
        }
        if (filter != null) {
            criteria.add(filter);
        }
        criteria.setProjection(Projections.distinct(Projections.property(SeriesObservationTime.RESULT_TIME)));
        criteria.addOrder(Order.asc(SeriesObservationTime.RESULT_TIME));
        LOGGER.debug("QUERY getResultTimesForSeriesObservation({}): {}", HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    @Override
    public DateTime getMinPhenomenonTime(Session session) {
        return getMinPhenomenonTime(SeriesObservationTime.class, session);
    }

    @Override
    public DateTime getMaxPhenomenonTime(Session session) {
        return getMaxPhenomenonTime(SeriesObservationTime.class, session);

    }

    @Override
    public DateTime getMinResultTime(Session session) {
        return getMinResultTime(SeriesObservationTime.class, session);

    }

    @Override
    public DateTime getMaxResultTime(Session session) {
        return getMaxResultTime(SeriesObservationTime.class, session);

    }

    @Override
    public TimePeriod getGlobalTemporalBoundingBox(Session session) {
        return getGlobalTemporalBoundingBox(SeriesObservationTime.class, session);

    }

    @Override
    public AbstractObservation createObservationFromValue(Value<?> value, Session session) {
        if (value instanceof BooleanValue) {
            SeriesBooleanObservation observation = new SeriesBooleanObservation();
            observation.setValue(((BooleanValue) value).getValue());
            return observation;
        } else if (value instanceof UnknownValue) {
            SeriesBlobObservation observation = new SeriesBlobObservation();
            observation.setValue(((UnknownValue) value).getValue());
            return observation;
        } else if (value instanceof CategoryValue) {
            SeriesCategoryObservation observation = new SeriesCategoryObservation();
            observation.setValue(((CategoryValue) value).getValue());
            return observation;
        } else if (value instanceof CountValue) {
            SeriesCountObservation observation = new SeriesCountObservation();
            observation.setValue(((CountValue) value).getValue());
            return observation;
        } else if (value instanceof GeometryValue) {
            SeriesGeometryObservation observation = new SeriesGeometryObservation();
            observation.setValue(((GeometryValue) value).getValue());
            return observation;
        } else if (value instanceof QuantityValue) {
            SeriesNumericObservation observation = new SeriesNumericObservation();
            observation.setValue(((QuantityValue) value).getValue());
            return observation;
        } else if (value instanceof TextValue) {
            SeriesTextObservation observation = new SeriesTextObservation();
            observation.setValue(((TextValue) value).getValue());
            return observation;
        } else if (value instanceof SweDataArrayValue) {
            SeriesSweDataArrayObservation observation = new SeriesSweDataArrayObservation();
            observation.setValue(((SweDataArrayValue) value).getValue().getXml());
            return observation;
        }
        return new SeriesObservation();
    }

    @Override
    protected void addObservationIdentifiersToObservation(ObservationIdentifiers observationIdentifiers,
            AbstractObservation hObservation, Session session) {
        SeriesDAO seriesDAO = new SeriesDAO();
        Series series =
                seriesDAO
                        .getOrInsertSeries(observationIdentifiers.getFeatureOfInterest(),
                                observationIdentifiers.getObservableProperty(), observationIdentifiers.getProcedure(),
                                session);
        ((SeriesObservation) hObservation).setSeries(series);
        seriesDAO.updateSeriesWithFirstLatestValues(series, hObservation, session);
    }

    @Override
    public Criteria getObservationClassCriteriaForResultModel(String resultModel, Session session) {
        if (StringHelper.isNotEmpty(resultModel)) {
            if (resultModel.equals(OmConstants.OBS_TYPE_MEASUREMENT)) {
                return createCriteriaForObservationClass(SeriesNumericObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_COUNT_OBSERVATION)) {
                return createCriteriaForObservationClass(SeriesCountObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_CATEGORY_OBSERVATION)) {
                return createCriteriaForObservationClass(SeriesCategoryObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_TRUTH_OBSERVATION)) {
                return createCriteriaForObservationClass(SeriesBooleanObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_TEXT_OBSERVATION)) {
                return createCriteriaForObservationClass(SeriesTextObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION)) {
                return createCriteriaForObservationClass(SeriesGeometryObservation.class, session);
            } else if (resultModel.equals(OmConstants.OBS_TYPE_COMPLEX_OBSERVATION)) {
                return createCriteriaForObservationClass(SeriesBlobObservation.class, session);
            }
        }
        return createCriteriaForObservationClass(SeriesObservation.class, session);
    }

    @Override
    public Criteria getDefaultObservationCriteria(Session session) {
        return getDefaultObservationCriteria(SeriesObservation.class, session);
    }

    @Override
    public Criteria getDefaultObservationInfoCriteria(Session session) {
        return getDefaultObservationCriteria(SeriesObservationInfo.class, session);
    }

    @Override
    public AbstractObservation getObservationByIdentifier(String identifier, Session session) {
        Criteria criteria = getDefaultObservationCriteria(session);
        addObservationIdentifierToCriteria(criteria, identifier, session);
        return (AbstractObservation) criteria.uniqueResult();
    }

    @Override
    public Criteria getObservationInfoCriteriaForFeatureOfInterestAndProcedure(String feature, String procedure,
            Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(SeriesObservationInfo.SERIES);
        seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        seriesCriteria.createCriteria(AbstractObservation.PROCEDURE).add(eq(Procedure.IDENTIFIER, procedure));
        return criteria;
    }

    @Override
    public Criteria getObservationInfoCriteriaForFeatureOfInterestAndOffering(String feature, String offering,
            Session session) {
        Criteria criteria = getDefaultObservationInfoCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(SeriesObservationInfo.SERIES);
        seriesCriteria.createCriteria(Series.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        criteria.createCriteria(AbstractObservation.OFFERINGS).add(eq(Offering.IDENTIFIER, offering));
        return criteria;
    }

    /**
     * Update series observation by setting deleted flag
     * 
     * @param series
     *            Series for which the observations should be updated
     * @param deleteFlag
     *            New deleted flag value
     * @param session
     *            Hibernate Session
     */
    public void updateObservationSetAsDeletedForSeries(List<Series> series, boolean deleteFlag, Session session) {
        if (CollectionHelper.isNotEmpty(series)) {
            Criteria criteria = getDefaultObservationCriteria(session);
            criteria.add(Restrictions.in(SeriesObservation.SERIES, series));
            ScrollableIterable<AbstractObservation> scroll = ScrollableIterable.fromCriteria(criteria);
            updateObservation(scroll, deleteFlag, session);
        }
    }

    @Override
    public Criteria getObservationCriteriaForProcedure(String procedure, Session session) {
        SeriesDAO seriesDAO = new SeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(SeriesObservation.SERIES);
        seriesDAO.addProcedureToCriteria(seriesCriteria, procedure);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaForObservableProperty(String observableProperty, Session session) {
        SeriesDAO seriesDAO = new SeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(SeriesObservation.SERIES);
        seriesDAO.addObservablePropertyToCriteria(seriesCriteria, observableProperty);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaForFeatureOfInterest(String featureOfInterest, Session session) {
        SeriesDAO seriesDAO = new SeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(SeriesObservation.SERIES);
        seriesDAO.addFeatureOfInterestToCriteria(seriesCriteria, featureOfInterest);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaFor(String procedure, String observableProperty, Session session) {
        SeriesDAO seriesDAO = new SeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(SeriesObservation.SERIES);
        seriesDAO.addProcedureToCriteria(seriesCriteria, procedure);
        seriesDAO.addObservablePropertyToCriteria(seriesCriteria, observableProperty);
        return criteria;
    }

    @Override
    public Criteria getObservationCriteriaFor(String procedure, String observableProperty, String featureOfInterest,
            Session session) {
        SeriesDAO seriesDAO = new SeriesDAO();
        Criteria criteria = getDefaultObservationCriteria(session);
        Criteria seriesCriteria = criteria.createCriteria(SeriesObservation.SERIES);
        seriesDAO.addFeatureOfInterestToCriteria(seriesCriteria, featureOfInterest);
        seriesDAO.addProcedureToCriteria(seriesCriteria, procedure);
        seriesDAO.addObservablePropertyToCriteria(seriesCriteria, observableProperty);
        return criteria;
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getObservationIdentifiers(String procedureIdentifier, Session session) {
        Criteria criteria =
                session.createCriteria(SeriesObservationInfo.class)
                        .setProjection(Projections.distinct(Projections.property(SeriesObservationInfo.IDENTIFIER)))
                        .add(Restrictions.isNotNull(SeriesObservationInfo.IDENTIFIER))
                        .add(Restrictions.eq(SeriesObservationInfo.DELETED, false));
        Criteria seriesCriteria = criteria.createCriteria(SeriesObservationInfo.SERIES);
        seriesCriteria.createCriteria(Series.PROCEDURE)
                .add(Restrictions.eq(Procedure.IDENTIFIER, procedureIdentifier));
        LOGGER.debug("QUERY getObservationIdentifiers(procedureIdentifier): {}",
                HibernateHelper.getSqlString(criteria));
        return criteria.list();
    }

    public ScrollableResults getSeriesNotMatchingSeries(Set<Long> seriesIDs, GetObservationRequest request,
            Set<String> features, Criterion temporalFilterCriterion, Session session) throws OwsExceptionReport {
        Criteria c = getSeriesObservationCriteriaFor(request, features, temporalFilterCriterion, null, session).createAlias(SeriesObservation.SERIES, "s");
        c.add(Restrictions.not(Restrictions.in("s." + Series.ID, seriesIDs)));
        c.setProjection(Projections.property(SeriesObservation.SERIES));
        return c.setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
    }

    public ScrollableResults getSeriesNotMatchingSeries(Set<Long> seriesIDs, GetObservationRequest request,
            Set<String> features, Session session) throws OwsExceptionReport {
        Criteria c = getSeriesObservationCriteriaFor(request, features, null, null, session).createAlias(SeriesObservation.SERIES, "s");
        c.add(Restrictions.not(Restrictions.in("s." + Series.ID, seriesIDs)));
        return c.setReadOnly(true).scroll(ScrollMode.FORWARD_ONLY);
    }

    @Override
    public SosEnvelope getSpatialFilteringProfileEnvelopeForOfferingId(String offeringID, Session session) throws OwsExceptionReport {
        return getSpatialFilteringProfileEnvelopeForOfferingId(SeriesObservationTime.class, offeringID, session);
    }

    @Override
    public List<Geometry> getSamplingGeometries(String feature, Session session) {
        Criteria criteria = session.createCriteria(SeriesObservationTime.class).createAlias(SeriesObservation.SERIES, "s");
        criteria.createCriteria("s." + Series.FEATURE_OF_INTEREST).add(eq(FeatureOfInterest.IDENTIFIER, feature));
        criteria.addOrder(Order.asc(AbstractObservationTime.PHENOMENON_TIME_START));
        criteria.setProjection(Projections.property(AbstractObservationTime.SAMPLING_GEOMETRY));
        return criteria.list();
    }  
}
