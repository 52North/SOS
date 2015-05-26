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
package org.n52.sos.ogc.sensorML.v20;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.sensorML.elements.SmlPosition;

/**
 * Class that represents SensorML 2.0 PhysicalProcess.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class AbstractPhysicalProcess extends DescribedObject {

    private static final long serialVersionUID = -4028425256170806503L;

    private ReferenceType attachedTo;

    private SpatialFrame localReferenceFrame;

    private TemporalFrame localTimeFrame;

    // TODO extend to list and support other type (point, text, datarecord, ...)
    private SmlPosition position;

    private Object timePosition;

    /**
     * @return the attachedTo
     */
    public ReferenceType getAttachedTo() {
        if (attachedTo == null && isSetParentProcedures()) {
            attachedTo = new ReferenceType(getParentProcedures().iterator().next());
            attachedTo.setTitle(attachedTo.getHref());
        }
        return attachedTo;
    }

    /**
     * Set the attachedTo reference. It is automatically added to
     * parentProcedure list.
     * 
     * @param attachedTo
     *            the attachedTo to set
     */
    public void setAttachedTo(ReferenceType attachedTo) {
        this.attachedTo = attachedTo;
        if (attachedTo.isSetTitle()) {
            addParentProcedure(attachedTo.getTitle());
        } else {
            addParentProcedure(attachedTo.getHref());
        }
    }

    public boolean isSetAttachedTo() {
        return attachedTo != null || isSetParentProcedures();
    }

    /**
     * @return the localReferenceFrame
     */
    public SpatialFrame getLocalReferenceFrame() {
        return localReferenceFrame;
    }

    /**
     * @param localReferenceFrame
     *            the localReferenceFrame to set
     */
    public void setLocalReferenceFrame(SpatialFrame localReferenceFrame) {
        this.localReferenceFrame = localReferenceFrame;
    }

    /**
     * @return the localTimeFrame
     */
    public TemporalFrame getLocalTimeFrame() {
        return localTimeFrame;
    }

    /**
     * @param localTimeFrame
     *            the localTimeFrame to set
     */
    public void setLocalTimeFrame(TemporalFrame localTimeFrame) {
        this.localTimeFrame = localTimeFrame;
    }

    /**
     * @return the position
     */
    public SmlPosition getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(SmlPosition position) {
        this.position = position;
    }

    /**
     * @return the timePosition
     */
    public Object getTimePosition() {
        return timePosition;
    }

    /**
     * @param timePosition
     *            the timePosition to set
     */
    public void setTimePosition(Object timePosition) {
        this.timePosition = timePosition;
    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public boolean isSetPosition() {
        return getPosition() != null;
    }

}
