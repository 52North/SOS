package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.sos.request.InsertResultTemplateRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class InsertResultTemplateRequestHandlerTest extends HandlerBaseTest {

    @InjectMocks
    private InsertResultTemplateRequestHandler handler;

    @Test
    public void validateAllFields() throws OwsExceptionReport {
        InsertResultTemplateRequest request = new InsertResultTemplateRequest();
        request.setIdentifier("id");
        request.setObservationTemplate(omConstellation);
        // SosResultEncoding encoding = new SosResultEncoding();
        // encoding.setEncoding(new SweTextEncoding());
        // request.setResultEncoding(encoding);

        Map<String, Object> map = handler.resolveAsMap(request);

        Assert.assertEquals("id", map.get(SosDataMapping.IRT_IDENTIFIER));
        Assert.assertNotNull(map.get(SosDataMapping.IRT_OBSERVATION_TEMPLATE));
        // Assert.assertEquals("xml",
        // map.get(SosDataMapping.IRT_RESULT_ENCODING));
    }
}
