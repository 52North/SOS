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
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.xlink.SimpleAttrs;
import org.n52.svalbard.inspire.base.Identifier;

import com.google.common.collect.Sets;

public class EnvironmentalMonitoringProgramme extends AbstractMonitoringObject {

    private static final long serialVersionUID = -1745187728122044345L;
    
    /**
     * 0..1
     */
    private Set<EnvironmentalMonitoringActivity> triggers = Sets.newHashSet();
    
    public EnvironmentalMonitoringProgramme(SimpleAttrs simpleAttrs) {
        super(simpleAttrs);
    }
    
    public EnvironmentalMonitoringProgramme(Identifier inspireId, ReferenceType mediaMonitored) {
        super(inspireId, mediaMonitored);
    }
    
    public EnvironmentalMonitoringProgramme(Identifier inspireId, Set<ReferenceType> mediaMonitored) {
        super(inspireId, mediaMonitored);
    }

    /**
     * @return the triggers
     */
    public Set<EnvironmentalMonitoringActivity> getTriggers() {
        return triggers;
    }

    /**
     * @param triggers the triggers to set
     */
    public void setTriggers(Set<EnvironmentalMonitoringActivity> triggers) {
        this.triggers.clear();
        this.triggers = triggers;
    }
    
    public boolean isSetTriggers() {
        return CollectionHelper.isNotEmpty(getTriggers());
    }
    
}
