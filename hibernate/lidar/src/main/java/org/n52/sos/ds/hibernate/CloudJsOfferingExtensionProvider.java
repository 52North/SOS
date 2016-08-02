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
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swes.OfferingExtensionKey;
import org.n52.sos.ogc.swes.OfferingExtensionProvider;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.GeometryHandler;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Envelope;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.SessionFactoryImpl;
import org.n52.sos.ogc.gml.GmlConstants;
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
        return getCloudJsForOffering(identifier);
    }

    private SwesExtensions getCloudJsForOffering(String identifier) {
        SwesExtensions extensions = new SwesExtensions();
        
//        extensions.addSwesExtension(new SwesExtensionImpl<SweText>().setValue(
//                new SweText().setValue(getCloudJsForOffering(identifier))));
        try {
            Session session = sessionHolder.getSession();
            
            CloudjsEntity entity = (CloudjsEntity) session.createCriteria(CloudjsEntity.class)
                    .add(Restrictions.eq("offering", identifier))
                    .uniqueResult();
            
            extensions.addSwesExtension(createCubicBox(entity));
            extensions.addSwesExtension(createScaleSpacingHierarchy(entity));
            return extensions;
        } catch (OwsExceptionReport ex) {
            Logger.getLogger(CloudJsOfferingExtensionProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private SwesExtension<SosEnvelope> createCubicBox(CloudjsEntity entity) {
        SosEnvelope envelope = new SosEnvelope();
        envelope.setEnvelope(new Envelope(entity.getLx(), entity.getUx(), entity.getLy(), entity.getUy()));
        envelope.setMinZ(entity.getLz()).setMaxZ(entity.getUz());
        envelope.setSrid(GeometryHandler.getInstance().getStorageEPSG());
        return new SwesExtensionImpl<SosEnvelope>().setValue(envelope).setNamespace(GmlConstants.NS_GML_32);
    }

    private SwesExtension<SweDataRecord> createScaleSpacingHierarchy(CloudjsEntity entity) {
        SweDataRecord record = new SweDataRecord();
        record.setDefinition("spacingScaleHierarchy");
        record.addField(new SweField("spacing", new SweCount().setValue(entity.getSpacing()).setDefinition("spacing")));
        record.addField(new SweField("scale", new SweQuantity().setValue(entity.getScale()).setDefinition("scale")));
        record.addField(new SweField("hierarchyStepSize", new SweCount().setValue(entity.getHierarchystepsize()).setDefinition("hierarchyStepSize")));
        return new SwesExtensionImpl<SweDataRecord>().setValue(record);
    }

    @Override
    public boolean hasExtendedOfferingFor(String identifier) {
        return true;
    }


}
