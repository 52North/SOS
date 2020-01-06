/**
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
package org.n52.sos.ext.deleteobservation;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.convert.ConverterException;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesObservationDAO;
import org.n52.sos.ds.hibernate.dao.observation.series.SeriesTimeExtrema;
import org.n52.sos.ds.hibernate.entities.EntitiyHelper;
import org.n52.sos.ds.hibernate.entities.ValidProcedureTime;
import org.n52.sos.ds.hibernate.entities.observation.Observation;
import org.n52.sos.ds.hibernate.entities.observation.full.ComplexObservation;
import org.n52.sos.ds.hibernate.entities.observation.full.ProfileObservation;
import org.n52.sos.ds.hibernate.entities.observation.series.Series;
import org.n52.sos.ds.hibernate.entities.observation.valued.NumericValuedObservation;
import org.n52.sos.ds.hibernate.type.ConfigurableTimestampType;
import org.n52.sos.ds.hibernate.type.UtcTimestampType;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.TemporalRestriction;
import org.n52.sos.ds.hibernate.util.TemporalRestrictions;
import org.n52.sos.ds.hibernate.util.TimePrimitiveFieldDescriptor;
import org.n52.sos.ds.hibernate.util.observation.HibernateObservationUtilities;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 * @since 1.0.0
 */
public class DeleteObservationDAO extends DeleteObservationAbstractDAO {

    private HibernateSessionHolder hibernateSessionHolder = new HibernateSessionHolder();

    @Override
    public synchronized DeleteObservationResponse deleteObservation(DeleteObservationRequest request)
            throws OwsExceptionReport {
        DeleteObservationResponse response = request.getResponse();
        Session session = null;
        Transaction transaction = null;
        try {
            session = hibernateSessionHolder.getSession();
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
            hibernateSessionHolder.returnSession(session);
        }
        return response;
    }

