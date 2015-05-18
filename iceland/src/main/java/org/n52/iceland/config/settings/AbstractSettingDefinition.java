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
package org.n52.iceland.config.settings;

import org.n52.iceland.config.AbstractOrdered;
import org.n52.iceland.config.SettingDefinition;
import org.n52.iceland.config.SettingDefinitionGroup;
import org.n52.iceland.config.SettingType;

/**
 * Generic implementation of <code>SettingDefinition</code>.
 * 
 * @param <S>
 *            the type of the class extending this class
 * @param <T>
 *            the type of the value
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
abstract class AbstractSettingDefinition<S extends AbstractSettingDefinition<S, T>, T> extends AbstractOrdered<S>
        implements SettingDefinition<S, T> {

    private boolean optional = false;

    private String identifier;

    private String title;

    private String description;

    private SettingDefinitionGroup group;

    private SettingType type;

    private T defaultValue;

    /**
     * @param type
     *            the <code>SettingType</code> of this setting definition
     */
    protected AbstractSettingDefinition(SettingType type) {
        this.type = type;
    }

    @Override
    public String getKey() {
        return identifier;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setKey(String key) {
        this.identifier = key;
        return (S) this;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean hasDescription() {
        return hasStringProperty(getDescription());
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setDescription(String description) {
        this.description = description;
        return (S) this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean hasTitle() {
        return hasStringProperty(getTitle());
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setTitle(String title) {
        this.title = title;
        return (S) this;
    }

    @Override
    public boolean isOptional() {
        return optional;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setOptional(boolean optional) {
        this.optional = optional;
        return (S) this;
    }

    @Override
    public SettingDefinitionGroup getGroup() {
        return group;
    }

    @Override
    public boolean hasGroup() {
        return getGroup() != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setGroup(SettingDefinitionGroup group) {
        this.group = group;
        return (S) this;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean hasDefaultValue() {
        return getDefaultValue() != null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return (S) this;
    }

    protected boolean hasStringProperty(String s) {
        return s != null && !s.isEmpty();
    }

    @Override
    public int hashCode() {
        return (getKey() != null) ? getKey().hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractSettingDefinition) {
            AbstractSettingDefinition<?, ?> o = (AbstractSettingDefinition<?, ?>) obj;
            return (getKey() == null ? o.getKey() == null : getKey().equals(o.getKey()))
            		&& (getTitle() == null ? o.getTitle() == null : getTitle().equals(o.getTitle()))
            		&& (getDescription() == null ? o.getDescription() == null : getDescription().equals(o.getDescription())) 
            		&& (getGroup() == null ? o.getGroup() == null : getGroup().equals(o.getGroup()))
            		&& (getDefaultValue() == null ? o.getDefaultValue() == null : getDefaultValue().equals(o.getDefaultValue()))
                    && (getType() == o.getType())
                    && (isOptional() == o.isOptional());
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s[key=%s]", getClass().getSimpleName(), getKey());
    }

    @Override
    public SettingType getType() {
        return this.type;
    }

    @SuppressWarnings("unchecked")
    S setType(SettingType type) {
        this.type = type;
        return (S) this;
    }

    @Override
    protected String getSuborder() {
        return getTitle();
    }
}
