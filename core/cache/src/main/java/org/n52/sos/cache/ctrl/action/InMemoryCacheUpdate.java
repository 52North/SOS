/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.cache.SosContentCacheUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

/**
 * TODO add log statements to all protected methods! TODO extract sub classes
 * for insertion updates
 *
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 *
 */
public abstract class InMemoryCacheUpdate extends SosContentCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCacheUpdate.class);

    /**
     * Get a list of all SosAbstractSamplingFeatures contained in the abstract feature.
     *
     * @param abstractFeature
     *            the abstract feature
     *
     * @return a list of all sampling features
     */
    protected List<AbstractSamplingFeature> sosFeaturesToList(AbstractFeature abstractFeature) {
        return asStream(abstractFeature).collect(Collectors.toList());
    }

    private Stream<AbstractSamplingFeature> asStream(FeatureCollection fc) {
        return fc.getMembers().values().stream().flatMap(this::asStream);
    }

    private Stream<AbstractSamplingFeature> asStream(AbstractFeature f) {
        if (f instanceof AbstractSamplingFeature) {
            return Stream.of((AbstractSamplingFeature) f);
        } else if (f instanceof FeatureCollection) {
            return asStream((FeatureCollection) f);
        } else {
            String errorMessage =
                    String.format("Feature Type \"%s\" not supported.", f != null ? f.getClass().getName() : "null");
            LOGGER.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }


    /**
     * Creates the Envelope for all passed sampling features.
     *
     * @param AbstractSamplingFeatures
     *            the sampling features
     *
     * @return the envelope for all features
     */
    protected Envelope createEnvelopeFrom(List<AbstractSamplingFeature> AbstractSamplingFeatures) {
        return AbstractSamplingFeatures.stream()
                .filter(AbstractSamplingFeature::isSetGeometry)
                .map(AbstractSamplingFeature::getGeometry)
                .map(Geometry::getEnvelopeInternal)
                .collect(Envelope::new,
                         Envelope::expandToInclude,
                         Envelope::expandToInclude);
    }

    @Override
    public String toString() {
        return String.format("%s [cache=%s]", getClass().getName(), getCache());
    }
}
