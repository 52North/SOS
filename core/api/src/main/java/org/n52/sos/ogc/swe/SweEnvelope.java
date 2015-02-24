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

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.swe.SweConstants.SweCoordinateName;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweTimeRange;
import org.n52.sos.util.SosHelper;

import com.google.common.base.Objects;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @since 4.0.0
 * 
 */
public class SweEnvelope extends SweAbstractDataComponent {
    private String referenceFrame;

    private SweVector upperCorner;

    private SweVector lowerCorner;

    private SweTimeRange time;

    public SweEnvelope() {
        this(null, null, null, null);
    }

    public SweEnvelope(String referenceFrame, SweVector upperCorner, SweVector lowerCorner) {
        this(referenceFrame, upperCorner, lowerCorner, null);
    }

    public SweEnvelope(SosEnvelope sosEnvelope, String uom) {
        this(String.valueOf(sosEnvelope.getSrid()), 
             createUpperCorner(sosEnvelope, uom),
             createLowerCorner(sosEnvelope, uom));
    }

    public SweEnvelope(String referenceFrame, SweVector upperCorner, SweVector lowerCorner, SweTimeRange time) {
        this.referenceFrame = referenceFrame;
        this.upperCorner = upperCorner;
        this.lowerCorner = lowerCorner;
        this.time = time;
    }

    public String getReferenceFrame() {
        return referenceFrame;
    }

    public boolean isReferenceFrameSet() {
        return getReferenceFrame() != null;
    }

    public SweEnvelope setReferenceFrame(String referenceFrame) {
        this.referenceFrame = referenceFrame;
        return this;
    }

    public SweVector getUpperCorner() {
        return upperCorner;
    }

    public boolean isUpperCornerSet() {
        return getUpperCorner() != null;
    }

    public SweEnvelope setUpperCorner(SweVector upperCorner) {
        this.upperCorner = upperCorner;
        return this;
    }

    public SweVector getLowerCorner() {
        return lowerCorner;
    }

    public boolean isLowerCornerSet() {
        return getLowerCorner() != null;
    }

    public SweEnvelope setLowerCorner(SweVector lowerCorner) {
        this.lowerCorner = lowerCorner;
        return this;
    }

    public SweTimeRange getTime() {
        return time;
    }

    public boolean isTimeSet() {
        return getTime() != null;
    }

    public SweEnvelope setTime(SweTimeRange time) {
        this.time = time;
        return this;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(getClass())
                .add("upperCorner", getUpperCorner())
                .add("lowerCorner", getLowerCorner())
                .add("time", getTime())
                .add("referenceFrame", getReferenceFrame())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getReferenceFrame(), getUpperCorner(), getLowerCorner(), getTime());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SweEnvelope) {
            SweEnvelope other = (SweEnvelope) obj;
            return Objects.equal(getReferenceFrame(), other.getReferenceFrame())
                    && Objects.equal(getUpperCorner(), other.getUpperCorner())
                    && Objects.equal(getLowerCorner(), other.getLowerCorner())
                    && Objects.equal(getTime(), other.getTime());

        }
        return false;
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Envelope;
    }

    public SosEnvelope toSosEnvelope() throws OwsExceptionReport {
        int srid = SosHelper.parseSrsName(getReferenceFrame());
        return new SosEnvelope(toEnvelope(), srid);
    }

    public Envelope toEnvelope() {
        Coordinate min = getLowerCornerAsCoordinate();
        Coordinate max = getUpperCornerAsCoordinate();
        return min != null && max != null ?
               new Envelope(min.x, max.x, min.y, max.y) : null;
    }

    public Coordinate getLowerCornerAsCoordinate() {
        if (isLowerCornerSet()) {
            return getSweVectorAsCoordinate(getLowerCorner());
        }
        return null;
    }

    public Coordinate getUpperCornerAsCoordinate() {
        if (isUpperCornerSet()) {
            return getSweVectorAsCoordinate(getUpperCorner());
        }
        return null;
    }

    private Coordinate getSweVectorAsCoordinate(SweVector vector) {
        if (vector != null &&
            vector.isSetCoordinates() &&
            vector.getCoordinates().size() >= 2) {
            Double x = extractDouble(vector.getCoordinates().get(0));
            Double y = extractDouble(vector.getCoordinates().get(1));
            if (x != null && y != null) {
                return new Coordinate(x, y);
            }
        }
        return null;
    }

    private Double extractDouble(SweCoordinate<?> coord) {
        if (coord != null && 
            coord.getValue() != null &&
            coord.getValue().getValue() instanceof Number) {
            return ((Number) coord.getValue().getValue()).doubleValue();
        }
        return null;
    }

    private static SweVector createLowerCorner(SosEnvelope env, String uom) {
        return createSweVector(env.getEnvelope().getMinX(), env.getEnvelope().getMinY(), uom);
    }

    private static SweVector createUpperCorner(SosEnvelope env, String uom) {
        return createSweVector(env.getEnvelope().getMaxX(), env.getEnvelope().getMaxY(), uom);
    }

    private static SweVector createSweVector(double x, double y, String uom) {
        SweQuantity xCoord = new SweQuantity().setValue(x).setAxisID("x").setUom(uom);
        SweQuantity yCoord = new SweQuantity().setValue(y).setAxisID("y").setUom(uom);
        return new SweVector(new SweCoordinate<Double>(SweCoordinateName.easting.name(), xCoord),
                new SweCoordinate<Double>(SweCoordinateName.northing.name(), yCoord));
    }
}
