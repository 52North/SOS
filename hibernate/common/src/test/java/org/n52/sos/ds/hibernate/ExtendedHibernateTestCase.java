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
package org.n52.sos.ds.hibernate;

import org.hibernate.Session;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;

/**
 * Abstract test class that contains implemented methods
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.0
 *
 *
 */
public abstract class ExtendedHibernateTestCase extends HibernateTestCase {

    @Deprecated
    protected static Class<?> getObservationClass(Session session) throws OwsExceptionReport {
        return getObservationDao().getObservationFactory().observationClass();
    }

    @SuppressWarnings("rawtypes")
    protected static Class<? extends DataEntity> getObservationClass() throws OwsExceptionReport {
        return getObservationDao().getObservationFactory().observationClass();
    }

    @Deprecated
    protected static Class<?> getObservationInfoClass(Session session) throws OwsExceptionReport {
        return getObservationDao().getObservationFactory().contextualReferencedClass();
    }

    @SuppressWarnings("rawtypes")
    protected static Class<? extends DataEntity> getContextualReferencedObservationClass() throws OwsExceptionReport {
        return getObservationDao().getObservationFactory().contextualReferencedClass();
    }

    protected static DaoFactory getDaoFactory() {
        I18NDAORepository i18NDAORepository = new I18NDAORepository();
        DaoFactory daoFactory = new DaoFactory();
        daoFactory.setI18NDAORepository(i18NDAORepository);
        return daoFactory;
    }

    protected static AbstractObservationDAO getObservationDao() throws OwsExceptionReport {
        return getDaoFactory().getObservationDAO();
    }
}
