/**
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.iceland.ds;

import java.util.Collection;
import java.util.Map;

import org.n52.iceland.ogc.gml.AbstractFeature;
import org.n52.iceland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.ogc.sos.SosEnvelope;

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

    int getStorageEPSG();

    int getStorage3DEPSG();
    
    int getDefaultResponseEPSG();

    int getDefaultResponse3DEPSG();
}
