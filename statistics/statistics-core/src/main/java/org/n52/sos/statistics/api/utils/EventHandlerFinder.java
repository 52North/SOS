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
package org.n52.sos.statistics.api.utils;

import java.util.Map;
import java.util.Objects;

import org.n52.sos.statistics.api.interfaces.StatisticsServiceEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandlerFinder {

    private static final Logger logger = LoggerFactory.getLogger(EventHandlerFinder.class);

    @SuppressWarnings("unchecked")
    public static <T> StatisticsServiceEventHandler<T> findHandler(Object object, Map<String, StatisticsServiceEventHandler<?>> handlers) {
        // Find concrete class
        String key = object.getClass().getSimpleName();
        logger.debug("Searching handler for object by key {} ", key);
        StatisticsServiceEventHandler<T> handler = (StatisticsServiceEventHandler<T>) handlers.get(key);

        // Find super class as handler
        if (handler == null) {
            Class<?> superclass = object.getClass().getSuperclass();
            while (superclass != null) {
                key = superclass.getSimpleName();
                logger.debug("Try super class as key {}", key);
                handler = (StatisticsServiceEventHandler<T>) handlers.get(key);
                if (handler != null) {
                    break;
                } else {
                    superclass = superclass.getSuperclass();
                }
            }
        }

        // Find default handler
        if (handler == null) {
            logger.debug("Not found using default handler by key 'default' if registered.");
            key = "default";
            handler = (StatisticsServiceEventHandler<T>) handlers.get(key);
        }

        Objects.requireNonNull(handler, "Can not find handler for object: " + key);
        logger.debug("Key {} found.", key);
        return handler;
    }

    private EventHandlerFinder() {
    }
}
