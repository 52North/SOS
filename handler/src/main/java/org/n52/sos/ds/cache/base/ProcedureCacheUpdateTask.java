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
package org.n52.sos.ds.cache.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.n52.iceland.exception.ows.concrete.GenericThrowableWrapperException;
import org.n52.io.request.IoParameters;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.series.db.dao.DbQuery;
import org.n52.shetland.ogc.gml.time.TimePeriod;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.cache.DatasourceCacheUpdateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
class ProcedureCacheUpdateTask extends AbstractThreadableDatasourceCacheUpdate {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcedureCacheUpdateTask.class);
    private ProcedureEntity procedure;
    private Collection<DatasetEntity> datasets = new HashSet<>();

    /**
     * Constructor. Note: never pass in Hibernate objects that have been loaded
     * by a session in a different thread *
     *
     * @param identifier
     *            Procedure identifier
     */
    ProcedureCacheUpdateTask(ProcedureEntity procedure, Collection<DatasetEntity> datasets) {
        this.procedure = procedure;
        this.datasets.clear();
        if (datasets != null) {
            this.datasets.addAll(datasets);
        }
    }

    protected void getProcedureInformationFromDbAndAddItToCacheMaps() throws OwsExceptionReport {
        if (datasets != null) {
            String identifier = procedure.getIdentifier();
            getCache().addProcedure(identifier);
            if (datasets != null && !datasets.isEmpty() && datasets.stream().anyMatch(d -> d.isPublished())) {
                getCache().addPublishedProcedure(identifier);
            }
            if (procedure.isSetName()) {
                getCache().addProcedureIdentifierHumanReadableName(identifier, procedure.getName());
            }
            getCache().setOfferingsForProcedure(identifier, DatasourceCacheUpdateHelper
                    .getAllOfferingIdentifiersFromDatasets(datasets));
            getCache().setObservablePropertiesForProcedure(identifier, DatasourceCacheUpdateHelper
                    .getAllObservablePropertyIdentifiersFromDatasets(datasets));

            if (procedure.hasParents()) {
                Collection<String> parents = getParents(procedure);
                getCache().addParentProcedures(identifier, parents);
                getCache().addPublishedProcedures(parents);
            }

            TimePeriod phenomenonTime = new TimePeriod();
            for (DatasetEntity dataset : datasets) {
                OfferingEntity offering = dataset.getOffering();
                    phenomenonTime.extendToContain(
                            new TimePeriod(offering.getPhenomenonTimeStart(), offering.getPhenomenonTimeEnd()));
            }
            getCache().setMinPhenomenonTimeForProcedure(identifier, phenomenonTime.getStart());
            getCache().setMaxPhenomenonTimeForProcedure(identifier, phenomenonTime.getEnd());
        }
    }

    private Collection<String> getParents(ProcedureEntity procedure) {
        Set<String> parentProcedures = Sets.newTreeSet();
        if (procedure.hasParents()) {
            for (ProcedureEntity parentEntity : procedure.getParents()) {
                parentProcedures.add(parentEntity.getIdentifier());
                parentProcedures.addAll(getParents(parentEntity));
            }
        }
        return parentProcedures;
    }

    private DbQuery createDatasetDbQuery(ProcedureEntity procedure) {
        Map<String, String> map = Maps.newHashMap();
        map.put(IoParameters.PROCEDURES, Long.toString(procedure.getId()));
        return new DbQuery(IoParameters.createFromSingleValueMap(map));
    }

    @Override
    public void execute() {
        try {
            getProcedureInformationFromDbAndAddItToCacheMaps();
        } catch (OwsExceptionReport owse) {
            getErrors().add(owse);
        } catch (Exception e) {
            getErrors().add(new GenericThrowableWrapperException(e)
                    .withMessage("Error while processing procedure cache update task!"));
        }
    }
}
