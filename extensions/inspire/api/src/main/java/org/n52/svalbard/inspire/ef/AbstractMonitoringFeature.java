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
package org.n52.svalbard.inspire.ef;

import java.util.Set;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.xlink.SimpleAttrs;
import org.n52.svalbard.inspire.base.Identifier;

import com.google.common.collect.Sets;

public abstract class AbstractMonitoringFeature extends AbstractMonitoringObject {

    private static final long serialVersionUID = 8628478394394160938L;
    
    /**
     * 0..*
     */
    private Set<ReportToLegalAct> reportedTo = Sets.newHashSet();
   
    /**
     * 0..*
     */
    private Set<OmObservation> hasObservation = Sets.newHashSet();
    
    /**
     * 0..*
     */
    private Set<EnvironmentalMonitoringActivity> involvedIn = Sets.newHashSet();
    
    public AbstractMonitoringFeature(SimpleAttrs simpleAttrs) {
        super(simpleAttrs);
    }
    
    public AbstractMonitoringFeature(Identifier inspireId, ReferenceType mediaMonitored) {
        super(inspireId, mediaMonitored);
    }
    
    public AbstractMonitoringFeature(Identifier inspireId, Set<ReferenceType> mediaMonitored) {
        super(inspireId, mediaMonitored);
    }

    /**
     * @return the reportedTo
     */
    public Set<ReportToLegalAct> getReportedTo() {
        return reportedTo;
    }

    /**
     * @param reportedTo the reportedTo to set
     */
    public void setReportedTo(Set<ReportToLegalAct> reportedTo) {
        this.reportedTo.clear();
        this.reportedTo = reportedTo;
    }
    
    public boolean isSetReportedTo() {
        return CollectionHelper.isNotEmpty(getReportedTo());
    }

    /**
     * @return the hasObservation
     */
    public Set<OmObservation> getHasObservation() {
        return hasObservation;
    }

    /**
     * @param hasObservation the hasObservation to set
     */
    public void setHasObservation(Set<OmObservation> hasObservation) {
        this.hasObservation.clear();
        this.hasObservation = hasObservation;
    }
    
    /**
     * @param hasObservation the hasObservation to add
     */
    public void addHasObservation(OmObservation hasObservation) {
        this.hasObservation.add(hasObservation);
    }

    public boolean isSetHasObservation() {
        return CollectionHelper.isNotEmpty(getHasObservation());
    }
    
    /**
     * @return the involvedIn
     */
    public Set<EnvironmentalMonitoringActivity> getInvolvedIn() {
        return involvedIn;
    }

    /**
     * @param involvedIn the involvedIn to set
     */
    public void setInvolvedIn(Set<EnvironmentalMonitoringActivity> involvedIn) {
        this.involvedIn.clear();
        this.involvedIn = involvedIn;
    }

    public boolean isSetInvolvedIn() {
        return CollectionHelper.isNotEmpty(getInvolvedIn());
    }
    
}
