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
package org.n52.sos.binding.rest.resources.observations;

import java.util.Map;

import net.opengis.om.x20.OMObservationType;
import net.opengis.sampling.x20.SFSamplingFeatureDocument;
import net.opengis.sampling.x20.SFSamplingFeatureType;
import net.opengis.sosREST.x10.LinkType;
import net.opengis.sosREST.x10.ObservationDocument;
import net.opengis.sosREST.x10.ObservationType;

import org.n52.sos.binding.rest.encode.ResourceEncoder;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
public abstract class AObservationsEncoder extends ResourceEncoder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AObservationsEncoder.class);

    protected ObservationDocument createRestObservationDocumentFrom(OMObservationType xb_OMObservation) throws OwsExceptionReport
    {
        ObservationDocument xb_ObservationRestDoc = ObservationDocument.Factory.newInstance();
        ObservationType xb_ObservationRest = xb_ObservationRestDoc.addNewObservation();
        
        createRestObservationFromOMObservation(xb_ObservationRest, xb_OMObservation,null);
        
        return xb_ObservationRestDoc;
    }

    protected ObservationType createRestObservationFromOMObservation(
            ObservationType xb_restObservation,
            OMObservationType xb_observation,
            Map<String,String> inDocumentReferenceToFeatureId) throws OwsExceptionReport
    {
        String observationId = getObservationId(xb_observation);
        if (observationId != null && !observationId.isEmpty())
        {
            // rel:self
            addSelfLink(observationId, xb_restObservation);
            // rel:delete-observation
            addDeleteLink(observationId, xb_restObservation);
        }
        // ref:features
        addFeatureLink(xb_observation,xb_restObservation,inDocumentReferenceToFeatureId);

        // rel:sensors
        addSensorLink(xb_observation,xb_restObservation);
        xb_restObservation.setOMObservation(xb_observation);
        return xb_restObservation;
    }
    
    private void addSensorLink(OMObservationType xb_observation,
            ObservationType xb_restObservation)
    {
        String sensorId = getSensorId(xb_observation);
        if (sensorId != null && !sensorId.isEmpty())
        {
            setValuesOfLinkToUniqueResource(xb_restObservation.addNewLink(),
                    sensorId,
                    bindingConstants.getResourceRelationSensorGet(),
                    bindingConstants.getResourceSensors());
        }
    }

    private String getSensorId(OMObservationType xb_observation)
    {
        if (isProcedureHrefSet(xb_observation))
        {
            return xb_observation.getProcedure().getHref();
        }
        else 
        {
            // XXX continue implementation here! How to parse it correct and generic?
        }
        throw new RuntimeException("NOT_YET_IMPLEMENTED");
    }

    private void addDeleteLink(String observationId,
            ObservationType xb_restObservation)
    {
        setValuesOfLinkToUniqueResource(xb_restObservation.addNewLink(),
                observationId,
                bindingConstants.getResourceRelationObservationDelete(),
                bindingConstants.getResourceObservations());
    }

    private void addSelfLink(String observationId,
            ObservationType xb_restObservation)
    {
        setValuesOfLinkToUniqueResource(xb_restObservation.addNewLink(),
                    observationId,
                    bindingConstants.getResourceRelationSelf(),
                    bindingConstants.getResourceObservations());
    }
    
    private String getObservationId(OMObservationType xb_observation)
    {
        return xb_observation.isSetIdentifier()?xb_observation.getIdentifier().getStringValue():null;
    }

    private void addFeatureLink(OMObservationType xb_observation,
            ObservationType xb_restObservation,
            Map<String, String> inDocumentReferenceToFeatureId) throws OwsExceptionReport
    {
        String featureId = getFeatureId(xb_observation,inDocumentReferenceToFeatureId);
        if (featureId != null && !featureId.isEmpty())
        {
            setValuesOfLinkToUniqueResource(
                    xb_restObservation.addNewLink(),
                    featureId,
                    bindingConstants.getResourceRelationFeatureGet(),
                    bindingConstants.getResourceFeatures());
        }
        else if (isFeatureHrefSet(xb_observation))
        {
            // Feature links points to external service
            LinkType externalFoiLink = xb_restObservation.addNewLink();
            externalFoiLink.setRel(createRelationWithNamespace(bindingConstants.getResourceRelationFeatureGet()));
            externalFoiLink.setHref(xb_observation.getFeatureOfInterest().getHref());
            externalFoiLink.setType(bindingConstants.getContentTypeUndefined().toString());
        }
    }

    private String getFeatureId(OMObservationType xb_observation,
            Map<String, String> inDocumentReferenceToFeatureId) throws OwsExceptionReport
    {
        if (isFeatureHrefSetAndNotAnUrlOrInDocumentReference(xb_observation))
        {
            return xb_observation.getFeatureOfInterest().getHref();
        }
        else if (isFeatureHrefSetAndInDocumentReference(xb_observation))
        {
            return inDocumentReferenceToFeatureId.get(xb_observation.getFeatureOfInterest().getHref());
        }
        else if (!isFeatureHrefSet(xb_observation) && xb_observation.getFeatureOfInterest() != null)
        {
            SFSamplingFeatureType xb_SFFeature;
            try {
                xb_SFFeature = SFSamplingFeatureDocument.Factory.parse(xb_observation.getFeatureOfInterest().newInputStream()).getSFSamplingFeature();
            } catch (Exception e) {
                String exceptionText = String.format("Encoding of SOS core response failed while processing XML encoded feature. Exception: %s",
                        e);
                LOGGER.debug(exceptionText);
                throw new NoApplicableCodeException().causedBy(e).withMessage(exceptionText);
            }
            if (xb_SFFeature.isSetIdentifier() && 
                    xb_SFFeature.getIdentifier().getStringValue() != null && 
                    !xb_SFFeature.getIdentifier().getStringValue().isEmpty()) {
                String featureId = xb_SFFeature.getIdentifier().getStringValue();
                String inDocumentReference = xb_SFFeature.getId();
                if (inDocumentReferenceToFeatureId != null &&
                        inDocumentReference != null
                        && !inDocumentReference.isEmpty())
                {
                    // Need to prepend the "#" for internal links
                    inDocumentReferenceToFeatureId.put("#"+inDocumentReference, featureId);
                }
                return featureId;
            }
        }
        return null;
    }

    private boolean isFeatureHrefSetAndNotAnUrlOrInDocumentReference(OMObservationType xb_observation)
    {
        return isFeatureHrefSet(xb_observation)
                && xb_observation.getFeatureOfInterest().getHref().indexOf("#") == -1
                && xb_observation.getFeatureOfInterest().getHref().indexOf("http://") == -1;
    }

    private boolean isFeatureHrefSetAndInDocumentReference(OMObservationType xb_observation)
    {
        return isFeatureHrefSet(xb_observation) 
                && xb_observation.getFeatureOfInterest().getHref().indexOf("#") > -1;
    }

    private boolean isFeatureHrefSet(OMObservationType xb_observation)
    {
        return xb_observation.getFeatureOfInterest() != null 
                && xb_observation.getFeatureOfInterest().isSetHref()
                && !xb_observation.getFeatureOfInterest().getHref().isEmpty();
    }

    private boolean isProcedureHrefSet(OMObservationType xb_observation)
    {
        return xb_observation.getProcedure().isSetHref() 
                && !xb_observation.getProcedure().getHref().isEmpty();
    }

}
