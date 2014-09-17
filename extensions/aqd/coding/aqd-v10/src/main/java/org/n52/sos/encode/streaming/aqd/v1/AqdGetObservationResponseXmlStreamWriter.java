/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.encode.streaming.aqd.v1;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.Encoder;
import org.n52.sos.encode.EncodingValues;
import org.n52.sos.encode.XmlStreamWriter;
import org.n52.sos.encode.streaming.StreamingDataEncoder;
import org.n52.sos.encode.streaming.StreamingEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.GmlConstants;
import org.n52.sos.ogc.om.AbstractStreaming;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.features.FeatureCollection;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.sos.w3c.W3CConstants;

/**
 * XML stream writer implementation for AQD eResporting
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.3.0
 *
 */
public class AqdGetObservationResponseXmlStreamWriter extends XmlStreamWriter<FeatureCollection> implements
        StreamingDataEncoder {

    private FeatureCollection featureCollection;

    /**
     * constructor
     */
    public AqdGetObservationResponseXmlStreamWriter() {
    }

    /**
     * constructor
     * 
     * @param observation
     *            {@link FeatureCollection} to write to stream
     */
    public AqdGetObservationResponseXmlStreamWriter(FeatureCollection featureCollection) {
        setFeatureCollection(featureCollection);
    }

    /**
     * Set {@link FeatureCollection} which should be written
     * 
     * @param featureCollection
     *            the {@link FeatureCollection}
     */
    private void setFeatureCollection(FeatureCollection featureCollection) {
        this.featureCollection = featureCollection;
    }

    /**
     * Get the {@link FeatureCollection} which should be written
     * 
     * @return the {@link FeatureCollection}
     */
    private FeatureCollection getFeatureCollection() {
        return featureCollection;
    }

    @Override
    public void write(OutputStream out) throws XMLStreamException, OwsExceptionReport {
        write(getFeatureCollection(), out);
    }

    @Override
    public void write(OutputStream out, EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        write(getFeatureCollection(), out, encodingValues);
    }

    @Override
    public void write(FeatureCollection featureCollection, OutputStream out) throws XMLStreamException,
            OwsExceptionReport {
        write(featureCollection, out, new EncodingValues());

    }

    @Override
    public void write(FeatureCollection featureCollection, OutputStream out, EncodingValues encodingValues)
            throws XMLStreamException, OwsExceptionReport {
        try {
            setFeatureCollection(featureCollection);
            init(out, encodingValues);
            start(encodingValues.isEmbedded());
            writeFeatureCollectionDoc(encodingValues);
            end();
            finish();
        } catch (XMLStreamException xmlse) {
            throw new NoApplicableCodeException().causedBy(xmlse);
        }
    }

    private void writeFeatureCollectionDoc(EncodingValues encodingValues) throws XMLStreamException,
            OwsExceptionReport {
        start(GmlConstants.QN_FEATURE_COLLECTION_32);
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        namespace(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM_2);
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        addGmlId(featureCollection.getGmlId());
        writeNewLine();
        for (AbstractFeature abstractFeature : featureCollection.getMembers().values()) {
            if (abstractFeature instanceof OmObservation
                    && ((OmObservation) abstractFeature).getValue() instanceof AbstractStreaming) {
                List<OmObservation> mergeObservation =
                        ((AbstractStreaming) ((OmObservation) abstractFeature).getValue()).mergeObservation();
                for (OmObservation omObservation : mergeObservation) {
                    writeMember(omObservation, getEncoder(abstractFeature, encodingValues.getAdditionalValues()),
                            encodingValues);
                }
            } else {
                writeMember(abstractFeature, getEncoder(abstractFeature, encodingValues.getAdditionalValues()),
                        encodingValues);
            }
        }
        indent--;
        end(GmlConstants.QN_FEATURE_COLLECTION_32);
    }

    private String addGmlId(String gmlId) throws XMLStreamException {
        attr(GmlConstants.QN_ID_32, gmlId);
        return gmlId;
    }

    private void writeMember(AbstractFeature abstractFeature, Encoder<XmlObject, AbstractFeature> encoder,
            EncodingValues encodingValues) throws XMLStreamException, OwsExceptionReport {
        start(GmlConstants.QN_FEATURE_MEMBER_32);
        writeNewLine();
        if (encoder instanceof StreamingEncoder<?, ?>) {
            ((StreamingEncoder<XmlObject, AbstractFeature>) encoder).encode(abstractFeature, getOutputStream(),
                    encodingValues.setAsDocument(true).setEmbedded(true).setIndent(indent));
        } else {
            rawText((encoder.encode(abstractFeature, encodingValues.getAdditionalValues())).xmlText(XmlOptionsHelper
                    .getInstance().getXmlOptions()));
        }
        indent--;
        writeNewLine();
        end(GmlConstants.QN_FEATURE_MEMBER_32);
        indent++;
    }

    private Encoder<XmlObject, AbstractFeature> getEncoder(AbstractFeature feature,
            Map<HelperValues, String> additionalValues) throws OwsExceptionReport {
        if (feature instanceof AbstractFeature && feature.isSetDefaultElementEncoding()) {
            return CodingHelper.getEncoder(feature.getDefaultElementEncoding(), feature);
        } else if (feature instanceof AbstractFeature && additionalValues.containsKey(HelperValues.ENCODE_NAMESPACE)) {
            return CodingHelper.getEncoder(additionalValues.get(HelperValues.ENCODE_NAMESPACE), feature);
        }
        return null;
    }

}
