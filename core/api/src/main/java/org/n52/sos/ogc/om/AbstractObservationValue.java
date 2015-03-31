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
package org.n52.sos.ogc.om;

import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.util.StringHelper;

public abstract class AbstractObservationValue<T extends Value<?>> implements ObservationValue<T> {

    private static final long serialVersionUID = -8969234704767943799L;
    
    private String observationID;
    
    private String observationType;
    
    private String observableProperty;
    
    private String tokenSeparator;
    
    private String tupleSeparator;
    
    private String decimalSeparator;
    
    private String unit;
    
    public void setValuesForResultEncoding(OmObservation observation) {
        setObservationID(observation.getObservationID());
        setObservableProperty(observation.getObservationConstellation().getObservableProperty().getIdentifier());
        setObservationType(observation.getObservationConstellation().getObservationType());
        setTokenSeparator(observation.getTokenSeparator());
        setTupleSeparator(observation.getTupleSeparator());
        setDecimalSeparator(observation.getDecimalSeparator());
    }

    /**
     * @return the observationID
     */
    public String getObservationID() {
        return observationID;
    }

    /**
     * @param observationID the observationID to set
     */
    private void setObservationID(String observationID) {
        this.observationID = observationID;
    }
    
    public boolean isSetObservationID() {
        return StringHelper.isNotEmpty(getObservationID());
    }

    /**
     * @return the observationType
     */
    public String getObservationType() {
        return observationType;
    }

    /**
     * @param observationType the observationType to set
     */
    private void setObservationType(String observationType) {
        this.observationType = observationType;
    }
    
    public boolean isSetObservationType() {
        return StringHelper.isNotEmpty(getObservationType());
    }

    /**
     * @return the observableProperty
     */
    public String getObservableProperty() {
        return observableProperty;
    }

    /**
     * @param observableProperty the observableProperty to set
     */
    private void setObservableProperty(String observableProperty) {
        this.observableProperty = observableProperty;
    }
    
    public boolean isSetObservablePropertyD() {
        return StringHelper.isNotEmpty(getObservableProperty());
    }

    /**
     * @return the tokenSeparator
     */
    public String getTokenSeparator() {
        return tokenSeparator;
    }

    /**
     * @param tokenSeparator the tokenSeparator to set
     */
    private void setTokenSeparator(String tokenSeparator) {
        this.tokenSeparator = tokenSeparator;
    }
    
    public boolean isSetTokenSeparator() {
        return StringHelper.isNotEmpty(getTokenSeparator());
    }

    /**
     * @return the tupleSeparator
     */
    public String getTupleSeparator() {
        return tupleSeparator;
    }

    /**
     * @param tupleSeparator the tupleSeparator to set
     */
    private void setTupleSeparator(String tupleSeparator) {
        this.tupleSeparator = tupleSeparator;
    }
    
    public boolean isSetTupleSeparator() {
        return StringHelper.isNotEmpty(getTupleSeparator());
    }
    
    /**
     * Get decimal separator
     * 
     * @return the decimalSeparator
     */
    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    /**
     * Set decimal separator
     * 
     * @param decimalSeparator
     *            the decimalSeparator to set
     */
    public void setDecimalSeparator(final String decimalSeparator) {
        this.decimalSeparator = decimalSeparator;
    }
    
    /**
     * Check whether decimal separator is set
     * 
     * @return <code>true</code>, if decimal separator is set
     */
    public boolean isSetDecimalSeparator() {
        return StringHelper.isNotEmpty(getDecimalSeparator());
    }

    /**
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public boolean isSetUnit() {
        return StringHelper.isNotEmpty(getUnit());
    }

}
