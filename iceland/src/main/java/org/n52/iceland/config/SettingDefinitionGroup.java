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
package org.n52.iceland.config;

import org.n52.iceland.service.MiscSettings;
import org.n52.iceland.service.ServiceSettings;

/**
 * Class to group ISettingDefinitions. Not needed by the service but only for
 * representation in the GUI.
 * <p/>
 * 
 * @see ServiceSettings#GROUP
 * @see MiscSettings#GROUP
 * @see SosServiceProviderFactorySettings#GROUP
 * @see SosServiceIdentificationFactorySettings#GROUP
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public class SettingDefinitionGroup extends AbstractOrdered<SettingDefinitionGroup> {

    private String title;

    private String description;

    private boolean showInDefaultSetting = true;

    /**
     * @return the title of this group
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return if this group has a non empty title
     */
    public boolean hasTitle() {
        return hasStringProperty(getTitle());
    }

    /**
     * Sets the title of this group.
     * 
     * @param title
     *            the title
     * 
     * @return this
     */
    public SettingDefinitionGroup setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * @return the description for this group
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return if this group has a non empty description
     */
    public boolean hasDescription() {
        return hasStringProperty(getDescription());
    }

    /**
     * Sets the description for this group.
     * 
     * @param description
     *            the description
     * 
     * @return this
     */
    public SettingDefinitionGroup setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Set if this settings group should be displayed in default settings
     * 
     * @param showInDefaultSetting
     *            Display in default settings
     * @return this
     */
    public SettingDefinitionGroup setShwoInDefaultSettings(boolean showInDefaultSetting) {
        this.showInDefaultSetting = showInDefaultSetting;
        return this;
    }

    /**
     * Should this group be displayed in default settings
     * 
     * @return <code>true</code>, if this group should be displayed in default
     *         settings
     */
    public boolean isShowInDefaultSettings() {
        return showInDefaultSetting;
    }

    /**
     * Checks if the parameter is not null and not empty.
     * 
     * @param s
     *            the string to test
     * 
     * @return if it is not null and not empty
     */
    protected boolean hasStringProperty(String s) {
        return s != null && !s.isEmpty();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.getTitle() != null ? this.getTitle().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SettingDefinitionGroup other = (SettingDefinitionGroup) obj;
        if ((this.getTitle() == null) ? (other.getTitle() != null) : !this.getTitle().equals(other.getTitle())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s[title=%s, description=%s, showInDefaultSetting=%b]", getClass().getSimpleName(),
                getTitle(), getDescription(), isShowInDefaultSettings());
    }

    @Override
    protected String getSuborder() {
        return getTitle();
    }
}
