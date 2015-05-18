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
package org.n52.iceland.request;

/**
 * Marker interface to responseFormat
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public interface ResponseFormat {

	/**
     * Get response format
     * 
     * @return response format
     */
    public String getResponseFormat();

    /**
     * Set response format
     * 
     * @param responseFormat
     *            response format
     */
    public void setResponseFormat(String responseFormat);

    /**
     * Is response format set?
     * 
     * @return True if response format is set 
     */
    public boolean isSetResponseFormat();
	
}
