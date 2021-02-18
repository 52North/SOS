/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.ds;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsAnyValue;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.Sos2Constants.UpdateSensorDescriptionParams;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;
import org.n52.shetland.ogc.sos.request.UpdateSensorRequest;
import org.n52.shetland.ogc.sos.response.UpdateSensorResponse;

/**
 * Renamed, in version 4.x called AbstractUpdateSensorDescriptionDAO
 *
 * @since 5.0.0
 *
 */
public abstract class AbstractUpdateSensorDescriptionHandler extends AbstractSosOperationHandler {

    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;

    public AbstractUpdateSensorDescriptionHandler(String service) {
        super(service, Sos2Constants.Operations.UpdateSensorDescription.name());
    }

    public abstract UpdateSensorResponse updateSensorDescription(UpdateSensorRequest request)
            throws OwsExceptionReport;

    public ProcedureDescriptionFormatRepository getProcedureDescriptionFormatRepository() {
        return procedureDescriptionFormatRepository;
    }

    @Inject
    public void setProcedureDescriptionFormatRepository(ProcedureDescriptionFormatRepository repo) {
        this.procedureDescriptionFormatRepository = repo;
    }

    @Override
    protected Set<OwsDomain> getOperationParameters(String service, String version) throws OwsExceptionReport {
        return new HashSet<>(Arrays.asList(getProcedureParameter(service, version),
                                           getDescriptionParameter(service, version),
                                           getProcedureDescriptionFormatParameter(service, version)));
    }

    private OwsDomain getDescriptionParameter(String service, String version) {
        UpdateSensorDescriptionParams name = Sos2Constants.UpdateSensorDescriptionParams.description;
        return new OwsDomain(name, OwsAnyValue.instance());
    }

    private OwsDomain getProcedureDescriptionFormatParameter(String service, String version) {
        UpdateSensorDescriptionParams name = Sos2Constants.UpdateSensorDescriptionParams.procedureDescriptionFormat;
        Set<String> procedureDescriptionFormats = this.procedureDescriptionFormatRepository
                .getSupportedTransactionalProcedureDescriptionFormats(service, version);
        return new OwsDomain(name, new OwsAllowedValues(procedureDescriptionFormats.stream().map(OwsValue::new)));
    }

}
