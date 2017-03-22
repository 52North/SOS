/**
 * ﻿Copyright (C) 2017
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
package org.n52.sos.convert;

import java.util.Collections;
import java.util.Set;

import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.ogc.series.wml.WaterMLConstants;
import org.n52.sos.request.AbstractObservationRequest;
import org.n52.sos.request.GetObservationByIdRequest;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.response.AbstractObservationResponse;
import org.n52.sos.response.GetObservationByIdResponse;
import org.n52.sos.response.GetObservationResponse;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class WaterMLv20SeriesMetadataModifier extends AbstractRequestResponseModifier<AbstractObservationRequest, AbstractObservationResponse> {
    
    private static final Set<RequestResponseModifierKeyType> REQUEST_RESPONSE_MODIFIER_KEY_TYPES = Sets.newHashSet(
            new RequestResponseModifierKeyType(SosConstants.SOS,
                    Sos2Constants.SERVICEVERSION,
                    new GetObservationRequest(),
                    new GetObservationResponse()),
            new RequestResponseModifierKeyType(SosConstants.SOS,
                    Sos2Constants.SERVICEVERSION,
                    new GetObservationByIdRequest(),
                    new GetObservationByIdResponse()));

    @Override
    public Set<RequestResponseModifierKeyType> getRequestResponseModifierKeyTypes() {
        return Collections.unmodifiableSet(REQUEST_RESPONSE_MODIFIER_KEY_TYPES);
    }

    @Override
    public AbstractObservationResponse modifyResponse(AbstractObservationRequest request, AbstractObservationResponse response)
            throws OwsExceptionReport {
        if (isWaterMLResponse(response) && !response.getObservationCollection().isEmpty()) {
            for (OmObservation omObservation : response.getObservationCollection()) {
                if (!omObservation.isSetValue()) {
                    continue;
                }
                if (omObservation.getObservationConstellation().isSetDefaultPointMetadata()) {
                    omObservation.getValue().setDefaultPointMetadata(
                            omObservation.getObservationConstellation().getDefaultPointMetadata());
                }
                if(omObservation.getObservationConstellation().isSetMetadata()) {
                    omObservation.getValue().setMetadata(
                            omObservation.getObservationConstellation().getMetadata());
                }
            }
        }
        return super.modifyResponse(request, response);
    }

    private boolean isWaterMLResponse(AbstractObservationResponse response) {
        return response.isSetResponseFormat() && response.getResponseFormat().equals(WaterMLConstants.NS_WML_20);
    }


}
