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
package org.n52.sos.ds.hibernate.dao.observation.ereporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.data.Data;
import org.n52.series.db.beans.dataset.ProfileDataset;
import org.n52.series.db.beans.ereporting.EReportingAssessmentTypeEntity;
import org.n52.series.db.beans.ereporting.EReportingBlobDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingBooleanDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingCategoryDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingComplexDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingCountDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingDataArrayDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingGeometryDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingNotInitializedDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingProfileDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingQuantityDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingReferencedDatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingSamplingPointEntity;
import org.n52.series.db.beans.ereporting.EReportingTextDatasetEntity;
import org.n52.shetland.aqd.AqdConstants;
import org.n52.shetland.aqd.ReportObligationType;
import org.n52.shetland.aqd.ReportObligations;
import org.n52.shetland.ogc.ows.exception.OptionNotSupportedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.DatasetFactory;
import org.n52.sos.ds.hibernate.util.QueryHelper;

public class EReportingSeriesDAO extends AbstractSeriesDAO {

    public EReportingSeriesDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    public Class<?> getSeriesClass() {
        return EReportingDatasetEntity.class;
    }

    @Override
    public Class<?> getNotInitializedDatasetClass() {
        return EReportingNotInitializedDatasetEntity.class;
    }

