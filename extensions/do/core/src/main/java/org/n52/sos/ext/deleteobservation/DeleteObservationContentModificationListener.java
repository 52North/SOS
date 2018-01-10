/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.cache.ContentCacheUpdate;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.janmayen.event.Event;
import org.n52.janmayen.event.EventListener;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @since 1.0.0
 */
public class DeleteObservationContentModificationListener implements
        EventListener {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DeleteObservationContentModificationListener.class);

    private static final Set<Class<? extends Event>> TYPES = Collections
            .<Class<? extends Event>>singleton(DeleteObservationEvent.class);

    private ContentCacheController contentCacheController;
    private DeleteObservationCacheFeederDAO cacheFeederDAO;
    private FeatureQueryHandler featureQueryHandler;

    @Inject
    public void setFeatureQueryHandler(FeatureQueryHandler featureQueryHandler) {
        this.featureQueryHandler = featureQueryHandler;
    }

    @Inject
    public void setCacheFeederDAO(DeleteObservationCacheFeederDAO cacheFeederDAO) {
        this.cacheFeederDAO = cacheFeederDAO;
    }

    @Inject
    public void setContentCacheController(
            ContentCacheController contentCacheController) {
        this.contentCacheController = contentCacheController;
    }

    @Override
    public Set<Class<? extends Event>> getTypes() {
        return Collections.unmodifiableSet(TYPES);
    }

    @Override
    public void handle(Event event) {
        if (event instanceof DeleteObservationEvent) {
            DeleteObservationEvent e = (DeleteObservationEvent) event;
            ContentCacheUpdate update = createUpdate(e.getDeletedObservation());
            LOGGER.debug("Updating Cache after content modification: {}", update);
            try {
                this.contentCacheController.update(update);
            } catch (OwsExceptionReport ex) {
                LOGGER.error("Error processing Event", ex);
            }
        } else {
            LOGGER.debug("Can not handle modification event: {}", event);
        }
    }

    private DeleteObservationCacheControllerUpdate createUpdate(OmObservation e) {
        return new DeleteObservationCacheControllerUpdate(
                this.featureQueryHandler, this.cacheFeederDAO, e);
    }
}
