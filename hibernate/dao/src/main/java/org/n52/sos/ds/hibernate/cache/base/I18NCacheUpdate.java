/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.List;

import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.i18n.AbstractFeatureI18NDAO;
import org.n52.sos.ds.hibernate.dao.i18n.I18NCapabiliesDAO;
import org.n52.sos.ds.hibernate.entities.i18n.AbstractFeatureI18N;
import org.n52.sos.ds.hibernate.entities.i18n.I18NCapabilities;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.SosServiceIdentificationFactory;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.JavaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            if (HibernateHelper.isEntitySupported(I18NCapabilities.class, getSession())) {
                List<I18NCapabilities> i18nCapabilitiesCodespace =
                        new I18NCapabiliesDAO().getI18NCapabilitiesObjects(getSession());
                SosServiceIdentificationFactory serviceIdentificationFactory =
                        Configurator.getInstance().getServiceIdentificationFactory();
                for (I18NCapabilities i18nCapabilities : i18nCapabilitiesCodespace) {
                    String codespace = i18nCapabilities.getCodespace().getCodespace();
                    serviceIdentificationFactory.addLanguageTitle(codespace, i18nCapabilities.getTitle());
                    serviceIdentificationFactory.addLanguageAbstract(codespace, i18nCapabilities.getAbstract());
                    getCache().addSupportedLanguage(codespace);
                }
            }
            addLanguagesFromI18NDAOs();
        } catch (OwsExceptionReport ce) {
            getErrors().add(ce);
        }
        LOGGER.info("Finished executing I18NCacheUpdate ({})", getStopwatchResult());
    }

    /**
     * Add languages, contained in the specific tables, to the supported
     * languages cache
     */
    private void addLanguagesFromI18NDAOs() {
        for (Object clazz : JavaHelper.getSubclasses(AbstractFeatureI18N.class)) {
            if (clazz instanceof Class<?>) {
                AbstractFeatureI18NDAO i18ndao = DaoFactory.getInstance().getI18NDAO((Class<?>) clazz, getSession());
                if (i18ndao != null) {
                    getCache().addSupportedLanguage(i18ndao.getCodespaceAsString(getSession()));
                }
            }
        }
    }
}
