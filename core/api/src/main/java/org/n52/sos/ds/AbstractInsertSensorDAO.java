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
package org.n52.sos.ds;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.OwsOperation;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.response.InsertSensorResponse;

/**
 * @since 4.0.0
 * 
 */
public abstract class AbstractInsertSensorDAO extends AbstractOperationDAO {

    public AbstractInsertSensorDAO(String service) {
        super(service, Sos2Constants.Operations.InsertSensor.name());
    }

    @Override
    protected void setOperationsMetadata(OwsOperation opsMeta, String service, String version)
            throws OwsExceptionReport {
        if (version.equals(Sos1Constants.SERVICEVERSION)) {
            opsMeta.addAnyParameterValue(Sos1Constants.RegisterSensorParams.SensorDescription);
            opsMeta.addAnyParameterValue(Sos1Constants.RegisterSensorParams.ObservationTemplate);
        } else {
            opsMeta.addAnyParameterValue(Sos2Constants.InsertSensorParams.procedureDescription);
            opsMeta.addPossibleValuesParameter(Sos2Constants.InsertSensorParams.procedureDescriptionFormat,
                    CodingRepository.getInstance().getSupportedProcedureDescriptionFormats(service, version));
            opsMeta.addAnyParameterValue(Sos2Constants.InsertSensorParams.observableProperty);
            opsMeta.addAnyParameterValue(Sos2Constants.InsertSensorParams.metadata);
            opsMeta.addDataTypeParameter(Sos2Constants.InsertSensorParams.metadata,
                    Sos2Constants.SCHEMA_LOCATION_URL_SOS_INSERTION_CAPABILITIES);
        }
    }

    public abstract InsertSensorResponse insertSensor(InsertSensorRequest request) throws OwsExceptionReport;
}
