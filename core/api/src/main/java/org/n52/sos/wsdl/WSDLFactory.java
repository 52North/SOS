/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.Validation;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.binding.Binding;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.exception.HTTPException;
import org.n52.iceland.request.operator.RequestOperator;
import org.n52.iceland.request.operator.RequestOperatorKey;
import org.n52.iceland.request.operator.RequestOperatorRepository;
import org.n52.iceland.service.ServiceSettings;
import org.n52.janmayen.Producer;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.service.OwsOperationKey;
import org.n52.sos.request.operator.WSDLAwareRequestOperator;
import org.n52.svalbard.encode.EncoderRepository;

/**
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
@Configurable
public class WSDLFactory implements Producer<String> {

    @Inject
    private RequestOperatorRepository requestOperatorRepository;

    @Inject
    private BindingRepository bindingRepository;

    @Inject
    private EncoderRepository encoderRepository;

    private String serviceURL;

    @Override
    public String get() throws ConfigurationError {
        try {
            return getWSDL();
        } catch (ConfigurationError ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ConfigurationError(ex);
        }
    }

    private String getWSDL() throws Exception {
        final WSDLBuilder builder = new WSDLBuilder(encoderRepository);
        if (isConfigured()) {
            final RequestOperatorRepository repo = getRequestOperatorRepository();
            final Set<RequestOperatorKey> requestOperators = repo.getActiveRequestOperatorKeys();
            final String serviceUrl = getServiceURL();

            Binding binding;

            binding = getBindingRepository().getBinding(MediaTypes.APPLICATION_SOAP_XML);
            if (binding != null) {
                builder.setSoapEndpoint(URI.create(serviceUrl));
                for (final RequestOperatorKey o : requestOperators) {
                    final RequestOperator op = repo.getRequestOperator(o);
                    if (op instanceof WSDLAwareRequestOperator) {
                        final WSDLAwareRequestOperator wop = (WSDLAwareRequestOperator) op;
                        if (wop.getSosOperationDefinition() != null) {
                            if (isHttpPostSupported(binding, wop)) {
                                builder.addSoapOperation(wop.getSosOperationDefinition());
                            }
                            addAdditionalPrefixes(wop, builder);
                            addAdditionalSchemaImports(wop, builder);
                        }
                    }
                }
            }

            binding = getBindingRepository().getBinding(MediaTypes.APPLICATION_XML);
            if (binding != null) {
                builder.setPoxEndpoint(URI.create(serviceUrl));
                for (final RequestOperatorKey o : requestOperators) {
                    final RequestOperator op = repo.getRequestOperator(o);
                    if (op instanceof WSDLAwareRequestOperator) {
                        final WSDLAwareRequestOperator wop = (WSDLAwareRequestOperator) op;
                        if (wop.getSosOperationDefinition() != null) {
                            if (isHttpPostSupported(binding, wop)) {
                                builder.addPoxOperation(wop.getSosOperationDefinition());
                            }
                            addAdditionalPrefixes(wop, builder);
                            addAdditionalSchemaImports(wop, builder);
                        }
                    }
                }
            }

            binding = getBindingRepository().getBinding(MediaTypes.APPLICATION_KVP);
            if (binding != null) {
                builder.setKvpEndpoint(URI.create(serviceUrl + "?"));
                for (final RequestOperatorKey o : requestOperators) {
                    final RequestOperator op = repo.getRequestOperator(o);
                    if (op instanceof WSDLAwareRequestOperator) {
                        final WSDLAwareRequestOperator wop = (WSDLAwareRequestOperator) op;
                        if (wop.getSosOperationDefinition() != null) {
                            if (isHttpGetSupported(binding, wop)) {
                                builder.addKvpOperation(wop.getSosOperationDefinition());
                            }
                            addAdditionalPrefixes(wop, builder);
                            addAdditionalSchemaImports(wop, builder);
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    private OwsOperationKey toOperationKey(RequestOperatorKey requestOperatorKeyType) {
        return new OwsOperationKey(requestOperatorKeyType.getServiceOperatorKey()
                .getService(),
                requestOperatorKeyType.getServiceOperatorKey()
                        .getVersion(),
                requestOperatorKeyType.getOperationName());
    }

    private void addAdditionalPrefixes(final WSDLAwareRequestOperator op, final WSDLBuilder builder) {
        final Map<String, String> additionalPrefixes = op.getAdditionalPrefixes();
        if (additionalPrefixes != null) {
            additionalPrefixes.forEach(builder::addNamespace);
        }
    }

    private void addAdditionalSchemaImports(final WSDLAwareRequestOperator op, final WSDLBuilder builder)
            throws Exception {
        final Map<String, String> additionalSchemaImports = op.getAdditionalSchemaImports();
        if (additionalSchemaImports != null) {
            additionalSchemaImports.forEach(builder::addSchemaImport);
        }
    }

    private boolean isHttpPostSupported(final Binding b, final RequestOperator ro) throws HTTPException {
        return ro.getKeys()
                .stream()
                .map(this::toOperationKey)
                .anyMatch(k -> {
                    try {
                        return b.checkOperationHttpPostSupported(k);
                    } catch (HTTPException ex) {
                        throw new ConfigurationError(ex);
                    }
                });
    }

    private boolean isHttpGetSupported(final Binding b, final RequestOperator ro) throws HTTPException {
        return ro.getKeys()
                .stream()
                .map(this::toOperationKey)
                .anyMatch(k -> {
                    try {
                        return b.checkOperationHttpGetSupported(k);
                    } catch (HTTPException ex) {
                        throw new ConfigurationError(ex);
                    }
                });
    }

    private RequestOperatorRepository getRequestOperatorRepository() {
        return requestOperatorRepository;
    }

    private BindingRepository getBindingRepository() {
        return bindingRepository;
    }

    private String getServiceURL() {
        return serviceURL;
    }

    @Setting(ServiceSettings.SERVICE_URL)
    public void setServiceURL(final URI serviceURL) throws ConfigurationError {
        Validation.notNull("Service URL", serviceURL);
        String url = serviceURL.toString();
        if (url.contains("?")) {
            url = url.split("[?]")[0];
        }
        this.serviceURL = url;
    }

    private boolean isConfigured() {
        return getRequestOperatorRepository() != null && getBindingRepository() != null && getServiceURL() != null
                && !getServiceURL().isEmpty();
    }
}
