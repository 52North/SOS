/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.config.json;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.n52.iceland.coding.encode.ResponseFormatKey;
import org.n52.iceland.config.json.JsonActivationDao;
import org.n52.sos.ogc.sos.SosObservationOfferingExtensionKey;
import org.n52.shetland.ogc.ows.service.OwsServiceKey;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatKey;
import org.n52.sos.config.SosActivationDao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SosJsonActivationDao extends JsonActivationDao implements SosActivationDao {
    protected static final String RESPONSE_FORMATS = "responseFormats";
    protected static final String PROCEDURE_DESCRIPTION_FORMATS = "procedureDescriptionFormats";
    protected static final String FORMAT = "format";
    protected static final String OFFERING_EXTENSIONS = "offeringExtensions";
    protected static final String BINDINGS = "bindings";
    protected static final String OPERATIONS = "operations";

    @Override
    public boolean isSosObservationOfferingExtensionActive(SosObservationOfferingExtensionKey key) {
        return isActive(OFFERING_EXTENSIONS, matches(key), true);
    }

    @Override
    public void setSosObservationOfferingExtensionStatus(SosObservationOfferingExtensionKey key, boolean active) {
        setStatus(OFFERING_EXTENSIONS, matches(key), s -> encode(s, key), active);
    }

    @Override
    public Set<SosObservationOfferingExtensionKey> getSosObservationOfferingExtensionKeys() {
        Function<JsonNode, SosObservationOfferingExtensionKey> fun =
                createDomainDecoder(SosObservationOfferingExtensionKey::new);
        return getKeys(OFFERING_EXTENSIONS, fun);
    }

    @Override
    public boolean isResponseFormatActive(ResponseFormatKey key) {
        return isActive(RESPONSE_FORMATS,
                matches(key, ResponseFormatKey::getServiceOperatorKey, ResponseFormatKey::getResponseFormat), true);
    }

    @Override
    public void setResponseFormatStatus(ResponseFormatKey key, boolean active) {
        setStatus(RESPONSE_FORMATS,
                matches(key, ResponseFormatKey::getServiceOperatorKey, ResponseFormatKey::getResponseFormat),
            s -> createFormatEncoder(s, key, ResponseFormatKey::getServiceOperatorKey,
                    ResponseFormatKey::getResponseFormat),
            active);
    }

    @Override
    public Set<ResponseFormatKey> getResponseFormatKeys() {
        return getKeys(RESPONSE_FORMATS, createFormatDecoder(ResponseFormatKey::new));
    }

    @Override
    public boolean isProcedureDescriptionFormatActive(ProcedureDescriptionFormatKey key) {
        return isActive(PROCEDURE_DESCRIPTION_FORMATS,
                matches(key, ProcedureDescriptionFormatKey::getServiceOperatorKey,
                        ProcedureDescriptionFormatKey::getProcedureDescriptionFormat),
                true);
    }

    @Override
    public void setProcedureDescriptionFormatStatus(ProcedureDescriptionFormatKey key, boolean active) {
        setStatus(PROCEDURE_DESCRIPTION_FORMATS,
            matches(key, ProcedureDescriptionFormatKey::getServiceOperatorKey,
                    ProcedureDescriptionFormatKey::getProcedureDescriptionFormat),
            s -> createFormatEncoder(s, key, ProcedureDescriptionFormatKey::getServiceOperatorKey,
                    ProcedureDescriptionFormatKey::getProcedureDescriptionFormat),
            active);
    }

    @Override
    public Set<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatKeys() {
        return getKeys(PROCEDURE_DESCRIPTION_FORMATS, createFormatDecoder(ProcedureDescriptionFormatKey::new));
    }

    // @Override
    // public boolean isBindingActive(BindingKey key) {
    // return isActive(BINDINGS, matches(key, BindingKey::ge,
    // BindingKey::getBinding), true);
    // }
    //
    // @Override
    // public void setBindingStatus(BindingKey key, boolean active) {
    // setStatus(BINDINGS, matches(key, BindingKey::getServiceOperatorKey,
    // BindingKey::getBinding),
    // s -> createFormatEncoder(s, key, BindingKey::getServiceOperatorKey,
    // BindingKey::getBinding), active);
    // }
    //
    // @Override
    // public Set<BindingKey> getBindingKeys() {
    // return getKeys(BINDINGS, createFormatDecoder(BindingKey::new));
    // }
    //
    // @Override
    // public boolean isOperationActive(OperationHandlerKey key) {
    // return isActive(OPERATIONS, matches(key,
    // OperationKey::getServiceOperatorKey, OperationKey::getOperation), true);
    // }
    //
    // @Override
    // public void setOperationStatus(OperationHandlerKey key, boolean active) {
    // setStatus(OPERATIONS, matches(key, OperationKey::getServiceOperatorKey,
    // OperationKey::getOperation),
    // s -> createFormatEncoder(s, key, OperationKey::getServiceOperatorKey,
    // OperationKey::getOperation), active);
    // }
    //
    // @Override
    // public Set<OperationKey> getOperationKeys() {
    // return getKeys(OPERATIONS, createFormatDecoder(OperationKey::new));
    // }

    protected <K> Function<JsonNode, K> createFormatDecoder(BiFunction<OwsServiceKey, String, K> fun) {
        Objects.requireNonNull(fun);
        return n -> fun.apply(decodeServiceOperatorKey(n), n.path(FORMAT).textValue());
    }

    protected <K> Supplier<ObjectNode> createFormatEncoder(Supplier<ObjectNode> supplier, K key,
            Function<K, OwsServiceKey> sokFun, Function<K, String> formatFun) {
        Objects.requireNonNull(supplier);
        Objects.requireNonNull(sokFun);
        Objects.requireNonNull(formatFun);
        return () -> {
            OwsServiceKey sok = key == null ? null : sokFun.apply(key);
            String format = key == null ? null : formatFun.apply(key);
            return encode(supplier, sok).get().put(FORMAT, format);
        };
    }

    protected <K> Predicate<JsonNode> matches(K key, Function<K, OwsServiceKey> sokFun,
            Function<K, String> formatFun) {
        OwsServiceKey sok = key == null ? null : sokFun.apply(key);
        String responseFormat = key == null ? null : formatFun.apply(key);
        return matches(sok).and(matchesFormat(responseFormat));
    }

    protected Predicate<JsonNode> matchesFormat(String responseFormat) {
        if (responseFormat == null) {
            return isNullOrMissing(FORMAT);
        }
        return n -> n.path(FORMAT).asText().equals(responseFormat);
    }

}
