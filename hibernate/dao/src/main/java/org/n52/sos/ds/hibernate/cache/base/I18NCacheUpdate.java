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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.ds.I18NDAO;
import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.i18n.I18NDAORepository;
import org.n52.sos.i18n.metadata.AbstractI18NMetadata;
import org.n52.sos.i18n.metadata.I18NFeatureMetadata;
import org.n52.sos.i18n.metadata.I18NObservablePropertyMetadata;
import org.n52.sos.i18n.metadata.I18NOfferingMetadata;
import org.n52.sos.i18n.metadata.I18NProcedureMetadata;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;

/**
 * Cache update class for I18N
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
public class I18NCacheUpdate extends AbstractThreadableDatasourceCacheUpdate {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18NCacheUpdate.class);

    @Override
    public void execute() {
        LOGGER.info("Executing I18NCacheUpdate");
        startStopwatch();
        try {
            getCache().addSupportedLanguage(Configurator.getInstance().getServiceIdentificationFactory().getAvailableLocales());
            getCache().addSupportedLanguage(getEntityLocales(I18NFeatureMetadata.class));
            getCache().addSupportedLanguage(getEntityLocales(I18NOfferingMetadata.class));
            getCache().addSupportedLanguage(getEntityLocales(I18NObservablePropertyMetadata.class));
            getCache().addSupportedLanguage(getEntityLocales(I18NProcedureMetadata.class));
        } catch (OwsExceptionReport ce) {
            getErrors().add(ce);
        }
        LOGGER.info("Finished executing I18NCacheUpdate ({})", getStopwatchResult());
    }

    private Collection<Locale> getEntityLocales(Class<? extends AbstractI18NMetadata> type)
            throws OwsExceptionReport {
        I18NDAO<? extends AbstractI18NMetadata> dao
                = I18NDAORepository.getInstance().getDAO(type);
        if (dao != null) {
            return dao.getAvailableLocales();
        } else {
            return Collections.emptySet();
        }
    }
}
