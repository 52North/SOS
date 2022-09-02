/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.aquarius.ds;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.geotools.util.Range;
import org.joda.time.DateTime;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.sos.proxy.harvest.AbstractProxyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ExtendedAttributeFilter;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeListServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.GradeMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.LocationDescriptionListServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.ParameterMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.QualifierMetadata;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataCorrectedServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataRawServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDataServiceResponse;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescription;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesDescriptionServiceRequest;
import com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.TimeSeriesUniqueIdListServiceRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.base.Strings;

import net.servicestack.client.IReturn;

@Configurable
public class AquariusHelper extends AbstractProxyHelper implements Constructable, AquariusTimeHelper {

    public static final String APPLY_ROUNDING = "proxy.aquarius.applyRounding";

    public static final String RETURN_FULL_COVERAGE = "proxy.aquarius.returnFullCoverage";

    public static final String INCLUDE_GAP_MARKERS = "proxy.aquarius.includeGapMarkers";

    public static final String DATA_TYPE = "proxy.aquarius.dataType";

    public static final String CREATE_TEMPORAL = "proxy.aquarius.temporal.create";

    public static final String PUBLISHED = "proxy.aquarius.published";

    private static final Logger LOGGER = LoggerFactory.getLogger(AquariusHelper.class);

    private static final String EXTENDED_ATTRIBUTE_TIMESERIES_KEY = "proxy.aquarius.extendenattribute.timeseries.key";

    private static final String EXTENDED_ATTRIBUTE_TIMESERIES_VALUE =
            "proxy.aquarius.extendenattribute.timeseries.value";

    private static final String EXTENDED_ATTRIBUTE_LOCATION = "proxy.aquarius.extendenattribute.location";

    private static final String EXTENDED_ATTRIBUTE_LOCATION_KEY = "proxy.aquarius.extendenattribute.location.key";

    private static final String EXTENDED_ATTRIBUTE_LOCATION_VALUE = "proxy.aquarius.extendenattribute.location.value";

    private static final String BELOW_IDENTIFIER = "proxy.aquarius.detectionlimit.below";

    private static final String ABOVE_IDENTIFIER = "proxy.aquarius.detectionlimit.above";

    private static final String ADDITIONAL_QUALIFIERS = "proxy.aquarius.qualifiers";

    private static final String GRADES_FROM_FILE = "proxy.aquarius.grades.file";

    private static final String CONFIGURED_LOCATIONS = "proxy.aquarius.location.configured";

    private static final String UPDATE_COUNTER = "proxy.aquarius.update.counter";

    private ObjectMapper om;

    private Map<String, ParameterMetadata> parameters = new HashMap<>();

    private Map<String, LocationDataServiceResponse> locations = new HashMap<>();

    private Map<String, TimeSeriesDescription> datasets = new HashMap<>();

    private boolean applyRounding = Boolean.TRUE.booleanValue();

    private boolean returnFullCoverage = Boolean.FALSE.booleanValue();

    private boolean includeGapMarkers = Boolean.TRUE.booleanValue();

    private DataType dataType = DataType.RAW;

    private Published published = Published.ALL;

    private String extendedAttributeTimeSeriesKey;

    private String extendedAttributeTimeSeriesValue;

    private boolean extendedAttributeLocationAsTimeSeries = Boolean.FALSE.booleanValue();

    private String extendedAttributeLocationKey;

    private String extendedAttributeLocationValue;

    private String belowQualifier;

    private String aboveQualifier;

    private Set<String> additionalQualifiers = new LinkedHashSet<>();

    private boolean createTemporal = Boolean.TRUE.booleanValue();

    private Map<String, QualifierMetadata> qualifiers = new LinkedHashMap<>();

    private Map<String, GradeMetadata> grades = new LinkedHashMap<>();

    private boolean useGradesFromFile = Boolean.FALSE.booleanValue();

    private Set<String> configurdLocations = new LinkedHashSet<>();

    private Integer updateCounter = 50;

