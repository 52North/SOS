/*
 * Copyright (C) 2012-2019 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.jfree.data.general.Dataset;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.series.db.beans.CompositeDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.dataset.ValueType;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationRequest;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationResponse;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.AbstractDeleteObservationHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesTimeExtrema;
import org.n52.sos.ds.hibernate.type.UtcTimestampType;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.hibernate.util.TemporalRestriction;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

@Configurable
public class DeleteObservationHandler extends AbstractDeleteObservationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteObservationHandler.class);

    private static final String DELETE_PARAMETER = "delete ";

    private static final String FROM_PARAMETER = " from ";

    private static final String EQUAL_PARAMETER = " = :";

    private static final String IN_PARAMETER = " in :";

    private static final String WHERE_PARAMETER = " where ";

    private static final String AND_PARAMETER = " and ";

    private static final String ERROR_LOG = "Error while updating deleted observation flag data!";

    private HibernateSessionHolder sessionHolder;

    @Inject
    private DaoFactory daoFactory;

    private Boolean deletePhysically = false;

    @Setting("service.transactional.DeletePhysically")
    public void setDeletePhysically(Boolean deletePhysically) {
        this.deletePhysically = deletePhysically;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        setSessionHolder(new HibernateSessionHolder(connectionProvider));
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class);
    }

    @Override
    public synchronized DeleteObservationResponse deleteObservation(DeleteObservationRequest request)
            throws OwsExceptionReport {
        DeleteObservationResponse response = new DeleteObservationResponse(request.getResponseFormat());
        response.setService(request.getService());
        response.setVersion(request.getVersion());
        Session session = null;
        Transaction transaction = null;
        try {
            session = getSessionHolder().getSession();
            transaction = session.beginTransaction();
            if (request.isSetObservationIdentifiers()) {
                deleteObservationsByIdentifier(request, response, session);
            } else {
                deleteObservationByParameter(request, response, session);
            }
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new NoApplicableCodeException().causedBy(he).withMessage(ERROR_LOG);
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce).withMessage(ERROR_LOG);
        } finally {
            getSessionHolder().returnSession(session);
        }
        return response;
    }

    private void deleteObservation(DeleteObservationRequest request, Collection<TemporalFilter> filters,
            Session session) throws OwsExceptionReport {
        deleteObservation(daoFactory.getSeriesDAO().getSeries(request.getProcedures(), request.getObservedProperties(),
                request.getFeatureIdentifiers(), request.getOfferings(), session), filters, session);
    }

    private void deleteObservation(Collection<DatasetEntity> serieses, Collection<TemporalFilter> filters,
            Session session) throws OwsExceptionReport {
        boolean temporalFilters = filters != null && !filters.isEmpty();
        Set<Long> modifiedDatasets = new HashSet<>();
        for (Long s : getSeriesInlcudeChildObs(
                serieses.stream().map(DatasetEntity::getId).collect(Collectors.toSet()), session)) {
            Query<?> q = session.createQuery(getUpdateQueryString(filters, temporalFilters));
            q.setParameter(DataEntity.PROPERTY_DELETED, true);
            q.setParameter(DataEntity.PROPERTY_DATASET, s);
            if (temporalFilters) {
                checkForPlaceholder(q, filters);
            }
            int executeUpdate = q.executeUpdate();
            session.flush();
            if (executeUpdate > 0) {
                modifiedDatasets.add(s);
            }
        }
        if (!modifiedDatasets.isEmpty()) {
            checkSeriesForFirstLatest(modifiedDatasets, session);
            if (deletePhysically) {
                // TODO select all parent ids -> delete childs -> delete parents
                Set<Long> parents = getParents(modifiedDatasets, filters, temporalFilters, session);
                if (!parents.isEmpty()) {
                    deleteDeletedChildObservations(parents, filters, temporalFilters, session);
                }
                deleteDeletedObservations(modifiedDatasets, filters, temporalFilters, session);
            }
        }
    }

    private Set<Long> getSeriesInlcudeChildObs(Set<Long> serieses, Session session) {
        StringBuilder builder = new StringBuilder();
        builder.append("select distinct o2.").append(DataEntity.PROPERTY_DATASET_ID).append(FROM_PARAMETER);
        builder.append(daoFactory.getObservationDAO().getObservationFactory().observationClass().getSimpleName())
                .append(" o ");
        builder.append(" JOIN ")
                .append(daoFactory.getObservationDAO().getObservationFactory().observationClass().getSimpleName())
                .append(" o2 ");
        builder.append(" ON o.").append(DataEntity.PROPERTY_ID).append(" = o2.").append(DataEntity.PROPERTY_PARENT);
        builder.append(WHERE_PARAMETER).append(" o.").append(DataEntity.PROPERTY_DATASET_ID).append(IN_PARAMETER)
                .append(DataEntity.PROPERTY_DATASET);
        Query<?> q = session.createQuery(builder.toString());
        q.setParameter(DataEntity.PROPERTY_DATASET, serieses);
        List<Long> list = (List<Long>) q.list();
        if (list != null && !list.isEmpty()) {
            serieses.addAll(list);
        }
        return serieses;
    }

    private Set<Long> getParents(Set<Long> modifiedDatasets, Collection<TemporalFilter> filters,
            boolean temporalFilters, Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        StringBuilder builder = new StringBuilder();
        builder.append("select distinct ").append(DataEntity.PROPERTY_ID).append(FROM_PARAMETER);
        builder.append(daoFactory.getObservationDAO().getObservationFactory().observationClass().getSimpleName());
        builder.append(WHERE_PARAMETER).append(DataEntity.PROPERTY_DATASET_ID).append(IN_PARAMETER)
                .append(DataEntity.PROPERTY_DATASET);
        if (temporalFilters) {
            builder.append(AND_PARAMETER).append("(" + SosTemporalRestrictions.filterHql(filters).toString())
                    .append(")");
        }
        Query<?> q = session.createQuery(builder.toString());
        q.setParameter(DataEntity.PROPERTY_DATASET, modifiedDatasets);
        if (temporalFilters) {
            checkForPlaceholder(q, filters);
        }
        List<Long> list = (List<Long>) q.list();
        return list != null ? new LinkedHashSet<>(list) : new LinkedHashSet<>();
    }

    private void deleteDeletedChildObservations(Set<Long> parents,
            Collection<TemporalFilter> filters, boolean temporalFilters, Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Query<?> q = session.createQuery(getDeletChildQueryString(parents));
        q.setParameter(DataEntity.PROPERTY_PARENT, parents);
        int executeUpdate = q.executeUpdate();
        LOGGER.debug("{} child observations were physically deleted!", executeUpdate);
        session.flush();

    }

    private void deleteDeletedObservations(Set<Long> modifiedDatasets, Collection<TemporalFilter> filters,
            boolean temporalFilters, Session session)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        Query<?> q = session.createQuery(getDeletQueryString(filters, temporalFilters));
        q.setParameter(DataEntity.PROPERTY_DATASET, modifiedDatasets);
        if (temporalFilters) {
            checkForPlaceholder(q, filters);
        }
        int executeUpdate = q.executeUpdate();
        LOGGER.debug("{} observations were physically deleted!", executeUpdate);
        session.flush();
    }

    private String getDeletQueryString(Collection<TemporalFilter> filters, boolean temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        StringBuilder builder = new StringBuilder();
        builder.append(DELETE_PARAMETER);
        builder.append(daoFactory.getObservationDAO().getObservationFactory().observationClass().getSimpleName());
        builder.append(WHERE_PARAMETER).append(DataEntity.PROPERTY_DATASET_ID).append(IN_PARAMETER)
                .append(DataEntity.PROPERTY_DATASET);
        if (temporalFilters) {
            builder.append(AND_PARAMETER).append("(" + SosTemporalRestrictions.filterHql(filters).toString())
                    .append(")");
        }
        return builder.toString();
    }

    private String getDeletChildQueryString(Set<Long> parents)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        StringBuilder builder = new StringBuilder();
        builder.append(DELETE_PARAMETER);
        builder.append(daoFactory.getObservationDAO().getObservationFactory().observationClass().getSimpleName());
        builder.append(WHERE_PARAMETER).append(DataEntity.PROPERTY_PARENT).append(IN_PARAMETER)
                .append(DataEntity.PROPERTY_PARENT);
        return builder.toString();
    }

    private String getUpdateQueryString(Collection<TemporalFilter> filters, boolean temporalFilters)
            throws UnsupportedTimeException, UnsupportedValueReferenceException, UnsupportedOperatorException {
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(daoFactory.getObservationDAO().getObservationFactory().observationClass().getSimpleName());
        builder.append(" set ").append(DataEntity.PROPERTY_DELETED).append(EQUAL_PARAMETER)
                .append(DataEntity.PROPERTY_DELETED);
        builder.append(WHERE_PARAMETER).append(DataEntity.PROPERTY_DATASET_ID).append(EQUAL_PARAMETER)
                .append(DataEntity.PROPERTY_DATASET);
        if (temporalFilters) {
            builder.append(AND_PARAMETER).append("(" + SosTemporalRestrictions.filterHql(filters).toString())
                    .append(")");
        }
        return builder.toString();
    }

    private void deleteObservationsByIdentifier(DeleteObservationRequest request, DeleteObservationResponse response,
            Session session) throws OwsExceptionReport, ConverterException {
        Set<String> ids = request.getObservationIdentifiers();
        List<DataEntity<?>> observations = daoFactory.getObservationDAO().getObservationByIdentifiers(ids, session);
        if (CollectionHelper.isNotEmpty(observations)) {
            Set<DatasetEntity> modifiedDatasets = new HashSet<>();
            for (DataEntity<?> observation : observations) {
                if (DeleteObservationConstants.NS_SOSDO_1_0.equals(request.getResponseFormat())) {
                    response.setObservationId(request.getObservationIdentifiers().iterator().next());
                }
                modifiedDatasets.add(observation.getDataset());
                delete(observation, session);
            }
            if (!modifiedDatasets.isEmpty()) {
                checkSeriesForFirstLatest(
                        modifiedDatasets.stream().map(DatasetEntity::getId).collect(Collectors.toSet()), session);
            }
        } else {
            if (DeleteObservationConstants.NS_SOSDO_1_0.equals(request.getResponseFormat())) {
                throw new InvalidParameterValueException(DeleteObservationConstants.PARAM_OBSERVATION,
                        Joiner.on(", ").join(request.getObservationIdentifiers()));
            }
        }
    }

    private void deleteObservationByParameter(DeleteObservationRequest request, DeleteObservationResponse response,
            Session session) throws OwsExceptionReport {
        deleteObservation(request, request.getTemporalFilters(), session);
    }

    private void delete(DataEntity<?> observation, Session session) {
        if (observation != null) {
            if (observation instanceof CompositeDataEntity) {
                for (DataEntity<?> o : ((CompositeDataEntity) observation).getValue()) {
                    delete(o, session);
                }
            }
            observation.setDeleted(true);
            session.saveOrUpdate(observation);
            if (deletePhysically) {
                checkForFirstLastReference(observation, session);
                session.delete(observation);
            }
            session.flush();
        }
    }

    private void checkForFirstLastReference(DataEntity<?> observation, Session session) {
        DatasetEntity dataset = observation.getDataset();
        if (dataset.getFirstObservation() != null && dataset.getFirstObservation().getId() != null
                && observation.getId() != null && dataset.getFirstObservation().getId().equals(observation.getId())) {
            dataset.setFirstObservation(null);
        }
        if (dataset.getLastObservation() != null && dataset.getLastObservation().getId() != null
                && observation.getId() != null && dataset.getLastObservation().getId().equals(observation.getId())) {
            dataset.setLastObservation(null);
        }
        session.update(dataset);
        session.flush();

    }

    private void checkForPlaceholder(Query<?> q, Collection<TemporalFilter> filters)
            throws UnsupportedValueReferenceException {

        int count = 1;
        for (TemporalFilter filter : filters) {
            if (filter.getTime() instanceof TimePeriod) {
                TimePeriod tp = (TimePeriod) filter.getTime();
                if (q.getComment().contains(":" + TemporalRestriction.START)) {
                    q.setParameter(TemporalRestriction.START + count, tp.getStart().toDate(),
                            UtcTimestampType.INSTANCE);
                }
                if (q.getComment().contains(":" + TemporalRestriction.END)) {
                    q.setParameter(TemporalRestriction.END + count, tp.getEnd().toDate(), UtcTimestampType.INSTANCE);
                }
            }
            if (filter.getTime() instanceof TimeInstant) {
                TimeInstant ti = (TimeInstant) filter.getTime();
                q.setParameter(TemporalRestriction.INSTANT + count, ti.getValue().toDate(), UtcTimestampType.INSTANCE);
            }
            count++;
        }
    }

    /**
     * Check if {@link Dataset} should be updated
     *
     * @param serieses
     *            Deleted observation
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    private void checkSeriesForFirstLatest(Set<Long> serieses, Session session) throws OwsExceptionReport {
        if (!serieses.isEmpty()) {
            AbstractSeriesObservationDAO observationDAO = daoFactory.getObservationDAO();
            Map<Long, SeriesTimeExtrema> minMaxTimes = observationDAO.getMinMaxSeriesTimesById(serieses, session);
            for (Long id : serieses) {
               DatasetEntity series = session.get(DatasetEntity.class, id);
                boolean update = false;
                if (minMaxTimes.containsKey(series.getId())) {
                    SeriesTimeExtrema extrema = minMaxTimes.get(series.getId());
                    if (!series.isSetFirstValueAt() || (series.isSetFirstValueAt() && !DateTimeHelper
                            .makeDateTime(series.getFirstValueAt()).equals(extrema.getMinPhenomenonTime()))) {
                        series.setFirstValueAt(extrema.getMinPhenomenonTime().toDate());
                        DataEntity<?> o =
                                observationDAO.getMinObservation(series, extrema.getMinPhenomenonTime(), session);
                        series.setFirstObservation(o);
                        if (series.getValueType().equals(ValueType.quantity)) {
                            series.setFirstQuantityValue(((QuantityDataEntity) o).getValue());
                        }
                        update = true;
                    }
                    if (!series.isSetLastValueAt() || (series.isSetLastValueAt() && !DateTimeHelper
                            .makeDateTime(series.getLastValueAt()).equals(extrema.getMaxPhenomenonTime()))) {
                        series.setLastValueAt(extrema.getMaxPhenomenonTime().toDate());
                        DataEntity<?> o =
                                observationDAO.getMaxObservation(series, extrema.getMaxPhenomenonTime(), session);
                        series.setLastObservation(o);
                        if (series.getValueType().equals(ValueType.quantity)) {
                            series.setLastQuantityValue(((QuantityDataEntity) o).getValue());

                        }
                        update = true;
                    }
                } else {
                    series.setFirstValueAt(null);
                    series.setFirstQuantityValue(null);
                    series.setFirstObservation(null);
                    series.setLastValueAt(null);
                    series.setLastQuantityValue(null);
                    series.setLastObservation(null);
                    update = true;
                }
                if (update) {
                    session.saveOrUpdate(series);
                    session.flush();
                }
            }
        }
    }

    private synchronized HibernateSessionHolder getSessionHolder() {
        return sessionHolder;
    }

    private synchronized void setSessionHolder(HibernateSessionHolder sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

}
