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
package org.n52.sos.ds.hibernate.entities;

import org.n52.sos.ds.hibernate.entities.ereporting.EReportingObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingObservationInfo;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingObservationTime;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.ereporting.values.EReportingValue;
import org.n52.sos.ds.hibernate.entities.ereporting.values.EReportingValueTime;
import org.n52.sos.ds.hibernate.entities.series.Series;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationInfo;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationTime;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValue;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValueTime;
import org.n52.sos.ds.hibernate.util.HibernateHelper;

public class EntitiyHelper {

    /**
     * instance
     */
    private static EntitiyHelper instance;

    /**
     * Get the EntitiyHelper instance
     *
     * @return Returns the instance of the EntitiyHelper.
     */
    public static synchronized EntitiyHelper getInstance() {
        if (instance == null) {
            instance = new EntitiyHelper();
        }
        return instance;
    }

    public boolean isSeriesSupported() {
        return getSeriesEntityClass() != null;
    }

    public boolean isSeriesObservationSupported() {
        return HibernateHelper.isEntitySupported(EReportingObservation.class)
                || HibernateHelper.isEntitySupported(SeriesObservation.class);
    }

    public boolean isObservationInfoSupported() {
        return getObservationInfoEntityClass() != null;
    }

    public boolean isSeriesObservationInfoSupported() {
        if (isObservationInfoSupported()) {
            return HibernateHelper.isEntitySupported(EReportingObservationInfo.class)
                    || HibernateHelper.isEntitySupported(SeriesObservationInfo.class);
        }
        return false;
    }

    public boolean isSeriesObservationTimeSupported() {
        if (isObservationTimeSupported()) {
            return HibernateHelper.isEntitySupported(EReportingObservationTime.class)
                    || HibernateHelper.isEntitySupported(SeriesObservationTime.class);
        }
        return false;
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
        if (HibernateHelper.isEntitySupported(EReportingSeries.class)) {
            return EReportingSeries.class;
        } else if (HibernateHelper.isEntitySupported(Series.class)) {
            return Series.class;
        }
        return null;
    }

    public Class<?> getObservationEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingObservation.class)) {
            return EReportingObservation.class;
        } else if (HibernateHelper.isEntitySupported(SeriesObservation.class)) {
            return SeriesObservation.class;
        } else if (HibernateHelper.isEntitySupported(Observation.class)) {
            return Observation.class;
        }
        return null;
    }

    public Class<?> getObservationInfoEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingObservationInfo.class)) {
            return EReportingObservationInfo.class;
        } else if (HibernateHelper.isEntitySupported(SeriesObservationInfo.class)) {
            return SeriesObservationInfo.class;
        } else if (HibernateHelper.isEntitySupported(ObservationInfo.class)) {
            return ObservationInfo.class;
        }
        return null;
    }

    public Class<?> getObservationTimeEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingObservationTime.class)) {
            return EReportingObservationTime.class;
        } else if (HibernateHelper.isEntitySupported(SeriesObservationTime.class)) {
            return SeriesObservationTime.class;
        }
        return null;
    }

    public Class<?> getValueEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingValue.class)) {
            return EReportingValue.class;
        } else if (HibernateHelper.isEntitySupported(SeriesValue.class)) {
            return SeriesValue.class;
        }
        return null;
    }

    public Class<?> getValueTimeEntityClass() {
        if (HibernateHelper.isEntitySupported(EReportingValueTime.class)) {
            return EReportingValueTime.class;
        } else if (HibernateHelper.isEntitySupported(SeriesValueTime.class)) {
            return SeriesValueTime.class;
        }
        return null;
    }

}
