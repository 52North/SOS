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
package org.n52.sos.ds.hibernate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.n52.sos.ds.AbstractGetFeatureOfInterestDAO;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.HibernateSqlQueryConstants;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationInfo;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationInfo;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.i18n.LocaleHelper;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Implementation of the abstract class AbstractGetFeatureOfInterestDAO
 *
 * @since 4.0.0
 *
 */
public class GetFeatureOfInterestDAO extends AbstractGetFeatureOfInterestDAO implements HibernateSqlQueryConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFeatureOfInterestDAO.class);

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    private static final String SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER = "getFeatureForIdentifier";

    private static final String SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE = "getFeatureForIdentifierProcedure";

    private static final String SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_OBSERVED_PROPERTY =
            "getFeatureForIdentifierObservableProperty";

    private static final String SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE_OBSERVED_PROPERTY =
            "getFeatureForIdentifierProcedureObservableProperty";

    private static final String SQL_QUERY_GET_FEATURE_FOR_PROCEDURE = "getFeatureForProcedure";

    private static final String SQL_QUERY_GET_FEATURE_FOR_PROCEDURE_OBSERVED_PROPERTY =
            "getFeatureForProcedureObservableProperty";

    private static final String SQL_QUERY_GET_FEATURE_FOR_OBSERVED_PROPERTY = "getFeatureForObservableProperty";

    /**
     * constructor
     */
    public GetFeatureOfInterestDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }
    
    @Override
    public GetFeatureOfInterestResponse getFeatureOfInterest(final GetFeatureOfInterestRequest request)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            FeatureCollection featureCollection;

            if (isSos100(request)) {
                // sos 1.0.0 either or
                if (isMixedFeatureIdentifierAndSpatialFilters(request)) {
                    throw new NoApplicableCodeException()
                            .withMessage("Only one out of featureofinterestid or location possible.");
                } else if (isFeatureIdentifierRequest(request) || isSpatialFilterRequest(request)) {
                    featureCollection = getFeatures(request, session);
                } else {
                    throw new CompositeOwsException(new MissingParameterValueException(
                            Sos1Constants.GetFeatureOfInterestParams.featureOfInterestID),
                            new MissingParameterValueException(Sos1Constants.GetFeatureOfInterestParams.location));
                }
            } else // SOS 2.0
            {
                featureCollection = getFeatures(request, session);
                /*
                 * Now, we return the list of returned features and not a
                 * complex encoded relatedFeature See
                 * AbstractGetFeatureOfInterestDAO:100-195 Don't forget to
                 * activate in MiscSettings the relatedFeature setting
                 * featureCollection = processRelatedFeatures(
                 * request.getFeatureIdentifiers(), featureCollection,
                 * ServiceConfiguration
                 * .getInstance().getRelatedSamplingFeatureRoleForChildFeatures
                 * ());
                 */
            }
            final GetFeatureOfInterestResponse response = new GetFeatureOfInterestResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            response.setAbstractFeature(featureCollection);
            return response;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage(
                    "Error while querying feature of interest data!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    /**
     * Check if the request contains spatial filters
     *
     * @param request
     *            GetFeatureOfInterest request to check
     * @return <code>true</code>, if the request contains spatial filters
     */
    private boolean isSpatialFilterRequest(final GetFeatureOfInterestRequest request) {
        return request.getSpatialFilters() != null && !request.getSpatialFilters().isEmpty();
    }

    /**
     * Check if the request contains feature identifiers
     *
     * @param request
     *            GetFeatureOfInterest request to check
     * @return <code>true</code>, if the request contains feature identifiers
     */
    private boolean isFeatureIdentifierRequest(final GetFeatureOfInterestRequest request) {
        return request.getFeatureIdentifiers() != null && !request.getFeatureIdentifiers().isEmpty();
    }

    /**
     * Check if the request contains spatial filters and feature identifiers
     *
     * @param request
     *            GetFeatureOfInterest request to check
     * @return <code>true</code>, if the request contains spatial filters and
     *         feature identifiers
     */
    private boolean isMixedFeatureIdentifierAndSpatialFilters(final GetFeatureOfInterestRequest request) {
        return isFeatureIdentifierRequest(request) && isSpatialFilterRequest(request);
    }

    /**
     * Check if the requested version is SOS 1.0.0
     *
     * @param request
     *            GetFeatureOfInterest request to check
     * @return <code>true</code>, if the requested version is SOS 1.0.0
     */
    private boolean isSos100(final GetFeatureOfInterestRequest request) {
        return request.getVersion().equals(Sos1Constants.SERVICEVERSION);
    }

    /**
     * Get featureOfInterest as a feature collection
     *
     * @param request
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate session
     * @return Feature collection with requested featuresOfInterest
     * @throws OwsExceptionReport
     *             If an error occurs during processing
     */
    private FeatureCollection getFeatures(final GetFeatureOfInterestRequest request, final Session session)
            throws OwsExceptionReport {
        final Set<String> foiIDs = new HashSet<String>(queryFeatureIdentifiersForParameter(request, session));
        if (request.isSetFeatureOfInterestIdentifiers()) {
            addRequestedRelatedFeatures(foiIDs, request.getFeatureIdentifiers());
        }
        // feature of interest
        FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject()
            .setFeatureIdentifiers(foiIDs)
            .setSpatialFilters(request.getSpatialFilters())
            .setConnection(session)
            .setVersion(request.getVersion())
            .setI18N(LocaleHelper.fromRequest(request));
        return new FeatureCollection(getConfigurator().getFeatureQueryHandler().getFeatures(queryObject));
    }

    /**
     * Adds the identifiers from <tt>featureIdentifiers</tt> to the
     * <tt>foiIDs</tt> if the feature is an relatedFeature and a child is
     * already contained in <tt>foiIDs</tt>
     *
     * @param foiIDs
     *            Feature identifiers
     * @param featureIdentifiers
     *            Feature identifiers to add
     */
    private void addRequestedRelatedFeatures(final Set<String> foiIDs, final List<String> featureIdentifiers) {
        requestedFeatures: for (final String requestedFeature : featureIdentifiers) {
            if (isRelatedFeature(requestedFeature)) {
                final Set<String> childFeatures = getCache().getChildFeatures(requestedFeature, true, false);
                for (final String featureWithObservation : foiIDs) {
                    if (childFeatures.contains(featureWithObservation)) {
                        foiIDs.add(requestedFeature);
                        continue requestedFeatures;
                    }
                }
            }
        }
    }

    /**
     * Get featureOfInterest identifiers for requested parameters
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate session
     * @return Resulting FeatureOfInterest identifiers list
     * @throws OwsExceptionReport
     *             If an error occurs during processing
     */
    @SuppressWarnings("unchecked")
    private List<String> queryFeatureIdentifiersForParameter(final GetFeatureOfInterestRequest req,
            final Session session) throws OwsExceptionReport {
        if (req.hasNoParameter()) {
            return new FeatureOfInterestDAO().getFeatureOfInterestIdentifiers(session);
        }
        if (req.containsOnlyFeatureParameter() && req.isSetFeatureOfInterestIdentifiers()) {
            final Criteria c =
                    session.createCriteria(FeatureOfInterest.class).setProjection(
                            Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
            final Collection<String> features = getFeatureIdentifiers(req.getFeatureIdentifiers());
            if (features != null && !features.isEmpty()) {
                c.add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
            }
            LOGGER.debug("QUERY queryFeatureIdentifiersForParameter(request): {}", HibernateHelper.getSqlString(c));
            return c.list();
        }
        if (checkForNamedQueries(req, session)) {
            return executeNamedQuery(req, session);
        }
        if (isSos100(req)) {
            return queryFeatureIdentifiersForParameterForSos100(req, session);
        }
        if (EntitiyHelper.getInstance().isSeriesSupported()) {
            return queryFeatureIdentifiersForParameterForSeries(req, session);
        }
        return queryFeatureIdentifiersOfParameterFromOldObservations(req, session);
    }

    /**
     * Query FeatureOfInterest identifiers for old observation concept
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate Sesstion
     * @return Resulting FeatureOfInterest identifiers list
     */
    @SuppressWarnings("unchecked")
    private List<String> queryFeatureIdentifiersOfParameterFromOldObservations(GetFeatureOfInterestRequest req,
            Session session) {
        Criteria c = getCriteriaForFeatureIdentifiersOfParameterFromOldObservations(req, session);
        LOGGER.debug("QUERY queryFeatureIdentifiersOfParameterFromOldObservations(request): {}",
                HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get Hibernate Criteria for query FeatureOfInterest identifiers for old
     * observation concept
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate Sesstion
     * @return Hibernate Criteria
     */
    private Criteria getCriteriaForFeatureIdentifiersOfParameterFromOldObservations(GetFeatureOfInterestRequest req,
            Session session) {
        final Criteria c = session.createCriteria(ObservationInfo.class);
        final Criteria fc = c.createCriteria(ObservationInfo.FEATURE_OF_INTEREST);
        fc.setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));

        // relates to observations.
        if (req.isSetFeatureOfInterestIdentifiers()) {
            final Collection<String> features = getFeatureIdentifiers(req.getFeatureIdentifiers());
            if (features != null && !features.isEmpty()) {
                fc.add(Restrictions.in(FeatureOfInterest.IDENTIFIER, features));
            }
        }
        // observableProperties
        if (req.isSetObservableProperties()) {
            c.createCriteria(ObservationInfo.OBSERVABLE_PROPERTY).add(
                    Restrictions.in(ObservableProperty.IDENTIFIER, req.getObservedProperties()));
        }
        // procedures
        if (req.isSetProcedures()) {
            c.createCriteria(ObservationInfo.PROCEDURE)
                    .add(Restrictions.in(Procedure.IDENTIFIER, req.getProcedures()));
        }
        return c;
    }

    /**
     * Query FeatureOfInterest identifiers for series concept
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate Sesstion
     * @return Resulting FeatureOfInterest identifiers list
     * @throws CodedException If an error occurs during processing
     */
    @SuppressWarnings("unchecked")
    private List<String> queryFeatureIdentifiersForParameterForSeries(GetFeatureOfInterestRequest req, Session session) throws CodedException {
        final Criteria c = session.createCriteria(FeatureOfInterest.class);
        if (req.isSetFeatureOfInterestIdentifiers()) {
            c.add(Restrictions.in(FeatureOfInterest.IDENTIFIER, req.getFeatureIdentifiers()));
        }
        c.add(Subqueries.propertyIn(FeatureOfInterest.ID,
                getDetachedCriteriaForSeriesWithProcedureObservableProperty(req, session)));
        c.setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
        LOGGER.debug("QUERY queryFeatureIdentifiersForParameterForSeries(request): {}",
                HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Query FeatureOfInterest identifiers for SOS 1.0.0 request
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate Sesstion
     * @return Resulting FeatureOfInterest identifiers list
     * @throws OwsExceptionReport
     *             If an error occurs during processing
     */
    @SuppressWarnings("unchecked")
    private List<String> queryFeatureIdentifiersForParameterForSos100(GetFeatureOfInterestRequest req, Session session)
            throws OwsExceptionReport {
        Criteria c = null;
        if (EntitiyHelper.getInstance().isSeriesSupported()) {
            c = getCriteriaForFeatureIdentifiersOfParameterFromSeriesObservations(req, session);
        } else {
            c = getCriteriaForFeatureIdentifiersOfParameterFromOldObservations(req, session);
            if (req.isSetTemporalFilters()) {
                c.add(TemporalRestrictions.filter(req.getTemporalFilters()));
            }
        }

        LOGGER.debug("QUERY queryFeatureIdentifiersForParameterForSos100(request): {}",
                HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Get Hibernate Criteria for query FeatureOfInterest identifiers for series
     * observation concept (SOS 1.0.0)
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate Sesstion
     * @return Hibernate Criteria
     * @throws OwsExceptionReport
     *             If an error occurs during processing
     */
    private Criteria getCriteriaForFeatureIdentifiersOfParameterFromSeriesObservations(
            GetFeatureOfInterestRequest req, Session session) throws OwsExceptionReport {
        final Criteria c = session.createCriteria(FeatureOfInterest.class);
        if (req.isSetFeatureOfInterestIdentifiers()) {
            c.add(Restrictions.in(FeatureOfInterest.IDENTIFIER, req.getFeatureIdentifiers()));
        }
        c.add(Subqueries.propertyIn(FeatureOfInterest.ID,
                getDetachedCriteriaForFeautreOfInterestForSeries(req, session)));
        c.setProjection(Projections.distinct(Projections.property(FeatureOfInterest.IDENTIFIER)));
        return c;
    }

    /**
     * Get Detached Criteria for series concept. Criteria results are
     * FeatureOfInterest entities.
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate Sesstion
     * @return Detached Criteria
     * @throws CodedException If an error occurs during processing
     */
    private DetachedCriteria getDetachedCriteriaForSeriesWithProcedureObservableProperty(
            GetFeatureOfInterestRequest req, Session session) throws CodedException {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(EntitiyHelper.getInstance().getSeriesEntityClass());
        detachedCriteria.add(Restrictions.eq(Series.DELETED, false));
        // observableProperties
        if (req.isSetObservableProperties()) {
            detachedCriteria.createCriteria(Series.OBSERVABLE_PROPERTY).add(
                    Restrictions.in(ObservableProperty.IDENTIFIER, req.getObservedProperties()));
        }
        // procedures
        if (req.isSetProcedures()) {
            detachedCriteria.createCriteria(Series.PROCEDURE).add(
                    Restrictions.in(Procedure.IDENTIFIER, req.getProcedures()));
        }
        detachedCriteria.setProjection(Projections.distinct(Projections.property(Series.FEATURE_OF_INTEREST)));
        return detachedCriteria;
    }

    /**
     * Get Detached Criteria for SOS 1.0.0 and series concept. Criteria results
     * are FeatureOfInterest entities.
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate Sesstion
     * @return Detached Criteria
     * @throws OwsExceptionReport
     *             If an error occurs during processing
     */
    private DetachedCriteria getDetachedCriteriaForFeautreOfInterestForSeries(GetFeatureOfInterestRequest req,
            Session session) throws OwsExceptionReport {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(EntitiyHelper.getInstance().getSeriesEntityClass());
        detachedCriteria.add(Subqueries.propertyIn(Series.ID,
                getDetachedCriteriaForSeriesWithProcedureObservablePropertyTemporalFilter(req, session)));
        detachedCriteria.setProjection(Projections.distinct(Projections.property(Series.FEATURE_OF_INTEREST)));
        return detachedCriteria;
    }

    /**
     * Get Detached Criteria for SOS 1.0.0 and series concept. Criteria results
     * are Series entities.
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate Sesstion
     * @return Detached Criteria
     * @throws CodedException If an error occurs during processing
     */
    private DetachedCriteria getDetachedCriteriaForSeriesWithProcedureObservablePropertyTemporalFilter(
            GetFeatureOfInterestRequest req, Session session) throws CodedException {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(EntitiyHelper.getInstance().getObservationInfoEntityClass());
        DetachedCriteria seriesCriteria = detachedCriteria.createCriteria(SeriesObservationInfo.SERIES);
        detachedCriteria.add(Restrictions.eq(Series.DELETED, false));
        // observableProperties
        if (req.isSetObservableProperties()) {
            seriesCriteria.createCriteria(Series.OBSERVABLE_PROPERTY).add(
                    Restrictions.in(ObservableProperty.IDENTIFIER, req.getObservedProperties()));
        }
        // procedures
        if (req.isSetProcedures()) {
            seriesCriteria.createCriteria(Series.PROCEDURE).add(
                    Restrictions.in(Procedure.IDENTIFIER, req.getProcedures()));
        }
        // temporal filter
        if (req.isSetTemporalFilters()) {
            detachedCriteria.add(TemporalRestrictions.filter(req.getTemporalFilters()));
        }

        detachedCriteria.setProjection(Projections.distinct(Projections.property(SeriesObservationInfo.SERIES)));
        return detachedCriteria;
    }

    /**
     * Check if named queries for GetFeatureOfInterest requests are available
     *
     * @param req
     *            GetFeatureOFInterest request
     * @param session
     *            Hibernate session
     * @return <code>true</code>, if a named query is available
     */
    private boolean checkForNamedQueries(GetFeatureOfInterestRequest req, Session session) {
        final boolean features = req.isSetFeatureOfInterestIdentifiers();
        final boolean observableProperties = req.isSetObservableProperties();
        final boolean procedures = req.isSetProcedures();
        // all
        if (features && observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(
                    SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE_OBSERVED_PROPERTY, session);
        }
        // observableProperties and procedures
        else if (!features && observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_PROCEDURE_OBSERVED_PROPERTY,
                    session);
        }
        // only observableProperties
        else if (!features && observableProperties && !procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_OBSERVED_PROPERTY, session);
        }
        // only procedures
        else if (!features && !observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_PROCEDURE, session);
        }
        // features and observableProperties
        else if (features && observableProperties && !procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_OBSERVED_PROPERTY,
                    session);
        }
        // features and procedures
        else if (features && !observableProperties && procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE, session);
        }
        // only features
        else if (features && !observableProperties && !procedures) {
            return HibernateHelper.isNamedQuerySupported(SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER, session);
        }
        return false;
    }

    /**
     * Execute named query for GetFeatureOfInterest request
     *
     * @param req
     *            GetFeatureOfInterest request
     * @param session
     *            Hibernate session
     * @return FeatureOfInterest identifiers list
     */
    @SuppressWarnings("unchecked")
    private List<String> executeNamedQuery(GetFeatureOfInterestRequest req, Session session) {
        final boolean features = req.isSetFeatureOfInterestIdentifiers();
        final boolean observableProperties = req.isSetObservableProperties();
        final boolean procedures = req.isSetProcedures();
        String namedQueryName = null;
        Map<String, Collection<String>> parameter = Maps.newHashMap();
        // all
        if (features && observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE_OBSERVED_PROPERTY;
            parameter.put(FEATURES, req.getFeatureIdentifiers());
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // observableProperties and procedures
        else if (!features && observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_PROCEDURE_OBSERVED_PROPERTY;
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // only observableProperties
        else if (!features && observableProperties && !procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_OBSERVED_PROPERTY;
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
        }
        // only procedures
        else if (!features && !observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_PROCEDURE;
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // features and observableProperties
        else if (features && observableProperties && !procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_OBSERVED_PROPERTY;
            parameter.put(FEATURES, req.getFeatureIdentifiers());
            parameter.put(OBSERVABLE_PROPERTIES, req.getObservedProperties());
        }
        // features and procedures
        else if (features && !observableProperties && procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER_PROCEDURE;
            parameter.put(FEATURES, req.getFeatureIdentifiers());
            parameter.put(PROCEDURES, req.getProcedures());
        }
        // only features
        else if (features && !observableProperties && !procedures) {
            namedQueryName = SQL_QUERY_GET_FEATURE_FOR_IDENTIFIER;
            parameter.put(FEATURES, req.getFeatureIdentifiers());
        }
        if (StringHelper.isNotEmpty(namedQueryName)) {
            Query namedQuery = session.getNamedQuery(namedQueryName);
            for (String key : parameter.keySet()) {
                namedQuery.setParameterList(key, parameter.get(key));
            }
            LOGGER.debug("QUERY getFeatureOfInterest() with NamedQuery: {}", namedQuery);
            return namedQuery.list();
        }
        return Lists.newLinkedList();
    }

}
