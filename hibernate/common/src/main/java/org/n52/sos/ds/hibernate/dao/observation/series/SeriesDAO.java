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
package org.n52.sos.ds.hibernate.dao.observation.series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.n52.series.db.beans.BlobDatasetEntity;
import org.n52.series.db.beans.BooleanDatasetEntity;
import org.n52.series.db.beans.CategoryDatasetEntity;
import org.n52.series.db.beans.CategoryProfileDatasetEntity;
import org.n52.series.db.beans.ComplexDatasetEntity;
import org.n52.series.db.beans.CountDatasetEntity;
import org.n52.series.db.beans.DataArrayDatasetEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.GeometryDatasetEntity;
import org.n52.series.db.beans.NotInitializedDatasetEntity;
import org.n52.series.db.beans.ProfileDatasetEntity;
import org.n52.series.db.beans.QuantityDatasetEntity;
import org.n52.series.db.beans.QuantityProfileDatasetEntity;
import org.n52.series.db.beans.ReferencedDatasetEntity;
import org.n52.series.db.beans.TextDatasetEntity;
import org.n52.series.db.beans.TextProfileDatasetEntity;
import org.n52.series.db.beans.data.Data;
import org.n52.series.db.beans.dataset.ProfileDataset;
import org.n52.series.db.beans.dataset.QuantityDataset;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.shetland.ogc.sos.request.GetObservationByIdRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.util.QueryHelper;

/**
 * Hibernate data access class for series
 *
 * @since 4.0.0
 *
 */
public class SeriesDAO extends AbstractSeriesDAO {

    public SeriesDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    public List<DatasetEntity> getSeries(GetObservationRequest request, Collection<String> features, Session session) throws
            OwsExceptionReport {
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
    public DatasetEntity getSeriesFor(String procedure, String observableProperty, String featureOfInterest, Session session) {
        return (DatasetEntity) getSeriesCriteriaFor(procedure, observableProperty, featureOfInterest, session).uniqueResult();
    }

    public List<DatasetEntity> getSeries(String procedure, String observableProperty, Session session) {
        return (List<DatasetEntity>) getSeriesCriteriaFor(procedure, observableProperty, session).list();
    }

    @Override
    public DatasetEntity getOrInsertSeries(ObservationContext ctx, Data<?> observation, final Session session) throws OwsExceptionReport {
        return getOrInsert(ctx, observation, session);
    }

    @Override
    public Class<?> getSeriesClass() {
        return DatasetEntity.class;
    }

    @Override
    public Class<?> getNotInitializedDatasetClass() {
        return NotInitializedDatasetEntity.class;
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request) {
        // nothing to add
    }

    public ObservationFactory getObservationFactory() {
        return SeriesObservationFactory.getInstance();
    }

    @Override
    public DatasetFactory getDatasetFactory() {
        return DefaultDatasetFactory.getInstance();
    }

    private static class DefaultDatasetFactory
            extends
            DatasetFactory {

        protected DefaultDatasetFactory() {
        }

        @Override
        public Class<? extends DatasetEntity> datasetClass() {
            return DatasetEntity.class;
        }

        @Override
        public Class<? extends BlobDatasetEntity> blobClass() {
            return BlobDatasetEntity.class;
        }

        @Override
        public Class<? extends BooleanDatasetEntity> truthClass() {
            return BooleanDatasetEntity.class;
        }

        @Override
        public Class<? extends CategoryDatasetEntity> categoryClass() {
            return CategoryDatasetEntity.class;
        }

        @Override
        public Class<? extends CountDatasetEntity> countClass() {
            return CountDatasetEntity.class;
        }

        @Override
        public Class<? extends GeometryDatasetEntity> geometryClass() {
            return GeometryDatasetEntity.class;
        }

        @Override
        public Class<? extends QuantityDataset> numericClass() {
            return QuantityDatasetEntity.class;
        }

        @Override
        public Class<? extends DataArrayDatasetEntity> sweDataArrayClass() {
            return DataArrayDatasetEntity.class;
        }

        @Override
        public Class<? extends TextDatasetEntity> textClass() {
            return TextDatasetEntity.class;
        }

        @Override
        public Class<? extends ComplexDatasetEntity> complexClass() {
            return ComplexDatasetEntity.class;
        }

        @Override
        public Class<? extends ProfileDatasetEntity> profileClass() {
            return ProfileDatasetEntity.class;
        }

        @Override
        public Class<? extends ProfileDataset> textProfileClass() {
            return TextProfileDatasetEntity.class;
        }

        @Override
        public Class<? extends ProfileDataset> categoryProfileClass() {
            return CategoryProfileDatasetEntity.class;
        }

        @Override
        public Class<? extends ProfileDataset> quantityProfileClass() {
            return QuantityProfileDatasetEntity.class;
        }

        @Override
        public Class<? extends ReferencedDatasetEntity> referenceClass() {
            return ReferencedDatasetEntity.class;
        }

        @Override
        public Class<? extends NotInitializedDatasetEntity> notInitializedClass() {
            return NotInitializedDatasetEntity.class;
        }

        public static DefaultDatasetFactory getInstance() {
            return Holder.INSTANCE;
        }

        private static class Holder {
            private static final DefaultDatasetFactory INSTANCE = new DefaultDatasetFactory();

            private Holder() {
            }
        }

    }

}
