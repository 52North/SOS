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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.n52.sos.encode.AbstractXmlEncoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.svalbard.inspire.omso.InspireOMSOConstants;
import org.n52.svalbard.inspire.omso.v30.encode.streaming.PointTimeSeriesObservationXmlStreamWriter;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * {@link ObservationEncoder} implementation for INSPIRE OM
 * 
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.4.0
 *
 */
public class InspireOmObservationEncoder extends AbstractXmlEncoder<Object>
        implements ObservationEncoder<XmlObject, Object>, StreamingEncoder<XmlObject, Object> {

    private static final Set<EncoderKey> ENCODER_KEYS =
            CodingHelper.encoderKeysForElements(InspireOMSOConstants.NS_OMSO_30, OmObservation.class);

    private static final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS =
            Collections.singletonMap(SosConstants.SOS, Collections.singletonMap(Sos2Constants.SERVICEVERSION,
                    Collections.singleton(InspireOMSOConstants.NS_OMSO_30)));

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return Collections.unmodifiableSet(ENCODER_KEYS);
    }

    @Override
    public XmlObject encode(Object element, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        if (element instanceof OmObservation) {
            return encodeInspireOmsoType((OmObservation) element, additionalValues);
        }
        throw new UnsupportedEncoderInputException(this, element);
    }

    @Override
    public void encode(Object objectToEncode, OutputStream outputStream) throws OwsExceptionReport {
        encode(objectToEncode, outputStream, new EncodingValues());
    }

    @Override
    public void encode(Object element, OutputStream outputStream, EncodingValues encodingValues)
            throws OwsExceptionReport {
        try {
            if (element instanceof OmObservation && InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION
                    .equals(((OmObservation) element).getObservationConstellation().getObservationType())) {
                new PointTimeSeriesObservationXmlStreamWriter().write((OmObservation)element, outputStream, encodingValues);
            } else {
                XmlOptions xmlOptions = getXmlOptions();
                if (encodingValues.isEmbedded()) {
                    xmlOptions.setSaveNoXmlDecl();
                }
                // writeIndent(encodingValues.getIndent(), outputStream);
                encode(element, encodingValues.getAdditionalValues()).save(outputStream, xmlOptions);
            }
        } catch (IOException ioe) {
            throw new NoApplicableCodeException().causedBy(ioe).withMessage("Error while writing element to stream!");
        } catch (XMLStreamException xmlse) {
                throw new NoApplicableCodeException().causedBy(xmlse)
                        .withMessage("Error while writing element to stream!");
        } finally {
            if (encodingValues.isEmbedded()) {
                getXmlOptions().remove(XmlOptions.SAVE_NO_XML_DECL);
            }
        }
    }

    @Override
    public boolean forceStreaming() {
        return false;
    }

    protected static XmlObject encodeInspireOmsoType(OmObservation o, Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (additionalValues == null) {
            additionalValues = Maps.<HelperValues, String> newEnumMap(HelperValues.class);
            additionalValues.put(HelperValues.DOCUMENT, Boolean.toString(true));
        }
        return CodingHelper.encodeObjectToXml(InspireOMSOConstants.NS_OMSO_30, o, additionalValues);
    }

    @Override
    public boolean isObservationAndMeasurmentV20Type() {
        return true;
    }

    @Override
    public boolean shouldObservationsWithSameXBeMerged() {
        return false;
    }

    @Override
    public boolean supportsResultStreamingForMergedValues() {
        return false;
    }

    @Override
    public Set<String> getSupportedResponseFormats(String service, String version) {
        if (SUPPORTED_RESPONSE_FORMATS.get(service) != null
                && SUPPORTED_RESPONSE_FORMATS.get(service).get(version) != null) {
            return SUPPORTED_RESPONSE_FORMATS.get(service).get(version);
        }
        return new HashSet<>(0);
    }

    @Override
    public Map<String, Set<String>> getSupportedResponseFormatObservationTypes() {
        return Maps.newHashMap();
    }

    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
        nameSpacePrefixMap.put(InspireOMSOConstants.NS_OMSO_30, InspireOMSOConstants.NS_OMSO_PREFIX);
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Sets.newHashSet(InspireOMSOConstants.OMSO_SCHEMA_LOCATION);
    }
}
