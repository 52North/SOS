/*
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
package org.n52.sos.ogc.sos;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.time.Time;

import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class SosProcedureDescription<T extends AbstractFeature> extends AbstractFeature {
    private Set<SosOffering> offerings = Sets.newLinkedHashSet();

    private final T description;

    private Time validTime;

    public SosProcedureDescription(T description) {
        super(description.getIdentifier());
        this.description = description;
    }

    public T getProcedureDescription() {
        return this.description;
    }

    public Set<SosOffering> getOfferings() {
        return offerings;
    }

    public void setOfferings(Collection<SosOffering> offering) {
        this.offerings = Optional.ofNullable(offering).map(HashSet::new).orElseGet(HashSet::new);
    }

    public void addOffering(SosOffering offering) {
        this.offerings.add(offering);
    }

    public boolean isSetOfferings(){
        return this.offerings != null && !this.offerings.isEmpty();
    }

    public Time getValidTime() {
        return validTime;
    }

    public void setValidTime(Time validTime) {
        this.validTime = validTime;
    }

    public boolean isSetValidTime() {
        return this.validTime != null && !this.validTime.isEmpty();
    }
}
