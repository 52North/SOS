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
package org.n52.sos.ds.hibernate.dao.observation.ereporting;

import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingBlobDataEntity;
import org.n52.series.db.beans.ereporting.EReportingBooleanDataEntity;
import org.n52.series.db.beans.ereporting.EReportingCategoryDataEntity;
import org.n52.series.db.beans.ereporting.EReportingComplexDataEntity;
import org.n52.series.db.beans.ereporting.EReportingCountDataEntity;
import org.n52.series.db.beans.ereporting.EReportingDataArrayDataEntity;
import org.n52.series.db.beans.ereporting.EReportingDataEntity;
import org.n52.series.db.beans.ereporting.EReportingDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingGeometryDataEntity;
import org.n52.series.db.beans.ereporting.EReportingProfileDataEntity;
import org.n52.series.db.beans.ereporting.EReportingQuantityDataEntity;
import org.n52.series.db.beans.ereporting.EReportingReferencedDataEntity;
import org.n52.series.db.beans.ereporting.EReportingTextDataEntity;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class EReportingObservationFactory extends ObservationFactory {
    protected EReportingObservationFactory() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends EReportingDataEntity> observationClass() {
        return EReportingDataEntity.class;
    }

    @Override
    public Class<? extends EReportingDataEntity> contextualReferencedClass() {
        return EReportingDataEntity.class;
    }

    @Override
    public Class<? extends EReportingDataEntity> temporalReferencedClass() {
        return EReportingDataEntity.class;
    }

    @Override
    public Class<? extends EReportingBlobDataEntity> blobClass() {
        return EReportingBlobDataEntity.class;
    }

    @Override
    public Class<? extends EReportingBooleanDataEntity> truthClass() {
        return EReportingBooleanDataEntity.class;
    }

    @Override
    public Class<? extends EReportingCategoryDataEntity> categoryClass() {
        return EReportingCategoryDataEntity.class;
    }

    @Override
    public Class<? extends EReportingCountDataEntity> countClass() {
        return EReportingCountDataEntity.class;
    }

    @Override
    public Class<? extends EReportingGeometryDataEntity> geometryClass() {
        return EReportingGeometryDataEntity.class;
    }

    @Override
    public Class<? extends EReportingQuantityDataEntity> numericClass() {
        return EReportingQuantityDataEntity.class;
    }

    @Override
    public Class<? extends EReportingDataArrayDataEntity> sweDataArrayClass() {
        return EReportingDataArrayDataEntity.class;
    }

    @Override
    public Class<? extends EReportingTextDataEntity> textClass() {
        return EReportingTextDataEntity.class;
    }

    @Override
    public Class<? extends EReportingProfileDataEntity> profileClass() {
        return EReportingProfileDataEntity.class;
    }

    @Override
    public Class<? extends EReportingComplexDataEntity> complexClass() {
        return EReportingComplexDataEntity.class;
    }

    @Override
    public Class<? extends EReportingReferencedDataEntity> referenceClass() {
        return EReportingReferencedDataEntity.class;
    }

    public DatasetEntity series() {
        return new EReportingDatasetEntity();
    }

    public Class<? extends DatasetEntity> seriesClass() {
        return EReportingDatasetEntity.class;
    }

    public static EReportingObservationFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final EReportingObservationFactory INSTANCE
                = new EReportingObservationFactory();

        private Holder() {
        }
    }

}
