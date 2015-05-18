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
package org.n52.iceland.ogc.ows;

import org.n52.iceland.util.StringHelper;

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
