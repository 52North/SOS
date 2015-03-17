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
package org.n52.sos.ogc.swe;

import java.util.Arrays;
import java.util.List;

import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.util.StringHelper;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 * 
 */
public class SweVector extends SweAbstractDataComponent {
    private List<SweCoordinate<?>> coordinates;

    private String referenceFrame;

    private String localFrame;

    public SweVector(List<SweCoordinate<?>> coordinates) {
        this.coordinates = coordinates;
    }

    public SweVector(SweCoordinate<?>... coordinates) {
        this(Arrays.asList(coordinates));
    }

    public SweVector() {
        this((List<SweCoordinate<?>>) null);
    }

    public List<SweCoordinate<?>> getCoordinates() {
        return coordinates;
    }

    public SweVector setCoordinates(final List<SweCoordinate<?>> coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    public boolean isSetCoordinates() {
        return getCoordinates() != null && !getCoordinates().isEmpty();
    }

    @Override
    public String toString() {
        return String.format("%s[coordinates=%s]", getClass().getSimpleName(), getCoordinates());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.coordinates);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SweVector other = (SweVector) obj;
        if (!Objects.equal(coordinates, other.coordinates)) {
            return false;
        }
        return true;
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Vector;
    }

    public void setReferenceFrame(String referenceFrame) {
        this.referenceFrame = referenceFrame;

    }

    public String getReferenceFrame() {
        return referenceFrame;
    }

    public boolean isSetReferenceFrame() {
        return StringHelper.isNotEmpty(referenceFrame);
    }

    public void setLocalFrame(String localFrame) {
        this.localFrame = localFrame;

    }

    public String getLocalFrame() {
        return localFrame;
    }

    public boolean isSetLocalFrame() {
        return StringHelper.isNotEmpty(localFrame);
    }

}
