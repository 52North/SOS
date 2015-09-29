package org.n52.sos.ogc.om;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.n52.sos.util.CollectionHelper;

public class ObservationMerger {
    
    public List<OmObservation> mergeObservations(List<OmObservation> observations, ObservationMergeIndicator observationMergeIndicator) {
        if (CollectionHelper.isNotEmpty(observations)) {
            final List<OmObservation> mergedObservations = new LinkedList<OmObservation>();
            int obsIdCounter = 1;
            for (final OmObservation sosObservation : observations) {
                if (mergedObservations.isEmpty()) {
                    sosObservation.setObservationID(Integer.toString(obsIdCounter++));
                    mergedObservations.add(sosObservation);
                } else {
                    boolean combined = false;
                    for (final OmObservation combinedSosObs : mergedObservations) {
                        if (combinedSosObs.checkForMerge(sosObservation)) {
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
        return observations;
    }
    
    public List<OmObservation> mergeObservations(List<OmObservation> observations) {
        return mergeObservations(observations, null);
    }
    
    public OmObservation mergeObservations(OmObservation observation, OmObservation observationToAdd, ObservationMergeIndicator observationMergeIndicator) {
        return null;
    }

    public OmObservation mergeObservations(OmObservation observation, OmObservation observationToAdd) {
        return mergeObservations(observation, observationToAdd, null);
    }
    
    protected boolean checkForMerge(OmObservation observation, OmObservation observationToAdd, ObservationMergeIndicator observationMergeIndicator) {
        boolean merge = true;
        if (observation.isSetAdditionalMergeIndicator() && observationToAdd.isSetAdditionalMergeIndicator()) {
            merge = observation.getAdditionalMergeIndicator().equals(observationToAdd.getAdditionalMergeIndicator());
        } else if ((observation.isSetAdditionalMergeIndicator() && !observationToAdd.isSetAdditionalMergeIndicator())
                || (!observation.isSetAdditionalMergeIndicator() && observationToAdd.isSetAdditionalMergeIndicator())) {
            merge = false;
        }
        merge = merge && checkObservationTypeForMerging(observation.getObservationConstellation());
        if (observationMergeIndicator.sameObservationConstellation()) {
            return observation.getObservationConstellation().equals(observationToAdd.getObservationConstellation()) && merge;
        }
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
        if (observationMergeIndicator.isSetAdditionalMergeIndicators()) {
            merge = merge && checkForAdditionalMergeIndicators(observation, observationToAdd, observationMergeIndicator.getAdditionalMergeIndicators());
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

    protected boolean checkForAdditionalMergeIndicators(OmObservation observation, OmObservation observationToAdd,
            Set<String> additionalMergeIndicators) {
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
