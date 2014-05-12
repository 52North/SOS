package org.n52.sos.ds.hibernate.entities.values;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.n52.sos.ds.hibernate.entities.FeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.ObservableProperty;
import org.n52.sos.ds.hibernate.entities.Offering;
import org.n52.sos.ds.hibernate.entities.Procedure;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasDeletedFlag;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasFeatureOfInterest;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasObservableProperty;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasOfferings;
import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasProcedure;

public class ObservationValueTime implements Serializable, HasProcedure, HasObservableProperty, HasFeatureOfInterest, HasDeletedFlag, HasOfferings{
    
    private static final long serialVersionUID = 1148485825119584513L;

    public static final String ID = "observationId";

    public static final String PHENOMENON_TIME_START = "phenomenonTimeStart";

    public static final String PHENOMENON_TIME_END = "phenomenonTimeEnd";

    public static final String VALID_TIME_START = "validTimeStart";

    public static final String VALID_TIME_END = "validTimeEnd";

    public static final String RESULT_TIME = "resultTime";
    
    private long observationId; 

    private Procedure procedure;
    
    private Date phenomenonTimeStart;

    private Date phenomenonTimeEnd;

    private Date resultTime;

    private Date validTimeStart;

    private Date validTimeEnd;
    
    private Set<Offering> offerings = new HashSet<Offering>(0);
    
    private boolean deleted;
    
    private ObservableProperty observableProperty;
    
    private FeatureOfInterest featureOfInterest;
    
    /**
     * Get the observation id
     * 
     * @return Observation id
     */
    public long getObservationId() {
        return observationId;
    }

    /**
     * Set the observation id
     * 
     * @param observationId
     *            Observation id to set
     */
    public void setObservationId(final long observationId) {
        this.observationId = observationId;
    }

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
    
    /**
     * Get the start phenomenon time
     * 
     * @return Start phenomenon time
     */
    public Date getPhenomenonTimeStart() {
        return phenomenonTimeStart;
    }

    /**
     * Set the start phenomenon time
     * 
     * @param phenomenonTimeStart
     *            Start phenomenon time to set
     */
    public void setPhenomenonTimeStart(final Date phenomenonTimeStart) {
        this.phenomenonTimeStart = phenomenonTimeStart;
    }

    /**
     * Get the end phenomenon time
     * 
     * @return End phenomenon time
     */
    public Date getPhenomenonTimeEnd() {
        return phenomenonTimeEnd;
    }

    /**
     * Set the end phenomenon time
     * 
     * @param phenomenonTimeEnd
     *            End phenomenon time to set
     */
    public void setPhenomenonTimeEnd(final Date phenomenonTimeEnd) {
        this.phenomenonTimeEnd = phenomenonTimeEnd;
    }

    /**
     * Get the result time
     * 
     * @return Result time
     */
    public Date getResultTime() {
        return resultTime;
    }

    /**
     * Set the result tiem
     * 
     * @param resultTime
     *            Result tiem to set
     */
    public void setResultTime(final Date resultTime) {
        this.resultTime = resultTime;
    }

    /**
     * Get the start valid time
     * 
     * @return Start valid time
     */
    public Date getValidTimeStart() {
        return validTimeStart;
    }

    /**
     * Set the start valid time
     * 
     * @param validTimeStart
     *            Start valid time to set
     */
    public void setValidTimeStart(final Date validTimeStart) {
        this.validTimeStart = validTimeStart;
    }

    /**
     * Get the end valid time
     * 
     * @return End valid time
     */
    public Date getValidTimeEnd() {
        return validTimeEnd;
    }

    /**
     * Set the end valid time
     * 
     * @param validTimeEnd
     *            End valid time to set
     */
    public void setValidTimeEnd(final Date validTimeEnd) {
        this.validTimeEnd = validTimeEnd;
    }

    @Override
    public HasDeletedFlag setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public Set<Offering> getOfferings() {
        return offerings;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setOfferings(final Object offerings) {
        if (offerings instanceof Set<?>) {
            this.offerings = (Set<Offering>) offerings;
        } else {
            getOfferings().add((Offering) offerings);
        }
    }

}
