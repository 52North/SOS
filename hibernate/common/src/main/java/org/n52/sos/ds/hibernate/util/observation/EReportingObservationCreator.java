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
package org.n52.sos.ds.hibernate.util.observation;

import java.util.Collections;
import java.util.Set;

import org.hibernate.Session;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingBlobDataEntity;
import org.n52.series.db.beans.ereporting.EReportingBooleanDataEntity;
import org.n52.series.db.beans.ereporting.EReportingCategoryDataEntity;
import org.n52.series.db.beans.ereporting.EReportingCountDataEntity;
import org.n52.series.db.beans.ereporting.EReportingDataArrayDataEntity;
import org.n52.series.db.beans.ereporting.EReportingDataEntity;
import org.n52.series.db.beans.ereporting.EReportingDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingGeometryDataEntity;
import org.n52.series.db.beans.ereporting.EReportingQualityEntity;
import org.n52.series.db.beans.ereporting.EReportingReferencedDataEntity;
import org.n52.series.db.beans.ereporting.EReportingTextDataEntity;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.svalbard.util.SweHelper;

public class EReportingObservationCreator implements AdditionalObservationCreator {

    private static final Set<AdditionalObservationCreatorKey> KEYS
            = AdditionalObservationCreatorRepository
            .encoderKeysForElements(AqdConstants.NS_AQD,
                                    EReportingDataEntity.class,
                                    EReportingBlobDataEntity.class,
                                    EReportingBooleanDataEntity.class,
                                    EReportingCategoryDataEntity.class,
                                    EReportingCountDataEntity.class,
                                    EReportingGeometryDataEntity.class,
                                    EReportingQualityEntity.class,
                                    EReportingDataArrayDataEntity.class,
                                    EReportingTextDataEntity.class,
                                    EReportingReferencedDataEntity.class,
                                    EReportingDatasetEntity.class);


    private final EReportingObservationHelper helper = new EReportingObservationHelper();
    private SweHelper sweHelper;

    public void setSweHelper(SweHelper sweHelper) {
        this.sweHelper = sweHelper;
    }

    @Override
    public Set<AdditionalObservationCreatorKey> getKeys() {
        return Collections.unmodifiableSet(KEYS);
    }

    @Override
    public OmObservation create(OmObservation omObservation, DataEntity<?> observation) throws CodedException {
        if (observation instanceof EReportingDataEntity) {
            EReportingDataEntity<?> eReportingObservation = (EReportingDataEntity<?>) observation;
            create(omObservation, eReportingObservation.getDataset());
            add(omObservation, observation);
            omObservation.setValue(new EReportingHelper(sweHelper).createSweDataArrayValue(omObservation, eReportingObservation));
            omObservation.getObservationConstellation().setObservationType(OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION);
        }
        return omObservation;
    }

    @Override
    public OmObservation create(OmObservation omObservation, DatasetEntity series) {
        EReportingDatasetEntity ereportingSeries = (EReportingDatasetEntity) series;
        for (NamedValue<?> namedValue : helper.createOmParameterForEReporting(ereportingSeries)) {
            omObservation.addParameter(namedValue);
        }
        return omObservation;
    }

    @Override
    public OmObservation create(OmObservation omObservation, DatasetEntity series, Session session) throws CodedException {
        return create(omObservation, series);
    }

    @Override
    public OmObservation create(OmObservation omObservation, DataEntity<?> observation, Session session) throws CodedException {
        return create(omObservation, observation);
    }

    @Override
    public OmObservation add(OmObservation omObservation, DataEntity<?> observation) {
        if (observation instanceof EReportingDataEntity) {
            EReportingDataEntity<?> eReportingObservation = (EReportingDataEntity<?>) observation;
            omObservation.setAdditionalMergeIndicator(eReportingObservation.getPrimaryObservation());
        }
        return omObservation;
    }

    @Override
    public OmObservation add(OmObservation omObservation, DataEntity<?> observation, Session session) {
        return add(omObservation, observation);
    }

}
