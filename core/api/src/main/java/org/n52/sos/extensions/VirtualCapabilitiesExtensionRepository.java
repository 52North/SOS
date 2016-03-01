/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;

/**
 * Repository for {@link VirtualCapabilitiesExtension} implementations
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class VirtualCapabilitiesExtensionRepository {
    
    private static final Logger LOG = LoggerFactory.getLogger(VirtualCapabilitiesExtensionRepository.class);
    
    private final ReentrantLock lock = new ReentrantLock();
    
    private static class LazyHolder {
        private static final VirtualCapabilitiesExtensionRepository INSTANCE = new VirtualCapabilitiesExtensionRepository();
        
        private LazyHolder() {};
    }
    public static VirtualCapabilitiesExtensionRepository getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    /**
     * Registered VirtualCapabilitiesExtension objects collection.
     */
    private List<VirtualCapabilitiesExtension> virtualCapabilitiesExtensionList = new ArrayList<VirtualCapabilitiesExtension>();
    
    /**
     * Register a new VirtualCapabilitiesExtension.
     */
    public boolean registerVirtualCapabilitiesExtension(VirtualCapabilitiesExtension dymanicCapabilitiesExtension) {
        lock.lock();
        try {
            return virtualCapabilitiesExtensionList.add(dymanicCapabilitiesExtension);            
        }
        finally {
            lock.unlock();
        }
    }
    /**
     * Unregister the specified VirtualCapabilitiesExtension.
     */
    public boolean unregisterVirtualCapabilitiesExtension(VirtualCapabilitiesExtension dymanicCapabilitiesExtension) {
        lock.lock();
        try {
            return virtualCapabilitiesExtensionList.remove(dymanicCapabilitiesExtension);            
        }
        finally {
            lock.unlock();
        }
    }
    
    /**
     * Returns a ContentCache adding the virtual capabilities managed by the registered VirtualExtension collection and required for the specified request.
     */
    public ContentCache injectVirtualCapabilities(ContentCache contentCache, AbstractServiceRequest<?> request) {
        for (VirtualCapabilitiesExtension extension : virtualCapabilitiesExtensionList) {
            try {
                contentCache = extension.injectVirtualCapabilities(contentCache, request);
            }
            catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        return contentCache;
    }
    
    /**
     * Injects the virtual objects managed by the registered VirtualExtension collection and required for the specified request.
     */
    public boolean injectVirtualResponse(AbstractServiceResponse response, ContentCache contentCache, AbstractServiceRequest<?> request) {
        boolean dataInjected = false;
        
        for (VirtualCapabilitiesExtension extension : virtualCapabilitiesExtensionList) {
            try {
                dataInjected |= extension.injectVirtualResponse(response, contentCache, request);
            }
            catch (Exception e) {
                LOG.error(e.getMessage());
            }
        }
        return dataInjected;
    }
}
