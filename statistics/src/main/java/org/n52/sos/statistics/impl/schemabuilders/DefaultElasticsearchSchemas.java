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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.n52.sos.statistics.api.mappings.MetadataDataMapping;
import org.n52.sos.statistics.api.mappings.ServiceEventDataMapping;
import org.n52.sos.statistics.api.parameters.AbstractEsParameter;
import org.n52.sos.statistics.api.parameters.ObjectEsParameter;
import org.n52.sos.statistics.api.parameters.SingleEsParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for further application specific Elasticsearch schema
 * creation.
 *
 */
public abstract class DefaultElasticsearchSchemas {

    private static final Logger logger = LoggerFactory.getLogger(DefaultElasticsearchSchemas.class);

    protected Map<String, Object> properties;
    protected Map<String, Object> mappings;

    public final Map<String, Object> getSchema() {
        properties = new HashMap<>(1);
        mappings = new HashMap<>();
        properties.put("properties", mappings);

        processSchemaClass(ServiceEventDataMapping.class);
        appSpecificSchema();

        return properties;
    }

    /**
     * Call this method in your subclass and point it to your class where the
     * mappings exists This class will process the
     * <code>public static final {@link AbstractEsParameter}</code> fields only.
     * 
     * @param schemaClass
     *            application specific schema
     */
    protected final void processSchemaClass(Class<?> schemaClass) {
        for (Field field : schemaClass.getDeclaredFields()) {
            AbstractEsParameter value = checkField(field);
            if (value != null) {
                resolveParameterField(value, mappings);
            }
        }
    }

    private void resolveParameterField(AbstractEsParameter value,
            Map<String, Object> map) {
        if (value instanceof SingleEsParameter) {
            SingleEsParameter single = (SingleEsParameter) value;
            map.put(single.getName(), single.getTypeAsMap());
        } else if (value instanceof ObjectEsParameter) {
            ObjectEsParameter object = (ObjectEsParameter) value;

            // loadup all the children
            // the wrapper properties map is needed to elasticsearch
            Map<String, Object> subproperties = new HashMap<>(1);
            Map<String, Object> childrenMap = new HashMap<>(value.getAllChildren().size());
            subproperties.put("properties", childrenMap);

            for (AbstractEsParameter child : object.getAllChildren()) {
                resolveParameterField(child, childrenMap);
            }

            map.put(object.getName(), subproperties);

        } else {
            throw new IllegalArgumentException("Invalid schema parameter value " + value.toString());
        }
    }

    private AbstractEsParameter checkField(Field field) {
        boolean bool = Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers());
        bool = bool && field.getType().isAssignableFrom((AbstractEsParameter.class));
        if (bool) {
            try {
                return (AbstractEsParameter) field.get(null);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    public final Map<String, Object> getMetadataSchema() {
        properties = new HashMap<>(1);
        mappings = new HashMap<>();
        properties.put("properties", mappings);
        processSchemaClass(MetadataDataMapping.class);
        return properties;
    }

    public abstract int getSchemaVersion();

    /**
     * @see {@link DefaultElasticsearchSchemas#processSchemaClass(Class)}
     */
    protected abstract void appSpecificSchema();
}
