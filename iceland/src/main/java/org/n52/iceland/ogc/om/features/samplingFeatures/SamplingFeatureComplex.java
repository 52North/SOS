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
package org.n52.iceland.ogc.om.features.samplingFeatures;

import com.google.common.base.Strings;

/**
 * Implementation for sam:SamplingFeatureComplex
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SamplingFeatureComplex {

    /**
     * Related sampling feature role
     */
    private final String relatedSamplingFeatureRole;

    /**
     * Related sampling feature
     */
    private final SamplingFeature relatedSamplingFeature;

    /**
     * constructor
     * 
     * @param relatedSamplingFeatureRole
     *            Related sampling feature role
     * @param relatedSamplingFeature
     *            Related sampling feature
     * @exception IllegalArgumentException
     *                If {@link #relatedSamplingFeatureRole} is null or empty
     *                and {@link #relatedSamplingFeature} is null or empty
     */
    public SamplingFeatureComplex(final String relatedSamplingFeatureRole, final SamplingFeature relatedSamplingFeature) {
        if (Strings.isNullOrEmpty(relatedSamplingFeatureRole)) {
            throw new IllegalArgumentException("relatedSamplingFeatureRole is required.");
        }
        if (relatedSamplingFeature == null || !relatedSamplingFeature.isSetIdentifier()) {
            throw new IllegalArgumentException(
                    "relatedSamplingFeature is required and MUST have set at least an identifier.");
        }
        this.relatedSamplingFeatureRole = relatedSamplingFeatureRole;
        this.relatedSamplingFeature = relatedSamplingFeature;
    }

    /**
     * Get Related sampling feature role
     * 
     * @return Related sampling feature role
     */
    public String getRelatedSamplingFeatureRole() {
        return relatedSamplingFeatureRole;
    }

    /**
     * Get related sampling feature
     * 
     * @return Related sampling feature
     */
    public SamplingFeature getRelatedSamplingFeature() {
        return relatedSamplingFeature;
    }

}
