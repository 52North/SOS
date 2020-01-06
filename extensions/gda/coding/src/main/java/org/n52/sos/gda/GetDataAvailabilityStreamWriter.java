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
package org.n52.sos.gda;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.n52.sos.gda.GetDataAvailabilityResponse.DataAvailability;
import org.n52.sos.gda.GetDataAvailabilityResponse.ObservationFormatDescriptor;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.gml.ReferenceType;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.swe.SweConstants;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.collect.Sets;

/**
 * GetDataAvailability response stream writer.
 * 
 * @author Christian Autermann <c.autermann@52north.org>
 * 
 * @since 4.4.0
 */
public class GetDataAvailabilityStreamWriter extends AbstractGetDataAvailabilityStreamWriter {
    
    public GetDataAvailabilityStreamWriter(String version, List<DataAvailability> gdas) {
        super(version, gdas);
    }

    @Override
    protected void writeGetDataAvailabilityResponse() throws XMLStreamException, OwsExceptionReport {
        start(GetDataAvailabilityConstants.GDA_GET_DATA_AVAILABILITY_RESPONSE);
        namespace(GetDataAvailabilityConstants.NS_GDA_PREFIX, GetDataAvailabilityConstants.NS_GDA);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        namespace(SweConstants.NS_SWE_PREFIX, SweConstants.NS_SWE_20);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        for (DataAvailability da : getGDAs()) {
            wirteDataAvailabilityMember(da);
        }
        end(GetDataAvailabilityConstants.GDA_GET_DATA_AVAILABILITY_RESPONSE);
    }

    @Override
    protected void wirteDataAvailabilityMember(DataAvailability da) throws XMLStreamException, OwsExceptionReport {
        start(GetDataAvailabilityConstants.GDA_DATA_AVAILABILITY_MEMBER);
        attr(GmlConstants.QN_ID_32, DATA_AVAILABILITY_PREFIX + dataAvailabilityCount++);
        writeProcedure(da, GetDataAvailabilityConstants.GDA_PROCEDURE);
        writeObservedProperty(da, GetDataAvailabilityConstants.GDA_OBSERVED_PROPERTY);
        writeFeatureOfInterest(da, GetDataAvailabilityConstants.GDA_FEATURE_OF_INTEREST);
        writePhenomenonTime(da, GetDataAvailabilityConstants.GDA_PHENOMENON_TIME);
        if (da.isSetCount()) {
            writeCount(da.getCount(), GetDataAvailabilityConstants.GDA_COUNT);
        }
        if (da.isSetResultTime()) {
            writeResultTimes(da.getResultTimes(), GetDataAvailabilityConstants.GDA_EXTENSION);
        }
        if (da.isSetOffering()) {
            writeOffering(da.getOffering(), GetDataAvailabilityConstants.GDA_EXTENSION);
        }
        if (da.isSetFormatDescriptors()) {
            Set<String> observationTypes = Sets.newHashSet();
            for (ObservationFormatDescriptor ofd : da.getFormatDescriptor().getObservationFormatDescriptors()) {
                observationTypes.addAll(ofd.getObservationTypes());
            }
            writeObservationTypes(observationTypes, GetDataAvailabilityConstants.GDA_EXTENSION);
        }
        if (da.isSetMetadata()) {
            writeMetadata(da.getMetadata(), GetDataAvailabilityConstants.GDA_EXTENSION);
        }
        end(GetDataAvailabilityConstants.GDA_DATA_AVAILABILITY_MEMBER);
    }

    protected void writeOffering(ReferenceType offering, QName element) throws XMLStreamException {
        start(GetDataAvailabilityConstants.GDA_EXTENSION);
        start(SweConstants.QN_TEXT_SWE_200);
        attr("definition", "offering");
        start(SweConstants.QN_VALUE_SWE_200);
        chars(offering.getHref());
        end(SweConstants.QN_VALUE_SWE_200);
        end(SweConstants.QN_TEXT_SWE_200);
        end(GetDataAvailabilityConstants.GDA_EXTENSION);
        
    }

    protected void writeObservationTypes(Set<String> observationTypes, QName element) throws XMLStreamException {
        int observationTypeCount = 1;
        start(GetDataAvailabilityConstants.GDA_EXTENSION);
        start(SweConstants.QN_DATA_RECORD_SWE_200);
        attr("definition", "observationTypes");
        for (String observationType : observationTypes) {
            start(SweConstants.QN_FIELD_200);
            attr("name", "observationType_" + observationTypeCount++);
            start(SweConstants.QN_TEXT_SWE_200);
            attr("definition", "observationType");
            start(SweConstants.QN_VALUE_SWE_200);
            chars(observationType);
            end(SweConstants.QN_VALUE_SWE_200);
            end(SweConstants.QN_TEXT_SWE_200);
            end(SweConstants.QN_FIELD_200);
        }
        end(SweConstants.QN_DATA_RECORD_SWE_200);
        end(GetDataAvailabilityConstants.GDA_EXTENSION);
    }

}
