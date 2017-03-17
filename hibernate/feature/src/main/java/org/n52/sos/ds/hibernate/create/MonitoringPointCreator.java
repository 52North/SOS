package org.n52.sos.ds.hibernate.create;

import java.util.Locale;

import org.hibernate.Session;
import org.n52.sos.ds.hibernate.entities.feature.wml.MonitoringPoint;
import org.n52.sos.ogc.gml.AbstractFeature;
import org.n52.sos.ogc.gml.CodeWithAuthority;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.series.wml.WmlMonitoringPoint;

import com.vividsolutions.jts.geom.Geometry;

public class MonitoringPointCreator extends AbstractMonitoringFeatureCreator<MonitoringPoint> {

    public MonitoringPointCreator(int storageEPSG, int storage3depsg) {
        super(storageEPSG, storage3depsg);
    }

    @Override
    public AbstractFeature create(MonitoringPoint f, Locale i18n, String version, Session s)
            throws OwsExceptionReport {
        AbstractFeature absFeat = createFeature(f, i18n, version, s);
        if (absFeat instanceof WmlMonitoringPoint) {
            WmlMonitoringPoint mp = (WmlMonitoringPoint)absFeat;
            addMonitoringFeatureData(mp, f, s);
        }
        return absFeat;
    }
   
    @Override
    public Geometry createGeometry(MonitoringPoint f, Session s) throws OwsExceptionReport {
        return createGeometryFrom(f, s);
    }

    @Override
    protected AbstractFeature getFeatureType(CodeWithAuthority identifier) {
        return new WmlMonitoringPoint(identifier);
    }

}
