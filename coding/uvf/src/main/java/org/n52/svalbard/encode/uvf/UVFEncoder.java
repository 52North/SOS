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
import org.n52.sos.convert.UVFRequestModifier;
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
import org.n52.sos.ogc.om.ObservationMergeIndicator;
import org.n52.sos.ogc.om.ObservationMerger;
import org.n52.sos.ogc.om.ObservationValue;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservableProperty;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.SingleObservationValue;
import org.n52.sos.ogc.om.TimeValuePair;
import org.n52.sos.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.om.values.CountValue;
import org.n52.sos.ogc.om.values.GeometryValue;
import org.n52.sos.ogc.om.values.MultiValue;
import org.n52.sos.ogc.om.values.QuantityValue;
import org.n52.sos.ogc.om.values.TVPValue;
import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.ogc.swe.simpleType.SweAbstractUomType;
import org.n52.sos.ogc.swe.simpleType.SweCount;
import org.n52.sos.ogc.swe.simpleType.SweQuantity;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.sos.response.BinaryAttachmentResponse;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.DateTimeHelper;
import org.n52.sos.util.JavaHelper;
import org.n52.sos.util.http.MediaType;
import org.n52.sos.w3c.SchemaLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
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
                  SosConstants.Operations.GetObservation, UVFConstants.CONTENT_TYPE_UVF),
          (EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos1Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservationById, UVFConstants.CONTENT_TYPE_UVF),
          (EncoderKey) new OperationEncoderKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION,
                  SosConstants.Operations.GetObservationById, UVFConstants.CONTENT_TYPE_UVF));
    
    private final Set<String> CONFORMANCE_CLASSES = ImmutableSet
            .of("http://www.opengis.net/spec/OMXML/2.0/conf/measurement");
    

    private final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
            SupportedTypeKey.ObservationType, Collections.singleton(OmConstants.OBS_TYPE_MEASUREMENT));
    
    private ObservationMerger merger = new ObservationMerger();
    
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
                return encodeGetObsResponse(aor.getObservationCollection());
            } else {
                return createEmptyFile();
            }
        }
        throw new UnsupportedEncoderInputException(this, objectToEncode);

    }

    private BinaryAttachmentResponse createEmptyFile() {
        return new BinaryAttachmentResponse(null, null, null);
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
        for (OmObservation o : merger.mergeObservations(observationCollection, ObservationMergeIndicator.defaultObservationMergerIndicator())) {
                if (o.isSetValue() && !checkForSingleObservationValue(o.getValue()) && !checkForMultiObservationValue(o.getValue())) {
                    String errorMessage = String.format(
                            "The resulting values are not of numeric type which is only supported by this encoder '%s'.",
                            this.getClass().getName());
                    LOGGER.error(errorMessage);
                    throw new NoApplicableCodeException().withMessage(errorMessage);
                }
            
                /*
                 * HEADER: Metadata
                 */
                writeFunktionInterpretation(fw);
                writeIndex(fw);
                writeMessGroesse(fw, o);
                if (o.getObservationConstellation().getObservationType().equals(OmConstants.OBS_TYPE_MEASUREMENT) ||
                        o.getValue().getValue() instanceof SweAbstractUomType<?>) {
                    writeMessEinheit(fw, o.getObservationConstellation().getObservableProperty());
                }
                writeMessStellennummer(fw, o);
                writeMessStellenname(fw, o);
                /*
                 * HEADER: Lines 1 - 4
                 */
                writeLine1(fw);
                TimePeriod temporalBBox = getTemporalBBoxFromObservations(observationCollection);
                writeLine2(fw, o.getObservationConstellation().getObservableProperty(), temporalBBox);
                writeLine3(fw, o);
                writeLine4(fw, temporalBBox);
                /*
                 * Observation Data
                 */
                writeObservationValue(fw, o);
        }
        return uvfFile;
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
            if (unit != null  && !unit.isEmpty()) {
                unit = ensureIdentifierLength(unit, UVFConstants.MAX_IDENTIFIER_LENGTH);
            }
        }
        writeToFile(fw, String.format("$sb Mess-Einheit: %s", unit));
    }

    private void writeMessStellennummer(FileWriter fw, OmObservation o) throws IOException {
        String featureOfInterestIdentifier = o.getObservationConstellation().getFeatureOfInterestIdentifier();
        if (featureOfInterestIdentifier != null && !featureOfInterestIdentifier.isEmpty()) {
            featureOfInterestIdentifier = ensureIdentifierLength(featureOfInterestIdentifier,
                UVFConstants.MAX_IDENTIFIER_LENGTH);
        }
        writeToFile(fw, String.format("$sb Mess-Stellennummer: %s", featureOfInterestIdentifier));
    }

    private void writeMessStellenname(FileWriter fw, OmObservation o) throws IOException {
        if (o.getObservationConstellation().getFeatureOfInterest().isSetName()) {
            final CodeType firstName = o.getObservationConstellation().getFeatureOfInterest().getFirstName();
            String name = ensureIdentifierLength(firstName.isSetValue()?firstName.getValue():"", UVFConstants.MAX_IDENTIFIER_LENGTH);
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
        if (observablePropertyIdentifier != null && !observablePropertyIdentifier.isEmpty()) {
            observablePropertyIdentifier = ensureIdentifierLength(observablePropertyIdentifier,
                    UVFConstants.MAX_IDENTIFIER_LENGTH);
        }
        sb.append(observablePropertyIdentifier);
        fillWithSpaces(sb, UVFConstants.MAX_IDENTIFIER_LENGTH);
        // Unit (optional)
        if (observableProperty instanceof OmObservableProperty) {
            String unit = ((OmObservableProperty)observableProperty).getUnit();
            if (unit != null && !unit.isEmpty()) {
                unit = ensureIdentifierLength(unit,
                    UVFConstants.MAX_IDENTIFIER_LENGTH);
                sb.append(" ");
                sb.append(unit);
            }
        }
        fillWithSpaces(sb, 30);
        // Centuries
        sb.append(centuries.getStart().getYear() + " " + centuries.getEnd().getYear());
        writeToFile(fw, sb.toString());
    }

    private void writeLine3(FileWriter fw, OmObservation o) throws IOException {
        // 3.Zeile 88888 0 0 0.000
        StringBuilder sb = new StringBuilder(45);
        if (!o.isSetObservationID()) {
            o.setObservationID(JavaHelper.generateID(o.toString()));
        }
        String identifier = ensureIdentifierLength(o.getObservationID(), UVFConstants.MAX_IDENTIFIER_LENGTH);
        sb.append(identifier);
        fillWithSpaces(sb, UVFConstants.MAX_IDENTIFIER_LENGTH);
        AbstractFeature f = o.getObservationConstellation().getFeatureOfInterest();
        if (o.getObservationConstellation().getFeatureOfInterest() instanceof SamplingFeature) {
            AbstractSamplingFeature sf = (AbstractSamplingFeature)f;
            String xString = sf.isSetGeometry() ? Double.toString(sf.getGeometry().getCoordinate().x) : "0.0";
            xString = ensureValueLength(xString, 10);
            sb.append(xString);
            fillWithSpaces(sb, 25);
            String yString = sf.isSetGeometry() ? Double.toString(sf.getGeometry().getCoordinate().y) : "0.0";
            yString = ensureValueLength(yString, 10);
            sb.append(yString);
            fillWithSpaces(sb, 35);
            if (sf.isSetGeometry() && !Double.isNaN(sf.getGeometry().getCoordinate().z)) {
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
        // yymmddhhmmvvvvvvvvvv
        // ^ date with ten chars
        //           ^ observed/measured value with 10 chars
        if (omObservation.getValue() instanceof SingleObservationValue<?>) {
            writeSingleObservationValue(fw, omObservation.getPhenomenonTime(),
                    ((SingleObservationValue<?>)omObservation.getValue()).getValue());
        } else if (omObservation.getValue() instanceof MultiObservationValues) {
            writeMultiObservationValues(fw, omObservation);
        } else {
            throw new NoApplicableCodeException().withMessage("Support for '%s' not yet implemented.",
                    omObservation.getValue().getClass().getName());
        }
    }

    /* ***********************************************************************
     *
     *      Helper methods
     *
     * ***********************************************************************/

    private void writeSingleObservationValue(FileWriter fw, Time phenomenonTime, Value<?> value) throws IOException,
            CodedException {
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
                    writeSingleObservationValue(fw, timeValuePair.getTime(), timeValuePair.getValue());
                }
           } else {
               throw new NoApplicableCodeException().withMessage("Support for '%s' not yet implemented.",
                       object.getClass().getName());
           }
        }
    }

    private String encodeObservationValue(Value<?> value) throws CodedException {
        if (value == null) {
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
