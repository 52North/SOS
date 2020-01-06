/*
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

import org.n52.series.db.beans.BlobDataEntity;
import org.n52.series.db.beans.BooleanDataEntity;
import org.n52.series.db.beans.CategoryDataEntity;
import org.n52.series.db.beans.ComplexDataEntity;
import org.n52.series.db.beans.CountDataEntity;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.GeometryDataEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.ReferencedDataEntity;
import org.n52.series.db.beans.TextDataEntity;
import org.n52.sos.ds.hibernate.dao.observation.ValuedObservationFactory;

public class SeriesValuedObervationFactory extends ValuedObservationFactory {

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends DataEntity> valuedObservationClass() {
        return DataEntity.class;
    }

    @Override
    public Class<? extends BlobDataEntity> blobClass() {
        return BlobDataEntity.class;
    }

    @Override
    public Class<? extends BooleanDataEntity> truthClass() {
        return BooleanDataEntity.class;
    }

    @Override
    public Class<? extends CategoryDataEntity> categoryClass() {
        return CategoryDataEntity.class;
    }

    @Override
    public Class<? extends CountDataEntity> countClass() {
        return CountDataEntity.class;
    }

    @Override
    public Class<? extends GeometryDataEntity> geometryClass() {
        return GeometryDataEntity.class;
    }

    @Override
    public Class<? extends QuantityDataEntity> numericClass() {
        return QuantityDataEntity.class;
    }

    @Override
    public Class<? extends DataArrayDataEntity> sweDataArrayClass() {
        return DataArrayDataEntity.class;
    }

    @Override
    public Class<? extends TextDataEntity> textClass() {
        return TextDataEntity.class;
    }

    @Override
    public Class<? extends ComplexDataEntity> complexClass() {
        return ComplexDataEntity.class;
    }

    @Override
    public Class<? extends ProfileDataEntity> profileClass() {
        return ProfileDataEntity.class;
    }

    @Override
    public Class<? extends ReferencedDataEntity> referenceClass() {
        return ReferencedDataEntity.class;
    }

    public static SeriesValuedObervationFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final SeriesValuedObervationFactory INSTANCE
                = new SeriesValuedObervationFactory();

        private Holder() {
        }
    }
}
