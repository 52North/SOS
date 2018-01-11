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
package org.n52.sos.decode.kvp;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Supplier;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.binding.kvp.AbstractKvpDecoder;
import org.n52.janmayen.function.ThrowingBiConsumer;
import org.n52.janmayen.function.ThrowingTriConsumer;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator;
import org.n52.shetland.ogc.filter.FilterConstants.TimeOperator2;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.util.CRSHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.DateTimeParseException;
import org.n52.sos.ds.FeatureQuerySettingsProvider;
import org.n52.svalbard.CodingSettings;
import org.n52.svalbard.decode.DecoderKey;
import org.n52.svalbard.decode.exception.DecodingException;

import com.google.common.base.Strings;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 * @param <R> the request type
 */
@Configurable
public abstract class AbstractSosKvpDecoder<R extends OwsServiceRequest> extends AbstractKvpDecoder<R> {
    private int storageEPSG;
    private int storage3DEPSG;
    private int defaultResponseEPSG;
    private int defaultResponse3DEPSG;
    private String srsNamePrefixUrl;
    private String srsNamePrefixUrn;

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, String version, String operation) {
        this(supplier, SosConstants.SOS, version, operation);
    }

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, String version, Enum<?> operation) {
        this(supplier, SosConstants.SOS, version, operation);
    }

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, DecoderKey... keys) {
        super(supplier, keys);
    }

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, Collection<? extends DecoderKey> keys) {
        super(supplier, keys);
    }

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, String service, String version, String operation) {
        super(supplier, service, version, operation);
    }

    public AbstractSosKvpDecoder(Supplier<? extends R> supplier, String service, String version, Enum<?> operation) {
        super(supplier, service, version, operation);
    }

    /**
     * Set storage EPSG code from settings
     *
     * @param epsgCode
     *                 EPSG code from settings
     *
     * @throws ConfigurationError
     *                            If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.STORAGE_EPSG)
    public void setStorageEPSG(int epsgCode) throws ConfigurationError {
        Validation.greaterZero("Storage EPSG Code", epsgCode);
        this.storageEPSG = epsgCode;
    }

    /**
     * Set storage 3D EPSG code from settings
     *
     * @param epsgCode3D
     *                   3D EPSG code from settings
     *
     * @throws ConfigurationError
     *                            If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.STORAGE_3D_EPSG)
    public void setStorage3DEPSG(int epsgCode3D) throws ConfigurationError {
        Validation.greaterZero("Storage 3D EPSG Code", epsgCode3D);
        this.storage3DEPSG = epsgCode3D;
    }

    /**
     * Set default response EPSG code from settings
     *
     * @param epsgCode
     *                 EPSG code from settings
     *
     * @throws ConfigurationError
     *                            If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_RESPONSE_EPSG)
    public void setDefaultResponseEPSG(int epsgCode) throws ConfigurationError {
        Validation.greaterZero("Storage EPSG Code", epsgCode);
        this.defaultResponseEPSG = epsgCode;
    }

    /**
     * Set default response 3D EPSG code from settings
     *
     * @param epsgCode3D
     *                   3D EPSG code from settings
     *
     * @throws ConfigurationError
     *                            If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_RESPONSE_3D_EPSG)
    public void setDefaultResponse3DEPSG(int epsgCode3D) throws ConfigurationError {
        Validation.greaterZero("Storage 3D EPSG Code", epsgCode3D);
        this.defaultResponse3DEPSG = epsgCode3D;
    }

    @Setting(CodingSettings.SRS_NAME_PREFIX_URL)
    public void setSrsUrlNamePrefix(String prefix) {
        this.srsNamePrefixUrn = CRSHelper.asHttpPrefix(prefix);
    }

    @Setting(CodingSettings.SRS_NAME_PREFIX_URN)
    public void setSrsUrnNamePrefix(String prefix) {
        srsNamePrefixUrl = CRSHelper.asUrnPrefix(prefix);
    }

    @Override
    protected void getCommonRequestParameterDefinitions(Builder<R> builder) {
        super.getCommonRequestParameterDefinitions(builder);
        builder.add(OWSConstants.AdditionalRequestParams.language, OwsServiceRequest::addSweTextExtension);
        builder.add(OWSConstants.AdditionalRequestParams.crs, OwsServiceRequest::addSweTextExtension);
//        builder.add(OWSConstants.AdditionalRequestParams.returnHumanReadableIdentifier, OwsServiceRequest::addSweBooleanExtension);
    }

    protected ThrowingBiConsumer<R, String, DecodingException> decodeNamespaces(
            ThrowingBiConsumer<? super R, ? super Map<String, String>, DecodingException> delegate) {
        return (request, value) -> delegate.accept(request, decodeNamespaces(value));
    }

    protected Map<String, String> decodeNamespaces(String value) {
        return Arrays.stream(value.replaceAll("\\),", "").replaceAll("\\)", "").split("xmlns\\("))
                .map(Strings::emptyToNull)
                .filter(Objects::nonNull)
                .map(string -> string.split(","))
                .collect(toMap(s -> s[0], s -> s[1]));
    }

    protected ThrowingTriConsumer<R, String, List<String>, DecodingException> decodeTemporalFilter(
            ThrowingBiConsumer<? super R, ? super TemporalFilter, DecodingException> delegate) {
        return (request, name, value) -> delegate.accept(request, decodeTemporalFilter(name, value));
    }

    protected ThrowingTriConsumer<R, String, String, DecodingException> decodeTime(
            ThrowingBiConsumer<? super R, ? super Time, DecodingException> delegate) {
        return (request, name, value) -> delegate.accept(request, decodeTime(name, value));
    }

    protected Time decodeTime(String name, String value) throws DecodingException {
        String[] times = name.split("/");
        switch (times.length) {
            case 1:
                return decodeTimeInstant(name, value);
            case 2:
                return decodeTimePeriod(name, times);
            default:
                throw new DecodingException(value, "The parameter value is not valid!");
        }
    }

    private TimePeriod decodeTimePeriod(String name, String[] times) throws DecodingException {
        try {
            return new TimePeriod(DateTimeHelper.parseIsoString2DateTime(times[0]),
                                  DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition(times[1]));
        } catch (DateTimeParseException ex) {
            throw new DecodingException(ex, name);
        }
    }

    private TimeInstant decodeTimeInstant(String name, String time) throws DecodingException {
        try {
            return new TimeInstant(DateTimeHelper.parseIsoString2DateTime(time),
                                   DateTimeHelper.getTimeLengthBeforeTimeZone(time));
        } catch (DateTimeParseException ex) {
            return new TimeInstant(new IndeterminateValue(time));
        }
    }

    protected TemporalFilter decodeTemporalFilter(String name, List<String> parameterValues)
            throws DecodingException {
        if (parameterValues == null || parameterValues.isEmpty()) {
            return null;
        }
        String value;
        String valueReference;
        String operator;

        switch (parameterValues.size()) {
            case 2:
                valueReference = parameterValues.get(0);
                value = parameterValues.get(1);
                return createTemporalFilter(value, name, valueReference);
            case 3:
                valueReference = parameterValues.get(0);
                operator = parameterValues.get(1);
                value = parameterValues.get(2);
                return createTemporalFilter(name, value, operator, valueReference);
            default:
                throw new DecodingException(name, "The parameter value is not valid!");
        }
    }

    private TemporalFilter createTemporalFilter(String value, String name, String valueReference)
            throws DecodingException {
        switch (value.split("/").length) {
            case 1:
                return createTemporalFilter(name, value, TimeOperator.TM_Equals, valueReference);
            case 2:
                return createTemporalFilter(name, value, TimeOperator.TM_During, valueReference);
            default:
                throw new DecodingException(name, "The paramter value '%s' is invalid!", value);
        }
    }

    private TemporalFilter createTemporalFilter(String name, String value, String operator, String valueReference)
            throws DecodingException {
        TimeOperator timeOperator;
        try {
            timeOperator = TimeOperator.from(operator);
        } catch (IllegalArgumentException e1) {
            try {
                timeOperator = TimeOperator.from(TimeOperator2.from(operator));
            } catch (IllegalArgumentException e2) {
                throw new DecodingException(name, "Unsupported operator '%s'!", operator);
            }
        }
        return createTemporalFilter(name, value, timeOperator, valueReference);
    }

    private TemporalFilter createTemporalFilter(String name, String value, TimeOperator timeOperator,
                                                String valueReference) throws DecodingException {
        String[] times = value.split("/");
        final Time time;
        if (times.length == 1 && timeOperator != TimeOperator.TM_During) {
            time = decodeTimeInstant(name, times[0]);
        } else if (times.length == 2 & timeOperator == TimeOperator.TM_During) {
            time = decodeTimePeriod(name, times);
        } else {
            throw new DecodingException(name, "The parameter value '%s' is invalid!", value);
        }
        return new TemporalFilter(timeOperator, time, valueReference);
    }

    protected ThrowingTriConsumer<R, String, List<String>, DecodingException> decodeSpatialFilter(
            ThrowingBiConsumer<? super R, ? super SpatialFilter, DecodingException> delegate) {
        return (request, name, value) -> delegate.accept(request, decodeSpatialFilter(name, value));
    }

    protected SpatialFilter decodeSpatialFilter(String name, List<String> parameterValues)
            throws DecodingException {

        List<String> values;
        Geometry geometry;
        GeometryFactory factory;
        int srid;
        String valueReference;

        if (parameterValues == null || parameterValues.isEmpty()) {
            return null;
        } else if (parameterValues.size() < 5 || parameterValues.size() > 6) {
            throw new DecodingException(name, "The parameter value is not valid!");
        } else if (parameterValues instanceof RandomAccess) {
            values = parameterValues;
        } else {
            values = new ArrayList<>(parameterValues);
        }

        valueReference = values.get(0);
        values = values.subList(1, values.size());

        String crs = values.get(values.size() - 1);
        if (crs.startsWith(this.srsNamePrefixUrl) || crs.startsWith(this.srsNamePrefixUrn)) {
            values = values.subList(0, values.size() - 1);
            srid = CRSHelper.parseSrsName(crs);
        } else {
            srid = this.storageEPSG;
        }

        if (values.size() != 4) {
            throw new DecodingException(name, "The parameter value is not valid!");
        }

        if (srid > 0) {
            factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);
        } else {
            factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING));
        }

        double[] coordinates = values.stream().mapToDouble(Double::valueOf).toArray();

        geometry = factory.createPolygon(new Coordinate[] {
            new Coordinate(coordinates[0], coordinates[1]),
            new Coordinate(coordinates[0], coordinates[3]),
            new Coordinate(coordinates[2], coordinates[3]),
            new Coordinate(coordinates[2], coordinates[1]),
            new Coordinate(coordinates[0], coordinates[1])
        });

        return new SpatialFilter(SpatialOperator.BBOX, geometry, valueReference);
    }
}
