package org.n52.sos.ogc.om;

import java.util.Set;

import org.n52.sos.util.CollectionHelper;

import com.google.common.collect.Sets;

public class ObservationMergeIndicator {
    
    private boolean procedure = true;
    
    private boolean observableProperty = true;
    
    private boolean featureOfInterest = true;
    
    private boolean offerings = true;
    
    private Set<String> additionalMergeIndicators = Sets.newHashSet(); 
    
    /**
     * @return the procedure
     */
    public boolean isProcedure() {
        return procedure;
    }

    /**
     * @param procedure the procedure to set
     */
    public ObservationMergeIndicator setProcedure(boolean procedure) {
        this.procedure = procedure;
        return this;
    }

    /**
     * @return the observableProperty
     */
    public boolean isObservableProperty() {
        return observableProperty;
    }

    /**
     * @param observableProperty the observableProperty to set
     */
    public ObservationMergeIndicator setObservableProperty(boolean observableProperty) {
        this.observableProperty = observableProperty;
        return this;
    }

    /**
     * @return the featureOfInterest
     */
    public boolean isFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * @param featureOfInterest the featureOfInterest to set
     */
    public ObservationMergeIndicator setFeatureOfInterest(boolean featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        return this;
    }
    
    /**
     * @return the offerings
     */
    public boolean isOfferings() {
        return offerings;
    }

    /**
     * @param offerings the offerings to set
     */
    public void setOfferings(boolean offerings) {
        this.offerings = offerings;
    }

    public boolean sameObservationConstellation() {
        return isProcedure() && isObservableProperty() && isFeatureOfInterest() && isOfferings();
    }
    
    public ObservationMergeIndicator setAdditionalMergeIndicators(Set<String> additionalMergeIndicators) {
        this.additionalMergeIndicators.clear();
        this.additionalMergeIndicators.addAll(additionalMergeIndicators);
        return this;
    }
    
    public ObservationMergeIndicator addAdditionalMergeIndicator(String additionalMergeIndicator) {
        this.additionalMergeIndicators.add(additionalMergeIndicator);
        return this;
    }

    public Set<String> getAdditionalMergeIndicators() {
        return additionalMergeIndicators;
    }

    public boolean isSetAdditionalMergeIndicators() {
        return CollectionHelper.isNotEmpty(getAdditionalMergeIndicators());
    }
    
    public boolean hasAdditionalMergeIndicator(String additionalMergeIndicator) {
       return getAdditionalMergeIndicators().contains(additionalMergeIndicator);
    }
    

}
