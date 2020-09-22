/*
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
package org.n52.sos.ds.hibernate;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

//    private static final String SIMPLE = "simple";
//
//    private static final String EREPORTING = "ereporting";
//
//    private static final String TRANSACTIONAL = "transactional";
//
//    private static final String DATASET = "dataset";
//
//    private static final String SAMPLING = "sampling";

    @Inject
    private SessionFactoryProvider provider;

//    @Bean
//    public EntityManagerFactory entityManagerFactory() {
//        Properties p = provider.getConfiguration()
//                .getProperties();
//        Map<String, String> map = new HashMap<>();
//        for (Entry<Object, Object> entry : p.entrySet()) {
//            map.put(entry.getKey()
//                    .toString(),
//                    entry.getValue()
//                            .toString());
//        }
//        return (EntityManagerFactory) sessionFactory();
//    }

    @Bean(name = {"sessionFactory", "entityManagerFactory"})
    public SessionFactory sessionFactory() {
        return provider.getConfiguration()
                .buildSessionFactory(provider.getServiceRegistry());
    }

    @Bean
    public DataSource dataSource(SessionFactory sessionFactory) {
        ConnectionProvider cp = sessionFactory.getSessionFactoryOptions()
                .getServiceRegistry()
                .getService(ConnectionProvider.class);
        return cp.unwrap(DataSource.class);
    }

//    private String getPersitenceUnit(Properties p) {
//        return p.contains(HibernateDatasourceConstants.HIBERNATE_DIRECTORY)
//                ? getPersitenceUnit(p.getProperty(HibernateDatasourceConstants.HIBERNATE_DIRECTORY))
//                : getPersitenceUnit("");
//    }
//
//    private String getPersitenceUnit(String value) {
//        return getConcept(value) + "-" + getExtension(value);
//    }
//
//    private String getConcept(String value) {
//        return value.contains(SIMPLE) ? SIMPLE : value.contains(EREPORTING) ? EREPORTING : TRANSACTIONAL;
//    }
//
//    private String getExtension(String value) {
//        return value.contains(SAMPLING) ? SAMPLING : DATASET;
//    }

}
