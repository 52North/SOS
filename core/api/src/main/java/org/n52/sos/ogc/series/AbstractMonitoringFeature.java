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
package org.n52.sos.ogc.series;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.iso.gmd.CiResponsibleParty;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.gml.VerticalDatum;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.w3c.xlink.Referenceable;

public abstract class AbstractMonitoringFeature extends AbstractSamplingFeature {

    private static final long serialVersionUID = -70039769462711980L;
    /* 0..* */
    private List<Referenceable<CiResponsibleParty>> relatedParty = new ArrayList<>();
    /* 0..* */
    private List<ReferenceType> monitoringType = new ArrayList<>();
    /* 0..* */
    private List<ReferenceType> descriptionReference = new ArrayList<>();
    /* 0..* */
    private List<Referenceable<VerticalDatum>> verticalDatum = new ArrayList<>();
    
    public AbstractMonitoringFeature(CodeWithAuthority featureIdentifier) {
        this(featureIdentifier, null);
    }
    
    public AbstractMonitoringFeature(CodeWithAuthority featureIdentifier, String gmlId) {
        super(featureIdentifier, gmlId);
    }

    /**
     * @return the relatedParty
     */
    public List<Referenceable<CiResponsibleParty>> getRelatedParty() {
        return relatedParty;
    }

    /**
     * @param relatedParty the relatedParty to set
     */
    public AbstractMonitoringFeature setRelatedParty(List<Referenceable<CiResponsibleParty>> relatedParty) {
        this.relatedParty.clear();
        if (relatedParty != null) {
            this.relatedParty.addAll(relatedParty);
        }
        return this;
    }
    
    /**
     * @param relatedParty the relatedParty to add
     */
    public AbstractMonitoringFeature addRelatedParty(List<Referenceable<CiResponsibleParty>> relatedParty) {
        if (relatedParty != null) {
            this.relatedParty.addAll(relatedParty);
        }
        return this;
    }
    
    /**
     * @param relatedParty the relatedParty to add
     */
    public AbstractMonitoringFeature addRelatedParty(Referenceable<CiResponsibleParty> relatedParty) {
        if (relatedParty != null) {
            this.relatedParty.add(relatedParty);
        }
        return this;
    }
    
    public boolean hasRelatedParty() {
        return getRelatedParty() != null && !getRelatedParty().isEmpty();
    }

    /**
     * @return the monitoringType
     */
    public List<ReferenceType> getMonitoringType() {
        return monitoringType;
    }

    /**
     * @param monitoringType the monitoringType to set
     */
    public AbstractMonitoringFeature setMonitoringType(List<ReferenceType> monitoringType) {
        this.monitoringType.clear();
        if (monitoringType != null) {
            this.monitoringType.addAll(monitoringType);
        }
        return this;
    }
    
    /**
     * @param monitoringType the monitoringType to add
     */
    public AbstractMonitoringFeature addMonitoringType(List<ReferenceType> monitoringType) {
        if (monitoringType != null) {
            this.monitoringType.addAll(monitoringType);
        }
        return this;
    }
    
    /**
     * @param monitoringType the monitoringType to add
     */
    public AbstractMonitoringFeature addMonitoringType(ReferenceType monitoringType) {
        if (monitoringType != null) {
            this.monitoringType.add(monitoringType);
        }
        return this;
    }
    
    public boolean hasMonitoringType() {
        return getMonitoringType() != null && !getMonitoringType().isEmpty();
    }

    /**
     * @return the descriptionReference
     */
    public List<ReferenceType> getDescriptionReference() {
        return descriptionReference;
    }

    /**
     * @param descriptionReference the descriptionReference to set
     */
    public AbstractMonitoringFeature setDescriptionReference(List<ReferenceType> descriptionReference) {
        this.descriptionReference.clear();
        if (descriptionReference != null) {
            this.descriptionReference.addAll(descriptionReference);
        }
        return this;
    }
    
    /**
     * @param descriptionReference the descriptionReference to add
     */
    public AbstractMonitoringFeature addDescriptionReference(List<ReferenceType> descriptionReference) {
        this.descriptionReference.clear();
        if (descriptionReference != null) {
            this.descriptionReference.addAll(descriptionReference);
        }
        return this;
    }
    
    /**
     * @param descriptionReference the descriptionReference to add
     */
    public AbstractMonitoringFeature addDescriptionReference(ReferenceType descriptionReference) {
        if (descriptionReference != null) {
            this.descriptionReference.add(descriptionReference);
        }
        return this;
    }
    
    public boolean hasDescriptionReference() {
        return getDescriptionReference() != null && !getDescriptionReference().isEmpty();
    }

    /**
     * @return the verticalDatum
     */
    public List<Referenceable<VerticalDatum>> getVerticalDatum() {
        return verticalDatum;
    }

    /**
     * @param verticalDatum the verticalDatum to set
     */
    public AbstractMonitoringFeature setVerticalDatum(List<Referenceable<VerticalDatum>> verticalDatum) {
        this.verticalDatum.clear();
        if (verticalDatum != null) {
            this.verticalDatum.addAll(verticalDatum);
        }
        return this;
    }
    
    /**
     * @param verticalDatum the verticalDatum to add
     */
    public AbstractMonitoringFeature addVerticalDatum(List<Referenceable<VerticalDatum>> verticalDatum) {
        if (verticalDatum != null) {
            this.verticalDatum.addAll(verticalDatum);
        }
        return this;
    }
    
    /**
     * @param verticalDatum the verticalDatum to add
     */
    public AbstractMonitoringFeature addVerticalDatum(Referenceable<VerticalDatum> verticalDatum) {
        if (verticalDatum != null) {
            this.verticalDatum.add(verticalDatum);
        }
        return this;
    }
    
    public boolean hasVerticalDatum() {
        return getVerticalDatum() != null && !getVerticalDatum().isEmpty();
    }

}
