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
package hibernate.spatial.initiator;

import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.service.Service;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.spatial.HibernateSpatialConfiguration;
import org.hibernate.spatial.dialect.oracle.ConnectionFinder;
import org.hibernate.spatial.integration.SpatialInitiator;

public class SpatialInitiator52N extends SpatialInitiator {

    @Override
    public Service initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
        HibernateSpatialConfiguration configuration = configure(registry);
        return new SpatialDialectFactory52N(configuration);
    }

    private HibernateSpatialConfiguration configure(ServiceRegistry serviceRegistry) {
        ConfigurationService configService = serviceRegistry.getService(ConfigurationService.class);
        ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
        return new HibernateSpatialConfiguration(readOgcStrict(configService),
                readConnectionFinder(configService, classLoaderService));
    }

    /**
     * Reads the configured property (if present), otherwise returns null
     */
    private Boolean readOgcStrict(ConfigurationService configService) {
        String ogcStrictKey = HibernateSpatialConfiguration.AvailableSettings.OGC_STRICT;
        return configService.getSetting(ogcStrictKey, new ConfigurationService.Converter<Boolean>() {
            @Override
            public Boolean convert(Object value) {
                return Boolean.parseBoolean(value.toString());
            }
        }, null);
    }

    /**
     * Reads the configured property (if present), otherwise returns null
     */
    private ConnectionFinder readConnectionFinder(ConfigurationService configService,
            ClassLoaderService classLoaderService) {
        String cfKey = HibernateSpatialConfiguration.AvailableSettings.CONNECTION_FINDER;
        String className = configService.getSetting(cfKey, new ConfigurationService.Converter<String>() {
            @Override
            public String convert(Object value) {
                if (value instanceof String) {
                    return (String) value;
                }
                return value.toString();
            }
        }, null);

        if (className == null) {
            return null;
        }

        try {
            return (ConnectionFinder) classLoaderService.classForName(className).newInstance();
        } catch (Exception e) {
            throw new HibernateException(" Could not instantiate ConnectionFinder: " + className, e);
        }

    }

}
