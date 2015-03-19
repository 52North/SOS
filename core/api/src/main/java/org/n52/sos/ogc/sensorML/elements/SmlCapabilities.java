/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.sensorML.elements;

import java.util.List;

import org.n52.sos.ogc.swe.DataRecord;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Lists;

/**
 * SOS internal representation of SensorML capabilities
 * 
 * @since 4.0.0
 */
public class SmlCapabilities extends AbstractSmlDataComponentContainer<SmlCapabilities> {
    
    
    private List<SmlCapability> capabilities = Lists.newArrayList();

    /**
     * default constructor
     */
    public SmlCapabilities() {
       super();
    }
    
    /**
     * constructor
     * 
     * @param name
     *            Type
     */
    public SmlCapabilities(String name) {
        setName(name);
    }

    /**
     * constructor
     * 
     * @param name
     *            Type
     * @param dataRecord
     *            DataRecord
     */
    public SmlCapabilities(String name, DataRecord dataRecord) {
        super(dataRecord);
        setName(name);
    }

    /**
     * @return the capabilities
     */
    public List<SmlCapability> getCapabilities() {
        if (!hasCapabilities() && isSetAbstractDataComponents()) {
            List<SmlCapability> capabilities = Lists.newArrayList();
            for (SweAbstractDataComponent component : getAbstractDataComponents()) {
                SmlCapability smlCapability = new SmlCapability(component.getName().getValue());
                smlCapability.setAbstractDataComponent(component);
                capabilities.add(smlCapability);
            }
            return capabilities;
        }
        return capabilities;
    }

    /**
     * @param capabilities the capabilities to set
     */
    public void setCapabilities(List<SmlCapability> capabilities) {
        if (CollectionHelper.isNotEmpty(capabilities)) {
            this.capabilities = capabilities;
            for (SmlCapability smlCapability : capabilities) {
                addAbstractDataComponents(smlCapability.getAbstractDataComponent());
            }
        }
    }
    
    /**
     * @param capabilities the capabilities to add
     */
    public void addCapabilities(List<SmlCapability> capabilities) {
        this.capabilities.addAll(capabilities);
        for (SmlCapability smlCapability : capabilities) {
            addAbstractDataComponents(smlCapability.getAbstractDataComponent());
        }
    }
    
    /**
     * @param capability the capability to add
     */
    public void addCapability(SmlCapability capability) {
        this.capabilities.add(capability);
        addAbstractDataComponents(capability.getAbstractDataComponent());
    }
    
    public boolean isSetCapabilities() {
        return hasCapabilities() || isSetAbstractDataComponents();
    }
    
    private boolean hasCapabilities() {
        return CollectionHelper.isNotEmpty(capabilities); 
    }

}
