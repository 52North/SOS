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
package org.n52.sos.extensions.hydrology;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.extensions.MeasureSet;
import org.n52.sos.extensions.ObservableObject;
import org.n52.sos.extensions.hydrology.epanet.EpanetModel;
import org.n52.sos.extensions.model.Model;
import org.n52.sos.extensions.model.ModelManager;

/**
 * Test class to validate Hydrology modeling classes.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class HydrologyModelingTest 
{
    private static final Logger LOG = Logger.getLogger(HydrologyModelingTest.class.toString());  
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    @AfterClass
    public static void tearDownClass() 
    {
    }
    @Before
    public void setUp() 
    {
    }
    
    /** Default settings file. */
    private static String DEFAULT_SETTINGS_FILE = "./misc/dynamic-models/hydrology-settings.xml";
    
    /** Test settings file loading */
    @Test
    public void testManagerSettings() 
    {
        ModelManager modelManager = new ModelManager();
        
        try
        {
            String warningMsg = String.format("Hydraulic workspace not loaded from settings, file='%s'", DEFAULT_SETTINGS_FILE);
            assertTrue(warningMsg, modelManager.loadSettings(DEFAULT_SETTINGS_FILE));
        } 
        catch (Exception e) 
        {
            LOG.log(Level.SEVERE, e.getMessage());
        }
        modelManager.close();
    }
    
    /** Test Hydraulic models */
    @Test
    public void testHydraulicModels() 
    {
        ModelManager modelManager = new ModelManager();
        
        try 
        {
            String warningMsg = String.format("Hydraulic workspace not loaded from settings, file='%s'", DEFAULT_SETTINGS_FILE);
            assertTrue(warningMsg, modelManager.loadSettings(DEFAULT_SETTINGS_FILE));
            
            String objectId = ObservableObject.UNDEFINED_OBJECT_ID_FLAG;
            ReferencedEnvelope envelope = ObservableObject.UNDEFINED_ENVELOPE_FILTER_FLAG;
            java.util.Date dateFrom = ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
            java.util.Date dateTo = ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
            
            for (Model model : modelManager.getModels())
            {
                if (model instanceof EpanetModel)
                {
                    EpanetModel epanetModel = (EpanetModel)model;
                    
                    File sqliteFile = new File(epanetModel.getFileName()+".db");
                    sqliteFile.delete();
                    File loggerFile = new File(epanetModel.getFileName()+".log");
                    loggerFile.delete();
                    
                    warningMsg = String.format("Hydraulic EPANET model '%s' not successly prepared", model.getName());
                    assertTrue(warningMsg, epanetModel.prepareObject());
                    sqliteFile.delete();
                    loggerFile.delete();
                    
                    LOG.log(Level.INFO, String.format("Hydraulic EPANET model '%s' successly tested!", model.getName()));
                }
                if (model.prepareObject())
                {
                    int objectCount = 0;
                    int measureCount = 0;
                    
                    for (ObservableObject observableObject : model.enumerateObservableObjects(objectId, envelope, dateFrom, dateTo))
                    {
                        warningMsg = String.format("Hydraulic Observable object '%s' successly prepared", model.getName());
                        assertTrue(warningMsg, observableObject.attributes.size()>0);
                        objectCount++;
                    }
                    if (model.getName().equalsIgnoreCase("Net1"))
                    {
                        assertTrue(warningMsg, objectCount==24);
                    }
                    else
                    {
                        assertTrue(warningMsg, objectCount>0);
                    }                    
                    
                    for (MeasureSet measureSet : model.enumerateMeasures(objectId, envelope, dateFrom, dateTo))
                    {
                        warningMsg = String.format("Hydraulic Measures '%s' successly prepared", model.getName());
                        assertTrue(warningMsg, measureSet.measures.size()>0);
                        measureCount++;
                    }
                    if (model.getName().equalsIgnoreCase("Net1"))
                    {
                        assertTrue(warningMsg, measureCount==122);                        
                    }
                    else
                    {
                        assertTrue(warningMsg, measureCount>0);
                    }
                    
                    LOG.log(Level.INFO, String.format("Hydraulic Observable model '%s' successly tested!", model.getName()));
                }
            }
        }
        catch (Exception e)
        {
            LOG.log(Level.SEVERE, e.getMessage());
        }
        modelManager.close();
    }
}
