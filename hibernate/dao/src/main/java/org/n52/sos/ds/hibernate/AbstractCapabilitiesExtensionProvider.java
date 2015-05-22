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
package org.n52.sos.ds.hibernate;

import java.util.Collections;
import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.cache.ContentCache;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.coding.CodingRepository;
import org.n52.iceland.ogc.sos.CapabilitiesExtensionKey;
import org.n52.iceland.ogc.sos.CapabilitiesExtensionProvider;

/**
 * TODO JavaDoc
 * @author Christian Autermann
 */
public abstract class AbstractCapabilitiesExtensionProvider implements CapabilitiesExtensionProvider {
    @Inject
    private CodingRepository codingRepository;
    @Inject
    private ContentCacheController contentCacheController;
    private final String operation;
    private final CapabilitiesExtensionKey key;

    public AbstractCapabilitiesExtensionProvider(String service,
                                                 String version,
                                                 String operation) {
        this.operation = operation;
        this.key = new CapabilitiesExtensionKey(service, version);
    }


    @Override
    @Deprecated
    public CapabilitiesExtensionKey getCapabilitiesExtensionKey() {
        return this.key;
    }

    @Override
    public boolean hasRelatedOperation() {
        return this.operation != null;
    }

    @Override
    public String getRelatedOperation() {
        return this.operation;
    }

    @Override
    public Set<CapabilitiesExtensionKey> getKeys() {
        return Collections.singleton(this.key);
    }

    protected ContentCache getCache() {
        return this.contentCacheController.getCache();
    }

    protected CodingRepository getCodingRepository() {
        return this.codingRepository;
    }

}
