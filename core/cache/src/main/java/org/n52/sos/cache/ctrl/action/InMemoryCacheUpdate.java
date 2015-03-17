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
package org.n52.sos.cache.ctrl.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.n52.sos.cache.ContentCacheUpdate;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO add log statements to all protected methods! TODO extract sub classes
 * for insertion updates
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 * 
 */
public abstract class InMemoryCacheUpdate extends ContentCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCacheUpdate.class);

    /**
     * Get a list of all SosSamplingFeatures contained in the abstract feature.
     * 
     * @param abstractFeature
     *            the abstract feature
     * 
     * @return a list of all sampling features
     */
    protected List<SamplingFeature> sosFeaturesToList(AbstractFeature abstractFeature) {
        if (abstractFeature instanceof FeatureCollection) {
            return getAllFeaturesFrom((FeatureCollection) abstractFeature);
        } else if (abstractFeature instanceof SamplingFeature) {
            return Collections.singletonList((SamplingFeature) abstractFeature);
        } else {
            String errorMessage =
                    String.format("Feature Type \"%s\" not supported.", abstractFeature != null ? abstractFeature
                            .getClass().getName() : abstractFeature);
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage); // TODO change
                                                              // type of
                                                              // exception to
                                                              // OER?
        }
    }

    private List<SamplingFeature> getAllFeaturesFrom(FeatureCollection featureCollection) {
        List<SamplingFeature> features = new ArrayList<SamplingFeature>(featureCollection.getMembers().size());
        for (AbstractFeature abstractFeature : featureCollection.getMembers().values()) {
            if (abstractFeature instanceof SamplingFeature) {
                features.add((SamplingFeature) abstractFeature);
            } else if (abstractFeature instanceof FeatureCollection) {
                features.addAll(getAllFeaturesFrom((FeatureCollection) abstractFeature));
            }
        }
        return features;
    }

    /**
     * Creates the Envelope for all passed sampling features.
     * 
     * @param samplingFeatures
     *            the sampling features
     * 
     * @return the envelope for all features
     */
    protected Envelope createEnvelopeFrom(List<SamplingFeature> samplingFeatures) {
        Envelope featureEnvelope = new Envelope();
        for (SamplingFeature samplingFeature : samplingFeatures) {
            if (samplingFeature.isSetGeometry()) {
                    featureEnvelope.expandToInclude(samplingFeature.getGeometry().getEnvelopeInternal());
            }
        }
        return featureEnvelope;
    }

    @Override
    public String toString() {
        return String.format("%s [cache=%s]", getClass().getName(), getCache());
    }
}
