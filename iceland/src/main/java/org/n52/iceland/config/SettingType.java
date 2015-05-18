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

import java.io.File;
import java.net.URI;

import org.n52.iceland.i18n.LocalizedString;
import org.n52.iceland.ogc.gml.time.TimeInstant;

/**
 * Enum to describe the type of a {@code SettingDefinition} and
 * {@code SettingValue}.
 *
 * @see SettingDefinition
 * @see SettingValue
 * @author Christian Autermann <c.autermann@52north.org>
 * @since 4.0.0
 */
public enum SettingType {
    /**
     * Type for {@link Boolean} and {@code boolean}.
     */
    BOOLEAN,
    /**
     * Type for {@link Integer} and {@code int}.
     */
    INTEGER,
    /**
     * Type for {@link File}.
     */
    FILE,
    /**
     * Type for {@link Double} and {@code double}.
     */
    NUMERIC,
    /**
     * Type for {@link String}.
     */
    STRING,
    /**
     * Type for {@link URI}.
     */
    URI,
    /**
     * Type for {@link TimeInstant}.
     */
    TIMEINSTANT,
    /**
     * Type for {@link LocalizedString}.
     */
    MULTILINGUAL_STRING,
    /**
     * Type for a selection.
     */
    CHOICE;

}
