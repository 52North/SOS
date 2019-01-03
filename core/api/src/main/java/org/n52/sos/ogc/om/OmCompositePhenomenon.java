/**
 * Copyright (C) 2012-2019 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.om;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Iterators;


public class OmCompositePhenomenon extends AbstractPhenomenon implements Iterable<OmObservableProperty>{
    /**
     * serial number
     */
    private static final long serialVersionUID = 364153143602078222L;

    /** the components of the composite phenomenon */
    private List<OmObservableProperty> phenomenonComponents;

    /**
     * standard constructor
     *
     * @param compPhenId
     *            id of the composite phenomenon
     * @param compPhenDesc
     *            description of the composite phenomenon
     * @param phenomenonComponents
     *            components of the composite phenomenon
     */
    public OmCompositePhenomenon(String compPhenId, String compPhenDesc,
            List<OmObservableProperty> phenomenonComponents) {
        super(compPhenId, compPhenDesc);
        this.phenomenonComponents = phenomenonComponents;
    }

    public OmCompositePhenomenon(String identifier) {
        super(identifier);
        this.phenomenonComponents = new LinkedList<>();
    }

    public OmCompositePhenomenon(String identifier, String description) {
        super(identifier, description);
        this.phenomenonComponents = new LinkedList<>();
    }

    /**
     * Get observableProperties
     *
     * @return Returns the phenomenonComponents.
     */
    public List<OmObservableProperty> getPhenomenonComponents() {
        return phenomenonComponents;
    }

    /**
     * Set observableProperties
     *
     * @param phenomenonComponents
     *            The phenomenonComponents to set.
     */
    public void setPhenomenonComponents(List<OmObservableProperty> phenomenonComponents) {
        this.phenomenonComponents = phenomenonComponents;
    }

    public void addPhenomenonComponent(OmObservableProperty observableProperty) {
        if (this.phenomenonComponents == null) {
            this.phenomenonComponents = new LinkedList<>();
        }
        this.phenomenonComponents.add(observableProperty);
    }

    @Override
    public Iterator<OmObservableProperty> iterator() {
        if (getPhenomenonComponents() == null) {
            return Iterators.emptyIterator();
        } else {
            return getPhenomenonComponents().iterator();
        }
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public boolean isObservableProperty() {
        return false;
    }
}