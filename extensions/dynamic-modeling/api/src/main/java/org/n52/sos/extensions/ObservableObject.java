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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Provides information of an Object with Observable Attributes in a generic data Model.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class ObservableObject 
{
    /** Unassigned object id filter flag. */
    public static final String UNDEFINED_OBJECT_ID_FLAG = "";
    /** Unassigned coordinate filter flag. */
    public static final ReferencedEnvelope UNDEFINED_ENVELOPE_FILTER_FLAG = null;
    /** Unassigned DateTime filter flag. */
    public static final Date UNDEFINED_DATETIME_FILTER_FLAG = null;
    
    /** 
     * Category/Type/Layer of the Object.
     */
    public String objectType;
    
    /** 
     * Name of the Object.
     */
    public String objectName;

    /** 
     * Description of the Object.
     */
    public String description;
    
    /** 
     * Feature of Interest of the Object.
     */
    public SimpleFeature featureOfInterest;
    
    /** 
     * Related Feature of Interest URL collection of the Object.
     */
    public List<String> relatedFeatureUrls = new ArrayList<String>();
    
    /** 
     * Observable Attribute collection of the Object.
     */
    public List<ObservableAttribute> attributes = new ArrayList<ObservableAttribute>();
        
    @Override
    public String toString()
    {
        return String.format("Category=%s Name=%s Attributes=[%s]", objectType, objectName, attributes.toString());
    }
    
    /**
     * Decompose the specified object filter pattern in single items.
     */
    public static List<String[]> decomposeObjectFilterPattern(String objectFilterPattern)
    {
        List<String[]> tempList = new ArrayList<String[]>();
        String objectType;
        String objectName;
        String attribName;
        
        for (String parseItems : objectFilterPattern.trim().split(";"))
        {
            String[] tempArray = parseItems.split(":");
            
            if (tempArray.length==2)
            {
                objectType = tempArray[0];
                tempArray  = tempArray[1].split("\\.");
            }
            else
            {
                objectType = "*";
                tempArray  = tempArray[0].split("\\.");
            }
            if (tempArray.length==2)
            {
                objectName = tempArray[0];
                attribName = tempArray[1];
            }
            else
            {
                objectName = "*";
                attribName = tempArray[0];                
            }
            tempList.add(new String[]{objectType,objectName,attribName});
        }
        return tempList;
    }
    
    /**
     * Compose the SQL-where clause to apply the specified object filter pattern to an observable model.
     */
    public static String composeObjectFilterWhereClause(String objectFilterPattern, String objectTypeField, String objectNameField)
    {
        StringBuilder stringBuilder = new StringBuilder();
        boolean doneOr = false;
        
        for (String[] patternItem : ObservableObject.decomposeObjectFilterPattern(objectFilterPattern))
        {
            if (composeOr(stringBuilder, objectTypeField, patternItem[0], objectNameField, patternItem[1]))
            {
                stringBuilder.append(" OR ");
                doneOr = true;
            }
        }
        if (stringBuilder.length()>0)
        {
            if (doneOr) stringBuilder.delete(stringBuilder.length()-4, stringBuilder.length());
            return stringBuilder.toString();
        }
        return "";
    }
    /**
     * Composer the OR-where clause from the specified object filter pattern.
     */
    private static boolean composeOr(StringBuilder stringBuilder, String objectTypeField, String objectTypeVal, String objectNameField, String objectNameVal)
    {
        String[] objectTypeItems = objectTypeVal.split("\\,");
        String[] objectNameItems = objectNameVal.split("\\,");
        
        boolean doneRes = false;
        boolean doneOr2 = false;
        boolean doneOne = false;
                
        for (String objectTypeItem : objectTypeItems)
        {
            stringBuilder.append("(");
            doneOr2 = false;
            doneOne = false;
            
            if (!objectTypeItem.equalsIgnoreCase("*"))
            {
                stringBuilder.append(objectTypeField);
                stringBuilder.append(objectTypeItem.contains("%") ? " LIKE " : "=");
                stringBuilder.append("'");
                stringBuilder.append(objectTypeItem);
                stringBuilder.append("'");
                stringBuilder.append(" OR ");
                doneOr2 = true;
            }
            if (!(doneOne = composeAnd(stringBuilder, objectNameField, objectNameItems)) && doneOr2)
            {
                stringBuilder.delete(stringBuilder.length()-4, stringBuilder.length());
            }            
            if (doneOr2 || doneOne)
            {
                stringBuilder.append(")").append(" OR ");
                doneRes = true;
            }
            else
            {
                stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
            }
        }
        if (doneRes)
        {
            stringBuilder.delete(stringBuilder.length()-4, stringBuilder.length());
        }
        return doneOr2;
    }
    /**
     * Composer the AND-where clause from the specified object filter pattern.
     */    
    private static boolean composeAnd(StringBuilder stringBuilder, String objectNameField, String[] objectNameItems)
    {
        boolean doneAnd = false;
        
        stringBuilder.append("(");
        
        for (String objectNameItem : objectNameItems)
        {
            if (!objectNameItem.equalsIgnoreCase("*"))
            {
                stringBuilder.append(objectNameField);
                stringBuilder.append(objectNameItem.contains("%") ? " LIKE " : " = ");
                stringBuilder.append("'");
                stringBuilder.append(objectNameItem);
                stringBuilder.append("'");
                stringBuilder.append(" AND ");
                doneAnd = true;
            }
        }
        if (doneAnd)
        {
            stringBuilder.delete(stringBuilder.length()-5, stringBuilder.length());
            stringBuilder.append(")");
        }
        else
        {
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
        }
        return doneAnd;
    }
}
