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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.joda.time.DateTime;

import org.n52.sos.cache.AbstractContentCache;
import org.n52.sos.cache.ContentCache;
import org.n52.sos.cache.InMemoryCacheImpl;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.i18n.MultilingualString;
import org.n52.sos.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.sos.ogc.filter.FilterConstants.TimeOperator;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.Time.TimeIndeterminateValue;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.OmObservationConstellation;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.request.DescribeSensorRequest;
import org.n52.sos.request.GetCapabilitiesRequest;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.response.DescribeSensorResponse;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * WritableCache that integrates the capabilities of dynamic models in a SOS service.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class DynamicWritableCache extends InMemoryCacheImpl 
{
    private static final long serialVersionUID = 3255022623147196463L;
    
    /**
     * Adds the available observable capabilities provided by the specified content cache.
     */
    public void addContentOfCache(ContentCache contentCache)
    {
        if (contentCache.getDefaultEPSGCode()!=0)
        {
            setDefaultEPSGCode(contentCache.getDefaultEPSGCode());
        }
        if (contentCache.hasGlobalEnvelope())
        {
            SosEnvelope envelope = contentCache.getGlobalEnvelope();
            if (hasGlobalEnvelope()) envelope.expandToInclude(getGlobalEnvelope());
            setGlobalEnvelope(envelope);
        }
        
        if (contentCache.hasMinPhenomenonTime())
        {
            DateTime minPhenomenonTime = contentCache.getMinPhenomenonTime();
            if (!hasMinPhenomenonTime() || minPhenomenonTime.isBefore(getMinPhenomenonTime().getMillis())) setMinPhenomenonTime(minPhenomenonTime);                        
        }
        if (contentCache.hasMaxPhenomenonTime())
        {
            DateTime maxPhenomenonTime = contentCache.getMaxPhenomenonTime();
            if (!hasMaxPhenomenonTime() || maxPhenomenonTime.isAfter(getMaxPhenomenonTime().getMillis())) setMaxPhenomenonTime(maxPhenomenonTime);
        }
        if (contentCache.hasMinResultTime())
        {
            DateTime minResultTime = contentCache.getMinResultTime();
            if (!hasMinResultTime() || minResultTime.isBefore(getMinResultTime().getMillis())) setMinResultTime(minResultTime);
        }
        if (contentCache.hasMaxResultTime())
        {
            DateTime maxResultTime = contentCache.getMaxResultTime();
            if (!hasMaxResultTime() || maxResultTime.isAfter(getMaxResultTime().getMillis())) setMaxResultTime(maxResultTime);            
        }
        
        Set<String> formats = contentCache.getRequestableProcedureDescriptionFormat();
        if (formats.size()>0) setRequestableProcedureDescriptionFormat(formats);
        
        for (Integer epsgCode : contentCache.getEpsgCodes())
        {
            if (!hasEpsgCode(epsgCode)) addEpsgCode(epsgCode);
        }
        for (Locale language : contentCache.getSupportedLanguages())
        {
            if (!isLanguageSupported(language)) addSupportedLanguage(language);
        }
        for (String featureOfInterest : contentCache.getFeaturesOfInterest())
        {
            if (hasFeatureOfInterest(featureOfInterest)) continue;
            addFeatureOfInterest(featureOfInterest);
            
            Set<String> features = contentCache.getParentFeatures(featureOfInterest, true, false);
            if (features.size()>0) addParentFeatures(featureOfInterest, features);
            
            Set<String> procedures = contentCache.getProceduresForFeatureOfInterest(featureOfInterest);
            if (procedures.size()>0) setProceduresForFeatureOfInterest(featureOfInterest, procedures);
        }
        for (String resultTemplate : contentCache.getResultTemplates())
        {
            if (hasResultTemplate(resultTemplate)) continue;
            addResultTemplate(resultTemplate);
            
            Set<String> observableProperties = contentCache.getObservablePropertiesForResultTemplate(resultTemplate);
            if (observableProperties.size()>0) setObservablePropertiesForResultTemplate(resultTemplate, observableProperties);
            
            Set<String> features = contentCache.getFeaturesOfInterestForResultTemplate(resultTemplate);
            if (features.size()>0) addFeaturesOfInterestForResultTemplate(resultTemplate, features);
        }
        for (String procedure : contentCache.getProcedures())
        {
            if (hasProcedure(procedure)) continue;
            addProcedure(procedure);
            
            if (contentCache.hasMinPhenomenonTimeForProcedure(procedure)) setMinPhenomenonTimeForProcedure(procedure, contentCache.getMinPhenomenonTimeForProcedure(procedure));
            if (contentCache.hasMaxPhenomenonTimeForProcedure(procedure)) setMaxPhenomenonTimeForProcedure(procedure, contentCache.getMaxPhenomenonTimeForProcedure(procedure));
            
            Set<String> parentProcedures = contentCache.getParentProcedures(procedure, true, false);
            if (parentProcedures.size()>0) addParentProcedures(procedure, parentProcedures);
            
            Set<String> offerings = contentCache.getOfferingsForProcedure(procedure);
            if (offerings.size()>0) setOfferingsForProcedure(procedure, offerings);

            Set<String> observableProperties = contentCache.getObservablePropertiesForProcedure(procedure);
            if (observableProperties.size()>0) setObservablePropertiesForProcedure(procedure, observableProperties);
        }
        for (String offering : contentCache.getOfferings())
        {
            if (hasOffering(offering)) continue;
            addOffering(offering);
            
            if (contentCache.hasMinPhenomenonTimeForOffering(offering)) setMinPhenomenonTimeForOffering(offering, contentCache.getMinPhenomenonTimeForOffering(offering));
            if (contentCache.hasMaxPhenomenonTimeForOffering(offering)) setMaxPhenomenonTimeForOffering(offering, contentCache.getMaxPhenomenonTimeForOffering(offering));
            if (contentCache.hasMinResultTimeForOffering(offering)) setMinResultTimeForOffering(offering, contentCache.getMinResultTimeForOffering(offering));
            if (contentCache.hasMaxResultTimeForOffering(offering)) setMaxResultTimeForOffering(offering, contentCache.getMaxResultTimeForOffering(offering));
            if (contentCache.hasSpatialFilteringProfileEnvelopeForOffering(offering)) setSpatialFilteringProfileEnvelopeForOffering(offering, contentCache.getSpatialFilteringProfileEnvelopeForOffering(offering));
            
            String name = contentCache.getNameForOffering(offering);
            if (name!=null && name.length()>0) setNameForOffering(offering, name);
            
            SosEnvelope envelope = contentCache.getEnvelopeForOffering(offering);
            if (!SosEnvelope.isNotNullOrEmpty(envelope)) setEnvelopeForOffering(offering, envelope);
            
            Set<String> allowedObservationTypes = contentCache.getAllowedObservationTypesForOffering(offering);
            if (allowedObservationTypes.size()>0) setAllowedObservationTypeForOffering(offering, allowedObservationTypes);
            
            Set<String> allowedFeatureTypes = contentCache.getAllowedFeatureOfInterestTypesForOffering(offering);
            if (allowedFeatureTypes.size()>0) setAllowedFeatureOfInterestTypeForOffering(offering, allowedFeatureTypes);
            
            Set<String> procedures = contentCache.getProceduresForOffering(offering);
            if (procedures.size()>0) setProceduresForOffering(offering, procedures);
            
            Set<String> childProcedures = contentCache.getHiddenChildProceduresForOffering(offering);
            if (childProcedures.size()>0) setHiddenChildProceduresForOffering(offering, childProcedures);
            
            Set<String> observableProperties = contentCache.getObservablePropertiesForOffering(offering);
            if (observableProperties.size()>0) setObservablePropertiesForOffering(offering, observableProperties);
            
            Set<String> observationTypes = contentCache.getObservationTypesForOffering(offering);
            if (observationTypes.size()>0) setObservationTypesForOffering(offering, observationTypes);
            
            Set<String> resultTemplates = contentCache.getResultTemplatesForOffering(offering);
            if (resultTemplates.size()>0) setResultTemplatesForOffering(offering, resultTemplates);
            
            Set<String> features = contentCache.getFeaturesOfInterestForOffering(offering);
            if (features.size()>0) setFeaturesOfInterestForOffering(offering, features);
            
            Set<String> featureTypes = contentCache.getFeatureOfInterestTypesForOffering(offering);
            if (featureTypes.size()>0) setFeatureOfInterestTypesForOffering(offering, featureTypes);
            
            Set<String> relatedFeatures = contentCache.getRelatedFeaturesForOffering(offering);
            if (relatedFeatures.size()>0) setRelatedFeaturesForOffering(offering, relatedFeatures);
                        
            Set<String> compositePhenomenons = contentCache.getCompositePhenomenonsForOffering(offering);
            if (compositePhenomenons.size()>0)
            {
                setCompositePhenomenonForOffering(offering, compositePhenomenons);
                
                for (String compositePhenomenon : compositePhenomenons)
                {
                    Set<String> observableProperties2 = contentCache.getObservablePropertiesForCompositePhenomenon(compositePhenomenon);
                    if (observableProperties2.size()>0) setObservablePropertiesForCompositePhenomenon(compositePhenomenon, observableProperties2);
                }
            }
            
            MultilingualString descr = contentCache.getI18nDescriptionsForOffering(offering);
            if (descr!=null && !descr.isEmpty()) setI18nDescriptionForOffering(offering, descr);
            
            MultilingualString names = contentCache.getI18nNamesForOffering(offering);
            if (names!=null && !names.isEmpty()) setI18nNameForOffering(offering, names);
        }
        for (String relatedFeature : contentCache.getRelatedFeatures())
        {
           Set<String> roles = contentCache.getRolesForRelatedFeature(relatedFeature);
           if (roles.size()>0) for (String role : roles) addRoleForRelatedFeature(relatedFeature, role);
        }
        for (String observableProperty : contentCache.getObservableProperties())
        {
            Set<String> offerings = contentCache.getOfferingsForObservableProperty(observableProperty);
            if (offerings.size()>0) setOfferingsForObservableProperty(observableProperty, offerings);
            
            Set<String> procedures = contentCache.getProceduresForObservableProperty(observableProperty);
            if (procedures.size()>0) setProceduresForObservableProperty(observableProperty, procedures);
        }
        if (contentCache instanceof AbstractContentCache)
        {
            AbstractContentCache tempCache = (AbstractContentCache)contentCache;
            Map<String,String> tempMap = null;
            
            tempMap = tempCache.getFeatureOfInterestHumanReadableNameForIdentifier();
            if (tempMap.size()>0) for (Map.Entry<String,String> entry : tempMap.entrySet()) addFeatureOfInterestIdentifierHumanReadableName(entry.getKey(), entry.getValue());
          
            tempMap = tempCache.getObservablePropertyHumanReadableNameForIdentifier();
            if (tempMap.size()>0) for (Map.Entry<String,String> entry : tempMap.entrySet()) addObservablePropertyIdentifierHumanReadableName(entry.getKey(), entry.getValue());
            
            tempMap = tempCache.getProcedureHumanReadableNameForIdentifier();
            if (tempMap.size()>0) for (Map.Entry<String,String> entry : tempMap.entrySet()) addProcedureIdentifierHumanReadableName(entry.getKey(), entry.getValue());

            tempMap = tempCache.getOfferingHumanReadableNameForIdentifier();
            if (tempMap.size()>0) for (Map.Entry<String,String> entry : tempMap.entrySet()) addOfferingIdentifierHumanReadableName(entry.getKey(), entry.getValue());
        }
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
    private java.util.Map.Entry<String,Boolean> extractIdentifiers(ObservableModel dynamicModel, AbstractServiceRequest<?> request)
    {
        Map<String,String> identifiersMap = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
        List<String> identifiersList = new ArrayList<String>();
        
        // Parse when available the source filter identifiers of requested objects.
        if (request instanceof GetFeatureOfInterestRequest)
        {
            GetFeatureOfInterestRequest theRequest = (GetFeatureOfInterestRequest)request;
            DynamicWritableCache.extractIdentifiers(theRequest.getFeatureIdentifiers(), identifiersMap, identifiersList);
            DynamicWritableCache.extractIdentifiers(theRequest.getObservedProperties(), identifiersMap, identifiersList);
            DynamicWritableCache.extractIdentifiers(theRequest.getProcedures(), identifiersMap, identifiersList);
        }
        else
        if (request instanceof GetObservationByIdRequest)
        {
            GetObservationByIdRequest theRequest = (GetObservationByIdRequest)request;
            DynamicWritableCache.extractIdentifiers(theRequest.getObservationIdentifier(), identifiersMap, identifiersList);
        }
        else
        if (request instanceof GetObservationRequest)
        {
            GetObservationRequest theRequest = (GetObservationRequest)request;
            DynamicWritableCache.extractIdentifiers(theRequest.getFeatureIdentifiers(), identifiersMap, identifiersList);
            DynamicWritableCache.extractIdentifiers(theRequest.getObservedProperties(), identifiersMap, identifiersList);
            DynamicWritableCache.extractIdentifiers(theRequest.getProcedures(), identifiersMap, identifiersList);
            DynamicWritableCache.extractIdentifiers(theRequest.getOfferings(), identifiersMap, identifiersList);
        }
        else
        if (request instanceof DescribeSensorRequest)
        {
            DescribeSensorRequest theRequest = (DescribeSensorRequest)request;
            DynamicWritableCache.extractIdentifiers(Collections.singletonList(theRequest.getProcedure()), identifiersMap, identifiersList);
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
    private java.util.Map.Entry<SpatialFilter,ReferencedEnvelope> extractSpatialFilter(ObservableModel dynamicModel, AbstractServiceRequest<?> request)
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
            if (srid==0) srid = getDefaultEPSGCode();
            
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
    private java.util.Map.Entry<TemporalFilter,TimePeriod> extractTemporalFilter(ObservableModel dynamicModel, AbstractServiceRequest<?> request)
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
    
    /**
     * Validate an ObservableObject using the specified spatial and temporal filters. 
     */
    private static boolean validateObject(ObservableModel dynamicModel, ObservableObject theObject, SpatialFilter spatialFilter, TemporalFilter temporalFilter) throws org.n52.sos.exception.CodedException
    {
        if (spatialFilter!=null && theObject.featureOfInterest!=null)
        {
            SpatialOperator spatialOperator = spatialFilter.getOperator();
            
            Geometry geometryA = spatialFilter.getGeometry();
            Geometry geometryB = ((Geometry)theObject.featureOfInterest.getDefaultGeometryProperty().getValue());
            
            switch (spatialOperator)
            {
                case Equals  : return geometryA.equals  (geometryB);
                case Disjoint: return geometryA.disjoint(geometryB);
                case Touches:  return geometryA.touches (geometryB);
                case Within:   return geometryA.within  (geometryB);
                case Overlaps: return geometryA.overlaps(geometryB);
                case Crosses:  return geometryA.crosses (geometryB);
                case Contains: return geometryA.contains(geometryB);
                case DWithin:  throw new UnsupportedOperatorException(spatialOperator);
                case Beyond:   throw new UnsupportedOperatorException(spatialOperator);
                case BBOX:     return true;
                default:
                    return geometryA.intersects(geometryB);
            }
        }
        return true;
    }

    /**
     * Adds the available observable capabilities provided by the specified dynamic model.
     */
    public void addContentOfDynamicModel(ObservableModel dynamicModel, AbstractServiceRequest<?> request) throws org.n52.sos.exception.CodedException
    {
        String objectId = ObservableObject.UNDEFINED_OBJECT_ID_FLAG;
        ReferencedEnvelope envelope = ObservableObject.UNDEFINED_ENVELOPE_FILTER_FLAG;
        TimePeriod timePeriod = new TimePeriod(ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG, ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG);
        
        TemporalFilter temporalFilter = null;
        SpatialFilter spatialFilter = null;
        
        DateTime minObservationTime = null;
        DateTime maxObservationTime = null;
        SosEnvelope globalEnvelope  = null;
        
        // ----------------------------------------------------------------------------------------------------
        // Extract the source data filters of the requested ServiceRequest.
        
        java.util.Map.Entry<String,Boolean> idf = extractIdentifiers(dynamicModel, request);
        if (idf.getValue()) objectId = idf.getKey();
        else return;
        
        java.util.Map.Entry<SpatialFilter,ReferencedEnvelope> spf = extractSpatialFilter(dynamicModel, request);
        spatialFilter = spf.getKey();
        envelope = spf.getValue();
        
        java.util.Map.Entry<TemporalFilter,TimePeriod> tmf = extractTemporalFilter(dynamicModel, request);
        temporalFilter = tmf.getKey();
        timePeriod = tmf.getValue();
        
        // ----------------------------------------------------------------------------------------------------
        // Add dynamic capabilities.
        
        java.util.Date dateFrom = timePeriod.getStart()!=null ? new java.util.Date(timePeriod.getStart().getMillis()) : ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
        java.util.Date dateTo   = timePeriod.getEnd()  !=null ? new java.util.Date(timePeriod.getEnd()  .getMillis()) : ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
        
        for (ObservableObject theObject : dynamicModel.enumerateObservableObjects(objectId, envelope, dateFrom, dateTo))
        {
            if (!DynamicWritableCache.validateObject(dynamicModel, theObject, spatialFilter, temporalFilter))
                continue;
            
            String offering = DynamicUtils.makeOfferingIdentifier(dynamicModel, theObject);
            String featureOfInterest = DynamicUtils.makeFeatureOfInterestIdentifier(dynamicModel, theObject);
            String procedure = DynamicUtils.makeProcedureIdentifier(dynamicModel, theObject);
            
            addOffering(offering);
            addProcedure(procedure);
            addProcedureForOffering(offering, procedure);
            
            DateTime minObsOfferingTime = null;
            DateTime maxObsOfferingTime = null;
            
            if (theObject.featureOfInterest!=null)
            {
                addFeatureOfInterest(featureOfInterest);
                addFeatureOfInterestForOffering(offering, featureOfInterest);
                addRelatedFeatureForOffering(offering, featureOfInterest);
                addProcedureForFeatureOfInterest(featureOfInterest, procedure);
                
                if (theObject.relatedFeatureUrls!=null && theObject.relatedFeatureUrls.size()>0)
                {
                    for (int i = 0, icount = theObject.relatedFeatureUrls.size(); i < icount; i++)
                    {
                        String relatedFeatureUrl = theObject.relatedFeatureUrls.get(i);
                        addRelatedFeatureForOffering(offering, relatedFeatureUrl);
                    }
                }
                
                SimpleFeature feature = theObject.featureOfInterest;
                BoundingBox bbox = feature.getBounds();
                int srid = 0;
                try { srid = CRS.lookupEpsgCode(bbox.getCoordinateReferenceSystem(), true); } catch (FactoryException e) { }
                if (srid!=0 && !hasEpsgCode(srid)) addEpsgCode(srid);
                
                Envelope envelope2 = new Envelope(bbox.getMinX(), bbox.getMaxX(), bbox.getMinY(), bbox.getMaxY());
                SosEnvelope observedArea = new SosEnvelope(envelope2, srid);
                setEnvelopeForOffering(offering, observedArea);
                setSpatialFilteringProfileEnvelopeForOffering(offering, observedArea);
                if (globalEnvelope==null) globalEnvelope = observedArea; else globalEnvelope.expandToInclude(observedArea);
                
                Geometry geometry = ((Geometry)feature.getDefaultGeometryProperty().getValue());
                int dimension = geometry.getDimension();
                
                if (dimension==2)
                {
                    setAllowedFeatureOfInterestTypeForOffering(offering, Collections.singletonList(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE));
                    setFeatureOfInterestTypesForOffering(offering, Collections.singletonList(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE));
                    addRoleForRelatedFeature(featureOfInterest, SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE);
                }                
                else
                if (dimension==1)
                {
                    setAllowedFeatureOfInterestTypeForOffering(offering, Collections.singletonList(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE));
                    setFeatureOfInterestTypesForOffering(offering, Collections.singletonList(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE));
                    addRoleForRelatedFeature(featureOfInterest, SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE);
                }                
                else 
                {
                    setAllowedFeatureOfInterestTypeForOffering(offering, Collections.singletonList(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT));
                    setFeatureOfInterestTypesForOffering(offering, Collections.singletonList(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT));
                    addRoleForRelatedFeature(featureOfInterest, SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT);
                }
            }
            if (theObject.attributes.size()>0)
            {
                setObservationTypesForOffering(offering, Collections.singletonList(OmConstants.OBS_TYPE_MEASUREMENT));
                
                for (ObservableAttribute attribute : theObject.attributes)
                {
                    String observableProperty = DynamicUtils.makeObservablePropertyIdentifier(dynamicModel, theObject, attribute);
                    
                    DateTime minObservationTime_i = new DateTime(attribute.dateFrom);
                    DateTime maxObservationTime_i = new DateTime(attribute.dateTo!=null ? attribute.dateTo : new DateTime());
                    if (minObservationTime==null) minObservationTime = minObservationTime_i; else if (minObservationTime_i.isBefore(minObservationTime)) minObservationTime = minObservationTime_i;                      
                    if (maxObservationTime==null) maxObservationTime = maxObservationTime_i; else if (maxObservationTime_i.isAfter (maxObservationTime)) maxObservationTime = maxObservationTime_i;
                    if (minObsOfferingTime==null) minObsOfferingTime = minObservationTime_i; else if (minObservationTime_i.isBefore(minObsOfferingTime)) minObsOfferingTime = minObservationTime_i;
                    if (maxObsOfferingTime==null) maxObsOfferingTime = maxObservationTime_i; else if (maxObservationTime_i.isAfter (maxObsOfferingTime)) maxObsOfferingTime = maxObservationTime_i;
                    
                    addObservablePropertyForOffering  (offering,  observableProperty);
                    addObservablePropertyForProcedure (procedure, observableProperty);
                    setProceduresForObservableProperty(observableProperty, Collections.singletonList(procedure));
                }
                setMinPhenomenonTimeForOffering(offering, minObsOfferingTime);
                setMaxPhenomenonTimeForOffering(offering, maxObsOfferingTime);                
                setMinResultTimeForOffering(offering, minObsOfferingTime);
                setMaxResultTimeForOffering(offering, maxObsOfferingTime);
            }
        }
        
        // ----------------------------------------------------------------------------------------------------
        // Add main resources and declarations.
        
        Set<String> formats = getRequestableProcedureDescriptionFormat();
        if (!formats.contains(SensorMLConstants.NS_SML)) getRequestableProcedureDescriptionFormat().add(SensorMLConstants.NS_SML);   
        if (!formats.contains(SensorML20Constants.NS_SML_20)) getRequestableProcedureDescriptionFormat().add(SensorML20Constants.NS_SML_20);
        //String WaterMLConstants_NS_WML_20_PROCEDURE_ENCODING = "http://www.opengis.net/waterml/2.0/observationProcess";
        //if (!formats.contains(WaterMLConstants_NS_WML_20_PROCEDURE_ENCODING)) getRequestableProcedureDescriptionFormats().add(WaterMLConstants_NS_WML_20_PROCEDURE_ENCODING);
        
        // Define valid spatial and temporal windows.
        if (globalEnvelope!=null)
        {
            if (hasGlobalEnvelope()) globalEnvelope.expandToInclude(getGlobalEnvelope());
            setGlobalEnvelope(globalEnvelope);
        }        
        if (minObservationTime!=null && hasMinPhenomenonTime())
        {
            DateTime minPhenomenonTime = getMinPhenomenonTime();
            if (minObservationTime.isBefore(minPhenomenonTime)) setMinPhenomenonTime(minObservationTime);
        }
        else
        if (minObservationTime!=null)
        {
            setMinPhenomenonTime(minObservationTime);
        }
        if (maxObservationTime!=null && hasMaxPhenomenonTime())
        {
            DateTime maxPhenomenonTime = getMaxPhenomenonTime();
            if (maxObservationTime.isAfter (maxPhenomenonTime)) setMaxPhenomenonTime(maxObservationTime);
        }
        else
        if (maxObservationTime!=null)
        {
            setMaxPhenomenonTime(maxObservationTime);
        }
        if (minObservationTime!=null && hasMinResultTime())
        {
            DateTime minResultTime = getMinResultTime();
            if (minObservationTime.isBefore(minResultTime)) setMinResultTime(minObservationTime);
        }
        else
        if (minObservationTime!=null)
        {
            setMinResultTime(minObservationTime);
        }
        if (maxObservationTime!=null && hasMaxResultTime())
        {
            DateTime maxResultTime = getMaxResultTime();
            if (maxObservationTime.isAfter (maxResultTime)) setMaxResultTime(maxObservationTime);
        }
        else
        if (maxObservationTime!=null)
        {
            setMaxResultTime(maxObservationTime);
        }
    }
    
    /**
     * Injects the virtual SensorDescription objects managed by this extension and required for the specified request.
     */
    private boolean injectDescribeSensorResponseDataOfDynamicModel(AbstractServiceResponse response, ObservableModel dynamicModel, AbstractServiceRequest<?> request) throws org.n52.sos.exception.CodedException
    {
        String objectId = ObservableObject.UNDEFINED_OBJECT_ID_FLAG;
        ReferencedEnvelope envelope = ObservableObject.UNDEFINED_ENVELOPE_FILTER_FLAG;
        TimePeriod timePeriod = new TimePeriod(ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG, ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG);
        
        TemporalFilter temporalFilter = null;
        SpatialFilter spatialFilter = null;
        
        DescribeSensorResponse theResponse = (DescribeSensorResponse)response;
        boolean dataInjected = false;
        
        // ----------------------------------------------------------------------------------------------------
        // Extract the source data filters of the requested ServiceRequest.
        
        java.util.Map.Entry<String,Boolean> idf = extractIdentifiers(dynamicModel, request);
        if (idf.getValue()) objectId = idf.getKey();
        else return false;
        
        java.util.Map.Entry<SpatialFilter,ReferencedEnvelope> spf = extractSpatialFilter(dynamicModel, request);
        spatialFilter = spf.getKey();
        envelope = spf.getValue();
        
        java.util.Map.Entry<TemporalFilter,TimePeriod> tmf = extractTemporalFilter(dynamicModel, request);
        temporalFilter = tmf.getKey();
        timePeriod = tmf.getValue();
        
        // ----------------------------------------------------------------------------------------------------
        // Add dynamic observations.
        
        java.util.Date dateFrom = timePeriod.getStart()!=null ? new java.util.Date(timePeriod.getStart().getMillis()) : ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
        java.util.Date dateTo   = timePeriod.getEnd()  !=null ? new java.util.Date(timePeriod.getEnd()  .getMillis()) : ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
        
        List<SosProcedureDescription> procedureCollection = theResponse.getProcedureDescriptions();
        procedureCollection.clear();
        procedureCollection = new ArrayList<SosProcedureDescription>();
        
        for (ObservableObject theObject : dynamicModel.enumerateObservableObjects(objectId, envelope, dateFrom, dateTo))
        {
            if (!DynamicWritableCache.validateObject(dynamicModel, theObject, spatialFilter, temporalFilter))
                continue;
            
            if (theObject.featureOfInterest!=null)
            {
                String descriptionFormat = ((DescribeSensorRequest)request).getProcedureDescriptionFormat();
                SosProcedureDescription procedure = DynamicUtils.makeProcedureDescription(dynamicModel, theObject, descriptionFormat);
                procedureCollection.add(procedure);
                dataInjected = true;
            }
        }
        if (dataInjected)
        {
            theResponse.setSensorDescriptions(procedureCollection);
            return true;
        }
        return false;
    }
    
    /**
     * Injects the virtual featureOfInterest objects managed by this extension and required for the specified request.
     */
    private boolean injectFeatureOfInterestResponseDataOfDynamicModel(AbstractServiceResponse response, ObservableModel dynamicModel, AbstractServiceRequest<?> request) throws org.n52.sos.exception.CodedException
    {
        String objectId = ObservableObject.UNDEFINED_OBJECT_ID_FLAG;
        ReferencedEnvelope envelope = ObservableObject.UNDEFINED_ENVELOPE_FILTER_FLAG;
        TimePeriod timePeriod = new TimePeriod(ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG, ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG);
        
        TemporalFilter temporalFilter = null;
        SpatialFilter spatialFilter = null;
        
        GetFeatureOfInterestResponse theResponse = (GetFeatureOfInterestResponse)response;
        boolean dataInjected = false;
        
        // ----------------------------------------------------------------------------------------------------
        // Extract the source data filters of the requested ServiceRequest.
        
        java.util.Map.Entry<String,Boolean> idf = extractIdentifiers(dynamicModel, request);
        if (idf.getValue()) objectId = idf.getKey();
        else return false;
        
        java.util.Map.Entry<SpatialFilter,ReferencedEnvelope> spf = extractSpatialFilter(dynamicModel, request);
        spatialFilter = spf.getKey();
        envelope = spf.getValue();
        
        java.util.Map.Entry<TemporalFilter,TimePeriod> tmf = extractTemporalFilter(dynamicModel, request);
        temporalFilter = tmf.getKey();
        timePeriod = tmf.getValue();
        
        // ----------------------------------------------------------------------------------------------------
        // Add dynamic featureOfInterest objects.
        
        java.util.Date dateFrom = timePeriod.getStart()!=null ? new java.util.Date(timePeriod.getStart().getMillis()) : ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
        java.util.Date dateTo   = timePeriod.getEnd()  !=null ? new java.util.Date(timePeriod.getEnd()  .getMillis()) : ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
        
        FeatureCollection featureCollection = (FeatureCollection)theResponse.getAbstractFeature();
        if (featureCollection==null) featureCollection = new FeatureCollection();
        
        for (ObservableObject theObject : dynamicModel.enumerateObservableObjects(objectId, envelope, dateFrom, dateTo))
        {
            if (!DynamicWritableCache.validateObject(dynamicModel, theObject, spatialFilter, temporalFilter))
                continue;
            
            if (theObject.featureOfInterest!=null)
            {
                SamplingFeature feature = DynamicUtils.makeSamplingFeature(dynamicModel, theObject, getDefaultEPSGCode());
                featureCollection.addMember(feature);
                dataInjected = true;
            }
        }
        if (dataInjected)
        {
            theResponse.setAbstractFeature(featureCollection);
            return true;
        }
        return false;
    }
    
    /**
     * Injects the virtual objects managed by this extension and required for the specified request.
     */
    private boolean injectObservationResponseDataOfDynamicModel(AbstractServiceResponse response, ObservableModel dynamicModel, AbstractServiceRequest<?> request) throws org.n52.sos.exception.CodedException
    {
        String objectId = ObservableObject.UNDEFINED_OBJECT_ID_FLAG;
        ReferencedEnvelope envelope = ObservableObject.UNDEFINED_ENVELOPE_FILTER_FLAG;
        TimePeriod timePeriod = new TimePeriod(ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG, ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG);
        
        TemporalFilter temporalFilter = null;
        SpatialFilter spatialFilter = null;
        
        AbstractObservationResponse theResponse = (AbstractObservationResponse)response;
        boolean dataInjected = false;
        
        // ----------------------------------------------------------------------------------------------------
        // Extract the source data filters of the requested ServiceRequest.
        
        java.util.Map.Entry<String,Boolean> idf = extractIdentifiers(dynamicModel, request);
        if (idf.getValue()) objectId = idf.getKey();
        else return false;
        
        java.util.Map.Entry<SpatialFilter,ReferencedEnvelope> spf = extractSpatialFilter(dynamicModel, request);
        spatialFilter = spf.getKey();
        envelope = spf.getValue();
        
        java.util.Map.Entry<TemporalFilter,TimePeriod> tmf = extractTemporalFilter(dynamicModel, request);
        temporalFilter = tmf.getKey();
        timePeriod = tmf.getValue();
        
        // ----------------------------------------------------------------------------------------------------
        // Extract the requested full-name properties.
        
        Map<String,String> propertiesMap = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
        
        if (request instanceof GetObservationByIdRequest)
        {
            GetObservationByIdRequest theRequest = (GetObservationByIdRequest)request;
            
            if (theRequest.getObservationIdentifier()!=null)
            {
                for (String identifier : theRequest.getObservationIdentifier())
                {
                    if (!propertiesMap.containsKey(identifier)) propertiesMap.put(identifier, identifier);
                }
            }
        }
        else
        if (request instanceof GetObservationRequest)
        {
            GetObservationRequest theRequest = (GetObservationRequest)request;
            
            if (theRequest.getObservedProperties()!=null)
            {
                for (String identifier : theRequest.getObservedProperties())
                {
                    if (!propertiesMap.containsKey(identifier)) propertiesMap.put(identifier, identifier);
                }
            }
        }
        
        // ----------------------------------------------------------------------------------------------------
        // Add dynamic observations.
        
        java.util.Date dateFrom = timePeriod.getStart()!=null ? new java.util.Date(timePeriod.getStart().getMillis()) : ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
        java.util.Date dateTo   = timePeriod.getEnd()  !=null ? new java.util.Date(timePeriod.getEnd()  .getMillis()) : ObservableObject.UNDEFINED_DATETIME_FILTER_FLAG;
        
        List<OmObservation> observations = new ArrayList<OmObservation>();
        observations.addAll(theResponse.getObservationCollection());
        
        for (MeasureSet measureSet : dynamicModel.enumerateMeasures(objectId, envelope, dateFrom, dateTo))
        {
            ObservableObject theObject = measureSet.ownerObject;
            
            if (measureSet.measures.size()==0 || !DynamicWritableCache.validateObject(dynamicModel, theObject, spatialFilter, temporalFilter))
                continue;
            
            String offering = DynamicUtils.makeOfferingIdentifier(dynamicModel, theObject);
            String observableProperty = DynamicUtils.makeObservablePropertyIdentifier(dynamicModel, theObject, measureSet.attribute);
            
            if (propertiesMap.size()==0 || propertiesMap.containsKey(observableProperty))
            {
                ObservableAttribute attribute = measureSet.attribute;
                
                SamplingFeature feature = DynamicUtils.makeSamplingFeature(dynamicModel, theObject, getDefaultEPSGCode());
                OmObservableProperty property = DynamicUtils.makeObservableProperty(dynamicModel, theObject, attribute);
                SosProcedureDescription procedure = DynamicUtils.makeProcedureDescription(dynamicModel, theObject, SensorML20Constants.NS_SML_20);
                
                OmObservationConstellation observationConstellation = new OmObservationConstellation()
                    .addOffering          (offering)
                    .setProcedure         (procedure)
                    .setFeatureOfInterest (feature)
                    .setObservableProperty(property)
                    .setObservationType   (OmConstants.OBS_TYPE_MEASUREMENT);
                
                OmObservation observation = new OmObservation();
                observation.setObservationConstellation(observationConstellation);
                observation.setIdentifier(observableProperty);
                observation.setDescription(attribute.description);
                observation.setResultTime(new TimeInstant(new DateTime(), TimeIndeterminateValue.template));
                observation.setResultType(OmConstants.OBS_TYPE_MEASUREMENT);
                
                for (Measure measure : measureSet.measures)
                {
                    ObservationValue<?> value = DynamicUtils.makeObservationValue(attribute, measure);
                    if (value==null) continue;
                    if (observation.getValue()==null) observation.setValue(value); else observation.mergeWithObservation(value);
                }
                if (observation.getValue()!=null)
                {
                    observations.add(observation);
                    dataInjected = true;
                }
            }
        }
        propertiesMap.clear();
        
        if (dataInjected)
        {
            theResponse.setObservationCollection(observations);
            return true;
        }     
        return false;
    }
    
    /**
     * Injects the virtual objects managed by this extension and required for the specified request.
     */
    public boolean addResponseDataOfDynamicModel(AbstractServiceResponse response, ObservableModel dynamicModel, AbstractServiceRequest<?> request) throws org.n52.sos.exception.CodedException
    {
        if (response instanceof DescribeSensorResponse)
        {
            return injectDescribeSensorResponseDataOfDynamicModel(response, dynamicModel, request);
        }
        if (response instanceof GetFeatureOfInterestResponse)
        {
            return injectFeatureOfInterestResponseDataOfDynamicModel(response, dynamicModel, request);
        }
        if (response instanceof AbstractObservationResponse)
        {
            return injectObservationResponseDataOfDynamicModel(response, dynamicModel, request);
        }
        return false;
    }
}
