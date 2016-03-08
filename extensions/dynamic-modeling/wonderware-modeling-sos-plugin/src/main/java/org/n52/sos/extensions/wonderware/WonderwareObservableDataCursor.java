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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.filter.text.cql2.CQL;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import org.n52.sos.extensions.ObservableModel;
import org.n52.sos.extensions.ObservableObject;
import org.n52.sos.extensions.util.FileUtils;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Basic template for an enumerable cursor of entities from a Wonderware SQL-command filter.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
abstract class WonderwareObservableDataCursor<T> implements Iterator<T>
{
    protected static final Logger LOG = Logger.getLogger(WonderwareObservableDataCursor.class.toString());
    
    /** Default SQL filter database. */
    private static final String DEFAULT_SQL_FILTER_FILENAME = "wonderware/select_data_wonderware.sql";
        
    /** Load the SQL-filter of the Wonderware entities database. */
    static
    {
        java.net.URI sqlfilterFile = FileUtils.resolveAbsoluteURI(DEFAULT_SQL_FILTER_FILENAME, WonderwareObservableDataCursor.class.getClassLoader());
        
        BufferedReader inputReader = null;
        InputStream inputStream = null;
                
        try
        {
            inputReader = new BufferedReader(new InputStreamReader(inputStream = sqlfilterFile.toURL().openStream()));
            
            // Read the SQL-batch from the resource.
            StringBuilder commandBuilder = new StringBuilder();
            String line = null;
            while ((line=inputReader.readLine())!=null) { if (line.trim().length()>0 && !line.startsWith("--")) commandBuilder.append(line); }
            
            wonderwareSqlFilter = commandBuilder.toString(); 
        }
        catch (IOException e)
        {
            LOG.severe(e.getMessage());
        }
        finally
        {
            if (inputStream!=null)
            {
                try { inputStream.close(); } catch (Exception e) { }
                inputStream = null;
            }
            if (inputReader!=null)
            {
                try { inputReader.close(); } catch (Exception e) { }
                inputReader = null;
            }            
        }
    }
    /** SQL-filter of the entities of the database. */
    private static String wonderwareSqlFilter; 
    
    private Connection connection;
    private Statement statement;
    
    protected ObservableModel currentModel;
    protected SimpleFeatureIterator featureIterator;    
    protected String featureFieldKey;
    protected List<SimpleObservableAttribute> databaseAttributeList;
    protected int defaultEpsgCode = 0;
    
    protected T currentObject;
    
    /** 
     * Creates a new WonderwareObservableDataCursor object.
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
    public WonderwareObservableDataCursor(ObservableModel observableModel, String objectId, String databaseDriverClass, String databaseConnectionUrl, SimpleFeatureSource featureSource, String featureKey, ReferencedEnvelope envelope, List<SimpleObservableAttribute> attributeList)
    {
        currentModel = observableModel;
        featureIterator = null;
        currentObject = null;
        featureFieldKey = featureKey;
        databaseAttributeList = attributeList;
        
        // Initialize data resources.
        try
        {
            FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2();
            Filter filter = null;
            
            if (envelope!=ObservableObject.UNDEFINED_ENVELOPE_FILTER_FLAG)
            {
                String geometryPropertyName = featureSource.getSchema().getGeometryDescriptor().getLocalName(); //-> usually "THE_GEOM"                
                filter = filterFactory.bbox(filterFactory.property(geometryPropertyName), envelope);
            }
            if (!com.google.common.base.Strings.isNullOrEmpty(objectId))
            {
                Filter whereFilter = CQL.toFilter(featureKey+"='"+objectId+"'");
                if (filter!=null) filter = filterFactory.and(filter, whereFilter); else filter = whereFilter;
            }
            SimpleFeatureCollection featureCollection = filter!=null ? featureSource.getFeatures(filter) : featureSource.getFeatures();
            featureIterator = featureCollection.features();
            defaultEpsgCode = CRS.lookupEpsgCode(featureSource.getSchema().getCoordinateReferenceSystem(), false);
            
            Class.forName(databaseDriverClass);
            connection = DriverManager.getConnection(databaseConnectionUrl);
            statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        }
        catch (Exception e) 
        { 
            throw new RuntimeException(e);
        }
    }
    
    /** 
     * Read the Observable entity from the specified ResultSet.
     */
    protected abstract T readObservableObject(SimpleFeature feature, List<SimpleObservableAttribute> databaseAttributeList) throws SQLException;
    
    /**
     * Creates a data cursor to the specified simple observable attribute
     */
    protected ResultSet createRecordsetOfSimpleObservableAttribute(SimpleFeature feature, SimpleObservableAttribute attribute) throws SQLException
    {        
        String attributeTagName = feature.getAttribute(attribute.fieldId).toString();
        
        if (!com.google.common.base.Strings.isNullOrEmpty(attributeTagName))
        {            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            java.util.Date startDate = attribute.retrievalStartDateTime();
            java.util.Date finalDate = attribute.retrievalFinalDateTime();
            Long queryCycleCount = attribute.retrievalCicleCount();
                        
            String commandText = wonderwareSqlFilter;
            commandText = commandText.replace("TAG_NAME_VALUE"   , "'"+attributeTagName+"'");
            commandText = commandText.replace("START_DATA_VALUE" , "'"+simpleDateFormat.format(startDate).toString()+"'");
            commandText = commandText.replace("END_DATA_VALUE"   , "'"+simpleDateFormat.format(finalDate).toString()+"'");
            commandText = commandText.replace("RETRIEVAL_VALUE"  , "'"+attribute.retrievalMode+"'");
            commandText = commandText.replace("CYCLE_COUNT_VALUE", queryCycleCount.toString());
            
            ResultSet recordset = null;
            if ((recordset = statement.executeQuery(commandText)).next())
            {
                return recordset;
            }
            org.n52.sos.extensions.util.JDBCUtils.close(recordset);
        }        
        return null;
    }
    
    /**
     * Returns {@code true} if the iteration has more elements.
     */
    public boolean hasNext() 
    {
        currentObject = null;
        if (featureIterator==null) return false;
        
        try
        {            
            // Read a valid object when available.
            while (featureIterator.hasNext())
            {
                SimpleFeature feature = featureIterator.next();
                
                Geometry geometry = ((Geometry)feature.getDefaultGeometryProperty().getValue());
                if (geometry.getSRID()==0) geometry.setSRID(defaultEpsgCode);
                
                if ((currentObject = readObservableObject(feature, databaseAttributeList))!=null) return true;
            }
        }
        catch (SQLException e) 
        {
            LOG.severe(e.getMessage());
        }
        
        if (featureIterator!=null)
        {
            featureIterator.close();
            featureIterator = null;
        }
        org.n52.sos.extensions.util.JDBCUtils.close(statement);
        org.n52.sos.extensions.util.JDBCUtils.close(connection);
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
