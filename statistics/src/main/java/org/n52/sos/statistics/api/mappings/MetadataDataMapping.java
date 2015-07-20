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
package org.n52.sos.statistics.api.mappings;

import org.n52.sos.statistics.api.parameters.AbstractEsParameter;
import org.n52.sos.statistics.api.parameters.Description;
import org.n52.sos.statistics.api.parameters.Description.InformationOrigin;
import org.n52.sos.statistics.api.parameters.Description.Operation;
import org.n52.sos.statistics.api.parameters.ElasticsearchTypeRegistry;
import org.n52.sos.statistics.api.parameters.SingleEsParameter;

public class MetadataDataMapping {

    // ---- METADATA type ----
    public static final String METADATA_TYPE_NAME = "mt";
    public static final String METADATA_ROW_ID = "1";
    public static final AbstractEsParameter METADATA_CREATION_TIME_FIELD = new SingleEsParameter("mt-creation-time", new Description(
            InformationOrigin.Computed, Operation.Metadata, "Creation time of the Elasticsearch index"), ElasticsearchTypeRegistry.dateField);

    public static final AbstractEsParameter METADATA_UPDATE_TIME_FIELD = new SingleEsParameter("mt-update-time", new Description(
            InformationOrigin.Computed, Operation.Metadata, "Update time of the Elasticsearch metadata type"), ElasticsearchTypeRegistry.dateField);

    public static final AbstractEsParameter METADATA_VERSION_FIELD = new SingleEsParameter("mt-version", new Description(InformationOrigin.Computed,
            Operation.Metadata, "Monoton increasing version field. The deployment schema and the Elasticsearch schema version must match"),
            ElasticsearchTypeRegistry.integerField);

    public static final AbstractEsParameter METADATA_UUIDS_FIELD = new SingleEsParameter("mt-uuids", new Description(InformationOrigin.Computed,
            Operation.Metadata, "List of unique user IDs"), ElasticsearchTypeRegistry.stringField);

}
