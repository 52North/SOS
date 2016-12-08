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
package org.n52.sos.ds.hibernate.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class DatasourceCacheUpdateHelper {

    private DatasourceCacheUpdateHelper() {

    }

    public static Set<String> getAllOfferingIdentifiersFrom(
            Collection<ObservationConstellation> observationConstellations) {
        Set<String> offerings = new HashSet<String>(observationConstellations.size());
        for (ObservationConstellation oc : observationConstellations) {
            offerings.add(oc.getOffering().getIdentifier());
        }
        return offerings;
    }

    public static Set<String> getAllOfferingIdentifiersFromDatasetEntitys(
            Collection<DatasetEntity> datasets) {
        Set<String> offerings = new HashSet<String>(datasets.size());
        for (DatasetEntity dataset : datasets) {
            offerings.add(dataset.getOffering().getDomainId());
        }
        return offerings;
    }

    public static Set<String> getAllProcedureIdentifiersFrom(
            Collection<ObservationConstellation> observationConstellations) {
        Set<String> procedures = new HashSet<String>(observationConstellations.size());
        for (ObservationConstellation oc : observationConstellations) {
            procedures.add(oc.getProcedure().getIdentifier());
        }
        return procedures;
    }

    public static Set<String> getAllProcedureIdentifiersFromDatasetEntitys(
            Collection<DatasetEntity> datasets) {
        return getAllProcedureIdentifiersFromDatasetEntitys(datasets, null);
    }

    public static Set<String> getAllProcedureIdentifiersFromDatasetEntitys(
            Collection<DatasetEntity> datasets, ProcedureFlag procedureFlag) {
        Set<String> procedures = new HashSet<String>(datasets.size());
        for (DatasetEntity dataset : datasets) {
            boolean addProcedure = false;
            if (procedureFlag == null) {
                //add all procedures
                addProcedure = true;
            } else {
                if (procedureFlag.equals(ProcedureFlag.PARENT) && !dataset.getProcedure().hasParents()) {
                    addProcedure = true;
                } else if (procedureFlag.equals(ProcedureFlag.HIDDEN_CHILD) && dataset.getProcedure().hasParents()) {
                    addProcedure = true;
                }
            }
            if (addProcedure) {
                procedures.add(dataset.getProcedure().getDomainId());
            }
        }
        return procedures;
    }

    public static Set<String> getAllObservablePropertyIdentifiersFrom(
            Collection<ObservationConstellation> observationConstellations) {
        Set<String> observableProperties = new HashSet<String>(observationConstellations.size());
        for (ObservationConstellation oc : observationConstellations) {
            observableProperties.add(oc.getObservableProperty().getIdentifier());
        }
        return observableProperties;
    }

    @SuppressWarnings("rawtypes")
    public static Collection<String> getAllOfferingIdentifiersFromDatasets(Collection<DatasetEntity> datasets) {
        Set<String> offerings = Sets.newTreeSet();
        for (DatasetEntity dataset : datasets) {
            offerings.add(dataset.getOffering().getDomainId());
        }
        return offerings;
    }

    @SuppressWarnings("rawtypes")
    public static Collection<String> getAllProcedureIdentifiersFromDatasets(Collection<DatasetEntity> datasets) {
        Set<String> procedures = Sets.newTreeSet();
        for (DatasetEntity dataset : datasets) {
            procedures.add(dataset.getProcedure().getDomainId());
        }
        return procedures;
    }

    @SuppressWarnings("rawtypes")
    public static Set<String> getAllObservablePropertyIdentifiersFromDatasets(Collection<DatasetEntity> datasets) {
        Set<String> observableProperties = Sets.newTreeSet();
        for (DatasetEntity dataset : datasets) {
            observableProperties.add(dataset.getPhenomenon().getDomainId());
        }
        return observableProperties;
    }

    @SuppressWarnings("rawtypes")
    public static Collection<String> getAllFeatureIdentifiersFromDatasets(Collection<DatasetEntity> datasets) {
        Set<String> features = Sets.newTreeSet();
        for (DatasetEntity dataset : datasets) {
            features.add(dataset.getFeature().getDomainId());
        }
        return features;
    }


    public static Collection<? extends String> getAllProcedureIdentifiersFromDatasets(
            Collection<org.n52.series.db.beans.DatasetEntity> datasets, ProcedureFlag parent) {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static Map<String,Collection<DatasetEntity>> mapByOffering(
            Collection<DatasetEntity> datasets) {
        Map<String,Collection<DatasetEntity>> map = Maps.newHashMap();
        for (DatasetEntity dataset : datasets) {
            CollectionHelper.addToCollectionMap(dataset.getOffering().getDomainId(), dataset, map);
        }
        return map;
    }

    @SuppressWarnings("rawtypes")
    public static Map<String,Collection<DatasetEntity>> mapByProcedure(
            Collection<DatasetEntity> datasets) {
        Map<String,Collection<DatasetEntity>> map = Maps.newHashMap();
        for (DatasetEntity dataset : datasets) {
            CollectionHelper.addToCollectionMap(dataset.getProcedure().getDomainId(), dataset, map);
        }
        return map;
    }

    @SuppressWarnings("rawtypes")
    public static Map<String,Collection<DatasetEntity>> mapByObservableProperty(
            Collection<DatasetEntity> datasets) {
        Map<String,Collection<DatasetEntity>> map = Maps.newHashMap();
        for (DatasetEntity dataset : datasets) {
            CollectionHelper.addToCollectionMap(dataset.getPhenomenon().getDomainId(), dataset, map);
        }
        return map;
    }
}
