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
package org.n52.sos.ext.deleteobservation;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.event.SosEvent;
import org.n52.sos.event.SosEventListener;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 1.0.0
 */
public class DeleteObservationContentModificationListener implements SosEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteObservationContentModificationListener.class);

    private static final Set<Class<? extends SosEvent>> TYPES = Collections
            .<Class<? extends SosEvent>> singleton(DeleteObservationEvent.class);

    @Override
    public Set<Class<? extends SosEvent>> getTypes() {
        return Collections.unmodifiableSet(TYPES);
    }

    @Override
    public void handle(SosEvent event) {
        if (event instanceof DeleteObservationEvent) {
            DeleteObservationEvent e = (DeleteObservationEvent) event;
            DeleteObservationCacheControllerUpdate update =
                    new DeleteObservationCacheControllerUpdate(e.getDeletedObservation());
            LOGGER.debug("Updating Cache after content modification: {}", update);
            try {
                Configurator.getInstance().getCacheController().update(update);
            } catch (OwsExceptionReport ex) {
                LOGGER.error("Error processing Event", ex);
            }
        } else {
            LOGGER.debug("Can not handle modification event: {}", event);
        }
    }
}
