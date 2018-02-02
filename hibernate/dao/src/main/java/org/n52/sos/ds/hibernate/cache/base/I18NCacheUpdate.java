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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.iceland.i18n.I18NDAO;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.metadata.AbstractI18NMetadata;
import org.n52.iceland.i18n.metadata.I18NFeatureMetadata;
import org.n52.iceland.i18n.metadata.I18NObservablePropertyMetadata;
import org.n52.iceland.i18n.metadata.I18NOfferingMetadata;
import org.n52.iceland.i18n.metadata.I18NProcedureMetadata;
import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.ogc.ows.OwsServiceMetadataRepository;

/**
 * Cache update class for I18N
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public class I18NCacheUpdate extends AbstractThreadableDatasourceCacheUpdate {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18NCacheUpdate.class);

    private final OwsServiceMetadataRepository serviceMetadataRepository;
    private final I18NDAORepository i18NDAORepository;

    public I18NCacheUpdate(OwsServiceMetadataRepository serviceMetadataRepository,
                           I18NDAORepository i18NDAORepository) {
        this.serviceMetadataRepository = serviceMetadataRepository;
        this.i18NDAORepository = i18NDAORepository;
    }

    @Override
    public void execute() {
        LOGGER.debug("Executing I18NCacheUpdate");
        startStopwatch();
        try {
            getCache().addSupportedLanguage(this.serviceMetadataRepository.getAvailableLocales());
            getCache().addSupportedLanguage(getEntityLocales(I18NFeatureMetadata.class));
            getCache().addSupportedLanguage(getEntityLocales(I18NOfferingMetadata.class));
            getCache().addSupportedLanguage(getEntityLocales(I18NObservablePropertyMetadata.class));
            getCache().addSupportedLanguage(getEntityLocales(I18NProcedureMetadata.class));
        } catch (OwsExceptionReport ce) {
            getErrors().copy(ce);
        }
        LOGGER.debug("Finished executing I18NCacheUpdate ({})", getStopwatchResult());
    }

    private Collection<Locale> getEntityLocales(Class<? extends AbstractI18NMetadata> type)
            throws OwsExceptionReport {
        I18NDAO<? extends AbstractI18NMetadata> dao = this.i18NDAORepository.getDAO(type);
        if (dao != null) {
            return dao.getAvailableLocales();
        } else {
            return Collections.emptySet();
        }
    }
}
