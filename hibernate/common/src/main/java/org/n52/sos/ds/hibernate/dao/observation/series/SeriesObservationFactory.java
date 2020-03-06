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
package org.n52.sos.ds.hibernate.dao.observation.series;

import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.entities.observation.full.BlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.BooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.CountObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.GeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.NumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ProfileObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ReferenceObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.SweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.TextObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.ContextualReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.TemporalReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesBlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesBooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesCategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesCountObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesGeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesNumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesProfileObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesReferenceObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesSweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.full.SeriesTextObservation;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class SeriesObservationFactory extends ObservationFactory {
    protected SeriesObservationFactory() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends SeriesObservation> observationClass() {
        return AbstractSeriesObservation.class;
    }

    @Override
    public Class<? extends TemporalReferencedSeriesObservation> temporalReferencedClass() {
        return TemporalReferencedSeriesObservation.class;
    }

    @Override
    public Class<? extends ContextualReferencedSeriesObservation> contextualReferencedClass() {
        return ContextualReferencedSeriesObservation.class;
    }

    @Override
    public Class<? extends BlobObservation> blobClass() {
        return SeriesBlobObservation.class;
    }

    @Override
    public Class<? extends BooleanObservation> truthClass() {
        return SeriesBooleanObservation.class;
    }

    @Override
    public Class<? extends CategoryObservation> categoryClass() {
        return SeriesCategoryObservation.class;
    }

    @Override
    public Class<? extends CountObservation> countClass() {
        return SeriesCountObservation.class;
    }

    @Override
    public Class<? extends GeometryObservation> geometryClass() {
        return SeriesGeometryObservation.class;
    }

    @Override
    public Class<? extends NumericObservation> numericClass() {
        return SeriesNumericObservation.class;
    }

    @Override
    public Class<? extends SweDataArrayObservation> sweDataArrayClass() {
        return SeriesSweDataArrayObservation.class;
    }

    @Override
    public Class<? extends TextObservation> textClass() {
        return SeriesTextObservation.class;
    }

    @Override
    public Class<? extends ComplexObservation> complexClass() {
        return SeriesComplexObservation.class;
    }

    @Override
    public Class<? extends ProfileObservation> profileClass() {
        return SeriesProfileObservation.class;
    }
    
    @Override
    public Class<? extends ReferenceObservation> referenceClass() {
        return SeriesReferenceObservation.class;
    }

    public Series series() {
        return new Series();
    }

    public Class<? extends Series> seriesClass() {
        return Series.class;
    }

    public static SeriesObservationFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final SeriesObservationFactory INSTANCE
                = new SeriesObservationFactory();

        private Holder() {
        }
    }
}
