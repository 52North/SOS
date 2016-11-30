/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.stream.XMLStreamException;

import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.iceland.util.Constants;
import org.n52.shetland.iso.GcoConstants;
import org.n52.shetland.iso.gmd.GmdConstants;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.GmlConstants;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.om.OmConstants;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.features.FeatureCollection;
import org.n52.shetland.ogc.om.features.SfConstants;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.swe.SweConstants;
import org.n52.shetland.w3c.SchemaLocation;
import org.n52.shetland.w3c.W3CConstants;
import org.n52.sos.aqd.AqdConstants;
import org.n52.sos.coding.encode.EncodingValues;
import org.n52.sos.coding.encode.XmlStreamWriter;
import org.n52.sos.encode.streaming.StreamingDataEncoder;
import org.n52.shetland.ogc.sos.AbstractStreaming;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlOptionsHelper;
import org.n52.svalbard.HelperValues;
import org.n52.svalbard.encode.Encoder;
import org.n52.svalbard.encode.exception.EncodingException;

import com.google.common.collect.Sets;

/**
 * XML stream writer implementation for AQD eResporting
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.3.0
 *
 */
public class AqdGetObservationResponseXmlStreamWriter
        extends XmlStreamWriter<FeatureCollection>
        implements StreamingDataEncoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(AqdGetObservationResponseXmlStreamWriter.class);

    private static final long TIMER_PERIOD = 250;

    private FeatureCollection featureCollection;

    private Timer timer = new Timer(String.format("empty-string-write-task-for-%s", getClass().getSimpleName()), true);

    private TimerTask timerTask = null;

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
    public void write(OutputStream out) throws XMLStreamException, EncodingException {
        write(getFeatureCollection(), out);
    }

    @Override
    public void write(OutputStream out, EncodingValues encodingValues) throws XMLStreamException, EncodingException {
        write(getFeatureCollection(), out, encodingValues);
    }

    @Override
    public void write(FeatureCollection featureCollection, OutputStream out) throws XMLStreamException,
            EncodingException {
        write(featureCollection, out, new EncodingValues());

    }

    @Override
    public void write(FeatureCollection featureCollection, OutputStream out, EncodingValues encodingValues)
            throws XMLStreamException, EncodingException {
        try {
            setFeatureCollection(featureCollection);
            init(out, encodingValues);
            start(encodingValues.isEmbedded());
            writeFeatureCollectionDoc(encodingValues);
            end();
            finish();
        } finally {
            cleanup();
        }
    }

    private void writeFeatureCollectionDoc(EncodingValues encodingValues) throws XMLStreamException,
            EncodingException {
        start(GmlConstants.QN_FEATURE_COLLECTION_32);
        addNamespaces();
        addSchemaLocations();
        addGmlId(featureCollection.getGmlId());
        TimeInstant resultTime = new TimeInstant(new DateTime(DateTimeZone.UTC));
        for (AbstractFeature abstractFeature : featureCollection.getMembers().values()) {
            long start = System.currentTimeMillis();
            if (abstractFeature instanceof OmObservation
                    && ((OmObservation) abstractFeature).getValue() instanceof AbstractStreaming) {
                try {
                    // start the timer task to write blank strings to avoid
                    // connection closing
                    startTimer();
                    Collection<OmObservation> mergeObservation =
                            ((AbstractStreaming) ((OmObservation) abstractFeature).getValue()).mergeObservation();
                    LOGGER.debug("Observation processing requires {} ms", (System.currentTimeMillis() - start));
                    int count = 0;
                    for (OmObservation omObservation : mergeObservation) {
                        if (abstractFeature.isSetGmlID()) {
                            if (count == 0) {
                                omObservation.setGmlId(abstractFeature.getGmlId());
                            } else {
                                omObservation.setGmlId(abstractFeature.getGmlId() + "_" + count);
                            }
                            count++;
                        }
                        omObservation.setResultTime(resultTime);
                        String xmlTextObservation = prepareObservation(omObservation, getEncoder(abstractFeature, encodingValues.getAdditionalValues()),
                                                                                  encodingValues);
                        // stop the timer task
                        stopTimer();
                        writeMember(xmlTextObservation);
                    }
                } catch (OwsExceptionReport ex) {
                    throw new EncodingException(ex);
                }
            } else {
                writeMember(abstractFeature, getEncoder(abstractFeature, encodingValues.getAdditionalValues()),
                        encodingValues);
            }
            LOGGER.debug("Writing member requires {} ms", (System.currentTimeMillis() - start));
        }
        indent--;
        end(GmlConstants.QN_FEATURE_COLLECTION_32);
    }

    private void addNamespaces() throws XMLStreamException {
        // W3C
        namespace(W3CConstants.NS_XLINK_PREFIX, W3CConstants.NS_XLINK);
        // xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        namespace("xsd", "http://www.w3.org/2001/XMLSchema");

        // OGC
        // xmlns:om="http://www.opengis.net/om/2.0"
        namespace(OmConstants.NS_OM_PREFIX, OmConstants.NS_OM_2);
        // xmlns:gml="http://www.opengis.net/gml/3.2
        namespace(GmlConstants.NS_GML_PREFIX, GmlConstants.NS_GML_32);
        // xmlns:swe="http://www.opengis.net/swe/2.0"
        namespace(SweConstants.NS_SWE_PREFIX, SweConstants.NS_SWE_20);
        // xmlns:sams="http://www.opengis.net/samplingSpatial/2.0"
        namespace(SfConstants.NS_SAMS_PREFIX, SfConstants.NS_SAMS);
        // xmlns:sam="http://www.opengis.net/sampling/2.0"
        namespace("sam", SfConstants.NS_SF);

        // ISO
        // xmlns:gmd="http://www.isotc211.org/2005/gmd"
        namespace(GmdConstants.NS_GMD_PREFIX, GmdConstants.NS_GMD);
        // xmlns:gco="http://www.isotc211.org/2005/gco"
        namespace(GcoConstants.NS_GCO_PREFIX, GcoConstants.NS_GCO);

        // AQD e-Reporting
        // xmlns:aqd="http://dd.eionet.europa.eu/schemaset/id2011850eu-1.0"
        namespace(AqdConstants.NS_AQD_PREFIX, AqdConstants.NS_AQD);
        // xmlns:am="http://inspire.ec.europa.eu/schemas/am/3.0"
        namespace(AqdConstants.NS_AM_PREFIX, AqdConstants.NS_AM);
        // xmlns:base="http://inspire.ec.europa.eu/schemas/base/3.3"
        namespace(AqdConstants.NS_BASE_PREFIX, AqdConstants.NS_BASE);
        // xmlns:base2="http://inspire.ec.europa.eu/schemas/base2/1.0"
        namespace(AqdConstants.NS_BASE2_PREFIX, AqdConstants.NS_BASE2);
        // xmlns:ompr="http://inspire.ec.europa.eu/schemas/ompr/2.0"
        namespace(AqdConstants.NS_OMPR_PREFIX, AqdConstants.NS_OMPR);
        // xmlns:ef="http://inspire.ec.europa.eu/schemas/ef/3.0"
        namespace(AqdConstants.NS_EF_PREFIX, AqdConstants.NS_EF);
        // xmlns:gn="urn:x-inspire:specification:gmlas:GeographicalNames:3.0"
        namespace(AqdConstants.NS_GN_PREFIX, AqdConstants.NS_GN);
        // xmlns:ad="urn:x-inspire:specification:gmlas:Addresses:3.0"
        namespace(AqdConstants.NS_AD_PREFIX, AqdConstants.NS_AD);
    }

    private void addSchemaLocations() throws XMLStreamException {
        Set<SchemaLocation> schemaLocations = Sets.newHashSet();
        schemaLocations.add(AqdConstants.NS_AQD_SCHEMA_LOCATION);
        schemaLocations.add(GmlConstants.GML_32_SCHEMAL_LOCATION);
        schemaLocations.add(OmConstants.OM_20_SCHEMA_LOCATION);
        schemaLocations.add(SweConstants.SWE_20_SCHEMA_LOCATION);
        schemaLocation(schemaLocations);

    }

    private String addGmlId(String gmlId) throws XMLStreamException {
        attr(GmlConstants.QN_ID_32, gmlId);
        return gmlId;
    }

    private String prepareObservation(OmObservation omObservation, Encoder<XmlObject, AbstractFeature> encoder,
            EncodingValues encodingValues) throws EncodingException, XMLStreamException {

        String xmlText = (encoder.encode(omObservation, encodingValues.getAdditionalValues())).xmlText(XmlOptionsHelper
                .getInstance().getXmlOptions());
        // TODO check for better solutions
        xmlText = xmlText.replace("ns:ReferenceType", "gml:ReferenceType");
        xmlText = xmlText.replace(":ns=\"http://www.opengis.net/gml/3.2\"", ":gml=\"http://www.opengis.net/gml/3.2\"");
        xmlText = xmlText.replace("ns:DataArrayPropertyType", "swe:DataArrayPropertyType");
        xmlText = xmlText.replace(":ns=\"http://www.opengis.net/swe/2.0\"", ":swe=\"http://www.opengis.net/swe/2.0\"");
        xmlText = xmlText.replace("<ns:", "<swe:");
        xmlText = xmlText.replace("</ns:", "</swe:");
        return xmlText;
    }

    private void writeMember(AbstractFeature abstractFeature, Encoder<XmlObject, AbstractFeature> encoder,
            EncodingValues encodingValues) throws XMLStreamException, EncodingException {
        // chars("");
        // if (encoder instanceof StreamingEncoder<?, ?>) {
        // ((StreamingEncoder<XmlObject, AbstractFeature>)
        // encoder).encode(abstractFeature, getOutputStream(),
        // encodingValues.setAsDocument(true).setEmbedded(true).setIndent(indent));
        // } else {
        writeMember((encoder.encode(abstractFeature, encodingValues.getAdditionalValues())).xmlText(XmlOptionsHelper
                .getInstance().getXmlOptions()));
        // }
    }

    private void writeMember(String memberContent) throws XMLStreamException, EncodingException {
        start(GmlConstants.QN_FEATURE_MEMBER_32);
        rawText(memberContent);
        indent--;
        end(GmlConstants.QN_FEATURE_MEMBER_32);
        indent++;
    }

    private Encoder<XmlObject, AbstractFeature> getEncoder(AbstractFeature feature,
            Map<HelperValues, String> additionalValues) throws EncodingException {
        if (feature instanceof AbstractFeature && feature.isSetDefaultElementEncoding()) {
            return CodingHelper.getEncoder(feature.getDefaultElementEncoding(), feature);
        } else if (feature instanceof AbstractFeature && additionalValues.containsKey(HelperValues.ENCODE_NAMESPACE)) {
            return CodingHelper.getEncoder(additionalValues.get(HelperValues.ENCODE_NAMESPACE), feature);
        }
        return null;
    }

    /**
     * Initializ a new {@link WriteTimerTask}
     */
    private void initTimer() {
        timerTask = new WriteTimerTask();
    }

    /**
     * Schedule the {@link WriteTimerTask}
     */
    private void startTimer() {
        if (timerTask == null) {
            initTimer();
        }
        timer.schedule(timerTask, TIMER_PERIOD, TIMER_PERIOD);
        LOGGER.debug("Timer started!");
    }

    /**
     * Cancel the {@link WriteTimerTask} and set to <code>null</code>
     */
    private void stopTimer() {
        if (this.timerTask != null) {
            this.timerTask.cancel();
            this.timerTask = null;
            LOGGER.debug("Timer task {} canceled", WriteTimerTask.class.getSimpleName());
        }
    }

    /**
     * Cleanup the {@link Timer} and {@link TimerTask} to avoid conncetion
     * timeout after 1000 ms Stops the {@link WriteTimerTask}, Cancel
     * {@link Timer} and set to <code>null</code>.
     */
    private void cleanup() {
        stopTimer();
        timerTask = null;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * {@link TimerTask} to write blank strings to the {@link OutputStream} to
     * avoid conncetion timeout after 1000 ms
     *
     * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
     * @since 4.3.0
     *
     */
    private class WriteTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                chars(Constants.BLANK_STRING);
                flush();
            } catch (XMLStreamException xmlse) {
                cleanup();
                LOGGER.error("Error while writing empty string by timer task!", xmlse);
            }
        }
    }

}
