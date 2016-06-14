/**
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
package org.n52.sos.ogc.om.values;


import java.io.Serializable;

import org.n52.sos.ogc.UoM;
import org.n52.sos.ogc.om.values.visitor.ValueVisitor;
import org.n52.sos.ogc.om.values.visitor.VoidValueVisitor;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Interface for measurement value representation for observation
 *
 * @since 4.0.0
 *
 * @param <T>
 *            specific value type
 */
public interface Value<T> extends Serializable {

    /**
     * Set the measurment value
     *
     * @param value
     *            Value to set
     */
    Value<T> setValue(T value);

    /**
     * Get the measurement value
     *
     * @return Measurement value
     */
    T getValue();

    /**
     * Set the unit of measure
     *
     * @param unit
     *            Unit of measure
     */
    void setUnit(String unit);

    /**
     * Get the unit of measure object
     *
     * @return Unit of measure
     */
    UoM getUnitObject();
    
    /**
     * Set the unit of measure object
     *
     * @param unit
     *            Unit of measure
     */
    void setUnit(UoM unit);

    /**
     * Get the unit of measure
     *
     * @return Unit of measure
     */
    String getUnit();

    /**
     * Check whether the value is set
     *
     * @return <code>true</code>, if value is set
     */
    boolean isSetValue();

    /**
     * Check whether the unit of measure is set
     *
     * @return <code>true</code>, if unit of measure is set
     */
    boolean isSetUnit();


    <X> X accept(ValueVisitor<X> visitor) throws OwsExceptionReport;

    void accept(VoidValueVisitor visitor) throws OwsExceptionReport;
}
