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
package org.n52.iceland.ogc.swes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.n52.iceland.ogc.swe.SweAbstractDataComponent;
import org.n52.iceland.ogc.swe.simpleType.SweBoolean;
import org.n52.iceland.util.StringHelper;

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
