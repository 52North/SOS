/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import java.util.Set;

import org.n52.janmayen.NcName;
import org.n52.series.db.beans.HibernateRelations.HasUnit;
import org.n52.series.db.beans.parameter.BooleanParameterEntity;
import org.n52.series.db.beans.parameter.CategoryParameterEntity;
import org.n52.series.db.beans.parameter.ComplexParameterEntity;
import org.n52.series.db.beans.parameter.CountParameterEntity;
import org.n52.series.db.beans.parameter.JsonParameterEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.series.db.beans.parameter.QuantityParameterEntity;
import org.n52.series.db.beans.parameter.TextParameterEntity;
import org.n52.series.db.beans.parameter.ValuedParameter;
import org.n52.series.db.beans.parameter.XmlParameterEntity;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweAbstractDataComponent;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.shetland.ogc.swe.SweField;
import org.n52.shetland.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.shetland.ogc.swe.simpleType.SweBoolean;
import org.n52.shetland.ogc.swe.simpleType.SweCategory;
import org.n52.shetland.ogc.swe.simpleType.SweCount;
import org.n52.shetland.ogc.swe.simpleType.SweQuantity;
import org.n52.shetland.ogc.swe.simpleType.SweText;

public class ParameterSweAbstractDataComponentCreator {

    public SweAbstractDataComponent visit(ParameterEntity parameter) throws OwsExceptionReport {
        if (parameter instanceof QuantityParameterEntity) {
            return visit((QuantityParameterEntity) parameter);
        } else if (parameter instanceof CountParameterEntity) {
            return visit((CountParameterEntity) parameter);
        } else if (parameter instanceof BooleanParameterEntity) {
            return visit((BooleanParameterEntity) parameter);
        } else if (parameter instanceof CategoryParameterEntity) {
            return visit((CategoryParameterEntity) parameter);
        } else if (parameter instanceof TextParameterEntity) {
            return visit((TextParameterEntity) parameter);
        } else if (parameter instanceof XmlParameterEntity) {
            return visit((XmlParameterEntity) parameter);
        } else if (parameter instanceof JsonParameterEntity) {
            return visit((JsonParameterEntity) parameter);
        } else if (parameter instanceof ComplexParameterEntity) {
            return visit((ComplexParameterEntity) parameter);
        }
        return null;
    }

    public SweQuantity visit(QuantityParameterEntity p) throws CodedException {
        SweQuantity component = new SweQuantity();
        component.setValue(p.getValue());
        return setCommonValues(component, p);
    }

    public SweBoolean visit(BooleanParameterEntity p) throws CodedException {
        SweBoolean component = new SweBoolean();
        component.setValue(p.getValue());
        return setCommonValues(component, p);
    }

    public SweCategory visit(CategoryParameterEntity p) throws CodedException {
        SweCategory component = new SweCategory();
        component.setValue(p.getValue());
        return setCommonValues(component, p);
    }

    public SweDataRecord visit(ComplexParameterEntity<Set<?>> p) throws OwsExceptionReport {
        SweDataRecord record = new SweDataRecord();
        for (Object o : p.getValue()) {
            if (o instanceof ParameterEntity) {
                ParameterEntity param = (ParameterEntity) o;
                String fieldName = getFieldName(param);
                record.addField(new SweField(fieldName, this.visit(param)));
                if (p.isSetDescription()) {
                    record.setDescription(p.getDescription());
                }
            }
        }
        return setCommonValues(record, p);
    }

    public SweCount visit(CountParameterEntity p) throws CodedException {
        SweCount component = new SweCount();
        component.setValue(p.getValue());
        return setCommonValues(component, p);
    }

    public SweText visit(TextParameterEntity p) throws OwsExceptionReport {
        SweText component = new SweText();
        component.setValue(p.getValue());
        return setCommonValues(component, p);
    }

    public SweText visit(XmlParameterEntity p) throws OwsExceptionReport {
        SweText component = new SweText();
        component.setValue(p.getValue());
        return setCommonValues(component, p);
    }

    public SweText visit(JsonParameterEntity p) throws OwsExceptionReport {
        SweText component = new SweText();
        component.setValue(p.getValue());
        return setCommonValues(component, p);
    }

    protected String getFieldName(ParameterEntity<?> p) {
        return NcName.makeValid(p.getName());
    }

    protected <T extends SweAbstractDataComponent> T setCommonValues(T component, ValuedParameter<?> parameter)
            throws CodedException {
        if (parameter != null) {
            if (parameter instanceof HasUnit && ((HasUnit) parameter).isSetUnit()
                    && component instanceof SweAbstractUomType) {
                SweAbstractUomType<?> uomType = (SweAbstractUomType<?>) component;
                uomType.setUom(((HasUnit) parameter).getUnit()
                        .getUnit());
            }
        }
        return component;
    }

    protected OwsExceptionReport notSupported(ParameterEntity p) {
        return new NoApplicableCodeException()
                .withMessage("Complex observation fields of type %s" + " are currently not supported", p.getValue());
    }

}
