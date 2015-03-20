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
package org.n52.sos.i18n;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.ds.I18NDAO;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.i18n.metadata.AbstractI18NMetadata;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;

import com.google.common.collect.Maps;

/**
 * I18N DAO repository
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 *
 */
@SuppressWarnings("rawtypes")
public class I18NDAORepository extends AbstractConfiguringServiceLoaderRepository<I18NDAO> {
    private static final Logger LOG = LoggerFactory.getLogger(I18NDAORepository.class);
    private final Map<Class<? extends AbstractI18NMetadata>, I18NDAO<?>> daos = Maps.newHashMap();


    /**
     * private constructor
     */
    private I18NDAORepository() {
        super(I18NDAO.class, false);
        load(false);
    }

    /**
     * Get the available DAO
     *
     * @param <T> the meta data type
     * @param c the meta data class
     * @return the loaded DAO
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractI18NMetadata> I18NDAO<T> getDAO(Class<T> c) {
        // TODO check for subtypes
        return (I18NDAO<T>) daos.get(c);
    }

    @Override
    protected  void processConfiguredImplementations(Set<I18NDAO> implementations) throws ConfigurationException {
        this.daos.clear();
        for (I18NDAO<?> dao : implementations) {
            if (dao.isSupported()){
                I18NDAO<?> prev = daos.put(dao.getType(), dao);
                if (prev != null) {
                    LOG.warn("Duplicate implementation of I18N DAO for %s: %s, %s", dao.getType(), dao, prev);
                }
            }
        }
    }

    /**
     * Get the singleton instance of the I18NDAORepository.
     *
     * @return Returns a singleton instance of the I18NDAORepository.
     */
    public static I18NDAORepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Lazy holder for this repository
     *
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * @since 4.1.0
     *
     */
    private static class LazyHolder {
        private static final I18NDAORepository INSTANCE = new I18NDAORepository();

        private LazyHolder() {
        }
    }

}
