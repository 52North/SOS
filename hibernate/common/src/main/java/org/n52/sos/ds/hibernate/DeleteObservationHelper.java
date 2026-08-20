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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.jfree.data.general.Dataset;
import org.n52.iceland.convert.ConverterException;
import org.n52.series.db.beans.AbstractDatasetEntity;
import org.n52.series.db.beans.CompositeDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetAggregationEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.GeometryEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.dataset.ValueType;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationRequest;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationResponse;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesTimeExtrema;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.utils.HibernateUnproxy;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.slf4j.Logger;

import com.google.common.base.Joiner;

public interface DeleteObservationHelper extends HibernateUnproxy {

    String DELETE_PARAMETER = "delete ";

    String FROM_PARAMETER = " from ";

    String EQUAL_PARAMETER = " = :";

    String IN_PARAMETER = " in :";

    String WHERE_PARAMETER = " where ";

    String ERROR_LOG = "Error while updating deleted observation flag data!";

    DaoFactory getDaoFactory();

    Logger getLogger();

    boolean isDeletePhysically();

    default void deleteObservation(Collection<DatasetEntity> datasets, Collection<TemporalFilter> filters,
            Session session) throws OwsExceptionReport {
        boolean temporalFilters = filters != null && !filters.isEmpty();
        Set<Long> modifiedDatasets = new HashSet<>();
        for (Long s : getSeriesInlcudeChildObs(datasets.stream()
                .map(DatasetEntity::getId)
                .collect(Collectors.toSet()), session)) {
            int executeUpdate = markDeleted(session, s, filters, temporalFilters);
            session.flush();
            if (executeUpdate > 0) {
                modifiedDatasets.add(s);
            }
        }
        if (!modifiedDatasets.isEmpty()) {
            checkSeriesForFirstLatest(modifiedDatasets, session);
            if (isDeletePhysically()) {
                // TODO select all parent ids -> delete childs -> delete parents
                Set<Long> parents = getParents(modifiedDatasets, filters, temporalFilters, session);
                if (!parents.isEmpty()) {
                    deleteDeletedChildObservations(parents, session);
                }
                deleteDeletedObservations(modifiedDatasets, filters, temporalFilters, session);
            }
        }
    }

    default void deleteObservation(DeleteObservationRequest request, Collection<TemporalFilter> filters,
            Session session) throws OwsExceptionReport {
        deleteObservation(getDaoFactory().getSeriesDAO()
                .getSeries(request.getProcedures(), request.getObservedProperties(), request.getFeatureIdentifiers(),
                        request.getOfferings(), session),
                filters, session);
    }

    default Set<Long> getSeriesInlcudeChildObs(Collection<Long> datasets, Session session) {
        StringBuilder builder = new StringBuilder();
        builder.append("select distinct o2.")
                .append(DataEntity.PROPERTY_DATASET_ID)
                .append(FROM_PARAMETER);
        builder.append(getDaoFactory().getObservationDAO()
                .getObservationFactory()
                .observationClass()
                .getSimpleName())
                .append(" o ");
        builder.append(" JOIN ")
                .append(getDaoFactory().getObservationDAO()
                        .getObservationFactory()
                        .observationClass()
                        .getSimpleName())
                .append(" o2 ");
        builder.append(" ON o.")
                .append(DataEntity.PROPERTY_ID)
                .append(" = o2.")
                .append(DataEntity.PROPERTY_PARENT);
        builder.append(WHERE_PARAMETER)
                .append(" o.")
                .append(DataEntity.PROPERTY_DATASET_ID)
                .append(IN_PARAMETER)
                .append(DataEntity.PROPERTY_DATASET);
        Query<?> q = session.createQuery(builder.toString());
        q.setParameter(DataEntity.PROPERTY_DATASET, datasets);
        List<Long> list = (List<Long>) q.list();
        if (list != null && !list.isEmpty()) {
            datasets.addAll(list);
        }
        return datasets instanceof Set ? (Set<Long>) datasets : new LinkedHashSet<>(datasets);
    }

    /**
     * Sets {@code deleted = true} on every observation of {@code dataset} that
     * matches {@code filters} (if any).
     *
     * @return the number of rows updated
     */
    default int markDeleted(Session session, Long dataset, Collection<TemporalFilter> filters,
            boolean temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        return markDeleted(session,
                getDaoFactory().getObservationDAO()
                        .getObservationFactory()
                        .observationClass(),
                dataset, filters, temporalFilters);
    }

