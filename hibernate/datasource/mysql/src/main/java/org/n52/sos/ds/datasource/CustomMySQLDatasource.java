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
package org.n52.sos.ds.datasource;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.n52.sos.config.SettingDefinition;

import com.google.common.collect.ImmutableSet;

public class CustomMySQLDatasource extends AbstractMySQLDatasource {
    private static final String DIALECT_NAME = "MySQL/MariaDB Custom Mapping";

    public CustomMySQLDatasource() {
        super(false);
        setUsernameDefault(USERNAME_DEFAULT_VALUE);
        setUsernameDescription(USERNAME_DESCRIPTION);
        setPasswordDefault(PASSWORD_DEFAULT_VALUE);
        setPasswordDescription(PASSWORD_DESCRIPTION);
        setDatabaseDefault(DATABASE_DEFAULT_VALUE);
        setDatabaseDescription(DATABASE_DESCRIPTION);
        setHostDefault(HOST_DEFAULT_VALUE);
        setHostDescription(HOST_DESCRIPTION);
        setPortDefault(PORT_DEFAULT_VALUE);
        setPortDescription(PORT_DESCRIPTION);
        super.setTransactional(false);
    }

    @Override
    public boolean needsSchema() {
        return false;
    }

    @Override
    public String getDialectName() {
        return DIALECT_NAME;
    }

    @Override
    public Set<SettingDefinition<?, ?>> getChangableSettingDefinitions(Properties current) {
        return filter(super.getChangableSettingDefinitions(current), ImmutableSet.of(OLD_CONCEPT_KEY, TRANSACTIONAL_KEY, SPATIAL_FILTERING_PROFILE_KEY, BATCH_SIZE_KEY));
    }




    @Override
    public Set<SettingDefinition<?, ?>> getSettingDefinitions() {
        return filter(super.getSettingDefinitions(), ImmutableSet.of(OLD_CONCEPT_KEY, TRANSACTIONAL_KEY, SPATIAL_FILTERING_PROFILE_KEY, BATCH_SIZE_KEY));
    }


    private Set<SettingDefinition<?,?>> filter(Set<SettingDefinition<?,?>> definitions, Set<String> keysToExclude) {
        Iterator<SettingDefinition<?, ?>> iterator = definitions.iterator();
        while(iterator.hasNext()) {
            if (keysToExclude.contains(iterator.next().getKey())) {
                iterator.remove();
            }
        }
        return definitions;
    }


}
