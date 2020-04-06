# 52°North SOS RELEASE NOTES

 The Sensor Observation Service (SOS) aggregates readings from live sensors as well as 
 sensor archives. The service provides an interface to get the pure data (encoded 
 in Observation&Measurement), information about the sensor itself (encoded in a 
 SensorML instance document), and information about the sensor mounting platform 
 (SensorML as well).

## Release 52n-sensorweb-sos-5.0.2
 
### Change Log

  - add more configuration options for docker
  - add default env variables
  - Fix issue with duplicated and not initialized datasets in the database when inserting complex observations.
  - Set observation identifier = pkid if not defined in request (configurable via setting)
  - Fix issue with duplicate created datasets (only without platform entity) when inserting ComplexObservations.
  
    
### Dependency Updates

  - Bump cargo-maven2-plugin from 1.7.10 to 1.7.11 (#782)
  - Bump mockito-core from 3.3.0 to 3.3.3 (#783)
  - Bump Saxon-HE from 9.9.1-7 to 10.0 (#784)
  - Bump arctic-sea.version from 7.3.0 to 7.3.1 (#786)
  - Bump janino from 3.1.1 to 3.1.2 (#787)
  - Bump spotbugs-annotations from 4.0.0 to 4.0.1 (#788)
  - Bump jettison from 1.4.0 to 1.4.1 (#789)
  - Bump org.springframework.version from 5.2.4.RELEASE to 5.2.5.RELEASE (#790 )
  - Bump postgis-jdbc from 2.4.0 to 2.5.0 (#792)
  - Update mssql jdbc from 8.2.1.jre8 to 8.2.2.jre8
  - Bump org.springframework.security.version from 5.3.0.RELEASE to 5.3.1.RELEASE (#794)
  - Bump arctic-sea.version from 7.3.1 to 7.4.0 (#796)
  - Bump elasticsearch.version from 7.6.1 to 7.6.2 (#797)
  - Bump postgresql from 42.2.11 to 42.2.12 (#798)
  - Bump byte-buddy from 1.10.8 to 1.10.9 (#799)
  - Update hibernate from 5.4.10 to 5.4.13

### Breaking Changes
 
  - 


## Release 52n-sensorweb-sos-5.0.1
 
### Change Log

  - Update urls to the wiki documentation
  - Fix lgtm alert for obsolete instanceof check of data entity
  - Fix checkstyle in sample data properties
  - Fix some warnings of deprecated junit methods
  - add faroe entrypoint
  - change label schema
  - use jetty:jre8-alpine
  - simplify Dockerfile
  - Update logback.xml
  - fix build with debug logging profile and docker logging
    
### Dependency Updates

  - Bump org.springframework.version from 5.2.3.RELEASE to 5.2.4.RELEASE (#762)
  - Bump dataset.hibernate.version from 1.0.0 to 1.1.0 (#764)
  - Bump jackson.version from 2.10.2 to 2.10.3 (#767)
  - Update maven-parent version from 16 to 18
  - Bump version.netcdf from 5.2.0 to 5.3.0 (#769)
  - Bump org.springframework.security.version from 5.2.2.RELEASE to 5.3.0.RELEASE (#770)
  - Bump postgis-jdbc from 2.3.0 to 2.4.0 (#772)
  - Bump Saxon-HE from 9.9.1-6 to 9.9.1-7 (#773)
  - Bump httpmime from 4.5.11 to 4.5.12 (#774)
  - Bump httpclient from 4.5.11 to 4.5.12 (#775)
  - Bump version.netcdf from 5.3.0 to 5.3.1 (#776)
  - Bump janino from 3.1.0 to 3.1.1 (#777)
  - Bump elasticsearch from 7.5.2 to 7.6.1 (#771)
  - Bump postgresql from 42.2.10 to 42.2.11 (#778)
  - Bump build-helper-maven-plugin from 3.0.0 to 3.1.0 (#779 )
  - Bump dataset.hibernate.version from 1.1.0 to 1.2.0

### Breaking Changes

  - 


## Release 52n-sensorweb-sos-5.0.0
 
### New features

  - 
  
### Changes
  
  - Use arctic-sea
  - New database model, harmonized with Sensor Web Server Helgoland and Sensor Web Server STA
  - Use Spring Framework for initializing beans
  - Sensor Web Viewer Helgoland v2
  
### Fixed issues

 - 

For more detailed information about the bugs look at https://github.com/52North/SOS/issues

