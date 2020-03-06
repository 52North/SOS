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
package org.n52.sos.ds.hibernate.entities.observation;

import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ProfileObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class VoidObservationVisitor
        implements ObservationVisitor<Void> {

    protected abstract void _visit(NumericObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(BlobObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(BooleanObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(CategoryObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(ComplexObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(CountObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(GeometryObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(TextObservation o)
            throws OwsExceptionReport;

    protected abstract void _visit(SweDataArrayObservation o)
            throws OwsExceptionReport;
    
    protected abstract void _visit(ProfileObservation o)
            throws OwsExceptionReport;

    @Override
    public Void visit(NumericObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(BlobObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(BooleanObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(CategoryObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(ComplexObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(CountObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(GeometryObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(TextObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

    @Override
    public Void visit(SweDataArrayObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }
    
    @Override
    public Void visit(ProfileObservation o)
            throws OwsExceptionReport {
        _visit(o);
        return null;
    }

}
