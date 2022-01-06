/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import org.n52.series.db.beans.BlobDataEntity;
import org.n52.series.db.beans.BooleanDataEntity;
import org.n52.series.db.beans.CategoryDataEntity;
import org.n52.series.db.beans.ComplexDataEntity;
import org.n52.series.db.beans.CountDataEntity;
import org.n52.series.db.beans.DataArrayDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.GeometryDataEntity;
import org.n52.series.db.beans.ProfileDataEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.ReferencedDataEntity;
import org.n52.series.db.beans.TextDataEntity;
import org.n52.series.db.beans.TrajectoryDataEntity;
import org.n52.series.db.beans.dataset.DatasetType;
import org.n52.series.db.beans.dataset.ObservationType;
import org.n52.series.db.beans.dataset.ValueType;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;

public abstract class DatasetFactory {

    public abstract Class<? extends DatasetEntity> datasetClass();

    protected DatasetEntity dataset()
            throws OwsExceptionReport {
        return instantiate(datasetClass());
    }

    public abstract Class<? extends DatasetEntity> notInitializedClass();

    private DatasetEntity notInitialized()
            throws OwsExceptionReport {
        return instantiate(notInitializedClass());
    }

    public abstract Class<? extends DatasetEntity> blobClass();

    public DatasetEntity blob()
            throws OwsExceptionReport {
        DatasetEntity blob = instantiate(blobClass());
        blob.setDatasetType(DatasetType.timeseries);
        blob.setObservationType(ObservationType.simple);
        blob.setValueType(ValueType.blob);
        return blob;
    }

    public abstract Class<? extends DatasetEntity> truthClass();

    public DatasetEntity truth()
            throws OwsExceptionReport {
        DatasetEntity truth = instantiate(blobClass());
        truth.setDatasetType(DatasetType.timeseries);
        truth.setObservationType(ObservationType.simple);
        truth.setValueType(ValueType.bool);
        return truth;
    }

    public abstract Class<? extends DatasetEntity> categoryClass();

    public DatasetEntity category()
            throws OwsExceptionReport {
        DatasetEntity category = instantiate(blobClass());
        category.setDatasetType(DatasetType.timeseries);
        category.setObservationType(ObservationType.simple);
        category.setValueType(ValueType.category);
        return category;
    }

    public abstract Class<? extends DatasetEntity> countClass();

    public DatasetEntity count()
            throws OwsExceptionReport {
        DatasetEntity count = instantiate(blobClass());
        count.setDatasetType(DatasetType.timeseries);
        count.setObservationType(ObservationType.simple);
        count.setValueType(ValueType.count);
        return count;
    }

    public abstract Class<? extends DatasetEntity> geometryClass();

    public DatasetEntity geometry()
            throws OwsExceptionReport {
        DatasetEntity geometry = instantiate(blobClass());
        geometry.setDatasetType(DatasetType.timeseries);
        geometry.setObservationType(ObservationType.simple);
        geometry.setValueType(ValueType.geometry);
        return geometry;
    }

    public abstract Class<? extends DatasetEntity> numericClass();

    public DatasetEntity numeric()
            throws OwsExceptionReport {
        DatasetEntity quantity = instantiate(blobClass());
        quantity.setDatasetType(DatasetType.timeseries);
        quantity.setObservationType(ObservationType.simple);
        quantity.setValueType(ValueType.quantity);
        return quantity;
    }

    public abstract Class<? extends DatasetEntity> sweDataArrayClass();

    public DatasetEntity sweDataArray()
            throws OwsExceptionReport {
        DatasetEntity dataarray = instantiate(blobClass());
        dataarray.setDatasetType(DatasetType.timeseries);
        dataarray.setObservationType(ObservationType.simple);
        dataarray.setValueType(ValueType.dataarray);
        return dataarray;
    }

    public abstract Class<? extends DatasetEntity> textClass();

    public DatasetEntity text()
            throws OwsExceptionReport {
        DatasetEntity text = instantiate(blobClass());
        text.setDatasetType(DatasetType.timeseries);
        text.setObservationType(ObservationType.simple);
        text.setValueType(ValueType.text);
        return text;
    }

    public abstract Class<? extends DatasetEntity> complexClass();

    public DatasetEntity complex()
            throws OwsExceptionReport {
        DatasetEntity complex = instantiate(blobClass());
        complex.setDatasetType(DatasetType.timeseries);
        complex.setObservationType(ObservationType.simple);
        complex.setValueType(ValueType.complex);
        return complex;
    }

    public abstract Class<? extends DatasetEntity> profileClass();

    public DatasetEntity profile()
            throws OwsExceptionReport {
        DatasetEntity profile = instantiate(blobClass());
        profile.setDatasetType(DatasetType.timeseries);
        profile.setObservationType(ObservationType.profile);
        profile.setValueType(ValueType.not_initialized);
        return profile;
    }

    public abstract Class<? extends DatasetEntity> textProfileClass();

    public DatasetEntity textProfile()
            throws OwsExceptionReport {
        DatasetEntity profile = instantiate(blobClass());
        profile.setDatasetType(DatasetType.timeseries);
        profile.setObservationType(ObservationType.profile);
        profile.setValueType(ValueType.text);
        return profile;
    }

