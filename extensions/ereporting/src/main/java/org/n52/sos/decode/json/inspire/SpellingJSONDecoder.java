package org.n52.sos.decode.json.inspire;

import org.n52.sos.inspire.aqd.Spelling;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.AQDJSONConstants;

import com.fasterxml.jackson.databind.JsonNode;

public class SpellingJSONDecoder extends AbstractJSONDecoder<Spelling> {
    public SpellingJSONDecoder() {
        super(Spelling.class);
    }

    @Override
    public Spelling decodeJSON(JsonNode node, boolean validate)
            throws OwsExceptionReport {
        Spelling spelling = new Spelling();
        spelling.setScript(parseNillableString(node
                .path(AQDJSONConstants.SCRIPT)));
        spelling.setText(node.path(AQDJSONConstants.TEXT).textValue());
        spelling.setTransliterationScheme(parseNillableString(node
                .path(AQDJSONConstants.SCRIPT)));
        return spelling;
    }

}
