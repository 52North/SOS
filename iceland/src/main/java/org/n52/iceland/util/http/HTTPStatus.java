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
package org.n52.iceland.util.http;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public enum HTTPStatus {
    CONTINUE(100),
    SWITCHING_PROTOCOLS(101),
    PROCESSING(102),
    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NON_AUTHORITIVE_INFORMATION(203),
    NO_CONTENT(204),
    RESET_CONTENT(205),
    PARTIAL_CONTENT(206),
    MULTI_STATUS(207),
    MULTIPLE_CHOICES(300),
    MOVED_PERMANENTLY(301),
    FOUND(302),
    SEE_OHTER(303),
    NOT_MODIFIED(304),
    USE_PROXY(305),
    TEMPORARY_REDIRECT(307),
    PERMANENT_REDIRECT(308),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    NOT_ACCEPTABLE(406),
    PROXY_AUTHENTICATION_REQUIRED(407),
    REQUEST_TIME_OUT(408),
    CONFLICT(409),
    GONE(410),
    LENGTH_REQUIRED(411),
    PRECONDITION_FAILED(412),
    REQUEST_ENTITY_TO_LARGE(413),
    REQUEST_URL_TO_LONG(414),
    UNSUPPORTED_MEDIA_TYPE(415),
    REQUESTED_RANGE_NOT_STAISFIABLE(416),
    EXPECTATION_FAILED(417),
    INTERNAL_SERVER_ERROR(500),
    NOT_IMPLEMENTED(501),
    BAD_GATEWAY(502),
    SERVICE_UNAVAILABLE(503),
    GATEWAY_TIME_OUT(504),
    HTTP_VERSION_NOT_SUPPORTED(505);
    private final int code;
    private static final int STATUS_100 = 100;

    private static final int STATUS_200 = 200;

    private static final int STATUS_300 = 300;

    private static final int STATUS_400 = 400;

    private static final int STATUS_500 = 500;

    private HTTPStatus(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean isInformative() {
        return code >= STATUS_100 && code < STATUS_200;
    }

    public boolean isSuccess() {
        return code >= STATUS_200 && code < STATUS_300;
    }

    public boolean isRedirect() {
        return code >= STATUS_300 && code < STATUS_400;
    }

    public boolean isError() {
        return code >= STATUS_400;
    }

    public boolean isClientError() {
        return code >= STATUS_400 && code < STATUS_500;
    }

    public boolean isServerError() {
        return code >= STATUS_500;
    }
}
