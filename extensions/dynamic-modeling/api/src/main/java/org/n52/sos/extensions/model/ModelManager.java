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
package org.n52.sos.extensions.model;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Defines a Workspace class to manage Models with Observable Objects.
 *  
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class ModelManager implements Closeable
{
    private static final Logger LOG = Logger.getLogger(ModelManager.class.toString());
    
    /** List of models managed by this object. */
    private List<Model> models = new ArrayList<Model>();
    
    /** List of prepared models managed by this object. */
    private ConcurrentLinkedQueue<Model> preparedModels = new ConcurrentLinkedQueue<Model>(); 
    
    /** Load the models from the specified settings file. */
    public boolean loadSettings(final String settingsFileName) throws Exception
    {
        LOG.info(String.format("Loading settings from '%s' file", settingsFileName));
        
        File settingsFile = new File(settingsFileName);
        close();
        
        if (settingsFileName!=null && settingsFile.exists())
        {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(settingsFileName);
            NodeList nodeList = document.getDocumentElement().getElementsByTagName("model");
            
            for (int i = 0, icount = nodeList.getLength(); i < icount; i++) 
            {
                Element modelEntry = (Element)nodeList.item(i);
                
                if (modelEntry!=null && modelEntry.hasAttribute("class"))
                {
                    String className = modelEntry.getAttributeNode("class").getValue();
                    Model model = (Model)Class.forName(className).newInstance();
                    if (model.loadSettings(settingsFileName, document.getDocumentElement(), modelEntry)) models.add(model);
                }
            }
            return models.size()>0;
        }
        return false;
    }
    /** Close the Object and release resources. */
    @Override
    public void close()
    {
        preparedModels.clear();
        models.clear();
    }
    
    /** Helper class to asynchronously prepare a dynamic model. */
    private class MyRunnable implements Runnable
    {
        /** Creates a new MyRunnable object. */
        public MyRunnable(Model model)
        {
            this.model = model;
        }
        private Model model;
        
        @Override
        public void run() 
        {
            try
            {
                if (model.prepareObject()) preparedModels.add(model);
            }
            catch (RuntimeException e) 
            {
                LOG.severe(e.getMessage());
            }
        }
    }    
    /** Prepare asynchronously the models managed by the Object. */
    public void asynchronouslyPrepareModels()
    {
        preparedModels.clear();
        
        for (Model model : models)
        {
            Thread thread = new Thread(new MyRunnable(model));
            thread.start();
        }
    }
    /** Prepare synchronously the models managed by the Object. It returns the prepared model count */
    public int prepareModels()
    {
        preparedModels.clear();
        
        for (Model model : models)
        {
            MyRunnable runnable = new MyRunnable(model);
            runnable.run();
        }
        return preparedModels.size();
    }
    
    /** Gets the list of prepared models managed by the Object. */
    public Model[] getPreparedModels()
    {
        return preparedModels.toArray(new Model[0]);
    } 
    /** Gets the list of models managed by the Object. */
    public List<Model> getModels()
    {
        return models;
    } 
}
