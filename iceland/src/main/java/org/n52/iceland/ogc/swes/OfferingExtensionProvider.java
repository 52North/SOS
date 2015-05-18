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

import java.util.Set;

/**
 * Interface for OfferingExtensionProvider. Implementations of this interface
 * are loaded by the {@link OfferingExtensionRepository}.
 * 
 * @since 4.1.0
 * 
 */
public interface OfferingExtensionProvider {

    /**
     * Get the offering extension for the specific offering identifier this
     * provider provides.
     * 
     * @param identifier
     *            the identifier to get extension for
     * @return provided offering extensions
     */
    SwesExtensions getOfferingExtensions(String identifier);

    /**
     * Check if this provider provide offering extensions for the specific
     * identifier
     * 
     * @param identifier
     *            the identifier to check
     * @return <code>true</code>, if offering extensions provided for this
     *         identifier
     */
    boolean hasExtendedOfferingFor(String identifier);

    /**
     * Get the offering extension keys
     * 
     * @return the offering extension keys
     */
    Set<OfferingExtensionKey> getOfferingExtensionKeyTypes();

}
