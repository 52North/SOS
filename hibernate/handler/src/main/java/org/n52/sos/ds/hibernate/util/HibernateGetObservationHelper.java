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
package org.n52.sos.ds.hibernate.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.HibernateCriterionHelper;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.n52.iceland.convert.ConverterException;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.filter.BinaryLogicFilter;
import org.n52.shetland.ogc.filter.ComparisonFilter;
import org.n52.shetland.ogc.filter.Filter;
import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.om.ObservationStream;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.exception.ResponseExceedsSizeLimitException;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.util.observation.OmObservationCreatorContext;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.ObservationEncoder;
import org.n52.svalbard.encode.XmlEncoderKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * Helper class for GetObservation DAOs
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public class HibernateGetObservationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateGetObservationHelper.class);

    private HibernateGetObservationHelper() {
    }

    /**
     * Get ObservationConstellations and check if size limit is exceeded
     *
     * @param request
     *                GetObservation request
     * @param session
     *                Hibernate session
     *
     * @return List of {@link ObservationConstellation}
     * @throws OwsExceptionReport
     */
    public static List<DatasetEntity> getAndCheckObservationConstellationSize(
            GetObservationRequest request, DaoFactory daoFactory, Session session) throws OwsExceptionReport {
        List<DatasetEntity> observationConstellations = getObservationConstellations(session, request, daoFactory);
        checkMaxNumberOfReturnedSeriesSize(observationConstellations.size());
        return observationConstellations;
    }

    /**
     * Check if the max number of returned time series is exceeded
     *
     * @param seriesObservations
     *                                  Observations to check
     * @param metadataObservationsCount
     *                                  Count of metadata observations
     *
     * @throws CodedException
     *                        If the size limit is exceeded
     */
    public static void checkMaxNumberOfReturnedTimeSeries(Collection<? extends DataEntity<?>> seriesObservations,
                                                          int metadataObservationsCount) throws CodedException {
        if (getMaxNumberOfReturnedTimeSeriess() > 0) {
            Set<Long> seriesIds = seriesObservations.stream()
                    .map(DataEntity::getDataset).map(DatasetEntity::getId).collect(Collectors.toSet());
            checkMaxNumberOfReturnedSeriesSize(seriesIds.size() + metadataObservationsCount);
        }
    }

    /**
     * Check if the size limit is exceeded
     *
     * @param size
     *             The size limit to check
     *
     * @throws CodedException
     *                        If the size limit is exceeded
     */
    public static void checkMaxNumberOfReturnedSeriesSize(int size) throws CodedException {
        // FIXME refactor profile handling
        if (getMaxNumberOfReturnedTimeSeriess() > 0 && size > getMaxNumberOfReturnedTimeSeriess()) {
            throw new ResponseExceedsSizeLimitException().at("maxNumberOfReturnedTimeSeries");
        }
    }

    /**
     * Check if the max number of returned values is exceeded
     *
     * @param size
     *             Max number count
     *
     * @throws CodedException
     *                        If the size limit is exceeded
     */
    public static void checkMaxNumberOfReturnedValues(int size) throws CodedException {
        // FIXME refactor profile handling
        if (getMaxNumberOfReturnedValues() > 0 && size > getMaxNumberOfReturnedValues()) {
            throw new ResponseExceedsSizeLimitException().at("maxNumberOfReturnedValues");
        }
    }

    public static int getMaxNumberOfValuesPerSeries(int size) {
        if (getMaxNumberOfReturnedValues() > 0) {
            return getMaxNumberOfReturnedValues() / size;
        }
        return getMaxNumberOfReturnedValues();
    }

    public static List<String> getAndCheckFeatureOfInterest(DatasetEntity observationConstellation,
                                                            Set<String> featureIdentifier,
                                                            DaoFactory  daoFactory,
                                                            Session session)
            throws OwsExceptionReport {
        FeatureOfInterestDAO dao = daoFactory.getFeatureOfInterestDAO();
        final List<String> featuresForConstellation = dao
                .getIdentifiers(observationConstellation, session);
        if (featureIdentifier == null) {
            return featuresForConstellation;
        } else {
            return CollectionHelper.conjunctCollections(featuresForConstellation, featureIdentifier);
        }
    }

    public static ObservationStream toSosObservation(Collection<DataEntity<?>> observations,
                                                       AbstractObservationRequest request,
                                                       Locale language,
                                                       String pdf,
                                                       OmObservationCreatorContext observationCreatorContext,
                                                       Session session) throws OwsExceptionReport, ConverterException {
        if (observations.isEmpty()) {
            return ObservationStream.empty();
        }
        final long startProcess = System.currentTimeMillis();
        ObservationStream sosObservations = HibernateObservationUtilities.createSosObservationsFromObservations(
                new HashSet<>(observations), request, language, pdf, observationCreatorContext, session);

        LOGGER.debug("Time to process {} observations needs {} ms!", observations.size(),
                                                                     (System.currentTimeMillis() - startProcess));
        return sosObservations;
    }

    public static OmObservation toSosObservation(DataEntity<?> observation,
                                                 AbstractObservationRequest request,
                                                 Locale language,
                                                 String pdf,
                                                 OmObservationCreatorContext observationCreatorContext,
                                                 Session session)
            throws OwsExceptionReport, ConverterException {
        if (observation != null) {
            final long startProcess = System.currentTimeMillis();
            OmObservation sosObservation = HibernateObservationUtilities
                    .createSosObservationFromObservation(observation, request, language, pdf, observationCreatorContext, session);
            LOGGER.debug("Time to process one observation needs {} ms!", (System.currentTimeMillis() - startProcess));
            return sosObservation;
        }
        return null;
    }

    /**
     * Add a result filter to the Criteria
     *
     * @param c
     *                     Hibernate criteria
     * @param resultFilter
     *                     Result filter to add
     *
     * @throws CodedException
     *                        If the requested filter is not supported!
     */
    @SuppressWarnings("rawtypes")
    public static void addResultFilterToCriteria(Criteria c, Filter resultFilter) throws CodedException {
        if (resultFilter instanceof ComparisonFilter) {
            c.add(getCriterionForComparisonFilter((ComparisonFilter) resultFilter));
        } else if (resultFilter instanceof BinaryLogicFilter) {
            BinaryLogicFilter binaryLogicFilter = (BinaryLogicFilter) resultFilter;
            Junction junction;
            if (null == binaryLogicFilter.getOperator()) {
                throw new NoApplicableCodeException()
                        .withMessage("The requested binary logic filter operator is invalid!");
            }
            switch (binaryLogicFilter.getOperator()) {
                case And:
                    junction = Restrictions.conjunction();
                    break;
                case Or:
                    junction = Restrictions.disjunction();
                    break;
                default:
                    throw new NoApplicableCodeException()
                            .withMessage("The requested binary logic filter operator is invalid!");
            }
            for (Filter<?> filterPredicate : binaryLogicFilter.getFilterPredicates()) {
                if (!(filterPredicate instanceof ComparisonFilter)) {
                    throw new NoApplicableCodeException().withMessage("The requested result filter is not supported!");
                }
                junction.add(getCriterionForComparisonFilter((ComparisonFilter) filterPredicate));
            }
            c.add(junction);
        } else {
            throw new NoApplicableCodeException().withMessage("The requested result filter is not supported!");
        }
    }

    /**
     * Get the Hibernate Criterion for the requested result filter
     *
     * @param resultFilter
     *                     Requested result filter
     *
     * @return Hibernate Criterion
     *
     * @throws CodedException
     *                        If the requested result filter is not supported
     */
    public static Criterion getCriterionForComparisonFilter(ComparisonFilter resultFilter) throws CodedException {
        if (FilterConstants.ComparisonOperator.PropertyIsLike.equals(resultFilter.getOperator())) {
            checkValueReferenceForResultFilter(resultFilter.getValueReference());
            if (resultFilter.isSetEscapeString()) {
                return HibernateCriterionHelper.getLikeExpression(DatasetEntity.DESCRIPTION,
                                                                  checkValueForWildcardSingleCharAndEscape(resultFilter), MatchMode.ANYWHERE, '$', true);
            } else {
                return Restrictions.like(DatasetEntity.DESCRIPTION,
                                         checkValueForWildcardSingleCharAndEscape(resultFilter), MatchMode.ANYWHERE);
            }
        } else {
            throw new NoApplicableCodeException().withMessage(
                    "The requested comparison filter {} is not supported! Only {} is supported!", resultFilter
                            .getOperator().name(), FilterConstants.ComparisonOperator.PropertyIsLike.name());
        }
    }

    /**
     * Check if the default SQL values for wildcard, single char or escape are
     * used. If not replace the characters from the result filter with the
     * default values.
     *
     * @param resultFilter
     *                     Requested result filter
     *
     * @return Modified request string with default character.
     */
    public static String checkValueForWildcardSingleCharAndEscape(ComparisonFilter resultFilter) {
        String value = resultFilter.getValue();
        if (resultFilter.isSetSingleChar() && !resultFilter.getSingleChar().equals("%")) {
            value = value.replace(resultFilter.getSingleChar(), "_");
        }
        if (resultFilter.isSetWildCard() && !resultFilter.getWildCard().equals("_")) {
            value = value.replace(resultFilter.getWildCard(), "_");
        }
        if (resultFilter.isSetEscapeString() && !resultFilter.getEscapeString().equals("$")) {
            value = value.replace(resultFilter.getWildCard(), "_");
        }
        return value;
    }

    /**
     * Check if the requested value reference is supported.
     *
     * @param valueReference
     *                       Requested value reference
     *
     * @throws CodedException
     *                        If the requested value reference is not supported.
     */
    public static void checkValueReferenceForResultFilter(String valueReference) throws CodedException {
        if (Strings.isNullOrEmpty(valueReference)) {
            throw new NoApplicableCodeException().withMessage(
                    "The requested valueReference is missing! The valueReference should be %s/%s!",
                    OmConstants.VALUE_REF_OM_OBSERVATION, GmlConstants.VALUE_REF_GML_DESCRIPTION);
        } else if (!valueReference.startsWith(OmConstants.VALUE_REF_OM_OBSERVATION) &&
                 !valueReference.contains(GmlConstants.VALUE_REF_GML_DESCRIPTION)) {
            throw new NoApplicableCodeException().withMessage(
                    "The requested valueReference is not supported! Currently only %s/%s is supported",
                    OmConstants.VALUE_REF_OM_OBSERVATION, GmlConstants.VALUE_REF_GML_DESCRIPTION);
        }
    }

    /**
     * Get ObervationConstellation from requested parameters
     *
     * @param session
     *                Hibernate session
     * @param request
     *                GetObservation request
     *
     * @return Resulting ObservationConstellation entities
     * @throws OwsExceptionReport
     */
    public static List<DatasetEntity> getObservationConstellations(final Session session,
                                                                              final GetObservationRequest request,
                                                                              DaoFactory daoFactory) throws OwsExceptionReport {
        return daoFactory.getSeriesDAO().getSeries(request, request.getFeatureIdentifiers(), session);
    }

    /**
     * Get Hibernate Criterion from requested temporal filters
     *
     * @param request
     *                GetObservation request
     *
     * @return Hibernate Criterion from requested temporal filters
     *
     * @throws OwsExceptionReport
     *                            If a temporal filter is not supported
     */
    public static Criterion getTemporalFilterCriterion(final GetObservationRequest request) throws OwsExceptionReport {

        final List<TemporalFilter> filters = request.getNotFirstLatestTemporalFilter();
        if (request.hasTemporalFilters() && CollectionHelper.isNotEmpty(filters)) {
            return SosTemporalRestrictions.filter(filters);
        } else {
            return null;
        }
    }

    /**
     * Check if the {@link ObservationEncoder} demands for merging of
     * observations with the same timeseries.
     *
     * @param responseFormat
     *                       Response format
     *
     * @return <code>true</code>, if the {@link ObservationEncoder} demands for
     *         merging of observations with the same timeseries.
     */
    public static boolean checkEncoderForMergeObservationValues(String responseFormat) {
        XmlEncoderKey key = new XmlEncoderKey(responseFormat, OmObservation.class);
        Encoder<XmlObject, OmObservation> encoder = getEncoder(key);
        if (encoder != null && encoder instanceof ObservationEncoder) {
            return ((ObservationEncoder<?, OmObservation>) encoder).shouldObservationsWithSameXBeMerged();
        }
        return false;
    }

    private static int getMaxNumberOfReturnedTimeSeriess() {
        return org.n52.iceland.service.ServiceConfiguration.getInstance().getMaxNumberOfReturnedTimeSeries();
    }

    private static int getMaxNumberOfReturnedValues() {
        return org.n52.iceland.service.ServiceConfiguration.getInstance().getMaxNumberOfReturnedValues();
    }

    private static <T, S> Encoder<T, S> getEncoder(XmlEncoderKey key) {
        return org.n52.iceland.coding.CodingRepository.getInstance().getEncoder(key);
    }
}
