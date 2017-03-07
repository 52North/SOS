/**
 * ﻿Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.converter;

import java.util.Collections;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.n52.schetland.uvf.UVFConstants;
import org.n52.sos.convert.RequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.request.RequestContext;
import org.n52.sos.response.AbstractServiceResponse;
import org.n52.sos.util.CollectionHelper;
import org.n52.sos.util.http.MediaTypes;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class UVFRequestModifierTest {

    @Rule
    public ExpectedException exp = ExpectedException.none();

    private RequestResponseModifier<GetObservationRequest,AbstractServiceResponse> modifier;
    
    private GetObservationRequest request;

    @Before
    public void setUp() throws Exception {
        modifier = new UVFRequestModifier();
        request = new GetObservationRequest();
        RequestContext requestContext = new RequestContext();
        requestContext.setAcceptType(Collections.singletonList(UVFConstants.CONTENT_TYPE_UVF));
        request.setRequestContext(requestContext);
        request.setFeatureIdentifiers(Collections.singletonList("test-feature-of-interest"));
        request.setProcedures(Collections.singletonList("test-procedure"));
        request.setObservedProperties(Collections.singletonList("test-observed-property"));
    }

    @Test
    public void shouldModifyOnlySOS200GetObservationRequests() {
        Assert.assertThat(modifier.getRequestResponseModifierKeyTypes().size(), Is.is(1));
        final RequestResponseModifierKeyType key = modifier.getRequestResponseModifierKeyTypes().iterator().next();
        Assert.assertThat(key.getService(), Is.is(SosConstants.SOS));
        Assert.assertThat(key.getVersion(), Is.is(Sos2Constants.SERVICEVERSION));
        Assert.assertThat(key.getRequest(), Is.is(CoreMatchers.instanceOf(GetObservationRequest.class)));
    }
    
    @Test
    public void shouldThrowExceptionIfFormatUVFAndMoreThanOneFeatureOfInterestRequested() throws OwsExceptionReport {
        exp.expect(NoApplicableCodeException.class);
        exp.expectMessage("When requesting UVF format, the request MUST have "
                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");

        request.setFeatureIdentifiers(CollectionHelper.list("foi-1", "foi-2"));
        modifier.modifyRequest(request);
    }

    @Test
    public void shouldThrowExceptionIfFormatUVFAndNoFeatureOfInterestRequested() throws OwsExceptionReport {
        exp.expect(NoApplicableCodeException.class);
        exp.expectMessage("When requesting UVF format, the request MUST have "
                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");

        final List<String> emptyList = Collections.emptyList();
        request.setFeatureIdentifiers(emptyList);
        modifier.modifyRequest(request);
    }

    @Test
    public void shouldThrowExceptionIfFormatUVFAndMoreThanOneProcedureRequested() throws OwsExceptionReport {
        exp.expect(NoApplicableCodeException.class);
        exp.expectMessage("When requesting UVF format, the request MUST have "
                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");

        request.setProcedures(CollectionHelper.list("procedure-1", "procedure-2"));
        modifier.modifyRequest(request);
    }

    @Test
    public void shouldThrowExceptionIfFormatUVFAndNoProcedureRequested() throws OwsExceptionReport {
        exp.expect(NoApplicableCodeException.class);
        exp.expectMessage("When requesting UVF format, the request MUST have "
                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");

        final List<String> emptyList = Collections.emptyList();
        request.setProcedures(emptyList);
        modifier.modifyRequest(request);
    }

    @Test
    public void shouldThrowExceptionIfFormatUVFAndMoreThanOneObservedPropertyRequested() throws OwsExceptionReport {
        exp.expect(NoApplicableCodeException.class);
        exp.expectMessage("When requesting UVF format, the request MUST have "
                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");

        request.setObservedProperties(CollectionHelper.list("obs-prop-1", "obs-prop-2"));
        modifier.modifyRequest(request);
    }

    @Test
    public void shouldThrowExceptionIfFormatUVFAndNoObservedPropertyRequested() throws OwsExceptionReport {
        exp.expect(NoApplicableCodeException.class);
        exp.expectMessage("When requesting UVF format, the request MUST have "
                + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");

        final List<String> emptyList = Collections.emptyList();
        request.setObservedProperties(emptyList);
        modifier.modifyRequest(request);
    }
    
    @Test
    public void shouldNotModifyRequestNotForUVF() throws OwsExceptionReport{
        request.getRequestContext().setContentType(MediaTypes.APPLICATION_JSON.toString());
        
        GetObservationRequest modifiedRequest = modifier.modifyRequest(request);
        
        Assert.assertThat(modifiedRequest, IsEqual.equalTo(modifiedRequest));
    }
    
    @Test
    public void shouldNotModifyValidRequest() throws OwsExceptionReport {
        GetObservationRequest modifiedRequest = modifier.modifyRequest(request);
        
        Assert.assertThat(modifiedRequest, IsEqual.equalTo(request));
    }
    
    @Test
    public void shouldReturnValidRequestResponseModifierFacilitator() {
        RequestResponseModifierFacilitator facilitator = modifier.getFacilitator();
        
        Assert.assertThat(facilitator.isAdderRemover(), Is.is(false));
        Assert.assertThat(facilitator.isMerger(), Is.is(false));
        Assert.assertThat(facilitator.isSplitter(), Is.is(false));
    }
}
