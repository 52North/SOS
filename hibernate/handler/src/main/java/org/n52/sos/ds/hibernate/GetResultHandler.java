/*
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ResultTemplateEntity;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.AbstractGetResultHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.ResultHandlingHelper;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.service.SosSettings;
import org.n52.svalbard.ConformanceClasses;
import org.n52.svalbard.util.SweHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Implementation of the abstract class AbstractGetResultHandler
 *
 * @since 4.0.0
 *
 */
@Configurable
public class GetResultHandler extends AbstractGetResultHandler implements AbstractResultHandler, Constructable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetResultHandler.class);

    private HibernateSessionHolder sessionHolder;

    private DaoFactory daoFactory;

    private boolean strictSpatialFilteringProfile;

    private ResultHandlingHelper resultHandlingHelper;

    private boolean supportsDatabaseEntities;

    public GetResultHandler() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Setting(SosSettings.STRICT_SPATIAL_FILTERING_PROFILE)
    public void setStrictSpatialFilteringProfile(final boolean strictSpatialFilteringProfile) {
        this.strictSpatialFilteringProfile = strictSpatialFilteringProfile;
    }

    @Override
    public void init() {
        this.supportsDatabaseEntities = HibernateHelper.isEntitySupported(ResultTemplateEntity.class);
        this.resultHandlingHelper = new ResultHandlingHelper(daoFactory.getGeometryHandler(),
                daoFactory.getSweHelper(), daoFactory.getDecoderRepository());
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            try {
                Session session = sessionHolder.getSession();
                if (strictSpatialFilteringProfile) {
                    return Sets.newHashSet(ConformanceClasses.SOS_V2_SPATIAL_FILTERING_PROFILE);
                }
                sessionHolder.returnSession(session);
            } catch (OwsExceptionReport owse) {
                LOGGER.error("Error while getting Spatial Filtering Profile conformance class!", owse);
            }
        }
        return super.getConformanceClasses(service, version);
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ResultTemplateEntity.class);
    }

    @Override
    public SweHelper getSweHelper() {
        return daoFactory.getSweHelper();
    }

    @Override
    public GetResultResponse getResult(final GetResultRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            final GetResultResponse response = new GetResultResponse();
            response.setService(request.getService());
            response.setVersion(request.getVersion());
            final Set<String> featureIdentifier =
                    QueryHelper.getFeatures(daoFactory.getFeatureQueryHandler(), request, session);
            final List<ResultTemplateEntity> resultTemplates =
                    queryResultTemplate(request, featureIdentifier, session);
            if (CollectionHelper.isNotEmpty(resultTemplates)) {
                final SosResultEncoding sosResultEncoding = createSosResultEncoding(resultTemplates.get(0)
                        .getEncoding());
                final SosResultStructure sosResultStructure = createSosResultStructure(resultTemplates.get(0)
                        .getStructure());
                final List<DataEntity<?>> observations;
                observations = querySeriesObservation(request, featureIdentifier, session);
                response.setResultValues(resultHandlingHelper.createResultValuesFromObservations(observations,
                        sosResultEncoding, sosResultStructure, getProfileHandler().getActiveProfile()
                                .getResponseNoDataPlaceholder()));
            }
            return response;
        } catch (final HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while querying result data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        } finally {
            sessionHolder.returnSession(session);
        }
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
    protected List<DataEntity<?>> querySeriesObservation(GetResultRequest request,
            Collection<String> featureIdentifiers, Session session) throws OwsExceptionReport {
        final Criteria c = createCriteriaFor(DataEntity.class, session);
        addSpatialFilteringProfileRestrictions(c, request, session);
        addParentChildRestriction(c);

        List<DatasetEntity> series = daoFactory.getSeriesDAO()
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
    private List<ResultTemplateEntity> queryResultTemplate(final GetResultRequest request,
            final Set<String> featureIdentifier, final Session session) {
        return supportsDatabaseEntities ? daoFactory.getResultTemplateDAO()
                .getResultTemplateObject(request.getOffering(), request.getObservedProperty(), featureIdentifier,
                        session)
                : new LinkedList<>();
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
                    daoFactory.getGeometryHandler()
                            .switchCoordinateAxisFromToDatasourceIfNeeded(request.getSpatialFilter()
                                    .getGeometry()
                                    .toGeometry())));
        }
    }
}
