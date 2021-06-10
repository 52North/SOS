/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.aquarius.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.n52.faroe.ConfigurationError;
import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.request.handler.OperationHandler;
import org.n52.iceland.request.handler.OperationHandlerRepository;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.ObservationValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.OmObservationConstellation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.SensorML20Constants;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;
import org.n52.shetland.ogc.sos.request.DescribeSensorRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.response.DescribeSensorResponse;
import org.n52.sos.aquarius.ds.AccessorConnector;
import org.n52.sos.aquarius.ds.AquariusHelper;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.Parameter;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.pojo.data.Point;
import org.n52.sos.ds.AbstractDescribeSensorHandler;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.svalbard.CodingSettings;

import com.google.common.base.Strings;

@Configurable
public class AquariusObservationHelper {

    private Map<String, Location> locations = new HashMap<>();

    private Map<String, AbstractFeature> features = new HashMap<>();

    private Map<String, AbstractFeature> procedures = new HashMap<>();

    private Map<String, OmObservableProperty> observableProperties = new HashMap<>();

    private String tokenSeparator;

    private String tupleSeparator;

    private String decimalSeparator;

    private FeatureQueryHandler featureQueryHandler;

    private ProfileHandler profileHandler;

    private AquariusHelper aquariusHelper;

    @Setting(CodingSettings.TOKEN_SEPARATOR)
    public void setTokenSeparator(final String separator) throws ConfigurationError {
        Validation.notNullOrEmpty("Token separator", separator);
        tokenSeparator = separator;
    }

    @Setting(CodingSettings.TUPLE_SEPARATOR)
    public void setTupleSeparator(final String separator) throws ConfigurationError {
        Validation.notNullOrEmpty("Tuple separator", separator);
        tupleSeparator = separator;
    }

    @Setting(CodingSettings.DECIMAL_SEPARATOR)
    public void setDecimalSeparator(final String separator) throws ConfigurationError {
        Validation.notNullOrEmpty("Decimal separator", separator);
        decimalSeparator = separator;
    }

    @Inject
    public void setFeatureQueryHandler(FeatureQueryHandler featureQueryHandler) {
        this.featureQueryHandler = featureQueryHandler;
    }

