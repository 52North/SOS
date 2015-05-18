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

import org.n52.iceland.ogc.swe.SweConstants;
import org.n52.iceland.util.StringHelper;

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
