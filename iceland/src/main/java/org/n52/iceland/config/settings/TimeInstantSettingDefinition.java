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

import org.n52.iceland.config.SettingType;
import org.n52.iceland.exception.ows.concrete.DateTimeParseException;
import org.n52.iceland.ogc.gml.time.Time;
import org.n52.iceland.util.DateTimeHelper;

/**
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.1.0
 * 
 */
public class TimeInstantSettingDefinition extends AbstractSettingDefinition<TimeInstantSettingDefinition, Time> {

    /**
     * constructor
     */
    public TimeInstantSettingDefinition() {
        super(SettingType.TIMEINSTANT);
    }

    /**
     * Setter for default value as {@link String}. Parses the string to
     * {@link Time} object
     * 
     * @param value
     *            Default value as String
     * @return this
     */
    public TimeInstantSettingDefinition setDefaultStringValue(String value) {
        try {
            setDefaultValue(DateTimeHelper.parseIsoString2DateTime2Time(value));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }

}
