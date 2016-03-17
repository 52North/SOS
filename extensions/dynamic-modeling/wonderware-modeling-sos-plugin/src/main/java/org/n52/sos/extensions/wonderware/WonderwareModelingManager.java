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
package org.n52.sos.extensions.wonderware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.cache.ContentCache;
import org.n52.sos.extensions.VirtualCapabilitiesExtension;
import org.n52.sos.extensions.VirtualCapabilitiesExtensionRepository;
import org.n52.sos.extensions.DynamicWritableCache;
import org.n52.sos.extensions.ObservableModel;
import org.n52.sos.extensions.model.ModelManager;
import org.n52.sos.extensions.util.FileUtils;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.response.AbstractServiceResponse;

/**
 * Main class manager to integrate Wonderware frameworks as 52°North SOS Sensors/Observation objects.
 *  
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class WonderwareModelingManager implements VirtualCapabilitiesExtension
{
    private static final Logger LOG = LoggerFactory.getLogger(WonderwareModelingManager.class);
    
    /** Default settings file. */
    public static String DEFAULT_SETTINGS_FILE = "./misc/dynamic-models/wonderware-settings.xml";
    
    /** Creates a new WonderwareModelingManager using the specified settings file. */
    public WonderwareModelingManager(String settingsFileName)
    {        
        VirtualCapabilitiesExtensionRepository virtualCapabilitiesExtensionRepository = VirtualCapabilitiesExtensionRepository.getInstance();
        virtualCapabilitiesExtensionRepository.registerVirtualCapabilitiesExtension(this);
        
        LOG.info("WonderwareModelingManager registered as VirtualCapabilitiesExtension!");
        
        try
        {
            java.net.URI url = FileUtils.resolveAbsoluteURI(settingsFileName, WonderwareModelingManager.class.getClassLoader());
            if (url!=null) settingsFileName = url.getPath();
            
            LOG.info("Creating WonderwareModelingManager... SettingsFile='"+settingsFileName+"'.");
            
            modelManager = new ModelManager();
            modelManager.loadSettings(settingsFileName);
            modelManager.asynchronouslyPrepareModels();
            
            LOG.info("WonderwareModelingManager successly created!");
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage());
        }
    }
    
    private static class LazyHolder {
        private static final WonderwareModelingManager INSTANCE = new WonderwareModelingManager(DEFAULT_SETTINGS_FILE);
        
        private LazyHolder() {};
    }    
    public static WonderwareModelingManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Returns the related Wonderware workspace managed.
     */
    public ModelManager getModelManager()
    {
        return modelManager;
    }
    private ModelManager modelManager;    

    /**
     * Returns a new ContentCache adding the virtual capabilities managed by this extension and required for the specified request.
     */
    public ContentCache injectVirtualCapabilities(ContentCache contentCache, AbstractServiceRequest<?> request) throws org.n52.sos.exception.CodedException
    {
      //LOG.info("WonderwareModelingManager::injectDymanicCapabilities()");
        
        if (contentCache instanceof DynamicWritableCache)
        {
            DynamicWritableCache dynamicCache = (DynamicWritableCache)contentCache;
            for (ObservableModel model : modelManager.getPreparedModels()) dynamicCache.addContentOfDynamicModel(model, request);
            return (ContentCache)dynamicCache;
        }
        else
        {
            DynamicWritableCache dynamicCache = new DynamicWritableCache();
            dynamicCache.addContentOfCache(contentCache);
            for (ObservableModel model : modelManager.getPreparedModels()) dynamicCache.addContentOfDynamicModel(model, request);
            return (ContentCache)dynamicCache;
        }
    }
    
    /**
     * Injects the virtual objects managed by this extension and required for the specified request.
     */
    public boolean injectVirtualResponse(AbstractServiceResponse response, ContentCache contentCache, AbstractServiceRequest<?> request) throws org.n52.sos.exception.CodedException
    {
      //LOG.info("WonderwareModelingManager::injectVirtualResponse()");
        boolean dataInjected = false;
        
        if (contentCache instanceof DynamicWritableCache)
        {
            DynamicWritableCache dynamicCache = (DynamicWritableCache)contentCache;
            for (ObservableModel model : modelManager.getPreparedModels()) dataInjected |= dynamicCache.addResponseDataOfDynamicModel(response, model, request);
            return dataInjected;
        }
        else
        {
            DynamicWritableCache dynamicCache = new DynamicWritableCache();
            dynamicCache.addContentOfCache(contentCache);
            for (ObservableModel model : modelManager.getPreparedModels()) dynamicCache.addContentOfDynamicModel(model, request);
            for (ObservableModel model : modelManager.getPreparedModels()) dataInjected |= dynamicCache.addResponseDataOfDynamicModel(response, model, request);
            return dataInjected;
        }
    }
}
