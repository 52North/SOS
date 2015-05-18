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

import org.n52.iceland.exception.ows.InvalidParameterValueException;
import org.n52.iceland.ogc.ows.OWSConstants.RequestParams;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class VersionNotSupportedException extends InvalidParameterValueException {
    private static final long serialVersionUID = 7013609926378578859L;

    public VersionNotSupportedException() {
        withMessage("The requested version is not supported!").at(RequestParams.version);
    }
}
