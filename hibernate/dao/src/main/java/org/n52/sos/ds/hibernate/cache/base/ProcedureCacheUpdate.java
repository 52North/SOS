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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.internal.util.collections.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.ds.ConnectionProvider;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.hibernate.cache.AbstractQueueingDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.cache.DatasourceCacheUpdateHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ProcedureDescriptionFormatDAO;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationConstellationInfo;
import org.n52.sos.ds.hibernate.util.TimeExtrema;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 * @author <a href="mailto:shane@axiomalaska.com">Shane StClair</a>
 *
 * @since 4.0.0
 */
public class ProcedureCacheUpdate extends AbstractQueueingDatasourceCacheUpdate<ProcedureCacheUpdateTask> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureCacheUpdate.class);

    private static final String THREAD_GROUP_NAME = "procedure-cache-update";

    private Map<String, Collection<String>> procedureMap;

    private Map<String,Collection<ObservationConstellationInfo>> procObsConstInfoMap;
    private DaoFactory daoFactory;


    /**
     * constructor
     * @param threads Thread count
     */
    public ProcedureCacheUpdate(int threads, ConnectionProvider connectionProvider, DaoFactory daoFactory) {
        super(threads, THREAD_GROUP_NAME, connectionProvider);
        this.daoFactory = daoFactory;
    }

    private Map<String,Collection<ObservationConstellationInfo>> getProcedureObservationConstellationInfo() {
        if (procObsConstInfoMap == null) {
            procObsConstInfoMap = ObservationConstellationInfo.mapByProcedure(
                daoFactory.getObservationConstellationDAO().getObservationConstellationInfo(getSession()));
        }
        return procObsConstInfoMap;
    }

    private Map<String, Collection<String>> getProcedureMap() {
        if (procedureMap == null) {
            procedureMap = daoFactory.getProcedureDAO().getProcedureIdentifiers(getSession());
        }
        return procedureMap;
    }

    private void getProcedureDescriptionFormat() {
        getCache().setRequestableProcedureDescriptionFormat(new ProcedureDescriptionFormatDAO().getProcedureDescriptionFormat(getSession()));
    }


    private void setTypeProcedure(Procedure procedure) {
        if (procedure.isType()) {
            getCache().addTypeInstanceProcedure(SosContentCache.TypeInstance.TYPE, procedure.getIdentifier());
        } else {
            getCache().addTypeInstanceProcedure(SosContentCache.TypeInstance.INSTANCE, procedure.getIdentifier());
        }
    }

    private void setAggregatedProcedure(Procedure procedure) {
        if (procedure.isAggregation()) {
            getCache().addComponentAggregationProcedure(SosContentCache.ComponentAggregation.AGGREGATION, procedure.getIdentifier());
        } else {
            getCache().addComponentAggregationProcedure(SosContentCache.ComponentAggregation.COMPONENT, procedure.getIdentifier());
        }
    }

    private void setTypeInstanceProcedure(Procedure procedure) {
        if (procedure.isSetTypeOf()) {
            getCache().addTypeOfProcedure(procedure.getTypeOf().getIdentifier(), procedure.getIdentifier());
        }
    }

    @Override
    protected ProcedureCacheUpdateTask[] getUpdatesToExecute() {
        Collection<ProcedureCacheUpdateTask> procedureUpdateTasks = Lists.newArrayList();
        Set<String> procedureIdentifiers = getProcedureMap().keySet();
        for (String procedureIdentifier : procedureIdentifiers) {
            procedureUpdateTasks.add(new ProcedureCacheUpdateTask(procedureIdentifier, daoFactory));
        }
        return procedureUpdateTasks.toArray(new ProcedureCacheUpdateTask[procedureUpdateTasks.size()]);
    }

    @Override
    public void execute() {
        //single threaded updates
        LOGGER.debug("Executing ProcedureCacheUpdate (Single Threaded Tasks)");
        startStopwatch();
        getProcedureDescriptionFormat();

        boolean obsConstSupported = HibernateHelper.isEntitySupported(ObservationConstellation.class);

        Map<String, Collection<String>> procedureMap = daoFactory.getProcedureDAO().getProcedureIdentifiers(getSession());
        List<Procedure> procedures = daoFactory.getProcedureDAO().getProcedureObjects(getSession());
        for (Procedure procedure : procedures) {
            String procedureIdentifier = procedure.getIdentifier();
             Collection<String> parentProcedures = procedureMap.get(procedureIdentifier);
//        }
//        for (Entry<String, Collection<String>> entry : procedureMap.entrySet()) {
//            String procedureIdentifier = entry.getKey();
//            Collection<String> parentProcedures = entry.getValue();
            getCache().addProcedure(procedureIdentifier);
            if (procedure.isSetName()) {
                getCache().addProcedureIdentifierHumanReadableName(procedureIdentifier, procedure.getName());
            }

            if (obsConstSupported) {
                Collection<ObservationConstellationInfo> ocis = getProcedureObservationConstellationInfo().get(procedureIdentifier);
                if (CollectionHelper.isNotEmpty(ocis)) {
                    getCache().setOfferingsForProcedure(procedureIdentifier, DatasourceCacheUpdateHelper
                            .getAllOfferingIdentifiersFromObservationConstellationInfos(ocis));
                    getCache().setObservablePropertiesForProcedure(procedureIdentifier, DatasourceCacheUpdateHelper
                            .getAllObservablePropertyIdentifiersFromObservationConstellationInfos(ocis));
                }
            } else {
                try {
                    getCache().setOfferingsForProcedure(procedureIdentifier, Sets.newHashSet(
                            daoFactory.getOfferingDAO().getOfferingIdentifiersForProcedure(procedureIdentifier, getSession())));
                } catch (OwsExceptionReport ce) {
                    LOGGER.error("Error while querying offering identifiers for procedure!", ce);
                    getErrors().add(ce);
                }
                getCache().setObservablePropertiesForProcedure(procedureIdentifier, Sets.newHashSet(
                        daoFactory.getObservablePropertyDAO().getObservablePropertyIdentifiersForProcedure(procedureIdentifier, getSession())));
            }

            setTypeProcedure(procedure);
            setAggregatedProcedure(procedure);
            setTypeInstanceProcedure(procedure);

            if (!CollectionHelper.isEmpty(parentProcedures)) {
                getCache().addParentProcedures(procedureIdentifier, parentProcedures);
            }
        }
        //time ranges
        //TODO querying procedure time extrema in a single query is definitely faster for a properly
        //     indexed Postgres db, but may not be true for all platforms. move back to multithreaded execution
        //     in ProcedureCacheUpdateTask if needed
        Map<String, TimeExtrema> procedureTimeExtrema = null;
        try {
            procedureTimeExtrema = daoFactory.getProcedureDAO().getProcedureTimeExtrema(getSession());
        } catch (OwsExceptionReport ce) {
            LOGGER.error("Error while querying offering time ranges!", ce);
            getErrors().add(ce);
        }
        if (!CollectionHelper.isEmpty(procedureTimeExtrema)) {
            for (Entry<String, TimeExtrema> entry : procedureTimeExtrema.entrySet()) {
                String procedureId = entry.getKey();
                TimeExtrema te = entry.getValue();
                getCache().setMinPhenomenonTimeForProcedure(procedureId, te.getMinPhenomenonTime());
                getCache().setMaxPhenomenonTimeForProcedure(procedureId, te.getMaxPhenomenonTime());
            }
        }
        LOGGER.debug("Finished executing ProcedureCacheUpdate (Single Threaded Tasks) ({})", getStopwatchResult());

        //multi-threaded execution
        LOGGER.debug("Executing ProcedureCacheUpdate (Multi-Threaded Tasks)");
        startStopwatch();
        super.execute();
        LOGGER.debug("Finished executing ProcedureCacheUpdate (Multi-Threaded Tasks) ({})", getStopwatchResult());
    }

}
