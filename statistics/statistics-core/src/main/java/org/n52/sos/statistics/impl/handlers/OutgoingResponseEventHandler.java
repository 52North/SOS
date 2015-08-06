package org.n52.sos.statistics.impl.handlers;

import java.util.Map;

import org.n52.iceland.event.events.OutgoingResponseEvent;
import org.n52.sos.statistics.api.AbstractElasticSearchDataHolder;
import org.n52.sos.statistics.api.interfaces.IServiceEventHandler;
import org.n52.sos.statistics.api.mappings.ServiceEventDataMapping;

public class OutgoingResponseEventHandler extends AbstractElasticSearchDataHolder implements IServiceEventHandler<OutgoingResponseEvent> {

    @Override
    public Map<String, Object> resolveAsMap(OutgoingResponseEvent event) {
        put(ServiceEventDataMapping.ORE_EXEC_TIME.getName(), event.getElapsedTime());
        put(ServiceEventDataMapping.ORE_COUNT.getName(), event.getRequestNumber());

        return dataMap;
    }

}
