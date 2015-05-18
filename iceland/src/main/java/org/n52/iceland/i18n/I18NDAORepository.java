/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.i18n;

import java.util.Map;
import java.util.Set;

import org.n52.iceland.ds.I18NDAO;
import org.n52.iceland.exception.ConfigurationException;
import org.n52.iceland.i18n.metadata.AbstractI18NMetadata;
import org.n52.iceland.util.AbstractConfiguringServiceLoaderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
