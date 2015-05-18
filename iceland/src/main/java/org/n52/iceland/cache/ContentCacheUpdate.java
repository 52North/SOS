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
import org.n52.iceland.util.Action;

/**
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.0.0
 */
public abstract class ContentCacheUpdate implements Action {
    private WritableContentCache cache;

    private OwsExceptionReport exceptionReport;

    /**
     * @return the cause of failure or {@code null}
     */
    public OwsExceptionReport getFailureCause() {
        return this.exceptionReport;
    }

    /**
     * Marks this cache update as failed.
     * 
     * @param exceptionReport
     *            the cause
     * 
     * @return this
     */
    protected ContentCacheUpdate fail(OwsExceptionReport exceptionReport) {
        this.exceptionReport = exceptionReport;
        return this;
    }

    /**
     * @return if this update failed
     */
    public boolean failed() {
        return this.exceptionReport != null;
    }

    /**
     * @return the writable cache of this action
     */
    public WritableContentCache getCache() {
        return cache;
    }

    /**
     * @param cache
     *            the writable cache for this action
     * 
     * @return this
     */
    public ContentCacheUpdate setCache(WritableContentCache cache) {
        this.cache = cache;
        return this;
    }

    /**
     * Clear any exceptions.
     * 
     * @return this
     */
    public ContentCacheUpdate reset() {
        this.exceptionReport = null;
        return this;
    }

    /**
     * @return if this a complete update that will replace the cache.
     */
    public boolean isCompleteUpdate() {
        return false;
    }

}
