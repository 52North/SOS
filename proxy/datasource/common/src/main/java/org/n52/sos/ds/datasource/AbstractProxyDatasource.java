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
package org.n52.sos.ds.datasource;

import java.util.Map;
import java.util.Set;

import org.n52.sos.ds.hibernate.util.HibernateConstants;

import com.google.common.collect.Sets;

public abstract class AbstractProxyDatasource extends AbstractH2Datasource {

    public static final String SPRING_PROFILE = "proxy";

    public static final String PROXY_HOST_KEY = "proxy.host";

    public static final String PROXY_HOST_DEFAULT_VALUE = "http://localhost";

    public static final String PROXY_PATH_KEY = "proxy.path";

    public static final String PROXY_PATH_DEFAULT_VALUE = "/path";

    protected static final String PROXY_HOST_TITLE = "Proxy host";

    protected static final String PROXY_PATH_TITLE = "Proxy path";

    private static final long serialVersionUID = 1L;

    @Override
    public Set<String> getSpringProfiles() {
        return Sets.newHashSet(SPRING_PROFILE);
    }

    @Override
    public void validatePrerequisites(Map<String, Object> settings) {
        settings.put(DATABASE_CONCEPT_KEY, DatabaseConcept.TRANSACTIONAL.name());
        settings.put(FEATURE_CONCEPT_KEY, FeatureConcept.DEFAULT_FEATURE_CONCEPT.name());
        settings.put(DATABASE_EXTENSION_KEY, DatabaseExtension.DATASOURCE.name());
        settings.put(HibernateConstants.CONNECTION_USERNAME, DEFAULT_USERNAME);
        settings.put(HibernateConstants.CONNECTION_PASSWORD, DEFAULT_PASSWORD);
        super.validatePrerequisites(settings);
    }
}
