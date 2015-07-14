package org.n52.sos.statistics.sos.models;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.n52.iceland.ogc.ows.Extension;
import org.n52.iceland.ogc.swes.SwesExtension;
import org.n52.sos.statistics.api.ServiceEventDataMapping;

public class ExtensionEsModelTest {

    @Test
    public void transformExtensionToEsModel() {
        Extension<String> ext = new SwesExtension<String>("my-value");
        ext.setDefinition("definition");
        ext.setIdentifier("identifier");
        ext.setNamespace("namespace");

        Map<String, Object> map = ExtensionEsModel.convert(ext);

        Assert.assertEquals("my-value", map.get(ServiceEventDataMapping.EXT_VALUE));
        Assert.assertEquals("definition", map.get(ServiceEventDataMapping.EXT_DEFINITION));
        Assert.assertEquals("identifier", map.get(ServiceEventDataMapping.EXT_IDENTIFIER));
    }

    @Test
    public void resultsInNullExtension() {
        Map<String, Object> map = ExtensionEsModel.convert((Extension<?>) null);
        Assert.assertNull(map);
    }

}
