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

/**
 * Provides information of one Observable Attribute of an Object in a generic data Model.
 * 
 * @author Alvaro Huarte <ahuarte@tracasa.es>
 */
public class ObservableAttribute 
{
    /** Creates a new ObservableAttribute object. */
    public ObservableAttribute()
    {
    }
    /** Creates a cloned ObservableAttribute object. */
    public ObservableAttribute(ObservableAttribute attribute)
    {
        name = attribute.name;
        description = attribute.description;
        dateFrom = attribute.dateFrom;
        dateTo = attribute.dateTo;
        stepTime = attribute.stepTime;
        units = attribute.units;
    }
    
    /** 
     * Name of the Attribute. 
     */
    public String name;

    /** 
     * Description of the Attribute. 
     */
    public String description;

    /** 
     * Uom (Units of measure) of the Attribute. 
     */
    public String units;
    
    /**
     * Valid start TimePeriod date of the Attribute.
     */
    public java.util.Date dateFrom;
    
    /**
     * Valid end TimePeriod date of the Attribute.
     */
    public java.util.Date dateTo;
    
    /**
     * Valid step time of the TimePeriod of the Attribute in milliseconds.
     */
    public long stepTime =  300000;
    
    @Override
    public String toString()
    {
        return String.format("Attribute=%s DateFrom=%s DateTo=%s StepTime=%d Units=[%s]", name, dateFrom.toString(), dateTo.toString(), stepTime, units!=null ? units : "");
    }
}
