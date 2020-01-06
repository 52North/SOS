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
package org.n52.sos.ogc.swe;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.elements.SmlPosition;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweCategory;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweCountRange;
import org.n52.sos.ogc.swe.simpleType.SweObservableProperty;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweQuantityRange;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swe.simpleType.SweTime;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.ogc.swe.stream.StreamingSweDataArray;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class VoidSweDataComponentVisitor implements
        SweDataComponentVisitor<Void> {

    @Override
    public Void visit(SweField component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweDataRecord component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweDataArray component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweCount component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweCountRange component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweBoolean component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweCategory component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweObservableProperty component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweQuantity component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweQuantityRange component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweText component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweTime component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweTimeRange component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweEnvelope component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweVector component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(StreamingSweDataArray component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SweSimpleDataRecord component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    @Override
    public Void visit(SmlPosition component)
            throws OwsExceptionReport {
        _visit(component);
        return null;
    }

    protected abstract void _visit(SweField component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweDataRecord component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweDataArray component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweCount component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweCountRange component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweBoolean component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweCategory component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweObservableProperty component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweQuantity component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweQuantityRange component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweText component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweTime component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweTimeRange component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweEnvelope component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweVector component)
            throws OwsExceptionReport;

    protected abstract void _visit(StreamingSweDataArray component)
            throws OwsExceptionReport;

    protected abstract void _visit(SweSimpleDataRecord component)
            throws OwsExceptionReport;

    protected abstract void _visit(SmlPosition component)
            throws OwsExceptionReport;

}
