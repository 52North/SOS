/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.web.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.exception.HTTPException;
import org.n52.iceland.request.operator.RequestOperatorKey;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.janmayen.http.HTTPMethods;
import org.n52.janmayen.http.MediaType;
import org.n52.shetland.ogc.ows.service.OwsOperationKey;
import org.n52.sos.context.ContextSwitcher;
import org.n52.sos.web.common.AbstractController;
import org.n52.sos.web.common.ControllerConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @since 4.0.0
 *
 */
@Controller
@RequestMapping(ControllerConstants.Paths.CLIENT)
public class ClientController
        extends
        AbstractController {
    public static final String BINDINGS = "bindings";
    public static final String VERSIONS = "versions";
    public static final String OPERATIONS = "operations";

    @Inject
    private ContextSwitcher contextSwitcher;

    @Inject
    private Optional<RequestOperatorRepository> requestOperatorRepository = Optional.empty();

    @Inject
    private Optional<BindingRepository> bindingRepository = Optional.empty();

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView get() {
        if (contextSwitcher.isConfigured()) {
            return new ModelAndView(ControllerConstants.Views.CLIENT, OPERATIONS, getAvailableOperations());
        } else {
            return new ModelAndView(ControllerConstants.Views.CLIENT);
        }
    }

    private List<AvailableOperation> getAvailableOperations() {
        final List<AvailableOperation> ops = new LinkedList<>();
        if (this.requestOperatorRepository.isPresent() && this.bindingRepository.isPresent()) {
            for (RequestOperatorKey rokt : this.requestOperatorRepository.get().getActiveRequestOperatorKeys()) {
                String service = rokt.getServiceOperatorKey().getService();
                String version = rokt.getServiceOperatorKey().getVersion();
                String operation = rokt.getOperationName();
                OwsOperationKey ok = new OwsOperationKey(service, version, operation);
                this.bindingRepository.get().getBindingsByMediaType().forEach((mediaType, binding) -> {
                    try {
                        if (binding.checkOperationHttpDeleteSupported(ok)) {
                            ops.add(new AvailableOperation(ok, mediaType, HTTPMethods.DELETE));
                        }
                        if (binding.checkOperationHttpGetSupported(ok)) {
                            ops.add(new AvailableOperation(ok, mediaType, HTTPMethods.GET));
                        }
                        if (binding.checkOperationHttpOptionsSupported(ok)) {
                            ops.add(new AvailableOperation(ok, mediaType, HTTPMethods.OPTIONS));
                        }
                        if (binding.checkOperationHttpPostSupported(ok)) {
                            ops.add(new AvailableOperation(ok, mediaType, HTTPMethods.POST));
                        }
                        if (binding.checkOperationHttpPutSupported(ok)) {
                            ops.add(new AvailableOperation(ok, mediaType, HTTPMethods.PUT));
                        }
                    } catch (HTTPException ex) {
                        /* ignore */
                    }
                });
            }
        }
        return ops;
    }

    public static class AvailableOperation {
        private final String service;
        private final String version;
        private final String operation;
        private final String contentType;
        private final String method;

        public AvailableOperation(
                String service, String version, String operation, String contentType, String method) {
            this.service = service;
            this.version = version;
            this.operation = operation;
            this.contentType = contentType;
            this.method = method;
        }

        public AvailableOperation(String service, String version, String operation,
                                  MediaType contentType, String method) {
            this(service, version, operation, contentType.toString(), method);
        }

        public AvailableOperation(OwsOperationKey key, MediaType contentType, String method) {
            this(key.getService(), key.getVersion(), key.getOperation(), contentType.toString(), method);
        }

        public String getService() {
            return service;
        }

        public String getVersion() {
            return version;
        }

        public String getOperation() {
            return operation;
        }

        public String getContentType() {
            return contentType;
        }

        public String getMethod() {
            return method;
        }

        @Override
        public String toString() {
            return String.format("AvailableOperation[method=%s, service=%s, version=%s, operation=%s, contentType=%s]",
                    getMethod(), getService(), getVersion(), getOperation(), getContentType());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getMethod(), getService(), getVersion(), getOperation(), getContentType());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AvailableOperation) {
                AvailableOperation other = (AvailableOperation) obj;
                return Objects.equals(getMethod(), other.getMethod())
                        && Objects.equals(getService(), other.getService())
                        && Objects.equals(getVersion(), other.getVersion())
                        && Objects.equals(getOperation(), other.getOperation())
                        && Objects.equals(getContentType(), other.getContentType());
            }
            return false;
        }
    }
}
