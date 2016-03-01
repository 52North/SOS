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
package org.n52.sos.extensions.hydrology.epanet;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.n52.sos.extensions.Measure;
import org.n52.sos.extensions.MeasureSet;
import org.n52.sos.extensions.ObservableAttribute;
import org.n52.sos.extensions.ObservableModel;
import org.n52.sos.extensions.ObservableObject;
import org.n52.sos.extensions.hydrology.epanet.io.output.EpanetDatabaseComposer;

/**
 * Implements an enumerable cursor of MeasureSet entities from a SQL-command filter.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
class EpanetObservableMeasureCursor extends EpanetObservableDataCursor<MeasureSet>
{
    private ConcurrentLinkedQueue<MeasureSet> measureQueue = new ConcurrentLinkedQueue<MeasureSet>();
    
    /** 
     * Creates a new EpanetObservableMeasureCursor object.
     * 
     * @param observableModel: Reference to the ObservableModel owner.
     * @param objectFilterPattern: Data filter pattern of valid type:object.property entities to return (e.g. '*:*.*[;...]').
     * @param envelope: Spatial envelope filter.
     * @param dateFrom: Minimum valid phenomenon DateTime.
     * @param dateTo: Maximum valid phenomenon DateTime.
     */
    public EpanetObservableMeasureCursor(ObservableModel observableModel, String sqliteFileName, CoordinateReferenceSystem coordinateSystem, String objectFilterPattern, ReferencedEnvelope envelope, java.util.Date dateFrom, java.util.Date dateTo, String whereClause) throws RuntimeException
    {
        super(observableModel, sqliteFileName, coordinateSystem, objectFilterPattern, envelope, dateFrom, dateTo, whereClause);
    }
    
    /** 
     * Read the MeasureSet from the specified ResultSet.
     */
    @Override
    protected MeasureSet readObservableObject(ResultSet recordset)
    {
        MeasureSet measureSet = measureQueue.poll();
        if (measureSet!=null) return measureSet;
        
        try
        {
            ObservableObject theObject = readMainObservableObjectInformation(recordset);
            if (theObject==null) return null;
            
            int propertyIndex = 1;
            propertyIndex += EpanetDatabaseComposer.makeTableDefinition(currentObjectType==1 ? EpanetDatabaseComposer.NODE_TABLENAME : EpanetDatabaseComposer.LINK_TABLENAME, true).size();
            
            String reportTableName = currentObjectType==1 ? EpanetDatabaseComposer.REPORT_NODE_TABLENAME : EpanetDatabaseComposer.REPORT_LINK_TABLENAME;
            List<Map.Entry<String,Class<?>>> tableDef = EpanetDatabaseComposer.makeTableDefinition(reportTableName, false);

            List<MeasureSet> measureList = new ArrayList<MeasureSet>();            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            java.util.Date phenomenonTime = null;
            java.util.Date startTime = null;
            java.util.Date finalTime = null;
            long numSteps = 0;
            
            // Read information of attributes.
            do
            {
                if (!theObject.objectName.equalsIgnoreCase(recordset.getString(1))) //-> Skip next object ?
                {
                    break;
                }
                
                phenomenonTime = simpleDateFormat.parse(recordset.getString(propertyIndex+1));
                if (startTime==null) startTime = finalTime = phenomenonTime; else finalTime = phenomenonTime;
                numSteps++;
                
                for (int itemIndex = 0, itemCount = tableDef.size()-2; itemIndex < itemCount; itemIndex++)
                {
                    Measure measureValue = new Measure();
                    measureValue.phenomenonTime = phenomenonTime;
                    if ((measureValue.value = recordset.getObject(propertyIndex+2+itemIndex))==null) continue;
                    
                    if (numSteps==1)
                    {
                        measureSet = new MeasureSet();
                        measureSet.ownerObject = theObject;
                        measureList.add(measureSet);
                    }
                    measureSet = measureList.get(itemIndex);
                    measureSet.measures.add(measureValue);
                }
            }
            while (recordset.next());
            
            // Save information of attributes.
            if (numSteps>0)
            {
                long stepTime = (finalTime.getTime()-startTime.getTime()) / (numSteps-1);
                int itemIndex = 0;
                
                for (Map.Entry<String,Class<?>> entry : tableDef)
                {
                    if (itemIndex>=2)
                    {
                        ObservableAttribute attribute = new ObservableAttribute();
                        attribute.name = entry.getKey();
                        attribute.description = "Provides measures of attribute '"+entry.getKey()+"' of object type '"+theObject.objectType+"'";
                        attribute.dateFrom = startTime;
                        attribute.dateTo = finalTime;
                        attribute.stepTime = stepTime;
                        attribute.units = uoms.get(attribute.name);
                        theObject.attributes.add(attribute);
                        
                        measureList.get(itemIndex-2).attribute = attribute;
                    }
                    itemIndex++;
                }
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
}
