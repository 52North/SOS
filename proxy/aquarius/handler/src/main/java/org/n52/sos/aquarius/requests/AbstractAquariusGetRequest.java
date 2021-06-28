package org.n52.sos.aquarius.requests;

import java.util.Map;

import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.proxy.request.AbstractGetRequest;

public abstract class AbstractAquariusGetRequest extends AbstractGetRequest {

    private String changesSinceToke;

    public AbstractAquariusGetRequest withChangesSinceToken(String changeSinceToken) {
        this.changesSinceToke = changeSinceToken;
        return this;
    }

    private boolean isSetChangesSinceToken() {
        return changesSinceToke != null && !changesSinceToke.isEmpty();
    }

    @Override
    public Map<String, String> getQueryParameters() {
        Map<String, String> parameter = createMap();
        if (isSetChangesSinceToken()) {
            parameter.put(AquariusConstants.Parameters.CHANGES_SINCE_TOKEN , changesSinceToke);
        }
        return parameter;
    }

}
