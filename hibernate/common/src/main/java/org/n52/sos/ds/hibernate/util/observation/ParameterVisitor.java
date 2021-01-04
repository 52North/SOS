/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.observation;

import java.math.BigDecimal;

import org.n52.series.db.beans.HibernateRelations.HasUnit;
import org.n52.series.db.beans.UnitEntity;
import org.n52.series.db.beans.parameter.ParameterBooleanEntity;
import org.n52.series.db.beans.parameter.ParameterCategoryEntity;
import org.n52.series.db.beans.parameter.ParameterCountEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.series.db.beans.parameter.ParameterQuantityEntity;
import org.n52.series.db.beans.parameter.ParameterTextEntity;
import org.n52.shetland.ogc.UoM;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

public class ParameterVisitor {

    @SuppressWarnings("rawtypes")
    public NamedValue visit(ParameterQuantityEntity p) throws OwsExceptionReport {
        NamedValue<BigDecimal> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new QuantityValue(p.getValue()));
        addUnit(p, namedValue.getValue());
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(ParameterBooleanEntity p) throws OwsExceptionReport {
        NamedValue<Boolean> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new BooleanValue(p.getValue()));
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(ParameterCategoryEntity p) throws OwsExceptionReport {
        NamedValue<String> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new CategoryValue(p.getValue()));
        addUnit(p, namedValue.getValue());
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(ParameterCountEntity p) throws OwsExceptionReport {
        NamedValue<Integer> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new CountValue(p.getValue()));
        return namedValue;
    }

    @SuppressWarnings("rawtypes")
    public NamedValue visit(ParameterTextEntity p) throws OwsExceptionReport {
        NamedValue<String> namedValue = new NamedValue<>();
        addName(namedValue, p);
        namedValue.setValue(new TextValue(p.getValue()));
        return namedValue;
    }

    // @SuppressWarnings("rawtypes")
    // public NamedValue visit(Para p) throws OwsExceptionReport {
    // NamedValue<XmlObject> namedValue = new NamedValue<>();
    // addName(namedValue, p);
    // namedValue.setValue(new XmlValue(p.getValueAsXml()));
    // return namedValue;
    // }

    public NamedValue<?> visit(ParameterEntity parameter) throws OwsExceptionReport {
        if (parameter instanceof ParameterQuantityEntity) {
            return visit((ParameterQuantityEntity) parameter);
        } else if (parameter instanceof ParameterCountEntity) {
            return visit((ParameterCountEntity) parameter);
        } else if (parameter instanceof ParameterBooleanEntity) {
            return visit((ParameterBooleanEntity) parameter);
        } else if (parameter instanceof ParameterCategoryEntity) {
            return visit((ParameterCategoryEntity) parameter);
        }
        NamedValue<String> namedValue = new NamedValue<>();
        addName(namedValue, parameter);
        namedValue.setValue(new TextValue(parameter.getValue().toString()));
        return namedValue;
    }

    protected void addUnit(ParameterEntity<?> vp, Value<?> v) {
        if (!v.isSetUnit() && vp instanceof HasUnit && ((HasUnit) vp).isSetUnit()) {
            UnitEntity unit = ((HasUnit) vp).getUnit();
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

    protected NamedValue<?> addName(NamedValue<?> namedValue, ParameterEntity<?> p) {
        ReferenceType referenceType = new ReferenceType(p.getName());
        namedValue.setName(referenceType);
        return namedValue;
    }

}
