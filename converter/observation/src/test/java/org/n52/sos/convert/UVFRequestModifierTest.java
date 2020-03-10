/*
 * Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.convert;

import java.util.Collections;

import javax.naming.ConfigurationException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.iceland.convert.RequestResponseModifierFacilitator;
import org.n52.iceland.convert.RequestResponseModifierKey;
import org.n52.janmayen.http.MediaTypes;
import org.n52.shetland.ogc.ows.OWSConstants;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.ows.extension.Extensions;
import org.n52.shetland.ogc.ows.service.OwsServiceRequest;
import org.n52.shetland.ogc.ows.service.OwsServiceRequestContext;
import org.n52.shetland.ogc.sos.Sos2Constants;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.ogc.swe.simpleType.SweText;
import org.n52.shetland.ogc.swes.SwesExtension;
import org.n52.shetland.uvf.UVFConstants;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 *
 */
public class UVFRequestModifierTest {

    @Rule
    public ExpectedException exp = ExpectedException.none();

    private UVFRequestModifier modifier;

    private GetObservationRequest request;

    @Before
    public void setUp()
            throws Exception {
        modifier = new UVFRequestModifier();
        modifier.setDefaultCRS(UVFConstants.ALLOWED_CRS.get(0));
        request = new GetObservationRequest();
        OwsServiceRequestContext requestContext = new OwsServiceRequestContext();
        requestContext.setAcceptType(Collections.singletonList(UVFConstants.CONTENT_TYPE_UVF));
        request.setRequestContext(requestContext);
        request.setFeatureIdentifiers(Collections.singletonList("test-feature-of-interest"));
        request.setProcedures(Collections.singletonList("test-procedure"));
        request.setObservedProperties(Collections.singletonList("test-observed-property"));
        SweText crsExtension = (SweText) new SweText().setValue(UVFConstants.ALLOWED_CRS.get(0))
                .setIdentifier(OWSConstants.AdditionalRequestParams.crs.name());
        request.addExtension(new SwesExtension<SweText>().setValue(crsExtension)
                .setIdentifier(OWSConstants.AdditionalRequestParams.crs.name()));
    }

    @Test
    public void shouldModifyOnlySOS200GetObservationRequests() {
        MatcherAssert.assertThat(modifier.getKeys().size(), Is.is(2));
        final RequestResponseModifierKey key = modifier.getKeys().iterator().next();
        MatcherAssert.assertThat(key.getService(), Is.is(SosConstants.SOS));
        MatcherAssert.assertThat(key.getVersion(), Is.is(Sos2Constants.SERVICEVERSION));
        MatcherAssert.assertThat(key.getVersion(), Is.is(Sos2Constants.SERVICEVERSION));
    }

    @Test
    public void shouldNotModifyRequestNotForUVF()
            throws OwsExceptionReport {
        request.getRequestContext().setContentType(MediaTypes.APPLICATION_JSON.toString());
        OwsServiceRequest modifiedRequest = modifier.modifyRequest(request);
        MatcherAssert.assertThat(modifiedRequest, IsInstanceOf.instanceOf(request.getClass()));
        MatcherAssert.assertThat((GetObservationRequest) modifiedRequest, IsEqual.equalTo(request));
    }

    @Test
    public void shouldNotModifyValidRequest()
            throws OwsExceptionReport {
        OwsServiceRequest modifiedRequest = modifier.modifyRequest(request);
        MatcherAssert.assertThat(modifiedRequest, IsInstanceOf.instanceOf(request.getClass()));
        MatcherAssert.assertThat((GetObservationRequest) modifiedRequest, IsEqual.equalTo(request));
        MatcherAssert.assertThat((GetObservationRequest) modifiedRequest, IsEqual.equalTo(request));
    }

    @Test
    public void shouldReturnValidRequestResponseModifierFacilitator() {
        RequestResponseModifierFacilitator facilitator = modifier.getFacilitator();

        MatcherAssert.assertThat(facilitator.isAdderRemover(), Is.is(false));
        MatcherAssert.assertThat(facilitator.isMerger(), Is.is(false));
        MatcherAssert.assertThat(facilitator.isSplitter(), Is.is(false));
    }

    @Test
    public void shouldThrowConfigExcetionIfDefaultCrsEpsgIsBelowAllowedMinumum()
            throws ConfigurationException {
        exp.expect(ConfigurationException.class);
        exp.expectMessage("Setting with key 'uvf.default.crs': '31465' outside allowed interval ]31466, 31469[.");

        modifier.setDefaultCRS("31465");
    }

    @Test
    public void shouldThrowConfigExcetionIfDefaultCrsEpsgisAbovenAllowedMaximum()
            throws ConfigurationException {
        exp.expect(ConfigurationException.class);
        exp.expectMessage("Setting with key 'uvf.default.crs': '31470' outside allowed interval ]31466, 31469[.");

        modifier.setDefaultCRS("31470");
    }

    @Test
    public void shouldThrowConfigExcetionIfDefaultCrsEpsgIsNotParsebleToInteger()
            throws ConfigurationException {
        exp.expect(ConfigurationException.class);
        exp.expectMessage(
                "Could not parse given new default CRS EPSG code 'aString'. Choose an integer of the interval"
                        + " ]31466, 31469[.");

        modifier.setDefaultCRS("aString");
    }

    @Test
    public void shouldAddExtensionWithDefaultCRSIfNotPresent()
            throws OwsExceptionReport {
        request.setExtensions(null);

        OwsServiceRequest modifiedRequest = modifier.modifyRequest(request);
        final Extensions extensions = modifiedRequest.getExtensions();

        MatcherAssert.assertThat(extensions.getExtensions().size(), Is.is(1));
        MatcherAssert.assertThat(extensions.containsExtension(OWSConstants.AdditionalRequestParams.crs), Is.is(true));
        MatcherAssert.assertThat(
                ((SweText) extensions.getExtension(OWSConstants.AdditionalRequestParams.crs).get().getValue())
                        .getValue(),
                Is.is(UVFConstants.ALLOWED_CRS.get(0)));
    }

    @Test
    public void shouldThrowExceptionWhenRequestedCRSIsOutsideAllowedValues()
            throws OwsExceptionReport {
        ((SweText) request.getExtension(OWSConstants.AdditionalRequestParams.crs).get().getValue())
                .setValue(Integer.toString(42));
        exp.expect(NoApplicableCodeException.class);
        exp.expectMessage("When requesting UVF format, the request MUST have a CRS of the German GK bands, e.g. "
                + "'[31466, 31467, 31468, 31469]'. Requested was: '42'.");

        modifier.modifyRequest(request);
    }
}
