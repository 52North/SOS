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

create table sos.`procedure` (procedureId bigint not null auto_increment, hibernateDiscriminator char(1) not null, procedureDescriptionFormatId bigint not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespacename bigint, description varchar(255), deleted char(1) default 'F' not null, descriptionFile longtext, referenceFlag char(1) default 'F', primary key (procedureId)) ENGINE=InnoDB;
create table sos.blobValue (observationId bigint not null, value longblob, primary key (observationId)) ENGINE=InnoDB;
create table sos.booleanValue (observationId bigint not null, value char(1), primary key (observationId), check (value in ('T','F')), check (value in ('T','F'))) ENGINE=InnoDB;
create table sos.categoryValue (observationId bigint not null, value varchar(255), primary key (observationId)) ENGINE=InnoDB;
create table sos.codespace (codespaceId bigint not null auto_increment, codespace varchar(255) not null, primary key (codespaceId)) ENGINE=InnoDB;
create table sos.compositePhenomenon (parentObservablePropertyId bigint not null, childObservablePropertyId bigint not null, primary key (childObservablePropertyId, parentObservablePropertyId)) ENGINE=InnoDB;
create table sos.countValue (observationId bigint not null, value integer, primary key (observationId)) ENGINE=InnoDB;
create table sos.featureOfInterest (featureOfInterestId bigint not null auto_increment, hibernateDiscriminator char(1) not null, featureOfInterestTypeId bigint not null, identifier varchar(255), codespace bigint, name varchar(255), codespacename bigint, description varchar(255), geom GEOMETRY, descriptionXml longtext, url varchar(255), primary key (featureOfInterestId)) ENGINE=InnoDB;
create table sos.featureOfInterestType (featureOfInterestTypeId bigint not null auto_increment, featureOfInterestType varchar(255) not null, primary key (featureOfInterestTypeId)) ENGINE=InnoDB;
create table sos.featureRelation (parentFeatureId bigint not null, childFeatureId bigint not null, primary key (childFeatureId, parentFeatureId)) ENGINE=InnoDB;
create table sos.geometryValue (observationId bigint not null, value GEOMETRY, primary key (observationId)) ENGINE=InnoDB;
create table sos.i18nfeatureOfInterest (id bigint not null auto_increment, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id)) ENGINE=InnoDB;
create table sos.i18nobservableProperty (id bigint not null auto_increment, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id)) ENGINE=InnoDB;
create table sos.i18noffering (id bigint not null auto_increment, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id)) ENGINE=InnoDB;
create table sos.i18nprocedure (id bigint not null auto_increment, objectId bigint not null, locale varchar(255) not null, name varchar(255), description varchar(255), shortname varchar(255), longname varchar(255), primary key (id)) ENGINE=InnoDB;
create table sos.numericValue (observationId bigint not null, value double, primary key (observationId)) ENGINE=InnoDB;
create table sos.observableProperty (observablePropertyId bigint not null auto_increment, hibernateDiscriminator char(1) not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespacename bigint, description varchar(255), primary key (observablePropertyId)) ENGINE=InnoDB;
create table sos.observation (observationId bigint not null auto_increment, seriesId bigint not null, phenomenonTimeStart datetime not null, phenomenonTimeEnd datetime not null, resultTime datetime not null, identifier varchar(255), codespace bigint, name varchar(255), codespacename bigint, description varchar(255), deleted char(1) default 'F' not null, validTimeStart datetime, validTimeEnd datetime, unitId bigint, samplingGeometry GEOMETRY, primary key (observationId)) ENGINE=InnoDB;
create table sos.observationConstellation (observationConstellationId bigint not null auto_increment, observablePropertyId bigint not null, procedureId bigint not null, observationTypeId bigint, offeringId bigint not null, deleted char(1) default 'F' not null, hiddenChild char(1) default 'F' not null, primary key (observationConstellationId)) ENGINE=InnoDB;
create table sos.observationHasOffering (observationId bigint not null, offeringId bigint not null, primary key (observationId, offeringId)) ENGINE=InnoDB;
create table sos.observationType (observationTypeId bigint not null auto_increment, observationType varchar(255) not null, primary key (observationTypeId)) ENGINE=InnoDB;
create table sos.offering (offeringId bigint not null auto_increment, hibernateDiscriminator char(1) not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespacename bigint, description varchar(255), primary key (offeringId)) ENGINE=InnoDB;
create table sos.offeringAllowedFeatureType (offeringId bigint not null, featureOfInterestTypeId bigint not null, primary key (offeringId, featureOfInterestTypeId)) ENGINE=InnoDB;
create table sos.offeringAllowedObservationType (offeringId bigint not null, observationTypeId bigint not null, primary key (offeringId, observationTypeId)) ENGINE=InnoDB;
create table sos.offeringHasRelatedFeature (relatedFeatureId bigint not null, offeringId bigint not null, primary key (offeringId, relatedFeatureId)) ENGINE=InnoDB;
create table sos.parameter (parameterId bigint not null auto_increment, observationId bigint not null, definition varchar(255) not null, title varchar(255), value longblob not null, primary key (parameterId)) ENGINE=InnoDB;
create table sos.procedureDescriptionFormat (procedureDescriptionFormatId bigint not null auto_increment, procedureDescriptionFormat varchar(255) not null, primary key (procedureDescriptionFormatId)) ENGINE=InnoDB;
create table sos.relatedFeature (relatedFeatureId bigint not null auto_increment, featureOfInterestId bigint not null, primary key (relatedFeatureId)) ENGINE=InnoDB;
create table sos.relatedFeatureHasRole (relatedFeatureId bigint not null, relatedFeatureRoleId bigint not null, primary key (relatedFeatureId, relatedFeatureRoleId)) ENGINE=InnoDB;
create table sos.relatedFeatureRole (relatedFeatureRoleId bigint not null auto_increment, relatedFeatureRole varchar(255) not null, primary key (relatedFeatureRoleId)) ENGINE=InnoDB;
create table sos.resultTemplate (resultTemplateId bigint not null auto_increment, offeringId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, featureOfInterestId bigint not null, identifier varchar(255) not null, resultStructure longtext not null, resultEncoding longtext not null, primary key (resultTemplateId)) ENGINE=InnoDB;
create table sos.sensorSystem (parentSensorId bigint not null, childSensorId bigint not null, primary key (childSensorId, parentSensorId)) ENGINE=InnoDB;
create table sos.series (seriesId bigint not null auto_increment, featureOfInterestId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, deleted char(1) default 'F' not null, published char(1) default 'T' not null, firstTimeStamp datetime, lastTimeStamp datetime, firstNumericValue double precision, lastNumericValue double precision, unitId bigint, primary key (seriesId)) ENGINE=InnoDB;
create table sos.sweDataArrayValue (observationId bigint not null, value longtext, primary key (observationId)) ENGINE=InnoDB;
create table sos.textValue (observationId bigint not null, value longtext, primary key (observationId)) ENGINE=InnoDB;
create table sos.unit (unitId bigint not null auto_increment, unit varchar(255) not null, primary key (unitId)) ENGINE=InnoDB;
create table sos.validProcedureTime (validProcedureTimeId bigint not null auto_increment, procedureId bigint not null, procedureDescriptionFormatId bigint not null, startTime datetime not null, endTime datetime, descriptionXml longtext not null, primary key (validProcedureTimeId)) ENGINE=InnoDB;
alter table sos.`procedure` add constraint procIdentifierUK  unique (identifier);
alter table sos.codespace add constraint codespaceUK  unique (codespace);
alter table sos.featureOfInterest add constraint foiIdentifierUK  unique (identifier);
alter table sos.featureOfInterest add constraint obsUrl  unique (url);
alter table sos.featureOfInterestType add constraint featureTypeUK  unique (featureOfInterestType);
alter table sos.i18nfeatureOfInterest add constraint i18nFeatureIdentity unique (objectId, locale);
create index i18nFeatureIdx on sos.i18nfeatureOfInterest (objectId);
alter table sos.i18nobservableProperty add constraint i18nobsPropIdentity unique (objectId, locale);
create index i18nObsPropIdx on sos.i18nobservableProperty (objectId);
alter table sos.i18noffering add constraint i18nOfferingIdentity unique (objectId, locale);
create index i18nOfferingIdx on sos.i18noffering (objectId);
alter table sos.i18nprocedure add constraint i18nProcedureIdentity unique (objectId, locale);
create index i18nProcedureIdx on sos.i18nprocedure (objectId);
alter table sos.observableProperty add constraint obsPropIdentifierUK  unique (identifier);
alter table sos.observation add constraint observationIdentity  unique (seriesId, phenomenonTimeStart, phenomenonTimeEnd, resultTime);
alter table sos.observation add constraint obsIdentifierUK  unique (identifier);
create index obsSeriesIdx on sos.observation (seriesId);
create index obsPhenTimeStartIdx on sos.observation (phenomenonTimeStart);
create index obsPhenTimeEndIdx on sos.observation (phenomenonTimeEnd);
create index obsResultTimeIdx on sos.observation (resultTime);
create index obsCodespaceIdx on sos.observation (codespace);
alter table sos.observationConstellation add constraint obsnConstellationIdentity  unique (observablePropertyId, procedureId, offeringId);
create index obsConstObsPropIdx on sos.observationConstellation (observablePropertyId);
create index obsConstProcedureIdx on sos.observationConstellation (procedureId);
create index obsConstOfferingIdx on sos.observationConstellation (offeringId);
create index obshasoffobservationidx on sos.observationHasOffering (observationId);
create index obshasoffofferingidx on sos.observationHasOffering (offeringId);
alter table sos.observationType add constraint observationTypeUK  unique (observationType);
alter table sos.offering add constraint offIdentifierUK  unique (identifier);
alter table sos.procedureDescriptionFormat add constraint procDescFormatUK  unique (procedureDescriptionFormat);
alter table sos.relatedFeatureRole add constraint relFeatRoleUK  unique (relatedFeatureRole);
create index resultTempOfferingIdx on sos.resultTemplate (offeringId);
create index resultTempeObsPropIdx on sos.resultTemplate (observablePropertyId);
create index resultTempProcedureIdx on sos.resultTemplate (procedureId);
create index resultTempIdentifierIdx on sos.resultTemplate (identifier);
alter table sos.series add constraint seriesIdentity  unique (featureOfInterestId, observablePropertyId, procedureId);
create index seriesFeatureIdx on sos.series (featureOfInterestId);
create index seriesObsPropIdx on sos.series (observablePropertyId);
create index seriesProcedureIdx on sos.series (procedureId);
alter table sos.unit add constraint unitUK  unique (unit);
create index validProcedureTimeStartTimeIdx on sos.validProcedureTime (startTime);
create index validProcedureTimeEndTimeIdx on sos.validProcedureTime (endTime);
alter table sos.`procedure` add constraint procProcDescFormatFk foreign key (procedureDescriptionFormatId) references sos.procedureDescriptionFormat (procedureDescriptionFormatId);
alter table sos.`procedure` add constraint procCodespaceIdentifierFk foreign key (codespace) references sos.codespace (codespaceId);
alter table sos.`procedure` add constraint procCodespaceNameFk foreign key (codespacename) references sos.codespace (codespaceId);
alter table sos.blobValue add constraint observationBlobValueFk foreign key (observationId) references sos.observation (observationId);
alter table sos.booleanValue add constraint observationBooleanValueFk foreign key (observationId) references sos.observation (observationId);
alter table sos.categoryValue add constraint observationCategoryValueFk foreign key (observationId) references sos.observation (observationId);
alter table sos.compositePhenomenon add constraint observablePropertyChildFk foreign key (childObservablePropertyId) references sos.observableProperty (observablePropertyId);
alter table sos.compositePhenomenon add constraint observablePropertyParentFk foreign key (parentObservablePropertyId) references sos.observableProperty (observablePropertyId);
alter table sos.countValue add constraint observationCountValueFk foreign key (observationId) references sos.observation (observationId);
alter table sos.featureOfInterest add constraint featureFeatureTypeFk foreign key (featureOfInterestTypeId) references sos.featureOfInterestType (featureOfInterestTypeId);
alter table sos.featureOfInterest add constraint featureCodespaceIdentifierFk foreign key (codespace) references sos.codespace (codespaceId);
alter table sos.featureofinterest add constraint featureCodespaceNameFk foreign key (codespacename) references sos.codespace (codespaceId);
alter table sos.featureRelation add constraint featureOfInterestChildFk foreign key (childFeatureId) references sos.featureOfInterest (featureOfInterestId);
alter table sos.featureRelation add constraint featureOfInterestParentFk foreign key (parentFeatureId) references sos.featureOfInterest (featureOfInterestId);
alter table sos.geometryValue add constraint observationGeometryValueFk foreign key (observationId) references sos.observation (observationId);
alter table sos.i18nfeatureOfInterest add constraint i18nFeatureFeatureFk foreign key (objectId) references sos.featureOfInterest (featureOfInterestId);
alter table sos.i18nobservableProperty add constraint i18nObsPropObsPropFk foreign key (objectId) references sos.observableProperty (observablePropertyId);
alter table sos.i18noffering add constraint i18nOfferingOfferingFk foreign key (objectId) references sos.offering (offeringId);
alter table sos.i18nprocedure add constraint i18nProcedureProcedureFk foreign key (objectId) references sos.`procedure` (procedureId);
alter table sos.numericValue add constraint observationNumericValueFk foreign key (observationId) references sos.observation (observationId);
alter table sos.observableproperty add constraint obsPropCodespaceIdentifierFk foreign key (codespace) references sos.codespace (codespaceId);
alter table sos.observableproperty add constraint obsPropCodespaceNameFk foreign key (codespacename) references sos.codespace (codespaceId);
alter table sos.observation add constraint observationSeriesFk foreign key (seriesId) references sos.series (seriesId);
alter table sos.observation add constraint obsCodespaceIdentifierFk foreign key (codespace) references sos.codespace (codespaceId);
alter table sos.observation add constraint obsCodespaceNameFk foreign key (codespacename) references sos.codespace (codespaceId);
alter table sos.observation add constraint observationUnitFk foreign key (unitId) references sos.unit (unitId);
alter table sos.observationConstellation add constraint obsConstObsPropFk foreign key (observablePropertyId) references sos.observableProperty (observablePropertyId);
alter table sos.observationConstellation add constraint obsnConstProcedureFk foreign key (procedureId) references sos.`procedure` (procedureId);
alter table sos.observationConstellation add constraint obsConstObservationIypeFk foreign key (observationTypeId) references sos.observationType (observationTypeId);
alter table sos.observationConstellation add constraint obsConstOfferingFk foreign key (offeringId) references sos.offering (offeringId);
alter table sos.observationHasOffering add constraint observationOfferingFk foreign key (offeringId) references sos.offering (offeringId);
alter table sos.observationHasOffering add constraint FK_9ex7hawh3dbplkllmw5w3kvej foreign key (observationId) references sos.observation (observationId);
alter table sos.offeringAllowedFeatureType add constraint offeringFeatureTypeFk foreign key (featureOfInterestTypeId) references sos.featureOfInterestType (featureOfInterestTypeId);
alter table sos.offeringAllowedFeatureType add constraint FK_6vvrdxvd406n48gkm706ow1pt foreign key (offeringId) references sos.offering (offeringId);
alter table sos.offeringAllowedObservationType add constraint offeringObservationTypeFk foreign key (observationTypeId) references sos.observationType (observationTypeId);
alter table sos.offeringAllowedObservationType add constraint FK_lkljeohulvu7cr26pduyp5bd0 foreign key (offeringId) references sos.offering (offeringId);
alter table sos.offeringHasRelatedFeature add constraint relatedFeatureOfferingFk foreign key (offeringId) references sos.offering (offeringId);
alter table sos.offeringHasRelatedFeature add constraint offeringRelatedFeatureFk foreign key (relatedFeatureId) references sos.relatedFeature (relatedFeatureId);
alter table sos.offering add constraint offCodespaceIdentifierFk foreign key (codespace) references sos.codespace (codespaceId);
alter table sos.offering add constraint offCodespaceNameFk foreign key (codespacename) references sos.codespace (codespaceId);
alter table sos.relatedFeature add constraint relatedFeatureFeatureFk foreign key (featureOfInterestId) references sos.featureOfInterest (featureOfInterestId);
alter table sos.relatedFeatureHasRole add constraint relatedFeatRelatedFeatRoleFk foreign key (relatedFeatureRoleId) references sos.relatedFeatureRole (relatedFeatureRoleId);
alter table sos.relatedFeatureHasRole add constraint FK_6ynwkk91xe8p1uibmjt98sog3 foreign key (relatedFeatureId) references sos.relatedFeature (relatedFeatureId);
alter table sos.resultTemplate add constraint resultTemplateOfferingIdx foreign key (offeringId) references sos.offering (offeringId);
alter table sos.resultTemplate add constraint resultTemplateObsPropFk foreign key (observablePropertyId) references sos.observableProperty (observablePropertyId);
alter table sos.resultTemplate add constraint resultTemplateProcedureFk foreign key (procedureId) references sos.`procedure` (procedureId);
alter table sos.resultTemplate add constraint resultTemplateFeatureIdx foreign key (featureOfInterestId) references sos.featureOfInterest (featureOfInterestId);
alter table sos.sensorSystem add constraint procedureChildFk foreign key (childSensorId) references sos.`procedure` (procedureId);
alter table sos.sensorSystem add constraint procedureParenfFk foreign key (parentSensorId) references sos.`procedure` (procedureId);
alter table sos.series add constraint seriesFeatureFk foreign key (featureOfInterestId) references sos.featureOfInterest (featureOfInterestId);
alter table sos.series add constraint seriesObPropFk foreign key (observablePropertyId) references sos.observableProperty (observablePropertyId);
alter table sos.series add constraint seriesProcedureFk foreign key (procedureId) references sos.`procedure` (procedureId);
alter table sos.series add constraint seriesUnitFk foreign key (unitId) references sos.unit (unitId);
alter table sos.sweDataArrayValue add constraint observationSweDataArrayValueFk foreign key (observationId) references sos.observation (observationId);
alter table sos.textValue add constraint observationTextValueFk foreign key (observationId) references sos.observation (observationId);
alter table sos.validProcedureTime add constraint validProcedureTimeProcedureFk foreign key (procedureId) references sos.`procedure` (procedureId);
alter table sos.validProcedureTime add constraint validProcProcDescFormatFk foreign key (procedureDescriptionFormatId) references sos.procedureDescriptionFormat (procedureDescriptionFormatId);
