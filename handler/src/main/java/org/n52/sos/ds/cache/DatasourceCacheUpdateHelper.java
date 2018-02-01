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
package org.n52.sos.ds.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.util.CollectionHelper;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @since 4.0.0
 *
 */
public class DatasourceCacheUpdateHelper {

    private DatasourceCacheUpdateHelper() {

    }

    public static Set<String> getAllOfferingIdentifiersFromDatasetEntitys(
            Collection<DatasetEntity> datasets) {
        Set<String> offerings = new HashSet<String>(datasets.size());
        for (DatasetEntity dataset : datasets) {
            if (dataset.getOffering() != null && !Strings.isNullOrEmpty(dataset.getOffering().getIdentifier())) {
                offerings.add(dataset.getOffering().getIdentifier());
            }
        }
        return offerings;
    }

    public static Set<String> getAllProcedureIdentifiersFromDatasetEntitys(
            Collection<DatasetEntity> datasets) {
        return getAllProcedureIdentifiersFromDatasetEntitys(datasets, null);
    }

    public static Set<String> getAllProcedureIdentifiersFromDatasetEntitys(
            Collection<DatasetEntity> datasets, ProcedureFlag procedureFlag) {
        Set<String> procedures = new HashSet<String>(datasets.size());
        for (DatasetEntity dataset : datasets) {
            if (dataset.getProcedure() != null) {
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
                    procedures.add(dataset.getProcedure().getIdentifier());
                }
            }
        }
        return procedures;
    }

    public static Collection<String> getAllOfferingIdentifiersFromDatasets(Collection<DatasetEntity> datasets) {
        Set<String> offerings = Sets.newTreeSet();
        for (DatasetEntity dataset : datasets) {
            if (dataset.getOffering() != null && !Strings.isNullOrEmpty(dataset.getOffering().getIdentifier())) {
                offerings.add(dataset.getOffering().getIdentifier());
            }
        }
        return offerings;
    }

    public static Collection<String> getAllProcedureIdentifiersFromDatasets(Collection<DatasetEntity> datasets) {
        Set<String> procedures = Sets.newTreeSet();
        for (DatasetEntity dataset : datasets) {
            if (dataset.getProcedure() != null && !Strings.isNullOrEmpty(dataset.getProcedure().getIdentifier())) {
                procedures.add(dataset.getProcedure().getIdentifier());
            }
        }
        return procedures;
    }

    public static Set<String> getAllObservablePropertyIdentifiersFromDatasets(Collection<DatasetEntity> datasets) {
        Set<String> observableProperties = Sets.newTreeSet();
        for (DatasetEntity dataset : datasets) {
            if (dataset.getPhenomenon() != null && !Strings.isNullOrEmpty(dataset.getPhenomenon().getIdentifier())) {
                observableProperties.add(dataset.getPhenomenon().getIdentifier());
            }
        }
        return observableProperties;
    }

    public static Collection<String> getAllFeatureIdentifiersFromDatasets(Collection<DatasetEntity> datasets) {
        Set<String> features = Sets.newTreeSet();
        for (DatasetEntity dataset : datasets) {
            if (dataset.getFeature() != null && !Strings.isNullOrEmpty(dataset.getFeature().getIdentifier())) {
                features.add(dataset.getFeature().getIdentifier());
            }
        }
        return features;
    }

    public static Collection<? extends String> getAllProcedureIdentifiersFromDatasets(
            Collection<DatasetEntity> datasets, ProcedureFlag parent) {
        Set<String> procedures = Sets.newTreeSet();
        for (DatasetEntity dataset : datasets) {
            if (dataset.getProcedure() != null && !Strings.isNullOrEmpty(dataset.getProcedure().getIdentifier())) {
                if (ProcedureFlag.HIDDEN_CHILD.equals(parent)) {
                    addChilds(dataset.getProcedure(), procedures);
                } else {
                    procedures.add(dataset.getProcedure().getIdentifier());
                }
            }
        }
        return procedures;
    }

    private static void addChilds(ProcedureEntity procedure, Collection<String> procedures) {
        if (procedure.hasChildren()) {
            for (ProcedureEntity child : procedure.getChildren()) {
                procedures.add(child.getIdentifier());
            }
        }
    }

    public static Map<String,Collection<DatasetEntity>> mapByOffering(
            Collection<DatasetEntity> datasets) {
        Map<String,Collection<DatasetEntity>> map = Maps.newHashMap();
        for (DatasetEntity dataset : datasets) {
            if (dataset.getOffering() != null && !Strings.isNullOrEmpty(dataset.getOffering().getIdentifier())) {
                CollectionHelper.addToCollectionMap(dataset.getOffering().getIdentifier(), dataset, map);
            }
        }
        return map;
    }

    public static Map<String,Collection<DatasetEntity>> mapByProcedure(
            Collection<DatasetEntity> datasets) {
        Map<String,Collection<DatasetEntity>> map = Maps.newHashMap();
        for (DatasetEntity dataset : datasets) {
            if (dataset.getProcedure() != null && !Strings.isNullOrEmpty(dataset.getProcedure().getIdentifier())) {
                CollectionHelper.addToCollectionMap(dataset.getProcedure().getIdentifier(), dataset, map);
            }
        }
        return map;
    }

    public static Map<String,Collection<DatasetEntity>> mapByObservableProperty(
            Collection<DatasetEntity> datasets) {
        Map<String,Collection<DatasetEntity>> map = Maps.newHashMap();
        for (DatasetEntity dataset : datasets) {
            if (dataset.getPhenomenon() != null && !Strings.isNullOrEmpty(dataset.getPhenomenon().getIdentifier())) {
                CollectionHelper.addToCollectionMap(dataset.getPhenomenon().getIdentifier(), dataset, map);
            }
        }
        return map;
    }
}
