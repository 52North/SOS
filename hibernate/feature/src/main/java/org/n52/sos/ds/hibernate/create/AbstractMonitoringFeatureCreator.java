package org.n52.sos.ds.hibernate.create;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.feature.AbstractMonitoringFeature;

public abstract class AbstractMonitoringFeatureCreator<T extends AbstractMonitoringFeature> extends AbstractFeatureOfInerestCreator<T> {

    public AbstractMonitoringFeatureCreator(int storageEPSG, int storage3depsg) {
        super(storageEPSG, storage3depsg);
    }
    
    protected void addMonitoringFeatureData(org.n52.sos.ogc.series.AbstractMonitoringFeature amp, AbstractMonitoringFeature f, Session s) {
        addVerticalDatum(amp, f, s);
        addRelatedParty(amp, f, s);
    }

    
    private void addVerticalDatum(org.n52.sos.ogc.series.AbstractMonitoringFeature amp, AbstractMonitoringFeature f, Session s) {
        // TODO Auto-generated method stub
        
    }
    
    private void addRelatedParty(org.n52.sos.ogc.series.AbstractMonitoringFeature amp, AbstractMonitoringFeature f, Session s) {
        // TODO Auto-generated method stub
        
    }

}
