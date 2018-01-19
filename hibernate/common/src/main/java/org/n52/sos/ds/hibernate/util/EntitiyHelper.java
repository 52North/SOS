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
package org.n52.sos.ds.hibernate.util;

import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingDataEntity;
import org.n52.series.db.beans.ereporting.EReportingDatasetEntity;

public class EntitiyHelper {

    /**
     * Get the EntitiyHelper instance
     *
     * @return Returns the instance of the EntitiyHelper.
     */
    @Deprecated
    public static synchronized EntitiyHelper getInstance() {
        return new EntitiyHelper();
    }

    public boolean isSeriesSupported() {
        return getSeriesEntityClass() != null;
    }

    public boolean isSeriesObservationSupported() {
        return HibernateHelper.isEntitySupported(EReportingDataEntity.class)
                || HibernateHelper.isEntitySupported(DataEntity.class);
    }

    public boolean isObservationInfoSupported() {
        return getObservationInfoEntityClass() != null;
    }

    public boolean isSeriesObservationInfoSupported() {
        return isObservationInfoSupported() &&
               (HibernateHelper.isEntitySupported(EReportingDataEntity.class) ||
                HibernateHelper.isEntitySupported(DataEntity.class));
    }

    public boolean isSeriesObservationTimeSupported() {
        return isObservationTimeSupported() &&
               (HibernateHelper.isEntitySupported(EReportingDataEntity.class) ||
                HibernateHelper.isEntitySupported(DataEntity.class));
    }

    public boolean isObservationTimeSupported() {
        return getObservationTimeEntityClass() != null;
    }

    public boolean isValueSupported() {
        return getValueEntityClass() != null;
    }

    public boolean isValueTimeSupported() {
        return getValueTimeEntityClass() != null;
    }

    public Class<?> getSeriesEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingDatasetEntity.class)) {
            return EReportingDatasetEntity.class;
        } else if (HibernateHelper.isEntitySupported(DatasetEntity.class)) {
            return DatasetEntity.class;
        }
        return null;
    }

    public Class<?> getObservationEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingDataEntity.class)) {
            return EReportingDataEntity.class;
        } else if (HibernateHelper.isEntitySupported(DataEntity.class)) {
            return DataEntity.class;
        }
        return null;
    }

    public Class<?> getObservationInfoEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingDataEntity.class)) {
            return EReportingDataEntity.class;
        } else if (HibernateHelper.isEntitySupported(DataEntity.class)) {
            return DataEntity.class;
        }
        return null;
    }

    public Class<?> getObservationTimeEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingDataEntity.class)) {
            return EReportingDataEntity.class;
        } else if (HibernateHelper.isEntitySupported(DataEntity.class)) {
            return DataEntity.class;
        }
        return null;
    }

    public Class<?> getValueEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingDataEntity.class)) {
            return EReportingDataEntity.class;
        } else if (HibernateHelper.isEntitySupported(DataEntity.class)) {
            return DataEntity.class;
        }
        return null;
    }

    public Class<?> getValueTimeEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingDataEntity.class)) {
            return EReportingDataEntity.class;
        } else if (HibernateHelper.isEntitySupported(DataEntity.class)) {
            return DataEntity.class;
        }
        return null;
    }

}
