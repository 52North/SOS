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

import java.io.Serializable;

import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.series.wml.DefaultPointMetadata;
import org.n52.sos.ogc.series.wml.Metadata;

/**
 * Interface for observation values.
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            observation value type
 */
public interface ObservationValue<T extends Value<?>> extends Serializable {

    /**
     * Get phenomenon or sampling time of the observation
     * 
     * @return Phenomenon or sampling time of the observation
     */
    Time getPhenomenonTime();

    /**
     * Set phenomenon or sampling time of the observation
     * 
     * @param phenomenonTime
     *            Phenomenon or sampling time of the observation
     */
    void setPhenomenonTime(Time phenomenonTime);

    /**
     * Get observation value
     * 
     * @return Observation value
     */
    T getValue();

    /**
     * Set observation value
     * 
     * @param value
     *            Observation value
     */
    void setValue(T value);

    boolean isSetValue();

    boolean isSetDefaultPointMetadata();

    void setDefaultPointMetadata(DefaultPointMetadata defaultPointMetadata);
    
    DefaultPointMetadata getDefaultPointMetadata();

    boolean isSetMetadata();

    void setMetadata(Metadata metadata);
    
    Metadata getMetadata();

}