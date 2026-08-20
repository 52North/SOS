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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Inject;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Session;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.ConverterException;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.exception.ResponseExceedsSizeLimitException;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.util.observation.HibernateOmObservationCreatorContext;
import org.n52.sos.ds.hibernate.values.HibernateStreamingSettings;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.ObservationEncoder;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configurable
@SuppressFBWarnings({ "EI_EXPOSE_REP2" })
public abstract class AbstractObservationDao extends AbstractDaoImpl {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractObservationDao.class);

    private EncoderRepository encoderRepository;

    private Integer maxNumberOfReturnedTimeSeries = -1;

    private Integer maxNumberOfReturnedValues = -1;

    private int chunkSize;

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Setting("profile.hydrology.maxReturnedTimeSeries")
    public void setMaxNumberOfReturnedTimeSeries(Integer value) {
        this.maxNumberOfReturnedTimeSeries = value;
    }

    public int getMaxNumberOfReturnedTimeSeries() {
        return maxNumberOfReturnedTimeSeries;
    }

    @Setting("profile.hydrology.maxReturnedValue")
    public void setMaxNumberOfReturnedValues(Integer value) {
        this.maxNumberOfReturnedValues = value;
    }

    public int getMaxNumberOfReturnedValues() {
        return maxNumberOfReturnedValues;
    }

    /**
     * Set the chunk size for chunk streaming
     *
     * @param chunkSize
     *            Size to set
     */
    @Setting(HibernateStreamingSettings.CHUNK_SIZE)
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    /**
     * Get the chunk size
     *
     * @return the chunk wize
     */
    public int getChunkSize() {
        return chunkSize;
    }

    /**
     * Get ObservationConstellations and check if size limit is exceeded
     *
     * @param request
     *            GetObservation request
     * @param session
     *            Hibernate session
     *
     * @return List of {@link DatasetEntity}
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public List<DatasetEntity> getAndCheckObservationConstellationSize(GetObservationRequest request,
            DaoFactory daoFactory, Session session) throws OwsExceptionReport {
        List<DatasetEntity> observationConstellations = getObservationConstellations(session, request, daoFactory);
        checkMaxNumberOfReturnedSeriesSize(observationConstellations.size());
        return observationConstellations;
    }

    /**
     * Check if the max number of returned time series is exceeded
     *
     * @param seriesObservations
     *            Observations to check
     * @param metadataObservationsCount
     *            Count of metadata observations
     *
     * @throws CodedException
     *             If the size limit is exceeded
     */
    public void checkMaxNumberOfReturnedTimeSeries(Collection<? extends DataEntity<?>> seriesObservations,
            int metadataObservationsCount) throws CodedException {
        if (getMaxNumberOfReturnedTimeSeries() > 0) {
            Set<Long> seriesIds = seriesObservations.stream().map(DataEntity::getDataset).map(DatasetEntity::getId)
                    .collect(Collectors.toSet());
            checkMaxNumberOfReturnedSeriesSize(seriesIds.size() + metadataObservationsCount);
        }
    }

    /**
     * Check if the size limit is exceeded
     *
     * @param size
     *            The size limit to check
     *
     * @throws CodedException
     *             If the size limit is exceeded
     */
    public void checkMaxNumberOfReturnedSeriesSize(int size) throws CodedException {
        // FIXME refactor profile handling
        if (getMaxNumberOfReturnedTimeSeries() > 0 && size > getMaxNumberOfReturnedTimeSeries()) {
            throw new ResponseExceedsSizeLimitException().at("maxNumberOfReturnedTimeSeries");
        }
    }

    /**
     * Check if the max number of returned values is exceeded
     *
     * @param size
     *            Max number count
     *
     * @throws CodedException
     *             If the size limit is exceeded
     */
    public void checkMaxNumberOfReturnedValues(int size) throws CodedException {
        // FIXME refactor profile handling
        if (getMaxNumberOfReturnedValues() > 0 && size > getMaxNumberOfReturnedValues()) {
            throw new ResponseExceedsSizeLimitException().at("maxNumberOfReturnedValues");
        }
    }

    public int getMaxNumberOfValuesPerSeries(int size) {
        if (getMaxNumberOfReturnedValues() > 0) {
            return getMaxNumberOfReturnedValues() / size;
        }
        return getMaxNumberOfReturnedValues();
    }

    public List<String> getAndCheckFeatureOfInterest(DatasetEntity observationConstellation,
            Set<String> featureIdentifier, DaoFactory daoFactory, Session session) throws OwsExceptionReport {
        FeatureOfInterestDAO dao = daoFactory.getFeatureOfInterestDAO();
        final List<String> featuresForConstellation = dao.getIdentifiers(observationConstellation, session);
        if (featureIdentifier == null) {
            return featuresForConstellation;
        } else {
            return CollectionHelper.conjunctCollections(featuresForConstellation, featureIdentifier);
        }
    }

    public ObservationStream toSosObservation(Collection<DataEntity<?>> observations,
            AbstractObservationRequest request, Locale language, String pdf,
            HibernateOmObservationCreatorContext observationCreatorContext, Session session)
            throws OwsExceptionReport, ConverterException {
        if (observations.isEmpty()) {
            return ObservationStream.empty();
        }
        final long startProcess = System.currentTimeMillis();
        ObservationStream sosObservations = HibernateObservationUtilities.createSosObservationsFromObservations(
                new HashSet<>(observations), request, language, pdf, observationCreatorContext, session);

        LOGGER.debug("Time to process {} observations needs {} ms!", observations.size(),
                System.currentTimeMillis() - startProcess);
        return sosObservations;
    }

    public OmObservation toSosObservation(DataEntity<?> observation, AbstractObservationRequest request,
            Locale language, String pdf, HibernateOmObservationCreatorContext observationCreatorContext,
            Session session) throws OwsExceptionReport, ConverterException {
        if (observation != null) {
            final long startProcess = System.currentTimeMillis();
            OmObservation sosObservation = HibernateObservationUtilities.createSosObservationFromObservation(
                    observation, request, language, pdf, observationCreatorContext, session);
            LOGGER.debug("Time to process one observation needs {} ms!", System.currentTimeMillis() - startProcess);
            return sosObservation;
        }
        return null;
    }

    /**
     * Get ObervationConstellation from requested parameters
     *
     * @param session
     *            Hibernate session
     * @param request
     *            GetObservation request
     *
     * @return Resulting ObservationConstellation entities
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public List<DatasetEntity> getObservationConstellations(final Session session, final GetObservationRequest request,
            DaoFactory daoFactory) throws OwsExceptionReport {
        return daoFactory.getSeriesDAO().getSeries(request, request.getFeatureIdentifiers(), session);
    }

    /**
     * Get the requested temporal filters, excluding first/latest indeterminate time filters
     *
     * @param request
     *            GetObservation request
     *
     * @return Requested temporal filters, or {@code null} if none were requested
     *
     * @throws OwsExceptionReport
     *             If a temporal filter is not supported
     */
    public List<TemporalFilter> getTemporalFilters(final GetObservationRequest request)
            throws OwsExceptionReport {
        final List<TemporalFilter> filters = request.getNotFirstLatestTemporalFilter();
        if (request.hasTemporalFilters() && CollectionHelper.isNotEmpty(filters)) {
            return filters;
        } else {
            return null;
        }
    }

    /**
     * Check if the {@link ObservationEncoder} demands for merging of observations with the same timeseries.
     *
     * @param responseFormat
     *            Response format
     *
     * @return <code>true</code>, if the {@link ObservationEncoder} demands for merging of observations with
     *         the same timeseries.
     */
    public boolean checkEncoderForMergeObservationValues(String responseFormat) {
        XmlEncoderKey key = new XmlEncoderKey(responseFormat, OmObservation.class);
        Encoder<XmlObject, OmObservation> encoder = getEncoder(key);
        if (encoder != null && encoder instanceof ObservationEncoder) {
            return ((ObservationEncoder<?, OmObservation>) encoder).shouldObservationsWithSameXBeMerged();
        }
        return false;
    }

    protected Encoder<XmlObject, OmObservation> getEncoder(XmlEncoderKey key) {
        return encoderRepository.getEncoder(key);
    }
}
