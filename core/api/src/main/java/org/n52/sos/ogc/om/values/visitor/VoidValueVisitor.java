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
package org.n52.sos.ogc.om.values.visitor;

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
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.om.values.UnknownValue;
import org.n52.sos.ogc.om.values.XmlValue;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class VoidValueVisitor implements ValueVisitor<Void> {
    @Override
    public Void visit(BooleanValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(CategoryValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(ComplexValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(CountValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(GeometryValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(HrefAttributeValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(NilTemplateValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(QuantityValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(ReferenceValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(SweDataArrayValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(TVPValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }
    
    @Override
    public Void visit(TLVTValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(TextValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(UnknownValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    @Override
    public Void visit(XmlValue value)
            throws OwsExceptionReport {
        _visit(value);
        return null;
    }

    protected abstract void _visit(BooleanValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(CategoryValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(ComplexValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(CountValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(GeometryValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(HrefAttributeValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(NilTemplateValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(QuantityValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(ReferenceValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(SweDataArrayValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(TVPValue value)
            throws OwsExceptionReport;
    
    protected abstract void _visit(TLVTValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(TextValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(UnknownValue value)
            throws OwsExceptionReport;

    protected abstract void _visit(XmlValue value)
            throws OwsExceptionReport;
}
