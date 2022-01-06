/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosResultEncoding;
import org.n52.shetland.ogc.sos.SosResultStructure;
import org.n52.shetland.ogc.swe.SweDataRecord;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateUnproxy;
import org.n52.sos.ds.hibernate.util.ResultHandlingHelper;
import org.n52.sos.exception.sos.concrete.NoSweCommonEncodingForOfferingObservablePropertyCombination;
import org.n52.svalbard.util.SweHelper;

public interface AbstractResultHandler extends HibernateUnproxy {

    SweHelper getSweHelper();

    ResultHandlingHelper getResultHandlingHelper();

    DaoFactory getDaoFactory();

    default SosResultEncoding createSosResultEncoding() {
        return new SosResultEncoding(getSweHelper().createDefaultTextEncoding());
    }

    default SosResultStructure generateSosResultStructure(String observedProperty, String offering,
            Set<String> featureIdentifier, Session session) throws OwsExceptionReport {
        List<DatasetEntity> datasets = getDaoFactory().getSeriesDAO()
                .getSeries(null, observedProperty, offering, null, session);
        if (datasets != null && !datasets.isEmpty()) {
            boolean procedure = checkForProcedures(datasets);
            boolean feature = checkForFeatures(datasets);
            DatasetEntity dataset = datasets.get(0);
            if (dataset.getFirstObservation() != null) {
                SweDataRecord createRecord = getResultHandlingHelper().createDataRecordForResultTemplate(
                        unproxy(dataset.getFirstObservation(), session), procedure, feature);
                return new SosResultStructure(createRecord);
            }
        }
        throw new NoSweCommonEncodingForOfferingObservablePropertyCombination(offering, observedProperty);
    }

    default boolean checkForProcedures(List<DatasetEntity> series) {
        return series.stream()
                .map(d -> d.getProcedure()
                        .getId())
                .collect(Collectors.toSet())
                .size() > 1;
    }

    default boolean checkForFeatures(List<DatasetEntity> series) {
        return series.stream()
                .map(d -> d.getFeature()
                        .getId())
                .collect(Collectors.toSet())
                .size() > 1;
    }
}
