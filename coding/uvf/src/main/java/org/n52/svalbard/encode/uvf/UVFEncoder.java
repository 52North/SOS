package org.n52.svalbard.encode.uvf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.n52.schetland.uvf.UVFConstants;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.encode.ObservationEncoder;
import org.n52.sos.encode.OperationEncoderKey;
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
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.TimeLocationValueTriple;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.MultiValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.SweDataArrayValue;
import org.n52.sos.ogc.om.values.TLVTValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.SweAbstractDataComponent;
import org.n52.sos.ogc.swe.SweDataArray;
import org.n52.sos.ogc.swe.SweDataRecord;
import org.n52.sos.ogc.swe.SweField;
import org.n52.sos.ogc.swe.encoding.SweTextEncoding;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.sos.response.AbstractObservationResponse.GlobalGetObservationValues;
import org.n52.sos.response.BinaryAttachmentResponse;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

public class UVFEncoder implements ObservationEncoder<BinaryAttachmentResponse, Object> {
    
    private final Logger LOGGER = LoggerFactory.getLogger(UVFEncoder.class);
    private final Set<String> MEDIA_TYPES = Sets.newHashSet(UVFConstants.CONTENT_TYPE_UVF.toString());

    private final Map<String, Map<String, Set<String>>> SUPPORTED_RESPONSE_FORMATS = Collections.singletonMap(
            SosConstants.SOS, (Map<String, Set<String>>) new ImmutableMap.Builder<String, Set<String>>()
            .put(Sos1Constants.SERVICEVERSION, MEDIA_TYPES)
            .put(Sos2Constants.SERVICEVERSION, MEDIA_TYPES)
            .build());

    private final Set<EncoderKey> ENCODER_KEYS = Sets.newHashSet(
          (EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, UVFConstants.CONTENT_TYPE_UVF),
          (EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservation, UVFConstants.CONTENT_TYPE_UVF));
    
    private final Set<String> CONFORMANCE_CLASSES = ImmutableSet
            .of("http://www.opengis.net/spec/OMXML/2.0/conf/measurement");
    

