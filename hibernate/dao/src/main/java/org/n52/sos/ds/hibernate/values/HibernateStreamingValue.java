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
