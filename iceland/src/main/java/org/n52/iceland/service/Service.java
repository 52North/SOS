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
package org.n52.iceland.service;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.iceland.binding.Binding;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.event.ServiceEventBus;
import org.n52.iceland.event.events.ExceptionEvent;
import org.n52.iceland.exception.HTTPException;
import org.n52.iceland.util.http.HTTPHeaders;
import org.n52.iceland.util.http.HTTPMethods;
import org.n52.iceland.util.http.HTTPStatus;
import org.n52.iceland.util.http.MediaType;
import org.n52.iceland.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The servlet of the Service which receives the incoming HttpPost and HttpGet
 * requests and sends the operation result documents to the client TODO review
 * exception handling
 *
 * @since 4.0.0
 */
public class Service extends ConfiguratedHttpServlet {
    private static final long serialVersionUID = -2103692310137045855L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

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
        ServiceEventBus.fire(new ExceptionEvent(exception));
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
