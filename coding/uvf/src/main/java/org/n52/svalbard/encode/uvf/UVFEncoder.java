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
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.gml.time.TimeInstant;
import org.n52.sos.ogc.gml.time.TimePeriod;
import org.n52.sos.ogc.om.OmConstants;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos1Constants;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.response.AbstractObservationResponse;
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

    private File encodeToUvf(List<OmObservation> observationCollection, File tempDir) throws IOException, DateTimeFormatException {
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
        writeMessEinheit(fw, o);
        writeMessStellennummer(fw, o);
        writeMessStellenname(fw, o);
        /*
         * HEADER: Lines 1 - 4
         */
        writeLine1(fw);
        writeLine2(fw);
        writeLine3(fw, o.getObservationConstellation().getFeatureOfInterest());
        writeLine4(fw, o.getPhenomenonTime());
        // 5.Zeile 7008030730 -777
        
        return uvfFile;
    }

    private void writeLine1(FileWriter fw) throws IOException {
        writeToFile(fw, "*Z");
    }

    private void writeLine2(FileWriter fw) {
        // TODO Auto-generated method stub
        // TODO IMPLEMENT HERE
        // 2.Zeile ABFLUSS m3/s 1900 1900
    }

    private void writeLine3(FileWriter fw, AbstractFeature f) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(f.getIdentifier());
        fillWithSpaces(sb, 15);
        if (f instanceof SamplingFeature) {
            SamplingFeature sf = (SamplingFeature)f;
            sb.append(sf.getGeometry().getCoordinate().x);
            fillWithSpaces(sb, 25);
            sb.append(sf.getGeometry().getCoordinate().y);
            fillWithSpaces(sb, 35);
//            if (sf.getGeometry().getCoordinate().z) {
            sb.append(sf.getGeometry().getCoordinate().z);                
//            } else {
//                sb.append("0.000");
//            }
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
//      3.Zeile 88888 0 0 0.000        
    }

    private void fillWithSpaces(StringBuilder sb, int i) {
        while (sb.length() < i) {
            sb.append(" ");
        }
    }

    private void writeLine4(FileWriter fw, Time time) throws IOException, DateTimeFormatException {
        StringBuilder sb = new StringBuilder();
        if (time instanceof TimeInstant) {
            String timeString = DateTimeHelper.formatDateTime2FormattedString(((TimeInstant) time).getValue(), UVFConstants.TIME_FORMAT);
            sb.append(timeString).append(timeString);
        } else if (time instanceof TimePeriod) {
            sb.append(DateTimeHelper.formatDateTime2FormattedString(((TimePeriod) time).getStart(), UVFConstants.TIME_FORMAT));
            sb.append(DateTimeHelper.formatDateTime2FormattedString(((TimePeriod) time).getEnd(), UVFConstants.TIME_FORMAT));
        }
        fillWithSpaces(sb, 20);
        sb.append("Zeit");
        fillWithSpaces(sb, 28);
        writeToFile(fw, sb.toString());
    }

    private void writeFunktionInterpretation(FileWriter fw) throws IOException {
        writeToFile(fw, "$ib Funktion-Interpretation: Linie");
    }

    private void writeIndex(FileWriter fw) throws IOException {
        writeToFile(fw, "$sb Index-Einheit: *** Zeit ***");        
    }

    private void writeMessGroesse(FileWriter fw, OmObservation o) throws IOException {
        writeToFile(fw, String.format("$sb Mess-Groesse: %s", o.getObservationConstellation().getObservablePropertyIdentifier()));
    }

    private void writeMessEinheit(FileWriter fw, OmObservation o) throws IOException {
//      $sb Mess-Einheit: m3/s
    }

    private void writeMessStellennummer(FileWriter fw, OmObservation o) throws IOException {
        writeToFile(fw, String.format("$sb Mess-Stellennummer: %s", o.getObservationConstellation().getFeatureOfInterestIdentifier()));
    }

    private void writeMessStellenname(FileWriter fw, OmObservation o) throws IOException {
        if (o.getObservationConstellation().getFeatureOfInterest().isSetName()) {
            writeToFile(fw, String.format("$sb Mess-Stellenname: %s", o.getObservationConstellation().getFeatureOfInterest().getFirstName()));
        }
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
        // TODO Auto-generated method stub
        return null;
    }
    
}
