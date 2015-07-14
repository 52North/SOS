package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.sos.request.GetFeatureOfInterestRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class GetFeatureOfInterestRequestHandlerTest extends HandlerBaseTest {

    @InjectMocks
    GetFeatureOfInterestRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void validateAllFields() {
        GetFeatureOfInterestRequest request = new GetFeatureOfInterestRequest();

        request.setFeatureIdentifiers(Arrays.asList("id1", "id2"));
        request.setObservedProperties(Arrays.asList("ob1", "ob2"));
        request.setProcedures(Arrays.asList("p1", "p2"));
        request.setSpatialFilters(Arrays.asList(spatialFilter));
        request.setTemporalFilters(Arrays.asList(temporalFilter));

        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertThat((List<String>) map.get(SosDataMapping.GFOI_FEATURE_IDENTIFIERS), CoreMatchers.hasItems("id1", "id2"));
        Assert.assertThat((List<String>) map.get(SosDataMapping.GFOI_OBSERVED_PROPERTIES), CoreMatchers.hasItems("ob1", "ob2"));
        Assert.assertThat((List<String>) map.get(SosDataMapping.GFOI_PROCEDURES), CoreMatchers.hasItems("p1", "p2"));
        Assert.assertNotNull(map.get(SosDataMapping.GFOI_SPATIAL_FILTER));
        Assert.assertNotNull(map.get(SosDataMapping.GFOI_TEMPORAL_FILTER));
    }
}
