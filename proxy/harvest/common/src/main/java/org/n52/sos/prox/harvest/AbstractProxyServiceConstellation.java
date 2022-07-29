package org.n52.sos.prox.harvest;

import org.n52.janmayen.event.Event;
import org.n52.sensorweb.server.helgoland.adapters.connector.AbstractServiceConstellation;
import org.n52.sos.event.events.UpdateCache;

public class AbstractProxyServiceConstellation extends AbstractServiceConstellation {

    public AbstractProxyServiceConstellation(String fullHarvester, String temporalHarvester) {
        super(fullHarvester, temporalHarvester);
    }

    @Override
    public Event getEvent() {
        return new UpdateCache();
    }
}
