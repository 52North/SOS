/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasChildFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDeletedFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservationId;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasOfferings;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasParentFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSamplingGeometry;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasParameters;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasRelatedObservations;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCoordinate;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasSrid;
import org.n52.sos.ds.hibernate.entities.IdentifierNameDescriptionEntity;

/**
 * A {@code BaseObservation} is an observation without time, value, procedure,
 * observed property or feature of interest.
 *
 * @author Christian Autermann
 * @see TemporalReferencedObservation
 * @see ContextualReferencedObservation
 */
public interface BaseObservation
        extends IdentifierNameDescriptionEntity,
                HasDeletedFlag,
                HasObservationId,
                HasSamplingGeometry,
                HasCoordinate,
                HasSrid,
                HasOfferings,
                HasChildFlag,
                HasParentFlag,
                HasParameters,
                HasRelatedObservations {

}
