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
package org.n52.dataset.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

public class ProxyProfileExcludeFilterDelegator implements TypeFilter, EnvironmentAware {
    protected static final String PROXY = "proxy";
    private static final Logger LOG = LoggerFactory.getLogger(ProxyProfileExcludeFilterDelegator.class);
    private boolean isProxy;

    @Override
    public void setEnvironment(Environment environment) {
        if (environment != null) {
            LOG.trace("EnvironmentAware environment: ", environment.getClass().getName());
            if (environment instanceof ConfigurableEnvironment) {
                LOG.trace("EnvironmentAware profiles: ",
                        String.join(",", ((ConfigurableEnvironment) environment).getActiveProfiles()));
                for (String profile : ((ConfigurableEnvironment) environment).getActiveProfiles()) {
                    if (profile.equalsIgnoreCase(PROXY)) {
                        setIsProxy();
                    }
                }
            }
        } else {
            LOG.trace("Environment is null!");
        }
    }

    private void setIsProxy() {
        this.isProxy = true;
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
            throws IOException {
        String className = metadataReader.getClassMetadata().getClassName();
        if (className != null && className.equals("org.n52.sensorweb.server.db.repositories.proxy.ServiceRepository")) {
           return !isProxy;
        }
        return false;
    }

}
