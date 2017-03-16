package org.n52.sos.ds.hibernate.entities.feature;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.n52.sos.ds.hibernate.entities.feature.gmd.ResponsiblePartyEntity;
import org.n52.sos.ds.hibernate.entities.feature.gml.VerticalDatumEntity;

public abstract class AbstractMonitoringFeature extends FeatureOfInterest {

    private static final long serialVersionUID = 1L;
    
    private MonitoringPointContent content;
    
    /**
     * @return the content
     */
    public MonitoringPointContent getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(MonitoringPointContent content) {
        this.content = content;
    }
    
    public boolean isSetContent() {
        return getContent() != null;
    }

    public List<ResponsiblePartyEntity> getRelatedParty() {
        if (isSetContent()) {
            return getContent().getRelatedParty();
        }
        return Collections.emptyList();
    }
    
    public AbstractMonitoringFeature setRelatedParty(Collection<ResponsiblePartyEntity> relatedParty) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().setRelatedParty(relatedParty);
        return this;
    }
    
    public AbstractMonitoringFeature addRelatedParty(Collection<ResponsiblePartyEntity> relatedParty) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().addRelatedParty(relatedParty);
        return this;
    }
    
    public AbstractMonitoringFeature addRelatedParty(ResponsiblePartyEntity relatedParty) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().addRelatedParty(relatedParty);
        return this;
    }
    
    public boolean hasRelatedParty() {
        return isSetContent() ? getContent().hasRelatedParty() : false;
    }
    
    public List<VerticalDatumEntity> getVerticalDatum() {
        if (isSetContent()) {
            return getContent().getVerticalDatum();
        }
        return Collections.emptyList();
    }
    
    public AbstractMonitoringFeature setVerticalDatum(Collection<VerticalDatumEntity> verticalDatum) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().setVerticalDatum(verticalDatum);
        return this;
    }
    
    public AbstractMonitoringFeature addVerticalDatum(Collection<VerticalDatumEntity> verticalDatum) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().addVerticalDatum(verticalDatum);
        return this;
    }
    
    public AbstractMonitoringFeature addVerticalDatum(VerticalDatumEntity verticalDatum) {
        if (!isSetContent()) {
            setContent(new MonitoringPointContent());
        }
        getContent().addVerticalDatum(verticalDatum);
        return this;
    }
    
    public boolean hasVerticalDatum() {
        return isSetContent() ? getContent().hasVerticalDatum() : false;
    }
}
