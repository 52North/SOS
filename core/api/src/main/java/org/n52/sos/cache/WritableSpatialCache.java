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
package org.n52.sos.cache;

import java.util.Collection;

import org.n52.sos.ogc.sos.SosEnvelope;

import com.vividsolutions.jts.geom.Envelope;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface WritableSpatialCache {
    /**
     * Sets the default EPSG code.
     *
     * @param defaultEPSGCode
     *                        the new default ESPG code
     */
    void setDefaultEPSGCode(int defaultEPSGCode);

    /**
     * Sets the specified envelope for the specified offering.
     *
     * @param offering
     *                 the offering
     * @param envelope
     *                 the envelope
     */
    void setEnvelopeForOffering(String offering, SosEnvelope envelope);

    /**
     * Sets the specified envelope for the specified offering.
     *
     * @param offering
     *                 the offering
     * @param envelope
     *                 the envelope
     */
    void setSpatialFilteringProfileEnvelopeForOffering(String offering,
                                                       SosEnvelope envelope);

    /**
     * Updates the Spatial Filtering Profile envelope for the specified offering
     * to include the specified envelope.
     *
     * @param offering
     *                 the offering
     * @param envelope
     *                 the envelope to include
     */
    void updateEnvelopeForOffering(String offering, Envelope envelope);

    /**
     * Updates the Spatial Filtering Profile envelope for the specified offering
     * to include the specified envelope.
     *
     * @param offering
     *                 the offering
     * @param envelope
     *                 the envelope to include
     */
    void updateSpatialFilteringProfileEnvelopeForOffering(String offering,
                                                          Envelope envelope);

    /**
     * Remove the envelope for the specified offering.
     *
     * @param offering
     *                 the offering
     */
    void removeEnvelopeForOffering(String offering);

    /**
     * Remove the Spatial Filtering Profile envelope for the specified offering.
     *
     * @param offering
     *                 the offering
     */
    void removeSpatialFilteringProfileEnvelopeForOffering(String offering);

    /**
     * Remove the specified epsg code.
     *
     * @param epsgCode
     *                 the epsg code
     */
    void removeEpsgCode(Integer epsgCode);

    /**
     * Remove the specified epsg codes.
     *
     * @param epsgCode
     *                 the epsg codes
     */
    void removeEpsgCodes(Collection<Integer> epsgCode);

    /**
     * Sets the global spatial envelope.
     *
     * @param globalEnvelope
     *                       the new spatial envelope
     */
    void setGlobalEnvelope(SosEnvelope globalEnvelope);

    /**
     * Updates the global spatial envelope to include the specified envelope.
     *
     * @param e
     *          the envelope
     */
    void updateGlobalEnvelope(Envelope e);

    /**
     * Recalculates the global spatial envelope based on the current offering
     * spatial envelopes.
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

    /**
     * Add the specified epsg code.
     *
     * @param epsgCode
     *                 the new epsg code
     */
    void addEpsgCode(Integer epsgCode);

    /**
     * Add the specified epsg codes.
     *
     * @param epsgCodes
     *                  the new epsg codes
     */
    void addEpsgCodes(Collection<Integer> epsgCodes);
}
