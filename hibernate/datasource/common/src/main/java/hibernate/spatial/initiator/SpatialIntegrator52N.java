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

import java.lang.reflect.Field;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.ServiceContributingIntegrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.spatial.GeometryType;
import org.hibernate.type.TypeResolver;

public class SpatialIntegrator52N implements ServiceContributingIntegrator {

    private static final String UNLOCK_ERROR_MSG = "SpatialIntegrator failed to unlock BasicTypeRegistry";

    @Override
    public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {
        addType(sessionFactory.getTypeResolver());
    }

    @Override
    public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory,
            SessionFactoryServiceRegistry serviceRegistry) {
        addType(metadata.getTypeResolver());
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        // do nothing.
    }

    @Override
    public void prepareServices(StandardServiceRegistryBuilder serviceRegistryBuilder) {
        serviceRegistryBuilder.addInitiator(new SpatialInitiator52N());
    }

    private void addType(TypeResolver typeResolver) {
        unlock(typeResolver);
        typeResolver.registerTypeOverride(GeometryType.INSTANCE);
        lock(typeResolver);
    }

    private void lock(TypeResolver typeResolver) {
        setLocked(typeResolver, true);
    }

    private void unlock(TypeResolver typeResolver) {
        setLocked(typeResolver, false);
    }

    private void setLocked(TypeResolver typeResolver, boolean locked) {
        try {
            Field registryFld = typeResolver.getClass().getDeclaredField("basicTypeRegistry");
            registryFld.setAccessible(true);
            Object registry = registryFld.get(typeResolver);
            Field lockedFld = registry.getClass().getDeclaredField("locked");
            lockedFld.setAccessible(true);
            lockedFld.setBoolean(registry, locked);
            lockedFld.setAccessible(false);
            registryFld.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(UNLOCK_ERROR_MSG, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(UNLOCK_ERROR_MSG, e);
        }

    }

}
