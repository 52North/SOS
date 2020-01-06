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

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.gwml.GWMLConstants;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.om.values.visitor.VoidValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.service.AbstractLoggingConfigurator.Level;
import org.n52.sos.util.GeometryHandler;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Represents the GroundWaterML 2.0 GW_GeologyLogCoverage
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class ProfileValue extends AbstractFeature implements Value<List<ProfileLevel>> {

    private static final long serialVersionUID = -6778711690384848654L;
    private QuantityValue fromLevel;
    private boolean queriedFromLevel = false;
    private QuantityValue toLevel;
    private boolean queriedToLevel = false;
    private List<ProfileLevel> values = Lists.newArrayList();
    
    @Override
    public ProfileValue setValue(List<ProfileLevel> value) {
        this.values.clear();
        this.values.addAll(value);
        return this;
    }
    
    public ProfileValue addValue(ProfileLevel value) {
        this.values.add(value);
        return this;
    }
    
    public ProfileValue addValues(List<ProfileLevel> value) {
        this.values.addAll(value);
        return this;
    }

    @Override
    public List<ProfileLevel> getValue() {
        return values;
    }

    @Override
    public void setUnit(String unit) {
        
    }

    @Override
    public String getUnit() {
        return null;
    }

    @Override
    public UoM getUnitObject() {
        return null;
    }

    @Override
    public boolean isSetUnit() {
        return false;
    }

    @Override
    public void setUnit(UoM unit) {
        // nothing to do
    }

    @Override
    public boolean isSetValue() {
        return !getValue().isEmpty();
    }

    /**
     * @return the fromLevel
     */
    public SweQuantity getFromLevel() {
        if (!isFromLevel() && !queriedFromLevel) {
            if (isSetValue()) {
                QuantityValue from = null;
                for (ProfileLevel profileLevel : values) {
                    if (profileLevel.isSetLevelStart()) {
                        if (from == null) {
                            from = profileLevel.getLevelStart();
                        } else if (profileLevel.getLevelStart().getValue() < from.getValue()) {
                            from = profileLevel.getLevelStart();
                        }
                    }
                }
                if (from != null) {
                    setFromLevel(from);
                    if (!isSetUnit() && from.isSetUnit()) {
                        setUnit(from.getUomObject());
                    }
                }
            }
            queriedFromLevel = true;
        }
        return fromLevel;
    }

    /**
     * @param fromLevel
     *            the fromLevel to set
     */
    public ProfileValue setFromLevel(QuantityValue fromLevel) {
        this.fromLevel = fromLevel;
        return this;
    }
    
    public boolean isSetFromLevel() {
        return getFromLevel() != null;
    }
    
    private boolean isFromLevel() {
        return this.fromLevel != null;
    }

    /**
     * @return the toLevel
     */
    public SweQuantity getToLevel() {
        if (!isToLevel() && !queriedToLevel) {
            if (isSetValue()) {
                QuantityValue to = null;
                for (ProfileLevel profileLevel : values) {
                    if (profileLevel.isSetLevelEnd()) {
                        if (to == null) {
                            to = profileLevel.getLevelEnd();
                        } else if (profileLevel.getLevelEnd().getValue() > to.getValue()) {
                            to = profileLevel.getLevelEnd();
                        }
                    }
                }
                if (to != null) {
                    setToLevel(to);
                    if (!isSetUnit() && to.isSetUnit()) {
                        setUnit(to.getUomObject());
                    }
                }
            }
            queriedToLevel = true;
            
        }
        return toLevel;
    }

    /**
     * @param toLevel
     *            the toLevel to set
     */
    public ProfileValue setToLevel(QuantityValue toLevel) {
        this.toLevel = toLevel;
        return this;
    }
    
    public boolean isSetToLevel() {
        return getToLevel() != null;
    }
    
    private boolean isToLevel() {
        return this.toLevel != null;
    }
    
    @Override
    public String getDefaultElementEncoding() {
        return GWMLConstants.NS_GWML_22;
    }

    @Override
    public <X> X accept(ValueVisitor<X> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidValueVisitor visitor) throws OwsExceptionReport {
        visitor.visit(this);
    }
    
    public SweDataRecord asDataRecord() {
        SweDataRecord dataRecord = new SweDataRecord();
        if (isSetIdentifier()) {
            dataRecord.setIdentifier(getIdentifier());
        }
        if (isSetName()) {
            dataRecord.setName(getName());        
        }
        if (isSetDescription()) {
            dataRecord.setDescription(getDescription());
        }
        int counter = 0;
        for (ProfileLevel level : getValue()) {
            dataRecord.addField(new SweField("level_" + counter++, level.asDataRecord()));
        }
        return dataRecord;
    }
    
    public Time getPhenomenonTime() {
        TimePeriod time = new TimePeriod();
        for (ProfileLevel profileLevel : values) {
            if (profileLevel.isSetPhenomenonTime()) {
                time.extendToContain(profileLevel.getPhenomenonTime()); 
            }
        }
        return time;
    }
    
    public boolean isSetGeometry() {
        return isSetValue() && getValue().iterator().next().isSetLocation();
    }
    
    public Geometry getGeometry() {
        if (isSetGeometry()) {
            TreeMap<Time, Coordinate> map = new TreeMap<>();
            int srid = -1;
            for (ProfileLevel level : getValue()) {
                if (level.isSetPhenomenonTime() && level.isSetLocation()) {
                    if (srid < 0) {
                        srid = level.getLocation().getSRID();
                    }
                    map.put(level.getPhenomenonTime(), level.getLocation().getCoordinate());
                }
            }
            if (!map.isEmpty()) {
                if (new HashSet<>(map.values()).size() == 1) {
                    return getValue().iterator().next().getLocation();
                } else {
                    return new GeometryFactory(new PrecisionModel(), srid).createLineString(map.values().toArray(new Coordinate[1]));
                }
            }
        }
        return null;
    }
    
}
