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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.cache.DatasourceCacheUpdateHelper;
import org.n52.sos.ds.hibernate.dao.ObservablePropertyDAO;
import org.n52.sos.ds.hibernate.dao.ObservationConstellationDAO;
import org.n52.sos.ds.hibernate.dao.OfferingDAO;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.entities.TObservableProperty;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ObservationConstellationInfo;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        ObservablePropertyDAO observablePropertyDAO = new ObservablePropertyDAO();
        Map<ObservableProperty, Collection<ObservableProperty>> observablePropertyHierarchy = observablePropertyDAO.getObservablePropertyHierarchy(getSession());
        List<ObservableProperty> ops = observablePropertyDAO.getObservablePropertyObjects(getSession());
//        Set<String> childObservableProperties = new HashSet<>(observablePropertyHierarchy.size());
//
//        for (Collection<ObservableProperty> children1: observablePropertyHierarchy.values()) {
//            for (ObservableProperty observableProperty1 : children1) {
//                childObservableProperties.add(observableProperty1.getIdentifier());
//            }
//        }


        // if ObservationConstellation is supported load them all at once,
        // otherwise query obs directly
        if (HibernateHelper.isEntitySupported(ObservationConstellation.class)) {
            ObservationConstellationDAO observationConstellationDAO = new ObservationConstellationDAO();
            Map<String, Collection<ObservationConstellationInfo>> ociMap = ObservationConstellationInfo.mapByObservableProperty(observationConstellationDAO.getObservationConstellationInfo(getSession()));

            for (ObservableProperty observableProperty : observablePropertyHierarchy.keySet()) {
                String observablePropertyIdentifier = observableProperty.getIdentifier();
                Collection<ObservableProperty> children = observablePropertyHierarchy.get(observableProperty);
                boolean isParent = !children.isEmpty();

                if (observableProperty.isSetName()) {
                	getCache().addObservablePropertyIdentifierHumanReadableName(observablePropertyIdentifier, observableProperty.getName());
                }

                if (!observableProperty.isHiddenChild()) {
                    Collection<ObservationConstellationInfo> ocis = ociMap.get(observablePropertyIdentifier);
                    if (CollectionHelper.isNotEmpty(ocis)) {
                        getCache().setOfferingsForObservableProperty(observablePropertyIdentifier, DatasourceCacheUpdateHelper.getAllOfferingIdentifiersFromObservationConstellationInfos(ocis));
                        getCache().setProceduresForObservableProperty(observablePropertyIdentifier, DatasourceCacheUpdateHelper.getAllProcedureIdentifiersFromObservationConstellationInfos(ocis));
                    }
                }

                if (isParent) {
                    getCache().addCompositePhenomenon(observablePropertyIdentifier);
                    for (ObservableProperty child : children) {
                        getCache().addCompositePhenomenonForObservableProperty(child.getIdentifier(), observablePropertyIdentifier);
                        getCache().addObservablePropertyForCompositePhenomenon(observablePropertyIdentifier, child.getIdentifier());
                    }

                }
            }
        } else {
            OfferingDAO offeringDAO = new OfferingDAO();
            ProcedureDAO procedureDAO = new ProcedureDAO();
            for (ObservableProperty op : observablePropertyHierarchy.keySet()) {
                String observableProperty = op.getIdentifier();
                try {
                    getCache().setOfferingsForObservableProperty(observableProperty, offeringDAO.getOfferingIdentifiersForObservableProperty(observableProperty, getSession()));
                } catch (OwsExceptionReport e) {
                    getErrors().add(e);
                }
                try {
                    getCache().setProceduresForObservableProperty(observableProperty, procedureDAO.getProcedureIdentifiersForObservableProperty(observableProperty, getSession()));
                } catch (OwsExceptionReport owse) {
                    getErrors().add(owse);
                }
            }
        }
        
        try {
            for (ObservableProperty observableProperty : observablePropertyDAO.getPublishedObservableProperty(getSession())) {
                String identifier = observableProperty.getIdentifier();
                getCache().addPublishedObservableProperty(identifier);
                Set<String> parents = new HashSet<>();
                getParents(parents, observableProperty);
                getCache().addPublishedObservableProperties(parents);
            }
        } catch (CodedException e) {
           getErrors().add(e);
        }
        LOGGER.debug("Executing ObservablePropertiesCacheUpdate ({})", getStopwatchResult());
    }
    
    private void getParents(Set<String> parents, ObservableProperty observableProperty) {
        if (observableProperty instanceof TObservableProperty && ((TObservableProperty)observableProperty).getParents() != null) {
            for (ObservableProperty parent : ((TObservableProperty)observableProperty).getParents()) {
                parents.add(parent.getIdentifier());
                getParents(parents, parent);
            }
        }
    }
    
}
