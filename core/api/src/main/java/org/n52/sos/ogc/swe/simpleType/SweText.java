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
package org.n52.sos.ogc.swe.simpleType;

import static com.google.common.base.Preconditions.checkNotNull;

import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;

/**
 * SOS internal representation of SWE simpleType text
 * 
 * @author Carsten Hollmann
 * @since 4.0.0
 */
public class SweText extends SweAbstractSimpleType<String> implements Comparable<SweText>, SweQuality {

    /**
     * value
     */
    private String value;

    /**
     * constructor
     */
    public SweText() {
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public SweText setValue(final String value) {
        this.value = value;
        return this;
    }

    @Override
    public String getStringValue() {
        return value;
    }

    @Override
    public boolean isSetValue() {
        return value != null && !value.isEmpty();
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Text;
    }

    @Override
    public int compareTo(SweText o) {
        return checkNotNull(o) == this ? 0
                : getValue() == o.getValue() ? 0
                    : getValue() == null ? -1
                       : o.getValue() == null ? 1
                          : getValue().compareTo(o.getValue());
    }
}
