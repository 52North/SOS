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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.n52.sos.ds.hibernate.entities.feature.gmd.ResponsiblePartyEntity;
import org.n52.sos.ds.hibernate.entities.feature.gml.VerticalDatumEntity;

/**
 * Hibernate entiity for the abstract monitoringPoint
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public abstract class AbstractMonitoringFeature extends FeatureOfInterest {

    private MonitoringPointContent content;
    
    /**
     * @return the content
     */
    public MonitoringPointContent getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(MonitoringPointContent content) {
        this.content = content;
    }
    
    public boolean isSetContent() {
        return getContent() != null;
    }

    public List<ResponsiblePartyEntity> getRelatedParty() {
        if (isSetContent()) {
            return getContent().getRelatedParty();
        }
        return Collections.emptyList();
    }
    
    public AbstractMonitoringFeature setRelatedParty(Collection<ResponsiblePartyEntity> relatedParty) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().setRelatedParty(relatedParty);
        return this;
    }
    
    public AbstractMonitoringFeature addRelatedParty(Collection<ResponsiblePartyEntity> relatedParty) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().addRelatedParty(relatedParty);
        return this;
    }
    
    public AbstractMonitoringFeature addRelatedParty(ResponsiblePartyEntity relatedParty) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().addRelatedParty(relatedParty);
        return this;
    }
    
    public boolean hasRelatedParty() {
        return isSetContent() ? getContent().hasRelatedParty() : false;
    }
    
    public List<VerticalDatumEntity> getVerticalDatum() {
        if (isSetContent()) {
            return getContent().getVerticalDatum();
        }
        return Collections.emptyList();
    }
    
    public AbstractMonitoringFeature setVerticalDatum(Collection<VerticalDatumEntity> verticalDatum) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().setVerticalDatum(verticalDatum);
        return this;
    }
    
    public AbstractMonitoringFeature addVerticalDatum(Collection<VerticalDatumEntity> verticalDatum) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().addVerticalDatum(verticalDatum);
        return this;
    }
    
    public AbstractMonitoringFeature addVerticalDatum(VerticalDatumEntity verticalDatum) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().addVerticalDatum(verticalDatum);
        return this;
    }
    
    public boolean hasVerticalDatum() {
        return isSetContent() ? getContent().hasVerticalDatum() : false;
    }
}
