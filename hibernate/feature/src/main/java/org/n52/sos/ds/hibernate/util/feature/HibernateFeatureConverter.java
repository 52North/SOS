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
package org.n52.sos.ds.hibernate.util.feature;

import java.util.ArrayList;
import java.util.Locale;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.util.feature.create.EnvironmentalMonitoringFacilityStrategy;
import org.n52.sos.ds.hibernate.util.feature.create.FeatureCreationStrategy;
import org.n52.sos.ds.hibernate.util.feature.create.FeatureOfInterestStrategy;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Geometry;

public class HibernateFeatureConverter {
    
    private FeatureOfInterest feature;
    private Locale i18n;
    private Session session;
    private String version;
    private int storageEPSG;
    private int storage3DEPSG;

    public HibernateFeatureConverter(FeatureOfInterest feature, Locale i18n, String version, int storageEPSG,
            int storage3DEPSG, Session session) {
        this.feature = feature;
        this.i18n = i18n;
        this.version = version;
        this.session = session;
        this.storageEPSG = storageEPSG;
        this.storage3DEPSG = storage3DEPSG;
    }

    public Optional<AbstractFeature> create() throws OwsExceptionReport {
        Optional<FeatureCreationStrategy> strategy = getCreationStrategy(feature);
        if (strategy.isPresent()) {
            return Optional.fromNullable(strategy.get().create(feature, i18n, version, session));
        } else {
            return Optional.absent();
        }
    }

    public Optional<Geometry> createGeometry() throws OwsExceptionReport {
        Optional<FeatureCreationStrategy> strategy = getCreationStrategy(feature);
        if (strategy.isPresent()) {
            return Optional.fromNullable(strategy.get().createGeometry(feature, session));
        } else {
            return Optional.absent();
        }
    }

    private Optional<FeatureCreationStrategy> getCreationStrategy(FeatureOfInterest f) {
        for (FeatureCreationStrategy strategy : getCreationStrategies()) {
            if (strategy.apply(f)) {
                return Optional.of(strategy);
            }
        }
        return Optional.absent();
    }

    protected ArrayList<FeatureCreationStrategy> getCreationStrategies() {
        return Lists.<FeatureCreationStrategy>newArrayList(new FeatureOfInterestStrategy(storageEPSG, storage3DEPSG), new EnvironmentalMonitoringFacilityStrategy(storageEPSG, storage3DEPSG));
    }

}