    @Inject
    public void setProfileHandler(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @Inject
    public void setAquariusHelper(AquariusHelper aquariusHelper) {
        this.aquariusHelper = aquariusHelper;
    }

    public Collection<OmObservation> toSosObservation(Map<TimeSeriesDescription, List<Point>> dataSeries,
            Map<String, Location> locations, GetObservationRequest request,
            OperationHandlerRepository operationHandlerRepository, String pdf, AccessorConnector connection)
            throws OwsExceptionReport {
        // this.locations = locations;
        clearCache();
        List<OmObservation> observations = new ArrayList<>();
        for (Entry<TimeSeriesDescription, List<Point>> ds : dataSeries.entrySet()) {
            observations.addAll(createObservations(ds.getKey(), ds.getValue(), request, pdf,
                    operationHandlerRepository, connection));
        }
        return observations;
    }

    private void clearCache() {
        this.features.clear();
        this.procedures.clear();
        this.observableProperties.clear();
    }

    private Collection<OmObservation> createObservations(TimeSeriesDescription timeSeries,
            List<Point> timeSeriesPoints, GetObservationRequest request, String pdf,
            OperationHandlerRepository operationHandlerRepository, AccessorConnector connection)
            throws OwsExceptionReport {
        List<OmObservation> observations = new ArrayList<>();
        OmObservation omObservation = createObservation(timeSeries, pdf, operationHandlerRepository, connection);
        for (Point timeSeriesPoint : timeSeriesPoints) {
            ObservationValue<?> value = createValue(timeSeriesPoint, timeSeries.getUnit());
            if (value != null) {
                OmObservation observation = omObservation.cloneTemplate();
                observation.setValue(value);
                observations.add(observation);
            }
        }
        return observations;
    }

    private ObservationValue<?> createValue(Point timeSeriesPoint, String unit) {
        if (timeSeriesPoint != null) {
            if (timeSeriesPoint.getValue()
                    .isNumeric()) {
                return new SingleObservationValue<>(new TimeInstant(new DateTime(timeSeriesPoint.getTimestamp())),
                        new QuantityValue(getValue(timeSeriesPoint.getValue()
                                .getNumeric()), unit));
            } else if (timeSeriesPoint.getValue()
                    .isDisplay()) {
                return new SingleObservationValue<>(new TimeInstant(new DateTime(timeSeriesPoint.getTimestamp())),
                        new TextValue(timeSeriesPoint.getValue()
                                .getDisplay()));
            }
        }
        return null;
    }

    private BigDecimal getValue(Double value) {
        if (value != null && !value.isNaN()) {
            return BigDecimal.valueOf(value);
        }
        return null;
    }

    private OmObservation createObservation(TimeSeriesDescription timeSeries, String pdf,
            OperationHandlerRepository operationHandlerRepository, AccessorConnector connection)
            throws OwsExceptionReport {
        OmObservation omObservation = new OmObservation();
        omObservation.setObservationConstellation(
                getObservationConstellation(timeSeries, pdf, operationHandlerRepository, connection));
        omObservation.setNoDataValue(getNoDataValue());
        omObservation.setTokenSeparator(getTokenSeparator());
        omObservation.setTupleSeparator(getTupleSeparator());
        omObservation.setDecimalSeparator(getDecimalSeparator());
        omObservation.addParameter(createDataSetParameter(timeSeries));
        return omObservation;
    }

    private NamedValue<?> createDataSetParameter(TimeSeriesDescription timeSeries) {
        NamedValue<String> nv = new NamedValue<>();
        nv.setName(new ReferenceType("offering"));
        nv.setValue(new TextValue(timeSeries.getIdentifier()));
        return nv;
    }

    private OmObservationConstellation getObservationConstellation(TimeSeriesDescription timeSeries, String pdf,
            OperationHandlerRepository operationHandlerRepository, AccessorConnector connection)
            throws OwsExceptionReport {
        String identifier = getLocationIdentifier(timeSeries);
        if (!locations.containsKey(identifier)) {
            Location loc = aquariusHelper.getLocation(identifier);
            Location location = loc != null ? loc : connection.getLocation(identifier);
            locations.put(location.getIdentifier(), location);
        }
        Location station = locations.get(identifier);
        AbstractFeature procedure = getOrCreateProcedure(station, pdf, operationHandlerRepository, connection);
        OmObservableProperty obsProp = getOrCreateObservableProperty(timeSeries.getParameter(), connection);
        AbstractFeature feature = getOrCreateFeatureOfInterest(station, connection);

        return new OmObservationConstellation().setFeatureOfInterest(feature)
                .setObservableProperty(obsProp)
                .setProcedure(procedure)
                .addOffering(timeSeries.getIdentifier())
                .setObservationType(OmConstants.OBS_TYPE_MEASUREMENT);
    }

    private String getLocationIdentifier(TimeSeriesDescription timeSeries) {
        if (timeSeries.getLocationIdentifier() != null && !timeSeries.getLocationIdentifier()
                .isEmpty()) {
            return timeSeries.getLocationIdentifier();
        } else {
            return timeSeries.getIdentifier()
                    .substring(timeSeries.getIdentifier()
                            .lastIndexOf("@") + 1);
        }
    }

    private AbstractFeature getOrCreateProcedure(Location location, String pdf,
            OperationHandlerRepository operationHandlerRepository, AccessorConnector connection)
            throws OwsExceptionReport {
        String procedureId = location.getLocationType()
                .replace(" ", "_");
        if (!procedures.containsKey(procedureId) || procedures.get(procedureId) == null) {
            AbstractFeature procedure = null;
            if (getActiveProfile().isEncodeProcedureInObservation()) {
                DescribeSensorRequest request = new DescribeSensorRequest();
                request.setService(SosConstants.SOS);
                request.setVersion(Sos2Constants.SERVICEVERSION);
                request.setProcedure(procedureId);
                if (Strings.isNullOrEmpty(pdf)) {
                    request.setProcedureDescriptionFormat(SensorML20Constants.NS_SML_20);
                } else {
                    request.setProcedureDescriptionFormat(pdf);
                }
                AbstractDescribeSensorHandler describeSensorHandler =
                        getDescribeSensorHandler(operationHandlerRepository);
                DescribeSensorResponse sensorDescription = describeSensorHandler.getSensorDescription(request);
                procedure = sensorDescription.getProcedureDescriptions()
                        .iterator()
                        .next();
            } else {
                procedure = new SosProcedureDescriptionUnknownType(location.getLocationType(),
                        SensorML20Constants.NS_SML_20, null);
                procedure.setHumanReadableIdentifier(location.getLocationType());
                procedure.addName(location.getLocationType());
            }
            procedures.put(location.getLocationType(), procedure);
        }
        return procedures.get(location.getLocationType());
    }

    private OmObservableProperty getOrCreateObservableProperty(String parameter, AccessorConnector accessorConnector)
            throws OwsExceptionReport {
        if (!observableProperties.containsKey(parameter)) {
            Parameter parameterEntity = getParameter(parameter, accessorConnector);
            if (parameterEntity != null) {
                OmObservableProperty op = new OmObservableProperty(parameterEntity.getIdentifier());
                // or identifier?
                op.addName(parameterEntity.getDisplayName());
                op.setDescription(parameterEntity.getDisplayName());
                op.setUnit(parameterEntity.getUnitIdentifier());
                observableProperties.put(parameterEntity.getIdentifier(), op);
            }
        }
        return observableProperties.get(parameter);
    }

    private Parameter getParameter(String parameterId, AccessorConnector accessorConnector) throws OwsExceptionReport {
        Parameter parameter = aquariusHelper.getParameter(parameterId);
        if (parameter == null) {
            parameter = accessorConnector.getParameter(parameterId);
        }
        return parameter;
    }

    private AbstractFeature getOrCreateFeatureOfInterest(Location location, AccessorConnector connection)
            throws OwsExceptionReport {
        if (!features.containsKey(location.getIdentifier())) {
            FeatureQueryHandlerQueryObject queryObject = new FeatureQueryHandlerQueryObject(connection);
            queryObject.setFeatureObject(location);
            AbstractFeature feature = getFeatureQueryHandler().getFeatureByID(queryObject);
            if (feature instanceof AbstractSamplingFeature) {
                ((AbstractSamplingFeature) feature)
                        .setEncode(getActiveProfile().isEncodeFeatureOfInterestInObservations());
            }
            features.put(location.getIdentifier(), feature);
        }
        return features.get(location.getIdentifier());
    }

    protected AbstractDescribeSensorHandler getDescribeSensorHandler(
            OperationHandlerRepository operationHandlerRepository) throws CodedException {
        OperationHandler operationHandler = operationHandlerRepository.getOperationHandler(SosConstants.SOS,
                SosConstants.Operations.DescribeSensor.toString());
        if (operationHandler != null && operationHandler instanceof AbstractDescribeSensorHandler) {
            return (AbstractDescribeSensorHandler) operationHandler;
        }
        throw new NoApplicableCodeException().withMessage("Could not get DescribeSensor handler");
    }

    protected FeatureQueryHandler getFeatureQueryHandler() {
        return featureQueryHandler;
    }

    protected Profile getActiveProfile() {
        return profileHandler.getActiveProfile();
    }

    protected String getTokenSeparator() {
        return tokenSeparator;
    }

    protected String getTupleSeparator() {
        return tupleSeparator;
    }

    protected String getDecimalSeparator() {
        return decimalSeparator;
    }

    protected String getNoDataValue() {
        return getActiveProfile().getResponseNoDataPlaceholder();
    }

}
