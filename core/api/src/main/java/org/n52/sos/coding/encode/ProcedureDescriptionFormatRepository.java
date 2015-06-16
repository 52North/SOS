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


import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.lifecycle.Constructable;
import org.n52.iceland.service.operator.ServiceOperatorKey;
import org.n52.iceland.service.operator.ServiceOperatorRepository;
import org.n52.iceland.util.activation.ActivationListener;
import org.n52.iceland.util.activation.ActivationListeners;
import org.n52.iceland.util.activation.ActivationManager;
import org.n52.iceland.util.activation.ActivationSource;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.n52.iceland.coding.encode.Encoder;
import org.n52.iceland.coding.encode.EncoderRepository;
import org.n52.iceland.coding.encode.ProcedureEncoder;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class ProcedureDescriptionFormatRepository
        implements Constructable,
                   ActivationManager<ProcedureDescriptionFormatKey>,
                   ActivationSource<ProcedureDescriptionFormatKey> {

    @Deprecated
    private static ProcedureDescriptionFormatRepository instance;
    private final ActivationListeners<ProcedureDescriptionFormatKey> activation = new ActivationListeners<>(true);
    private final Map<String, Map<String, Set<String>>> procedureDescriptionFormats = Maps.newHashMap();
    private final Set<ProcedureDescriptionFormatKey> keys = new HashSet<>();
    private EncoderRepository encoderRepository;
    private ServiceOperatorRepository serviceOperatorRepository;

    @Override
    public void init() {
        ProcedureDescriptionFormatRepository.instance = this;

        Objects.requireNonNull(this.encoderRepository);
        Objects.requireNonNull(this.serviceOperatorRepository);

        generateProcedureDescriptionFormatMaps();
    }

    private void generateProcedureDescriptionFormatMaps() {
        this.procedureDescriptionFormats.clear();
        this.keys.clear();
        Set<ServiceOperatorKey> serviceOperatorKeyTypes
                = this.serviceOperatorRepository.getServiceOperatorKeys();
        for (Encoder<?, ?> encoder : this.encoderRepository.getEncoders()) {
            if (encoder instanceof ProcedureEncoder) {
                ProcedureEncoder<?, ?> procedureEncoder = (ProcedureEncoder<?, ?>) encoder;
                for (ServiceOperatorKey sokt : serviceOperatorKeyTypes) {
                    Set<String> formats = procedureEncoder.getSupportedProcedureDescriptionFormats(sokt.getService(), sokt.getVersion());
                    if (formats != null) {
                        for (String format : formats) {
                            addProcedureDescriptionFormat(new ProcedureDescriptionFormatKey(sokt, format));
                        }
                    }

                }
            }
        }
    }

    @Inject
    public void setEncoderRepository(EncoderRepository repository) {
        this.encoderRepository = repository;
    }

    @Inject
    public void setServiceOperatorRepository(ServiceOperatorRepository repository) {
        this.serviceOperatorRepository = repository;
    }

    protected void addProcedureDescriptionFormat(ProcedureDescriptionFormatKey key) {
        this.keys.add(key);
        Map<String, Set<String>> byService = this.procedureDescriptionFormats.get(key.getService());
        if (byService == null) {
            this.procedureDescriptionFormats.put(key.getService(), byService = Maps.newHashMap());
        }
        Set<String> byVersion = byService.get(key.getVersion());
        if (byVersion == null) {
            byService.put(key.getVersion(), byVersion = Sets.newHashSet());
        }
        byVersion.add(key.getProcedureDescriptionFormat());
    }

    public Map<ServiceOperatorKey, Set<String>> getSupportedProcedureDescriptionFormats() {
        Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (ServiceOperatorKey sokt : this.serviceOperatorRepository.getServiceOperatorKeys()) {
            map.put(sokt, getSupportedProcedureDescriptionFormats(sokt));
        }
        return map;
    }

    public Set<String> getSupportedProcedureDescriptionFormats(ServiceOperatorKey sokt) {
        return getSupportedProcedureDescriptionFormats(sokt.getService(), sokt.getVersion());
    }

    public Set<String> getSupportedProcedureDescriptionFormats(String service, String version) {
        Map<String, Set<String>> byService = procedureDescriptionFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        Set<String> rfs = byService.get(version);
        if (rfs == null) {
            return Collections.emptySet();
        }

        ServiceOperatorKey sokt = new ServiceOperatorKey(service, version);
        Set<String> result = Sets.newHashSet();
        for (String a : rfs) {
            if (isActive(new ProcedureDescriptionFormatKey(sokt, a))) {
                result.add(a);
            }
        }
        return result;
    }

    public Map<ServiceOperatorKey, Set<String>> getAllProcedureDescriptionFormats() {
        Map<ServiceOperatorKey, Set<String>> map = Maps.newHashMap();
        for (ServiceOperatorKey sokt : this.serviceOperatorRepository.getServiceOperatorKeys()) {
            map.put(sokt, getAllSupportedProcedureDescriptionFormats(sokt));
        }
        return map;
    }

    public Set<String> getAllSupportedProcedureDescriptionFormats(String service, String version) {
        Map<String, Set<String>> byService = procedureDescriptionFormats.get(service);
        if (byService == null) {
            return Collections.emptySet();
        }
        Set<String> formats = byService.get(version);
        if (formats == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(formats);
    }

    public Set<String> getAllSupportedProcedureDescriptionFormats(ServiceOperatorKey sokt) {
        return getAllSupportedProcedureDescriptionFormats(sokt.getService(), sokt.getVersion());
    }

    @Override
    public void activate(ProcedureDescriptionFormatKey key) {
        this.activation.activate(key);
    }

    @Override
    public void deactivate(ProcedureDescriptionFormatKey key) {
        this.activation.deactivate(key);
    }

    @Override
    public boolean isActive(ProcedureDescriptionFormatKey key) {
        return this.activation.isActive(key);
    }

    @Override
    public void registerListener(ActivationListener<ProcedureDescriptionFormatKey> listener) {
        this.activation.registerListener(listener);
    }

    @Override
    public void deregisterListener(ActivationListener<ProcedureDescriptionFormatKey> listener) {
        this.activation.deregisterListener(listener);
    }

    @Override
    public void setActive(ProcedureDescriptionFormatKey key, boolean active) {
        this.activation.setActive(key, active);
    }

    @Override
    public Set<ProcedureDescriptionFormatKey> getKeys() {
        return Collections.unmodifiableSet(this.keys);
    }

    @Deprecated
    public static ProcedureDescriptionFormatRepository getInstance() {
        return instance;
    }
}
