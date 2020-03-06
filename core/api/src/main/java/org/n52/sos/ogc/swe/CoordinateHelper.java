/**
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ogc.swe;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.config.SettingsManager;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.util.CollectionHelper;

import com.google.common.base.Strings;

/**
 * Helper class for SWE coordinates
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
@Configurable
public class CoordinateHelper {

    private static CoordinateHelper instance = null;

    private Set<String> northingNames = Collections.emptySet();

    private Set<String> eastingNames = Collections.emptySet();

    private Set<String> altitudeNames = Collections.emptySet();

    private CoordinateHelper() {
    }

    public static synchronized CoordinateHelper getInstance() {
        if (instance == null) {
            instance = new CoordinateHelper();
            SettingsManager.getInstance().configure(instance);
        }
        return instance;
    }

    /**
     * @return the northingNames
     */
    public Set<String> getNorthingNames() {
        return northingNames;
    }

    /**
     * @param northingNames
     *            the northingNames to set
     */
    @Setting(CoordinateSettings.NORTHING_COORDINATE_NAME)
    public void setNorthingNames(String northingNames) {
        if (!Strings.isNullOrEmpty(northingNames)) {
            this.northingNames = CollectionHelper.csvStringToSet(northingNames);
        }
    }

    /**
     * Check if northing names contains name
     * 
     * @param name
     *            Name to check
     * @return <code>true</code>, if the name is defined.
     */
    public boolean hasNorthingName(String...names) {
        return check(getNorthingNames(), names);
    }

    /**
     * @return the eastingNames
     */
    public Set<String> getEastingNames() {
        return eastingNames;
    }

    /**
     * @param eastingNames
     *            the eastingNames to set
     */
    @Setting(CoordinateSettings.EASTING_COORDINATE_NAME)
    public void setEastingNames(String eastingNames) {
        if (!Strings.isNullOrEmpty(eastingNames)) {
            this.eastingNames = CollectionHelper.csvStringToSet(eastingNames);
        }
    }

    /**
     * Check if easting names contains name
     * 
     * @param name
     *            Name to check
     * @return <code>true</code>, if the name is defined.
     */
    public boolean hasEastingName(String... names) {
        return check(getEastingNames(), names);
    }

    /**
     * @return the altitudeNames
     */
    public Set<String> getAltitudeNames() {
        return altitudeNames;
    }

    /**
     * @param altitudeNames
     *            the altitudeNames to set
     */
    @Setting(CoordinateSettings.ALTITUDE_COORDINATE_NAME)
    public void setAltitudeNames(String altitudeNames) {
        if (!Strings.isNullOrEmpty(altitudeNames)) {
            this.altitudeNames = CollectionHelper.csvStringToSet(altitudeNames);
        }
    }

    /**
     * Check if altitude names contains name
     * 
     * @param name
     *            Name to check
     * @return <code>true</code>, if the name is defined.
     */
    public boolean hasAltitudeName(String...names) {
        return check(getAltitudeNames(), names);
    }

    private boolean check(Set<String> set, String...names) {
        for (String string : set) {
            for (String name : names) {
                if (string.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

}
