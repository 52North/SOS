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

import static org.n52.iceland.util.http.HTTPStatus.BAD_REQUEST;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class ResponseExceedsSizeLimitException extends CodedSosException {
    private static final long serialVersionUID = 192859897753197663L;

    public ResponseExceedsSizeLimitException() {
        super(SosExceptionCode.ResponseExceedsSizeLimit);
        setStatus(BAD_REQUEST);
    }

    public ResponseExceedsSizeLimitException forLimit(final int size, final int limit) {
        withMessage("The request matched %d observations, which exceeds this server's limit of %d", size, limit);
        return this;
    }
}
