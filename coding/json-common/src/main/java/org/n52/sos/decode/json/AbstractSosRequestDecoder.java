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
package org.n52.sos.decode.json;

import static org.n52.sos.coding.json.JSONConstants.SERVICE;
import static org.n52.sos.coding.json.JSONConstants.VERSION;
import static org.n52.sos.coding.json.JSONConstants.EXTENSIONS;
import static org.n52.sos.coding.json.JSONConstants.DEFINITION;
import static org.n52.sos.coding.json.JSONConstants.VALUE;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.n52.sos.coding.json.JSONValidator;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.JsonDecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.util.http.MediaTypes;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 * 
 * @param <T>
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class AbstractSosRequestDecoder<T extends AbstractServiceRequest<?>> extends JSONDecoder<T> {

    public AbstractSosRequestDecoder(Class<T> type, String service, String version, Enum<?> operation) {
        this(type, service, version, operation.name());
    }

    public AbstractSosRequestDecoder(Class<T> type, String service, String version, String operation) {
        super(Sets.newHashSet(new JsonDecoderKey(type), new OperationDecoderKey(service, version, operation,
                MediaTypes.APPLICATION_JSON)));
    }

    public AbstractSosRequestDecoder(Set<DecoderKey> keys) {
        super(keys);
    }

    public AbstractSosRequestDecoder(Class<T> type, String service, Enum<?> operation) {
        this(type, service, null, operation.name());
    }

    public AbstractSosRequestDecoder(Class<T> type, String service, String operation) {
        this(type, service, null, operation);
    }

    @Override
    public T decodeJSON(JsonNode node, boolean validate) throws OwsExceptionReport {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (validate) {
            JSONValidator.getInstance().validateAndThrow(node, getSchemaURI());
        }
        T t = decodeRequest(node);
        t.setService(node.path(SERVICE).textValue());
        t.setVersion(node.path(VERSION).textValue());
        t.setExtensions(parseExtensions(node.path(EXTENSIONS)));
        return t;

    }

    @SuppressWarnings("rawtypes")
    protected SwesExtensions parseExtensions(JsonNode node) {
        SwesExtensions extensions = new SwesExtensions();
        if (node.isArray()) {
            for (JsonNode n : node) {
            	SwesExtension extension = parseExtension(n);
            	if (extension != null) {
            		extensions.addSwesExtension(extension);
            	}
            }
        }
        return extensions;
    }

    @SuppressWarnings("rawtypes")
	protected SwesExtension parseExtension(JsonNode node) {
    	SwesExtension extension = null;
    	if (node.isObject() && node.has(DEFINITION) && node.has(VALUE)) {
    		if (node.path("value").isBoolean()) {
    			extension = new SwesExtensionImpl<SweBoolean>()
					.setDefinition(node.path(DEFINITION).asText())
					.setValue(new SweBoolean()
						.setValue(node.path(VALUE).asBoolean())
					);
    		} else if (node.path(VALUE).isTextual()) {
    			extension = new SwesExtensionImpl<SweText>()
					.setDefinition(node.path(DEFINITION).asText())
					.setValue(new SweText()
						.setValue(node.path(VALUE).asText())
					);
    		}
    	}
		return extension;
    }

    protected List<String> parseStringOrStringList(JsonNode node) {
        if (node.isArray()) {
            List<String> offerings = Lists.newArrayListWithExpectedSize(node.size());
            for (JsonNode n : node) {
                if (n.isTextual()) {
                    offerings.add(n.textValue());
                }
            }
            return offerings;
        } else if (node.isTextual()) {
            return Collections.singletonList(node.textValue());
        } else {
            return null;
        }
    }

    protected ComparisonFilter parseComparisonFilter(JsonNode node) {
        // TODO
        return null;
    }

    protected abstract String getSchemaURI();

    protected abstract T decodeRequest(JsonNode node) throws OwsExceptionReport;
}
