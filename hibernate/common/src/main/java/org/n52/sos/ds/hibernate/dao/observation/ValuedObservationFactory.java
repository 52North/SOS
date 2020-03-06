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
package org.n52.sos.ds.hibernate.dao.observation;

import org.n52.sos.ds.hibernate.entities.observation.ValuedObservation;
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
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public abstract class ValuedObservationFactory {
    
    @SuppressWarnings("rawtypes")
    public abstract Class<? extends ValuedObservation> valuedObservationClass();

    public abstract Class<? extends BlobValuedObservation> blobClass();

    public BlobValuedObservation blob()
            throws OwsExceptionReport {
        return instantiate(blobClass());
    }

    public abstract Class<? extends BooleanValuedObservation> truthClass();

    public BooleanValuedObservation truth()
            throws OwsExceptionReport {
        return instantiate(truthClass());
    }

    public abstract Class<? extends CategoryValuedObservation> categoryClass();

    public CategoryValuedObservation category()
            throws OwsExceptionReport {
        return instantiate(categoryClass());
    }

    public abstract Class<? extends CountValuedObservation> countClass();

    public CountValuedObservation count()
            throws OwsExceptionReport {
        return instantiate(countClass());
    }

    public abstract Class<? extends GeometryValuedObservation> geometryClass();

    public GeometryValuedObservation geometry()
            throws OwsExceptionReport {
        return instantiate(geometryClass());
    }

    public abstract Class<? extends NumericValuedObservation> numericClass();

    public NumericValuedObservation numeric()
            throws OwsExceptionReport {
        return instantiate(numericClass());
    }

    public abstract Class<? extends SweDataArrayValuedObservation> sweDataArrayClass();

    public SweDataArrayValuedObservation sweDataArray()
            throws OwsExceptionReport {
        return instantiate(sweDataArrayClass());
    }

    public abstract Class<? extends TextValuedObservation> textClass();

    public TextValuedObservation text()
            throws OwsExceptionReport {
        return instantiate(textClass());
    }

    public abstract Class<? extends ComplexValuedObservation> complexClass();

    public ComplexValuedObservation complex()
            throws OwsExceptionReport {
        return instantiate(complexClass());
    }
    
    public abstract Class<? extends ProfileValuedObservation> profileClass();

    public ProfileValuedObservation profile()
            throws OwsExceptionReport {
        return instantiate(profileClass());
    }
    
    public abstract Class<? extends ReferenceValuedObservation> referenceClass();

    public ReferenceValuedObservation reference()
            throws OwsExceptionReport {
        return instantiate(referenceClass());
    }

    private <T extends ValuedObservation<?>> T instantiate(Class<T> c)
            throws OwsExceptionReport {
        try {
            return c.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new NoApplicableCodeException().causedBy(ex)
                    .withMessage("Error while creating observation instance for %s", c);
        }
    }

    @SuppressWarnings("rawtypes")
    public Class<? extends ValuedObservation> classForValuedObservationType(
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
            }
        }
        return valuedObservationClass();
    }

    public ValuedObservation<?> forValuedObservationType(String observationType)
            throws OwsExceptionReport {
        return instantiate(classForValuedObservationType(observationType));
    }

}
