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
package org.n52.sos.ogc.swes;

import org.n52.iceland.exception.ows.InvalidParameterValueException;
import org.n52.iceland.ogc.gml.AbstractFeature;
import org.n52.iceland.ogc.ows.Extension;
import org.n52.iceland.ogc.ows.Extensions;
import org.n52.iceland.ogc.ows.OWSConstants.HasExtension;

/**
 * SOS internal representation of AbstractSWES element
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.1.0
 *
 */
public abstract class AbstractSWES extends AbstractFeature implements HasExtension<AbstractSWES> {

    private static final long serialVersionUID = -7371500673994109819L;

    private Extensions extensions;

    @Override
    public Extensions getExtensions() {
        return extensions;
    }

    @Override
    public AbstractSWES setExtensions(final Extensions extensions) {
        this.extensions = extensions;
        return this;
    }

    @Override
    public AbstractSWES addExtensions(Extensions extensions) {
        if (getExtensions() == null) {
            setExtensions(extensions);
        } else {
            getExtensions().addExtension(extensions.getExtensions());
        }
        return this;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public AbstractSWES addExtension(Extension extension) {
        if (getExtensions() == null) {
            setExtensions(new Extensions());
        }
        getExtensions().addExtension(extension);
        return this;
    }

    @Override
    public boolean isSetExtensions() {
        return getExtensions() != null && !getExtensions().isEmpty();
    }
    
    @Override
    public boolean hasExtension(Enum identifier) {
        if (isSetExtensions()) {
            return getExtensions().containsExtension(identifier);
        }
        return false;
    }
    
    @Override
    public boolean hasExtension(String identifier) {
        if (isSetExtensions()) {
            return getExtensions().containsExtension(identifier);
        }
        return false;
    }

    @Override
    public Extension<?> getExtension(Enum identifier) throws InvalidParameterValueException {
        if (hasExtension(identifier)) {
            return getExtensions().getExtension(identifier);
        }
        return null;
    }
    
    @Override
    public Extension<?> getExtension(String identifier) throws InvalidParameterValueException {
        if (hasExtension(identifier)) {
            return getExtensions().getExtension(identifier);
        }
        return null;
    }

}
