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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

import org.n52.sos.extensions.Measure;
import org.n52.sos.extensions.MeasureSet;
import org.n52.sos.extensions.ObservableAttribute;
import org.n52.sos.extensions.ObservableModel;
import org.n52.sos.extensions.ObservableObject;
import org.n52.sos.extensions.model.AbstractModel;

/**
 * Implements an enumerable cursor of MeasureSet entities from a Wonderware SQL-command filter.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
class WonderwareObservableMeasureCursor extends WonderwareObservableDataCursor<MeasureSet>
{
    private ConcurrentLinkedQueue<MeasureSet> measureQueue = new ConcurrentLinkedQueue<MeasureSet>();
    
    /** 
     * Creates a new WonderwareObservableMeasureCursor object.
     * 
     * @param observableModel: Reference to the ObservableModel owner.
     * @param objectId: Object ID or Name from who recover data (Optional).
     * @param databaseDriverClass: Related database driver name to use. 
     * @param databaseConnectionUrl: Related database connection string which provides the observable values.
     * @param featureSource: Related FeatureSource which provides the main FeatureOfInterest collection.
     * @param featureKey: FieldName to related the FeatureSource with the database.
     * @param envelope: Spatial envelope filter.   
     * @param attributeList: List of attributes to read from the database.
     */
    public WonderwareObservableMeasureCursor(ObservableModel observableModel, String objectId, String databaseDriverClass, String databaseConnectionUrl, SimpleFeatureSource featureSource, String featureKey, ReferencedEnvelope envelope, List<SimpleObservableAttribute> attributeList)
    {
        super(observableModel, objectId, databaseDriverClass, databaseConnectionUrl, featureSource, featureKey, envelope, attributeList);
    }
    
    /** 
     * Read the MeasureSet from the specified ResultSet.
     */
    @Override
    protected MeasureSet readObservableObject(SimpleFeature feature, List<SimpleObservableAttribute> databaseAttributeList)
    {        
        try
        {
            String objectType = feature.getName().getLocalPart();
            String objectName = feature.getAttribute(featureFieldKey).toString();
            
            ObservableObject theObject = new ObservableObject();
            theObject.objectType = objectType;
            theObject.objectName = objectName;
            theObject.featureOfInterest = feature;
            theObject.description = "Historic and live observable properties of the object '"+objectName+"' of type '"+objectType+"'";
            
            List<MeasureSet> measureList = new ArrayList<MeasureSet>();
            ObservableAttribute attribute = null;
            MeasureSet measureSet = null;
            
            // Read information of attributes.
            for (SimpleObservableAttribute databaseAttribute : databaseAttributeList)
            {
                ResultSet recordset = createRecordsetOfSimpleObservableAttribute(feature, databaseAttribute);
                if (recordset==null) continue;
                
                java.util.Date startDate = databaseAttribute.retrievalStartDateTime();
                java.util.Date finalDate = databaseAttribute.retrievalFinalDateTime();
                
                do
                {
                    Measure measureValue = new Measure();
                    measureValue.phenomenonTime = new java.util.Date(recordset.getTimestamp(4).getTime());
                    measureValue.value = recordset.getObject(5);

                    if (attribute==null)
                    {
                        attribute = new ObservableAttribute();
                        attribute.name = databaseAttribute.name;
                        attribute.description = recordset.getString(3);
                        attribute.dateFrom = startDate;
                        attribute.dateTo = finalDate;
                        attribute.stepTime = databaseAttribute.stepTime;
                        attribute.units = recordset.getString(6); 
                        theObject.attributes.add(attribute);
                    }
                    if (measureSet==null)
                    {
                        measureSet = new MeasureSet();
                        measureSet.ownerObject = theObject;
                        measureSet.attribute = attribute;
                        measureList.add(measureSet);
                    }
                    measureSet.measures.add(measureValue);
                }
                while (recordset.next());
                
                org.n52.sos.extensions.util.JDBCUtils.close(recordset);
                recordset = null;
            }
            
            // Set related feature URLs.
            if (theObject.featureOfInterest!=null && currentModel instanceof AbstractModel)
            {
                AbstractModel abstractModel = (AbstractModel)currentModel;
                abstractModel.populateRelatedFeatureUrls(theObject);
            }
            
            // Save information of measures.
            measureQueue.addAll(measureList);
            measureList.clear();
            measureList = null;
            
            return measureQueue.poll();
        }
        catch (Exception e) 
        {
            LOG.severe(e.getMessage());
            return null;
        }
    }
    
    /**
     * Returns {@code true} if the iteration has more elements.
     */
    @Override
    public boolean hasNext() 
    {
        return !measureQueue.isEmpty() || super.hasNext();
    }
    /**
     * Returns the next element in the iteration.
     */
    @Override
    public MeasureSet next() 
    {
        if (!measureQueue.isEmpty())
        {
            MeasureSet theObject = measureQueue.poll();
            currentObject = null;
            return theObject;
        }
        return super.next();
    }    
}
