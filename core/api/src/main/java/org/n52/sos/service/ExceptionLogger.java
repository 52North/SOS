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
package org.n52.sos.service;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.event.SosEvent;
import org.n52.sos.event.SosEventListener;
import org.n52.sos.event.events.ExceptionEvent;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single point of exception logging.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class ExceptionLogger implements SosEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionLogger.class);

    public static final Set<Class<? extends SosEvent>> EVENTS = Collections
            .<Class<? extends SosEvent>> singleton(ExceptionEvent.class);

    @Override
    public Set<Class<? extends SosEvent>> getTypes() {
        return EVENTS;
    }

    @Override
    public void handle(final SosEvent event) {
        final ExceptionEvent ee = (ExceptionEvent) event;

        // TODO review logging of exceptions. Stacktrace only on debug level?
        if (ee.getException() instanceof OwsExceptionReport) {
            final OwsExceptionReport owse = (OwsExceptionReport) ee.getException();
            if (owse.getStatus() == null) {
                log(owse);
            } else if (owse.getStatus().getCode() >= 500) {
                LOGGER.error("Exception thrown", owse);
            } else if (owse.getStatus().getCode() >= 400) {
                LOGGER.warn("Exception thrown", owse);
            } else {
                log(owse);
            }
        } else {
            LOGGER.debug("Error processing request", ee.getException());
        }
    }

    private void log(final OwsExceptionReport owse) {
        LOGGER.debug("Exception thrown", owse);
    }
}
