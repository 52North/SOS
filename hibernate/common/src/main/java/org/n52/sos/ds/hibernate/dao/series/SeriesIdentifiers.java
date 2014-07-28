package org.n52.sos.ds.hibernate.dao.series;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.series.Series;

public class SeriesIdentifiers extends AbstractSeriesIdentifiers {
    
    private FeatureOfInterest featureOfInterest;

    private ObservableProperty observableProperty;

    private Procedure procedure;
    
    public SeriesIdentifiers() {
        
    }

    /**
     * @return the featureOfInterest
     */
    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * @param featureOfInterest
     *            the featureOfInterest to set
     */
    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }
    
    public boolean isSetFeatureOfInterest() {
        return getFeatureOfInterest() != null;
    }

    /**
     * @return the observableProperty
     */
    public ObservableProperty getObservableProperty() {
        return observableProperty;
    }

    /**
     * @param observableProperty
     *            the observableProperty to set
     */
    public void setObservableProperty(ObservableProperty observableProperty) {
        this.observableProperty = observableProperty;
    }
    
    public boolean isSetObservableProperty() {
        return getObservableProperty() != null;
    }
    
    /**
     * @return the procedure
     */
    public Procedure getProcedure() {
        return procedure;
    }

    /**
     * @param procedure
     *            the procedure to set
     */
    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }
    
    public boolean isSetProcedure() {
        return getProcedure() != null;
    }
    
    public void addIdentifierRestrictionsToCritera(Criteria c) {
        if (isSetFeatureOfInterest()) {
            addFeatureOfInterestToCriteria(c, getFeatureOfInterest());
        }
        if (isSetObservableProperty()) {
            addObservablePropertyToCriteria(c, getObservableProperty());
        }
        if (isSetProcedure()) {
            addProcedureToCriteria(c, getProcedure());
        }
    }
    
    public void addValuesToSeries(Series series) {
        if (isSetFeatureOfInterest()) {
            series.setFeatureOfInterest(getFeatureOfInterest());
        }
        if (isSetObservableProperty()) {
            series.setObservableProperty(getObservableProperty());
        }
        if (isSetProcedure()) {
            series.setProcedure(getProcedure());
        }
    }
    
    /**
     * Add featureOfInterest restriction to Hibernate Criteria
     * 
     * @param c
     *            Hibernate Criteria to add restriction
     * @param feature
     *            FeatureOfInterest to add
     */
    private void addFeatureOfInterestToCriteria(Criteria c, FeatureOfInterest feature) {
        c.add(Restrictions.eq(Series.FEATURE_OF_INTEREST, feature));
    }
    
    /**
     * Add observedProperty restriction to Hibernate Criteria
     * 
     * @param c
     *            Hibernate Criteria to add restriction
     * @param observedProperty
     *            ObservableProperty to add
     */
    private void addObservablePropertyToCriteria(Criteria c, ObservableProperty observedProperty) {
        c.add(Restrictions.eq(Series.OBSERVABLE_PROPERTY, observedProperty));
    }
    
    /**
     * Add procedure restriction to Hibernate Criteria
     * 
     * @param c
     *            Hibernate Criteria to add restriction
     * @param procedure
     *            Procedure to add
     */
    private void addProcedureToCriteria(Criteria c, Procedure procedure) {
        c.add(Restrictions.eq(Series.PROCEDURE, procedure));

    }

}
