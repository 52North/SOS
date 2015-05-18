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
package org.n52.iceland.exception.sos;

import org.n52.iceland.exception.ows.concrete.InvalidServiceParameterException;
import org.n52.iceland.ogc.ows.OWSConstants;
import org.n52.iceland.ogc.sos.SosConstants;

public class InvalidSosServiceParameterException extends InvalidServiceParameterException {

    /**
     * 
     */
    private static final long serialVersionUID = 3160084437440473955L;
    
    public InvalidSosServiceParameterException(String value) {
        super(value);
        withMessage("The value of the mandatory parameter '%s' must be '%s'. Delivered value was: %s",
              OWSConstants.GetCapabilitiesParams.service, SosConstants.SOS, value);
    }

}
