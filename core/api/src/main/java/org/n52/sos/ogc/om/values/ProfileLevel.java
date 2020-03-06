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
package org.n52.sos.ogc.om.values;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Represents the level of a profile
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class ProfileLevel implements Comparable<ProfileLevel> {

    private QuantityValue levelStart;
    private QuantityValue levelEnd;
    private List<Value<?>> value = Lists.newArrayList();
    private Geometry location;
    private Time phenomenonTime;

    /**
     * constructor
     */
    public ProfileLevel() {
        super();
    }
    
    /**
     * constructor
     * 
     * @param levelStart
     *            the levelStart value
     * @param toDepth
     *            the toDepth value
     * @param value
     *            the values
     */
    public ProfileLevel(QuantityValue levelStart, QuantityValue levelEnd, List<Value<?>> value) {
        super();
        this.levelStart = levelStart;
        this.levelEnd = levelEnd;
        this.value = value;
    }

    /**
     * @return the levelStart
     */
    public QuantityValue getLevelStart() {
        return levelStart;
    }

    /**
     * @param levelStart
     *            the levelStart to set
     */
    public ProfileLevel setLevelStart(QuantityValue levelStart) {
        this.levelStart = levelStart;
        return this;
    }
    
    public boolean isSetLevelStart() {
        return getLevelStart() != null;
    }

    /**
     * @return the levelEnd
     */
    public QuantityValue getLevelEnd() {
        return levelEnd;
    }

    /**
     * @param levelEnd
     *            the levelEnd to set
     */
    public ProfileLevel setLevelEnd(QuantityValue levelEnd) {
        this.levelEnd = levelEnd;
        return this;
    }
    
    public boolean isSetLevelEnd() {
        return getLevelEnd() != null;
    }

    /**
     * @return the value
     */
    public List<Value<?>> getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public ProfileLevel setValue(List<Value<?>> value) {
        this.value.clear();
        this.value.addAll(value);
        return this;
    }
    
    /**
     * @param value
     *            the value to set
     */
    public ProfileLevel addValue(Value<?> value) {
        this.value.add(value);
        return this;
    }
    
    public boolean isSetValue() {
        return getValue() != null;
    }

    /**
     * @return the simpleValue
     */
    public Value<?> getSimpleValue() {
        return value.iterator().next();
    }

    /**
     * @return the location
     */
    public Geometry getLocation() {
        return location;
    }

    /**
     * @param location
     *            the location to set
     */
    public ProfileLevel setLocation(Geometry location) {
        this.location = location;
        return this;
    }
    
    public boolean isSetLocation() {
        return getLocation() != null;
    }

    /**
     * @return the phenomenonTime
     */
    public Time getPhenomenonTime() {
        return phenomenonTime;
    }

    /**
     * @param phenomenonTime the phenomenonTime to set
     */
    public void setPhenomenonTime(Time phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }
    
    public boolean isSetPhenomenonTime() {
        return getPhenomenonTime() != null;
    }

    @Override
    public int compareTo(ProfileLevel o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (getLevelStart() == null ^ o.getLevelStart() == null) {
            return (getLevelStart() == null) ? -1 : 1;
        }
        if (getLevelStart() == null && o.getLevelStart() == null) {
            return 0;
        }
        return getLevelStart().compareTo(o.getLevelStart());
    }
    
    public SweDataRecord asDataRecord() {
        SweDataRecord dataRecord = new SweDataRecord();
        if (isSetLevelStart()) {
            dataRecord.addField(new SweField(getLevelStart().getName(), getLevelStart()));
        }
        if (isSetLevelStart()) {
            dataRecord.addField(new SweField(getLevelStart().getName(), getLevelStart()));
        }
        return valueAsDataRecord(dataRecord);
    }
    
    public SweDataRecord valueAsDataRecord() {
        return valueAsDataRecord(new SweDataRecord());
    }
    
    public SweDataRecord valueAsDataRecord(SweDataRecord dataRecord) {
        int counter = 1;
        for (Value<?> value : getValue()) {
            if (value instanceof SweAbstractDataComponent) {
                SweAbstractDataComponent adc = (SweAbstractDataComponent) value;
                String name = "";
                if (adc.isSetName()) {
                    name = adc.getName().getValue();
                } else if (adc.isSetDefinition()) {
                    name = adc.getDefinition();
                } else {
                    name = "component_" + counter++;
                }
                dataRecord.addField(new SweField(name, adc));
            }
        }
        if (counter == 1 && dataRecord.getFields().size() > 1
                && dataRecord.getFields().stream().map(f -> f.getName().getValue()).collect(Collectors.toSet())
                        .size() != dataRecord.getFields().size()) {
            for (SweField field : dataRecord.getFields()) {
                    field.getName().setValue(field.getName().getValue() + "_" + counter++);
            }
        }
        return dataRecord;
    }
    
    public <X> Collection<X> accept(ProfileLevelVisitor<X> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    public Collection<NamedValue<?>> getLevelStartEndAsParameter() {
        SortedSet<NamedValue<?>> parameter = new TreeSet<NamedValue<?>>();
        if (isSetLevelStart() && getLevelStart().isSetDefinition()) {
            parameter.add(new NamedValue(new ReferenceType(getLevelStart().getDefinition()), getLevelStart()));
        }
        if (isSetLevelEnd() && getLevelEnd().isSetDefinition()) {
            parameter.add(new NamedValue(new ReferenceType(getLevelEnd().getDefinition()), getLevelEnd()));
        }
        return parameter;
    }
}