/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.sensorML.elements;

import java.util.List;

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.ogc.swe.SweCoordinate;
import org.n52.sos.ogc.swe.SweDataComponentVisitor;
import org.n52.sos.ogc.swe.VoidSweDataComponentVisitor;

/**
 * SOS internal representation of SensorML position
 *
 * @since 4.0.0
 */
public class SmlPosition extends SweAbstractDataComponent {

    private String name;

    private boolean fixed;

    private String referenceFrame;

    private List<SweCoordinate<?>> position;

    /**
     * default constructor
     */
    public SmlPosition() {
        super();
    }

    /**
     * constructor
     *
     * @param name
     *                       Position name
     * @param fixed
     *                       is fixed
     * @param referenceFrame
     *                       Position reference frame
     * @param position
     *                       Position coordinates
     */
    public SmlPosition(final String name, final boolean fixed,
                       final String referenceFrame,
                       final List<SweCoordinate<?>> position) {
        super();
        setName(name);
        this.fixed = fixed;
        this.referenceFrame = referenceFrame;
        this.position = position;
    }

    /**
     * constructor
     *
     * @param name
     *                       Position name
     * @param fixed
     *                       is fixed
     * @param referenceFrame
     *                       Position reference frame
     * @param position
     *                       Position coordinates
     */
    public SmlPosition(final CodeType name, final boolean fixed,
                       final String referenceFrame,
                       final List<SweCoordinate<?>> position) {
        super();
        setName(name);
        this.fixed = fixed;
        this.referenceFrame = referenceFrame;
        this.position = position;
    }

    /**
     * @return the fixed
     */
    public boolean isFixed() {
        return fixed;
    }

    /**
     * @param fixed
     *              the fixed to set
     *
     * @return This object
     */
    public SmlPosition setFixed(final boolean fixed) {
        this.fixed = fixed;
        return this;
    }

    /**
     * @return the referenceFrame
     */
    public String getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * @param referenceFrame
     *                       the referenceFrame to set
     *
     * @return This object
     */
    public SmlPosition setReferenceFrame(final String referenceFrame) {
        this.referenceFrame = referenceFrame;
        return this;
    }

    /**
     * @return the position
     */
    public List<SweCoordinate<?>> getPosition() {
        return position;
    }

    /**
     * @param position
     *                 the position to set
     *
     * @return This object
     */
    public SmlPosition setPosition(final List<SweCoordinate<?>> position) {
        this.position = position;
        return this;
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Position;
    }

    @Override
    public <T> T accept(SweDataComponentVisitor<T> visitor)
            throws OwsExceptionReport {
        return visitor.visit(this);
    }

    @Override
    public void accept(VoidSweDataComponentVisitor visitor)
            throws OwsExceptionReport {
        visitor.visit(this);
    }
}
