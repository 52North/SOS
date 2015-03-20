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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractResponseEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * {@code Encoder} to handle {@link GetDataAvailabilityResponse}s.
 * 
 * @author Christian Autermann
 * 
 * @since 4.0.0
 */
public class GetDataAvailabilityXmlEncoder extends AbstractResponseEncoder<GetDataAvailabilityResponse> {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(GetDataAvailabilityXmlEncoder.class);

    public GetDataAvailabilityXmlEncoder() {
        super(SosConstants.SOS, Sos2Constants.SERVICEVERSION, GetDataAvailabilityConstants.OPERATION_NAME,
                Sos2Constants.NS_SOS_20, SosConstants.NS_SOS_PREFIX, GetDataAvailabilityResponse.class, false);
    }

    @Override
    protected Set<SchemaLocation> getConcreteSchemaLocations() {
        return Sets.newHashSet(GetDataAvailabilityConstants.GET_DATA_AVAILABILITY_SCHEMA_LOCATION);
    }

    @Override
    protected XmlObject create(GetDataAvailabilityResponse response) throws OwsExceptionReport {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (Sos2Constants.NS_SOS_20.equals(response.getNamespace())) {
                new SosGetDataAvailabilityStreamWriter(response.getVersion(), response.getDataAvailabilities()).write(out);
            } else {
                new GetDataAvailabilityStreamWriter(response.getVersion(), response.getDataAvailabilities()).write(out);
                XmlObject encodedObject = XmlObject.Factory.parse(out.toString("UTF8"));
                LOG.debug("Encoded object {} is valid: {}", encodedObject.schemaType().toString(),
                        XmlHelper.validateDocument(encodedObject));
                return encodedObject;
            }
            return XmlObject.Factory.parse(out.toString("UTF8"));
        } catch (XMLStreamException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage("Error encoding response");
        } catch (DateTimeFormatException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage("Error encoding response");
        } catch (XmlException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage("Error encoding response");
        } catch (UnsupportedEncodingException ex) {
            throw new NoApplicableCodeException().causedBy(ex).withMessage("Error encoding response");
        }
    }
}
