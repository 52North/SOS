/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds.hibernate.util.procedure.enrich;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.n52.sos.ds.FeatureQueryHandlerQueryObject;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.service.Configurator;
import org.n52.sos.util.SosHelper;

import com.google.common.collect.Sets;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class FeatureOfInterestEnrichment extends ProcedureDescriptionEnrichment {
    
    private Session session;
    
    public FeatureOfInterestEnrichment setSession(Session session) {
        this.session = checkNotNull(session);
        return this;

    }
    
    @Override
    public void enrich() throws OwsExceptionReport {
        Collection<String> featureOfInterestIDs = getFeatureOfInterestIDs();
        getDescription().addFeaturesOfInterest(featureOfInterestIDs);
        getDescription().addFeaturesOfInterest(getAbstractFeaturesMap(featureOfInterestIDs));
    }

    @Override
    public boolean isApplicable() {
        return super.isApplicable() && procedureSettings().isEnrichWithFeatures();
    }

    /**
     * Get featureOfInterests for procedure and version
     *
     * @return Collection with featureOfInterests
     *
     * @throws OwsExceptionReport If an error occurs
     */
    private Collection<String> getFeatureOfInterestIDs()
            throws OwsExceptionReport {
        Set<String> features = Sets.newHashSet();
        // add cache map for proc/fois and get fois for proc
        for (String offering : getCache().getOfferingsForProcedure(getIdentifier())) {
            // don't include features for offerings which this procedure is a
            // hidden child of
            if (!getCache().getHiddenChildProceduresForOffering(offering).contains(getIdentifier())) {
                features.addAll(getCache().getFeaturesOfInterestForOffering(offering));
            }
        }
        return SosHelper.getFeatureIDs(features, getVersion());
    }

    private Map<String, AbstractFeature> getAbstractFeaturesMap(Collection<String> featureOfInterestIDs) throws OwsExceptionReport {
        FeatureQueryHandlerQueryObject object = new FeatureQueryHandlerQueryObject();
        object.setFeatureIdentifiers(featureOfInterestIDs);
        object.setConnection(session);
        if (isSetLocale()) {
            object.setI18N(getLocale());
        }
        return Configurator.getInstance().getFeatureQueryHandler().getFeatures(object);
    }
}
