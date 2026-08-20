/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.locationtech.jts.geom.Geometry;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
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
import org.n52.sos.ds.dao.GetResultTemplateDao;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.ds.utils.ResultHandlingHelper;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.svalbard.util.SweHelper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public class GetResultDaoImpl extends AbstractDaoImpl implements GetResultDao {

    private HibernateSessionHolder sessionHolder;

    private GetResultTemplateHandler resultTemplateHandler;

    private DaoFactory daoFactory;

    private ResultHandlingHelper resultHandlingHelper;

    private ProfileHandler profileHandler;

    private Optional<GetResultTemplateDao> getResultTemplateDao;

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

    @Inject
    public void setGetResultTemplateDao(Optional<GetResultTemplateDao> getResultTemplateDao) {
        this.getResultTemplateDao = getResultTemplateDao;
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
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying result data!")
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

    private Optional<GetResultTemplateDao> getGetResultTemplateDao() {
        return getResultTemplateDao == null ? Optional.empty() : getResultTemplateDao;
    }

    private GetResultTemplateHandler getResultTemplateHandler() {
        return resultTemplateHandler;
    }

    private GetResultResponse getResult(GetResultRequest request, GetResultResponse response, Session session)
            throws OwsExceptionReport {
        GetResultTemplateResponse resultTemplate = queryResultTemplate(request, session);
        if (resultTemplate != null) {
            SosResultEncoding resultEncoding = resultTemplate.getResultEncoding();
            SosResultStructure resultStructure = resultTemplate.getResultStructure();
            final List<DataEntity<?>> observations =
                    queryObservations(request, request.getFeatureIdentifiers(), session);
            response.setResultValues(getResultHandlingHelper().createResultValuesFromObservations(observations,
                    resultEncoding, resultStructure,
                    getProfileHandler().getActiveProfile().getResponseNoDataPlaceholder(), session));
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
     *            Set of feature identifiers. If <tt>null</tt>, query filter will not be added. If
     *            <tt>empty</tt>, <tt>null</tt> will be returned.
     * @param session
     *            Hibernate session
     * @return List of Observation objects
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<DataEntity<?>> queryObservations(GetResultRequest request, Collection<String> featureIdentifiers,
            Session session) throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(DataEntity.class);
        Root root = query.from(DataEntity.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_DELETED), false));
        Predicate spatialFilter = getSpatialFilteringProfilePredicate(cb, root, request);
        if (spatialFilter != null) {
            predicates.add(spatialFilter);
        }
        predicates.add(cb.isNull(root.get(DataEntity.PROPERTY_PARENT)));

        List<DatasetEntity> series = getDaoFactory().getSeriesDAO().getSeries(request, featureIdentifiers, session);
        if (CollectionHelper.isEmpty(series)) {
            return null;
        } else {
            predicates.add(root.get(DataEntity.PROPERTY_DATASET_ID)
                               .in(series.stream().map(DatasetEntity::getId).collect(Collectors.toSet())));
        }
        if (request.getTemporalFilter() != null && !request.getTemporalFilter().isEmpty()) {
            predicates.add(SosTemporalRestrictions.filter(cb, root, request.getTemporalFilter()));
        }

        query.where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.asc(root.get(DataEntity.PROPERTY_SAMPLING_TIME_START)));
        return session.createQuery(query).list();
    }

    /**
     * Get the Spatial Filtering Profile predicate for the request
     *
     * @param cb
     *            CriteriaBuilder
     * @param root
     *            Root of the observation query
     * @param request
     *            GetResult request
     * @return The predicate, or <code>null</code> if the request has no spatial filter
     * @throws OwsExceptionReport
     *             If Spatial Filtering Profile is not supported or an error occurs
     */
    private Predicate getSpatialFilteringProfilePredicate(CriteriaBuilder cb, Root<?> root, GetResultRequest request)
            throws OwsExceptionReport {
        if (request.hasSpatialFilteringProfileSpatialFilter()) {
            return SpatialRestrictions.filter(cb, root.<Geometry>get(DataEntity.PROPERTY_GEOMETRY_ENTITY),
                    request.getSpatialFilter().getOperator(),
                    getDaoFactory().getGeometryHandler().switchCoordinateAxisFromToDatasourceIfNeeded(
                            request.getSpatialFilter().getGeometry().toGeometry()));
        }
        return null;
    }

    private GetResultTemplateResponse queryResultTemplate(final GetResultRequest request, Session session)
            throws OwsExceptionReport {
        GetResultTemplateRequest r = new GetResultTemplateRequest(request.getService(), request.getVersion());
        r.setOffering(request.getOffering());
        r.setObservedProperty(request.getObservedProperty());
        return getGetResultTemplateDao().isPresent()
                ? getGetResultTemplateDao().get().queryResultTemplate(r,
                        new GetResultTemplateResponse(request.getService(), request.getVersion()), session)
                : getResultTemplateHandler().getResultTemplate(r);
    }

}
