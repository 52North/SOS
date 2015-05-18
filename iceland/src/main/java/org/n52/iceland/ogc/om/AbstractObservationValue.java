/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ogc.om;

import org.n52.iceland.ogc.om.values.Value;
import org.n52.iceland.util.StringHelper;

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
