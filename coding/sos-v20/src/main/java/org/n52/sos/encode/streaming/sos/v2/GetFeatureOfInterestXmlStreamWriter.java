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
package org.n52.sos.encode.streaming.sos.v2;

import java.io.OutputStream;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.n52.sos.coding.CodingRepository;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.encode.streaming.StreamingDataEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.Sos2StreamingConstants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swes.SwesConstants;
import org.n52.sos.response.GetFeatureOfInterestResponse;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.Configurator;
import org.n52.sos.service.profile.Profile;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.sos.w3c.W3CConstants;

import com.google.common.collect.Sets;

/**
 * Implementatio of {@link XmlStreamWriter} for {@link GetFeatureOfInterestResponse}
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.4.0
 *
 */
public class GetFeatureOfInterestXmlStreamWriter extends AbstractSwesXmlStreamWriter<GetFeatureOfInterestResponse> implements StreamingDataEncoder {

    private GetFeatureOfInterestResponse response;
    
    /**
     * constructor
     */
    public GetFeatureOfInterestXmlStreamWriter() {
    }

    /**
     * constructor
     *
     * @param response
     *            {@link GetObservationResponse} to write to stream
     */
    public GetFeatureOfInterestXmlStreamWriter(GetFeatureOfInterestResponse response) {
        setResponse(response);
    }
    
    @Override
    public void write(OutputStream out) throws XMLStreamException, OwsExceptionReport {
        write(getResponse(), out);
    }

    @Override
    public void write(OutputStream out, EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        write(getResponse(), out, encodingValues);
    }

    @Override
    public void write(GetFeatureOfInterestResponse response, OutputStream out) throws XMLStreamException, OwsExceptionReport {
        write(response, out, new EncodingValues());
    }

    @Override
    public void write(GetFeatureOfInterestResponse response, OutputStream out, EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        try {
            init(out, encodingValues);
            start(encodingValues.isEmbedded());
            writeGetFeatureOfInterestResponseDoc(response, encodingValues);
            end();
            finish();
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse);
        }
    }
    
    /**
     * Set the {@link GetFeatureOfInterestResponse} to be written to stream
     *
     * @param response
     *            {@link GetFeatureOfInterestResponse} to write to stream
     */
    protected void setResponse(GetFeatureOfInterestResponse response) {
        this.response = response;
    }

    /**
     * Get the {@link GetFeatureOfInterestResponse} to write to stream
     *
     * @return {@link GetFeatureOfInterestResponse} to write
     */
    protected GetFeatureOfInterestResponse getResponse() {
        return response;
    }
    
    private void writeGetFeatureOfInterestResponseDoc(GetFeatureOfInterestResponse response, EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        start(Sos2StreamingConstants.QN_GET_FEATURE_OF_INTEREST_RESPONSE);
        Map<String, String> namespaces = new LinkedHashMap<>();
        namespaces.put(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        namespaces.put(Sos2StreamingConstants.NS_SOS_PREFIX, Sos2StreamingConstants.NS_SOS_20);
        namespaces.put(SwesConstants.NS_SWES_PREFIX, SwesConstants.NS_SWES_20);
        namespaces.put(SfConstants.NS_SA_PREFIX, SfConstants.NS_SA);
        namespaces.put(SfConstants.NS_SAMS_PREFIX, SfConstants.NS_SAMS);
        namespaces.put(SfConstants.NS_SF_PREFIX, SfConstants.NS_SF);
        namespaces.put(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        for (Entry<String, String> namespace : namespaces.entrySet()) {
            namespace(namespace.getKey(), namespace.getValue());
        }
        // get observation encoder
        encodingValues.getAdditionalValues().put(HelperValues.DOCUMENT, null);
        // write schemaLocation
        schemaLocation(getSchemaLocation(encodingValues, namespaces.values()));
        writeNewLine();
        if (response.isSetExtensions()) {
            writeExtensions(response.getExtensions());
            writeNewLine();
        }
        AbstractFeature feature = response.getAbstractFeature();
        if (feature instanceof FeatureCollection) {
            for (AbstractFeature f : (FeatureCollection) feature) {
                if (f instanceof AbstractSamplingFeature && ((AbstractSamplingFeature) f).isSetGeometry()) {
                    writeFeatureMember(f, encodingValues);
                } else {
                    writeReferencedFeatureMember(f);
                }
                writeNewLine();
            }
        } else if (feature instanceof AbstractSamplingFeature) {
            if (feature instanceof AbstractSamplingFeature && ((AbstractSamplingFeature) feature).isSetGeometry()) {
                writeFeatureMember(feature, encodingValues);
            } else {
                writeReferencedFeatureMember(feature);
            }
            writeNewLine();
        }
        indent--;
        end(Sos2StreamingConstants.QN_GET_FEATURE_OF_INTEREST_RESPONSE);
    }

    private Set<SchemaLocation> getSchemaLocation(EncodingValues encodingValue, Collection<String> namespaces) {
        Set<SchemaLocation> schemaLocations = Sets.newHashSet();
        if (encodingValue.isSetEncoder()
                && CollectionHelper.isNotEmpty(encodingValue.getEncoder().getSchemaLocations())) {
            schemaLocations.addAll(encodingValue.getEncoder().getSchemaLocations());
        } else {
            schemaLocations.add(Sos2Constants.SOS_GET_FEATURE_OF_INTEREST_SCHEMA_LOCATION);
        }
        for (String namespace : namespaces) {
            schemaLocations.addAll(CodingRepository.getInstance().getSchemaLocation(namespace));
        }
        Profile activeProfile = getActiveProfile();
        if (activeProfile.isSetEncodeFeatureOfInterestNamespace()) {
            schemaLocations.addAll(CodingRepository.getInstance()
                    .getSchemaLocation(activeProfile.getEncodingNamespaceForFeatureOfInterest()));
        }
        return schemaLocations;
    }

    private void writeFeatureMember(AbstractFeature af, EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        start(Sos2StreamingConstants.QN_FEATURE_MEMBER);
        writeNewLine();
        
        Map<HelperValues, String> additionalValues =
                new EnumMap<SosConstants.HelperValues, String>(HelperValues.class);
        Profile activeProfile = getActiveProfile();
        if (activeProfile.isSetEncodeFeatureOfInterestNamespace()) {
            additionalValues.put(HelperValues.ENCODE_NAMESPACE,
                    activeProfile.getEncodingNamespaceForFeatureOfInterest());
        }
        rawText(encodeGml(encodingValues.getAdditionalValues(), af)
                .xmlText(XmlOptionsHelper.getInstance().getXmlOptions()));
        indent--;
        writeNewLine();
        end(Sos2StreamingConstants.QN_FEATURE_MEMBER);
        indent++;
    }
    
    private void writeReferencedFeatureMember(AbstractFeature af) throws XMLStreamException {
        empty(Sos2StreamingConstants.QN_FEATURE_MEMBER);
        addXlinkHrefAttr(af.getIdentifier());
        if (af.isSetName()) {
            addXlinkTitleAttr(af.getFirstName().getValue());
        }
    }
    
    protected Profile getActiveProfile() {
        return Configurator.getInstance().getProfileHandler().getActiveProfile();
    }
}
