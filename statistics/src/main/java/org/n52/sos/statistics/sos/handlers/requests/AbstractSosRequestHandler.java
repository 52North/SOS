/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.n52.iceland.statistics.api.AbstractElasticSearchDataHolder;
import org.n52.iceland.statistics.api.interfaces.StatisticsServiceEventHandler;
import org.n52.iceland.statistics.api.interfaces.geolocation.IStatisticsLocationUtil;
import org.n52.iceland.statistics.api.mappings.ServiceEventDataMapping;
import org.n52.janmayen.net.IPAddress;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.sos.statistics.sos.models.ExtensionEsModel;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractSosRequestHandler<T extends OwsServiceRequest>
        extends AbstractElasticSearchDataHolder
        implements StatisticsServiceEventHandler<T> {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractSosRequestHandler.class);

    @Inject
    protected IStatisticsLocationUtil locationUtil;

    private AbstractSosRequestHandler<?> init(T request) {

        // Global constants
        put(ServiceEventDataMapping.SR_SERVICE_FIELD, request.getService());
        put(ServiceEventDataMapping.SR_VERSION_FIELD, request.getVersion());
        put(ServiceEventDataMapping.SR_OPERATION_NAME_FIELD, request.getOperationName());
        put(ServiceEventDataMapping.SR_LANGUAGE_FIELD, request.getRequestedLanguage());

        // requestcontext
        IPAddress ip = locationUtil.resolveOriginalIpAddress(request.getRequestContext());
        put(ServiceEventDataMapping.SR_IP_ADDRESS_FIELD, ip);
        put(ServiceEventDataMapping.SR_GEO_LOC_FIELD, locationUtil.ip2SpatialData(ip));
        if (request.getRequestContext() != null) {
            put(ServiceEventDataMapping.SR_PROXIED_REQUEST_FIELD,
                    request.getRequestContext().getForwardedForChain().isPresent());
            put(ServiceEventDataMapping.SR_CONTENT_TYPE, request.getRequestContext().getContentType().orElse(null));
            put(ServiceEventDataMapping.SR_ACCEPT_TYPES, request.getRequestContext().getAcceptType().orElse(null));
        }
        // extensions
        if (request.getExtensions() != null && request.getExtensions().getExtensions() != null) {
            put(ServiceEventDataMapping.SR_EXTENSIONS,
                    ExtensionEsModel.convert(request.getExtensions().getExtensions()));
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> resolveAsMap(OwsServiceRequest request) {
        init((T) request);
        resolveConcreteRequest((T) request);
        return Collections.unmodifiableMap(dataMap);
    }

    protected abstract void resolveConcreteRequest(T request);

}
