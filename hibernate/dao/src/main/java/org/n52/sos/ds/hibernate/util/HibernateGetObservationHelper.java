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
package org.n52.sos.ds.hibernate.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.HibernateCriterionHelper;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.coding.CodingRepository;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.hibernate.dao.AbstractSpatialFilteringProfileDAO;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.entities.AbstractObservation;
import org.n52.sos.ds.hibernate.entities.AbstractSpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.XmlEncoderKey;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.sos.ResponseExceedsSizeLimitException;
import org.n52.sos.ogc.filter.BinaryLogicFilter;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.Filter;
import org.n52.sos.ogc.filter.FilterConstants;
import org.n52.sos.ogc.filter.FilterConstants.ComparisonOperator;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class HibernateGetObservationHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateGetObservationHelper.class);

    public static List<ObservationConstellation> getAndCheckObservationConstellationSize(GetObservationRequest request,
            Session session) throws CodedException {
        List<ObservationConstellation> observationConstellations = getObservationConstellations(session, request);
        checkMaxNumberOfReturnedSeriesSize(observationConstellations.size());
        return observationConstellations;
    }

    public static void checkMaxNumberOfReturnedTimeSeries(Collection<SeriesObservation> seriesObservations,
            int metadataObservationsCount) throws CodedException {
        if (Integer.MAX_VALUE != ServiceConfiguration.getInstance().getMaxNumberOfReturnedTimeSeries()) {
            Set<Long> seriesIds = Sets.newHashSet();
            for (SeriesObservation seriesObs : seriesObservations) {
                seriesIds.add(seriesObs.getSeries().getSeriesId());
            }
            checkMaxNumberOfReturnedSeriesSize(seriesIds.size() + metadataObservationsCount);
        }
    }

    public static void checkMaxNumberOfReturnedSeriesSize(int size) throws CodedException {
        // FIXME refactor profile handling
        if (size > ServiceConfiguration.getInstance().getMaxNumberOfReturnedTimeSeries()) {
            throw new ResponseExceedsSizeLimitException().at("maxNumberOfReturnedTimeSeries");
        }
    }

    public static void checkMaxNumberOfReturnedValues(int size) throws CodedException {
        // FIXME refactor profile handling
        if (size > ServiceConfiguration.getInstance().getMaxNumberOfReturnedValues()) {
            throw new ResponseExceedsSizeLimitException().at("maxNumberOfReturnedValues");
        }
    }

    /**
     * Get and check featureOfInterest identifiers for Hydrology-Profile
     * 
     * @param observationConstellation
     *            ObservationConstellation
     * @param featureIdentifier
     *            FeatureOfInterest identifiers
     * @param session
     *            Hibernate session
     * @return Checked featureOfInterest identifiers
     * @throws CodedException
     *             If an error occurs
     */
    public static List<String> getAndCheckFeatureOfInterest(final ObservationConstellation observationConstellation,
            final Set<String> featureIdentifier, final Session session) throws CodedException {
        final List<String> featuresForConstellation =
                new FeatureOfInterestDAO().getFeatureOfInterestIdentifiersForObservationConstellation(
                        observationConstellation, session);
        if (featureIdentifier == null) {
            return featuresForConstellation;
        } else {
            return CollectionHelper.conjunctCollections(featuresForConstellation, featureIdentifier);
        }
    }

    /**
     * Convert observation entities to internal observations
     * 
     * @param observations
     *            Observation entities
     * @param version
     *            Service version
     * @param resultModel
     *            Requested result model
     * @param session
     *            Hibernate session
     * @return Internal observation objects
     * @throws OwsExceptionReport
     *             If an error occurs
     * @throws ConverterException
     *             If an error occurs during the conversion
     */
    public static List<OmObservation> toSosObservation(final Collection<AbstractObservation> observations,
            final String version, final String resultModel, final Session session) throws OwsExceptionReport,
            ConverterException {
        if (!observations.isEmpty()) {
            Map<Long, AbstractSpatialFilteringProfile> spatialFilteringProfile = Maps.newHashMap();
            AbstractSpatialFilteringProfileDAO<?> spatialFilteringProfileDAO =
                    DaoFactory.getInstance().getSpatialFilteringProfileDAO(session);
            if (spatialFilteringProfileDAO != null) {
                spatialFilteringProfile =
                        spatialFilteringProfileDAO.getSpatialFilertingProfiles(
                                HibernateObservationUtilities.getObservationIds(observations), session);
            }
            final long startProcess = System.currentTimeMillis();
            final List<OmObservation> sosObservations =
                    HibernateObservationUtilities.createSosObservationsFromObservations(
                            new HashSet<AbstractObservation>(observations), spatialFilteringProfile, version,
                            resultModel, session);
            LOGGER.debug("Time to process {} observations needs {} ms!", observations.size(),(System.currentTimeMillis() - startProcess));
            return sosObservations;
        } else {
            return Collections.emptyList();
        }
    }
    
    public static OmObservation toSosObservation(AbstractObservation observation,
            final String version, final String resultModel, final Session session) throws OwsExceptionReport,
            ConverterException {
        if (observation != null) {
            AbstractSpatialFilteringProfile spatialFilteringProfile = null;;
            AbstractSpatialFilteringProfileDAO<?> spatialFilteringProfileDAO =
                    DaoFactory.getInstance().getSpatialFilteringProfileDAO(session);
            if (spatialFilteringProfileDAO != null) {
                spatialFilteringProfile =
                        spatialFilteringProfileDAO.getSpatialFilertingProfile(observation.getObservationId(), session);
            }
            final long startProcess = System.currentTimeMillis();
            final OmObservation sosObservation =
                    HibernateObservationUtilities.createSosObservationFromObservation(
                            observation, spatialFilteringProfile, version,
                            resultModel, session);
            LOGGER.debug("Time to process one observation needs {} ms!", (System.currentTimeMillis() - startProcess));
            return sosObservation;
        }
        return null;
    }

    /**
     * Add a result filter to the Criteria
     * 
     * @param c
     *            Hibernate criteria
     * @param resultFilter
     *            Result filter to add
     * @throws CodedException
     *             If the requested filter is not supported!
     */
    @SuppressWarnings("rawtypes")
    public static void addResultFilterToCriteria(Criteria c, Filter resultFilter) throws CodedException {
        if (resultFilter instanceof ComparisonFilter) {
            c.add(getCriterionForComparisonFilter((ComparisonFilter) resultFilter));
        } else if (resultFilter instanceof BinaryLogicFilter) {
            BinaryLogicFilter binaryLogicFilter = (BinaryLogicFilter) resultFilter;
            Junction junction = null;
            if (FilterConstants.BinaryLogicOperator.And.equals(binaryLogicFilter.getOperator())) {
                junction = Restrictions.conjunction();
            } else if (FilterConstants.BinaryLogicOperator.Or.equals(binaryLogicFilter.getOperator())) {
                junction = Restrictions.disjunction();
            } else {
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
     *            Requested result filter
     * @return Hibernate Criterion
     * @throws CodedException
     *             If the requested result filter is not supported
     */
    public static Criterion getCriterionForComparisonFilter(ComparisonFilter resultFilter) throws CodedException {
        if (ComparisonOperator.PropertyIsLike.equals(resultFilter.getOperator())) {
            checkValueReferenceForResultFilter(resultFilter.getValueReference());
            if (resultFilter.isSetEscapeString()) {
                return HibernateCriterionHelper.getLikeExpression(Observation.DESCRIPTION,
                        checkValueForWildcardSingleCharAndEscape(resultFilter), MatchMode.ANYWHERE,
                        Constants.DOLLAR_CHAR, true);
            } else {
                return Restrictions.like(Observation.DESCRIPTION,
                        checkValueForWildcardSingleCharAndEscape(resultFilter), MatchMode.ANYWHERE);
            }
        } else {
            throw new NoApplicableCodeException().withMessage(
                    "The requested comparison filter {} is not supported! Only {} is supported!", resultFilter
                            .getOperator().name(), ComparisonOperator.PropertyIsLike.name());
        }
    }

    /**
     * Check if the default SQL values for wildcard, single char or escape are
     * used. If not replace the characters from the result filter with the
     * default values.
     * 
     * @param resultFilter
     *            Requested result filter
     * @return Modified request string with default character.
     */
    public static String checkValueForWildcardSingleCharAndEscape(ComparisonFilter resultFilter) {
        String value = resultFilter.getValue();
        if (resultFilter.isSetSingleChar() && !resultFilter.getSingleChar().equals(Constants.PERCENT_STRING)) {
            value = value.replace(resultFilter.getSingleChar(), Constants.UNDERSCORE_STRING);
        }
        if (resultFilter.isSetWildCard() && !resultFilter.getWildCard().equals(Constants.UNDERSCORE_STRING)) {
            value = value.replace(resultFilter.getWildCard(), Constants.UNDERSCORE_STRING);
        }
        if (resultFilter.isSetEscapeString() && !resultFilter.getEscapeString().equals(Constants.DOLLAR_STRING)) {
            value = value.replace(resultFilter.getWildCard(), Constants.UNDERSCORE_STRING);
        }
        return value;
    }

    /**
     * Check if the requested value reference is supported.
     * 
     * @param valueReference
     *            Requested value reference
     * @throws CodedException
     *             If the requested value reference is not supported.
     */
    public static void checkValueReferenceForResultFilter(String valueReference) throws CodedException {
        if (Strings.isNullOrEmpty(valueReference)) {
            throw new NoApplicableCodeException().withMessage(
                    "The requested valueReference is missing! The valueReference should be %s/%s!",
                    OmConstants.VALUE_REF_OM_OBSERVATION, GmlConstants.VALUE_REF_GML_DESCRIPTION);
        } else if (!valueReference.startsWith(OmConstants.VALUE_REF_OM_OBSERVATION)
                && !valueReference.contains(GmlConstants.VALUE_REF_GML_DESCRIPTION))
            throw new NoApplicableCodeException().withMessage(
                    "The requested valueReference is not supported! Currently only %s/%s is supported",
                    OmConstants.VALUE_REF_OM_OBSERVATION, GmlConstants.VALUE_REF_GML_DESCRIPTION);
    }

    /**
     * Get ObervationConstellation from requested parameters
     * 
     * @param session
     *            Hibernate session
     * @param request
     *            GetObservation request
     * @return Resulting ObservationConstellation entities
     */
    public static List<ObservationConstellation> getObservationConstellations(final Session session,
            final GetObservationRequest request) {
        return new ObservationConstellationDAO().getObservationConstellations(request.getProcedures(),
                request.getObservedProperties(), request.getOfferings(), session);
    }

    /**
     * Get Hibernate Criterion from requested temporal filters
     * 
     * @param request
     *            GetObservation request
     * @return Hibernate Criterion from requested temporal filters
     * @throws OwsExceptionReport
     *             If a temporal filter is not supported
     */
    public static Criterion getTemporalFilterCriterion(final GetObservationRequest request) throws OwsExceptionReport {

        final List<TemporalFilter> filters = request.getNotFirstLatestTemporalFilter();
        if (request.hasTemporalFilters() && CollectionHelper.isNotEmpty(filters)) {
            return TemporalRestrictions.filter(filters);
        } else {
            return null;
        }
    }

    public static boolean checkEncoderForMergeObservationValues(String responseFormat) {
        Encoder<XmlObject, OmObservation> encoder =
                CodingRepository.getInstance().getEncoder(new XmlEncoderKey(responseFormat, OmObservation.class));
        if (encoder == null && encoder instanceof ObservationEncoder) {
            return ((ObservationEncoder<?, OmObservation>) encoder).shouldObservationsWithSameXBeMerged();
        }
        return false;
    }
}
