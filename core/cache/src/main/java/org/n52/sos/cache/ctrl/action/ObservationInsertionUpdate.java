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
package org.n52.sos.cache.ctrl.action;

import java.util.List;

import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.exception.CodedException;
import org.n52.sos.ogc.OGCConstants;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.time.Time;
import org.n52.sos.ogc.om.NamedValue;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.request.InsertObservationRequest;
import org.n52.sos.util.Action;
import org.n52.sos.util.GeometryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * When executing this &auml;ction (see {@link Action}), the following relations
 * are added, settings are updated in cache:
 * <ul>
 * <li>Observation Type</li>
 * <li>Observation identifier (OPTIONAL)</li>
 * <li>Procedure &rarr; Observation identifier (OPTIONAL)</li>
 * <li>Global spatial bounding box</li>
 * <li>Feature identifier</li>
 * <li>Feature types</li>
 * <li>Feature &harr; procedure</li>
 * <li>Feature &harr; feature</li>
 * <li>Offering &harr; related feature</li>
 * <li>Offering &harr; procedure</li>
 * <li>Offering &harr; observable property</li>
 * <li>Offering &rarr; observation type</li>
 * <li>Offering &rarr; temporal bounding box</li>
 * <li>Offering &rarr; spatial bounding box</li>
 * <li>Procedure &rarr; temporal bounding box</li>
 * <li>Global temporal bounding box</li>
 * </ul>
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 * 
 */
public class ObservationInsertionUpdate extends InMemoryCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObservationInsertionUpdate.class);

    private final InsertObservationRequest request;

    public ObservationInsertionUpdate(InsertObservationRequest request) {
        if (request == null) {
            String msg =
                    String.format("Missing argument: '%s': %s", InsertObservationRequest.class.getName(), request);
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.request = request;
    }

    @Override
    public void execute() {
        final WritableContentCache cache = getCache();
        // TODO Review required methods and update test accordingly (@see
        // SensorInsertionInMemoryCacheUpdate)
        // Always update the javadoc when changing this method!
        for (OmObservation observation : request.getObservations()) {
            final String observableProperty =
                    observation.getObservationConstellation().getObservableProperty().getIdentifier();
            final String observationType = observation.getObservationConstellation().getObservationType();
            final String procedure = observation.getObservationConstellation().getProcedure().getIdentifier();
            final Time phenomenonTime = observation.getPhenomenonTime();
            final Time resultTime = observation.getResultTime();

            cache.updatePhenomenonTime(phenomenonTime);
            cache.updateResultTime(resultTime);
            cache.updatePhenomenonTimeForProcedure(procedure, phenomenonTime);

            // update features
            List<SamplingFeature> observedFeatures =
                    sosFeaturesToList(observation.getObservationConstellation().getFeatureOfInterest());

            final Envelope envelope = createEnvelopeFrom(observedFeatures);
            cache.updateGlobalEnvelope(envelope);

            for (SamplingFeature sosSamplingFeature : observedFeatures) {
                String featureOfInterest = sosSamplingFeature.getIdentifierCodeWithAuthority().getValue();

                cache.addFeatureOfInterest(featureOfInterest);
                if (sosSamplingFeature.isSetName()) {
                	cache.addFeatureOfInterestIdentifierHumanReadableName(featureOfInterest, sosSamplingFeature.getFirstName().getValue());
                }
                cache.addProcedureForFeatureOfInterest(featureOfInterest, procedure);
                if (sosSamplingFeature.isSetSampledFeatures()) {
                    for (AbstractFeature parentFeature : sosSamplingFeature.getSampledFeatures()) {
                        getCache().addParentFeature(sosSamplingFeature.getIdentifierCodeWithAuthority().getValue(),
                                parentFeature.getIdentifierCodeWithAuthority().getValue());
                    }
                }
                for (String offering : request.getOfferings()) {
                    cache.addRelatedFeatureForOffering(offering, featureOfInterest);
                    cache.addFeatureOfInterestForOffering(offering, featureOfInterest);
                    if (!OGCConstants.UNKNOWN.equals(sosSamplingFeature.getFeatureType())) {
                        cache.addFeatureOfInterestTypesForOffering(offering, sosSamplingFeature.getFeatureType());
                    }
                }
            }

            // update Spatial Filtering Profile envelope
            Envelope spatialFitleringProfileEnvelope = new Envelope();
            if (observation.isSetParameter()) {
                for (NamedValue<?> namedValue : observation.getParameter()) {
                    if (Sos2Constants.HREF_PARAMETER_SPATIAL_FILTERING_PROFILE.equals(namedValue.getName().getHref())) {
                        if (namedValue.getValue().isSetValue()) {
                            spatialFitleringProfileEnvelope.expandToInclude(((Geometry) namedValue.getValue().getValue()).getEnvelopeInternal());
                            spatialFitleringProfileEnvelope.expandToInclude(((Geometry) namedValue.getValue()
                                    .getValue()).getEnvelopeInternal());
                        }
                    }
                }
            }

            // update offerings
            for (String offering : request.getOfferings()) {
                // procedure
                cache.addOffering(offering);
                if (!cache.getHiddenChildProceduresForOffering(offering).contains(procedure)) {
                    cache.addProcedureForOffering(offering, procedure);
                }
                cache.addOfferingForProcedure(procedure, offering);
                // observable property
                cache.addOfferingForObservableProperty(observableProperty, offering);
                cache.addObservablePropertyForOffering(offering, observableProperty);
                // observation type
                cache.addObservationTypesForOffering(offering, observationType);
                // envelopes/bounding boxes (spatial and temporal)
                cache.updatePhenomenonTimeForOffering(offering, phenomenonTime);
                cache.updateResultTimeForOffering(offering, resultTime);
                cache.updateEnvelopeForOffering(offering, envelope);
                if (spatialFitleringProfileEnvelope != null && !envelope.isNull()) {
                    cache.updateSpatialFilteringProfileEnvelopeForOffering(offering, spatialFitleringProfileEnvelope);
                }
            }

        }
    }
}
