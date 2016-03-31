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
package org.n52.sos.extensions;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;

/**
 * Information context of a SOS request to fetch observable objects.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class ObservableContextArgs {
    
    /** No flags */
    public static final int NONE_FLAGS = 0;
    /** Start TimeInstant flag */
    public static final int FIRST_TIMEINSTANT_FLAG = 1;
    /** Latest TimeInstant flag */
    public static final int LASTEST_TIMEINSTANT_FLAG = 2;
    
    /**
     * Creates a new empty ObservableRequestContextArgs.
     */
    public ObservableContextArgs()
    {
        this.objectId = "";
        this.flags = NONE_FLAGS;
    }
    /**
     * Creates a new ObservableRequestContextArgs with the specified parameters.
     */
    public ObservableContextArgs(ObservableContextArgs otherContextArgs)
    {
        this.objectId = otherContextArgs.objectId;
        this.spatialFilter = otherContextArgs.spatialFilter;
        this.envelope = otherContextArgs.envelope;
        this.temporalFilter = otherContextArgs.temporalFilter;
        this.dateFrom = otherContextArgs.dateFrom;
        this.dateTo = otherContextArgs.dateTo;
        this.flags = otherContextArgs.flags;
        this.request = otherContextArgs.request;
    }
    
    /**
     * ServiceRequest currently managed.
     */
    public AbstractServiceRequest<?> request;
    
    /**
     * ObjectId criteria of the request.
     */
    public String objectId;
    
    /**
     * Main Spatial filter criteria of the request.
     */
    public SpatialFilter spatialFilter;
    
    /**
     * Spatial envelope criteria of the request.
     */
    public ReferencedEnvelope envelope;
    
    /**
     * Main TemporalFilter criteria of the request.
     */
    public TemporalFilter temporalFilter;
    
    /**
     * DateFrom criteria of the request.
     */
    public Date dateFrom;
    
    /**
     * DateTo criteria of the request.
     */
    public Date dateTo;
    
    /**
     * Extra flags criteria of the request.
     */
    public int flags;
    
    /**
     * Parse the specified SOS request.
     */
    public boolean parseRequest(ObservableModel dynamicModel, AbstractServiceRequest<?> request, int defaultEPSGCode)
    {
        String objectId = ObservableObject.UNDEFINED_OBJECT_ID_FLAG;
        ReferencedEnvelope envelope = ObservableObject.UNDEFINED_ENVELOPE_FILTER_FLAG;
        TimePeriod timePeriod = new TimePeriod(ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG, ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG);
        
        // ----------------------------------------------------------------------------------------------------
        // Extract the source data filters of the requested ServiceRequest.
        
        java.util.Map.Entry<String,Boolean> idf = extractIdentifiers(dynamicModel, request);
        if (idf.getValue()) objectId = idf.getKey();
        else return false;
        
        java.util.Map.Entry<SpatialFilter,ReferencedEnvelope> spf = extractSpatialFilter(dynamicModel, request, defaultEPSGCode);
        this.spatialFilter = spf.getKey();
        envelope = spf.getValue();
        
        java.util.Map.Entry<TemporalFilter,TimePeriod> tmf = extractTemporalFilter(dynamicModel, request, this);
        this.temporalFilter = tmf.getKey();
        timePeriod = tmf.getValue();
        
        // ----------------------------------------------------------------------------------------------------
        // Set the information.
        
        Date dateFrom = timePeriod.getStart()!=null ? new Date(timePeriod.getStart().getMillis()) : ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
        Date dateTo   = timePeriod.getEnd()  !=null ? new Date(timePeriod.getEnd()  .getMillis()) : ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
        
        this.objectId = objectId;
        this.envelope = envelope;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.request = request;
        
        return true;
    }
    
    /**
     * Appends to identifiersList the no-duplicated identifiers from the source list.
     */
    private static boolean extractIdentifiers(List<String> sourceList, Map<String,String> identifiersMap, List<String> identifiersList)
    {
        if (sourceList!=null && sourceList.size()>0)
        {
            boolean itemAdded = false;
            
            for (int i = 0, icount = sourceList.size(); i < icount; i++)
            {
                String itemText = sourceList.get(i);
                
                int currentPos = itemText.lastIndexOf(':');
                if (currentPos!=-1) itemText = itemText.substring(currentPos+1);
                
                String[] split = itemText.split("/");
                if (split.length>=3) itemText = split[0]+"/"+split[1];
                
                if (!identifiersMap.containsKey(itemText))
                {
                    identifiersMap.put(itemText, itemText);
                    identifiersList.add(itemText);
                    itemAdded = true;
                }
            }
            return itemAdded;
        }
        return false;
    }
    /**
     * Returns the requested identifier collection defined by the specified ServiceRequest.
     */
    private static java.util.Map.Entry<String,Boolean> extractIdentifiers(ObservableModel dynamicModel, AbstractServiceRequest<?> request)
    {
        Map<String,String> identifiersMap = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
        List<String> identifiersList = new ArrayList<String>();
        
        // Parse when available the source filter identifiers of requested objects.
        if (request instanceof GetFeatureOfInterestRequest)
        {
            GetFeatureOfInterestRequest theRequest = (GetFeatureOfInterestRequest)request;
            extractIdentifiers(theRequest.getFeatureIdentifiers(), identifiersMap, identifiersList);
            extractIdentifiers(theRequest.getObservedProperties(), identifiersMap, identifiersList);
            extractIdentifiers(theRequest.getProcedures(), identifiersMap, identifiersList);
        }
        else
        if (request instanceof GetObservationByIdRequest)
        {
            GetObservationByIdRequest theRequest = (GetObservationByIdRequest)request;
            extractIdentifiers(theRequest.getObservationIdentifier(), identifiersMap, identifiersList);
        }
        else
        if (request instanceof GetObservationRequest)
        {
            GetObservationRequest theRequest = (GetObservationRequest)request;
            extractIdentifiers(theRequest.getFeatureIdentifiers(), identifiersMap, identifiersList);
            extractIdentifiers(theRequest.getObservedProperties(), identifiersMap, identifiersList);
            extractIdentifiers(theRequest.getProcedures(), identifiersMap, identifiersList);
            extractIdentifiers(theRequest.getOfferings(), identifiersMap, identifiersList);
        }
        else
        if (request instanceof DescribeSensorRequest)
        {
            DescribeSensorRequest theRequest = (DescribeSensorRequest)request;
            extractIdentifiers(Collections.singletonList(theRequest.getProcedure()), identifiersMap, identifiersList);
        }
        else
        if (request instanceof GetCapabilitiesRequest)
        {
            GetCapabilitiesRequest theRequest = (GetCapabilitiesRequest)request;
            
            List<String> sections = theRequest.getSections();
            boolean contentNeeded = true;
            
            if (sections!=null)
            {
                contentNeeded = sections.size()==0;
                
                for (String sectionName : theRequest.getSections())
                {
                    if (sectionName.equalsIgnoreCase("OperationsMetadata") || sectionName.equalsIgnoreCase("Contents"))
                    {
                        contentNeeded = true;
                        break;
                    }
                }
            }
            if (!contentNeeded)
            return new AbstractMap.SimpleEntry<String,Boolean>(ObservableObject.UNDEFINED_OBJECT_ID_FLAG, false);
        }
        else
        {
            return new AbstractMap.SimpleEntry<String,Boolean>(ObservableObject.UNDEFINED_OBJECT_ID_FLAG, true);
        }
        
        // Skip no-current model.
        String  objectId = ObservableObject.UNDEFINED_OBJECT_ID_FLAG;
        String modelName = dynamicModel.getName();
        boolean failFunc = identifiersList.size()>0;
        
        for (String key : identifiersList)
        {
            String[] split = key.split("/");
            
            if (split!=null && split.length==2 && split[0].equalsIgnoreCase(modelName))
            {
                objectId += split[1] + ",";
                failFunc = false;
            }
        }
        identifiersList.clear();
        identifiersMap.clear();
        
        // Return identifiers.
        if (!failFunc)
        {
            int len = objectId.length();
            if (len>0 && objectId.charAt(len-1)==',') objectId = objectId.substring(0,len-1);
            return new AbstractMap.SimpleEntry<String,Boolean>(objectId, true);
        }
        return new AbstractMap.SimpleEntry<String,Boolean>(ObservableObject.UNDEFINED_OBJECT_ID_FLAG, false);
    }
    
    /**
     * Returns the requested spatial filter defined by the specified ServiceRequest.
     */
    private static java.util.Map.Entry<SpatialFilter,ReferencedEnvelope> extractSpatialFilter(ObservableModel dynamicModel, AbstractServiceRequest<?> request, int defaultEPSGCode)
    {
        SpatialFilter spatialFilter = null;
        
        // TODO: Extract only first spatial filter available from the request.
        if (request instanceof GetFeatureOfInterestRequest)
        {
            GetFeatureOfInterestRequest theRequest = (GetFeatureOfInterestRequest)request;
            if (theRequest.isSetSpatialFilters()) spatialFilter = theRequest.getSpatialFilters().get(0);
        }
        else
        if (request instanceof GetObservationRequest)
        {
            GetObservationRequest theRequest = (GetObservationRequest)request;
            if (theRequest.isSetSpatialFilter()) spatialFilter = theRequest.getSpatialFilter();
        }
        if (spatialFilter!=null && spatialFilter.getGeometry()!=null && !spatialFilter.getGeometry().isEmpty())
        {
            Geometry geometry = spatialFilter.getGeometry();
            int srid = geometry.getSRID();
            if (srid==0) srid = defaultEPSGCode;
            
            CoordinateReferenceSystem coordinateSystem = null;
            try { coordinateSystem = CRS.decode("EPSG:"+srid); } catch (Exception e) { coordinateSystem = DefaultGeographicCRS.WGS84; }
            
            Envelope env = geometry.getEnvelopeInternal();
            ReferencedEnvelope envelope = new ReferencedEnvelope(env.getMinX(), env.getMaxX(), env.getMinY(), env.getMaxY(), coordinateSystem);
            
            return new AbstractMap.SimpleEntry<SpatialFilter,ReferencedEnvelope>(spatialFilter, envelope);
        }
        return new AbstractMap.SimpleEntry<SpatialFilter,ReferencedEnvelope>(null, ObservableObject.UNDEFINED_ENVELOPE_FILTER_FLAG);
    }
    
    /**
     * Returns the requested temporal filter defined by the specified ServiceRequest.
     */
    private static java.util.Map.Entry<TemporalFilter,TimePeriod> extractTemporalFilter(ObservableModel dynamicModel, AbstractServiceRequest<?> request, ObservableContextArgs observableContextArgs)
    {
        List<TemporalFilter> temporalFilters = null;
        
        // TODO: Extract only first temporal filter available from the request.
        if (request instanceof GetFeatureOfInterestRequest)
        {
            GetFeatureOfInterestRequest theRequest = (GetFeatureOfInterestRequest)request;
            if (theRequest.isSetTemporalFilters()) temporalFilters = theRequest.getTemporalFilters();
        }
        else
        if (request instanceof GetObservationRequest)
        {
            GetObservationRequest theRequest = (GetObservationRequest)request;
            if (theRequest.isSetTemporalFilter()) temporalFilters = theRequest.getTemporalFilters();
        }
        else
        if (request instanceof DescribeSensorRequest)
        {
            DescribeSensorRequest theRequest = (DescribeSensorRequest)request;
            if (theRequest.isSetValidTime()) temporalFilters = Collections.singletonList(new TemporalFilter(TimeOperator.TM_Equals, theRequest.getValidTime(), null));
        }
        if (temporalFilters!=null && temporalFilters.size()>0)
        {
            TimePeriod timePeriod = new TimePeriod();
            
            for (TemporalFilter filter : temporalFilters)
            {
                Time time = filter.getTime();
                
                if (time instanceof TimeInstant)
                {
                    TimeInstant ti = (TimeInstant)time;
                    timePeriod.extendToContain(ti);
                    
                    if (ti.isSetSosIndeterminateTime())
                    {
                        SosConstants.SosIndeterminateTime sit = ti.getSosIndeterminateTime();
                        
                        if (sit==SosConstants.SosIndeterminateTime.first)
                        {
                            observableContextArgs.flags |= ObservableContextArgs.FIRST_TIMEINSTANT_FLAG;
                        }
                        else
                        if (sit==SosConstants.SosIndeterminateTime.latest)
                        {
                            observableContextArgs.flags |= ObservableContextArgs.LASTEST_TIMEINSTANT_FLAG;
                        }
                    }
                }
                else
                if (time instanceof TimePeriod)
                {
                    TimePeriod tp = (TimePeriod)time;
                    timePeriod.extendToContain(tp);
                }
            }
            return new AbstractMap.SimpleEntry<TemporalFilter,TimePeriod>(temporalFilters.get(0), timePeriod);
        }
        return new AbstractMap.SimpleEntry<TemporalFilter,TimePeriod>(null, new TimePeriod());
    }    
}
