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

import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.cache.DatasourceCacheUpdateHelper;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationConstellationInfo;
import org.n52.sos.util.CollectionHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.ogc.ows.OwsExceptionReport;

/**
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class ObservablePropertiesCacheUpdate extends AbstractThreadableDatasourceCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservablePropertiesCacheUpdate.class);

    @Override
    public void execute() {
        LOGGER.debug("Executing ObservablePropertiesCacheUpdate");
        startStopwatch();
        List<ObservableProperty> ops = new ObservablePropertyDAO().getObservablePropertyObjects(getSession());
        // if ObservationConstellation is supported load them all at once,
        // otherwise query obs directly
        if (HibernateHelper.isEntitySupported(ObservationConstellation.class)) {
            Map<String, Collection<ObservationConstellationInfo>> ociMap =
                    ObservationConstellationInfo.mapByObservableProperty(new ObservationConstellationDAO()
                            .getObservationConstellationInfo(getSession()));
            for (ObservableProperty op : ops) {
                final String obsPropIdentifier = op.getIdentifier();
                if (op.isSetName()) {
                	getCache().addObservablePropertyIdentifierHumanReadableName(obsPropIdentifier, op.getName());
                }
                Collection<ObservationConstellationInfo> ocis = ociMap.get(obsPropIdentifier);
                if (CollectionHelper.isNotEmpty(ocis)) {
                    getCache().setOfferingsForObservableProperty(
                            obsPropIdentifier,
                            DatasourceCacheUpdateHelper
                                    .getAllOfferingIdentifiersFromObservationConstellationInfos(ocis));
                    getCache().setProceduresForObservableProperty(
                            obsPropIdentifier,
                            DatasourceCacheUpdateHelper
                                    .getAllProcedureIdentifiersFromObservationConstellationInfos(ocis));
                }
            }
        } else {
            for (ObservableProperty op : ops) {
                final String obsPropIdentifier = op.getIdentifier();
                try {
                    getCache().setOfferingsForObservableProperty(
                            obsPropIdentifier,
                            new OfferingDAO().getOfferingIdentifiersForObservableProperty(obsPropIdentifier,
                                    getSession()));
                } catch (OwsExceptionReport e) {
                    getErrors().add(e);
                }
                try {
                    getCache().setProceduresForObservableProperty(
                            obsPropIdentifier,
                            new ProcedureDAO().getProcedureIdentifiersForObservableProperty(obsPropIdentifier,
                                    getSession()));
                } catch (OwsExceptionReport owse) {
                    getErrors().add(owse);
                }
            }
        }
        LOGGER.debug("Executing ObservablePropertiesCacheUpdate ({})", getStopwatchResult());
    }
}
