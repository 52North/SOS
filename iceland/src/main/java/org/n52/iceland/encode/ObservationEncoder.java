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
package org.n52.iceland.encode;

import java.util.Set;

/**
 * @since 4.0.0
 * 
 * @param <S>
 * @param <T>
 */
public interface ObservationEncoder<S, T> extends Encoder<S, T> {

    /**
     * Indicator whether the ObservationEncoder of type or subtype
     * Observation&Measurement 2.0
     * 
     * @return Of type or not
     */
    boolean isObservationAndMeasurmentV20Type();

    /**
     * Indicator whether the single observations with the same procedure,
     * observableProperty and featureOfInterest should be merged to one
     * observation.
     * 
     * @return Merge or not
     */
    boolean shouldObservationsWithSameXBeMerged();
    
    boolean supportsResultStreamingForMergedValues();

    /**
     * Get the supported response formats for this
     * {@linkplain ObservationEncoder} and the specified service and version.
     * 
     * @param service
     *            the service
     * @param version
     *            the version
     * 
     * @return the response formats
     */
    Set<String> getSupportedResponseFormats(String service, String version);

}
