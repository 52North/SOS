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
package org.n52.iceland.exception.ows;

import static org.n52.iceland.util.http.HTTPStatus.BAD_REQUEST;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class InvalidParameterValueException extends CodedOwsException {
    private static final long serialVersionUID = 7664405001972222761L;

    public InvalidParameterValueException() {
        super(OwsExceptionCode.InvalidParameterValue);
        setStatus(BAD_REQUEST);
    }

    public InvalidParameterValueException(final String parameterName, final String value) {
        super(OwsExceptionCode.InvalidParameterValue);
        withMessage("The value '%s' of the parameter '%s' is invalid", value, parameterName).at(parameterName);
        setStatus(BAD_REQUEST);
    }

    public InvalidParameterValueException(final Enum<?> parameterName, final String value) {
        this(parameterName.name(), value);
    }
}
