/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
import org.n52.sos.ds.hibernate.cache.AbstractQueueingDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.cache.DatasourceCacheUpdateHelper;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDescriptionFormatDAO;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationConstellationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * @author Shane StClair <shane@axiomalaska.com>
 *
 * @since 4.0.0
 */
public class ProcedureCacheUpdate extends AbstractQueueingDatasourceCacheUpdate<ProcedureCacheUpdateTask> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureCacheUpdate.class);

    private static final String THREAD_GROUP_NAME = "procedure-cache-update";

    private final ProcedureDAO procedureDAO = new ProcedureDAO();

    private final OfferingDAO offeringDAO = new OfferingDAO();

    private final ObservablePropertyDAO observablePropertyDAO = new ObservablePropertyDAO();

    private Map<String, Collection<String>> procedureMap;

    private Map<String,Collection<ObservationConstellationInfo>> procObsConstInfoMap;

    /**
     * constructor
     * @param threads Thread count
     */
    public ProcedureCacheUpdate(int threads) {
        super(threads, THREAD_GROUP_NAME);
    }

    private Map<String,Collection<ObservationConstellationInfo>> getProcedureObservationConstellationInfo() {
        if (procObsConstInfoMap == null) {
            procObsConstInfoMap = ObservationConstellationInfo.mapByProcedure(
                new ObservationConstellationDAO().getObservationConstellationInfo(getSession()));
        }
        return procObsConstInfoMap;
    }

    private Map<String, Collection<String>> getProcedureMap() {
        if (procedureMap == null) {
            procedureMap = procedureDAO.getProcedureIdentifiers(getSession());
        }
        return procedureMap;
    }

    private void getProcedureDescriptionFormat() {
        getCache().setRequestableProcedureDescriptionFormat(new ProcedureDescriptionFormatDAO().getProcedureDescriptionFormat(getSession()));
    }

    @Override
    protected ProcedureCacheUpdateTask[] getUpdatesToExecute() {
        Collection<ProcedureCacheUpdateTask> procedureUpdateTasks = Lists.newArrayList();
        Set<String> procedureIdentifiers = getProcedureMap().keySet();
        for (String procedureIdentifier : procedureIdentifiers) {
            procedureUpdateTasks.add(new ProcedureCacheUpdateTask(procedureIdentifier));
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

        Map<String, Collection<String>> procedureMap = procedureDAO.getProcedureIdentifiers(getSession());
        List<Procedure> procedures = procedureDAO.getProcedureObjects(getSession());
        for (Procedure procedure : procedures) {
        	String procedureIdentifier = procedure.getIdentifier();
        	 Collection<String> parentProcedures = procedureMap.get(procedureIdentifier);
//		}
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
                            offeringDAO.getOfferingIdentifiersForProcedure(procedureIdentifier, getSession())));
                } catch (OwsExceptionReport ce) {
                    LOGGER.error("Error while querying offering identifiers for procedure!", ce);
                    getErrors().add(ce);
                }
                getCache().setObservablePropertiesForProcedure(procedureIdentifier, Sets.newHashSet(
                        observablePropertyDAO.getObservablePropertyIdentifiersForProcedure(procedureIdentifier, getSession())));
            }

            if (!CollectionHelper.isEmpty(parentProcedures)) {
                getCache().addParentProcedures(procedureIdentifier, parentProcedures);
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
