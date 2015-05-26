package org.n52.sos.decode;

import java.util.Collections;
import java.util.Set;

import org.n52.iceland.coding.decode.Decoder;
import org.n52.iceland.coding.decode.DecoderKey;
import org.n52.iceland.ogc.sos.Sos1Constants;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.sos.coding.decode.AbstractStringRequestDecoder;
import org.n52.sos.util.CodingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * String request {@link Decoder} for SOS 1.0.0 requests
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 5.0.0
 *
 */
public class SosStringDecoderv100 extends AbstractStringRequestDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosStringDecoderv100.class);

    private static final Set<DecoderKey> DECODER_KEYS = CodingHelper.xmlDecoderKeysForOperation(SosConstants.SOS,
            Sos1Constants.SERVICEVERSION, SosConstants.Operations.GetCapabilities,
            SosConstants.Operations.GetObservation, SosConstants.Operations.GetFeatureOfInterest,
            SosConstants.Operations.GetObservationById, SosConstants.Operations.DescribeSensor);

    public SosStringDecoderv100() {
        LOGGER.debug("Decoder for the following keys initialized successfully: {}!", Joiner.on(", ")
                .join(DECODER_KEYS));
    }

    @Override
    public Set<DecoderKey> getDecoderKeyTypes() {
        return Collections.unmodifiableSet(DECODER_KEYS);
    }
}
