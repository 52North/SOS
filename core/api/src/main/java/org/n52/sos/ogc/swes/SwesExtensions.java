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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.simpleType.SweBoolean;
import org.n52.sos.util.StringHelper;

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

    public boolean addSwesExtension(final Collection<SwesExtension<?>> extensions) {
       return getExtensions().addAll(extensions);
    }
    
    public boolean addSwesExtension(final SwesExtension<?> extensions) {
        return getExtensions().add(extensions);
     }

    public Set<SwesExtension<?>> getExtensions() {
        return extensions;
    }

    @SuppressWarnings("rawtypes")
    public boolean containsExtension(Enum identifier) {
        return containsExtension(identifier.name());
    }

    public boolean containsExtension(String identifier) {
        for (SwesExtension<?> extension : getExtensions()) {
            if (isExtensionNameEquals(identifier, extension)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    public SwesExtension<?> getExtension(Enum identifier) {
        return getExtension(identifier.name());
    }

    public SwesExtension<?> getExtension(String identifier) {
        for (SwesExtension<?> extension : getExtensions()) {
            if (isExtensionNameEquals(identifier, extension)) {
                return extension;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return extensions.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("SwesExtensions [extensions=%s]", extensions);
    }

    private boolean isExtensionNameEquals(final String extensionName, final SwesExtension<?> swesExtension) {
        return checkSwesExtensionDefinition(extensionName, swesExtension)
                || checkSwesExtensionIdentifier(extensionName, swesExtension)
                || checkSweExtensionValue(extensionName, swesExtension);
    }

    private boolean checkSweExtensionValue(String extensionName, SwesExtension<?> swesExtension) {
        if (swesExtension.getValue() instanceof SweAbstractDataComponent) {
            SweAbstractDataComponent sweAbstractDataComponent = (SweAbstractDataComponent) swesExtension.getValue();
            return (sweAbstractDataComponent.isSetDefinition() && sweAbstractDataComponent.getDefinition()
                    .equalsIgnoreCase(extensionName))
                    || (sweAbstractDataComponent.isSetIdentifier() && sweAbstractDataComponent.getIdentifier()
                            .equalsIgnoreCase(extensionName));
        }
        return false;
    }

    private boolean checkSwesExtensionIdentifier(String extensionName, SwesExtension<?> swesExtension) {
        if (StringHelper.isNotEmpty(extensionName)) {
            return swesExtension.isSetIdentifier() && swesExtension.getIdentifier().equalsIgnoreCase(extensionName);
        }
        return false;
    }

    private boolean checkSwesExtensionDefinition(String extensionName, SwesExtension<?> swesExtension) {
        if (extensionName != null && swesExtension != null) {
            return swesExtension.isSetDefinition() && swesExtension.getDefinition().equalsIgnoreCase(extensionName);
        }
        return false;
    }

}
