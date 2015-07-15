package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.sos.request.GetResultRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class GetResultRequestHandlerTest extends HandlerBaseTest {

    @InjectMocks
    private GetResultRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void validateAllFields() {
        GetResultRequest request = new GetResultRequest();
        request.setFeatureIdentifiers(Arrays.asList("fi1", "fi2"));
        request.setObservationTemplateIdentifier("template1");
        request.setObservedProperty("obp");
        request.setOffering("off");
        request.setSpatialFilter(spatialFilter);
        request.setTemporalFilter(Arrays.asList(temporalFilter));

        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertThat((List<String>) map.get(SosDataMapping.GR_FEATURE_IDENTIFIERS), CoreMatchers.hasItems("fi1", "fi2"));
        Assert.assertEquals("template1", map.get(SosDataMapping.GR_OBSERVATION_TEMPLATE_IDENTIFIER));
        Assert.assertEquals("obp", map.get(SosDataMapping.GR_OBSERVATION_PROPERTY));
        Assert.assertEquals("off", map.get(SosDataMapping.GR_OFFERING));
        Assert.assertNotNull(map.get(SosDataMapping.GR_SPATIAL_FILTER));
        Assert.assertNotNull(map.get(SosDataMapping.GR_TEMPORAL_FILTER));
    }

}
