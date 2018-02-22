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
package org.n52.sos.ds.hibernate.dao.observation.series;

import java.util.Optional;

import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.data.Data;
import org.n52.series.db.beans.data.Data.BlobData;
import org.n52.series.db.beans.data.Data.BooleanData;
import org.n52.series.db.beans.data.Data.CategoryData;
import org.n52.series.db.beans.data.Data.ComplexData;
import org.n52.series.db.beans.data.Data.CountData;
import org.n52.series.db.beans.data.Data.DataArrayData;
import org.n52.series.db.beans.data.Data.GeometryData;
import org.n52.series.db.beans.data.Data.ProfileData;
import org.n52.series.db.beans.data.Data.QuantityData;
import org.n52.series.db.beans.data.Data.ReferencedData;
import org.n52.series.db.beans.data.Data.TextData;
import org.n52.series.db.beans.dataset.BlobDataset;
import org.n52.series.db.beans.dataset.BooleanDataset;
import org.n52.series.db.beans.dataset.CategoryDataset;
import org.n52.series.db.beans.dataset.ComplexDataset;
import org.n52.series.db.beans.dataset.CountDataset;
import org.n52.series.db.beans.dataset.DataArrayDataset;
import org.n52.series.db.beans.dataset.Dataset;
import org.n52.series.db.beans.dataset.GeometryDataset;
import org.n52.series.db.beans.dataset.NotInitializedDataset;
import org.n52.series.db.beans.dataset.ProfileDataset;
import org.n52.series.db.beans.dataset.QuantityDataset;
import org.n52.series.db.beans.dataset.ReferencedDataset;
import org.n52.series.db.beans.dataset.TextDataset;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

public abstract class DatasetFactory {

    public abstract Class<? extends Dataset> datasetClass();

    private Dataset dataset()
            throws OwsExceptionReport {
        return instantiate(datasetClass());
    }

    public abstract Class<? extends NotInitializedDataset> notInitializedClass();

    private NotInitializedDataset notInitialized()
            throws OwsExceptionReport {
        return instantiate(notInitializedClass());
    }

    public abstract Class<? extends BlobDataset> blobClass();

    public BlobDataset blob()
            throws OwsExceptionReport {
        return instantiate(blobClass());
    }

    public abstract Class<? extends BooleanDataset> truthClass();

    public BooleanDataset truth()
            throws OwsExceptionReport {
        return instantiate(truthClass());
    }

    public abstract Class<? extends CategoryDataset> categoryClass();

    public CategoryDataset category()
            throws OwsExceptionReport {
        return instantiate(categoryClass());
    }

    public abstract Class<? extends CountDataset> countClass();

    public CountDataset count()
            throws OwsExceptionReport {
        return instantiate(countClass());
    }

    public abstract Class<? extends GeometryDataset> geometryClass();

    public GeometryDataset geometry()
            throws OwsExceptionReport {
        return instantiate(geometryClass());
    }

    public abstract Class<? extends QuantityDataset> numericClass();

    public QuantityDataset numeric()
            throws OwsExceptionReport {
        return instantiate(numericClass());
    }

    public abstract Class<? extends DataArrayDataset> sweDataArrayClass();

    public DataArrayDataset sweDataArray()
            throws OwsExceptionReport {
        return instantiate(sweDataArrayClass());
    }

    public abstract Class<? extends TextDataset> textClass();

    public TextDataset text()
            throws OwsExceptionReport {
        return instantiate(textClass());
    }

    public abstract Class<? extends ComplexDataset> complexClass();

    public ComplexDataset complex()
            throws OwsExceptionReport {
        return instantiate(complexClass());
    }

    public abstract Class<? extends ProfileDataset> profileClass();

    public ProfileDataset profile()
            throws OwsExceptionReport {
        return instantiate(profileClass());
    }

    public abstract Class<? extends ProfileDataset> textProfileClass();

    public Dataset textProfile()
            throws OwsExceptionReport {
        return instantiate(textProfileClass());
    }

    public abstract Class<? extends ProfileDataset> categoryProfileClass();

    public Dataset categoryProfile()
            throws OwsExceptionReport {
        return instantiate(categoryProfileClass());
    }

    public abstract Class<? extends ProfileDataset> quantityProfileClass();

    public Dataset quantityProfile()
            throws OwsExceptionReport {
        return instantiate(quantityProfileClass());
    }

    public abstract Class<? extends ReferencedDataset> referenceClass();

    public ReferencedDataset reference()
            throws OwsExceptionReport {
        return instantiate(referenceClass());
    }

    private <T extends Dataset> T instantiate(Class<T> c)
            throws OwsExceptionReport {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new NoApplicableCodeException().causedBy(ex)
                    .withMessage("Error while creating observation instance for %s", c);
        }
    }

    public Class<? extends Dataset> classForObservationType(
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
                    return datasetClass();
            }
        }
        return datasetClass();
    }

    public Dataset forObservationType(String observationType)
            throws OwsExceptionReport {
        return instantiate(classForObservationType(observationType));
    }

    public Dataset visit(Data<?> o)
            throws OwsExceptionReport {
        if (o != null) {
            if (o instanceof QuantityData) {
                return numeric();
            } else if (o instanceof BlobData) {
                return blob();
            } else if (o instanceof BooleanData) {
                return truth();
            } else if (o instanceof CategoryData) {
                return category();
            } else if (o instanceof CountData) {
                return count();
            } else if (o instanceof GeometryData) {
                return geometry();
            } else if (o instanceof DataArrayData) {
                return sweDataArray();
            } else if (o instanceof TextData) {
                return text();
            } else if (o instanceof ComplexData) {
                return complex();
            } else if (o instanceof ProfileData) {
                Optional<DataEntity<?>> value = ((ProfileDataEntity)o).getValue().stream().findFirst();
                if (value.isPresent()) {
                    if (value.get() instanceof QuantityData) {
                        return quantityProfile();
                    } else if (value.get() instanceof CategoryData) {
                        return categoryProfile();
                    } else if (value.get() instanceof TextData) {
                        return textProfile();
                    }
                }
                return profile();
            } else if (o instanceof ReferencedData) {
                return reference();
            }
        }

        return notInitialized();
    }
}
