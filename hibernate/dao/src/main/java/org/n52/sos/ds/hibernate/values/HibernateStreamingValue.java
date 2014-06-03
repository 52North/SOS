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
import org.n52.sos.ds.hibernate.dao.ValueDAO;
import org.n52.sos.ds.hibernate.dao.ValueTimeDAO;
import org.n52.sos.ds.hibernate.entities.interfaces.BlobValue;
import org.n52.sos.ds.hibernate.entities.interfaces.BooleanValue;
import org.n52.sos.ds.hibernate.entities.interfaces.CategoryValue;
import org.n52.sos.ds.hibernate.entities.interfaces.CountValue;
import org.n52.sos.ds.hibernate.entities.interfaces.GeometryValue;
import org.n52.sos.ds.hibernate.entities.interfaces.NumericValue;
import org.n52.sos.ds.hibernate.entities.interfaces.SweDataArrayValue;
import org.n52.sos.ds.hibernate.entities.interfaces.TextValue;
import org.n52.sos.ds.hibernate.entities.values.ObservationValue;
import org.n52.sos.ds.hibernate.entities.values.ObservationValueTime;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OWSConstants.AdditionalRequestParams;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.XmlHelper;

import com.vividsolutions.jts.geom.Geometry;

public abstract class HibernateStreamingValue extends StreamingValue {

    private static final long serialVersionUID = -7451818170087729427L;

    protected final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    protected final ValueDAO valueDAO = new ValueDAO();

    protected final ValueTimeDAO valueTimeDAO = new ValueTimeDAO();

    protected Session session;

    protected long procedure;
    
    protected long featureOfInterest;
    
    protected long observableProperty;

    protected OmObservation observationTemplate;

    protected GetObservationRequest request;

    protected Criterion temporalFilterCriterion;

    public HibernateStreamingValue(GetObservationRequest request, long procedure, long observableProperty, long featureOfInterest) {
        this.request = request;
        this.procedure = procedure;
        this.observableProperty = observableProperty;
        this.featureOfInterest = featureOfInterest;
    }

