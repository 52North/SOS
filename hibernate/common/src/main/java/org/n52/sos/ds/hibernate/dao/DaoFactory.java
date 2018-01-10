/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.EReportingSetting;
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

/**
 * Hibernate data access factory.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.0
 *
 */
@Configurable
public class DaoFactory {

    private Set<Integer> validityFlags;
    private Set<Integer> verificationFlags;
    private I18NDAORepository i18NDAORepository;

    @Inject
    public void setI18NDAORepository(I18NDAORepository i18NDAORepository) {
        this.i18NDAORepository = i18NDAORepository;
    }

    @Setting(value = EReportingSetting.EREPORTING_VALIDITY_FLAGS, required = false)
    public void setValidityFlags(String validityFlags) {
        this.validityFlags = Optional.ofNullable(validityFlags).map(s -> Arrays.stream(s.split(","))
                .map(Integer::parseInt).collect(toSet())).orElseGet(Collections::emptySet);
    }

    @Setting(value = EReportingSetting.EREPORTING_VERIFICATION_FLAGS, required = false)
    public void setVerificationFlags(String verificationFlags) {
        this.verificationFlags = Optional.ofNullable(verificationFlags).map(s -> Arrays.stream(s.split(","))
                .map(Integer::parseInt).collect(toSet())).orElseGet(Collections::emptySet);
    }

    public AbstractSeriesDAO getSeriesDAO() throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(AbstractEReportingObservation.class)) {
            return new EReportingSeriesDAO(this);
        } else if (HibernateHelper.isEntitySupported(AbstractSeriesObservation.class)) {
            return new SeriesDAO(this);
        } else {
            throw new NoApplicableCodeException().withMessage("Implemented series DAO is missing!");
        }
    }

    /**
     * Get the currently supported Hibernate Observation data access implementation
     *
     * @return Currently supported Hibernate Observation data access implementation
     *
     * @throws OwsExceptionReport If no Hibernate Observation data access is supported
     */
    public AbstractObservationDAO getObservationDAO() throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(AbstractEReportingObservation.class)) {
            return new EReportingObservationDAO(this.verificationFlags, this.validityFlags, this);
        } else if (HibernateHelper.isEntitySupported(AbstractSeriesObservation.class)) {
            return new SeriesObservationDAO(this);
        } else if (HibernateHelper.isEntitySupported(AbstractLegacyObservation.class)) {
            return new LegacyObservationDAO(this);
        } else {
            throw new NoApplicableCodeException().withMessage("Implemented observation DAO is missing!");
        }
    }

    public AbstractObservationTimeDAO getObservationTimeDAO() throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(TemporalReferencedEReportingObservation.class)) {
            return new EReportingObservationTimeDAO();
        } else if (HibernateHelper.isEntitySupported(TemporalReferencedSeriesObservation.class)) {
            return new SeriesObservationTimeDAO();
        } else {
            throw new NoApplicableCodeException().withMessage("Implemented observation time DAO is missing!");
        }
    }

    public AbstractSeriesValueDAO getValueDAO() throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(AbstractValuedEReportingObservation.class)) {
            return new EReportingValueDAO(this.verificationFlags, this.validityFlags);
        } else if (HibernateHelper.isEntitySupported(AbstractValuedSeriesObservation.class)) {
            return new SeriesValueDAO();
//        } else if (HibernateHelper.isEntitySupported(ObservationValue.class)) {
//            return new ObserervationValueDAO();
        } else {
            throw new NoApplicableCodeException().withMessage("Implemented value DAO is missing!");
        }
    }

    public AbstractSeriesValueTimeDAO getValueTimeDAO() throws OwsExceptionReport {
        if (HibernateHelper.isEntitySupported(TemporalReferencedEReportingObservation.class)) {
            return new EReportingValueTimeDAO(this.verificationFlags, this.validityFlags);
        } else if (HibernateHelper.isEntitySupported(TemporalReferencedSeriesObservation.class)) {
            return new SeriesValueTimeDAO();
//        } else if (HibernateHelper.isEntitySupported(ObservationValueTime.class)) {
//            return new ObservationValueTimeDAO();
        } else {
            throw new NoApplicableCodeException().withMessage("Implemented value time DAO is missing!");
        }
    }

    public ProcedureDAO getProcedureDAO() {
        return new ProcedureDAO(this);
    }

    public ObservablePropertyDAO getObservablePropertyDAO() {
        return new ObservablePropertyDAO(this);
    }

    public FeatureOfInterestDAO getFeatureOfInterestDAO() {
        return new FeatureOfInterestDAO(this);
    }

    public ValidProcedureTimeDAO getValidProcedureTimeDAO() {
        return new ValidProcedureTimeDAO(this);
    }

    public ObservationConstellationDAO getObservationConstellationDAO() {
        return new ObservationConstellationDAO(this);
    }

    public RelatedFeatureDAO getRelatedFeatureDAO() {
        return new RelatedFeatureDAO(this);
    }

    public UnitDAO getUnitDAO() {
        return new UnitDAO();
    }

    public ResultTemplateDAO getResultTemplateDAO() {
        return new ResultTemplateDAO();
    }

    public RelatedFeatureRoleDAO getRelatedFeatureRoleDAO() {
        return new RelatedFeatureRoleDAO();
    }

    public CodespaceDAO getCodespaceDAO() {
        return new CodespaceDAO();
    }

    public ObservationTypeDAO getObservationTypeDAO() {
        return new ObservationTypeDAO();
    }

    public OfferingDAO getOfferingDAO() {
        return new OfferingDAO(this);
    }

    public ParameterDAO getParameterDAO() {
        return new ParameterDAO();
    }

    public ProcedureDescriptionFormatDAO getProcedureDescriptionFormatDAO() {
        return new ProcedureDescriptionFormatDAO();
    }

    public I18NDAORepository getI18NDAORepository() {
        return i18NDAORepository;
    }

}