    @Setting(APPLY_ROUNDING)
    public AquariusHelper setApplyRoundig(boolean applyRounding) {
        this.applyRounding = applyRounding;
        return this;
    }

    @Setting(RETURN_FULL_COVERAGE)
    public AquariusHelper setreturnFullCoverage(boolean returnFullCoverage) {
        this.returnFullCoverage = returnFullCoverage;
        return this;
    }

    @Setting(INCLUDE_GAP_MARKERS)
    public AquariusHelper setIncludeGapMarkers(boolean includeGapMarkers) {
        this.includeGapMarkers = includeGapMarkers;
        return this;
    }

    @Setting(EXTENDED_ATTRIBUTE_TIMESERIES_KEY)
    public AquariusHelper setextendedAttributeTimeseriesKey(String key) {
        this.extendedAttributeTimeSeriesKey = key;
        return this;
    }

    @Setting(EXTENDED_ATTRIBUTE_TIMESERIES_VALUE)
    public AquariusHelper setExtendedAttributeTimeseriesValue(String value) {
        this.extendedAttributeTimeSeriesValue = value;
        return this;
    }

    @Setting(EXTENDED_ATTRIBUTE_LOCATION)
    public AquariusHelper setExtendedAttributeLocationAsTimeSeries(boolean extendedAttributeLocationAsTimeSeries) {
        this.extendedAttributeLocationAsTimeSeries = extendedAttributeLocationAsTimeSeries;
        return this;
    }

    @Setting(EXTENDED_ATTRIBUTE_LOCATION_KEY)
    public AquariusHelper setextendedAttributeLocationKey(String key) {
        this.extendedAttributeLocationKey = key;
        return this;
    }

    @Setting(EXTENDED_ATTRIBUTE_LOCATION_VALUE)
    public AquariusHelper setExtendedAttributeLocationValue(String value) {
        this.extendedAttributeLocationValue = value;
        return this;
    }

    @Setting(BELOW_IDENTIFIER)
    public AquariusHelper setBelowQualifierIdentifier(String belowQualifier) {
        this.belowQualifier = belowQualifier;
        return this;
    }

    @Setting(ABOVE_IDENTIFIER)
    public AquariusHelper setAboveQualifierIdentifier(String aboveQualifier) {
        this.aboveQualifier = aboveQualifier;
        return this;
    }

    @Setting(ADDITIONAL_QUALIFIERS)
    public AquariusHelper setAdditionalQualifiers(String additionalQualifiers) {
        this.additionalQualifiers.clear();
        if (!Strings.isNullOrEmpty(additionalQualifiers)) {
            this.additionalQualifiers
                    .addAll(Stream.of(additionalQualifiers.trim().split(",")).collect(Collectors.toSet()));
        }
        return this;
    }

    @Setting(GRADES_FROM_FILE)
    public AquariusHelper setUseGradesFromFile(boolean useGradesFromFile) {
        this.useGradesFromFile = useGradesFromFile;
        return this;
    }

    @Setting(CONFIGURED_LOCATIONS)
    public AquariusHelper setConfiguredLocations(String locations) {
        this.configurdLocations.clear();
        if (!Strings.isNullOrEmpty(locations)) {
            this.configurdLocations.addAll(Arrays.asList(locations.split(",")));
        }
        return this;
    }

    @Setting(UPDATE_COUNTER)
    public AquariusHelper setUpdateCount(Integer updateCounter) {
        this.updateCounter = updateCounter;
        return this;
    }

