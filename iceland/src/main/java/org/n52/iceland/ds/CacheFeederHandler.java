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

import org.n52.iceland.cache.WritableContentCache;
import org.n52.iceland.ogc.ows.OwsExceptionReport;

/**
 * Interface for implementations of cache feeder Handlers. Used to feed the
 * CapabilitiesCache with data from the data source.
 * 
 * @since 4.0.0
 */
public interface CacheFeederHandler extends DatasourceDaoIdentifier {
    void updateCache(WritableContentCache capabilitiesCache) throws OwsExceptionReport;

    /**
     * Reload all cache data for a list of offerings, for instance after a DeleteSensor event
     * 
     * @param capabilitiesCache The cache to update
     * @param offerings A list of offerings to update
     * @throws OwsExceptionReport
     */
    void updateCacheOfferings(WritableContentCache capabilitiesCache, Collection<String> offerings)
            throws OwsExceptionReport;
}
