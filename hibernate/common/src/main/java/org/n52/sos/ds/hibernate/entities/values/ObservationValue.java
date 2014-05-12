package org.n52.sos.ds.hibernate.entities.values;


import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedure;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservableProperty;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasFeatureOfInterest;

public class ObservationValue extends AbstractValue implements HasProcedure, HasObservableProperty, HasFeatureOfInterest {
    
    private Procedure procedure;
    
    private ObservableProperty observableProperty;
    
    private FeatureOfInterest featureOfInterest;

    @Override
    public Procedure getProcedure() {
        return procedure;
    }

    @Override
    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    @Override
    public ObservableProperty getObservableProperty() {
        return observableProperty;
    }

    @Override
    public void setObservableProperty(ObservableProperty observableProperty) {
        this.observableProperty = observableProperty;
    }

    @Override
    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    @Override
    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

}
