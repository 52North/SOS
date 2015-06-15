--
-- Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
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

-- ###############################################################################################
-- # !!! Before you execute this scricpt we recommend to create a dump from the current database #
-- ###############################################################################################

create table blobValue (observationId bigint not null, value longblob, primary key (observationId)) ENGINE=InnoDB;
create table booleanValue (observationId bigint not null, value char(1), primary key (observationId), check (value in ('T','F'))) ENGINE=InnoDB;
create table categoryValue (observationId bigint not null, value varchar(255), primary key (observationId)) ENGINE=InnoDB;
create table codespace (codespaceId bigint not null auto_increment, codespace varchar(255) not null unique, primary key (codespaceId)) ENGINE=InnoDB;
create table compositePhenomenon (parentObservablePropertyId bigint not null, childObservablePropertyId bigint not null, primary key (childObservablePropertyId, parentObservablePropertyId)) ENGINE=InnoDB;
create table countValue (observationId bigint not null, value integer, primary key (observationId)) ENGINE=InnoDB;
create table featureOfInterest (featureOfInterestId bigint not null auto_increment, hibernateDiscriminator char(1) not null, featureOfInterestTypeId bigint not null, identifier varchar(255) unique, codespaceId bigint, name longtext, geom GEOMETRY, descriptionXml longtext, url varchar(255) unique, primary key (featureOfInterestId)) ENGINE=InnoDB;
create table featureOfInterestType (featureOfInterestTypeId bigint not null auto_increment, featureOfInterestType varchar(255) not null unique, primary key (featureOfInterestTypeId)) ENGINE=InnoDB;
create table featureRelation (parentFeatureId bigint not null, childFeatureId bigint not null, primary key (childFeatureId, parentFeatureId)) ENGINE=InnoDB;
create table geometryValue (observationId bigint not null, value GEOMETRY, primary key (observationId)) ENGINE=InnoDB;
create table numericValue (observationId bigint not null, value decimal(19,2), primary key (observationId)) ENGINE=InnoDB;
create table observableProperty (observablePropertyId bigint not null auto_increment, hibernateDiscriminator char(1) not null, identifier varchar(255) not null unique, description varchar(255), disabled char(1) not null default 'F', primary key (observablePropertyId), check (disabled in ('T','F'))) ENGINE=InnoDB;
create table observation (observationId bigint not null auto_increment, featureOfInterestId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, phenomenonTimeStart datetime not null, phenomenonTimeEnd datetime not null, resultTime datetime not null, identifier varchar(255) unique, codespaceId bigint, description varchar(255), deleted char(1) default 'F' not null, validTimeStart datetime, validTimeEnd datetime, unitId bigint, primary key (observationId), unique (featureOfInterestId, observablePropertyId, procedureId, phenomenonTimeStart, phenomenonTimeEnd, resultTime)) ENGINE=InnoDB;
create table observationConstellation (observationConstellationId bigint not null auto_increment, observablePropertyId bigint not null, procedureId bigint not null, observationTypeId bigint, offeringId bigint not null, deleted char(1) default 'F' not null, hiddenChild char(1) default 'F' not null, primary key (observationConstellationId), unique (observablePropertyId, procedureId, offeringId)) ENGINE=InnoDB;
create table observationHasOffering (observationId bigint not null, offeringId bigint not null, primary key (observationId, offeringId)) ENGINE=InnoDB;
create table observationType (observationTypeId bigint not null auto_increment, observationType varchar(255) not null unique, primary key (observationTypeId)) ENGINE=InnoDB;
create table offering (offeringId bigint not null auto_increment, hibernateDiscriminator char(1) not null, identifier varchar(255) not null unique, name varchar(255), disabled char(1) not null default 'F', primary key (offeringId), check (disabled in ('T','F'))) ENGINE=InnoDB;
create table offeringAllowedFeatureType (offeringId bigint not null, featureOfInterestTypeId bigint not null, primary key (offeringId, featureOfInterestTypeId)) ENGINE=InnoDB;
create table offeringAllowedObservationType (offeringId bigint not null, observationTypeId bigint not null, primary key (offeringId, observationTypeId)) ENGINE=InnoDB;
create table offeringHasRelatedFeature (relatedFeatureId bigint not null, offeringId bigint not null, primary key (offeringId, relatedFeatureId)) ENGINE=InnoDB;
create table parameter (parameterId bigint not null auto_increment, observationId bigint not null, definition varchar(255) not null, title varchar(255), value longblob not null, primary key (parameterId)) ENGINE=InnoDB;
create table`procedure`(procedureId bigint not null auto_increment, hibernateDiscriminator char(1) not null, procedureDescriptionFormatId bigint not null, identifier varchar(255) not null unique, deleted char(1) default 'F' not null, descriptionFile longtext, disabled char(1) not null default 'F', primary key (procedureId), check (disabled in ('T','F'))) ENGINE=InnoDB;
create table procedureDescriptionFormat (procedureDescriptionFormatId bigint not null auto_increment, procedureDescriptionFormat varchar(255) not null, primary key (procedureDescriptionFormatId)) ENGINE=InnoDB;
create table relatedFeature (relatedFeatureId bigint not null auto_increment, featureOfInterestId bigint not null, primary key (relatedFeatureId)) ENGINE=InnoDB;
create table relatedFeatureHasRole (relatedFeatureId bigint not null, relatedFeatureRoleId bigint not null, primary key (relatedFeatureId, relatedFeatureRoleId)) ENGINE=InnoDB;
create table relatedFeatureRole (relatedFeatureRoleId bigint not null auto_increment, relatedFeatureRole varchar(255) not null unique, primary key (relatedFeatureRoleId)) ENGINE=InnoDB;
create table resultTemplate (resultTemplateId bigint not null auto_increment, offeringId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, featureOfInterestId bigint not null, identifier varchar(255) not null, resultStructure longtext not null, resultEncoding longtext not null, primary key (resultTemplateId)) ENGINE=InnoDB;
create table sensorSystem (parentSensorId bigint not null, childSensorId bigint not null, primary key (childSensorId, parentSensorId)) ENGINE=InnoDB;
create table spatialFilteringProfile (spatialFilteringProfileId bigint not null auto_increment, observation bigint not null, definition varchar(255) not null, title varchar(255), geom GEOMETRY not null, primary key (spatialFilteringProfileId)) ENGINE=InnoDB;
create table sweDataArrayValue (observationId bigint not null, value longtext, primary key (observationId)) ENGINE=InnoDB;
create table textValue (observationId bigint not null, value longtext, primary key (observationId)) ENGINE=InnoDB;
create table unit (unitId bigint not null auto_increment, unit varchar(255) not null unique, primary key (unitId)) ENGINE=InnoDB;
create table validProcedureTime (validProcedureTimeId bigint not null auto_increment, procedureId bigint not null, procedureDescriptionFormatId bigint not null, startTime datetime not null, endTime datetime, descriptionXml longtext not null, primary key (validProcedureTimeId)) ENGINE=InnoDB;
alter table blobValue add index observationBlobValueFk (observationId), add constraint observationBlobValueFk foreign key (observationId) references observation (observationId);
alter table booleanValue add index observationBooleanValueFk (observationId), add constraint observationBooleanValueFk foreign key (observationId) references observation (observationId);
alter table categoryValue add index observationCategoryValueFk (observationId), add constraint observationCategoryValueFk foreign key (observationId) references observation (observationId);
alter table compositePhenomenon add index observablePropertyChildFk (childObservablePropertyId), add constraint observablePropertyChildFk foreign key (childObservablePropertyId) references observableProperty (observablePropertyId);
alter table compositePhenomenon add index observablePropertyParentFk (parentObservablePropertyId), add constraint observablePropertyParentFk foreign key (parentObservablePropertyId) references observableProperty (observablePropertyId);
alter table countValue add index observationCountValueFk (observationId), add constraint observationCountValueFk foreign key (observationId) references observation (observationId);
alter table featureOfInterest add index featureFeatureTypeFk (featureOfInterestTypeId), add constraint featureFeatureTypeFk foreign key (featureOfInterestTypeId) references featureOfInterestType (featureOfInterestTypeId);
alter table featureOfInterest add index featureCodespaceFk (codespaceId), add constraint featureCodespaceFk foreign key (codespaceId) references codespace (codespaceId);
alter table featureRelation add index featureOfInterestChildFk (childFeatureId), add constraint featureOfInterestChildFk foreign key (childFeatureId) references featureOfInterest (featureOfInterestId);
alter table featureRelation add index featureOfInterestParentFk (parentFeatureId), add constraint featureOfInterestParentFk foreign key (parentFeatureId) references featureOfInterest (featureOfInterestId);
alter table geometryValue add index observationGeometryValueFk (observationId), add constraint observationGeometryValueFk foreign key (observationId) references observation (observationId);
alter table numericValue add index observationNumericValueFk (observationId), add constraint observationNumericValueFk foreign key (observationId) references observation (observationId);
create index obsFeatureIdx on observation (featureOfInterestId);
create index obsObsPropIdx on observation (observablePropertyId);
create index obsProcedureIdx on observation (procedureId);
create index obsPhenTimeStartIdx on observation (phenomenonTimeStart);
create index obsPhenTimeEndIdx on observation (phenomenonTimeEnd);
create index obsResultTimeIdx on observation (resultTime);
create index obsCodespaceIdx on observation (codespaceId);
alter table observation add index observationFeatureFk (featureOfInterestId), add constraint observationFeatureFk foreign key (featureOfInterestId) references featureOfInterest (featureOfInterestId);
alter table observation add index observationObPropFk (observablePropertyId), add constraint observationObPropFk foreign key (observablePropertyId) references observableProperty (observablePropertyId);
alter table observation add index observationProcedureFk (procedureId), add constraint observationProcedureFk foreign key (procedureId) references `procedure` (procedureId);
alter table observation add index observationCodespaceFk (codespaceId), add constraint observationCodespaceFk foreign key (codespaceId) references codespace (codespaceId);
alter table observation add index observationUnitFk (unitId), add constraint observationUnitFk foreign key (unitId) references unit (unitId);
create index obsConstObsPropIdx on observationConstellation (observablePropertyId);
create index obsConstProcedureIdx on observationConstellation (procedureId);
create index obsConstOfferingIdx on observationConstellation (offeringId);
alter table observationConstellation add index obsConstObsPropFk (observablePropertyId), add constraint obsConstObsPropFk foreign key (observablePropertyId) references observableProperty (observablePropertyId);
alter table observationConstellation add index obsnConstProcedureFk (procedureId), add constraint obsnConstProcedureFk foreign key (procedureId) references`procedure`(procedureId);
alter table observationConstellation add index obsConstObservationIypeFk (observationTypeId), add constraint obsConstObservationIypeFk foreign key (observationTypeId) references observationType (observationTypeId);
alter table observationConstellation add index obsConstOfferingFk (offeringId), add constraint obsConstOfferingFk foreign key (offeringId) references offering (offeringId);
create index obshasoffobservationidx on observationHasOffering (observationId);
create index obshasoffofferingidx on observationHasOffering (offeringId);
alter table observationHasOffering add index observationOfferingFk (offeringId), add constraint observationOfferingFk foreign key (offeringId) references offering (offeringId);
alter table observationHasOffering add index FK7D7608F4A0D4D3BD (observationId), add constraint FK7D7608F4A0D4D3BD foreign key (observationId) references observation (observationId);
alter table offeringAllowedFeatureType add index offeringFeatureTypeFk (featureOfInterestTypeId), add constraint offeringFeatureTypeFk foreign key (featureOfInterestTypeId) references featureOfInterestType (featureOfInterestTypeId);
alter table offeringAllowedFeatureType add index FKF68CB72EE4EF3005 (offeringId), add constraint FKF68CB72EE4EF3005 foreign key (offeringId) references offering (offeringId);
alter table offeringAllowedObservationType add index offeringObservationTypeFk (observationTypeId), add constraint offeringObservationTypeFk foreign key (observationTypeId) references observationType (observationTypeId);
alter table offeringAllowedObservationType add index FK28E66A64E4EF3005 (offeringId), add constraint FK28E66A64E4EF3005 foreign key (offeringId) references offering (offeringId);
alter table offeringHasRelatedFeature add index relatedFeatureOfferingFk (offeringId), add constraint relatedFeatureOfferingFk foreign key (offeringId) references offering (offeringId);
alter table offeringHasRelatedFeature add index offeringRelatedFeatureFk (relatedFeatureId), add constraint offeringRelatedFeatureFk foreign key (relatedFeatureId) references relatedFeature (relatedFeatureId);
alter table parameter add index parameterObservationFk (observationId), add constraint parameterObservationFk foreign key (observationId) references observation (observationId);
alter table `procedure` add index procProcDescFormatFk (procedureDescriptionFormatId), add constraint procProcDescFormatFk foreign key (procedureDescriptionFormatId) references procedureDescriptionFormat (procedureDescriptionFormatId);
alter table relatedFeature add index relatedFeatureFeatureFk (featureOfInterestId), add constraint relatedFeatureFeatureFk foreign key (featureOfInterestId) references featureOfInterest (featureOfInterestId);
alter table relatedFeatureHasRole add index relatedFeatRelatedFeatRoleFk (relatedFeatureRoleId), add constraint relatedFeatRelatedFeatRoleFk foreign key (relatedFeatureRoleId) references relatedFeatureRole (relatedFeatureRoleId);
alter table relatedFeatureHasRole add index FK5643E7654A79987 (relatedFeatureId), add constraint FK5643E7654A79987 foreign key (relatedFeatureId) references relatedFeature (relatedFeatureId);
create index resultTempOfferingIdx on resultTemplate (offeringId);
create index resultTempeObsPropIdx on resultTemplate (observablePropertyId);
create index resultTempProcedureIdx on resultTemplate (procedureId);
create index resultTempIdentifierIdx on resultTemplate (identifier);
alter table resultTemplate add index resultTemplateOfferingIdx (offeringId), add constraint resultTemplateOfferingIdx foreign key (offeringId) references offering (offeringId);
alter table resultTemplate add index resultTemplateObsPropFk (observablePropertyId), add constraint resultTemplateObsPropFk foreign key (observablePropertyId) references observableProperty (observablePropertyId);
alter table resultTemplate add index resultTemplateProcedureFk (procedureId), add constraint resultTemplateProcedureFk foreign key (procedureId) references`procedure`(procedureId);
alter table resultTemplate add index resultTemplateFeatureIdx (featureOfInterestId), add constraint resultTemplateFeatureIdx foreign key (featureOfInterestId) references featureOfInterest (featureOfInterestId);
alter table sensorSystem add index procedureChildFk (childSensorId), add constraint procedureChildFk foreign key (childSensorId) references `procedure` (procedureId);
alter table sensorSystem add index procedureParenfFk (parentSensorId), add constraint procedureParenfFk foreign key (parentSensorId) references`procedure`(procedureId);
create index sfpObservationIdx on spatialFilteringProfile (observation);
alter table spatialFilteringProfile add index sfpObservationFK (observation), add constraint sfpObservationFK foreign key (observation) references observation (observationId);
alter table sweDataArrayValue add index observationSweDataArrayValueFk (observationId), add constraint observationSweDataArrayValueFk foreign key (observationId) references observation (observationId);
alter table textValue add index observationTextValueFk (observationId), add constraint observationTextValueFk foreign key (observationId) references observation (observationId);
create index validProcedureTimeStartTimeIdx on validProcedureTime (startTime);
create index validProcedureTimeEndTimeIdx on validProcedureTime (endTime);
alter table validProcedureTime add index validProcedureTimeProcedureFk (procedureId), add constraint validProcedureTimeProcedureFk foreign key (procedureId) references`procedure`(procedureId);
alter table validProcedureTime add index validProcProcDescFormatFk (procedureDescriptionFormatId), add constraint validProcProcDescFormatFk foreign key (procedureDescriptionFormatId) references procedureDescriptionFormat (procedureDescriptionFormatId);