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
package org.n52.svalbard.encode.uvf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.schetland.uvf.UVFConstants;
import org.n52.schetland.uvf.UVFConstants.LineEnding;
import org.n52.sos.config.annotation.Configurable;
import org.n52.sos.config.annotation.Setting;
import org.n52.sos.convert.UVFRequestModifier;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.OperationEncoderKey;
import org.n52.sos.encode.streaming.StreamingDataEncoder;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.concrete.DateTimeFormatException;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeType;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.AbstractPhenomenon;
import org.n52.sos.ogc.om.MultiObservationValues;
import org.n52.sos.ogc.om.ObservationMergeIndicator;
import org.n52.sos.ogc.om.ObservationMerger;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.StreamingValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.MultiValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.series.wml.MeasurementTimeseriesMetadata;
import org.n52.sos.ogc.series.wml.WaterMLConstants.InterpolationType;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.sos.response.BinaryAttachmentResponse;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.uvf.UVFSettings;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

/**
 * The UVFEncoder implements the so called <b>U</b>niversal <b>V</b>ariable <b>F</b>ormat.
 * 
 * The definitions found on the internet are the following:
 * 
 * <ul>
 *  <li>
 *      <a href="http://www.aquaplan.de/public_papers/imex/sectionUVF.html">Aquaplan Specification of UVF</a> 
 *      (<a href="https://web.archive.org/web/20080220032227/http://www.aquaplan.de/public_papers/imex/sectionUVF.html">
 *      archived Version</a>)
 *  </li>
 *  <li>
 *      <a href="http://wiki.bluemodel.org/index.php/UVF-Format">Bluemodel.org updated version</a> 
 *      (<a href="https://web.archive.org/web/20080220032227/http://wiki.bluemodel.org/index.php/UVF-Format">archived 
 *      version</a>)
 * </li>
 * </ul>
 * 
 * It is used for exchanging timeseries and looks like the following:
 * <pre> 0: $sb Index-Einheit: 
 1: $ib Funktion-Interpretation: Summenlinie
 2: $sb Mess-Groesse: Niederschlag
 3: $sb Mess-Einheit: mm
 4: $sb Mess-Stellennummer: 5242
 5: *Z
 6: Niederschlag: mm             1900 2000
 7: 5242              1231240   1413414     52.52
 8: 73110107301002100636Zeit    
 9: 7311010730         0
10: 7311050224         0
11: 7311050240 .30333331
12: 7311050255        .5
13: 7311050414        .5
14: 7311050415 -777     
15: 7311050419 -777     
16: 7311050420 .80000001</pre>
 * The Lines 0 to 8 specify the header of the dataset. Hereby, the lines 0 to 4 provide optional metadata. The lines 5 
 * to 8 are mandatory and are referenced in the specification by "Zeile 1,2,3,4".
 * Line 14 and 15 show the handling of gaps or no data values. They are encoded with <code>-777</code>. Gaps are defined
 * by two values: one for the start and one for the end of the gap.
 * The following assumptions/constraints are implemented:
 * <ul>
 * <li>
 *      Only ONE timeseries will be encoded. Hence, the {@link UVFRequestModifier} ensures, that each request for the 
 *      UVF contains ONE observed property, ONE feature of interest, and ONE procedure.
 * </li>
 * <li>
 *      Only observations of type Measurement and Count are supported.
 * </li>
 * <li>
 *      The encoder does not check for gaps and encodes only start and end. This MUST be done before inserting the data
 *      in the SOS database.
 * </li>
 * <li>
 *      Identifiers are shortened via {@link String#substring(int, int)} to the length of 
 *      {@link UVFConstants#MAX_IDENTIFIER_LENGTH} (15) starting from the end of the given identifier.
 * </li>
 * <li>
 *      Values (e.g. measurements and coordinates) are shortened via {@link String#substring(int, int)} to the length of
 *      {@link UVFConstants#MAX_VALUE_LENGTH} (10) starting form the beginning of the String representation of the 
 *      value. No rounding is performed.
 * </li>
 * <li>
 *      The UVF requires that all coordinates are in Gauß Krüger, hence a coordinate transformation is performed
 *      before the response is encoded. This requires that the EPSG code of the best matching GK band is given. When
 *      not present in the request, a default value is used. This value can be specified using the admin WebUI of the
 *      SOS. Currently the following EPSG codes are allowed (see {@link UVFConstants#ALLOWED_CRS}):
 *      <ul>
 *      <li>31466</li>
 *      <li>31467</li>
 *      <li>31468</li>
 *      <li>31469</li>
 *      </ul>
 * </ul>
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
@Configurable
public class UVFEncoder implements ObservationEncoder<BinaryAttachmentResponse, Object>, StreamingDataEncoder {

    private final Logger LOGGER = LoggerFactory.getLogger(UVFEncoder.class);
    private final Set<String> MEDIA_TYPES = Sets.newHashSet(
            UVFConstants.CONTENT_TYPE_UVF.toString(),
            UVFConstants.CONTENT_TYPE_UVF_MAC.toString(),
            UVFConstants.CONTENT_TYPE_UVF_UNIX.toString(),
            UVFConstants.CONTENT_TYPE_UVF_WINDOWS.toString());

    private final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS = Collections.singletonMap(
            SosConstants.SOS, (Map<String, Set<String>>) new ImmutableMap.Builder<String, Set<String>>()
            .put(Sos1Constants.SERVICEVERSION, MEDIA_TYPES)
            .put(Sos2Constants.SERVICEVERSION, MEDIA_TYPES)
            .build());

    private final Set<EncoderKey> ENCODER_KEYS = createEncoderKeys();
            
    
    private final Set<String> CONFORMANCE_CLASSES = ImmutableSet
            .of("http://www.opengis.net/spec/OMXML/2.0/conf/measurement");
    

    private final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.ObservationType, Collections.singleton(OmConstants.OBS_TYPE_MEASUREMENT));
    
    private ObservationMerger merger = new ObservationMerger();
    private DateTimeZone timeZone = null;
    private LineEnding lineEnding = LineEnding.Unix;
    
    public UVFEncoder() {
        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
                Joiner.on(", ").join(ENCODER_KEYS));
    }

    @Override
    public Set<EncoderKey> getEncoderKeyType() {
        return ENCODER_KEYS;
    }

    @Override
    public BinaryAttachmentResponse encode(Object element)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        return encode(element, new EnumMap<HelperValues, String>(HelperValues.class));
    }

    @Override
    public BinaryAttachmentResponse encode(Object objectToEncode, Map<HelperValues, String> additionalValues)
            throws OwsExceptionReport, UnsupportedEncoderInputException {
        if (objectToEncode instanceof AbstractObservationResponse) {
            AbstractObservationResponse aor = (AbstractObservationResponse) objectToEncode;
            if (!aor.getObservationCollection().isEmpty()) {
                return encodeGetObsResponse(aor);
            } else {
                return createEmptyFile();
            }
        }
        throw new UnsupportedEncoderInputException(this, objectToEncode);

    }
    
    @Setting(UVFSettings.UVF_TIME_ZONE_SETTING_KEY)
    public void setTimeZone(String timeZone) {
        if (!Strings.isNullOrEmpty(timeZone)) {
            this.timeZone  = DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone.trim()));
        } else {
            this.timeZone = null;
        }
    }
    
    @Setting(UVFSettings.UVF_LINE_ENDING_KEY)
    public void setLineEnding(String lineEnding) {
        if (!Strings.isNullOrEmpty(lineEnding)) {
            this.lineEnding  = UVFConstants.LineEnding.valueOf(lineEnding.trim());
        } else {
            this.lineEnding = UVFConstants.LineEnding.Unix;
        }
    }

    private Set<EncoderKey> createEncoderKeys() {
        Set<EncoderKey> keys = Sets.newHashSet();
        for (String s : MEDIA_TYPES) {
            MediaType mediaType = MediaType.parse(s);
            keys.add((EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                    SosConstants.Operations.GetObservation, mediaType));
            keys.add((EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                    SosConstants.Operations.GetObservation, mediaType));
            keys.add((EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                    SosConstants.Operations.GetObservationById, mediaType));
            keys.add((EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                    SosConstants.Operations.GetObservationById, mediaType));
        }
        return keys;
    }

    private BinaryAttachmentResponse createEmptyFile() {
        return new BinaryAttachmentResponse(null, null, null);
    }

    private BinaryAttachmentResponse encodeGetObsResponse(AbstractObservationResponse aor) throws OwsExceptionReport {
        File tempDir = Files.createTempDir();
        BinaryAttachmentResponse response = null;
        try {
            File uvfFile = encodeToUvf(aor.getObservationCollection(), tempDir, getContentType(aor));
            response = new BinaryAttachmentResponse(Files.toByteArray(uvfFile), getContentType(),
                    String.format(uvfFile.getName(), makeDateSafe(new DateTime(DateTimeZone.UTC))));
        } catch (IOException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage("Couldn't create UVF file");
        } finally {
            tempDir.delete();
        }

        return response;

    }

    private File encodeToUvf(List<OmObservation> observationCollection, File tempDir, MediaType contentType) throws IOException, OwsExceptionReport {
        List<OmObservation> mergeObservations = merger.mergeObservations(observationCollection, ObservationMergeIndicator.defaultObservationMergerIndicator());
        String lineEnding = getLineEnding(contentType);
        String filename = getFilename(mergeObservations);
        File uvfFile = new File(tempDir, filename);
        FileWriter fw = new FileWriter(uvfFile);
        for (OmObservation o : mergeObservations) {
                if (o.isSetValue() && !checkForSingleObservationValue(o.getValue()) && !checkForMultiObservationValue(o.getValue()) && !checkForStreamingValue(o.getValue())) {
                    String errorMessage = String.format(
                            "The resulting values are not of numeric type which is only supported by this encoder '%s'.",
                            this.getClass().getName());
                    LOGGER.error(errorMessage);
                    throw new NoApplicableCodeException().withMessage(errorMessage);
                }
            
                /*
                 * HEADER: Metadata
                 */
                writeFunktionInterpretation(fw, o, lineEnding);
                writeIndex(fw, lineEnding);
                writeMessGroesse(fw, o, lineEnding);
                writeMessEinheit(fw, o, lineEnding);
                writeMessStellennummer(fw, o, lineEnding);
                writeMessStellenname(fw, o, lineEnding);
                /*
                 * HEADER: Lines 1 - 4
                 */
                writeLine1(fw, lineEnding);
                TimePeriod temporalBBox = getTemporalBBoxFromObservations(observationCollection);
                writeLine2(fw, o, temporalBBox, lineEnding);
                writeLine3(fw, o, lineEnding);
                writeLine4(fw, temporalBBox, lineEnding);
                /*
                 * Observation Data
                 */
                writeObservationValue(fw, o, lineEnding);
        }
        return uvfFile;
    }
    
    private boolean checkForStreamingValue(ObservationValue<?> value) {
        return value instanceof StreamingValue;
    }

    private boolean checkForSingleObservationValue(ObservationValue<?> value) {
        return value instanceof SingleObservationValue<?> && value.getValue().isSetValue()
                && (value.getValue() instanceof CountValue || value.getValue() instanceof QuantityValue);
    }

    private boolean checkForMultiObservationValue(ObservationValue<?> value) {
        return value instanceof MultiObservationValues<?> && value.getValue().isSetValue()
                && value.getValue() instanceof TVPValue && ((TVPValue)value.getValue()).isSetValue()
                && (((TVPValue)value.getValue()).getValue().get(0).getValue() instanceof CountValue 
                        || ((TVPValue)value.getValue()).getValue().get(0).getValue() instanceof QuantityValue);
    }

    private void writeFunktionInterpretation(FileWriter fw, OmObservation o, String lineEnding) throws IOException {
        String function = getFunction(o);
        if (!Strings.isNullOrEmpty(function)) {
            writeToFile(fw, String.format("$ib Funktion-Interpretation: %s", function), lineEnding);
        }
    }

    private void writeIndex(FileWriter fw, String lineEnding) throws IOException {
        writeToFile(fw, "$sb Index-Einheit: *** Zeit ***", lineEnding);
    }

    private void writeMessGroesse(FileWriter fw, OmObservation o, String lineEnding) throws IOException {
        String observablePropertyIdentifier = o.getObservationConstellation().getObservablePropertyIdentifier();
        observablePropertyIdentifier = ensureIdentifierLength(observablePropertyIdentifier,
                UVFConstants.MAX_IDENTIFIER_LENGTH);
        writeToFile(fw, String.format("$sb Mess-Groesse: %s", observablePropertyIdentifier), lineEnding);
    }

    private void writeMessEinheit(FileWriter fw, OmObservation o, String lineEnding) throws IOException {
        // $sb Mess-Einheit: m3/s
        // Unit (optional)
        String unit = getUnit(o);
        if (unit != null  && !unit.isEmpty()) {
            unit = ensureIdentifierLength(unit, UVFConstants.MAX_IDENTIFIER_LENGTH);
            writeToFile(fw, String.format("$sb Mess-Einheit: %s", unit), lineEnding);
        }
    }

    private void writeMessStellennummer(FileWriter fw, OmObservation o, String lineEnding) throws IOException {
        String featureOfInterestIdentifier = o.getObservationConstellation().getFeatureOfInterestIdentifier();
        if (featureOfInterestIdentifier != null && !featureOfInterestIdentifier.isEmpty()) {
            featureOfInterestIdentifier = ensureIdentifierLength(featureOfInterestIdentifier,
                UVFConstants.MAX_IDENTIFIER_LENGTH);
        }
        writeToFile(fw, String.format("$sb Mess-Stellennummer: %s", featureOfInterestIdentifier), lineEnding);
    }

    private void writeMessStellenname(FileWriter fw, OmObservation o, String lineEnding) throws IOException {
        if (o.getObservationConstellation().getFeatureOfInterest().isSetName()) {
            final CodeType firstName = o.getObservationConstellation().getFeatureOfInterest().getFirstName();
            String name = ensureIdentifierLength(firstName.isSetValue()?firstName.getValue():"", UVFConstants.MAX_IDENTIFIER_LENGTH);
            writeToFile(fw, String.format("$sb Mess-Stellenname: %s",
                    name), lineEnding);
        }
    }

    private void writeLine1(FileWriter fw, String lineEnding) throws IOException {
        writeToFile(fw, "*Z", lineEnding);
    }

    private void writeLine2(FileWriter fw, OmObservation o, TimePeriod centuries, String lineEnding) throws IOException {
        // 2.Zeile ABFLUSS m3/s 1900 1900
        StringBuilder sb = new StringBuilder(39);
        // Identifier
        AbstractPhenomenon observableProperty = o.getObservationConstellation().getObservableProperty();
        String observablePropertyIdentifier = observableProperty.getIdentifier();
        if (observablePropertyIdentifier != null && !observablePropertyIdentifier.isEmpty()) {
            observablePropertyIdentifier = ensureIdentifierLength(observablePropertyIdentifier,
                    UVFConstants.MAX_IDENTIFIER_LENGTH);
        }
        sb.append(observablePropertyIdentifier);
        fillWithSpaces(sb, UVFConstants.MAX_IDENTIFIER_LENGTH);
        // Unit (optional)
        String unit = getUnit(o);
        if (unit != null && !unit.isEmpty()) {
            unit = ensureIdentifierLength(unit,
                UVFConstants.MAX_IDENTIFIER_LENGTH);
            sb.append(" ");
            sb.append(unit);
        }
        fillWithSpaces(sb, 30);
        // Centuries
        sb.append(centuries.getStart().getCenturyOfEra() + "00 " + centuries.getEnd().getCenturyOfEra() + "00");
        writeToFile(fw, sb.toString(), lineEnding);
    }

    private void writeLine3(FileWriter fw, OmObservation o, String lineEnding) throws IOException {
        // 3.Zeile 88888 0 0 0.000
        StringBuilder sb = new StringBuilder(45);
        if( o.getObservationConstellation().isSetIdentifier()) {
            sb.append(ensureIdentifierLength(o.getObservationConstellation().getIdentifier(), UVFConstants.MAX_IDENTIFIER_LENGTH));
        } else if (o.isSetIdentifier()) {
            sb.append(ensureIdentifierLength(o.getIdentifier(), UVFConstants.MAX_IDENTIFIER_LENGTH));
        } else {
            if (!o.isSetObservationID()) {
                o.setObservationID(JavaHelper.generateID(o.toString()));
            }
            sb.append(ensureIdentifierLength(o.getObservationID(), UVFConstants.MAX_IDENTIFIER_LENGTH));
        }
        fillWithSpaces(sb, UVFConstants.MAX_IDENTIFIER_LENGTH);
        AbstractFeature f = o.getObservationConstellation().getFeatureOfInterest();
        if (o.getObservationConstellation().getFeatureOfInterest() instanceof AbstractSamplingFeature
                && ((AbstractSamplingFeature) o.getObservationConstellation().getFeatureOfInterest())
                        .isSetGeometry()) {
            AbstractSamplingFeature sf = (AbstractSamplingFeature)f;
            // Rechtswert
            String xString = sf.isSetGeometry() ? Double.toString(sf.getGeometry().getCoordinate().y) : "";
            xString = ensureValueLength(xString, 10);
            sb.append(xString);
            fillWithSpaces(sb, 25);
            // Hochwert
            String yString = sf.isSetGeometry() ? Double.toString(sf.getGeometry().getCoordinate().x) : "";
            yString = ensureValueLength(yString, 10);
            sb.append(yString);
            fillWithSpaces(sb, 35);
            if (sf.isSetGeometry() && !Double.isNaN(sf.getGeometry().getCoordinate().z)) {
                String zString = Double.toString(sf.getGeometry().getCoordinate().z);
                zString = ensureValueLength(zString, 10);
                sb.append(zString);
            }
        }
        fillWithSpaces(sb, 45);
        writeToFile(fw, sb.toString(), lineEnding);
    }

    private void writeLine4(FileWriter fw, TimePeriod temporalBBox, String lineEnding) throws IOException, DateTimeFormatException {
        StringBuilder sb = new StringBuilder(28);
        sb.append(DateTimeHelper.formatDateTime2FormattedString(checkTimeZone(temporalBBox.getStart()), UVFConstants.TIME_FORMAT));
        sb.append(DateTimeHelper.formatDateTime2FormattedString(checkTimeZone(temporalBBox.getEnd()), UVFConstants.TIME_FORMAT));
        fillWithSpaces(sb, 20);
        sb.append("Zeit");
        fillWithSpaces(sb, 28);
        writeToFile(fw, sb.toString(), lineEnding);
    }

    private void writeObservationValue(FileWriter fw, OmObservation omObservation, String lineEnding) throws IOException,
            OwsExceptionReport {
        // yymmddhhmmvvvvvvvvvv
        // ^ date with ten chars
        //           ^ observed/measured value with 10 chars
        if (omObservation.getValue() instanceof SingleObservationValue<?>) {
            writeSingleObservationValue(fw, omObservation.getPhenomenonTime(),
                    ((SingleObservationValue<?>)omObservation.getValue()).getValue(), lineEnding);
        } else if (omObservation.getValue() instanceof MultiObservationValues) {
            writeMultiObservationValues(fw, omObservation, lineEnding);
        } else if (omObservation.getValue() instanceof StreamingValue) {
            writeStreamingValueValues(fw, omObservation, lineEnding);
        } else {
            throw new NoApplicableCodeException().withMessage("Support for '%s' not yet implemented.",
                    omObservation.getValue().getClass().getName());
        }
    }

    private String getFunction(OmObservation o) {
        if (o.getObservationConstellation().isSetDefaultPointMetadata() || o.getObservationConstellation().isSetMetadata()) {
            if (o.getObservationConstellation().isSetMetadata()
                    && o.getObservationConstellation().getMetadata().isSetTimeseriesMetadata()
                    && o.getObservationConstellation().getMetadata().getTimeseriesmetadata() instanceof MeasurementTimeseriesMetadata
                    && ((MeasurementTimeseriesMetadata)o.getObservationConstellation().getMetadata().getTimeseriesmetadata()).isCumulative()) {
                return UVFConstants.FunktionInterpretation.Summenlinie.name();
            }
            if (o.getObservationConstellation().isSetDefaultPointMetadata()
                    && o.getObservationConstellation().getDefaultPointMetadata().isSetDefaultTVPMeasurementMetadata()
                    && o.getObservationConstellation().getDefaultPointMetadata().getDefaultTVPMeasurementMetadata().isSetInterpolationType()) {
                return getFunctionFor(o.getObservationConstellation().getDefaultPointMetadata().getDefaultTVPMeasurementMetadata().getInterpolationtype());
                
            }
        } else if (o.isSetValue() && (o.getValue().isSetMetadata() || o.getValue().isSetDefaultPointMetadata())) {
            if (o.getValue().isSetMetadata()
                    && o.getValue().getMetadata().isSetTimeseriesMetadata()
                    && o.getValue().getMetadata().getTimeseriesmetadata() instanceof MeasurementTimeseriesMetadata
                    && ((MeasurementTimeseriesMetadata)o.getValue().getMetadata().getTimeseriesmetadata()).isCumulative()) {
                return UVFConstants.FunktionInterpretation.Summenlinie.name();
            }
            if (o.getValue().isSetDefaultPointMetadata()
                    && o.getValue().getDefaultPointMetadata().isSetDefaultTVPMeasurementMetadata()
                    && o.getValue().getDefaultPointMetadata().getDefaultTVPMeasurementMetadata().isSetInterpolationType()) {
                return getFunctionFor(o.getValue().getDefaultPointMetadata().getDefaultTVPMeasurementMetadata().getInterpolationtype());
            }
        }
        return null;
    }

    private String getFunctionFor(InterpolationType interpolationtype) {
        switch (interpolationtype) {
        case Continuous:
            return UVFConstants.FunktionInterpretation.Linie.name();
        case AveragePrec:
            return UVFConstants.FunktionInterpretation.Blockanfang.name();
        case MaxPrec:
            return UVFConstants.FunktionInterpretation.Blockanfang.name();
        case MinPrec:
            return UVFConstants.FunktionInterpretation.Blockanfang.name();
        case TotalPrec:
            return UVFConstants.FunktionInterpretation.Blockanfang.name();
        case ConstPrec:
            return UVFConstants.FunktionInterpretation.Blockanfang.name();
        case AverageSucc:
            return UVFConstants.FunktionInterpretation.Blockende.name();
        case MaxSucc:
            return UVFConstants.FunktionInterpretation.Blockende.name();
        case MinSucc:
            return UVFConstants.FunktionInterpretation.Blockende.name();
        case TotalSucc:
            return UVFConstants.FunktionInterpretation.Blockende.name();
        case ConstSucc:
            return UVFConstants.FunktionInterpretation.Blockende.name();
        case Discontinuous:
            return null;
        case InstantTotal:
            return null;
        case Statistical:
            return null;
        default:
            return null;
        }
    }

    private String getUnit(OmObservation o) {
        if (o.getObservationConstellation().getObservableProperty() instanceof OmObservableProperty
                && ((OmObservableProperty)o.getObservationConstellation().getObservableProperty()).isSetUnit()) {
            return ((OmObservableProperty)o.getObservationConstellation().getObservableProperty()).getUnit();
        } else if (o.getValue().isSetValue() && o.getValue().getValue().isSetUnit()) {
                return o.getValue().getValue().getUnit();
        }
        return null;
    }

    /* ***********************************************************************
     *
     *      Helper methods
     *
     * ***********************************************************************/

    private void writeSingleObservationValue(FileWriter fw, Time phenomenonTime, Value<?> value, String lineEnding) throws IOException,
            CodedException {
        StringBuilder sb = new StringBuilder(20);
        if (phenomenonTime instanceof TimeInstant) {
            sb.append(DateTimeHelper.formatDateTime2FormattedString(checkTimeZone(((TimeInstant)phenomenonTime).getValue()),
                    UVFConstants.TIME_FORMAT));
        } else {
            sb.append(DateTimeHelper.formatDateTime2FormattedString(checkTimeZone(((TimePeriod)phenomenonTime).getEnd()),
                    UVFConstants.TIME_FORMAT));
        }
        sb.append(encodeObservationValue(value));
        
        fillWithSpaces(sb, 20);
        writeToFile(fw, sb.toString(), lineEnding);
    }

    private void writeMultiObservationValues(FileWriter fw, OmObservation omObservation, String lineEnding)
            throws IOException, CodedException {
        MultiValue<?> values = ((MultiObservationValues<?>)omObservation.getValue()).getValue();
        /*
         * - SweDataArrayValue => not allowed, only CountObservation and Measurement
         * - TLVTValue => not supported, because UVF supports timeseries for one location only
         * - TVPValue
         */
        if (values.getValue() instanceof List<?> &&
                !((List<?>)values.getValue()).isEmpty()) {
            Object object = ((List<?>)values.getValue()).get(0);
            
           if (object instanceof TimeValuePair) {
                @SuppressWarnings("unchecked")
                List<TimeValuePair> valuesList = (List<TimeValuePair>) values.getValue();
                for (TimeValuePair timeValuePair : valuesList) {
                    writeSingleObservationValue(fw, timeValuePair.getTime(), timeValuePair.getValue(), lineEnding);
                }
           } else {
               throw new NoApplicableCodeException().withMessage("Support for '%s' not yet implemented.",
                       object.getClass().getName());
           }
        }
    }

    private void writeStreamingValueValues(FileWriter fw, OmObservation omObservation, String lineEnding) throws CodedException, OwsExceptionReport, IOException {
        StreamingValue observationValue = (StreamingValue) omObservation.getValue();
        while (observationValue.hasNextValue()) {
            TimeValuePair timeValuePair = observationValue.nextValue();
            if (timeValuePair != null) {
                writeSingleObservationValue(fw, timeValuePair.getTime(), timeValuePair.getValue(), lineEnding);
            }
        }
    }

    private String encodeObservationValue(Value<?> value) throws CodedException {
        if (value == null || (value != null && !value.isSetValue())) {
            return UVFConstants.NO_DATA_STRING;
        }
        if (!(value instanceof SweQuantity) && !(value instanceof SweCount)) {
            String errorMessage = String.format("SweType '%s' not supported. Only '%s' and '%s'.",
                    value.getClass().getName(),
                    SweQuantity.class.getName(),
                    SweCount.class.getName());
            LOGGER.error(errorMessage);
            throw new NoApplicableCodeException().withMessage(errorMessage);
        }
        String encodedValue = JavaHelper.asString(value.getValue());
        if (encodedValue.length()> UVFConstants.MAX_VALUE_LENGTH) {
            encodedValue = encodedValue.substring(0, UVFConstants.MAX_VALUE_LENGTH);
        }
        return encodedValue;
    }

    private TimePeriod getTemporalBBoxFromObservations(List<OmObservation> observationCollection) throws CodedException {
        DateTime start = null;
        DateTime end = null;
        for (OmObservation observation : observationCollection) {
            Time phenomenonTime = observation.getPhenomenonTime();
            if (phenomenonTime instanceof TimeInstant) {
                final DateTime time = ((TimeInstant) phenomenonTime).getTimePosition().getTime();
                if (start == null || time.isBefore(start)) {
                    start = time;
                }
                if (end == null || time.isAfter(end)) {
                    end = time;
                }
            } else {
                final DateTime periodStart = ((TimePeriod) phenomenonTime).getStart();
                if (start == null || periodStart.isBefore(start)) {
                    start = periodStart;
                }
                final DateTime periodEnd = ((TimePeriod) phenomenonTime).getEnd();
                if (end == null || periodEnd.isAfter(end)) {
                    end = periodEnd;
                }
            }
        }
        if (start != null && end != null) {
            return new TimePeriod(start, end);
        } else {
            final String message = "Could not extract centuries from observation collection";
            LOGGER.error(message);
            throw new NoApplicableCodeException().withMessage(message);
        }
    }

    private String ensureValueLength(String valueString, int maxLength) {
        if (valueString.length() > maxLength) {
            valueString = valueString.substring(0, maxLength);
        }
        return valueString;
    }

    private String ensureIdentifierLength(String identifier, int maxLength) {
        if (identifier.contains("/")) {
            identifier = identifier.substring(identifier.lastIndexOf("/") + 1);
        }
        if (identifier.length() > maxLength) {
            int endIndex = identifier.length();
            int beginIndex = endIndex - maxLength;
            identifier = identifier.substring(beginIndex, endIndex);
        }
        return identifier;
    }

    private void fillWithSpaces(StringBuilder sb, int i) {
        while (sb.length() < i) {
            sb.append(" ");
        }
        sb.trimToSize();
    }

    private MediaType getContentType(AbstractObservationResponse aor) {
        if (aor.isSetResponseFormat()) {
            try {
                return MediaType.parse(aor.getResponseFormat());
            } catch (IllegalArgumentException e) {
                LOGGER.debug("ResponseFormat '{}' is not a mediy type!", aor.getResponseFormat());
            }
        }
        return aor.getContentType();
    }

    private void writeToFile(FileWriter fw, String string, String lineEnding) throws IOException {
        fw.write(string + lineEnding);
        fw.flush();
    }

    private String getLineEnding(MediaType contentType) {
        if (contentType != null) {
            if (contentType.equals(UVFConstants.CONTENT_TYPE_UVF_UNIX)) {
                return UVFConstants.LINE_ENDING_UNIX;
            } else if (contentType.equals(UVFConstants.CONTENT_TYPE_UVF_WINDOWS)) {
                return UVFConstants.LINE_ENDING_WINDOWS;
            } else if (contentType.equals(UVFConstants.CONTENT_TYPE_UVF_MAC)) {
                return UVFConstants.LINE_ENDING_MAC;
            }
        }
        return getDefaultLineEnding();
    }

    private boolean checkForParameter(MediaType contentType, MediaType requestedContentType) {
        if (requestedContentType.hasParameters()) {
            if (requestedContentType.getParameter("lineEnding") != null) {
                for (String value : requestedContentType.getParameter("lineEnding")) {
                    if (contentType.getParameter("lineEnding").contains(value)) {
                        return true;
                    }
                }
            } else if (requestedContentType.getParameter("lineending") != null) {
                for (String value : requestedContentType.getParameter("lineending")) {
                    if (contentType.getParameter("lineEnding").contains(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getDefaultLineEnding() {
        switch (lineEnding) {
        case Unix:
            return UVFConstants.LINE_ENDING_UNIX;
        case Windows:
            return UVFConstants.LINE_ENDING_WINDOWS;
        case Mac:
            return UVFConstants.LINE_ENDING_MAC;
        default:
            return UVFConstants.LINE_ENDING_UNIX;
        }
    }

    private String getFilename(List<OmObservation> observations) {
        Set<String> identifiers = new HashSet<>();
        for (OmObservation o : observations) {
            if (o.getObservationConstellation().isSetIdentifier()) {
                identifiers.add(o.getObservationConstellation().getIdentifier());
            }
        }
        StringBuffer pathBuffer = new StringBuffer();
        if (!identifiers.isEmpty()) {
            for (String identifier : identifiers) {
                pathBuffer.append(identifier).append("_");
            }
            pathBuffer.replace(pathBuffer.lastIndexOf("_"), pathBuffer.length(), "");
        } else {
            pathBuffer.append("_").append(Long.toString(java.lang.System.nanoTime()));
        }
        pathBuffer.append(".uvf");
        return pathBuffer.toString();
    }

    private String makeDateSafe(DateTime dt) {
        return dt.toString().replace(":", "");
    }
    
    private DateTime checkTimeZone(DateTime time) {
        if (timeZone != null) {
            return time.toDateTime(timeZone);
        }
        return time;
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }
    
    @Override
    public void addNamespacePrefixToMap(Map<String, String> nameSpacePrefixMap) {
     // NOOP, no need (we're not encoding xml)
    }

    @Override
    public MediaType getContentType() {
        return UVFConstants.CONTENT_TYPE_UVF;
    }

    @Override
    public Set<SchemaLocation> getSchemaLocations() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

    @Override
    public boolean isObservationAndMeasurmentV20Type() {
        return false;
    }

    @Override
    public boolean shouldObservationsWithSameXBeMerged() {
        return true;
    }

    @Override
    public boolean supportsResultStreamingForMergedValues() {
        return false;
    }

    @Override
    public Set<String> getSupportedResponseFormats(String service, String version) {
        if (SUPPORTED_RESPONSE_FORMATS.get(service) != null) {
            if (SUPPORTED_RESPONSE_FORMATS.get(service).get(version) != null) {
                return SUPPORTED_RESPONSE_FORMATS.get(service).get(version);
            }
        }
        return Collections.emptySet();
    }

    @Override
    public Map<String, Set<String>> getSupportedResponseFormatObservationTypes() {
        return null;
    }
    
}
