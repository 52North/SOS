/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.gml;

import java.util.List;

import org.n52.sos.w3c.Nillable;
import org.n52.sos.w3c.xlink.Referenceable;

import com.google.common.collect.Lists;

/**
 * Internal representation of the OGC GML VerticlaCRS.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class VerticalCRS extends AbstractCRS {

    private static final long serialVersionUID = -3827860576029113036L;
    
    private Referenceable<VerticalCS> verticalCS = Referenceable.of(Nillable.<VerticalCS>missing());
    
    private Referenceable<VerticalDatum> verticalDatum = Referenceable.of(Nillable.<VerticalDatum>missing());

    public VerticalCRS(CodeWithAuthority identifier, String scope, Referenceable<VerticalCS> verticalCS, Referenceable<VerticalDatum> verticalDatum) {
        this(identifier, Lists.newArrayList(scope), verticalCS, verticalDatum);
    }
    
    public VerticalCRS(CodeWithAuthority identifier, List<String> scope, Referenceable<VerticalCS> verticalCS, Referenceable<VerticalDatum> verticalDatum) {
        super(identifier, scope);
        setVerticalCS(verticalCS);
        setVerticalDatum(verticalDatum);
    }

    /**
     * @return the verticalCS
     */
    public Referenceable<VerticalCS> getVerticalCS() {
        return verticalCS;
    }

    /**
     * @param verticalCS the verticalCS to set
     */
    public void setVerticalCS(Referenceable<VerticalCS> verticalCS) {
        this.verticalCS = verticalCS;
    }

    /**
     * @return the verticalDatum
     */
    public Referenceable<VerticalDatum> getVerticalDatum() {
        return verticalDatum;
    }

    /**
     * @param verticalDatum the verticalDatum to set
     */
    public void setVerticalDatum(Referenceable<VerticalDatum> verticalDatum) {
        this.verticalDatum = verticalDatum;
    }
    
}
