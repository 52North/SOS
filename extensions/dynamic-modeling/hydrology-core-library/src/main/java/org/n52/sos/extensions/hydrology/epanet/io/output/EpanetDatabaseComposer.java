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
package org.n52.sos.extensions.hydrology.epanet.io.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.addition.epanet.hydraulic.io.AwareStep;
import org.addition.epanet.hydraulic.io.HydraulicReader;
import org.addition.epanet.network.FieldsMap;
import org.addition.epanet.network.Network;
import org.addition.epanet.network.PropertiesMap;
import org.addition.epanet.network.structures.Demand;
import org.addition.epanet.network.structures.Field;
import org.addition.epanet.network.structures.Link;
import org.addition.epanet.network.structures.Link.StatType;
import org.addition.epanet.network.structures.Node;
import org.addition.epanet.network.structures.Tank;
import org.addition.epanet.util.ENException;
import org.n52.sos.extensions.util.FileUtils;

/**
 * Class to export an EPANET network to a database.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class EpanetDatabaseComposer
{
    /** Default SQL schema database. */
    private static final String DEFAULT_SQL_SCHEMA_FILENAME = "epanet/create_network_schema.sql";
    
    /** TableName of the network node objects. */
    public static final String NODE_TABLENAME = "epanet_node";
    /** TableName of the network link objects. */
    public static final String LINK_TABLENAME = "epanet_arc";
    /** TableName of the simulation results of the network node objects. */
    public static final String REPORT_NODE_TABLENAME = "epanet_report_node";
    /** TableName of the simulation results of the network link objects. */
    public static final String REPORT_LINK_TABLENAME = "epanet_report_arc";
    
    /** TableName of the network unit objects. */
    public static final String UNITS_TABLENAME = "epanet_units";
    
    private static List<NodeVariableType> nodesVariables = new ArrayList<NodeVariableType>();
    private static List<LinkVariableType> linksVariables = new ArrayList<LinkVariableType>();
        
    static
    {
        nodesVariables.add(NodeVariableType.DEMAND);
        nodesVariables.add(NodeVariableType.HEAD);
        nodesVariables.add(NodeVariableType.PRESSURE);
        nodesVariables.add(NodeVariableType.QUALITY);

        linksVariables.add(LinkVariableType.FLOW);
        linksVariables.add(LinkVariableType.VELOCITY);
        linksVariables.add(LinkVariableType.UNITHEADLOSS);
        linksVariables.add(LinkVariableType.FRICTIONFACTOR);
        linksVariables.add(LinkVariableType.QUALITY);
        linksVariables.add(LinkVariableType.STATUS);
    }
    
    private static enum NodeVariableType 
    {
        ELEVATION("ELEVATION", FieldsMap.Type.ELEV, Double.class),
        BASEDEMAND("BASEDEMAND", FieldsMap.Type.DEMAND, Double.class),
        INITQUALITY("INITQUALITY", FieldsMap.Type.QUALITY, Double.class),
        PRESSURE("PRESSURE", FieldsMap.Type.PRESSURE, Double.class),
        HEAD("HEAD", FieldsMap.Type.HEAD, Double.class),
        QUALITY("QUALITY", FieldsMap.Type.QUALITY, Double.class),
        DEMAND("DEMAND", FieldsMap.Type.DEMAND, Double.class);
        
        public final String name;
        public final FieldsMap.Type type;
        public final Class<?> valueType;
        
        NodeVariableType(String name, FieldsMap.Type type, Class<?> valueType) 
        {
            this.name = name;
            this.type = type;
            this.valueType = valueType; 
        }
        public double getValue(FieldsMap fmap, AwareStep step, Node node, int nideIndex) throws ENException 
        {
            switch (this)
            {
                case ELEVATION:
                    return fmap.revertUnit(type, node.getElevation());
                case BASEDEMAND:
                {
                    double dsum = 0;
                    for (Demand demand : node.getDemand()) dsum += demand.getBase();
                    return fmap.revertUnit(type, dsum);
                }
                case INITQUALITY:
                {
                    double dsum = 0;
                    for (double v : node.getC0()) dsum += v;
                    return dsum!= 0 ? fmap.revertUnit(type, dsum/node.getC0().length) : fmap.revertUnit(type, dsum);
                }
                case DEMAND:
                    return step!=null ? step.getNodeDemand(nideIndex, node, fmap) : 0;
                case HEAD:
                    return step!=null ? step.getNodeHead(nideIndex, node, fmap) : 0;
                case PRESSURE:
                    return step!=null ? step.getNodePressure(nideIndex, node, fmap) : 0;
                case QUALITY:
                    return step!=null ? fmap.revertUnit(type, step.getNodeQuality(nideIndex)) : 0;                    
                default:
                    throw new RuntimeException("NodeVariableType::getvalue(); Unsupported type="+this.name);
            }
        }
    }
    private static enum LinkVariableType 
    {
        ARC_LENGTH("ARC_LENGTH", FieldsMap.Type.LENGTH, Double.class),
        DIAMETER("DIAMETER", FieldsMap.Type.DIAM, Double.class),
        ROUGHNESS("ROUGHNESS", null, Double.class),
        FLOW("FLOW", FieldsMap.Type.FLOW, Double.class),
        VELOCITY("VELOCITY", FieldsMap.Type.VELOCITY, Double.class),
        UNITHEADLOSS("UNITHEADLOSS", FieldsMap.Type.HEADLOSS, Double.class),
        FRICTIONFACTOR("FRICTIONFACTOR", FieldsMap.Type.FRICTION, Double.class),
        QUALITY("QUALITY", FieldsMap.Type.QUALITY, Double.class),
        STATUS("STATUS", FieldsMap.Type.STATUS, Integer.class);
        
        public final String name;
        public final FieldsMap.Type type;
        public final Class<?> valueType;

        LinkVariableType(String name, FieldsMap.Type type, Class<?> valueType) 
        {
            this.name = name;
            this.type = type;
            this.valueType = valueType;
        }
        public double getValue(PropertiesMap.FormType formType, FieldsMap fmap, AwareStep step, Link link, int linkIndex) throws ENException 
        {
            switch (this)
            {
                case ARC_LENGTH:
                    return fmap.revertUnit(type, link.getLenght());
                case DIAMETER:
                    return fmap.revertUnit(type, link.getDiameter());
                case ROUGHNESS:
                    return link.getType()==Link.LinkType.PIPE && formType==PropertiesMap.FormType.DW ? fmap.revertUnit(FieldsMap.Type.DIAM,link.getRoughness()) : link.getRoughness();
                case FLOW:
                    return step!=null ? Math.abs(step.getLinkFlow(linkIndex, link, fmap)) : 0;
                case VELOCITY:
                    return step!=null ? Math.abs(step.getLinkVelocity(linkIndex, link, fmap)) : 0;
                case UNITHEADLOSS:
                    return step!=null ? step.getLinkHeadLoss(linkIndex, link, fmap) : 0;
                case FRICTIONFACTOR:
                    return step!=null ? step.getLinkFriction(linkIndex, link, fmap) : 0;
                case QUALITY:
                    return step!=null ? fmap.revertUnit(type, step.getLinkAvrQuality(linkIndex)) : 0;
                case STATUS:
                    return step!=null ? (step.getLinkFlow(linkIndex, link, fmap)!=0 ? StatType.OPEN.id : StatType.CLOSED.id) : link.getStat().id;
                default:
                    throw new RuntimeException("LinkVariableType::getvalue(); Unsupported type="+this.name);
            }
        }
    }
    
    /** 
     * Convert the specified seconds value to a valid text representation of a time object. 
     */
    private static String getClockTimeAsString(long secondsValue)
    {
        long h =  secondsValue / 3600;
        long m = (secondsValue % 3600) / 60;
        long s =  secondsValue - 3600*h - 60*m;
        
        return String.format("%02d:%02d:%02d",h,m,s);
    }
    
    /**
     * Returns the Table definition of the specified table/object-model in the current EPANET network model. 
     */
    public static List<Map.Entry<String,Class<?>>> makeTableDefinition(String tableName, boolean appendGeometryFields)
    {
        List<Map.Entry<String,Class<?>>> tableDef = new ArrayList<Map.Entry<String,Class<?>>>();
        
        if (tableName.equalsIgnoreCase(NODE_TABLENAME))
        {
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("object_id"  , String.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("enet_type"  , String.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("elevation"  , Double.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("basedemand" , Double.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("initquality", Double.class));
            
            if (appendGeometryFields)
            {
                tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("x", Double.class));
                tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("y", Double.class));
            }
        }
        else
        if (tableName.equalsIgnoreCase(LINK_TABLENAME))
        {
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("object_id", String.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("enet_type", String.class));            
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("node_id_1", String.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("node_id_2", String.class));            
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("diameter" , Double.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("roughness", Double.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("status"   , Integer.class));
            
            if (appendGeometryFields)
            {
                tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("x1", Double.class));
                tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("y1", Double.class));
                tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("x2", Double.class));
                tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("y2", Double.class));
            }
        }
        else
        if (tableName.equalsIgnoreCase(REPORT_NODE_TABLENAME))
        {
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("object_id", String.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("step_time", java.sql.Time.class));            
            for (NodeVariableType nodeVar : nodesVariables) tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>(nodeVar.name.toLowerCase(), nodeVar.valueType));            
        }
        else
        if (tableName.equalsIgnoreCase(REPORT_LINK_TABLENAME))
        {
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("object_id", String.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("step_time", java.sql.Time.class));            
            for (LinkVariableType linkVar : linksVariables) tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>(linkVar.name.toLowerCase(), linkVar.valueType));            
        }
        else
        if (tableName.equalsIgnoreCase(UNITS_TABLENAME))
        {
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("object_id", String.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("unit_name", String.class));
            tableDef.add(new AbstractMap.SimpleEntry<String,Class<?>>("precision", Integer.class));
        }
        return tableDef;
    }
    /**
     * Returns the concatenated list of fields of the specified table. 
     */
    public static String makeSelectFieldsClause(String tableName, String fieldHeader, boolean appendGeometryFields)
    {
        StringBuilder sb = new StringBuilder();
        
        for (Map.Entry<String,Class<?>> entry : makeTableDefinition(tableName, appendGeometryFields))
        {
            if (!com.google.common.base.Strings.isNullOrEmpty(fieldHeader)) sb.append(fieldHeader).append(".");
            sb.append(entry.getKey());
            sb.append(',');
        }
        sb.deleteCharAt(sb.length()-1);
        
        return sb.toString();
    }
    
    /**
     * Write source data of nodes to database. 
     */
    private int composeNodes(Network network, Connection connection) throws SQLException, RuntimeException
    {
        PreparedStatement statement = 
                connection.prepareStatement("INSERT INTO "+NODE_TABLENAME+" ("+makeSelectFieldsClause(NODE_TABLENAME,"",true)+") VALUES(?,?,?,?,?,?,?);");
        
        FieldsMap fieldsMap = network.getFieldsMap();
        int nodeIndex = 0;
        
        try
        {
            String reservoirTag = "RESERVOIR";
            String junctionTag = "JUNCTION";
            String tankTag = "TANK";
            
            for (Node node : network.getNodes())
            {
                statement.setString(1, node.getId());
                statement.setString(2, node instanceof Tank ? (((Tank)node).getArea()==0 ? reservoirTag : tankTag) : junctionTag);
                statement.setDouble(3, NodeVariableType.ELEVATION  .getValue(fieldsMap, null, node, nodeIndex));
                statement.setDouble(4, NodeVariableType.BASEDEMAND .getValue(fieldsMap, null, node, nodeIndex));
                statement.setDouble(5, NodeVariableType.INITQUALITY.getValue(fieldsMap, null, node, nodeIndex));
                statement.setDouble(6, node.getPosition().getX());
                statement.setDouble(7, node.getPosition().getY());
            
                statement.executeUpdate();
                nodeIndex++;
            }
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(statement);
            return nodeIndex;
        }
        catch (Exception e)
        {
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(statement);            
            throw new RuntimeException(e);
        }
    }
    /**
     * Write source data of links to database. 
     */    
    private int composeLinks(Network network, Connection connection) throws SQLException, RuntimeException
    {
        PreparedStatement statement = 
                connection.prepareStatement("INSERT INTO "+LINK_TABLENAME+" ("+makeSelectFieldsClause(LINK_TABLENAME,"",true)+") VALUES(?,?,?,?,?,?,?,?,?,?,?);");
        
        FieldsMap fieldsMap = network.getFieldsMap();
        int linkIndex = 0;
        
        try
        {
            PropertiesMap.FormType formType = network.getPropertiesMap().getFormflag();
            
            for (Link link : network.getLinks())
            {
                Node startNode = link.getFirst();
                Node finalNode = link.getSecond();
                
                statement.setString(1, link.getId());
                statement.setString(2, link.getType().name());
                statement.setString(3, startNode.getId());
                statement.setString(4, finalNode.getId());
                statement.setDouble(5, LinkVariableType.DIAMETER .getValue(formType, fieldsMap, null, link, linkIndex));
                statement.setDouble(6, LinkVariableType.ROUGHNESS.getValue(formType, fieldsMap, null, link, linkIndex));
                statement.setInt   (7, link.getStat().id);
                statement.setDouble(8, startNode.getPosition().getX());
                statement.setDouble(9, startNode.getPosition().getY());
                statement.setDouble(10,finalNode.getPosition().getX());
                statement.setDouble(11,finalNode.getPosition().getY());
                
                statement.executeUpdate();
                linkIndex++;
            }
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(statement);
            return linkIndex;
        }
        catch (Exception e)
        {
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(statement);            
            throw new RuntimeException(e);
        }        
    }
    /**
     * Write result data of nodes/links to database. 
     */
    private int composeReportObjects(Network network, Connection connection, HydraulicReader hydraulicReader, List<Long> targetTimes, Logger log) throws SQLException, RuntimeException
    {
        PreparedStatement nodeStatement = 
                connection.prepareStatement("INSERT INTO "+REPORT_NODE_TABLENAME+" ("+makeSelectFieldsClause(REPORT_NODE_TABLENAME,"",true)+") VALUES (?,?,?,?,?,?);");
        
        PreparedStatement linkStatement = 
                connection.prepareStatement("INSERT INTO "+REPORT_LINK_TABLENAME+" ("+makeSelectFieldsClause(REPORT_LINK_TABLENAME,"",true)+") VALUES (?,?,?,?,?,?,?,?);");
        
        PropertiesMap propertiesMap = network.getPropertiesMap();
        FieldsMap fieldsMap = network.getFieldsMap();
        try
        {
            PropertiesMap.FormType formType = propertiesMap.getFormflag();
            int recordCount = 0;
            
            for (long time = propertiesMap.getRstart(); time <= propertiesMap.getDuration(); time += propertiesMap.getRstep())
            {
                AwareStep step = hydraulicReader.getStep((int)time);
                
                if (targetTimes!=null && targetTimes.size()>0 && !targetTimes.contains(time))
                    continue;
                
                String timeText = getClockTimeAsString(time);
                int nodeIndex = 0;
                int linkIndex = 0;
                
                for (Node node : network.getNodes())
                {
                    nodeStatement.setString(1, node.getId());
                    nodeStatement.setString(2, timeText);
                    int parameterIndex = 3;
                    
                    for (NodeVariableType nodeVar : nodesVariables) 
                    {
                        double value = nodeVar.getValue(fieldsMap, step, node, nodeIndex);
                        nodeStatement.setDouble(parameterIndex, value);
                        parameterIndex++;
                    }
                    nodeStatement.executeUpdate();
                    recordCount++;
                    nodeIndex++;
                }
                for (Link link : network.getLinks())
                {
                    linkStatement.setString(1, link.getId());
                    linkStatement.setString(2, timeText);
                    int parameterIndex = 3;
                    
                    for (LinkVariableType linkVar : linksVariables) 
                    {
                        double value = linkVar.getValue(formType, fieldsMap, step, link, linkIndex);
                        linkStatement.setDouble(parameterIndex, value);
                        parameterIndex++;
                    }
                    linkStatement.executeUpdate();
                    recordCount++;
                    linkIndex++;
                }
            }
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(nodeStatement);
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(linkStatement);
            
            return recordCount;
        }
        catch (Exception e)
        {
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(nodeStatement);
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(linkStatement);
            
            throw new RuntimeException(e);
        }
    }

    /**
     * Write source units of measure of the network to database. 
     */
    private int composeUnitsOfMeasures(Network network, Connection connection) throws SQLException, RuntimeException
    {
        PreparedStatement statement = 
                connection.prepareStatement("INSERT INTO "+UNITS_TABLENAME+" ("+makeSelectFieldsClause(UNITS_TABLENAME,"",true)+") VALUES(?,?,?);");
        
        HashMap<String,Field> fieldsHash = new HashMap<String,Field>();
        FieldsMap fieldsMap = network.getFieldsMap();
        Field field = null;
        int unitCount = 0;
        
        try
        {
            for (NodeVariableType nodeVar : nodesVariables)
            {
                try
                {
                    field = fieldsMap.getField(nodeVar.type);
                    if (fieldsHash.containsKey(nodeVar.name)) continue; else fieldsHash.put(nodeVar.name, field);
                    
                    statement.setString(1, nodeVar.name);
                    statement.setString(2, field.getUnits()!=null ? field.getUnits() : "");
                    statement.setInt   (3, field.getPrecision());
                    statement.executeUpdate();
                    unitCount++;
                }
                catch (ENException e) { }
            }
            for (LinkVariableType linkVar : linksVariables)
            {
                try
                {
                    field = fieldsMap.getField(linkVar.type);
                    if (fieldsHash.containsKey(linkVar.name)) continue; else fieldsHash.put(linkVar.name, field);
                    
                    statement.setString(1, linkVar.name);
                    statement.setString(2, field.getUnits()!=null ? field.getUnits() : "");
                    statement.setInt   (3, field.getPrecision());
                    statement.executeUpdate();
                    unitCount++;
                }
                catch (ENException e) { }
            }
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(statement);
            fieldsHash.clear();
            return unitCount;
        }
        catch (Exception e)
        {
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(statement);
            fieldsHash.clear();
            
            throw new RuntimeException(e);
        }        
    }
    
    /**
     * Fill the schema table of the specified database connection.
     */
    public static boolean createSchemaDatabase(Connection connection) throws RuntimeException
    {
        java.net.URI schemaFile = FileUtils.resolveAbsoluteURI(DEFAULT_SQL_SCHEMA_FILENAME, EpanetDatabaseComposer.class.getClassLoader());
        
        BufferedReader inputReader = null;
        InputStream inputStream = null;
        Statement statement = null;
        
        try
        {
            inputReader = new BufferedReader(new InputStreamReader(inputStream = schemaFile.toURL().openStream()));
            
            // Read the SQL-batch from the resource.
            StringBuilder commandBuilder = new StringBuilder();
            String line = null;
            while ((line=inputReader.readLine())!=null) { if ((line=line.trim()).length()>0 && !line.startsWith("--")) commandBuilder.append(line); }
            
            // Create the schema tables of the EPANET network.
            statement = connection.createStatement();
            statement.setQueryTimeout(10); // set timeout to 10 seconds.
            
            for (String sql : commandBuilder.toString().split(";"))
            {
                statement.execute(sql+";");
            }
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(statement);
            return true;
        }
        catch (Exception e)
        {
            org.n52.sos.extensions.hydrology.util.JDBCUtils.close(statement);
            throw new RuntimeException(e);
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
    
    /**
     * Export the specified EPANET Network to a database.
     */
    public boolean writeToDatabase(Network network, List<Long> targetTimes, Connection connection, boolean writeNetwork, Logger log) throws RuntimeException, IOException
    {      
        PropertiesMap propertiesMap = network.getPropertiesMap();
        
        File hydraulicFile = File.createTempFile("MyHydraulicTask_", ".bin.hydraulic");
        HydraulicReader hydraulicReader = null;
        
        try
        {
            EpanetHydraulicQualitySim hydraulicSim = new EpanetHydraulicQualitySim(network, log);
            hydraulicSim.simulate(hydraulicFile);
            
            // Check the target DateTimes to solve.
            if (targetTimes!=null && targetTimes.size()>0) 
            {
                for (Long time : targetTimes)
                {
                    String epanetTime = org.addition.epanet.util.Utilities.getClockTime(time);                    
                    if ( time < propertiesMap.getRstart  ()) throw new RuntimeException("Target time '" + epanetTime + "' smaller than simulation start time");
                    if ( time > propertiesMap.getDuration()) throw new RuntimeException("Target time '" + epanetTime + "' bigger than simulation duration");
                    if ((time - propertiesMap.getRstart  ()) % propertiesMap.getRstep()!=0) throw new RuntimeException("Target time '" + epanetTime + "' not found");
                }
            }
            composeUnitsOfMeasures(network, connection);
            
            if (writeNetwork)
            {
                composeNodes(network, connection);
                composeLinks(network, connection);
            }
            hydraulicReader = new HydraulicReader(new RandomAccessFile(hydraulicFile, "r"));
            composeReportObjects(network, connection, hydraulicReader, targetTimes, log);
            
            return true;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (hydraulicReader!=null) hydraulicReader.close();
            hydraulicFile.delete();
        }
    }
}
