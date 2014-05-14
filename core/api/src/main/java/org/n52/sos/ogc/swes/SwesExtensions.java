/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 */
public class SwesExtensions {

    private final Set<SwesExtension<?>> extensions = new HashSet<SwesExtension<?>>(0);

    /**
     * @param extensionName
     * 
     * @return <b><tt>true</tt></b>, only if the extension with the definition
     *         <tt>extensionName</tt> is holding a {@link Boolean} and is set to
     *         <tt>true</tt>.
     */
    public boolean isBooleanExtensionSet(final String extensionName) {
        for (final SwesExtension<?> swesExtension : extensions) {
            if (isExtensionNameEquals(extensionName, swesExtension)) {
                final Object value = swesExtension.getValue();
                if (value instanceof SweBoolean) {
                    return ((SweBoolean) value).getValue();
                }
                return false;
            }
        }
        return false;
    }

    private boolean isExtensionNameEquals(final String extensionName, final SwesExtension<?> swesExtension) {
        return extensionName.equalsIgnoreCase(swesExtension.getDefinition())
                || (swesExtension.getValue() instanceof SweAbstractDataComponent
                        && ((SweAbstractDataComponent) swesExtension.getValue()).isSetDefinition() && ((SweAbstractDataComponent) swesExtension
                            .getValue()).getDefinition().equalsIgnoreCase(extensionName));
    }

    public boolean addSwesExtension(final SwesExtension<?> extension) {
        return extensions.add(extension);
    }
    
    public Set<SwesExtension<?>> getExtensions() {
        return extensions;
    }

    public boolean isEmpty() {
        return extensions.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("SwesExtensions [extensions=%s]", extensions);
    }

}
