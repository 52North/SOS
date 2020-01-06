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
package org.n52.sos.ogc.sos;

import java.util.Set;

import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.swes.SwesExtension;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class RelatedOfferings implements SwesExtension<Set<OfferingContext>> {
    private String identifier;
    private String definition;
    private Set<OfferingContext> offeringRelations = Sets.newHashSet();

    
    @Override
    public String getNamespace() {
        return RelatedOfferingConstants.NS_RO;
    }

    @Override
    public SwesExtension<Set<OfferingContext>> setNamespace(String namespace) {
        return this;
    }

    @Override
    public boolean isSetNamespace() {
        return !Strings.isNullOrEmpty(getNamespace());
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public SwesExtension<Set<OfferingContext>> setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public boolean isSetIdentifier() {
        return !Strings.isNullOrEmpty(getIdentifier());
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    @Override
    public SwesExtension<Set<OfferingContext>> setDefinition(String definition) {
        this.definition = definition;
        return this;
    }

    @Override
    public boolean isSetDefinition() {
        return !Strings.isNullOrEmpty(getDefinition());
    }

    @Override
    public Set<OfferingContext> getValue() {
        return offeringRelations;
    }

    @Override
    public SwesExtension<Set<OfferingContext>> setValue(Set<OfferingContext> value) {
        this.offeringRelations.clear();
        this.offeringRelations.addAll(value);
        return this;
    }
    
    public SwesExtension<Set<OfferingContext>> addValue(String role, String offering) {
        this.offeringRelations.add(new OfferingContext(new ReferenceType(role), new ReferenceType(offering)));
        return this;
    }
    
    public SwesExtension<Set<OfferingContext>> addValue(ReferenceType role, ReferenceType offering) {
        this.offeringRelations.add(new OfferingContext(role, offering));
        return this;
    }
    
    public SwesExtension<Set<OfferingContext>> addValue(OfferingContext offeringContext) {
        this.offeringRelations.add(offeringContext);
        return this;
    }

}
