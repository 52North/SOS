/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.ogm.cfg.OgmProperties;
import org.n52.sos.config.SettingDefinitionProvider;
import org.n52.sos.config.settings.IntegerSettingDefinition;
import org.n52.sos.config.settings.StringSettingDefinition;
import org.n52.sos.ds.Datasource;
import org.n52.sos.ds.HibernateDatasourceConstants;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.StringHelper;

public abstract class AbstractHibernateOgmDatasource extends AbstractHibernateCoreDatasource {
    
    protected abstract String getProvider();
    
    @Override
    public String getConnectionProviderIdentifier() {
        return HibernateDatasourceConstants.OGM_CONNECTION_PROVIDER_IDENTIFIER;
    }
    
    @Override
    public String getDatasourceDaoIdentifier() {
        return HibernateDatasourceConstants.OGM_DATASOURCE_DAO_IDENTIFIER;
    }
    
//    connetion_timeout

    
    @Override
    public Properties getDatasourceProperties(final Map<String, Object> settings) {
        final Properties p = new Properties();
        p.put(HibernateConstants.CONNECTION_USERNAME, settings.get(USERNAME_KEY));
        p.put(HibernateConstants.CONNECTION_PASSWORD, settings.get(PASSWORD_KEY));
//        p.put(HibernateConstants.CONNECTION_URL, toURL(settings));
        p.put(HibernateConstants.CONNECTION_PROVIDER_CLASS, C3P0_CONNECTION_POOL);
        p.put(HibernateConstants.C3P0_MIN_SIZE, settings.get(MIN_POOL_SIZE_KEY).toString());
        p.put(HibernateConstants.C3P0_MAX_SIZE, settings.get(MAX_POOL_SIZE_KEY).toString());
        p.put(HibernateConstants.C3P0_IDLE_TEST_PERIOD, "1");
        p.put(HibernateConstants.C3P0_ACQUIRE_INCREMENT, "1");
        p.put(HibernateConstants.C3P0_TIMEOUT, "0");
        p.put(HibernateConstants.C3P0_MAX_STATEMENTS, "0");

        return p;
    }

    protected abstract String[] parseURL(String url);
    
}