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
package org.n52.sos.ds.observation;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.binding.BindingRepository;
import org.n52.janmayen.http.MediaTypes;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DetectionLimitEntity;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.VerticalMetadataEntity;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.ogc.OGCConstants;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.CodeType;
import org.n52.shetland.ogc.gml.CodeWithAuthority;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.SingleObservationValue;
import org.n52.shetland.ogc.om.TimeValuePair;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweDataArray;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.shetland.util.OMHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.SosHelper;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.util.GmlHelper;
import org.n52.svalbard.util.SweHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Configurable
@SuppressFBWarnings({ "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ObservationHelper implements Constructable {

    private DecoderRepository decoderRepository;
    private BindingRepository bindingRepository;
    private SweHelper sweHelper;
    private GeometryHandler geometryHandler;
    private SosHelper sosHelper;
    private EReportingHelper eReportingHelper;
    private SpatialFilteringProfileCreator spatialFilteringProfileCreator;

    private String qualifierDefinitionBelow = "http://www.example.com/sensors/lower_threshold";
    private String qualifierDefinitionAbove = "http://www.example.com/sensors/upper_threshold";
    private String qualifierDescriptionBelow = "Lower limit for sensor";
    private String qualifierDescriptionAbove = "Upper limit for sensor";
    private String censoredReasonHrefBelow = OGCConstants.BELOW_DETECTION_RANGE;
    private String censoredReasonHrefAbove = OGCConstants.ABOVE_DETECTION_RANGE;
    private String censoredReasonTitleBelow = "Below threshold of sensor";
    private String censoredReasonTitleAbove = "Above threshold of sensor";

    @Inject
    public void setDecoderRepository(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    @Inject
    public void setBindingRepository(BindingRepository bindingRepository) {
        this.bindingRepository = bindingRepository;
    }

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    @Inject
    public void setSweHelper(SweHelper sweHelper) {
        this.sweHelper = sweHelper;
    }

    @Inject
    public void setSosHelper(SosHelper sosHelper) {
        this.sosHelper = sosHelper;
    }

    @Override
    public void init() {
        this.eReportingHelper = new EReportingHelper(sweHelper);
        this.spatialFilteringProfileCreator = new SpatialFilteringProfileCreator(geometryHandler);
    }

    public DecoderRepository getDecoderRepository() {
        return decoderRepository;
    }

    public GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    public SweHelper getSweHelper() {
        return sweHelper;
    }

    /**
     * Create a {@link TimeValuePair} from {@link DataEntity}
     *
     * @param abstractValue
     *            {@link DataEntity} to create
     *            {@link TimeValuePair} from
     * @return resulting {@link TimeValuePair}
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    public TimeValuePair createTimeValuePairFrom(DataEntity<?> abstractValue) throws OwsExceptionReport {
        return new TimeValuePair(createPhenomenonTime(abstractValue),
                new ObservationValueCreator(this).visit(abstractValue));
    }

    /**
     * Add {@code DataEntity} data to {@code OmObservation}
     *
     * @param observation
     *            {@link OmObservation} to add data
     * @param responseFormat
     *
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    public OmObservation addValuesToObservation(DataEntity<?> o, OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        observation.setObservationID(Long.toString(o.getId()));
        if (!observation.isSetIdentifier() && o.isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(o.getIdentifier());
            if (o.isSetIdentifierCodespace()) {
                identifier.setCodeSpace(o.getIdentifierCodespace().getName());
            }
            observation.setIdentifier(identifier);
        }
        if (!observation.isSetName() && o.isSetName()) {
            CodeType name = new CodeType(o.getName());
            if (o.isSetNameCodespace()) {
                try {
                    name.setCodeSpace(new URI(o.getNameCodespace().getName()));
                } catch (URISyntaxException e) {
                    throw new NoApplicableCodeException().causedBy(e).withMessage("Invalid codespace value: {}",
                            o.getNameCodespace().getName());
                }
            }
            observation.setName(name);
        }
        if (!observation.isSetDescription() && o.isSetDescription()) {
            observation.setDescription(o.getDescription());
        }
        Value<?> value = new ObservationValueCreator(this).visit(o);
        if (!value.isSetUnit()
                && observation.getObservationConstellation().getObservableProperty() instanceof OmObservableProperty
                && ((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                        .isSetUnit()) {
            value.setUnit(((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                    .getUnit());
        }
        if (!value.isSetValue() && o.hasDetectionLimit()) {
            observation.addParameter(createDetectionLimit(o.getDetectionLimit(), value.getUnitObject()));
        }
        if (!observation.getObservationConstellation().isSetObservationType()) {
            observation.getObservationConstellation().setObservationType(OMHelper.getObservationTypeFor(value));
        }
        observation.setResultTime(createResutlTime(o.getResultTime()));
        observation.setValidTime(createValidTime(o.getValidTimeStart(), o.getValidTimeEnd()));
        if (o.isSetGeometryEntity()) {
            observation.addParameter(spatialFilteringProfileCreator.create(o.getGeometryEntity()
                    .getGeometry()));
        }
        if (o.getDataset().hasVerticalMetadata()) {
            VerticalMetadataEntity verticalMetadata = o.getDataset().getVerticalMetadata();
            if (o.hasVerticalInterval()) {
                observation.addParameter(createParameter(getVerticalFromName(verticalMetadata), o.getVerticalFrom(),
                        verticalMetadata.getVerticalUnit()));
                observation.addParameter(createParameter(getVerticalToName(verticalMetadata), o.getVerticalTo(),
                        verticalMetadata.getVerticalUnit()));
            } else {
                observation.addParameter(createParameter(getVerticalFromName(verticalMetadata), o.getVerticalFrom(),
                        verticalMetadata.getVerticalUnit()));
            }
        }
        addRelatedObservation(o, observation);
        addParameter(o, observation);
        addValueSpecificDataToObservation(o, observation, responseFormat);
        addObservationValueToObservation(o, observation, value, responseFormat);
        return observation;
    }

    /**
     * Get the observation ids from {@link DataEntity}s
     *
     * @param abstractValuesResult
     *            {@link DataEntity}s to get ids from
     * @return Set with ids
     */
    protected Set<Long> getObservationIds(Collection<? extends DataEntity> abstractValuesResult) {
        Set<Long> ids = new HashSet<>(abstractValuesResult.size());
        for (DataEntity<?> abstractValue : abstractValuesResult) {
            ids.add(abstractValue.getId());
        }
        return ids;
    }

    /**
     * Create the phenomenon time from {@link DataEntity}
     *
     * @param abstractValue
     *            {@link DataEntity} for get time from
     * @return phenomenon time
     */
    protected Time createPhenomenonTime(DataEntity<?> abstractValue) {
        // create time element
        final DateTime phenStartTime = new DateTime(abstractValue.getSamplingTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (abstractValue.getSamplingTimeEnd() != null) {
            phenEndTime = new DateTime(abstractValue.getSamplingTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        return createTime(phenStartTime, phenEndTime);
    }

    /**
     * Create phenomenon time from min and max
     * {@link DataEntity}s
     *
     * @param minTime
     *            minimum {@link DataEntity}
     * @param maxTime
     *            maximum {@link DataEntity}
     * @return phenomenon time
     */
    protected Time createPhenomenonTime(DataEntity<?> minTime, DataEntity<?> maxTime) {
        // create time element

        final DateTime phenStartTime = DateTimeHelper.makeDateTime(minTime.getSamplingTimeStart());
        DateTime phenEndTime;
        if (maxTime.getSamplingTimeEnd() != null) {
            phenEndTime = DateTimeHelper.makeDateTime(minTime.getSamplingTimeEnd());
        } else {
            phenEndTime = phenStartTime;
        }
        return GmlHelper.createTime(phenStartTime, phenEndTime);
    }

    /**
     * Create result time from {@link DataEntity}
     *
     * @param maxTime
     *            {@link DataEntity} to create result
     *            time from
     * @return result time
     */
    protected TimeInstant createResutlTime(DataEntity<?> maxTime) {
        DateTime dateTime = DateTimeHelper.makeDateTime(maxTime.getResultTime());
        return new TimeInstant(dateTime);
    }

    /**
     * Create result time from {@link Date}
     *
     * @param date
     *            {@link Date} to create result time from
     * @return result time
     */
    protected TimeInstant createResutlTime(Date date) {
        DateTime dateTime = new DateTime(date, DateTimeZone.UTC);
        return new TimeInstant(dateTime);
    }

    /**
     * Create valid time from min and max
     * {@link DataEntity}s
     *
     * @param minTime
     *            minimum {@link DataEntity}
     * @param maxTime
     *            maximum {@link DataEntity}
     * @return valid time or null if valid time is not set in datasource
     */
    protected Time createValidTime(DataEntity<?> minTime, DataEntity<?> maxTime) {
        // create time element
        if (minTime.getValidTimeStart() != null && maxTime.getValidTimeEnd() != null) {
            final DateTime startTime = DateTimeHelper.makeDateTime(minTime.getValidTimeStart());
            DateTime endTime = DateTimeHelper.makeDateTime(minTime.getValidTimeEnd());
            return GmlHelper.createTime(startTime, endTime);
        }
        return null;
    }

    /**
     * Create {@link TimePeriod} from {@link Date}s
     *
     * @param start
     *            Start {@link Date}
     * @param end
     *            End {@link Date}
     * @return {@link TimePeriod} or null if {@link Date}s are null
     */
    protected TimePeriod createValidTime(Date start, Date end) {
        // create time element
        if (start != null && end != null) {
            final DateTime startTime = new DateTime(start, DateTimeZone.UTC);
            DateTime endTime = new DateTime(end, DateTimeZone.UTC);
            return new TimePeriod(startTime, endTime);
        }
        return null;
    }

    /**
     * Create {@link Time} from {@link DateTime}s
     *
     * @param start
     *            Start {@link DateTime}
     * @param end
     *            End {@link DateTime}
     * @return Resulting {@link Time}
     */
    protected Time createTime(DateTime start, DateTime end) {
        if (start.equals(end)) {
            return new TimeInstant(start);
        } else {
            return new TimePeriod(start, end);
        }
    }

    protected NamedValue<?> createDetectionLimit(DetectionLimitEntity detectionLimit, UoM uoM) {
        final NamedValue<BigDecimal> namedValue = new NamedValue<>();
        final ReferenceType referenceType =
                new ReferenceType(detectionLimit.getFlag() > 0 ? "exceed limit" : "below limit");
        namedValue.setName(referenceType);
        namedValue.setValue(new QuantityValue(detectionLimit.getDetectionLimit(), uoM));
        return namedValue;
    }

    private String getVerticalFromName(VerticalMetadataEntity verticalMetadata) {
        return verticalMetadata.isSetVerticalFromName() ? verticalMetadata.getVerticalFromName()
                : getVerticalToName(verticalMetadata);
    }

    private String getVerticalToName(VerticalMetadataEntity verticalMetadata) {
        return verticalMetadata.isSetVerticalToName() ? verticalMetadata.getVerticalToName()
                : getNameFromOrientation(verticalMetadata);
    }

    private String getNameFromOrientation(VerticalMetadataEntity verticalMetadata) {
        return verticalMetadata.getOrientation() != null && verticalMetadata.getOrientation() > 0 ? "height" : "depth";
    }

    private NamedValue<BigDecimal> createParameter(String name, BigDecimal value, UnitEntity unit) {
        final NamedValue<BigDecimal> namedValue = new NamedValue<>();
        final ReferenceType referenceType = new ReferenceType(name);
        namedValue.setName(referenceType);
        namedValue.setValue(new QuantityValue(value, unit != null ? unit.getUnit() : "m"));
        return namedValue;
    }

    protected void addValueSpecificDataToObservation(DataEntity<?> o, OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        // nothing to do
    }

    protected void addRelatedObservation(DataEntity<?> o, OmObservation observation) throws OwsExceptionReport {
        new RelatedObservationAdder(observation, o, sosHelper.getServiceURL(),
                bindingRepository.isActive(MediaTypes.APPLICATION_KVP)).add();
    }

    protected void addParameter(DataEntity<?> o, OmObservation observation) throws OwsExceptionReport {
        new ParameterAdder(observation, o).add();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private SingleObservationValue getSingleObservationValue(DataEntity<?> o, Value<?> value)
            throws OwsExceptionReport {
        return new SingleObservationValue(createPhenomenonTime(o), value);
    }

    public OmObservation mergeValueToObservation(DataEntity<?> o, OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        if (checkResponseFormat(responseFormat) && o.hasEreportingProfile()) {
            if (!observation.isSetValue()) {
                addValuesToObservation(o, observation, responseFormat);
            } else {
                checkTime(o, observation);
                eReportingHelper.mergeValues((SweDataArray) observation.getValue().getValue().getValue(),
                        eReportingHelper.createSweDataArray(observation, o));
            }
            if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION
                    .equals(observation.getObservationConstellation().getObservationType())) {
                observation.getObservationConstellation()
                        .setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            }

        } else {
            if (!observation.isSetValue()) {
                addValuesToObservation(o, observation, responseFormat);
            } else {
                // TODO
                if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION
                        .equals(observation.getObservationConstellation().getObservationType())) {
                    observation.getObservationConstellation()
                            .setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
                }
                observation.mergeWithObservation(getSingleObservationValue(o,
                        new ObservationValueCreator(this).visit(o)));
            }
        }
        return observation;
    }

    public void addObservationValueToObservation(DataEntity<?> o, OmObservation observation, Value<?> value,
            String responseFormat) throws OwsExceptionReport {
        if (checkResponseFormat(responseFormat)) {
            if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION
                    .equals(observation.getObservationConstellation().getObservationType())) {
                observation.getObservationConstellation()
                        .setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            }
            observation.setValue(eReportingHelper.createSweDataArrayValue(observation, o));
        } else {
            observation.setValue(getSingleObservationValue(o, value));
        }
    }

    private boolean checkResponseFormat(String responseFormat) {
        return AqdConstants.NS_AQD.equals(responseFormat);
    }

    public String getDiscriminator(DataEntity<?> o) {
        if (o.hasEreportingProfile()) {
            return o.getEreportingProfile().getPrimaryObservation();
        }
        return null;
    }

    private void checkTime(DataEntity<?> o, OmObservation observation) {
        if (observation.isSetValue()) {
            Time obsPhenTime = observation.getValue().getPhenomenonTime();
            Time valuePhenTime = createPhenomenonTime(o);
            if (obsPhenTime != null) {
                TimePeriod timePeriod;
                if (obsPhenTime instanceof TimePeriod) {
                    timePeriod = (TimePeriod) obsPhenTime;
                } else {
                    timePeriod = new TimePeriod();
                    timePeriod.extendToContain(obsPhenTime);
                }
                timePeriod.extendToContain(valuePhenTime);
                observation.getValue().setPhenomenonTime(timePeriod);
            } else {
                observation.getValue().setPhenomenonTime(valuePhenTime);
            }
        }
        TimeInstant rt = createResutlTime(o.getResultTime());
        if (observation.getResultTime().getValue().isBefore(rt.getValue())) {
            observation.setResultTime(rt);
        }
        if (o.isSetValidTime()) {
            TimePeriod vt = createValidTime(o.getValidTimeStart(), o.getValidTimeEnd());
            if (observation.isSetValidTime()) {
                observation.getValidTime().extendToContain(vt);
            } else {
                observation.setValidTime(vt);
            }
        }
    }

    @Setting(value = DetectionLimitSettings.QUALIFIER_DEFINITION_BELOW_KEY, required = false)
    public void setQualifierDefinitionBelow(String qualifierDefinitionBelow) {
        this.qualifierDefinitionBelow = qualifierDefinitionBelow;
    }

    public String getQualifierDefinitionBelow() {
        return qualifierDefinitionBelow;
    }

    @Setting(value = DetectionLimitSettings.QUALIFIER_DEFINITION_ABOVE_KEY, required = false)
    public void setQualifierDefinitionAbove(String qualifierDefinitionAbove) {
        this.qualifierDefinitionAbove = qualifierDefinitionAbove;
    }

    public String getQualifierDefinitionAbove() {
        return qualifierDefinitionAbove;
    }

    @Setting(value = DetectionLimitSettings.QUALIFIER_DESCRIPTION_BELOW_KEY, required = false)
    public void setQualifierDescriptionBelow(String qualifierDescriptionBelow) {
        this.qualifierDescriptionBelow = qualifierDescriptionBelow;
    }

    public String getQualifierDescriptionBelow() {
        return qualifierDescriptionBelow;
    }

    @Setting(value = DetectionLimitSettings.QUALIFIER_DESCRIPTION_ABOVE_KEY, required = false)
    public void setQualifierDescriptionAbove(String qualifierDescriptionAbove) {
        this.qualifierDescriptionAbove = qualifierDescriptionAbove;
    }

    public String getQualifierDescriptionAbove() {
        return qualifierDescriptionAbove;
    }

    @Setting(value = DetectionLimitSettings.CENSORED_REASONS_HREF_BELOW_KEY, required = false)
    public void setCensoredReasonHrefBelow(String censoredReasonHrefBelow) {
        this.censoredReasonHrefBelow = censoredReasonHrefBelow;
    }

    public String getCensoredReasonHrefBelow() {
        return censoredReasonHrefBelow;
    }

    @Setting(value = DetectionLimitSettings.CENSORED_REASONS_HREF_ABOVE_KEY, required = false)
    public void setCensoredReasonHrefAbove(String censoredReasonHrefAbove) {
        this.censoredReasonHrefAbove = censoredReasonHrefAbove;
    }

    public String getCensoredReasonHrefAbove() {
        return censoredReasonHrefAbove;
    }

    @Setting(value = DetectionLimitSettings.CENSORED_REASONS_TITLE_BELOW_KEY, required = false)
    public void setCensoredReasonTitleBelow(String censoredReasonTitleBelow) {
        this.censoredReasonTitleBelow = censoredReasonTitleBelow;
    }

    public String getCensoredReasonTitleBelow() {
        return censoredReasonTitleBelow;
    }

    @Setting(value = DetectionLimitSettings.CENSORED_REASONS_TITLE_ABOVE_KEY, required = false)
    public void setCensoredReasonTitleAbove(String censoredReasonTitleAbove) {
        this.censoredReasonTitleAbove = censoredReasonTitleAbove;
    }

    public String getCensoredReasonTitleAbove() {
        return censoredReasonTitleAbove;
    }

}
