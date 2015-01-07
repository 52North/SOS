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
package org.n52.sos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.2.0
 */
public class JSONUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtils.class);

    private static final JsonNodeFactory FACTORY = JsonNodeFactory.withExactBigDecimals(false);

    private static final ObjectReader READER;

    private static final ObjectWriter WRITER;

    private static final Function<Object, JsonNode> TO_JSON_STRING
                = new Function<Object, JsonNode>() {
                    @Override
                    public JsonNode apply(Object object) {
                        if (object == null) {
                            return nodeFactory().nullNode();
                        } else if (object instanceof JsonNode) {
                            return (JsonNode) object;
                        } else {
                            return nodeFactory().textNode(String.valueOf(object));
                        }
                    }
                };

    static {
        final ObjectMapper mapper =
                new ObjectMapper().setNodeFactory(FACTORY).enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        READER = mapper.reader();
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
        pp.indentArraysWith(DefaultPrettyPrinter.Lf2SpacesIndenter.instance);
        WRITER = mapper.writer(pp);
    }

    protected JSONUtils() {
    }

    public static ObjectReader getReader() {
        return READER;
    }

    public static ObjectWriter getWriter() {
        return WRITER;
    }

    public static JsonNodeFactory nodeFactory() {
        return FACTORY;
    }

    public static String print(final JsonNode node) {
        final StringWriter writer = new StringWriter();
        try {
            print(writer, node);
            writer.flush();
        } catch (IOException e) {
            // cannot happen
        } finally {
            try {
                Closeables.close(writer, true);
            } catch (IOException ioe) {
                LOGGER.error("Error while colsing closeable!", ioe);
            }
        }
        return writer.toString();
    }

    public static void print(final Writer writer, final JsonNode node) throws IOException {
        getWriter().writeValue(writer, node);
    }

    public static void print(final OutputStream writer, final JsonNode node) throws IOException {
        getWriter().writeValue(writer, node);
    }

    public static JsonNode loadURL(final URL url) throws IOException {
        return getReader().readTree(url.openStream());
    }

    public static JsonNode loadPath(final String path) throws IOException {
        final JsonNode ret;
        final FileInputStream in = new FileInputStream(path);
        try {
            ret = getReader().readTree(in);
        } finally {
            in.close();
        }
        return ret;
    }

    public static JsonNode loadFile(final File file) throws IOException {
        final JsonNode ret;

        final FileInputStream in = new FileInputStream(file);
        try {
            ret = getReader().readTree(in);
        } finally {
            in.close();
        }

        return ret;
    }

    public static JsonNode loadStream(final InputStream in) throws IOException {
        return getReader().readTree(in);
    }

    public static JsonNode loadReader(final Reader reader) throws IOException {
        return getReader().readTree(reader);
    }

    public static JsonNode loadString(final String json)  {
        try {
            return loadReader(new StringReader(json));
        } catch (IOException ex) {
            // cannot happen
            throw new RuntimeException(ex);
        }
    }

    public static ObjectNode toJSON(Map<String, ?> map) {
        ObjectNode node = nodeFactory().objectNode();
        if (map != null) {
            node.putAll(Maps.transformValues(map, TO_JSON_STRING));
        }
        return node;
    }

    public static ArrayNode toJSON(Collection<?> coll) {
        ArrayNode node = nodeFactory().arrayNode();
        node.addAll(Collections2.transform(coll, TO_JSON_STRING));
        return node;
    }
}
