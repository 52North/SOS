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
package org.n52.sos.ds;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsAnyValue;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsNoValues;
import org.n52.shetland.ogc.ows.OwsPossibleValues;
import org.n52.shetland.ogc.ows.OwsRange;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ResultFilterConstants;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosConstants.GetObservationParams;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.GetObservationResponse;
import org.n52.shetland.util.DateTimeFormatException;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.coding.encode.ResponseFormatRepository;
import org.n52.sos.util.SosHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * interface for getting observations for a passed getObservation request from
 * the data source
 *
 * Renamed, in version 4.x called AbstractGetObservationDAO
 *
 * @since 5.0.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public abstract class AbstractGetObservationHandler extends AbstractSosOperationHandler {

    private ResponseFormatRepository responseFormatRepository;

    public AbstractGetObservationHandler(final String service) {
        super(service, SosConstants.Operations.GetObservation.name());
    }

    @Inject
    public void setResponseFormatRepository(ResponseFormatRepository responseFormatRepository) {
        this.responseFormatRepository = responseFormatRepository;
    }

    protected ResponseFormatRepository getResponseFormatRepository() {
        return this.responseFormatRepository;
    }

    /**
     * Get the min/max phenomenon time of contained observations
     *
     * @return min/max phenomenon time
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs.
     */
    private Optional<OwsRange> getPhenomenonTime() throws OwsExceptionReport {
        DateTime minDate = getCache().getMinPhenomenonTime();
        DateTime maxDate = getCache().getMaxPhenomenonTime();
        return getDateRange(minDate, maxDate);
    }

    /**
     * Get the min/max result time of contained observations
     *
     * @return min/max result time
     *
     *
     * @throws OwsExceptionReport
     *             * If an error occurs.
     */
    protected Optional<OwsRange> getResultTime() throws OwsExceptionReport {
        DateTime minDate = getCache().getMinResultTime();
        DateTime maxDate = getCache().getMaxResultTime();
        return getDateRange(minDate, maxDate);
    }

    private List<OwsValue> getResultModels() {
        return OmConstants.RESULT_MODELS.stream()
                .map(qn -> qn.getPrefix() + ":" + qn.getLocalPart())
                .map(OwsValue::new)
                .collect(toList());
    }

    /**
     * process the GetObservation query
     *
     * @param request
     *                GetObservation object which represents the getObservation
     *                request
     *
     * @return ObservationDocument representing the requested values in an OGC
     *         conform O&M observation document
     *
     * @throws OwsExceptionReport
     *             * if query of the database or creating the O&M document
     *                            failed
     */
    public abstract GetObservationResponse getObservation(GetObservationRequest request) throws OwsExceptionReport;

    @Override
    protected Set<OwsDomain> getOperationParameters(String service, String version) throws OwsExceptionReport {

        Stream<OwsDomain> commonParameters = Stream.of(getOfferingParameter(service, version),
                                                       getQueryableProcedureParameter(service, version),
                                                       getResponseFormatParameter(service, version),
                                                       getPublishedObservablePropertyParameter(service, version),
                                                       getPublishedFeatureOfInterestParameter(service, version));
        Stream<OwsDomain> versionParameters;
        switch (version) {
            case Sos2Constants.SERVICEVERSION:
                versionParameters = Stream.of(getTemporalFilterParameter(service, version),
                                              getSpatialFilterParameter(service, version),
                                              getResultFilterParameter(service, version));
                break;
            case Sos1Constants.SERVICEVERSION:
                versionParameters = Stream.of(getEventTimeParameter(service, version),
                                              getSrsNameParameter(service, version),
                                              getResultParameter(service, version),
                                              getResponseModeParameter(service, version),
                                              getResultModelParameter(service, version));
                break;
            default:
                versionParameters = Stream.empty();
                break;
        }
        return Stream.concat(commonParameters, versionParameters).collect(toSet());
    }

    private Optional<OwsRange> getDateRange(DateTime minDate, DateTime maxDate) throws DateTimeFormatException {
        Optional<String> min = formatDate(minDate);
        Optional<String> max = formatDate(maxDate);

        if (min.isPresent() || max.isPresent()) {
            return Optional.of(new OwsRange(min.map(OwsValue::new).orElse(null),
                                            max.map(OwsValue::new).orElse(null)));
        } else {
            return Optional.empty();
        }
    }

    private OwsDomain getSpatialFilterParameter(String service, String version) {
        Enum<?> name = Sos2Constants.GetObservationParams.spatialFilter;
        return getEnvelopeParameter(name, SosHelper.getFeatureIDs(getCache().getFeaturesOfInterest(), version));
    }

    private OwsDomain getResultFilterParameter(String service, String version) {
        return new OwsDomain(ResultFilterConstants.METADATA_RESULT_FILTER, OwsAnyValue.instance());
    }

    private OwsDomain getResponseFormatParameter(String service, String version) {
        GetObservationParams name = SosConstants.GetObservationParams.responseFormat;
        Set<String> responseFormats =
                getResponseFormatRepository().getSupportedResponseFormats(SosConstants.SOS, version);
        return new OwsDomain(name, new OwsAllowedValues(responseFormats.stream().map(OwsValue::new)));
    }

    private OwsDomain getTemporalFilterParameter(String service, String version) throws OwsExceptionReport {
        Sos2Constants.GetObservationParams name = Sos2Constants.GetObservationParams.temporalFilter;
        Optional<OwsPossibleValues> allowedValues = getPhenomenonTime().<OwsPossibleValues> map(OwsAllowedValues::new);
        return new OwsDomain(name, allowedValues.orElseGet(OwsNoValues::instance));
    }

    private OwsDomain getResultModelParameter(String service, String version) {
        GetObservationParams name = SosConstants.GetObservationParams.resultModel;
        return new OwsDomain(name, new OwsAllowedValues(getResultModels()));
    }

    private OwsDomain getResponseModeParameter(String service, String version) {
        GetObservationParams name = SosConstants.GetObservationParams.responseMode;
        return new OwsDomain(name, new OwsAllowedValues(SosConstants.RESPONSE_MODES.stream().map(OwsValue::new)));
    }

    private OwsDomain getEventTimeParameter(String service, String version) throws OwsExceptionReport {
        Sos1Constants.GetObservationParams name = Sos1Constants.GetObservationParams.eventTime;
        Optional<OwsPossibleValues> allowedValues = getPhenomenonTime().<OwsPossibleValues> map(OwsAllowedValues::new);
        return new OwsDomain(name, allowedValues.orElseGet(OwsNoValues::instance));
    }

    private OwsDomain getSrsNameParameter(String service, String version) {
        GetObservationParams name = SosConstants.GetObservationParams.srsName;
        return new OwsDomain(name, OwsAnyValue.instance());
    }

    private OwsDomain getResultParameter(String service, String version) {
        return new OwsDomain(SosConstants.GetObservationParams.result, OwsAnyValue.instance());
    }

    private static Optional<String> formatDate(DateTime date) throws DateTimeFormatException {
        if (date != null) {
            return Optional.of(DateTimeHelper.formatDateTime2ResponseString(date));
        } else {
            return Optional.empty();
        }
    }

}
