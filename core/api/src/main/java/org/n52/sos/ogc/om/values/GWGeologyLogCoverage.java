/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.util.List;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gwml.GWMLConstants;
import org.n52.sos.ogc.om.values.LogValue;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.om.values.visitor.VoidValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.collect.Lists;

/**
 * Represents the GroundWaterML 2.0 GW_GeologyLogCoverage
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class GWGeologyLogCoverage extends AbstractFeature implements Value<List<LogValue>> {

    private static final long serialVersionUID = -6778711690384848654L;
    private List<LogValue> values = Lists.newArrayList();
    
    @Override
    public void setValue(List<LogValue> value) {
        this.values.clear();
        this.values.addAll(value);
    }
    
    public GWGeologyLogCoverage addValue(LogValue value) {
        this.values.add(value);
        return this;
    }

    @Override
    public List<LogValue> getValue() {
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
    public boolean isSetValue() {
        return !getValue().isEmpty();
    }

    @Override
    public boolean isSetUnit() {
        return false;
    }
    
    @Override
    public String getDefaultElementEncoding() {
        return GWMLConstants.NS_GWML_21;
    }

    @Override
    public <X> X accept(ValueVisitor<X> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidValueVisitor visitor) throws OwsExceptionReport {
        visitor.visit(this);
    }
}
