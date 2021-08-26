/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.ereporting.EReportingSamplingPointEntity;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.EReportingSetting;
import org.n52.sos.ds.FeatureQueryHandler;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.ereporting.EReportingObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.ereporting.EReportingObservationTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.ereporting.EReportingSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.ereporting.EReportingValueDAO;
import org.n52.sos.ds.hibernate.dao.observation.ereporting.EReportingValueTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesValueDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesValueTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesObservationTimeDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesValueDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesValueTimeDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.request.operator.AbstractRequestOperator;
import org.n52.sos.service.SosSettings;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.SosHelper;
import org.n52.svalbard.decode.DecoderRepository;
import org.n52.svalbard.encode.EncoderRepository;
import org.n52.svalbard.util.SweHelper;
import org.n52.svalbard.util.XmlOptionsHelper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Hibernate data access factory.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.0.0
 *
 */
@Configurable
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class DaoFactory {

    private Set<Integer> validityFlags;
    private Set<Integer> verificationFlags;
    private EncoderRepository encoderRepository;
    private DecoderRepository decoderRepository;
    private XmlOptionsHelper xmlOptionsHelper;
    private I18NDAORepository i18NDAORepository;
    private GeometryHandler geometryHandler;
    private SweHelper sweHelper;
    private FeatureQueryHandler featureQueryHandler;
    private boolean includeChildObservableProperties;
    private boolean staSupportsUrls;
    private SosHelper sosHelper;

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

    @Inject
    public void setEncoderRepository(EncoderRepository encoderRepository) {
        this.encoderRepository = encoderRepository;
    }

    @Inject
    public void setDecoderRepository(DecoderRepository decoderRepository) {
        this.decoderRepository = decoderRepository;
    }

    @Inject
    public void setXmlOptionsHelper(XmlOptionsHelper xmlOptionsHelper) {
        this.xmlOptionsHelper = xmlOptionsHelper;
    }

    @Inject
    public void setGeometryHandler(GeometryHandler geometryHandler) {
        this.geometryHandler = geometryHandler;
    }

    @Inject
    public void setSweHelper(SweHelper sweHelper) {
        this.sweHelper = sweHelper;
    }

    @Inject
    public void setSosHelper(SosHelper sosHelper) {
        this.sosHelper = sosHelper;
    }

    public boolean isIncludeChildObservableProperties() {
        return includeChildObservableProperties;
    }

    @Setting(AbstractRequestOperator.EXPOSE_CHILD_OBSERVABLE_PROPERTIES)
    public void setIncludeChildObservableProperties(boolean include) {
        this.includeChildObservableProperties = include;
    }


    public boolean isStaSupportsUrls() {
        return staSupportsUrls;
    }

    @Setting(SosSettings.STA_SUPPORTS_URLS)
    public void setStaSupportsUrls(boolean staSupportsUrls) {
        this.staSupportsUrls = staSupportsUrls;
    }

    @Inject
    public void setFeatureQueryHandler(FeatureQueryHandler featureQueryHandler) {
        this.featureQueryHandler = featureQueryHandler;
    }

    public AbstractSeriesDAO getSeriesDAO() {
        if (HibernateHelper.isEntitySupported(EReportingSamplingPointEntity.class)) {
            return new EReportingSeriesDAO(this);
        }
        return new SeriesDAO(this);
    }

    public boolean isSeriesDAO() {
        if (HibernateHelper.isEntitySupported(EReportingSamplingPointEntity.class)) {
            return true;
        } else {
            return HibernateHelper.isEntitySupported(DataEntity.class);
        }
    }


    /**
     * Get the currently supported Hibernate Observation data access implementation
     *
     * @return Currently supported Hibernate Observation data access implementation
     *
     * @throws OwsExceptionReport If no Hibernate Observation data access is supported
     */
    public AbstractSeriesObservationDAO getObservationDAO() {
        if (HibernateHelper.isEntitySupported(EReportingSamplingPointEntity.class)) {
            return new EReportingObservationDAO(this.verificationFlags, this.validityFlags, this);
        }
        return new SeriesObservationDAO(this);
    }

    public AbstractObservationTimeDAO getObservationTimeDAO() {
        if (HibernateHelper.isEntitySupported(EReportingSamplingPointEntity.class)) {
            return new EReportingObservationTimeDAO();
        }
        return new SeriesObservationTimeDAO();
    }

    public AbstractSeriesValueDAO getValueDAO() {
        if (HibernateHelper.isEntitySupported(EReportingSamplingPointEntity.class)) {
            return new EReportingValueDAO(this.verificationFlags, this.validityFlags, this);
        }
        return new SeriesValueDAO(this);
    }

    public AbstractSeriesValueTimeDAO getValueTimeDAO() {
        if (HibernateHelper.isEntitySupported(EReportingSamplingPointEntity.class)) {
            return new EReportingValueTimeDAO(this.verificationFlags, this.validityFlags, this);
        }
        return new SeriesValueTimeDAO(this);
    }

    public AbstractFeatureOfInterestDAO getFeatureDAO() throws CodedException {
        return getFeatureOfInterestDAO();
    }

    public ProcedureDAO getProcedureDAO() {
        return new ProcedureDAO(this);
    }

    public CategoryDAO getCategoryDAO() {
        return new CategoryDAO(this);
    }

    public PlatformDAO getPlatformDAO() {
        return new PlatformDAO(this);
    }

    public ObservablePropertyDAO getObservablePropertyDAO() {
        return new ObservablePropertyDAO(this);
    }

    public FeatureOfInterestDAO getFeatureOfInterestDAO() {
        return new FeatureOfInterestDAO(this);
    }

    public ProcedureHistoryDAO getProcedureHistoryDAO() {
        return new ProcedureHistoryDAO(this);
    }

    public RelatedFeatureDAO getRelatedFeatureDAO() {
        return new RelatedFeatureDAO(this);
    }

    public UnitDAO getUnitDAO() {
        return new UnitDAO();
    }

    public ResultTemplateDAO getResultTemplateDAO() {
        return new ResultTemplateDAO(getEncoderRepository(), getXmlOptionsHelper(), getDecoderRepository());
    }

    public CodespaceDAO getCodespaceDAO() {
        return new CodespaceDAO();
    }

    public FormatDAO getObservationTypeDAO() {
        return new FormatDAO();
    }

    public FormatDAO getFeatureTypeDAO() {
        return new FormatDAO();
    }

    public OfferingDAO getOfferingDAO() {
        return new OfferingDAO(this);
    }

    public ParameterDAO getParameterDAO() {
        return new ParameterDAO();
    }

    public FormatDAO getProcedureDescriptionFormatDAO() {
        return new FormatDAO();
    }

    public VerticalMetadataDAO getVerticalMetadataDAO() {
        return new VerticalMetadataDAO(this);
    }

    public I18NDAORepository getI18NDAORepository() {
        return i18NDAORepository;
    }

    public GeometryHandler getGeometryHandler() {
        return geometryHandler;
    }

    public SweHelper getSweHelper() {
        return sweHelper;
    }

    public SosHelper getSosHelper() {
        return sosHelper;
    }

    public FeatureQueryHandler getFeatureQueryHandler() {
        return featureQueryHandler;
    }

    public String getServiceURL() {
        return getSosHelper().getServiceURL();
    }

    public XmlOptionsHelper getXmlOptionsHelper() {
        return xmlOptionsHelper;
    }

    public DecoderRepository getDecoderRepository() {
        return decoderRepository;
    }

    public EncoderRepository getEncoderRepository() {
        return encoderRepository;
    }

}
