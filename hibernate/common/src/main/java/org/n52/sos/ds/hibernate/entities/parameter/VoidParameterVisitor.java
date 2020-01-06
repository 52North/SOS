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

import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public abstract class VoidParameterVisitor implements ParameterVisitor<Void> {

    protected abstract void _visit(QuantityValuedParameter p)
            throws OwsExceptionReport;

    protected abstract void _visit(BooleanValuedParameter p)
            throws OwsExceptionReport;

    protected abstract void _visit(CategoryValuedParameter p)
            throws OwsExceptionReport;

    protected abstract void _visit(CountValuedParameter p)
            throws OwsExceptionReport;

    protected abstract void _visit(TextValuedParameter p)
            throws OwsExceptionReport;

    protected abstract void _visit(XmlValuedParameter p)
            throws OwsExceptionReport;


    @Override
    public NamedValue<Void> visit(QuantityValuedParameter p)
            throws OwsExceptionReport {
        _visit(p);
        return null;
    }

    @Override
    public NamedValue<Void> visit(BooleanValuedParameter p)
            throws OwsExceptionReport {
        _visit(p);
        return null;
    }

    @Override
    public NamedValue<Void> visit(CategoryValuedParameter p)
            throws OwsExceptionReport {
        _visit(p);
        return null;
    }

    @Override
    public NamedValue<Void> visit(CountValuedParameter p)
            throws OwsExceptionReport {
        _visit(p);
        return null;
    }

    @Override
    public NamedValue<Void> visit(TextValuedParameter p)
            throws OwsExceptionReport {
        _visit(p);
        return null;
    }

    @Override
    public NamedValue<Void> visit(XmlValuedParameter p)
            throws OwsExceptionReport {
        _visit(p);
        return null;
    }

}
