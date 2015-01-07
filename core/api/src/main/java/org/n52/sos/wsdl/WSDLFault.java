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
package org.n52.sos.wsdl;

import java.net.URI;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

/**
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class WSDLFault {
    
    public static final WSDLFault EXCEPTION_MESSAGE = new WSDLFault("ExceptionMessage",
            WSDLConstants.OWS_EXCEPTION_ACTION);

    public static final WSDLFault REQUEST_EXTENSION_NOT_SUPPORTED_EXCEPTION = new WSDLFault(
            "RequestExtensionNotSupportedException", WSDLConstants.SWES_EXCEPTION_ACTION);

    public static final WSDLFault INVALID_REQUEST_EXCEPTION = new WSDLFault("InvalidRequestException",
            WSDLConstants.SWES_EXCEPTION_ACTION);

    public static final WSDLFault NO_APPLICABLE_CODE_EXCEPTION = new WSDLFault("NoApplicableCodeException",
            WSDLConstants.OWS_EXCEPTION_ACTION);

    public static final WSDLFault INVALID_UPDATE_SEQUENCE_EXCEPTION = new WSDLFault("InvalidUpdateSequenceException",
            WSDLConstants.OWS_EXCEPTION_ACTION);

    public static final WSDLFault VERSION_NEGOTIATION_FAILED_EXCEPTION = new WSDLFault(
            "VersionNegotiationFailedException", WSDLConstants.OWS_EXCEPTION_ACTION);

    public static final WSDLFault MISSING_PARAMETER_VALUE_EXCEPTION = new WSDLFault("MissingParameterValueException",
            WSDLConstants.OWS_EXCEPTION_ACTION);

    public static final WSDLFault INVALID_PARAMETER_VALUE_EXCEPTION = new WSDLFault("InvalidParameterValueException",
            WSDLConstants.OWS_EXCEPTION_ACTION);

    public static final WSDLFault OPERATION_NOT_SUPPORTED_EXCEPTION = new WSDLFault("OperationNotSupportedException",
            WSDLConstants.OWS_EXCEPTION_ACTION);

//    public static final Collection<WSDLFault> DEFAULT_FAULTS = ImmutableList.of(MISSING_PARAMETER_VALUE_EXCEPTION,
//            INVALID_PARAMETER_VALUE_EXCEPTION, OPERATION_NOT_SUPPORTED_EXCEPTION, NO_APPLICABLE_CODE_EXCEPTION,
//            INVALID_REQUEST_EXCEPTION, REQUEST_EXTENSION_NOT_SUPPORTED_EXCEPTION);
    
    public static final Collection<WSDLFault> DEFAULT_FAULTS = ImmutableList.of(EXCEPTION_MESSAGE);

    private final String name;

    private final URI action;

    public WSDLFault(String name, URI action) {
        this.name = name;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public URI getAction() {
        return action;
    }
}
