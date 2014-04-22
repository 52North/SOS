package org.n52.sos.ogc.om;

import org.n52.sos.ogc.om.values.Value;
import org.n52.sos.util.StringHelper;

public abstract class AbstractObservationValue<T extends Value<?>> implements ObservationValue<T> {

    private static final long serialVersionUID = -8969234704767943799L;
    
    private String observationID;
    
    private String observationType;
    
    private String observableProperty;
    
    private String tokenSeperator;
    
    private String tupleSeperator;
    
    public void setValuesForResultEncoding(OmObservation observation) {
        setObservationID(observation.getObservationID());
        setObservableProperty(observation.getObservationConstellation().getObservableProperty().getIdentifier());
        setObservationType(observation.getObservationConstellation().getObservationType());
        setTokenSeperator(observation.getTokenSeparator());
        setTupleSeperator(observation.getTupleSeparator());
    }

    /**
     * @return the observationID
     */
    public String getObservationID() {
        return observationID;
    }

    /**
     * @param observationID the observationID to set
     */
    private void setObservationID(String observationID) {
        this.observationID = observationID;
    }
    
    public boolean isSetObservationID() {
        return StringHelper.isNotEmpty(getObservationID());
    }

    /**
     * @return the observationType
     */
    public String getObservationType() {
        return observationType;
    }

    /**
     * @param observationType the observationType to set
     */
    private void setObservationType(String observationType) {
        this.observationType = observationType;
    }
    
    public boolean isSetObservationType() {
        return StringHelper.isNotEmpty(getObservationType());
    }

    /**
     * @return the observableProperty
     */
    public String getObservableProperty() {
        return observableProperty;
    }

    /**
     * @param observableProperty the observableProperty to set
     */
    private void setObservableProperty(String observableProperty) {
        this.observableProperty = observableProperty;
    }
    
    public boolean isSetObservablePropertyD() {
        return StringHelper.isNotEmpty(getObservableProperty());
    }

    /**
     * @return the tokenSeperator
     */
    public String getTokenSeperator() {
        return tokenSeperator;
    }

    /**
     * @param tokenSeperator the tokenSeperator to set
     */
    private void setTokenSeperator(String tokenSeperator) {
        this.tokenSeperator = tokenSeperator;
    }
    
    public boolean isSetTokenSeperator() {
        return StringHelper.isNotEmpty(getTokenSeperator());
    }

    /**
     * @return the tupleSeperator
     */
    public String getTupleSeperator() {
        return tupleSeperator;
    }

    /**
     * @param tupleSeperator the tupleSeperator to set
     */
    private void setTupleSeperator(String tupleSeperator) {
        this.tupleSeperator = tupleSeperator;
    }
    
    public boolean isSetTupleSeperator() {
        return StringHelper.isNotEmpty(getTupleSeperator());
    }

}
