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
package org.n52.sos.ds.hibernate.dao.observation.legacy;

import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.entities.observation.ContextualReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.TemporalReferencedObservation;
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
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.ContextualReferencedLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.TemporalReferencedLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.full.LegacyBlobObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.full.LegacyBooleanObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.full.LegacyCategoryObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.full.LegacyComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.full.LegacyCountObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.full.LegacyGeometryObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.full.LegacyNumericObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.full.LegacySweDataArrayObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.full.LegacyTextObservation;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class LegacyObservationFactory extends ObservationFactory {
    protected LegacyObservationFactory() {
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends Observation> observationClass() {
        return AbstractLegacyObservation.class;
    }

    @Override
    public Class<? extends ContextualReferencedObservation> contextualReferencedClass() {
        return ContextualReferencedLegacyObservation.class;
    }

    @Override
    public Class<? extends TemporalReferencedObservation> temporalReferencedClass() {
        return TemporalReferencedLegacyObservation.class;
    }

    @Override
    public Class<? extends BlobObservation> blobClass() {
        return LegacyBlobObservation.class;
    }

    @Override
    public Class<? extends BooleanObservation> truthClass() {
        return LegacyBooleanObservation.class;
    }

    @Override
    public Class<? extends CategoryObservation> categoryClass() {
        return LegacyCategoryObservation.class;
    }

    @Override
    public Class<? extends CountObservation> countClass() {
        return LegacyCountObservation.class;
    }

    @Override
    public Class<? extends GeometryObservation> geometryClass() {
        return LegacyGeometryObservation.class;
    }

    @Override
    public Class<? extends NumericObservation> numericClass() {
        return LegacyNumericObservation.class;
    }

    @Override
    public Class<? extends SweDataArrayObservation> sweDataArrayClass() {
        return LegacySweDataArrayObservation.class;
    }

    @Override
    public Class<? extends TextObservation> textClass() {
        return LegacyTextObservation.class;
    }

    @Override
    public Class<? extends ComplexObservation> complexClass() {
        return LegacyComplexObservation.class;
    }

    @Override
    public Class<? extends ProfileObservation> profileClass() {
        return null;
    }

    @Override
    public Class<? extends ReferenceObservation> referenceClass() {
        return null;
    }

    public static LegacyObservationFactory getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final LegacyObservationFactory INSTANCE
                = new LegacyObservationFactory();

        private Holder() {
        }
    }
}
