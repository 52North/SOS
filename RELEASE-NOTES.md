# 52°North SOS RELEASE NOTES

 The Sensor Observation Service (SOS) aggregates readings from live sensors as well as 
 sensor archives. The service provides an interface to get the pure data (encoded 
 in Observation&Measurement), information about the sensor itself (encoded in a 
 SensorML instance document), and information about the sensor mounting platform 
 (SensorML as well).

## Release 52n-sensorweb-sos-4.4.14
 
### New features

  - Add Helgoland v2 to SOS as a second Viewer
  
### Changes
  
  - Move check for duplicate observation identifier to check for duplicity in observation persister
  - Remove duplicate lib versions
  - Add index on observation.identifier column
  
### Fixed issues

  - Issue #731: docker-compose/nginx-proxy.conf is invalid

## Release 52n-sensorweb-sos-4.4.13
 
### New features

  - 
  
### Changes
  
  - Remove obsolete Oracle notes in Streaming settings.
  - Update geotools version to 19.4
  
### Fixed issues

  - Fix Admin -> Settings -> Datasource call in combination with Oracle datasource
  - Fix missing of first observation value when using scrollable result streaming.
  - Check blocks in InsertResult for duplicity to avoid duplicate observations.
  
 
## Release 52n-sensorweb-sos-4.4.12
## Release 52n-sensorweb-sos-4.4.11
 
### New features

  - Support insertion of gml:identifier/name/description of observations via InsertResult
  
### Changes
  
  - Remove obsolete restulTime set mapping from datasource because the resultTimes are queried separately via the /extras path.
  - Update REST-API with performance improvements (only add and join parameter if required, query concrete data entities for dataset)
  
### Fixed issues

  - Fix insertion of om:parameter values via InsertResult operation
  - Fix insertion of null/empty values via InsertResult operation
  - Fix insertion of profile observations via InsertResult without a geometry
  
  
## Release 52n-sensorweb-sos-4.4.10
 
### New features

  - 
  
### Changes
  
  - Add proceudre name to sml:identifications short- and longName.
  - Add seriesid property (with insert/update = false) to observation mappings to avoid a join to the series table when querying observations by seriesid.
  
### Fixed issues

  - Add missing position to SensorML 2.0 when generating the procedure description
  
  
## Release 52n-sensorweb-sos-4.4.9
 
### New features

  - Added Oracle support for querying the geometry type in the database.
  
### Changes
  
  - 
  
### Fixed issues

  - 
  
  
## Release 52n-sensorweb-sos-4.4.8
 
### New features

  - Add feature to not abort the insertion of observations via InsertResult whether some observations already exist in the database.
  
### Changes
  
  - 
  
### Fixed issues

  - Issue #653: Capabilities SettingsBETA
  - Issue #655: Request GetFeatureOfInterest is not validated with streaming options
  - Encoding of INSPIRE download service eextension
  
  
## Release 52n-sensorweb-sos-4.4.7
 
### New features

  - 
  
### Changes
  
  - 
  
### Fixed issues

  - Cache update of result templates with dynamic featureOfInterest definition
  - NPE when a series with no observations exist, the next values check fails because the session is closed
 
## Release 52n-sensorweb-sos-4.4.6
 
### New features

  - Dockerfile to build docker images
  
### Changes
  
  - Store cache.tmp in /tmp folder
  - Store datasource.properties, configuration.db and eReportingHeader.db in /conf folder.
  
### Fixed issues

  - NPE in SosOffering constructor with name as CodeType

## Release 52n-sensorweb-sos-4.4.5
 
### New features

  - Support for Category insertion (used by the Sensor Web REST-API): 
        Supports the creation of category table and relation in the series table which is selectable during the installation.
  - Create offering hierarchy when the inserted procedure is attached to a parent procedure.
  
### Changes
  
  - Performance improvement for cache update when samplingGeometries are defined.
  - Do no use a new session for getting i18n data when a session is still in use.
  
### Fixed issues

  - NPE when removing featureOfInterest for offerings in the cache.
  - Set end time in validproceduredescription table when deleting the procedure.
  - Fixes #643: Issue when using ObservableProperty that contains the word "depth"
  - Fixes issue with wrong features in enriched SensorML procedure description


## Release 52n-sensorweb-sos-4.4.4
 
### New features

  - Add support for ResultFiltering for SOS 1.0.0
  - E-PRTR support
  - Add encoding of swes:Extensions as MetadataProperty
  - Implement SOS 2.0 ResultFiltering extension 2.0 to support other filter instead of only ComparisonFilter
  
### Changes
  
  - Do not transform geometries if the CS have different dimensions
  
### Fixed issues
  
  - Fix issue with observation identifier becomes to long when using split swe data array feature
  - Fix version of 3rd-party library jackson
  - Fix insertion and requesting of profiles via result handling operations
  - Fix INSPIRE OMSO schemaLocation
  - Add GML 3.3 namespace and schemaLocaiton to INSPIRE OMSO encoder
  - Get all NAMESPACE tokens when checking for schemaLocations
  - Fix issue with duplicate encoding of SensorML characteristics
  - FIx issue when SensorML contact is not of type responsible party
  - Fix overwriting of SensorML keywords
  - FIX NPE when envelope is null
  - Fix swe:Fields that does not contain an element
  - Fixed mixed hierarchical and non-hierarchical offerings in Capabilites
      
 
## Release 52n-sensorweb-sos-4.4.3
 
