/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.SettingsService;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Generator class for SensorML 1.0.1 procedure descriptions
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class HibernateProcedureDescriptionGeneratorFactorySml101
        implements HibernateProcedureDescriptionGeneratorFactory {

    private final DaoFactory daoFactory;
    private final SettingsService settingsService;
    private final GeometryHandler geometryHandler;
    private final I18NDAORepository i18NDAORepository;
    private final ContentCacheController cacheController;
    private final ProfileHandler profileHandler;

    @Inject
    public HibernateProcedureDescriptionGeneratorFactorySml101(DaoFactory daoFactory,
                                                               SettingsService settingsService,
                                                               GeometryHandler geometryHandler,
                                                               I18NDAORepository i18NDAORepository,
                                                               ContentCacheController cacheController,
                                                               ProfileHandler profileHandler) {
        this.daoFactory = daoFactory;
        this.settingsService = settingsService;
        this.geometryHandler = geometryHandler;
        this.i18NDAORepository = i18NDAORepository;
        this.cacheController = cacheController;
        this.profileHandler = profileHandler;
    }

    @Override
    public Set<HibernateProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(HibernateProcedureDescriptionGeneratorSml101.GENERATOR_KEY_TYPES);
    }

    @Override
    public HibernateProcedureDescriptionGenerator create(HibernateProcedureDescriptionGeneratorKey key) {
        HibernateProcedureDescriptionGenerator generator
                = new HibernateProcedureDescriptionGeneratorSml101(getProfileHandler(),
                                                                   getGeometryHandler(),
                                                                   getDaoFactory(),
                                                                   getI18NDAORepository(),
                                                                   getCacheController());
        getSettingsService().configureOnce(generator);
        return generator;
    }

    public DaoFactory getDaoFactory() {
        return daoFactory;
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
