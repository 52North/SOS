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

-- Database name to apply changes to
use sos
create table dbo.[procedure] (procedureId bigint identity not null, hibernateDiscriminator char(1) not null, procedureDescriptionFormatId bigint not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespacename bigint, description varchar(255), deleted char(1) default 'F' not null check (deleted in ('T','F')), descriptionFile varchar(MAX), referenceFlag char(1) default 'F' check (referenceFlag in ('T','F')), primary key (procedureId));
create table dbo.blobValue (observationId bigint not null, value varbinary(MAX), primary key (observationId));
create table dbo.booleanValue (observationId bigint not null, value char(1), primary key (observationId), check (value in ('T','F')), check (value in ('T','F')));
create table dbo.categoryValue (observationId bigint not null, value varchar(255), primary key (observationId));
create table dbo.codespace (codespaceId bigint identity not null, codespace varchar(255) not null, primary key (codespaceId));
create table dbo.compositePhenomenon (parentObservablePropertyId bigint not null, childObservablePropertyId bigint not null, primary key (childObservablePropertyId, parentObservablePropertyId));
create table dbo.countValue (observationId bigint not null, value int, primary key (observationId));
create table dbo.featureOfInterest (featureOfInterestId bigint identity not null, hibernateDiscriminator char(1) not null, featureOfInterestTypeId bigint not null, identifier varchar(255), codespace bigint, name varchar(255), codespacename bigint, description varchar(255), geom GEOMETRY, descriptionXml varchar(MAX), url varchar(255), primary key (featureOfInterestId));
create table dbo.featureOfInterestType (featureOfInterestTypeId bigint identity not null, featureOfInterestType varchar(255) not null, primary key (featureOfInterestTypeId));
create table dbo.featureRelation (parentFeatureId bigint not null, childFeatureId bigint not null, primary key (childFeatureId, parentFeatureId));
create table dbo.geometryValue (observationId bigint not null, value GEOMETRY, primary key (observationId));
create table dbo.i18nfeatureOfInterest (id bigint not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
create table dbo.i18nobservableProperty (id bigint not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
create table dbo.i18noffering (id bigint not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
create table dbo.i18nprocedure (id bigint not null, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), shortname varchar(255), longname varchar(255), primary key (id));
create table dbo.numericValue (observationId bigint not null, value double precision, primary key (observationId));
create table dbo.observableProperty (observablePropertyId bigint identity not null, hibernateDiscriminator char(1) not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespacename bigint, description varchar(255), primary key (observablePropertyId));
create table dbo.observation (observationId bigint identity not null, seriesId bigint not null, phenomenonTimeStart datetime2 not null, phenomenonTimeEnd datetime2 not null, resultTime datetime2 not null, identifier varchar(255), codespace bigint, name varchar(255), codespacename bigint, description varchar(255), deleted char(1) default 'F' not null check (deleted in ('T','F')), validTimeStart datetime2, validTimeEnd datetime2, unitId bigint, samplingGeometry GEOMETRY, primary key (observationId));
create table dbo.observationConstellation (observationConstellationId bigint identity not null, observablePropertyId bigint not null, procedureId bigint not null, observationTypeId bigint, offeringId bigint not null, deleted char(1) default 'F' not null check (deleted in ('T','F')), hiddenChild char(1) default 'F' not null check (hiddenChild in ('T','F')), primary key (observationConstellationId));
create table dbo.observationHasOffering (observationId bigint not null, offeringId bigint not null, primary key (observationId, offeringId));
create table dbo.observationType (observationTypeId bigint identity not null, observationType varchar(255) not null, primary key (observationTypeId));
create table dbo.offering (offeringId bigint identity not null, hibernateDiscriminator char(1) not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespacename bigint, description varchar(255), primary key (offeringId));
create table dbo.offeringAllowedFeatureType (offeringId bigint not null, featureOfInterestTypeId bigint not null, primary key (offeringId, featureOfInterestTypeId));
create table dbo.offeringAllowedObservationType (offeringId bigint not null, observationTypeId bigint not null, primary key (offeringId, observationTypeId));
create table dbo.offeringHasRelatedFeature (relatedFeatureId bigint not null, offeringId bigint not null, primary key (offeringId, relatedFeatureId));
create table dbo.parameter (parameterId bigint identity not null, observationId bigint not null, definition varchar(255) not null, title varchar(255), value varbinary(MAX) not null, primary key (parameterId));
create table dbo.procedureDescriptionFormat (procedureDescriptionFormatId bigint identity not null, procedureDescriptionFormat varchar(255) not null, primary key (procedureDescriptionFormatId));
create table dbo.relatedFeature (relatedFeatureId bigint identity not null, featureOfInterestId bigint not null, primary key (relatedFeatureId));
create table dbo.relatedFeatureHasRole (relatedFeatureId bigint not null, relatedFeatureRoleId bigint not null, primary key (relatedFeatureId, relatedFeatureRoleId));
create table dbo.relatedFeatureRole (relatedFeatureRoleId bigint identity not null, relatedFeatureRole varchar(255) not null, primary key (relatedFeatureRoleId));
create table dbo.resultTemplate (resultTemplateId bigint identity not null, offeringId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, featureOfInterestId bigint not null, identifier varchar(255) not null, resultStructure varchar(MAX) not null, resultEncoding varchar(MAX) not null, primary key (resultTemplateId));
create table dbo.sensorSystem (parentSensorId bigint not null, childSensorId bigint not null, primary key (childSensorId, parentSensorId));
create table dbo.series (seriesId bigint identity not null, featureOfInterestId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, deleted char(1) default 'F' not null check (deleted in ('T','F')), published char(1) default 'F' not null check (published in ('T','F')), firstTimeStamp datetime2, lastTimeStamp datetime2, firstNumericValue double precision, lastNumericValue double precision, unitId bigint, primary key (seriesId));
create table dbo.sweDataArrayValue (observationId bigint not null, value varchar(MAX), primary key (observationId));
create table dbo.textValue (observationId bigint not null, value varchar(MAX), primary key (observationId));
create table dbo.unit (unitId bigint identity not null, unit varchar(255) not null, primary key (unitId));
create table dbo.validProcedureTime (validProcedureTimeId bigint identity not null, procedureId bigint not null, procedureDescriptionFormatId bigint not null, startTime datetime2 not null, endTime datetime2, descriptionXml varchar(MAX) not null, primary key (validProcedureTimeId));
alter table dbo.[procedure] add constraint procIdentifierUK unique (identifier);
alter table dbo.codespace add constraint codespaceUK unique (codespace);
alter table dbo.featureOfInterest add constraint foiIdentifierUK unique (identifier);
alter table dbo.featureOfInterest add constraint obsUrl unique (url);
alter table dbo.featureOfInterestType add constraint featureTypeUK unique (featureOfInterestType);
alter table dbo.i18nfeatureOfInterest add constraint i18nFeatureIdentity unique (objectId, locale);
create index i18nFeatureIdx on dbo.i18nfeatureOfInterest (objectId);
alter table dbo.i18nobservableProperty add constraint i18nobsPropIdentity unique (objectId, locale);
create index i18nObsPropIdx on dbo.i18nobservableProperty (objectId);
alter table dbo.i18noffering add constraint i18nOfferingIdentity unique (objectId, locale);
create index i18nOfferingIdx on dbo.i18noffering (objectId);
alter table dbo.i18nprocedure add constraint i18nProcedureIdentity unique (objectId, locale);
create index i18nProcedureIdx on dbo.i18nprocedure (objectId);
alter table dbo.observableProperty add constraint obsPropIdentifierUK unique (identifier);
alter table dbo.observation add constraint observationIdentity unique (seriesId, phenomenonTimeStart, phenomenonTimeEnd, resultTime);
alter table dbo.observation add constraint obsIdentifierUK unique (identifier);
create index obsSeriesIdx on dbo.observation (seriesId);
create index obsPhenTimeStartIdx on dbo.observation (phenomenonTimeStart);
create index obsPhenTimeEndIdx on dbo.observation (phenomenonTimeEnd);
create index obsResultTimeIdx on dbo.observation (resultTime);
create index obsCodespaceIdx on dbo.observation (codespace);
alter table dbo.observationConstellation add constraint obsnConstellationIdentity unique (observablePropertyId, procedureId, offeringId);
create index obsConstObsPropIdx on dbo.observationConstellation (observablePropertyId);
create index obsConstProcedureIdx on dbo.observationConstellation (procedureId);
create index obsConstOfferingIdx on dbo.observationConstellation (offeringId);
create index obshasoffobservationidx on dbo.observationHasOffering (observationId);
create index obshasoffofferingidx on dbo.observationHasOffering (offeringId);
alter table dbo.observationType add constraint observationTypeUK unique (observationType);
alter table dbo.offering add constraint offIdentifierUK unique (identifier);
alter table dbo.procedureDescriptionFormat add constraint procDescFormatUK unique (procedureDescriptionFormat);
alter table dbo.relatedFeatureRole add constraint relFeatRoleUK unique (relatedFeatureRole);
create index resultTempOfferingIdx on dbo.resultTemplate (offeringId);
create index resultTempeObsPropIdx on dbo.resultTemplate (observablePropertyId);
create index resultTempProcedureIdx on dbo.resultTemplate (procedureId);
create index resultTempIdentifierIdx on dbo.resultTemplate (identifier);
alter table dbo.series add constraint seriesIdentity unique (featureOfInterestId, observablePropertyId, procedureId);
create index seriesFeatureIdx on dbo.series (featureOfInterestId);
create index seriesObsPropIdx on dbo.series (observablePropertyId);
create index seriesProcedureIdx on dbo.series (procedureId);
alter table dbo.unit add constraint unitUK unique (unit);
create index validProcedureTimeStartTimeIdx on dbo.validProcedureTime (startTime);
create index validProcedureTimeEndTimeIdx on dbo.validProcedureTime (endTime);
alter table dbo.[procedure] add constraint procProcDescFormatFk foreign key (procedureDescriptionFormatId) references dbo.procedureDescriptionFormat;
alter table dbo."procedure" add constraint procCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo."procedure" add constraint procCodespaceNameFk foreign key (codespacename) references dbo.codespace;
alter table dbo.blobValue add constraint observationBlobValueFk foreign key (observationId) references dbo.observation;
alter table dbo.booleanValue add constraint observationBooleanValueFk foreign key (observationId) references dbo.observation;
alter table dbo.categoryValue add constraint observationCategoryValueFk foreign key (observationId) references dbo.observation;
alter table dbo.compositePhenomenon add constraint observablePropertyChildFk foreign key (childObservablePropertyId) references dbo.observableProperty;
alter table dbo.compositePhenomenon add constraint observablePropertyParentFk foreign key (parentObservablePropertyId) references dbo.observableProperty;
alter table dbo.countValue add constraint observationCountValueFk foreign key (observationId) references dbo.observation;
alter table dbo.featureOfInterest add constraint featureFeatureTypeFk foreign key (featureOfInterestTypeId) references dbo.featureOfInterestType;
alter table dbo.featureOfInterest add constraint featureCodespaceFk foreign key (codespace) references dbo.codespace;
ALTER TABLE dbo.featureofinterest add constraint featureCodespaceNameFk foreign key (codespacename) references dbo.codespace
alter table dbo.featureRelation add constraint featureOfInterestChildFk foreign key (childFeatureId) references dbo.featureOfInterest;
alter table dbo.featureRelation add constraint featureOfInterestParentFk foreign key (parentFeatureId) references dbo.featureOfInterest;
alter table dbo.geometryValue add constraint observationGeometryValueFk foreign key (observationId) references dbo.observation;
alter table dbo.i18nfeatureOfInterest add constraint i18nFeatureFeatureFk foreign key (objectId) references dbo.featureOfInterest;
alter table dbo.i18nobservableProperty add constraint i18nObsPropObsPropFk foreign key (objectId) references dbo.observableProperty;
alter table dbo.i18noffering add constraint i18nOfferingOfferingFk foreign key (objectId) references dbo.offering;
alter table dbo.i18nprocedure add constraint i18nProcedureProcedureFk foreign key (objectId) references dbo.[procedure];
alter table dbo.numericValue add constraint observationNumericValueFk foreign key (observationId) references dbo.observation;
alter table dbo.observableproperty add constraint obsPropCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo.observableproperty add constraint obsPropCodespaceNameFk foreign key (codespacename) references dbo.codespace;
alter table dbo.observation add constraint observationSeriesFk foreign key (seriesId) references dbo.series;
alter table dbo.observation add constraint obsCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo.observation add constraint obsCodespaceNameFk foreign key (codespacename) references dbo.codespace;
alter table dbo.observation add constraint observationUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.observationConstellation add constraint obsConstObsPropFk foreign key (observablePropertyId) references dbo.observableProperty;
alter table dbo.observationConstellation add constraint obsnConstProcedureFk foreign key (procedureId) references dbo.[procedure];
alter table dbo.observationConstellation add constraint obsConstObservationIypeFk foreign key (observationTypeId) references dbo.observationType;
alter table dbo.observationConstellation add constraint obsConstOfferingFk foreign key (offeringId) references dbo.offering;
alter table dbo.observationHasOffering add constraint observationOfferingFk foreign key (offeringId) references dbo.offering;
alter table dbo.observationHasOffering add constraint FK_9ex7hawh3dbplkllmw5w3kvej foreign key (observationId) references dbo.observation;
alter table dbo.offering add constraint offCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo.offering add constraint offCodespaceNameFk foreign key (codespacename) references dbo.codespace;
alter table dbo.offeringAllowedFeatureType add constraint offeringFeatureTypeFk foreign key (featureOfInterestTypeId) references dbo.featureOfInterestType;
alter table dbo.offeringAllowedFeatureType add constraint FK_6vvrdxvd406n48gkm706ow1pt foreign key (offeringId) references dbo.offering;
alter table dbo.offeringAllowedObservationType add constraint offeringObservationTypeFk foreign key (observationTypeId) references dbo.observationType;
alter table dbo.offeringAllowedObservationType add constraint FK_lkljeohulvu7cr26pduyp5bd0 foreign key (offeringId) references dbo.offering;
alter table dbo.offeringHasRelatedFeature add constraint relatedFeatureOfferingFk foreign key (offeringId) references dbo.offering;
alter table dbo.offeringHasRelatedFeature add constraint offeringRelatedFeatureFk foreign key (relatedFeatureId) references dbo.relatedFeature;
alter table dbo.relatedFeature add constraint relatedFeatureFeatureFk foreign key (featureOfInterestId) references dbo.featureOfInterest;
alter table dbo.relatedFeatureHasRole add constraint relatedFeatRelatedFeatRoleFk foreign key (relatedFeatureRoleId) references dbo.relatedFeatureRole;
alter table dbo.relatedFeatureHasRole add constraint FK_6ynwkk91xe8p1uibmjt98sog3 foreign key (relatedFeatureId) references dbo.relatedFeature;
alter table dbo.resultTemplate add constraint resultTemplateOfferingIdx foreign key (offeringId) references dbo.offering;
alter table dbo.resultTemplate add constraint resultTemplateObsPropFk foreign key (observablePropertyId) references dbo.observableProperty;
alter table dbo.resultTemplate add constraint resultTemplateProcedureFk foreign key (procedureId) references dbo.[procedure];
alter table dbo.resultTemplate add constraint resultTemplateFeatureIdx foreign key (featureOfInterestId) references dbo.featureOfInterest;
alter table dbo.sensorSystem add constraint procedureChildFk foreign key (childSensorId) references dbo.[procedure];
alter table dbo.sensorSystem add constraint procedureParenfFk foreign key (parentSensorId) references dbo.[procedure];
alter table dbo.series add constraint seriesFeatureFk foreign key (featureOfInterestId) references dbo.featureOfInterest;
alter table dbo.series add constraint seriesObPropFk foreign key (observablePropertyId) references dbo.observableProperty;
alter table dbo.series add constraint seriesProcedureFk foreign key (procedureId) references dbo.[procedure];
alter table dbo.series add constraint seriesUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.sweDataArrayValue add constraint observationSweDataArrayValueFk foreign key (observationId) references dbo.observation;
alter table dbo.textValue add constraint observationTextValueFk foreign key (observationId) references dbo.observation;
alter table dbo.validProcedureTime add constraint validProcedureTimeProcedureFk foreign key (procedureId) references dbo.[procedure];
alter table dbo.validProcedureTime add constraint validProcProcDescFormatFk foreign key (procedureDescriptionFormatId) references dbo.procedureDescriptionFormat;
DECLARE @ObjectName NVARCHAR(100);SELECT @ObjectName = ccu.CONSTRAINT_NAME FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu, INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc WHERE ccu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME AND ccu.TABLE_NAME='featureOfInterest' AND ccu.COLUMN_NAME='url';IF (OBJECT_ID(@ObjectName, 'UQ') IS NOT NULL) BEGIN EXEC('ALTER TABLE dbo.featureOfInterest DROP CONSTRAINT ' + @ObjectName); END; 
CREATE UNIQUE NONCLUSTERED INDEX featureOfInterest_url ON dbo.featureOfInterest(url)WHERE url IS NOT NULL;
DECLARE @ObjectName2 NVARCHAR(100);SELECT @ObjectName2 = ccu.CONSTRAINT_NAME FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu, INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc WHERE ccu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME AND ccu.TABLE_NAME='featureOfInterest' AND ccu.COLUMN_NAME='identifier';IF (OBJECT_ID(@ObjectName2, 'UQ') IS NOT NULL) BEGIN EXEC('ALTER TABLE dbo.featureOfInterest DROP CONSTRAINT ' + @ObjectName2); END; 
CREATE UNIQUE NONCLUSTERED INDEX featureOfInterest_identifier ON dbo.featureOfInterest(identifier)WHERE identifier IS NOT NULL;
DECLARE @ObjectName3 NVARCHAR(100);SELECT @ObjectName3 = ccu.CONSTRAINT_NAME FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu, INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc WHERE ccu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME AND ccu.TABLE_NAME='observation' AND ccu.COLUMN_NAME='identifier';IF (OBJECT_ID(@ObjectName3, 'UQ') IS NOT NULL) BEGIN EXEC('ALTER TABLE dbo.observation DROP CONSTRAINT ' + @ObjectName3); END; 
CREATE UNIQUE NONCLUSTERED INDEX observation_identifier ON dbo.observation(identifier)WHERE identifier IS NOT NULL;
