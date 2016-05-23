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
package org.n52.sos.iso.gmd;

import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.util.Constants;
import org.n52.sos.util.JavaHelper;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class GmlBaseUnit {
            
    private String id;
    private final String identifier;
    private final CodeType catalogSymbol;
    private final String unitSystem;

    public GmlBaseUnit(String id, String identifier, CodeType catalogSymbol,
                       String unitSystem) {
        this.id = id;
        this.identifier = identifier;
        this.catalogSymbol = catalogSymbol;
        this.unitSystem = unitSystem;
    }

    public String getId() {
        return id;
    }
    
    public GmlBaseUnit unifyId(Object object) {
        id = id + Constants.UNDERSCORE_STRING + JavaHelper.generateID(object.toString());
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public CodeType getCatalogSymbol() {
        return catalogSymbol;
    }

    public String getUnitSystem() {
        return unitSystem;
    }

    public static GmlBaseUnit uncertaintyEstimation() {
        return new GmlBaseUnit("PercentageUnit", "http://dd.eionet.europa.eu/vocabularies/aq/resultquality/uncertaintyestimation/", new CodeType("%", "http://www.opengis.net/def/uom/UCUM/"), "http://www.opengis.net/def/uom/UCUM/");
    }

}
