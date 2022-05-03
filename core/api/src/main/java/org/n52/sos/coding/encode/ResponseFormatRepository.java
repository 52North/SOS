/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.n52.iceland.coding.encode.ResponseFormatKey;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.iceland.util.activation.ActivationListener;
import org.n52.iceland.util.activation.ActivationListeners;
import org.n52.iceland.util.activation.ActivationManager;
import org.n52.janmayen.function.Functions;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.shetland.ogc.ows.service.OwsServiceKey;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.encode.ObservationEncoder;

import com.google.common.collect.Maps;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ResponseFormatRepository implements ActivationManager<ResponseFormatKey> {
    private final Map<String, Map<String, Set<String>>> responseFormats = Maps.newHashMap();
    private final ActivationListeners<ResponseFormatKey> activation = new ActivationListeners<>(true);

    private ServiceOperatorRepository serviceOperatorRepository;
    private EncoderRepository encoderRepository;

    /**
     * This class does not implement {@link Constructable} due to some circular dependencies that can lead to an
     * incorrect initialization order; instead {@link ResponseFormatRepositoryInitializer} does this for us.
     *
     * @param serviceOperatorRepository the service operator respository
     * @param encoderRepository         the encoder repository
     */
    void init(ServiceOperatorRepository serviceOperatorRepository,
              EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
        this.serviceOperatorRepository = serviceOperatorRepository;
        generateResponseFormatMaps();
    }

    private void generateResponseFormatMaps() {
        this.responseFormats.clear();
        Set<OwsServiceKey> serviceOperatorKeyTypes = getServiceOperatorKeys();
        this.encoderRepository.getEncoders().stream()
                .filter(x -> x instanceof ObservationEncoder)
                .map(x -> (ObservationEncoder<?, ?>) x)
                .forEach((ObservationEncoder<?, ?> encoder) -> {
                    serviceOperatorKeyTypes.forEach(key -> {
                        Optional.ofNullable(encoder.getSupportedResponseFormats(key))
                                .orElseGet(Collections::emptySet).stream()
                                .map((String rf) -> new ResponseFormatKey(key, rf))
                                .forEach(ResponseFormatRepository.this::addResponseFormat);
                    });
                });

    }

    protected void addResponseFormat(ResponseFormatKey key) {
        isActive(key);
        this.responseFormats.computeIfAbsent(key.getService(), Functions.forSupplier(HashMap::new))
                .computeIfAbsent(key.getVersion(), Functions.forSupplier(HashSet::new))
                .add(key.getResponseFormat());
    }

    public Set<String> getSupportedResponseFormats(OwsServiceKey sokt) {
        return getSupportedResponseFormats(sokt.getService(), sokt.getVersion());
    }

    public Set<String> getSupportedResponseFormats(String service, String version) {
        OwsServiceKey sokt = new OwsServiceKey(service, version);
        return this.responseFormats.getOrDefault(service, Collections.emptyMap())
                .getOrDefault(version, Collections.emptySet()).stream()
                .map(rf -> new ResponseFormatKey(sokt, rf))
                .filter(this::isActive)
                .map(ResponseFormatKey::getResponseFormat)
                .collect(toSet());
    }

    public Map<OwsServiceKey, Set<String>> getSupportedResponseFormats() {
        return getServiceOperatorKeys().stream()
                .collect(toMap(Function.identity(), this::getSupportedResponseFormats));
    }

    public Set<String> getAllSupportedResponseFormats(String service, String version) {
        return Collections.unmodifiableSet(this.responseFormats.getOrDefault(service, Collections.emptyMap())
                .getOrDefault(version, Collections.emptySet()));
    }

    public Map<OwsServiceKey, Set<String>> getAllSupportedResponseFormats() {
        return getServiceOperatorKeys().stream()
                .collect(toMap(Function.identity(), this::getAllSupportedResponseFormats));
    }

    public Set<String> getAllSupportedResponseFormats(OwsServiceKey sokt) {
        return getAllSupportedResponseFormats(sokt.getService(), sokt.getVersion());
    }

    private Set<OwsServiceKey> getServiceOperatorKeys() {
        return this.serviceOperatorRepository.getServiceOperatorKeys();
    }

    @Override
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

}
