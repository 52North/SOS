/*
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


import java.util.Collection;

import org.n52.iceland.ogc.ows.Value;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;

import com.google.common.base.Objects;

/**
 * Interface for the SOS internal representation of SWE simpleTypes
 *
 * @param <T>
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.0
 */
public abstract class SweAbstractSimpleType<T>
        extends SweAbstractDataComponent
        implements Value<T, SweAbstractSimpleType<T>> {

    // TODO quality needs to be a collection
    private Collection<SweQuality> quality;

    /**
     * Get quality information
     *
     * @return Quality information
     */
    public Collection<SweQuality> getQuality() {
        return quality;
    }

    /**
     * Set quality information
     *
     * @param quality
     *            quality information to set
     * @return This SweAbstractSimpleType
     */
    public SweAbstractSimpleType<T> setQuality(Collection<SweQuality> quality) {
        this.quality = quality;
        return this;
    }

    /**
     * @return <tt>true</tt>, if the quality field is not <tt>null</tt>,<br>
     *         <tt>false</tt> else.
     */
    public boolean isSetQuality() {
        return quality != null && !quality.isEmpty();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getValue());
    }

    @Override
    public String toString() {
        return String.format("%s [value=%s; quality=%s; simpleType=%s]", this.getClass().getSimpleName(), getValue(),
                getQuality(), getDataComponentType());
    }

}
