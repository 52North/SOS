/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
import java.util.Set;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.quality.OmResultQuality;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.ProfileValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;
import org.n52.sos.w3c.xlink.AttributeSimpleAttrs;
import org.n52.sos.w3c.xlink.SimpleAttrs;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Class represents a SOS/O&M observation
 * 
 * @since 4.0.0
 */
public class OmObservation extends AbstractFeature implements Serializable, AttributeSimpleAttrs {
    private static final long serialVersionUID = 2703347670924921229L;
    private SimpleAttrs simpleAttrs;

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
    private ParameterHolder parameterHolder = new ParameterHolder();
    
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

    /** separator of value tuples, which are contained in the result element */
    private String tupleSeparator;
    
    /** separator of decimal values, which are contained in the result element */
    private String decimalSeparator;

    /**
     * Measurment quality
     */
    private Set<OmResultQuality> qualityList = Sets.newHashSet();

    private Set<OmObservationContext> relatedObservations = Sets.newHashSet();
    
    private String additionalMergeIndicator;
    
    private String seriesType;

    /**
     * constructor
     */
    public OmObservation() {
        super();
    }
    
    @Override
    public void setSimpleAttrs(SimpleAttrs simpleAttrs) {
        this.simpleAttrs = simpleAttrs;
     }

     @Override
     public SimpleAttrs getSimpleAttrs() {
         return simpleAttrs;
     }

