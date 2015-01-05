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

import org.n52.sos.ds.hibernate.entities.ereporting.HiberanteEReportingRelations.*;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationTime;
import org.n52.sos.util.StringHelper;

public class EReportingObservationTime extends SeriesObservationTime implements EReportingValuesTime {

    private static final long serialVersionUID = -5055301771550925701L;
    
    private Integer validation = EReportingValues.DEFAULT_VALIDATION;

    private Integer verification = EReportingValues.DEFAULT_VERIFICATION;

    private String primaryObservation = EReportingValues.DEFAULT_PRIMARY_OBSERVATION;

    public EReportingSeries getEReportingSeries() {
        if (hasEReportingSeries()) {
            return (EReportingSeries) getSeries();
        }
        return null;
    }

    @Override
    public HasEReportingSeries setEReportingSeries(EReportingSeries series) {
        setSeries(series);
        return this;
    }

    @Override
    public boolean hasEReportingSeries() {
        return getSeries() != null && getSeries() instanceof EReportingSeries;
    }
    
    @Override
    public Integer getVerification() {
        return verification;
    }

    @Override
    public EReportingObservationTime setVerification(Integer verification) {
        this.verification = verification;
        return this;
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
    public EReportingObservationTime setValidation(Integer validation) {
        this.validation = validation;
        return this;
    }

    @Override
    public boolean isSetValidation() {
        return getValidation() != null;
    }

    @Override
    public String getPrimaryObservation() {
        return primaryObservation;
    }

    @Override
    public EReportingObservationTime setPrimaryObservation(String primaryObservation) {
        this.primaryObservation = primaryObservation;
        return this;
    }

    @Override
    public boolean isSetPrimaryObservation() {
        return StringHelper.isNotEmpty(getPrimaryObservation());
    }
    
}
