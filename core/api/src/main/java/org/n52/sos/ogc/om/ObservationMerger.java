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
package org.n52.sos.ogc.om;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Lists;

public class ObservationMerger {
    
    public List<OmObservation> mergeObservations(Collection<OmObservation> observations, ObservationMergeIndicator observationMergeIndicator) {
        if (CollectionHelper.isNotEmpty(observations)) {
            final List<OmObservation> mergedObservations = new LinkedList<OmObservation>();
            int obsIdCounter = 1;
            for (final OmObservation sosObservation : observations) {
                if (mergedObservations.isEmpty()) {
                    if (!sosObservation.isSetGmlID()) {
                        sosObservation.setObservationID(Integer.toString(obsIdCounter++));
                    }
                    mergedObservations.add(sosObservation);
                } else {
                    boolean combined = false;
                    for (final OmObservation combinedSosObs : mergedObservations) {
                        if (checkForMerge(combinedSosObs, sosObservation, observationMergeIndicator)) {
//                        if (combinedSosObs.checkForMerge(sosObservation)) {
                            combinedSosObs.setResultTime(null);
                            combinedSosObs.mergeWithObservation(sosObservation);
                            combined = true;
                            break;
                        }
                    }
                    if (!combined) {
                        mergedObservations.add(sosObservation);
                    }
                }
            }
            return mergedObservations;
        }
        return Lists.newArrayList(observations);
    }
    
    public List<OmObservation> mergeObservations(Collection<OmObservation> observations) {
        return mergeObservations(observations, new ObservationMergeIndicator());
    }
    
    public OmObservation mergeObservations(OmObservation observation, OmObservation observationToAdd, ObservationMergeIndicator observationMergeIndicator) {
        if (checkForMerge(observation, observationToAdd, observationMergeIndicator)) {
            observation.setResultTime(null);
            observation.mergeWithObservation(observationToAdd);
            return observation;
        }
        return observation;
    }

    public OmObservation mergeObservations(OmObservation observation, OmObservation observationToAdd) {
        return mergeObservations(observation, observationToAdd, new ObservationMergeIndicator());
    }
    
    protected boolean checkForMerge(OmObservation observation, OmObservation observationToAdd, ObservationMergeIndicator observationMergeIndicator) {
        boolean merge = true;
        if (observation.isSetAdditionalMergeIndicator() && observationToAdd.isSetAdditionalMergeIndicator()) {
            merge = observation.getAdditionalMergeIndicator().equals(observationToAdd.getAdditionalMergeIndicator());
        } else if ((observation.isSetAdditionalMergeIndicator() && !observationToAdd.isSetAdditionalMergeIndicator())
                || (!observation.isSetAdditionalMergeIndicator() && observationToAdd.isSetAdditionalMergeIndicator())) {
            merge = false;
        }
        if (observationMergeIndicator.isCheckObservationType()) {
            merge = merge && checkObservationTypeForMerging(observationToAdd.getObservationConstellation());
        }
        if (observationMergeIndicator.sameObservationConstellation()) {
            merge = merge && observation.getObservationConstellation().equals(observationToAdd.getObservationConstellation());
        } else {
            if (observationMergeIndicator.isProcedure()) {
                merge = merge && checkForProcedure(observation, observationToAdd);
            }
            if (observationMergeIndicator.isObservableProperty()) {
                merge = merge && checkForObservableProperty(observation, observationToAdd);
            }
            if (observationMergeIndicator.isFeatureOfInterest()) {
                merge = merge && checkForFeatureOfInterest(observation, observationToAdd);
            }
            if (observationMergeIndicator.isOfferings()) {
                merge = merge && checkForOfferings(observation, observationToAdd);
            }
        }
        
        if (observationMergeIndicator.isPhenomenonTime()) {
            merge = merge && checkForPhenomenonTime(observation, observationToAdd);
        }
        if (observationMergeIndicator.isSetResultTime()) {
            merge = merge && checkForResultTime(observation, observationToAdd);
        }
        if (observationMergeIndicator.isSamplingGeometry()) {
            merge = merge && checkForSamplingGeometry(observation, observationToAdd);
        }
        return merge;
        
    }

    protected boolean checkForProcedure(OmObservation observation, OmObservation observationToAdd) {
        return observation.getObservationConstellation().getProcedure().equals(observationToAdd.getObservationConstellation().getProcedure());
    }

    protected boolean checkForObservableProperty(OmObservation observation, OmObservation observationToAdd) {
        return observation.getObservationConstellation().getObservableProperty().equals(observationToAdd.getObservationConstellation().getObservableProperty());
    }

    protected boolean checkForFeatureOfInterest(OmObservation observation, OmObservation observationToAdd) {
        return observation.getObservationConstellation().getFeatureOfInterest().equals(observationToAdd.getObservationConstellation().getFeatureOfInterest());
    }

    protected boolean checkForOfferings(OmObservation observation, OmObservation observationToAdd) {
        return observation.getObservationConstellation().getOfferings().equals(observationToAdd.getObservationConstellation().getOfferings());
    }

    protected boolean checkForPhenomenonTime(OmObservation observation, OmObservation observationToAdd) {
        return observation.getPhenomenonTime().equals(observationToAdd.getPhenomenonTime());
    }
    
    protected boolean checkForResultTime(OmObservation observation, OmObservation observationToAdd) {
        return observation.getResultTime().equals(observationToAdd.getResultTime());
    }
    
    protected boolean checkForSamplingGeometry(OmObservation observation, OmObservation observationToAdd) {
        if (observation.isSetSpatialFilteringProfileParameter() && observationToAdd.isSetSpatialFilteringProfileParameter()) {
            // TODO check for NULL
            return observation.getSpatialFilteringProfileParameter().getValue().getValue().equals(observationToAdd.getSpatialFilteringProfileParameter().getValue().getValue());
        }
        return false;
    }

    /**
     * TODO change if currently not supported types could be merged.
     * 
     * @return <code>true</code>, if the observation can be merged
     */
    protected boolean checkObservationTypeForMerging(OmObservationConstellation observationConstellation) {
        return (observationConstellation.isSetObservationType() && !OmConstants.OBS_TYPE_SWE_ARRAY_OBSERVATION.equals(observationConstellation.getObservationType())
                && !OmConstants.OBS_TYPE_COMPLEX_OBSERVATION.equals(observationConstellation.getObservationType())
                && !OmConstants.OBS_TYPE_OBSERVATION.equals(observationConstellation.getObservationType())
                && !OmConstants.OBS_TYPE_UNKNOWN.equals(observationConstellation.getObservationType()));
    }
    
}
