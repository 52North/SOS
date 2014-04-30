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
package org.n52.sos.ds.hibernate.values;

import java.util.Date;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.series.SeriesValueDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesValueTimeDAO;
import org.n52.sos.ds.hibernate.entities.interfaces.BlobValue;
import org.n52.sos.ds.hibernate.entities.interfaces.BooleanValue;
import org.n52.sos.ds.hibernate.entities.interfaces.CategoryValue;
import org.n52.sos.ds.hibernate.entities.interfaces.CountValue;
import org.n52.sos.ds.hibernate.entities.interfaces.GeometryValue;
import org.n52.sos.ds.hibernate.entities.interfaces.NumericValue;
import org.n52.sos.ds.hibernate.entities.interfaces.SweDataArrayValue;
import org.n52.sos.ds.hibernate.entities.interfaces.TextValue;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValue;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValueTime;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
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
import org.n52.sos.util.XmlHelper;

public abstract class HibernateSeriesStreamingValue extends StreamingValue {

    private static final long serialVersionUID = 201732114914686926L;

    protected final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    protected final SeriesValueDAO seriesValueDAO = new SeriesValueDAO();

    protected final SeriesValueTimeDAO seriesValueTimeDAO = new SeriesValueTimeDAO();

    protected Session session;

    protected long series;

    protected OmObservation observationTemplate;

    protected GetObservationRequest request;

    protected Criterion temporalFilterCriterion;

    public HibernateSeriesStreamingValue(GetObservationRequest request, long series) {
        this.request = request;
        this.series = series;
    }

