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
package org.n52.sos.ogc.om;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Class represents a SOS/O&M observation
 * 
 * @since 4.0.0
 */
public class OmObservation extends AbstractFeature implements Serializable {
    private static final long serialVersionUID = 2703347670924921229L;

    /**
     * ID of this observation; in the standard 52n SOS PostgreSQL database, this
     * is implemented through a sequence type.
     */
    private String observationID;

    /** result time of the observation */
    private TimeInstant resultTime;

    /** valid time of the observation */
    private TimePeriod validTime;

    /**
     * constellation of procedure, obervedProperty, offering and observationType
     */
    private OmObservationConstellation observationConstellation;

    /** type of the value or the result the value points to */
    private String resultType;

    /**
     * O&M parameter
     */
    private Collection<NamedValue<?>> parameter;

    /**
     * Map with observation values for each obsservableProeprty
     */
    private ObservationValue<?> value;

    /**
     * token separator for the value tuples contained in the result element of
     * the generic observation
     */
    private String tokenSeparator;

    /** no data value for the values contained in the result element */
    private String noDataValue;

    /** separator of value tuples, which are contained in the resulte element */
    private String tupleSeparator;

    /**
     * constructor
     */
    public OmObservation() {
        super();
    }

    /**
     * Get the observation constellation
     * 
     * @return the observationConstellation
     */
    public OmObservationConstellation getObservationConstellation() {
        return observationConstellation;
    }

    /**
     * Set the observation constellation
     * 
     * @param observationConstellation
     *            the observationConstellation to set
     */
    public void setObservationConstellation(final OmObservationConstellation observationConstellation) {
        this.observationConstellation = observationConstellation;
    }

    /**
     * Get observation ID
     * 
     * @return the observationID
     */
    public String getObservationID() {
        return observationID;
    }

    /**
     * Set observation ID
     * 
     * @param observationID
     *            the observationID to set
     */
    public void setObservationID(final String observationID) {
        this.observationID = observationID;
    }

    /**
     * Get phenomenon time
     * 
     * @return the phenomenonTime
     */
    public Time getPhenomenonTime() {
        return value.getPhenomenonTime();
    }

    /**
     * Get result time
     * 
     * @return the resultTime
     */
    public TimeInstant getResultTime() {
        return resultTime;
    }

    /**
     * Set result time
     * 
     * @param resultTime
     *            the resultTime to set
     */
    public void setResultTime(final TimeInstant resultTime) {
        this.resultTime = resultTime;
    }

    /**
     * Get valid time
     * 
     * @return the validTime
     */
    public TimePeriod getValidTime() {
        return validTime;
    }

    /**
     * Set valid time
     * 
     * @param validTime
     *            the validTime to set
     */
    public void setValidTime(final TimePeriod validTime) {
        this.validTime = validTime;
    }

    /**
     * Get result type
     * 
     * @return the resultType
     */
    public String getResultType() {
        return resultType;
    }

    /**
     * Set result type
     * 
     * @param resultType
     *            the resultType to set
     */
    public void setResultType(final String resultType) {
        this.resultType = resultType;
    }

    /**
     * Get token separator
     * 
     * @return the tokenSeparator
     */
    public String getTokenSeparator() {
        return tokenSeparator;
    }

    /**
     * Set token separator
     * 
     * @param tokenSeparator
     *            the tokenSeparator to set
     */
    public void setTokenSeparator(final String tokenSeparator) {
        this.tokenSeparator = tokenSeparator;
    }

    /**
     * Get noData value
     * 
     * @return the noDataValue
     */
    public String getNoDataValue() {
        return noDataValue;
    }

    /**
     * Set noData value
     * 
     * @param noDataValue
     *            the noDataValue to set
     */
    public void setNoDataValue(final String noDataValue) {
        this.noDataValue = noDataValue;
    }

    /**
     * Get tuple separator
     * 
     * @return the tupleSeparator
     */
    public String getTupleSeparator() {
        return tupleSeparator;
    }

    /**
     * Set tuple separator
     * 
     * @param tupleSeparator
     *            the tupleSeparator to set
     */
    public void setTupleSeparator(final String tupleSeparator) {
        this.tupleSeparator = tupleSeparator;
    }

    /**
     * Get observation values
     * 
     * @return the values
     */
    public ObservationValue<?> getValue() {
        return value;
    }

    /**
     * Set observation values
     * 
     * @param value
     *            the values to set
     */
    public void setValue(final ObservationValue<?> value) {
        this.value = value;
    }

    /**
     * Merge this observation with passed observation
     * 
     * @param sosObservation
     *            Observation to merge
     */
    public void mergeWithObservation(final OmObservation sosObservation) {
        mergeValues(sosObservation);
        mergeResultTimes(sosObservation);
        setObservationTypeToSweArrayObservation();
    }

    /**
     * Set the observation type to
     * {@link OmConstants#OBS_TYPE_SWE_ARRAY_OBSERVATION}
     */
    private void setObservationTypeToSweArrayObservation() {
        observationConstellation.setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
    }

    /**
     * Merge result time with passed observation result time
     * 
     * @param sosObservation
     *            Observation to merge
     */
    private void mergeResultTimes(final OmObservation sosObservation) {
        if (isSetResultTime() && sosObservation.isSetResultTime()) {
            if (getResultTime().getValue().isBefore(sosObservation.getResultTime().getValue())) {
                resultTime = sosObservation.getResultTime();
            }
        } else if (!isSetResultTime() && sosObservation.isSetResultTime()) {
            resultTime = sosObservation.getResultTime();
        }
    }

