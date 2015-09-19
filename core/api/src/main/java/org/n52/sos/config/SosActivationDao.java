/*
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

import java.util.Set;

import org.n52.iceland.coding.encode.ResponseFormatKey;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatKey;

/**
 *
 * @author Christian Autermann
 */
public interface SosActivationDao {

    /**
     * Checks if the response format is active for the specified service and
     * version.
     *
     * @param key
     *            the service/version/responseFormat combination
     *
     * @return if the format is active
     */
    boolean isResponseFormatActive(ResponseFormatKey key);

    /**
     * Sets the status of a response format for the specified service and
     * version.
     *
     * @param key
     *               the service/version/responseFormat combination
     * @param active
     *               the status
     *
     * @see #setActive(ResponseFormatKey, boolean)
     */
    void setResponseFormatStatus(ResponseFormatKey key, boolean active);

    Set<ResponseFormatKey> getResponseFormatKeys();

    /**
     * Checks if the procedure description format is active for the specified
     * service and version.
     *
     * @param key
     *            the service/version/procedure description combination
     *
     * @return if the format is active
     */
    boolean isProcedureDescriptionFormatActive(ProcedureDescriptionFormatKey key);

    /**
     * Sets the status of a response format for the specified service and
     * version.
     *
     * @param key
     *               the service/version/responseFormat combination
     * @param active
     *               the status
     *
     * @see #setActive(ProcedureDescriptionFormatKey, boolean)
     */
    void setProcedureDescriptionFormatStatus(ProcedureDescriptionFormatKey key,
                                             boolean active);

    Set<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatKeys();

}
