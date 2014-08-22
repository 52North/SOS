package org.n52.sos.ds.hibernate.dao.observation;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasWriteableObservationContext;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Procedure;

/**
 * Class to carry observation identifiers (featureOfInterest,
 * observableProperty, procedure).
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * @since 4.0.0
 *
 */
public class ObservationContext {
    private FeatureOfInterest featureOfInterest;
    private ObservableProperty observableProperty;
    private Procedure procedure;

    /**
     * @return the featureOfInterest
     */
    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }

    /**
     * @param featureOfInterest
     *                          the featureOfInterest to set
     */
    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * @return the observableProperty
     */
    public ObservableProperty getObservableProperty() {
        return observableProperty;
    }

    /**
     * @param observableProperty
     *                           the observableProperty to set
     */
    public void setObservableProperty(ObservableProperty observableProperty) {
        this.observableProperty = observableProperty;
    }

    /**
     * @return the procedure
     */
    public Procedure getProcedure() {
        return procedure;
    }

    /**
     * @param procedure
     *                  the procedure to set
     */
    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public boolean isSetFeatureOfInterest() {
        return getFeatureOfInterest() != null;
    }

    public boolean isSetObservableProperty() {
        return getObservableProperty() != null;
    }

    public boolean isSetProcedure() {
        return getProcedure() != null;
    }

    public void addIdentifierRestrictionsToCritera(Criteria criteria) {
        if (isSetFeatureOfInterest()) {
            criteria.add(Restrictions
                    .eq(HasWriteableObservationContext.FEATURE_OF_INTEREST,
                        getFeatureOfInterest()));
        }
        if (isSetObservableProperty()) {
            criteria.add(Restrictions
                    .eq(HasWriteableObservationContext.OBSERVABLE_PROPERTY,
                        getObservableProperty()));
        }
        if (isSetProcedure()) {
            criteria.add(Restrictions
                    .eq(HasWriteableObservationContext.PROCEDURE,
                        getProcedure()));
        }
    }

    public void addValuesToSeries(HasWriteableObservationContext contextual) {
        if (isSetFeatureOfInterest()) {
            contextual.setFeatureOfInterest(getFeatureOfInterest());
        }
        if (isSetObservableProperty()) {
            contextual.setObservableProperty(getObservableProperty());
        }
        if (isSetProcedure()) {
            contextual.setProcedure(getProcedure());
        }
    }
}
