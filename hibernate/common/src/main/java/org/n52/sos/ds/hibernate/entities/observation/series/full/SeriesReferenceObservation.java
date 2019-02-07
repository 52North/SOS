/**
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.observation.series.full;

import org.n52.sos.ds.hibernate.entities.observation.ObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.VoidObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.VoidValuedObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.full.ReferenceObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.StringHelper;

public class SeriesReferenceObservation
        extends
        AbstractSeriesObservation<ReferenceType>
        implements
        ReferenceObservation {

    private static final long serialVersionUID = 1L;
    private ReferenceType value;
    private String href;
    private String title;
    private String role;

    @Override
    public ReferenceType getValue() {
        if (value == null) {
            value = new ReferenceType();
        }
        value.setHref(getHref());
        value.setTitle(getTitle());
        value.setRole(role);
        return value;
    }

    @Override
    public void setValue(ReferenceType value) {
        this.value = value;
        setHref(value.isSetHref() ? value.getHref() : null);
        setTitle(value.isSetTitle() ? value.getTitle() : null);
        setRole(value.isSetRole() ? value.getRole() : null);
    }

    @Override
    public boolean isSetValue() {
        return isSetHref();
    }

    @Override
    public String getValueAsString() {
        return getHref();
    }

    @Override
    public String getHref() {
        return href;
    }

    @Override
    public void setHref(String href) {
       this.href = href;
    }

    @Override
    public boolean isSetHref() {
        return StringHelper.isNotEmpty(getHref());
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    public boolean isSetTitle() {
        return StringHelper.isNotEmpty(getTitle());
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean isSetRole() {
        return StringHelper.isNotEmpty(getRole());
    }

    @Override
    public void accept(VoidObservationVisitor visitor)
            throws OwsExceptionReport {
        visitor.visit(this);
    }

    @Override
    public <T> T accept(ObservationVisitor<T> visitor)
            throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidValuedObservationVisitor visitor)
            throws OwsExceptionReport {
        visitor.visit(this);
    }

    @Override
    public <T> T accept(ValuedObservationVisitor<T> visitor)
            throws OwsExceptionReport {
        return visitor.visit(this);
    }
}