    private AbstractObservationRequest getRequest(DeleteObservationRequest request) {
        return (AbstractObservationRequest) new GetObservationRequest().setService(request.getService())
                .setVersion(request.getVersion());
    }

    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.ORM_DATASOURCE_DAO_IDENTIFIER;
    }

    private void deleteObservationsByIdentifier(DeleteObservationRequest request, DeleteObservationResponse response,
            Session session) throws OwsExceptionReport, ConverterException {
        Set<String> ids = request.getObservationIdentifiers();
        List<Observation<?>> observations = DaoFactory.getInstance().getObservationDAO().getObservationByIdentifiers(ids, session);
        if (CollectionHelper.isNotEmpty(observations)) {
            for (Observation<?> observation : observations) {
                delete(observation, session);
            }
            if (DeleteObservationConstants.NS_SOSDO_1_0.equals(request.getResponseFormat())) {
                Observation<?> observation = observations.iterator().next();
                OmObservation so = HibernateObservationUtilities
                        .createSosObservationsFromObservations(Sets.<Observation<?>>newHashSet(observation), getRequest(request),
                                null, session)
                        .iterator().next();
                response.setObservationId(request.getObservationIdentifiers().iterator().next());
                response.setDeletedObservation(so);
            }
        } else if (EntitiyHelper.getInstance().isSeriesSupported()) {
            deleteSeriesObservation(DaoFactory.getInstance().getSeriesDAO().getSeries(ids, session), null, session);
        } else {
            if (DeleteObservationConstants.NS_SOSDO_1_0.equals(request.getResponseFormat())) {
                throw new InvalidParameterValueException(DeleteObservationConstants.PARAM_OBSERVATION,
                        Joiner.on(", ").join(request.getObservationIdentifiers()));
            }
        }
    }

    private void deleteObservationByParameter(DeleteObservationRequest request, DeleteObservationResponse response,
            Session session) throws OwsExceptionReport {
        Criterion filter = null;
        if (CollectionHelper.isNotEmpty(request.getTemporalFilters())) {
            filter = TemporalRestrictions.filter(request.getTemporalFilters());
        }
        if (EntitiyHelper.getInstance().isSeriesSupported()) {
            deleteSeriesObservation(request, request.getTemporalFilters(), session);
           
        } else {
            ScrollableResults result = DaoFactory.getInstance().getObservationDAO().getObservations(request.getProcedures(),
                    request.getObservedProperties(), request.getFeatureIdentifiers(), request.getOfferings(),
                    filter, session);
            if (result.next()) {
                while (result.next()) {
                    delete((Observation<?>) result.get()[0], session);
                }
            }
        }
    }
    
    private Observation<?> delete(Observation<?> observation, Session session) {
        if (observation != null) {
            if (observation instanceof ComplexObservation) {
                for (Observation<?> o : ((ComplexObservation)observation).getValue()) {
                    delete(o, session);
                }
            } else if (observation instanceof ProfileObservation) {
                for (Observation<?> o : ((ProfileObservation)observation).getValue()) {
                    delete(o, session);
                }
            }
            observation.setDeleted(true);
            session.saveOrUpdate(observation);
            session.flush();
            return observation;
        }
        return null;
    }
    
    private void deleteSeriesObservation(DeleteObservationRequest request, Set<TemporalFilter> filters, Session session)
            throws CodedException, OwsExceptionReport {
        deleteSeriesObservation(DaoFactory.getInstance().getSeriesDAO().getSeries(request.getProcedures(),
                request.getObservedProperties(), request.getFeatureIdentifiers(), request.getOfferings(), session),
                filters, session);
    }

    private void deleteSeriesObservation(List<Series> serieses, Set<TemporalFilter> filters, Session session) throws CodedException, OwsExceptionReport {
        boolean temporalFilters = filters != null && !filters.isEmpty();
        Set<Series> modifiedSeries = new HashSet<>();
        StringBuilder builder = new StringBuilder();
        builder.append("update ");
        builder.append(DaoFactory.getInstance().getObservationDAO().getObservationFactory().observationClass().getSimpleName());
            builder.append(" set deleted = :deleted");
        builder.append(" where seriesid = :id");
        if (temporalFilters) {
            builder.append(" AND (" + TemporalRestrictions.filterHql(filters).toString()).append(")");
        }
        for (Series s : serieses) {
            Query q = session.createQuery(builder.toString())
                    .setBoolean("deleted", true)
                    .setLong("id", s.getSeriesId());
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

    private void checkForPlaceholder(Query q, Set<TemporalFilter> filters) throws UnsupportedValueReferenceException {
        int count = 1;
        for (TemporalFilter filter : filters) {
            TimePrimitiveFieldDescriptor tpfd = TemporalRestrictions.getFields(filter.getValueReference());
            if (filter.getTime() instanceof TimePeriod) {
                TimePeriod tp = (TimePeriod) filter.getTime();
                if (q.getComment().contains(":" + TemporalRestriction.START)) {
                    q.setParameter(TemporalRestriction.START + count, tp.getStart().toDate(), UtcTimestampType.INSTANCE);
                }
                if (q.getComment().contains(":" + TemporalRestriction.END)) {
                    q.setParameter(TemporalRestriction.END + count, tp.getEnd().toDate(), UtcTimestampType.INSTANCE);
                }
            } if (filter.getTime() instanceof TimeInstant) {
                TimeInstant ti = (TimeInstant) filter.getTime();
                q.setParameter(TemporalRestriction.INSTANT + count, ti.getValue().toDate(), UtcTimestampType.INSTANCE);
            }
            count++;
        }
    }

    /**
     * Check if {@link Series} should be updated
     * 
     * @param serieses
     *            Deleted observation
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport 
     */
    private void checkSeriesForFirstLatest(Set<Series> serieses, Session session) throws OwsExceptionReport {
        if (!serieses.isEmpty()) {
            AbstractSeriesObservationDAO observationDAO =
                    (AbstractSeriesObservationDAO) DaoFactory.getInstance().getObservationDAO();
            Map<Long, SeriesTimeExtrema> minMaxTimes = observationDAO.getMinMaxSeriesTimes(serieses, session);
            for (Series series : serieses) {
                boolean update = false;
                if (minMaxTimes.containsKey(series.getSeriesId())) {
                    SeriesTimeExtrema extrema = minMaxTimes.get(series.getSeriesId());
                    if (!series.isSetFirstTimeStamp() || (series.isSetFirstTimeStamp() && !DateTimeHelper.makeDateTime(series.getFirstTimeStamp())
                            .equals(extrema.getMinPhenomenonTime()))) {
                        series.setFirstTimeStamp(extrema.getMinPhenomenonTime().toDate());
                        if (series.getSeriesType().equals("quantity")) {
                            NumericValuedObservation o = (NumericValuedObservation) observationDAO
                                    .getMinObservation(series, extrema.getMinPhenomenonTime(), session);
                            series.setFirstNumericValue(o.getValue());
                        }
                        update = true;
                    }
                    if (!series.isSetLastTimeStamp() || (series.isSetLastTimeStamp() && !DateTimeHelper.makeDateTime(series.getLastTimeStamp())
                            .equals(extrema.getMaxPhenomenonTime()))) {
                        series.setLastTimeStamp(extrema.getMaxPhenomenonTime().toDate());
                        if (series.getSeriesType().equals("quantity")) {
                            NumericValuedObservation o = (NumericValuedObservation) observationDAO
                                    .getMaxObservation(series, extrema.getMaxPhenomenonTime(), session);
                            series.setLastNumericValue(o.getValue());
                        }
                        update = true;
                    }
                } else {
                    series.setFirstTimeStamp(null);
                    series.setFirstNumericValue(null);
                    series.setLastTimeStamp(null);
                    series.setLastNumericValue(null);
                    update = true;
                }
                if (update) {
                    session.saveOrUpdate(series);
                    session.flush();
                }
            }
        }
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ValidProcedureTime.class);
    }
}
