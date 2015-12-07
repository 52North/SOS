package org.n52.svalbard.encode.inspire;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.sos.encode.AbstractXmlEncoder;
import org.n52.sos.encode.EncoderKey;
import org.n52.sos.exception.ows.concrete.UnsupportedEncoderInputException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.om.features.SfConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.ConformanceClasses;
import org.n52.sos.ogc.sos.SosConstants.HelperValues;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.w3c.SchemaLocation;
import org.n52.svalbard.inspire.ef.EnvironmentalMonitoringFacility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import eu.europa.ec.inspire.schemas.ef.x40.EnvironmentalMonitoringFacilityType;

public abstract class AbstractEnvironmentalMonitoringFaciltityTypeEncoder extends AbstractXmlEncoder<AbstractFeature> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentalMonitoringFaciltityTypeEncoder.class);
//
//    @SuppressWarnings("unchecked")
//    private static final Set<EncoderKey> ENCODER_KEYS =
//            CollectionHelper.union(CodingHelper.encoderKeysForElements(SfConstants.NS_SAMS, AbstractFeature.class),
//                    CodingHelper.encoderKeysForElements(SfConstants.NS_SF, AbstractFeature.class));
//
//    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES = Collections.singletonMap(
//            SupportedTypeKey.FeatureType,
//            (Set<String>) Sets.newHashSet(OGCConstants.UNKNOWN, SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_POINT,
//                    SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_CURVE,
//                    SfConstants.SAMPLING_FEAT_TYPE_SF_SAMPLING_SURFACE));
//
//    public EnvironmentalMonitoringFaciltityTypeEncoder() {
//        LOGGER.debug("Encoder for the following keys initialized successfully: {}!",
//                Joiner.on(", ").join(ENCODER_KEYS));
//    }
//
//    @Override
//    public Set<EncoderKey> getEncoderKeyType() {
//        return Collections.unmodifiableSet(ENCODER_KEYS);
//    }
//
//    @Override
//    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
//        return Collections.unmodifiableMap(SUPPORTED_TYPES);
//    }
//
//    @Override
//    public Set<String> getConformanceClasses() {
//        return Collections.emptySet();
//    }
//
//    @Override
//    public void addNamespacePrefixToMap(final Map<String, String> nameSpacePrefixMap) {
//        nameSpacePrefixMap.put(SfConstants.NS_SAMS, SfConstants.NS_SAMS_PREFIX);
//        nameSpacePrefixMap.put(SfConstants.NS_SF, SfConstants.NS_SF_PREFIX);
//    }
//
//    @Override
//    public Set<SchemaLocation> getSchemaLocations() {
//        return Sets.newHashSet(SfConstants.SF_SCHEMA_LOCATION, SfConstants.SAMS_SCHEMA_LOCATION);
//    }



    protected EnvironmentalMonitoringFacilityType createEnvironmentalMonitoringFaciltityType(EnvironmentalMonitoringFacility environmentalMonitoringFacility) {
        EnvironmentalMonitoringFacilityType emft = EnvironmentalMonitoringFacilityType.Factory.newInstance();
        
        
        return emft;
    }

}
