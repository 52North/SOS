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
package org.n52.sos.ds.hibernate;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.series.db.beans.CompositeDataEntity;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.dataset.Dataset;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.InvalidParameterValueException;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationRequest;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationResponse;
import org.n52.shetland.ogc.sos.request.AbstractObservationRequest;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.ds.AbstractDeleteObservationHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesTimeExtrema;
import org.n52.sos.ds.hibernate.type.UtcTimestampType;
import org.n52.sos.ds.hibernate.util.SosTemporalRestrictions;
import org.n52.sos.ds.hibernate.util.TemporalRestriction;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.ds.hibernate.util.observation.OmObservationCreatorContext;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;

import com.google.common.base.Joiner;

@Configurable
public class DeleteObservationHandler extends AbstractDeleteObservationHandler {

    private HibernateSessionHolder sessionHolder;

    private DaoFactory daoFactory;

    private OmObservationCreatorContext observationCreatorContext;

    private Boolean deletePhysically = false;

    @Inject
    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setOmObservationCreatorContext(OmObservationCreatorContext observationCreatorContext) {
        this.observationCreatorContext = observationCreatorContext;
    }

    @Setting("service.transactional.DeletePhysically")
    public void setDeletePhysically(Boolean deletePhysically) {
        this.deletePhysically = deletePhysically;
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
            session = sessionHolder.getSession();
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
            throw new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while updating deleted observation flag data!");
        } catch (ConverterException ce) {
            throw new NoApplicableCodeException().causedBy(ce)
                    .withMessage("Error while updating deleted observation flag data!");
        } finally {
            sessionHolder.returnSession(session);
        }
        return response;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    private AbstractObservationRequest getRequest(DeleteObservationRequest request) {
        return (AbstractObservationRequest) new GetObservationRequest().setService(request.getService())
                .setVersion(request.getVersion());
    }

    private void deleteObservationsByIdentifier(DeleteObservationRequest request, DeleteObservationResponse response,
            Session session) throws OwsExceptionReport, ConverterException {
        Set<String> ids = request.getObservationIdentifiers();
        List<DataEntity<?>> observations = daoFactory.getObservationDAO().getObservationByIdentifiers(ids, session);
        if (CollectionHelper.isNotEmpty(observations)) {
            for (DataEntity<?> observation : observations) {
                delete(observation, session);
            }
            if (DeleteObservationConstants.NS_SOSDO_1_0.equals(request.getResponseFormat())) {
                DataEntity<?> observation = observations.iterator().next();
                Set<DataEntity<?>> oberservations = Collections.singleton(observation);
                OmObservation so = HibernateObservationUtilities.createSosObservationsFromObservations(oberservations,
                        getRequest(request), getRequestedLocale(request), null, observationCreatorContext, session)
                        .next();
                response.setObservationId(request.getObservationIdentifiers().iterator().next());
                response.setDeletedObservation(so);
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
                session.delete(observation);
            }
            session.flush();
        }
    }

    private void deleteObservation(DeleteObservationRequest request, Collection<TemporalFilter> filters,
            Session session) throws OwsExceptionReport {
        deleteObservation(daoFactory.getSeriesDAO().getSeries(request.getProcedures(), request.getObservedProperties(),
                request.getFeatureIdentifiers(), request.getOfferings(), session), filters, session);
    }

    private void deleteObservation(Collection<DatasetEntity> serieses, Collection<TemporalFilter> filters,
            Session session) throws OwsExceptionReport {
        boolean temporalFilters = filters != null && !filters.isEmpty();
        Set<Dataset> modifiedSeries = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(daoFactory.getObservationDAO().getObservationFactory().observationClass().getSimpleName());
        builder.append(" set ").append(DataEntity.PROPERTY_DELETED).append(" = :").append(DataEntity.PROPERTY_DELETED);
        builder.append(" where ").append(DataEntity.PROPERTY_DATASET).append(" = :").append(DataEntity.PROPERTY_DATASET);
        if (temporalFilters) {
            builder.append(" AND (" + SosTemporalRestrictions.filterHql(filters).toString()).append(")");
        }
        for (Dataset s : serieses) {
            Query<?> q = session.createQuery(builder.toString()).setParameter(DataEntity.PROPERTY_DELETED, true)
                    .setParameter(DataEntity.PROPERTY_DATASET, s);
            if (temporalFilters) {
                checkForPlaceholder(q, filters);
            }
            int executeUpdate = q.executeUpdate();
            session.flush();
            if (executeUpdate > 0) {
                modifiedSeries.add(s);
            }
        }
        if (!modifiedSeries.isEmpty()) {
            checkSeriesForFirstLatest(modifiedSeries, session);
        }
    }

    private void checkForPlaceholder(Query<?> q, Collection<TemporalFilter> filters)
            throws UnsupportedValueReferenceException {

        int count = 1;
        for (TemporalFilter filter : filters) {
            if (filter.getTime() instanceof TimePeriod) {
                TimePeriod tp = (TimePeriod) filter.getTime();
                if (q.getComment().contains(":" + TemporalRestriction.START)) {
                    q.setParameter(TemporalRestriction.START + count, tp.getStart().toDate(), UtcTimestampType.INSTANCE);
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
     */
    private void checkSeriesForFirstLatest(Set<Dataset> serieses, Session session) throws OwsExceptionReport {
        if (!serieses.isEmpty()) {
            AbstractSeriesObservationDAO observationDAO = daoFactory.getObservationDAO();
            Map<Long, SeriesTimeExtrema> minMaxTimes = observationDAO.getMinMaxSeriesTimes(serieses, session);
            for (Dataset series : serieses) {
                boolean update = false;
                if (minMaxTimes.containsKey(series.getId())) {
                    SeriesTimeExtrema extrema = minMaxTimes.get(series.getId());
                    if (!series.isSetFirstValueAt() || (series.isSetFirstValueAt() && !DateTimeHelper
                            .makeDateTime(series.getFirstValueAt()).equals(extrema.getMinPhenomenonTime()))) {
                        series.setFirstValueAt(extrema.getMinPhenomenonTime().toDate());
                        if (series.getValueType().equals("quantity")) {
                            QuantityDataEntity o = (QuantityDataEntity) observationDAO.getMinObservation(series,
                                    extrema.getMinPhenomenonTime(), session);
                            series.setFirstQuantityValue(o.getValue());
                            series.setFirstObservation(o);
                        }
                        update = true;
                    }
                    if (!series.isSetLastValueAt() || (series.isSetLastValueAt() && !DateTimeHelper
                            .makeDateTime(series.getLastValueAt()).equals(extrema.getMaxPhenomenonTime()))) {
                        series.setLastValueAt(extrema.getMaxPhenomenonTime().toDate());
                        if (series.getValueType().equals("quantity")) {
                            QuantityDataEntity o = (QuantityDataEntity) observationDAO.getMaxObservation(series,
                                    extrema.getMaxPhenomenonTime(), session);
                            series.setLastQuantityValue(o.getValue());
                            series.setLastObservation(o);
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

}
