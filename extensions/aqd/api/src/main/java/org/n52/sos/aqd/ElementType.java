/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.aqd;

import org.n52.sos.aqd.AqdConstants.PrimaryObservation;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.util.StringHelper;

public class ElementType {
    

    public static ElementType START_TIME = new ElementType("StartTime", OmConstants.PHEN_SAMPLING_TIME,
            OmConstants.PHEN_UOM_ISO8601);

    public static ElementType END_TIME = new ElementType("EndTime", OmConstants.PHEN_SAMPLING_TIME, OmConstants.PHEN_UOM_ISO8601);

    public static ElementType VERIFICATION = new ElementType("Verification", AqdConstants.DEFINITION_VERIFICATION);

    public static ElementType VALIDITY = new ElementType("Validity", AqdConstants.DEFINITION_VALIDITY);
    
    public static ElementType DATA_CAPTURE = new ElementType("DataCapture", AqdConstants.DEFINITION_DATA_CAPTURE, AqdConstants.DEFINITION_UOM_STATISTICS_PERCENTAGE);
    
    private final String name;

    private final String definition;

    private final String uom;

    public ElementType(String name, String definition) {
        this(name, definition, null);
    }

    public ElementType(String name, String definition, String uom) {
        this.name = name;
        this.definition = definition;
        this.uom = uom;
    }

    private ElementType(String name,PrimaryObservation primaryObs, String uom) {
        this(name, primaryObs.getConceptURI(), uom);
    }
    
    public String getName() {
        return name;
    }

    public String getDefinition() {
        return definition;
    }

    public String getUOM() {
        return uom;
    }

    public boolean isSetUOM() {
        return StringHelper.isNotEmpty(getUOM());
    }
    
    public static ElementType getValueElementType(PrimaryObservation primaryObs, String uom)  {
        return new ElementType("Value", primaryObs, uom);
    }
}
