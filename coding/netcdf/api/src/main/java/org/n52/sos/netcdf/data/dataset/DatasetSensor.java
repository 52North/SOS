/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.netcdf.data.dataset;

import org.n52.shetland.ogc.sensorML.AbstractSensorML;

/**
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public abstract class DatasetSensor implements Comparable<DatasetSensor> {

    private AbstractSensorML description;

    public abstract String getSensorIdentifier();

    public void setSensorDescription(AbstractSensorML description) {
        this.description = description;
    }

    public AbstractSensorML getSensorDescritpion() {
        return description;
    }

    public boolean isSetSensorDescription() {
        return getSensorDescritpion() != null;
    }

    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof DatasetSensor) {
            DatasetSensor anotherAbstractAsset = (DatasetSensor) anObject;
            if (getSensorIdentifier().equals(anotherAbstractAsset.getSensorIdentifier())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int h = 0;
        int len = getSensorIdentifier().length();
        if (len > 0) {
            int off = 0;
            char[] val = getSensorIdentifier().toCharArray();

            for (int i = 0; i < len; i++) {
                h = 31 * h + val[off++];
            }
        }
        return h;
    }

    @Override
    public int compareTo(DatasetSensor o) {
        return getSensorIdentifier().compareTo(o.getSensorIdentifier());
    }

}
