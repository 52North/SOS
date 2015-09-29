package org.n52.sos.inspire.omso;


import org.n52.sos.w3c.SchemaLocation;

public interface InspireOMSOConstants {
    
    String NS_OMSO_30 = "http://inspire.ec.europa.eu/schemas/omso/3.0";
    
    String NS_OMSO_PREFIX = "omso";
    
    String SCHEMA_LOCATION_URL_OMSO = "http://inspire.ec.europa.eu/schemas/omso/3.0/SpecialisedObservations.xsd";
    
    SchemaLocation OMSO_SCHEMA_LOCATION = new SchemaLocation(NS_OMSO_30, SCHEMA_LOCATION_URL_OMSO);
    
    // observation types
    String OBS_TYPE_POINT_OBSERVATION = "PointObservation";
    
    String OBS_TYPE_POINT_TIME_SERIES_OBSERVATION = "PointTimeSeriesObservation";
    
    String OBS_TYPE_MULTI_POINT_OBSERVATION = "MultiPointObservation";
    
    String OBS_TYPE_PROFILE_OBSERVATION = "ProfileObservation";
    
    String OBS_TYPE_TRAJECTORY_OBSERVATION = "TrajectoryObservation";

}
