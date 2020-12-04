/*
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
package org.n52.sos.ds.parameter;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.n52.series.db.beans.HibernateRelations.HasName;
import org.n52.series.db.beans.HibernateRelations.HasUnit;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.parameter.BooleanParameterEntity;
import org.n52.series.db.beans.parameter.CategoryParameterEntity;
import org.n52.series.db.beans.parameter.ComplexParameterEntity;
import org.n52.series.db.beans.parameter.CountParameterEntity;
import org.n52.series.db.beans.parameter.JsonParameterEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.series.db.beans.parameter.QuantityParameterEntity;
import org.n52.series.db.beans.parameter.TemporalParameterEntity;
import org.n52.series.db.beans.parameter.TextParameterEntity;
import org.n52.series.db.beans.parameter.TimeRange;
import org.n52.series.db.beans.parameter.ValuedParameter;
import org.n52.series.db.beans.parameter.XmlParameterEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.TimeRangeValue;
import org.n52.shetland.ogc.om.values.TimeValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.RangeValue;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweAbstractDataRecord;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.util.DateTimeHelper;

public class ParameterVisitor {

    @SuppressWarnings("rawtypes")
    public NamedValue visit(QuantityParameterEntity p) throws OwsExceptionReport {
        NamedValue<BigDecimal> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new QuantityValue(p.getValue()));
        addUnit(p, namedValue.getValue());
        addDescription(namedValue.getValue(), p);
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(BooleanParameterEntity p) throws OwsExceptionReport {
        NamedValue<Boolean> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new BooleanValue(p.getValue()));
        addDescription(namedValue.getValue(), p);
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(CategoryParameterEntity p) throws OwsExceptionReport {
        NamedValue<String> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new CategoryValue(p.getValue()));
        addUnit(p, namedValue.getValue());
        addDescription(namedValue.getValue(), p);
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(CountParameterEntity p) throws OwsExceptionReport {
        NamedValue<Integer> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new CountValue(p.getValue()));
        addDescription(namedValue.getValue(), p);
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(TextParameterEntity p) throws OwsExceptionReport {
        NamedValue<String> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new TextValue(p.getValue()));
        addDescription(namedValue.getValue(), p);
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(XmlParameterEntity p) throws OwsExceptionReport {
        NamedValue<String> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new TextValue(p.getValue()));
        addDescription(namedValue.getValue(), p);
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(JsonParameterEntity p) throws OwsExceptionReport {
        NamedValue<String> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new TextValue(p.getValue()));
        addDescription(namedValue.getValue(), p);
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(ComplexParameterEntity p) throws OwsExceptionReport {
        NamedValue<SweAbstractDataRecord> namedValue = new NamedValue<>();
        addName(namedValue, p);
        ParameterSweAbstractDataComponentCreator visitor = new ParameterSweAbstractDataComponentCreator();
        SweDataRecord record = visitor.visit(p);
        namedValue.setValue(new ComplexValue(record));
        return namedValue;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public NamedValue visit(TemporalParameterEntity p) throws OwsExceptionReport {
        TimeRange value = p.getValue();
        NamedValue namedValue = new NamedValue<>();
        if (value.isPeriod()) {
            namedValue
                    .setValue(new TimeRangeValue(new RangeValue<DateTime>(DateTimeHelper.makeDateTime(value.getFrom()),
                            DateTimeHelper.makeDateTime(value.getTo()))));

        } else {
            namedValue = new NamedValue<>();
            namedValue.setValue(new TimeValue(DateTimeHelper.makeDateTime(value.getInstant())));
        }
        addName(namedValue, p);
        addDescription(namedValue.getValue(), p);
        return namedValue;
    }

    public NamedValue<?> visit(ParameterEntity parameter) throws OwsExceptionReport {
        if (parameter instanceof QuantityParameterEntity) {
            return visit((QuantityParameterEntity) parameter);
        } else if (parameter instanceof CountParameterEntity) {
            return visit((CountParameterEntity) parameter);
        } else if (parameter instanceof BooleanParameterEntity) {
            return visit((BooleanParameterEntity) parameter);
        } else if (parameter instanceof CategoryParameterEntity) {
            return visit((CategoryParameterEntity) parameter);
        } else if (parameter instanceof XmlParameterEntity) {
            return visit((XmlParameterEntity) parameter);
        } else if (parameter instanceof JsonParameterEntity) {
            return visit((JsonParameterEntity) parameter);
        } else if (parameter instanceof ComplexParameterEntity) {
            return visit((ComplexParameterEntity) parameter);
        } else if (parameter instanceof TemporalParameterEntity) {
            return visit((TemporalParameterEntity) parameter);
        }
        NamedValue<String> namedValue = new NamedValue<>();
        addName(namedValue, parameter);
        TextValue value = new TextValue(parameter.getValue().toString());
        addDescription(value, parameter);
        namedValue.setValue(value);
        return namedValue;
    }

    private void addDescription(Value<?> value, ValuedParameter p) {
        if (p.isSetDescription()
                && value instanceof SweAbstractDataComponent) {
            ((SweAbstractDataComponent) value).setDescription(((ParameterEntity) p).getDescription());
        }
    }

    protected void addUnit(HasUnit vp, Value<?> v) {
        if (!v.isSetUnit() && vp.isSetUnit()) {
            UnitEntity unit = vp.getUnit();
            UoM uom = new UoM(unit.getUnit());
            if (unit.isSetName()) {
                uom.setName(unit.getName());
            }
            if (unit.isSetLink()) {
                uom.setLink(unit.getLink());
            }
            v.setUnit(uom);
        }
    }

    protected NamedValue<?> addName(NamedValue<?> namedValue, HasName p) {
        ReferenceType referenceType = new ReferenceType(p.getName());
        namedValue.setName(referenceType);
        return namedValue;
    }

}
