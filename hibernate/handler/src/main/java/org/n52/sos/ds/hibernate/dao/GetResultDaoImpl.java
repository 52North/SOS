/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.request.GetResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.shetland.ogc.sos.response.GetResultTemplateResponse;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.GetResultTemplateHandler;
import org.n52.sos.ds.dao.GetResultDao;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.ds.utils.ResultHandlingHelper;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.svalbard.util.SweHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class GetResultDaoImpl extends AbstractDaoImpl implements GetResultDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetResultDaoImpl.class);

    private HibernateSessionHolder sessionHolder;

    private GetResultTemplateHandler resultTemplateHandler;

    private DaoFactory daoFactory;

    private ResultHandlingHelper resultHandlingHelper;

    private ProfileHandler profileHandler;

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setGetResultTemplateHandler(GetResultTemplateHandler resultTemplateHandler) {
        this.resultTemplateHandler = resultTemplateHandler;
    }

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public SweHelper getSweHelper() {
        return getDaoFactory().getSweHelper();
    }

    protected ProfileHandler getProfileHandler() {
        return profileHandler;
    }

    public ResultHandlingHelper getResultHandlingHelper() {
        if (resultHandlingHelper == null) {
            this.resultHandlingHelper = new ResultHandlingHelper(getDaoFactory().getObservationHelper());
        }
        return resultHandlingHelper;
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    @Override
    public GetResultResponse queryResultData(GetResultRequest request, GetResultResponse response)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return queryResultData(request, response, session);
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying result data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public GetResultResponse queryResultData(GetResultRequest request, GetResultResponse response, Object connection)
            throws OwsExceptionReport {
        if (checkConnection(connection)) {
            return getResult(request, response, HibernateSessionHolder.getSession(connection));
        }
        return response;
    }

    private GetResultResponse getResult(GetResultRequest request, GetResultResponse response, Session session)
            throws OwsExceptionReport {
        GetResultTemplateResponse resultTemplate = queryResultTemplate(request);
        if (resultTemplate != null) {
            SosResultEncoding resultEncoding = resultTemplate.getResultEncoding();
            SosResultStructure resultStructure = resultTemplate.getResultStructure();
            final List<DataEntity<?>> observations =
                    queryObservations(request, request.getFeatureIdentifiers(), session);
            response.setResultValues(getResultHandlingHelper().createResultValuesFromObservations(observations,
                    resultEncoding, resultStructure, getProfileHandler().getActiveProfile()
                            .getResponseNoDataPlaceholder(),
                    session));
            return response;
        }
        return response;
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
     * @param session
     *            Hibernate session
     * @return List of Observation objects
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    @SuppressWarnings("unchecked")
    private List<DataEntity<?>> queryObservations(GetResultRequest request, Collection<String> featureIdentifiers,
            Session session) throws OwsExceptionReport {
        final Criteria c = createCriteriaFor(DataEntity.class, session);
        addSpatialFilteringProfileRestrictions(c, request, session);
        addParentChildRestriction(c);

        List<DatasetEntity> series = getDaoFactory().getSeriesDAO()
                .getSeries(request, featureIdentifiers, session);
        if (CollectionHelper.isEmpty(series)) {
            return null;
        } else {
            c.add(Restrictions.in(DataEntity.PROPERTY_DATASET_ID, series.stream()
                    .map(DatasetEntity::getId)
                    .collect(Collectors.toSet())));
        }

        if (request.getTemporalFilter() != null && !request.getTemporalFilter()
                .isEmpty()) {
            addTemporalFilter(c, request.getTemporalFilter());
        }

        LOGGER.trace("QUERY queryObservation(request, featureIdentifiers): {}", HibernateHelper.getSqlString(c));
        return c.list();

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
    private void addTemporalFilter(Criteria c, List<TemporalFilter> temporalFilter)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        c.add(SosTemporalRestrictions.filter(temporalFilter));
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
        return session.createCriteria(clazz)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .add(Restrictions.eq(DataEntity.PROPERTY_DELETED, false))
                .addOrder(Order.asc(DataEntity.PROPERTY_SAMPLING_TIME_START));
    }

    private void addParentChildRestriction(Criteria c) {
        c.add(Restrictions.isNull(DataEntity.PROPERTY_PARENT));
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
            criteria.add(SpatialRestrictions.filter(DataEntity.PROPERTY_GEOMETRY_ENTITY, request.getSpatialFilter()
                    .getOperator(),
                    getDaoFactory().getGeometryHandler()
                            .switchCoordinateAxisFromToDatasourceIfNeeded(request.getSpatialFilter()
                                    .getGeometry()
                                    .toGeometry())));
        }
    }

    private GetResultTemplateResponse queryResultTemplate(final GetResultRequest request) throws OwsExceptionReport {
        GetResultTemplateRequest r = new GetResultTemplateRequest(request.getService(), request.getVersion());
        r.setOffering(request.getOffering());
        r.setObservedProperty(request.getObservedProperty());
        return resultTemplateHandler.getResultTemplate(r);
    }

}
