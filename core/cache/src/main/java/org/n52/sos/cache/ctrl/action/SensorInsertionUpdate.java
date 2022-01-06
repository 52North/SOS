/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
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
import java.util.HashSet;
import java.util.Set;

import org.n52.iceland.convert.ConverterRepository;
import org.n52.iceland.util.action.Action;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.sos.SosOffering;
import org.n52.shetland.ogc.sos.request.InsertSensorRequest;
import org.n52.shetland.ogc.sos.response.InsertSensorResponse;
import org.n52.shetland.ogc.swes.SwesFeatureRelationship;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.cache.SosWritableContentCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * When executing this &auml;ction (see {@link Action}), the following relations
 * are added and some settings are updated in cache:
 * <ul>
 * <li>Procedure</li>
 * <li>Procedure &harr; parent procedures</li>
 * <li>Offering &harr; procedure</li>
 * <li>Offering &rarr; name</li>
 * </ul>
 * <li>Offering &rarr; allowed observation type</li>
 * <li>Offering &rarr; related feature</li>
 * <li>Related features &rarr; role</li>
 * <li>Observable Property &harr; Procedure</li>
 * <li>Offering &harr; observable property</li>
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

    private final ConverterRepository converter;

    public SensorInsertionUpdate(InsertSensorRequest request, InsertSensorResponse response,
            ConverterRepository converter) {
        if (request == null || response == null) {
            String msg = String.format("Missing argument: '%s': %s; '%s': %s", InsertSensorRequest.class.getName(),
                    request, InsertSensorResponse.class.getName(), response);
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.response = response;
        this.request = request;
        this.converter = converter;
    }

    @Override
    public void execute() {
        final SosWritableContentCache cache = getCache();
        final String procedure = response.getAssignedProcedure();

        // procedure relations
        cache.addProcedure(procedure);
        cache.addPublishedProcedure(procedure);
        if (request.getProcedureDescription().isSetParentProcedure()) {
            cache.addParentProcedures(procedure,
                    Sets.newHashSet(request.getProcedureDescription().getParentProcedure().getTitleOrFromHref()));
            cache.addPublishedProcedure(request.getProcedureDescription().getParentProcedure().getHref());
        }

        // Update procedureDescriptionFormats
        String procedureDescriptionFormat = request.getProcedureDescriptionFormat();
        Set<String> formats = Sets.newHashSet(procedureDescriptionFormat);
        Set<String> toNamespaceConverterFrom = converter.getToNamespaceConverterFrom(procedureDescriptionFormat);
        if (CollectionHelper.isNotEmpty(toNamespaceConverterFrom)) {
            formats.addAll(toNamespaceConverterFrom);
        }
        getCache().addProcedureDescriptionFormatsForProcedure(procedure, formats);

        // if the inserted procedure is not a type, add values to cache
        if (!request.isType()) {
            // TODO child procedures
            // offerings
            Set<String> childs = new HashSet<>();
            Set<String> parents = new HashSet<>();
            for (SosOffering sosOffering : request.getAssignedOfferings()) {
                if (sosOffering.isParentOffering()) {
                    cache.addHiddenChildProcedureForOffering(sosOffering.getIdentifier(), procedure);
                    parents.add(sosOffering.getIdentifier());
                } else {
                    cache.addOffering(sosOffering.getIdentifier());
                    cache.addPublishedOffering(sosOffering.getIdentifier());
                    cache.addProcedureForOffering(sosOffering.getIdentifier(), procedure);
                    if (sosOffering.isSetName()) {
                        cache.setNameForOffering(sosOffering.getIdentifier(), sosOffering.getOfferingName());
                        cache.addOfferingIdentifierHumanReadableName(sosOffering.getIdentifier(),
                                sosOffering.getOfferingName());
                    }
                    childs.add(sosOffering.getIdentifier());
                }
                if (!parents.isEmpty() && !childs.isEmpty()) {
                    for (String child : childs) {
                        cache.addParentOfferings(child, parents);
                    }
                }
                // add offering for procedure whether it's a normal offering or
                // hidden child
                cache.addOfferingForProcedure(procedure, sosOffering.getIdentifier());

                // allowed observation types
                cache.addAllowedObservationTypesForOffering(sosOffering.getIdentifier(),
                        request.getMetadata().getObservationTypes());
                // allowed featureOfInterest types
                cache.addAllowedFeatureOfInterestTypesForOffering(sosOffering.getIdentifier(),
                        request.getMetadata().getFeatureOfInterestTypes());
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
                cache.addPublishedObservableProperty(observableProperty);
            }
        }
        // add featureOfInterest
        if (request.getProcedureDescription().isSetFeatures()) {
            for (AbstractFeature feature : request.getProcedureDescription().getFeaturesOfInterestMap().values()) {
                if (feature.isSetIdentifier()) {
                    cache.addFeatureOfInterest(feature.getIdentifier());
                    cache.addPublishedFeatureOfInterest(feature.getIdentifier());
                    cache.addFeatureOfInterest(feature.getIdentifier());
                    getCache().setProceduresForFeatureOfInterest(feature.getIdentifier(), Sets.newHashSet(procedure));
                    if (feature.isSetName()) {
                        getCache().addFeatureOfInterestIdentifierHumanReadableName(feature.getIdentifier(),
                                feature.getFirstName().getValue());
                    }
                }
            }
        }

        // procedure type/instance metadata
        if (request.isType()) {
            cache.addTypeInstanceProcedure(SosContentCache.TypeInstance.TYPE, response.getAssignedProcedure());
        } else {
            cache.addTypeInstanceProcedure(SosContentCache.TypeInstance.INSTANCE, response.getAssignedProcedure());
        }
        if (request.getProcedureDescription().isAggregation()) {
            cache.addComponentAggregationProcedure(SosContentCache.ComponentAggregation.AGGREGATION,
                    response.getAssignedProcedure());
        } else {
            cache.addComponentAggregationProcedure(SosContentCache.ComponentAggregation.COMPONENT,
                    response.getAssignedProcedure());
        }
        if (request.getProcedureDescription().isSetTypeOf()) {
            cache.addTypeOfProcedure(request.getProcedureDescription().getTypeOf().getTitle(),
                    response.getAssignedProcedure());
        }
    }
}
