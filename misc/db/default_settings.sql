--
-- Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
-- Software GmbH
--
-- This program is free software; you can redistribute it and/or modify it
-- under the terms of the GNU General Public License version 2 as published
-- by the Free Software Foundation.
--
-- If the program is linked with libraries which are licensed under one of
-- the following licenses, the combination of the program with the linked
-- library is not considered a "derivative work" of the program:
--
--     - Apache License, version 2.0
--     - Apache Software License, version 1.0
--     - GNU Lesser General Public License, version 3
--     - Mozilla Public License, versions 1.0, 1.1 and 2.0
--     - Common Development and Distribution License (CDDL), version 1.0
--
-- Therefore the distribution of the program linked with libraries licensed
-- under the aforementioned licenses, is permitted by the copyright holders
-- if the distribution is compliant with both the GNU General Public
-- License version 2 and the aforementioned licenses.
--
-- This program is distributed in the hope that it will be useful, but
-- WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
-- Public License for more details.
--


-- script to create the schema for the SQLite 
-- configuration database and inserts default settings

DROP TABLE IF EXISTS "administrator_user";
DROP TABLE IF EXISTS "settings";
DROP TABLE IF EXISTS "boolean_settings";
DROP TABLE IF EXISTS "file_settings";
DROP TABLE IF EXISTS "integer_settings";
DROP TABLE IF EXISTS "numeric_settings";
DROP TABLE IF EXISTS "string_settings";
DROP TABLE IF EXISTS "uri_settings";
DROP TABLE IF EXISTS "operations";
DROP TABLE IF EXISTS "observation_encodings";
DROP TABLE IF EXISTS "procedure_encodings";


CREATE TABLE administrator_user (
	id  integer,
	password varchar,
	username varchar unique,
	primary key (id)
);

CREATE TABLE settings (
	identifier varchar not null,
	primary key (identifier)
);

CREATE TABLE boolean_settings (
	value integer,
	identifier varchar not null,
	primary key (identifier)
);

CREATE TABLE file_settings (
	value varchar,
	identifier varchar not null,
	primary key (identifier)
);

CREATE TABLE integer_settings (
	value integer,
	identifier varchar not null,
	primary key (identifier)
);

CREATE TABLE numeric_settings (
	value double,
	identifier varchar not null,
	primary key (identifier)
);

CREATE TABLE string_settings (
	value varchar,
	identifier varchar not null,
	primary key (identifier)
);

CREATE TABLE uri_settings (
	value varchar,
	identifier varchar not null,
	primary key (identifier)
);

CREATE TABLE observation_encodings (
	responseFormat varchar not null,
	service varchar not null,
	version varchar not null,
	active integer not null,
	primary key (responseFormat, service, version)
);

CREATE TABLE operations (
	operation varchar not null, 
	service varchar not null,
	version varchar not null,
	active integer not null,
	primary key (operation, service, version)
);

CREATE TABLE procedure_encodings (
	procedureDescriptionFormat varchar not null,
	service varchar not null,
	version varchar not null,
	active integer not null,
	primary key (procedureDescriptionFormat, service, version)
);

INSERT INTO "settings" VALUES('misc.characterEncoding');
INSERT INTO "settings" VALUES('misc.decimalSeperator');
INSERT INTO "settings" VALUES('misc.defaultFeaturePrefix');
INSERT INTO "settings" VALUES('misc.defaultObservablePropertyPrefix');
INSERT INTO "settings" VALUES('misc.defaultOfferingPrefix');
INSERT INTO "settings" VALUES('misc.defaultProcedurePrefix');
INSERT INTO "settings" VALUES('misc.httpResponseCodeUseInKvpAndPoxBinding');
INSERT INTO "settings" VALUES('misc.includeStackTraceInExceptionReport');
INSERT INTO "settings" VALUES('misc.srsNamePrefixSosV1');
INSERT INTO "settings" VALUES('misc.srsNamePrefixSosV2');
INSERT INTO "settings" VALUES('misc.switchCoordinatesForEpsgCodes');
INSERT INTO "settings" VALUES('misc.tokenSeparator');
INSERT INTO "settings" VALUES('misc.tupleSeparator');
INSERT INTO "settings" VALUES('profile.hydrology.maxReturnedValue');
INSERT INTO "settings" VALUES('profile.hydrology.maxReturnedTimeSeries');
INSERT INTO "settings" VALUES('profile.hydrology.overallExtrema');


