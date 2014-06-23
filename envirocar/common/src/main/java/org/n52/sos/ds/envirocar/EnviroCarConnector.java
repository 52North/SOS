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
package org.n52.sos.ds.envirocar;

import java.util.Properties;

import org.envirocar.server.mongo.guice.MongoConnectionModule;
import org.envirocar.server.mongo.guice.MongoConverterModule;
import org.envirocar.server.mongo.guice.MongoDaoModule;
import org.envirocar.server.mongo.guice.MongoMappedClassesModule;
import org.envirocar.server.mongo.guice.MongoModule;
import org.n52.sos.ds.ConnectionProvider;
import org.n52.sos.ds.ConnectionProviderException;
import org.n52.sos.ds.DataConnectionProvider;
import org.n52.sos.ds.EnviroCarConstants;
import org.n52.sos.ds.envirocar.guice.EnviroCarDaoModule;
import org.n52.sos.exception.ConfigurationException;

import com.google.inject.Injector;
import com.google.inject.Guice;

public class EnviroCarConnector implements DataConnectionProvider {
    
    private Injector injector;

    @Override
    public Object getConnection() throws ConnectionProviderException {
        return injector.getInstance(EnviroCarDaoFactory.class);
    }

    @Override
    public void returnConnection(Object connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void initialize(Properties properties) throws ConfigurationException {
        if (injector == null) {
        injector = Guice.createInjector(new MongoModule(), new MongoMappedClassesModule(), new MongoDaoModule(),
                        new MongoConverterModule(), new MongoConnectionModule(properties), new EnviroCarDaoModule());
        }
    }

    @Override
    public void cleanup() {
//       MongoDB.getMongoClient().close();

    }

    @Override
    public String getConnectionProviderIdentifier() {
        return EnviroCarConstants.ENVIROCAR_CONNECTION_PROVIDER_IDENTIFIER;
    }

}
