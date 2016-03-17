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

import java.util.Collections;

import org.n52.sos.exception.ows.concrete.InvalidSridException;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.BooleanValue;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TextValue;
import org.n52.sos.ogc.sensorML.v20.SimpleProcess;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.service.ServiceConfiguration;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Utility class with helper dynamic-object conversion functions.
 */
class DynamicUtils 
{
    /** No constructor available. */
    protected DynamicUtils()
    {
        super();
    }
    
    /** Compose a complex identifier suing the parameters specified. */
    private static String composeComplexIdentifier(String identifierPrefix, String identifierKey)
    {
        String complexId = identifierPrefix;
        if ( complexId.endsWith("/")) complexId = complexId.substring(0, complexId.length()-1);
        if (!complexId.endsWith(":")) complexId = complexId + ":";
        complexId += identifierKey;
        return complexId;
    }    
    /** Returns the offering full-name identifier of the specified object. */
    public static String makeOfferingIdentifier(ObservableModel dynamicModel, ObservableObject theObject)
    {
        return composeComplexIdentifier(ServiceConfiguration.getInstance().getDefaultOfferingPrefix(), dynamicModel.getName()+"/"+theObject.objectName);
    }
    /** Returns the featureOfInterest full-name identifier of the specified object. */
    public static String makeFeatureOfInterestIdentifier(ObservableModel dynamicModel, ObservableObject theObject)
    {
        return composeComplexIdentifier(ServiceConfiguration.getInstance().getDefaultFeaturePrefix(), dynamicModel.getName()+"/"+theObject.objectName);
    }
    /** Returns the procedure full-name identifier of the specified object. */
    public static String makeProcedureIdentifier(ObservableModel dynamicModel, ObservableObject theObject)
    {
        return composeComplexIdentifier(ServiceConfiguration.getInstance().getDefaultProcedurePrefix(), dynamicModel.getName()+"/"+theObject.objectName);
    }
    /** Returns the property full-name identifier of the specified object. */
    public static String makeObservablePropertyIdentifier(ObservableModel dynamicModel, ObservableObject theObject, ObservableAttribute attribute)
    {
        return composeComplexIdentifier(ServiceConfiguration.getInstance().getDefaultObservablePropertyPrefix(), dynamicModel.getName()+"/"+theObject.objectName+"/"+attribute.name);
    }
    
    /**
     * Creates a new ObservationValue from the specified Measure.
     */
    public static ObservationValue<?> makeObservationValue(ObservableAttribute attribute, Measure measure)
    {
        ObservationValue<?> value = null;
        
        if (measure.value instanceof Double)
        {
            QuantityValue v = new QuantityValue((Double)measure.value, attribute.units);
            value = new SingleObservationValue<Double>(new TimeInstant(measure.phenomenonTime), v);
        }
        else
        if (measure.value instanceof Integer)
        {
            CountValue v = new CountValue((Integer)measure.value);
            value = new SingleObservationValue<Integer>(new TimeInstant(measure.phenomenonTime), v);
        }
        else
        if (measure.value instanceof Boolean)
        {
            BooleanValue v = new BooleanValue((Boolean)measure.value);
            value = new SingleObservationValue<Boolean>(new TimeInstant(measure.phenomenonTime), v);
        }
        else
        if (measure.value instanceof org.n52.sos.ogc.gml.AbstractGeometry)
        {
            GeometryValue v = new GeometryValue((org.n52.sos.ogc.gml.AbstractGeometry)measure.value);
            return new SingleObservationValue<Geometry>(new TimeInstant(measure.phenomenonTime), v);            
        }
        else
        if (measure.value!=null)
        {
            TextValue v = new TextValue(measure.value.toString());
            value = new SingleObservationValue<String>(new TimeInstant(measure.phenomenonTime), v);            
        }
        return value;
    }
    
    /**
     * Creates a new SamplingFeature from the specified object. 
     */
    public static SamplingFeature makeSamplingFeature(ObservableModel dynamicModel, ObservableObject theObject, int defaultSrid) throws InvalidSridException
    {
        SamplingFeature feature = new SamplingFeature(new CodeWithAuthority(DynamicUtils.makeFeatureOfInterestIdentifier(dynamicModel, theObject)));
        feature.setDescription(theObject.description);
        
        if (theObject.featureOfInterest!=null)
        {
            Geometry geometry = ((Geometry)theObject.featureOfInterest.getDefaultGeometryProperty().getValue());
            if (geometry.getSRID()==0) geometry.setSRID(defaultSrid);
            int dimension = geometry.getDimension();
            
            if (dimension==2)
            {
                feature.setFeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE);
            }
            if (dimension==1)
            {
                feature.setFeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE);
            }
            else
            {
                feature.setFeatureType(SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT);
            }
            feature.setGeometry(geometry);
        }
        if (theObject.relatedFeatureUrls!=null && theObject.relatedFeatureUrls.size()>0)
        {
            String relatedFeatureUrl = theObject.relatedFeatureUrls.get(0);
            feature.setUrl(relatedFeatureUrl);
        }
        return feature;
    }
    
    /**
     * Creates a new ObservableProperty from the specified object. 
     */    
    public static OmObservableProperty makeObservableProperty(ObservableModel dynamicModel, ObservableObject theObject, ObservableAttribute attribute)
    {
        OmObservableProperty property = new OmObservableProperty(DynamicUtils.makeObservablePropertyIdentifier(dynamicModel, theObject, attribute));
        property.setDescription(attribute.description);
        property.setUnit(attribute.units);
        property.setValueType(OmConstants.OBS_TYPE_MEASUREMENT);
        return property;
    }
    
    /**
     * Creates a new ProcedureDescription from the specified object.
     */
    public static SosProcedureDescription makeProcedureDescription(ObservableModel dynamicModel, ObservableObject theObject, String descriptionFormat)
    {
        SosProcedureDescription procedure = new SimpleProcess().setIdentifier(DynamicUtils.makeProcedureIdentifier(dynamicModel, theObject));
        procedure.setDescription(theObject.description);
        
        String featuresOfInterest = DynamicUtils.makeFeatureOfInterestIdentifier(dynamicModel, theObject);
        procedure.setFeaturesOfInterest(Collections.singletonList(featuresOfInterest));
        
        SosOffering offering = new SosOffering(DynamicUtils.makeOfferingIdentifier(dynamicModel, theObject), "");
        offering.setDescription(theObject.description);
        procedure.addOffering(offering);
        
        if (descriptionFormat!=null && descriptionFormat.length()>0) {
            procedure.setDescriptionFormat(descriptionFormat);
        }
        return procedure;
    }
}
