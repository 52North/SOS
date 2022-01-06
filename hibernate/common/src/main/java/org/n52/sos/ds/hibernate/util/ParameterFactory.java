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
package org.n52.sos.ds.hibernate.util;

import org.n52.series.db.beans.parameter.ParameterBooleanEntity;
import org.n52.series.db.beans.parameter.ParameterCategoryEntity;
import org.n52.series.db.beans.parameter.ParameterCountEntity;
import org.n52.series.db.beans.parameter.ParameterEntity;
import org.n52.series.db.beans.parameter.ParameterQuantityEntity;
import org.n52.series.db.beans.parameter.ParameterTextEntity;
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
import org.n52.shetland.ogc.om.values.UnknownValue;
import org.n52.shetland.ogc.om.values.Value;
import org.n52.shetland.ogc.om.values.XmlValue;
import org.n52.shetland.ogc.om.values.visitor.ValueVisitor;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

public class ParameterFactory implements ValueVisitor<ParameterEntity<?>, OwsExceptionReport> {

    protected ParameterFactory() {
    }

    public Class<? extends ParameterBooleanEntity> truthClass() {
        return ParameterBooleanEntity.class;
    }

    public ParameterBooleanEntity truth() throws OwsExceptionReport {
        return instantiate(truthClass());
    }

    public Class<? extends ParameterCategoryEntity> categoryClass() {
        return ParameterCategoryEntity.class;
    }

    public ParameterCategoryEntity category() throws OwsExceptionReport {
        return instantiate(categoryClass());
    }

    public Class<? extends ParameterCountEntity> countClass() {
        return ParameterCountEntity.class;
    }

    public ParameterCountEntity count() throws OwsExceptionReport {
        return instantiate(countClass());
    }

    public Class<? extends ParameterQuantityEntity> quantityClass() {
        return ParameterQuantityEntity.class;
    }

    public ParameterQuantityEntity quantity() throws OwsExceptionReport {
        return instantiate(quantityClass());
    }

    public Class<? extends ParameterTextEntity> textClass() {
        return ParameterTextEntity.class;
    }

    public ParameterTextEntity text() throws OwsExceptionReport {
        return instantiate(textClass());
    }

    // public Class<? extends XmlParameter> xmlClass() {
    // return XmlParameter.class;
    // }
    //
    // public XmlParameter xml() throws OwsExceptionReport {
    // return instantiate(xmlClass());
    // }

    private <T extends ParameterEntity<?>> T instantiate(Class<T> c) throws OwsExceptionReport {

        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new NoApplicableCodeException().causedBy(ex)
                    .withMessage("Error while creating parameter instance for %s", c);
        }
    }

    @Override
    public ParameterEntity<?> visit(BooleanValue value) throws OwsExceptionReport {
        return truth();
    }

    @Override
    public ParameterEntity<?> visit(CategoryValue value) throws OwsExceptionReport {
        return category();
    }

    @Override
    public ParameterEntity<?> visit(ComplexValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(CountValue value) throws OwsExceptionReport {
        return count();
    }

    @Override
    public ParameterEntity<?> visit(GeometryValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(HrefAttributeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(NilTemplateValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(QuantityValue value) throws OwsExceptionReport {
        return quantity();
    }

    @Override
    public ParameterEntity<?> visit(ReferenceValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(SweDataArrayValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(TVPValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(TextValue value) throws OwsExceptionReport {
        return text();
    }

    @Override
    public ParameterEntity<?> visit(UnknownValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(TLVTValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(CvDiscretePointCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(MultiPointCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(RectifiedGridCoverage value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(ProfileValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(TimeRangeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(XmlValue<?> value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ParameterEntity<?> visit(QuantityRangeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    private OwsExceptionReport notSupported(Value<?> value)
            throws OwsExceptionReport {
        throw new NoApplicableCodeException()
                .withMessage("Unsupported om:parameter value %s", value
                             .getClass().getCanonicalName());
    }

    public static ParameterFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final ParameterFactory INSTANCE = new ParameterFactory();

        private Holder() {
        }
    }

}