    @Override
    protected void queryTimes() {
        try {
            SeriesValueTime minTime;
            SeriesValueTime maxTime;
            // query with temporal filter
            if (temporalFilterCriterion != null) {
                minTime = seriesValueTimeDAO.getMinSeriesValueFor(request, series, temporalFilterCriterion, session);
                maxTime = seriesValueTimeDAO.getMaxSeriesValueFor(request, series, temporalFilterCriterion, session);
            }
            // query without temporal or indeterminate filters
            else {
                minTime = seriesValueTimeDAO.getMinSeriesValueFor(request, series, session);
                maxTime = seriesValueTimeDAO.getMaxSeriesValueFor(request, series, session);
            }
            setPhenomenonTime(createPhenomenonTime(minTime, maxTime));
            setResultTime(createResutlTime(maxTime));
            setValidTime(createValidTime(minTime, maxTime));
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    @Override
    protected void queryUnit() {
        try {
            setUnit(seriesValueDAO.getUnit(request, series, session));
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
    
    protected TimeValuePair createTimeValuePairFrom(SeriesValue seriesValue) throws OwsExceptionReport {
        return new TimeValuePair(createPhenomenonTime(seriesValue), getValueFrom(seriesValue));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void addValuesToObservation(OmObservation observation, SeriesValue seriesValue) throws OwsExceptionReport {
        observation.setObservationID(Long.toString(seriesValue.getObservationId()));
        if (seriesValue.isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(seriesValue.getIdentifier());
            if (seriesValue.isSetCodespace()) {
                identifier.setCodeSpace(seriesValue.getCodespace().getCodespace());
            }
            observation.setIdentifier(identifier);
        }
        if (seriesValue.isSetDescription()) {
            observation.setDescription(seriesValue.getDescription());
        }
        observation.setResultTime(createResutlTime(seriesValue.getResultTime()));
        observation.setValidTime(createValidTime(seriesValue.getValidTimeStart(), seriesValue.getValidTimeEnd()));
        observation.setValue(new SingleObservationValue(createPhenomenonTime(seriesValue), getValueFrom(seriesValue)));
    }

    public void setObservationTemplate(OmObservation observationTemplate) {
        this.observationTemplate = observationTemplate;
    }

    public void setTemporalFilterCriterion(Criterion temporalFilterCriterion) {
        this.temporalFilterCriterion = temporalFilterCriterion;
    
    }

    private Time createPhenomenonTime(SeriesValue seriesValue) {
        // create time element
        final DateTime phenStartTime = new DateTime(seriesValue.getPhenomenonTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (seriesValue.getPhenomenonTimeEnd() != null) {
            phenEndTime = new DateTime(seriesValue.getPhenomenonTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        return createTime(phenStartTime, phenEndTime);
    }

    private Time createPhenomenonTime(SeriesValueTime minTime, SeriesValueTime maxTime) {
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

    private TimeInstant createResutlTime(SeriesValueTime maxTime) {
        DateTime dateTime = new DateTime(maxTime.getResultTime(), DateTimeZone.UTC);
        return new TimeInstant(dateTime);
    }

    private TimeInstant createResutlTime(Date maxTime) {
        DateTime dateTime = new DateTime(maxTime, DateTimeZone.UTC);
        return new TimeInstant(dateTime);
    }

    private Time createValidTime(SeriesValueTime minTime, SeriesValueTime maxTime) {
        // create time element
        if (minTime.getValidTimeStart() != null && maxTime.getValidTimeEnd() != null) {
            final DateTime startTime = new DateTime(minTime.getValidTimeStart(), DateTimeZone.UTC);
            DateTime endTime = new DateTime(maxTime.getValidTimeEnd(), DateTimeZone.UTC);
            return createTime(startTime, endTime);
        }
        return null;
    }

    private TimePeriod createValidTime(Date validTimeStart, Date validTimeEnd) {
        // create time element
        if (validTimeStart != null && validTimeEnd != null) {
            final DateTime startTime = new DateTime(validTimeStart, DateTimeZone.UTC);
            DateTime endTime = new DateTime(validTimeEnd, DateTimeZone.UTC);
            return new TimePeriod(startTime, endTime);
        }
        return null;
    }

    private Time createTime(DateTime phenStartTime, DateTime phenEndTime) {
        if (phenStartTime.equals(phenEndTime)) {
            return new TimeInstant(phenStartTime);
        } else {
            return new TimePeriod(phenStartTime, phenEndTime);
        }
    }

    private Value<?> getValueFrom(SeriesValue seriesValue) throws OwsExceptionReport {
        Value<?> value = null;
        if (seriesValue instanceof NumericValue) {
            value = new QuantityValue(((NumericValue) seriesValue).getValue());
        } else if (seriesValue instanceof BooleanValue) {
            value =
                    new org.n52.sos.ogc.om.values.BooleanValue(
                            Boolean.valueOf(((BooleanValue) seriesValue).getValue()));
        } else if (seriesValue instanceof CategoryValue) {
            value = new org.n52.sos.ogc.om.values.CategoryValue(((CategoryValue) seriesValue).getValue());
        } else if (seriesValue instanceof CountValue) {
            value = new org.n52.sos.ogc.om.values.CountValue(Integer.valueOf(((CountValue) seriesValue).getValue()));
        } else if (seriesValue instanceof TextValue) {
            value = new org.n52.sos.ogc.om.values.TextValue(((TextValue) seriesValue).getValue().toString());
        } else if (seriesValue instanceof GeometryValue) {
            value = new org.n52.sos.ogc.om.values.GeometryValue(((GeometryValue) seriesValue).getValue());
        } else if (seriesValue instanceof BlobValue) {
            value = new UnknownValue(((BlobValue) seriesValue).getValue());
        } else if (seriesValue instanceof SweDataArrayValue) {
            org.n52.sos.ogc.om.values.SweDataArrayValue sweDataArrayValue =
                    new org.n52.sos.ogc.om.values.SweDataArrayValue();
            final XmlObject xml = XmlHelper.parseXmlString(((SweDataArrayValue) seriesValue).getValue());
            sweDataArrayValue.setValue((SweDataArray) CodingHelper.decodeXmlElement(xml));
            value = sweDataArrayValue;
        }
        if (value != null && seriesValue.isSetUnit()) {
            value.setUnit(seriesValue.getUnit().getUnit());
        }
        return value;
    }

}
