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
package org.n52.sos.ds.hibernate.entities.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.n52.sos.ds.hibernate.entities.feature.gmd.ResponsiblePartyEntity;
import org.n52.sos.ds.hibernate.entities.feature.gml.VerticalDatumEntity;
import org.n52.sos.util.CollectionHelper;

public class MonitoringPointContent {

    /* 0..* */
    private List<ResponsiblePartyEntity> relatedParty = new ArrayList<>();
    /* 0..* */
    private List<VerticalDatumEntity> verticalDatum = new ArrayList<>();
    
    public List<ResponsiblePartyEntity> getRelatedParty() {
        return relatedParty;
    }
    
    public void setRelatedParty(Collection<ResponsiblePartyEntity> relatedParty) {
        this.relatedParty.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(relatedParty)) {
            this.relatedParty.addAll(relatedParty);
        }
    }
    
    public void addRelatedParty(Collection<ResponsiblePartyEntity> relatedParty) {
        if (relatedParty != null) {
            this.relatedParty.addAll(relatedParty);
        }
    }
    
    public void addRelatedParty(ResponsiblePartyEntity relatedParty) {
        if (relatedParty != null) {
            this.relatedParty.add(relatedParty);
        }
    }
    
    public boolean hasRelatedParty() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getRelatedParty());
    }
    
    public List<VerticalDatumEntity> getVerticalDatum() {
        return verticalDatum;
    }
    
    public void setVerticalDatum(Collection<VerticalDatumEntity> verticalDatum) {
        this.verticalDatum.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(verticalDatum)) {
            this.verticalDatum.addAll(verticalDatum);
        }
    }
    
    public void addVerticalDatum(Collection<VerticalDatumEntity> verticalDatum) {
        if (verticalDatum != null) {
            this.verticalDatum.addAll(verticalDatum);
        }
    }
    
    public void addVerticalDatum(VerticalDatumEntity verticalDatum) {
        if (verticalDatum != null) {
            this.verticalDatum.add(verticalDatum);
        }
    }
    
    public boolean hasVerticalDatum() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getVerticalDatum());
    }
}
