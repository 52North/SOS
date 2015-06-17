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
package org.n52.sos.coding.encode;


import org.n52.sos.coding.encode.ObservationEncoder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.lifecycle.Constructable;
import org.n52.iceland.service.operator.ServiceOperatorKey;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.iceland.util.activation.ActivationListener;
import org.n52.iceland.util.activation.ActivationListeners;
import org.n52.iceland.util.activation.ActivationManager;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.n52.iceland.coding.encode.Encoder;
import org.n52.iceland.coding.encode.EncoderRepository;
import org.n52.iceland.coding.encode.ResponseFormatKey;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ResponseFormatRepository implements ActivationManager<ResponseFormatKey>, Constructable {
    @Deprecated
    private static ResponseFormatRepository instance;
    private final Map<String, Map<String, Set<String>>> responseFormats = Maps.newHashMap();
    private final ActivationListeners<ResponseFormatKey> activation = new ActivationListeners<>(true);

    private ServiceOperatorRepository serviceOperatorRepository;
    private EncoderRepository encoderRepository;

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Inject
    public void setServiceOperatorRepository(ServiceOperatorRepository serviceOperatorRepository) {
        this.serviceOperatorRepository = serviceOperatorRepository;
    }

    @Override
    public void init() {
        ResponseFormatRepository.instance = this;
        generateResponseFormatMaps();
    }

    private void generateResponseFormatMaps() {
        this.responseFormats.clear();
        Set<ServiceOperatorKey> serviceOperatorKeyTypes
                = getServiceOperatorKeys();

        for (Encoder<?, ?> encoder : this.encoderRepository.getEncoders()) {
            if (encoder instanceof ObservationEncoder) {
                ObservationEncoder<?, ?> observationEncoder = (ObservationEncoder<?, ?>) encoder;
                for (ServiceOperatorKey key : serviceOperatorKeyTypes) {
                    Set<String> responseFormats = observationEncoder.getSupportedResponseFormats(key.getService(), key.getVersion());
                    if (responseFormats != null) {
                        for (String responseFormat : responseFormats) {
                            addResponseFormat(new ResponseFormatKey(key, responseFormat));
                        }
                    }
                }
            }
        }
    }

    protected void addResponseFormat(ResponseFormatKey key) {
        Map<String, Set<String>> byService = this.responseFormats.get(key.getService());
        if (byService == null) {
            this.responseFormats.put(key.getService(), byService = Maps.newHashMap());
        }
        Set<String> byVersion = byService.get(key.getVersion());
        if (byVersion == null) {
            byService.put(key.getVersion(), byVersion = Sets.newHashSet());
        }
        byVersion.add(key.getResponseFormat());
    }

    public Set<String> getSupportedResponseFormats(ServiceOperatorKey sokt) {
        return getSupportedResponseFormats(sokt.getService(), sokt.getVersion());
    }

    public Set<String> getSupportedResponseFormats(String service,
                                                   String version) {
        Map<String, Set<String>> byService = responseFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        Set<String> responseFormats = byService.get(version);
        if (responseFormats == null) {
            return Collections.emptySet();
        }

        ServiceOperatorKey sokt = new ServiceOperatorKey(service, version);
        Set<String> result = Sets.newHashSet();
        for (String responseFormat : responseFormats) {
            ResponseFormatKey rfkt = new ResponseFormatKey(sokt, responseFormat);
            if (isActive(rfkt)) {
                result.add(responseFormat);
            }
        }
        return result;
    }

    public Set<String> getAllSupportedResponseFormats(String service,
                                                      String version) {
        Map<String, Set<String>> byService = this.responseFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        Set<String> rfs = byService.get(version);
        if (rfs == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(rfs);
    }

    public Map<ServiceOperatorKey, Set<String>> getAllSupportedResponseFormats() {
        Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (ServiceOperatorKey sokt : getServiceOperatorKeys()) {
            map.put(sokt, getAllSupportedResponseFormats(sokt));
        }
        return map;
    }

    private Set<ServiceOperatorKey> getServiceOperatorKeys() {
        return this.serviceOperatorRepository
                .getServiceOperatorKeys();
    }

    public Set<String> getAllSupportedResponseFormats(ServiceOperatorKey sokt) {
        return getAllSupportedResponseFormats(sokt.getService(),
                                              sokt.getVersion());
    }

     public Map<ServiceOperatorKey, Set<String>> getSupportedResponseFormats() {
        Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (ServiceOperatorKey sokt : getServiceOperatorKeys()) {
            map.put(sokt, getSupportedResponseFormats(sokt));
        }
        return map;
    }

    public void setActive(ResponseFormatKey rfkt, boolean active) {
        this.activation.setActive(rfkt, active);
    }

    @Override
    public void activate(ResponseFormatKey key) {
        this.activation.activate(key);
    }

    @Override
    public void deactivate(ResponseFormatKey key) {
        this.activation.deactivate(key);
    }

    @Override
    public void registerListener(ActivationListener<ResponseFormatKey> listener) {
        this.activation.registerListener(listener);
    }

    @Override
    public void deregisterListener(ActivationListener<ResponseFormatKey> listener) {
        this.activation.deregisterListener(listener);
    }

    @Override
    public boolean isActive(ResponseFormatKey key) {
        return this.activation.isActive(key);
    }

    @Deprecated
    public static ResponseFormatRepository getInstance() {
        return instance;
    }

}
