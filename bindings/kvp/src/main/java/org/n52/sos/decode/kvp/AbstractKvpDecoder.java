/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

import org.joda.time.DateTime;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.decode.Decoder;
import org.n52.sos.ds.FeatureQuerySettingsProvider;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.MissingParameterValueException;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.exception.ows.concrete.MissingServiceParameterException;
import org.n52.sos.exception.ows.concrete.MissingVersionParameterException;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator2;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.ows.CompositeOwsException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.SosIndeterminateTime;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.service.ServiceConstants;
import org.n52.sos.util.Constants;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 4.0.0
 * 
 */
@Configurable
public abstract class AbstractKvpDecoder implements Decoder<AbstractServiceRequest<?>, Map<String, String>> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractKvpDecoder.class);

    protected static final int VALID_COORDINATE_SIZE = 4;

    private int storageEPSG;

    private int storage3DEPSG;

    private int defaultResponseEPSG;

    private int defaultResponse3DEPSG;

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.emptySet();
    }

    @Override
    public Map<ServiceConstants.SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.emptyMap();
    }

    public int getStorageEPSG() {
        return storageEPSG;
    }

    public int getStorage3DEPSG() {
        return storage3DEPSG;
    }

    public int getDefaultResponseEPSG() {
        return defaultResponseEPSG;
    }

    public int getDefaultResponse3DEPSG() {
        return defaultResponse3DEPSG;
    }

    /**
     * Set storage EPSG code from settings
     * 
     * @param epsgCode
     *            EPSG code from settings
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.STORAGE_EPSG)
    public void setStorageEpsg(final int epsgCode) throws ConfigurationException {
        Validation.greaterZero("Storage EPSG Code", epsgCode);
        storageEPSG = epsgCode;
    }

    /**
     * Set storage 3D EPSG code from settings
     * 
     * @param epsgCode3D
     *            3D EPSG code from settings
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.STORAGE_3D_EPSG)
    public void setStorage3DEpsg(final int epsgCode3D) throws ConfigurationException {
        Validation.greaterZero("Storage 3D EPSG Code", epsgCode3D);
        storage3DEPSG = epsgCode3D;
    }

    /**
     * Set default response EPSG code from settings
     * 
     * @param epsgCode
     *            EPSG code from settings
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_RESPONSE_EPSG)
    public void setDefaultResponseEpsg(final int epsgCode) throws ConfigurationException {
        Validation.greaterZero("Storage EPSG Code", epsgCode);
        defaultResponseEPSG = epsgCode;
    }

    /**
     * Set default response 3D EPSG code from settings
     * 
     * @param epsgCode3D
     *            3D EPSG code from settings
     * @throws ConfigurationException
     *             If an error occurs
     */
    @Setting(FeatureQuerySettingsProvider.DEFAULT_RESPONSE_3D_EPSG)
    public void setDefaultResponse3DEpsg(final int epsgCode3D) throws ConfigurationException {
        Validation.greaterZero("Storage 3D EPSG Code", epsgCode3D);
        defaultResponse3DEPSG = epsgCode3D;
    }

    protected boolean parseExtensionParameter(AbstractServiceRequest<?> request, String parameterValues,
            String parameterName) throws OwsExceptionReport {
        return false;
    }

    protected boolean parseDefaultParameter(AbstractServiceRequest<?> request, String parameterValues,
            String parameterName) throws OwsExceptionReport {
        // service (mandatory)
        if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.service.name())) {
            request.setService(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
            return true;
        }
        // version (mandatory)
        else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.version.name())) {
            request.setVersion(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
            return true;
        }
        // request (mandatory)
        else if (parameterName.equalsIgnoreCase(OWSConstants.RequestParams.request.name())) {
            KvpHelper.checkParameterSingleValue(parameterValues, parameterName);
            return true;
        }
        // language (optional)
        else if (parameterName.equalsIgnoreCase(OWSConstants.AdditionalRequestParams.language.name())) {
            request.addExtension(getLanguageExtension(KvpHelper.checkParameterSingleValue(parameterValues,
                    parameterName)));
            return true;
        }
        // CRS (optional)
        else if (parameterName.equalsIgnoreCase(OWSConstants.AdditionalRequestParams.crs.name())) {
            request.addExtension(getCrsExtension(KvpHelper.checkParameterSingleValue(parameterValues, parameterName)));
            return true;
        }

        else if (parameterName.equalsIgnoreCase(OWSConstants.AdditionalRequestParams.returnHumanReadableIdentifier
                .name())) {
            request.addExtension(getReturnHumanReadableIdentifierExtension(KvpHelper.checkParameterSingleValue(
                    parameterValues, parameterName)));
            return true;
        } else {
            return parseExtensionParameter(request, parameterValues, parameterName);
        }
    }

    /**
     * Check if service and version are contained in the request
     * 
     * @param request
     *            Parsed request
     * @param exceptions
     *            {@link CompositeOwsException} to add
     *            {@link MissingParameterValueException}s
     */
    protected void checkIfServiceVersionIsMissing(AbstractServiceRequest<?> request, CompositeOwsException exceptions) {
        if (!request.isSetService()) {
            exceptions.add(new MissingServiceParameterException());
        }

        if (!request.isSetVersion()) {
            exceptions.add(new MissingVersionParameterException());
        }
    }

    protected SpatialFilter parseSpatialFilter(List<String> parameterValues, String parameterName)
            throws OwsExceptionReport {
        if (!parameterValues.isEmpty()) {
            if (!(parameterValues instanceof RandomAccess)) {
                parameterValues = new ArrayList<String>(parameterValues);
            }
            SpatialFilter spatialFilter = new SpatialFilter();

            boolean hasSrid = false;

            spatialFilter.setValueReference(parameterValues.get(0));

            int srid = getStorageEPSG();
            if (parameterValues.get(parameterValues.size() - 1).startsWith(getSrsNamePrefixSosV2())
                    || parameterValues.get(parameterValues.size() - 1).startsWith(getSrsNamePrefix())) {
                hasSrid = true;
                srid = SosHelper.parseSrsName(parameterValues.get(parameterValues.size() - 1));
            }

            List<String> coordinates;
            if (hasSrid) {
                coordinates = parameterValues.subList(1, parameterValues.size() - 1);
            } else {
                coordinates = parameterValues.subList(1, parameterValues.size());
            }

            if (coordinates.size() != VALID_COORDINATE_SIZE) {
                throw new InvalidParameterValueException().at(parameterName).withMessage(
                        "The parameter value is not valid!");
            }
            String lowerCorner =
                    String.format(Locale.US, "%s %s", new BigDecimal(coordinates.get(Constants.INT_0)).toString(),
                            new BigDecimal(coordinates.get(Constants.INT_1)).toString());
            String upperCorner =
                    String.format(Locale.US, "%s %s", new BigDecimal(coordinates.get(Constants.INT_2)).toString(),
                            new BigDecimal(coordinates.get(Constants.INT_3)).toString());
            spatialFilter.setGeometry(JTSHelper.createGeometryFromWKT(
                    JTSHelper.createWKTPolygonFromEnvelope(lowerCorner, upperCorner), srid));
            spatialFilter.setOperator(SpatialOperator.BBOX);
            return spatialFilter;
        }
        return null;
    }

    /**
     * @param parameterValue
     * @param parameterName
     * @return SOS time object
     * @throws OwsExceptionReport
     * @throws DateTimeParseException
     */
    protected Time parseValidTime(String parameterValue, String parameterName) throws OwsExceptionReport,
            DateTimeParseException {
        return parseTime(parameterValue, parameterName);
    }

    /**
     * @param parameterValue
     * @return SOS time object
     * @throws CodedException
     */
    protected Time parseTime(String parameterValue, String parameterName) throws CodedException {
        String[] times = parameterValue.split("/");
        if (times.length == 1) {
            TimeInstant ti = new TimeInstant();
            if (SosIndeterminateTime.contains(times[0])) {
                ti.setSosIndeterminateTime(SosIndeterminateTime.getEnumForString(times[0]));
            } else {
                DateTime instant = DateTimeHelper.parseIsoString2DateTime(times[0]);
                ti.setValue(instant);
                ti.setRequestedTimeLength(DateTimeHelper.getTimeLengthBeforeTimeZone(times[0]));
            }
            return ti;
        } else if (times.length == 2) {
            DateTime start = DateTimeHelper.parseIsoString2DateTime(times[0]);
            // check if end time is a full ISO 8106 string
            int timeLength = DateTimeHelper.getTimeLengthBeforeTimeZone(times[1]);
            DateTime origEnd = DateTimeHelper.parseIsoString2DateTime(times[1]);
            DateTime end = DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition(origEnd, timeLength);
            TimePeriod timePeriod = new TimePeriod(start, end);
            return timePeriod;
        } else {
            throw new InvalidParameterValueException().at(parameterName).withMessage(
                    "The parameter value is not valid!");
        }
    }

    protected List<TemporalFilter> parseTemporalFilter(List<String> parameterValues, String parameterName)
            throws OwsExceptionReport, DateTimeParseException {
        List<TemporalFilter> filterList = new ArrayList<TemporalFilter>(1);
        // order: valueReference, time
        if (parameterValues.size() == 2) {
            filterList.add(createTemporalFilterFromValue(parameterValues.get(1), parameterValues.get(0)));
        } 
        // order: valueReference, temporal operator, time
        else if (parameterValues.size() == 3) {
            filterList.add(createTemporalFilterFromValue(parameterValues.get(2), parameterValues.get(1), parameterValues.get(0)));
        } else {
            throw new InvalidParameterValueException().withMessage("The parameter value is not valid!");
        }
        
        return filterList;
    }

    protected Map<String, String> parseNamespaces(String parameterValues) {
        List<String> array =
                Arrays.asList(parameterValues.replaceAll("\\),", "").replaceAll("\\)", "").split("xmlns\\("));
        Map<String, String> namespaces = new HashMap<String, String>(array.size());
        for (String string : array) {
            if (string != null && !string.isEmpty()) {
                String[] s = string.split(",");
                namespaces.put(s[0], s[1]);
            }
        }
        return namespaces;
    }

    private TemporalFilter createTemporalFilterFromValue(String value, String valueReference)
            throws OwsExceptionReport, DateTimeParseException {
        String[] times = value.split("/");
        if (times.length == 1) {
            return createTemporalFilterFromValue(value, TimeOperator.TM_Equals.name(), valueReference);
        } else if (times.length == 2) {
            return createTemporalFilterFromValue(value, TimeOperator.TM_During.name(), valueReference);
        } else {
            throw new InvalidParameterValueException().withMessage("The paramter value '%s' is invalid!", value);
        }
    }
    
    private TemporalFilter createTemporalFilterFromValue(String value, String operator, String valueReference)
            throws OwsExceptionReport, DateTimeParseException {
        TemporalFilter temporalFilter = new TemporalFilter();
        temporalFilter.setValueReference(valueReference);
        temporalFilter.setOperator(getTimeOperator(operator));
        String[] times = value.split("/");
        if (times.length == 1 && !temporalFilter.getOperator().equals(TimeOperator.TM_During)) {
            TimeInstant ti = new TimeInstant();
            if (SosIndeterminateTime.contains(times[0])) {
                ti.setSosIndeterminateTime(SosIndeterminateTime.getEnumForString(times[0]));
            } else {
                DateTime instant = DateTimeHelper.parseIsoString2DateTime(times[0]);
                ti.setValue(instant);
                ti.setRequestedTimeLength(DateTimeHelper.getTimeLengthBeforeTimeZone(times[0]));
            }
            temporalFilter.setTime(ti);
        } else if (times.length == 2 & temporalFilter.getOperator().equals(TimeOperator.TM_During)) {
            DateTime start = DateTimeHelper.parseIsoString2DateTime(times[0]);
            // check if end time is a full ISO 8106 string
            int timeLength = DateTimeHelper.getTimeLengthBeforeTimeZone(times[1]);
            DateTime origEnd = DateTimeHelper.parseIsoString2DateTime(times[1]);
            DateTime end = DateTimeHelper.setDateTime2EndOfMostPreciseUnit4RequestedEndPosition(origEnd, timeLength);
            TimePeriod tp = new TimePeriod();
            tp.setStart(start);
            tp.setEnd(end);
            temporalFilter.setTime(tp);
        } else {
            throw new InvalidParameterValueException().withMessage("The paramter value '%s' is invalid!", value);
        }
        return temporalFilter;
    }

    private TimeOperator getTimeOperator(String operator) {
        try {
            return TimeOperator.from(operator);
        } catch (IllegalArgumentException iae) {
            LOGGER.debug("Not a FES 1.0.0 temporal operator!", iae);
        }
        return TimeOperator.from(TimeOperator2.from(operator));
    }

    protected String getSrsNamePrefix() {
        return ServiceConfiguration.getInstance().getSrsNamePrefix();
    }

    protected String getSrsNamePrefixSosV2() {
        return ServiceConfiguration.getInstance().getSrsNamePrefixSosV2();
    }

    protected SwesExtension<SweText> getLanguageExtension(String language) {
        return getSweTextFor(OWSConstants.AdditionalRequestParams.language.name(), language);
    }

    protected SwesExtension<SweText> getCrsExtension(String crs) {
        return getSweTextFor(OWSConstants.AdditionalRequestParams.crs.name(), crs);
    }

    protected SwesExtension<SweBoolean> getReturnHumanReadableIdentifierExtension(String returnHumanReadableIdentifier) {
        SweBoolean bool =
                (SweBoolean) new SweBoolean().setValue(Boolean.parseBoolean(returnHumanReadableIdentifier))
                        .setIdentifier(OWSConstants.AdditionalRequestParams.returnHumanReadableIdentifier.name());
        return new SwesExtensionImpl<SweBoolean>().setValue(bool);
    }

    protected SwesExtension<SweText> getSweTextFor(String identifier, String value) {
        SweText text = (SweText) new SweText().setValue(value).setIdentifier(identifier);
        return new SwesExtensionImpl<SweText>().setValue(text);
    }

}
