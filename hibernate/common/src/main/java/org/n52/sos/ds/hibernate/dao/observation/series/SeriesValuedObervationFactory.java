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

import org.n52.sos.ds.hibernate.dao.observation.ValuedObservationFactory;
import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.BlobValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.BooleanValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.CategoryValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.ComplexValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.CountValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.GeometryValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.NumericValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.ProfileValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.ReferenceValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.SweDataArrayValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.valued.TextValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.BlobValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.BooleanValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CategoryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ComplexValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CountValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.GeometryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.NumericValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ProfileValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ReferenceValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.SweDataArrayValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.TextValuedObservation;

public class SeriesValuedObervationFactory extends ValuedObservationFactory{

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends ValuedObservation> valuedObservationClass() {
        return AbstractValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends BlobValuedObservation> blobClass() {
        return BlobValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends BooleanValuedObservation> truthClass() {
        return BooleanValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends CategoryValuedObservation> categoryClass() {
        return CategoryValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends CountValuedObservation> countClass() {
        return CountValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends GeometryValuedObservation> geometryClass() {
        return GeometryValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends NumericValuedObservation> numericClass() {
        return NumericValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends SweDataArrayValuedObservation> sweDataArrayClass() {
        return SweDataArrayValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends TextValuedObservation> textClass() {
        return TextValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends ComplexValuedObservation> complexClass() {
        return ComplexValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends ProfileValuedObservation> profileClass() {
        return ProfileValuedSeriesObservation.class;
    }

    @Override
    public Class<? extends ReferenceValuedObservation> referenceClass() {
        return ReferenceValuedSeriesObservation.class;
    }

    public static SeriesValuedObervationFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final SeriesValuedObervationFactory INSTANCE
                = new SeriesValuedObervationFactory();

        private Holder() {
        }
    }
}