INSERT INTO "settings" VALUES('procedureDesc.ENRICH_WITH_OFFERINGS');
INSERT INTO "settings" VALUES('procedureDesc.ENRICH_WITH_FEATURES');
INSERT INTO "settings" VALUES('procedureDesc.ENRICH_WITH_DISCOVERY_INFORMATION');

INSERT INTO "settings" VALUES('service.addOutputsToSensorML');
INSERT INTO "settings" VALUES('service.blockGetObservationRequestsWithoutRestriction');
INSERT INTO "settings" VALUES('service.cacheThreadCount');
INSERT INTO "settings" VALUES('service.capabilitiesCacheUpdateInterval');
INSERT INTO "settings" VALUES('service.configurationFiles');
INSERT INTO "settings" VALUES('service.defaultEpsg');
INSERT INTO "settings" VALUES('service.default3DEpsg');
INSERT INTO "settings" VALUES('service.encodeFullChildrenInDescribeSensor');
INSERT INTO "settings" VALUES('service.jdbc.deregister');
INSERT INTO "settings" VALUES('service.lease');
INSERT INTO "settings" VALUES('service.maxGetObservationResults');
INSERT INTO "settings" VALUES('service.minimumGzipSize');
INSERT INTO "settings" VALUES('service.response.validate');
INSERT INTO "settings" VALUES('service.streaming.force');
INSERT INTO "settings" VALUES('service.security.transactional.active');
INSERT INTO "settings" VALUES('service.sensorDirectory');
INSERT INTO "settings" VALUES('service.sosUrl');
INSERT INTO "settings" VALUES('service.strictSpatialFilteringProfile');
INSERT INTO "settings" VALUES('service.supportsQuality');
INSERT INTO "settings" VALUES('service.transactionalAllowedIps');
INSERT INTO "settings" VALUES('service.transactionalAllowedProxies');
INSERT INTO "settings" VALUES('service.transactionalToken');
INSERT INTO "settings" VALUES('service.useDefaultPrefixes');
INSERT INTO "settings" VALUES('service.SpatialDatasource');

INSERT INTO "settings" VALUES('serviceIdentification.abstract');
INSERT INTO "settings" VALUES('serviceIdentification.accessConstraints');
INSERT INTO "settings" VALUES('serviceIdentification.fees');
INSERT INTO "settings" VALUES('serviceIdentification.file');
INSERT INTO "settings" VALUES('serviceIdentification.keywords');
INSERT INTO "settings" VALUES('serviceIdentification.serviceType');
INSERT INTO "settings" VALUES('serviceIdentification.serviceTypeCodeSpace');
INSERT INTO "settings" VALUES('serviceIdentification.title');

INSERT INTO "settings" VALUES('serviceProvider.address');
INSERT INTO "settings" VALUES('serviceProvider.city');
INSERT INTO "settings" VALUES('serviceProvider.country');
INSERT INTO "settings" VALUES('serviceProvider.email');
INSERT INTO "settings" VALUES('serviceProvider.file');
INSERT INTO "settings" VALUES('serviceProvider.individualName');
INSERT INTO "settings" VALUES('serviceProvider.name');
INSERT INTO "settings" VALUES('serviceProvider.phone');
INSERT INTO "settings" VALUES('serviceProvider.positionName');
INSERT INTO "settings" VALUES('serviceProvider.postalCode');
INSERT INTO "settings" VALUES('serviceProvider.site');
INSERT INTO "settings" VALUES('serviceProvider.state');

INSERT INTO "administrator_user" VALUES(1,'$2a$10$y1TfEacanLJHkC0mqtkpy.KSt7r6DjdebUdbTn2kpqfwbiVRgnWsa','admin');

