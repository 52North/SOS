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

import java.util.List;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

import org.n52.sos.extensions.ObservableAttribute;
import org.n52.sos.extensions.ObservableModel;
import org.n52.sos.extensions.ObservableObject;
import org.n52.sos.extensions.model.AbstractModel;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Implements a FULL-FeatureStore enumerable cursor of ObservableObject entities from a Wonderware database.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
class WonderwareFullObservableObjectCursor extends WonderwareObservableDataCursor<ObservableObject>
{
    /** 
     * Creates a new WonderwareFullObservableObjectCursor object.
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
    public WonderwareFullObservableObjectCursor(ObservableModel observableModel, String objectId, String databaseDriverClass, String databaseConnectionUrl, SimpleFeatureSource featureSource, String featureKey, ReferencedEnvelope envelope, List<SimpleObservableAttribute> attributeList)
    {
        super(observableModel, objectId, databaseDriverClass, databaseConnectionUrl, featureSource, featureKey, envelope, attributeList);
    }
    
    /** 
     * Read the ObservableObject from the specified ResultSet.
     */
    @Override
    protected ObservableObject readObservableObject(SimpleFeature feature, List<SimpleObservableAttribute> databaseAttributeList)
    {
        throw new UnsupportedOperationException("cannot read from cursor");
    }
    
    /**
     * Returns {@code true} if the iteration has more elements.
     */
    @Override
    public boolean hasNext() 
    {
        if (featureIterator!=null && featureIterator.hasNext())
        {
            SimpleFeature feature = featureIterator.next();
            
            Geometry geometry = ((Geometry)feature.getDefaultGeometryProperty().getValue());
            if (geometry.getSRID()==0) geometry.setSRID(defaultEpsgCode);
            
            String objectType = feature.getName().getLocalPart();
            String objectName = feature.getAttribute(featureFieldKey).toString();
            
            ObservableObject theObject = new ObservableObject();
            theObject.objectType = objectType;
            theObject.objectName = objectName;
            theObject.featureOfInterest = feature;
            theObject.description = "Historic and live observable properties of the object '"+objectName+"' of type '"+objectType+"'";
            
            // Assigns information of attributes.
            for (SimpleObservableAttribute databaseAttribute : databaseAttributeList)
            {
                java.util.Date startDate = databaseAttribute.dateFrom;
                java.util.Date finalDate = databaseAttribute.dateTo!=null ? databaseAttribute.dateTo : new java.util.Date(); //-> now()!

                ObservableAttribute attribute = new ObservableAttribute();
                attribute.name = databaseAttribute.name;
                attribute.description = "Describes historic and live measures of the attribute '"+attribute.name+"'";
                attribute.dateFrom = startDate;
                attribute.dateTo = finalDate;
                attribute.stepTime = databaseAttribute.stepTime;
                attribute.units = databaseAttribute.units;
                theObject.attributes.add(attribute);
            }
            if (theObject.featureOfInterest!=null && currentModel instanceof AbstractModel)
            {
                AbstractModel abstractModel = (AbstractModel)currentModel;
                abstractModel.populateRelatedFeatureUrls(theObject);
            }
            currentObject = theObject;
            return true;
        }
        return super.hasNext();
    }    
}
