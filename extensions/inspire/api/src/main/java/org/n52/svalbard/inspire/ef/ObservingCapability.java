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
package org.n52.svalbard.inspire.ef;

import java.net.URI;

import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.w3c.xlink.SimpleAttrs;

public class ObservingCapability extends SimpleAttrs {
    
    /**
     * 1..1
     */
    private Time observingTime;
    
    /**
     * 1..1
     */
    private ReferenceType processType; 
    
    /**
     * 1..1
     */
    private ReferenceType resultNature;
    
    /**
     * 0..1
     */
    private URI onlineResource;
    
    /**
     * 1..1
     */
    private SosProcedureDescription procedure;
    
    /**
     * 0..1
     */
    private AbstractFeature featureOfInterest;
    
    /**
     * 1..1
     */
    private ReferenceType observedProperty;
    
    public ObservingCapability(String href) {
        setHref(href);
    }
    
    public ObservingCapability(Time observingTime, ReferenceType processType, ReferenceType resultNature, SosProcedureDescription procedure, ReferenceType observedProperty) {
        this.observingTime = observingTime;
        this.processType = processType;
        this.resultNature = resultNature;
        this.procedure = procedure;
        this.observedProperty = observedProperty;
    }
}
