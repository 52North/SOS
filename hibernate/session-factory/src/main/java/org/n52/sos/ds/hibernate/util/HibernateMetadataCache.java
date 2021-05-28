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
package org.n52.sos.ds.hibernate.util;

import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.hibernate.Session;

import com.google.common.collect.ImmutableSet;

public final class HibernateMetadataCache {
    private static HibernateMetadataCache instance;

    private static final Object LOCK = new Object();

    private final Set<String> supportedEntities;

    private Metamodel classMetadata;

    private HibernateMetadataCache(Session session) {
        this.classMetadata = initClassMetadata(session);
        this.supportedEntities = initSupportedEntities(classMetadata);
    }

    private Metamodel initClassMetadata(Session session) {
        return session.getEntityManagerFactory().getMetamodel();
        // return
        // ImmutableMap.copyOf(session.getSessionFactory().getAllClassMetadata());
    }

    private Set<String> initSupportedEntities(Metamodel classMetadata) {
        return ImmutableSet
                .copyOf(classMetadata.getEntities().stream().map(e -> e.getName()).collect(Collectors.toSet()));
        // return ImmutableSet.copyOf(classMetadata.keySet());
    }

    public boolean isColumnSupported(Class<?> entityClass, String column) {
        if (isEntitySupported(entityClass)) {
            EntityType<?> metadata = this.classMetadata.entity(entityClass);
            for (String propertyName : metadata.getAttributes().stream().map(a -> a.getName())
                    .collect(Collectors.toSet())) {
                if (propertyName.equals(column)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isEntitySupported(Class<?> entityClass) {
        return entityClass != null
                && (isEntitySupported(entityClass.getName()) || isEntitySupported(entityClass.getSimpleName()));
    }

    public boolean isEntitySupported(String entityClass) {
        return this.supportedEntities.contains(entityClass);
    }

    public static void init(Session session) {
        synchronized (LOCK) {
            instance = new HibernateMetadataCache(session);
        }
    }

    public static HibernateMetadataCache getInstance() {
        synchronized (LOCK) {
            if (instance != null) {
                return instance;
            } else {
                throw new IllegalStateException("Not initialized");
            }
        }
    }
}