    public void init() {
        try {
            this.om = JsonMapper.builder().findAndAddModules().build();
            URL resource = AquariusHelper.class.getResource("/aquarius/grades.json");
            if (resource != null) {
                File file = FileUtils.toFile(resource);
                if (file != null) {
                    GradeListServiceResponse parsedGrades = om.readValue(file, GradeListServiceResponse.class);
                    if (parsedGrades != null) {
                        setGrades(parsedGrades.getGrades());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while loading grades from file on startup!", e);
        }
    }

    public String getBelowQualifier() {
        return belowQualifier;
    }

    public boolean isSetBelowQualifier() {
        return !Strings.isNullOrEmpty(getBelowQualifier());
    }

    public String getAboveQualifier() {
        return aboveQualifier;
    }

    public boolean isSetAboveQualifier() {
        return !Strings.isNullOrEmpty(getAboveQualifier());
    }

    public Set<String> getAdditionalQualifiers() {
        return Collections.unmodifiableSet(additionalQualifiers);
    }

    public boolean isSetAdditionalQualifiers() {
        return this.additionalQualifiers != null && !this.additionalQualifiers.isEmpty();
    }

    public boolean isSetApplyRounding() {
        return applyRounding;
    }

    public boolean isSetUseGradesFromFile() {
        return useGradesFromFile;
    }

    public Set<String> getConfiguredLocations() {
        return Collections.unmodifiableSet(configurdLocations);
    }

    public boolean hasConfiguredLocations() {
        return !getConfiguredLocations().isEmpty();
    }

    public Integer getUpdateCount() {
        return updateCounter;
    }

    private boolean isSetExtendedAttributeTimeSeriesKey() {
        return extendedAttributeTimeSeriesKey != null && !extendedAttributeTimeSeriesKey.isEmpty();
    }

    private boolean isSetExtendedAttributeTimeSeriesValue() {
        return extendedAttributeTimeSeriesValue != null && !extendedAttributeTimeSeriesValue.isEmpty();
    }

    private boolean isExtendendAttributeTimeSeries() {
        return isSetExtendedAttributeTimeSeriesKey() && isSetExtendedAttributeTimeSeriesValue();
    }

    private boolean isSetExtendedAttributeLocationKey() {
        return extendedAttributeLocationKey != null && !extendedAttributeLocationKey.isEmpty();
    }

    private boolean isSetExtendedAttributeLocationValue() {
        return extendedAttributeLocationValue != null && !extendedAttributeLocationValue.isEmpty();
    }

    private boolean isExtendendAttributeLocation() {
        return isSetExtendedAttributeLocationKey() && isSetExtendedAttributeLocationValue();
    }

    private boolean isExtendendAttributeLocationAsTimeseries() {
        return extendedAttributeLocationAsTimeSeries;
    }

    // @Setting(CREATE_TEMPORAL)
    public AquariusHelper setCreateTemporal(boolean createTemporal) {
        this.createTemporal = createTemporal;
        return this;
    }

    public boolean isCreateTemporal() {
        return createTemporal;
    }

    @Setting(DATA_TYPE)
    public AquariusHelper setDataType(String dataType) {
        if (dataType != null && !this.dataType.equals(DataType.valueOf(dataType))) {
            this.dataType = DataType.valueOf(dataType);
        }
        return this;
    }

    public DataType getDataType() {
        return dataType;
    }

    public ParameterMetadata getParameter(String parameterId) {
        return parameters.get(parameterId);
    }

    @Setting(PUBLISHED)
    public AquariusHelper setPublished(String published) {
        if (published != null && !this.published.equals(Published.valueOf(published))) {
            this.published = Published.valueOf(published);
        }
        return this;
    }

    public Published getPublished() {
        return published;
    }

    public AquariusHelper addParameter(ParameterMetadata parameter) {
        if (parameter != null) {
            parameters.put(parameter.getIdentifier(), parameter);
        }
        return this;
    }

    public AquariusHelper addParameters(Collection<ParameterMetadata> parameters) {
        if (parameters != null) {
            parameters.stream().forEach(m -> addParameter(m));
        }
        return this;
    }

    public boolean hasLocation(String locationId) {
        return locations.containsKey(locationId);
    }

    public LocationDataServiceResponse getLocation(String locationId) {
        return locations.get(locationId);
    }

    public AquariusHelper addLocation(LocationDataServiceResponse location) {
        if (location != null) {
            locations.put(location.getIdentifier(), location);
        }
        return this;
    }

    public AquariusHelper addLocations(Collection<LocationDataServiceResponse> locations) {
        if (locations != null) {
            locations.stream().forEach(m -> addLocation(m));
        }
        return this;
    }

    public TimeSeriesDescription getDataset(String timeSeriesIdentifier) {
        return datasets.get(timeSeriesIdentifier);
    }

    public AquariusHelper addDataset(TimeSeriesDescription timeSeries) {
        if (timeSeries != null) {
            datasets.put(timeSeries.getUniqueId(), timeSeries);
        }
        return this;
    }

    public boolean hasDataset(String dataSetId) {
        return datasets.containsKey(dataSetId);
    }

    public LocationDescriptionListServiceRequest getLocationDescriptionListRequest(String locationIdentifier) {
        LocationDescriptionListServiceRequest request = getLocationDescriptionListRequest();
        request.setLocationIdentifier(locationIdentifier);
        return request;
    }

    public LocationDescriptionListServiceRequest getLocationDescriptionListRequest() {
        switch (getPublished()) {
            case FALSE:
                return (LocationDescriptionListServiceRequest) getBaseLocationDescriptionListRequest()
                        .setPublish(Boolean.FALSE);
            case TRUE:
                return (LocationDescriptionListServiceRequest) getBaseLocationDescriptionListRequest()
                        .setPublish(Boolean.TRUE);
            case ALL:
            default:
                return getBaseLocationDescriptionListRequest();
        }
    }

    public LocationDataServiceRequest getLocationData(String locationIdentifier) {
        return new LocationDataServiceRequest().setLocationIdentifier(locationIdentifier);
    }

    private LocationDescriptionListServiceRequest getBaseLocationDescriptionListRequest() {
        LocationDescriptionListServiceRequest request = new LocationDescriptionListServiceRequest();
        if (isExtendendAttributeLocation()
                || isExtendendAttributeLocationAsTimeseries() && isExtendendAttributeTimeSeries()) {
            Map<String, String> filters = getExtendedFilterMapLocation();
            request.setExtendedFilters(createExtendedFilters(filters));
        }
        return request;
    }

    private ArrayList<ExtendedAttributeFilter> createExtendedFilters(Map<String, String> filters) {
        if (filters != null && !filters.isEmpty()) {
            ArrayList<ExtendedAttributeFilter> list = new ArrayList<>();
            for (Entry<String, String> filter : filters.entrySet()) {
                list.add(new ExtendedAttributeFilter().setFilterName(filter.getKey())
                        .setFilterValue(filter.getValue()));
            }
            return list;
        }
        return null;
    }

    public TimeSeriesDescriptionServiceRequest getGetTimeSeriesDescriptionListRequest() {
        switch (getPublished()) {
            case FALSE:
                return getBaseTimeSeriesDescriptionListRequest().setPublish(Boolean.FALSE);
            case TRUE:
                return getBaseTimeSeriesDescriptionListRequest().setPublish(Boolean.TRUE);
            case ALL:
            default:
                return getBaseTimeSeriesDescriptionListRequest();
        }
    }

    private TimeSeriesDescriptionServiceRequest getBaseTimeSeriesDescriptionListRequest() {
        TimeSeriesDescriptionServiceRequest request = new TimeSeriesDescriptionServiceRequest();
        if (isExtendendAttributeTimeSeries()) {
            Map<String, String> filters = getExtendedFilterMapTimeSeries();
            request.setExtendedFilters(createExtendedFilters(filters));
        }
        return request;
    }

    public TimeSeriesUniqueIdListServiceRequest getTimeSeriesUniqueIdsRequest() {
        switch (getPublished()) {
            case FALSE:
                return getBaseTimeSeriesUnitqueIdsListRequest().setPublish(Boolean.FALSE);
            case TRUE:
                return getBaseTimeSeriesUnitqueIdsListRequest().setPublish(Boolean.TRUE);
            case ALL:
            default:
                return getBaseTimeSeriesUnitqueIdsListRequest();
        }
    }

    private TimeSeriesUniqueIdListServiceRequest getBaseTimeSeriesUnitqueIdsListRequest() {
        TimeSeriesUniqueIdListServiceRequest request = new TimeSeriesUniqueIdListServiceRequest();
        if (isExtendendAttributeTimeSeries()) {
            Map<String, String> filters = getExtendedFilterMapLocation();
            request.setExtendedFilters(createExtendedFilters(filters));
        }
        return request;
    }

    private Map<String, String> getExtendedFilterMapTimeSeries() {
        Map<String, String> map = new LinkedHashMap<>();
        if (isExtendendAttributeTimeSeries()) {
            map.put(extendedAttributeTimeSeriesKey, extendedAttributeTimeSeriesValue);
        }
        return map;
    }

    private Map<String, String> getExtendedFilterMapLocation() {
        if (isExtendendAttributeLocationAsTimeseries() && isExtendendAttributeTimeSeries()) {
            return getExtendedFilterMapTimeSeries();
        }
        Map<String, String> map = new LinkedHashMap<>();
        if (isExtendendAttributeLocation()) {
            map.put(extendedAttributeLocationKey, extendedAttributeLocationValue);
        }
        return map;
    }

    public IReturn<TimeSeriesDataServiceResponse> getTimeSeriesDataRequest(String timeSeriesUniqueId,
            DateTime queryFrom, DateTime queryTo) {
        switch (getDataType()) {
            case CORRECTED:
                TimeSeriesDataCorrectedServiceRequest request = new TimeSeriesDataCorrectedServiceRequest()
                        .setTimeSeriesUniqueId(timeSeriesUniqueId).setReturnFullCoverage(returnFullCoverage)
                        .setIncludeGapMarkers(includeGapMarkers).setApplyRounding(applyRounding);
                if (queryFrom != null) {
                    request.setQueryFrom(queryFrom.toDate().toInstant());
                }
                if (queryTo != null) {
                    request.setQueryTo(queryTo.toDate().toInstant());
                }
                return request;
            case RAW:
            default:
                TimeSeriesDataRawServiceRequest rawRequest = new TimeSeriesDataRawServiceRequest()
                        .setTimeSeriesUniqueId(timeSeriesUniqueId).setApplyRounding(applyRounding);
                if (queryFrom != null) {
                    rawRequest.setQueryFrom(queryFrom.toDate().toInstant());
                }
                if (queryTo != null) {
                    rawRequest.setQueryTo(queryTo.toDate().toInstant());
                }
                return rawRequest;
        }
    }

    public Range<DateTime> getTimeRange(TimeSeriesDescription timeSeries) {
        switch (getDataType()) {
            case CORRECTED:
                return (timeSeries.getCorrectedStartTime() != null && timeSeries.getCorrectedEndTime() != null)
                        ? new Range<DateTime>(DateTime.class, toDateTime(timeSeries.getCorrectedStartTime()),
                                toDateTime(timeSeries.getCorrectedEndTime()))
                        : null;
            default:
                return (timeSeries.getRawStartTime() != null && timeSeries.getRawEndTime() != null)
                        ? new Range<DateTime>(DateTime.class, toDateTime(timeSeries.getRawStartTime()),
                                toDateTime(timeSeries.getRawEndTime()))
                        : null;
        }
    }

    public boolean checkForData(TimeSeriesDescription timeSeries) {
        switch (getDataType()) {
            case CORRECTED:
                return timeSeries.getCorrectedStartTime() != null && timeSeries.getCorrectedEndTime() != null;
            default:
                return timeSeries.getRawStartTime() != null && timeSeries.getRawEndTime() != null;
        }
    }

    public TimeSeriesData applyChecker(TimeSeriesDataServiceResponse timeSeriesData) {
        return applyGradeChecker(applyQualifierChecker(new TimeSeriesData(timeSeriesData)));
    }

    public TimeSeriesData applyChecker(TimeSeriesData timeSeriesData) {
        return applyGradeChecker(applyQualifierChecker(timeSeriesData));
    }

    private TimeSeriesData applyQualifierChecker(TimeSeriesData timeSeriesData) {
        if (isSetAboveQualifier() || isSetBelowQualifier()
                || isSetAdditionalQualifiers() && timeSeriesData.hasQualifiers()) {
            for (com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier q : timeSeriesData
                    .getQualifiers()) {
                Qualifier qualifier = createQualifier(q);
                if (isSetAboveQualifier() && qualifier.getIdentifier().equalsIgnoreCase(getAboveQualifier())) {
                    timeSeriesData.addChecker(qualifier.setKey(QualifierKey.of(QualifierKey.ABOVE)));
                } else if (isSetBelowQualifier() && qualifier.getIdentifier().equalsIgnoreCase(getBelowQualifier())) {
                    timeSeriesData.addChecker(qualifier.setKey(QualifierKey.of(QualifierKey.BELOW)));
                } else if (isSetAdditionalQualifiers()
                        && getAdditionalQualifiers().contains(qualifier.getIdentifier())) {
                    timeSeriesData.addChecker(enhanceQualifier(qualifier, q));
                }
            }
        }
        return timeSeriesData;
    }

    private TimeSeriesData applyGradeChecker(TimeSeriesData timeSeriesData) {
        for (com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Grade grade : timeSeriesData
                .getGrades()) {
            timeSeriesData.addChecker(enhanceGrade(grade));
        }
        return timeSeriesData;
    }

    private Qualifier createQualifier(
            com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier original) {
        Qualifier sosQualifier = new Qualifier();
        sosQualifier.setIdentifier(original.getIdentifier());
        sosQualifier.setStartTime(original.getStartTime());
        sosQualifier.setEndTime(original.getEndTime());
        return sosQualifier;
    }

    private Qualifier enhanceQualifier(Qualifier qualifier,
            com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Qualifier original) {
        qualifier.setKey(QualifierKey.of(qualifier.getIdentifier()));
        if (getQualifiers().containsKey(original.getIdentifier())) {
            QualifierMetadata quali = getQualifier(original.getIdentifier());
            qualifier.setCode(quali.getCode());
            qualifier.setDisplayName(quali.getDisplayName());
        }
        return qualifier;
    }

    private Grade enhanceGrade(com.aquaticinformatics.aquarius.sdk.timeseries.servicemodels.Publish.Grade original) {
        Grade sosGrade = new Grade();
        sosGrade.setStartTime(original.getStartTime());
        sosGrade.setEndTime(original.getEndTime());
        if (getGrades().containsKey(original.getGradeCode())) {
            GradeMetadata g = getGrade(original.getGradeCode());
            sosGrade.setDescription(g.getDescription());
            sosGrade.setDisplayName(g.getDisplayName());
        }
        return sosGrade;
    }

    public Map<String, GradeMetadata> getGrades() {
        return Collections.unmodifiableMap(grades);
    }

    public GradeMetadata getGrade(String code) {
        return getGrades().get(code);
    }

    public Map<String, QualifierMetadata> getQualifiers() {
        return Collections.unmodifiableMap(qualifiers);
    }

    public QualifierMetadata getQualifier(String identifier) {
        return getQualifiers().get(identifier);
    }

    public void setGrades(List<GradeMetadata> arrayList) {
        Map<String, GradeMetadata> map = new LinkedHashMap<>();
        for (GradeMetadata grade : arrayList) {
            map.put(grade.getIdentifier(), grade);
        }
        this.grades.putAll(map);
    }

    public void setQualifiers(List<QualifierMetadata> arrayList) {
        Map<String, QualifierMetadata> map = new LinkedHashMap<>();
        if (isSetAdditionalQualifiers()) {
            for (QualifierMetadata qualifier : arrayList) {
                if (getAdditionalQualifiers().contains(qualifier.getIdentifier())) {
                    map.put(qualifier.getIdentifier(), qualifier);
                }
            }
        }
        this.qualifiers.putAll(map);
    }

    public enum DataType {
        RAW,
        CORRECTED;
    }

    public enum Published {
        TRUE,
        FALSE,
        ALL;
    }
}
