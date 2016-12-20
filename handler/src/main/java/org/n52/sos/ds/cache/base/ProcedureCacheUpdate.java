/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.cache.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.iceland.ds.ConnectionProvider;
import org.n52.io.request.IoParameters;
import org.n52.proxy.db.dao.ProxyDatasetDao;
import org.n52.proxy.db.dao.ProxyProcedureDao;
import org.n52.series.db.DataAccessException;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.hibernate.cache.AbstractQueueingDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.cache.DatasourceCacheUpdateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
 *
 * @since 4.0.0
 */
public class ProcedureCacheUpdate extends AbstractQueueingDatasourceCacheUpdate<ProcedureCacheUpdateTask> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureCacheUpdate.class);

    private static final String THREAD_GROUP_NAME = "procedure-cache-update";
    private List<ProcedureEntity> procedures = new ArrayList<>();
    private Map<String,Collection<DatasetEntity>> procedureDatasetMap = new HashMap<>();

    /**
     * constructor
     * @param threads Thread count
     */
    public ProcedureCacheUpdate(int threads, ConnectionProvider connectionProvider) {
        super(threads, THREAD_GROUP_NAME, connectionProvider);
    }

    @SuppressWarnings("unchecked")
    private Map<String,Collection<DatasetEntity>> getOfferingDatasets() throws OwsExceptionReport {
        if (procedureDatasetMap == null) {
            try {
                procedureDatasetMap.putAll(DatasourceCacheUpdateHelper.mapByProcedure(
                    new ProxyDatasetDao(getSession()).getAllInstances(new DbQuery(IoParameters.createDefaults()))));
            } catch (DataAccessException dae) {
                throw new NoApplicableCodeException().causedBy(dae).withMessage("Error while querying datasets for offerings");
            }
        }
        return procedureDatasetMap;
    }

//    private void getProcedureDescriptionFormat() {
//        getCache().setRequestableProcedureDescriptionFormat(new ProcedureDescriptionFormatDAO().getProcedureDescriptionFormat(getSession()));
//    }


//    private void setTypeProcedure(Procedure procedure) {
//        if (procedure.isType()) {
//            getCache().addTypeInstanceProcedure(SosContentCache.TypeInstance.TYPE, procedure.getIdentifier());
//        } else {
//            getCache().addTypeInstanceProcedure(SosContentCache.TypeInstance.INSTANCE, procedure.getIdentifier());
//        }
//    }
//
//    private void setAggregatedProcedure(Procedure procedure) {
//        if (procedure.isAggregation()) {
//            getCache().addComponentAggregationProcedure(SosContentCache.ComponentAggregation.AGGREGATION, procedure.getIdentifier());
//        } else {
//            getCache().addComponentAggregationProcedure(SosContentCache.ComponentAggregation.COMPONENT, procedure.getIdentifier());
//        }
//    }
//
//    private void setTypeInstanceProcedure(Procedure procedure) {
//        if (procedure.isSetTypeOf()) {
//            getCache().addTypeOfProcedure(procedure.getTypeOf().getIdentifier(), procedure.getIdentifier());
//        }
//    }

    @Override
    protected ProcedureCacheUpdateTask[] getUpdatesToExecute() {
        Collection<ProcedureCacheUpdateTask> procedureUpdateTasks = Lists.newArrayList();
        for (ProcedureEntity procedure : procedures) {
            procedureUpdateTasks.add(new ProcedureCacheUpdateTask(procedure, procedureDatasetMap.get(procedure.getDomainId())));
        }
        return procedureUpdateTasks.toArray(new ProcedureCacheUpdateTask[procedureUpdateTasks.size()]);
    }

    @Override
    public void execute() {
        //single threaded updates
        LOGGER.debug("Executing ProcedureCacheUpdate (Single Threaded Tasks)");
        startStopwatch();
//        getProcedureDescriptionFormat();

//        try {
//        Map<String, Collection<String>> procedureMap = procedureDAO.getProcedureIdentifiers(getSession());
//            List<ProcedureEntity> procedures = procedureDAO.getAllInstances(new DbQuery(IoParameters.createDefaults()));
//            for (ProcedureEntity procedure : procedures) {
//                String identifier = procedure.getDomainId();
//                getCache().addProcedure(identifier);
//                if (procedure.isSetName()) {
//                    getCache().addProcedureIdentifierHumanReadableName(identifier, procedure.getName());
//                }
//                getCache().setOfferingsForProcedure(identifier, DatasourceCacheUpdateHelper
//                        .getAllOfferingIdentifiersFromDatasets(procedureDatasetMap.get(identifier)));
//                getCache().setObservablePropertiesForProcedure(identifier, DatasourceCacheUpdateHelper
//                        .getAllObservablePropertyIdentifiersFromDatasets(procedureDatasetMap.get(identifier)));
//
//                if (procedure.hasParents()) {
//                    getCache().addParentProcedures(identifier, getParents(procedure));
//                }
//            }
//        } catch (DataAccessException dae) {
//            getErrors().add(new NoApplicableCodeException().causedBy(dae)
//                    .withMessage("Error while updating procedure cache!"));
//        }
//        //time ranges
//        //TODO querying procedure time extrema in a single query is definitely faster for a properly
//        //     indexed Postgres db, but may not be true for all platforms. move back to multithreaded execution
//        //     in ProcedureCacheUpdateTask if needed
//        Map<String, TimeExtrema> procedureTimeExtrema = null;
//        try {
//            procedureTimeExtrema = procedureDAO.getProcedureTimeExtrema(getSession());
//
//        } catch (OwsExceptionReport ce) {
//            LOGGER.error("Error while querying offering time ranges!", ce);
//            getErrors().add(ce);
//        }
//        if (!CollectionHelper.isEmpty(procedureTimeExtrema)) {
//            for (Entry<String, TimeExtrema> entry : procedureTimeExtrema.entrySet()) {
//                String procedureId = entry.getKey();
//                TimeExtrema te = entry.getValue();
//                getCache().setMinPhenomenonTimeForProcedure(procedureId, te.getMinPhenomenonTime());
//                getCache().setMaxPhenomenonTimeForProcedure(procedureId, te.getMaxPhenomenonTime());
//            }
//        }
        LOGGER.debug("Finished executing ProcedureCacheUpdate (Single Threaded Tasks) ({})", getStopwatchResult());

        //multi-threaded execution
        LOGGER.debug("Executing ProcedureCacheUpdate (Multi-Threaded Tasks)");
        startStopwatch();
        super.execute();
        LOGGER.debug("Finished executing ProcedureCacheUpdate (Multi-Threaded Tasks) ({})", getStopwatchResult());
    }

}
