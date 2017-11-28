/*
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sensorweb.sos.sta.operation;

import org.n52.shetland.ogc.ows.exception.CompositeOwsException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sta.StaConstants;
import org.n52.shetland.ogc.sta.request.StaGetObservedPropertiesRequest;
import org.n52.shetland.ogc.sta.response.StaGetObservedPropertiesResponse;
import org.n52.sos.request.operator.AbstractRequestOperator;

/**
 * SensorThings request operator for ObservedProperty entities.
 *
 * @author <a href="mailto:m.kiesow@52north.org">Martin Kiesow</a>
 */
public class StaGetObservedPropertiesOperator extends AbstractRequestOperator<StaAbstractGetObservedPropertiesHandler, StaGetObservedPropertiesRequest, StaGetObservedPropertiesResponse> {

    public StaGetObservedPropertiesOperator() {
        super(StaConstants.SERVICE_NAME, StaConstants.VERSION_1_0, StaConstants.Operation.GET_OBSERVED_PROPERTIES.name(), StaGetObservedPropertiesRequest.class);
    }

    @Override
    protected StaGetObservedPropertiesResponse receive(StaGetObservedPropertiesRequest request) throws OwsExceptionReport {
        final StaGetObservedPropertiesResponse response = getOperationHandler().getObservedProperties(request);

        return response;
    }

    @Override
    protected void checkParameters(StaGetObservedPropertiesRequest request) throws OwsExceptionReport {

        final CompositeOwsException exceptions = new CompositeOwsException();
        try {
            checkServiceParameter(request.getService());
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }
        try {
            checkSingleVersionParameter(request);
        } catch (OwsExceptionReport owse) {
            exceptions.add(owse);
        }

        exceptions.throwIfNotEmpty();
    }
}
