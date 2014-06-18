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

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.text.StrBuilder;
import org.n52.sos.ds.EnviroCarHibernateConstants;
import org.n52.sos.ds.hibernate.ogm.envirocar.entities.ObservableProperty;
import org.n52.sos.util.Constants;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class EnviroCarMongoDBDatasoure extends MongoDBDatasource {
    
    private String DIALECT_NAME = "MongoDB4Envirocar";
    
    protected static final String ENVIROCAR_DATABASE_DESCRIPTION =
            "Set this to the name of the database you want to use for SOS. The default name vor EnviroCar is 'envirocar'";
    
    
    public EnviroCarMongoDBDatasoure() {
        setDatabaseDefault("envirocar");
        setDatabaseDescription(ENVIROCAR_DATABASE_DESCRIPTION);
    }
    
    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
        Properties p = super.getDatasourceProperties(settings);
        Set<String> annotadedClasses = Sets.newHashSet(ObservableProperty.class.getCanonicalName());
        p.put(HIBERNATE_ANNOTADED_CLASSES, Joiner.on(Constants.COLON_STRING).join(annotadedClasses));
        return p;
    }
    
    @Override
    public String getDatasourceDaoIdentifier() {
        return EnviroCarHibernateConstants.ENVIROCAR_DATASOURCE_DAO_IDENTIFIER;
    }
    
    @Override
    public String getDialectName() {
        return DIALECT_NAME;
    }
    
}
