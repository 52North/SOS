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

import static org.n52.iceland.util.http.HTTPStatus.BAD_REQUEST;

import org.joda.time.DateTime;
import org.n52.iceland.ogc.gml.time.Time;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class DateTimeFormatException extends DateTimeException {
    private static final long serialVersionUID = 4594521785170898431L;

    public DateTimeFormatException(final Time value) {
        this(value, null);
    }

    public DateTimeFormatException(final Time value, final Throwable cause) {
        withMessage("Error formatting ITime %s", value);
        if (cause != null) {
            causedBy(cause);
        }
        setStatus(BAD_REQUEST);
    }

    public DateTimeFormatException(final DateTime value) {
        this(value, null);
    }

    public DateTimeFormatException(final DateTime value, final Throwable cause) {
        withMessage("Error formatting DateTime %s", value);
        if (cause != null) {
            causedBy(cause);
        }
        setStatus(BAD_REQUEST);
    }
}
