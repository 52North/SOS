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
package org.n52.svalbard.gda.v20;

import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.n52.sos.gda.AbstractGetDataAvailabilityStreamWriter;
import org.n52.sos.gda.GetDataAvailabilityConstants;
import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.gda.GetDataAvailabilityResponse.FormatDescriptor;
import org.n52.sos.gda.GetDataAvailabilityResponse.ObservationFormatDescriptor;
import org.n52.sos.gda.GetDataAvailabilityResponse.ProcedureDescriptionFormatDescriptor;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.w3c.W3CConstants;

/**
 * GetDataAvailability response stream writer.
 * 
 * @author Carsten Hollmann
 * 
 * @since 4.4.0
 */
public class GetDataAvailabilityStreamWriter extends AbstractGetDataAvailabilityStreamWriter {
    
    public GetDataAvailabilityStreamWriter(String version, List<DataAvailability> gdas) {
        super(version, gdas);
    }

    @Override
    protected void writeGetDataAvailabilityResponse() throws XMLStreamException, OwsExceptionReport {
        start(GetDataAvailabilityConstants.GDA_GET_DATA_AVAILABILITY_20_RESPONSE);
        namespace(GetDataAvailabilityConstants.NS_GDA_PREFIX, GetDataAvailabilityConstants.NS_GDA_20);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        namespace(SweConstants.NS_SWE_PREFIX, SweConstants.NS_SWE_20);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        for (DataAvailability da : getGDAs()) {
            wirteDataAvailabilityMember(da);
        }
        end(GetDataAvailabilityConstants.GDA_GET_DATA_AVAILABILITY_20_RESPONSE);
    }
    
    protected void writeOffering(DataAvailability da, QName element) throws XMLStreamException {
        start(element);
        attr(GetDataAvailabilityConstants.XLINK_HREF, da.getOffering().getHref());
        if (da.getOffering().isSetTitle()) {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getOffering().getTitle());
        } else {
            attr(GetDataAvailabilityConstants.XLINK_TITLE, da.getOffering().getTitleFromHref());
        }
        end(element);
    }

    protected void wirteDataAvailabilityMember(DataAvailability da) throws XMLStreamException, OwsExceptionReport {
        start(GetDataAvailabilityConstants.GDA_DATA_AVAILABILITY_20_MEMBER);
        attr(GmlConstants.QN_ID_32, DATA_AVAILABILITY_PREFIX + dataAvailabilityCount++);
        writeProcedure(da, GetDataAvailabilityConstants.GDA_20_PROCEDURE);
        writeObservedProperty(da, GetDataAvailabilityConstants.GDA_20_OBSERVED_PROPERTY);
        writeFeatureOfInterest(da, GetDataAvailabilityConstants.GDA_20_FEATURE_OF_INTEREST);
        writePhenomenonTime(da, GetDataAvailabilityConstants.GDA_20PHENOMENON_TIME);
        if (da.isSetCount()) {
            writeCount(da.getCount(), GetDataAvailabilityConstants.GDA_20_COUNT);
        }
        if (da.isSetResultTime()) {
            writeResultTimes(da.getResultTimes(), GetDataAvailabilityConstants.GDA_20_EXTENSION);
        }
        if (da.isSetOffering()) {
            writeOffering(da, GetDataAvailabilityConstants.GDA_20_OFFERING);
        }
        if (da.isSetFormatDescriptors()) {
           writeFormatDescriptor(da.getFormatDescriptor(), GetDataAvailabilityConstants.GDA_20_FORMAT_DESCRIPTOR);
        }
        if (da.isSetMetadata()) {
            writeMetadata(da.getMetadata(), GetDataAvailabilityConstants.GDA_20_EXTENSION);
        }
        end(GetDataAvailabilityConstants.GDA_DATA_AVAILABILITY_20_MEMBER);
    }

    protected void writeFormatDescriptor(FormatDescriptor formatDescriptor, QName element) throws XMLStreamException {
        start(element);
        writeProcedureDescriptionFormatDescriptor(formatDescriptor.getProcedureDescriptionFormatDescriptor(), GetDataAvailabilityConstants.GDA_20_PROCEDURE_FORMAT_DESCRIPTOR);
        for (ObservationFormatDescriptor observationFormatDescriptor : formatDescriptor.getObservationFormatDescriptors()) {
            writeObservationFormatDescriptor(observationFormatDescriptor, GetDataAvailabilityConstants.GDA_20_OBSERVATION_FORMAT_DESCRIPTOR);
        }
        end(element);
    }
    
    protected void writeProcedureDescriptionFormatDescriptor(ProcedureDescriptionFormatDescriptor formatDescriptor, QName element) throws XMLStreamException {
        start(element);
        writeElementWithStringValue(formatDescriptor.getProcedureDescriptionFormat(), GetDataAvailabilityConstants.GDA_20_PROCEDURE_DESCRIPTION_FORMAT);
        end(element);
    }
    
    protected void writeObservationFormatDescriptor(ObservationFormatDescriptor formatDescriptor, QName element) throws XMLStreamException {
        start(element);
        writeElementWithStringValue(formatDescriptor.getResponseFormat(), GetDataAvailabilityConstants.GDA_20_RESPONSE_FORMAT);
        for (String observationType : formatDescriptor.getObservationTypes()) {
            writeElementWithStringValue(observationType, GetDataAvailabilityConstants.GDA_20_OBSERVATION_TYPE);
        }
        end(element);
    }

    protected void writeElementWithStringValue(String value, QName element) throws XMLStreamException {
        start(element);
        chars(value);
        end(element);
    }
    
   

}