    private <T extends DataEntity> int markDeleted(Session session, Class<T> observationClass, Long dataset,
            Collection<TemporalFilter> filters, boolean temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(observationClass);
        Root<T> root = update.from(observationClass);
        update.set(DataEntity.PROPERTY_DELETED, true);
        Predicate predicate = cb.equal(root.get(DataEntity.PROPERTY_DATASET_ID), dataset);
        if (temporalFilters) {
            predicate = cb.and(predicate, SosTemporalRestrictions.filter(cb, root, filters));
        }
        update.where(predicate);
        return session.createMutationQuery(update)
                .executeUpdate();
    }

    default Set<Long> getParents(Collection<Long> modifiedDatasets, Collection<TemporalFilter> filters,
            boolean temporalFilters, Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        return getParents(session,
                getDaoFactory().getObservationDAO()
                        .getObservationFactory()
                        .observationClass(),
                modifiedDatasets, filters, temporalFilters);
    }

    private <T extends DataEntity> Set<Long> getParents(Session session, Class<T> observationClass,
            Collection<Long> modifiedDatasets, Collection<TemporalFilter> filters, boolean temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(observationClass);
        Predicate predicate = root.get(DataEntity.PROPERTY_DATASET_ID)
                .in(modifiedDatasets);
        if (temporalFilters) {
            predicate = cb.and(predicate, SosTemporalRestrictions.filter(cb, root, filters));
        }
        query.select(root.get(DataEntity.PROPERTY_ID))
                .distinct(true)
                .where(predicate);
        List<Long> list = session.createQuery(query)
                .list();
        return list != null ? new LinkedHashSet<>(list) : new LinkedHashSet<>();
    }

    default void deleteDeletedChildObservations(Collection<Long> parents, Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Query<?> q = session.createQuery(getDeletChildQueryString(parents));
        q.setParameter(DataEntity.PROPERTY_PARENT, parents);
        int executeUpdate = q.executeUpdate();
        getLogger().debug("{} child observations were physically deleted!", executeUpdate);
        session.flush();

    }

    default void deleteDeletedObservations(Collection<Long> modifiedDatasets, Collection<TemporalFilter> filters,
            boolean temporalFilters, Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        int executeUpdate = deletePhysically(session,
                getDaoFactory().getObservationDAO()
                        .getObservationFactory()
                        .observationClass(),
                modifiedDatasets, filters, temporalFilters);
        getLogger().debug("{} observations were physically deleted!", executeUpdate);
        session.flush();
    }

    private <T extends DataEntity> int deletePhysically(Session session, Class<T> observationClass,
            Collection<Long> modifiedDatasets, Collection<TemporalFilter> filters, boolean temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaDelete<T> delete = cb.createCriteriaDelete(observationClass);
        Root<T> root = delete.from(observationClass);
        Predicate predicate = root.get(DataEntity.PROPERTY_DATASET_ID)
                .in(modifiedDatasets);
        if (temporalFilters) {
            predicate = cb.and(predicate, SosTemporalRestrictions.filter(cb, root, filters));
        }
        delete.where(predicate);
        return session.createMutationQuery(delete)
                .executeUpdate();
    }

    default void deleteDeletedObservations(Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        StringBuilder builder = new StringBuilder();
        builder.append(DELETE_PARAMETER);
        builder.append(getDaoFactory().getObservationDAO()
                .getObservationFactory()
                .observationClass()
                .getSimpleName());
        builder.append(WHERE_PARAMETER)
                .append(DatasetEntity.PROPERTY_DELETED)
                .append(EQUAL_PARAMETER)
                .append(DatasetEntity.PROPERTY_DELETED);
        Query<?> q = session.createQuery(builder.toString());
        q.setParameter(DatasetEntity.PROPERTY_DELETED, true);
        int executeUpdate = q.executeUpdate();
        getLogger().debug("{} deleted observations were physically deleted!", executeUpdate);
        session.flush();
    }

