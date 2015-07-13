package org.n52.sos.statistics.sos.models;

import java.util.Map;

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

    public static ExtensionEsModel convert(Extension<?> extension) {
        return new ExtensionEsModel(extension);
    }

    @Override
    public Map<String, Object> getAsMap() {
        if (extension == null) {
            return null;
        }
        put(ServiceEventDataMapping.EXT_DEFINITION, extension.getDefinition());
        put(ServiceEventDataMapping.EXT_IDENTIFIER, extension.getIdentifier());
        put(ServiceEventDataMapping.EXT_VALUE, extension.getValue());
        return dataMap;
    }
}
