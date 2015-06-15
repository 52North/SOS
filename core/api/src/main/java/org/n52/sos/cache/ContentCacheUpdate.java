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
package org.n52.sos.cache;

import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.Action;

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
