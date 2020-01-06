/**
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
package org.n52.sos.ds.hibernate.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.dao.FeatureOfInterestDAO;
import org.n52.sos.ds.hibernate.entities.feature.AbstractFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.feature.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.parameter.feature.FeatureParameterAdder;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.SosHelper;

public abstract class AbstractFeatureOfInerestCreator<T extends FeatureOfInterest> extends AbstractFeatureCreator<T> {

        public AbstractFeatureOfInerestCreator(int storageEPSG, int storage3depsg) {
            super(storageEPSG, storage3depsg);
        }

        public AbstractFeature createFeature(FeatureOfInterest f, Locale i18n, String version, Session s) throws OwsExceptionReport {
            FeatureOfInterestDAO featureOfInterestDAO = new FeatureOfInterestDAO();
            final CodeWithAuthority identifier = featureOfInterestDAO.getIdentifier(f);
            if (!SosHelper.checkFeatureOfInterestIdentifierForSosV2(f.getIdentifier(), version)) {
                identifier.setValue(null);
            }
            final AbstractFeature absFeat = getFeatureType(identifier);
            addNameAndDescription(i18n, f, absFeat, featureOfInterestDAO, s);
            if (absFeat instanceof AbstractSamplingFeature) {
                AbstractSamplingFeature absSampFeat = (AbstractSamplingFeature) absFeat;
                absSampFeat.setGeometry(createGeometryFrom(f, s));
                absSampFeat.setFeatureType(f.getFeatureOfInterestType().getFeatureOfInterestType());
                absSampFeat.setUrl(f.getUrl());
                if (f.isSetDescriptionXml()) {
                    absSampFeat.setXmlDescription(f.getDescriptionXml());
                }
                addParameter(absSampFeat, f);
                final Set<AbstractFeatureOfInterest> parentFeatures = f.getParents();
                if (parentFeatures != null && !parentFeatures.isEmpty()) {
                    final List<AbstractFeature> sampledFeatures = new ArrayList<AbstractFeature>(parentFeatures.size());
                    for (final AbstractFeatureOfInterest parentFeature : parentFeatures) {
                        sampledFeatures.add(parentFeature.accept(new HibernateFeatureVisitor(i18n, version, getStorageEPSG(), getStorage3DEPSG(), s)));
                    }
                    absSampFeat.setSampledFeatures(sampledFeatures);
                }
            }
            return absFeat;
        }
        
        protected void addParameter(AbstractSamplingFeature absSampFeat,FeatureOfInterest f) throws OwsExceptionReport {
            new FeatureParameterAdder(absSampFeat, f).add();
        }
        
        protected abstract AbstractFeature getFeatureType(CodeWithAuthority identifier);
}
