/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import static org.n52.janmayen.http.HTTPStatus.INTERNAL_SERVER_ERROR;
import static org.n52.shetland.util.CollectionHelper.isEmpty;
import static org.n52.shetland.util.CollectionHelper.isNotEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.ds.ConnectionProvider;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.ogc.sos.response.GetResultResponse;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.encoding.SweAbstractEncoding;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.AbstractGetResultHandler;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ResultTemplateDAO;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.ResultTemplate;
import org.n52.sos.ds.hibernate.entities.observation.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.QueryHelper;
import org.n52.sos.ds.hibernate.util.ResultHandlingHelper;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.hibernate.util.SpatialRestrictions;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.ConformanceClasses;
import org.n52.svalbard.decode.Decoder;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.decode.XmlNamespaceDecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.decode.exception.NoDecoderForKeyException;
import org.n52.svalbard.util.XmlHelper;

import com.google.common.collect.Sets;

/**
 * Implementation of the abstract class AbstractGetResultHandler
 *
 * @since 4.0.0
 *
 */
public class GetResultDAO extends AbstractGetResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetResultDAO.class);

    private HibernateSessionHolder sessionHolder;
    private FeatureQueryHandler featureQueryHandler;
    private final EntitiyHelper entitiyHelper = new EntitiyHelper();
    private DecoderRepository decoderRepository;
    private DaoFactory daoFactory;
    private GeometryHandler geometryHandler;

    public GetResultDAO() {
        super(SosConstants.SOS);
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setFeatureQueryHandler(FeatureQueryHandler featureQueryHandler) {
        this.featureQueryHandler = featureQueryHandler;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setDecoderRepository(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    protected DecoderRepository getDecoderRepository() {
        return decoderRepository;
    }

    protected <T> T decode(String xml) throws DecodingException {
        try {
            return decode(XmlObject.Factory.parse(xml));
        } catch (XmlException ex) {
            throw new DecodingException(ex);
        }
    }

    protected <T> T decode(XmlObject xbObject) throws DecodingException {
        final DecoderKey key = getDecoderKey(xbObject);
        final Decoder<T, XmlObject> decoder = getDecoderRepository().getDecoder(key);
        if (decoder == null) {
            throw new NoDecoderForKeyException(key);
        }
        return decoder.decode(xbObject);
    }

    protected DecoderKey getDecoderKey(XmlObject doc) {
        return new XmlNamespaceDecoderKey(XmlHelper.getNamespace(doc), doc.getClass());
    }

    @Override
    public GetResultResponse getResult(GetResultRequest request) throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            GetResultResponse response = new GetResultResponse(request.getService(), request.getVersion());
            Set<String> featureIdentifier = QueryHelper.getFeatures(this.featureQueryHandler, request, session);
            List<ResultTemplate> resultTemplates = queryResultTemplate(request, featureIdentifier, session);
            if (isNotEmpty(resultTemplates)) {

                SweAbstractEncoding encoding = decode(resultTemplates.get(0).getResultEncoding());
                SweAbstractDataComponent structure = decode(resultTemplates.get(0).getResultStructure());

                List<Observation<?>> observations;

                if (entitiyHelper.isSeriesObservationSupported()) {
                    observations = querySeriesObservation(request, featureIdentifier, session);
                } else {
                    observations = queryObservation(request, featureIdentifier, session);
                }

                response.setResultValues(ResultHandlingHelper.createResultValuesFromObservations(observations, encoding, structure));
            }
            return response;
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying result data!")
                    .setStatus(INTERNAL_SERVER_ERROR);
        } catch (DecodingException ex) {
            throw new NoApplicableCodeException().causedBy(ex);
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        if (SosConstants.SOS.equals(service) && Sos2Constants.SERVICEVERSION.equals(version)) {
            return Sets.newHashSet(ConformanceClasses.SOS_V2_SPATIAL_FILTERING_PROFILE);
        }
        return super.getConformanceClasses(service, version);
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
            final Set<String> featureIdentifiers, final Session session) throws OwsExceptionReport {
        final Criteria c = createCriteriaFor(AbstractLegacyObservation.class, session);
        addSpatialFilteringProfileRestrictions(c, request, session);

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
     * @param session
     *            Hibernate session
     * @return List of Observation objects
     * @throws OwsExceptionReport
     *             If an error occurs.
     */
    @SuppressWarnings("unchecked")
    protected List<Observation<?>> querySeriesObservation(GetResultRequest request,
            Collection<String> featureIdentifiers, Session session) throws OwsExceptionReport {
        final Criteria c = createCriteriaFor(AbstractSeriesObservation.class, session);
        addSpatialFilteringProfileRestrictions(c, request, session);

        List<Series> series = daoFactory.getSeriesDAO().getSeries(request.getObservedProperty(), featureIdentifiers, session);
        if (CollectionHelper.isEmpty(series)) {
            return null;
        } else {
            c.add(Restrictions.in(AbstractSeriesObservation.SERIES, series));
        }

        if (request.isSetOffering()) {
            addOfferingRestriction(c, request.getOffering());
        }
        if (request.getTemporalFilter() != null && !request.getTemporalFilter().isEmpty()) {
            addTemporalFilter(c, request.getTemporalFilter());
        }

        LOGGER.debug("QUERY queryObservation(request, featureIdentifiers): {}", HibernateHelper.getSqlString(c));
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
                criteria.add(SpatialRestrictions.filter(
                        AbstractObservation.SAMPLING_GEOMETRY,
                        request.getSpatialFilter().getOperator(),
                        geometryHandler.switchCoordinateAxisFromToDatasourceIfNeeded(request.getSpatialFilter().getGeometry())));
        }
    }


}
