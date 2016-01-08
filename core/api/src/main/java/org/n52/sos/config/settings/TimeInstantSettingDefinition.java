/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.config.settings;

import org.n52.sos.config.SettingType;
import org.n52.sos.exception.ows.concrete.DateTimeParseException;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.util.DateTimeHelper;

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
