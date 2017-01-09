/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.coding.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.util.JSONUtils;
import org.n52.sos.decode.json.JSONDecodingException;
import org.n52.svalbard.decode.exception.DecodingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.download.ResourceURIDownloader;
import com.github.fge.jsonschema.core.load.download.URIDownloader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class JSONValidator {
    private static final Logger LOG = LoggerFactory.getLogger(JSONValidator.class);

    private final JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory
            .newBuilder()
            .setLoadingConfiguration(LoadingConfiguration.newBuilder()
                    .addScheme("http", new ResourceRedirect()).freeze())
            .freeze();

    private JSONValidator() {
    }

    public JsonSchemaFactory getJsonSchemaFactory() {
        return jsonSchemaFactory;
    }

    public ProcessingReport validate(String json, String schema) throws IOException {
        return validate(JSONUtils.loadString(json), schema);
    }

    public boolean isValid(String json, String schema) throws IOException {
        return isValid(JSONUtils.loadString(json), schema);
    }

    public ProcessingReport validate(URL url, String schema) throws IOException {
        return validate(JSONUtils.loadURL(url), schema);
    }

    public boolean isValid(URL url, String schema) throws IOException {
        return isValid(JSONUtils.loadURL(url), schema);
    }

    public ProcessingReport validate(File file, String schema) throws IOException {
        return validate(JSONUtils.loadFile(file), schema);
    }

    public boolean isValid(File file, String schema) throws IOException {
        return isValid(JSONUtils.loadFile(file), schema);
    }

    public ProcessingReport validate(InputStream is, String schema) throws IOException {
        return validate(JSONUtils.loadStream(is), schema);
    }

    public boolean isValid(InputStream is, String schema) throws IOException {
        return isValid(JSONUtils.loadStream(is), schema);
    }

    public ProcessingReport validate(Reader reader, String schema) throws IOException {
        return validate(JSONUtils.loadReader(reader), schema);
    }

    public boolean isValid(Reader reader, String schema) throws IOException {
        return isValid(JSONUtils.loadReader(reader), schema);
    }

    public boolean isValid(JsonNode node, String schema) {
        return validate(node, schema).isSuccess();
    }

    public ProcessingReport validate(JsonNode node, String schema) {
        JsonSchema jsonSchema;
        try {
            jsonSchema = getJsonSchemaFactory().getJsonSchema(schema);
        } catch (ProcessingException ex) {
            throw new IllegalArgumentException("Unknown schema: " + schema, ex);
        }
        return jsonSchema.validateUnchecked(node);
    }

    public String encode(ProcessingReport report, JsonNode instance) {
        ObjectNode objectNode = JSONUtils.nodeFactory().objectNode();
        objectNode.set(JSONConstants.INSTANCE, instance);
        ArrayNode errors = objectNode.putArray(JSONConstants.ERRORS);
        for (ProcessingMessage m : report) {
            errors.add(m.asJson());
        }
        return JSONUtils.print(objectNode);
    }

    public void validateAndThrow(JsonNode instance, String schema) throws DecodingException {
        ProcessingReport report = JSONValidator.getInstance()
                .validate(instance, schema);
        if (!report.isSuccess()) {
            String message = encode(report, instance);
            LOG.info("Invalid JSON instance:\n{}", message);
            throw new JSONDecodingException(message);
        }
    }

    public static JSONValidator getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static JSONValidator INSTANCE = new JSONValidator();

        private LazyHolder() {
        }
    }

    private class ResourceRedirect implements URIDownloader {
        private final URIDownloader resource = ResourceURIDownloader.getInstance();

        @Override
        public InputStream fetch(URI source) throws IOException {
            return resource.fetch(URI.create(toResource(source)));
        }

        protected String toResource(URI source) {
            return String.format("resource://%s.json", source.getPath().replace("/json", ""));
        }
    }
}
