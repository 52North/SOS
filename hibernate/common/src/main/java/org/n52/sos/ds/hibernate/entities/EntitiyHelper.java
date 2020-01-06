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
package org.n52.sos.ds.hibernate.entities;

import org.n52.sos.ds.hibernate.entities.observation.ereporting.AbstractEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.AbstractValuedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.ContextualReferencedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.EReportingSeries;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.TemporalReferencedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.ContextualReferencedLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.ContextualReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.TemporalReferencedSeriesObservation;
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
        return HibernateHelper.isEntitySupported(AbstractEReportingObservation.class)
                || HibernateHelper.isEntitySupported(AbstractSeriesObservation.class);
    }

    public boolean isObservationInfoSupported() {
        return getObservationInfoEntityClass() != null;
    }

    public boolean isSeriesObservationInfoSupported() {
        if (isObservationInfoSupported()) {
            return HibernateHelper.isEntitySupported(ContextualReferencedEReportingObservation.class)
                    || HibernateHelper.isEntitySupported(ContextualReferencedSeriesObservation.class);
        }
        return false;
    }

    public boolean isSeriesObservationTimeSupported() {
        if (isObservationTimeSupported()) {
            return HibernateHelper.isEntitySupported(TemporalReferencedEReportingObservation.class)
                    || HibernateHelper.isEntitySupported(TemporalReferencedSeriesObservation.class);
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
        if (HibernateHelper.isEntitySupported(AbstractEReportingObservation.class)) {
            return AbstractEReportingObservation.class;
        } else if (HibernateHelper.isEntitySupported(AbstractSeriesObservation.class)) {
            return AbstractSeriesObservation.class;
        } else if (HibernateHelper.isEntitySupported(AbstractLegacyObservation.class)) {
            return AbstractLegacyObservation.class;
        }
        return null;
    }

    public Class<?> getObservationInfoEntityClass() {
        if (HibernateHelper.isEntitySupported(ContextualReferencedEReportingObservation.class)) {
            return ContextualReferencedEReportingObservation.class;
        } else if (HibernateHelper.isEntitySupported(ContextualReferencedSeriesObservation.class)) {
            return ContextualReferencedSeriesObservation.class;
        } else if (HibernateHelper.isEntitySupported(ContextualReferencedLegacyObservation.class)) {
            return ContextualReferencedLegacyObservation.class;
        }
        return null;
    }

    public Class<?> getObservationTimeEntityClass() {
        if (HibernateHelper.isEntitySupported(TemporalReferencedEReportingObservation.class)) {
            return TemporalReferencedEReportingObservation.class;
        } else if (HibernateHelper.isEntitySupported(TemporalReferencedSeriesObservation.class)) {
            return TemporalReferencedSeriesObservation.class;
        }
        return null;
    }

    public Class<?> getValueEntityClass() {
        if (HibernateHelper.isEntitySupported(AbstractValuedEReportingObservation.class)) {
            return AbstractValuedEReportingObservation.class;
        } else if (HibernateHelper.isEntitySupported(AbstractValuedSeriesObservation.class)) {
            return AbstractValuedSeriesObservation.class;
        }
        return null;
    }

    public Class<?> getValueTimeEntityClass() {
        if (HibernateHelper.isEntitySupported(TemporalReferencedEReportingObservation.class)) {
            return TemporalReferencedEReportingObservation.class;
        } else if (HibernateHelper.isEntitySupported(TemporalReferencedSeriesObservation.class)) {
            return TemporalReferencedSeriesObservation.class;
        }
        return null;
    }

}
