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
package org.n52.sos.ds.hibernateutil.feature.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxyHelper;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.SosHelper;

public class FeatureOfInterestStrategy extends AbstractFeatureCreationStrategy {

    public FeatureOfInterestStrategy(int storageEPSG, int storage3depsg) {
        super(storageEPSG, storage3depsg);
    }

    @Override
    public boolean apply(FeatureOfInterest feature) {
        return FeatureOfInterest.class.equals(HibernateProxyHelper.getClassWithoutInitializingProxy(feature));
   }

    @Override
    public AbstractFeature create(FeatureOfInterest f, Locale i18n, String version, Session s) throws OwsExceptionReport {
        FeatureOfInterestDAO featureOfInterestDAO = new FeatureOfInterestDAO();
        final CodeWithAuthority identifier = featureOfInterestDAO.getIdentifier(f);
        if (!SosHelper.checkFeatureOfInterestIdentifierForSosV2(f.getIdentifier(), version)) {
            identifier.setValue(null);
        }
        final SamplingFeature sampFeat = new SamplingFeature(identifier);
        addNameAndDescription(i18n, f, sampFeat, featureOfInterestDAO);
        sampFeat.setGeometry(createGeometry(f, s));
        sampFeat.setFeatureType(f.getFeatureOfInterestType().getFeatureOfInterestType());
        sampFeat.setUrl(f.getUrl());
        if (f.isSetDescriptionXml()) {
            sampFeat.setXmlDescription(f.getDescriptionXml());
        }
        final Set<FeatureOfInterest> parentFeatures = f.getParents();
        if (parentFeatures != null && !parentFeatures.isEmpty()) {
            final List<AbstractFeature> sampledFeatures = new ArrayList<AbstractFeature>(parentFeatures.size());
            for (final FeatureOfInterest parentFeature : parentFeatures) {
                sampledFeatures.add(create(parentFeature, i18n, version, s));
            }
            sampFeat.setSampledFeatures(sampledFeatures);
        }
        return sampFeat;
    }

}
