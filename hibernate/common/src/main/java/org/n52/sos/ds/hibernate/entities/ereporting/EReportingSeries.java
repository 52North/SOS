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
package org.n52.sos.ds.hibernate.entities.ereporting;

import org.n52.sos.ds.hibernate.entities.ereporting.HiberanteEReportingRelations.HasEReportingSamplingPoint;
import org.n52.sos.ds.hibernate.entities.series.Series;

public class EReportingSeries extends Series implements HasEReportingSamplingPoint {

    private static final long serialVersionUID = -2717429959149898898L;
    
    private EReportingSamplingPoint samplingPoint;

    @Override
    public EReportingSamplingPoint getSamplingPoint() {
        return samplingPoint;
    }

    @Override
    public EReportingSeries setSamplingPoint(EReportingSamplingPoint samplingPoint) {
        this.samplingPoint = samplingPoint;
        return this;
    }

    @Override
    public boolean hasSamplingPoint() {
        return getSamplingPoint() != null;
    }

}
