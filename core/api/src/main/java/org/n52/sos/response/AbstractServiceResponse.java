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
package org.n52.sos.response;

import org.n52.sos.ogc.swes.SwesConstants.HasSwesExtension;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.service.AbstractServiceCommunicationObject;
import org.n52.sos.util.http.MediaType;

/**
 * abstract super class for all service request classes
 * 
 * @since 4.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractServiceResponse extends AbstractServiceCommunicationObject implements HasSwesExtension<AbstractServiceResponse> {

    private MediaType contentType;
    
    private SwesExtensions extensions;

    public AbstractServiceResponse setContentType(MediaType contentType) {
        this.contentType = contentType;
        return this;
    }

    public MediaType getContentType() {
        return this.contentType;
    }

    public boolean isSetContentType() {
        return getContentType() != null;
    }

    @Override
    public SwesExtensions getExtensions() {
        return extensions;
    }

    @Override
    public AbstractServiceResponse setExtensions(final SwesExtensions extensions) {
        this.extensions = extensions;
        return this;
    }
    
    @Override
    public AbstractServiceResponse addExtensions(final SwesExtensions extensions) {
        if (getExtensions() == null) {
            setExtensions(extensions);
        } else {
            getExtensions().addSwesExtension(extensions.getExtensions());
        }
        return this;
    }

    @Override
    public AbstractServiceResponse addExtension(final SwesExtension extension) {
        if (getExtensions() == null) {
            setExtensions(new SwesExtensions());
        }
        getExtensions().addSwesExtension(extension);
        return this;
    }

    @Override
    public boolean isSetExtensions() {
        return extensions != null && !extensions.isEmpty();
    }
}
