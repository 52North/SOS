package org.n52.sos.decode.json.inspire;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.sos.inspire.aqd.Pronunciation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.AQDJSONConstants;
import org.n52.sos.util.Functions;

import com.fasterxml.jackson.databind.JsonNode;

public class PronunciationJSONDecoder extends AbstractJSONDecoder<Pronunciation> {

    public PronunciationJSONDecoder() {
        super(Pronunciation.class);
    }

    @Override
    public Pronunciation decodeJSON(JsonNode node, boolean validate)
            throws OwsExceptionReport {
        Pronunciation pronunciation = new Pronunciation();
        pronunciation
                .setIPA(parseNillableString(node.path(AQDJSONConstants.IPA)));
        pronunciation.setSoundLink(parseNillableString(node
                .path(AQDJSONConstants.SOUND_LINK)).transform(Functions
                        .stringToURI()));
        return pronunciation;
    }

}
