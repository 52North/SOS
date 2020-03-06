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
package org.n52.sos.encode;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.coding.OperationKey;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.http.MediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public abstract class AbtractVersionedResponseEncoder<T extends AbstractServiceResponse> extends AbstractResponseEncoder<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbtractVersionedResponseEncoder.class);
    private final Set<EncoderKey> encoderKeys;
    
    /**
     * constructor
     *
     * @param service
     *            Service
     * @param version
     *            Service version
     * @param operation
     *            Service operation name
     * @param namespace
     *            Service XML schema namespace
     * @param prefix
     *            Service XML schema prefix
     * @param responseType
     *            Response type
     * @param validationEnabled
     *            Indicator if the created/encoded object can be validated
     */
    @SuppressWarnings("unchecked")
    public AbtractVersionedResponseEncoder(String service, String version, String operation, String namespace, String prefix,
            Class<T> responseType, boolean validationEnabled, String operationVersion) {
        super(service, version, operationVersion, namespace, prefix, responseType, validationEnabled);
        OperationKey key = new OperationKey(service, version, operation);
        this.encoderKeys = Sets.newHashSet(new XmlEncoderKey(namespace, responseType),
                new VersionedOperationEncoderKey(key, MediaTypes.TEXT_XML, operationVersion),
                new VersionedOperationEncoderKey(key, MediaTypes.APPLICATION_XML, operationVersion));
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!", Joiner.on(", ").join(encoderKeys));
    }

    /**
     * constructor
     *
     * @param service
     *            Service
     * @param version
     *            Service version
     * @param operation
     *            Service operation name
     * @param namespace
     *            Service XML schema namespace
     * @param prefix
     *            Service XML schema prefix
     * @param responseType
     *            Response type
     */
    public AbtractVersionedResponseEncoder(String service, String version, String operation, String namespace, String prefix,
            Class<T> responseType, String operationVersion) {
        this(service, version, operation, namespace, prefix, responseType, true, operationVersion);
    }
    
    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(encoderKeys);
    }

}
