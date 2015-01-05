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
package org.n52.sos.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.binding.Binding;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.event.SosEventBus;
import org.n52.sos.event.events.ExceptionEvent;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.util.http.HTTPHeaders;
import org.n52.sos.util.http.HTTPMethods;
import org.n52.sos.util.http.HTTPStatus;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.util.http.MediaTypes;

/**
 * The servlet of the SOS which receives the incoming HttpPost and HttpGet
 * requests and sends the operation result documents to the client TODO review
 * exception handling
 *
 * @since 4.0.0
 */
public class SosService extends ConfiguratedHttpServlet {
    private static final long serialVersionUID = -2103692310137045855L;

    private static final Logger LOGGER = LoggerFactory.getLogger(SosService.class);

    public static final String BINDING_DELETE_METHOD = "doDeleteOperation";

    public static final String BINDING_PUT_METHOD = "doPutOperation";

    public static final String BINDING_POST_METHOD = "doPostOperation";

    public static final String BINDING_GET_METHOD = "doGetOperation";

    private static final AtomicLong counter = new AtomicLong(0);

    @Override
    public void init() throws ServletException {
        LOGGER.info("SOS endpoint initalized successfully!");
    }

    protected HttpServletRequest logRequest(HttpServletRequest request, long count) {
        if (LOGGER.isDebugEnabled()) {
            Enumeration<?> headerNames = request.getHeaderNames();
            StringBuilder headers = new StringBuilder();
            while (headerNames.hasMoreElements()) {
                String name = (String) headerNames.nextElement();
                headers.append("> ").append(name).append(": ").append(request.getHeader(name)).append("\n");
            }
            LOGGER.debug("Incoming request No. {}:\n> [{} {} {}] from {} {}\n{}", count, request.getMethod(),
                    request.getRequestURI(), request.getProtocol(), request.getRemoteAddr(), request.getRemoteHost(),
                    headers);
        }
        return request;
    }

    private void logResponse(HttpServletResponse response, long count, long start) {
        long duration = System.currentTimeMillis() - start;
        LOGGER.debug("Outgoing response for request No. {} is committed = {} (took {}ms)", count, response.isCommitted(), duration);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        long start = System.currentTimeMillis();
        long currentCount = counter.incrementAndGet();
        logRequest(request, currentCount);
        try {
            getBinding(request).doDeleteOperation(request, response);
        } catch (HTTPException exception) {
            onHttpException(request, response, exception);
        } finally {
            logResponse(response, currentCount, start);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        long start = System.currentTimeMillis();
        long currentCount = counter.incrementAndGet();
        logRequest(request, currentCount);
        try {
            getBinding(request).doGetOperation(request, response);
        } catch (HTTPException exception) {
            onHttpException(request, response, exception);
        } finally {
            logResponse(response, currentCount, start);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        long start = System.currentTimeMillis();
        long currentCount = counter.incrementAndGet();
        logRequest(request, currentCount);
        try {
            getBinding(request).doPostOperation(request, response);
        } catch (HTTPException exception) {
            onHttpException(request, response, exception);
        } finally {
            logResponse(response, currentCount, start);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        long start = System.currentTimeMillis();
        long currentCount = counter.incrementAndGet();
        logRequest(request, currentCount);
        try {
            getBinding(request).doPutOperation(request, response);
        } catch (HTTPException exception) {
            onHttpException(request, response, exception);
        } finally {
            logResponse(response, currentCount, start);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        long start = System.currentTimeMillis();
        long currentCount = counter.incrementAndGet();
        logRequest(request, currentCount);
        Binding binding = null;
        try {

            binding = getBinding(request);
            binding.doOptionsOperation(request, response);
        } catch (HTTPException exception) {
            if (exception.getStatus() == HTTPStatus.METHOD_NOT_ALLOWED) {
                if (binding != null) {
                    doDefaultOptions(binding, request, response);
                } else {
                    super.doOptions(request, response);
                }
            } else {
                onHttpException(request, response, exception);
            }
        } finally {
            logResponse(response, currentCount, start);
        }
    }

    /**
     * Get the implementation of {@link Binding} that is registered for the
     * given <code>request</code>.
     *
     * @param request
     *            URL pattern from request URL
     *
     * @return The implementation of {@link Binding} that is registered for the
     *         given <code>urlPattern</code>.
     *
     *
     * @throws HTTPException If the URL pattern or ContentType is not supported
     *                       by this SOS.
     */
    private Binding getBinding(HttpServletRequest request) throws HTTPException {
        final String requestURI = request.getPathInfo();
        final BindingRepository repo = BindingRepository.getInstance();
        if (requestURI == null || requestURI.isEmpty() || requestURI.equals("/")) {
            MediaType contentType = getContentType(request);
            // strip of the parameters to get rid of things like encoding
            Binding binding = repo.getBinding(contentType.withoutParameters());
            if (binding == null) {
                throw new HTTPException(HTTPStatus.UNSUPPORTED_MEDIA_TYPE);
            } else {
                return binding;
            }
        }

        for (String prefix : repo.getBindings().keySet()) {
            if (requestURI.startsWith(prefix)) {
                return repo.getBinding(prefix);
            }
        }
        throw new HTTPException(HTTPStatus.NOT_FOUND);
    }

    private MediaType getContentType(HttpServletRequest request)
            throws HTTPException {
        if (request.getContentType() == null) {
            // default to KVP for GET requests
            if (request.getMethod().equals(HTTPMethods.GET)) {
                return MediaTypes.APPLICATION_KVP;
            } else {
                throw new HTTPException(HTTPStatus.BAD_REQUEST);
            }
        } else {
            try {
                return MediaType.parse(request.getContentType());
            } catch (IllegalArgumentException e) {
                throw new HTTPException(HTTPStatus.BAD_REQUEST, e);
            }
        }
    }

    protected void onHttpException(HttpServletRequest request, HttpServletResponse response, HTTPException exception)
            throws IOException {
        SosEventBus.fire(new ExceptionEvent(exception));
        response.sendError(exception.getStatus().getCode(), exception.getMessage());
    }

    protected void doDefaultOptions(Binding binding, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        Set<String> methods = getDeclaredBindingMethods(binding.getClass());
        StringBuilder allow = new StringBuilder();
        if (methods.contains(BINDING_GET_METHOD)) {
            allow.append(HTTPMethods.GET);
            allow.append(", ");
            allow.append(HTTPMethods.HEAD);
        }
        if (methods.contains(BINDING_POST_METHOD)) {
            if (allow.length() != 0) {
                allow.append(", ");
            }
            allow.append(HTTPMethods.POST);
        }
        if (methods.contains(BINDING_PUT_METHOD)) {
            if (allow.length() != 0) {
                allow.append(", ");
            }
            allow.append(HTTPMethods.PUT);
        }
        if (methods.contains(BINDING_DELETE_METHOD)) {
            if (allow.length() != 0) {
                allow.append(", ");
            }
            allow.append(HTTPMethods.DELETE);
        }

        if (allow.length() != 0) {
            allow.append(", ");
        }
        allow.append(HTTPMethods.TRACE);
        allow.append(", ");
        allow.append(HTTPMethods.OPTIONS);
        response.setHeader(HTTPHeaders.ALLOW, allow.toString());
    }

    private Set<String> getDeclaredBindingMethods(Class<?> c) {
        if (c.equals(Binding.class)) {
            return new HashSet<String>();
        } else {
            Set<String> parent = getDeclaredBindingMethods(c.getSuperclass());
            for (Method m : c.getDeclaredMethods()) {
                parent.add(m.getName());
            }
            return parent;
        }
    }
}
