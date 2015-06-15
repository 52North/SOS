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

import java.util.Set;

import javax.inject.Inject;

import org.n52.iceland.coding.encode.ProcedureDescriptionFormatKey;
import org.n52.iceland.coding.encode.ResponseFormatKey;
import org.n52.iceland.ogc.swes.OfferingExtensionKey;
import org.n52.iceland.util.activation.ActivationInitializer;
import org.n52.iceland.util.activation.ActivationSource;
import org.n52.iceland.util.activation.DefaultActivationInitializer;
import org.n52.iceland.util.activation.FunctionalActivationListener;

public class SosActivationService {

    private SosActivationDao dao;

    @Inject
    public void setSosActivationDao(SosActivationDao dao) {
        this.dao = dao;
    }

    /**
     * Checks if the response format is active for the specified service and
     * version.
     *
     * @param key
     *            the service/version/responseFormat combination
     *
     * @return if the format is active
     */
    public boolean isResponseFormatActive(ResponseFormatKey key) {
        return this.dao.isResponseFormatActive(key);
    }

    public FunctionalActivationListener<ResponseFormatKey> getResponseFormatListener() {
        return this.dao::setResponseFormatStatus;
    }

    public ActivationSource<ResponseFormatKey> getResponseFormatSource() {
        return ActivationSource.create(this::isResponseFormatActive,
                                       this::getResponseFormatKeys);
    }

    protected Set<ResponseFormatKey> getResponseFormatKeys() {
        return this.dao.getResponseFormatKeys();
    }

    public ActivationInitializer<ResponseFormatKey> getResponseFormatInitializer() {
        return new DefaultActivationInitializer<>(getResponseFormatSource());
    }

    /**
     * Checks if the procedure description format is active for the specified
     * service and version.
     *
     * @param key
     *            the service/version/procedure description combination
     *
     * @return if the format is active
     */
    public boolean isProcedureDescriptionFormatActive(
            ProcedureDescriptionFormatKey key) {
        return this.dao.isProcedureDescriptionFormatActive(key);
    }

    public FunctionalActivationListener<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatListener() {
        return this.dao::setProcedureDescriptionFormatStatus;
    }

    public ActivationSource<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatSource() {
        return ActivationSource.create(this::isProcedureDescriptionFormatActive,
                                       this::getProcedureDescriptionFormatKeys);
    }

    protected Set<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatKeys() {
        return dao.getProcedureDescriptionFormatKeys();
    }

    public ActivationInitializer<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatInitializer() {
        return new DefaultActivationInitializer<>(getProcedureDescriptionFormatSource());
    }

    /**
     * Checks if the offering extension is active.
     *
     * @param key
     *            the offering extension key
     *
     * @return if the offering extension is active
     */
    public boolean isOfferingExtensionActive(OfferingExtensionKey key) {
        return this.dao.isOfferingExtensionActive(key);
    }

    public FunctionalActivationListener<OfferingExtensionKey> getOfferingExtensionListener() {
        return this.dao::setOfferingExtensionStatus;
    }

    public ActivationSource<OfferingExtensionKey> getOfferingExtensionSource() {
        return ActivationSource.create(this::isOfferingExtensionActive,
                                       this::getOfferingExtensionKeys);
    }

    protected Set<OfferingExtensionKey> getOfferingExtensionKeys() {
        return this.dao.getOfferingExtensionKeys();
    }

    public ActivationInitializer<OfferingExtensionKey> getOfferingExtensionInitializer() {
        return new DefaultActivationInitializer<>(getOfferingExtensionSource());
    }
}
