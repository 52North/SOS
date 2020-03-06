/**
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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.dao.metadata.SeriesMetadataDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationTimeDAO;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.metadata.SeriesMetadata;
import org.n52.sos.ds.hibernate.entities.observation.ContextualReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.ContextualReferencedLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.ContextualReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.TemporalReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.gda.AbstractGetDataAvailabilityDAO;
import org.n52.sos.gda.GetDataAvailabilityConstants;
import org.n52.sos.gda.GetDataAvailabilityRequest;
import org.n52.sos.gda.GetDataAvailabilityResponse;
import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.gda.GetDataAvailabilityResponse.FormatDescriptor;
import org.n52.sos.gda.GetDataAvailabilityResponse.ObservationFormatDescriptor;
import org.n52.sos.gda.GetDataAvailabilityResponse.ProcedureDescriptionFormatDescriptor;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * {@code IGetDataAvailabilityDao} to handle {@link GetDataAvailabilityRequest}
 * s.
 *
 * @author Christian Autermann
 * @since 4.0.0
 */
public class GetDataAvailabilityDAO extends AbstractGetDataAvailabilityDAO implements HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetDataAvailabilityDAO.class);


    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES = "getDataAvailabilityForFeatures";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES_PROCEDURES =
            "getDataAvailabilityForFeaturesProcedures";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES_OBSERVED_PROPERTIES =
            "getDataAvailabilityForFeaturesObservableProperties";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES_PROCEDURES_OBSERVED_PROPERTIES =
            "getDataAvailabilityForFeaturesProceduresObservableProperties";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURES = "getDataAvailabilityForProcedures";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURES_OBSERVED_PROPERTIES =
            "getDataAvailabilityForProceduresObservableProperties";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_OBSERVED_PROPERTIES =
            "getDataAvailabilityForObservableProperties";

    private static final String SQL_QUERY_GET_DATA_AVAILABILITY_FOR_SERIES = "getDataAvailabilityForSeries";
    private static final String SQL_QUERY_GET_OFFERING_DATA_AVAILABILITY_FOR_SERIES = "getOfferingDataAvailabilityForSeries";
    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    public GetDataAvailabilityDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public GetDataAvailabilityResponse getDataAvailability(GetDataAvailabilityRequest req) throws OwsExceptionReport {
        Session session = sessionHolder.getSession();
        try {
            List<?> dataAvailabilityValues = queryDataAvailabilityValues(req, session);
            if (isForceGDAv20Response()) {
                req.setResponseFormat(GetDataAvailabilityConstants.NS_GDA_20);
            }
            GetDataAvailabilityResponse response = req.getResponse();
            response.setNamespace(req.getNamespace());
            for (Object o : dataAvailabilityValues) {
                if (o != null) {
                    response.addDataAvailability((DataAvailability) o);
                }
            }
            return response;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while querying data for GetDataAvailability!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    /**
     * Query data availability information depending on supported functionality
     *
     * @param req
     *            GetDataAvailability request
     * @param session
     *            Hibernate session
     * @return Data availability information
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private List<?> queryDataAvailabilityValues(GetDataAvailabilityRequest req, Session session)
            throws OwsExceptionReport {
        // check is named queries are supported
        if (checkForNamedQueries(req, session)) {
            return executeNamedQuery(req, session);
        }
        // check if series mapping is supporte
        else if (EntitiyHelper.getInstance().isSeriesSupported()) {
            return querySeriesDataAvailabilities(req, session);
        } else {
            Criteria c = getDefaultObservationInfoCriteria(session);

            if (req.isSetFeaturesOfInterest()) {
                c.createCriteria(ContextualReferencedObservation.FEATURE_OF_INTEREST)
                        .add(Restrictions.in(FeatureOfInterest.IDENTIFIER, req.getFeaturesOfInterest()));
            }
            if (req.isSetProcedures()) {
                c.createCriteria(ContextualReferencedObservation.PROCEDURE)
                        .add(Restrictions.in(Procedure.IDENTIFIER, req.getProcedures()));

            }
            if (req.isSetObservedProperties()) {
                c.createCriteria(ContextualReferencedObservation.OBSERVABLE_PROPERTY)
                        .add(Restrictions.in(ObservableProperty.IDENTIFIER, req.getObservedProperties()));
            }

            if (req.isSetOfferings()) {
                c.createCriteria(ContextualReferencedLegacyObservation.OFFERINGS)
                        .add(Restrictions.in(Offering.IDENTIFIER, req.getOfferings()));
            }

            ProjectionList projectionList = Projections.projectionList();
            projectionList.add(Projections.groupProperty(ContextualReferencedObservation.PROCEDURE))
                    .add(Projections.groupProperty(ContextualReferencedObservation.OBSERVABLE_PROPERTY))
                    .add(Projections.groupProperty(ContextualReferencedObservation.FEATURE_OF_INTEREST))
                    .add(Projections.min(ContextualReferencedObservation.PHENOMENON_TIME_START))
                    .add(Projections.max(ContextualReferencedObservation.PHENOMENON_TIME_END));
            if (isShowCount(req)) {
                projectionList.add(Projections.rowCount());
            }
            c.setProjection(projectionList);
            c.setResultTransformer(new DataAvailabilityTransformer(session));
            LOGGER.debug("QUERY getDataAvailability(request): {}", HibernateHelper.getSqlString(c));
            List<?> list = c.list();
            if (isIncludeResultTime(req)) {
                for (Object o : list) {
                    DataAvailability dataAvailability = (DataAvailability) o;
                    dataAvailability.setResultTimes(getResultTimesFromObservation(dataAvailability, req, session));
                }
            }
            return list;
        }
    }

    /**
     * Get the result times for the timeseries
     *
     * @param dataAvailability
     *            Timeseries to get result times for
     * @param request
     *            GetDataAvailability request
     * @param session
     *            Hibernate session
     * @return List of result times
     * @throws OwsExceptionReport
     *             if the requested temporal filter is not supported
     */
    @SuppressWarnings("unchecked")
    private List<TimeInstant> getResultTimesFromObservation(DataAvailability dataAvailability,
            GetDataAvailabilityRequest request, Session session) throws OwsExceptionReport {
        Criteria c = getDefaultObservationInfoCriteria(session);
        c.createCriteria(ContextualReferencedObservation.FEATURE_OF_INTEREST).add(
                Restrictions.eq(FeatureOfInterest.IDENTIFIER, dataAvailability.getFeatureOfInterest().getHref()));
        c.createCriteria(ContextualReferencedObservation.PROCEDURE).add(
                Restrictions.eq(Procedure.IDENTIFIER, dataAvailability.getProcedure().getHref()));
        c.createCriteria(ContextualReferencedObservation.OBSERVABLE_PROPERTY).add(
                Restrictions.eq(ObservableProperty.IDENTIFIER, dataAvailability.getObservedProperty().getHref()));
        if (request.isSetOfferings()) {
            c.createCriteria(ContextualReferencedLegacyObservation.OFFERINGS).add(
                    Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }
        if (hasPhenomenonTimeFilter(request.getExtensions())) {
            c.add(TemporalRestrictions.filter(getPhenomenonTimeFilter(request.getExtensions())));
        }
        c.setProjection(Projections.distinct(Projections.property(ContextualReferencedObservation.RESULT_TIME)));
        c.addOrder(Order.asc(ContextualReferencedObservation.RESULT_TIME));
        LOGGER.debug("QUERY getResultTimesFromObservation({}): {}", HibernateHelper.getSqlString(c));
        List<TimeInstant> resultTimes = Lists.newArrayList();
        for (Date date : (List<Date>) c.list()) {
            resultTimes.add(new TimeInstant(date));
        }
        return resultTimes;
    }

    private Criteria getDefaultObservationInfoCriteria(Session session) {
        return session.createCriteria(ContextualReferencedObservation.class)
                .add(Restrictions.eq(ContextualReferencedObservation.DELETED, false))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
    }

    /**
     * GetDataAvailability processing is series mapping is supported.
     *
     * @param request
     *            GetDataAvailability request
     * @param session
     *            Hibernate session
     * @return List of valid data availability information
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private List<?> querySeriesDataAvailabilities(GetDataAvailabilityRequest request, Session session)
            throws OwsExceptionReport {
        GdaRequestContext context = new GdaRequestContext()
                .setRequest(request)
                .setSeriesObservationDAO(getSeriesObservationDAO())
                .setSupportsSeriesObservationTime(EntitiyHelper.getInstance().isSeriesObservationTimeSupported());
        boolean gdaV20 = checkForGDAv20(request);
        List<Series> resultSeries = DaoFactory.getInstance().getSeriesDAO().getSeries(request, session);
        Map<Long, List<SeriesMetadata>> metadata = null;
        if (resultSeries != null && !resultSeries.isEmpty() && gdaV20 && HibernateHelper.isEntitySupported(SeriesMetadata.class)) {
            metadata = new SeriesMetadataDAO().getMetadata(resultSeries, session);
        } else {
            metadata = new LinkedHashMap<>();
        }
        for (final Series series : resultSeries) {
            if (gdaV20) {
                context.setMinMaxTransformer(new SeriesOfferingMinMaxTransformer())
                .setSupportsNamedQuery(HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_OFFERING_DATA_AVAILABILITY_FOR_SERIES, session));
                processDataAvailabilityForEachOffering(series, context, metadata, session);
            } else {
                context.setMinMaxTransformer(new SeriesMinMaxTransformer())
                .setSupportsNamedQuery(HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_SERIES, session));
                processDataAvailability(series, context, session);
            }
        }
        if (gdaV20) {
            return context.getDataAvailabilityList();
        }
        return checkForDuplictation(context.getDataAvailabilityList());
    }

    private boolean checkForGDAv20(GetDataAvailabilityRequest request) {
        return (request.isSetResponseFormat() && GetDataAvailabilityConstants.NS_GDA_20.equals(request.getResponseFormat()))
                || GetDataAvailabilityConstants.NS_GDA_20.equals(request.getNamespace()) || isForceGDAv20Response();
    }

    /**
     * Get {@link DataAvailability}s for each offering of a series
     * 
     * @param series
     *            Series to get {@link DataAvailability}s for
     * @param context
     *            Request context to get {@link DataAvailability}s
     * @param metadata 
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void processDataAvailabilityForEachOffering(Series series, GdaRequestContext context, Map<Long, List<SeriesMetadata>> metadata, Session session) throws OwsExceptionReport {
        long start = System.currentTimeMillis();
        List<OfferingMinMaxTime> offeringTimePeriodList = null;
        if (series.isSetOffering() && series.isSetFirstLastTime()) {
            offeringTimePeriodList = Lists.newArrayList();
            offeringTimePeriodList.add(new OfferingMinMaxTime().setOffering(series.getOffering().getIdentifier())
                    .setTimePeriod(new TimePeriod(series.getFirstTimeStamp(), series.getLastTimeStamp())));
        } else {
            if (context.isSupportsNamedQuery()) {
                offeringTimePeriodList = getOfferingTimePeriodFromNamedQuery(series.getSeriesId(), context.getMinMaxTransformer(), session);
            } else if (offeringTimePeriodList == null && context.isSupportsSeriesObservationTime()) {
                SeriesObservationTimeDAO seriesObservationTimeDAO =
                        (SeriesObservationTimeDAO) DaoFactory.getInstance().getObservationTimeDAO();
                offeringTimePeriodList = getOfferingTimePeriodFromSeriesGetDataAvailability(seriesObservationTimeDAO,
                        series, context.getRequest(), context.getMinMaxTransformer(), session);
            }
            // get time information from SeriesObservation
            else if (offeringTimePeriodList == null) {
                offeringTimePeriodList = getOfferingTimePeriodFromSeriesObservation(context.getSeriesObservationDAO(),
                        series, context.getRequest(), context.getMinMaxTransformer(), session);
            }
        }
        if (CollectionHelper.isNotEmpty(offeringTimePeriodList)) {
            for (OfferingMinMaxTime ommt : offeringTimePeriodList) {
                if (ommt != null && !ommt.isEmpty()) {
                    DataAvailability dataAvailability =
                            new DataAvailability(getProcedureReference(series, context.getProcedures()), getObservedPropertyReference(
                                    series, context.getObservableProperties()), getFeatureOfInterestReference(series,
                                            context.getFeaturesOfInterest(), session), ommt.getTimePeriod());
                    if (isShowCount(context.getRequest())) {
                        dataAvailability.setCount(getCountFor(series, context.getRequest(), session));
                    }
                    if (isIncludeResultTime(context.getRequest())) {
                        dataAvailability.setResultTimes(getResultTimesFromSeriesObservation(context.getSeriesObservationDAO(), series,
                                context.getRequest(), session));
                    }
                    dataAvailability.setOffering(getOfferingReference(series, context.getOfferings(), ommt.getOffering(), session));
                    dataAvailability.setFormatDescriptor(getFormatDescriptor(ommt.getOffering(), context, series));
                    checkForMetadataExtension(dataAvailability, series, metadata, session);
                    context.addDataAvailability(dataAvailability);
                }
            }
        }
        checkForParentOfferings(context);
        LOGGER.trace("Processing of data availability for seriesid {} took {} ms", series.getSeriesId(), (System.currentTimeMillis() - start));
    }

    private void checkForMetadataExtension(DataAvailability dataAvailability, Series series, Map<Long, List<SeriesMetadata>> metadata, Session session) {
        if (metadata != null && !metadata.isEmpty()) {
            List<SeriesMetadata> metadataList = metadata.get(series.getSeriesId());
            if (CollectionHelper.isNotEmpty(metadataList)) {
                for (SeriesMetadata seriesMetadata : metadataList) {
                    dataAvailability.addMetadata(seriesMetadata.getDomain(), new NamedValue<>(new ReferenceType(seriesMetadata.getIdentifier()),
                            new ReferenceValue(new ReferenceType(seriesMetadata.getValue()))));
                }
            }
        }
    }

    private void checkForParentOfferings(GdaRequestContext context) {
        if (context.isSetDataAvailabilityList()) {
            List<String> requestedOfferings = context.getRequest().getOfferings();
            for (String requestedOffering : requestedOfferings) {
                Set<String> childOfferings = getCache().getChildOfferings(requestedOffering, true, false);
                if (!childOfferings.isEmpty()) {
                    if (context.hasDataAvailability(requestedOffering)) {
                        Set<DataAvailability> parentDataAvailabilities =
                                context.getDataAvailability(requestedOffering);
                        for (String childOffering : childOfferings) {
                            Set<DataAvailability> childDataAvailabilities = context.getDataAvailability(childOffering);
                            for (DataAvailability childDataAvailability : childDataAvailabilities) {
                                for (DataAvailability parentDataAvailability : parentDataAvailabilities) {
                                    parentDataAvailability.merge(childDataAvailability, true);
                                }
                            }
                        }
                    } else {
                        Set<DataAvailability> parentDataAvailabilities = Sets.newHashSet();
                        for (String childOffering : childOfferings) {
                            Set<DataAvailability> childDataAvailabilities = context.getDataAvailability(childOffering);
                            for (DataAvailability childDataAvailability : childDataAvailabilities) {
                                addParentDataAvailabilityIfMissing(parentDataAvailabilities, childDataAvailability,
                                        new ReferenceType(requestedOffering));
                                for (DataAvailability parentDataAvailability : parentDataAvailabilities) {
                                    parentDataAvailability.merge(childDataAvailability, true);
                                }
                            }
                        }
                        context.addDataAvailabilities(parentDataAvailabilities);
                    }
                }
            }
        }
    }

    private List<DataAvailability> checkForDuplictation(List<DataAvailability> dataAvailabilityValues) {
        List<DataAvailability> checked = Lists.newLinkedList();
        for (DataAvailability dataAvailability : dataAvailabilityValues) {
            if (checked.isEmpty()) {
                checked.add(dataAvailability);
            } else {
                boolean notDuplicated = true;
                for (DataAvailability checkedDA : checked) {
                    if (dataAvailability.equals(checkedDA)) {
                        checkedDA.getPhenomenonTime().extendToContain(dataAvailability.getPhenomenonTime());
                        notDuplicated = false;
                    }
                }
                if (notDuplicated) {
                    checked.add(dataAvailability);
                }
            }
        }
        return checked;
    }

    private boolean addParentDataAvailabilityIfMissing(Set<DataAvailability> parentDataAvailabilities,
            DataAvailability childDataAvailability, ReferenceType offering) {
        boolean notContained = true;
        for (DataAvailability parentDataAvailability : parentDataAvailabilities) {
            if (parentDataAvailability.sameConstellation(childDataAvailability)) {
                notContained = false;
            }
        }
        if (notContained) {
            parentDataAvailabilities.add(childDataAvailability.clone(offering));
        }
        return notContained;
    }

    @SuppressWarnings("unchecked")
    private List<OfferingMinMaxTime> getOfferingTimePeriodFromNamedQuery(long seriesId,
            ResultTransformer minMaxTransformer, Session session) {
        Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_SERIES);
        namedQuery.setParameter(ContextualReferencedSeriesObservation.SERIES, seriesId);
        LOGGER.debug("QUERY getOfferingTimePeriodFromNamedQuery(series) with NamedQuery: {}", namedQuery);
        namedQuery.setResultTransformer(minMaxTransformer);
        return (List<OfferingMinMaxTime>) namedQuery.list();
    }

    @SuppressWarnings("unchecked")
    private List<OfferingMinMaxTime> getOfferingTimePeriodFromSeriesGetDataAvailability(
            SeriesObservationTimeDAO seriesGetDataAvailabilityDAO, Series series, GetDataAvailabilityRequest request,
            ResultTransformer minMaxTransformer, Session session) {
        Criteria criteria =
                seriesGetDataAvailabilityDAO.getOfferingMinMaxTimeCriteriaForSeriesGetDataAvailabilityDAO(series,
                        request.getOfferings(), session);
        criteria.setResultTransformer(minMaxTransformer);
        LOGGER.debug("QUERY getOfferingTimePeriodFromSeriesGetDataAvailability(series, offerings): {}", HibernateHelper.getSqlString(criteria));
        return (List<OfferingMinMaxTime>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    private List<OfferingMinMaxTime> getOfferingTimePeriodFromSeriesObservation(
            AbstractSeriesObservationDAO seriesObservationDAO, Series series, GetDataAvailabilityRequest request,
            ResultTransformer minMaxTransformer, Session session) {
        Criteria criteria =
                seriesObservationDAO
                        .getOfferingMinMaxTimeCriteriaForSeriesObservation(series, request.getOfferings(), session);
        criteria.setResultTransformer(minMaxTransformer);
        LOGGER.debug("QUERY getOfferingTimePeriodFromSeriesObservation(series, offerings): {}", HibernateHelper.getSqlString(criteria));
        return (List<OfferingMinMaxTime>) criteria.list();
    }

    /**
     * Get {@link DataAvailability}s for each series
     * 
     * @param series
     *            Series to get {@link DataAvailability}s for
     * @param context
     *            Request context to get {@link DataAvailability}s
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void processDataAvailability(Series series, GdaRequestContext context, Session session) throws OwsExceptionReport {
        TimePeriod timePeriod = null;
        if (!context.getRequest().isSetOfferings()) {
            // get time information from series object
            if (series.isSetFirstLastTime()) {
                timePeriod = new TimePeriod(series.getFirstTimeStamp(), series.getLastTimeStamp());
            }
            // get time information from a named query
            else if (timePeriod == null && context.isSupportsNamedQuery()) {
                timePeriod = getTimePeriodFromNamedQuery(series.getSeriesId(), context.getMinMaxTransformer(), session);
            }
        }
        // get time information from SeriesGetDataAvailability mapping if
        // supported
        if (timePeriod == null && context.isSupportsSeriesObservationTime()) {
            SeriesObservationTimeDAO seriesObservationTimeDAO =
                    (SeriesObservationTimeDAO) DaoFactory.getInstance().getObservationTimeDAO();

            timePeriod =
                    getTimePeriodFromSeriesGetDataAvailability(seriesObservationTimeDAO, series, context.getRequest(),
                            context.getMinMaxTransformer(), session);
        }
        // get time information from SeriesObservation
        else if (timePeriod == null) {
            timePeriod =
                    getTimePeriodFromSeriesObservation(context.getSeriesObservationDAO(), series, context.getRequest(),
                            context.getMinMaxTransformer(), session);
        }
        // create DataAvailabilities
        if (timePeriod != null && !timePeriod.isEmpty()) {
            DataAvailability dataAvailability =
                    new DataAvailability(getProcedureReference(series, context.getProcedures()), getObservedPropertyReference(
                            series, context.getObservableProperties()), getFeatureOfInterestReference(series,
                                    context.getFeaturesOfInterest(), session), timePeriod);
            if (isShowCount(context.getRequest())) {
                dataAvailability.setCount(getCountFor(series, context.getRequest(), session));
            }
            if (isIncludeResultTime(context.getRequest())) {
                dataAvailability.setResultTimes(getResultTimesFromSeriesObservation(context.getSeriesObservationDAO(), series,
                        context.getRequest(), session));
            }
            context.addDataAvailability(dataAvailability);
        }
    }

    /**
     * Get time information from a named query
     *
     * @param seriesId
     *            Series id
     * @param seriesMinMaxTransformer
     *            Hibernate result transformator for min/max time value
     * @param session
     *            Hibernate Session
     * @return Time period
     */
    private TimePeriod getTimePeriodFromNamedQuery(long seriesId, ResultTransformer seriesMinMaxTransformer,
            Session session) {
        Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_SERIES);
        namedQuery.setParameter(ContextualReferencedSeriesObservation.SERIES, seriesId);
        LOGGER.debug("QUERY getTimePeriodFromNamedQuery(series) with NamedQuery: {}", namedQuery);
        namedQuery.setResultTransformer(seriesMinMaxTransformer);
        return (TimePeriod) namedQuery.uniqueResult();
    }

    /**
     * Get time information from SeriesGetDataAvailability mapping
     *
     * @param seriesGetDataAvailabilityDAO
     *            Series GetDataAvailability DAO class
     * @param series
     *            Series to get information for
     * @param request
     * @param seriesMinMaxTransformer
     *            Hibernate result transformator for min/max time value
     * @param session
     *            Hibernate Session
     * @return Time period
     */
    private TimePeriod getTimePeriodFromSeriesGetDataAvailability(
            SeriesObservationTimeDAO seriesGetDataAvailabilityDAO, Series series, GetDataAvailabilityRequest request,
            ResultTransformer seriesMinMaxTransformer, Session session) {
        Criteria criteria =
                seriesGetDataAvailabilityDAO.getMinMaxTimeCriteriaForSeriesGetDataAvailabilityDAO(series,
                        request.getOfferings(), session);
        criteria.setResultTransformer(seriesMinMaxTransformer);
        LOGGER.debug("QUERY getTimePeriodFromSeriesObservation(series): {}", HibernateHelper.getSqlString(criteria));
        return (TimePeriod) criteria.uniqueResult();
    }

    /**
     * Get time information from SeriesObservation
     *
     * @param seriesObservationDAO
     *            Series observation DAO class
     * @param series
     *            Series to get information for
     * @param request
     * @param seriesMinMaxTransformer
     *            Hibernate result transformator for min/max time value
     * @param session
     *            Hibernate Session
     * @return Time period
     */
    private TimePeriod getTimePeriodFromSeriesObservation(AbstractSeriesObservationDAO seriesObservationDAO,
            Series series, GetDataAvailabilityRequest request, ResultTransformer seriesMinMaxTransformer,
            Session session) {
        Criteria criteria =
                seriesObservationDAO
                        .getMinMaxTimeCriteriaForSeriesObservation(series, request.getOfferings(), session);
        criteria.setResultTransformer(seriesMinMaxTransformer);
        LOGGER.debug("QUERY getTimePeriodFromSeriesObservation(series): {}", HibernateHelper.getSqlString(criteria));
        return (TimePeriod) criteria.uniqueResult();
    }

    /**
     * Get the result times for the timeseries
     *
     * @param seriesObservationDAO
     *            DAO
     * @param series
     *            time series
     * @param request
     *            GetDataAvailability request
     * @param session
     *            Hibernate session
     * @return List of result times
     * @throws OwsExceptionReport
     *             if the requested temporal filter is not supported
     */
    private List<TimeInstant> getResultTimesFromSeriesObservation(AbstractSeriesObservationDAO seriesObservationDAO,
            Series series, GetDataAvailabilityRequest request, Session session) throws OwsExceptionReport {
        Criterion filter = null;
        if (hasPhenomenonTimeFilter(request.getExtensions())) {
            filter = TemporalRestrictions.filter(getPhenomenonTimeFilter(request.getExtensions()));
        }
        List<Date> dateTimes =
                seriesObservationDAO.getResultTimesForSeriesObservation(series, request.getOfferings(), filter,
                        session);
        List<TimeInstant> resultTimes = Lists.newArrayList();
        for (Date date : dateTimes) {
            resultTimes.add(new TimeInstant(date));
        }
        return resultTimes;
    }

    /**
     * Get count of available observation for this time series
     *
     * @param series
     *            Time series
     * @param request
     *            GetDataAvailability request
     * @param session
     *            Hibernate session
     * @return Count of available observations
     */
    private Long getCountFor(Series series, GetDataAvailabilityRequest request, Session session) {
        Criteria criteria = session.createCriteria(TemporalReferencedSeriesObservation.class)
        		.add(Restrictions.eq(TemporalReferencedSeriesObservation.DELETED, false));
        criteria.add(Restrictions.eq(TemporalReferencedSeriesObservation.SERIES, series));
//        if (request.isSetOfferings()) {
//            criteria.createCriteria(TemporalReferencedSeriesObservation.OFFERINGS)
//                    .add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
//        }
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    private FormatDescriptor getFormatDescriptor(String offering, GdaRequestContext context, Series series) {
        return new FormatDescriptor(
                new ProcedureDescriptionFormatDescriptor(
                        series.getProcedure().getProcedureDescriptionFormat().getProcedureDescriptionFormat()),
                getObservationFormatDescriptors(offering, context));
    }

    private Set<ObservationFormatDescriptor> getObservationFormatDescriptors(String offering, GdaRequestContext context) {
        Map<String, Set<String>> responsFormatObservationTypesMap = Maps.newHashMap();
        for (String observationType : getCache().getAllObservationTypesForOffering(offering)) {
            Set<String> responseFormats = CodingRepository.getInstance().getResponseFormatsForObservationType(observationType, context.getRequest().getService(), context.getRequest().getVersion());
            for (String responseFormat : responseFormats) {
                if (responsFormatObservationTypesMap.containsKey(responseFormat)) {
                    responsFormatObservationTypesMap.get(responseFormat).add(observationType);
                } else {
                    responsFormatObservationTypesMap.put(responseFormat, Sets.newHashSet(observationType));
                }
            }
        }
        Set<ObservationFormatDescriptor> formatDescriptors = Sets.newHashSet();
        for (String responsFormat : responsFormatObservationTypesMap.keySet()) {
            formatDescriptors.add(new ObservationFormatDescriptor(responsFormat, responsFormatObservationTypesMap.get(responsFormat)));
        }
        return formatDescriptors;
    }

    private boolean checkForNamedQueries(GetDataAvailabilityRequest req, Session session) {
        final boolean features = req.isSetFeaturesOfInterest();
        final boolean observableProperties = req.isSetObservedProperties();
        final boolean procedures = req.isSetProcedures();
        // all
        if (features && observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(
                    SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES_PROCEDURES_OBSERVED_PROPERTIES, session);
        }
        // observableProperties and procedures
        else if (!features && observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(
                    SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURES_OBSERVED_PROPERTIES, session);
        }
        // only observableProperties
        else if (!features && observableProperties && !procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_OBSERVED_PROPERTIES,
                    session);
        }
        // only procedures
        else if (!features && !observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURES, session);
        }
        // features and observableProperties
        else if (features && observableProperties && !procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES_OBSERVED_PROPERTIES, session);
        }
        // features and procedures
        else if (features && !observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES_PROCEDURES,
                    session);
        }
        // only features
        else if (features && !observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES, session);
        }
        return false;
    }

    private List<?> executeNamedQuery(GetDataAvailabilityRequest req, Session session) {
        final boolean features = req.isSetFeaturesOfInterest();
        final boolean observableProperties = req.isSetObservedProperties();
        final boolean procedures = req.isSetProcedures();
        String namedQueryName = null;
        Map<String, Collection<String>> parameter = Maps.newHashMap();
        // all
        if (features && observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES_PROCEDURES_OBSERVED_PROPERTIES;
            parameter.put(FEATURES, req.getFeaturesOfInterest());
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // observableProperties and procedures
        else if (!features && observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURES_OBSERVED_PROPERTIES;
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // only observableProperties
        else if (!features && observableProperties && !procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_OBSERVED_PROPERTIES;
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
        }
        // only procedures
        else if (!features && !observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_PROCEDURES;
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // features and observableProperties
        else if (features && observableProperties && !procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES_OBSERVED_PROPERTIES;
            parameter.put(FEATURES, req.getFeaturesOfInterest());
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
        }
        // features and procedures
        else if (features && !observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES_PROCEDURES;
            parameter.put(FEATURES, req.getFeaturesOfInterest());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // only features
        else if (features && !observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES;
            parameter.put(FEATURES, req.getFeaturesOfInterest());
        }
        if (StringHelper.isNotEmpty(namedQueryName)) {
            Query namedQuery = session.getNamedQuery(namedQueryName);
            for (String key : parameter.keySet()) {
                namedQuery.setParameterList(key, parameter.get(key));
            }
            LOGGER.debug("QUERY getProceduresForFeatureOfInterest(feature) with NamedQuery: {}", namedQuery);
            namedQuery.setResultTransformer(new DataAvailabilityTransformer(session));
            return namedQuery.list();
        }
        return Lists.newLinkedList();
    }

    private ReferenceType getProcedureReference(Series series, Map<String, ReferenceType> procedures) {
        String identifier = series.getProcedure().getIdentifier();
        if (!procedures.containsKey(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            if (series.getProcedure().isSetName()) {
                referenceType.setTitle(series.getProcedure().getName());
            }
            procedures.put(identifier, referenceType);
        }
        return procedures.get(identifier);
    }

    private ReferenceType getObservedPropertyReference(Series series, Map<String, ReferenceType> observableProperties) {
        String identifier = series.getObservableProperty().getIdentifier();
        if (!observableProperties.containsKey(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            if (series.getObservableProperty().isSetName()) {
                referenceType.setTitle(series.getObservableProperty().getName());
            }
            observableProperties.put(identifier, referenceType);
        }
        return observableProperties.get(identifier);
    }

    private ReferenceType getFeatureOfInterestReference(Series series, Map<String, ReferenceType> featuresOfInterest,
            Session session) throws OwsExceptionReport {
        String identifier = series.getFeatureOfInterest().getIdentifier();
        if (!featuresOfInterest.containsKey(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject();
            queryObject.setFeature(series.getFeatureOfInterest()).setConnection(session)
                    .setVersion(Sos2Constants.SERVICEVERSION);
            AbstractFeature feature = Configurator.getInstance().getFeatureQueryHandler().getFeatureByID(queryObject);
            if (feature.isSetName() && feature.getFirstName().isSetValue()) {
                referenceType.setTitle(feature.getFirstName().getValue());
            }
            featuresOfInterest.put(identifier, referenceType);
        }
        return featuresOfInterest.get(identifier);
    }
    
    private ReferenceType getOfferingReference(Series series, Map<String, ReferenceType> offerings, String offering,
            Session session) throws OwsExceptionReport {
        if (!offerings.containsKey(offering)) {
            ReferenceType referenceType = new ReferenceType(offering);
            if (series.isSetOffering() && series.getOffering().isSetName()) {
                referenceType.setTitle(series.getOffering().getName());
            }
            offerings.put(offering, referenceType);
        }
        return offerings.get(offering);
    }

    /**
     * Check if optional count should be added
     *
     * @param request
     *            GetDataAvailability request
     * @return <code>true</code>, if optional count should be added
     */
    private boolean isShowCount(GetDataAvailabilityRequest request) {
        if (request.isSetExtensions()) {
            return request.getExtensions().isBooleanExtensionSet(SHOW_COUNT);
        }
        return isForceValueCount();
    }

    /**
     * Check if result times should be added
     *
     * @param request
     *            GetDataAvailability request
     * @return <code>true</code>, if result times should be added
     */
    private boolean isIncludeResultTime(GetDataAvailabilityRequest request) {
        if (request.isSetExtensions()) {
            return request.getExtensions().isBooleanExtensionSet(INCLUDE_RESULT_TIMES)
                    || hasPhenomenonTimeFilter(request.getExtensions());
        }
        return false;
    }

    /**
     * Check if extensions contains a temporal filter with valueReference
     * phenomenonTime
     *
     * @param extensions
     *            Extensions to check
     * @return <code>true</code>, if extensions contains a temporal filter with
     *         valueReference phenomenonTime
     */
    private boolean hasPhenomenonTimeFilter(SwesExtensions extensions) {
        boolean hasFilter = false;
        for (SwesExtension<?> extension : extensions.getExtensions()) {
            if (extension.getValue() instanceof TemporalFilter) {
                TemporalFilter filter = (TemporalFilter) extension.getValue();
                if (TemporalRestrictions.PHENOMENON_TIME_VALUE_REFERENCE.equals(filter.getValueReference())) {
                    hasFilter = true;
                }
            }
        }
        return hasFilter;
    }

    /**
     * Get the temporal filter with valueReference phenomenonTime from
     * extensions
     *
     * @param extensions
     *            To get filter from
     * @return Temporal filter with valueReference phenomenonTime
     */
    private TemporalFilter getPhenomenonTimeFilter(SwesExtensions extensions) {
        for (SwesExtension<?> extension : extensions.getExtensions()) {
            if (extension.getValue() instanceof TemporalFilter) {
                TemporalFilter filter = (TemporalFilter) extension.getValue();
                if (TemporalRestrictions.PHENOMENON_TIME_VALUE_REFERENCE.equals(filter.getValueReference())) {
                    return filter;
                }
            }
        }
        return null;
    }

    protected AbstractSeriesObservationDAO getSeriesObservationDAO() throws OwsExceptionReport {
        AbstractObservationDAO observationDAO = DaoFactory.getInstance().getObservationDAO();
        if (observationDAO instanceof AbstractSeriesObservationDAO) {
            return (AbstractSeriesObservationDAO) observationDAO;
        } else {
            throw new NoApplicableCodeException().withMessage("The required '%s' implementation is no supported!",
                    AbstractObservationDAO.class.getName());
        }
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    public class GdaRequestContext {

        private GetDataAvailabilityRequest request;
        private boolean seriesObservationTimeSupported;
        private boolean namedQuerySupported;
        private ResultTransformer minMaxTransformer;
        private AbstractSeriesObservationDAO seriesObservationDAO;
        private List<DataAvailability> dataAvailabilityValues = Lists.newArrayList();
        private Map<String, ReferenceType> procedures = new HashMap<>();
        private Map<String, ReferenceType> observableProperties = new HashMap<>();
        private Map<String, ReferenceType> featuresOfInterest = new HashMap<>();
        private Map<String, ReferenceType> offerings = new HashMap<>();

        public GdaRequestContext setRequest(GetDataAvailabilityRequest request) {
            this.request = request;
            return this;
        }

        public Map<String, ReferenceType> getFeaturesOfInterest() {
            return featuresOfInterest;
        }

        public Map<String, ReferenceType> getObservableProperties() {
            return observableProperties;
        }

        public Map<String, ReferenceType> getProcedures() {
            return procedures;
        }
        
        public Map<String, ReferenceType> getOfferings() {
            return offerings;
        }

        public GdaRequestContext setDataAvailabilityList(List<DataAvailability> dataAvailabilityValues) {
            this.dataAvailabilityValues.clear();
            return addDataAvailabilities(dataAvailabilityValues);
        }

        public GdaRequestContext addDataAvailability(DataAvailability dataAvailability) {
            if (dataAvailability != null) {
                this.dataAvailabilityValues.add(dataAvailability);
            }
            return this;
        }

        public GdaRequestContext addDataAvailabilities(Collection<DataAvailability> dataAvailabilityValues) {
            if (dataAvailabilityValues != null) {
                this.dataAvailabilityValues.addAll(dataAvailabilityValues);
            }
            return this;
        }

        public GdaRequestContext setSupportsSeriesObservationTime(boolean seriesObservationTimeSupported) {
            this.seriesObservationTimeSupported = seriesObservationTimeSupported;
            return this;
        }

        public GdaRequestContext setSupportsNamedQuery(boolean namedQuerySupported) {
            this.namedQuerySupported = namedQuerySupported;
            return this;
        }

        public GdaRequestContext setMinMaxTransformer(ResultTransformer minMaxTransformer) {
            this.minMaxTransformer = minMaxTransformer;
            return this;
        }

        public GdaRequestContext setSeriesObservationDAO(AbstractSeriesObservationDAO seriesObservationDAO) {
            this.seriesObservationDAO = seriesObservationDAO;
            return this;
        }

        public GetDataAvailabilityRequest getRequest() {
            return request;
        }
        
        public List<DataAvailability> getDataAvailabilityList() {
            return Lists.newArrayList(dataAvailabilityValues);
        }
        
        public boolean hasDataAvailability(String requestedOffering) {
            for (DataAvailability dataAvailability : dataAvailabilityValues) {
                if (requestedOffering.equals(dataAvailability.getOfferingString())) {
                    return true;
                }
            }
            return false;
        }

        public Set<DataAvailability> getDataAvailability(String offering) {
            Set<DataAvailability> das = Sets.newHashSet();
            for (DataAvailability dataAvailability : dataAvailabilityValues) {
                if (offering.equals(dataAvailability.getOfferingString())) {
                    das.add(dataAvailability);
                }
            }
            return das;
        }

        public boolean isSetDataAvailabilityList() {
            return CollectionHelper.isNotEmpty(getDataAvailabilityList());
        }

        public boolean isSupportsSeriesObservationTime() {
            return seriesObservationTimeSupported;
        }

        public boolean isSupportsNamedQuery() {
            return namedQuerySupported;
        }

        public ResultTransformer getMinMaxTransformer() {
            return minMaxTransformer;
        }

        public AbstractSeriesObservationDAO getSeriesObservationDAO() {
            return seriesObservationDAO;
        }
    
    }

    /**
     * Class represents the min/max time for an offering.
     * 
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.4.0
     *
     */
    public class OfferingMinMaxTime {
        private String offering;
        private TimePeriod timePeriod;
    
        public OfferingMinMaxTime setOffering(Object offering) {
            this.offering = (String)offering;
            return this;
        }
        
        public boolean isEmpty() {
            // TODO Auto-generated method stub
            return false;
        }

        public String getOffering() {
            return offering;
        }
    
        public OfferingMinMaxTime setTimePeriod(TimePeriod timePeriod) {
           this.timePeriod = timePeriod;
           return this;
        }
        
        public TimePeriod getTimePeriod() {
            return timePeriod;
        }
    }

    /**
     * Class to transform ResultSets to DataAvailabilities.
     */
    private static class DataAvailabilityTransformer implements ResultTransformer {
        private static final long serialVersionUID = -373512929481519459L;

        private final Logger LOGGER = LoggerFactory.getLogger(DataAvailabilityTransformer.class);

        private final Session session;

        DataAvailabilityTransformer(Session session) {
            this.session = session;
        }

        @Override
        public DataAvailability transformTuple(Object[] tuple, String[] aliases) {
            Map<String, ReferenceType> procedures = new HashMap<>();
            Map<String, ReferenceType> observableProperties = new HashMap<>();
            Map<String, ReferenceType> featuresOfInterest = new HashMap<>();
            if (tuple != null) {
                try {
                    ReferenceType procedure = null;
                    ReferenceType observableProperty = null;
                    ReferenceType featureOfInterest = null;
                    TimePeriod timePeriod = null;
                    long valueCount = -1;
                    if (tuple.length == 5 || tuple.length == 6) {
                        procedure = getProcedureReferenceType(tuple[0], procedures);
                        observableProperty = getObservablePropertyReferenceType(tuple[1], observableProperties);
                        featureOfInterest = getFeatureOfInterestReferenceType(tuple[2], featuresOfInterest);
                        timePeriod = new TimePeriod(tuple[3], tuple[4]);
                        if (tuple.length == 6) {
                            valueCount = (Long) tuple[5];
                        }
                    } else if (tuple.length == 8 || tuple.length == 9) {
                        procedure = getProcedureReferenceType(tuple[0], procedures);
                        addTitleToReferenceType(tuple[1], procedure);
                        observableProperty = getObservablePropertyReferenceType(tuple[2], observableProperties);
                        addTitleToReferenceType(tuple[3], observableProperty);
                        featureOfInterest = getFeatureOfInterestReferenceType(tuple[4], featuresOfInterest);
                        addTitleToReferenceType(tuple[5], featureOfInterest);
                        timePeriod = new TimePeriod(tuple[6], tuple[7]);
                        if (tuple.length == 9) {
                            valueCount = (Long) tuple[8];
                        }
                    }
                    if (timePeriod != null && !timePeriod.isEmpty()) {
                        return new DataAvailability(procedure, observableProperty, featureOfInterest, timePeriod,
                                valueCount);
                    }
                } catch (OwsExceptionReport e) {
                    LOGGER.error("Error while querying GetDataAvailability", e);
                }
            }
            return null;
        }


        private ReferenceType getProcedureReferenceType(Object procedure, Map<String, ReferenceType> procedures)
                throws OwsExceptionReport {
            String identifier = null;
            if (procedure instanceof Procedure) {
                identifier = ((Procedure) procedure).getIdentifier();
            } else if (procedure instanceof String) {
                identifier = (String) procedure;
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "GetDataAvailability procedure query object type {} is not supported!", procedure.getClass()
                                .getName());
            }
            if (!procedures.containsKey(identifier)) {
                ReferenceType referenceType = new ReferenceType(identifier);
                // TODO
                // SosProcedureDescription sosProcedureDescription = new
                // HibernateProcedureConverter().createSosProcedureDescription(procedure,
                // procedure.getProcedureDescriptionFormat().getProcedureDescriptionFormat(),
                // Sos2Constants.SERVICEVERSION, session);
                // if ()
                procedures.put(identifier, referenceType);
            }
            return procedures.get(identifier);
        }

        private ReferenceType getObservablePropertyReferenceType(Object observableProperty,
                Map<String, ReferenceType> observableProperties) throws CodedException {
            String identifier = null;
            if (observableProperty instanceof ObservableProperty) {
                identifier = ((ObservableProperty) observableProperty).getIdentifier();
            } else if (observableProperty instanceof String) {
                identifier = (String) observableProperty;
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "GetDataAvailability procedure query object type {} is not supported!", observableProperty
                                .getClass().getName());
            }
            if (!observableProperties.containsKey(identifier)) {
                ReferenceType referenceType = new ReferenceType(identifier);
                // TODO
                // if (observableProperty.isSetDescription()) {
                // referenceType.setTitle(observableProperty.getDescription());
                // }
                observableProperties.put(identifier, referenceType);
            }
            return observableProperties.get(identifier);
        }

        private ReferenceType getFeatureOfInterestReferenceType(Object featureOfInterest,
                Map<String, ReferenceType> featuresOfInterest) throws OwsExceptionReport {
            String identifier = null;
            if (featureOfInterest instanceof FeatureOfInterest) {
                identifier = ((FeatureOfInterest) featureOfInterest).getIdentifier();
            } else if (featureOfInterest instanceof String) {
                identifier = (String) featureOfInterest;
            } else {
                throw new NoApplicableCodeException().withMessage(
                        "GetDataAvailability procedure query object type {} is not supported!", featureOfInterest
                                .getClass().getName());
            }
            if (!featuresOfInterest.containsKey(identifier)) {
                ReferenceType referenceType = new ReferenceType(identifier);
                FeatureQueryHandlerQueryObject queryObject =
                        new FeatureQueryHandlerQueryObject().addFeatureIdentifier(identifier).setConnection(session)
                                .setVersion(Sos2Constants.SERVICEVERSION);
                AbstractFeature feature =
                        Configurator.getInstance().getFeatureQueryHandler().getFeatureByID(queryObject);
                if (feature.isSetName() && feature.getFirstName().isSetValue()) {
                    referenceType.setTitle(feature.getFirstName().getValue());
                }
                featuresOfInterest.put(identifier, referenceType);
            }
            return featuresOfInterest.get(identifier);
        }

        private void addTitleToReferenceType(Object object, ReferenceType referenceType) {
            if (!referenceType.isSetTitle() && object instanceof String) {
                referenceType.setTitle((String) object);
            }
        }

        @Override
        @SuppressWarnings("rawtypes")
        public List transformList(List collection) {
            return collection;
        }
    }

    /**
     * Class to transform ResultSets to {@link TimePeriod}.
     * 
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.x
     *
     */
    private static class SeriesMinMaxTransformer implements ResultTransformer {
        private static final long serialVersionUID = -373512929481519459L;

        @Override
        public TimePeriod transformTuple(Object[] tuple, String[] aliases) {
            if (tuple != null) {
                return new TimePeriod(tuple[0], tuple[1]);
            }
            return null;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public List transformList(List collection) {
            return collection;
        }
    }
    
    /**
     * Class to transform ResultSets to {@link OfferingMinMaxTime}.
     * 
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.4.0
     *
     */
    private class SeriesOfferingMinMaxTransformer implements ResultTransformer {
        private static final long serialVersionUID = -373512929481519459L;

        @Override
        public OfferingMinMaxTime transformTuple(Object[] tuple, String[] aliases) {
            if (tuple != null) {
                OfferingMinMaxTime offeringMinMaxTime = new OfferingMinMaxTime();
                offeringMinMaxTime.setOffering(tuple[0]);
                offeringMinMaxTime.setTimePeriod(new TimePeriod(tuple[1], tuple[2]));
                 return offeringMinMaxTime;
            }
            return null;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public List transformList(List collection) {
            return collection;
        }
    }
    
    @Override
    public boolean isSupported() {
        return true;
    }

}
