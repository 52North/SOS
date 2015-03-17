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
package org.n52.sos.ogc.ows;

import org.n52.sos.util.StringHelper;

/**
 * Class represents an OWS range element
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 4.0.0
 *
 */
public class OwsRange {
    
    private String minValue;
    
    private String maxValue;
    
    private String spacing;

    /**
     * @return the minValue
     */
    public String getMinValue() {
        return minValue;
    }

    /**
     * @param minValue the minValue to set
     */
    public OwsRange setMinValue(String minValue) {
        this.minValue = minValue;
        return this;
    }
    
    /**
     * @return
     */
    public boolean isSetMinValue() {
        return StringHelper.isNotEmpty(getMinValue());
    }

    /**
     * @return the maxValue
     */
    public String getMaxValue() {
        return maxValue;
    }

    /**
     * @param maxValue the maxValue to set
     */
    public OwsRange setMaxValue(String maxValue) {
        this.maxValue = maxValue;
        return this;
    }
    
    /**
     * @return
     */
    public boolean isSetMaxValue() {
        return StringHelper.isNotEmpty(getMaxValue());
    }

    /**
     * @return the spacing
     */
    public String getSpacing() {
        return spacing;
    }

    /**
     * @param spacing the spacing to set
     */
    public OwsRange setSpacing(String spacing) {
        this.spacing = spacing;
        return this;
    }
    
    /**
     * @return
     */
    public boolean isSetSpacing() {
        return StringHelper.isNotEmpty(getSpacing());
    }

}