    @Override
    protected void queryTimes() {
        try {
            ObservationValueTime minTime;
            ObservationValueTime maxTime;
            // query with temporal filter
            if (temporalFilterCriterion != null) {
                minTime = valueTimeDAO.getMinValueFor(request, procedure, observableProperty, featureOfInterest, temporalFilterCriterion, session);
                maxTime = valueTimeDAO.getMaxValueFor(request, procedure, observableProperty, featureOfInterest, temporalFilterCriterion, session);
            }
            // query without temporal or indeterminate filters
            else {
                minTime = valueTimeDAO.getMinValueFor(request, procedure, observableProperty, featureOfInterest, session);
                maxTime = valueTimeDAO.getMaxValueFor(request, procedure, observableProperty, featureOfInterest, session);
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
            setUnit(valueDAO.getUnit(request, procedure, observableProperty, featureOfInterest, session));
        } catch (Exception e) {
            // TODO: handle exception
        }
        
    }
    
    protected TimeValuePair createTimeValuePairFrom(ObservationValue observationValue) throws OwsExceptionReport {
        return new TimeValuePair(createPhenomenonTime(observationValue), getValueFrom(observationValue));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void addValuesToObservation(OmObservation observation, ObservationValue observationValue) throws OwsExceptionReport {
        observation.setObservationID(Long.toString(observationValue.getObservationId()));
        if (observationValue.isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(observationValue.getIdentifier());
            if (observationValue.isSetCodespace()) {
                identifier.setCodeSpace(observationValue.getCodespace().getCodespace());
            }
            observation.setIdentifier(identifier);
        }
        if (observationValue.isSetDescription()) {
            observation.setDescription(observationValue.getDescription());
        }
        observation.setResultTime(createResutlTime(observationValue.getResultTime()));
        observation.setValidTime(createValidTime(observationValue.getValidTimeStart(), observationValue.getValidTimeEnd()));
        observation.setValue(new SingleObservationValue(createPhenomenonTime(observationValue), getValueFrom(observationValue)));
    }
    
    @SuppressWarnings("unchecked")
    protected void checkForModifications(OmObservation observation) throws OwsExceptionReport {
        if (isSetAdditionalRequestParams() && contains(AdditionalRequestParams.crs)) {
            Object additionalRequestParam = getAdditionalRequestParams(AdditionalRequestParams.crs);
            int targetCRS = -1;
            if (additionalRequestParam instanceof Integer) {
                targetCRS = (Integer)additionalRequestParam;
            } else if (additionalRequestParam instanceof String) {
                targetCRS = Integer.parseInt((String)additionalRequestParam);
            }
            if (observation.isSetParameter()) {
                for (NamedValue<?> namedValue : observation.getParameter()) {
                    if (Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE.equals(namedValue.getName().getHref())) {
                        NamedValue<Geometry> spatialFilteringProfileParameter = (NamedValue<Geometry>) namedValue;
                        spatialFilteringProfileParameter.getValue().setValue(
                                GeometryHandler.getInstance().transform(spatialFilteringProfileParameter.getValue().getValue(),
                                        targetCRS));
                    }
                }
            }
        }
    }

    public void setObservationTemplate(OmObservation observationTemplate) {
        this.observationTemplate = observationTemplate;
    }

    public void setTemporalFilterCriterion(Criterion temporalFilterCriterion) {
        this.temporalFilterCriterion = temporalFilterCriterion;
    
    }

    private Time createPhenomenonTime(ObservationValue observationValue) {
        // create time element
        final DateTime phenStartTime = new DateTime(observationValue.getPhenomenonTimeStart(), DateTimeZone.UTC);
        DateTime phenEndTime;
        if (observationValue.getPhenomenonTimeEnd() != null) {
            phenEndTime = new DateTime(observationValue.getPhenomenonTimeEnd(), DateTimeZone.UTC);
        } else {
            phenEndTime = phenStartTime;
        }
        return createTime(phenStartTime, phenEndTime);
    }

    private Time createPhenomenonTime(ObservationValueTime minTime, ObservationValueTime maxTime) {
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

    private TimeInstant createResutlTime(ObservationValueTime maxTime) {
        DateTime dateTime = new DateTime(maxTime.getResultTime(), DateTimeZone.UTC);
        return new TimeInstant(dateTime);
    }

    private TimeInstant createResutlTime(Date maxTime) {
        DateTime dateTime = new DateTime(maxTime, DateTimeZone.UTC);
        return new TimeInstant(dateTime);
    }

    private Time createValidTime(ObservationValueTime minTime, ObservationValueTime maxTime) {
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

    private Value<?> getValueFrom(ObservationValue observationValue) throws OwsExceptionReport {
        Value<?> value = null;
        if (observationValue instanceof NumericValue) {
            value = new QuantityValue(((NumericValue) observationValue).getValue());
        } else if (observationValue instanceof BooleanValue) {
            value =
                    new org.n52.sos.ogc.om.values.BooleanValue(
                            Boolean.valueOf(((BooleanValue) observationValue).getValue()));
        } else if (observationValue instanceof CategoryValue) {
            value = new org.n52.sos.ogc.om.values.CategoryValue(((CategoryValue) observationValue).getValue());
        } else if (observationValue instanceof CountValue) {
            value = new org.n52.sos.ogc.om.values.CountValue(Integer.valueOf(((CountValue) observationValue).getValue()));
        } else if (observationValue instanceof TextValue) {
            value = new org.n52.sos.ogc.om.values.TextValue(((TextValue) observationValue).getValue().toString());
        } else if (observationValue instanceof GeometryValue) {
            value = new org.n52.sos.ogc.om.values.GeometryValue(((GeometryValue) observationValue).getValue());
        } else if (observationValue instanceof BlobValue) {
            value = new UnknownValue(((BlobValue) observationValue).getValue());
        } else if (observationValue instanceof SweDataArrayValue) {
            org.n52.sos.ogc.om.values.SweDataArrayValue sweDataArrayValue =
                    new org.n52.sos.ogc.om.values.SweDataArrayValue();
            final XmlObject xml = XmlHelper.parseXmlString(((SweDataArrayValue) observationValue).getValue());
            sweDataArrayValue.setValue((SweDataArray) CodingHelper.decodeXmlElement(xml));
            value = sweDataArrayValue;
        }
        if (value != null && observationValue.isSetUnit()) {
            value.setUnit(observationValue.getUnit().getUnit());
        }
        return value;
    }

}
