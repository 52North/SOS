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

public class ServiceEventDataMapping {
    public static final String UNHANDLED_SERVICEEVENT_TYPE = "unhandled-serviceevent-type";
    public static final String UUID_FIELD = "instance-uuid";
    public static final String TIMESTAMP_FIELD = "@timestamp";

    // ---- METADATA type ----
    public static final String METADATA_TYPE_NAME = "mt";
    public static final String METADATA_CREATION_TIME_FIELD = "mt-creation-time";
    public static final String METADATA_UPDATE_TIME_FIELD = "mt-update-time";
    public static final String METADATA_VERSION_FIELD = "mt-version";
    public static final String METADATA_UUIDS_FIELD = "mt-uuids";
    public static final String METADATA_ROW_ID = "1";

    // --------------- OutgoingResponseEvent --------------//
    public static final String ORE_EXEC_TIME = "outre-exec-time";
    public static final String ORE_COUNT = "outre-count";
    public static final String ORE_BYTES_WRITTEN = "outre-bytes-written";

    // --------------- Iceland Exception --------------//
    public static final String EX_STATUS = "exception-status";
    public static final String EX_VERSION = "exception-version";
    public static final String EX_MESSAGE = "exception-message";

    // --------------- CodedException --------------//
    public static final String CEX_LOCATOR = "codedexception-locator";
    public static final String CEX_SOAP_FAULT = "codedexception-soapfault";

    // --------------- OwsExceptionReport --------------//
    public static final String OWSEX_NAMESPACE = "owsexception-namespace";

    // ---------------- DEFAULT VALUES SERVICE REQUESTs--------------//
    public static final String SR_VERSION_FIELD = "sr-version";
    public static final String SR_SERVICE_FIELD = "sr-service";
    public static final String SR_LANGUAGE_FIELD = "sr-language";
    public static final String SR_OPERATION_NAME_FIELD = "sr-operation-name";
    public static final String SR_IP_ADDRESS_FIELD = "sr-source-ip-address";
    public static final String SR_CONTENT_TYPE = "sr-content-type";
    public static final String SR_ACCEPT_TYPES = "sr-accept-types";
    public static final String SR_GEO_LOC_FIELD = "sr-source-geolocation";
    // -------------- START SUB FIELD ---------------------------------//
    public static final String GEO_LOC_COUNTRY_CODE = "country-code";
    public static final String GEO_LOC_CITY_CODE = "city-name";
    public static final String GEO_LOC_GEOPOINT = "geopoint";
    // -------------- SUB FIELD ---------------- //
    public static final String SR_PROXIED_REQUEST_FIELD = "sr-proxied-request";
    public static final String SR_EXTENSIONS = "sr-extensions";

    // --------------- EXTENSIONS -------------//
    public static final String EXT_DEFINITION = "extension-definition";
    public static final String EXT_IDENTIFIER = "extension-identifier";
    public static final String EXT_VALUE = "extension-value";
    
    
    //----------------- BYTES WRITTEN -----------//
    public static final String BYTES_WRITTEN = "bytes";
    public static final String BYTES_WRITTEN_DISPLAY = "display";

}
