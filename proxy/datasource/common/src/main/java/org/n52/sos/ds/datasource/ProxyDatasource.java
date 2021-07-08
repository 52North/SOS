/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.ds.datasource;

import java.util.Set;

import org.n52.iceland.ds.Datasource;

import com.google.common.collect.Sets;

public interface ProxyDatasource extends Datasource {

    String SPRING_PROFILE = "proxy";

    String PROXY_HOST_KEY = "proxy.host";

    String PROXY_HOST_DEFAULT_VALUE = "http://localhost";

    String PROXY_PATH_KEY = "proxy.path";

    String PROXY_PATH_DEFAULT_VALUE = "/path";

    String PROXY_HOST_TITLE = "Proxy Service Host";

    String PROXY_PATH_TITLE = "Proxy Servic Path";

    @Override
    default Set<String> getSpringProfiles() {
        return Sets.newHashSet(SPRING_PROFILE);
    }

}
