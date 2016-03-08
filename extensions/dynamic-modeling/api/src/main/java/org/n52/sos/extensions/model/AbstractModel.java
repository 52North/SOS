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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.n52.sos.extensions.ObservableObject;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.BoundingBox;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * Implements a partial Model class with Objects that provide Observable Attributes.
 *  
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public abstract class AbstractModel implements Model
{
    protected String name = "";
    protected String description = "";
    protected List<String> relatedFeatureExpressions = new ArrayList<String>();
    
    /** Gets the name of this model. */
    @Override
    public String getName()
    {
        return name;
    }
    /**
     * Get the description of this data model.
     */
    @Override
    public String getDescription()
    {
        return description;
    }
    
    /**
     * Load the configuration data from the specified settings entry. 
     */
    @Override
    public boolean loadSettings(String settingsFileName, org.w3c.dom.Element rootEntry, org.w3c.dom.Element modelEntry)
    {
        NodeList nodeList = modelEntry.getChildNodes();
        
        for (int i = 0, icount = nodeList.getLength(); i < icount; i++)
        {
            Node node = nodeList.item(i);
            if (node.getNodeType()!=Node.ELEMENT_NODE) continue;
            String nodeName = node.getNodeName();
            
            if (nodeName.equalsIgnoreCase("name"))
            {
                name = node.getTextContent();
            }
            else
            if (nodeName.equalsIgnoreCase("description"))
            {
                description = node.getTextContent();
            }
            else
            if (nodeName.equalsIgnoreCase("relatedFeatureExpression"))
            {
                String tempText = node.getTextContent();
                if (!com.google.common.base.Strings.isNullOrEmpty(tempText)) relatedFeatureExpressions.add(tempText);
            }
        }
        return !com.google.common.base.Strings.isNullOrEmpty(name);
    }
    
    /**
     * Configure the RelatedFeatureUrls of the specified ObservableObject using the current settings of this model.
     */
    public boolean populateRelatedFeatureUrls(ObservableObject theObject)
    {
        if (relatedFeatureExpressions!=null && relatedFeatureExpressions.size()>0 && theObject.featureOfInterest!=null)
        {
            SimpleFeature feature = theObject.featureOfInterest;
            
            for (int i = 0, icount = relatedFeatureExpressions.size(); i < icount; i++)
            {
                String expression = relatedFeatureExpressions.get(i);
                expression = expression.replaceAll("(?i)\\$ObjectID\\$", feature.getID());
                
                BoundingBox bbox = feature.getBounds();
                expression = expression.replaceAll("(?i)\\$bbox\\$", String.format(Locale.ENGLISH, "BBOX=%.8f,%.8f,%.8f,%.8f", bbox.getMinX(),bbox.getMinY(),bbox.getMaxX(),bbox.getMaxY()));
                
                for (Property property : feature.getProperties()) 
                {
                    if (property.getValue()!=null) expression = expression.replaceAll("(?i)\\$"+property.getName()+"\\$", property.getValue().toString());
                }
                theObject.relatedFeatureUrls.add(expression);
            }
            return true;
        }
        return false;
    }
}
