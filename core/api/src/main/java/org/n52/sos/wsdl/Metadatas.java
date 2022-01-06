/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
package org.n52.sos.wsdl;

import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.swes.SwesConstants;
import org.n52.shetland.w3c.wsdl.Fault;
import org.n52.shetland.w3c.wsdl.WSDLConstants.SoapRequestActionUris;
import org.n52.shetland.w3c.wsdl.WSDLConstants.SoapResponseActionUris;

public interface Metadatas {
    Metadata DELETE_SENSOR = Metadata.newMetadata()
            .setName(Sos2Constants.Operations.DeleteSensor.name()).setVersion(Sos2Constants.SERVICEVERSION)
            .setRequest(SwesConstants.QN_DELETE_SENSOR).setRequestAction(SoapRequestActionUris.DELETE_SENSOR)
            .setResponse(SwesConstants.QN_DELETE_SENSOR_RESPONSE)
            .setResponseAction(SoapResponseActionUris.DELETE_SENSOR).setFaults(Fault.DEFAULT_FAULTS).build();

    Metadata DESCRIBE_SENSOR = Metadata.newMetadata()
            .setName(SosConstants.Operations.DescribeSensor.name()).setVersion(Sos2Constants.SERVICEVERSION)
            .setRequest(SwesConstants.QN_DESCRIBE_SENSOR).setRequestAction(SoapRequestActionUris.DESCRIBE_SENSOR)
            .setResponse(SwesConstants.QN_DESCRIBE_SENSOR_RESPONSE)
            .setResponseAction(SoapResponseActionUris.DESCRIBE_SENSOR).setFaults(Fault.DEFAULT_FAULTS).build();

    Metadata GET_CAPABILITIES = Metadata.newMetadata()
            .setName(SosConstants.Operations.GetCapabilities.name()).setVersion(Sos2Constants.SERVICEVERSION)
            .setRequest(Sos2Constants.QN_GET_CAPABILITIES)
            .setRequestAction(SoapRequestActionUris.GET_CAPABILITIES).setResponse(Sos2Constants.QN_CAPABILITIES)
            .setResponseAction(SoapResponseActionUris.GET_CAPABILITIES).setFaults(Fault.DEFAULT_FAULTS)
            // .addFault(WSDLFault.VERSION_NEGOTIATION_FAILED_EXCEPTION)
            // .addFault(WSDLFault.INVALID_UPDATE_SEQUENCE_EXCEPTION)
            .build();

    Metadata GET_FEATURE_OF_INTEREST = Metadata.newMetadata()
            .setName(SosConstants.Operations.GetFeatureOfInterest.name()).setVersion(Sos2Constants.SERVICEVERSION)
            .setRequest(Sos2Constants.QN_GET_FEATURE_OF_INTEREST)
            .setRequestAction(SoapRequestActionUris.GET_FEATURE_OF_INTEREST)
            .setResponse(Sos2Constants.QN_GET_FEATURE_OF_INTEREST_RESPONSE)
            .setResponseAction(SoapResponseActionUris.GET_FEATURE_OF_INTEREST).setFaults(Fault.DEFAULT_FAULTS)
            .build();

    Metadata GET_OBSERVATION = Metadata.newMetadata()
            .setName(SosConstants.Operations.GetObservation.name()).setVersion(Sos2Constants.SERVICEVERSION)
            .setRequest(Sos2Constants.QN_GET_OBSERVATION).setRequestAction(SoapRequestActionUris.GET_OBSERVATION)
            .setResponse(Sos2Constants.QN_GET_OBSERVATION_RESPONSE)
            .setResponseAction(SoapResponseActionUris.INSERT_OBSERVATION).setFaults(Fault.DEFAULT_FAULTS)
            .build();

    Metadata GET_OBSERVATION_BY_ID = Metadata.newMetadata()
            .setName(SosConstants.Operations.GetObservationById.name()).setVersion(Sos2Constants.SERVICEVERSION)
            .setRequest(Sos2Constants.QN_GET_OBSERVATION_BY_ID)
            .setRequestAction(SoapRequestActionUris.GET_OBSERVATION_BY_ID)
            .setResponse(Sos2Constants.QN_GET_OBSERVATION_BY_ID_RESPONSE)
            .setResponseAction(SoapResponseActionUris.GET_OBSERVATION_BY_ID).setFaults(Fault.DEFAULT_FAULTS)
            .build();

