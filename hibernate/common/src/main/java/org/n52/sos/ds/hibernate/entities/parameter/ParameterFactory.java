/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
import org.n52.sos.ogc.ows.OwsExceptionReport;

public class ParameterFactory {

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

    private <T extends ValuedParameter<?>> T instantiate(Class<T> c) throws OwsExceptionReport {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new NoApplicableCodeException().causedBy(ex)
                    .withMessage("Error while creating parameter instance for %s", c);
        }
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
