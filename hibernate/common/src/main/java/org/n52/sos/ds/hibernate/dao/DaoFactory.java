/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.ds.hibernate.dao.ereporting.EReportingObservationDAO;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingObservationTimeDAO;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingSeriesDAO;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingValueDAO;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingValueTimeDAO;
import org.n52.sos.ds.hibernate.dao.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesObservationTimeDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesValueDAO;
import org.n52.sos.ds.hibernate.dao.series.SeriesValueTimeDAO;
import org.n52.sos.ds.hibernate.entities.Observation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingObservation;
import org.n52.sos.ds.hibernate.entities.ereporting.EReportingObservationTime;
import org.n52.sos.ds.hibernate.entities.ereporting.values.EReportingValue;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservation;
import org.n52.sos.ds.hibernate.entities.series.SeriesObservationTime;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValue;
import org.n52.sos.ds.hibernate.entities.series.values.SeriesValueTime;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;


/**
 * Hibernate data access factory.
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

    public AbstractSeriesDAO getSeriesDAO() throws CodedException {
        if (HibernateHelper.isEntitySupported(EReportingObservation.class)) {
            return new EReportingSeriesDAO();
        } else if (HibernateHelper.isEntitySupported(SeriesObservation.class)) {
            return new SeriesDAO();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Implemented series DAO is missing!");
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
    public AbstractObservationDAO getObservationDAO()
            throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(EReportingObservation.class)) {
            return new EReportingObservationDAO();
        } else if (HibernateHelper.isEntitySupported(SeriesObservation.class)) {
            return new SeriesObservationDAO();
        } else if (HibernateHelper.isEntitySupported(Observation.class)) {
            return new ObservationDAO();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Implemented observation DAO is missing!");
        }
    }
    
    public AbstractObservationTimeDAO getObservationTimeDAO() throws CodedException {
        if (HibernateHelper.isEntitySupported(EReportingObservationTime.class)) {
            return new EReportingObservationTimeDAO();
        } else if (HibernateHelper.isEntitySupported(SeriesObservationTime.class)) {
            return new SeriesObservationTimeDAO();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Implemented observation time DAO is missing!");
        }
    }
    
    public AbstractValueDAO getValueDAO() throws CodedException {
        if (HibernateHelper.isEntitySupported(EReportingValue.class)) {
            return new EReportingValueDAO();
        } else if (HibernateHelper.isEntitySupported(SeriesValue.class)) {
            return new SeriesValueDAO();
//        } else if (HibernateHelper.isEntitySupported(ObservationValue.class)) {
//            return new ObserervationValueDAO();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Implemented value DAO is missing!");
        }
    }
    
    public AbstractValueTimeDAO getValueTimeDAO() throws CodedException {
        if (HibernateHelper.isEntitySupported(EReportingObservation.class)) {
            return new EReportingValueTimeDAO();
        } else if (HibernateHelper.isEntitySupported(SeriesValueTime.class)) {
            return new SeriesValueTimeDAO();
//        } else if (HibernateHelper.isEntitySupported(ObservationValueTime.class)) {
//            return new ObservationValueTimeDAO();
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("Implemented value time DAO is missing!");
        }
    }

}
