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
package org.n52.sos.ds.hibernate.entities.feature.gmd;

import org.n52.sos.ds.hibernate.entities.feature.gml.VerticalCRSEntity;

import com.google.common.base.Strings;

/**
 * Hibernate entity for exVerticalExtent.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class ExVerticalExtentEntity extends AbstractCiEntity {
    
    private Double minimumValue;
    private String minValuNilReason;
    private Double maximumValue;
    private String maxValuNilReason;
    private VerticalCRSEntity verticalCRS;
    
    /**
     * @return the minimumValue
     */
    public Double getMinimumValue() {
        return minimumValue;
    }
    /**
     * @param minimumValue the minimumValue to set
     */
    public void setMinimumValue(Double minimumValue) {
        this.minimumValue = minimumValue;
    }
    public boolean isSetMinimumValue() {
        return getMinimumValue() != null;
    }
    
    /**
     * @return the minValuNilReason
     */
    public String getMinValuNilReason() {
        return minValuNilReason;
    }
    /**
     * @param minValuNilReason the minValuNilReason to set
     */
    public void setMinValuNilReason(String minValuNilReason) {
        this.minValuNilReason = minValuNilReason;
    }
    
    public boolean isSetMinValuNilReason() {
        return Strings.isNullOrEmpty(getMinValuNilReason());
    }
    /**
     * @return the maximumValue
     */
    public Double getMaximumValue() {
        return maximumValue;
    }
    /**
     * @param maximumValue the maximumValue to set
     */
    public void setMaximumValue(Double maximumValue) {
        this.maximumValue = maximumValue;
    }
    
    public boolean isSetMaximumValue() {
        return getMaximumValue() != null;
    }
    /**
     * @return the maxValuNilReason
     */
    public String getMaxValuNilReason() {
        return maxValuNilReason;
    }
    /**
     * @param maxValuNilReason the maxValuNilReason to set
     */
    public void setMaxValuNilReason(String maxValuNilReason) {
        this.maxValuNilReason = maxValuNilReason;
    }
    
    public boolean isSetMaxValuNilReason() {
        return Strings.isNullOrEmpty(getMaxValuNilReason());
    }
    /**
     * @return the verticalCRS
     */
    public VerticalCRSEntity getVerticalCRS() {
        return verticalCRS;
    }
    /**
     * @param verticalCRS the verticalCRS to set
     */
    public void setVerticalCRS(VerticalCRSEntity verticalCRS) {
        this.verticalCRS = verticalCRS;
    }
    
    public boolean isSetVerticalCRS() {
        return getVerticalCRS() != null;
    }

}
