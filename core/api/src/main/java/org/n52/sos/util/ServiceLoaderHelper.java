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
package org.n52.sos.util;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.n52.sos.ds.DatasourceDaoIdentifier;
import org.n52.sos.exception.ows.concrete.NoImplementationFoundException;
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
