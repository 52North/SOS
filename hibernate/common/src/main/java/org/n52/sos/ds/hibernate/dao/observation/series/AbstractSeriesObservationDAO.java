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
package org.n52.sos.ds.hibernate.dao.observation.series;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.locationtech.jts.geom.Geometry;
import org.n52.series.db.beans.AbstractFeatureEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.observation.AbstractObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.ObservationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class AbstractSeriesObservationDAO extends AbstractObservationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSeriesObservationDAO.class);

    public AbstractSeriesObservationDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    @Override
    protected DatasetEntity addObservationContextToObservation(DatasetEntity dataset, ObservationContext ctx, DataEntity<?> observation,
            Session session) throws OwsExceptionReport {
        if (dataset == null) {
            AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
            dataset = seriesDAO.getOrInsertSeries(ctx, observation, session);
        }
        observation.setDataset(dataset);

        OfferingDAO offeringDAO = getDaoFactory().getOfferingDAO();
        offeringDAO.updateOfferingMetadata(dataset.getOffering(), observation, session);
        return dataset;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public DataEntity<?> getExampleObservationFor(String procedure, String observableProperty, Session session) {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getObservationFactory().observationClass());
        Root root = query.from(getObservationFactory().observationClass());
        Join<DataEntity, DatasetEntity> dataset = root.join(DataEntity.PROPERTY_DATASET);
        List<Predicate> predicates = defaultObservationPredicates(cb, root);
        predicates.add(cb.equal(dataset.get(DatasetEntity.PROPERTY_PUBLISHED), true));
        predicates.add(seriesDAO.procedurePredicate(cb, dataset, procedure));
        predicates.add(seriesDAO.observablePropertyPredicate(cb, dataset, observableProperty));
        query.where(predicates.toArray(new Predicate[0]));
        List<DataEntity<?>> examples = session.createQuery(query).setMaxResults(1).list();
        DataEntity<?> example = examples.isEmpty() ? null : examples.get(0);
        if (example == null) {
            LOGGER.debug(
                    "Could not receive example observation from database for procedure '{}' observing property '{}'.",
                    procedure, observableProperty);
        }
        return example;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<Geometry> getSamplingGeometries(String feature, Session session) throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Geometry> query = cb.createQuery(Geometry.class);
        Root root = query.from(getObservationFactory().temporalReferencedClass());
        Join dataset = root.join(DataEntity.PROPERTY_DATASET);
        Join datasetFeature = dataset.join(DatasetEntity.PROPERTY_FEATURE);
        List<Predicate> predicates = defaultObservationPredicates(cb, root);
        predicates.add(cb.equal(datasetFeature.get(AbstractFeatureEntity.IDENTIFIER), feature));
        predicates.add(cb.isNotNull(root.get(DataEntity.PROPERTY_GEOMETRY_ENTITY)));
        query.select(root.get(DataEntity.PROPERTY_GEOMETRY_ENTITY)).where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.asc(root.get(DataEntity.PROPERTY_SAMPLING_TIME_START)));
        return session.createQuery(query).list();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Long getSamplingGeometriesCount(String feature, Session session) throws OwsExceptionReport {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root root = query.from(getObservationFactory().temporalReferencedClass());
        Join dataset = root.join(DataEntity.PROPERTY_DATASET);
        Join datasetFeature = dataset.join(DatasetEntity.PROPERTY_FEATURE);
        List<Predicate> predicates = defaultObservationPredicates(cb, root);
        predicates.add(cb.equal(datasetFeature.get(AbstractFeatureEntity.IDENTIFIER), feature));
        predicates.add(cb.isNotNull(root.get(DataEntity.PROPERTY_GEOMETRY_ENTITY)));
        query.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));
        return session.createQuery(query).uniqueResult();
    }

    /**
     * Get the first not deleted observation for the {@link DatasetEntity}
     *
     * @param series
     *            Series to get observation for
     * @param session
     *            Hibernate session
     * @return First not deleted observation
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public DataEntity<?> getFirstObservationFor(DatasetEntity series, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getObservationFactory().observationClass());
        Root root = query.from(getObservationFactory().observationClass());
        List<Predicate> predicates = defaultObservationPredicates(cb, root);
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_DATASET_ID), series.getId()));
        query.where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get(DataEntity.PROPERTY_SAMPLING_TIME_START)));
        List<DataEntity> results = session.createQuery(query).setMaxResults(1).list();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Get the last not deleted observation for the {@link DatasetEntity}
     *
     * @param series
     *            Series to get observation for
     * @param session
     *            Hibernate session
     * @return Last not deleted observation
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public DataEntity<?> getLastObservationFor(DatasetEntity series, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getObservationFactory().observationClass());
        Root root = query.from(getObservationFactory().observationClass());
        List<Predicate> predicates = defaultObservationPredicates(cb, root);
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_DATASET_ID), series.getId()));
        query.where(predicates.toArray(new Predicate[0])).orderBy(cb.desc(root.get(DataEntity.PROPERTY_SAMPLING_TIME_END)));
        List<DataEntity> results = session.createQuery(query).setMaxResults(1).list();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<String> getOfferingsForSeries(DatasetEntity series, Session session) {
        return Lists.newArrayList(series.getOffering().getIdentifier());
    }

    public Map<Long, SeriesTimeExtrema> getMinMaxSeriesTimes(Set<DatasetEntity> serieses, Session session) {
        return getMinMaxSeriesTimesById(serieses.stream().map(DatasetEntity::getId).collect(Collectors.toSet()),
                session);
    }

    @SuppressWarnings("rawtypes")
    public Map<Long, SeriesTimeExtrema> getMinMaxSeriesTimesById(Set<Long> serieses, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        Root root = query.from(getObservationFactory().temporalReferencedClass());
        List<Predicate> predicates = defaultObservationPredicates(cb, root);
        predicates.add(root.get(DataEntity.PROPERTY_DATASET_ID).in(serieses));
        query.multiselect(root.get(DataEntity.PROPERTY_DATASET_ID),
                cb.least(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_START)),
                cb.greatest(root.<Date>get(DataEntity.PROPERTY_SAMPLING_TIME_END)),
                cb.least(root.<Date>get(DataEntity.PROPERTY_RESULT_TIME)),
                cb.greatest(root.<Date>get(DataEntity.PROPERTY_RESULT_TIME)))
                .where(predicates.toArray(new Predicate[0]))
                .groupBy(root.get(DataEntity.PROPERTY_DATASET_ID));

        Map<Long, SeriesTimeExtrema> map = Maps.newHashMap();
        for (Object[] tuple : session.createQuery(query).list()) {
            SeriesTimeExtrema result = toSeriesTimeExtrema(tuple);
            if (result.isSetSeries()) {
                map.put(result.getSeries(), result);
            }
        }
        return map;
    }

    private SeriesTimeExtrema toSeriesTimeExtrema(Object[] tuple) {
        SeriesTimeExtrema seriesTimeExtrema = new SeriesTimeExtrema();
        if (tuple != null) {
            seriesTimeExtrema.setSeries((Long) tuple[0]);
            seriesTimeExtrema.setMinPhenomenonTime(DateTimeHelper.makeDateTime(tuple[1]));
            seriesTimeExtrema.setMaxPhenomenonTime(DateTimeHelper.makeDateTime(tuple[2]));
            if (tuple.length == 5) {
                seriesTimeExtrema.setMinResultTime(DateTimeHelper.makeDateTime(tuple[3]));
                seriesTimeExtrema.setMaxResultTime(DateTimeHelper.makeDateTime(tuple[4]));
            }
        }
        return seriesTimeExtrema;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public DataEntity<?> getMinObservation(DatasetEntity series, org.joda.time.DateTime time, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getObservationFactory().observationClass());
        Root root = query.from(getObservationFactory().observationClass());
        List<Predicate> predicates = defaultObservationPredicates(cb, root);
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_DATASET_ID), series.getId()));
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_SAMPLING_TIME_START), time.toDate()));
        query.where(predicates.toArray(new Predicate[0]));
        return (DataEntity<?>) session.createQuery(query).uniqueResult();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public DataEntity<?> getMaxObservation(DatasetEntity series, org.joda.time.DateTime time, Session session) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery query = cb.createQuery(getObservationFactory().observationClass());
        Root root = query.from(getObservationFactory().observationClass());
        List<Predicate> predicates = defaultObservationPredicates(cb, root);
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_DATASET_ID), series.getId()));
        predicates.add(cb.equal(root.get(DataEntity.PROPERTY_SAMPLING_TIME_END), time.toDate()));
        query.where(predicates.toArray(new Predicate[0]));
        return (DataEntity<?>) session.createQuery(query).uniqueResult();
    }

}
