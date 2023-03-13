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
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.binding.BindingRepository;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.janmayen.http.MediaTypes;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;
import org.n52.svalbard.CodingSettings;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Strings;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Generator class for SensorML 2.0 procedure descriptions
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ProcedureDescriptionGeneratorFactorySml20 implements ProcedureDescriptionGeneratorFactory {

    private final SettingsService settingsService;
    private final GeometryHandler geometryHandler;
    private final I18NDAORepository i18NDAORepository;
    private final ContentCacheController cacheController;
    private final ProfileHandler profileHandler;
    private String srsNamePrefixUrl = "";
    private BindingRepository bindingRepository;

    @Inject
    public ProcedureDescriptionGeneratorFactorySml20(SettingsService settingsService,
                                                               GeometryHandler geometryHandler,
                                                               I18NDAORepository i18NDAORepository,
                                                               ContentCacheController cacheController,
                                                               ProfileHandler profileHandler,
                                                               BindingRepository bindingRepository) {
        this.settingsService = settingsService;
        this.geometryHandler = geometryHandler;
        this.i18NDAORepository = i18NDAORepository;
        this.cacheController = cacheController;
        this.profileHandler = profileHandler;
        this.bindingRepository = bindingRepository;
    }

    @Setting(CodingSettings.SRS_NAME_PREFIX_URL)
    public void setSrsNamePrefixUrl(String srsNamePrefixUrl) {
        if (!Strings.isNullOrEmpty(srsNamePrefixUrl)) {
            this.srsNamePrefixUrl = srsNamePrefixUrl;
        }
    }

    @Override
    public Set<ProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(ProcedureDescriptionGeneratorSml20.GENERATOR_KEY_TYPES);
    }

    @Override
    public ProcedureDescriptionGenerator create(ProcedureDescriptionGeneratorKey key) {
        ProcedureDescriptionGenerator generator
                = new ProcedureDescriptionGeneratorSml20(getProfileHandler(),
                                                           getGeometryHandler(),
                                                           getI18NDAORepository(),
                                                           getCacheController(),
                                                           getSrsNamePrefixUrl(),
                                                           bindingRepository.isActive(MediaTypes.APPLICATION_KVP));
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

    public String getSrsNamePrefixUrl() {
        return srsNamePrefixUrl;
    }
}
