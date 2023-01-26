/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.hibernate.dao.observation;

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
import org.n52.series.db.beans.TrajectoryDataEntity;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

public abstract class ObservationFactory {

    public abstract Class<? extends DataEntity> observationClass();

    public abstract Class<? extends DataEntity> contextualReferencedClass();

    public abstract Class<? extends DataEntity> temporalReferencedClass();

    public abstract Class<? extends BlobDataEntity> blobClass();

    public BlobDataEntity blob()
            throws OwsExceptionReport {
        return instantiate(blobClass());
    }

    public abstract Class<? extends BooleanDataEntity> truthClass();

    public BooleanDataEntity truth()
            throws OwsExceptionReport {
        return instantiate(truthClass());
    }

    public abstract Class<? extends CategoryDataEntity> categoryClass();

    public CategoryDataEntity category()
            throws OwsExceptionReport {
        return instantiate(categoryClass());
    }

    public abstract Class<? extends CountDataEntity> countClass();

    public CountDataEntity count()
            throws OwsExceptionReport {
        return instantiate(countClass());
    }

    public abstract Class<? extends GeometryDataEntity> geometryClass();

    public GeometryDataEntity geometry()
            throws OwsExceptionReport {
        return instantiate(geometryClass());
    }

    public abstract Class<? extends QuantityDataEntity> numericClass();

    public QuantityDataEntity numeric()
            throws OwsExceptionReport {
        return instantiate(numericClass());
    }

    public abstract Class<? extends DataArrayDataEntity> sweDataArrayClass();

    public DataArrayDataEntity sweDataEntityArray()
            throws OwsExceptionReport {
        return instantiate(sweDataArrayClass());
    }

    public abstract Class<? extends TextDataEntity> textClass();

    public TextDataEntity text()
            throws OwsExceptionReport {
        return instantiate(textClass());
    }

    public abstract Class<? extends ComplexDataEntity> complexClass();

    public ComplexDataEntity complex()
            throws OwsExceptionReport {
        return instantiate(complexClass());
    }

    public abstract Class<? extends ProfileDataEntity> profileClass();

    public ProfileDataEntity profile()
            throws OwsExceptionReport {
        return instantiate(profileClass());
    }

    public abstract Class<? extends TrajectoryDataEntity> trajectoryClass();

    public TrajectoryDataEntity trajectory()
            throws OwsExceptionReport {
        return instantiate(trajectoryClass());
    }

    public abstract Class<? extends ReferencedDataEntity> referenceClass();

    public ReferencedDataEntity reference()
            throws OwsExceptionReport {
        return instantiate(referenceClass());
    }

    private <T extends DataEntity<?>> T instantiate(Class<T> c)
            throws OwsExceptionReport {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new NoApplicableCodeException().causedBy(ex)
                    .withMessage("Error while creating observation instance for %s", c);
        }
    }

    @SuppressWarnings("rawtypes")
    public Class<? extends DataEntity> classForObservationType(
            String observationType) {
        if (observationType != null) {
            switch (observationType) {
                case OmConstants.OBS_TYPE_MEASUREMENT:
                    return numericClass();
                case OmConstants.OBS_TYPE_COUNT_OBSERVATION:
                    return countClass();
                case OmConstants.OBS_TYPE_CATEGORY_OBSERVATION:
                    return categoryClass();
                case OmConstants.OBS_TYPE_TRUTH_OBSERVATION:
                    return truthClass();
                case OmConstants.OBS_TYPE_TEXT_OBSERVATION:
                    return textClass();
                case OmConstants.OBS_TYPE_GEOMETRY_OBSERVATION:
                    return geometryClass();
                case OmConstants.OBS_TYPE_COMPLEX_OBSERVATION:
                    return complexClass();
                case OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION:
                    return sweDataArrayClass();
                case OmConstants.OBS_TYPE_REFERENCE_OBSERVATION:
                    return referenceClass();
                case OmConstants.OBS_TYPE_UNKNOWN:
                    return blobClass();
                default:
                    return observationClass();
            }
        }
        return observationClass();
    }

    public DataEntity<?> forObservationType(String observationType)
            throws OwsExceptionReport {
        return instantiate(classForObservationType(observationType));
    }
}
