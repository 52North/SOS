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

import static org.n52.sos.util.CollectionHelper.isEmpty;
import static org.n52.sos.util.CollectionHelper.isNotEmpty;
import static org.n52.sos.util.http.HTTPStatus.INTERNAL_SERVER_ERROR;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.AbstractGetResultDAO;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ResultTemplateDAO;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.ResultTemplate;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.ResultHandlingHelper;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosResultEncoding;
import org.n52.sos.ogc.sos.SosResultStructure;
import org.n52.sos.request.GetResultRequest;
import org.n52.sos.response.GetResultResponse;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * Implementation of the abstract class AbstractGetResultDAO
 *
 * @since 4.0.0
 *
 */
public class GetResultDAO extends AbstractGetResultDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetResultDAO.class);
    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();
    private ResultHandlingHelper helper = new ResultHandlingHelper();

    /**
     * constructor
     */
    public GetResultDAO() {
        super(SosConstants.SOS);
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    @Override
    public GetResultResponse getResult(final GetResultRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            final GetResultResponse response = new GetResultResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            final Set<String> featureIdentifier = QueryHelper.getFeatures(request, session);
            final List<ResultTemplate> resultTemplates = queryResultTemplate(request, featureIdentifier, session);
            if (isNotEmpty(resultTemplates)) {
                final SosResultEncoding sosResultEncoding =
                        new SosResultEncoding(resultTemplates.get(0).getResultEncoding());
                final SosResultStructure sosResultStructure =
                        new SosResultStructure(resultTemplates.get(0).getResultStructure());
                final List<Observation<?>> observations;
                ResultTemplate resultTemplate = resultTemplates.get(0);
                String procedure = null;
                if (resultTemplate.isSetProcedure()) {
                    procedure = resultTemplates.get(0).getProcedure().getIdentifier();
                }
                if (EntitiyHelper.getInstance().isSeriesObservationSupported()) {
                    observations = querySeriesObservation(request, featureIdentifier, procedure, session);
                } else {
                    observations = queryObservation(request, featureIdentifier, procedure, session);
                }

                response.setResultValues(helper.createResultValuesFromObservations(observations,
                        sosResultEncoding, sosResultStructure));
            }
            return response;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying result data!")
                    .setStatus(INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public Set<String> getConformanceClasses() {
        if (ServiceConfiguration.getInstance().isStrictSpatialFilteringProfile()) {
            return Sets.newHashSet(ConformanceClasses.SOS_V2_SPATIAL_FILTERING_PROFILE);
        }
        return super.getConformanceClasses();
    }
    
    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ResultTemplate.class);
    }

    /**
     * Query observations from database depending on requested filters
     *
     * @param request
     *            GetObservation request
     * @param featureIdentifiers
     *            Set of feature identifiers. If <tt>null</tt>, query filter
     *            will not be added. If <tt>empty</tt>, <tt>null</tt> will be
     *            returned.
     * @param session
     *            Hibernate session
     * @return List of Observation objects
     *
     *
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    @SuppressWarnings("unchecked")
    protected List<Observation<?>> queryObservation(final GetResultRequest request,
            final Set<String> featureIdentifiers, String procedure, final Session session) throws OwsExceptionReport {
        final Criteria c = createCriteriaFor(AbstractLegacyObservation.class, session);
        addSpatialFilteringProfileRestrictions(c, request, session);
        addParentChildRestriction(c);

        if (isEmpty(featureIdentifiers)) {
            return null; // because no features where found regarding the
                         // filters
        } else if (isNotEmpty(featureIdentifiers)) {
            c.createCriteria(AbstractLegacyObservation.FEATURE_OF_INTEREST).add(
                    Restrictions.in(FeatureOfInterest.IDENTIFIER, featureIdentifiers));
        }
        if (request.isSetObservedProperty()) {
            c.createCriteria(AbstractLegacyObservation.OBSERVABLE_PROPERTY).add(
                    Restrictions.eq(ObservableProperty.IDENTIFIER, request.getObservedProperty()));
        }
        if (!Strings.isNullOrEmpty(procedure)) {
            c.createCriteria(Observation.PROCEDURE).add(
                    Restrictions.eq(Procedure.IDENTIFIER, procedure));
        }
        if (request.isSetOffering()) {
            addOfferingRestriction(c, request.getOffering());
        }
        if (request.getTemporalFilter() != null && !request.getTemporalFilter().isEmpty()) {
            addTemporalFilter(c, request.getTemporalFilter());
        }
        c.addOrder(Order.asc(AbstractLegacyObservation.PHENOMENON_TIME_START));

        LOGGER.debug("QUERY queryObservation(request, featureIdentifiers): {}", HibernateHelper.getSqlString(c));
        return c.list();

    }

    /**
     * Query series observations from database depending on requested filters
     *
     * @param request
     *            GetObservation request
     * @param featureIdentifiers
     *            Set of feature identifiers. If <tt>null</tt>, query filter
     *            will not be added. If <tt>empty</tt>, <tt>null</tt> will be
     *            returned.
     * @param procedure 
     * @param session
     *            Hibernate session
     * @return List of Observation objects
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    @SuppressWarnings("unchecked")
    protected List<Observation<?>> querySeriesObservation(GetResultRequest request,
            Collection<String> featureIdentifiers, String procedure, Session session) throws OwsExceptionReport {
        final Criteria c = createCriteriaFor(AbstractSeriesObservation.class, session);
        addSpatialFilteringProfileRestrictions(c, request, session);
        addParentChildRestriction(c);

        List<Series> series = DaoFactory.getInstance().getSeriesDAO().getSeries(procedure,
                request.getObservedProperty(), request.getOffering(), featureIdentifiers, session);
        if (CollectionHelper.isEmpty(series)) {
            return null;
        } else {
            c.add(Restrictions.in(AbstractSeriesObservation.SERIES_ID,
                    series.stream().map(Series::getSeriesId).collect(Collectors.toSet())));
        }
        
        if (request.getTemporalFilter() != null && !request.getTemporalFilter().isEmpty()) {
            addTemporalFilter(c, request.getTemporalFilter());
        }

        LOGGER.debug("QUERY queryObservation(request, featureIdentifiers): {}", HibernateHelper.getSqlString(c));
        return c.list();

    }

    private void addParentChildRestriction(Criteria c) {
        c.add(Restrictions.eq(Observation.CHILD, false));
    }

    /**
     * Query corresponding ResultTemplate
     *
     * @param request
     *            GetResult request
     * @param featureIdentifier
     *            Associated featureOfInterest identifier
     * @param session
     *            Hibernate session
     * @return Resulting ResultTemplates as list
     */
    private List<ResultTemplate> queryResultTemplate(final GetResultRequest request,
            final Set<String> featureIdentifier, final Session session) {
        final List<ResultTemplate> resultTemplates =
                new ResultTemplateDAO().getResultTemplateObject(request.getOffering(), request.getObservedProperty(),
                        featureIdentifier, session);
        return resultTemplates;
    }

    /**
     * Add offering identifier restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param offering
     *            Offering identifier ot add
     */
    private void addOfferingRestriction(Criteria c, String offering) {
        c.createCriteria(AbstractObservation.OFFERINGS).add(Restrictions.eq(Offering.IDENTIFIER, offering));
    }

    /**
     * Add offering identifier restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param temporalFilter
     *            Temporal filters to add
     * @throws UnsupportedTimeException
     *             If the time is not supported
     * @throws UnsupportedValueReferenceException
     *             If the valueReference is not supported
     * @throws UnsupportedOperatorException
     *             If the temporal operator is not supported
     */
    private void addTemporalFilter(Criteria c, List<TemporalFilter> temporalFilter) throws UnsupportedTimeException,
            UnsupportedValueReferenceException, UnsupportedOperatorException {
        c.add(TemporalRestrictions.filter(temporalFilter));
    }

    /**
     * Create Hibernate Criteria for the class and add ascending of phenomenon
     * start time
     *
     * @param clazz
     *            The class for the Criteria
     * @param session
     *            Hibernate session
     * @return Hibernate Criteria for the class and add ascending of phenomenon
     *         start time
     */
    @SuppressWarnings("rawtypes")
    private Criteria createCriteriaFor(Class clazz, Session session) {
        return session.createCriteria(clazz).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq(AbstractLegacyObservation.DELETED, false))
                .addOrder(Order.asc(AbstractLegacyObservation.PHENOMENON_TIME_START));
    }

    /**
     * @param criteria
     *            Hibernate Criteria to add restriction
     * @param request
     *            GetResult request
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If Spatial Filtering Profile is not supported or an error
     *             occurs
     */
    private void addSpatialFilteringProfileRestrictions(Criteria criteria, GetResultRequest request, Session session)
            throws OwsExceptionReport {
        if (request.hasSpatialFilteringProfileSpatialFilter()) {
            if (GeometryHandler.getInstance().isSpatialDatasource()) {
                criteria.add(SpatialRestrictions.filter(
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
}
