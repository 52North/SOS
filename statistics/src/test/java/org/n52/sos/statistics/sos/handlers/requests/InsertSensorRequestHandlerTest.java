package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.sos.ogc.sos.SosInsertionMetadata;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.sos.SosProcedureDescription;
import org.n52.sos.ogc.sos.SosProcedureDescriptionUnknowType;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class InsertSensorRequestHandlerTest extends HandlerBaseTest {

    @InjectMocks
    InsertSensorRequestHandler handler;

    @SuppressWarnings("unchecked")
    @Test
    public void validateAllFields() {
        InsertSensorRequest request = new InsertSensorRequest();
        request.setAssignedOfferings(Arrays.asList(new SosOffering("p")));
        request.setAssignedProcedureIdentifier("proc");
        request.setObservableProperty(Arrays.asList("op1", "op2"));
        request.setProcedureDescription(new SosProcedureDescriptionUnknowType("id", "format", "xml"));
        request.setProcedureDescriptionFormat("solo-format");
        request.setMetadata(new SosInsertionMetadata());
        request.getMetadata().setFeatureOfInterestTypes(Arrays.asList("foi1", "foi2"));
        request.getMetadata().setObservationTypes(Arrays.asList("ot1", "ot2"));

        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertThat(map.get(SosDataMapping.IS_ASSIGNED_OFFERINGS), CoreMatchers.instanceOf(List.class));
        Assert.assertThat(map.get(SosDataMapping.IS_ASSIGNED_PROCEDURE_IDENTIFIERS), CoreMatchers.is("proc"));
        Assert.assertThat((List<String>) map.get(SosDataMapping.IS_OBSERVABLE_PROPERTY), CoreMatchers.hasItems("op1", "op2"));
        Assert.assertThat(map.get(SosDataMapping.IS_PROCEDURE_DESCRIPTION), CoreMatchers.instanceOf(SosProcedureDescription.class));
        Assert.assertThat(map.get(SosDataMapping.IS_PROCEDURE_DESCRIPTION_FORMAT), CoreMatchers.is("solo-format"));
        Assert.assertThat((Set<String>) map.get(SosDataMapping.IS_FEATURE_OF_INTEREST_TYPES), CoreMatchers.hasItems("foi1", "foi2"));
        Assert.assertThat((Set<String>) map.get(SosDataMapping.IS_OBSERVATION_TYPES), CoreMatchers.hasItems("ot1", "ot2"));

    }
}
