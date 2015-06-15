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

create table parameter (parameterId number(19,0) not null, observationId number(19,0) not null, definition varchar2(255 char) not null, title varchar2(255 char), value blob not null, primary key (parameterId));
create table blobValue (observationId number(19,0) not null, value blob, primary key (observationId));
create table booleanValue (observationId number(19,0) not null, value char(1 char), primary key (observationId), check (value in ('T','F')));
create table categoryValue (observationId number(19,0) not null, value varchar2(255 char), primary key (observationId));
create table codespace (codespaceId number(19,0) not null, codespace varchar2(255 char) not null unique, primary key (codespaceId));
create table compositePhenomenon (parentObservablePropertyId number(19,0) not null, childObservablePropertyId number(19,0) not null, primary key (childObservablePropertyId, parentObservablePropertyId));
create table countValue (observationId number(19,0) not null, value number(10,0), primary key (observationId));
create table featureOfInterest (featureOfInterestId number(19,0) not null, hibernateDiscriminator char(1 char) not null, featureOfInterestTypeId number(19,0) not null, identifier varchar2(255 char) unique, codespaceId number(19,0), name clob, geom SDO_GEOMETRY, descriptionXml clob, url varchar2(255 char) unique, primary key (featureOfInterestId));
create table featureOfInterestType (featureOfInterestTypeId number(19,0) not null, featureOfInterestType varchar2(255 char) not null unique, primary key (featureOfInterestTypeId));
create table featureRelation (parentFeatureId number(19,0) not null, childFeatureId number(19,0) not null, primary key (childFeatureId, parentFeatureId));
create table geometryValue (observationId number(19,0) not null, value SDO_GEOMETRY, primary key (observationId));
create table numericValue (observationId number(19,0) not null, value number(19,2), primary key (observationId));
create table observableProperty (observablePropertyId number(19,0) not null, hibernateDiscriminator char(1 char) not null, identifier varchar2(255 char) not null unique, description varchar2(255 char), disabled char(1 char) default 'F' not null check (disabled in ('T','F')), primary key (observablePropertyId));
create table observation (observationId number(19,0) not null, featureOfInterestId number(19,0) not null, observablePropertyId number(19,0) not null, procedureId number(19,0) not null, phenomenonTimeStart timestamp not null, phenomenonTimeEnd timestamp not null, resultTime timestamp not null, identifier varchar2(255 char) unique, codespaceId number(19,0), deleted char(1 char) default 'F' not null check (deleted in ('T','F')), validTimeStart timestamp, validTimeEnd timestamp, unitId number(19,0), primary key (observationId), unique (featureOfInterestId, observablePropertyId, procedureId, phenomenonTimeStart, phenomenonTimeEnd, resultTime));
create table observationConstellation (observationConstellationId number(19,0) not null, observablePropertyId number(19,0) not null, procedureId number(19,0) not null, observationTypeId number(19,0), offeringId number(19,0) not null, deleted char(1 char) default 'F' not null check (deleted in ('T','F')), hiddenChild char(1 char) default 'F' not null check (hiddenChild in ('T','F')), primary key (observationConstellationId), unique (observablePropertyId, procedureId, offeringId));
create table observationHasOffering (observationId number(19,0) not null, offeringId number(19,0) not null, primary key (observationId, offeringId));
create table observationType (observationTypeId number(19,0) not null, observationType varchar2(255 char) not null unique, primary key (observationTypeId));
create table offering (offeringId number(19,0) not null, hibernateDiscriminator char(1 char) not null, identifier varchar2(255 char) not null unique, name varchar2(255 char), disabled char(1 char) default 'F' not null check (disabled in ('T','F')), primary key (offeringId));
create table offeringAllowedFeatureType (offeringId number(19,0) not null, featureOfInterestTypeId number(19,0) not null, primary key (offeringId, featureOfInterestTypeId));
create table offeringAllowedObservationType (offeringId number(19,0) not null, observationTypeId number(19,0) not null, primary key (offeringId, observationTypeId));
create table offeringHasRelatedFeature (relatedFeatureId number(19,0) not null, offeringId number(19,0) not null, primary key (offeringId, relatedFeatureId));
create table procedure (procedureId number(19,0) not null, hibernateDiscriminator char(1 char) not null, procedureDescriptionFormatId number(19,0) not null, identifier varchar2(255 char) not null unique, deleted char(1 char) default 'F' not null check (deleted in ('T','F')), descriptionFile clob, disabled char(1 char) default 'F' not null check (disabled in ('T','F')), primary key (procedureId));
create table procedureDescriptionFormat (procedureDescriptionFormatId number(19,0) not null, procedureDescriptionFormat varchar2(255 char) not null, primary key (procedureDescriptionFormatId));
create table relatedFeature (relatedFeatureId number(19,0) not null, featureOfInterestId number(19,0) not null, primary key (relatedFeatureId));
create table relatedFeatureHasRole (relatedFeatureId number(19,0) not null, relatedFeatureRoleId number(19,0) not null, primary key (relatedFeatureId, relatedFeatureRoleId));
create table relatedFeatureRole (relatedFeatureRoleId number(19,0) not null, relatedFeatureRole varchar2(255 char) not null unique, primary key (relatedFeatureRoleId));
create table resultTemplate (resultTemplateId number(19,0) not null, offeringId number(19,0) not null, observablePropertyId number(19,0) not null, procedureId number(19,0) not null, featureOfInterestId number(19,0) not null, identifier varchar2(255 char) not null, resultStructure clob not null, resultEncoding clob not null, primary key (resultTemplateId));
create table sensorSystem (parentSensorId number(19,0) not null, childSensorId number(19,0) not null, primary key (childSensorId, parentSensorId));
create table spatialFilteringProfile (spatialFilteringProfileId number(19,0) not null, observation number(19,0) not null, definition varchar2(255 char) not null, title varchar2(255 char), geom SDO_GEOMETRY not null, primary key (spatialFilteringProfileId));
create table sweDataArrayValue (observationId number(19,0) not null, value clob, primary key (observationId));
create table textValue (observationId number(19,0) not null, value clob, primary key (observationId));
create table unit (unitId number(19,0) not null, unit varchar2(255 char) not null unique, primary key (unitId));
create table validProcedureTime (validProcedureTimeId number(19,0) not null, procedureId number(19,0) not null, procedureDescriptionFormatId number(19,0) not null, startTime timestamp not null, endTime timestamp, descriptionXml clob not null, primary key (validProcedureTimeId));
alter table blobValue add constraint observationBlobValueFk foreign key (observationId) references observation;
alter table booleanValue add constraint observationBooleanValueFk foreign key (observationId) references observation;
alter table categoryValue add constraint observationCategoryValueFk foreign key (observationId) references observation;
alter table compositePhenomenon add constraint observablePropertyChildFk foreign key (childObservablePropertyId) references observableProperty;
alter table compositePhenomenon add constraint observablePropertyParentFk foreign key (parentObservablePropertyId) references observableProperty;
alter table countValue add constraint observationCountValueFk foreign key (observationId) references observation;
alter table featureOfInterest add constraint featureFeatureTypeFk foreign key (featureOfInterestTypeId) references featureOfInterestType;
alter table featureOfInterest add constraint featureCodespaceFk foreign key (codespaceId) references codespace;
alter table featureRelation add constraint featureOfInterestChildFk foreign key (childFeatureId) references featureOfInterest;
alter table featureRelation add constraint featureOfInterestParentFk foreign key (parentFeatureId) references featureOfInterest;
alter table geometryValue add constraint observationGeometryValueFk foreign key (observationId) references observation;
alter table numericValue add constraint observationNumericValueFk foreign key (observationId) references observation;
create index obsFeatureIdx on observation (featureOfInterestId);
create index obsObsPropIdx on observation (observablePropertyId);
create index obsProcedureIdx on observation (procedureId);
create index obsPhenTimeStartIdx on observation (phenomenonTimeStart);
create index obsPhenTimeEndIdx on observation (phenomenonTimeEnd);
create index obsResultTimeIdx on observation (resultTime);
create index obsCodespaceIdx on observation (codespaceId);
alter table observation add constraint observationFeatureFk foreign key (featureOfInterestId) references featureOfInterest;
alter table observation add constraint observationObPropFk foreign key (observablePropertyId) references observableProperty;
alter table observation add constraint observationProcedureFk foreign key (procedureId) references procedure;
alter table observation add constraint observationCodespaceFk foreign key (codespaceId) references codespace;
alter table observation add constraint observationUnitFk foreign key (unitId) references unit;
create index obsConstObsPropIdx on observationConstellation (observablePropertyId);
create index obsConstProcedureIdx on observationConstellation (procedureId);
create index obsConstOfferingIdx on observationConstellation (offeringId);
alter table observationConstellation add constraint obsConstObsPropFk foreign key (observablePropertyId) references observableProperty;
alter table observationConstellation add constraint obsnConstProcedureFk foreign key (procedureId) references procedure;
alter table observationConstellation add constraint obsConstObservationIypeFk foreign key (observationTypeId) references observationType;
alter table observationConstellation add constraint obsConstOfferingFk foreign key (offeringId) references offering;
create index obshasoffobservationidx on observationHasOffering (observationId);
create index obshasoffofferingidx on observationHasOffering (offeringId);
alter table observationHasOffering add constraint observationOfferingFk foreign key (offeringId) references offering;
alter table observationHasOffering add constraint FK7D7608F4A0D4D3BD foreign key (observationId) references observation;
alter table offeringAllowedFeatureType add constraint offeringFeatureTypeFk foreign key (featureOfInterestTypeId) references featureOfInterestType;
alter table offeringAllowedFeatureType add constraint FKF68CB72EE4EF3005 foreign key (offeringId) references offering;
alter table offeringAllowedObservationType add constraint offeringObservationTypeFk foreign key (observationTypeId) references observationType;
alter table offeringAllowedObservationType add constraint FK28E66A64E4EF3005 foreign key (offeringId) references offering;
alter table offeringHasRelatedFeature add constraint relatedFeatureOfferingFk foreign key (offeringId) references offering;
alter table offeringHasRelatedFeature add constraint offeringRelatedFeatureFk foreign key (relatedFeatureId) references relatedFeature;
alter table parameter add constraint parameterObservationFk foreign key (observationId) references observation;
alter table procedure add constraint procProcDescFormatFk foreign key (procedureDescriptionFormatId) references procedureDescriptionFormat;
alter table relatedFeature add constraint relatedFeatureFeatureFk foreign key (featureOfInterestId) references featureOfInterest;
alter table relatedFeatureHasRole add constraint relatedFeatRelatedFeatRoleFk foreign key (relatedFeatureRoleId) references relatedFeatureRole;
alter table relatedFeatureHasRole add constraint FK5643E7654A79987 foreign key (relatedFeatureId) references relatedFeature;
create index resultTempOfferingIdx on resultTemplate (offeringId);
create index resultTempeObsPropIdx on resultTemplate (observablePropertyId);
create index resultTempProcedureIdx on resultTemplate (procedureId);
create index resultTempIdentifierIdx on resultTemplate (identifier);
alter table resultTemplate add constraint resultTemplateOfferingIdx foreign key (offeringId) references offering;
alter table resultTemplate add constraint resultTemplateObsPropFk foreign key (observablePropertyId) references observableProperty;
alter table resultTemplate add constraint resultTemplateProcedureFk foreign key (procedureId) references procedure;
alter table resultTemplate add constraint resultTemplateFeatureIdx foreign key (featureOfInterestId) references featureOfInterest;
alter table sensorSystem add constraint procedureChildFk foreign key (childSensorId) references procedure;
alter table sensorSystem add constraint procedureParenfFk foreign key (parentSensorId) references procedure;
create index sfpObservationIdx on spatialFilteringProfile (observation);
alter table spatialFilteringProfile add constraint sfpObservationFK foreign key (observation) references observation;
alter table sweDataArrayValue add constraint observationSweDataArrayValueFk foreign key (observationId) references observation;
alter table textValue add constraint observationTextValueFk foreign key (observationId) references observation;
create index validProcedureTimeStartTimeIdx on validProcedureTime (startTime);
create index validProcedureTimeEndTimeIdx on validProcedureTime (endTime);
alter table validProcedureTime add constraint validProcedureTimeProcedureFk foreign key (procedureId) references procedure;
alter table validProcedureTime add constraint validProcProcDescFormatFk foreign key (procedureDescriptionFormatId) references procedureDescriptionFormat;
create sequence codespaceId_seq;
create sequence featureOfInterestId_seq;
create sequence featureOfInterestTypeId_seq;
create sequence observablePropertyId_seq;
create sequence observationConstellationId_seq;
create sequence observationId_seq;
create sequence observationTypeId_seq;
create sequence offeringId_seq;
create sequence parameterId_seq;
create sequence procDescFormatId_seq;
create sequence procedureId_seq;
create sequence relatedFeatureId_seq;
create sequence relatedFeatureRoleId_seq;
create sequence resultTemplateId_seq;
create sequence spatialFilteringProfileId_seq;
create sequence unitId_seq;
create sequence validProcedureTimeId_seq;