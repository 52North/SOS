/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import org.n52.sos.exception.ows.InvalidParameterValueException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.swes.SwesConstants.HasSwesExtension;

/**
 * SOS internal representation of AbstractSWES element
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public abstract class AbstractSWES extends AbstractFeature implements HasSwesExtension<AbstractSWES> {

    private static final long serialVersionUID = -7371500673994109819L;

    private SwesExtensions extensions;

    @Override
    public SwesExtensions getExtensions() {
        return extensions;
    }

    @Override
    public AbstractSWES setExtensions(final SwesExtensions extensions) {
        this.extensions = extensions;
        return this;
    }

    @Override
    public AbstractSWES addExtensions(SwesExtensions extensions) {
        if (getExtensions() == null) {
            setExtensions(extensions);
        } else {
            getExtensions().addSwesExtension(extensions.getExtensions());
        }
        return this;
    }

    @SuppressWarnings("rawtypes")
    public AbstractSWES addExtension(final SwesExtension extension) {
        if (getExtensions() == null) {
            setExtensions(new SwesExtensions());
        }
        getExtensions().addSwesExtension(extension);
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
    public SwesExtension<?> getExtension(Enum identifier) throws InvalidParameterValueException {
        if (hasExtension(identifier)) {
            return getExtensions().getExtension(identifier);
        }
        return null;
    }
    
    @Override
    public SwesExtension<?> getExtension(String identifier) throws InvalidParameterValueException {
        if (hasExtension(identifier)) {
            return getExtensions().getExtension(identifier);
        }
        return null;
    }

}