     @Override
     public boolean isSetSimpleAttrs() {
         return getSimpleAttrs() != null && getSimpleAttrs().isSetHref();
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
     * @return 
     */
    public OmObservation setObservationConstellation(final OmObservationConstellation observationConstellation) {
        this.observationConstellation = observationConstellation;
        return this;
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
    
    public boolean isSetPhenomenonTime() {
        return getPhenomenonTime() != null && !getPhenomenonTime().isEmpty();
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
     * Get decimal separator
     * 
     * @return the decimalSeparator
     */
    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    /**
     * Set decimal separator
     * 
     * @param decimalSeparator
     *            the decimalSeparator to set
     */
    public void setDecimalSeparator(final String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
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

    public boolean isSetValue() {
        return getValue() != null && getValue().isSetValue();
    }

    /**
     * Merge this observation with passed observation
     * 
     * @param sosObservation
     *            Observation to merge
     */
    public void mergeWithObservation(final OmObservation sosObservation) {
        mergeValues(sosObservation.getValue());
        mergeResultTimes(sosObservation);
//        setObservationTypeToSweArrayObservation();
    }

    /**
     * Merge this observation with passed observation
     * 
     * @param observationValue
     *            Observation to merge
     */
    public void mergeWithObservation(ObservationValue<?> observationValue) {
        mergeValues(observationValue);
        // mergeResultTimes(sosObservation);
//        setObservationTypeToSweArrayObservation();
    }

//    /**
//     * Set the observation type to
//     * {@link OmConstants#OBS_TYPE_SWE_ARRAY_OBSERVATION}
//     */
//    private void setObservationTypeToSweArrayObservation() {
//        observationConstellation.setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
//    }

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
     * @param observationValue
     *            Observation to merge
     */
    protected boolean mergeValues(final ObservationValue<?> observationValue) {
        TVPValue tvpValue;
        if (getValue() instanceof SingleObservationValue) {
            if (getValue().getValue() instanceof ProfileValue && observationValue.getValue() instanceof ProfileValue) {
                ((ProfileValue)getValue().getValue()).addValues(((ProfileValue)observationValue.getValue()).getValue());
                return true;
            } else if (getValue().getValue() instanceof SweDataArrayValue
                    && observationValue.getValue() instanceof SweDataArrayValue
                    && ((SweDataArray) getValue().getValue().getValue()).getElementType()
                            .equals(((SweDataArray) observationValue.getValue().getValue()).getElementType())) {
                ((SweDataArray) getValue().getValue().getValue())
                        .addAll(((SweDataArray) observationValue.getValue().getValue()).getValues());
                return true;
            } else {
                tvpValue = convertSingleValueToMultiValue((SingleObservationValue<?>) value);
            }
        } else {
            tvpValue = (TVPValue) ((MultiObservationValues<?>) value).getValue();
        }
        if (observationValue instanceof SingleObservationValue) {
            final SingleObservationValue<?> singleValue = (SingleObservationValue<?>) observationValue;
            if (!(singleValue.getValue() instanceof NilTemplateValue)) {
                final TimeValuePair timeValuePair =
                        new TimeValuePair(singleValue.getPhenomenonTime(), singleValue.getValue());
                tvpValue.addValue(timeValuePair);
            }
        } else if (observationValue instanceof MultiObservationValues) {
            final MultiObservationValues<?> multiValue = (MultiObservationValues<?>) observationValue;
            tvpValue.addValues(((TVPValue) multiValue.getValue()).getValue());
        }
        return true;
    }

    /**
     * Converts {@link SingleObservationValue} to {@link TVPValue} and updates the value of this observation.
     * 
     * @param singleValue
     *            Single observation value
     * @return Converted TVPValue value
     */
    public TVPValue convertSingleValueToMultiValue(final SingleObservationValue<?> singleValue) {
        final MultiObservationValues<List<TimeValuePair>> multiValue =
                new MultiObservationValues<List<TimeValuePair>>();
        final TVPValue tvpValue = new TVPValue();
        if (singleValue.isSetUnit()) {
            tvpValue.setUnit(singleValue.getUnit());
        } else if (singleValue.getValue().isSetUnit()) {
            tvpValue.setUnit(singleValue.getValue().getUnit());
        }
        if (singleValue.isSetMetadata()) {
            multiValue.setMetadata(singleValue.getMetadata());
        }
        if (singleValue.isSetDefaultPointMetadata()) {
            multiValue.setDefaultPointMetadata(singleValue.getDefaultPointMetadata());
        }
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
     * Check whether decimal separator is set
     * 
     * @return <code>true</code>, if decimal separator is set
     */
    public boolean isSetDecimalSeparator() {
        return StringHelper.isNotEmpty(getDecimalSeparator());
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
        return parameterHolder.getParameter();
    }

    /**
     * Set parameter
     * 
     * @param parameter
     *            the parameter to set
     */
    public void setParameter(Collection<NamedValue<?>> parameter) {
        this.parameterHolder.addParameter(parameter);
    }

    /**
     * Add parameter
     * 
     * @param namedValue
     *            the namedValue to add to parameter
     */
    public void addParameter(NamedValue<?> namedValue) {
        parameterHolder.addParameter(namedValue);
    }
    
    public ParameterHolder getParameterHolder() {
        return parameterHolder;
    }

    /**
     * Check whether parameter is set
     * 
     * @return <code>true</code>, if parameter is set
     */
    public boolean isSetParameter() {
        return parameterHolder != null && CollectionHelper.isNotEmpty(getParameter());
    }

    /**
     * Check whether spatial filtering profile parameter is set
     * 
     * @return <code>true</code>, if spatial filtering profile parameter is set
     */
    public boolean isSetSpatialFilteringProfileParameter() {
        return parameterHolder.isSetSpatialFilteringProfileParameter();
    }
    
    /**
     * Remove spatial filtering profile parameter
     */
    public void removeSpatialFilteringProfileParameter() {
        if (isSetSpatialFilteringProfileParameter()) {
            removeParameter(getSpatialFilteringProfileParameter());
        }
    }

    /**
     * Remove parameter from list
     * 
     * @param parameter
     *            Parameter to remove
     */
    public void removeParameter(NamedValue<?> parameter) {
        getParameterHolder().removeParameter(parameter);
    }
    
    /**
     * Add sampling geometry to observation
     * 
     * @param samplingGeometry
     *            The sampling geometry to set
     * @return this
     */
    public OmObservation addSpatialFilteringProfileParameter(Geometry samplingGeometry) {
        parameterHolder.addSpatialFilteringProfileParameter(samplingGeometry);
        return this;
    }

    /**
     * Get spatial filtering profile parameter
     * 
     * @return Spatial filtering profile parameter
     */
    public NamedValue<Geometry> getSpatialFilteringProfileParameter() {
        return parameterHolder.getSpatialFilteringProfileParameter();
    }

    /**
     * Check whether height parameter is set
     * 
     * @return <code>true</code>, if height parameter is set
     */
    public boolean isSetHeightParameter() {
        return parameterHolder.isSetHeightParameter();
    }

    /**
     * Get height parameter
     * 
     * @return Height parameter
     */
    public NamedValue<Double> getHeightParameter() {
        return parameterHolder.getHeightParameter();
    }
    

    /**
     * Check whether depth parameter is set
     * 
     * @return <code>true</code>, if depth parameter is set
     */
    public boolean isSetDepthParameter() {
        return parameterHolder.isSetDepthParameter();
    }

    /**
     * Get depth parameter
     * 
     * @return Depth parameter
     */
    public NamedValue<Double> getDepthParameter() {
        return parameterHolder.getDepthParameter();
    }
    
    
    public boolean isSetHeightDepthParameter() {
        return parameterHolder.isSetHeightDepthParameter();
    }
    
    public NamedValue<Double> getHeightDepthParameter() {
        return parameterHolder.getHeightDepthParameter();
    }

    public OmObservation cloneTemplate() {
       return cloneTemplate(new OmObservation());
    }
    
    public OmObservation cloneTemplate(boolean withIdentifierNameDesription) {
        OmObservation clonedTemplate = cloneTemplate(new OmObservation());
        if (withIdentifierNameDesription) {
            if (this.getObservationConstellation().isSetIdentifier()) {
                clonedTemplate.setIdentifier(this.getObservationConstellation().getIdentifier());
                clonedTemplate.setName(this.getObservationConstellation().getName());
                clonedTemplate.setDescription(this.getObservationConstellation().getDescription());
            } else {
                clonedTemplate.setIdentifier(this.getIdentifier());
                clonedTemplate.setName(this.getName());
                clonedTemplate.setDescription(this.getDescription());
            }
        }
        return clonedTemplate;
     }
    
    protected OmObservation cloneTemplate(OmObservation clone) {
        clone.setObservationConstellation(this.getObservationConstellation());
        clone.setMetaDataProperty(this.getMetaDataProperty());
        if (this.getParameter() != null) {
            clone.setParameter(Sets.newTreeSet(this.getParameter()));
        }
        clone.setRelatedObservations(this.getRelatedObservations());
        clone.setResultType(this.getResultType());
        clone.setTokenSeparator(this.getTokenSeparator());
        clone.setTupleSeparator(this.getTupleSeparator());
        clone.setDecimalSeparator(this.getDecimalSeparator());
        clone.setSeriesType(this.getSeriesType());
        return clone;
    }
    
    public OmObservation copyTo(OmObservation copyOf) {
        super.copyTo(copyOf);
        copyOf.setObservationID(getObservationID());
        copyOf.setResultTime(getResultTime());
        copyOf.setValidTime(getValidTime());
        copyOf.setObservationConstellation(getObservationConstellation());
        copyOf.setResultType(getResultType());
        copyOf.setParameter(getParameter());
        copyOf.setValue(getValue());
        copyOf.setTokenSeparator(getTokenSeparator());
        copyOf.setNoDataValue(getNoDataValue());
        copyOf.setTupleSeparator(getTupleSeparator());
        copyOf.setDecimalSeparator(getDecimalSeparator());
        copyOf.setResultQuality(getResultQuality());
        copyOf.setRelatedObservations(getRelatedObservations());
        copyOf.setAdditionalMergeIndicator(getAdditionalMergeIndicator());
        return copyOf;
    } 

    @Override
    public String getGmlId() {
        if (Strings.isNullOrEmpty(super.getGmlId()) && isSetObservationID()) {
            setGmlId("o_" + getObservationID());
        }
        return super.getGmlId();
    }

    /**
     * Set result quality
     * 
     * @param qualityList
     *            Result quality to set
     */
    public OmObservation setResultQuality(Set<OmResultQuality> qualityList) {
        this.qualityList = qualityList;
        return this;
    }

    public OmObservation addResultQuality(Set<OmResultQuality> qualityList) {
        this.qualityList.addAll(qualityList);
        return this;
    }

    public OmObservation addResultQuality(OmResultQuality qualityList) {
        this.qualityList.add(qualityList);
        return this;
    }

    /**
     * Get result quality
     * 
     * @return Result quality
     */
    public Set<OmResultQuality> getResultQuality() {
        return qualityList;
    }

    public boolean isSetResultQuality() {
        return CollectionHelper.isNotEmpty(getResultQuality());
    }

    /**
     * Get related observations
     * 
     * @return the relatedObservations
     */
    public Set<OmObservationContext> getRelatedObservations() {
        return relatedObservations;
    }

    /**
     * Set related observations
     * 
     * @param relatedObservations
     *            the relatedObservations to set
     */
    public void setRelatedObservations(Set<OmObservationContext> relatedObservations) {
        this.relatedObservations.clear();
        this.relatedObservations.addAll(relatedObservations);
    }

    /**
     * Add related observations
     * 
     * @param relatedObservations
     *            the relatedObservations to set
     */
    public void addRelatedObservations(Set<OmObservationContext> relatedObservations) {
        this.relatedObservations.addAll(relatedObservations);
    }

    /**
     * Add a related observation
     * 
     * @param relatedObservations
     *            the relatedObservations to set
     */
    public void addRelatedObservation(OmObservationContext relatedObservation) {
        this.relatedObservations.add(relatedObservation);
    }

    /**
     * Check if related observations are set
     * 
     * @return <code>true</code>, if related observations are set
     */
    public boolean isSetRelatedObservations() {
        return CollectionHelper.isNotEmpty(getRelatedObservations());
    }

    public OmObservation setAdditionalMergeIndicator(String additionalMergeIndicator) {
        this.additionalMergeIndicator = additionalMergeIndicator;
        return this;
    }

    public String getAdditionalMergeIndicator() {
        return additionalMergeIndicator;
    }

    public boolean isSetAdditionalMergeIndicator() {
        return StringHelper.isNotEmpty(getAdditionalMergeIndicator());
    }

    public boolean checkForMerge(OmObservation observation) {
        boolean merge = true;
        if (isSetAdditionalMergeIndicator() && observation.isSetAdditionalMergeIndicator()) {
            merge = getAdditionalMergeIndicator().equals(observation.getAdditionalMergeIndicator());
        } else if ((isSetAdditionalMergeIndicator() && !observation.isSetAdditionalMergeIndicator())
                || (!isSetAdditionalMergeIndicator() && observation.isSetAdditionalMergeIndicator())) {
            merge = false;
        }
        return getObservationConstellation().equals(observation.getObservationConstellation()) && merge && getObservationConstellation().checkObservationTypeForMerging();
    }

    /**
     * @return the seriesType
     */
    public String getSeriesType() {
        return seriesType;
    }

    /**
     * @param seriesType the seriesType to set
     */
    public void setSeriesType(String seriesType) {
        this.seriesType = seriesType;
    }
    
    public boolean isSetSeriesType() {
        return !Strings.isNullOrEmpty(getSeriesType());
    }
    
    /**
     * Check whether category parameter is set
     * 
     * @return <code>true</code>, if category parameter is set
     */
    public boolean isSetCategoryParameter() {
        return parameterHolder.hasParameter(OmConstants.PARAMETER_NAME_CATEGORY);
    }
    
    /**
     * Remove category parameter
     */
    public void removeCategoryParameter() {
        if (isSetCategoryParameter()) {
            removeParameter(getCategoryParameter());
        }
    }
    
    /**
     * Add category to observation
     * 
     * @param category
     *            The category to set
     * @return this
     */
    public OmObservation addCategoryParameter(String category) {
        parameterHolder.addParameter(new NamedValue<String>(new ReferenceType(OmConstants.PARAMETER_NAME_CATEGORY),
                new TextValue(category)));
        return this;
    }
    
    public OmObservation addCategoryParameter(TextValue category) {
        parameterHolder.addParameter(new NamedValue<String>(new ReferenceType(OmConstants.PARAMETER_NAME_CATEGORY),
                category));
        return this;
    }

    /**
     * Get category parameter
     * 
     * @return category parameter
     */
    public NamedValue<?> getCategoryParameter() {
        return parameterHolder.getParameter(OmConstants.PARAMETER_NAME_CATEGORY);
    }
}