    default String getDeletChildQueryString(Collection<Long> parents)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        StringBuilder builder = new StringBuilder();
        builder.append(DELETE_PARAMETER);
        builder.append(getDaoFactory().getObservationDAO()
                .getObservationFactory()
                .observationClass()
                .getSimpleName());
        builder.append(WHERE_PARAMETER)
                .append(DataEntity.PROPERTY_PARENT)
                .append(IN_PARAMETER)
                .append(DataEntity.PROPERTY_PARENT);
        return builder.toString();
    }

    default void deleteObservationsByIdentifier(DeleteObservationRequest request, DeleteObservationResponse response,
            Session session) throws OwsExceptionReport, ConverterException {
        Set<String> ids = request.getObservationIdentifiers();
        List<DataEntity<?>> observations = getDaoFactory().getObservationDAO()
            .getObservationByIdentifiers(ids, session);
        if (CollectionHelper.isNotEmpty(observations)) {
            Set<DatasetEntity> modifiedDatasets = new HashSet<>();
            for (DataEntity<?> observation : observations) {
                if (DeleteObservationConstants.NS_SOSDO_1_0.equals(request.getResponseFormat())) {
                    response.setObservationId(request.getObservationIdentifiers()
                            .iterator()
                            .next());
                }
                modifiedDatasets.add(observation.getDataset());
                delete(observation, session);
            }
            if (!modifiedDatasets.isEmpty()) {
                checkSeriesForFirstLatest(modifiedDatasets.stream()
                        .map(DatasetEntity::getId)
                        .collect(Collectors.toSet()), session);
            }
        } else {
            if (DeleteObservationConstants.NS_SOSDO_1_0.equals(request.getResponseFormat())) {
                throw new InvalidParameterValueException(DeleteObservationConstants.PARAM_OBSERVATION, Joiner.on(", ")
                        .join(request.getObservationIdentifiers()));
            }
        }
    }

    default void deleteObservationByParameter(DeleteObservationRequest request, DeleteObservationResponse response,
            Session session) throws OwsExceptionReport {
        deleteObservation(request, request.getTemporalFilters(), session);
    }

    default void delete(DataEntity<?> observation, Session session) {
        if (observation != null) {
            if (observation instanceof CompositeDataEntity entity) {
                for (DataEntity<?> o : entity.getValue()) {
                    delete(o, session);
                }
            }
            observation.setDeleted(true);
            session.saveOrUpdate(observation);
            if (isDeletePhysically()) {
                checkForFirstLastReference(observation, session);
                session.delete(observation);
            }
            session.flush();
        }
    }

    default void checkForFirstLastReference(DataEntity<?> observation, Session session) {
        DatasetEntity dataset = observation.getDataset();
        if (dataset.getFirstObservation() != null && dataset.getFirstObservation()
                .getId() != null && observation.getId() != null && dataset.getFirstObservation()
                        .getId()
                        .equals(observation.getId())) {
            dataset.setFirstObservation(null);
        }
        if (dataset.getLastObservation() != null && dataset.getLastObservation()
                .getId() != null && observation.getId() != null && dataset.getLastObservation()
                        .getId()
                        .equals(observation.getId())) {
            dataset.setLastObservation(null);
        }
        session.update(dataset);
        session.flush();

    }

    /**
     * Check if {@link Dataset} should be updated
     *
     * @param datasets
     *            Deleted observation
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    default void checkSeriesForFirstLatest(Collection<Long> datasets, Session session) throws OwsExceptionReport {
        if (!datasets.isEmpty()) {
            AbstractSeriesObservationDAO observationDAO = getDaoFactory().getObservationDAO();
            Map<Long, SeriesTimeExtrema> minMaxTimes = observationDAO.getMinMaxSeriesTimesById(
                    datasets instanceof Set ? (Set<Long>) datasets : new LinkedHashSet<>(datasets), session);
            Set<Long> aggregations = new LinkedHashSet<>();
            for (Long id : datasets) {
                DatasetEntity dataset = session.get(DatasetEntity.class, id);
                if (dataset.isSetAggregation()) {
                    aggregations.add(dataset.getAggregation().getId());
                }
                boolean update = false;
                if (minMaxTimes.containsKey(dataset.getId())) {
                    SeriesTimeExtrema extrema = minMaxTimes.get(dataset.getId());
                    if (!dataset.isSetFirstValueAt() || dataset.isSetFirstValueAt() && !DateTimeHelper
                            .makeDateTime(dataset.getFirstValueAt()).equals(extrema.getMinPhenomenonTime())) {
                        dataset.setFirstValueAt(extrema.getMinPhenomenonTime()
                                .toDate());
                        DataEntity<?> o = unproxy(
                                observationDAO.getMinObservation(dataset, extrema.getMinPhenomenonTime(), session),
                                session);
                        dataset.setFirstObservation(o);
                        if (dataset.getValueType()
                                .equals(ValueType.quantity)) {
                            dataset.setFirstQuantityValue(((QuantityDataEntity) o).getValue());
                        }
                        update = true;
                    }
                    if (!dataset.isSetLastValueAt()
                            || dataset.isSetLastValueAt() && !DateTimeHelper.makeDateTime(dataset.getLastValueAt())
                                    .equals(extrema.getMaxPhenomenonTime())) {
                        dataset.setLastValueAt(extrema.getMaxPhenomenonTime()
                                .toDate());
                        DataEntity<?> o = unproxy(
                                observationDAO.getMaxObservation(dataset, extrema.getMaxPhenomenonTime(), session),
                                session);
                        dataset.setLastObservation(o);
                        if (dataset.getValueType()
                                .equals(ValueType.quantity)) {
                            dataset.setLastQuantityValue(((QuantityDataEntity) o).getValue());

                        }
                        update = true;
                    }
                    if (!dataset.isSetResultTimeStart() || dataset.isSetResultTimeStart() && !DateTimeHelper
                            .makeDateTime(dataset.getResultTimeEnd()).equals(extrema.getMinResultTime())) {
                        dataset.setFirstValueAt(extrema.getMinResultTime()
                                .toDate());
                        update = true;
                    }
                    if (!dataset.isSetResultTimeEnd()
                            || dataset.isSetResultTimeEnd() && !DateTimeHelper.makeDateTime(dataset.getResultTimeEnd())
                                    .equals(extrema.getMaxResultTime())) {
                        dataset.setResultTimeEnd(extrema.getMaxResultTime()
                                .toDate());
                        update = true;
                    }
                } else {
                    dataset.setFirstValueAt(null);
                    dataset.setFirstQuantityValue(null);
                    dataset.setFirstObservation(null);
                    dataset.setLastValueAt(null);
                    dataset.setLastQuantityValue(null);
                    dataset.setLastObservation(null);
                    dataset.setResultTimeStart(null);
                    dataset.setResultTimeEnd(null);
                    dataset.setGeometryEntity(null);
                    update = true;
                }
                if (update) {
                    session.saveOrUpdate(dataset);
                    session.flush();
                }
                if (!aggregations.isEmpty()) {
                    updateAggregations(aggregations, session);
                }
            }
        }
    }

    default void updateAggregations(Set<Long> aggregations, Session session) {
        for (Long id : aggregations) {
            DatasetAggregationEntity aggregation = session.get(DatasetAggregationEntity.class, id);
            Set<Date> samplingTimeStart = new LinkedHashSet<>();
            Set<Date> samplingTimeEnd = new LinkedHashSet<>();
            Set<Date> resultTimeStart = new LinkedHashSet<>();
            Set<Date> resultTimeEnd = new LinkedHashSet<>();
            GeometryEntity geom = new GeometryEntity();
            if (aggregation.isSetDatasets()) {
                for (AbstractDatasetEntity ade : aggregation.getDatasets()) {
                    samplingTimeStart.add(ade.getSamplingTimeStart());
                    samplingTimeEnd.add(ade.getSamplingTimeEnd());
                    resultTimeStart.add(ade.getResultTimeStart());
                    resultTimeEnd.add(ade.getResultTimeEnd());
                    if (ade.getGeometry() != null) {
                        geom.union(ade.getGeometryEntity());
                    }
                }
                aggregation.setSamplingTimeStart(Collections.min(samplingTimeStart));
                aggregation.setSamplingTimeEnd(Collections.max(samplingTimeEnd));
                aggregation.setResultTimeStart(Collections.min(resultTimeStart));
                aggregation.setResultTimeEnd(Collections.max(resultTimeEnd));
                session.saveOrUpdate(aggregation);
                session.flush();
            }
        }
    }

}