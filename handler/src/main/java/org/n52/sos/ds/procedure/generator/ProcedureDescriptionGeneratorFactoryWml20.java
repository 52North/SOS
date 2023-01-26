/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
import org.n52.sensorweb.server.db.old.dao.DbQueryFactory;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Generator class for WaterML 2.0 procedure descriptions
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ProcedureDescriptionGeneratorFactoryWml20 extends AbstractProcedureDescriptionGeneratorFactory {

    @Inject
    public ProcedureDescriptionGeneratorFactoryWml20(SettingsService settingsService,
                                                               GeometryHandler geometryHandler,
                                                               I18NDAORepository i18NDAORepository,
                                                               ContentCacheController cacheController,
                                                               ProfileHandler profileHandler,
                                                               ProcedureDescriptionSettings procedureSettings,
                                                               DbQueryFactory dbQueryFactory) {
        super(settingsService, geometryHandler, i18NDAORepository, cacheController, profileHandler, procedureSettings,
                dbQueryFactory);
    }

    @Override
    public Set<ProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(ProcedureDescriptionGeneratorWml20.GENERATOR_KEY_TYPES);
    }

    @Override
    public ProcedureDescriptionGenerator create(ProcedureDescriptionGeneratorKey key) {
        ProcedureDescriptionGenerator generator
                = new ProcedureDescriptionGeneratorWml20(getProfileHandler(),
                                                                   getGeometryHandler(),
                                                                   getI18NDAORepository(),
                                                                   getCacheController(),
                                                                   getProcedureSettings(),
                                                                   getDbQueryFactory());
        getSettingsService().configureOnce(key);
        return generator;
    }

}
