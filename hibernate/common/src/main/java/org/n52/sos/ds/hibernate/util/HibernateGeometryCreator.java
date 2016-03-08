package org.n52.sos.ds.hibernate.util;

import org.n52.sos.ds.hibernate.entities.HibernateRelations.HasCoordinate;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.util.GeometryHandler;
import org.n52.sos.util.JTSHelper;
import org.n52.sos.util.JavaHelper;

import com.vividsolutions.jts.geom.Geometry;

public class HibernateGeometryCreator {
    
    private int storageEPSG = GeometryHandler.getInstance().getStorageEPSG();
    private int storage3depsg =  GeometryHandler.getInstance().getStorage3DEPSG();

    public HibernateGeometryCreator() {
        
    }
    
    public HibernateGeometryCreator(int storageEPSG, int storage3DEPSG) {
        this.storageEPSG = storageEPSG;
        this.storage3depsg = storage3DEPSG;
    }
    
    private int getStorageEPSG() {
        return storageEPSG;
    }

    private int getStorage3DEPSG() {
        return storage3depsg;
    }

    /**
     * Get the geometry from featureOfInterest object.
     *
     * @param feature
     * @return geometry
     * @throws OwsExceptionReport
     */
    public Geometry createGeometry(final HasCoordinate coodinates) throws OwsExceptionReport {
      if (coodinates.isSetLongLat()) {
            int epsg = getStorageEPSG();
            if (coodinates.isSetSrid()) {
                epsg = coodinates.getSrid();
            }
            final String wktString =
                    GeometryHandler.getInstance().getWktString(coodinates.getLongitude(), coodinates.getLatitude(), epsg);
            final Geometry geom = JTSHelper.createGeometryFromWKT(wktString, epsg);
            if (coodinates.isSetAltitude()) {
                geom.getCoordinate().z = JavaHelper.asDouble(coodinates.getAltitude());
                if (geom.getSRID() == getStorage3DEPSG()) {
                    geom.setSRID(getStorage3DEPSG());
                }
            }
            return geom;
        }
        return null;
    }
    
}
