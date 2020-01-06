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

import java.util.ArrayList;
import java.util.List;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.xlink.Referenceable;

import com.google.common.collect.Lists;

/**
 * Internal representation of the OGC GML AbstractCRS.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public abstract class AbstractCRS extends IdentifiedObject {

    private static final long serialVersionUID = 2034560874264953187L;
    /* 0..* */
    private List<Referenceable<DomainOfValidity>> domainOfValidity = new ArrayList<>();
    /* 1..* */
    private List<String> scope = new ArrayList<>();
    
    public AbstractCRS(CodeWithAuthority identifier, String scope) {
        this(identifier, Lists.newArrayList(scope));
    }
    
    public AbstractCRS(CodeWithAuthority identifier, List<String> scope) {
        super(identifier);
        setScope(scope);
    }
    
    public List<Referenceable<DomainOfValidity>> getDomainOfValidity() {
        return domainOfValidity;
    }

    public AbstractCRS setDomainOfValidity(List<Referenceable<DomainOfValidity>> domainOfValidity) {
        this.domainOfValidity.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(domainOfValidity)) {
            this.domainOfValidity.addAll(domainOfValidity);
        }
        return this;
    }
    
    public AbstractCRS addDomainOfValidity(List<Referenceable<DomainOfValidity>> domainOfValidity) {
        this.domainOfValidity.addAll(domainOfValidity);
        return this;
    }
    
    public AbstractCRS addDomainOfValidity(Referenceable<DomainOfValidity> domainOfValidity) {
        this.domainOfValidity.add(domainOfValidity);
        return this;
    }
    
    public boolean hasDomainOfValidity() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getDomainOfValidity());
    }
    
    public List<String> getScope() {
        return scope;
    }

    public AbstractCRS setScope(List<String> scope) {
        this.scope.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(scope)) {
            this.scope.addAll(scope);
        }
        return this;
    }
    
    public AbstractCRS addScope(List<String> scope) {
        this.scope.addAll(scope);
        return this;
    }
    
    public AbstractCRS addScope(String scope) {
        this.scope.add(scope);
        return this;
    }
    
    public boolean hasScope() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getScope());
    }
    
}
