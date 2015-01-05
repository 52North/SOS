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
package org.n52.sos.config;

import org.n52.sos.ogc.ows.SosServiceIdentificationFactorySettings;
import org.n52.sos.ogc.ows.SosServiceProviderFactorySettings;
import org.n52.sos.service.MiscSettings;
import org.n52.sos.service.ServiceSettings;

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
