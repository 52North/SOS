/**
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

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.schetland.uvf.UVFConstants;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.convert.UVFRequestModifier;
import org.n52.sos.exception.ConfigurationException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OWSConstants;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.swe.simpleType.SweText;
import org.n52.sos.ogc.swes.SwesExtensionImpl;
import org.n52.sos.ogc.swes.SwesExtensions;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.request.RequestContext;
import org.n52.sos.util.http.MediaTypes;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class UVFRequestModifierTest {

    @Rule
    public ExpectedException exp = ExpectedException.none();

    private UVFRequestModifier modifier;
    
    private GetObservationRequest request;

    @Before
    public void setUp() throws Exception {
        modifier = new UVFRequestModifier();
        modifier.setDefaultCRS(UVFConstants.ALLOWED_CRS.get(0));
        request = new GetObservationRequest();
        RequestContext requestContext = new RequestContext();
        requestContext.setAcceptType(Collections.singletonList(UVFConstants.CONTENT_TYPE_UVF));
        request.setRequestContext(requestContext);
        request.setFeatureIdentifiers(Collections.singletonList("test-feature-of-interest"));
        request.setProcedures(Collections.singletonList("test-procedure"));
        request.setObservedProperties(Collections.singletonList("test-observed-property"));
        SweText crsExtension = (SweText) new SweText()
                .setValue(UVFConstants.ALLOWED_CRS.get(0))
                .setIdentifier(OWSConstants.AdditionalRequestParams.crs.name());
        request.addExtension(new SwesExtensionImpl<SweText>().setValue(crsExtension));
    }

    @Test
    public void shouldModifyOnlySOS200GetObservationRequests() {
        Assert.assertThat(modifier.getRequestResponseModifierKeyTypes().size(), Is.is(2));
        final RequestResponseModifierKeyType key = modifier.getRequestResponseModifierKeyTypes().iterator().next();
        Assert.assertThat(key.getService(), Is.is(SosConstants.SOS));
        Assert.assertThat(key.getVersion(), Is.is(Sos2Constants.SERVICEVERSION));
        Assert.assertThat(key.getRequest(),
                CoreMatchers.either(Is.is(CoreMatchers.instanceOf(GetObservationRequest.class)))
                        .or(Is.is(CoreMatchers.instanceOf(GetObservationByIdRequest.class))));
    }
    
//    @Test
//    public void shouldThrowExceptionIfFormatUVFAndMoreThanOneFeatureOfInterestRequested() throws OwsExceptionReport {
//        exp.expect(NoApplicableCodeException.class);
//        exp.expectMessage("When requesting UVF format, the request MUST have "
//                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");
//
//        request.setFeatureIdentifiers(CollectionHelper.list("foi-1", "foi-2"));
//        modifier.modifyRequest(request);
//    }
//
//    @Test
//    public void shouldThrowExceptionIfFormatUVFAndNoFeatureOfInterestRequested() throws OwsExceptionReport {
//        exp.expect(NoApplicableCodeException.class);
//        exp.expectMessage("When requesting UVF format, the request MUST have "
//                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");
//
//        final List<String> emptyList = Collections.emptyList();
//        request.setFeatureIdentifiers(emptyList);
//        modifier.modifyRequest(request);
//    }
//
//    @Test
//    public void shouldThrowExceptionIfFormatUVFAndMoreThanOneProcedureRequested() throws OwsExceptionReport {
//        exp.expect(NoApplicableCodeException.class);
//        exp.expectMessage("When requesting UVF format, the request MUST have "
//                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");
//
//        request.setProcedures(CollectionHelper.list("procedure-1", "procedure-2"));
//        modifier.modifyRequest(request);
//    }
//
//    @Test
//    public void shouldThrowExceptionIfFormatUVFAndNoProcedureRequested() throws OwsExceptionReport {
//        exp.expect(NoApplicableCodeException.class);
//        exp.expectMessage("When requesting UVF format, the request MUST have "
//                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");
//
//        final List<String> emptyList = Collections.emptyList();
//        request.setProcedures(emptyList);
//        modifier.modifyRequest(request);
//    }
//
//    @Test
//    public void shouldThrowExceptionIfFormatUVFAndMoreThanOneObservedPropertyRequested() throws OwsExceptionReport {
//        exp.expect(NoApplicableCodeException.class);
//        exp.expectMessage("When requesting UVF format, the request MUST have "
//                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");
//
//        request.setObservedProperties(CollectionHelper.list("obs-prop-1", "obs-prop-2"));
//        modifier.modifyRequest(request);
//    }
//
//    @Test
//    public void shouldThrowExceptionIfFormatUVFAndNoObservedPropertyRequested() throws OwsExceptionReport {
//        exp.expect(NoApplicableCodeException.class);
//        exp.expectMessage("When requesting UVF format, the request MUST have "
//                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");
//
//        final List<String> emptyList = Collections.emptyList();
//        request.setObservedProperties(emptyList);
//        modifier.modifyRequest(request);
//    }

    @Test
    public void shouldNotModifyRequestNotForUVF() throws OwsExceptionReport{
        request.getRequestContext().setContentType(MediaTypes.APPLICATION_JSON.toString());
        AbstractObservationRequest modifiedRequest = modifier.modifyRequest(request);
        Assert.assertThat(modifiedRequest, IsInstanceOf.instanceOf(request.getClass()));
        Assert.assertThat((GetObservationRequest)modifiedRequest, IsEqual.equalTo(request));
    }

    @Test
    public void shouldNotModifyValidRequest() throws OwsExceptionReport {
        AbstractObservationRequest modifiedRequest = modifier.modifyRequest(request);
        Assert.assertThat(modifiedRequest, IsInstanceOf.instanceOf(request.getClass()));
        Assert.assertThat((GetObservationRequest)modifiedRequest, IsEqual.equalTo(request));
        Assert.assertThat((GetObservationRequest)modifiedRequest, IsEqual.equalTo(request));
    }

    @Test
    public void shouldReturnValidRequestResponseModifierFacilitator() {
        RequestResponseModifierFacilitator facilitator = modifier.getFacilitator();
        
        Assert.assertThat(facilitator.isAdderRemover(), Is.is(false));
        Assert.assertThat(facilitator.isMerger(), Is.is(false));
        Assert.assertThat(facilitator.isSplitter(), Is.is(false));
    }

    @Test
    public void shouldThrowConfigExcetionIfDefaultCrsEpsgIsBelowAllowedMinumum() {
        exp.expect(ConfigurationException.class);
        exp.expectMessage("Setting with key 'uvf.default.crs': '31465' outside allowed interval ]31466, 31469[.");

        modifier.setDefaultCRS("31465");
    }

    @Test
    public void shouldThrowConfigExcetionIfDefaultCrsEpsgIsAbovenAllowedMaximum() {
        exp.expect(ConfigurationException.class);
        exp.expectMessage("Setting with key 'uvf.default.crs': '31470' outside allowed interval ]31466, 31469[.");

        modifier.setDefaultCRS("31470");
    }

    @Test
    public void shouldThrowConfigExcetionIfDefaultCrsEpsgIsNotParsebleToInteger() {
        exp.expect(ConfigurationException.class);
        exp.expectMessage("Could not parse given new default CRS EPSG code 'aString'. Choose an integer of the interval"
                + " ]31466, 31469[.");

        modifier.setDefaultCRS("aString");
    }

    @Test
    public void shouldAddExtensionWithDefaultCRSIfNotPresent() throws OwsExceptionReport {
        request.setExtensions(null);

        AbstractObservationRequest modifiedRequest = modifier.modifyRequest(request);
        final SwesExtensions extensions = modifiedRequest.getExtensions();

        Assert.assertThat(extensions.getExtensions().size(), Is.is(1));
        Assert.assertThat(extensions.containsExtension(OWSConstants.AdditionalRequestParams.crs), Is.is(true));
        Assert.assertThat(
                ((SweText)extensions.getExtension(OWSConstants.AdditionalRequestParams.crs).getValue()).getValue(),
                Is.is(UVFConstants.ALLOWED_CRS.get(0)));
    }

    @Test
    public void shouldThrowExceptionWhenRequestedCRSIsOutsideAllowedValues() throws OwsExceptionReport {
        ((SweText)request.getExtension(OWSConstants.AdditionalRequestParams.crs).getValue()).setValue(Integer.toString(42));
        exp.expect(NoApplicableCodeException.class);
        exp.expectMessage("When requesting UVF format, the request MUST have a CRS of the German GK bands, e.g. "
                + "'[31466, 31467, 31468, 31469]'. Requested was: '42'.");

        modifier.modifyRequest(request);
    }
}