    private final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.ObservationType, Collections.singleton(OmConstants.OBS_TYPE_MEASUREMENT));
    
    private GlobalGetObservationValues globalValues = null;
    
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
            if (aor.hasGlobalValues()) {
                globalValues = aor.getGlobalValues();
            }
            return encodeGetObsResponse(aor.getObservationCollection());
        }
        throw new UnsupportedEncoderInputException(this, objectToEncode);

    }

    private BinaryAttachmentResponse encodeGetObsResponse(List<OmObservation> observationCollection) throws CodedException {
        File tempDir = Files.createTempDir();
        BinaryAttachmentResponse response = null;
        try {
            File uvfFile = encodeToUvf(observationCollection, tempDir);
            response = new BinaryAttachmentResponse(Files.toByteArray(uvfFile), getContentType(),
                    String.format(uvfFile.getName(), makeDateSafe(new DateTime(DateTimeZone.UTC))));
        } catch (IOException e) {
            throw new NoApplicableCodeException().causedBy(e).withMessage("Couldn't create UVF file");
        } finally {
            tempDir.delete();
        }

        return response;

    }

    private File encodeToUvf(List<OmObservation> observationCollection, File tempDir) throws IOException, CodedException {
        String filename = getFilename(observationCollection);
        File uvfFile = new File(tempDir, filename);
        FileWriter fw = new FileWriter(uvfFile);
        OmObservation o = observationCollection.iterator().next();
        /*
         * HEADER: Metadata
         */
        writeFunktionInterpretation(fw);
        writeIndex(fw);
        writeMessGroesse(fw, o);
        writeMessEinheit(fw, o.getObservationConstellation().getObservableProperty());
        writeMessStellennummer(fw, o);
        writeMessStellenname(fw, o);
        /*
         * HEADER: Lines 1 - 4
         */
        writeLine1(fw);
        TimePeriod temporalBBox = null;
        if (globalValues != null) {
            temporalBBox = getTemporalBBoxFromGlobalValues();
        }
        if (temporalBBox == null){
            temporalBBox = getTemporalBBoxFromObservations(observationCollection);
        }
        writeLine2(fw, o.getObservationConstellation().getObservableProperty(), temporalBBox);
        writeLine3(fw, o.getObservationConstellation().getFeatureOfInterest());
        writeLine4(fw, temporalBBox);
        /*
         * Observation Data
         */
        for (OmObservation omObservation : observationCollection) {
            writeObservationValue(fw, omObservation);
        }
        return uvfFile;
    }

    private void writeFunktionInterpretation(FileWriter fw) throws IOException {
        writeToFile(fw, "$ib Funktion-Interpretation: Linie");
    }

    private void writeIndex(FileWriter fw) throws IOException {
        writeToFile(fw, "$sb Index-Einheit: *** Zeit ***");
    }

    private void writeMessGroesse(FileWriter fw, OmObservation o) throws IOException {
        String observablePropertyIdentifier = o.getObservationConstellation().getObservablePropertyIdentifier();
        observablePropertyIdentifier = ensureIdentifierLength(observablePropertyIdentifier,
                UVFConstants.MAX_IDENTIFIER_LENGTH);
        writeToFile(fw, String.format("$sb Mess-Groesse: %s", observablePropertyIdentifier));
    }

    private void writeMessEinheit(FileWriter fw, AbstractPhenomenon observableProperty) throws IOException {
        // $sb Mess-Einheit: m3/s
        // Unit (optional)
        String unit = "";
        if (observableProperty instanceof OmObservableProperty) {
            unit = ((OmObservableProperty)observableProperty).getUnit();
            unit = ensureIdentifierLength(unit,
                    UVFConstants.MAX_IDENTIFIER_LENGTH);
        }
        writeToFile(fw, String.format("$sb Mess-Einheit: %s", unit));
    }

    private void writeMessStellennummer(FileWriter fw, OmObservation o) throws IOException {
        String featureOfInterestIdentifier = o.getObservationConstellation().getFeatureOfInterestIdentifier();
        featureOfInterestIdentifier = ensureIdentifierLength(featureOfInterestIdentifier,
                UVFConstants.MAX_IDENTIFIER_LENGTH);
        writeToFile(fw, String.format("$sb Mess-Stellennummer: %s", featureOfInterestIdentifier));
    }

    private void writeMessStellenname(FileWriter fw, OmObservation o) throws IOException {
        if (o.getObservationConstellation().getFeatureOfInterest().isSetName()) {
            final CodeType firstName = o.getObservationConstellation().getFeatureOfInterest().getFirstName();
            String name = ensureIdentifierLength((firstName.isSetCodeSpace()?firstName.getCodeSpace():"") +
                    (firstName.isSetValue()?firstName.getValue():""), UVFConstants.MAX_IDENTIFIER_LENGTH);
            writeToFile(fw, String.format("$sb Mess-Stellenname: %s",
                    name));
        }
    }

    private void writeLine1(FileWriter fw) throws IOException {
        writeToFile(fw, "*Z");
    }

    private void writeLine2(FileWriter fw, AbstractPhenomenon observableProperty, TimePeriod centuries) throws IOException {
        // 2.Zeile ABFLUSS m3/s 1900 1900
        StringBuilder sb = new StringBuilder(39);
        // Identifier
        String observablePropertyIdentifier = observableProperty.getIdentifier();
        observablePropertyIdentifier = ensureIdentifierLength(observablePropertyIdentifier,
                UVFConstants.MAX_IDENTIFIER_LENGTH);
        sb.append(observablePropertyIdentifier);
        fillWithSpaces(sb, UVFConstants.MAX_IDENTIFIER_LENGTH);
        // Unit (optional)
        if (observableProperty instanceof OmObservableProperty) {
            String unit = ((OmObservableProperty)observableProperty).getUnit();
            unit = ensureIdentifierLength(unit,
                    UVFConstants.MAX_IDENTIFIER_LENGTH);
            sb.append(" ");
            sb.append(unit);
        }
        fillWithSpaces(sb, 30);
        // Centuries
        sb.append(centuries.getStart().getYear() + " " + centuries.getEnd().getYear());
        writeToFile(fw, sb.toString());
    }

    private void writeLine3(FileWriter fw, AbstractFeature f) throws IOException {
        // 3.Zeile 88888 0 0 0.000
        StringBuilder sb = new StringBuilder(45);
        String foiIdentifier = f.getIdentifier();
        foiIdentifier = ensureIdentifierLength(foiIdentifier, UVFConstants.MAX_IDENTIFIER_LENGTH);
        sb.append(foiIdentifier);
        fillWithSpaces(sb, UVFConstants.MAX_IDENTIFIER_LENGTH);
        if (f instanceof SamplingFeature) {
            SamplingFeature sf = (SamplingFeature)f;
            String xString = Double.toString(sf.getGeometry().getCoordinate().x);
            xString = ensureValueLength(xString, 10);
            sb.append(xString);
            fillWithSpaces(sb, 25);
            String yString = Double.toString(sf.getGeometry().getCoordinate().y);
            yString = ensureValueLength(yString, 10);
            sb.append(yString);
            fillWithSpaces(sb, 35);
            if (!Double.isNaN(sf.getGeometry().getCoordinate().z)) {
                String zString = Double.toString(sf.getGeometry().getCoordinate().z);
                zString = ensureValueLength(zString, 10);
                sb.append(zString);
            } else {
                sb.append("0.000");
            }
            fillWithSpaces(sb, 45);
        } else {
            sb.append("0");
            fillWithSpaces(sb, 25);
            sb.append("0");
            fillWithSpaces(sb, 35);
            sb.append("0.000");
            fillWithSpaces(sb, 45);
        }
        writeToFile(fw, sb.toString());
    }

    private void writeLine4(FileWriter fw, TimePeriod temporalBBox) throws IOException, DateTimeFormatException {
        StringBuilder sb = new StringBuilder(28);
        sb.append(DateTimeHelper.formatDateTime2FormattedString(temporalBBox.getStart(), UVFConstants.TIME_FORMAT));
        sb.append(DateTimeHelper.formatDateTime2FormattedString(temporalBBox.getEnd(), UVFConstants.TIME_FORMAT));
        fillWithSpaces(sb, 20);
        sb.append("Zeit");
        fillWithSpaces(sb, 28);
        writeToFile(fw, sb.toString());
    }

    private void writeObservationValue(FileWriter fw, OmObservation omObservation) throws IOException,
            CodedException {
        // TODO implement no data handling 5.Zeile 7008030730 -777
        // TODO implement normal data handling
        // yymmddhhmmvvvvvvvvvv
        // ^ date with ten chars
        //           ^ observed/measured value with 10 chars
        /*
         * Welche Fälle sind hier zu beachten?
         * 2. SingleObservationValue vs. MultiObservationValue vs. StreamingValue
         *    2.1 single observation
         *    2.2 multi observation value: schleife über values
         *    TODO streaming value mal deployed testen mit den Testdaten
         *    2.3 streaming observation value:
         *      ((StreamingValue<?>)omObservation.getValue()).hasNextValue()
         *      ((StreamingValue<?>)omObservation.getValue()).nextValue()
         * 3. Welche Arten von NoDataValue gibt es?
         *    --> 3.2 Null <--
         * 4. Wie kürzen wir die Werte? Substring vs. String.format()
         *    substring: werte von vorne schreiben; identifier von hinten
         */
        if (omObservation.getValue() instanceof SingleObservationValue<?>) {
            writeSingleObservationValue(fw, omObservation.getPhenomenonTime(),
                    ((SingleObservationValue<?>)omObservation.getValue()).getValue());
        } else if (omObservation.getValue() instanceof MultiObservationValues) {
            writeMultiObservationValues(fw, omObservation);
        }
    }

    /* ***********************************************************************
     *
     *      Helper methods
     *
     * ***********************************************************************/

    private void writeSingleObservationValue(FileWriter fw, Time phenomenonTime, Value<?> value) throws IOException,
            DateTimeFormatException {
        StringBuilder sb = new StringBuilder(20);
        if (phenomenonTime instanceof TimeInstant) {
            sb.append(DateTimeHelper.formatDateTime2FormattedString(((TimeInstant)phenomenonTime).getValue(),
                    UVFConstants.TIME_FORMAT));
        } else {
            sb.append(DateTimeHelper.formatDateTime2FormattedString(((TimePeriod)phenomenonTime).getEnd(),
                    UVFConstants.TIME_FORMAT));
        }
        sb.append(encodeObservationValue(value));
        
        fillWithSpaces(sb, 20);
        writeToFile(fw, sb.toString());
    }

    private void writeMultiObservationValues(FileWriter fw, OmObservation omObservation)
            throws IOException, CodedException {
        MultiValue<?> values = ((MultiObservationValues<?>)omObservation.getValue()).getValue();
        /*
         * - SweDataArrayValue
         * - TLVTValue => not supported, because UVF supports timeseries for one location only
         * - TVPValue
         */
        if (values.getValue() instanceof List<?> &&
                !((List<?>)values.getValue()).isEmpty()) {
            Object object = ((List<?>)values.getValue()).get(0);
            
            if (object instanceof TimeLocationValueTriple) {
                final String message = String.format("Encoding of Observations with values of type '%s' not supported.",
                        TLVTValue.class.getName());
                LOGGER.error(message);
                throw new NoApplicableCodeException().withMessage(message);
            } else if (object instanceof TimeValuePair) {
                @SuppressWarnings("unchecked")
                List<TimeValuePair> valuesList = (List<TimeValuePair>) values.getValue();
                for (TimeValuePair timeValuePair : valuesList) {
                    writeSingleObservationValue(fw, timeValuePair.getTime(), timeValuePair.getValue());
                }
            }
        } else if (values instanceof SweDataArrayValue) {
            SweDataArray sweDataArray = (SweDataArray) values.getValue();
            List<List<String>> blocks = sweDataArray.getValues();
            SweTextEncoding encoding = (SweTextEncoding) sweDataArray.getEncoding();
            SweDataRecord elementType = (SweDataRecord) sweDataArray.getElementType();
            int timestampIndex = elementType.getFieldIndexByIdentifier(OmConstants.PHENOMENON_TIME);
            final String valueFieldIdentifierOrName = omObservation.getObservationConstellation().getObservablePropertyIdentifier();
            int valueIndex = elementType.getFieldIndexByIdentifier(
                    valueFieldIdentifierOrName);
            for (List<String> block : blocks) {
                writeSingleObservationValue(fw,
                        // FIXME do we always have time instants here?
                        new TimeInstant(DateTimeHelper.parseIsoString2DateTime(block.get(timestampIndex))),
                        encodeSweValue(elementType.getFieldByIdentifier(valueFieldIdentifierOrName),
                                block.get(valueIndex),
                                encoding.getDecimalSeparator()));
            }
        }
    }

    private Value<?> encodeSweValue(SweField field, String value, String decimalSeparator) throws CodedException {
        SweAbstractDataComponent element = field.getElement();
        if (element instanceof SweQuantity) {
            value = value.replace(decimalSeparator, ".");
            return new QuantityValue(Double.parseDouble(value), ((SweQuantity) element).getUom());
        } else {
            final String message = String.format(
                    "Encoding of SweArrayObservations with values of type '%s' not supported.",
                    element.getClass().getName());
            LOGGER.error(message);
            throw new NoApplicableCodeException().withMessage(message);
        }
    }

    private String encodeObservationValue(Value<?> value) {
        Object val = value.getValue();
        String encodedValue = val.toString();
        if (encodedValue.length()> UVFConstants.MAX_VALUE_LENGTH) {
            encodedValue = encodedValue.substring(0, UVFConstants.MAX_VALUE_LENGTH);
        }
        return encodedValue;
    }

    private TimePeriod getTemporalBBoxFromGlobalValues() {
        if (globalValues.isSetPhenomenonTime()) {
            Time time = globalValues.getPhenomenonTime();
            if (time instanceof TimeInstant) {
                return new TimePeriod(time, time);
            } else {
                return (TimePeriod) time;
            }
        }
        return null;
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

    private void writeToFile(FileWriter fw, String string) throws IOException {
        fw.write(string + "\n");
        fw.flush();
    }

    private String getFilename(List<OmObservation> observationCollection) {
//        List<Time> times = Lists.newArrayList(sensorDataset.getTimes());
//        Collections.sort(times);
//        DateTime firstTime = getDateTime(times.get(0));
//        DateTime lastTime = getDateTime(times.get(times.size() - 1));
//
        StringBuffer pathBuffer = new StringBuffer();
//        pathBuffer.append(sensorDataset.getSensorIdentifier().replaceAll("http://", "").replaceAll("/", "_"));
//        pathBuffer.append("_" + sensorDataset.getFeatureType().name().toLowerCase());
//        pathBuffer.append("_" + makeDateSafe(firstTime));
//        // if (!(sensorDataset instanceof IStaticTimeDataset)) {
//        pathBuffer.append("_" + makeDateSafe(lastTime));
//        // }
        pathBuffer.append("_" + Long.toString(java.lang.System.nanoTime()) + ".uvf");
        return pathBuffer.toString();
    }

    private String makeDateSafe(DateTime dt) {
        return dt.toString().replace(":", "");
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
