package org.n52.sos.event.events;

import org.n52.sos.event.SosEvent;
import org.n52.sos.response.AbstractServiceResponse;

public class ResponseEvent implements SosEvent {
    private final AbstractServiceResponse response;

    public ResponseEvent(AbstractServiceResponse response) {
        this.response = response;
    }

    public AbstractServiceResponse getResponse() {
        return response;
    }

}
