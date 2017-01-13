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
package org.n52.sos.ds.hibernate.create;

import java.util.Locale;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.feature.FeatureVisitor;
import org.n52.sos.ds.hibernate.entities.feature.Specimen;
import org.n52.sos.ds.hibernate.entities.feature.inspire.EnvironmentalMonitoringFacility;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;

public class HibernateFeatureVisitor implements FeatureVisitor<AbstractFeature> { 

    private Locale i18n;
    private Session session;
    private String version;
    private int storageEPSG;
    private int storage3DEPSG;
    
    public HibernateFeatureVisitor(Locale i18n, String version, int storageEPSG, int storage3DEPSG, Session session) {
        this.i18n = i18n;
        this.version = version;
        this.session = session;
        this.storageEPSG = storageEPSG;
        this.storage3DEPSG = storage3DEPSG;
    }
    
    
    public AbstractFeature visit(FeatureOfInterest f) throws OwsExceptionReport {
        if (f instanceof Specimen) {
            return visit((Specimen)f);
        } else if (f instanceof EnvironmentalMonitoringFacility) {
            return visit((EnvironmentalMonitoringFacility)f);
        }
        return new FeatureOfInterestCreator(storageEPSG, storage3DEPSG).create(f, i18n, version, session);
    }

    public AbstractFeature visit(Specimen f) throws OwsExceptionReport {
        return new SpecimenCreator(storageEPSG, storage3DEPSG).create(f, i18n, version, session);
    }

    public AbstractFeature visit(EnvironmentalMonitoringFacility f) throws OwsExceptionReport {
        return new EnvironmentalMonitoringFacilityCreator(storageEPSG, storage3DEPSG).create(f, i18n, version, session);
    }
}