INSERT INTO "boolean_settings" VALUES(0,'misc.httpResponseCodeUseInKvpAndPoxBinding');
INSERT INTO "boolean_settings" VALUES(0,'misc.includeStackTraceInExceptionReport');
INSERT INTO "boolean_settings" VALUES(1,'service.supportsQuality');
INSERT INTO "boolean_settings" VALUES(0,'service.blockGetObservationRequestsWithoutRestriction');
INSERT INTO "boolean_settings" VALUES(0,'service.useDefaultPrefixes');
INSERT INTO "boolean_settings" VALUES(1,'service.encodeFullChildrenInDescribeSensor');
INSERT INTO "boolean_settings" VALUES(1,'service.addOutputsToSensorML');
INSERT INTO "boolean_settings" VALUES(0,'service.strictSpatialFilteringProfile');
INSERT INTO "boolean_settings" VALUES(0,'service.response.validate');
INSERT INTO "boolean_settings" VALUES(1,'service.streaming.force');
INSERT INTO "boolean_settings" VALUES(1,'service.jdbc.deregister');
INSERT INTO "boolean_settings" VALUES(1,'service.SpatialDatasource');
INSERT INTO "boolean_settings" VALUES(0,'service.security.transactional.active');
INSERT INTO "boolean_settings" VALUES(1,'procedureDesc.ENRICH_WITH_OFFERINGS');
INSERT INTO "boolean_settings" VALUES(1,'procedureDesc.ENRICH_WITH_FEATURES');
INSERT INTO "boolean_settings" VALUES(1,'procedureDesc.ENRICH_WITH_DISCOVERY_INFORMATION');
INSERT INTO "boolean_settings" VALUES(1,'profile.hydrology.overallExtrema');

INSERT INTO "file_settings" VALUES(NULL,'serviceIdentification.file');
INSERT INTO "file_settings" VALUES(NULL,'serviceProvider.file');

INSERT INTO "integer_settings" VALUES(600,'service.lease');
INSERT INTO "integer_settings" VALUES(0,'service.maxGetObservationResults');
INSERT INTO "integer_settings" VALUES(4326,'service.defaultEpsg');
INSERT INTO "integer_settings" VALUES(4979,'service.default3DEpsg');
INSERT INTO "integer_settings" VALUES(1048576,'service.minimumGzipSize');
INSERT INTO "integer_settings" VALUES(5,'service.cacheThreadCount');
INSERT INTO "integer_settings" VALUES(5,'service.capabilitiesCacheUpdateInterval');
INSERT INTO "integer_settings" VALUES(2147483647,'profile.hydrology.maxReturnedValue');
INSERT INTO "integer_settings" VALUES(2147483647,'profile.hydrology.maxReturnedTimeSeries');

