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
package org.n52.sos.ds.hibernate.values;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.entities.AbstractObservationTime;
import org.n52.sos.ds.hibernate.entities.interfaces.BlobValue;
import org.n52.sos.ds.hibernate.entities.interfaces.BooleanValue;
import org.n52.sos.ds.hibernate.entities.interfaces.CategoryValue;
import org.n52.sos.ds.hibernate.entities.interfaces.CountValue;
import org.n52.sos.ds.hibernate.entities.interfaces.GeometryValue;
import org.n52.sos.ds.hibernate.entities.interfaces.NumericValue;
import org.n52.sos.ds.hibernate.entities.interfaces.SweDataArrayValue;
import org.n52.sos.ds.hibernate.entities.interfaces.TextValue;
import org.n52.sos.ds.hibernate.entities.values.AbstractValue;
import org.n52.sos.ds.hibernate.util.observation.SpatialFilteringProfileAdder;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.XmlHelper;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract class for streaming values
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class AbstractHibernateStreamingValue extends StreamingValue {

    private static final long serialVersionUID = -8355955808723620476L;

    protected final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    private SpatialFilteringProfileAdder spatialFilteringProfileAdder;

    protected Session session;

    protected OmObservation observationTemplate;

    protected GetObservationRequest request;

    protected Criterion temporalFilterCriterion;

    /**
     * constructor
     * 
     * @param request
     *            {@link GetObservationRequest}
     */
    public AbstractHibernateStreamingValue(GetObservationRequest request) {
        this.request = request;
    }

    /**
     * Set the observation template which contains all metadata
     * 
     * @param observationTemplate
     *            Observation template to set
     */
    public void setObservationTemplate(OmObservation observationTemplate) {
        this.observationTemplate = observationTemplate;
    }

    /**
     * Set the temporal filter {@link Criterion}
     * 
     * @param temporalFilterCriterion
     *            Temporal filter {@link Criterion}
     */
    public void setTemporalFilterCriterion(Criterion temporalFilterCriterion) {
        this.temporalFilterCriterion = temporalFilterCriterion;

    }

    /**
     * Create a {@link TimeValuePair} from {@link AbstractValue}
     * 
     * @param abstractValue
     *            {@link AbstractValue} to create {@link TimeValuePair} from
     * @return resulting {@link TimeValuePair}
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    protected TimeValuePair createTimeValuePairFrom(AbstractValue abstractValue) throws OwsExceptionReport {
        return new TimeValuePair(createPhenomenonTime(abstractValue), getValueFrom(abstractValue));
    }

    /**
     * Add {@link AbstractValue} data to {@link OmObservation}
     * 
     * @param observation
     *            {@link OmObservation} to add data
     * @param abstractValue
     *            {@link AbstractValue} to get data from
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void addValuesToObservation(OmObservation observation, AbstractValue abstractValue)
            throws OwsExceptionReport {
        observation.setObservationID(Long.toString(abstractValue.getObservationId()));
        if (abstractValue.isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(abstractValue.getIdentifier());
            if (abstractValue.isSetCodespace()) {
                identifier.setCodeSpace(abstractValue.getCodespace().getCodespace());
            }
            observation.setIdentifier(identifier);
        }
        if (abstractValue.isSetDescription()) {
            observation.setDescription(abstractValue.getDescription());
        }
        Value<?> value = getValueFrom(abstractValue);
        if (!observation.getObservationConstellation().isSetObservationType()) {
            observation.getObservationConstellation().setObservationType(OMHelper.getObservationTypeFor(value));
        }
        observation.setResultTime(createResutlTime(abstractValue.getResultTime()));
        observation.setValidTime(createValidTime(abstractValue.getValidTimeStart(), abstractValue.getValidTimeEnd()));
        observation.setValue(new SingleObservationValue(createPhenomenonTime(abstractValue),
                value));
    }

    /**
     * Get the observation ids from {@link AbstractValue}s
     * 
     * @param abstractValuesResult
     *            {@link AbstractValue}s to get ids from
     * @return Set with ids
     */
    protected Set<Long> getObservationIds(Collection<AbstractValue> abstractValuesResult) {
        Set<Long> ids = new HashSet<Long>();
        for (AbstractValue abstractValue : abstractValuesResult) {
            ids.add(abstractValue.getObservationId());
        }
        return ids;
    }

    /**
     * Get the {@link SpatialFilteringProfileAdder}
     * 
     * @return the spatialFilteringProfileAdder
     */
    @Deprecated
    protected SpatialFilteringProfileAdder getSpatialFilteringProfileAdder() {
        return spatialFilteringProfileAdder;
    }

    /**
     * Set the {@link SpatialFilteringProfileAdder}
     * 
     * @param spatialFilteringProfileAdder
     *            the spatialFilteringProfileAdder to set
     */
    @Deprecated
    protected void setSpatialFilteringProfileAdder(SpatialFilteringProfileAdder spatialFilteringProfileAdder) {
        this.spatialFilteringProfileAdder = spatialFilteringProfileAdder;
    }
    
    /**
     * Check if the {@link SpatialFilteringProfileAdder} is set
     * 
     * @return <code>true</code>, if the {@link SpatialFilteringProfileAdder} is
     *         set
     */
    @Deprecated
    protected boolean isSetSpatialFilteringProfileAdder() {
        return getSpatialFilteringProfileAdder() != null;
    }
    
    /**
     * Create the phenomenon time from {@link AbstractValue}
     * 
     * @param abstractValue
     *            {@link AbstractValue} for get time from
     * @return phenomenon time
     */
    protected Time createPhenomenonTime(AbstractValue abstractValue) {
        // create time element
        final DateTime phenStartTime = new DateTime(abstractValue.getPhenomenonTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (abstractValue.getPhenomenonTimeEnd() != null) {
            phenEndTime = new DateTime(abstractValue.getPhenomenonTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        return createTime(phenStartTime, phenEndTime);
    }

    /**
     * Create phenomenon time from min and max {@link AbstractObservationTime}s
     * 
     * @param minTime
     *            minimum {@link AbstractObservationTime}
     * @param maxTime
     *            maximum {@link AbstractObservationTime}
     * @return phenomenon time
     */
    protected Time createPhenomenonTime(AbstractObservationTime minTime, AbstractObservationTime maxTime) {
        // create time element
        final DateTime phenStartTime = new DateTime(minTime.getPhenomenonTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (maxTime.getPhenomenonTimeEnd() != null) {
            phenEndTime = new DateTime(maxTime.getPhenomenonTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        return createTime(phenStartTime, phenEndTime);
    }

    /**
     * Create result time from {@link AbstractObservationTime}
     * 
     * @param maxTime
     *            {@link AbstractObservationTime} to create result time from
     * @return result time
     */
    protected TimeInstant createResutlTime(AbstractObservationTime maxTime) {
        DateTime dateTime = new DateTime(maxTime.getResultTime(), DateTimeZone.UTC);
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
     * Create valid time from min and max {@link AbstractObservationTime}s
     * 
     * @param minTime
     *            minimum {@link AbstractObservationTime}
     * @param maxTime
     *            maximum {@link AbstractObservationTime}
     * @return valid time or null if valid time is not set in datasource
     */
    protected Time createValidTime(AbstractObservationTime minTime, AbstractObservationTime maxTime) {
        // create time element
        if (minTime.getValidTimeStart() != null && maxTime.getValidTimeEnd() != null) {
            final DateTime startTime = new DateTime(minTime.getValidTimeStart(), DateTimeZone.UTC);
            DateTime endTime = new DateTime(maxTime.getValidTimeEnd(), DateTimeZone.UTC);
            return createTime(startTime, endTime);
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

    /**
     * Get internal {@link Value} from {@link AbstractValue}
     * 
     * @param abstractValue
     *            {@link AbstractValue} to get {@link Value} from
     * @return {@link Value} or null if the concrete {@link AbstractValue} is
     *         not supported
     * @throws OwsExceptionReport
     *             If an error occurs when creating
     *             {@link org.n52.sos.ogc.om.values.SweDataArrayValue}
     */
    protected Value<?> getValueFrom(AbstractValue abstractValue) throws OwsExceptionReport {
        Value<?> value = null;
        if (abstractValue instanceof NumericValue) {
            value = new QuantityValue(((NumericValue) abstractValue).getValue());
        } else if (abstractValue instanceof BooleanValue) {
            value =
                    new org.n52.sos.ogc.om.values.BooleanValue(Boolean.valueOf(((BooleanValue) abstractValue)
                            .getValue()));
        } else if (abstractValue instanceof CategoryValue) {
            value = new org.n52.sos.ogc.om.values.CategoryValue(((CategoryValue) abstractValue).getValue());
        } else if (abstractValue instanceof CountValue) {
            value = new org.n52.sos.ogc.om.values.CountValue(Integer.valueOf(((CountValue) abstractValue).getValue()));
        } else if (abstractValue instanceof TextValue) {
            value = new org.n52.sos.ogc.om.values.TextValue(((TextValue) abstractValue).getValue().toString());
        } else if (abstractValue instanceof GeometryValue) {
            value = new org.n52.sos.ogc.om.values.GeometryValue(((GeometryValue) abstractValue).getValue());
        } else if (abstractValue instanceof BlobValue) {
            value = new UnknownValue(((BlobValue) abstractValue).getValue());
        } else if (abstractValue instanceof SweDataArrayValue) {
            org.n52.sos.ogc.om.values.SweDataArrayValue sweDataArrayValue =
                    new org.n52.sos.ogc.om.values.SweDataArrayValue();
            final XmlObject xml = XmlHelper.parseXmlString(((SweDataArrayValue) abstractValue).getValue());
            sweDataArrayValue.setValue((SweDataArray) CodingHelper.decodeXmlElement(xml));
            value = sweDataArrayValue;
        }
        if (value != null && abstractValue.isSetUnit()) {
            value.setUnit(abstractValue.getUnit().getUnit());
        }
        return value;
    }
    
    protected NamedValue<?> createSpatialFilteringProfileParameter(Geometry samplingGeometry)
            throws OwsExceptionReport {
        final NamedValue<Geometry> namedValue = new NamedValue<Geometry>();
        final ReferenceType referenceType = new ReferenceType(OmConstants.PARAM_NAME_SAMPLING_GEOMETRY);
        namedValue.setName(referenceType);
        // TODO add lat/long version
        Geometry geometry = samplingGeometry;
        namedValue.setValue(new org.n52.sos.ogc.om.values.GeometryValue(GeometryHandler.getInstance()
                .switchCoordinateAxisOrderIfNeeded(geometry)));
        return namedValue;
    }

}
