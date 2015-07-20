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
package org.n52.sos.statistics.api;

import org.n52.sos.statistics.api.parameters.AbstractEsParameter;
import org.n52.sos.statistics.api.parameters.ElasticsearchTypeRegistry;
import org.n52.sos.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.sos.statistics.api.parameters.SingleEsParameter;

public class ServiceEventDataMapping {
    public static final AbstractEsParameter UNHANDLED_SERVICEEVENT_TYPE = new SingleEsParameter("unhandled-serviceevent-type",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter UUID_FIELD = new SingleEsParameter("instance-uuid", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter TIMESTAMP_FIELD = new SingleEsParameter("@timestamp", ElasticsearchTypeRegistry.dateField);

    // --------------- OutgoingResponseEvent --------------//
    public static final AbstractEsParameter ORE_EXEC_TIME = new SingleEsParameter("outre-exec-time", ElasticsearchTypeRegistry.integerField);
    public static final AbstractEsParameter ORE_COUNT = new SingleEsParameter("outre-count", ElasticsearchTypeRegistry.longField);
    public static final AbstractEsParameter ORE_BYTES_WRITTEN = ObjectEsParameterFactory.bytesWritten("outre-bytes-written", null);

    // --------------- Iceland Exception --------------//
    public static final AbstractEsParameter EX_STATUS = new SingleEsParameter("exception-status", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter EX_VERSION = new SingleEsParameter("exception-version", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter EX_MESSAGE = new SingleEsParameter("exception-message", ElasticsearchTypeRegistry.stringField);

    // --------------- CodedException --------------//
    public static final AbstractEsParameter CEX_LOCATOR = new SingleEsParameter("codedexception-locator", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter CEX_SOAP_FAULT = new SingleEsParameter("codedexception-soapfault", ElasticsearchTypeRegistry.stringField);

    // --------------- OwsExceptionReport --------------//
    public static final AbstractEsParameter OWSEX_NAMESPACE = new SingleEsParameter("owsexception-namespace", ElasticsearchTypeRegistry.stringField);

    // ---------------- DEFAULT VALUES SERVICE REQUESTs--------------//
    public static final AbstractEsParameter SR_VERSION_FIELD = new SingleEsParameter("sr-version", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter SR_SERVICE_FIELD = new SingleEsParameter("sr-service", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter SR_LANGUAGE_FIELD = new SingleEsParameter("sr-language", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter SR_OPERATION_NAME_FIELD = new SingleEsParameter("sr-operation-name",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter SR_IP_ADDRESS_FIELD =
            new SingleEsParameter("sr-source-ip-address", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter SR_CONTENT_TYPE = new SingleEsParameter("sr-content-type", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter SR_ACCEPT_TYPES = new SingleEsParameter("sr-accept-types", ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter SR_GEO_LOC_FIELD = ObjectEsParameterFactory.geoLocation("sr-source-geolocation", null);
    public static final AbstractEsParameter SR_PROXIED_REQUEST_FIELD = new SingleEsParameter("sr-proxied-request",
            ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter SR_EXTENSIONS = ObjectEsParameterFactory.extension("sr-extensions", null);

    // --------------- DEFAULT RESPONSE EVENTS --------------//

    public static final AbstractEsParameter SRESP_CONTENT_TYPE = new SingleEsParameter("sresp-content-type", ElasticsearchTypeRegistry.stringField);

}
