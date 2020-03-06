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
package org.n52.sos.ds.hibernate.entities.observation.legacy;

import java.util.Date;

import org.hibernate.Session;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.observation.AbstractTemporalReferencedObservation;
import org.n52.sos.ds.hibernate.entities.parameter.observation.ParameterAdder;
import org.n52.sos.ds.hibernate.util.HibernateGeometryCreator;
import org.n52.sos.ds.hibernate.util.observation.ObservationValueCreator;
import org.n52.sos.ds.hibernate.util.observation.RelatedObservationAdder;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.OMHelper;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Abstract implementation of {@link ValuedLegacyObservation}.
 *
 * @param <T> the value type
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @author Christian Autermann
 * @since 4.1.0
 */
public abstract class AbstractValuedLegacyObservation<T>
        extends AbstractTemporalReferencedObservation
        implements ValuedLegacyObservation<T> {

    private static final long serialVersionUID = -3803490157787902881L;
    private Unit unit;
    private Procedure procedure;
    private ObservableProperty observableProperty;
    private AbstractFeatureOfInterest featureOfInterest;
    private Offering offering;
    private Double verticalTo;
    private String verticalFromName;
    private String verticalToName;
    private Double verticalFrom;

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

    @Override
    public Procedure getProcedure() {
        return procedure;
    }

    @Override
    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public ObservableProperty getObservableProperty() {
        return observableProperty;
    }

    @Override
    public void setObservableProperty(ObservableProperty observableProperty) {
        this.observableProperty = observableProperty;
    }

    @Override
    public AbstractFeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    @Override
    public void setFeatureOfInterest(AbstractFeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }
    
    @Override
    public Offering getOffering() {
        return offering;
    }
    
    @Override
    public void setOffering(Offering offering) {
       this.offering = offering;
    }

    @Override
    public boolean isSetOffering() {
        return getOffering() != null;
    }
    
    /**
     * Create a {@link TimeValuePair} from {@link AbstractValue}
     * 
     * @return resulting {@link TimeValuePair}
     * @throws OwsExceptionReport
     *             If an error occurs when getting the value
     */
    public TimeValuePair createTimeValuePairFrom() throws OwsExceptionReport {
        return new TimeValuePair(createPhenomenonTime(), accept(new ObservationValueCreator()));
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
        if (!observation.isSetIdentifier() && isSetIdentifier()) {
            CodeWithAuthority identifier = new CodeWithAuthority(getIdentifier());
            if (isSetCodespace()) {
                identifier.setCodeSpace(getCodespace().getCodespace());
            }
            observation.setIdentifier(identifier);
        }
        if (!observation.isSetName() && isSetDescription()) {
            CodeType name = new CodeType(getName());
            if (isSetCodespace()) {
                name.setCodeSpace(getCodespace().getCodespace());
            }
            observation.setName(name);
        }
        if (!observation.isSetDescription() && isSetDescription()) {
            observation.setDescription(getDescription());
        }
        Value<?> value = accept(new ObservationValueCreator());
        if (!value.isSetUnit()
                && observation.getObservationConstellation().getObservableProperty() instanceof OmObservableProperty
                && ((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                        .isSetUnit()) {
            value.setUnit( ((OmObservableProperty) observation.getObservationConstellation().getObservableProperty())
                        .getUnit());
        }
        if (!observation.getObservationConstellation().isSetObservationType()) {
            observation.getObservationConstellation().setObservationType(OMHelper.getObservationTypeFor(value));
        }
        observation.setResultTime(createResutlTime(getResultTime()));
        observation.setValidTime(createValidTime(getValidTimeStart(), getValidTimeEnd()));
        if (hasSamplingGeometry()) {
            observation.addParameter(createSpatialFilteringProfileParameter(getSamplingGeometry()));
        } else if (isSetLongLat()) {
            observation.addParameter(createSpatialFilteringProfileParameter(new HibernateGeometryCreator().createGeometry(this)));
        }
        addRelatedObservation(observation);
        addParameter(observation);
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
    
    @Override
    public OmObservation mergeValueToObservation(OmObservation observation, String responseFormat) throws OwsExceptionReport {
        if (!observation.isSetValue()) {
            addValuesToObservation(observation, responseFormat);
        } else {
            // TODO
            if (!OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(observation.getObservationConstellation()
                    .getObservationType())) {
                observation.getObservationConstellation().setObservationType(
                        OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
            }
            observation.mergeWithObservation(getSingleObservationValue(accept(new ObservationValueCreator())));
        }
        return observation;
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private SingleObservationValue getSingleObservationValue(Value<?> value) throws OwsExceptionReport {
        return new SingleObservationValue(createPhenomenonTime(), value);
    }
    
    private void addRelatedObservation(OmObservation observation) throws OwsExceptionReport {
        new RelatedObservationAdder(observation, this).add();
    }
    
    private void addParameter(OmObservation observation) throws OwsExceptionReport {
        new ParameterAdder(observation, this).add();
    }

    @Override
    public void addValueSpecificDataToObservation(OmObservation observation, String responseFormat) throws OwsExceptionReport {
        // nothing to do
    }

    @Override
    public void addValueSpecificDataToObservation(OmObservation observation, Session session, SwesExtensions swesExtensions)
            throws OwsExceptionReport {
        // nothing to do
    }

    @Override
    public void addObservationValueToObservation(OmObservation observation, Value<?> value, String responseFormat)
            throws OwsExceptionReport {
        observation.setValue(getSingleObservationValue(value));
    }
    
    @Override
    public String getDiscriminator() {
        return null;
    }

    public Double getVerticalFrom() {
        return verticalFrom;
    }
    
    public void setVerticalFrom(Double verticalFrom) {
        this.verticalFrom = verticalFrom;
    }

    public boolean hasVerticalFrom() {
        return getVerticalFrom() != null;
    }

    public void setVerticalTo(Double verticalTo) {
        this.verticalTo = verticalTo;
    }
    
    public boolean hasVerticalTo() {
        return getVerticalTo() != null;
    }
    
    public Double getVerticalTo() {
        return verticalTo;
    }
    
    public String getVerticalFromName() {
        return verticalFromName;
    }
    
    public void setVerticalFromName(String verticalFromName) {
        this.verticalFromName = verticalFromName;
    }
    
    public boolean hasVerticalFromName() {
        return getVerticalFromName() != null && !getVerticalFromName().isEmpty();
    }
    
    public String getVerticalToName() {
        return verticalToName;
    }
    
    public void setVerticalToName(String verticalToName) {
        this.verticalToName = verticalToName;
    }
    
    public boolean hasVerticalToName() {
        return getVerticalToName() != null && !getVerticalToName().isEmpty();
    }
    
}
