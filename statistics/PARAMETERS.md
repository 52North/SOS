
###Metadata

####Computed
 - **mt-creation-time** - Creation time of the Elasticsearch index. Type: date
 - **mt-update-time** - Update time of the Elasticsearch metadata type. Type: date
 - **mt-version** - Monoton increasing version field. The deployment schema and the Elasticsearch schema version must match. Type: integer
 - **mt-uuids** - List of unique user IDs. Type: string

###DeleteSensor

####RequestEvent
 - **deletesensor-procedure-identifier** - Procedure ID. Type: string

###GetResultTemplate

####RequestEvent
 - **getresulttemplate-observed-property** - Observed property. Type: string
 - **getresulttemplate-offering** - Offering. Type: string

###InsertSensor

####RequestEvent
 - **insertsensor-assigned-offerings** - Assigned offerings. Type: string
 - **insertsensor-assigned-procedure-identifiers** - Procedures. Type: string
 - **insertsensor-observable-property** - Observable properties. Type: string
 - **insertsensor-procedure-description** - Description of the procedure. Type: string
 - **insertsensor-description-format** - Description format of the procedure. Type: string
 - **insertsensor-feature-of-interest-types** - Feature of interest. Type: string
 - **insertsensor-observation-types** - Observation types. Type: string

###InsertResultTemplate

####RequestEvent
 - **insertresulttemplate-identifier** - ID. Type: string
 - **insertresulttemplate-observation-template** - Observation template ID. Type: string

###GetObservation

####RequestEvent
 - **getobservation-procedures** - Procedure of the observation. Type: string
 - **getobservation-feature-of-interests** - Feature of interests. Type: string
 - **getobservation-spatial-filter** - Spatial filter.
	 - **operation** - Operator. Type: string
	 - **shape** - Elasticsearch shape. Type: geo_shape
	 - **value-reference** - Value reference. Type: string
 - **getobservation-temporal-filter** - Temporal filter.
	 - **duration** - Duration between the END-START timestamp in milliseconds. Type: long
	 - **start** - Start timestamp. Type: date
	 - **end** - End timestamp. Type: date
	 - **timeInstant** - Timestamp if the value is TimeInstant type. Type: date
	 - **span-days** - This is a computed field based on the start timestamp and end timestamp of the TimePeriod instances.The value is the date for each days which are  between the start/end timestamps interval. Intarval ends are included.. Type: date
	 - **operator** - Operator. Type: string
	 - **value-reference** - Value reference. Type: string
 - **getobservation-observed-properties** - Observed properties. Type: string
 - **getobservation-offerings** - Offerings. Type: string
 - **getobservation-response-format** - Response format. Type: string
 - **getobservation-merged-observation-values** - Are the observation values merged. Type: boolean

###GetObservationById

####RequestEvent
 - **getobservationbyid-observation-identifier** - ID of the observation. Type: string

###InsertObservation

####RequestEvent
 - **insertobservation-assigned-sensorid** - Assigned sensor ID. Type: string
 - **insertobservation-offerings** - Offering. Type: string
 - **insertobservation-observation** - Observations.
	 - **constellation** - Observation constellation.
		 - **procedure** - Procedure. Type: string
		 - **observable-property** - Observable property. Type: string
		 - **feature-of-interest** - Feature of interest. Type: string
		 - **observation-type** - Observation type. Type: string
	 - **sampling-geometry** - Observation geometry.
		 - **operation** - Operator. Type: string
		 - **shape** - Elasticsearch shape. Type: geo_shape
		 - **value-reference** - Value reference. Type: string
	 - **phenomenon-time** - No available description.
		 - **duration** - Duration between the END-START timestamp in milliseconds. Type: long
		 - **start** - Start timestamp. Type: date
		 - **end** - End timestamp. Type: date
		 - **timeInstant** - Timestamp if the value is TimeInstant type. Type: date
		 - **span-days** - This is a computed field based on the start timestamp and end timestamp of the TimePeriod instances.The value is the date for each days which are  between the start/end timestamps interval. Intarval ends are included.. Type: date
	 - **result-time** - No available description.
		 - **duration** - Duration between the END-START timestamp in milliseconds. Type: long
		 - **start** - Start timestamp. Type: date
		 - **end** - End timestamp. Type: date
		 - **timeInstant** - Timestamp if the value is TimeInstant type. Type: date
		 - **span-days** - This is a computed field based on the start timestamp and end timestamp of the TimePeriod instances.The value is the date for each days which are  between the start/end timestamps interval. Intarval ends are included.. Type: date
	 - **valid-time** - No available description.
		 - **duration** - Duration between the END-START timestamp in milliseconds. Type: long
		 - **start** - Start timestamp. Type: date
		 - **end** - End timestamp. Type: date
		 - **timeInstant** - Timestamp if the value is TimeInstant type. Type: date
		 - **span-days** - This is a computed field based on the start timestamp and end timestamp of the TimePeriod instances.The value is the date for each days which are  between the start/end timestamps interval. Intarval ends are included.. Type: date

###Default

####ExceptionEvent
 - **exception-status** - HTTP status of the exception if any. Type: string
 - **exception-version** - Version of the exception. Type: string
 - **exception-message** - Text of the exception. Type: string
 - **exception-classtype** - The Java class type of the exceptions by simple name. Type: string
 - **codedexception-locator** - CodedException locator. Type: string
 - **codedexception-soapfault** - CodedException SOAP fault message. Type: string
 - **owsexception-namespace** - OWSException namespace. Type: string

