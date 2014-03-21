/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
/**
 
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 
 *
 
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 
 *
 
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 
 *
 
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 
 *
 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 
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
package org.n52.sos.ds.hibernate.dao;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.dao.i18n.AbstractFeatureI18NDAO;
import org.n52.sos.ds.hibernate.dao.i18n.I18NFeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.dao.i18n.I18NObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.i18n.I18NOfferingDAO;
import org.n52.sos.ds.hibernate.dao.i18n.I18NProcedureDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesSpatialFilteringProfileDAO;
import org.n52.sos.ds.hibernate.entities.AbstractIdentifierNameDescriptionEntity;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.SpatialFilteringProfile;
import org.n52.sos.ds.hibernate.entities.TFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.TObservableProperty;
import org.n52.sos.ds.hibernate.entities.TOffering;
import org.n52.sos.ds.hibernate.entities.TProcedure;
import org.n52.sos.ds.hibernate.entities.i18n.AbstractFeatureI18N;
import org.n52.sos.ds.hibernate.entities.i18n.I18NFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.i18n.I18NInsertionObject;
import org.n52.sos.ds.hibernate.entities.i18n.I18NObservableProperty;
import org.n52.sos.ds.hibernate.entities.i18n.I18NOffering;
import org.n52.sos.ds.hibernate.entities.i18n.I18NProcedure;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesSpatialFilteringProfile;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.i18n.I18NFeatureObject;
import org.n52.sos.i18n.I18NObject;
import org.n52.sos.i18n.I18NObservablePropertyObject;
import org.n52.sos.i18n.I18NOfferingObject;
import org.n52.sos.i18n.I18NProcedureObject;

/**
 * Hibernate data access factory
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 * 
 */
public class DaoFactory {

    /**
     * instance
     */
    private static DaoFactory instance;

    /**
     * Get the DaoFactory instance
     * 
     * @return Returns the instance of the DaoFactory.
     */
    public static synchronized DaoFactory getInstance() {
        if (instance == null) {
            instance = new DaoFactory();
        }
        return instance;
    }

    /**
     * Get the currently supported Hibernate Observation data access
     * implementation
     * 
     * @param session
     *            Hibernate session
     * @return Currently supported Hibernate Observation data access
     *         implementation
     * @throws CodedException
     *             If no Hibernate Observation data access is supported
     */
    public AbstractObservationDAO getObservationDAO(Session session) throws CodedException {
        if (HibernateHelper.isEntitySupported(SeriesObservation.class, session)) {
            return new SeriesObservationDAO();
        } else if (HibernateHelper.isEntitySupported(Observation.class, session)) {
            return new ObservationDAO();
        } else {
            throw new NoApplicableCodeException().withMessage("Implemented observation DAO is missing!");
        }
    }

    /**
     * Get the currently supported Hibernate SpatialFilteringProfile data access
     * implementation or null
     * 
     * @param session
     *            Hibernate session
     * @return Currently supported Hibernate SpatialFilteringProfile data access
     *         implementation
     */
    public AbstractSpatialFilteringProfileDAO<?> getSpatialFilteringProfileDAO(Session session) throws CodedException {
        if (HibernateHelper.isEntitySupported(SeriesSpatialFilteringProfile.class, session)) {
            return new SeriesSpatialFilteringProfileDAO();
        } else if (HibernateHelper.isEntitySupported(SpatialFilteringProfile.class, session)) {
            return new SpatialFilteringProfileDAO();
        }
        return null;
    }

    public AbstractFeatureI18NDAO getI18NDAO(AbstractIdentifierNameDescriptionEntity abstractIdentifierNameDescriptionEntity,
            Session session) {
        // if (abstractIdentifierNameDescriptionEntity instanceof
        // FeatureOfInterest) {
        // if (HibernateHelper.isEntitySupported(I18NFeatureOfInterest.class,
        // session)) {
        // return new I18NFeatureOfInterestDAO();
        // }
        // } else if (abstractIdentifierNameDescriptionEntity instanceof
        // ObservableProperty) {
        // if (HibernateHelper.isEntitySupported(I18NObservableProperty.class,
        // session)) {
        // return new I18NObservablePropertyDAO();
        // }
        // } else if (abstractIdentifierNameDescriptionEntity instanceof
        // Offering) {
        // if (HibernateHelper.isEntitySupported(I18NOffering.class, session)) {
        // return new I18NOfferingDAO();
        // }
        // } else if (abstractIdentifierNameDescriptionEntity instanceof
        // Procedure) {
        // if (HibernateHelper.isEntitySupported(I18NProcedure.class, session))
        // {
        // return new I18NProcedureDAO();
        // }
        // }
        return getI18NDAO(abstractIdentifierNameDescriptionEntity.getClass(), session);
    }

    public AbstractFeatureI18NDAO getI18NDAO(Class<?> clazz, Session session) {
        if (clazz == I18NFeatureObject.class || clazz == I18NFeatureOfInterest.class
                || clazz == FeatureOfInterest.class || clazz == TFeatureOfInterest.class) {
            if (HibernateHelper.isEntitySupported(I18NFeatureOfInterest.class, session)) {
                return new I18NFeatureOfInterestDAO();
            }
        } else if (clazz == I18NObservablePropertyObject.class || clazz == I18NObservableProperty.class
                || clazz == ObservableProperty.class || clazz == TObservableProperty.class) {
            if (HibernateHelper.isEntitySupported(I18NObservableProperty.class, session)) {
                return new I18NObservablePropertyDAO();
            }
        } else if (clazz == I18NOfferingObject.class || clazz == I18NOffering.class || clazz == Offering.class
                || clazz == TOffering.class) {
            if (HibernateHelper.isEntitySupported(I18NOffering.class, session)) {
                return new I18NOfferingDAO();
            }
        } else if (clazz == I18NProcedureObject.class || clazz == I18NProcedure.class || clazz == Procedure.class
                || clazz == TProcedure.class) {
            if (HibernateHelper.isEntitySupported(I18NProcedure.class, session)) {
                return new I18NProcedureDAO();
            }
        }
        return null;
    }

    public AbstractFeatureI18NDAO getI18NDAO(AbstractFeatureI18N abstractI18N, Session session) {
        // if (HibernateHelper.isEntitySupported(AbstractI18N.class, session)) {
        // if (abstractI18N instanceof I18NFeatureOfInterest) {
        // return new I18NFeatureOfInterestDAO();
        // } else if (abstractI18N instanceof I18NObservableProperty) {
        // return new I18NObservablePropertyDAO();
        // } else if (abstractI18N instanceof I18NOffering) {
        // return new I18NOfferingDAO();
        // } else if (abstractI18N instanceof I18NProcedure) {
        // return new I18NProcedureDAO();
        // }
        // }
        return getI18NDAO(abstractI18N.getClass(), session);
    }

}
