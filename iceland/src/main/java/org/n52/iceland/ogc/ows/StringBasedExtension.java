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

import org.n52.iceland.ogc.swes.SwesExtension;

/**
 * <!--@deprecated use {@link org.n52.sos.ogc.swes.SwesExtension&lt;java.lang.String&gt;}-->
 * 
 * FIXME should this one replaced by SwesExtension<String> or should {@link SwesExtension} provide a direct and easy way to get the string represenation of this extension Object?
 *
 */
public interface StringBasedExtension extends SwesExtension<String> {

    /**
     * Get this extension as a String.
     *
     * @return the extension as a xml text
     * 
     * <!--@deprecated use {@link Encoder.encode(Object).toString()}-->
     */
	public String getExtension();
}
