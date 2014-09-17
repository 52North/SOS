/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.List;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.quality.SosQuality;
import org.n52.sos.ogc.om.values.Value;

/**
 * Class representing a single value observation value
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            value type
 */
public class SingleObservationValue<T> extends AbstractObservationValue<Value<T>> {
    /**
     * serial number
     */
    private static final long serialVersionUID = -8162038672393523937L;

    /**
     * Phenomenon time
     */
    private Time phenomenonTime;

    /**
     * Measurement value
     */
    private Value<T> value;

    /**
     * Measurment quality
     */
    private List<SosQuality> qualityList;

    /**
     * constructor
     */
    public SingleObservationValue() {
    }

    /**
     * constructor
     * 
     * @param value
     *            Measurement value
     */
    public SingleObservationValue(Value<T> value) {
        this.value = value;
    }

    /**
     * constructor
     * 
     * @param phenomenonTime
     *            Phenomenon time
     * @param value
     *            Measurement value
     * @param qualityList
     *            Measurment quality
     */
    public SingleObservationValue(Time phenomenonTime, Value<T> value, List<SosQuality> qualityList) {
        this.phenomenonTime = phenomenonTime;
        this.value = value;
        this.qualityList = qualityList;
    }

    /**
     * constructor
     * 
     * @param phenomenonTime
     *            Phenomenon time
     * @param value
     *            Measurement value
     */
    public SingleObservationValue(Time phenomenonTime, Value<T> value) {
        this.phenomenonTime = phenomenonTime;
        this.value = value;
    }

    @Override
    public Time getPhenomenonTime() {
        return phenomenonTime;
    }

    @Override
    public void setPhenomenonTime(Time phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    @Override
    public Value<T> getValue() {
        return value;
    }

    @Override
    public void setValue(Value<T> value) {
        this.value = value;
    }

    /**
     * Set measurement quality
     * 
     * @param qualityList
     *            Measurement quality to set
     */
    public void setQualityList(List<SosQuality> qualityList) {
        this.qualityList = qualityList;
    }

    /**
     * Get measurement quality
     * 
     * @return Measurement quality
     */
    public List<SosQuality> getQualityList() {
        return qualityList;
    }
}
