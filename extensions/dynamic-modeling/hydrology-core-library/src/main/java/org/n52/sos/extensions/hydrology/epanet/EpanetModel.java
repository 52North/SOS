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

import java.io.File;
import java.io.IOException;

import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;

import org.n52.sos.extensions.MeasureSet;
import org.n52.sos.extensions.ObservableContextArgs;
import org.n52.sos.extensions.ObservableObject;
import org.n52.sos.extensions.hydrology.epanet.io.output.EpanetDatabaseComposer;
import org.n52.sos.extensions.model.AbstractModel;
import org.n52.sos.extensions.model.Model;
import org.n52.sos.extensions.util.FileUtils;

/**
 * Implements the Hydraulic model of EPANET network structures.
 *  
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class EpanetModel extends AbstractModel
{
    private static final Logger LOG = Logger.getLogger(EpanetModel.class.toString());    
    
    /** Default seed database as target of Network structures. */
    private static final String DEFAULT_SQLITE_SEED_DATABASE_FILE = "epanet/sqlite.template.db";
    
    private String fileName;
    private String sqliteFileName;
    private String objectFilter = "*:*.*";
    
    private CoordinateReferenceSystem coordinateSystem;
    private EpanetSolver solver;
    
    /** Gets the EPANET Network FileName of this model. */
    public String getFileName()
    {
        return fileName;
    }
    
    /**
     * Load the configuration data from the specified settings entry.
     */
    @Override
    public boolean loadSettings(String settingsFileName, org.w3c.dom.Element rootEntry, org.w3c.dom.Element modelEntry)
    {
        if (super.loadSettings(settingsFileName, rootEntry, modelEntry))
        {
            NodeList nodeList = modelEntry.getChildNodes();
            
            for (int i = 0, icount = nodeList.getLength(); i < icount; i++)
            {
                Node node = nodeList.item(i);
                if (node.getNodeType()!=Node.ELEMENT_NODE) continue;
                String nodeName = node.getNodeName();
                
                if (nodeName.equalsIgnoreCase("srid"))
                {
                    String srid = node.getTextContent();
                    
                    if (!srid.equals("0")) try
                    {
                        coordinateSystem = srid.toUpperCase().contains("EPSG:") ? CRS.decode(srid) : CRS.parseWKT(srid);
                    }
                    catch (Exception e)
                    {
                        LOG.severe(String.format("Invalid CoordinateReferenceSystemID defined in settings entry '%s', msg='%s'", name, e.getMessage()));
                    }
                }
                else
                if (nodeName.equalsIgnoreCase("fileName"))
                {
                    java.net.URI uri = FileUtils.resolveAbsoluteURI(node.getTextContent(), settingsFileName);
                    
                    if (uri==null)
                    {
                        LOG.severe(String.format("Invalid EPANET model FileName defined in settings entry '%s', msg='%s'", name, node.getTextContent()));
                        return false;
                    }
                    else
                    {
                        File file = new File(uri);
                        fileName = file.getAbsolutePath();
                    }
                }
                else
                if (nodeName.equalsIgnoreCase("objectFilter"))
                {
                    objectFilter = node.getTextContent();
                }
                else
                if (nodeName.equalsIgnoreCase("networkSolver"))
                {
                    String className = ((Element)node).getAttributeNode("class").getValue();
                    
                    try 
                    {
                        solver = (EpanetSolver)Class.forName(className).newInstance();
                        solver.loadSettings(settingsFileName, rootEntry, modelEntry, (Element)node);
                    }
                    catch (Exception e) 
                    {
                        LOG.severe(String.format("Invalid EpanetSolver Type defined in settings entry '%s', msg='%s'", className, e.getMessage()));
                        return false;
                    }
                }
            }
            return solver!=null;
        }
        return false;
    }
    
    /**
     * Create a Network database related to this EPANET model.
     */
    protected File createNetworkSchemaDatabase(String sqliteFileName) throws RuntimeException
    {
        java.net.URI sptseedFile = FileUtils.resolveAbsoluteURI(DEFAULT_SQLITE_SEED_DATABASE_FILE, EpanetModel.class.getClassLoader());
        File sptliteFile = new File(sqliteFileName);
        
        // -----------------------------------------------------------------------------------------------------------------
        // Clone the seed to the specified EPANET database.
        
        String sqliteSeedFileName = DEFAULT_SQLITE_SEED_DATABASE_FILE;
        if (sptseedFile!=null) sqliteSeedFileName = sptseedFile.toString();
        
        LOG.info(String.format("Cloning the network seed database '%s' for the EPANET model '%s'", sqliteSeedFileName, name));
        
        if (sptseedFile==null)
        {
            String errorMsg = String.format("Invalid network seed database '%s'", sqliteSeedFileName);
            throw new RuntimeException(errorMsg);
        }
        try
        {
            org.apache.commons.io.FileUtils.copyURLToFile(sptseedFile.toURL(), sptliteFile);
        }
        catch (IOException e)
        {
            String errorMsg = String.format("Exception copying the Spatialite seed database from '%s' to '%s'", sptseedFile.getPath(), sptliteFile.toPath());
            throw new RuntimeException(errorMsg);
        }
        
        LOG.info(String.format("Network database '%s' of EPANET model '%s' initialized!", sptliteFile.getAbsolutePath(), name));
        
        // -----------------------------------------------------------------------------------------------------------------
        // Fill the EPANET Database schema.
        
        LOG.info(String.format("Creating the network database schema of EPANET model '%s'", name));
        
        Connection connection = null;
                
        try
        {
            connection = org.n52.sos.extensions.hydrology.util.JDBCUtils.openSqliteConnection(sqliteFileName);
            connection.setAutoCommit(false);
            
            EpanetDatabaseComposer.createSchemaDatabase(connection);
            connection.commit();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(connection);
        }
        
        LOG.info(String.format("Database schema of EPANET model '%s' created!", name));
        
        return sptliteFile;
    }

    /**
     * Prepare the Network structure managed.
     */
    @Override
    public boolean prepareObject() throws RuntimeException
    {
        String currentSqliteFileName = sqliteFileName;
        
        File networkFile = new File(fileName);
        File sptliteFile = null;
        
        // Test valid EPANET network FileName.
        if (!networkFile.exists())
        {
            String errorMsg = String.format("Invalid EPANET model FileName defined '%s'", fileName);
            throw new RuntimeException(errorMsg);
        }
        if (com.google.common.base.Strings.isNullOrEmpty(currentSqliteFileName))
        {
            currentSqliteFileName = fileName+".db";
            sqliteFileName = currentSqliteFileName;
        }
        
        // Test valid EPANET database FileName.
        if ((sptliteFile = new File(currentSqliteFileName)).exists() && networkFile.lastModified()>sptliteFile.lastModified())
        {
            String simulationName = Long.toString(System.currentTimeMillis());
            
            LOG.info(String.format("Deleting obsolete Spatialite database '%s'", sptliteFile.getAbsolutePath()));
            sptliteFile.deleteOnExit();
            
            sptliteFile = new File( FileUtils.resolveAbsoluteURI(fileName+"."+simulationName+".db" , fileName) );
            sptliteFile.deleteOnExit();            
            File logFle = new File( FileUtils.resolveAbsoluteURI(fileName+"."+simulationName+".log", fileName) );
            logFle.deleteOnExit();
            
            currentSqliteFileName = sptliteFile.getPath(); 
        }
        
        // Simulate the Network and save results when needed.
        if (!sptliteFile.exists())
        {
            LOG.info(String.format("Preparing the network database '%s' for the EPANET model '%s'", currentSqliteFileName, name));
            
            if (solver==null)
            {
                LOG.severe(String.format("Undefined Network solver of EPANET model '%s'", name));
                return false;
            }
            if (solver.solveNetwork(this, currentSqliteFileName))
            {
                LOG.info(String.format("Network database for the EPANET model '%s' successly prepared!", name));
                sqliteFileName = currentSqliteFileName;
                return true;
            }                
            LOG.warning(String.format("Network database for the EPANET model '%s' preparation failed!", name));
            return false;
        }
        return true;
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
        final Model currentModel = this;
        
        return new Iterable<ObservableObject>() 
        {
            public final Iterator<ObservableObject> iterator() 
            {
                String whereClause = "";
                String objectFilterToUse = objectFilter;
                
                final String objectId = observableContextArgs.objectId;
                final ReferencedEnvelope envelope = observableContextArgs.envelope;
                final Date dateFrom = observableContextArgs.dateFrom;
                final Date dateTo = observableContextArgs.dateTo;
                
                // Only apply the filter for 'GetCapabilities' requests.
                if (!(observableContextArgs.request instanceof org.n52.sos.request.GetCapabilitiesRequest))
                {
                    objectFilterToUse = "*:*.*";
                }
                // Define the where clause if needed.
                if (!com.google.common.base.Strings.isNullOrEmpty(objectId))
                {
                    whereClause = "a.object_id='"+objectId+"'";
                }
                return new EpanetObservableObjectCursor(currentModel,sqliteFileName, coordinateSystem, objectFilterToUse, envelope, dateFrom, dateTo, whereClause);
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
        final Model currentModel = this;
        
        return new Iterable<MeasureSet>() 
        {
            public final Iterator<MeasureSet> iterator()
            {
                String whereClause = "";
                String objectFilterToUse = objectFilter;
                
                final String objectId = observableContextArgs.objectId;
                final ReferencedEnvelope envelope = observableContextArgs.envelope;
                final Date dateFrom = observableContextArgs.dateFrom;
                final Date dateTo = observableContextArgs.dateTo;
                final int flags = observableContextArgs.flags;
                
                // Only apply the filter for 'GetCapabilities' requests.
                if (!(observableContextArgs.request instanceof org.n52.sos.request.GetCapabilitiesRequest))
                {
                    objectFilterToUse = "*:*.*";
                }
                // Define the where clause if needed.
                if (!com.google.common.base.Strings.isNullOrEmpty(objectId))
                {
                    whereClause = "a.object_id='"+objectId+"'";
                }
                return new EpanetObservableMeasureCursor(currentModel,sqliteFileName, coordinateSystem, objectFilterToUse, envelope, dateFrom, dateTo, whereClause, flags);
            }
        };
    }
}
