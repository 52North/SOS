package org.n52.sos.ds.hibernate.entities.feature;

import java.util.ArrayList;
import java.util.List;

import org.n52.sos.iso.gmd.CiResponsibleParty;
import org.n52.sos.ogc.gml.VerticalDatum;
import org.n52.sos.util.CollectionHelper;

public abstract class AbstractMonitoringFeature extends FeatureOfInterest {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /* 0..* */
    private List<CiResponsibleParty> relatedParty = new ArrayList<>();
    /* 0..* */
    private List<VerticalDatum> verticalDatum = new ArrayList<>();
    
    public List<CiResponsibleParty> getRelatedParty() {
        return relatedParty;
    }
    
    public AbstractMonitoringFeature setRelatedParty(List<CiResponsibleParty> relatedParty) {
        this.relatedParty.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(relatedParty)) {
            this.relatedParty.addAll(relatedParty);
        }
        return this;
    }
    
    public AbstractMonitoringFeature addRelatedParty(List<CiResponsibleParty> relatedParty) {
        if (relatedParty != null) {
            this.relatedParty.addAll(relatedParty);
        }
        return this;
    }
    
    public AbstractMonitoringFeature addRelatedParty(CiResponsibleParty relatedParty) {
        if (relatedParty != null) {
            this.relatedParty.add(relatedParty);
        }
        return this;
    }

    public boolean hasRelatedParty() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getRelatedParty());
    }
    
    public List<VerticalDatum> getVerticalDatum() {
        return verticalDatum;
    }
    
    public AbstractMonitoringFeature setVerticalDatum(List<VerticalDatum> verticalDatum) {
        this.verticalDatum.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(verticalDatum)) {
            this.verticalDatum.addAll(verticalDatum);
        }
        return this;
    }
    
    public AbstractMonitoringFeature addVerticalDatum(List<VerticalDatum> verticalDatum) {
        if (verticalDatum != null) {
            this.verticalDatum.addAll(verticalDatum);
        }
        return this;
    }
    
    public AbstractMonitoringFeature addVerticalDatum(VerticalDatum verticalDatum) {
        if (verticalDatum != null) {
            this.verticalDatum.add(verticalDatum);
        }
        return this;
    }
    
    
    public boolean hasVerticalDatum() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getVerticalDatum());
    }

}
