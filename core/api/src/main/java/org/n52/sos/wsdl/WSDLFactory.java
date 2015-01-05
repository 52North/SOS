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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.n52.sos.binding.Binding;
import org.n52.sos.binding.BindingConstants;
import org.n52.sos.binding.BindingRepository;
import org.n52.sos.coding.OperationKey;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.HTTPException;
import org.n52.sos.request.operator.RequestOperator;
import org.n52.sos.request.operator.RequestOperatorKey;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.request.operator.WSDLAwareRequestOperator;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.ServiceConfiguration;
import org.n52.sos.util.Producer;

/**
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class WSDLFactory implements Producer<String> {
    @Override
    public String get() throws ConfigurationException {
        try {
            return getWSDL();
        } catch (final Exception ex) {
            throw new ConfigurationException(ex);
        }
    }

    @Override
    public String get(Locale language) {
        // No language support
        return get();
    }

    @Override
    public String get(String identification) {
        return get();
    }

    private String getWSDL() throws Exception {
        final WSDLBuilder builder = new WSDLBuilder();
        if (Configurator.getInstance() != null) {
            final Map<String, Binding> bindings = BindingRepository.getInstance().getBindings();
            final RequestOperatorRepository repo = RequestOperatorRepository.getInstance();

            final Set<RequestOperatorKey> requestOperators = repo.getActiveRequestOperatorKeys();

            final String serviceUrl = ServiceConfiguration.getInstance().getServiceURL();

            if (bindings.containsKey(BindingConstants.SOAP_BINDING_ENDPOINT)) {
                builder.setSoapEndpoint(URI.create(serviceUrl + BindingConstants.SOAP_BINDING_ENDPOINT));
                final Binding b = bindings.get(BindingConstants.SOAP_BINDING_ENDPOINT);
                for (final RequestOperatorKey o : requestOperators) {
                    final RequestOperator op = repo.getRequestOperator(o);
                    if (op instanceof WSDLAwareRequestOperator) {
                        final WSDLAwareRequestOperator wop = (WSDLAwareRequestOperator) op;
                        if (wop.getSosOperationDefinition() != null) {
                            if (isHttpPostSupported(b, wop)) {
                                builder.addSoapOperation(wop.getSosOperationDefinition());
                            }
                            addAdditionalPrefixes(wop, builder);
                            addAdditionalSchemaImports(wop, builder);
                        }
                    }
                }
            }
            if (bindings.containsKey(BindingConstants.POX_BINDING_ENDPOINT)) {
                builder.setPoxEndpoint(URI.create(serviceUrl + BindingConstants.POX_BINDING_ENDPOINT));
                final Binding b = bindings.get(BindingConstants.POX_BINDING_ENDPOINT);
                for (final RequestOperatorKey o : requestOperators) {
                    final RequestOperator op = repo.getRequestOperator(o);
                    if (op instanceof WSDLAwareRequestOperator) {
                        final WSDLAwareRequestOperator wop = (WSDLAwareRequestOperator) op;
                        if (wop.getSosOperationDefinition() != null) {
                            if (isHttpPostSupported(b, wop)) {
                                builder.addPoxOperation(wop.getSosOperationDefinition());
                            }
                            addAdditionalPrefixes(wop, builder);
                            addAdditionalSchemaImports(wop, builder);
                        }
                    }
                }
            }
            if (bindings.containsKey(BindingConstants.KVP_BINDING_ENDPOINT)) {
                builder.setKvpEndpoint(URI.create(serviceUrl + BindingConstants.KVP_BINDING_ENDPOINT + "?"));
                final Binding b = bindings.get(BindingConstants.KVP_BINDING_ENDPOINT);
                for (final RequestOperatorKey o : requestOperators) {
                    final RequestOperator op = repo.getRequestOperator(o);
                    if (op instanceof WSDLAwareRequestOperator) {
                        final WSDLAwareRequestOperator wop = (WSDLAwareRequestOperator) op;
                        if (wop.getSosOperationDefinition() != null) {
                            if (isHttpGetSupported(b, wop)) {
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

    private OperationKey toOperationKey(final RequestOperatorKey requestOperatorKeyType) {
        return new OperationKey(requestOperatorKeyType.getServiceOperatorKey().getService(), requestOperatorKeyType
                .getServiceOperatorKey().getVersion(), requestOperatorKeyType.getOperationName());
    }

    private void addAdditionalPrefixes(final WSDLAwareRequestOperator op, final WSDLBuilder builder) {
        final Map<String, String> additionalPrefixes = op.getAdditionalPrefixes();
        if (additionalPrefixes != null) {
            for (final Map.Entry<String, String> ap : additionalPrefixes.entrySet()) {
                builder.addNamespace(ap.getKey(), ap.getValue());
            }
        }
    }

    private void addAdditionalSchemaImports(final WSDLAwareRequestOperator op, final WSDLBuilder builder)
            throws Exception {
        final Map<String, String> additionalSchemaImports = op.getAdditionalSchemaImports();
        if (additionalSchemaImports != null) {
            for (final Map.Entry<String, String> as : additionalSchemaImports.entrySet()) {
                builder.addSchemaImport(as.getKey(), as.getValue());
            }
        }
    }

    private boolean isHttpPostSupported(final Binding b, final RequestOperator ro) throws HTTPException {
        return b.checkOperationHttpPostSupported(toOperationKey(ro.getRequestOperatorKeyType()));
    }

    private boolean isHttpGetSupported(final Binding b, final RequestOperator ro) throws HTTPException {
        return b.checkOperationHttpGetSupported(toOperationKey(ro.getRequestOperatorKeyType()));
    }
}
