/*
 * Copyright (C) 2012-2023 52°North Spatial Information Research GmbH
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
package org.n52.sos.proxy.harvest;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.n52.janmayen.event.EventBus;
import org.n52.sensorweb.server.db.factory.ServiceEntityFactory;
import org.n52.sensorweb.server.db.query.DatasetQuerySpecifications;
import org.n52.sensorweb.server.db.repositories.core.DatasetRepository;
import org.n52.sensorweb.server.helgoland.adapters.da.CRUDRepository;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.PlatformEntity;
import org.n52.series.db.beans.ServiceEntity;
import org.slf4j.Logger;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

public interface Harvester {

    default ServiceEntity getServiceEntity() {
        ServiceEntity serviceEntity = getServiceEntityFactory().getServiceEntity();
        if (serviceEntity != null && (serviceEntity.getConnector() == null || serviceEntity.getConnector()
                .isEmpty())) {
            serviceEntity.setConnector(getConnectorName());
        }
        return serviceEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    default ServiceEntity getOrInsertServiceEntity() {
        ServiceEntity serviceEntity = getServiceEntity();
        serviceEntity.setId(null);
        return unproxy(getCRUDRepository().insertService(serviceEntity));
    }

    default Specification<DatasetEntity> getDatasetServicQS(ServiceEntity service) {
        DatasetQuerySpecifications dQs = getCRUDRepository().getDatasetQuerySpecification();
        return dQs.matchServices(Long.toString(service.getId())).and(dQs.notNullIdentifier());
    }

    default List<DatasetEntity> getAllDatasets(ServiceEntity service) {
        return getDatasetRepository().findAll(getDatasetServicQS(service));
    }

    default Map<String, DatasetEntity> getIdentifierDatasetMap(ServiceEntity service) {
        Map<String, DatasetEntity> datasets = new LinkedHashMap<>();
        getAllDatasets(service).forEach(d -> {
           datasets.put(d.getIdentifier(), d);
        });
        return datasets;
    }

    @Transactional(rollbackFor = Exception.class)
    default void deleteObsoleteData(Map<String, DatasetEntity> datasets) {
        if (!datasets.isEmpty()) {
            getLogger().debug("Start removing datasets/timeSeries!");
            Set<Long> features = new LinkedHashSet<>();
            Set<Long> platforms = new LinkedHashSet<>();
            for (Entry<String, DatasetEntity> entry : datasets.entrySet()) {
                getLogger().debug("Removing timeSeries with id '{}' from metadata!", entry.getKey());
                DatasetEntity dataset = entry.getValue();
                if (getProxyHelper().isDeletePhysically()) {
                    getCRUDRepository().removeRelatedData(dataset);
                    features.add(dataset.getFeature().getId());
                    platforms.add(dataset.getPlatform().getId());
                    PlatformEntity platform = dataset.getPlatform();
                    getDatasetRepository().delete(dataset);

                } else {
                    dataset.setDeleted(true);
                    getDatasetRepository().saveAndFlush(dataset);
                }
            }
            getCRUDRepository().removeFeature(features);
            getCRUDRepository().removePlatform(platforms);
            getLogger().debug("Finished removing datasets/timeSeries!");
        }
    }

    <T> T unproxy(T entity);

    CRUDRepository getCRUDRepository();

    ServiceEntityFactory getServiceEntityFactory();

    DatasetRepository getDatasetRepository();

    String getConnectorName();

    AbstractProxyHelper getProxyHelper();

    EventBus getEventBus();

    Logger getLogger();

}
