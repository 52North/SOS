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

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.sos.extensions.model.Model;
import org.n52.sos.extensions.model.ModelManager;

/**
 * Test class to prepare Hydrology Networks.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class HydrologyModelingGeneratorTest 
{
    private static final Logger LOG = Logger.getLogger(HydrologyModelingGeneratorTest.class.toString());  
    
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
        
    /** Prepare Hydraulic networks */
    @Test
    public void prepareHydraulicModels() 
    {
        ModelManager modelManager = new ModelManager();
        
        try 
        {
            String warningMsg = String.format("Hydraulic workspace not loaded from settings, file='%s'", DEFAULT_SETTINGS_FILE);
            assertTrue(warningMsg, modelManager.loadSettings(DEFAULT_SETTINGS_FILE));
                        
            for (Model model : modelManager.getModels())
            {
                if (model.prepareObject())
                {                    
                    LOG.log(Level.INFO, String.format("Hydraulic model '%s' successly created!", model.getName()));
                }
                else
                {
                    LOG.log(Level.SEVERE, String.format("Hydraulic model '%s' creation failed!", model.getName()));                    
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