    Metadata GET_RESULT = Metadata.newMetadata().setName(SosConstants.Operations.GetResult.name())
            .setVersion(Sos2Constants.SERVICEVERSION).setRequest(Sos2Constants.QN_GET_RESULT)
            .setRequestAction(SoapRequestActionUris.GET_RESULT).setResponse(Sos2Constants.QN_GET_RESULT_RESPONSE)
            .setResponseAction(SoapResponseActionUris.GET_RESULT).setFaults(Fault.DEFAULT_FAULTS).build();

    Metadata GET_RESULT_TEMPLATE = Metadata.newMetadata()
            .setName(Sos2Constants.Operations.GetResultTemplate.name()).setVersion(Sos2Constants.SERVICEVERSION)
            .setRequest(Sos2Constants.QN_GET_RESULT_TEMPLATE)
            .setRequestAction(SoapRequestActionUris.GET_RESULT_TEMPLATE)
            .setResponse(Sos2Constants.QN_GET_RESULT_TEMPLATE_RESPONSE)
            .setResponseAction(SoapResponseActionUris.GET_RESULT_TEMPLATE).setFaults(Fault.DEFAULT_FAULTS)
            .build();

    Metadata INSERT_OBSERVATION = Metadata.newMetadata()
            .setName(SosConstants.Operations.InsertObservation.name()).setVersion(Sos2Constants.SERVICEVERSION)
            .setRequest(Sos2Constants.QN_INSERT_OBSERVATION)
            .setRequestAction(SoapRequestActionUris.INSERT_OBSERVATION)
            .setResponse(Sos2Constants.QN_INSERT_OBSERVATION_RESPONSE)
            .setResponseAction(SoapResponseActionUris.INSERT_OBSERVATION).setFaults(Fault.DEFAULT_FAULTS)
            .build();

    Metadata INSERT_RESULT = Metadata.newMetadata()
            .setName(Sos2Constants.Operations.InsertResult.name()).setVersion(Sos2Constants.SERVICEVERSION)
            .setRequest(Sos2Constants.QN_INSERT_RESULT).setRequestAction(SoapRequestActionUris.INSERT_RESULT)
            .setResponse(Sos2Constants.QN_INSERT_RESULT_RESPONSE)
            .setResponseAction(SoapResponseActionUris.INSERT_RESULT).setFaults(Fault.DEFAULT_FAULTS).build();

    Metadata INSERT_RESULT_TEMPLATE = Metadata.newMetadata()
            .setName(Sos2Constants.Operations.InsertResultTemplate.name())
            .setVersion(Sos2Constants.SERVICEVERSION).setRequest(Sos2Constants.QN_INSERT_RESULT_TEMPLATE)
            .setRequestAction(SoapRequestActionUris.INSERT_RESULT_TEMPLATE)
            .setResponse(Sos2Constants.QN_INSERT_RESULT_TEMPLATE_RESPONSE)
            .setResponseAction(SoapResponseActionUris.INSERT_RESULT_TEMPLATE).setFaults(Fault.DEFAULT_FAULTS)
            .build();

    Metadata INSERT_SENSOR = Metadata.newMetadata()
            .setName(Sos2Constants.Operations.InsertSensor.name()).setVersion(Sos2Constants.SERVICEVERSION)
            .setRequest(SwesConstants.QN_INSERT_SENSOR).setRequestAction(SoapRequestActionUris.INSERT_SENSOR)
            .setResponse(SwesConstants.QN_INSERT_SENSOR_RESPONSE)
            .setResponseAction(SoapResponseActionUris.INSERT_SENSOR).setFaults(Fault.DEFAULT_FAULTS).build();

    Metadata UPDATE_SENSOR_DESCRIPTION = Metadata.newMetadata()
            .setName(Sos2Constants.Operations.UpdateSensorDescription.name())
            .setVersion(Sos2Constants.SERVICEVERSION).setRequest(SwesConstants.QN_UPDATE_SENSOR_DESCRIPTION)
            .setRequestAction(SoapRequestActionUris.UPDATE_SENSOR_DESCRIPTION)
            .setResponse(SwesConstants.QN_UPDATE_SENSOR_DESCRIPTION_RESPONSE)
            .setResponseAction(SoapResponseActionUris.UPDATE_SENSOR_DESCRIPTION)
            .setFaults(Fault.DEFAULT_FAULTS).build();

}
