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
import java.util.Set;

import org.n52.schetland.uvf.UVFConstants;
import org.n52.sos.convert.RequestResponseModifier;
import org.n52.sos.convert.RequestResponseModifierFacilitator;
import org.n52.sos.convert.RequestResponseModifierKeyType;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.AbstractServiceResponse;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class UVFRequestModifier implements RequestResponseModifier<GetObservationRequest, AbstractServiceResponse> {

    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = Sets.newHashSet(
            new RequestResponseModifierKeyType(
                    SosConstants.SOS,
                    Sos2Constants.SERVICEVERSION,
                    new GetObservationRequest()));
    
    @Override
    public Set<RequestResponseModifierKeyType> getRequestResponseModifierKeyTypes() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }


    @Override
    public GetObservationRequest modifyRequest(GetObservationRequest request) throws OwsExceptionReport {
        if (request.getRequestContext().getAcceptType().isPresent() && 
                request.getRequestContext().getAcceptType().get().contains(UVFConstants.CONTENT_TYPE_UVF)) {
            if (request.isSetFeatureOfInterest() && request.getFeatureIdentifiers().size() == 1 &&
                    request.isSetObservableProperty() && request.getObservedProperties().size() == 1 &&
                    request.isSetProcedure() && request.getProcedures().size() == 1) {
                return request;
            } else {
                throw new NoApplicableCodeException().withMessage("When requesting UVF format, the request MUST have "
                        + "ONE procedure, ONE observedProperty, and ONE featureOfInterest.");
            }
        }
        return request;
    }

    @Override
    public AbstractServiceResponse modifyResponse(GetObservationRequest request, AbstractServiceResponse response)
            throws OwsExceptionReport {
        return response;
    }

    @Override
    public RequestResponseModifierFacilitator getFacilitator() {
        return new RequestResponseModifierFacilitator();
    }

}
