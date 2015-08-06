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

import org.n52.sos.statistics.api.interfaces.IServiceEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventHandlerFinder {

    private static final Logger logger = LoggerFactory.getLogger(EventHandlerFinder.class);

    @SuppressWarnings("unchecked")
    public static <T> IServiceEventHandler<T> findHandler(Object object, Map<String, IServiceEventHandler<?>> handlers) {
        String key = object.getClass().getSimpleName();

        IServiceEventHandler<T> handler = (IServiceEventHandler<T>) handlers.get(key);
        logger.debug("Searching handler for object by key {} ", key);
        if (handler == null) {
            logger.debug("Not found using default handler by key 'default' if registered.");
            handler = (IServiceEventHandler<T>) handlers.get("default");
        } else {
            logger.debug("Key {} found.", key);
        }

        Objects.requireNonNull(handler, "Can not find handler for object: " + key);
        return handler;
    }

    private EventHandlerFinder() {
    }

}
