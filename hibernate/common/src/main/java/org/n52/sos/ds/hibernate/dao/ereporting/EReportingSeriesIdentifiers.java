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
package org.n52.sos.ds.hibernate.dao.ereporting;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.dao.series.SeriesIdentifiers;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.series.Series;

public class EReportingSeriesIdentifiers extends SeriesIdentifiers {
    private EReportingSamplingPoint samplingPoint;

    /**
     * @return the featureOfInterest
     */
    public EReportingSamplingPoint getEReportingSamplingPoint() {
        return samplingPoint;
    }

    /**
     * @param featureOfInterest
     *            the featureOfInterest to set
     */
    public void setEReportingSamplingPoint(EReportingSamplingPoint samplingPoint) {
        this.samplingPoint = samplingPoint;
    }

    public boolean isSetEReportingSamplingPoint() {
        return getEReportingSamplingPoint() != null;
    }

    @Override
    public void addIdentifierRestrictionsToCritera(Criteria c) {
        super.addIdentifierRestrictionsToCritera(c);
        if (isSetEReportingSamplingPoint()) {
            addEReportingSamplingPointToCriteria(c, getEReportingSamplingPoint());
        }
    }

    @Override
    public void addValuesToSeries(Series series) {
        super.addValuesToSeries(series);
        if (isSetEReportingSamplingPoint() && series instanceof EReportingSeries) {
            ((EReportingSeries) series).setEReportingSamplingPoint(getEReportingSamplingPoint());
        }
    }

    /**
     * Add EReportingSamplingPoint restriction to Hibernate Criteria
     * 
     * @param c
     *            Hibernate Criteria to add restriction
     * @param samplingPoint
     *            EReportingSamplingPoint to add
     */
    private void addEReportingSamplingPointToCriteria(Criteria c, EReportingSamplingPoint samplingPoint) {
        c.add(Restrictions.eq(EReportingSeries.SAMPLING_POINT, samplingPoint));

    }
}