### New features

  - Request timeout support (#603)
  
### Changes
  
  - Use Java 8
  - Increase GDA performance when metadata are defined
  - Remove obsolete observationHasOffering table for series concept(s)
  - Increase DeleteObservation performance
  
### Fixed issues
  
  - Issue #607: UVF encoding does not consider contentType from requested responseFormat
  - Issue #608: InsertResult throws NPE for hidden child procedures
  - altitude/depth value setting to coordinate.z
  - gml:description order for O&M XML stream encoder
  - Sampling geometry with 3D coordinates
  - Referenced series creation
  - WaterML TVP encoding for single value
  - Setting of GDA 2.0 response format if forced
  - Check for FES filter extension
  - Fix support for referenced featureOfInterst (gml:id) in InsertObservation with multiple observations
  - Some minor fixes
      
 
## Release 52n-sensorweb-sos-4.4.2

 
### New features

  - Result filtering for GetObservation and GetDataAvailability
  - Spatial filtering for GetDataAvailability
  - Insertion of static reference values (stored as own procedure and series)
  
### Changes
  
  - Move REST-API and Helgoland Client to webapp module (included by default)
  
### Fixed issues
  
  - Issue #212: Allow sort by status in 52n-sos-webapp/admin/operations
  - Issue #555: Wrong error code when sending plain SensorML and not a request
  - Issue #572: Fix NPE in InsertSensor when procedure description has invalid namespace declaration
  - Issue #574: Capabilities shows metadata and test client provides examples for not supported operations
  - Issue #575: HibernateMetadataCache not updated after resetting the SOS
  - Issue #581: Cache: loosing spatial information after manually triggered update
  - Issue #584: responseFormat-Parameter "uvf" results in Java Exception
  - Issue #590: Creating a permlink is not working for +2000 chars
  - Issue #592: SQL Server datasource does not add null constraint to series.identifier
  - Issue #596: InsertSensor does not check if offering is already related to another procedure
  - Set vertical datum name from datasource entity (#585)
  - Encode description element (#585)
  - Fix issue when the cache thread cound is equal or greater than the max connection count
  - Fix CRS and EPSG code settings (#579)
  - Creation of LineString WKT in GmlDecoderv321

## Release 52n-sensorweb-sos-4.4.1

 
### New features

  - Support for ReferencedObservation
  - Support for insertion of profile observations via InsertResult/Template operations
  
### Changes
  
  - Load Microsoft SQL Server JDBC from Maven repository
  - Improvements on cache serialization
  
### Fixed issues
  
  - Issue #566: Reload Capabilities Cache leads to severe error
  - Issue #571: Sensors disappear (same as #566)
  - Fix resetting of HIBERNATE_DIRECTORY config on save
  - Fix the deletion of child observations
  - Fix issue with not correct observation type for GWML GeologyLogCoverage

## Release 52n-sensorweb-sos-4.4.0

 
### New features

  - INSPIRE support for
    - Technical Guidance for implementing download services using the OGC Sensor Observation Service 
            and ISO 19143 Filter Encoding (http://inspire.ec.europa.eu/id/document/tg/download-sos) (Since 4.3.0)
      - https://wiki.52north.org/SensorWeb/SensorObservationServiceIVDocumentation#INSPIRE_Download_Service_extension
    - Guidelines for the use of Observations & Measurements and Sensor Web Enablement-related 
            standards in INSPIRE (D2.9) (http://inspire.ec.europa.eu/id/document/tg/d2.9-o%26m-swe)
      - https://wiki.52north.org/SensorWeb/InspireSpecialisedObservations
      - GetDataAvailability 2.0
        - Result contains the offering, procedure description formats and responseFormat/observationTypes.
      - Hierarchical offering (similar to hierarchical procedure)
        - allows to display only the parent offerings in the Capabilities
  - InsertFeatureOfInterest operation
    - Allows the insertion of featureOfInterest 
  - Support for insertion of procedure types
    - https://wiki.52north.org/SensorWeb/SensorObservationServiceIVDocumentation#A_61_61_Procedure_type_support
  - DeleteResultTemplate operation
    - Allows the deletion of resultTemplates by identifier or offering/observedProperty pairs
  - Update procedure name/description in UpdateSensorDescription
    - Update the procedure name/description if changed 
  - UVF encoding
    - https://wiki.52north.org/SensorWeb/UvfEncoding
  - WaterML 2.0 MonitoringPoint support in database
    - Inclusive support of relatedParty and verticalDatum
    - https://wiki.52north.org/SensorWeb/WaterML
  - ResultHandling
    - Support insertion of ComplexObservations
    - Support insertion of samplingGeometry
    - Support for insertion of related featureOfInterest and procedure identifier in result values
  - Enhanced DeleteObservation to allow deletion for featureOfInterest, procedure, observedProperty,
          offering and temporalFilter
  - Support for stored ISO strings as time in database (Hibernate datatype)
  - Get name of observedProperty from procedure description if available
    - Would be inserted into the observedProperty table.
  - Support for netCDF encoding
    - Support CF
    - Support OceanSites
  - Register binding
    - Allows the insertion of the raw sensor description via HTTP POST
    - https://wiki.52north.org/SensorWeb/RegisterBinding
  - Insertion of "up-to-date" sample data that can be simply used by the Helgoland Client
    - The SOS example requests are still for the test data!
  - Show client IP during installation for transactional security settings
  
### Changes
  
  - Update setting descriptions
  - Contains Sensor Web REST-API v2.0.0
  - Contains Helgoland map client v1.0.0
  - Select profile (default SOS 2.0, Hydrology, INSPIRE) via admin interface
  
### Fixed issues

  - Issue #215: i18N GUI not rendered if required tables are missing
  - Issue #221: Check identifiers of transactional operations for reserved characters
  - Issue #250: Dispatching after reset is wrong
  - Issue #255: NumericSettingDefinition breaks settings view in admin interface bug enhancement high priority
  - Issue #258: SQL-Server 2012 Express: Can not clean datasource via 
                    admin/datasource -> clear datasource
  - Issue #261: NPE is thrown when sending not supported XML requests.
  - Issue #264: SOS throws a NPE in SensorML 1.0.1 decoder if characteristics/capabilities 
                    element has no AbstractDataRecord element
  - Issue #266: Reload CapabilitiesExtensionProvider in CapabilitiesExtensionRepository 
                    after changing operations status
  - Issue #267: Capabilities shows procedureDescriptionFormats in transactional 
                    operations which are not supported
  - Issue #277: Check for not null/empty coordinates in CoordinateTransformer.transformSweCoordinates() 
                    before joining
  - Issue #279: Extend the supported coordinate names
  - Issue #282: Fix potential problems in CoordinateTransformer.getCrsFromString(String)
  - Issue #285: Client problem with feature capabilities in DescribeSensor 
                    response with SensorML 1.0.1 encoded procedure
  - Issue #287: The XML text of the parsed SensorML 2.0 AbstractProcess from components is invalid.
  - Issue #288: Clear Datasource fails with 500 Internal Server Error
  - Issue #305: After changing datasource settings requests fail with exception
  - Issue #318: NPE if SensorML 2.0 description contains an empty <sml:classification />
  - Issue #323: GetFeatureOfInterestRequestDecoder no parse featureOfInterest parameter
  - Issue #349: Change wording for SOS time period error
  - Issue #351: Development Branch always 'enriching with discovery information'
  - Issue #361: First/last numeric values are not updated in series table in develop branch
  - Issue #367: java.lang.NullPointerException checking hasObservations
  - Issue #369: SOS Client InsertObservation example '[POX] InsertObservation - ComplexObservation (SOS 2.0.0)'
  - Issue #370: ExceptionReport: NullPointerException+CastException for ComplexObservation result template
  - Issue #389: The build process fails after merging of #385
  - Issue #395: Creating database schema fails for MySQL
  - Issue #422: Failed conversion between SML 2.0 and SML 1.0.1
  - Issue #439: quality tag how metadata of output list
  - Hidden NPE cause by not set request context while inserting new sample data
  - Avoid query for next value chunk if previous has less than chunk size


## Release 52n-sensorweb-sos-4.3.16


### New features

    
### Changes
    
  
### Fixed issues

  - Issue #566: Reload Capabilities Cache leads to severe error
  - Issue #571: Sensors disappear (same as #566)
    
    
## Release 52n-sensorweb-sos-4.3.15


### New features

    
### Changes
    
  
### Fixed issues

  - avoid orphaned session objects after re-configure SOS
* prevents resetting HIBERNATE_DIRECTORY config (missing extension/readonly folder) 
    

## Release 52n-sensorweb-sos-4.3.14


### New features

    
### Changes
    
  
### Fixed issues

  - some fixes for the rest api (bundle)


## Release 52n-sensorweb-sos-4.3.13


### New features

    
### Changes
    
  - Update PostgreSQL JDBC to current which works with PG 9.6

  
### Fixed issues

  - some minor fixes (getting unit from series)

## Release 52n-sensorweb-sos-4.3.12


### New features

  - Make timezones configurable to handle time w/o zone information properly (instead of expecting UTC only)
  - Add named query for samplingGeometry check in offering cache update
  - Get unit from series table
    
### Changes
    
  - Set overall time extrema flag to false
  - Enhancements for not publiched objects (series concept)
  - Update REST-API to 1.10.0
  
### Fixed issues

  - some minor fixes
 

## Release 52n-sensorweb-sos-4.3.11


### New features
  
    
### Changes
    
  - Update REST-API version of bundle to 1.9.5
  
### Fixed issues

  - Issue #506: Get HTTP Status 500 - Handler processing failed when install 52North SOS-webapp-4.3.10
  - Missing c3p0 library
  - Do not list not published procedure/featureOfInterest/observedProperty in Capabilities (series concept)
 
## Release 52n-sensorweb-sos-4.3.10


### New features
  
    
### Changes

  
### Fixed issues

  - Issue #503: Capabilities cache update fails for old database concept
 
## Release 52n-sensorweb-sos-4.3.9


### New features
  
  - helgoland client
    
### Changes
 
 
  
### Fixed issues
    
  - Issue #390: Add default="NULL" to nullable timestamp properties in mapping files
  - Set metadata property
  - Document-Ready-Callback for SOS Test client
  - Fix issue with not defined altitude in SensorML position
  - Fix mapping files for MySQL if timestamp column is nullable
    
 
## Release 52n-sensorweb-sos-4.3.8


### New features
  
  - Add offering to series table (optional -> can be null)
  - Add support of SensorML connections encoding
  - Add support for gml:MetadataProperty encoding
    
### Changes
 
  - Issue #427: Check and remove lazy and fetch attributes from Hibernate mapping files
  - Add child procedure and featureOfInterest to GDA request
  - Update PostgreSQL version to support PostgreSQL 9.6.x
  - Update REST-API version of bundle to 1.9.0
  - Also implements support for offering in series table
  
### Fixed issues
    
  - Issue #350: java.lang.nullPointerException w/ SML contact element
  - Issue #446: Issue regarding getCapabilities
  - Issue #452: 'Clear Database' utility fails to remove data from PostgreSQL
  - Issue #466: SOS 4.3.7 InsertObservation Geometry doesn't work
  - Issue #467: Get 500 error with indeterminatePosition in TimeInstant
  - Issue #484: WaterML 2.0 TVP response contains no values if phenomenonTime is a time period
  - Fix issue with H2 file database when create schema is selected
    
 
## Release 52n-sensorweb-sos-4.3.7


### New features
  
  - Support for samplingGeometry stored as lat/lon
    
### Changes
 
  - Issue #427: Check and remove lazy and fetch attributes from Hibernate mapping files
  - Change XML validation to validate encoded objects only if debug log level is enabled.
  - Update REST-API version of bundle to 1.8.0
  
### Fixed issues
  
  - NPE in SmlCapabilities when calling getCapabilities
  - Fix indentation and implement short-circuit of the loop (https://github.com/52North/SOS/commit/23ab94e9da25f74a83c89ce22593228b39ac0890)
  - Fix random id generation (https://github.com/52North/SOS/commit/6cbfd9a1f7849af0358d2b99de3a41605beb07f7)
  - Issue #215: i18N GUI not rendered if required tables are missing
  - Issue #250: Dispatching after reset is wrong
  - Issue #382: Deleting Complex Observation
  - Issue #393: SamplingGeometry in InsertObservation is not inserted into database using JSON binding
  - Issue #397: InsertResult adding result to every observation offering available
  - Issue #398: Invalid namespace prefix when querying AQD GetObservation with samplingGeometry in the response.
  - Issue #405: Observation data failing to insert
  - Issue #416: Test Client - wrong type of the wml2:metadata element?
  - Issue #417: REST InsertObservation does not permit resultTime specified as xlink
  - Issue #418: REST binding does not work with enabled transactional security
  - Issue #424: AQD GetObservation executes a query for each series to get min/max phenomenonTime
  - Issue #426: Add OGC-SOS 1.0.0 GetObservation resultModel support
    

## Release 52n-sensorweb-sos-4.3.6


### New features
  
  
### Changes
 
  - Update REST-API version of bundle to 1.7.2
  
### Fixed issues
  
  
## Release 52n-sensorweb-sos-4.3.5


### New features
  
  - Add last cache update time and list in cache summary
  
### Changes
 
  - Harmonize REST-API feature mapping
  - Remove TOffering usage in offering cache update
  
### Fixed issues
  
  - REST-API FeatureResource mapping tale name, set name as in SOS FeatureOfInterest mapping
  - Issue #359: Missing "Version" and "Build date"
  - Issue #362: Minor typo in "Datasource configuration" wizard  
  - Issue #365: SOS Client InsertResultTemplate example '[POX] InsertResultTemplate - with ResultTime (SOS 2.0.0)'
 
## Release 52n-sensorweb-sos-4.3.4


### New features
  
  - Support OM_SamplingObservation in JSON binding (samplingGeometry in om:paramerter)
  - Add alternative Hibernate mapping files for REST-API which can be used with e-Reporting databse concept.
  
### Changes
  
  - Update REST-API version of bundle to 1.6.0
  
### Fixed issues
  
  - Issue #320: StackOverflowError in DescribeSensor operation when inserted with relatedFeature
  - Issue #321: InsertObservation with samplingGeometry om:parameter (OM_SpatialObservation) fails with exception
  - Issue #322: Missing samplingGeometry (om:parameter) in returned observation
 
## Release 52n-sensorweb-sos-4.3.3


### New features
  
  
### Changes
  
  
### Fixed issues
  
  - Issue #310: Invalid prefix in om:parameter of .../SamplingPoint
 
 
## Release 52n-sensorweb-sos-4.3.2


### New features
  
  
### Changes
  
  - Use Hibernate 4.3.11
  
### Fixed issues
  
  - Issue #299: SOS does not start on a system with Turkish language

	  
## Release 52n-sensorweb-sos-4.3.1


### New features
  
  - Add support for Oracle SID
     
### Changes
  
  
### Fixed issues
  
 
## Release 52n-sensorweb-sos-4.3.0

 
### New features
  
  - Support for AQD e-Reporting flows E
  - Primary validated assessment data - measurements (E1a).
  - Primary validated assessment data -modelled (E1b)
  - Primary up-to-date assessment data - measurements (E2a) 
  - More information about AQD e-Reporting: http://www.eionet.europa.eu/aqportal
	  * Documentation: https://wiki.52north.org/SensorWeb/AqdEReporting
	* Flexible identifier
	  * The flexible identifier extension offers the possibility to return human readable names 
	    as identifiers instead of the sometimes cryptic identifiers.
	  * Affects offering, procedure, observableProperty and featureOfInterest.
	  * Documentation: https://wiki.52north.org/SensorWeb/FlexibleIdentifier
  - Add core and custom datasources for Oracle (#228)
  - Support clear datasource for SQL Server (#199)
  - Add webapp-bundle module which builds a 52N SOS war including
  - Sensor Web Client REST-API (https://github.com/52North/timeseries-api)
    - Direct access to the database tables.
  - JavaScript SOS Client (https://github.com/52North/js-sensorweb-client)
	  - Linked by default to the SWC REST-API of the bundle
  
### Changes
  
  - Harmonize existing datasources (#228)
  - Use latest vecmath version
  - Move Role to correct package (#201)
  
### Fixed issues
  
  - Issue #211: Failing InsertSensor if sml:DataInterface in puts
  - Issue #225: Use setting default when missing
  - Issue #227: GetResult returns values of not published series
  - Issue #226: ERROR log statements while importing observations: "Duplicity of the featureOfInterest identifier"
  - Issue #230: INSPIRE namespace element is not present in SpatialDatasetIdentifier
  - Issue #233: Outdated links
	
 
## Release 52n-sensorweb-sos-4.2.0


### New features
 
  - INSPIRE SOS:
  - Details: https://wiki.52north.org/SensorWeb/SensorObservationServiceIVDocumentation#INSPIRE_Download_Service_extensi
  - Multilingualism:
    - Name/description of offering, procedure, observableProperty, and featureOfInterest can be defined in different languages
    - Define/Change via the admin interface, insertion via transactional operations is not yet supported.
    - Details: https://wiki.52north.org/SensorWeb/SensorObservationServiceIVDocumentation#Multilingualism_support
  - Coordinate transformation:
    - Requested coordinates are transformed to the defined database CRS
    - Coordinates in the responses are transformed to the requested or defined response CRS
        *Details: https://wiki.52north.org/SensorWeb/SensorObservationServiceIVDocumentation#CRS_support
      *https://github.com/52North/SOS/pull/131
  - Support for transactional feature relation insertion
  - Insert valid sampledFeature feature and add relation to database
  - https://github.com/52North/SOS/pull/125
  - Maven source plugin added to build source jar artifacts (good for debugging)
  - https://github.com/52North/SOS/pull/121
  - Support for procedures with external (http URL) description
  - URL should be stored in descriptionfile column of the procedure table
  - Support for "getFirst" in temporal filter of GetObservation requests, as it was implemented in 52N SOS 3.x
  - Support for choice settings
  - https://github.com/52North/SOS/pull/182
  - EXI binding
  - Support for EXI encoded XML messages
  - https://github.com/52North/SOS/pull/189
  - Support for SensorML 2.0
  - https://github.com/52North/SOS/pull/190


### Changes
 
  - Database model: Use doubles instead of BigDecimals for numeric obs values
  - Discussion: https://github.com/52North/SOS/issues/120
  - !!! Requires database update via SQL script (/mis/db/../.._update_41_to_42.sql
  - Use series first/last timestamp for procedure time extrema (Performance improvement)
  - Query the procedure time extrema from the series table if the information are available
  - https://github.com/52North/SOS/pull/130
  - Query samplingGeometry extent only if values are not null (Performance improvement)
  - Check if not null sampledGeometries are contained in the database.
  - If not, the extent query is not executed.
  - https://github.com/52North/SOS/pull/128
  - Update used version of 52N XML Common to 2.1.0
  - SamplingFeature schema fixed multiplicity of sampledFeature from 1..1 to 1..*
  - Transactional operations are disabled by default
  - https://github.com/52North/SOS/pull/187
  
 
### Fixed issues
 
  - Fix checkSchemaCreation during install
  - https://github.com/52North/SOS/pull/119
  - Removed mandatory schema settings for full DB datasources and fixed the MySQL one.
  - MySQL does not support separate schema. Schema is a synonym for database.
  - https://github.com/52North/SOS/pull/129
  - Issue #185: Multilingual settings are not loaded from settings file
  - https://github.com/52North/SOS/pull/186
  - Issue #172: JSON GetFeatureOfInterest request ignores the observedProperty parameter
  - https://github.com/52North/SOS/pull/184
  - Issue #176: DeleteObservation throws NPE when deleting the latest observation for a series
  - https://github.com/52North/SOS/pull/184
  - Persisted service URL was not displayed in settings
  - https://github.com/52North/SOS/pull/181
  - Fix/stop threads in async cache persistence strategy and created by json schema factory
  - https://github.com/52North/SOS/pull/171
  - Issue #167: Improve error message when requesting empty time period
  - https://github.com/52North/SOS/pull/170
  - Issue #168: Laxer checking of output format
  - https://github.com/52North/SOS/pull/169
  - Fix IP address with port proxy chain
  - https://github.com/52North/SOS/pull/165
  - Issue #147: Impossible to NOT use the public schema 
  - https://github.com/52North/SOS/pull/148
  - Fix NPE in DataAvailabilityTransformer for NamedQueries by using new TimerPeriod constructor with Object parameter
  - https://github.com/52North/SOS/pull/146
  - Issue #191: JDBC string SQL server: optional instance parameter is required in our impelementation
  - https://github.com/52North/SOS/pull/193
   
## Release 52n-sensorweb-sos-4.1.5


### New features
 
 
### Changes
 
  
### Fixed bugs
  
  - Fix problem when requesting DescribeSensor with procedureDescriptionFormat = http://www.opengis.net/waterml/2.0/observationProcess.
   - The SOS does not set the procedureDescriptionFormat to the sensor description before the SensorML 1.0.1 to WML 2.0 
         converter is called which leads to an error in the converter.
  
## Release 52n-sensorweb-sos-4.1.4


### New features

 
### Changes

  
### Fixed bugs
 
  - Fix problem with query for Spatial Filtering Profile envelope for database that do not support the spatial extent function.
  - https://github.com/52North/SOS/pull/161

## Release 52n-sensorweb-sos-4.1.3


### New features
 
  - Add published flag to series table
 
### Changes
 
  - Set OGC unknown URL instead of URN as xlink:href value of sampledFeature
  
### Fixed bugs
   
      
## Release 52n-sensorweb-sos-4.1.2


### New features
 
### Changes
  
### Fixed bugs
 
   - NullPointerException when Features Of Interest have no geometries
   - https://github.com/52North/SOS/issues/149  
 
 
## Release 52n-sensorweb-sos-4.1.1


### New features
 
### Changes
  
### Fixed bugs
 
   - Issue with OpenGeo repository
   - GetFeatureOfInterest request with featureOfInterest identifier and procedure/observableProperty identifier
       ignores the featureOfInterest identifier
   - Performance issue for first/latest GetObservation with series concept


## Release 52n-sensorweb-sos-4.1


### New features
 
  - Binding selection by ContentType at /sos and /service
  - Add columns to mapping files for an easier integration of the Timeseries API (only for Series concept)
  - Added first/latest time (all) and value (only OM_Measurement) columns to series table and update 
        these columns during insertion process
  - Add Microsoft SQL Server support
  - https://github.com/52North/SOS/commit/e288e502fbc1f0b833bad1e90d60b36c491d0792
  - Create JSON cache dump in admin interface
  - https://github.com/52North/SOS/commit/9cef2ba00c862058e7ada1f7a2862ef98a3e3096
  - GetObservation with multiple temporal filter and different valueReferences
  - same valueReference:       filter OR filter
  - different valueReferences: (filterA1 OR filterA2) AND filterB
  - Streaming response:
  - can be activated via settings
  - StreamWriter for SOAP envelope, O&M 2.0, and WaterML 2.0
  - Allows bigger responses because the no XML DOM-Tree is created
  - Streaming datasource (Hibernate) for GetObservation:
  - can be activate via settings
  - Query observation metadata and create template observations
  - In the encoder the observation values are queried
  - Two possibilities, configurable via settings:
      	* scroll: send query and get scrollable values
      	* chunk: 
      	  * send paging requests and process each chunk
      	  * chunk size is configurable via settings
  - reduced latency for GetObservation response
  - Add datasource dependent DAO and ConnectionProvider loading
  - This allows to include DAOs and ConnectionProvider for different datasources in the same SOS which 
         can be selected during the installation.
  - Corresponding to the selected datasource, the required DAOs and ConnectionProvider are loaded during 
        the initialization.
  - https://github.com/52North/SOS/commit/d906f74267e8efcc51e53703a391aafb29119178
  - If featureOfInterest geometry is missing, create the geometry from existing samplingGeometries
  - https://github.com/52North/SOS/commit/dc8ce1eb00de99b09e4a60574be96c34bf441eb2
 
### Changes
 
  - Show the observation identifiers only in capabilities if size is less than 100, TODO: make this configurable
  - Adapt the GetDataAvailability request/response to the OGC Discussion Paper:
  - OGC Sensor Observation Service 2.0 Hydrology Profile (https://portal.opengeospatial.org/files/?artifact_id=57327)
  - XML schema: http://waterml2.org/schemas/gda/1.0/gda.xsd
  - Remove support for raw SQL execution in the admin interface because of security constraints 
  - https://github.com/52North/SOS/commit/ff5f8dd86f97024337f51492009559f11492bd7d
  - Update dependency versions:
  - joda-time (2.3)
  - json-schema-validator (2.2.3)
  - json-schema-core (1.2.1)
  - springframework (3.2.6)
  - PostgreSQL JDBC (9.3-1101-jdbc4)
  - PostGIS JDBC (2.1.3)
  - Hibernate (4.3.5.Final)
  - Reduced Maven build profiles 
  - https://github.com/52North/SOS/commit/279f815fd7372ab5cb3ac06add2995486f0c2402
  - Switch to Java 7 as compiler/source/target version
  - https://github.com/52North/SOS/commit/b1c172cd2b087b4a76eed8648e32952dee55c098
  - Asynchronous cache serialization (see issue Cache serialization performance issue)
  - https://github.com/52North/SOS/commit/09dcbdf8180bc0b7f260d435385254f58e50a78c
  - Better SQL Exceptions: Create a composite exception and add all Throwable as a single exception.
  - https://github.com/52North/SOS/commit/5b1ebe002aabc2d9108eaf196f8927605b77fc9b
  - Name unique constraints in mapping files
  - constraints are updated if you check 'Force updating existing tables' at the 'Datasource configuration' 
        installation page.
  - https://github.com/52North/SOS/commit/8e005c7b21f4c51bd238a8eeafdd592d8953c750
  - Move samplingGeometry from separate table (spatialfilteringprofile) to observation table
  - Improved performance if samplingGeometries are set.
  - Old concept with spatialfilteringprofile table is still supported. To use the old concept check 
        '!!! DEPRECATED !!! Old Spatial Filtering Profile' at the 'Datasource configuration' installation page.
  - For each supported DBMS an update script is available (/misc/db/..) to add the new column, copy the 
        samplingGeometries and remove the spatialfilteringprofile table.
  - https://github.com/52North/SOS/commit/65e8a4d7867f3e19c3c444438f79df5a3616fb8d
  - Change offering fetch mode to lazy to reduce the number of subqueries when querying the observations.
  - https://github.com/52North/SOS/commit/67475c6d3741641d02a4be4fb6c466526152c09f
      
 
### Fixed bugs
   
  - Check if JSON binding is supported before testing
  - Show Spatial Filtering Profile key in the Profile section of the Capabilities.ServiceIdentification if 
      strict Spatial Filtering Profile is selected
  - https://github.com/52North/SOS/commit/437fd75
  - Fix TimeRange decoding
  - https://github.com/52North/SOS/commit/4151647
  - Fix TimeInstant constructor with parameter java.util.Date 
  - https://github.com/52North/SOS/commit/3dc6205
  - Fix SOS 1.0 schemaLocation randomly missing after restarts (https://github.com/52North/SOS/issues/45)
  - https://github.com/52North/SOS/commit/f849cc9
  - Fix for Cache serialization performance issue
  - After each InsertObservation the cache was directly serialized to the file. Thus, the insert requests were 
        getting slower the more data were available.
  - https://github.com/52North/SOS/commit/b1c172cd2b087b4a76eed8648e32952dee55c098
  - Fix problem with duplicated entries in generated SQL scripts.
  - https://github.com/52North/SOS/commit/6e70d910e6f8f7140d79f65c83250cf5370b73fc
  - Fix #74: Exception when executing SOS 1.0.0 DescribeSensor requests
  - https://github.com/52North/SOS/commit/7e0e6a2ca8670e8f7ec632d899d9781391a04bbd
  - Throw exception if the inserted sensor description has swe:DataArray sml:output element without a swe:DataRecord elementType element.
  - https://github.com/52North/SOS/commit/5c55aeeb92a8ed60a12449c1171b757a38fc57bc
  - Fix GetObservation invalid srs exception code.
  - https://github.com/52North/SOS/commit/0b20dd424be7556a767170ba8392897b36cdd8c8
  - Fix #79: File-based H2/GeoDB fails on sampling geometry index
  - https://github.com/52North/SOS/commit/a5814ee539f25ada7eeedd75e63a37e68c6682cf
  - Fix #81: GetDataAvailability returns invalid times and value counts
  - https://github.com/52North/SOS/commit/49c004ff8a7255aed677bef05c913254070387e1
  - Fix #83: Update GetInvolved link on client landing page
  - https://github.com/52North/SOS/commit/c710a08dcdc05545d5515a24e643bc191bf71a94
  - Fix #84: AbstractSettingsDefinition equals() returns true if all variables are the same except key, title and description
  - https://github.com/52North/SOS/commit/c710a08dcdc05545d5515a24e643bc191bf71a94
  - Fix used Hibernate mapping path constants
  - https://github.com/52North/SOS/commit/8be9ad0d4bb6e388e9b8a5f9ed05ed6423b994d4
  - Fix #87: Inform user about javascript requirement for the webapp
  - https://github.com/52North/SOS/commit/5014f1fa00199e573b80ca931928fa53542602f7
  - Fix #80: Resetting the SOS does not delete the cache file
  - https://github.com/52North/SOS/commit/a43b945940963ec4152da7769de242213e219a70
  - Fix #101: SOS 1.0.0 GetObservation SOAP request fails
  - https://github.com/52North/SOS/commit/4972a4e0a13bc0e3e5cb951f4d322c064a22e8de
  - Fix #106: Possible thread safety issue in HTTPUtils' GenericWritable
  - https://github.com/52North/SOS/commit/835ce396cc9dda645a15bb2c538580943af7cfd5
  - Fix #109: First position is missing in the featuerOfInterest geometry if generated from samplingGeometries
  - https://github.com/52North/SOS/commit/6ccf283def95198c3ba8dd452ee10232a7a6251f
	* Fix #116: Time fields in observation table are identical when executing InsertResult with om:resultTime
	  * https://github.com/52North/SOS/commit/286cd8d166e3bfd95b936aec4d9ad4d487aa800b
 
 
## Release 52n-sensorweb-sos-4.0.1


### New features

### Changes
 
### Fixed bugs
    
  - Fix NcNameResolver.fixNcName and Show nested SQLExceptions in OwsExceptionReport
  - Add missing setting of namedQuery parameter


## Release 52n-sensorweb-sos-4.0.0


### New features

  - Series observation concept is now supported for transactional operations. 
  - This means that the observation table contains a seriesId column and references to the new series table.
  - FeatureOfInterest, procedure and observableProperty moved from observation to series table
  - Convert SQL scripts for PostgreSQL, MySQL and Oracle are available
  - DaoFactory for Hibernate DAOs to get  supported, old or series, observation DAO
  - SQL scripts for PostgreSQL, MySQL and Oracle are available to:
  - create database model (old and series concept)
  - clear tables (old and series concept)
  - drop database model (old and series concept)
  - SOS Administrator extended
  - Renaming of observed properties/phenomena
  - Beta: Static Capabilities incl. online editor and validator
  - Beta: Capabilities Extensions incl. online editor and validator
  - Beta: Offering Extensions incl. online editor and validator

### Changes

  - Default database model is now the series observation concept, the old observation concept is still 
         supported and can be activated during the installation by checking "Old observation concept".

### Fixed bugs

  - [Bug 976] Schema location for SML uses prefix instead of namespace
  - [Bug 977] Cannot add <swe:quality> elements in sml:output/swe:Quantity
  - [Bug 980] envelope is missing definition attribute
  - [Bug 981] CPU load issue after some days in operation
  - [Bug 982] NullPointer Error Inserting a ProcessModel
  - [Bug 983] InsertObservation fails because of root namespaces with "Invalid xsi:type qname: 'gml:MeasureType' in element..."
  - [Bug 987] Series-Concept: GetObservation response contains deleted observations
  - [Bug 990] Hibernate directories in datasource.properties should be able to be relative paths in classpath
  - [Bug 993] Cache Update blocks all operations
  - [Bug 996] Installer finishes smoothly when "PostgreSQL/PostGIS Core" datasource is selected but not tables exists
  - [Bug 997] FeatureOfInterest JSON Encoding with wrong "name"
  - [Bug 1001] Could not finish set-up using installer: Could not connect to the database: ERROR: relation "codespaceid_seq" already exists. 
                 Command: create sequence public.codespaceId_seq
  - [Bug 1005] Spatial Filtering Profile filtering does not work after refactoring the Hibernate GetObservation request processing (r22393)
    
## Release 52n-sensorweb-sos-4.0.0-RC6


### New features

  - Update database model during installation process if needed (EXPERIMENTAL)

### Changes

  - Added description column to observation table: !!! UPDATE of database model is required !!!
      - see file update_obs_add_description.sql in [SOS]\misc\db\[DBMS]
     
### Fixed bugs

  - [Bug 975] NPE in TimeInstant.compareTo() when time value is null

## Release 52n-sensorweb-sos-4.0.0-RC5


### New features

  - SQLScriptGenerator-Tool supports schema definition
  - Create/Clear/Drop SQL scripts for PostgreSQL and Oracle added
  - Include defined schema when checking if the tables still exists

### Changes

  - GML-Encoder TimePosition: set "unknown" as IndeterminateValue if time and indeterminate value 
      are not set in SOS object instead of 0000-01-01... time stamp
  - Move hibernate dialect to own project

### Fixed bugs

  - [Bug 951] JSON GetObservation with no temporal filters throws a NPE


## Release 52n-sensorweb-sos-4.0.0-RC4


### New features

  - OGC FES 2.0 ComparisonOp and LogicOp filter encoding

### Changes



### Fixed bugs

  - [Bug 943] Race condition between offering cache update task and GeometryHandler settings on startup
  - [Bug 944] Intermittent cache update SQLite error (commit failed)
  - [Bug 937] SOS returns not-well-formed response (since RC1)
  - [Bug 953] Invalid xsi:type qname when inserting an SWEDataArray observation with SOAP binding
  - [Bug 964] KVP requests should require the service parameter
  - [Bug 965] KVP: The  position after decimal point of spatial filter values are ignored

## Release 52n-sensorweb-sos-4.0.0-RC3


### New features

  - Setting to enable response validation if debug mode is disabled.

### Changes



### Fixed bugs

  - Double call of om:result element content creation

## Release 52n-sensorweb-sos-4.0.0-RC2


### New features

  - Initial series support for custom database models with series (procedure, observableProperty, featureOfInterest):
    - Supported operations are GetObservation and GetDataAvailability

### Changes

  - Update sensor description enrichment to comply with SensorML for Discovery Profile (OGC 09-163r2)

### Fixed bugs

  - [Bug 942] NPE when inserting external referenced feature
    - NPE when a referenced featureOfInterest (no geometry) was inserted via InsertObservation and the feature does not exists in the SOS 

## Release 52n-sensorweb-sos-4.0.0-RC1


### New features

  - JSON-Binding for SOS 2.0 and O&M 2.0 (not yet supported: SensorML 1.0.1 (contained as XML string))
  - Oracle database support
  - Simple security solution: IP and token based
  - SOS 2.0 Spatial Filtering Profile support
  - Support for 'validTime' parameter in DescribeSensor requests

### Changes

  - Database model: divided into Core and Transactional model
  - Example data insertion: use JSON encoded request
  - Test Client modified
  - all example queries are listed
  - apply filter to reduce number of queries
  - Rename column 'names' to 'name' in featureOfInterest table (update script)

### Fixed bugs

  - [Bug 860] - Dynamic generation of sensor description if descriptionURL and descriptionXml are not set
  - [Bug 919] - InvalidRequest exception when send SOAP-InsertObservation request with OM_SWEArrayObservation type
  - [Bug 920] - org.hibernate.LazyInitializationException: illegal access to loading collection in OfferingCacheUpdateTask's 
                  getRelatedFeatureIdentifiersFrom(TOffering offering)
  - [Bug 922] - Implement Related Feature Handling
  - [Bug 929] - Reload Capabilities Cache fails if there is a deleted procedure in the database 
  - [Bug 930] - Extension "SplitDataArrayIntoObservations"
  - [Bug 931] - Extension "MergeObservationsIntoDataArray"
  - [Bug 932] - Child procedure is shown in parent procedure offering after InsertSensor.
  - [Bug 933] - InsertSensor with not defined offering can be inserted multiple times without exceptions
  - [Bug 934] - Flexible test data insertion with JSON files
  - [Bug 935] - SOS 1.0.0 DescribeSensor request throws invalid outputFormat exception
  - [Bug 941] - CapabilitiesCache setMaxResultTime() and setMinResultTime() changes global phenomenonTime

## Release 52n-sensorweb-sos-4.0.0-Beta2

### New features

  - Related Feature Support in InsertSensor, InsertObservation, GetObservation, GetFeatureOfInterest (see #922 or Documentation->Features->Related Features for more details) 
  - Dynamic WSDL generation, query via http://[HOST]:[PORT]/[WEBAPP_NAME]/wsdl, e.g. http://localhost:8080/52n-sos-webapp-4.0.0-SNAPSHOT/wsdl
  - InsertSensor: 
    - for more than one offering
    - again for another offering
    - again for the same offering if the procedure was deleted before, observations inserted before deletion are not provided
  - Serialization of cache: serialize cache to file after updates and during shutdown, load cache from file during startup if exist (fast startup)
  - Administrator: 
    - GUI to en-/disable single operations
    - GUI to en-/disable single encodings
    - GUI to en-/disable single bindings
    - Clear database, insert test data, remove test data, reload cache
  - Sort elements in GetCapabilities response by A-Z, 0-9,...
  - Storing codespace of gml:identifier for featureOfInterest and observation in DB
  - RESTful Binding
  - POX Binding
  - DeleteObservation operation
  - SOS 1.0.0 support:
    - GetCapabilities (KVP,POX,SOAP)
    - DescribeSensor (KVP,POX,SOAP)
    - GetObservation (KVP,POX,SOAP)
    - GetFeatureOfInterest (POX,SOAP)
  - WaterML 2.0 Encoding
  - GetDataAvailability operation, de-/encoding
  - Additional Supported filter (need more tests regarding correct behavior):
    - temporal:	After, Before, Begins, Ends, EndedBy, BegunBy, TContains, Overlaps, Meets, MetBy, OverlappedBy

### Changes

  - Database model has changed:
    - observation are not inserted twice if they relate to a different offering but same procedure, observedProperty and featureOfInterest
    - store procedure- and observableProperty-id in observation table
  - Change coordinate order handling from de-/encoder to DAOs
  - Exception and Response Code handling
  - SosEventBus: fire and listen for events, currently used by cache updates after transactions
  - Cache:
    - switch from database updates to in memory updates
    - update cache after transactional operation directly
  - The settings are saved in a separate, independent database
  - Database queries for Observation returning specialized types depending on stored value

### Fixed bugs

  - Insert an observation again without an error.
  - [Bug 800] Missing operation metadata parameter for operation InsertResultTemplate and InsertResult
  - [Bug 805] InsertSensor request is not parsed correct: InsertSensorRequest.getProcedureDescription().getOfferingIdentifiers() returns empty list
  - [Bug 811] ClassCastException when inserting observation with TimePeriod phenomenonTime and referenced resultTime
  - [Bug 834] Administrator: Reset using reset_admin.sql does not work
  - [Bug 843] Check hasCode() implementations for resetting hash
  - [Bug 847] Useless error message when sending POX to SOAP endpoint
  - [Bug 883] When single observation with resultTime are merged, the returned resultTime is not the latest


## Release 52n-sos-webapp-4.0.0-Beta1

### New features

  - Reference implementation of the OGS SOS 2.0 specification
  - Supported operations and extensions: 
   - Core:                          GetCapabilities, DescribeSensor, GetObservation
   - Enhanced Operations Extension: GetFeatureOfInterest, GetObservationById
   - Transactional Extension:       InsertSesnsor, InsertObservation, DeleteSensor
   - ResultHandling Extension:      InsertResultTemplate, InsertResult, GetResultTemplate, GetResult
   - Further operations:            UpdateSensorDescription
  - Supported bindings:               KVP and SOAP
  - Supported filter:
   - temporal:                      During, T_Equals
   - spatial:                       BBOX
  - Supported codings:                O&M 2.0, SWES 2.0, SWECommon 2.0, SensorML 1.0.1, FES 2.0, Sampling 2.0, GML 3.2.1
  - Supported feature types:          SamplingPoint, SamplingCurve, SamplingSurface
  - Supported observation types:      OM_Measurement, OM_CountObservation, OM_CategoryObservation, OM_TruthObservation,
                                        OM_TextObservation, OM_SWEArrayObservation
  - new database model: use integer values as ids, necessary to map the SOS 2.0
  - Hibernate + Hibernate Spatial for database connection, 
  - Easy integration of new bindings, encodings and operations without changing the core sources
  - Simple installation of the 52North SOS: step by step installation
  - Administration GUI: change setting, clear database, add example data, change log levels
  - New TestClient


### Changes


### Fixed bugs



For more detailed information about the bugs look at https://github.com/52North/SOS/issues

