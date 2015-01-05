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
package org.n52.sos.ds.hibernate.entities;

import java.io.Serializable;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservationType;
import org.n52.sos.util.StringHelper;

/**
 * @since 4.0.0
 * 
 */
public class ObservationType implements Serializable {

    private static final long serialVersionUID = -8338409455186689274L;

    public static final String ID = "observationTypeId";

    public static final String OBSERVATION_TYPE = HasObservationType.OBSERVATION_TYPE;

    private long observationTypeId;

    private String observationType;

    public ObservationType() {
    }

    public ObservationType(String observationType) {
        this.observationType = observationType;
    }

    public long getObservationTypeId() {
        return this.observationTypeId;
    }

    public void setObservationTypeId(long observationTypeId) {
        this.observationTypeId = observationTypeId;
    }

    public String getObservationType() {
        return this.observationType;
    }

    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    public boolean isSetObservationType() {
        return StringHelper.isNotEmpty(getObservationType());
    }

}
