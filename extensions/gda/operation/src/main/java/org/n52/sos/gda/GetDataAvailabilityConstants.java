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
package org.n52.sos.gda;

import java.net.URI;

import javax.xml.namespace.QName;

import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.W3CConstants;
import org.n52.sos.wsdl.WSDLFault;
import org.n52.sos.wsdl.WSDLOperation;

/**
 * Constants for the GetDataAvailability SOS operation.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public interface GetDataAvailabilityConstants {
    
    String NS_GDA = "http://www.opengis.net/sosgda/1.0";
    
    String SCHEMA_LOCATION_URL_GET_DATA_AVAILABILITY =  "http://waterml2.org/schemas/gda/1.0/gda.xsd";
    
    SchemaLocation GET_DATA_AVAILABILITY_SCHEMA_LOCATION = new SchemaLocation(NS_GDA,
            SCHEMA_LOCATION_URL_GET_DATA_AVAILABILITY);
    
    String NS_GDA_PREFIX = "gda";
    
    String XPATH_PREFIXES_GDA = XmlHelper.getXPathPrefix(NS_GDA_PREFIX, NS_GDA);
    
    String EN_GET_DATA_AVAILABILITY_MEMBER = "dataAvailabilityMember";

    String EN_GET_DATA_AVAILABILITY = "GetDataAvailability";

    String AN_VERSION = "version";

    String AN_SERVICE = "service";

    String EN_GET_DATA_AVAILABILITY_RESPONSE = "GetDataAvailabilityResponse";

    String DATA_AVAILABILITY = "dataAvailability";

    /* TODO is this one right? */
    String CONFORMANCE_CLASS = "http://www.opengis.net/spec/SOS/2.0/conf/daRetrieval";

    /**
     * The operation name.
     */
    String OPERATION_NAME = "GetDataAvailability";

    /**
     * The {@code QName} for {@code sos:dataAvailabilityMember}.
     */
    QName SOS_DATA_AVAILABILITY_MEMBER = new QName(Sos2Constants.NS_SOS_20, EN_GET_DATA_AVAILABILITY_MEMBER,
            SosConstants.NS_SOS_PREFIX);
    
    /**
     * The {@code QName} for {@code gda:dataAvailabilityMember}.
     */
    QName GDA_DATA_AVAILABILITY_MEMBER = new QName(GetDataAvailabilityConstants.NS_GDA, EN_GET_DATA_AVAILABILITY_MEMBER,
            GetDataAvailabilityConstants.NS_GDA_PREFIX);

    /**
     * The {@code QName} for {@code sos:GetDataAvailabilityResponse}.
     */
    QName SOS_GET_DATA_AVAILABILITY_RESPONSE = new QName(Sos2Constants.NS_SOS_20, EN_GET_DATA_AVAILABILITY_RESPONSE,
            SosConstants.NS_SOS_PREFIX);

    /**
     * The {@code QName} for {@code gda:GetDataAvailabilityResponse}.
     */
    QName GDA_GET_DATA_AVAILABILITY_RESPONSE = new QName(GetDataAvailabilityConstants.NS_GDA, EN_GET_DATA_AVAILABILITY_RESPONSE,
            GetDataAvailabilityConstants.NS_GDA_PREFIX);
    
    /**
     * The {@code QName} for {@code sos:GetDataAvailability}.
     */
    QName SOS_GET_DATA_AVAILABILITY = new QName(Sos2Constants.NS_SOS_20, EN_GET_DATA_AVAILABILITY,
            SosConstants.NS_SOS_PREFIX);
    
    /**
     * The {@code QName} for {@code gda:GetDataAvailability}.
     */
    QName GDA_GET_DATA_AVAILABILITY = new QName(GetDataAvailabilityConstants.NS_GDA, EN_GET_DATA_AVAILABILITY,
            GetDataAvailabilityConstants.NS_GDA_PREFIX);
    
    QName GDA_EXTENSION = new QName(GetDataAvailabilityConstants.NS_GDA, "extension",
            GetDataAvailabilityConstants.NS_GDA_PREFIX);

    /**
     * The {@code QName} for {@code version}.
     */
    QName VERSION = new QName(AN_VERSION);

    /**
     * The {@code QName} for {@code service}.
     */
    QName SERVICE = new QName(AN_SERVICE);

    /**
     * The {@code QName} for {@code sos:version}.
     */
    QName SOS_VERSION = new QName(Sos2Constants.NS_SOS_20, AN_VERSION, SosConstants.NS_SOS_PREFIX);

    /**
     * The {@code QName} for {@code sos:service}.
     */
    QName SOS_SERVICE = new QName(Sos2Constants.NS_SOS_20, AN_SERVICE, SosConstants.NS_SOS_PREFIX);

    /**
     * The {@code QName} for {@code xlink:href}.
     */
    QName XLINK_HREF = new QName(W3CConstants.NS_XLINK, W3CConstants.AN_HREF, W3CConstants.NS_XLINK_PREFIX);

    /**
     * The {@code QName} for {@code xlink:title}.
     */
    public static final QName XLINK_TITLE = new QName(W3CConstants.NS_XLINK, W3CConstants.AN_TITLE,
            W3CConstants.NS_XLINK_PREFIX);

    /**
     * The {@code QName} for {@code om:featureOfInterest}.
     */
    QName OM_FEATURE_OF_INTEREST = new QName(OmConstants.NS_OM_2, OmConstants.EN_FEATURE_OF_INTEREST,
            OmConstants.NS_OM_PREFIX);
    
    /**
     * The {@code QName} for {@code gda:featureOfInterest}.
     */
    QName GDA_FEATURE_OF_INTEREST = new QName(GetDataAvailabilityConstants.NS_GDA, OmConstants.EN_FEATURE_OF_INTEREST,
            GetDataAvailabilityConstants.NS_GDA_PREFIX);

    /**
     * The {@code QName} for {@code om:observedProperty}.
     */
    QName OM_OBSERVED_PROPERTY = new QName(OmConstants.NS_OM_2, OmConstants.EN_OBSERVED_PROPERTY,
            OmConstants.NS_OM_PREFIX);
    
    /**
     * The {@code QName} for {@code gda:observedProperty}.
     */
    QName GDA_OBSERVED_PROPERTY = new QName(GetDataAvailabilityConstants.NS_GDA, OmConstants.EN_OBSERVED_PROPERTY,
            GetDataAvailabilityConstants.NS_GDA_PREFIX);

    /**
     * The {@code QName} for {@code om:phenomenonTime}.
     */
    QName OM_PHENOMENON_TIME =
            new QName(OmConstants.NS_OM_2, OmConstants.EN_PHENOMENON_TIME, OmConstants.NS_OM_PREFIX);
    
    /**
     * The {@code QName} for {@code gda:phenomenonTime}.
     */
    QName GDA_PHENOMENON_TIME =
            new QName(GetDataAvailabilityConstants.NS_GDA, OmConstants.EN_PHENOMENON_TIME, GetDataAvailabilityConstants.NS_GDA_PREFIX);

    /**
     * The {@code QName} for {@code om:procedure}.
     */
    QName OM_PROCEDURE = new QName(OmConstants.NS_OM_2, OmConstants.EN_PROCEDURE, OmConstants.NS_OM_PREFIX);
    
    /**
     * The {@code QName} for {@code gda:procedure}.
     */
    QName GDA_PROCEDURE = new QName(GetDataAvailabilityConstants.NS_GDA, OmConstants.EN_PROCEDURE, GetDataAvailabilityConstants.NS_GDA_PREFIX);
    
    /**
     * The {@code QName} for {@code sos:procedure}.
     */
    QName SOS_COUNT = new QName(Sos2Constants.NS_SOS_20, "count", SosConstants.NS_SOS_PREFIX);
    
    /**
     * The {@code QName} for {@code gda:procedure}.
     */
    QName GDA_COUNT = new QName(GetDataAvailabilityConstants.NS_GDA, "count", GetDataAvailabilityConstants.NS_GDA_PREFIX);

    /**
     * The available parameters of the operation.
     */
    enum GetDataAvailabilityParams {
        featureOfInterest, observedProperty, procedure, offering;
    }

    WSDLOperation WSDL_OPERATION =
            WSDLOperation
                    .newWSDLOperation()
                    .setName(OPERATION_NAME)
                    .setVersion(Sos2Constants.SERVICEVERSION)
                    .setRequest(SOS_GET_DATA_AVAILABILITY)
                    .setRequestAction(
                            URI.create("http://www.opengis.net/def/serviceOperation/sos/daRetrieval/2.0/GetDataAvailability"))
                    .setResponse(SOS_GET_DATA_AVAILABILITY_RESPONSE)
                    .setResponseAction(
                            URI.create("http://www.opengis.net/def/serviceOperation/sos/daRetrieval/2.0/GetDataAvailabilityResponse"))
                    .setFaults(WSDLFault.DEFAULT_FAULTS).build();
}
