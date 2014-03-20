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
package org.n52.sos.ds.hibernate;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.joda.time.DateTime;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.dao.series.SeriesDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesObservationTimeDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationTime;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationInfo;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.gda.AbstractGetDataAvailabilityDAO;
import org.n52.sos.gda.GetDataAvailabilityRequest;
import org.n52.sos.gda.GetDataAvailabilityResponse;
import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * {@code IGetDataAvailabilityDao} to handle {@link GetDataAvailabilityRequest}
 * s.
 * 
 * @author Christian Autermann
 * @since 4.0.0
 */
public class GetDataAvailabilityDAO extends AbstractGetDataAvailabilityDAO implements HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetDataAvailabilityDAO.class);

    private HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

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

    public GetDataAvailabilityDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public GetDataAvailabilityResponse getDataAvailability(GetDataAvailabilityRequest req) throws OwsExceptionReport {
        Session session = sessionHolder.getSession();
        try {
            List<?> dataAvailabilityValues = queryDataAvailabilityValues(req, session);
            GetDataAvailabilityResponse response = new GetDataAvailabilityResponse();
            response.setService(req.getService());
            response.setVersion(req.getVersion());
            for (Object o : dataAvailabilityValues) {
                response.addDataAvailability((DataAvailability) o);
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
        // check if series mapping is supported
        else if (HibernateHelper.isEntitySupported(Series.class, session)) {
            return querySeriesDataAvailabilities(req, session);
        } else {
            Criteria c =
                    session.createCriteria(ObservationInfo.class).add(Restrictions.eq(ObservationInfo.DELETED, false));

            if (req.isSetFeaturesOfInterest()) {
                c.createCriteria(ObservationInfo.FEATURE_OF_INTEREST).add(
                        Restrictions.in(FeatureOfInterest.IDENTIFIER, req.getFeaturesOfInterest()));
            }
            if (req.isSetProcedures()) {
                c.createCriteria(ObservationInfo.PROCEDURE).add(
                        Restrictions.in(Procedure.IDENTIFIER, req.getProcedures()));

            }
            if (req.isSetObservedProperties()) {
                c.createCriteria(ObservationInfo.OBSERVABLE_PROPERTY).add(
                        Restrictions.in(ObservableProperty.IDENTIFIER, req.getObservedProperties()));
            }
            
            if (req.isSetObservedProperties()) {
                c.createCriteria(ObservationInfo.OFFERINGS).add(
                        Restrictions.in(Offering.IDENTIFIER, req.getObservedProperties()));
            }

            c.setProjection(Projections.projectionList().add(Projections.groupProperty(ObservationInfo.PROCEDURE))
                    .add(Projections.groupProperty(ObservationInfo.OBSERVABLE_PROPERTY))
                    .add(Projections.groupProperty(ObservationInfo.FEATURE_OF_INTEREST))
                    .add(Projections.min(ObservationInfo.PHENOMENON_TIME_START))
                    .add(Projections.max(ObservationInfo.PHENOMENON_TIME_END))
                    .add(Projections.rowCount()));

            c.setResultTransformer(new DataAvailabilityTransformer(session));
            LOGGER.debug("QUERY getDataAvailability(request): {}", HibernateHelper.getSqlString(c));
            return c.list();
        }
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
        List<DataAvailability> dataAvailabilityValues = Lists.newLinkedList();
        Map<String, ReferenceType> procedures = new HashMap<String, ReferenceType>();
        Map<String, ReferenceType> observableProperties = new HashMap<String, ReferenceType>();
        Map<String, ReferenceType> featuresOfInterest = new HashMap<String, ReferenceType>();
        SeriesObservationDAO seriesObservationDAO = new SeriesObservationDAO();
        SeriesMinMaxTransformer seriesMinMaxTransformer = new SeriesMinMaxTransformer();
        boolean supportsNamedQuery =
                HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_SERIES, session);
        boolean supportsSeriesObservationTime =
                HibernateHelper.isEntitySupported(SeriesObservationTime.class, session);
        SeriesObservationTimeDAO seriesObservationTimeDAO = new SeriesObservationTimeDAO();
        for (final Series series : new SeriesDAO().getSeries(request.getProcedures(), request.getObservedProperties(),
                request.getFeaturesOfInterest(), session)) {
            TimePeriod timePeriod = null;
            // get time information from a named query
            if (supportsNamedQuery) {
                timePeriod = getTimePeriodFromNamedQuery(series.getSeriesId(), seriesMinMaxTransformer, session);
            }
            // get time information from SeriesGetDataAvailability mapping if
            // supported
            else if (supportsSeriesObservationTime) {
                timePeriod =
                        getTimePeriodFromSeriesGetDataAvailability(seriesObservationTimeDAO, series, request,
                                seriesMinMaxTransformer, session);
            }
            // get time information from SeriesObservation
            else {
                timePeriod =
                        getTimePeriodFromSeriesObservation(seriesObservationDAO, series, request, seriesMinMaxTransformer,
                                session);
            }
            if (timePeriod != null && !timePeriod.isEmpty()) {
                
                dataAvailabilityValues.add(new DataAvailability(getProcedureReference(series, procedures),
                        getObservedPropertyReference(series, observableProperties), getFeatureOfInterestReference(
                                series, featuresOfInterest, session), timePeriod, getValueCountFor(series, request, session)));
            }
        }
        return dataAvailabilityValues;
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
    private TimePeriod getTimePeriodFromNamedQuery(long seriesId, SeriesMinMaxTransformer seriesMinMaxTransformer,
            Session session) {
        Query namedQuery = session.getNamedQuery(SQL_QUERY_GET_DATA_AVAILABILITY_FOR_SERIES);
        namedQuery.setParameter(SeriesObservationInfo.SERIES, seriesId);
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
            SeriesObservationTimeDAO seriesGetDataAvailabilityDAO, Series series,
            GetDataAvailabilityRequest request, SeriesMinMaxTransformer seriesMinMaxTransformer, Session session) {
        Criteria criteria = 
                    seriesGetDataAvailabilityDAO.getMinMaxTimeCriteriaForSeriesGetDataAvailabilityDAO(series, request.getOfferings(), session);
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
    private TimePeriod getTimePeriodFromSeriesObservation(SeriesObservationDAO seriesObservationDAO, Series series,
            GetDataAvailabilityRequest request, SeriesMinMaxTransformer seriesMinMaxTransformer, Session session) {
        Criteria criteria = seriesObservationDAO.getMinMaxTimeCriteriaForSeriesObservation(series, request.getOfferings(), session);
        criteria.setResultTransformer(seriesMinMaxTransformer);
        LOGGER.debug("QUERY getTimePeriodFromSeriesObservation(series): {}", HibernateHelper.getSqlString(criteria));
        return (TimePeriod) criteria.uniqueResult();
    }

    private Long getValueCountFor(Series series, GetDataAvailabilityRequest request, Session session) {
        Criteria criteria = session.createCriteria(SeriesObservationInfo.class);
        criteria.add(Restrictions.eq(SeriesObservationInfo.SERIES, series));
        if (request.isSetOfferings()) {
            criteria.createCriteria(SeriesObservationTime.OFFERINGS).add(Restrictions.in(Offering.IDENTIFIER, request.getOfferings()));
        }
        criteria.setProjection(Projections.rowCount());
        return (Long)criteria.uniqueResult();
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
            return HibernateHelper.isNamedQuerySupported(
                    SQL_QUERY_GET_DATA_AVAILABILITY_FOR_FEATURES_OBSERVED_PROPERTIES, session);
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

    private ReferenceType getObservedPropertyReference(Series series, Map<String, ReferenceType> observableProperties) {
        String identifier = series.getObservableProperty().getIdentifier();
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

    private ReferenceType getFeatureOfInterestReference(Series series, Map<String, ReferenceType> featuresOfInterest,
            Session session) throws OwsExceptionReport {
        String identifier = series.getFeatureOfInterest().getIdentifier();
        if (!featuresOfInterest.containsKey(identifier)) {
            ReferenceType referenceType = new ReferenceType(identifier);
            FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject();
            queryObject.addFeatureIdentifier(identifier).setConnection(session).setVersion(Sos2Constants.SERVICEVERSION);
            AbstractFeature feature =
                    Configurator.getInstance().getFeatureQueryHandler().getFeatureByID(queryObject);
            if (feature.isSetName() && feature.getFirstName().isSetValue()) {
                referenceType.setTitle(feature.getFirstName().getValue());
            }
            featuresOfInterest.put(identifier, referenceType);
        }
        return featuresOfInterest.get(identifier);
    }

    /**
     * Class to transform ResultSets to DataAvailabilities.
     */
    private static class DataAvailabilityTransformer implements ResultTransformer {
        private static final long serialVersionUID = -373512929481519459L;

        private final Logger LOGGER = LoggerFactory.getLogger(DataAvailabilityTransformer.class);

        private Session session;

        public DataAvailabilityTransformer(Session session) {
            this.session = session;
        }

        @Override
        public DataAvailability transformTuple(Object[] tuple, String[] aliases) {
            Map<String, ReferenceType> procedures = new HashMap<String, ReferenceType>();
            Map<String, ReferenceType> observableProperties = new HashMap<String, ReferenceType>();
            Map<String, ReferenceType> featuresOfInterest = new HashMap<String, ReferenceType>();
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
                    timePeriod =
                            new TimePeriod(new DateTime(((Timestamp) tuple[3]).getTime(), DateTimeZone.UTC),
                                    new DateTime(((Timestamp) tuple[4]).getTime(), DateTimeZone.UTC));
                    if (tuple.length == 6) {
                        valueCount = (Long)tuple[5];
                    }
                } else if (tuple.length == 8 || tuple.length == 9) {
                    procedure = getProcedureReferenceType(tuple[0], procedures);
                    addTitleToReferenceType(tuple[1], procedure);
                    observableProperty = getObservablePropertyReferenceType(tuple[2], observableProperties);
                    addTitleToReferenceType(tuple[3], observableProperty);
                    featureOfInterest = getFeatureOfInterestReferenceType(tuple[4], featuresOfInterest);
                    addTitleToReferenceType(tuple[5], featureOfInterest);
                    timePeriod =
                            new TimePeriod(new DateTime(((Timestamp) tuple[6]).getTime()), new DateTime(
                                    ((Timestamp) tuple[7]).getTime()));
                    if (tuple.length == 9) {
                        valueCount = (Long)tuple[8];
                    }
                }
                return new DataAvailability(procedure, observableProperty, featureOfInterest, timePeriod, valueCount);
            } catch (OwsExceptionReport e) {
                LOGGER.error("Error while querying GetDataAvailability", e);
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
                FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject()
                    .addFeatureIdentifier(identifier)
                    .setConnection(session)
                    .setVersion(Sos2Constants.SERVICEVERSION);
                AbstractFeature feature =
                        Configurator.getInstance().getFeatureQueryHandler()
                                .getFeatureByID(queryObject);
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

    private static class SeriesMinMaxTransformer implements ResultTransformer {
        private static final long serialVersionUID = -373512929481519459L;

        @Override
        public TimePeriod transformTuple(Object[] tuple, String[] aliases) {
            if (tuple != null && tuple[0] != null && tuple[1]  != null) {
                return new TimePeriod(new DateTime(((Timestamp) tuple[0]).getTime(), DateTimeZone.UTC), new DateTime(
                    ((Timestamp) tuple[1]).getTime(), DateTimeZone.UTC));
            }
            return null;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public List transformList(List collection) {
            return collection;
        }
    }
}
