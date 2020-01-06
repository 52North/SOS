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
package org.n52.sos.ogc.gml;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.w3c.xlink.Referenceable;

/**
 * Internal representation of the OGC GML AbstractCoordinateSystem.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public abstract class AbstractCoordinateSystem extends IdentifiedObject {

    private static final long serialVersionUID = -8178398617300946309L;
    
    /* 1..* */
    private List<Referenceable<CoordinateSystemAxis>> coordinateSystemAxis = new ArrayList<>();
    
    private Aggregation aggregation;
    
    public AbstractCoordinateSystem(CodeWithAuthority identifier, Referenceable<CoordinateSystemAxis> coordinateSystemAxis) {
        super(identifier);
        addCoordinateSystemAxis(coordinateSystemAxis);
    }
    
    public AbstractCoordinateSystem(CodeWithAuthority identifier, List<Referenceable<CoordinateSystemAxis>> coordinateSystemAxis) {
        super(identifier);
        addCoordinateSystemAxis(coordinateSystemAxis);
    }

    /**
     * @return the coordinateSystemAxis
     */
    public List<Referenceable<CoordinateSystemAxis>> getCoordinateSystemAxis() {
        return coordinateSystemAxis;
    }

    /**
     * @param coordinateSystemAxis the coordinateSystemAxis to set
     * @return 
     */
    public AbstractCoordinateSystem setCoordinateSystemAxis(List<Referenceable<CoordinateSystemAxis>> coordinateSystemAxis) {
        this.coordinateSystemAxis.clear();
        this.coordinateSystemAxis.addAll(coordinateSystemAxis);
        return this;
    }
    
    /**
     * @param coordinateSystemAxis the coordinateSystemAxis to set
     * @return 
     */
    public AbstractCoordinateSystem addCoordinateSystemAxis(List<Referenceable<CoordinateSystemAxis>> coordinateSystemAxis) {
        this.coordinateSystemAxis.addAll(coordinateSystemAxis);
        return this;
    }
    
    
    /**
     * @param coordinateSystemAxis the coordinateSystemAxis to set
     * @return 
     */
    public AbstractCoordinateSystem addCoordinateSystemAxis(Referenceable<CoordinateSystemAxis> coordinateSystemAxis) {
        this.coordinateSystemAxis.add(coordinateSystemAxis);
        return this;
    }

    /**
     * @return the aggregation
     */
    public Aggregation getAggregation() {
        return aggregation;
    }

    /**
     * @param aggregation the aggregation to set
     * @return 
     */
    public AbstractCoordinateSystem setAggregation(Aggregation aggregation) {
        this.aggregation = aggregation;
        return this;
    }
    
    public boolean isSetAggregation() {
        return getAggregation() != null;
    }

}
