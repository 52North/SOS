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
package org.n52.sos.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import org.n52.sos.binding.Binding;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.http.HTTPMethods;
import org.n52.sos.util.http.MediaType;

import com.google.common.base.Objects;

/**
 * @since 4.0.0
 *
 */
@Controller
@RequestMapping(ControllerConstants.Paths.CLIENT)
public class ClientController extends AbstractController {
    public static final String BINDINGS = "bindings";

    public static final String VERSIONS = "versions";

    public static final String OPERATIONS = "operations";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView get() {
        if (Configurator.getInstance() != null) {
            return new ModelAndView(ControllerConstants.Views.CLIENT, OPERATIONS, getAvailableOperations());
        } else {
            return new ModelAndView(ControllerConstants.Views.CLIENT);
        }
    }

    private List<AvailableOperation> getAvailableOperations() {
        final List<AvailableOperation> ops = new LinkedList<AvailableOperation>();
        for (RequestOperatorKey rokt : RequestOperatorRepository.getInstance().getActiveRequestOperatorKeys()) {
            final String service = rokt.getServiceOperatorKey().getService();
            final String version = rokt.getServiceOperatorKey().getVersion();
            final String operation = rokt.getOperationName();
            final OperationKey ok = new OperationKey(service, version, operation);
            for (Entry<String, Binding> b : BindingRepository.getInstance().getBindings().entrySet()) {
                try {
                    final String pattern = b.getKey();
                    final Binding binding = b.getValue();
                    if (binding.checkOperationHttpDeleteSupported(ok)) {
                        for (MediaType contentType : binding.getSupportedEncodings()) {
                            ops.add(new AvailableOperation(
                                    service, version, operation,
                                    contentType.toString(), HTTPMethods.DELETE));
                        }
                    }
                    if (binding.checkOperationHttpGetSupported(ok)) {
                        for (MediaType contentType : binding.getSupportedEncodings()) {
                            ops.add(new AvailableOperation(
                                    service, version, operation,
                                    contentType.toString(), HTTPMethods.GET));
                        }
                    }
                    if (binding.checkOperationHttpOptionsSupported(ok)) {
                        for (MediaType contentType : binding.getSupportedEncodings()) {
                            ops.add(new AvailableOperation(
                                    service, version, operation,
                                    contentType.toString(), HTTPMethods.OPTIONS));
                        }
                    }
                    if (binding.checkOperationHttpPostSupported(ok)) {
                        for (MediaType contentType : binding.getSupportedEncodings()) {
                            ops.add(new AvailableOperation(
                                    service, version, operation,
                                    contentType.toString(), HTTPMethods.POST));
                        }
                    }
                    if (binding.checkOperationHttpPutSupported(ok)) {
                        for (MediaType contentType : binding.getSupportedEncodings()) {
                            ops.add(new AvailableOperation(
                                    service, version, operation,
                                    contentType.toString(), HTTPMethods.PUT));
                        }
                    }
                } catch (HTTPException ex) {
                    /* ignore */
                }
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

        public AvailableOperation(String service, String version, String operation, String contentType, String method) {
            this.service = service;
            this.version = version;
            this.operation = operation;
            this.contentType = contentType;
            this.method = method;
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
            return Objects.hashCode(getMethod(), getService(), getVersion(), getOperation(), getContentType());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof AvailableOperation) {
                AvailableOperation other = (AvailableOperation) obj;
                return Objects.equal(getMethod(), other.getMethod())
                        && Objects.equal(getService(), other.getService())
                        && Objects.equal(getVersion(), other.getVersion())
                        && Objects.equal(getOperation(), other.getOperation())
                        && Objects.equal(getContentType(), other.getContentType());
            }
            return false;
        }
    }
}
