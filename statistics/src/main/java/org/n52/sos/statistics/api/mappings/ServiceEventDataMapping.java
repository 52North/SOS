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
package org.n52.sos.statistics.api.mappings;

import org.n52.sos.statistics.api.parameters.AbstractEsParameter;
import org.n52.sos.statistics.api.parameters.Description;
import org.n52.sos.statistics.api.parameters.Description.InformationOrigin;
import org.n52.sos.statistics.api.parameters.Description.Operation;
import org.n52.sos.statistics.api.parameters.ElasticsearchTypeRegistry;
import org.n52.sos.statistics.api.parameters.ObjectEsParameterFactory;
import org.n52.sos.statistics.api.parameters.SingleEsParameter;

public class ServiceEventDataMapping {
    public static final AbstractEsParameter UNHANDLED_SERVICEEVENT_TYPE = new SingleEsParameter("unhandled-serviceevent-type", new Description(
            InformationOrigin.Computed, Operation.Default,
            "If no processing handler is defined this field stores the Java class full name of the event"), ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter UUID_FIELD = new SingleEsParameter("instance-uuid", new Description(InformationOrigin.Computed,
            Operation.Default, "Unique ID of the instance who stored the event"), ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter TIMESTAMP_FIELD = new SingleEsParameter("@timestamp", new Description(InformationOrigin.Computed,
            Operation.Default, "UTC timestamp of the event insertion"), ElasticsearchTypeRegistry.dateField);

    // --------------- OutgoingResponseEvent --------------//
    public static final AbstractEsParameter ORE_EXEC_TIME = new SingleEsParameter("outre-exec-time", new Description(
            InformationOrigin.OutgoingResponseEvent, Operation.Default, "The execution time of processing the request-response"),
            ElasticsearchTypeRegistry.integerField);

    public static final AbstractEsParameter ORE_COUNT = new SingleEsParameter("outre-count", new Description(InformationOrigin.OutgoingResponseEvent,
            Operation.Default, "An incremental number since the start of the webapplication. This field indicates the serial number of the request"),
            ElasticsearchTypeRegistry.longField);

    public static final AbstractEsParameter ORE_BYTES_WRITTEN = ObjectEsParameterFactory.bytesWritten("outre-bytes-written", new Description(
            InformationOrigin.OutgoingResponseEvent, Operation.Default, "Size of the response document"));

    // --------------- Iceland Exception --------------//
    public static final AbstractEsParameter EX_STATUS = new SingleEsParameter("exception-status", new Description(InformationOrigin.ExceptionEvent,
            Operation.Default, "HTTP status of the exception if any"), ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter EX_VERSION = new SingleEsParameter("exception-version", new Description(InformationOrigin.ExceptionEvent,
            Operation.Default, "Version of the exception"), ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter EX_MESSAGE = new SingleEsParameter("exception-message", new Description(InformationOrigin.ExceptionEvent,
            Operation.Default, "Text of the exception"), ElasticsearchTypeRegistry.stringField);

    // --------------- CodedException --------------//
    public static final AbstractEsParameter CEX_LOCATOR = new SingleEsParameter("codedexception-locator", new Description(
            InformationOrigin.ExceptionEvent, Operation.Default, "CodedException locator"), ElasticsearchTypeRegistry.stringField);
    public static final AbstractEsParameter CEX_SOAP_FAULT = new SingleEsParameter("codedexception-soapfault", new Description(
            InformationOrigin.ExceptionEvent, Operation.Default, "CodedException SOAP fault message"), ElasticsearchTypeRegistry.stringField);

    // --------------- OwsExceptionReport --------------//
    public static final AbstractEsParameter OWSEX_NAMESPACE = new SingleEsParameter("owsexception-namespace", new Description(
            InformationOrigin.ExceptionEvent, Operation.Default, "OWSException namespace"), ElasticsearchTypeRegistry.stringField);

    // ---------------- DEFAULT VALUES SERVICE REQUESTs--------------//
    public static final AbstractEsParameter SR_VERSION_FIELD = new SingleEsParameter("sr-version", new Description(InformationOrigin.RequestEvent,
            Operation.Default, "Version of the deployment"), ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter SR_SERVICE_FIELD = new SingleEsParameter("sr-service", new Description(InformationOrigin.RequestEvent,
            Operation.Default, "Name of deployment. E.g: SOS"), ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter SR_LANGUAGE_FIELD = new SingleEsParameter("sr-language", new Description(InformationOrigin.RequestEvent,
            Operation.Default, "Language of the deployment if specified"), ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter SR_OPERATION_NAME_FIELD = new SingleEsParameter("sr-operation-name", new Description(
            InformationOrigin.RequestEvent, Operation.Default, "Name of the requested operation. E.g: GetCapabilities"),
            ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter SR_IP_ADDRESS_FIELD = new SingleEsParameter("sr-source-ip-address", new Description(
            InformationOrigin.Computed, Operation.Default, "Source IP address of the client proxies are stripped away"),
            ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter SR_CONTENT_TYPE = new SingleEsParameter("sr-content-type", new Description(InformationOrigin.Computed,
            Operation.Default, "Content type of the request"), ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter SR_ACCEPT_TYPES = new SingleEsParameter("sr-accept-types", new Description(InformationOrigin.Computed,
            Operation.Default, "Accept type of the request"), ElasticsearchTypeRegistry.stringField);

    public static final AbstractEsParameter SR_GEO_LOC_FIELD = ObjectEsParameterFactory.geoLocation("sr-source-geolocation", new Description(
            InformationOrigin.Computed, Operation.Default,
            "Based on the IP address if this feature is enabled the latitude and longitude coordinates are computed"));

    public static final AbstractEsParameter SR_PROXIED_REQUEST_FIELD = new SingleEsParameter("sr-proxied-request", new Description(
            InformationOrigin.Computed, Operation.Default, "Is the request came through a proxy or proxies"), ElasticsearchTypeRegistry.booleanField);

    public static final AbstractEsParameter SR_EXTENSIONS = ObjectEsParameterFactory.extension("sr-extensions", new Description(
            InformationOrigin.Computed, Operation.Default, "Extensions"));

    // --------------- DEFAULT RESPONSE EVENTS --------------//

    public static final AbstractEsParameter SRESP_CONTENT_TYPE = new SingleEsParameter("sresp-content-type", new Description(
            InformationOrigin.ResponseEvent, Operation.Default, "Response content type"), ElasticsearchTypeRegistry.stringField);

}
