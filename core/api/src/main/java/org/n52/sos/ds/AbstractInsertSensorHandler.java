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

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.n52.shetland.ogc.ows.OwsAllowedValues;
import org.n52.shetland.ogc.ows.OwsAnyValue;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.OwsDomainMetadata;
import org.n52.shetland.ogc.ows.OwsValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.Sos1Constants;
import org.n52.shetland.ogc.sos.Sos1Constants.RegisterSensorParams;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.Sos2Constants.InsertSensorParams;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.shetland.ogc.sos.response.InsertSensorResponse;
import org.n52.sos.coding.encode.ProcedureDescriptionFormatRepository;

/**
 * Renamed, in version 4.x called AbstractInsertSensorDAO
 *
 * @since 5.0.0
 *
 */
public abstract class AbstractInsertSensorHandler extends AbstractSosOperationHandler {

    private ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository;

    public AbstractInsertSensorHandler(String service) {
        super(service, Sos2Constants.Operations.InsertSensor.name());
    }


    @Inject
    public void setProcedureDescriptionFormatRepository(
            ProcedureDescriptionFormatRepository procedureDescriptionFormatRepository) {
        this.procedureDescriptionFormatRepository = procedureDescriptionFormatRepository;
    }

    public ProcedureDescriptionFormatRepository getProcedureDescriptionFormatRepository() {
        return procedureDescriptionFormatRepository;
    }

    public abstract InsertSensorResponse insertSensor(InsertSensorRequest request) throws OwsExceptionReport;

    @Override
    protected Set<OwsDomain> getOperationParameters(String service, String version) throws OwsExceptionReport {
        switch (version) {
            case Sos1Constants.SERVICEVERSION:
                return new HashSet<>(Arrays.asList(getSensorDescriptionParameter(service, version),
                                                   getObservationTemplateParameter(service, version)));
            case Sos2Constants.SERVICEVERSION:
                return new HashSet<>(Arrays.asList(getProcedureDescriptionParameter(service, version),
                                                   getProcedureDescriptionFormatParameter(service, version),
                                                   getAnyObservablePropertyParameter(service, version),
                                                   getMetadataProperty(service, version)));
            default:
                return Collections.emptySet();
        }
    }

    private OwsDomain getSensorDescriptionParameter(String service, String version) {
        RegisterSensorParams name = Sos1Constants.RegisterSensorParams.SensorDescription;
        return new OwsDomain(name, OwsAnyValue.instance());
    }

    private OwsDomain getObservationTemplateParameter(String service, String version) {
        RegisterSensorParams name = Sos1Constants.RegisterSensorParams.ObservationTemplate;
        return new OwsDomain(name, OwsAnyValue.instance());
    }

    private OwsDomain getProcedureDescriptionParameter(String service, String version) {
        InsertSensorParams name = Sos2Constants.InsertSensorParams.procedureDescription;
        return new OwsDomain(name, OwsAnyValue.instance());
    }

    private OwsDomain getProcedureDescriptionFormatParameter(String service, String version) {
        InsertSensorParams name = Sos2Constants.InsertSensorParams.procedureDescriptionFormat;
        return new OwsDomain(name, new OwsAllowedValues(getProcedureDescriptionFormatRepository()
                .getSupportedTransactionalProcedureDescriptionFormats(service, version).stream().map(OwsValue::new)));
    }

    private OwsDomain getAnyObservablePropertyParameter(String service, String version) {
        InsertSensorParams name = Sos2Constants.InsertSensorParams.observableProperty;
        return new OwsDomain(name, OwsAnyValue.instance());
    }

    private OwsDomain getMetadataProperty(String service, String version) {
        InsertSensorParams name = Sos2Constants.InsertSensorParams.metadata;
        OwsDomainMetadata dataType
                = new OwsDomainMetadata(URI.create(Sos2Constants.SCHEMA_LOCATION_URL_SOS_INSERTION_CAPABILITIES));
        return new OwsDomain(name, OwsAnyValue.instance(), null, null, dataType, null, null);
    }
}
