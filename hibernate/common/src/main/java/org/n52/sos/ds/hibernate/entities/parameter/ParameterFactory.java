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
package org.n52.sos.ds.hibernate.entities.parameter;

import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CategoryValue;
import org.n52.sos.ogc.om.values.ComplexValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.HrefAttributeValue;
import org.n52.sos.ogc.om.values.NilTemplateValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.ReferenceValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.om.values.XmlValue;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public class ParameterFactory implements ValueVisitor<ValuedParameter<?>> {

    protected ParameterFactory() {
    }

    public Class<? extends BooleanValuedParameter> truthClass() {
        return BooleanValuedParameter.class;
    }

    public BooleanValuedParameter truth() throws OwsExceptionReport {
        return instantiate(truthClass());
    }

    public Class<? extends CategoryValuedParameter> categoryClass() {
        return CategoryValuedParameter.class;
    }

    public CategoryValuedParameter category() throws OwsExceptionReport {
        return instantiate(categoryClass());
    }

    public Class<? extends CountValuedParameter> countClass() {
        return CountValuedParameter.class;
    }

    public CountValuedParameter count() throws OwsExceptionReport {
        return instantiate(countClass());
    }

    public Class<? extends QuantityValuedParameter> quantityClass() {
        return QuantityValuedParameter.class;
    }

    public QuantityValuedParameter quantity() throws OwsExceptionReport {
        return instantiate(quantityClass());
    }

    public Class<? extends TextValuedParameter> textClass() {
        return TextValuedParameter.class;
    }

    public TextValuedParameter text() throws OwsExceptionReport {
        return instantiate(textClass());
    }

    public Class<? extends XmlValuedParameter> xmlClass() {
        return XmlValuedParameter.class;
    }

    public XmlValuedParameter xml() throws OwsExceptionReport {
        return instantiate(xmlClass());
    }

    private <T extends ValuedParameter<?>> T instantiate(Class<T> c) throws OwsExceptionReport {

        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new NoApplicableCodeException().causedBy(ex)
                    .withMessage("Error while creating parameter instance for %s", c);
        }
    }

    @Override
    public ValuedParameter<?> visit(BooleanValue value) throws OwsExceptionReport {
        return truth();
    }

    @Override
    public ValuedParameter<?> visit(CategoryValue value) throws OwsExceptionReport {
        return category();
    }

    @Override
    public ValuedParameter<?> visit(ComplexValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ValuedParameter<?> visit(CountValue value) throws OwsExceptionReport {
        return count();
    }

    @Override
    public ValuedParameter<?> visit(GeometryValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ValuedParameter<?> visit(HrefAttributeValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ValuedParameter<?> visit(NilTemplateValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ValuedParameter<?> visit(QuantityValue value) throws OwsExceptionReport {
        return quantity();
    }

    @Override
    public ValuedParameter<?> visit(ReferenceValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ValuedParameter<?> visit(SweDataArrayValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ValuedParameter<?> visit(TVPValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ValuedParameter<?> visit(TextValue value) throws OwsExceptionReport {
        return text();
    }

    @Override
    public ValuedParameter<?> visit(UnknownValue value) throws OwsExceptionReport {
        throw notSupported(value);
    }

    @Override
    public ValuedParameter<?> visit(XmlValue value)
            throws OwsExceptionReport {
        return xml();
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

    private static class Holder {
        private static final ParameterFactory INSTANCE = new ParameterFactory();

        private Holder() {
        }
    }

}
