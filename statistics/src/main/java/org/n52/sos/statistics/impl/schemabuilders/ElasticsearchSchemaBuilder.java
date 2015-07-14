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
package org.n52.sos.statistics.impl.schemabuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.n52.sos.statistics.sos.SosDataMapping;

import com.google.common.collect.ImmutableMap;

/**
 * Schema builder for creating schema for Elasticsearch
 */
public class ElasticsearchSchemaBuilder {

    protected Map<String, Object> properties;
    protected Map<String, Object> mappings;

    /***** Objects for creating data types for elasticsearch *******/
    protected static Map<String, Object> stringField = ImmutableMap.<String, Object> of("type", "string", "index", "not_analyzed");
    protected static Map<String, Object> stringAnalyzedField = ImmutableMap.<String, Object> of("type", "string", "index", "analyzed");
    protected static Map<String, Object> dateField = ImmutableMap.<String, Object> of("type", "date");
    protected static Map<String, Object> integerField = ImmutableMap.<String, Object> of("type", "integer");
    protected static Map<String, Object> longField = ImmutableMap.<String, Object> of("type", "long");
    protected static Map<String, Object> doubleField = ImmutableMap.<String, Object> of("type", "double");
    protected static Map<String, Object> booleanField = ImmutableMap.<String, Object> of("type", "boolean");
    protected static Map<String, Object> geoPointField = ImmutableMap.<String, Object> of("type", "geo_point");
    protected static Map<String, Object> geoShapeField = ImmutableMap.<String, Object> of("type", "geo_shape", "precision", "1km");

    private ElasticsearchSchemaBuilder() {
        properties = new HashMap<>(1);
        mappings = new HashMap<>();

        properties.put("properties", mappings);
    }

    public ElasticsearchSchemaBuilder addStringField(String fieldName) {
        Objects.requireNonNull(fieldName);
        mappings.put(fieldName, stringField);
        return this;
    }

    public ElasticsearchSchemaBuilder addStringAnalyzedField(String fieldName) {
        Objects.requireNonNull(fieldName);
        mappings.put(fieldName, stringAnalyzedField);
        return this;
    }

    public ElasticsearchSchemaBuilder addDateField(String fieldName) {
        Objects.requireNonNull(fieldName);
        mappings.put(fieldName, dateField);
        return this;
    }

    public ElasticsearchSchemaBuilder addIntegerField(String fieldName) {
        Objects.requireNonNull(fieldName);
        mappings.put(fieldName, integerField);
        return this;
    }

    public ElasticsearchSchemaBuilder addLongField(String fieldName) {
        Objects.requireNonNull(fieldName);
        mappings.put(fieldName, longField);
        return this;
    }

    public ElasticsearchSchemaBuilder addDoubleField(String fieldName) {
        Objects.requireNonNull(fieldName);
        mappings.put(fieldName, doubleField);
        return this;
    }

    public ElasticsearchSchemaBuilder addBooleanField(String fieldName) {
        Objects.requireNonNull(fieldName);
        mappings.put(fieldName, booleanField);
        return this;
    }

    public ElasticsearchSchemaBuilder addGeoPointField(String fieldName) {
        Objects.requireNonNull(fieldName);
        mappings.put(fieldName, geoPointField);
        return this;
    }

    public ElasticsearchSchemaBuilder addGeoShapeField(String fieldName) {
        Objects.requireNonNull(fieldName);
        mappings.put(fieldName, geoShapeField);
        return this;
    }

    public ElasticsearchSchemaBuilder addTemporalFilterField(String fieldName) {
        Objects.requireNonNull(fieldName);
        ElasticsearchSchemaBuilder builder = ElasticsearchSchemaBuilder.builder();
        builder.addLongField(SosDataMapping.TIME_DURARTION);
        builder.addDateField(SosDataMapping.TIME_START);
        builder.addDateField(SosDataMapping.TIME_END);
        builder.addDateField(SosDataMapping.TIME_TIMEINSTANT);
        builder.addStringField(SosDataMapping.TEMPORAL_FILTER_OPERATOR);

        addObject(fieldName, builder.build());
        return this;
    }

    public ElasticsearchSchemaBuilder addSpatialFilterField(String fieldName) {
        Objects.requireNonNull(fieldName);

        ElasticsearchSchemaBuilder builder = ElasticsearchSchemaBuilder.builder();
        builder.addStringField(SosDataMapping.SPATIAL_FILTER_OPERATOR);
        builder.addStringField(SosDataMapping.SPATIAL_FILTER_VALUE_REF);
        builder.addGeoShapeField(SosDataMapping.SPATIAL_FILTER_SHAPE);

        addObject(fieldName, builder.build());
        return this;
    }

    public ElasticsearchSchemaBuilder addTimeField(String fieldName) {
        Objects.requireNonNull(fieldName);
        ElasticsearchSchemaBuilder builder = ElasticsearchSchemaBuilder.builder();
        builder.addLongField(SosDataMapping.TIME_DURARTION);
        builder.addDateField(SosDataMapping.TIME_START);
        builder.addDateField(SosDataMapping.TIME_END);
        builder.addDateField(SosDataMapping.TIME_TIMEINSTANT);

        addObject(fieldName, builder.build());
        return this;
    }

    public Map<String, Object> build() {
        return properties;
    }

    public static ElasticsearchSchemaBuilder builder() {
        return new ElasticsearchSchemaBuilder();
    }

    public ElasticsearchSchemaBuilder addObject(String fieldName,
            Map<String, Object> object) {
        mappings.put(fieldName, object);
        return this;
    }
}
