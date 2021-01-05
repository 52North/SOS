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
package org.n52.sos.ds.hibernate.util;

import java.math.BigDecimal;

import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.shetland.ogc.om.values.BooleanValue;
import org.n52.shetland.ogc.om.values.CategoryValue;
import org.n52.shetland.ogc.om.values.ComplexValue;
import org.n52.shetland.ogc.om.values.CountValue;
import org.n52.shetland.ogc.om.values.CvDiscretePointCoverage;
import org.n52.shetland.ogc.om.values.GeometryValue;
import org.n52.shetland.ogc.om.values.HrefAttributeValue;
import org.n52.shetland.ogc.om.values.MultiPointCoverage;
import org.n52.shetland.ogc.om.values.NilTemplateValue;
import org.n52.shetland.ogc.om.values.ProfileValue;
import org.n52.shetland.ogc.om.values.QuantityRangeValue;
import org.n52.shetland.ogc.om.values.QuantityValue;
import org.n52.shetland.ogc.om.values.RectifiedGridCoverage;
import org.n52.shetland.ogc.om.values.ReferenceValue;
import org.n52.shetland.ogc.om.values.SweDataArrayValue;
import org.n52.shetland.ogc.om.values.TLVTValue;
import org.n52.shetland.ogc.om.values.TVPValue;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.om.values.TimeRangeValue;
import org.n52.shetland.ogc.om.values.TimeValue;
import org.n52.shetland.ogc.om.values.TrajectoryValue;
import org.n52.shetland.ogc.om.values.UnknownValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.om.values.XmlValue;
import org.n52.shetland.ogc.om.values.visitor.ValueVisitor;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

public abstract class AbstractParameterFactory<T extends ParameterEntity<?>>
        implements ValueVisitor<T, OwsExceptionReport> {

    protected AbstractParameterFactory() {
    }

    public abstract ParameterEntity<Boolean> truth() throws OwsExceptionReport;

    public abstract ParameterEntity<String> category() throws OwsExceptionReport;

    public abstract ParameterEntity<Integer> count() throws OwsExceptionReport;

    public abstract ParameterEntity<BigDecimal> quantity() throws OwsExceptionReport;

    public abstract ParameterEntity<String> text() throws OwsExceptionReport;

    // public Class<? extends XmlParameter> xmlClass() {
    // return XmlParameter.class;
    // }
    //
    // public XmlParameter xml() throws OwsExceptionReport {
    // return instantiate(xmlClass());
    // }

    @Override
    public T visit(BooleanValue value) throws OwsExceptionReport {
        return (T) truth();
    }

    @Override
    public T visit(CategoryValue value) throws OwsExceptionReport {
        return (T) category();
    }

    @Override
    public T visit(ComplexValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(CountValue value) throws OwsExceptionReport {
        return (T) count();
    }

    @Override
    public T visit(GeometryValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(HrefAttributeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(NilTemplateValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(QuantityValue value) throws OwsExceptionReport {
        return (T) quantity();
    }

    @Override
    public T visit(ReferenceValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(SweDataArrayValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(TVPValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(TextValue value) throws OwsExceptionReport {
        return (T) text();
    }

    @Override
    public T visit(UnknownValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(TLVTValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(MultiPointCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(RectifiedGridCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(ProfileValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(TrajectoryValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(TimeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(TimeRangeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(XmlValue<?> value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public T visit(QuantityRangeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    private OwsExceptionReport notSupported(Value<?> value) throws OwsExceptionReport {
        throw new NoApplicableCodeException().withMessage("Unsupported om:parameter value %s", value.getClass()
                .getCanonicalName());
    }

    // public static AbstractParameterFactory getInstance() {
    // return Holder.INSTANCE;
    // }
    //
    // private static final class Holder {
    // private static final AbstractParameterFactory INSTANCE = new
    // AbstractParameterFactory();
    //
    // private Holder() {
    // }
    // }

}
