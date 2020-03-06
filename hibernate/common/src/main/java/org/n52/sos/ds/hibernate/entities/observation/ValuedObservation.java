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
package org.n52.sos.ds.hibernate.entities.observation;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasUnitValue;
import org.n52.sos.ds.hibernate.entities.observation.valued.BlobValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.BooleanValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CategoryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.ComplexValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.CountValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.GeometryValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.NumericValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.SweDataArrayValuedObservation;
import org.n52.sos.ds.hibernate.entities.observation.valued.TextValuedObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * A {@code ValuedObservation} is a temporal referenced observation that
 * additionally holds a value that is described by a unit. This light-weight
 * class intentionally misses observed property, procedure and feature of
 * interest. These are featured in the more complete representations of
 * {@link ContextualReferencedObservation} and {@link Observation}.
 *
 * @author Christian Autermann
 * @param <T> the value type
 * @see BlobValuedObservation
 * @see BooleanValuedObservation
 * @see CategoryValuedObservation
 * @see ComplexValuedObservation
 * @see CountValuedObservation
 * @see GeometryValuedObservation
 * @see NumericValuedObservation
 * @see SweDataArrayValuedObservation
 * @see TextValuedObservation
 */
public interface ValuedObservation<T>
        extends TemporalReferencedObservation, HasUnitValue<T> {

    void accept(VoidValuedObservationVisitor visitor) throws OwsExceptionReport;

    <T> T accept(ValuedObservationVisitor<T> visitor) throws OwsExceptionReport;

}
