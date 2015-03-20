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
 * SOS internal representation of SensorML characteristics
 * 
 * @since 4.0.0
 */
public class SmlCharacteristics extends AbstractSmlDataComponentContainer<SmlCharacteristics> {
    
    private List<SmlCharacteristic> characteristics = Lists.newArrayList();

    /**
     * default constructor
     */
    public SmlCharacteristics() {
        super();
    }

    /**
     * constructor
     * 
     * @param dataRecord
     *            dataRecord
     */
    public SmlCharacteristics(DataRecord dataRecord) {
        super(dataRecord);
    }

    
    /**
     * @return the characteristics
     */
    public List<SmlCharacteristic> getCharacteristic() {
        if (!hasCharacteristics() && isSetAbstractDataComponents()) {
            List<SmlCharacteristic> characteristics = Lists.newArrayList();
            for (SweAbstractDataComponent component : getAbstractDataComponents()) {
                SmlCharacteristic smlCharacteristic = new SmlCharacteristic(component.getName().getValue());
                smlCharacteristic.setAbstractDataComponent(component);
                characteristics.add(smlCharacteristic);
            }
            return characteristics;
        }
        return characteristics;
    }

    /**
     * @param characteristics the characteristics to set
     */
    public void setCharacteristic(List<SmlCharacteristic> characteristics) {
        if (CollectionHelper.isNotEmpty(characteristics)) {
            this.characteristics = characteristics;
            for (SmlCharacteristic smlCharacteristic : characteristics) {
                addAbstractDataComponents(smlCharacteristic.getAbstractDataComponent());
            }
        }
    }
    
    /**
     * @param characteristics the characteristics to add
     */
    public void addCharacteristic(List<SmlCharacteristic> characteristics) {
        this.characteristics.addAll(characteristics);
        for (SmlCharacteristic smlCharacteristic : characteristics) {
            addAbstractDataComponents(smlCharacteristic.getAbstractDataComponent());
        }
    }
    
    /**
     * @param characteristic the characteristic to add
     */
    public void addCharacteristic(SmlCharacteristic characteristic) {
        this.characteristics.add(characteristic);
        addAbstractDataComponents(characteristic.getAbstractDataComponent());
    }
    
    public boolean isSetCharacteristics() {
        return hasCharacteristics() || isSetAbstractDataComponents();
    }
    
    private boolean hasCharacteristics() {
        return CollectionHelper.isNotEmpty(characteristics); 
    }

}
