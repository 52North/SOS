package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class InsertObservationRequestHandlerTest extends HandlerBaseTest {

    @InjectMocks
    InsertObservationRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void validateAllFields() {
        InsertObservationRequest request = new InsertObservationRequest();
        request.addObservation(omObservation);
        request.setAssignedSensorId("sensorId");
        request.setOfferings(Arrays.asList("of1"));

        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertEquals("sensorId", map.get(SosDataMapping.IO_ASSIGNED_SENSORID));
        Assert.assertThat((List<String>) map.get(SosDataMapping.IO_ASSIGNED_SENSORID), CoreMatchers.hasItem("of1"));
        Assert.assertNotNull(map.get(SosDataMapping.IO_OBSERVATION));
    }
}
