package org.n52.sos.inspire.omor;

import org.n52.sos.w3c.SchemaLocation;

public interface InspireOMORConstants {

    String NS_OMOR_30 = "http://inspire.ec.europa.eu/schemas/omor/3.0";
    
    String NS_OMOR_PREFIX = "omor";
    
    String SCHEMA_LOCATION_URL_OMOR = "http://inspire.ec.europa.eu/schemas/omor/3.0/ObservationReferences.xsd";
    
    SchemaLocation OMOR_SCHEMA_LOCATION = new SchemaLocation(NS_OMOR_30, SCHEMA_LOCATION_URL_OMOR);
}
