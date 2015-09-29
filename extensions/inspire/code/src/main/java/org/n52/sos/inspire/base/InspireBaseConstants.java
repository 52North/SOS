package org.n52.sos.inspire.base;

import org.n52.sos.w3c.SchemaLocation;

public interface InspireBaseConstants {

    String NS_BASE_30 = "http://inspire.ec.europa.eu/schemas/base/3.3";
    
    String NS_BASE_PREFIX = "base";
    
    String SCHEMA_LOCATION_URL_BASE = "http://inspire.ec.europa.eu/schemas/base/3.3/BaseTypes.xsd";
    
    SchemaLocation BASE_SCHEMA_LOCATION = new SchemaLocation(NS_BASE_30, SCHEMA_LOCATION_URL_BASE);
}
