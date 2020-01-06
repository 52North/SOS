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
import java.util.SortedMap;

import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.om.values.visitor.VoidValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.JavaHelper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Class that represents a rectified grid coverage
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class RectifiedGridCoverage implements DiscreteCoverage<SortedMap<QuantityValued<?, ?>, Value<?>>> {

    private static final long serialVersionUID = 5209844268871191549L;

    private String gmlId;

    private SortedMap<QuantityValued<?, ?>, Value<?>> value = Maps.newTreeMap();
    
    private String rangeParameters;

    private UoM unit;

    public RectifiedGridCoverage(String gmlId) {
        if (Strings.isNullOrEmpty(gmlId)) {
            gmlId = JavaHelper.generateID(toString());
        } else if (!gmlId.startsWith("rgc_")) {
            gmlId = "rgc_" + gmlId;
        }
        this.gmlId = gmlId;
    }

    public String getGmlId() {
        return gmlId;
    }

    @Override
    public RectifiedGridCoverage setValue(SortedMap<QuantityValued<?, ?>, Value<?>> value) {
        this.value.clear();
        addValue(value);
        return this;
    }
    
    public void addValue(QuantityValued<?, ?> key, Value<?> value) {
        this.value.put(key, value);
    }

    public void addValue(Double key, Value<?> value) {
        this.value.put(new QuantityValue(key), value);
    }
    
    public void addValue(Double from, Double to, Value<?> value) {
        this.value.put(new QuantityRangeValue(from, to), value);
    }

    public void addValue(SortedMap<QuantityValued<?, ?>, Value<?>> value) {
        this.value.putAll(value);
    }

    @Override
    public SortedMap<QuantityValued<?, ?>, Value<?>> getValue() {
        return value;
    }

    @Override
    public void setUnit(String unit) {
        this.unit = new UoM(unit);
    }

    @Override
    public String getUnit() {
        if (isSetUnit()) {
            return unit.getUom();
        }
        return null;
    }

    @Override
    public UoM getUnitObject() {
        return this.unit;
    }

    @Override
    public void setUnit(UoM unit) {
        this.unit = unit;
    }

    @Override
    public boolean isSetUnit() {
        return getUnitObject() != null && !getUnitObject().isEmpty();
    }

    @Override
    public boolean isSetValue() {
        return CollectionHelper.isNotEmpty(value);
    }

    @Override
    public <X> X accept(ValueVisitor<X> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidValueVisitor visitor) throws OwsExceptionReport {
        visitor.visit(this);
    }

    /**
     * Get the domainSet
     * 
     * @return The domainSet as {@link Double} {@link List}
     */
    public List<QuantityValued<?, ?>> getDomainSet() {
        return Lists.newArrayList(getValue().keySet());
    }

    public Collection<Value<?>> getRangeSet() {
        return getValue().values();
    }

    @Override
    public String getRangeParameters() {
        return rangeParameters;
    }

    @Override
    public void setRangeParameters(String rangeParameters) {
        this.rangeParameters = rangeParameters;
    }
    
    @Override
    public boolean isSetRangeParameters() {
        return !Strings.isNullOrEmpty(getRangeParameters());
    }

}
