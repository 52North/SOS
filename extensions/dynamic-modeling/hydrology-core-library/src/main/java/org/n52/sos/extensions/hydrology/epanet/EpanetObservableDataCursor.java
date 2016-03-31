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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.n52.sos.extensions.ObservableObject;
import org.n52.sos.extensions.ObservableModel;
import org.n52.sos.extensions.hydrology.epanet.io.output.EpanetDatabaseComposer;
import org.n52.sos.extensions.model.AbstractModel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

/**
 * Basic template for an enumerable cursor of entities from a SQL-command filter.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
abstract class EpanetObservableDataCursor<T> implements Iterator<T>
{
    protected static final Logger LOG = Logger.getLogger(EpanetObservableDataCursor.class.toString());
    
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private SimpleFeatureType nodeFeatureType = null;
    private SimpleFeatureType linkFeatureType = null;
    private String nodeWhereClause = null;
    private String linkWhereClause = null;
    protected Map<String,String> uoms = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
    
    private Connection connection;
    private Statement statement;
    private ResultSet recordset;
    
    protected ObservableModel currentModel;
    protected int currentObjectType; /* 0=Unassigned, 1=Nodes, 2=Links */
    protected T currentObject;
    protected int defaultEpsgCode = 0;
    
    /** 
     * Creates a new EpanetObservableDataCursor object.
     * 
     * @param observableModel: Reference to the ObservableModel owner.
     * @param objectFilterPattern: Data filter pattern of valid type:object.property entities to return (e.g. '*:*.*[;...]').
     * @param envelope: Spatial envelope filter.
     * @param dateFrom: Minimum valid phenomenon DateTime.
     * @param dateTo: Maximum valid phenomenon DateTime.
     */
    public EpanetObservableDataCursor(ObservableModel observableModel, String sqliteFileName, CoordinateReferenceSystem coordinateSystem, String objectFilterPattern, ReferencedEnvelope envelope, java.util.Date dateFrom, java.util.Date dateTo, String whereClause) throws RuntimeException
    {
        currentObjectType = 0;
        currentObject = null;
        currentModel = observableModel;
        
        // Fix the CoordinateReferenceSystem in use!
        if (coordinateSystem==null)
        {
            try { coordinateSystem = CRS.decode("EPSG:4326"); } catch (Exception e) { coordinateSystem = DefaultGeographicCRS.WGS84; }
        }
        if (coordinateSystem!=null)
        {
            try { defaultEpsgCode = CRS.lookupEpsgCode(coordinateSystem, false); } catch (Exception e) { defaultEpsgCode = 4326; }
        }
        
        // Initialize SimpleFeatureTypeBuilder of network node objects.        
        SimpleFeatureTypeBuilder nodeFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        nodeFeatureTypeBuilder.setName("Node");
        nodeFeatureTypeBuilder.setCRS(coordinateSystem); // <- Coordinate reference system
        nodeFeatureTypeBuilder.setDefaultGeometry("geometry");
        for (Map.Entry<String,Class<?>> entry : EpanetDatabaseComposer.makeTableDefinition(EpanetDatabaseComposer.NODE_TABLENAME, false))
        {
            nodeFeatureTypeBuilder.add(entry.getKey(), entry.getValue());
        }
        nodeFeatureTypeBuilder.add("geometry", Point.class);        

        // Initialize SimpleFeatureTypeBuilder of network link objects.
        SimpleFeatureTypeBuilder linkFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
        linkFeatureTypeBuilder.setName("Link");
        linkFeatureTypeBuilder.setCRS(coordinateSystem); // <- Coordinate reference system
        linkFeatureTypeBuilder.setDefaultGeometry("geometry");
        for (Map.Entry<String,Class<?>> entry : EpanetDatabaseComposer.makeTableDefinition(EpanetDatabaseComposer.LINK_TABLENAME, false))
        {
            linkFeatureTypeBuilder.add(entry.getKey(), entry.getValue());
        }
        linkFeatureTypeBuilder.add("geometry", LineString.class);
        
        nodeFeatureType = nodeFeatureTypeBuilder.buildFeatureType();
        linkFeatureType = linkFeatureTypeBuilder.buildFeatureType();
        
        // -------------------------------------------------------------------------------------------
        // Initialize database resources.
        
        if ((connection = org.n52.sos.extensions.hydrology.util.JDBCUtils.openSqliteConnection(sqliteFileName))!=null) try
        {
            String tempWhereClause = null;
                        
            // ... compose the final WHERE clause using the specified object data filters.
            if (!com.google.common.base.Strings.isNullOrEmpty(objectFilterPattern) && com.google.common.base.Strings.isNullOrEmpty(whereClause))
            {
                tempWhereClause = ObservableObject.composeObjectFilterWhereClause(objectFilterPattern, "a.enet_type", "a.object_id");
                nodeWhereClause = composeComplexWhere(nodeWhereClause, tempWhereClause);
                linkWhereClause = composeComplexWhere(linkWhereClause, tempWhereClause);
            }
            if (dateFrom!=ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG && dateTo!=ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG)
            {
                tempWhereClause = String.format(Locale.ENGLISH, "b.step_time>='%s' AND b.step_time<='%s'", getClockTimeAsString(dateFrom), getClockTimeAsString(dateTo));
                nodeWhereClause = composeComplexWhere(nodeWhereClause, tempWhereClause);
                linkWhereClause = composeComplexWhere(linkWhereClause, tempWhereClause);
            }
            if (envelope!=ObservableObject.UNDEFINED_ENVELOPE_FILTER_FLAG)
            {
                double x1 = envelope.getMinX();
                double y1 = envelope.getMinY();
                double x2 = envelope.getMaxX();
                double y2 = envelope.getMaxY();
                
                if (coordinateSystem!=null && envelope.getCoordinateReferenceSystem()!=null && !CRS.equalsIgnoreMetadata(coordinateSystem,envelope.getCoordinateReferenceSystem())) try
                {
                    BoundingBox bbox = envelope.toBounds(coordinateSystem);
                    x1 = bbox.getMinX();
                    y1 = bbox.getMinY();
                    x2 = bbox.getMaxX();
                    y2 = bbox.getMaxY();
                }
                catch (Exception e)
                {
                    LOG.severe(e.getMessage());
                }
                nodeWhereClause = composeComplexWhere(nodeWhereClause, String.format(Locale.ENGLISH, "a.x >=%.6f AND a.y >=%.6f AND a.x <=%.6f AND a.y <=%.6f", x1,y1,x2,y2));                
                linkWhereClause = composeComplexWhere(linkWhereClause, String.format(Locale.ENGLISH, "a.x2>=%.6f AND a.x1<=%.6f AND a.y2>=%.6f AND a.y1<=%.6f", x1,x2,y1,y2));
            }
            if (!com.google.common.base.Strings.isNullOrEmpty(whereClause))
            {
                nodeWhereClause = composeComplexWhere(nodeWhereClause, whereClause);
                linkWhereClause = composeComplexWhere(linkWhereClause, whereClause);
            }
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            
            loadUnitsOfMeatureTable();
        }
        catch (SQLException e) 
        {
            throw new RuntimeException(e);
        }
    }
    
    /** 
     * Returns a complex SQL-where clause concatenating the specified single clauses when needed.
     */
    private static String composeComplexWhere(String whereClause, String appendWhereClause)
    {
        if (!com.google.common.base.Strings.isNullOrEmpty(appendWhereClause))
        {
            if (!com.google.common.base.Strings.isNullOrEmpty(whereClause))
            {
                appendWhereClause += " AND (" + whereClause + ")";
            }
            return appendWhereClause;
        }
        return whereClause;
    }
    
    /** 
     * Convert the specified seconds value to a valid text representation of a time object. 
     */
    protected static String getClockTimeAsString(java.util.Date dateTime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);        
        return String.format("%02d:%02d:%02d",h,m,s);        
    }
    
    /**
     * Load the units of measures entries.
     */
    private boolean loadUnitsOfMeatureTable() throws SQLException
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT ");
        stringBuilder.append(EpanetDatabaseComposer.makeSelectFieldsClause(EpanetDatabaseComposer.UNITS_TABLENAME, "", false));
        stringBuilder.append(" FROM ");
        stringBuilder.append(EpanetDatabaseComposer.UNITS_TABLENAME);
        
        if ((recordset = statement.executeQuery(stringBuilder.toString()))!=null)
        {
            while (recordset.next())
            {
                String object_id = recordset.getString(1);
                String unit_name = recordset.getString(2);
                uoms.put(object_id,unit_name);
            }
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(recordset);
            recordset = null;
            return true;
        }
        return false;
    }
    
    /** 
     * Read the main ObservableObject information from the specified ResultSet.
     */
    protected ObservableObject readMainObservableObjectInformation(ResultSet recordset) throws SQLException
    {
        if (recordset.isAfterLast()) 
            return null;
        
        ObservableObject theObject = new ObservableObject();
        
        theObject.objectName = recordset.getString(1);
        theObject.objectType = recordset.getString(2);
        theObject.description = "Hydraulic observable properties of the object '"+theObject.objectName+"' of type '"+theObject.objectType+"'";
        
        SimpleFeature feature = null;
        int propertyIndex = 1;
        
        if (currentObjectType==1) //-> Reading a node object.
        {
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(nodeFeatureType);
            
            for (Map.Entry<String,Class<?>> entry : EpanetDatabaseComposer.makeTableDefinition(EpanetDatabaseComposer.NODE_TABLENAME,false))
            {
                if (entry.getValue()!=null) featureBuilder.add(recordset.getObject(propertyIndex));
                propertyIndex++;
            }
            double x = recordset.getDouble(propertyIndex++);
            double y = recordset.getDouble(propertyIndex++);
            featureBuilder.add(GEOMETRY_FACTORY.createPoint(new Coordinate(x,y)));
            
            feature = featureBuilder.buildFeature(theObject.objectName);
         }
         else
         if (currentObjectType==2)
         {
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(linkFeatureType);
            
            for (Map.Entry<String,Class<?>> entry : EpanetDatabaseComposer.makeTableDefinition(EpanetDatabaseComposer.LINK_TABLENAME,false))
            {
                if (entry.getValue()!=null) featureBuilder.add(recordset.getObject(propertyIndex));
                propertyIndex++;
            }
            double x1 = recordset.getDouble(propertyIndex++);
            double y1 = recordset.getDouble(propertyIndex++);
            double x2 = recordset.getDouble(propertyIndex++);
            double y2 = recordset.getDouble(propertyIndex++);
            featureBuilder.add(GEOMETRY_FACTORY.createLineString(new Coordinate[]{new Coordinate(x1,y1),new Coordinate(x2,y2)}));
            
            feature = featureBuilder.buildFeature(theObject.objectName);
        }
        theObject.featureOfInterest = feature;
        
        if (theObject.featureOfInterest!=null)
        {
            Geometry geometry = ((Geometry)feature.getDefaultGeometryProperty().getValue());
            if (geometry.getSRID()==0) geometry.setSRID(defaultEpsgCode);
        }
        if (theObject.featureOfInterest!=null && currentModel instanceof AbstractModel)
        {
            AbstractModel abstractModel = (AbstractModel)currentModel;
            abstractModel.populateRelatedFeatureUrls(theObject);
        }
        return theObject;
    }
    
    /** 
     * Read the Observable entity from the specified ResultSet.
     */
    protected abstract T readObservableObject(ResultSet recordset);
    
    /**
     * Returns {@code true} if the iteration has more elements.
     */
    public boolean hasNext() 
    {
        try
        {
            currentObject = null;
            
            // Initialize the database cursor (Reading now node or link objects?).
            if (recordset==null && currentObjectType<=1)
            {
                String networkTableName = currentObjectType==0 ? EpanetDatabaseComposer.NODE_TABLENAME : EpanetDatabaseComposer.LINK_TABLENAME;
                String resultsTableName = currentObjectType==0 ? EpanetDatabaseComposer.REPORT_NODE_TABLENAME : EpanetDatabaseComposer.REPORT_LINK_TABLENAME;
                String queryWhereClause = currentObjectType==0 ? nodeWhereClause : linkWhereClause;
                currentObjectType++;
                
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT ");
                stringBuilder.append(EpanetDatabaseComposer.makeSelectFieldsClause(networkTableName, "a", true));
                stringBuilder.append(",");
                stringBuilder.append(EpanetDatabaseComposer.makeSelectFieldsClause(resultsTableName, "b", true));
                stringBuilder.append(" FROM ");
                stringBuilder.append(networkTableName + " a");
                stringBuilder.append(" LEFT JOIN ");
                stringBuilder.append(resultsTableName + " b");
                stringBuilder.append(" ON ");
                stringBuilder.append(" a.object_id=b.object_id");
                
                if (!com.google.common.base.Strings.isNullOrEmpty(queryWhereClause))
                {
                    stringBuilder.append(" WHERE (");
                    stringBuilder.append(queryWhereClause);
                    stringBuilder.append(")");
                }
                stringBuilder.append(" ORDER BY a.object_id;");
                
                if (!(recordset = statement.executeQuery(stringBuilder.toString())).next())
                {
                    if (currentObjectType==1)
                    {
                        org.n52.sos.extensions.hydrology.util.JDBCUtils.close(recordset);
                        recordset = null;
                        return this.hasNext();
                    }
                    org.n52.sos.extensions.hydrology.util.JDBCUtils.close(recordset);
                    org.n52.sos.extensions.hydrology.util.JDBCUtils.close(statement);
                    org.n52.sos.extensions.hydrology.util.JDBCUtils.close(connection);
                    return false;
                }
            }
            
            // Read a valid object when available.
            do
            {
                if ((currentObject = readObservableObject(recordset))!=null) 
                {
                    return true;
                }
            }
            while (recordset.next());
        }
        catch (SQLException e) 
        {
            LOG.severe(e.getMessage());
        }
        
        // Stop reading database ?
        if (currentObjectType==1)
        {
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(recordset);
            recordset = null;
            return this.hasNext();
        }        
        org.n52.sos.extensions.hydrology.util.JDBCUtils.close(recordset);
        org.n52.sos.extensions.hydrology.util.JDBCUtils.close(statement);
        org.n52.sos.extensions.hydrology.util.JDBCUtils.close(connection);
        return false;
    }
    /**
     * Returns the next element in the iteration.
     */
    public T next() 
    {
        T theObject = currentObject;
        currentObject = null;
        return theObject;
    }
    /**
     * Removes from the underlying collection the last element returned
     * by this iterator (optional operation).
     */
    public void remove() 
    {
        throw new UnsupportedOperationException("cannot remove from cursor");
    }
}
