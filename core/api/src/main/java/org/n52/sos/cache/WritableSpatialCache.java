/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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

import org.locationtech.jts.geom.Envelope;

import org.n52.shetland.util.ReferencedEnvelope;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface WritableSpatialCache extends CacheConstants {
    /**
     * Sets the default EPSG code.
     *
     * @param defaultEPSGCode the new default ESPG code
     */
    void setDefaultEPSGCode(int defaultEPSGCode);

    /**
     * Sets the specified envelope for the specified offering.
     *
     * @param offering the offering
     * @param envelope the envelope
     */
    void setEnvelopeForOffering(String offering, ReferencedEnvelope envelope);

    /**
     * Sets the specified envelope for the specified offering.
     *
     * @param offering the offering
     * @param envelope the envelope
     */
    void setSpatialFilteringProfileEnvelopeForOffering(String offering,
                                                       ReferencedEnvelope envelope);

    /**
     * Updates the Spatial Filtering Profile envelope for the specified offering to include the specified envelope.
     *
     * @param offering the offering
     * @param envelope the envelope to include
     */
    void updateEnvelopeForOffering(String offering, Envelope envelope);

    /**
     * Updates the Spatial Filtering Profile envelope for the specified offering to include the specified envelope.
     *
     * @param offering the offering
     * @param envelope the envelope to include
     */
    void updateSpatialFilteringProfileEnvelopeForOffering(String offering,
                                                          Envelope envelope);

    /**
     * Remove the envelope for the specified offering.
     *
     * @param offering the offering
     */
    void removeEnvelopeForOffering(String offering);

    /**
     * Remove the Spatial Filtering Profile envelope for the specified offering.
     *
     * @param offering the offering
     */
    void removeSpatialFilteringProfileEnvelopeForOffering(String offering);

    /**
     * Sets the global spatial envelope.
     *
     * @param globalEnvelope the new spatial envelope
     */
    void setGlobalEnvelope(ReferencedEnvelope globalEnvelope);

    /**
     * Updates the global spatial envelope to include the specified envelope.
     *
     * @param e the envelope
     */
    void updateGlobalEnvelope(Envelope e);

    /**
     * Recalculates the global spatial envelope based on the current offering spatial envelopes.
     */
    void recalculateGlobalEnvelope();

    /**
     * Reset the offerings to envelope relation.
     */
    void clearEnvelopeForOfferings();

    /**
     * Reset the offerings to Spatial Filtering Profile envelope relation.
     */
    void clearSpatialFilteringProfileEnvelopeForOfferings();

}
