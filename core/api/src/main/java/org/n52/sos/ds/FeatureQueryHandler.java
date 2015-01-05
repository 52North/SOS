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
package org.n52.sos.ds;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosEnvelope;

/**
 * Interface for querying featurefInterest data from a data source
 * 
 * @since 4.0.0
 * 
 */
public interface FeatureQueryHandler extends DatasourceDaoIdentifier {

    /**
     * Query feature data from data source for an identifier
     * 
     * @param featureID
     *            FOI identifier
     * @param connection
     *            Data source connection
     * @param version
     *            SOS version
     * @return SOS representation of the FOI
     * 
     * 
     * @throws OwsExceptionReport
     */
    @Deprecated
    AbstractFeature getFeatureByID(String featureID, Object connection, String version) throws OwsExceptionReport;

    /**
     * Query feature data from data source for an identifier
     * 
     * @param featureID
     *            FOI identifier
     * @param connection
     *            Data source connection
     * @param version
     *            SOS version
     * @param responseSrid
     *            response srid for feature geometry, if negative not
     *            transformation
     * @return SOS representation of the FOI
     * 
     * 
     * @throws OwsExceptionReport
     */
    AbstractFeature getFeatureByID(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport;

    /**
     * Query feature identifier from data source for a spatial filter
     * 
     * @param filter
     *            Spatial filter
     * @param connection
     *            Data source connection
     * @return List of FOI identifieres
     * 
     * 
     * @throws OwsExceptionReport
     */
    @Deprecated
    Collection<String> getFeatureIDs(SpatialFilter filter, Object connection) throws OwsExceptionReport;
    
    Collection<String> getFeatureIDs(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport;

    /**
     * Get feature data for identifiers and/or for a spatial filter
     * 
     * @param foiIDs
     *            FOI identifiers
     * @param list
     *            Spatial filter
     * @param connection
     *            Data source connection
     * @param version
     *            SOS version
     * @return Map of identifier and SOS FOI representation
     * 
     * 
     * @throws OwsExceptionReport
     */
    @Deprecated
    Map<String, AbstractFeature> getFeatures(Collection<String> foiIDs, List<SpatialFilter> list, Object connection,
            String version) throws OwsExceptionReport;

    /**
     * Get feature data for identifiers and/or for a spatial filter
     * 
     * @param foiIDs
     *            FOI identifiers
     * @param list
     *            Spatial filter
     * @param connection
     *            Data source connection
     * @param version
     *            SOS version
     * @param responseSrid
     *            response srid for feature geometry, if negative not
     *            transformation
     * @return Map of identifier and SOS FOI representation
     * 
     * 
     * @throws OwsExceptionReport
     */
    Map<String, AbstractFeature> getFeatures(FeatureQueryHandlerQueryObject queryObject) throws OwsExceptionReport;

    /**
     * Query the envelope for feature ids
     * 
     * @param featureIDs
     *            FOI identifiers
     * @param connection
     *            Data source connection
     * @param responseSrid
     * @return Envelope of requested FOI identifiers
     * 
     * @throws OwsExceptionReport
     */
    @Deprecated
    SosEnvelope getEnvelopeForFeatureIDs(Collection<String> featureIDs, Object connection) throws OwsExceptionReport;

    /**
     * Query the envelope for feature ids
     * 
     * @param featureIDs
     *            FOI identifiers
     * @param connection
     *            Data source connection
     * @param responseSrid
     *            response srid for feature geometry, if negative not
     *            transformation
     * @return Envelope of requested FOI identifiers
     * 
     * @throws OwsExceptionReport
     */
    SosEnvelope getEnvelopeForFeatureIDs(FeatureQueryHandlerQueryObject queryObject)
            throws OwsExceptionReport;

    /**
     * FIXME Add javadoc to clarify the semantics of this method
     * 
     * @param samplingFeature
     *            Feature to insert into datasource
     * @param connection
     *            Datasource connection
     * @return Identifier of the inserted feature
     * @throws OwsExceptionReport
     */
    String insertFeature(SamplingFeature samplingFeature, Object connection) throws OwsExceptionReport;

    @Deprecated
    int getDefaultEPSG();
    
    @Deprecated
    int getDefault3DEPSG();
    
    int getStorageEPSG();

    int getStorage3DEPSG();
    
    int getDefaultResponseEPSG();

    int getDefaultResponse3DEPSG();
}
