package org.n52.sos.statistics.sos.handlers.requests;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.n52.iceland.ogc.ows.Extensions;
import org.n52.iceland.ogc.swes.SwesExtension;
import org.n52.iceland.request.GetCapabilitiesRequest;
import org.n52.iceland.util.net.IPAddress;
import org.n52.sos.statistics.api.ServiceEventDataMapping;

import basetest.HandlerBaseTest;

public class AbstractSosRequestHandlerTest extends HandlerBaseTest {

    // any handler would do
    @InjectMocks
    GetCapabilitiesRequestHandler handler;

    @Before
    public void setUp() {
        Mockito.when(locationUtil.resolveOriginalIpAddress(Mockito.any())).thenReturn(new IPAddress("123.123.123.123"));
    }

    @Test
    public void validateAllFields() {

        GetCapabilitiesRequest request = new GetCapabilitiesRequest("SOS");
        request.setVersion("2.0.0");
        request.setRequestContext(requestContext);

        Extensions extensions = new Extensions();
        SwesExtension<String> ext = new SwesExtension<String>("value1");
        extensions.addExtension(ext);

        SwesExtension<String> ext2 = new SwesExtension<String>("value2");
        ext2.setDefinition("def2");
        ext2.setIdentifier("id2");
        extensions.addExtension(ext2);

        request.addExtensions(extensions);
        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_VERSION_FIELD));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_SERVICE_FIELD));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_PROXIED_REQUEST_FIELD));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_OPERATION_NAME_FIELD));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_IP_ADDRESS_FIELD));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_CONTENT_TYPE));
        Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_ACCEPT_TYPES));
        Assert.assertEquals(2, ((List<?>) map.get(ServiceEventDataMapping.SR_EXTENSIONS)).size());
    }
}
