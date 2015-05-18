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
package org.n52.iceland.util;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.n52.iceland.ds.DatasourceDaoIdentifier;
import org.n52.iceland.exception.ows.concrete.NoImplementationFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceLoaderHelper {
    
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLoaderHelper.class);
    
    /**
     * Return an implementation of a class, loaded by the ServiceLoader
     * 
     * @param clazz The class to load
     * @return An implementation of the class
     * @throws NoImplementationFoundException
     */
    public static <T> T loadImplementation(Class<T> clazz) throws NoImplementationFoundException {
        T impl = null;
        ServiceLoader<T> sl = ServiceLoader.load(clazz);
        Iterator<T> i = sl.iterator();
        //TODO throw exception if more than one implementation is found?
        impl = i.hasNext() ? i.next() : null;
        if (impl == null) {
            throw new NoImplementationFoundException(clazz);
        }
        return impl;
    }
    
    /**
     * Return an implementation of a class, loaded by the ServiceLoader
     * 
     * @param clazz The class to load
     * @param datasourceIdentificator The identificator for the loaded class
     * @return An implementation of the class
     * @throws NoImplementationFoundException
     */
    public static <T> T loadImplementation(Class<T> clazz, String datasourceIdentificator) throws NoImplementationFoundException {
        T impl = null;
        ServiceLoader<T> sl = ServiceLoader.load(clazz);
        Iterator<T> i = sl.iterator();
        T currentImplementation = null;
        while (i.hasNext() && impl == null) {
            try {
                currentImplementation = i.next();
            } catch (ServiceConfigurationError sce) {
                LOG.warn(String.format("Implementation for %s could be loaded!", clazz), sce);
            }
            if (currentImplementation instanceof DatasourceDaoIdentifier) {
                if (datasourceIdentificator.equalsIgnoreCase(
                        ((DatasourceDaoIdentifier) currentImplementation).getDatasourceDaoIdentifier())) {
                    impl = currentImplementation;
                }
            }
        }
        if (impl == null) {
            throw new NoImplementationFoundException(clazz);
        }
        return impl;
    }
}
