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
package org.n52.sos.ds.hibernate.dao;

import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.ereporting.EReportingObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.ereporting.EReportingObservationTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.ereporting.EReportingSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.ereporting.EReportingValueDAO;
import org.n52.sos.ds.hibernate.dao.observation.ereporting.EReportingValueTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.legacy.LegacyObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesValueDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesValueTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesValueDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesValueTimeDAO;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.AbstractEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.AbstractValuedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.ereporting.TemporalReferencedEReportingObservation;
import org.n52.sos.ds.hibernate.entities.observation.legacy.AbstractLegacyObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.AbstractValuedSeriesObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.TemporalReferencedSeriesObservation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;



public class DaoFactory {
    /**
     * instance
     */
    private static final DaoFactory INSTANCE = new DaoFactory();

    public AbstractSeriesDAO getSeriesDAO() throws CodedException {
        if (HibernateHelper.isEntitySupported(AbstractEReportingObservation.class)) {
            return new EReportingSeriesDAO();
        } else if (HibernateHelper.isEntitySupported(AbstractSeriesObservation.class)) {
            return new SeriesDAO();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Implemented series DAO is missing!");
        }
    }
    
    public boolean isSeriesDAO() {
        if (HibernateHelper.isEntitySupported(AbstractEReportingObservation.class)) {
            return true;
        } else if (HibernateHelper.isEntitySupported(AbstractSeriesObservation.class)) {
            return true;
        } else {
           return false;
        }
    }
    

    /**
     * Get the currently supported Hibernate Observation data access
     * implementation
     *
     * @return Currently supported Hibernate Observation data access
     *         implementation
     *
     * @throws OwsExceptionReport
     *                        If no Hibernate Observation data access is supported
     */
    public AbstractObservationDAO getObservationDAO() throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(AbstractEReportingObservation.class)) {
            return new EReportingObservationDAO();
        } else if (HibernateHelper.isEntitySupported(AbstractSeriesObservation.class)) {
            return new SeriesObservationDAO();
        } else if (HibernateHelper.isEntitySupported(AbstractLegacyObservation.class)) {
            return new LegacyObservationDAO();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Implemented observation DAO is missing!");
        }
    }

    public AbstractObservationTimeDAO getObservationTimeDAO()
            throws CodedException {
        if (HibernateHelper.isEntitySupported(TemporalReferencedEReportingObservation.class)) {
            return new EReportingObservationTimeDAO();
        } else if (HibernateHelper.isEntitySupported(TemporalReferencedSeriesObservation.class)) {
            return new SeriesObservationTimeDAO();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Implemented observation time DAO is missing!");
        }
    }

    public AbstractSeriesValueDAO getValueDAO() throws CodedException {
        if (HibernateHelper.isEntitySupported(AbstractValuedEReportingObservation.class)) {
            return new EReportingValueDAO();
        } else if (HibernateHelper.isEntitySupported(AbstractValuedSeriesObservation.class)) {
            return new SeriesValueDAO();
//        } else if (HibernateHelper.isEntitySupported(ObservationValue.class)) {
//            return new ObserervationValueDAO();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Implemented value DAO is missing!");
        }
    }

    public AbstractSeriesValueTimeDAO getValueTimeDAO() throws CodedException {
        if (HibernateHelper.isEntitySupported(TemporalReferencedEReportingObservation.class)) {
            return new EReportingValueTimeDAO();
        } else if (HibernateHelper.isEntitySupported(TemporalReferencedSeriesObservation.class)) {
            return new SeriesValueTimeDAO();
//        } else if (HibernateHelper.isEntitySupported(ObservationValueTime.class)) {
//            return new ObservationValueTimeDAO();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Implemented value time DAO is missing!");
        }
    }
    
    public AbstractFeatureOfInterestDAO getFeatureDAO() throws CodedException {
        return new FeatureOfInterestDAO();
    }

    private DaoFactory() {
    }

    /**
     * Get the DaoFactory instance
     *
     * @return Returns the instance of the DaoFactory.
     */
    public static DaoFactory getInstance() {
        return INSTANCE;
    }

}
