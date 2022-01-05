/*
 * Copyright (C) 2012-2022 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds;

import java.util.Collection;
import java.util.Map;

import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.util.ReferencedEnvelope;

/**
 * Interface for querying featurefInterest data from a data source
 *
 * @since 4.0.0
 *
 */
public interface FeatureQueryHandler {

    /**
     * Query feature data from data source for an identifier
     *
     * @param queryObject
     *            {@link FeatureQueryHandlerQueryObject} that holds the
     *            identifier
     * @return SOS representation of the FOI
     *
     *
     * @throws OwsExceptionReport if an error occurs
     */
    AbstractFeature getFeatureByID(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport;

    /**
     * Query feature data from data source for identifiers
     *
     * @param queryObject
     *            {@link FeatureQueryHandlerQueryObject} that holds the
     *            identifiers
     * @return SOS representation of the FOIs
     * @throws OwsExceptionReport if an error occurs
     */
    Collection<String> getFeatureIDs(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport;

    /**
     * Get feature data for identifiers and/or for a spatial filter
     *
     * @param queryObject
     *            {@link FeatureQueryHandlerQueryObject} that holds the
     *            identifiers and/or spatial filter
     * @return Map of identifier and SOS FOI representation
     *
     *
     * @throws OwsExceptionReport if an error occurs
     */
    Map<String, AbstractFeature> getFeatures(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport;

    /**
     * Query the envelope for feature ids
     *
     * @param queryObject
     *            {@link FeatureQueryHandlerQueryObject} that holds the
     *            identifiers
     * @return Envelope of requested FOI identifiers
     *
     * @throws OwsExceptionReport if an error occurs
     */
    ReferencedEnvelope getEnvelopeForFeatureIDs(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport;

    /**
     * FIXME Add javadoc to clarify the semantics of this method
     *
     * @param samplingFeature
     *            Feature to insert into datasource
     * @param connection
     *            Datasource connection
     * @return Identifier of the inserted feature
     * @throws OwsExceptionReport if an error occurs
     */
    String insertFeature(AbstractSamplingFeature samplingFeature, Object connection) throws OwsExceptionReport;

    int getStorageEPSG();

    int getStorage3DEPSG();

}
