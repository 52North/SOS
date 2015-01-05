/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.util.http;

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
