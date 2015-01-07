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
package org.n52.sos.cache.ctrl;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.cache.ContentCacheUpdate;
import org.n52.sos.cache.ctrl.action.ObservationInsertionUpdate;
import org.n52.sos.cache.ctrl.action.ResultInsertionUpdate;
import org.n52.sos.cache.ctrl.action.ResultTemplateInsertionUpdate;
import org.n52.sos.cache.ctrl.action.SensorDeletionUpdate;
import org.n52.sos.cache.ctrl.action.SensorInsertionUpdate;
import org.n52.sos.event.SosEvent;
import org.n52.sos.event.SosEventListener;
import org.n52.sos.event.events.ObservationInsertion;
import org.n52.sos.event.events.ResultInsertion;
import org.n52.sos.event.events.ResultTemplateInsertion;
import org.n52.sos.event.events.SensorDeletion;
import org.n52.sos.event.events.SensorInsertion;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class DefaultContentModificationListener implements SosEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContentModificationListener.class);

    @SuppressWarnings("unchecked")
    private static final Set<Class<? extends SosEvent>> TYPES = Sets.<Class<? extends SosEvent>> newHashSet(
            SensorInsertion.class, ObservationInsertion.class, ResultTemplateInsertion.class, SensorDeletion.class,
            ResultInsertion.class);

    @Override
    public Set<Class<? extends SosEvent>> getTypes() {
        return Collections.unmodifiableSet(TYPES);
    }

    @Override
    public void handle(SosEvent event) {
        if (event instanceof SensorInsertion) {
            SensorInsertion e = (SensorInsertion) event;
            handle(new SensorInsertionUpdate(e.getRequest(), e.getResponse()));
        } else if (event instanceof ObservationInsertion) {
            ObservationInsertion e = (ObservationInsertion) event;
            handle(new ObservationInsertionUpdate(e.getRequest()));
        } else if (event instanceof ResultTemplateInsertion) {
            ResultTemplateInsertion e = (ResultTemplateInsertion) event;
            handle(new ResultTemplateInsertionUpdate(e.getRequest(), e.getResponse()));
        } else if (event instanceof SensorDeletion) {
            SensorDeletion e = (SensorDeletion) event;
            handle(new SensorDeletionUpdate(e.getRequest()));
        } else if (event instanceof ResultInsertion) {
            ResultInsertion e = (ResultInsertion) event;
            handle(new ResultInsertionUpdate(e.getRequest().getTemplateIdentifier(), e.getResponse().getObservation()));
        } else {
            LOGGER.debug("Can not handle modification event: {}", event);
        }
    }

    protected void handle(ContentCacheUpdate update) {
        LOGGER.debug("Updating Cache after content modification: {}", update);
        try {
            Configurator.getInstance().getCacheController().update(update);
        } catch (OwsExceptionReport ex) {
            LOGGER.error("Error processing Event", ex);
        }
    }
}
