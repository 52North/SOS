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
package org.n52.iceland.cache;

import org.n52.iceland.ogc.ows.OwsExceptionReport;
import org.n52.iceland.util.Cleanupable;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public interface ContentCacheController extends Cleanupable {
    /**
     * @return the content cache
     */
    ContentCache getCache();

    /**
     * Update the content cache with the specified update.
     *
     * @param update
     *            the update
     *
     * @throws OwsExceptionReport
     *             if an error occurs while updating the cache
     */
    void update(ContentCacheUpdate update) throws OwsExceptionReport;

    /**
     * Renew the content cache completly.
     *
     * @throws OwsExceptionReport
     *             if an error occurs while updating the cache
     */
    void update() throws OwsExceptionReport;

    /**
     * @return Is a cache update currently in progress?
     */
    boolean isUpdateInProgress();


    /**
     * @return the ContentCachePersistenceStrategy
     * @since 4.2.0
     */
    ContentCachePersistenceStrategy getContentCachePersistenceStrategy();
}
