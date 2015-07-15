package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class GetObservationRequestHandlerTest extends HandlerBaseTest {

    @InjectMocks
    GetObservationRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void hasAllFilterRequest() throws OwsExceptionReport {
        GetObservationRequest request = new GetObservationRequest();
        request.setRequestContext(requestContext);

        request.setOfferings(Arrays.asList("of1", "of2"));
        request.setProcedures(Arrays.asList("p1", "p2"));
        request.setObservedProperties(Arrays.asList("ob1", "ob2"));
        request.setFeatureIdentifiers(Arrays.asList("id1", "id2"));
        request.setMergeObservationValues(true);
        request.setResponseFormat("batman arkham night");

        request.setSpatialFilter(spatialFilter);
        request.setTemporalFilters(Arrays.asList(temporalFilter));

        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertThat((List<String>) map.get(SosDataMapping.GO_OFFERINGS), CoreMatchers.hasItems("of1", "of2"));
        Assert.assertThat((List<String>) map.get(SosDataMapping.GO_PROCEDURES), CoreMatchers.hasItems("p1", "p2"));
        Assert.assertThat((List<String>) map.get(SosDataMapping.GO_OBSERVED_PROPERTIES), CoreMatchers.hasItems("ob1", "ob2"));
        Assert.assertThat((List<String>) map.get(SosDataMapping.GO_FEATURE_OF_INTERESTS), CoreMatchers.hasItems("id1", "id2"));
        Assert.assertThat(map.get(SosDataMapping.GO_RESPONSE_FORMAT), CoreMatchers.is("batman arkham night"));
        Assert.assertThat(map.get(SosDataMapping.GO_IS_MERGED_OBSERVATION_VALUES), CoreMatchers.is(true));
        Assert.assertThat(map.get(SosDataMapping.GO_SPATIAL_FILTER), CoreMatchers.notNullValue());
        Assert.assertThat(map.get(SosDataMapping.GO_TEMPORAL_FILTER), CoreMatchers.allOf(CoreMatchers.instanceOf(List.class)));
    }
}