    /**
     * Merge observation values with passed observation values
     * 
     * @param sosObservation
     *            Observation to merge
     */
    private void mergeValues(final OmObservation sosObservation) {
        TVPValue tvpValue;
        if (getValue() instanceof SingleObservationValue) {
            tvpValue = convertSingleValueToMultiValue((SingleObservationValue<?>) value);
        } else {
            tvpValue = (TVPValue) ((MultiObservationValues<?>) value).getValue();
        }
        if (sosObservation.getValue() instanceof SingleObservationValue) {
            final SingleObservationValue<?> singleValue = (SingleObservationValue<?>) sosObservation.getValue();
            if (!(singleValue.getValue() instanceof NilTemplateValue)) {
                final TimeValuePair timeValuePair =
                        new TimeValuePair(singleValue.getPhenomenonTime(), singleValue.getValue());
                tvpValue.addValue(timeValuePair);
            }
        } else if (sosObservation.getValue() instanceof MultiObservationValues) {
            final MultiObservationValues<?> multiValue = (MultiObservationValues<?>) sosObservation.getValue();
            tvpValue.addValues(((TVPValue) multiValue.getValue()).getValue());
        }
    }

    /**
     * Convert {@link SingleObservationValue} to {@link TVPValue}
     * 
     * @param singleValue
     *            Single observation value
     * @return Converted TVPValue value
     */
    private TVPValue convertSingleValueToMultiValue(final SingleObservationValue<?> singleValue) {
        final MultiObservationValues<List<TimeValuePair>> multiValue =
                new MultiObservationValues<List<TimeValuePair>>();
        final TVPValue tvpValue = new TVPValue();
        tvpValue.setUnit(singleValue.getValue().getUnit());
        final TimeValuePair timeValuePair = new TimeValuePair(singleValue.getPhenomenonTime(), singleValue.getValue());
        tvpValue.addValue(timeValuePair);
        multiValue.setValue(tvpValue);
        value = multiValue;
        return tvpValue;
    }

    /**
     * Check whether observation id is set
     * 
     * @return <code>true</code>, if observation id is set
     */
    public boolean isSetObservationID() {
        return StringHelper.isNotEmpty(getObservationID());
    }

    /**
     * Check whether tuple separator is set
     * 
     * @return <code>true</code>, if tuple separator is set
     */
    public boolean isSetTupleSeparator() {
        return StringHelper.isNotEmpty(getTupleSeparator());
    }

    /**
     * Check whether token separator is set
     * 
     * @return <code>true</code>, if token separator is set
     */
    public boolean isSetTokenSeparator() {
        return StringHelper.isNotEmpty(getTokenSeparator());
    }

    /**
     * Check whether result time is set
     * 
     * @return <code>true</code>, if result time is set
     */
    public boolean isSetResultTime() {
        return resultTime != null && resultTime.isSetValue();
    }

    /**
     * Check whether result time is template is set
     * 
     * @return <code>true</code>, if result time is template is set
     */
    public boolean isTemplateResultTime() {
        return isSetResultTime()
                && (getResultTime().isIndeterminateValueEqualTo(Time.TimeIndeterminateValue.template) || getResultTime()
                        .isNilReasonEqualTo(Time.NilReason.template));
    }

    /**
     * Check whether result type is set
     * 
     * @return <code>true</code>, if result type is set
     */
    public boolean isSetResultType() {
        return StringHelper.isNotEmpty(resultType);
    }

    /**
     * Check whether valid time is set
     * 
     * @return <code>true</code>, if valid time is set
     */
    public boolean isSetValidTime() {
        return validTime != null && !validTime.isEmpty();
    }

    /**
     * Get parameter
     * 
     * @return the parameter
     */
    public Collection<NamedValue<?>> getParameter() {
        return parameter;
    }

    /**
     * Set parameter
     * 
     * @param parameter
     *            the parameter to set
     */
    public void setParameter(Collection<NamedValue<?>> parameter) {
        this.parameter = parameter;
    }

    /**
     * Add parameter
     * 
     * @param namedValue
     *            the namedValue to add to parameter
     */
    public void addParameter(NamedValue<?> namedValue) {
        if (parameter == null) {
            parameter = Sets.newHashSet();
        }
        parameter.add(namedValue);
    }

    /**
     * Check whether parameter is set
     * 
     * @return <code>true</code>, if parameter is set
     */
    public boolean isSetParameter() {
        return CollectionHelper.isNotEmpty(getParameter());
    }

    /**
     * Check whether spatial filtering profile parameter is set
     * 
     * @return <code>true</code>, if spatial filtering profile parameter is set
     */
    public boolean isSetSpatialFilteringProfileParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isSamplingGeometryParameter(namedValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get spatial filtering profile parameter
     * 
     * @return Spatial filtering profile parameter
     */
    @SuppressWarnings("unchecked")
    public NamedValue<Geometry> getSpatialFilteringProfileParameter() {
        if (isSetParameter()) {
            for (NamedValue<?> namedValue : getParameter()) {
                if (isSamplingGeometryParameter(namedValue)) {
                    return (NamedValue<Geometry>) namedValue;
                }
            }
        }
        return null;
    }

    /**
     * Check whether sampling geometry for spatial filtering profile is set
     * 
     * @return <code>true</code>, if sampling geometry for spatial filtering
     *         profile is set
     */
    private boolean isSamplingGeometryParameter(NamedValue<?> namedValue) {
        return namedValue.isSetName() && namedValue.getName().isSetHref()
                && namedValue.getName().getHref().equals(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY)
                && namedValue.getValue() instanceof GeometryValue;
    }
    
    public OmObservation cloneTemplate() {
        OmObservation clone = new OmObservation();
        clone.setObservationConstellation(this.getObservationConstellation());
        clone.setResultType(this.getResultType());
        clone.setTokenSeparator(this.getTokenSeparator());
        clone.setTupleSeparator(this.getTupleSeparator());
        return clone;
    }
}
