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
package org.n52.sos.convert;

import java.util.Collections;
import java.util.List;

import org.n52.sos.ogc.sensorML.SensorML20Constants;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * {@link Converter} class to convert SensorML 2.0 URL to MimeType and the other
 * way round.
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.2.0
 *
 */
public class SensorML20UrlMimeTypeConverter implements Converter<SosProcedureDescription, SosProcedureDescription> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorML20UrlMimeTypeConverter.class);

    private static final List<ConverterKeyType> CONVERTER_KEY_TYPES = CollectionHelper.list(
            new ConverterKeyType(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL), new ConverterKeyType(
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE));

    public SensorML20UrlMimeTypeConverter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public List<ConverterKeyType> getConverterKeyTypes() {
        return Collections.unmodifiableList(CONVERTER_KEY_TYPES);
    }

    @Override
    public SosProcedureDescription convert(SosProcedureDescription objectToConvert) throws ConverterException {
        return objectToConvert;
    }

}