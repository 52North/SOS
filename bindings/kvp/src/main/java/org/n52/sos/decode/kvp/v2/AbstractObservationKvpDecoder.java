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
package org.n52.sos.decode.kvp.v2;

import org.n52.sos.decode.kvp.AbstractKvpDecoder;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.util.KvpHelper;

public abstract class AbstractObservationKvpDecoder extends AbstractKvpDecoder {
    
    @Override
    protected boolean parseDefaultParameter(AbstractServiceRequest<?> request, String parameterValues,
            String parameterName) throws OwsExceptionReport {
        if (parameterName.equalsIgnoreCase(SosConstants.GetObservationParams.resultType.name())) {
            if (request instanceof AbstractObservationRequest) {
                ((AbstractObservationRequest)request).setResultModel(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
                return true;
            }
        }
        // responseFormat (optional)
        else if (parameterName.equalsIgnoreCase(SosConstants.GetObservationParams.responseFormat.name())) {
            ((AbstractObservationRequest)request).setResponseFormat(KvpHelper.checkParameterSingleValue(parameterValues, parameterName));
            return true;
        }
        return super.parseDefaultParameter(request, parameterValues, parameterName);
    }
    
}
