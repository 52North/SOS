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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.faroe.annotation.Configurable;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.util.observation.HibernateOmObservationCreatorContext;
import org.n52.sos.ds.hibernate.values.dataset.HibernateChunkSeriesStreamingValue;
import org.n52.sos.ds.hibernate.values.dataset.HibernateSeriesStreamingValue;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.ObservationEncoder;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configurable
@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
public class GetObservationByIdDaoImpl extends AbstractObservationDao
        implements org.n52.sos.ds.dao.GetObservationByIdDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationByIdDaoImpl.class);

    private HibernateSessionHolder sessionHolder;

    private OwsServiceMetadataRepository serviceMetadataRepository;

    private HibernateOmObservationCreatorContext observationCreatorContext;

    private DaoFactory daoFactory;

    private EncoderRepository encoderRepository;

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setServiceMetadataRepository(OwsServiceMetadataRepository repo) {
        this.serviceMetadataRepository = repo;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setOmObservationCreatorContext(HibernateOmObservationCreatorContext observationCreatorContext) {
        this.observationCreatorContext = observationCreatorContext;
    }

    @Override
    public Collection<OmObservation> queryObservationsById(GetObservationByIdRequest request)
            throws OwsExceptionReport {
        Session session = null;
        try {
            session = sessionHolder.getSession();
            return getObservations(request, session);
        } catch (HibernateException he) {
            throw new NoApplicableCodeException().causedBy(he).withMessage("Error while querying observation data!");
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    @Override
    public Collection<OmObservation> queryObservationsById(GetObservationByIdRequest request, Object connection)
            throws OwsExceptionReport {
        if (checkConnection(connection)) {
            return getObservations(request, HibernateSessionHolder.getSession(connection));
        }
        return queryObservationsById(request);
    }

    public List<OmObservation> getObservations(GetObservationByIdRequest request, Session session)
            throws OwsExceptionReport {
        try {
            List<OmObservation> omObservations = querySeriesObservation(request, session);
            HibernateObservationUtilities.createSosObservationsFromObservations(
                    checkObservations(queryObservation(request, session), request), request,
                    getProcedureDescriptionFormat(request.getResponseFormat()), observationCreatorContext, session)
                    .forEachRemaining(omObservations::add);
            return omObservations;
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage("Error while processing observation data!")
                    .setStatus(HTTPStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private List<DataEntity<?>> checkObservations(List<DataEntity<?>> queryObservation,
            GetObservationByIdRequest request) {
        if (!request.isCheckForDuplicity()) {
            return queryObservation;
        }
        List<DataEntity<?>> checkedObservations = Lists.newArrayList();
        Set<String> identifiers = Sets.newHashSet();
        for (DataEntity<?> observation : queryObservation) {
            if (!identifiers.contains(observation.getIdentifier())) {
                identifiers.add(observation.getIdentifier());
                checkedObservations.add(observation);
            }
        }
        return checkedObservations;
    }

    /**
     * Query observations for observation identifiers
     *
     * @param request
     *            GetObservationById request
     * @param session
     *            Hibernate session
     * @return Resulting observations
     * @throws CodedException
     *             If an error occurs during querying the database
     */
    @SuppressWarnings("unchecked")
    private List<DataEntity<?>> queryObservation(GetObservationByIdRequest request, Session session)
            throws OwsExceptionReport {
        Criteria c = daoFactory.getObservationDAO().getObservationClassCriteriaForResultModel(request.getResultModel(),
                session);
        c.add(Restrictions.in(DataEntity.IDENTIFIER, request.getObservationIdentifier()));
        LOGGER.trace("QUERY queryObservation(request): {}", HibernateHelper.getSqlString(c));
        return c.list();
    }

    /**
     * Query the series observations for streaming datasource
     *
     * @param request
     *            The GetObservation request
     * @param session
     *            Hibernate Session
     * @return List of internal observations
     * @throws OwsExceptionReport
     *             If an error occurs.
     * @throws ConverterException
     *             If an error occurs during sensor description creation.
     */
    private List<OmObservation> querySeriesObservation(GetObservationByIdRequest request, final Session session)
            throws OwsExceptionReport, ConverterException {
        final long start = System.currentTimeMillis();
        final List<OmObservation> result = new LinkedList<OmObservation>();
        // get valid featureOfInterest identifier
        List<DatasetEntity> serieses = daoFactory.getSeriesDAO().getSeries(request, session);
        checkMaxNumberOfReturnedSeriesSize(serieses.size());
        for (DatasetEntity series : serieses) {
            ObservationStream createSosObservationFromSeries = series.hasEreportingProfile()
                    ? HibernateObservationUtilities.createSosObservationFromEReportingSeries(series, request,
                            getProcedureDescriptionFormat(request.getResponseFormat()), observationCreatorContext,
                            session)
                    : HibernateObservationUtilities.createSosObservationFromSeries(series, request,
                            getProcedureDescriptionFormat(request.getResponseFormat()), observationCreatorContext,
                            session);
            OmObservation observationTemplate = createSosObservationFromSeries.next();
            HibernateSeriesStreamingValue streamingValue = new HibernateChunkSeriesStreamingValue(
                    sessionHolder.getConnectionProvider(), daoFactory, request, series, getChunkSize());
            streamingValue.setResponseFormat(request.getResponseFormat());
            streamingValue.setObservationTemplate(observationTemplate);
            observationTemplate.setValue(streamingValue);
            result.add(observationTemplate);
        }
        LOGGER.debug("Time to query observations needs {} ms!", System.currentTimeMillis() - start);
        return result;
    }

    private String getProcedureDescriptionFormat(String responseFormat) {
        Encoder<Object, Object> encoder =
                encoderRepository.getEncoder(new XmlEncoderKey(responseFormat, OmObservation.class));
        if (encoder != null && encoder instanceof ObservationEncoder) {
            return ((ObservationEncoder<?, ?>) encoder).getProcedureEncodingNamspace();
        }
        return null;
    }

}
