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
package org.n52.sos.service;

public interface SosSettings {

    String SENSOR_DIRECTORY = "service.sensorDirectory";
    String MAX_GET_OBSERVATION_RESULTS = "service.maxGetObservationResults";
    String DEREGISTER_JDBC_DRIVER = "service.jdbc.deregister";
    String STRICT_SPATIAL_FILTERING_PROFILE  = "service.strictSpatialFilteringProfile";
    String EXPOSE_CHILD_OBSERVABLE_PROPERTIES = "service.exposeChildObservableProperties";
    String LIST_ONLY_PARENT_OFFERINGS = "service.capabilities.listOnlyParentOfferings";
    String UPDATE_FEATURE_GEOMETRY = "service.updateFeatureGeometry";
    String CACHE_FILE_FOLDER = "service.cacheFileFolder";
    String CREATE_FOI_GEOM_FROM_SAMPLING_GEOMS = "service.createFeatureGeometryFromSamplingGeometries";
    String ALLOW_TEMPLATE_WITHOUT_PROCEDURE_FEATURE = "service.allowTemplateWithoutProcedureAndFeature";
    String INCLUDE_RESULT_TIME_FOR_MERGING = "service.includeResultTimeForMerging";
    String CHECK_FOR_DUPLICITY = "service.checkForDuplicity";
    String STA_SUPPORTS_URLS = "service.sta.supports.urls";
}
