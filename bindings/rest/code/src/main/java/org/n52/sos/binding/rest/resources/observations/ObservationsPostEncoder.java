/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.sos.binding.rest.resources.observations;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.opengis.om.x20.OMObservationType;
import net.opengis.sos.x20.GetObservationResponseType.ObservationData;
import net.opengis.sosREST.x10.ObservationCollectionDocument;
import net.opengis.sosREST.x10.ObservationCollectionType;
import net.opengis.sosREST.x10.ObservationDocument;
import net.opengis.sosREST.x10.ObservationType;

import org.n52.sos.binding.rest.requests.RestResponse;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.response.ServiceResponse;
import org.n52.sos.util.SosHelper;
import org.n52.sos.util.http.HTTPStatus;


/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public class ObservationsPostEncoder extends AObservationsEncoder {
    
    @Override
    public ServiceResponse encodeRestResponse(RestResponse restResponse) throws OwsExceptionReport
    {
        if (restResponse != null && restResponse instanceof ObservationsPostResponse) {
            ObservationsPostResponse observationsPostResponse = (ObservationsPostResponse) restResponse;
            ObservationDocument xb_ObservationRestDoc = createRestObservationDocumentFrom(observationsPostResponse.getXb_OMObservation());
            
            
            ServiceResponse response = createServiceResponseFromXBDocument(
                    xb_ObservationRestDoc,
                    bindingConstants.getResourceObservations(),
                    HTTPStatus.CREATED, false, true);
            
            addLocationHeader(response,
                    observationsPostResponse.getObservationIdentifier(),
                    bindingConstants.getResourceObservations());
            
            return response;
        }
        if (restResponse != null && restResponse instanceof ObservationsCollectionPostResponse) {
            ObservationsCollectionPostResponse observationsCollectionPostResponse = (ObservationsCollectionPostResponse) restResponse;
            ObservationCollectionDocument xb_ObservationCollectionDoc = ObservationCollectionDocument.Factory.newInstance();
            ObservationCollectionType xb_ObservationCollection = xb_ObservationCollectionDoc.addNewObservationCollection();
            ArrayList<ObservationType> xb_observationList = new ArrayList<ObservationType>();
            Map<String,String> inDocumentReferenceToFeatureId = new HashMap<String, String>();
            for (OMObservationType xb_OMobservation : observationsCollectionPostResponse.getXb_OMObservationCollection())
            {
                SosHelper.checkFreeMemory();
                ObservationType xb_restObservation = createRestObservationFromOMObservation(ObservationType.Factory.newInstance(),
                        xb_OMobservation, inDocumentReferenceToFeatureId);
                xb_observationList.add(xb_restObservation);
            }
            ObservationType[] xb_obsTypeArray = xb_observationList.toArray(new ObservationType[xb_observationList.size()]);
            xb_ObservationCollection.setObservationArray(xb_obsTypeArray);

            ServiceResponse response = createServiceResponseFromXBDocument(
                    xb_ObservationCollectionDoc,
                    bindingConstants.getResourceObservations(),
                    HTTPStatus.OK, true, false);

            return response;
        }
        return null;
    }

}
