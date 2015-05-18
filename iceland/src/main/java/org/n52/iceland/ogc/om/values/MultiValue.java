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
package org.n52.iceland.ogc.om.values;

import org.n52.iceland.ogc.gml.time.Time;

/**
 * Interface for observation values with more than one value
 * 
 * @since 4.0.0
 * 
 * @param <T>
 *            type of the multi value
 */
public interface MultiValue<T> extends Value<T> {

    /**
     * Get the phenomenon time for the multiple values
     * 
     * @return Phenomenon time
     */
    Time getPhenomenonTime();

    @Override
    void setValue(T value);

}
