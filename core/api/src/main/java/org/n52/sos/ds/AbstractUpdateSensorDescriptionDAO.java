/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.UpdateSensorRequest;
import org.n52.sos.response.UpdateSensorResponse;

/**
 * @since 4.0.0
 * 
 */
public abstract class AbstractUpdateSensorDescriptionDAO extends AbstractOperationDAO {

    public AbstractUpdateSensorDescriptionDAO(String service) {
        super(service, Sos2Constants.Operations.UpdateSensorDescription.name());
    }

    @Override
    protected void setOperationsMetadata(OwsOperation opsMeta, String service, String version)
            throws OwsExceptionReport {
        addProcedureParameter(opsMeta);
        if (version.equals(Sos2Constants.SERVICEVERSION)) {
            opsMeta.addPossibleValuesParameter(Sos2Constants.UpdateSensorDescriptionParams.procedureDescriptionFormat,
                    CodingRepository.getInstance().getSupportedProcedureDescriptionFormats(service, version));
        }
        opsMeta.addAnyParameterValue(Sos2Constants.UpdateSensorDescriptionParams.description);
    }

    public abstract UpdateSensorResponse updateSensorDescription(UpdateSensorRequest request)
            throws OwsExceptionReport;

}
