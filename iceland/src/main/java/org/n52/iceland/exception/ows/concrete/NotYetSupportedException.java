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

import static org.n52.iceland.util.http.HTTPStatus.INTERNAL_SERVER_ERROR;

import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.util.http.HTTPStatus;

import com.google.common.base.Joiner;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class NotYetSupportedException extends NoApplicableCodeException {
    private static final long serialVersionUID = 8214490617892996058L;

    private final HTTPStatus status = INTERNAL_SERVER_ERROR;

    public NotYetSupportedException() {
        setStatus(status);
    }

    public NotYetSupportedException(final String feature) {
        withMessage("%s is not yet supported", feature);
        setStatus(status);
    }

    public NotYetSupportedException(final String type, final Object feature) {
        withMessage("The %s %s is not yet supported", type, feature);
        setStatus(status);
    }

    public NotYetSupportedException(final String type, final Object feature, final Object... supportedFeatures) {
        withMessage("The %s %s is not yet supported. Currently supported: %s", type, feature,
                Joiner.on(", ").join(supportedFeatures));
        setStatus(status);
    }
}