    public abstract Class<? extends DatasetEntity> categoryProfileClass();

    public DatasetEntity categoryProfile()
            throws OwsExceptionReport {
        DatasetEntity profile = instantiate(blobClass());
        profile.setDatasetType(DatasetType.timeseries);
        profile.setObservationType(ObservationType.profile);
        profile.setValueType(ValueType.category);
        return profile;
    }

    public abstract Class<? extends DatasetEntity> quantityProfileClass();

    public DatasetEntity quantityProfile()
            throws OwsExceptionReport {
        DatasetEntity profile = instantiate(blobClass());
        profile.setDatasetType(DatasetType.timeseries);
        profile.setObservationType(ObservationType.profile);
        profile.setValueType(ValueType.quantity);
        return profile;
    }

    public abstract Class<? extends DatasetEntity> trajectoryClass();

    public DatasetEntity trajectory()
            throws OwsExceptionReport {
        DatasetEntity trajectory = instantiate(blobClass());
        trajectory.setDatasetType(DatasetType.trajectory);
        trajectory.setObservationType(ObservationType.simple);
        trajectory.setValueType(ValueType.not_initialized);
        return trajectory;
    }

    public abstract Class<? extends DatasetEntity> textTrajectoryClass();

    public DatasetEntity textTrajectory()
            throws OwsExceptionReport {
        DatasetEntity trajectory = instantiate(blobClass());
        trajectory.setDatasetType(DatasetType.trajectory);
        trajectory.setObservationType(ObservationType.simple);
        trajectory.setValueType(ValueType.text);
        return trajectory;
    }

    public abstract Class<? extends DatasetEntity> categoryTrajectoryClass();

    public DatasetEntity categoryTrajectory()
            throws OwsExceptionReport {
        DatasetEntity trajectory = instantiate(blobClass());
        trajectory.setDatasetType(DatasetType.trajectory);
        trajectory.setObservationType(ObservationType.simple);
        trajectory.setValueType(ValueType.category);
        return trajectory;
    }

    public abstract Class<? extends DatasetEntity> quantityTrajectoryClass();

    public DatasetEntity quantityTrajectory()
            throws OwsExceptionReport {
        DatasetEntity trajectory = instantiate(blobClass());
        trajectory.setDatasetType(DatasetType.trajectory);
        trajectory.setObservationType(ObservationType.simple);
        trajectory.setValueType(ValueType.quantity);
        return trajectory;
    }

    public abstract Class<? extends DatasetEntity> referenceClass();

    public DatasetEntity reference()
            throws OwsExceptionReport {
        return instantiate(referenceClass()).setDatasetType(DatasetType.timeseries)
                .setObservationType(ObservationType.simple).setValueType(ValueType.reference);
    }

    private <T extends DatasetEntity> T instantiate(Class<T> c)
            throws OwsExceptionReport {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new NoApplicableCodeException().causedBy(ex)
                    .withMessage("Error while creating observation instance for %s", c);
        }
    }

    public Class<? extends DatasetEntity> classForObservationType(
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

    public DatasetEntity forObservationType(String observationType)
            throws OwsExceptionReport {
        return instantiate(classForObservationType(observationType));
    }

    public DatasetEntity visit(DataEntity<?> o)
            throws OwsExceptionReport {
        if (o != null) {
            if (o instanceof QuantityDataEntity) {
                return numeric();
            } else if (o instanceof BlobDataEntity) {
                return blob();
            } else if (o instanceof BooleanDataEntity) {
                return truth();
            } else if (o instanceof CategoryDataEntity) {
                return category();
            } else if (o instanceof CountDataEntity) {
                return count();
            } else if (o instanceof GeometryDataEntity) {
                return geometry();
            } else if (o instanceof DataArrayDataEntity) {
                return sweDataArray();
            } else if (o instanceof TextDataEntity) {
                return text();
            } else if (o instanceof ComplexDataEntity) {
                return complex();
            } else if (o instanceof ProfileDataEntity) {
                Optional<DataEntity<?>> value = ((ProfileDataEntity) o).getValue().stream().findFirst();
                if (value.isPresent()) {
                    if (value.get() instanceof QuantityDataEntity) {
                        return quantityProfile();
                    } else if (value.get() instanceof CategoryDataEntity) {
                        return categoryProfile();
                    } else if (value.get() instanceof TextDataEntity) {
                        return textProfile();
                    }
                }
                return profile();
            } else if (o instanceof TrajectoryDataEntity) {
                Optional<DataEntity<?>> value = ((TrajectoryDataEntity) o).getValue().stream().findFirst();
                if (value.isPresent()) {
                    if (value.get() instanceof QuantityDataEntity) {
                        return quantityTrajectory();
                    } else if (value.get() instanceof CategoryDataEntity) {
                        return categoryTrajectory();
                    } else if (value.get() instanceof TextDataEntity) {
                        return textTrajectory();
                    }
                }
                return trajectory();
            } else if (o instanceof ReferencedDataEntity) {
                return reference();
            }
        }

        return notInitialized();
    }
}
