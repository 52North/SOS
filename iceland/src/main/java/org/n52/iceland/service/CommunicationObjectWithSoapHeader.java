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
package org.n52.iceland.service;

import java.util.List;

/**
 * Interface to provide SOAP Header support in Request and Response objects.
 * 
 * @author Matthes Rieke
 * 
 * @since 4.0.0
 * 
 */
public interface CommunicationObjectWithSoapHeader {

    /**
     * @return the SoapHeader encoded as an InputStream containing XML.
     */
    List<SoapHeader> getSoapHeader();

    /**
     * @param header
     *            the SoapHeader encoded as an InputStream containing XML.
     */
    void setSoapHeader(List<SoapHeader> header);

    /**
     * Convenience method to check if the SoapHeader is set.
     * 
     * @return true if Header is set
     */
    boolean isSetSoapHeader();

}
