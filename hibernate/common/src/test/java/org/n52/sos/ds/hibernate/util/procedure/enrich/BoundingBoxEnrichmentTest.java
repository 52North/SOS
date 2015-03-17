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
package org.n52.sos.ds.hibernate.util.procedure.enrich;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Collection;

import org.junit.Test;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sensorML.AbstractSensorML;
import org.n52.sos.ogc.sensorML.SensorML;
import org.n52.sos.ogc.sensorML.SensorMLConstants;
import org.n52.sos.ogc.sensorML.System;
import org.n52.sos.ogc.sos.SosEnvelope;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swe.SweEnvelope;
import org.n52.sos.service.ProcedureDescriptionSettings;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.geom.Envelope;


public class BoundingBoxEnrichmentTest {
	
	@Test public void
	should_set_definition_of_observed_bbox_envelope()
			throws OwsExceptionReport {
		final BoundingBoxEnrichment enrichmentMock = mock(BoundingBoxEnrichment.class);
		
				final SosOffering sosOffering = new SosOffering("offeringIdentifier", "offeringName");
		final Collection<SosOffering> sosOfferings = Lists.newArrayList(sosOffering);
		final Envelope envelope = new Envelope(1.0,2.0,3.0,4.0);
		final SosEnvelope sosEnvelope = new SosEnvelope(envelope, 4326);
		when(enrichmentMock.getSosOfferings()).thenReturn(sosOfferings);
		when(enrichmentMock.createEnvelopeForOfferings()).thenReturn(sosEnvelope);
		final ProcedureDescriptionSettings procSettMock = mock(ProcedureDescriptionSettings.class);
		when(procSettMock.getLatLongUom()).thenReturn("deg");
		when(enrichmentMock.procedureSettings()).thenReturn(procSettMock);
		doCallRealMethod().when(enrichmentMock).enrich((AbstractSensorML) any());
		
		final SensorML sml = new SensorML();
		final System system = new System();
		sml.addMember(system);
		enrichmentMock.enrich(sml);
		assertThat(sml.getCapabilities(),hasSize(1));
		assertThat(sml.getCapabilities().get(0).getName(), is(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX));
		assertThat(sml.getCapabilities().get(0).getDataRecord().getFields().get(0).getElement().getDefinition(), is(SensorMLConstants.OBSERVED_BBOX_DEFINITION_URN));
	}
	
	@Test public void
	should_set_reference_frame_of_observed_bbox_envelope()
			throws OwsExceptionReport {
		final BoundingBoxEnrichment enrichmentMock = mock(BoundingBoxEnrichment.class);

		final SosOffering sosOffering = new SosOffering("offeringIdentifier", "offeringName");
		final Collection<SosOffering> sosOfferings = Lists.newArrayList(sosOffering);
		final Envelope envelope = new Envelope(1.0,2.0,3.0,4.0);
		final SosEnvelope sosEnvelope = new SosEnvelope(envelope, 4326);
		when(enrichmentMock.getSosOfferings()).thenReturn(sosOfferings);
		when(enrichmentMock.createEnvelopeForOfferings()).thenReturn(sosEnvelope);
		final ProcedureDescriptionSettings procSettMock = mock(ProcedureDescriptionSettings.class);
		when(procSettMock.getLatLongUom()).thenReturn("deg");
		when(enrichmentMock.procedureSettings()).thenReturn(procSettMock);
		doCallRealMethod().when(enrichmentMock).enrich((AbstractSensorML) any());

		final SensorML sml = new SensorML();
		final System system = new System();
		sml.addMember(system);
		enrichmentMock.enrich(sml);
		assertThat(sml.getCapabilities(),hasSize(1));
		assertThat(sml.getCapabilities().get(0).getName(), is(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX));
		assertThat(((SweEnvelope)sml.getCapabilities().get(0).getDataRecord().getFields().get(0).getElement()).getReferenceFrame(), is("4326"));
	}

}
