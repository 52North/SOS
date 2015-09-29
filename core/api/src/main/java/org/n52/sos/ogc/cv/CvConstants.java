package org.n52.sos.ogc.cv;

import org.n52.sos.w3c.SchemaLocation;

public interface CvConstants {
    
    String NS_CV = "http://www.opengis.net/cv/0.2/gml32";
            
    String NS_CV_PREFIX = "cv";
        
    String SCHEMA_LOCATION_URL_CV = "http://bp.schemas.opengis.net/06-188r2/cv/0.2.2_gml32/cv.xsd";
    
    SchemaLocation CV_SCHEMA_LOCATION = new SchemaLocation(NS_CV, SCHEMA_LOCATION_URL_CV);

}
