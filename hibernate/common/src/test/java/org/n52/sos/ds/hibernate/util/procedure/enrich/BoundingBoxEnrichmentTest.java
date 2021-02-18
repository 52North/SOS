/*
 * Copyright (C) 2012-2021 52°North Initiative for Geospatial Open Source
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

import java.util.Collection;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Envelope;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sensorML.AbstractSensorML;
import org.n52.shetland.ogc.sensorML.SensorML;
import org.n52.shetland.ogc.sensorML.SensorMLConstants;
import org.n52.shetland.ogc.sensorML.System;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.swe.SweEnvelope;
import org.n52.shetland.util.ReferencedEnvelope;
import org.n52.sos.ds.procedure.AbstractProcedureCreationContext;
import org.n52.sos.ds.procedure.enrich.BoundingBoxEnrichment;
import org.n52.sos.service.ProcedureDescriptionSettings;
import org.n52.sos.util.GeometryHandler;

import com.google.common.collect.Lists;

public class BoundingBoxEnrichmentTest {

    private final BoundingBoxEnrichment enrichmentMock = Mockito.mock(BoundingBoxEnrichment.class);

    @Before
    public void setUp() throws OwsExceptionReport {
        final SosOffering sosOffering = new SosOffering("offeringIdentifier", "offeringName");
        final Collection<SosOffering> sosOfferings = Lists.newArrayList(sosOffering);
        final Envelope envelope = new Envelope(1.0, 2.0, 3.0, 4.0);
        final ReferencedEnvelope sosEnvelope = new ReferencedEnvelope(envelope, 4326);
        Mockito.when(enrichmentMock.getSosOfferings()).thenReturn(sosOfferings);
        Mockito.when(enrichmentMock.createEnvelopeForOfferings()).thenReturn(sosEnvelope);
        final ProcedureDescriptionSettings procSettMock = Mockito.mock(ProcedureDescriptionSettings.class);
        Mockito.when(procSettMock.getLatLongUom()).thenReturn("deg");
        Mockito.when(enrichmentMock.procedureSettings()).thenReturn(procSettMock);
        Mockito.doCallRealMethod().when(enrichmentMock).enrich((AbstractSensorML) ArgumentMatchers.any());
        final GeometryHandler geomHandlerMock = Mockito.mock(GeometryHandler.class);
        final AbstractProcedureCreationContext ctxMock = Mockito.mock(AbstractProcedureCreationContext.class);
        Mockito.when(ctxMock.getGeometryHandler()).thenReturn(geomHandlerMock);
        Mockito.when(geomHandlerMock.isNorthingFirstEpsgCode(sosEnvelope.getSrid())).thenReturn(true);
        Mockito.when(enrichmentMock.getProcedureCreationContext()).thenReturn(ctxMock);
    }

    @Test
    public void should_set_definition_of_observed_bbox_envelope() throws OwsExceptionReport {
        final SensorML sml = new SensorML();
        final System system = new System();
        sml.addMember(system);
        enrichmentMock.enrich(sml);
        MatcherAssert.assertThat(sml.getCapabilities(), Matchers.hasSize(1));
        MatcherAssert.assertThat(sml.getCapabilities().get(0).getName(),
                Matchers.is(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX));
        MatcherAssert.assertThat(sml.getCapabilities().get(0).getDataRecord().getFields().get(0).getElement().getDefinition(),
                Matchers.is(SensorMLConstants.OBSERVED_BBOX_DEFINITION_URN));
    }

    @Test
    public void should_set_reference_frame_of_observed_bbox_envelope() throws OwsExceptionReport {
        final SensorML sml = new SensorML();
        final System system = new System();
        sml.addMember(system);
        enrichmentMock.enrich(sml);
        MatcherAssert.assertThat(sml.getCapabilities(), Matchers.hasSize(1));
        MatcherAssert.assertThat(sml.getCapabilities().get(0).getName(),
                Matchers.is(SensorMLConstants.ELEMENT_NAME_OBSERVED_BBOX));
        MatcherAssert.assertThat(((SweEnvelope) sml.getCapabilities().get(0).getDataRecord().getFields().get(0).getElement())
                .getReferenceFrame(), Matchers.is("4326"));
    }

}
