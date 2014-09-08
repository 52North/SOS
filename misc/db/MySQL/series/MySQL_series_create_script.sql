--
-- Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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

create table `procedure` (procedureId bigint not null auto_increment, hibernateDiscriminator char(1) not null, procedureDescriptionFormatId bigint not null, identifier varchar(255) not null, deleted char(1) default 'F' not null, descriptionFile longtext, referenceFlag char(1) default 'F', primary key (procedureId)) ENGINE=InnoDB;
create table blobValue (observationId bigint not null, value longblob, primary key (observationId)) ENGINE=InnoDB;
create table booleanValue (observationId bigint not null, value char(1), primary key (observationId), check (value in ('T','F')), check (value in ('T','F'))) ENGINE=InnoDB;
create table categoryValue (observationId bigint not null, value varchar(255), primary key (observationId)) ENGINE=InnoDB;
create table codespace (codespaceId bigint not null auto_increment, codespace varchar(255) not null, primary key (codespaceId)) ENGINE=InnoDB;
create table compositePhenomenon (parentObservablePropertyId bigint not null, childObservablePropertyId bigint not null, primary key (childObservablePropertyId, parentObservablePropertyId)) ENGINE=InnoDB;
create table countValue (observationId bigint not null, value integer, primary key (observationId)) ENGINE=InnoDB;
create table featureOfInterest (featureOfInterestId bigint not null auto_increment, hibernateDiscriminator char(1) not null, featureOfInterestTypeId bigint not null, identifier varchar(255), codespaceId bigint, name longtext, geom GEOMETRY, descriptionXml longtext, url varchar(255), primary key (featureOfInterestId)) ENGINE=InnoDB;
create table featureOfInterestType (featureOfInterestTypeId bigint not null auto_increment, featureOfInterestType varchar(255) not null, primary key (featureOfInterestTypeId)) ENGINE=InnoDB;
create table featureRelation (parentFeatureId bigint not null, childFeatureId bigint not null, primary key (childFeatureId, parentFeatureId)) ENGINE=InnoDB;
create table geometryValue (observationId bigint not null, value GEOMETRY, primary key (observationId)) ENGINE=InnoDB;
create table numericValue (observationId bigint not null, value double precision, primary key (observationId)) ENGINE=InnoDB;
create table observableProperty (observablePropertyId bigint not null auto_increment, hibernateDiscriminator char(1) not null, identifier varchar(255) not null, description varchar(255), primary key (observablePropertyId)) ENGINE=InnoDB;
create table observation (observationId bigint not null auto_increment, seriesId bigint not null, phenomenonTimeStart datetime not null, phenomenonTimeEnd datetime not null, resultTime datetime not null, identifier varchar(255), codespaceId bigint, description varchar(255), deleted char(1) default 'F' not null, validTimeStart datetime, validTimeEnd datetime, unitId bigint, samplingGeometry GEOMETRY, primary key (observationId)) ENGINE=InnoDB;
create table observationConstellation (observationConstellationId bigint not null auto_increment, observablePropertyId bigint not null, procedureId bigint not null, observationTypeId bigint, offeringId bigint not null, deleted char(1) default 'F' not null, hiddenChild char(1) default 'F' not null, primary key (observationConstellationId)) ENGINE=InnoDB;
create table observationHasOffering (observationId bigint not null, offeringId bigint not null, primary key (observationId, offeringId)) ENGINE=InnoDB;
create table observationType (observationTypeId bigint not null auto_increment, observationType varchar(255) not null, primary key (observationTypeId)) ENGINE=InnoDB;
create table offering (offeringId bigint not null auto_increment, hibernateDiscriminator char(1) not null, identifier varchar(255) not null, name varchar(255), primary key (offeringId)) ENGINE=InnoDB;
create table offeringAllowedFeatureType (offeringId bigint not null, featureOfInterestTypeId bigint not null, primary key (offeringId, featureOfInterestTypeId)) ENGINE=InnoDB;
create table offeringAllowedObservationType (offeringId bigint not null, observationTypeId bigint not null, primary key (offeringId, observationTypeId)) ENGINE=InnoDB;
create table offeringHasRelatedFeature (relatedFeatureId bigint not null, offeringId bigint not null, primary key (offeringId, relatedFeatureId)) ENGINE=InnoDB;
create table parameter (parameterId bigint not null auto_increment, observationId bigint not null, definition varchar(255) not null, title varchar(255), value longblob not null, primary key (parameterId)) ENGINE=InnoDB;
create table procedureDescriptionFormat (procedureDescriptionFormatId bigint not null auto_increment, procedureDescriptionFormat varchar(255) not null, primary key (procedureDescriptionFormatId)) ENGINE=InnoDB;
create table relatedFeature (relatedFeatureId bigint not null auto_increment, featureOfInterestId bigint not null, primary key (relatedFeatureId)) ENGINE=InnoDB;
create table relatedFeatureHasRole (relatedFeatureId bigint not null, relatedFeatureRoleId bigint not null, primary key (relatedFeatureId, relatedFeatureRoleId)) ENGINE=InnoDB;
create table relatedFeatureRole (relatedFeatureRoleId bigint not null auto_increment, relatedFeatureRole varchar(255) not null, primary key (relatedFeatureRoleId)) ENGINE=InnoDB;
create table resultTemplate (resultTemplateId bigint not null auto_increment, offeringId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, featureOfInterestId bigint not null, identifier varchar(255) not null, resultStructure longtext not null, resultEncoding longtext not null, primary key (resultTemplateId)) ENGINE=InnoDB;
create table sensorSystem (parentSensorId bigint not null, childSensorId bigint not null, primary key (childSensorId, parentSensorId)) ENGINE=InnoDB;
create table series (seriesId bigint not null auto_increment, featureOfInterestId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, deleted char(1) default 'F' not null, firstTimeStamp datetime, lastTimeStamp datetime, firstNumericValue double precision, lastNumericValue double precision, unitId bigint, primary key (seriesId)) ENGINE=InnoDB;
create table sweDataArrayValue (observationId bigint not null, value longtext, primary key (observationId)) ENGINE=InnoDB;
create table textValue (observationId bigint not null, value longtext, primary key (observationId)) ENGINE=InnoDB;
create table unit (unitId bigint not null auto_increment, unit varchar(255) not null, primary key (unitId)) ENGINE=InnoDB;
create table validProcedureTime (validProcedureTimeId bigint not null auto_increment, procedureId bigint not null, procedureDescriptionFormatId bigint not null, startTime datetime not null, endTime datetime, descriptionXml longtext not null, primary key (validProcedureTimeId)) ENGINE=InnoDB;
alter table `procedure` add constraint procIdentifierUK  unique (identifier);
alter table codespace add constraint codespaceUK  unique (codespace);
alter table featureOfInterest add constraint foiIdentifierUK  unique (identifier);
alter table featureOfInterest add constraint obsUrl  unique (url);
alter table featureOfInterestType add constraint featureTypeUK  unique (featureOfInterestType);
alter table observableProperty add constraint obsPropIdentifierUK  unique (identifier);
alter table observation add constraint observationIdentity  unique (seriesId, phenomenonTimeStart, phenomenonTimeEnd, resultTime);
alter table observation add constraint obsIdentifierUK  unique (identifier);
create index obsSeriesIdx on observation (seriesId);
create index obsPhenTimeStartIdx on observation (phenomenonTimeStart);
create index obsPhenTimeEndIdx on observation (phenomenonTimeEnd);
create index obsResultTimeIdx on observation (resultTime);
create index obsCodespaceIdx on observation (codespaceId);
alter table observationConstellation add constraint obsnConstellationIdentity  unique (observablePropertyId, procedureId, offeringId);
create index obsConstObsPropIdx on observationConstellation (observablePropertyId);
create index obsConstProcedureIdx on observationConstellation (procedureId);
create index obsConstOfferingIdx on observationConstellation (offeringId);
create index obshasoffobservationidx on observationHasOffering (observationId);
create index obshasoffofferingidx on observationHasOffering (offeringId);
alter table observationType add constraint observationTypeUK  unique (observationType);
alter table offering add constraint offIdentifierUK  unique (identifier);
alter table procedureDescriptionFormat add constraint procDescFormatUK  unique (procedureDescriptionFormat);
alter table relatedFeatureRole add constraint relFeatRoleUK  unique (relatedFeatureRole);
create index resultTempOfferingIdx on resultTemplate (offeringId);
create index resultTempeObsPropIdx on resultTemplate (observablePropertyId);
create index resultTempProcedureIdx on resultTemplate (procedureId);
create index resultTempIdentifierIdx on resultTemplate (identifier);
alter table series add constraint seriesIdentity  unique (featureOfInterestId, observablePropertyId, procedureId);
create index seriesFeatureIdx on series (featureOfInterestId);
create index seriesObsPropIdx on series (observablePropertyId);
create index seriesProcedureIdx on series (procedureId);
alter table unit add constraint unitUK  unique (unit);
create index validProcedureTimeStartTimeIdx on validProcedureTime (startTime);
create index validProcedureTimeEndTimeIdx on validProcedureTime (endTime);
alter table `procedure` add constraint procProcDescFormatFk foreign key (procedureDescriptionFormatId) references procedureDescriptionFormat (procedureDescriptionFormatId);
alter table blobValue add constraint observationBlobValueFk foreign key (observationId) references observation (observationId);
alter table booleanValue add constraint observationBooleanValueFk foreign key (observationId) references observation (observationId);
alter table categoryValue add constraint observationCategoryValueFk foreign key (observationId) references observation (observationId);
alter table compositePhenomenon add constraint observablePropertyChildFk foreign key (childObservablePropertyId) references observableProperty (observablePropertyId);
alter table compositePhenomenon add constraint observablePropertyParentFk foreign key (parentObservablePropertyId) references observableProperty (observablePropertyId);
alter table countValue add constraint observationCountValueFk foreign key (observationId) references observation (observationId);
alter table featureOfInterest add constraint featureFeatureTypeFk foreign key (featureOfInterestTypeId) references featureOfInterestType (featureOfInterestTypeId);
alter table featureOfInterest add constraint featureCodespaceFk foreign key (codespaceId) references codespace (codespaceId);
alter table featureRelation add constraint featureOfInterestChildFk foreign key (childFeatureId) references featureOfInterest (featureOfInterestId);
alter table featureRelation add constraint featureOfInterestParentFk foreign key (parentFeatureId) references featureOfInterest (featureOfInterestId);
alter table geometryValue add constraint observationGeometryValueFk foreign key (observationId) references observation (observationId);
alter table numericValue add constraint observationNumericValueFk foreign key (observationId) references observation (observationId);
alter table observation add constraint observationSeriesFk foreign key (seriesId) references series (seriesId);
alter table observation add constraint observationCodespaceFk foreign key (codespaceId) references codespace (codespaceId);
alter table observation add constraint observationUnitFk foreign key (unitId) references unit (unitId);
alter table observationConstellation add constraint obsConstObsPropFk foreign key (observablePropertyId) references observableProperty (observablePropertyId);
alter table observationConstellation add constraint obsnConstProcedureFk foreign key (procedureId) references `procedure` (procedureId);
alter table observationConstellation add constraint obsConstObservationIypeFk foreign key (observationTypeId) references observationType (observationTypeId);
alter table observationConstellation add constraint obsConstOfferingFk foreign key (offeringId) references offering (offeringId);
alter table observationHasOffering add constraint observationOfferingFk foreign key (offeringId) references offering (offeringId);
alter table observationHasOffering add constraint FK_9ex7hawh3dbplkllmw5w3kvej foreign key (observationId) references observation (observationId);
alter table offeringAllowedFeatureType add constraint offeringFeatureTypeFk foreign key (featureOfInterestTypeId) references featureOfInterestType (featureOfInterestTypeId);
alter table offeringAllowedFeatureType add constraint FK_6vvrdxvd406n48gkm706ow1pt foreign key (offeringId) references offering (offeringId);
alter table offeringAllowedObservationType add constraint offeringObservationTypeFk foreign key (observationTypeId) references observationType (observationTypeId);
alter table offeringAllowedObservationType add constraint FK_lkljeohulvu7cr26pduyp5bd0 foreign key (offeringId) references offering (offeringId);
alter table offeringHasRelatedFeature add constraint relatedFeatureOfferingFk foreign key (offeringId) references offering (offeringId);
alter table offeringHasRelatedFeature add constraint offeringRelatedFeatureFk foreign key (relatedFeatureId) references relatedFeature (relatedFeatureId);
alter table relatedFeature add constraint relatedFeatureFeatureFk foreign key (featureOfInterestId) references featureOfInterest (featureOfInterestId);
alter table relatedFeatureHasRole add constraint relatedFeatRelatedFeatRoleFk foreign key (relatedFeatureRoleId) references relatedFeatureRole (relatedFeatureRoleId);
alter table relatedFeatureHasRole add constraint FK_6ynwkk91xe8p1uibmjt98sog3 foreign key (relatedFeatureId) references relatedFeature (relatedFeatureId);
alter table resultTemplate add constraint resultTemplateOfferingIdx foreign key (offeringId) references offering (offeringId);
alter table resultTemplate add constraint resultTemplateObsPropFk foreign key (observablePropertyId) references observableProperty (observablePropertyId);
alter table resultTemplate add constraint resultTemplateProcedureFk foreign key (procedureId) references `procedure` (procedureId);
alter table resultTemplate add constraint resultTemplateFeatureIdx foreign key (featureOfInterestId) references featureOfInterest (featureOfInterestId);
alter table sensorSystem add constraint procedureChildFk foreign key (childSensorId) references `procedure` (procedureId);
alter table sensorSystem add constraint procedureParenfFk foreign key (parentSensorId) references `procedure` (procedureId);
alter table series add constraint seriesFeatureFk foreign key (featureOfInterestId) references featureOfInterest (featureOfInterestId);
alter table series add constraint seriesObPropFk foreign key (observablePropertyId) references observableProperty (observablePropertyId);
alter table series add constraint seriesProcedureFk foreign key (procedureId) references `procedure` (procedureId);
alter table series add constraint seriesUnitFk foreign key (unitId) references unit (unitId);
alter table sweDataArrayValue add constraint observationSweDataArrayValueFk foreign key (observationId) references observation (observationId);
alter table textValue add constraint observationTextValueFk foreign key (observationId) references observation (observationId);
alter table validProcedureTime add constraint validProcedureTimeProcedureFk foreign key (procedureId) references `procedure` (procedureId);
alter table validProcedureTime add constraint validProcProcDescFormatFk foreign key (procedureDescriptionFormatId) references procedureDescriptionFormat (procedureDescriptionFormatId);