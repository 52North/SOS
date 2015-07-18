/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.service.it.statistics;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.n52.sos.statistics.api.ServiceEventDataMapping;
import org.n52.sos.statistics.sos.SosDataMapping;

public class RequestIt extends AbstractStatisticsBase {
	
	public void checkDefaultFields() throws URISyntaxException, IOException, InterruptedException {
		Map<String, Object> map = sendAndWaitUntilRequestIsProcessed("GetCapabilitiesRequest.json");	
		
		Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_OPERATION_NAME_FIELD));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_ACCEPT_TYPES));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_CONTENT_TYPE));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_IP_ADDRESS_FIELD));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_PROXIED_REQUEST_FIELD));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_SERVICE_FIELD));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.SR_VERSION_FIELD));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.ORE_COUNT));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.ORE_EXEC_TIME));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.ORE_BYTES_WRITTEN));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.UUID_FIELD));
	}
	
	@Test
	public void checkExceptionMessageFields() throws URISyntaxException, IOException, InterruptedException {
		Map<String, Object> map = sendAndWaitUntilRequestIsProcessed("GetObservationRequest-single.json");	
		
		Assert.assertNotNull(map.get(ServiceEventDataMapping.EX_MESSAGE));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.EX_STATUS));
		Assert.assertNotNull(map.get(ServiceEventDataMapping.EX_VERSION));
		
	}
	
	@Test
	public void GetCapabilitiesRequest() throws URISyntaxException, IOException, InterruptedException {
		Map<String, Object> map = sendAndWaitUntilRequestIsProcessed("GetCapabilitiesRequest.json");	
		Assert.assertEquals("GetCapabilities",map.get(ServiceEventDataMapping.SR_OPERATION_NAME_FIELD));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void GetObservationRequestMergeIntoArray() throws URISyntaxException, IOException, InterruptedException {
		Map<String, Object> map = sendAndWaitUntilRequestIsProcessed("GetObservationRequest-merge-into-array.json");	
		Assert.assertEquals("GetObservation",map.get(ServiceEventDataMapping.SR_OPERATION_NAME_FIELD));
		
		List<Map<String,Object>> exts = (List<Map<String,Object>>) map.get(ServiceEventDataMapping.SR_EXTENSIONS);
		Assert.assertTrue(exts.stream().anyMatch(l -> 
		l.get(ServiceEventDataMapping.EXT_DEFINITION).equals("MergeObservationsIntoDataArray")));
	}
	
	@Test
	public void GetObservationRequestsingle() throws URISyntaxException, IOException, InterruptedException {
		Map<String, Object> map = sendAndWaitUntilRequestIsProcessed("GetObservationRequest-single.json");	
		Assert.assertEquals("GetObservation",map.get(ServiceEventDataMapping.SR_OPERATION_NAME_FIELD));
		Assert.assertNotNull(map.get(SosDataMapping.GO_SPATIAL_FILTER));
		Assert.assertNotNull(map.get(SosDataMapping.GO_TEMPORAL_FILTER));
	}
	
	@Test
	public void GetObservationRequestMultiple() throws URISyntaxException, IOException, InterruptedException {
		Map<String, Object> map = sendAndWaitUntilRequestIsProcessed("GetObservationRequest-multiple.json");	
		Assert.assertEquals("GetObservation",map.get(ServiceEventDataMapping.SR_OPERATION_NAME_FIELD));
		Assert.assertNotNull(map.get(SosDataMapping.GO_SPATIAL_FILTER));
		Assert.assertEquals(2, ((Collection<?>) map.get(SosDataMapping.GO_TEMPORAL_FILTER)).size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void InsertObservationRequestSingleObservation() throws URISyntaxException, IOException, InterruptedException {
		Map<String, Object> map = sendAndWaitUntilRequestIsProcessed("InsertObservationRequest-single-observation.json");
		Assert.assertNotNull(map);
		
		List<Map<String,Object>> observations = (List<Map<String, Object>>) map.get(SosDataMapping.IO_OBSERVATION);
		Map<String, Object> observation = observations.get(0);
		Assert.assertNotNull(observation.get(SosDataMapping.OMOBS_RESULT_TIME));
		Assert.assertNotNull(observation.get(SosDataMapping.OMOBS_PHENOMENON_TIME));
		
		Map<String,Object> constellation = (Map<String,Object>) observation.get(SosDataMapping.OMOBS_CONSTELLATION);
		Assert.assertNotNull(constellation.get(SosDataMapping.OMOCONSTELL_FEATURE_OF_INTEREST));
		Assert.assertNotNull(constellation.get(SosDataMapping.OMOCONSTELL_OBSERVABLE_PROPERTY));
		Assert.assertNotNull(constellation.get(SosDataMapping.OMOCONSTELL_OBSERVATION_TYPE));
		Assert.assertNotNull(constellation.get(SosDataMapping.OMOCONSTELL_PROCEDURE));
		
	}

}
