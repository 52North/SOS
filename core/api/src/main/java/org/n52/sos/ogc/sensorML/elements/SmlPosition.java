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
package org.n52.sos.ogc.sensorML.elements;

import java.util.List;

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweConstants.SweDataComponentType;
import org.n52.sos.ogc.swe.SweCoordinate;
import org.n52.sos.ogc.swe.SweVector;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.StringHelper;

/**
 * SOS internal representation of SensorML position
 * 
 * @since 4.0.0
 */
public class SmlPosition extends SweAbstractDataComponent {

    private boolean fixed;

    private String referenceFrame;

    private List<SweCoordinate<?>> position;
    
    private SweVector vector;
    
    private SweAbstractDataComponent dataComponent;

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
     *            Position name
     * @param fixed
     *            is fixed
     * @param referenceFrame
     *            Position reference frame
     * @param position
     *            Position coordinates
     */
    public SmlPosition(final String name, final boolean fixed, final String referenceFrame,
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
     *            Position name
     * @param fixed
     *            is fixed
     * @param referenceFrame
     *            Position reference frame
     * @param position
     *            Position coordinates
     */
    public SmlPosition(final CodeType name, final boolean fixed, final String referenceFrame,
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
     *            the fixed to set
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
        if (!isSetReferenceFrame() && isSetVector() && getVector().isSetReferenceFrame()) {
            return getVector().getReferenceFrame();
        }
        return referenceFrame;
    }

    /**
     * @param referenceFrame
     *            the referenceFrame to set
     * @return This object
     */
    public SmlPosition setReferenceFrame(final String referenceFrame) {
        this.referenceFrame = referenceFrame;
        return this;
    }
    
    public boolean isSetReferenceFrame() {
        return StringHelper.isNotEmpty(referenceFrame);
    }

    /**
     * @return the position
     */
    public List<SweCoordinate<?>> getPosition() {
        if (!isSetPosition() && isSetVector() && getVector().isSetCoordinates()) {
            if (!isSetName() && vector.isSetName()) {
                setName(vector.getName());
            }
            return vector.getCoordinates();
        }
        return position;
    }

    /**
     * @param position
     *            the position to set
     * @return This object
     */
    public SmlPosition setPosition(final List<SweCoordinate<?>> position) {
        this.position = position;
        return this;
    }
    
    public boolean isSetPosition() {
        return CollectionHelper.isNotEmpty(position);
    }
    
    public SweVector getVector() {
        if (!isSetVector() && isSetPosition()) {
            SweVector vector = (SweVector)copyValueTo(new SweVector(getPosition()));
            vector.setReferenceFrame(getReferenceFrame());
            if (isSetName()) {
                vector.setName(getName());
            }
            return vector;
        }
        return vector;
    }

    public void setVector(SweVector vector) {
       this.vector = vector;
    }
    
    public boolean isSetVector() {
        return vector != null;
    }
    
    public SweAbstractDataComponent getAbstractDataComponent() {
        if (!isSetAbstractDataComponent() && isSetVector()) {
            return vector;
        }
        return dataComponent;
    }

    public void setAbstractDataComponent(SweAbstractDataComponent dataComponent) {
        if (dataComponent instanceof SweVector) {
            setVector((SweVector)dataComponent);
        }
       this.dataComponent = dataComponent;
    }
    
    public boolean isSetAbstractDataComponent() {
        return dataComponent != null && isSetVector();
    }

    @Override
    public SweDataComponentType getDataComponentType() {
        return SweDataComponentType.Position;
    }

}
