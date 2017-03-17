package org.n52.sos.ds.hibernate.entities.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.n52.sos.ds.hibernate.entities.feature.gmd.ResponsiblePartyEntity;
import org.n52.sos.ds.hibernate.entities.feature.gml.VerticalDatumEntity;
import org.n52.sos.util.CollectionHelper;

public class MonitoringPointContent {

    /* 0..* */
    private List<ResponsiblePartyEntity> relatedParty = new ArrayList<>();
    /* 0..* */
    private List<VerticalDatumEntity> verticalDatum = new ArrayList<>();
    
    public List<ResponsiblePartyEntity> getRelatedParty() {
        return relatedParty;
    }
    
    public void setRelatedParty(Collection<ResponsiblePartyEntity> relatedParty) {
        this.relatedParty.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(relatedParty)) {
            this.relatedParty.addAll(relatedParty);
        }
    }
    
    public void addRelatedParty(Collection<ResponsiblePartyEntity> relatedParty) {
        if (relatedParty != null) {
            this.relatedParty.addAll(relatedParty);
        }
    }
    
    public void addRelatedParty(ResponsiblePartyEntity relatedParty) {
        if (relatedParty != null) {
            this.relatedParty.add(relatedParty);
        }
    }
    
    public boolean hasRelatedParty() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getRelatedParty());
    }
    
    public List<VerticalDatumEntity> getVerticalDatum() {
        return verticalDatum;
    }
    
    public void setVerticalDatum(Collection<VerticalDatumEntity> verticalDatum) {
        this.verticalDatum.clear();
        if (!CollectionHelper.nullEmptyOrContainsOnlyNulls(verticalDatum)) {
            this.verticalDatum.addAll(verticalDatum);
        }
    }
    
    public void addVerticalDatum(Collection<VerticalDatumEntity> verticalDatum) {
        if (verticalDatum != null) {
            this.verticalDatum.addAll(verticalDatum);
        }
    }
    
    public void addVerticalDatum(VerticalDatumEntity verticalDatum) {
        if (verticalDatum != null) {
            this.verticalDatum.add(verticalDatum);
        }
    }
    
    public boolean hasVerticalDatum() {
        return !CollectionHelper.nullEmptyOrContainsOnlyNulls(getVerticalDatum());
    }
}
