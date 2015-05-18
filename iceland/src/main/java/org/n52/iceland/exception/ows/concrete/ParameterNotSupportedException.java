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
package org.n52.iceland.exception.ows.concrete;

import org.n52.iceland.exception.ows.OptionNotSupportedException;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ParameterNotSupportedException extends OptionNotSupportedException {
    private static final long serialVersionUID = -4888724617519223794L;

    public ParameterNotSupportedException(String parameterName) {
        withMessage("The optional parameter '%s' is not supported by this service!", parameterName);
    }
}