INSERT INTO "string_settings" VALUES('+49(0)251/396 371-0','serviceProvider.phone');
INSERT INTO "string_settings" VALUES(',','misc.tokenSeparator');
INSERT INTO "string_settings" VALUES('.','misc.decimalSeperator');
INSERT INTO "string_settings" VALUES('/sensors','service.sensorDirectory');
INSERT INTO "string_settings" VALUES('2044-2045;2081-2083;2085-2086;2093;2096-2098;2105-2132;2169-2170;2176-2180;2193;2200;2206-2212;2319;2320-2462;2523-2549;2551-2735;2738-2758;2935-2941;2953;3006-3030;3034-3035;3058-3059;3068;3114-3118;3126-3138;3300-3301;3328-3335;3346;3350-3352;3366;3416;4001-4999;20004-20032;20064-20092;21413-21423;21473-21483;21896-21899;22171;22181-22187;22191-22197;25884;27205-27232;27391-27398;27492;28402-28432;28462-28492;30161-30179;30800;31251-31259;31275-31279;31281-31290;31466-31700','misc.switchCoordinatesForEpsgCodes');
INSERT INTO "string_settings" VALUES('48155','serviceProvider.postalCode');
INSERT INTO "string_settings" VALUES('52N SOS','serviceIdentification.title');
INSERT INTO "string_settings" VALUES('52North Sensor Observation Service - Data Access for the Sensor Web','serviceIdentification.abstract');
INSERT INTO "string_settings" VALUES('52North','serviceProvider.name');
INSERT INTO "string_settings" VALUES(';','misc.tupleSeparator');
INSERT INTO "string_settings" VALUES('Germany','serviceProvider.country');
INSERT INTO "string_settings" VALUES('http://www.example.org/feature/','misc.defaultFeaturePrefix');
INSERT INTO "string_settings" VALUES('http://www.example.org/observableProperty/','misc.defaultObservablePropertyPrefix');
INSERT INTO "string_settings" VALUES('http://www.example.org/offering/','misc.defaultOfferingPrefix');
INSERT INTO "string_settings" VALUES('http://www.example.org/procedure/','misc.defaultProcedurePrefix');
INSERT INTO "string_settings" VALUES('http://www.opengis.net/def/crs/EPSG/0/','misc.srsNamePrefixSosV2');
INSERT INTO "string_settings" VALUES('info@52north.org','serviceProvider.email');
INSERT INTO "string_settings" VALUES('Martin-Luther-King-Weg 24','serviceProvider.address');
INSERT INTO "string_settings" VALUES('Münster','serviceProvider.city');
INSERT INTO "string_settings" VALUES('NONE','serviceIdentification.accessConstraints');
INSERT INTO "string_settings" VALUES('NONE','serviceIdentification.fees');
INSERT INTO "string_settings" VALUES('North Rhine-Westphalia','serviceProvider.state');
INSERT INTO "string_settings" VALUES('OGC:SOS','serviceIdentification.serviceType');
INSERT INTO "string_settings" VALUES(NULL,'serviceIdentification.serviceTypeCodeSpace');
INSERT INTO "string_settings" VALUES('TBA','serviceProvider.individualName');
INSERT INTO "string_settings" VALUES('TBA','serviceProvider.positionName');
INSERT INTO "string_settings" VALUES('urn:ogc:def:crs:EPSG::','misc.srsNamePrefixSosV1');
INSERT INTO "string_settings" VALUES('UTF-8','misc.characterEncoding');
INSERT INTO "string_settings" VALUES(NULL,'service.configurationFiles');
INSERT INTO "string_settings" VALUES(NULL,'serviceIdentification.keywords');
INSERT INTO "string_settings" VALUES(NULL,'service.transactionalAllowedIps');
INSERT INTO "string_settings" VALUES('127.0.0.1','service.transactionalAllowedProxies');
INSERT INTO "string_settings" VALUES(NULL,'service.transactionalToken');

INSERT INTO "uri_settings" VALUES('http://52north.org/swe','serviceProvider.site');
INSERT INTO "uri_settings" VALUES('http://localhost:8080/52n-sos-webapp/service','service.sosUrl');

INSERT INTO "procedure_encodings" VALUES('http://www.opengis.net/sensorML/1.0.1','SOS','2.0.0',1);
INSERT INTO "procedure_encodings" VALUES('text/xml; subtype="sensorML/1.0.1"','SOS','1.0.0',1);

INSERT INTO "observation_encodings" VALUES('http://www.opengis.net/om/1.0','SOS','1.0.0',1);
INSERT INTO "observation_encodings" VALUES('http://www.opengis.net/om/2.0','SOS','2.0.0',1);
INSERT INTO "observation_encodings" VALUES('http://www.opengis.net/waterml/2.0','SOS','2.0.0',1);
INSERT INTO "observation_encodings" VALUES('text/xml; subtype="om/1.0.0"','SOS','1.0.0',1);

INSERT INTO "operations" VALUES('DeleteObservation','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('DeleteSensor','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('DescribeSensor','SOS','1.0.0',1);
INSERT INTO "operations" VALUES('DescribeSensor','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('GetCapabilities','SOS','1.0.0',1);
INSERT INTO "operations" VALUES('GetCapabilities','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('GetDataAvailability','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('GetFeatureOfInterest','SOS','1.0.0',1);
INSERT INTO "operations" VALUES('GetFeatureOfInterest','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('GetObservation','SOS','1.0.0',1);
INSERT INTO "operations" VALUES('GetObservation','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('GetObservationById','SOS','1.0.0',1);
INSERT INTO "operations" VALUES('GetObservationById','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('GetResult','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('GetResultTemplate','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('InsertObservation','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('InsertResult','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('InsertResultTemplate','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('InsertSensor','SOS','2.0.0',1);
INSERT INTO "operations" VALUES('UpdateSensorDescription','SOS','2.0.0',1);
