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

import org.n52.iceland.exception.ows.InvalidParameterValueException;

import com.google.common.base.Joiner;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class ContentTypeNotSupportedException extends InvalidParameterValueException {

    private static final long serialVersionUID = 400L;

    public ContentTypeNotSupportedException(final String contentType, final String... supportedContentType) {
        super("HTTP header Accept", contentType);
        withMessage(
                "Requested content type '%s' as specified in 'Accept' header not supported. Please use something like: '%s'.",
                contentType, Joiner.on(", ").join(supportedContentType));
        setStatus(BAD_REQUEST);
    }

}
