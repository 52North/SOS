/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.ereporting.EReportingDataEntity;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.aqd.ReportObligations;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.util.CollectionHelper;

public interface EReportingDaoHelper {

    default void addValidityAndVerificationRestrictions(Criteria c, AbstractObservationRequest request)
            throws OwsExceptionReport {
        if (request.isSetResponseFormat() && AqdConstants.NS_AQD.equals(request.getResponseFormat())) {
            ReportObligationType flow = ReportObligations.getFlow(request.getExtensions());
            if (ReportObligationType.E1A.equals(flow) || ReportObligationType.E1B.equals(flow)) {
                if (isSetValidityFlags()) {
                    c.add(Restrictions.in(EReportingDataEntity.VALIDATION, getValidityFlags()));
                }
                if (isSetVerificationFlags()) {
                    c.add(Restrictions.in(EReportingDataEntity.VERIFICATION, getVerificationFlags()));
                }
            }
        }
    }

    Set<Integer> getVerificationFlags();

    Set<Integer> getValidityFlags();

    default boolean isSetVerificationFlags() {
        return CollectionHelper.isNotEmpty(getVerificationFlags());
    }

    default boolean isSetValidityFlags() {
        return CollectionHelper.isNotEmpty(getValidityFlags());
    }

}
