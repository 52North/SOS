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
package org.n52.sos.ds.hibernate;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.entities.observation.ContextualReferencedObservation;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 * Abstract test class that contains implemented methods
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 *
 *
 */
public abstract class ExtendedHibernateTestCase extends HibernateTestCase {

    @Deprecated
    protected static Class<?> getObservationClass(Session session) throws OwsExceptionReport {
        return DaoFactory.getInstance().getObservationDAO().getObservationFactory().observationClass();
    }

    @Deprecated
    protected static Class<?> getObservationInfoClass(Session session) throws OwsExceptionReport {
        return DaoFactory.getInstance().getObservationDAO().getObservationFactory().contextualReferencedClass();
    }

    @SuppressWarnings("rawtypes")
    protected static Class<? extends Observation> getObservationClass() throws OwsExceptionReport {
        return DaoFactory.getInstance().getObservationDAO().getObservationFactory().observationClass();
    }

    protected static Class<? extends ContextualReferencedObservation> getContextualReferencedObservationClass() throws OwsExceptionReport {
        return DaoFactory.getInstance().getObservationDAO().getObservationFactory().contextualReferencedClass();
    }
}
