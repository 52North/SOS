package org.n52.sos.statistics.impl.handlers;

import java.util.Map;

import org.n52.iceland.event.ServiceEvent;
import org.n52.sos.statistics.api.AbstractElasticSearchDataHolder;
import org.n52.sos.statistics.api.interfaces.IServiceEventHandler;
import org.n52.sos.statistics.api.mappings.ServiceEventDataMapping;

public class DefaultServiceEventHandler extends AbstractElasticSearchDataHolder implements IServiceEventHandler<ServiceEvent> {

    @Override
    public Map<String, Object> resolveAsMap(ServiceEvent event) {
        put(ServiceEventDataMapping.UNHANDLED_SERVICEEVENT_TYPE.getName(), event.getClass());
        return dataMap;
    }

}
