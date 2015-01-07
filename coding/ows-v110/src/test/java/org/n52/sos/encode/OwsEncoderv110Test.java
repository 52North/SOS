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
package org.n52.sos.encode;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.AbstractMap;
import java.util.Map;

import net.opengis.ows.x11.ExceptionDocument;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ServiceIdentificationDocument.ServiceIdentification;

import org.apache.xmlbeans.XmlObject;
import org.junit.AfterClass;
import org.junit.Test;
import org.n52.sos.config.SettingsManager;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.ows.SosServiceIdentification;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * 
 * @since 4.0.0
 * 
 */
public class OwsEncoderv110Test {

    private final OwsEncoderv110 encoder = new OwsEncoderv110();

    @AfterClass
    public static void cleanUp() {
        SettingsManager.getInstance().cleanup();
    }

    @Test
    public final void should_encode_Exception_into_owsExceptionReport_by_default() throws OwsExceptionReport {
        assertThat(encoder.encode(generateException()), instanceOf(ExceptionReportDocument.class));
    }

    @Test
    public void should_encode_Exception_into_owsException_when_using_flag() throws OwsExceptionReport {
        assertThat(encoder.encode(generateException(), encodeInObservationMap()), instanceOf(ExceptionDocument.class));
    }

    @SuppressWarnings("unchecked")
    // see
    // http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ300
    // for more details
    private Map<HelperValues, String> encodeInObservationMap() {
        return CollectionHelper.map(new AbstractMap.SimpleEntry<SosConstants.HelperValues, String>(
                SosConstants.HelperValues.ENCODE_OWS_EXCEPTION_ONLY, ""));
    }

    private NoApplicableCodeException generateException() {
        final NoApplicableCodeException nace = new NoApplicableCodeException();
        nace.setVersion(Sos2Constants.SERVICEVERSION);
        return nace;
    }

    @Test
    public void should_encode_service_identification_without_service_type_codespace() throws OwsExceptionReport {
        String serviceTypeValue = "serviceType";
        SosServiceIdentification serviceId = new SosServiceIdentification();
        serviceId.setServiceType(serviceTypeValue);
        XmlObject xbEncoded = CodingHelper.encodeObjectToXml(OWSConstants.NS_OWS, serviceId);
        assertThat(xbEncoded, instanceOf(ServiceIdentification.class));
        ServiceIdentification xbServiceId = (ServiceIdentification) xbEncoded;
        assertThat(xbServiceId.getServiceType().getStringValue(), equalTo(serviceTypeValue));
        assertThat(xbServiceId.getServiceType().getCodeSpace(), nullValue());
    }

    @Test
    public void should_encode_service_identification_with_service_type_codespace() throws OwsExceptionReport {
        String serviceTypeValue = "serviceType";
        String serviceTypeCodeSpaceValue = "codeSpace";
        SosServiceIdentification serviceId = new SosServiceIdentification();
        serviceId.setServiceType(serviceTypeValue);
        serviceId.setServiceTypeCodeSpace(serviceTypeCodeSpaceValue);
        XmlObject xbEncoded = CodingHelper.encodeObjectToXml(OWSConstants.NS_OWS, serviceId);
        assertThat(xbEncoded, instanceOf(ServiceIdentification.class));
        ServiceIdentification xbServiceId = (ServiceIdentification) xbEncoded;
        assertThat(xbServiceId.getServiceType().getStringValue(), equalTo(serviceTypeValue));
        assertThat(xbServiceId.getServiceType().getCodeSpace(), equalTo(serviceTypeCodeSpaceValue));
    }
}
