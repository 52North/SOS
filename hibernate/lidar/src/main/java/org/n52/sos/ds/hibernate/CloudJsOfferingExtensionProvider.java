/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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

import java.util.Set;

import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swes.OfferingExtensionKey;
import org.n52.sos.ogc.swes.OfferingExtensionProvider;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.ogc.swes.SwesExtensions;

import com.google.common.collect.Sets;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionFactoryImpl;
import org.n52.lidar.importer.core.db.LidarPostgreDB;
import org.n52.lidar.importer.core.db.PostgresSettings;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public class CloudJsOfferingExtensionProvider implements OfferingExtensionProvider {

    Set<OfferingExtensionKey> providerKeys = Sets.newHashSet(new OfferingExtensionKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION, "CloudJS"));

    private final HibernateSessionHolder sessionHolder = new HibernateSessionHolder();

    public CloudJsOfferingExtensionProvider() {
    }

    @Override
    public Set<OfferingExtensionKey> getOfferingExtensionKeyTypes() {
        return providerKeys;
    }

    @Override
    public SwesExtensions getOfferingExtensions(String identifier) {
        SwesExtensions extensions = new SwesExtensions();
        extensions.addSwesExtension(new SwesExtensionImpl<SweText>().setValue(
                new SweText().setValue(getCloudJsForOffering(identifier))));
        return extensions;
    }

    private String getCloudJsForOffering(String identifier) {
        try {
            Session session = sessionHolder.getSession();
            Properties properties = ((SessionFactoryImpl) session.getSessionFactory()).getProperties();
            String password = properties.getProperty("hibernate.connection.password");
            String username = properties.getProperty("hibernate.connection.username");
            String url = properties.getProperty("hibernate.connection.url");
            
            CloudjsEntity uniqueResult = (CloudjsEntity) session.createCriteria(CloudjsEntity.class)
                    .add(Restrictions.eq("offering", identifier))
                    .uniqueResult();
            
            //LidarPostgreDB db = new LidarPostgreDB(
            //      new PostgresSettings(url, "192.168.99.1", username, password));
            // TODO get cloud.js for offering
            //return db.getCloudJS(identifier);

            return uniqueResult.getCloudjs();
        } catch (OwsExceptionReport ex) {
            Logger.getLogger(CloudJsOfferingExtensionProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean hasExtendedOfferingFor(String identifier) {
        return true;
    }


}
