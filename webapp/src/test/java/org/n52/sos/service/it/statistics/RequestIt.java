package org.n52.sos.service.it.statistics;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class RequestIt extends AbstractStatisticsBase {
	
	@Test
	public void doSomething() throws URISyntaxException, IOException {
		String json = ExampleReaderUtil.readExample("GetCapabilitiesRequest.json");
		postJsonAsString(json);
	}
}
