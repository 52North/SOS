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
package org.n52.svalbard.inspire.omso.v30.encode;

import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.series.wml.WaterMLConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.svalbard.inspire.omso.InspireOMSOConstants;
import org.n52.svalbard.inspire.omso.PointTimeSeriesObservation;
import org.n52.svalbard.inspire.omso.v30.encode.streaming.PointTimeSeriesObservationXmlStreamWriter;

import com.google.common.collect.Sets;

import eu.europa.ec.inspire.schemas.omso.x30.PointTimeSeriesObservationType;
import net.opengis.om.x20.OMObservationType;

/**
 * {@link Encoder} implementation for {@link PointTimeSeriesObservation} to
 * {@link PointTimeSeriesObservationType}
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class PointTimeSeriesObservationTypeEncoder extends AbstractOmInspireEncoder {

    private static final Set<EncoderKey> ENCODER_KEYS =
            CodingHelper.encoderKeysForElements(InspireOMSOConstants.NS_OMSO_30, PointTimeSeriesObservation.class);

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }
    
    @Override
    public Map<String, Set<String>> getSupportedResponseFormatObservationTypes() {
        return Collections.singletonMap(InspireOMSOConstants.NS_OMSO_30,
                (Set<String>) Sets.newHashSet(InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION));
    }

    @Override
    protected XmlObject createResult(OmObservation sosObservation) throws OwsExceptionReport {
        return encodeResult(sosObservation.getValue());
    }

    @Override
    protected XmlObject encodeResult(ObservationValue<?> observationValue) throws OwsExceptionReport {
        return CodingHelper.encodeObjectToXml(WaterMLConstants.NS_WML_20, observationValue);
    }

    @Override
    protected String getObservationType() {
        return InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION;
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        return super.encode(element, additionalValues);
    }

    @Override
    public void encode(Object objectToEncode, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        encodingValues.setEncoder(this);
        if (objectToEncode instanceof OmObservation) {
            try {
                new PointTimeSeriesObservationXmlStreamWriter().write((OmObservation) objectToEncode, outputStream,
                        encodingValues);
            } catch (XMLStreamException xmlse) {
                throw new NoApplicableCodeException().causedBy(xmlse)
                        .withMessage("Error while writing element to stream!");
            }
        }
        super.encode(objectToEncode, outputStream, encodingValues);
    }

    protected OMObservationType createOmObservationType() {
        return PointTimeSeriesObservationType.Factory.newInstance(getXmlOptions());
    }

}
