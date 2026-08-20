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
package org.n52.sos.ds.hibernate.util;

import org.hibernate.MappingException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypesScanner;
import org.springframework.util.ClassUtils;

/**
 * Registers the Hibernate mapped classes of a package with a native Hibernate bootstrap.
 *
 * Hibernate only auto-detects mapped classes when bootstrapped through JPA, where the persistence unit root supplies
 * the archive to scan. A native bootstrap has to enumerate them, so the scanning is delegated to Spring's
 * {@link PersistenceManagedTypesScanner} - the same implementation behind
 * {@code LocalContainerEntityManagerFactoryBean#setPackagesToScan}.
 *
 */
public final class EntityScanner {

    private EntityScanner() {
    }

    /**
     * Register every mapped class found in the given packages with the configuration.
     *
     * @param configuration
     *            the configuration to add the mapped classes to
     * @param packagesToScan
     *            the packages to scan
     */
    public static void applyTo(Configuration configuration, String... packagesToScan) {
        PersistenceManagedTypes types = scan(packagesToScan);
        ClassLoader classLoader = EntityScanner.class.getClassLoader();
        for (String className : types.getManagedClassNames()) {
            try {
                configuration.addAnnotatedClass(ClassUtils.forName(className, classLoader));
            } catch (ClassNotFoundException ex) {
                throw new MappingException("Failed to load mapped class " + className, ex);
            }
        }
        types.getManagedPackages().forEach(configuration::addPackage);
    }

    /**
     * Register every mapped class found in the given packages with the metadata sources.
     *
     * @param sources
     *            the metadata sources to add the mapped classes to
     * @param packagesToScan
     *            the packages to scan
     */
    public static void applyTo(MetadataSources sources, String... packagesToScan) {
        PersistenceManagedTypes types = scan(packagesToScan);
        types.getManagedClassNames().forEach(sources::addAnnotatedClassName);
        types.getManagedPackages().forEach(sources::addPackage);
    }

    private static PersistenceManagedTypes scan(String... packagesToScan) {
        return new PersistenceManagedTypesScanner(new DefaultResourceLoader()).scan(packagesToScan);
    }
}
