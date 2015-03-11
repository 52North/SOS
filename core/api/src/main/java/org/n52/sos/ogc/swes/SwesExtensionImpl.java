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

import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.util.StringHelper;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SwesExtensionImpl<T> implements SwesExtension<T> {
    
    private String namespace = SweConstants.NS_SWE_20;
    
    private String identifier;

    private T value;

    private String definition;

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public SwesExtension<T> setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public boolean isSetNamespace() {
        return StringHelper.isNotEmpty(getNamespace());
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public SwesExtension<T> setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public boolean isSetIdentifier() {
        return StringHelper.isNotEmpty(getIdentifier());
    }

    @Override
	public String getDefinition() {
        return definition;
    }

    @Override
	public SwesExtension<T> setDefinition(final String definition) {
        this.definition = definition;
        return this;
    }

    @Override
    public boolean isSetDefinition() {
        return StringHelper.isNotEmpty(getDefinition());
    }

    @Override
	public T getValue() {
        return value;
    }

    @Override
	public SwesExtension<T> setValue(final T value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return String.format("SwesExtension [value=%s, definition=%s]", value, definition);
    }

}
