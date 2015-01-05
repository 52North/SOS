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
package org.n52.sos.encode.json.impl;

import static org.n52.sos.coding.json.JSONConstants.CODE;
import static org.n52.sos.coding.json.JSONConstants.EXCEPTIONS;
import static org.n52.sos.coding.json.JSONConstants.LOCATOR;
import static org.n52.sos.coding.json.JSONConstants.TEXT;
import static org.n52.sos.coding.json.JSONConstants.VERSION;

import org.n52.sos.encode.ExceptionEncoderKey;
import org.n52.sos.encode.json.JSONEncoder;
import org.n52.sos.encode.json.JSONEncodingException;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.OwsExceptionCode;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.JSONUtils;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public class OwsExceptionReportEncoder extends JSONEncoder<OwsExceptionReport> {
    private static final Logger log = LoggerFactory.getLogger(OwsExceptionReportEncoder.class);

    public static final char LF = '\n';

    public static final String LINE = "line";

    public static final String CLASS = "class";

    public static final String FILE = "file";

    public static final String METHOD = "method";

    public static final String STACK_TRACE = "stackTrace";

    public OwsExceptionReportEncoder() {
        super(OwsExceptionReport.class, new ExceptionEncoderKey(MediaTypes.APPLICATION_JSON));
    }

    @Override
    public JsonNode encodeJSON(OwsExceptionReport t) throws JSONEncodingException {
        final ObjectNode exceptionReport = JSONUtils.nodeFactory().objectNode();
        exceptionReport.put(VERSION, t.getVersion());
        final ArrayNode exceptions = exceptionReport.putArray(EXCEPTIONS);
        for (CodedException ce : t.getExceptions()) {
            final ObjectNode exception = exceptions.addObject();
            exception.put(CODE,
                    ce.getCode() != null ? ce.getCode().toString() : OwsExceptionCode.NoApplicableCode.toString());
            if (ce.getLocator() != null && !ce.getLocator().isEmpty()) {
                exception.put(LOCATOR, ce.getLocator());
            }
            final String message = getExceptionText(ce);
            if (message != null && !message.isEmpty()) {
                exception.put(TEXT, message);
            }
            if (log.isDebugEnabled()) {
                exception.put(STACK_TRACE, encodeStackTrace(ce));
            }
        }
        return exceptionReport;
    }

    protected String getExceptionText(CodedException ce) {
        final StringBuilder exceptionText = new StringBuilder();
        if (ce.getMessage() != null) {
            exceptionText.append(ce.getMessage());
        }
        if (ce.getCause() != null) {
            if (exceptionText.length() > 0) {
                exceptionText.append(LF);
            }
            exceptionText.append("[EXEPTION]: ").append(LF);
            String localizedMessage = ce.getCause().getLocalizedMessage();
            String message = ce.getCause().getMessage();
            if (localizedMessage != null && message != null) {
                if (!message.equals(localizedMessage)) {
                    exceptionText.append(message).append(LF);
                }
                exceptionText.append(localizedMessage).append(LF);
            } else {
                exceptionText.append(localizedMessage).append(LF);
                exceptionText.append(message).append(LF);
            }
        }
        return exceptionText.toString();
    }

    private JsonNode encodeStackTrace(Throwable t) {
        ArrayNode json = nodeFactory().arrayNode();
        for (StackTraceElement e : t.getStackTrace()) {
            json.add(String.format("%s.%s(:%d)", e.getClassName(), e.getMethodName(), e.getLineNumber()));
        }
        return json;
    }
}
