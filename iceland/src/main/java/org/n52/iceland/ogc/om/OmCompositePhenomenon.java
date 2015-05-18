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

import java.util.List;

/**
 * class represents a composite phenomenon
 * 
 * @since 4.0.0
 */
public class OmCompositePhenomenon extends AbstractPhenomenon {
    /**
     * serial number
     */
    private static final long serialVersionUID = 364153143602078222L;

    /** the components of the composite phenomenon */
    private List<OmObservableProperty> phenomenonComponents;

    /**
     * standard constructor
     * 
     * @param compPhenId
     *            id of the composite phenomenon
     * @param compPhenDesc
     *            description of the composite phenomenon
     * @param phenomenonComponents
     *            components of the composite phenomenon
     */
    public OmCompositePhenomenon(String compPhenId, String compPhenDesc,
            List<OmObservableProperty> phenomenonComponents) {
        super(compPhenId, compPhenDesc);
        this.phenomenonComponents = phenomenonComponents;
    }

    /**
     * Get observableProperties
     * 
     * @return Returns the phenomenonComponents.
     */
    public List<OmObservableProperty> getPhenomenonComponents() {
        return phenomenonComponents;
    }

    /**
     * Set observableProperties
     * 
     * @param phenomenonComponents
     *            The phenomenonComponents to set.
     */
    public void setPhenomenonComponents(List<OmObservableProperty> phenomenonComponents) {
        this.phenomenonComponents = phenomenonComponents;
    }
}
