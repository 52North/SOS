/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.entities.observation.series.valued;

import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.VoidValuedObservationVisitor;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.SweDataArrayValuedObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.util.StringHelper;

/**
 * Implementation of a {@link ValuedObservation} for the series observation
 * concept, that holds a {@link SweDataArray} value represented by a string.
 *
 * @author Christian Autermann
 */
public class SweDataArrayValuedSeriesObservation extends AbstractValuedSeriesObservation<String>
        implements SweDataArrayValuedObservation {

    private static final long serialVersionUID = 8620387145785871292L;

    private String value;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean isSetValue() {
        return StringHelper.isNotEmpty(getValue());
    }

    @Override
    public String getValueAsString() {
        return getValue();
    }

    @Override
    public void accept(VoidValuedObservationVisitor visitor) throws OwsExceptionReport {
        visitor.visit(this);
    }

    @Override
    public <T> T accept(ValuedObservationVisitor<T> visitor) throws OwsExceptionReport {
        return visitor.visit(this);
    }
}
