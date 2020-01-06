/*
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
package org.n52.sos.convert;

import java.util.Collections;
import java.util.Set;

import org.n52.iceland.convert.Converter;
import org.n52.iceland.convert.ConverterException;
import org.n52.iceland.convert.ConverterKey;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.sensorML.SensorML20Constants;
import org.n52.shetland.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * {@link Converter} class to convert SensorML 2.0 URL to MimeType and the other
 * way round.
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
public class SensorML20UrlMimeTypeConverter
        extends
        ProcedureDescriptionConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensorML20UrlMimeTypeConverter.class);

    private static final Set<ConverterKey> CONVERTER_KEY_TYPES = CollectionHelper.set(
            new ConverterKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL),
            new ConverterKey(SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_URL,
                    SensorML20Constants.SENSORML_20_OUTPUT_FORMAT_MIME_TYPE));

    public SensorML20UrlMimeTypeConverter() {
        LOGGER.debug("Converter for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(CONVERTER_KEY_TYPES));
    }

    @Override
    public Set<ConverterKey> getKeys() {
        return Collections.unmodifiableSet(CONVERTER_KEY_TYPES);
    }

    @Override
    public AbstractFeature convert(AbstractFeature objectToConvert)
            throws ConverterException {
        return objectToConvert;
    }

}