    @Override
    public List<DatasetEntity> getSeries(GetObservationRequest request, Collection<String> features, Session session) throws OwsExceptionReport {
        List<DatasetEntity> series = new ArrayList<>();
        if (CollectionHelper.isNotEmpty(features)) {
            for (List<String> ids : QueryHelper.getListsForIdentifiers(features)) {
                series.addAll(getSeriesSet(request, ids, session));
            }
        } else {
            series.addAll(getSeriesSet(request, features, session));
        }
        return series;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DatasetEntity> getSeries(GetObservationByIdRequest request, Session session) throws OwsExceptionReport {
        return getSeriesCriteria(request, session).list();
    }

    @Override
    public List<DatasetEntity> getSeries(GetDataAvailabilityRequest request, Session session)
            throws OwsExceptionReport {
        return new ArrayList<>(getSeriesCriteria(request, session));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DatasetEntity> getSeries(GetResultRequest request, Collection<String> featureIdentifiers,
            Session session) throws OwsExceptionReport {
        return getSeriesCriteria(request, featureIdentifiers, session).list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DatasetEntity> getSeries(String observedProperty, Collection<String> features, Session session) {
        if (CollectionHelper.isNotEmpty(features)) {
            List<DatasetEntity> series = new ArrayList<>();
            for (List<String> ids : QueryHelper.getListsForIdentifiers(features)) {
                series.addAll(getSeriesCriteria(observedProperty, ids, session).list());
            }
            return series;
        } else {
            return getSeriesCriteria(observedProperty, features, session).list();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DatasetEntity> getSeries(String procedure, String observedProperty, String offering, Collection<String> features, Session session) {
        if (CollectionHelper.isNotEmpty(features)) {
            List<DatasetEntity> series = new ArrayList<>();
            for (List<String> ids : QueryHelper.getListsForIdentifiers(features)) {
                series.addAll(getSeriesCriteria(procedure, observedProperty, offering, ids, session).list());
            }
            return series;
        } else {
            return getSeriesCriteria(procedure, observedProperty, offering, features, session).list();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DatasetEntity> getSeries(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Session session) {
        if (CollectionHelper.isNotEmpty(features)) {
            List<DatasetEntity> series = new ArrayList<>();
            for (List<String> ids : QueryHelper.getListsForIdentifiers(features)) {
                series.addAll(getSeriesCriteria(procedures, observedProperties, ids, session).list());
            }
            return series;
        } else {
            return getSeriesCriteria(procedures, observedProperties, features, session).list();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DatasetEntity> getSeries(Collection<String> procedures, Collection<String> observedProperties,
            Collection<String> features, Collection<String> offerings, Session session) {
        if (CollectionHelper.isNotEmpty(features)) {
            List<DatasetEntity> series = new ArrayList<>();
            for (List<String> ids : QueryHelper.getListsForIdentifiers(features)) {
                series.addAll(getSeriesCriteria(procedures, observedProperties, ids, offerings, session).list());
            }
            return series;
        } else {
            return getSeriesCriteria(procedures, observedProperties, features, offerings, session).list();
        }
    }

    @Override
    public EReportingDatasetEntity getSeriesFor(String procedure, String observableProperty, String featureOfInterest,
            Session session) {
        return (EReportingDatasetEntity) getSeriesCriteriaFor(procedure, observableProperty, featureOfInterest, session).uniqueResult();
    }

    @Override
    public List<DatasetEntity> getSeries(String procedure, String observableProperty, Session session) {
        return (List<DatasetEntity>) getSeriesCriteriaFor(procedure, observableProperty, session).list();
    }

    @Override
    public EReportingDatasetEntity getOrInsertSeries(ObservationContext identifiers, Data<?> observation, Session session) throws OwsExceptionReport {
        return (EReportingDatasetEntity) super.getOrInsert(identifiers, observation, session);
    }

    /**
     * Add EReportingSamplingPoint restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param samplingPoint
     *            EReportingSamplingPoint identifier to add
     */
    public void addEReportingSamplingPointToCriteria(Criteria c, String samplingPoint) {
        c.createCriteria(EReportingDatasetEntity.SAMPLING_POINT).add(Restrictions.eq(EReportingSamplingPointEntity.IDENTIFIER, samplingPoint));

    }

    /**
     * Add EReportingSamplingPoint restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param samplingPoint
     *            EReportingSamplingPoint to add
     */
    public void addEReportingSamplingPointToCriteria(Criteria c, EReportingSamplingPointEntity samplingPoint) {
        c.add(Restrictions.eq(EReportingDatasetEntity.SAMPLING_POINT, samplingPoint));
    }

    /**
     * Add EReportingSamplingPoint restriction to Hibernate Criteria
     *
     * @param c
     *            Hibernate Criteria to add restriction
     * @param samplingPoints
     *            EReportingSamplingPoint identifiers to add
     */
    public void addEReportingSamplingPointToCriteria(Criteria c, Collection<String> samplingPoints) {
        c.createCriteria(EReportingDatasetEntity.SAMPLING_POINT).add(Restrictions.in(EReportingSamplingPointEntity.IDENTIFIER, samplingPoints));
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request) throws OwsExceptionReport {
        if (request.isSetResponseFormat() && AqdConstants.NS_AQD.equals(request.getResponseFormat())) {
            ReportObligationType flow = ReportObligations.getFlow(request.getExtensions());
            if (null == flow) {
                throw new OptionNotSupportedException().withMessage("The requested e-Reporting flow %s is not supported!", flow.name());
            } else {
                switch (flow) {
                    case E1A:
                    case E2A:
                        addAssessmentType(c, AqdConstants.AssessmentType.Fixed.name());
                        break;
                    case E1B:
                        addAssessmentType(c, AqdConstants.AssessmentType.Model.name());
                        break;
                    default:
                        throw new OptionNotSupportedException().withMessage("The requested e-Reporting flow %s is not supported!", flow.name());
                }
            }
        }
    }

    @Override
    public ObservationFactory getObservationFactory() {
        return EReportingObservationFactory.getInstance();
    }

    private void addAssessmentType(Criteria c, String assessmentType) {
        c.createCriteria(EReportingDatasetEntity.SAMPLING_POINT).createCriteria(EReportingSamplingPointEntity.ASSESSMENTTYPE).
        add(Restrictions.ilike(EReportingAssessmentTypeEntity.ASSESSMENT_TYPE, assessmentType));
    }


    @Override
    public DatasetFactory getDatasetFactory() {
        return EReportingDatasetFactory.getInstance();
    }

    private static class EReportingDatasetFactory
            extends
            DatasetFactory {

        protected EReportingDatasetFactory() {
        }

        @Override
        public Class<? extends EReportingDatasetEntity> datasetClass() {
            return EReportingDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingBlobDatasetEntity> blobClass() {
            return EReportingBlobDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingBooleanDatasetEntity> truthClass() {
            return EReportingBooleanDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingCategoryDatasetEntity> categoryClass() {
            return EReportingCategoryDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingCountDatasetEntity> countClass() {
            return EReportingCountDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingGeometryDatasetEntity> geometryClass() {
            return EReportingGeometryDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingQuantityDatasetEntity> numericClass() {
            return EReportingQuantityDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingDataArrayDatasetEntity> sweDataArrayClass() {
            return EReportingDataArrayDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingTextDatasetEntity> textClass() {
            return EReportingTextDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingComplexDatasetEntity> complexClass() {
            return EReportingComplexDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingProfileDatasetEntity> profileClass() {
            return EReportingProfileDatasetEntity.class;
        }

        @Override
        public Class<? extends ProfileDataset> textProfileClass() {
            return profileClass();
        }

        @Override
        public Class<? extends ProfileDataset> categoryProfileClass() {
            return  profileClass();
        }

        @Override
        public Class<? extends ProfileDataset> quantityProfileClass() {
            return profileClass();
        }

        @Override
        public Class<? extends EReportingReferencedDatasetEntity> referenceClass() {
            return EReportingReferencedDatasetEntity.class;
        }

        @Override
        public Class<? extends EReportingNotInitializedDatasetEntity> notInitializedClass() {
            return EReportingNotInitializedDatasetEntity.class;
        }

        public static EReportingDatasetFactory getInstance() {
            return Holder.INSTANCE;
        }

        private static class Holder {
            private static final EReportingDatasetFactory INSTANCE = new EReportingDatasetFactory();

            private Holder() {
            }
        }

    }

}
