package org.n52.sos.ogc.om;

public class ObservationMergeIndicator {
    
    private boolean procedure = true;
    
    private boolean observableProperty = true;
    
    private boolean featureOfInterest = true;
    
    private boolean offerings = true;
    
    private boolean phenomenonTime = false;
    
    private boolean samplingGeometry = false;
    
    
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

    /**
     * @return the phenomenonTime
     */
    public boolean isPhenomenonTime() {
        return phenomenonTime;
    }

    /**
     * @param phenomenonTime the phenomenonTime to set
     */
    public void setPhenomenonTime(boolean phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    /**
     * @return the samplingGeometry
     */
    public boolean isSamplingGeometry() {
        return samplingGeometry;
    }

    /**
     * @param samplingGeometry the samplingGeometry to set
     */
    public void setSamplingGeometry(boolean samplingGeometry) {
        this.samplingGeometry = samplingGeometry;
    }
    

}
