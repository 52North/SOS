package org.n52.svalbard.inspire.omso.v30.decode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.n52.sos.decode.AbstractOmDecoderv20;
import org.n52.sos.decode.DecoderKey;
import org.n52.sos.service.ServiceConstants.SupportedTypeKey;
import org.n52.svalbard.inspire.omso.InspireOMSOConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class InpspireOMSODecoder extends AbstractOmDecoderv20 {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InpspireOMSODecoder.class);
    
    private static final Set<DecoderKey> DECODER_KEYS = Sets.newHashSet();

    private static final Map<SupportedTypeKey, Set<String>> SUPPORTED_TYPES
            = ImmutableMap.of(SupportedTypeKey.ObservationType, (Set<String>) ImmutableSet
                    .of(InspireOMSOConstants.OBS_TYPE_MULTI_POINT_OBSERVATION,
                            InspireOMSOConstants.OBS_TYPE_POINT_OBSERVATION,
                            InspireOMSOConstants.OBS_TYPE_POINT_TIME_SERIES_OBSERVATION,
                            InspireOMSOConstants.OBS_TYPE_PROFILE_OBSERVATION,
                            InspireOMSOConstants.OBS_TYPE_TRAJECTORY_OBSERVATION));

    private static final Set<String> CONFORMANCE_CLASSES = Sets.newHashSet();

    public InpspireOMSODecoder() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }

    @Override
    public Map<SupportedTypeKey, Set<String>> getSupportedTypes() {
        return Collections.unmodifiableMap(SUPPORTED_TYPES);
    }

    @Override
    public Set<String> getConformanceClasses() {
        return Collections.unmodifiableSet(CONFORMANCE_CLASSES);
    }

}
