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
package org.n52.iceland.response;

import org.n52.iceland.ogc.ows.OWSConstants;
import org.n52.iceland.ogc.ows.OwsCapabilities;

/**
 * @since 4.0.0
 * 
 */
public class GetCapabilitiesResponse extends AbstractServiceResponse {

    private OwsCapabilities capabilities;
	
    private String xmlString;

    public OwsCapabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(OwsCapabilities capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public String getOperationName() {
        return OWSConstants.Operations.GetCapabilities.name();
    }

    public String getXmlString() {
        return xmlString;
    }

    public void setXmlString(String xmlString) {
        this.xmlString = xmlString;
    }
    
    public boolean isStatic() {
        return getXmlString() != null && !getXmlString().isEmpty();
    }
}
