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
package org.n52.svalbard.inspire.omso.v30.decode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.AbstractOmDecoderv20;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.svalbard.inspire.omso.InspireOMSOConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Encoder for INSPIRE OM Specialised Observations
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class InpspireOMSODecoder extends AbstractOmDecoderv20 {

    private static final Logger LOGGER = LoggerFactory.getLogger(InpspireOMSODecoder.class);

    private static final Set<DecoderKey> DECODER_KEYS = Sets.newHashSet();

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES =
            ImmutableMap.of(SupportedTypeKey.ObservationType,
                    (Set<String>) ImmutableSet.of(InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION,
                            InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION,
                            InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION,
                            InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION,
                            InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION));

    private static final Set<String> CONFORMANCE_CLASSES = Sets.newHashSet();

    public InpspireOMSODecoder() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

}
