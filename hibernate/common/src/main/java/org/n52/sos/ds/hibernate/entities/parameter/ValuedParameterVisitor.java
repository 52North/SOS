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
package org.n52.sos.ds.hibernate.entities.parameter;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUnit;
import org.n52.sos.ds.hibernate.entities.Unit;
import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.om.values.XmlValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public class ValuedParameterVisitor implements ParameterVisitor<NamedValue<?>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public NamedValue visit(QuantityValuedParameter p) throws OwsExceptionReport {
        NamedValue<Double> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new QuantityValue(p.getValue()));
        addUnit(p, namedValue.getValue());
        return namedValue;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public NamedValue visit(BooleanValuedParameter p) throws OwsExceptionReport {
        NamedValue<Boolean> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new BooleanValue(p.getValue()));
        return namedValue;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public NamedValue visit(CategoryValuedParameter p) throws OwsExceptionReport {
        NamedValue<String> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new CategoryValue(p.getValue()));
        addUnit(p, namedValue.getValue());
        return namedValue;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public NamedValue visit(CountValuedParameter p) throws OwsExceptionReport {
        NamedValue<Integer> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new CountValue(p.getValue()));
        return namedValue;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public NamedValue visit(TextValuedParameter p) throws OwsExceptionReport {
        NamedValue<String> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new TextValue(p.getValue()));
        return namedValue;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public NamedValue visit(XmlValuedParameter p) throws OwsExceptionReport {
        NamedValue<XmlObject> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new XmlValue(p.getValueAsXml()));
        return namedValue;
    }

    protected void addUnit(ValuedParameter<?> vp, Value<?> v) {
        if (!v.isSetUnit() && vp instanceof HasUnit && ((HasUnit)vp).isSetUnit()) {
            Unit unit = ((HasUnit)vp).getUnit();
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

    protected NamedValue<?> addName(NamedValue<?> namedValue, ValuedParameter<?> p) {
        ReferenceType referenceType = new ReferenceType(p.getName());
        namedValue.setName(referenceType);
        return namedValue;
    }



}
