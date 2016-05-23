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
package org.n52.sos.ds.hibernate.util.procedure.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.util.AbstractConfiguringServiceLoaderRepository;

/**
 * Repository for {@link HibernateProcedureDescriptionGeneratorFactory}
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class HibernateProcedureDescriptionGeneratorRepository extends
        AbstractConfiguringServiceLoaderRepository<HibernateProcedureDescriptionGeneratorFactory> {

    private static class LazyHolder {
        private static final HibernateProcedureDescriptionGeneratorRepository INSTANCE =
                new HibernateProcedureDescriptionGeneratorRepository();

        private LazyHolder() {
        };
    }

    public static HibernateProcedureDescriptionGeneratorRepository getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final Map<HibernateProcedureDescriptionGeneratorFactoryKeyType, HibernateProcedureDescriptionGeneratorFactory> factories =
            new HashMap<HibernateProcedureDescriptionGeneratorFactoryKeyType, HibernateProcedureDescriptionGeneratorFactory>(
                    0);

    private HibernateProcedureDescriptionGeneratorRepository() {
        super(HibernateProcedureDescriptionGeneratorFactory.class, false);
        load(false);
    }

    @Override
    protected void processConfiguredImplementations(final Set<HibernateProcedureDescriptionGeneratorFactory> factory)
            throws ConfigurationException {
        this.factories.clear();
        for (final HibernateProcedureDescriptionGeneratorFactory aFactory : factory) {
            for (final HibernateProcedureDescriptionGeneratorFactoryKeyType factoryKeyType : aFactory
                    .getHibernateProcedureDescriptionGeneratorFactoryKeyTypes()) {

                this.factories.put(factoryKeyType, aFactory);
            }
        }
        // TODO check for encoder/decoder used by converter
    }

    public HibernateProcedureDescriptionGeneratorFactory getFactory(final String descriptionFormat) {
        return getFactory(new HibernateProcedureDescriptionGeneratorFactoryKeyType(descriptionFormat));
    }

    public HibernateProcedureDescriptionGeneratorFactory getFactory(
            final HibernateProcedureDescriptionGeneratorFactoryKeyType key) {
        return (HibernateProcedureDescriptionGeneratorFactory) factories.get(key);
    }

    /**
     * Checks if a factory is available to generate the description
     * 
     * @param descriptionFormat
     *            Default format
     * @return If a factory is available
     */
    public boolean hasHibernateProcedureDescriptionGeneratorFactory(final String descriptionFormat) {
        return getFactory(new HibernateProcedureDescriptionGeneratorFactoryKeyType(descriptionFormat)) != null;
    }

}
