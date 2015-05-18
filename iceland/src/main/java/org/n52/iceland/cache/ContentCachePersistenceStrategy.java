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

import org.n52.iceland.util.Cleanupable;

import com.google.common.base.Optional;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public interface ContentCachePersistenceStrategy extends Cleanupable {
    /**
     * Read the persisted content cache if it present.
     *
     * @return the cache
     */
    Optional<WritableContentCache> load();

    /**
     * Persist the specified cache in the event of a partial content update.
     *
     * @param cache the cache
     */
    void persistOnPartialUpdate(ContentCache cache);

    /**
     * Persist the specified cache in the event of a complete content update.
     *
     * @param cache the cache
     */
    void persistOnCompleteUpdate(ContentCache cache);

    /**
     * Persist the specified cache in the event of a service shutdown.
     *
     * @param cache the cache
     */
    void persistOnShutdown(ContentCache cache);

    /**
     * Delete any persistent cache instances created by this strategy.
     */
    @Override
    public void cleanup();
}
