/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasWriteableObservationContext;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSamplingPoint;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.HiberanteEReportingRelations.HasEReportingSamplingPoint;

public class EReportingObservationContext extends ObservationContext {
    private EReportingSamplingPoint samplingPoint;

    /**
     * @return the featureOfInterest
     */
    public EReportingSamplingPoint getSamplingPoint() {
        return samplingPoint;
    }

    /**
     * @param samplingPoint
     *            the samplingPoint to set
     */
    public void setSamplingPoint(EReportingSamplingPoint samplingPoint) {
        this.samplingPoint = samplingPoint;
    }

    public boolean isSetSamplingPoint() {
        return getSamplingPoint() != null;
    }

    @Override
    public void addIdentifierRestrictionsToCritera(Criteria criteria) {
        super.addIdentifierRestrictionsToCritera(criteria);
        if (isSetSamplingPoint()) {
            criteria.add(Restrictions.eq(HasEReportingSamplingPoint.SAMPLING_POINT, getSamplingPoint()));
        }
    }

    @Override
    public void addValuesToSeries(HasWriteableObservationContext contextual) {
        super.addValuesToSeries(contextual);
        if (isSetSamplingPoint() && contextual instanceof HasEReportingSamplingPoint) {
            ((HasEReportingSamplingPoint) contextual).setSamplingPoint(getSamplingPoint());
        }
    }
}
