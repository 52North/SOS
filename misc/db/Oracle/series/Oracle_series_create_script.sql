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

create table "procedure" (procedureId number(19,0) not null, hibernateDiscriminator char(1 char) not null, procedureDescriptionFormatId number(19,0) not null, identifier varchar2(255 char) not null, CODESPACE number(19,0), name varchar2(255 char), CODESPACENAME number(19,0), DESCRIPTION varchar2(255 char), deleted char(1 char) default 'F' not null check (deleted in ('T','F')), descriptionFile clob, referenceFlag char(1 char) default 'F' check (referenceFlag in ('T','F')), primary key (procedureId));
create table blobValue (observationId number(19,0) not null, value blob, primary key (observationId));
create table booleanValue (observationId number(19,0) not null, value char(1 char), primary key (observationId), check (value in ('T','F')), check (value in ('T','F')));
create table categoryValue (observationId number(19,0) not null, value varchar2(255 char), primary key (observationId));
create table codespace (codespaceId number(19,0) not null, codespace varchar2(255 char) not null, primary key (codespaceId));
create table compositePhenomenon (parentObservablePropertyId number(19,0) not null, childObservablePropertyId number(19,0) not null, primary key (childObservablePropertyId, parentObservablePropertyId));
create table countValue (observationId number(19,0) not null, value number(10,0), primary key (observationId));
create table featureOfInterest (featureOfInterestId number(19,0) not null, hibernateDiscriminator char(1 char) not null, featureOfInterestTypeId number(19,0) not null, identifier varchar2(255 char), CODESPACE number(19,0), name varchar2(255 char), CODESPACENAME number(19,0), DESCRIPTION varchar2(255 char), geom SDO_GEOMETRY, descriptionXml clob, url varchar2(255 char), primary key (featureOfInterestId));
create table featureOfInterestType (featureOfInterestTypeId number(19,0) not null, featureOfInterestType varchar2(255 char) not null, primary key (featureOfInterestTypeId));
CREATE TABLE i18nfeatureOfInterest (ID number(19,0) not null, OBJECTID number(19,0) not null, LOCALE varchar2(255 char) not null, NAME varchar2(255 char), DESCRIPTION varchar2(255 char), primary key (ID));
CREATE TABLE i18nobservableProperty (ID number(19,0) not null, OBJECTID number(19,0) not null, LOCALE varchar2(255 char) not null, NAME varchar2(255 char), DESCRIPTION varchar2(255 char), primary key (ID));
CREATE TABLE i18noffering (ID number(19,0) not null, OBJECTID number(19,0) not null, LOCALE varchar2(255 char) not null, NAME varchar2(255 char), DESCRIPTION varchar2(255 char), primary key (ID));
CREATE TABLE i18nprocedure (ID number(19,0) not null, OBJECTID number(19,0) not null, LOCALE varchar2(255 char) not null, NAME varchar2(255 char), DESCRIPTION varchar2(255 char), SHOTNAME varchar2(255 char), LONGNAME varchar2(255 char), primary key (ID));
create table featureRelation (parentFeatureId number(19,0) not null, childFeatureId number(19,0) not null, primary key (childFeatureId, parentFeatureId));
create table geometryValue (observationId number(19,0) not null, value SDO_GEOMETRY, primary key (observationId));
create table numericValue (observationId number(19,0) not null, value double precision, primary key (observationId));
create table observableProperty (observablePropertyId number(19,0) not null, hibernateDiscriminator char(1 char) not null, identifier varchar2(255 char) not null, CODESPACE number(19,0), name varchar2(255 char), CODESPACENAME number(19,0), DESCRIPTION varchar2(255 char), primary key (observablePropertyId));
create table observation (observationId number(19,0) not null, seriesId number(19,0) not null, phenomenonTimeStart timestamp not null, phenomenonTimeEnd timestamp not null, resultTime timestamp not null, identifier varchar2(255 char), CODESPACE number(19,0), name varchar2(255 char), CODESPACENAME number(19,0), DESCRIPTION varchar2(255 char), deleted char(1 char) default 'F' not null check (deleted in ('T','F')), validTimeStart timestamp, validTimeEnd timestamp, unitId number(19,0), samplingGeometry SDO_GEOMETRY, primary key (observationId));
create table observationConstellation (observationConstellationId number(19,0) not null, observablePropertyId number(19,0) not null, procedureId number(19,0) not null, observationTypeId number(19,0), offeringId number(19,0) not null, deleted char(1 char) default 'F' not null check (deleted in ('T','F')), hiddenChild char(1 char) default 'F' not null check (hiddenChild in ('T','F')), primary key (observationConstellationId));
create table observationHasOffering (observationId number(19,0) not null, offeringId number(19,0) not null, primary key (observationId, offeringId));
create table observationType (observationTypeId number(19,0) not null, observationType varchar2(255 char) not null, primary key (observationTypeId));
create table offering (offeringId number(19,0) not null, hibernateDiscriminator char(1 char) not null, identifier varchar2(255 char) not null, CODESPACE number(19,0), name varchar2(255 char), CODESPACENAME number(19,0), DESCRIPTION varchar2(255 char), primary key (offeringId));
create table offeringAllowedFeatureType (offeringId number(19,0) not null, featureOfInterestTypeId number(19,0) not null, primary key (offeringId, featureOfInterestTypeId));
create table offeringAllowedObservationType (offeringId number(19,0) not null, observationTypeId number(19,0) not null, primary key (offeringId, observationTypeId));
create table offeringHasRelatedFeature (relatedFeatureId number(19,0) not null, offeringId number(19,0) not null, primary key (offeringId, relatedFeatureId));
create table parameter (parameterId number(19,0) not null, observationId number(19,0) not null, definition varchar2(255 char) not null, title varchar2(255 char), value blob not null, primary key (parameterId));
create table procedureDescriptionFormat (procedureDescriptionFormatId number(19,0) not null, procedureDescriptionFormat varchar2(255 char) not null, primary key (procedureDescriptionFormatId));
create table relatedFeature (relatedFeatureId number(19,0) not null, featureOfInterestId number(19,0) not null, primary key (relatedFeatureId));
create table relatedFeatureHasRole (relatedFeatureId number(19,0) not null, relatedFeatureRoleId number(19,0) not null, primary key (relatedFeatureId, relatedFeatureRoleId));
create table relatedFeatureRole (relatedFeatureRoleId number(19,0) not null, relatedFeatureRole varchar2(255 char) not null, primary key (relatedFeatureRoleId));
create table resultTemplate (resultTemplateId number(19,0) not null, offeringId number(19,0) not null, observablePropertyId number(19,0) not null, procedureId number(19,0) not null, featureOfInterestId number(19,0) not null, identifier varchar2(255 char) not null, resultStructure clob not null, resultEncoding clob not null, primary key (resultTemplateId));
create table sensorSystem (parentSensorId number(19,0) not null, childSensorId number(19,0) not null, primary key (childSensorId, parentSensorId));
create table series (seriesId number(19,0) not null, featureOfInterestId number(19,0) not null, observablePropertyId number(19,0) not null, procedureId number(19,0) not null, deleted char(1 char) default 'F' not null check (deleted in ('T','F')), published char(1 char) default 'T' not null check (published in ('T','F')), firstTimeStamp timestamp, lastTimeStamp timestamp, firstNumericValue double precision, lastNumericValue double precision, unitId number(19,0), primary key (seriesId));
create table sweDataArrayValue (observationId number(19,0) not null, value clob, primary key (observationId));
create table textValue (observationId number(19,0) not null, value clob, primary key (observationId));
create table unit (unitId number(19,0) not null, unit varchar2(255 char) not null, primary key (unitId));
create table validProcedureTime (validProcedureTimeId number(19,0) not null, procedureId number(19,0) not null, procedureDescriptionFormatId number(19,0) not null, startTime timestamp not null, endTime timestamp, descriptionXml clob not null, primary key (validProcedureTimeId));
alter table "procedure" add constraint procIdentifierUK  unique (identifier);
alter table codespace add constraint codespaceUK  unique (codespace);
alter table featureOfInterest add constraint foiIdentifierUK  unique (identifier);
alter table featureOfInterest add constraint obsUrl  unique (url);
alter table featureOfInterestType add constraint featureTypeUK  unique (featureOfInterestType);
ALTER TABLE i18nfeatureOfInterest ADD CONSTRAINT i18nFeatureIdentity unique (OBJECTID, LOCALE);
CREATE index i18nFeatureIdx ON i18nfeatureOfInterest (OBJECTID);
ALTER TABLE i18nobservableProperty ADD CONSTRAINT i18nobsPropIdentity  unique (OBJECTID, LOCALE);
CREATE index i18nObsPropIdx ON i18nobservableProperty (OBJECTID);
ALTER TABLE i18noffering ADD CONSTRAINT i18nOfferingIdentity  unique (OBJECTID, LOCALE);
CREATE index i18nOfferingIdx ON i18noffering (OBJECTID);
ALTER TABLE i18nprocedure ADD CONSTRAINT i18nProcedureIdentity  unique (OBJECTID, LOCALE);
CREATE index i18nProcedureIdx ON i18nprocedure (OBJECTID);
alter table observableProperty add constraint obsPropIdentifierUK  unique (identifier);
alter table observation add constraint observationIdentity  unique (seriesId, phenomenonTimeStart, phenomenonTimeEnd, resultTime);
alter table observation add constraint obsIdentifierUK  unique (identifier);
create index obsSeriesIdx on observation (seriesId);
create index obsPhenTimeStartIdx on observation (phenomenonTimeStart);
create index obsPhenTimeEndIdx on observation (phenomenonTimeEnd);
create index obsResultTimeIdx on observation (resultTime);
create index obsCodespaceIdx on observation (codespace);
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
alter table "procedure" add constraint procProcDescFormatFk foreign key (procedureDescriptionFormatId) references procedureDescriptionFormat;
ALTER TABLE "procedure" ADD CONSTRAINT procCodespaceIdentifierFk FOREIGN KEY (CODESPACE) REFERENCES CODESPACE;
ALTER TABLE "procedure" ADD CONSTRAINT procCodespaceNameFk FOREIGN KEY (CODESPACENAME) REFERENCES CODESPACE;
alter table blobValue add constraint observationBlobValueFk foreign key (observationId) references observation;
alter table booleanValue add constraint observationBooleanValueFk foreign key (observationId) references observation;
alter table categoryValue add constraint observationCategoryValueFk foreign key (observationId) references observation;
alter table compositePhenomenon add constraint observablePropertyChildFk foreign key (childObservablePropertyId) references observableProperty;
alter table compositePhenomenon add constraint observablePropertyParentFk foreign key (parentObservablePropertyId) references observableProperty;
alter table countValue add constraint observationCountValueFk foreign key (observationId) references observation;
alter table featureOfInterest add constraint featureFeatureTypeFk foreign key (featureOfInterestTypeId) references featureOfInterestType;
alter table featureOfInterest add constraint featureCodespaceIdentifierFk foreign key (codespace) references codespace;
ALTER TABLE FEATUREOFINTEREST ADD CONSTRAINT featureCodespaceNameFk FOREIGN KEY (CODESPACENAME) REFERENCES CODESPACE;
ALTER TABLE i18nfeatureOfInterest ADD CONSTRAINT i18nFeatureFeatureFk FOREIGN KEY (OBJECTID) REFERENCES featureOfInterest;
ALTER TABLE i18nobservableProperty ADD CONSTRAINT i18nObsPropObsPropFk FOREIGN KEY (OBJECTID) REFERENCES observableProperty;
ALTER TABLE i18noffering ADD CONSTRAINT i18nOfferingOfferingFk FOREIGN KEY (OBJECTID) REFERENCES OFFERING;
ALTER TABLE i18nprocedure ADD CONSTRAINT i18nProcedureProcedureFk FOREIGN KEY (OBJECTID) REFERENCES "procedure";
alter table featureRelation add constraint featureOfInterestChildFk foreign key (childFeatureId) references featureOfInterest;
alter table featureRelation add constraint featureOfInterestParentFk foreign key (parentFeatureId) references featureOfInterest;
alter table geometryValue add constraint observationGeometryValueFk foreign key (observationId) references observation;
alter table numericValue add constraint observationNumericValueFk foreign key (observationId) references observation;
ALTER TABLE OBSERVABLEPROPERTY ADD CONSTRAINT obsPropCodespaceIdentifierFk FOREIGN KEY (CODESPACE) REFERENCES CODESPACE;
ALTER TABLE OBSERVABLEPROPERTY ADD CONSTRAINT obsPropCodespaceNameFk FOREIGN KEY (CODESPACENAME) REFERENCES CODESPACE;
alter table observation add constraint observationSeriesFk foreign key (seriesId) references series;
ALTER TABLE OBSERVATION ADD CONSTRAINT obsCodespaceIdentifierFk FOREIGN KEY (CODESPACE) REFERENCES CODESPACE;
ALTER TABLE OBSERVATION ADD CONSTRAINT obsCodespaceNameFk FOREIGN KEY (CODESPACENAME) REFERENCES CODESPACE;
alter table observation add constraint observationUnitFk foreign key (unitId) references unit;
alter table observationConstellation add constraint obsConstObsPropFk foreign key (observablePropertyId) references observableProperty;
alter table observationConstellation add constraint obsnConstProcedureFk foreign key (procedureId) references "procedure";
alter table observationConstellation add constraint obsConstObservationIypeFk foreign key (observationTypeId) references observationType;
alter table observationConstellation add constraint obsConstOfferingFk foreign key (offeringId) references offering;
alter table observationHasOffering add constraint observationOfferingFk foreign key (offeringId) references offering;
alter table observationHasOffering add constraint FK_9ex7hawh3dbplkllmw5w3kvej foreign key (observationId) references observation;
ALTER TABLE OFFERING ADD CONSTRAINT offCodespaceIdentifierFk FOREIGN KEY (CODESPACE) REFERENCES CODESPACE;
ALTER TABLE OFFERING ADD CONSTRAINT offCodespaceNameFk FOREIGN KEY (CODESPACENAME) REFERENCES CODESPACE;
alter table offeringAllowedFeatureType add constraint offeringFeatureTypeFk foreign key (featureOfInterestTypeId) references featureOfInterestType;
alter table offeringAllowedFeatureType add constraint FK_6vvrdxvd406n48gkm706ow1pt foreign key (offeringId) references offering;
alter table offeringAllowedObservationType add constraint offeringObservationTypeFk foreign key (observationTypeId) references observationType;
alter table offeringAllowedObservationType add constraint FK_lkljeohulvu7cr26pduyp5bd0 foreign key (offeringId) references offering;
alter table offeringHasRelatedFeature add constraint relatedFeatureOfferingFk foreign key (offeringId) references offering;
alter table offeringHasRelatedFeature add constraint offeringRelatedFeatureFk foreign key (relatedFeatureId) references relatedFeature;
alter table relatedFeature add constraint relatedFeatureFeatureFk foreign key (featureOfInterestId) references featureOfInterest;
alter table relatedFeatureHasRole add constraint relatedFeatRelatedFeatRoleFk foreign key (relatedFeatureRoleId) references relatedFeatureRole;
alter table relatedFeatureHasRole add constraint FK_6ynwkk91xe8p1uibmjt98sog3 foreign key (relatedFeatureId) references relatedFeature;
alter table resultTemplate add constraint resultTemplateOfferingIdx foreign key (offeringId) references offering;
alter table resultTemplate add constraint resultTemplateObsPropFk foreign key (observablePropertyId) references observableProperty;
alter table resultTemplate add constraint resultTemplateProcedureFk foreign key (procedureId) references "procedure";
alter table resultTemplate add constraint resultTemplateFeatureIdx foreign key (featureOfInterestId) references featureOfInterest;
alter table sensorSystem add constraint procedureChildFk foreign key (childSensorId) references "procedure";
alter table sensorSystem add constraint procedureParenfFk foreign key (parentSensorId) references "procedure";
alter table series add constraint seriesFeatureFk foreign key (featureOfInterestId) references featureOfInterest;
alter table series add constraint seriesObPropFk foreign key (observablePropertyId) references observableProperty;
alter table series add constraint seriesProcedureFk foreign key (procedureId) references "procedure";
alter table series add constraint seriesUnitFk foreign key (unitId) references unit;
alter table sweDataArrayValue add constraint observationSweDataArrayValueFk foreign key (observationId) references observation;
alter table textValue add constraint observationTextValueFk foreign key (observationId) references observation;
alter table validProcedureTime add constraint validProcedureTimeProcedureFk foreign key (procedureId) references "procedure";
alter table validProcedureTime add constraint validProcProcDescFormatFk foreign key (procedureDescriptionFormatId) references procedureDescriptionFormat;
create sequence codespaceId_seq;
create sequence featureOfInterestId_seq;
create sequence featureOfInterestTypeId_seq;
create sequence i18nObsPropId_seq;
create sequence i18nOfferingId_seq;
create sequence i18nProcedureId_seq;
create sequence i18nfeatureOfInterestId_seq;
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
create sequence seriesId_seq;
create sequence unitId_seq;
create sequence validProcedureTimeId_seq;
