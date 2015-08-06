package org.n52.sos.statistics.impl.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.n52.iceland.event.events.CountingOutputstreamEvent;
import org.n52.sos.statistics.api.AbstractElasticSearchDataHolder;
import org.n52.sos.statistics.api.interfaces.IServiceEventHandler;
import org.n52.sos.statistics.api.mappings.ServiceEventDataMapping;
import org.n52.sos.statistics.api.parameters.ObjectEsParameterFactory;

public class CountingOutputStreamEventHandler extends AbstractElasticSearchDataHolder implements IServiceEventHandler<CountingOutputstreamEvent> {

    @Override
    public Map<String, Object> resolveAsMap(CountingOutputstreamEvent event) {
        Map<String, Object> data = new HashMap<>();
        data.put(ObjectEsParameterFactory.BYTES.getName(), event.getBytesWritten());
        data.put(ObjectEsParameterFactory.DISPLAY_BYTES.getName(), FileUtils.byteCountToDisplaySize(event.getBytesWritten()));
        put(ServiceEventDataMapping.ORE_BYTES_WRITTEN, data);

        return dataMap;
    }

}
