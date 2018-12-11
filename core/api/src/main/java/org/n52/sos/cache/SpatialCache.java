/*
 * Copyright (C) 2012-2018 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.cache;

import java.util.Optional;
import java.util.Set;

import org.n52.shetland.util.ReferencedEnvelope;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface SpatialCache {
    /**
     * @return the default EPSG code
     */
    int getDefaultEPSGCode();

    /**
     * Get the envelope associated with the specified offering.
     *
     * @param offering the offering
     *
     * @return the envelope
     */
    ReferencedEnvelope getEnvelopeForOffering(String offering);

    /**
     * Get the Spatial Filtering Profile envelope associated with the specified offering.
     *
     * @param offering the offering
     *
     * @return the envelope
     */
    ReferencedEnvelope getSpatialFilteringProfileEnvelopeForOffering(String offering);

    /**
     * Checks whether the specified offering has a envelope.
     *
     * @param offering the offering
     *
     * @return {@code true} if it has a envelope
     */
    default boolean hasEnvelopeForOffering(String offering) {
        return Optional.ofNullable(getEnvelopeForOffering(offering))
                .filter(ReferencedEnvelope::isSetEnvelope).isPresent();
    }

    /**
     * Checks whether the specified offering has a Spatial Filtering Profile envelope.
     *
     * @param offering the offering
     *
     * @return {@code true} if it has a envelope
     */
    default boolean hasSpatialFilteringProfileEnvelopeForOffering(String offering) {
        return Optional.ofNullable(getSpatialFilteringProfileEnvelopeForOffering(offering))
                .filter(ReferencedEnvelope::isSetEnvelope).isPresent();
    }

    /**
     * @return the global spatial envelope (never null)
     */
    ReferencedEnvelope getGlobalEnvelope();

    /**
     * @return whether the global spatial envelope is set or not
     */
    default boolean hasGlobalEnvelope() {
        return Optional.ofNullable(getGlobalEnvelope())
                .filter(ReferencedEnvelope::isSetEnvelope).isPresent();
    }

    /**
     * @return all EPSG codes
     */
    Set<Integer> getEpsgCodes();

    /**
     * Checks whether the specified EPSG code exists.
     *
     * @param epsgCode the EPSG code
     *
     * @return {@code true} if it exists
     */
    default boolean hasEpsgCode(Integer epsgCode) {
        return getEpsgCodes().contains(epsgCode);
    }
}
