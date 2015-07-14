package org.n52.sos.statistics.sos.models;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.n52.iceland.ogc.ows.Extension;
import org.n52.sos.statistics.api.ServiceEventDataMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionEsModel extends AbstractElasticsearchModel {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionEsModel.class);

    private final Extension<?> extension;

    private ExtensionEsModel(Extension<?> extension) {
        this.extension = extension;
    }

    public static Map<String, Object> convert(Extension<?> extension) {
        return new ExtensionEsModel(extension).getAsMap();
    }

    public static List<Map<String, Object>> convert(Collection<Extension<?>> extensions) {
        if (extensions == null || extensions.isEmpty()) {
            return null;
        }
        return extensions.stream().map(ExtensionEsModel::convert).collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> getAsMap() {
        if (extension == null) {
            return null;
        }
        put(ServiceEventDataMapping.EXT_DEFINITION, extension.getDefinition());
        put(ServiceEventDataMapping.EXT_IDENTIFIER, extension.getIdentifier());
        put(ServiceEventDataMapping.EXT_VALUE, extension.getValue());
        return dataMap;
    }
}
