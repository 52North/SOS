/**
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
package org.n52.sos.decode.kvp;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.decode.OperationDecoderKey;
import org.n52.sos.decode.kvp.v2.GetObservationKvpDecoderv20;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swes.SwesExtension;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.util.KvpHelper;
import org.n52.sos.util.http.MediaTypes;

public class AqdGetObservationKvpDecoder extends GetObservationKvpDecoderv20 {

	private static final DecoderKey KVP_DECODER_KEY_TYPE = new OperationDecoderKey(
			AqdConstants.AQD, AqdConstants.VERSION,
			SosConstants.Operations.GetObservation, MediaTypes.APPLICATION_KVP);

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.singleton(KVP_DECODER_KEY_TYPE);
    }
    
    @Override
    protected boolean parseExtensionParameter(AbstractServiceRequest<?> request, String parameterValues,
        String parameterName) throws OwsExceptionReport {
        if (parameterName
                .equalsIgnoreCase(AqdConstants.EXTENSION_FLOW)) {
            request.addExtension(getFlowExtension(KvpHelper
                    .checkParameterSingleValue(parameterValues, parameterName)));
            return true;
        } else {
            return super.parseExtensionParameter(request, parameterValues, parameterName);
        }
    }

    private SwesExtension<SweText> getFlowExtension(String value) {
        return getSweTextFor(AqdConstants.EXTENSION_FLOW, value);
    }
}
