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

import org.n52.sos.ogc.swe.SweAbstractDataComponent;

/**
 * SOS internal representation of SensorML IOs
 * 
 * @param <T>
 * 
 * @since 4.0.0
 */
public class SmlIo<T> {

    private String ioName;

    private SweAbstractDataComponent ioValue;

    /**
     * default constructor
     */
    public SmlIo() {
        super();
    }

    /**
     * constructor
     * 
     * @param ioValue
     *            The IO value
     */
    public SmlIo(final SweAbstractDataComponent ioValue) {
        super();
        this.ioValue = ioValue;
    }

    /**
     * @return the inputName
     */
    public String getIoName() {
        return ioName;
    }

    /**
     * @param inputName
     *            the inputName to set
     * @return This object
     */
    public SmlIo<T> setIoName(final String inputName) {
        this.ioName = inputName;
        return this;
    }

    /**
     * @return the input
     */
    public SweAbstractDataComponent getIoValue() {
        return ioValue;
    }

    /**
     * @param ioValue
     *            the input to set
     * @return This object
     */
    public SmlIo<T> setIoValue(final SweAbstractDataComponent ioValue) {
        this.ioValue = ioValue;
        return this;
    }

    public boolean isSetName() {
        return ioName != null && !ioName.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("SosSMLIo [ioName=%s, ioValue=%s]", ioName, ioValue);
    }

    public Boolean isSetValue() {
        return ioValue != null;
    }

}
