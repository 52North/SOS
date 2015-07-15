# Parameters
The following parameters for request are measured in the statistics collection module.

### Default for all requests
##### RequestEvent
- **sr-version** - version of the service
- **sr-service** - name of the service e.g: SOS
- **sr-operation-name** - name of the operation which is requestes e.g: GetCapabilites
- **extension-definition** - definition of the Extension
- **extension-identifier** - identifier of the Extension
- **extension-value** - toString() version of the value of the Extension

##### OutgoingResponseEvent
- **outre-exec-time** - the execution time of processing the request-response
- **outre-count** - an incremental number since the start of the webapplication. this field indicates the serial number of the request.

##### External/Computed values
- **sr-source-ip-address** - source ip address of the client even in case of proxying
- **sr-content-type** - HTTP content-type of the request
- **sr-accept-types** - HTTP accept-types of the request
- **sr-source-geolocation**
   - **country-code** - [ISO-3166-1](https://en.wikipedia.org/wiki/ISO_3166-1) two letter country code.
   - **city-name** - name of the nearest city based on the IP address
   - **geopoint** - latitude and longitude coordinates of the client
- **proxied-request** - boolean field if whether the request come through a proxy.

### Default to all responses
##### ResponseEvent
- **sresp-mediatype** - HTTP content type of the response

### Exceptions
##### ExceptionEvent
- **exception-status** - HTTP status if it exists 
- **exception-version** - Service version
- **exception-message** - The detail message
- **codedexception-locator** - locator of the CodedException
- **codedexception-soapfault** - soap fault reson CodedException
- **owsexception-namespace** - namespace of this exception OwsException

###  GetCapabilities
##### RequestEvent
- **getcapabilities-versions** - list of the accept version
- **getcapabilities-formats** - list of the accept formats
- **getcapabilities-sections** - list of the sections of the request
- **getcapabilities-updatesequence** - update sequence

### DescribeSensor
##### RequestEvent
- **describesensor-procedure** - name of the procedure
- **describesensor-procedure-description-format** - format of the description of the procedure
- **describesensor-validtime**
  - **describesensor-validtime-duration** - in case of TimePeriod the difference between start end end timestamps of the request parameter this value is a computed value
  - **describesensor-validtime-start** - start timestamp of the TimePeriod parameter
  - **describesensor-validtime-end** - end timestamp of the TimePeriod parameter
  - **describesensor-validtime-timeinstant** - in case of TimeInstant this field contains the TimeInstant value

### GetObservation
##### RequestEvent
- **getobservation-procedures** - list of the procedures
- **getobservation-spatial-filter** - The spatial filter of the request
- **getobservation-observed-properties** - list of the observed properties
- **getobservation-offerings** - list of the offerings in question
- **getobservation-temporal-filter** - list of temporal filters if any
- **getobservation-response-format** - response format
- **getobservation-merged-observation-values** - boolean value if the observation are merged


### GetObservationById
##### RequestEvent
- **getobservationbyid-observation-identifier** - identifier of the observation

### GetFeatureOfInterest
##### RequestEvent
- **getfeatureofinterest-feature-identifiers** - list of identifiers of the features
- **getfeatureofinterest-observed-properties** - list of the observed properties
- **getfeatureofinterest-procedures** - list of the procedures
- **getfeatureofinterest-spatial-filter** - list of spatial filters
- **getfeatureofinterest-temporal-filter** - list of temporal filters

### InsertSensor
##### RequestEvent
- **insertsensor-assigned-offerings** - list of assigned offerings to the sensor
- **insertsensor-assigned-procedure-identifiers** - list of procedure identifiers
- **insertsensor-observable-property** - observable property of the sensor
- **insertsensor-procedure-description** - description of the procedure
- **insertsensor-description-format** - format of the sensor
- **insertsensor-feature-of-interest-types"** - feature of interest types of the metadata
- **insertsensor-observation-types** - observation type of the metadata

### UpdateSensor
##### RequestEvent
- **updatesensor-procedure-identifier** - identifier of the procedure to update
- **updatesensor-description-format** - description format of the sensor

### DeleteSensor
##### RequestEvent
- **deletesensor-procedure-identifier** - identifier of the procedure

### InsertObservation
##### RequestEvent
- **insertobservation-assigned-sensorid** - id of the sensor to insert the observation to
- **insertobservation-offerings** - offerings of the observation
- **insertobservation-observation** - the requested observation and some of it's interesting fields
 
### InsertResultTemplate
##### RequestEvent
- **insertresulttemplate-identifier** - identifier of the result template
- **insertresulttemplate-result-encoding** - encoding of the template
 
### InsertResult
##### RequestEvent
- **insertresult-template-identifier** - identifier of the template
 
### GetResultTemplate
##### RequestEvent
- **getresulttemplate-observed-property** - observed property of the template
- **getresulttemplate-offering** - offering of the template

### GetResult
##### RequestEvent
- **getresult-feature-identifiers** - list of feautre of interest identifiers
- **getresult-namespaces** - list of namespaces
- **getresult-observation-template-identifier** - template idenfitier
- **getresult-observation-property** - name of the observation property
- **getresult-offering** - name of the offering

### GetDataAvailability
##### RequestEvent
- **getdataavailability-features-of-interest** - list of feature of interests
- **getdataavailability-namespace** - list of namespaces
- **getdataavailability-observed-properties** - list of observed properties
- **getdataavailability-offerings** - list of offerings
- **getdataavailability-procedures** - list of procedures

### Utility fields
##### External/Computed values
- **@timestamp** - UTC timestamp of the arrival of the clients request.
- **instance-uuid** - unique id of the SOS deployment
- **unhandled-serviceevent-type** - in case of unhandled internal service events the java full classname appears here.

