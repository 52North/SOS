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
package org.n52.sos.decode.json.inspire;

import java.net.URI;

import org.n52.sos.decode.json.JSONDecoder;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.AQDJSONConstants;
import org.n52.sos.util.Nillable;
import org.n52.sos.util.Reference;
import org.n52.sos.util.Referenceable;
import org.n52.sos.util.ThrowableFunction;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Function;

public abstract class AbstractJSONDecoder<T> extends JSONDecoder<T> {

    public AbstractJSONDecoder(Class<T> type) {
        super(type);
    }

    protected Nillable<String> parseNillableString(JsonNode node) {
        return parseNillable(node).transform(new Function<JsonNode, String>() {
            @Override
            public String apply(JsonNode input) {
                return input.textValue();
            }
        });
    }

    protected Nillable<JsonNode> parseNillable(JsonNode node) {
        if (node.isMissingNode() || node.isNull()) {
            return Nillable.absent();
        } else if (node.isObject() && node.path(AQDJSONConstants.NIL)
                   .asBoolean()) {
            return Nillable.nil(node.path(AQDJSONConstants.REASON).textValue());
        }
        return Nillable.of(node);
    }

    protected Nillable<Reference> parseNillableReference(JsonNode node) {
        return parseNillable(node)
                .transform(new Function<JsonNode, Reference>() {

                    @Override
                    public Reference apply(JsonNode node) {
                        return parseReference(node);
                    }
                });
    }

    protected Referenceable<JsonNode> parseReferenceable(JsonNode node) {
        Nillable<JsonNode> nillable = parseNillable(node);

        if (nillable.isAbsent() || nillable.isNil()) {
            return Referenceable.of(nillable);
        }

        if (node.has(AQDJSONConstants.HREF)) {
            return Referenceable.of(parseReference(node));
        }

        return Referenceable.of(node);
    }

    protected Reference parseReference(JsonNode node) {
        Reference ref = new Reference();
        ref.setHref(URI.create(node.path(AQDJSONConstants.HREF).textValue()));
        ref.setActuate(node.path(AQDJSONConstants.ACTUATE).textValue());
        ref.setArcrole(node.path(AQDJSONConstants.ARCROLE).textValue());
        ref.setRemoteSchema(node.path(AQDJSONConstants.REMOTE_SCHEMA).textValue());
        ref.setRole(node.path(AQDJSONConstants.ROLE).textValue());
        ref.setShow(node.path(AQDJSONConstants.SHOW).textValue());
        ref.setTitle(node.path(AQDJSONConstants.TITLE).textValue());
        ref.setType(node.path(AQDJSONConstants.TYPE).textValue());
        return ref;
    }

    protected Referenceable<Time> parseReferenceableTime(JsonNode node) {
        return parseReferenceable(node)
                .transform(new Function<JsonNode, Time>() {

                    @Override
                    public Time apply(JsonNode node) {
                        try {
                            return parseTime(node);
                        } catch (DateTimeParseException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                });
    }

    protected Nillable<CodeType> parseNillableCodeType(JsonNode node) {
        return parseNillable(node)
                .transform(new Function<JsonNode, CodeType>() {

                    @Override
                    public CodeType apply(JsonNode node) {
                        return parseCodeType(node);
                    }
                });
    }

    protected <T> Nillable<T> decodeJsonToNillable(JsonNode node, final Class<T> type)
            throws OwsExceptionReport {
        ThrowableFunction<JsonNode, T> fun
                = new ThrowableFunction<JsonNode, T>() {

                    @Override
                    protected T applyThrowable(JsonNode input)
                            throws OwsExceptionReport {
                        return decodeJsonToObject(input, type);
                    }
                };

        Nillable<T> result = parseNillable(node).transform(fun);

        if (fun.hasErrors()) {
            fun.propagateIfPossible(OwsExceptionReport.class);
        }
        return result;
    }

    protected <T> Referenceable<T> decodeJsonToReferencable(JsonNode node, final Class<T> type)
            throws OwsExceptionReport {
        ThrowableFunction<JsonNode, T> fun
                = new ThrowableFunction<JsonNode, T>() {

                    @Override
                    protected T applyThrowable(JsonNode input)
                            throws OwsExceptionReport {
                        return decodeJsonToObject(input, type);
                    }
                };

        Referenceable<T> result = parseReferenceable(node).transform(fun);

        if (fun.hasErrors()) {
            fun.propagateIfPossible(OwsExceptionReport.class);
        }
        return result;
    }
}
