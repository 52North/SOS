/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.envirocar.cache.base;

import java.util.List;

import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.ogm.envirocar.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.ogm.envirocar.entities.ObservableProperty;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnviroCarObservablePropertiesCacheUpdate extends AbstractThreadableDatasourceCacheUpdate {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnviroCarObservablePropertiesCacheUpdate.class);

    @Override
    public void execute() {
        LOGGER.debug("Executing ObservablePropertiesCacheUpdate");
        startStopwatch();
        try {
            List<ObservableProperty> ops = new ObservablePropertyDAO().getIdentifier(getSession());
            for (ObservableProperty string : ops) {
                LOGGER.info(string.getId());
            }
        } catch (Exception e) {
            
            getErrors().add(new NoApplicableCodeException().causedBy(e));
        }

        //if ObservationConstellation is supported load them all at once, otherwise query obs directly
//        if (HibernateHelper.isEntitySupported(ObservationConstellation.class, getSession())) {
//            Map<String, Collection<ObservationConstellationInfo>> ociMap = ObservationConstellationInfo.mapByObservableProperty(
//                    new ObservationConstellationDAO().getObservationConstellationInfo(getSession()));
//            for (ObservableProperty op : ops) {
//                final String obsPropIdentifier = op.getIdentifier();
//                Collection<ObservationConstellationInfo> ocis = ociMap.get(obsPropIdentifier);
//                if (CollectionHelper.isNotEmpty(ocis)) {
//                    getCache().setOfferingsForObservableProperty(obsPropIdentifier,
//                            DatasourceCacheUpdateHelper.getAllOfferingIdentifiersFromObservationConstellationInfos(ocis));
//                    getCache().setProceduresForObservableProperty(obsPropIdentifier,
//                            DatasourceCacheUpdateHelper.getAllProcedureIdentifiersFromObservationConstellationInfos(ocis));
//                }
//            }
//        } else {
//            for (ObservableProperty op : ops) {
//                final String obsPropIdentifier = op.getIdentifier();
//                try {
//                    getCache().setOfferingsForObservableProperty(obsPropIdentifier,
//                            new OfferingDAO().getOfferingIdentifiersForObservableProperty(obsPropIdentifier, getSession()));
//                } catch (CodedException e) {
//                    getErrors().add(e);
//                }
//                getCache().setProceduresForObservableProperty(obsPropIdentifier,
//                        new ProcedureDAO().getProcedureIdentifiersForObservableProperty(obsPropIdentifier, getSession()));                
//            }
//        }
        LOGGER.debug("Executing ObservablePropertiesCacheUpdate ({})", getStopwatchResult());
    }

}
