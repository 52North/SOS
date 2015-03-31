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

import java.util.Collection;

import org.n52.sos.cache.WritableContentCache;
import org.n52.sos.ogc.sos.SosOffering;
import org.n52.sos.ogc.swes.SwesFeatureRelationship;
import org.n52.sos.request.InsertSensorRequest;
import org.n52.sos.response.InsertSensorResponse;
import org.n52.sos.util.Action;
import org.n52.sos.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When executing this &auml;ction (see {@link Action}), the following relations
 * are added and some settings are updated in cache:
 * <ul>
 * <li>Procedure</li>
 * <li>Procedure &harr; parent procedures</li>
 * <li>Offering &harr; procedure</li>
 * <li>Offering &rarr; name</li>
 * </ul>
 * <li>Offering &rarr; allowed observation type</li> <li>Offering &rarr; related
 * feature</li> <li>Related features &rarr; role</li> <li>Observable Property
 * &harr; Procedure</li> <li>Offering &harr; observable property</li>
 * 
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 * 
 */
public class SensorInsertionUpdate extends InMemoryCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorInsertionUpdate.class);

    private final InsertSensorResponse response;

    private final InsertSensorRequest request;

    public SensorInsertionUpdate(InsertSensorRequest request, InsertSensorResponse response) {
        if (request == null || response == null) {
            String msg =
                    String.format("Missing argument: '%s': %s; '%s': %s", InsertSensorRequest.class.getName(),
                            request, InsertSensorResponse.class.getName(), response);
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.response = response;
        this.request = request;
    }

    @Override
    public void execute() {
        final WritableContentCache cache = getCache();
        final String procedure = response.getAssignedProcedure();

        // procedure relations
        cache.addProcedure(procedure);
        if (request.getProcedureDescription().isSetParentProcedures()) {
            cache.addParentProcedures(procedure, request.getProcedureDescription().getParentProcedures());
        }
        // TODO child procedures

        // offerings
        for (SosOffering sosOffering : request.getAssignedOfferings()) {
            if (sosOffering.isParentOffering()) {
                cache.addHiddenChildProcedureForOffering(sosOffering.getIdentifier(), procedure);
            } else {
                cache.addOffering(sosOffering.getIdentifier());
                cache.addProcedureForOffering(sosOffering.getIdentifier(), procedure);
                if (sosOffering.isSetName()) {
                    cache.setNameForOffering(sosOffering.getIdentifier(), sosOffering.getOfferingName());
                    cache.addOfferingIdentifierHumanReadableName(sosOffering.getIdentifier(), sosOffering.getOfferingName());
                }
            }

            // add offering for procedure whether it's a normal offering or
            // hidden child
            cache.addOfferingForProcedure(procedure, sosOffering.getIdentifier());

            // allowed observation types
            cache.addAllowedObservationTypesForOffering(sosOffering.getIdentifier(), request.getMetadata()
                    .getObservationTypes());
            // allowed featureOfInterest types
            cache.addAllowedFeatureOfInterestTypesForOffering(sosOffering.getIdentifier(), request
                    .getMetadata().getFeatureOfInterestTypes());
        }

        // related features
        final Collection<SwesFeatureRelationship> relatedFeatures = request.getRelatedFeatures();
        if (CollectionHelper.isNotEmpty(relatedFeatures)) {
            for (SwesFeatureRelationship relatedFeature : relatedFeatures) {
                final String identifier = relatedFeature.getFeature().getIdentifierCodeWithAuthority().getValue();
                for (SosOffering sosOffering : request.getAssignedOfferings()) {
                    // TODO check if check for parent offering is necessary;
                    cache.addRelatedFeatureForOffering(sosOffering.getIdentifier(), identifier);
                }
                cache.addRoleForRelatedFeature(identifier, relatedFeature.getRole());
            }
        }

        // observable property relations
        for (String observableProperty : request.getObservableProperty()) {
            cache.addProcedureForObservableProperty(observableProperty, procedure);
            cache.addObservablePropertyForProcedure(procedure, observableProperty);
            for (SosOffering sosOffering : request.getAssignedOfferings()) {
                cache.addOfferingForObservableProperty(observableProperty, sosOffering.getIdentifier());
                cache.addObservablePropertyForOffering(sosOffering.getIdentifier(), observableProperty);
            }
        }
    }
}
