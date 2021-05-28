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
package org.n52.sos.ds.hibernate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.n52.series.db.beans.DatasetAggregationEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.delobs.DeleteObservationRequest;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesDAO;
import org.n52.sos.ds.hibernate.util.HibernateHelper;

import com.google.common.collect.Lists;

public interface DeleteDataHelper extends DeleteObservationHelper {

    default void deleteDataset(DatasetEntity dataset, Session session) throws OwsExceptionReport {
        deleteReferencedDatasets(dataset, session);
        deleteObservation(Lists.newArrayList(dataset), null, session);
        deleteDatastream(dataset, session);
        getDaoFactory().getResultTemplateDAO()
                .delete(dataset, session);
        session.delete(dataset);
        session.flush();
        deleteOffering(dataset, session);
        deleteProcedure(dataset, session);
    }

    default void deleteOffering(DatasetEntity dataset, Session session) throws OwsExceptionReport {
        OfferingEntity offering = dataset.getOffering();
        List<DatasetEntity> datasets = getDatasetForOffering(offering, session);
        if (datasets == null || datasets.isEmpty()) {
            getDaoFactory().getOfferingDAO()
                    .delete(Lists.newArrayList(offering), session);
        }
    }

    default List<DatasetEntity> getDatasetForProcedure(ProcedureEntity procedure, Session session) {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        Criteria c = seriesDAO.getDefaultAllSeriesCriteria(session);
        seriesDAO.addProcedureToCriteria(c, procedure);
        return c.list();
    }

    default List<DatasetEntity> getDatasetForOffering(OfferingEntity offering, Session session) {
        AbstractSeriesDAO seriesDAO = getDaoFactory().getSeriesDAO();
        Criteria c = seriesDAO.getDefaultAllSeriesCriteria(session);
        seriesDAO.addOfferingToCriteria(c, offering);
        return c.list();
    }

    default void deleteProcedure(DatasetEntity dataset, Session session) throws OwsExceptionReport {
        ProcedureEntity procedure = dataset.getProcedure();
        List<DatasetEntity> datasets = getDatasetForProcedure(procedure, session);
        if (datasets == null || datasets.isEmpty()) {
            deleteSensor(procedure, session);
        }
    }

    default void deleteProcedure(ProcedureEntity procedure, Session session) throws OwsExceptionReport {
        // delete proc history
        getDaoFactory().getProcedureHistoryDAO()
                .delete(procedure, session);
        // delete procedure
        session.delete(procedure);
        session.flush();
    }

    default void deleteSensor(ProcedureEntity procedure, Session session) throws OwsExceptionReport {
        deleteChildren(procedure, session);
        deleteReferencedDatasets(procedure, session);
        String identifier = procedure.getIdentifier();
        if (isDeletePhysically()) {
            // delete observations
            deleteObservations(identifier, session);
            // delete result templates
            getDaoFactory().getResultTemplateDAO()
                    .delete(procedure, session);
            // delete datasets
            List<DatasetEntity> deleteSeries = getDaoFactory().getSeriesDAO()
                    .delete(procedure, session);
            // delete offerings
            getDaoFactory().getOfferingDAO()
                    .delete(deleteSeries.stream()
                            .map(DatasetEntity::getOffering)
                            .collect(Collectors.toSet()), session);
            deleteProcedure(procedure, session);
        } else {
            setDeleteSensorFlag(identifier, true, session);
            getDaoFactory().getProcedureHistoryDAO()
                    .setEndTime(identifier, session);
        }

    }

    default void deleteChildren(ProcedureEntity procedure, Session session) throws OwsExceptionReport {
        if (procedure.hasChildren()) {
            for (ProcedureEntity child : procedure.getChildren()) {
                deleteSensor(child, session);
            }
        }
    }

    default void deleteReferencedDatasets(ProcedureEntity procedure, Session session) throws OwsExceptionReport {
        List<DatasetEntity> datasets = getDaoFactory().getSeriesDAO()
                .getSeries(procedure.getIdentifier(), null, null, null, session);
        for (DatasetEntity dataset : datasets) {
            if (dataset.hasReferenceValues()) {
                for (DatasetEntity referenceValue : dataset.getReferenceValues()) {
                    deleteDataset(referenceValue, session);
                }
            }
        }
    }

    default void deleteReferencedDatasets(DatasetEntity dataset, Session session) throws OwsExceptionReport {
        if (dataset.hasReferenceValues()) {
            for (DatasetEntity referenceValue : dataset.getReferenceValues()) {
                deleteDataset(referenceValue, session);
            }
        }
    }

    default void deleteDatastream(DatasetEntity dataset, Session session) {
        if (dataset.isSetAggregation() && HibernateHelper.isEntitySupported(DatasetAggregationEntity.class)) {
            StringBuilder builder = new StringBuilder();
            builder.append(DELETE_PARAMETER);
            builder.append(DatasetAggregationEntity.class.getSimpleName());
            builder.append(WHERE_PARAMETER);
            builder.append(DatasetAggregationEntity.PROPERTY_ID);
            builder.append(EQUAL_PARAMETER);
            builder.append(DatasetAggregationEntity.PROPERTY_ID);
            Query<?> q = session.createQuery(builder.toString());
            q.setParameter(DatasetAggregationEntity.PROPERTY_ID, dataset.getAggregation()
                    .getId());
            int executeUpdate = q.executeUpdate();
            logExecution(executeUpdate);
            session.flush();
        }
    }

    default void logExecution(int executeUpdate) {
        getLogger().debug("{} datastreams were physically deleted!", executeUpdate);
    }

    default void deleteObservations(String procedureIdentifier, Session session) throws OwsExceptionReport {
        DeleteObservationRequest request = new DeleteObservationRequest();
        request.setService(SosConstants.SOS);
        request.setVersion(Sos2Constants.SERVICEVERSION);
        request.addProcedure(procedureIdentifier);
        deleteObservation(request, null, session);
    }

    /**
     * Set the deleted flag of the procedure and corresponding entities
     * (observations, series, obervationConstellation) to <code>true</code>
     *
     * @param identifier
     *            Procedure identifier
     * @param deleteFlag
     *            Deleted flag to set
     * @param session
     *            Hibernate session
     * @throws OwsExceptionReport
     *             If the procedure is not contained in the database
     */
    default void setDeleteSensorFlag(String identifier, boolean deleteFlag, Session session)
            throws OwsExceptionReport {
        if (identifier != null && !identifier.isEmpty()) {
            List<DatasetEntity> dataset = getDaoFactory().getSeriesDAO()
                    .updateSeriesSetAsDeletedForProcedureAndGetSeries(identifier, deleteFlag, session);
            deleteObservation(dataset, Collections.<TemporalFilter> emptyList(), session);
        } else {
            throw new NoApplicableCodeException().withMessage("The requested identifier is not contained in database");
        }
    }

}
