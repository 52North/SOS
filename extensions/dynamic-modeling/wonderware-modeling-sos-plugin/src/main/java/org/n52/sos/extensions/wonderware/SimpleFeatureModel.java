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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;

import org.n52.sos.extensions.MeasureSet;
import org.n52.sos.extensions.ObservableContextArgs;
import org.n52.sos.extensions.ObservableObject;
import org.n52.sos.extensions.model.AbstractModel;
import org.n52.sos.extensions.util.FileUtils;

/**
 * Implements an Observable model using a Wonderware data provider with a SimpleFeatureStore as FeatureOfInterest provider.
 *  
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class SimpleFeatureModel extends AbstractModel
{
    private static final Logger LOG = Logger.getLogger(SimpleFeatureModel.class.toString());    
    
    private String databaseDriverClass;
    private String databaseConnectionUrl;
    private String featureStoreUrl;
    private SimpleFeatureSource featureSource;
    private String featureKey;
    private List<SimpleObservableAttribute> attributeList = new ArrayList<SimpleObservableAttribute>();    
    
    /**
     * Load the configuration data from the specified settings entry.
     */
    @Override
    public boolean loadSettings(String settingsFileName, org.w3c.dom.Element rootEntry, org.w3c.dom.Element modelEntry)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        if (super.loadSettings(settingsFileName, rootEntry, modelEntry))
        {
            NodeList nodeList = modelEntry.getChildNodes();
            
            for (int i = 0, icount = nodeList.getLength(); i < icount; i++)
            {
                Node node = nodeList.item(i);
                if (node.getNodeType()!=Node.ELEMENT_NODE) continue;
                String nodeName = node.getNodeName();
                
                if (nodeName.equalsIgnoreCase("databaseDriverClass"))
                {
                    databaseDriverClass = node.getTextContent();
                }
                else
                if (nodeName.equalsIgnoreCase("databaseConnectionUrl"))
                {
                    databaseConnectionUrl = node.getTextContent();
                }
                else
                if (nodeName.equalsIgnoreCase("featureStoreUrl"))
                {
                    featureStoreUrl = node.getTextContent();
                    
                    java.net.URI uri = FileUtils.resolveAbsoluteURI(featureStoreUrl, settingsFileName);                    
                    if (uri!=null)
                    {
                        File file = new File(uri);
                        featureStoreUrl = file.getAbsolutePath();
                    }
                }
                else
                if (nodeName.equalsIgnoreCase("featureKey"))
                {
                    featureKey = node.getTextContent();
                }
                else
                if (nodeName.equalsIgnoreCase("attributes"))
                {
                    NodeList nodeList_j = ((org.w3c.dom.Element)node).getElementsByTagName("attribute");
                    
                    for (int j = 0, jcount = nodeList_j.getLength(); j < jcount; j++)
                    {                        
                        SimpleObservableAttribute attribute = new SimpleObservableAttribute();
                        
                        node = nodeList_j.item(j);
                        node = node.getFirstChild();
                        
                        while ((node = node.getNextSibling())!=null)
                        {
                            if (node.getNodeType()!=Node.ELEMENT_NODE) continue;
                            nodeName = node.getNodeName();
                            
                            if (nodeName.equalsIgnoreCase("attributeName"))
                            {
                                attribute.name = node.getTextContent();
                            }
                            else
                            if (nodeName.equalsIgnoreCase("fieldId"))
                            {
                                attribute.fieldId = node.getTextContent();
                            }
                            else
                            if (nodeName.equalsIgnoreCase("dateFrom"))
                            {
                                java.util.Date dateTime = new java.util.Date();
                                try { dateTime = simpleDateFormat.parse(node.getTextContent()); } catch (ParseException e) { LOG.severe(e.getMessage()); } 
                                attribute.dateFrom = dateTime;
                            }
                            else
                            if (nodeName.equalsIgnoreCase("dateTo"))
                            {
                                java.util.Date dateTime = null;
                                String tempText = node.getTextContent();
                                if (!tempText.toLowerCase().contains("now")) try { dateTime = simpleDateFormat.parse(node.getTextContent()); } catch (ParseException e) { LOG.severe(e.getMessage()); } 
                                attribute.dateTo = dateTime;
                            }
                            else
                            if (nodeName.equalsIgnoreCase("stepTime"))
                            {
                                attribute.stepTime = Long.parseLong(node.getTextContent());
                            }
                            else
                            if (nodeName.equalsIgnoreCase("retrievalMode"))
                            {
                                attribute.retrievalMode = node.getTextContent();
                            }
                            else
                            if (nodeName.equalsIgnoreCase("retrievalAlignment"))
                            {
                                attribute.retrievalAlignment = RetrievalAlignment.valueOf(node.getTextContent());
                            }                            
                        }
                        attributeList.add(attribute);
                    }                    
                }
            }
            return !com.google.common.base.Strings.isNullOrEmpty(databaseConnectionUrl) && !com.google.common.base.Strings.isNullOrEmpty(featureStoreUrl);
        }
        return false;
    }
       
    /**
     * Prepare the data structure managed.
     */
    @Override
    public boolean prepareObject() throws RuntimeException
    {
        return true;
    }
    
    /**
     * Returns the current ObservableModel managed.
     */
    private SimpleFeatureModel getObservableModel()
    {
        return this;
    }
    
    /**
     * Returns the related FeatureSource of the specified feature connection string.
     */
    private SimpleFeatureSource calculateFeatureSource(String featureStoreUrl) throws IOException
    {
        if (this.featureSource!=null) return this.featureSource;
        
        if (FileUtils.safeExistFile(featureStoreUrl))
        {
            File file = new File(featureStoreUrl);
            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            return this.featureSource = featureSource;
        }
        else
        {
            Map<String,java.io.Serializable> params = new HashMap<String,java.io.Serializable>();
            
            for (String parseItems : featureStoreUrl.split(";"))
            {
                String[] tempArray = parseItems.split("=");                
                params.put(tempArray[0], tempArray[1]);
            }            
            for (Iterator<DataStoreFactorySpi> it = DataStoreFinder.getAvailableDataStores(); it.hasNext(); )
            {
                DataStoreFactorySpi factory = it.next();
                
                if (factory.canProcess(params))
                {
                    DataStore store = factory.createNewDataStore(params);
                    String[] typeNames = store.getTypeNames();
                    return this.featureSource = store.getFeatureSource(typeNames[0]);
                }
            }
            return null;
        }
    }    
    
    /**
     * Enumerate the available Observable Object collection from the specified filter criteria.
     * 
     * @param observableContextArgs: Information context of a request to fetch objects.
     * <p>With:
     * @param objectId: Object ID or Name from who recover data (Optional).
     * @param envelope: Spatial envelope filter.
     * @param dateFrom: Minimum valid phenomenon DateTime.
     * @param dateTo: Maximum valid phenomenon DateTime.
     * @param flags: Flags of the request.
     * 
     * @return ObservableObject collection that matches the specified filter criteria.
     */
    public Iterable<ObservableObject> enumerateObservableObjects(final ObservableContextArgs observableContextArgs) throws RuntimeException
    {
        return new Iterable<ObservableObject>() 
        {
            public final Iterator<ObservableObject> iterator() 
            {                
                try
                {
                    SimpleFeatureSource featureSource = calculateFeatureSource(featureStoreUrl);
                    if (featureSource==null) throw new RuntimeException(String.format("Unsupported FeatureStore from the ConnectionString '%s'", featureStoreUrl));
                    
                    final String objectId = observableContextArgs.objectId;
                    final ReferencedEnvelope envelope = observableContextArgs.envelope;
                    final Date dateTimeFrom = observableContextArgs.dateFrom;
                    final Date dateTimeTo = observableContextArgs.dateTo;
                    
                    List<SimpleObservableAttribute> databaseAttributeList = new ArrayList<SimpleObservableAttribute>();
                    Date dateFrom2 = dateTimeFrom;
                    Date dateTo2 = dateTimeTo;
                    boolean makeFullCursor = dateFrom2==ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG && dateTo2==ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
                    
                    for (SimpleObservableAttribute attribute : attributeList)
                    {
                        SimpleObservableAttribute databaseAttribute = new SimpleObservableAttribute(attribute);
                        
                        if (dateFrom2!=ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG)
                        {
                            databaseAttribute.dateFrom = dateFrom2;
                        }
                        if (dateTo2!=ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG)
                        {
                            databaseAttribute.dateTo = dateTo2;
                        }                        
                        databaseAttributeList.add(databaseAttribute);
                    }
                    return makeFullCursor 
                        ? 
                        new WonderwareFullObservableObjectCursor(getObservableModel(), objectId, databaseDriverClass, databaseConnectionUrl, featureSource, featureKey, envelope, databaseAttributeList)
                        :
                        new WonderwareObservableObjectCursor(getObservableModel(), objectId, databaseDriverClass, databaseConnectionUrl, featureSource, featureKey, envelope, databaseAttributeList);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    /**
     * Enumerate the available Measures from the specified filter criteria.
     * 
     * @param observableContextArgs: Information context of a request to fetch objects.
     * <p>With:
     * @param objectId: Object ID or Name from who recover data (Optional).
     * @param envelope: Spatial envelope filter.
     * @param dateFrom: Minimum valid phenomenon DateTime.
     * @param dateTo: Maximum valid phenomenon DateTime.
     * @param flags: Flags of the request.
     * 
     * @return ObservableResultSet collection that matches the specified filter criteria.
     */
    public Iterable<MeasureSet> enumerateMeasures(final ObservableContextArgs observableContextArgs) throws RuntimeException
    {
        return new Iterable<MeasureSet>() 
        {
            public final Iterator<MeasureSet> iterator() 
            {
                try
                {
                    SimpleFeatureSource featureSource = calculateFeatureSource(featureStoreUrl);
                    if (featureSource==null) throw new RuntimeException(String.format("Unsupported FeatureStore from the ConnectionString '%s'", featureStoreUrl));
                    
                    final String objectId = observableContextArgs.objectId;
                    final ReferencedEnvelope envelope = observableContextArgs.envelope;
                    final Date dateTimeFrom = observableContextArgs.dateFrom;
                    final Date dateTimeTo = observableContextArgs.dateTo;
                    final int flags = observableContextArgs.flags;
                    
                    List<SimpleObservableAttribute> databaseAttributeList = new ArrayList<SimpleObservableAttribute>();
                    Date dateFrom2 = dateTimeFrom;
                    Date dateTo2 = dateTimeTo;
                    
                    for (SimpleObservableAttribute attribute : attributeList)
                    {
                        SimpleObservableAttribute databaseAttribute = new SimpleObservableAttribute(attribute);
                        
                        if (dateFrom2!=ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG)
                        {
                            databaseAttribute.dateFrom = dateFrom2;
                        }
                        if (dateTo2!=ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG)
                        {
                            databaseAttribute.dateTo = dateTo2;
                        }
                        databaseAttributeList.add(databaseAttribute);
                    }
                    return new WonderwareObservableMeasureCursor(getObservableModel(), objectId, databaseDriverClass, databaseConnectionUrl, featureSource, featureKey, envelope, databaseAttributeList, flags);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
