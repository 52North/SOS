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
package org.n52.sos.ds.procedure.generator;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.SettingsService;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;

public class ProcedureDescriptionGeneratorFactoryInspireOmpr30 implements ProcedureDescriptionGeneratorFactory {

    private final SettingsService settingsService;
    private final GeometryHandler geometryHandler;
    private final I18NDAORepository i18NDAORepository;
    private final ContentCacheController cacheController;
    private final ProfileHandler profileHandler;

    @Inject
    public ProcedureDescriptionGeneratorFactoryInspireOmpr30(SettingsService settingsService,
                                                               GeometryHandler geometryHandler,
                                                               I18NDAORepository i18NDAORepository,
                                                               ContentCacheController cacheController,
                                                               ProfileHandler profileHandler) {
        this.settingsService = settingsService;
        this.geometryHandler = geometryHandler;
        this.i18NDAORepository = i18NDAORepository;
        this.cacheController = cacheController;
        this.profileHandler = profileHandler;
    }

    @Override
    public Set<ProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(ProcedureDescriptionGeneratorInspireOmpr30.GENERATOR_KEY_TYPES);
    }

    @Override
    public ProcedureDescriptionGenerator create(ProcedureDescriptionGeneratorKey key) {
        ProcedureDescriptionGenerator generator
                = new ProcedureDescriptionGeneratorInspireOmpr30(getProfileHandler(),
                                                                   getGeometryHandler(),
                                                                   getI18NDAORepository(),
                                                                   getCacheController());
        getSettingsService().configureOnce(key);
        return generator;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }

    public GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    public I18NDAORepository getI18NDAORepository() {
        return i18NDAORepository;
    }

    public ContentCacheController getCacheController() {
        return cacheController;
    }

    public ProfileHandler getProfileHandler() {
        return profileHandler;
    }
}
