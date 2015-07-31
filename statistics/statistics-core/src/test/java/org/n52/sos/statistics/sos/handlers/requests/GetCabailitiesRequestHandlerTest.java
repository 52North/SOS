package org.n52.sos.statistics.sos.handlers.requests;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.n52.iceland.request.GetCapabilitiesRequest;
import org.n52.sos.statistics.sos.SosDataMapping;

import basetest.HandlerBaseTest;

public class GetCabailitiesRequestHandlerTest extends HandlerBaseTest {
	
    @InjectMocks
    GetCapabilitiesRequestHandler handler;
	
	@SuppressWarnings("unchecked")
	@Test
	public void testAllFields() {
		GetCapabilitiesRequest request = new GetCapabilitiesRequest("SOS");
		request.setAcceptVersions(Arrays.asList("ver1","ver2"));
		request.setAcceptFormats(Arrays.asList("for1","for2"));
		request.setSections(Arrays.asList("a","b","c"));
		request.setUpdateSequence("update-norbi");
		
		Map<String, Object> map = handler.resolveAsMap(request);
		
		Assert.assertEquals("update-norbi", map.get(SosDataMapping.GC_UPDATE_SEQUENCE.getName()));
		Assert.assertThat((Collection<String>) map.get(SosDataMapping.GC_VERSIONS_FIELD.getName()), CoreMatchers.hasItems("ver1","ver2"));
		Assert.assertThat((Collection<String>) map.get(SosDataMapping.GC_FORMATS_FIELD.getName()), CoreMatchers.hasItems("for1","for2"));
		Assert.assertThat((Collection<String>) map.get(SosDataMapping.GC_SECTIONS.getName()), CoreMatchers.hasItems("a","b","c"));
		Assert.assertThat(map.get(SosDataMapping.GC_SECTIONS_CONCAT.getName()), CoreMatchers.is("a_b_c"));
	}

}
