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
package org.n52.sos.ds.hibernate.entities.values;

import java.util.Date;

import org.apache.xmlbeans.XmlObject;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.AbstractObservationTime;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCodespace;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDescription;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasIdentifier;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUnit;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.interfaces.BlobValue;
import org.n52.sos.ds.hibernate.entities.interfaces.BooleanValue;
import org.n52.sos.ds.hibernate.entities.interfaces.CategoryValue;
import org.n52.sos.ds.hibernate.entities.interfaces.CountValue;
import org.n52.sos.ds.hibernate.entities.interfaces.GeometryValue;
import org.n52.sos.ds.hibernate.entities.interfaces.NumericValue;
import org.n52.sos.ds.hibernate.entities.interfaces.SweDataArrayValue;
import org.n52.sos.ds.hibernate.entities.interfaces.TextValue;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.OMHelper;
import org.n52.sos.util.XmlHelper;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract class for values
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public abstract class AbstractValue extends AbstractObservationTime implements HasIdentifier, HasCodespace, HasUnit,
        HasDescription {

    private static final long serialVersionUID = -3803490157787902881L;

    private Unit unit;


    @Override
    public Unit getUnit() {
        return unit;
    }

    @Override
    public void setUnit(final Unit unit) {
        this.unit = unit;
    }

    @Override
    public boolean isSetUnit() {
        return getUnit() != null && getUnit().isSetUnit();
    }
    
    public abstract OmObservation mergeValueToObservation(OmObservation observation, String responseFormat) throws OwsExceptionReport;
    
    protected abstract void addValueSpecificDataToObservation(OmObservation observation, String responseFormat) throws OwsExceptionReport;
    
    public abstract void addValueSpecificDataToObservation(OmObservation observation, Session session, SwesExtensions swesExtensions) throws OwsExceptionReport;
    
    protected abstract void addObservationValueToObservation(OmObservation observation, Value<?> value, String responseFormat) throws OwsExceptionReport;
    
    public abstract String getDiscriminator();
    
    /**
     * Create a {@link TimeValuePair} from {@link AbstractValue}
     * 
     * @param abstractValue
     *            {@link AbstractValue} to create {@link TimeValuePair} from
     * @return resulting {@link TimeValuePair}
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    public TimeValuePair createTimeValuePairFrom() throws OwsExceptionReport {
        return new TimeValuePair(createPhenomenonTime(), getValueFrom(this));
    }
    
    /**
     * Add {@link AbstractValue} data to {@link OmObservation}
     * 
     * @param observation
     *            {@link OmObservation} to add data
     * @param responseFormat 
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    public OmObservation addValuesToObservation(OmObservation observation, String responseFormat)
            throws OwsExceptionReport {
        observation.setObservationID(Long.toString(getObservationId()));
        if (isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(getIdentifier());
            if (isSetCodespace()) {
                identifier.setCodeSpace(getCodespace().getCodespace());
            }
            observation.setIdentifier(identifier);
        }
        if (isSetDescription()) {
            observation.setDescription(getDescription());
        }
        Value<?> value = getValueFrom(this);
        if (!observation.getObservationConstellation().isSetObservationType()) {
            observation.getObservationConstellation().setObservationType(OMHelper.getObservationTypeFor(value));
        }
        observation.setResultTime(createResutlTime(getResultTime()));
        observation.setValidTime(createValidTime(getValidTimeStart(), getValidTimeEnd()));
        addValueSpecificDataToObservation(observation, responseFormat);
        addObservationValueToObservation(observation, value, responseFormat);
        return observation;
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
                .switchCoordinateAxisFromToDatasourceIfNeeded(geometry)));
        return namedValue;
    }

}
