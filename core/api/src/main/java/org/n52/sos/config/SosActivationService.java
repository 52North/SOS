/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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

import org.n52.iceland.coding.encode.ResponseFormatKey;
import org.n52.iceland.config.ActivationService;
import org.n52.iceland.util.activation.ActivationInitializer;
import org.n52.iceland.util.activation.ActivationSource;
import org.n52.iceland.util.activation.DefaultActivationInitializer;
import org.n52.iceland.util.activation.FunctionalActivationListener;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatKey;
import org.n52.sos.ogc.sos.SosObservationOfferingExtensionKey;

public class SosActivationService extends ActivationService {

    private SosActivationDao activationDao;

    @Override
    public SosActivationDao getActivationDao() {
        return this.activationDao;
    }

    @Inject
    public void setActivationDao(SosActivationDao activationDao) {
        this.activationDao = activationDao;
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
        return getActivationDao().isResponseFormatActive(key);
    }

    public FunctionalActivationListener<ResponseFormatKey> getResponseFormatListener() {
        return getActivationDao()::setResponseFormatStatus;
    }

    public ActivationSource<ResponseFormatKey> getResponseFormatSource() {
        return ActivationSource.create(this::isResponseFormatActive, this::getResponseFormatKeys);
    }

    protected Set<ResponseFormatKey> getResponseFormatKeys() {
        return getActivationDao().getResponseFormatKeys();
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
    public boolean isProcedureDescriptionFormatActive(ProcedureDescriptionFormatKey key) {
        return getActivationDao().isProcedureDescriptionFormatActive(key);
    }

    public FunctionalActivationListener<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatListener() {
        return getActivationDao()::setProcedureDescriptionFormatStatus;
    }

    public ActivationSource<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatSource() {
        return ActivationSource.create(this::isProcedureDescriptionFormatActive,
                this::getProcedureDescriptionFormatKeys);
    }

    protected Set<ProcedureDescriptionFormatKey> getProcedureDescriptionFormatKeys() {
        return getActivationDao().getProcedureDescriptionFormatKeys();
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
    public boolean isSosObservationOfferingExtensionActive(SosObservationOfferingExtensionKey key) {
        return getActivationDao().isSosObservationOfferingExtensionActive(key);
    }

    public FunctionalActivationListener<SosObservationOfferingExtensionKey>
        getSosObservationOfferingExtensionListener() {
        return getActivationDao()::setSosObservationOfferingExtensionStatus;
    }

    public ActivationSource<SosObservationOfferingExtensionKey> getSosObservationOfferingExtensionSource() {
        return ActivationSource.create(this::isSosObservationOfferingExtensionActive,
                this::getSosObservationOfferingExtensionKeys);
    }

    protected Set<SosObservationOfferingExtensionKey> getSosObservationOfferingExtensionKeys() {
        return getActivationDao().getSosObservationOfferingExtensionKeys();
    }

    public ActivationInitializer<SosObservationOfferingExtensionKey> getSosObservationOfferingExtensionInitializer() {
        return new DefaultActivationInitializer<>(getSosObservationOfferingExtensionSource());
    }

}
