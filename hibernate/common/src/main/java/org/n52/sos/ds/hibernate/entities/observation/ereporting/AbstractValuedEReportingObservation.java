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
package org.n52.sos.ds.hibernate.entities.observation.ereporting;

import org.n52.sos.ds.hibernate.entities.observation.series.AbstractValuedSeriesObservation;

public abstract class AbstractValuedEReportingObservation<T>
        extends AbstractValuedSeriesObservation<T>
        implements ValuedEReportingObservation<T> {

    private static final long serialVersionUID = 996063222630981539L;
    private Integer validation;
    private Integer verification;

    @Override
    public EReportingSeries getEReportingSeries() {
        return hasEReportingSeries() ? (EReportingSeries) getSeries() : null;
    }

    @Override
    public void setEReportingSeries(EReportingSeries series) {
        setSeries(series);
    }

    @Override
    public boolean hasEReportingSeries() {
        return getSeries() instanceof EReportingSeries;
    }

    @Override
    public Integer getVerification() {
        return verification;
    }

    @Override
    public void setVerification(Integer verification) {
        this.verification = verification;
    }

    @Override
    public boolean isSetVerification() {
        return getVerification() != null;
    }

    @Override
    public Integer getValidation() {
        return validation;
    }

    @Override
    public void setValidation(Integer validation) {
        this.validation = validation;
    }

    @Override
    public boolean isSetValidation() {
        return getValidation() != null;
    }
}