####Computed
 - **unhandled-serviceevent-type** - If no processing handler is defined this field stores the Java class full name of the event. Type: string
 - **instance-uuid** - Unique ID of the instance who stored the event. Type: string
 - **@timestamp** - UTC timestamp of the event insertion. Type: date
 - **sr-source-ip-address** - Source IP address of the client proxies are stripped away. Type: string
 - **sr-content-type** - Content type of the request. Type: string
 - **sr-accept-types** - Accept type of the request. Type: string
 - **sr-source-geolocation** - Based on the IP address if this feature is enabled the latitude and longitude coordinates are computed.
	 - **country-code** - [ISO-3166-1](https://en.wikipedia.org/wiki/ISO_3166-1) two letter country code. Type: string
	 - **city-name** - name of the nearest city based on the IP address. Type: string
	 - **geopoint** - latitude and longitude coordinates of the client. Type: geo_point
 - **sr-proxied-request** - Is the request came through a proxy or proxies. Type: boolean
 - **sr-extensions** - Extensions.
	 - **extension-definition** - Definition. Type: string
	 - **extension-identifier** - Identifier. Type: string
	 - **extension-value** - Value object `toString()` version. Type: string

####ResponseEvent
 - **sresp-content-type** - Response content type. Type: string

####OutgoingResponseEvent
 - **outre-exec-time** - The execution time of processing the request-response. Type: integer
 - **outre-count** - An incremental number since the start of the webapplication. This field indicates the serial number of the request. Type: long
 - **outre-bytes-written** - Size of the response document.
	 - **bytes** - Size in bytes. Type: long
	 - **display** - Size in human readable form. Type: string

####RequestEvent
 - **sr-version** - Version of the deployment. Type: string
 - **sr-service** - Name of deployment. E.g: SOS. Type: string
 - **sr-language** - Language of the deployment if specified. Type: string
 - **sr-operation-name** - Name of the requested operation. E.g: GetCapabilities. Type: string

###GetCapabilities

####RequestEvent
 - **getcapabilities-versions** - Accept versions. Type: string
 - **getcapabilities-formats** - Accept formats. Type: string
 - **getcapabilities-sections** - Sections. Type: string
 - **getcapabilities-updatesequence** - Update sequence. Type: string

###DescribeSensor

####RequestEvent
 - **describesensor-procedure** - Procedure to describe. Type: string
 - **describesensor-procedure-description-format** - Description format. Type: string
 - **describesensor-validtime** - Validtime of the request.
	 - **duration** - Duration between the END-START timestamp in milliseconds. Type: long
	 - **start** - Start timestamp. Type: date
	 - **end** - End timestamp. Type: date
	 - **timeInstant** - Timestamp if the value is TimeInstant type. Type: date
	 - **span-days** - This is a computed field based on the start timestamp and end timestamp of the TimePeriod instances.The value is the date for each days which are  between the start/end timestamps interval. Intarval ends are included.. Type: date

###InsertResult

####RequestEvent
 - **insertresult-template-identifier** - Template ID. Type: string
 - **insertresult-result-values** - Result values. Type: string

###UpdateSensor

####RequestEvent
 - **updatesensor-procedure-identifier** - Procedure ID. Type: string
 - **updatesensor-description-format** - Description format of the procedure. Type: string

###GetFeatureOfInterest

####RequestEvent
 - **getfeatureofinterest-feature-identifiers** - Feature identifiers. Type: string
 - **getfeatureofinterest-observed-properties** - Observed properties. Type: string
 - **getfeatureofinterest-procedures** - Procedures. Type: string
 - **getfeatureofinterest-spatial-filter** - Spatial filter.
	 - **operation** - Operator. Type: string
	 - **shape** - Elasticsearch shape. Type: geo_shape
	 - **value-reference** - Value reference. Type: string
 - **getfeatureofinterest-temporal-filter** - Temporal filter.
	 - **duration** - Duration between the END-START timestamp in milliseconds. Type: long
	 - **start** - Start timestamp. Type: date
	 - **end** - End timestamp. Type: date
	 - **timeInstant** - Timestamp if the value is TimeInstant type. Type: date
	 - **span-days** - This is a computed field based on the start timestamp and end timestamp of the TimePeriod instances.The value is the date for each days which are  between the start/end timestamps interval. Intarval ends are included.. Type: date
	 - **operator** - Operator. Type: string
	 - **value-reference** - Value reference. Type: string

###GetDataAvailability

####RequestEvent
 - **getdataavailability-features-of-interest** - Feature of interest. Type: string
 - **getdataavailability-observed-properties** - Observed property. Type: string
 - **getdataavailability-offerings** - Offering. Type: string
 - **getdataavailability-procedures** - Procedure. Type: string

###GetResult

####RequestEvent
 - **getresult-feature-identifiers** - Feature IDs. Type: string
 - **getresult-observation-template-identifier** - Observation template ID. Type: string
 - **getresult-observation-property** - Observation property. Type: string
 - **getresult-offering** - Offering. Type: string
 - **getresult-spatial-filter** - Spatial filter.
	 - **operation** - Operator. Type: string
	 - **shape** - Elasticsearch shape. Type: geo_shape
	 - **value-reference** - Value reference. Type: string
 - **getresult-temporal-filter** - Temporal filter.
	 - **duration** - Duration between the END-START timestamp in milliseconds. Type: long
	 - **start** - Start timestamp. Type: date
	 - **end** - End timestamp. Type: date
	 - **timeInstant** - Timestamp if the value is TimeInstant type. Type: date
	 - **span-days** - This is a computed field based on the start timestamp and end timestamp of the TimePeriod instances.The value is the date for each days which are  between the start/end timestamps interval. Intarval ends are included.. Type: date
	 - **operator** - Operator. Type: string
	 - **value-reference** - Value reference. Type: string
