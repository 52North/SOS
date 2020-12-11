/*
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
package org.n52.sos.ds.hibernate.dao.observation.ereporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ereporting.EReportingAssessmentTypeEntity;
import org.n52.series.db.beans.ereporting.EReportingSamplingPointEntity;
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
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingDaoHelper;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.DatasetFactory;
import org.n52.sos.ds.hibernate.util.QueryHelper;

public class EReportingSeriesDAO extends AbstractSeriesDAO implements EReportingDaoHelper {

    public EReportingSeriesDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    public List<DatasetEntity> getSeries(GetObservationRequest request, Collection<String> features, Session session)
            throws OwsExceptionReport {
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
    public List<DatasetEntity> getSeries(GetObservationByIdRequest request, Session session)
            throws OwsExceptionReport {
        return getSeriesCriteria(request.getObservationIdentifier(), session).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<DatasetEntity> getSeries(Collection<String> identifiers, Session session) throws OwsExceptionReport {
        return getSeriesCriteria(identifiers, session).list();
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
    public List<DatasetEntity> getSeries(String procedure, String observedProperty, String offering,
            Collection<String> features, Session session) {
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

    @SuppressWarnings("unchecked")
    @Override
    public List<DatasetEntity> getSeries(String procedure, String observableProperty, Session session) {
        return (List<DatasetEntity>) getSeriesCriteriaFor(procedure, observableProperty, session).list();
    }

    @Override
    public DatasetEntity getSeriesFor(String procedure, String observableProperty, String featureOfInterest,
            Session session) {
        return (DatasetEntity) getSeriesCriteriaFor(procedure, observableProperty, featureOfInterest, session)
                .uniqueResult();
    }

    @Override
    public DatasetEntity getOrInsertSeries(ObservationContext identifiers, DataEntity<?> observation, Session session)
            throws OwsExceptionReport {
        return (DatasetEntity) super.getOrInsert(identifiers, observation, session);
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
        c.createCriteria(getSamplingPointAssociationPath())
                .add(Restrictions.eq(EReportingSamplingPointEntity.IDENTIFIER, samplingPoint));

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
        c.add(Restrictions.eq(getSamplingPointAssociationPath(), samplingPoint));
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
        c.createCriteria(getSamplingPointAssociationPath())
                .add(Restrictions.in(EReportingSamplingPointEntity.IDENTIFIER, samplingPoints));
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request) throws OwsExceptionReport {
        if (request.isSetResponseFormat() && AqdConstants.NS_AQD.equals(request.getResponseFormat())) {
            ReportObligationType flow = ReportObligations.getFlow(request.getExtensions());
            if (null == flow) {
                throw new OptionNotSupportedException()
                        .withMessage("The request does not conatain an e-Reporting flow parameter!");
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
                        throw new OptionNotSupportedException()
                            .withMessage("The requested e-Reporting flow %s is not supported!", flow.name());
                }
            }
        }
    }

    @Override
    public ObservationFactory getObservationFactory() {
        return EReportingObservationFactory.getInstance();
    }

    private void addAssessmentType(Criteria c, String assessmentType) {
        c.createCriteria(getSamplingPointAssociationPath())
                .createCriteria(EReportingSamplingPointEntity.ASSESSMENTTYPE)
                .add(Restrictions.ilike(EReportingAssessmentTypeEntity.ASSESSMENT_TYPE, assessmentType));
    }

    @Override
    public DatasetFactory getDatasetFactory() {
        return EReportingDatasetFactory.getInstance();
    }

    @Override
    public Set<Integer> getVerificationFlags() {
        return Collections.emptySet();
    }

    @Override
    public Set<Integer> getValidityFlags() {
        return Collections.emptySet();
    }

    private static class EReportingDatasetFactory extends DatasetFactory {

        protected EReportingDatasetFactory() {
        }

        @Override
        public Class<? extends DatasetEntity> datasetClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> blobClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> truthClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> categoryClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> countClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> geometryClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> numericClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> sweDataArrayClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> textClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> complexClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> profileClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> textProfileClass() {
            return profileClass();
        }

        @Override
        public Class<? extends DatasetEntity> categoryProfileClass() {
            return profileClass();
        }

        @Override
        public Class<? extends DatasetEntity> quantityProfileClass() {
            return profileClass();
        }

        @Override
        public Class<? extends DatasetEntity> trajectoryClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> textTrajectoryClass() {
            return trajectoryClass();
        }

        @Override
        public Class<? extends DatasetEntity> categoryTrajectoryClass() {
            return trajectoryClass();
        }

        @Override
        public Class<? extends DatasetEntity> quantityTrajectoryClass() {
            return trajectoryClass();
        }

        @Override
        public Class<? extends DatasetEntity> referenceClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends DatasetEntity> notInitializedClass() {
            return DatasetEntity.class;
        }

        public static EReportingDatasetFactory getInstance() {
            return Holder.INSTANCE;
        }

        private static final class Holder {
            private static final EReportingDatasetFactory INSTANCE = new EReportingDatasetFactory();

            private Holder() {
            }
        }

    }

}
