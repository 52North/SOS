/*
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
package org.n52.sos.ds.cache.base;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.n52.io.request.IoParameters;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.PhenomenonEntity;
import org.n52.series.db.beans.dataset.DatasetType;
import org.n52.series.db.old.dao.DatasetDao;
import org.n52.sensorweb.server.db.old.dao.DbQuery;
import org.n52.series.db.old.dao.PhenomenonDao;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.sos.ds.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.cache.DatasourceCacheUpdateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class ObservablePropertiesCacheUpdate extends AbstractThreadableDatasourceCacheUpdate
        implements DatasourceCacheUpdateHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservablePropertiesCacheUpdate.class);

    @Override
    public void execute() {
        LOGGER.debug("Executing ObservablePropertiesCacheUpdate");
        startStopwatch();
        try {
            Collection<PhenomenonEntity> observableProperties =
                    new PhenomenonDao(getSession()).get(new DbQuery(IoParameters.createDefaults()));
            for (PhenomenonEntity observableProperty : observableProperties) {
                Collection<DatasetEntity> datasets =
                        new DatasetDao<>(getSession()).get(createDatasetDbQuery(observableProperty));
                String identifier = observableProperty.getIdentifier();

                if (observableProperty.isSetName()) {
                    getCache().addObservablePropertyIdentifierHumanReadableName(identifier,
                            observableProperty.getName());
                }
                if (observableProperty.hasChildren()) {
                    for (PhenomenonEntity child : observableProperty.getChildren()) {
                        getCache().addCompositePhenomenonForObservableProperty(child.getIdentifier(), identifier);
                        getCache().addObservablePropertyForCompositePhenomenon(identifier, child.getIdentifier());
                    }
                }

                if (datasets != null && !datasets.isEmpty()) {
                    if (datasets.stream().anyMatch(
                        d -> d.isPublished() || d.getDatasetType().equals(DatasetType.not_initialized))) {
                        getCache().addPublishedObservableProperty(identifier);
                    }
                    getCache().setOfferingsForObservableProperty(identifier,
                            getAllOfferingIdentifiersFromDatasets(datasets));
                    getCache().setProceduresForObservableProperty(identifier,
                            getAllProcedureIdentifiersFromDatasets(datasets));
                }
            }
        } catch (HibernateException he) {
            getErrors().add(new NoApplicableCodeException().causedBy(he)
                    .withMessage("Error while updating featureOfInterest cache!"));
        }
        LOGGER.debug("Executing ObservablePropertiesCacheUpdate ({})", getStopwatchResult());
    }

    private DbQuery createDatasetDbQuery(PhenomenonEntity observableProperty) {
        IoParameters parameters = IoParameters.createDefaults();
        return new DbQuery(parameters.extendWith(IoParameters.PHENOMENA, Long.toString(observableProperty.getId())));
    }
}
