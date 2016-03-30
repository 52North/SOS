package org.n52.sos.inspire.ompr;

import org.n52.sos.w3c.SchemaLocation;

public interface InspireOMPRConstants {
    
    String NS_OMPR_30 = "http://inspire.ec.europa.eu/schemas/ompr/3.0";
    
    String NS_OMPR_PREFIX = "ompr";
    
    String SCHEMA_LOCATION_URL_OMPR = "http://inspire.ec.europa.eu/schemas/ompr/3.0/Process.xsd";
    
    SchemaLocation OMPR_SCHEMA_LOCATION = new SchemaLocation(NS_OMPR_30, SCHEMA_LOCATION_URL_OMPR);

}
