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

create table public."procedure" (procedureId int8 not null, hibernateDiscriminator char(1) not null, procedureDescriptionFormatId int8 not null, identifier varchar(255) not null, codespace int8, name varchar(255), codespacename int8, description varchar(255), deleted char(1) default 'F' not null check (deleted in ('T','F')), descriptionFile text, referenceFlag char(1) default 'F' check (referenceFlag in ('T','F')), primary key (procedureId));
create table public.blobValue (observationId int8 not null, value oid, primary key (observationId));
create table public.booleanValue (observationId int8 not null, value char(1), primary key (observationId), check (value in ('T','F')), check (value in ('T','F')));
create table public.categoryValue (observationId int8 not null, value varchar(255), primary key (observationId));
create table public.codespace (codespaceId int8 not null, codespace varchar(255) not null, primary key (codespaceId));
create table public.compositePhenomenon (parentObservablePropertyId int8 not null, childObservablePropertyId int8 not null, primary key (childObservablePropertyId, parentObservablePropertyId));
create table public.countValue (observationId int8 not null, value int4, primary key (observationId));
create table public.featureOfInterest (featureOfInterestId int8 not null, hibernateDiscriminator char(1) not null, featureOfInterestTypeId int8 not null, identifier varchar(255), codespace int8, name varchar(255), codespacename int8, description varchar(255), geom GEOMETRY, descriptionXml text, url varchar(255), primary key (featureOfInterestId));
create table public.featureOfInterestType (featureOfInterestTypeId int8 not null, featureOfInterestType varchar(255) not null, primary key (featureOfInterestTypeId));
create table public.featureRelation (parentFeatureId int8 not null, childFeatureId int8 not null, primary key (childFeatureId, parentFeatureId));
create table public.geometryValue (observationId int8 not null, value GEOMETRY, primary key (observationId));
create table public.i18nfeatureOfInterest (id int8 not null, objectId int8 not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
create table public.i18nobservableProperty (id int8 not null, objectId int8 not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
create table public.i18noffering (id int8 not null, objectId int8 not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
create table public.i18nprocedure (id int8 not null, objectId int8 not null, locale varchar(255) not null, name varchar(255), description varchar(255), shortname varchar(255), longname varchar(255), primary key (id));
create table public.numericValue (observationId int8 not null, value double precision, primary key (observationId));
create table public.observableProperty (observablePropertyId int8 not null, hibernateDiscriminator char(1) not null, identifier varchar(255) not null, codespace int8, name varchar(255), codespacename int8, description varchar(255), primary key (observablePropertyId));
create table public.observation (observationId int8 not null, seriesId int8 not null, phenomenonTimeStart timestamp not null, phenomenonTimeEnd timestamp not null, resultTime timestamp not null, identifier varchar(255), codespace int8, name varchar(255), codespacename int8, description varchar(255), deleted char(1) default 'F' not null check (deleted in ('T','F')), validTimeStart timestamp, validTimeEnd timestamp, unitId int8, samplingGeometry GEOMETRY, primary key (observationId));
create table public.observationConstellation (observationConstellationId int8 not null, observablePropertyId int8 not null, procedureId int8 not null, observationTypeId int8, offeringId int8 not null, deleted char(1) default 'F' not null check (deleted in ('T','F')), hiddenChild char(1) default 'F' not null check (hiddenChild in ('T','F')), primary key (observationConstellationId));
create table public.observationHasOffering (observationId int8 not null, offeringId int8 not null, primary key (observationId, offeringId));
create table public.observationType (observationTypeId int8 not null, observationType varchar(255) not null, primary key (observationTypeId));
create table public.offering (offeringId int8 not null, hibernateDiscriminator char(1) not null, identifier varchar(255) not null, codespace int8, name varchar(255), codespacename int8, description varchar(255), primary key (offeringId));
create table public.offeringAllowedFeatureType (offeringId int8 not null, featureOfInterestTypeId int8 not null, primary key (offeringId, featureOfInterestTypeId));
create table public.offeringAllowedObservationType (offeringId int8 not null, observationTypeId int8 not null, primary key (offeringId, observationTypeId));
create table public.offeringHasRelatedFeature (relatedFeatureId int8 not null, offeringId int8 not null, primary key (offeringId, relatedFeatureId));
create table public.parameter (parameterId int8 not null, observationId int8 not null, definition varchar(255) not null, title varchar(255), value oid not null, primary key (parameterId));
create table public.procedureDescriptionFormat (procedureDescriptionFormatId int8 not null, procedureDescriptionFormat varchar(255) not null, primary key (procedureDescriptionFormatId));
create table public.relatedFeature (relatedFeatureId int8 not null, featureOfInterestId int8 not null, primary key (relatedFeatureId));
create table public.relatedFeatureHasRole (relatedFeatureId int8 not null, relatedFeatureRoleId int8 not null, primary key (relatedFeatureId, relatedFeatureRoleId));
create table public.relatedFeatureRole (relatedFeatureRoleId int8 not null, relatedFeatureRole varchar(255) not null, primary key (relatedFeatureRoleId));
create table public.resultTemplate (resultTemplateId int8 not null, offeringId int8 not null, observablePropertyId int8 not null, procedureId int8 not null, featureOfInterestId int8 not null, identifier varchar(255) not null, resultStructure text not null, resultEncoding text not null, primary key (resultTemplateId));
create table public.sensorSystem (parentSensorId int8 not null, childSensorId int8 not null, primary key (childSensorId, parentSensorId));
create table public.series (seriesId int8 not null, featureOfInterestId int8 not null, observablePropertyId int8 not null, procedureId int8 not null, deleted char(1) default 'F' not null check (deleted in ('T','F')), published char(1) default 'T' not null check (published in ('T','F')), firstTimeStamp timestamp, lastTimeStamp timestamp, firstNumericValue double precision, lastNumericValue double precision, unitId int8, primary key (seriesId));
create table public.sweDataArrayValue (observationId int8 not null, value text, primary key (observationId));
create table public.textValue (observationId int8 not null, value text, primary key (observationId));
create table public.unit (unitId int8 not null, unit varchar(255) not null, primary key (unitId));
create table public.validProcedureTime (validProcedureTimeId int8 not null, procedureId int8 not null, procedureDescriptionFormatId int8 not null, startTime timestamp not null, endTime timestamp, descriptionXml text not null, primary key (validProcedureTimeId));
alter table public."procedure" add constraint procIdentifierUK unique (identifier);
alter table public.codespace add constraint codespaceUK unique (codespace);
alter table public.featureOfInterest add constraint foiIdentifierUK unique (identifier);
alter table public.featureOfInterest add constraint obsUrl unique (url);
alter table public.featureOfInterestType add constraint featureTypeUK unique (featureOfInterestType);
alter table public.i18nfeatureOfInterest add constraint i18nFeatureIdentity unique (objectId, locale);
create index i18nFeatureIdx on public.i18nfeatureOfInterest (objectId);
alter table public.i18nobservableProperty add constraint i18nobsPropIdentity unique (objectId, locale);
create index i18nObsPropIdx on public.i18nobservableProperty (objectId);
alter table public.i18noffering add constraint i18nOfferingIdentity unique (objectId, locale);
create index i18nOfferingIdx on public.i18noffering (objectId);
alter table public.i18nprocedure add constraint i18nProcedureIdentity unique (objectId, locale);
create index i18nProcedureIdx on public.i18nprocedure (objectId);
alter table public.observableProperty add constraint obsPropIdentifierUK unique (identifier);
alter table public.observation add constraint observationIdentity unique (seriesId, phenomenonTimeStart, phenomenonTimeEnd, resultTime);
alter table public.observation add constraint obsIdentifierUK unique (identifier);
create index obsSeriesIdx on public.observation (seriesId);
create index obsPhenTimeStartIdx on public.observation (phenomenonTimeStart);
create index obsPhenTimeEndIdx on public.observation (phenomenonTimeEnd);
create index obsResultTimeIdx on public.observation (resultTime);
create index obsCodespaceIdx on public.observation (codespaceId);
alter table public.observationConstellation add constraint obsnConstellationIdentity unique (observablePropertyId, procedureId, offeringId);
create index obsConstObsPropIdx on public.observationConstellation (observablePropertyId);
create index obsConstProcedureIdx on public.observationConstellation (procedureId);
create index obsConstOfferingIdx on public.observationConstellation (offeringId);
create index obshasoffobservationidx on public.observationHasOffering (observationId);
create index obshasoffofferingidx on public.observationHasOffering (offeringId);
alter table public.observationType add constraint observationTypeUK unique (observationType);
alter table public.offering add constraint offIdentifierUK unique (identifier);
alter table public.procedureDescriptionFormat add constraint procDescFormatUK unique (procedureDescriptionFormat);
alter table public.relatedFeatureRole add constraint relFeatRoleUK unique (relatedFeatureRole);
create index resultTempOfferingIdx on public.resultTemplate (offeringId);
create index resultTempeObsPropIdx on public.resultTemplate (observablePropertyId);
create index resultTempProcedureIdx on public.resultTemplate (procedureId);
create index resultTempIdentifierIdx on public.resultTemplate (identifier);
alter table public.series add constraint seriesIdentity unique (featureOfInterestId, observablePropertyId, procedureId);
create index seriesFeatureIdx on public.series (featureOfInterestId);
create index seriesObsPropIdx on public.series (observablePropertyId);
create index seriesProcedureIdx on public.series (procedureId);
alter table public.unit add constraint unitUK unique (unit);
create index validProcedureTimeStartTimeIdx on public.validProcedureTime (startTime);
create index validProcedureTimeEndTimeIdx on public.validProcedureTime (endTime);
alter table public."procedure" add constraint procProcDescFormatFk foreign key (procedureDescriptionFormatId) references public.procedureDescriptionFormat;
alter table public."procedure" add constraint procCodespaceIdentifierFk foreign key (codespace) references public.codespace;
alter table public."procedure" add constraint procCodespaceNameFk foreign key (codespacename) references public.codespace;
alter table public.blobValue add constraint observationBlobValueFk foreign key (observationId) references public.observation;
alter table public.booleanValue add constraint observationBooleanValueFk foreign key (observationId) references public.observation;
alter table public.categoryValue add constraint observationCategoryValueFk foreign key (observationId) references public.observation;
alter table public.compositePhenomenon add constraint observablePropertyChildFk foreign key (childObservablePropertyId) references public.observableProperty;
alter table public.compositePhenomenon add constraint observablePropertyParentFk foreign key (parentObservablePropertyId) references public.observableProperty;
alter table public.countValue add constraint observationCountValueFk foreign key (observationId) references public.observation;
alter table public.featureOfInterest add constraint featureFeatureTypeFk foreign key (featureOfInterestTypeId) references public.featureOfInterestType;
alter table public.featureOfInterest add constraint featureCodespaceIdentifierFk foreign key (codespace) references public.codespace;
ALTER TABLE public.featureofinterest add constraint featureCodespaceNameFk foreign key (codespacename) references public.codespace
alter table public.featureRelation add constraint featureOfInterestChildFk foreign key (childFeatureId) references public.featureOfInterest;
alter table public.featureRelation add constraint featureOfInterestParentFk foreign key (parentFeatureId) references public.featureOfInterest;
alter table public.geometryValue add constraint observationGeometryValueFk foreign key (observationId) references public.observation;
alter table public.i18nfeatureOfInterest add constraint i18nFeatureFeatureFk foreign key (objectId) references public.featureOfInterest;
alter table public.i18nobservableProperty add constraint i18nObsPropObsPropFk foreign key (objectId) references public.observableProperty;
alter table public.i18noffering add constraint i18nOfferingOfferingFk foreign key (objectId) references public.offering;
alter table public.i18nprocedure add constraint i18nProcedureProcedureFk foreign key (objectId) references public."procedure";
alter table public.numericValue add constraint observationNumericValueFk foreign key (observationId) references public.observation;
alter table public.observableproperty add constraint obsPropCodespaceIdentifierFk foreign key (codespace) references public.codespace;
alter table public.observableproperty add constraint obsPropCodespaceNameFk foreign key (codespacename) references public.codespace;
alter table public.observation add constraint observationSeriesFk foreign key (seriesId) references public.series;
alter table public.observation add constraint obsCodespaceIdentifierFk foreign key (codespace) references public.codespace;
alter table public.observation add constraint obsCodespaceNameFk foreign key (codespacename) references public.codespace;
alter table public.observation add constraint observationUnitFk foreign key (unitId) references public.unit;
alter table public.observationConstellation add constraint obsConstObsPropFk foreign key (observablePropertyId) references public.observableProperty;
alter table public.observationConstellation add constraint obsnConstProcedureFk foreign key (procedureId) references public."procedure";
alter table public.observationConstellation add constraint obsConstObservationIypeFk foreign key (observationTypeId) references public.observationType;
alter table public.observationConstellation add constraint obsConstOfferingFk foreign key (offeringId) references public.offering;
alter table public.observationHasOffering add constraint observationOfferingFk foreign key (offeringId) references public.offering;
alter table public.observationHasOffering add constraint FK_9ex7hawh3dbplkllmw5w3kvej foreign key (observationId) references public.observation;
alter table public.offering add constraint offCodespaceIdentifierFk foreign key (codespace) references public.codespace;
alter table public.offering add constraint offCodespaceNameFk foreign key (codespacename) references public.codespace;
alter table public.offeringAllowedFeatureType add constraint offeringFeatureTypeFk foreign key (featureOfInterestTypeId) references public.featureOfInterestType;
alter table public.offeringAllowedFeatureType add constraint FK_6vvrdxvd406n48gkm706ow1pt foreign key (offeringId) references public.offering;
alter table public.offeringAllowedObservationType add constraint offeringObservationTypeFk foreign key (observationTypeId) references public.observationType;
alter table public.offeringAllowedObservationType add constraint FK_lkljeohulvu7cr26pduyp5bd0 foreign key (offeringId) references public.offering;
alter table public.offeringHasRelatedFeature add constraint relatedFeatureOfferingFk foreign key (offeringId) references public.offering;
alter table public.offeringHasRelatedFeature add constraint offeringRelatedFeatureFk foreign key (relatedFeatureId) references public.relatedFeature;
alter table public.relatedFeature add constraint relatedFeatureFeatureFk foreign key (featureOfInterestId) references public.featureOfInterest;
alter table public.relatedFeatureHasRole add constraint relatedFeatRelatedFeatRoleFk foreign key (relatedFeatureRoleId) references public.relatedFeatureRole;
alter table public.relatedFeatureHasRole add constraint FK_6ynwkk91xe8p1uibmjt98sog3 foreign key (relatedFeatureId) references public.relatedFeature;
alter table public.resultTemplate add constraint resultTemplateOfferingIdx foreign key (offeringId) references public.offering;
alter table public.resultTemplate add constraint resultTemplateObsPropFk foreign key (observablePropertyId) references public.observableProperty;
alter table public.resultTemplate add constraint resultTemplateProcedureFk foreign key (procedureId) references public."procedure";
alter table public.resultTemplate add constraint resultTemplateFeatureIdx foreign key (featureOfInterestId) references public.featureOfInterest;
alter table public.sensorSystem add constraint procedureChildFk foreign key (childSensorId) references public."procedure";
alter table public.sensorSystem add constraint procedureParenfFk foreign key (parentSensorId) references public."procedure";
alter table public.series add constraint seriesFeatureFk foreign key (featureOfInterestId) references public.featureOfInterest;
alter table public.series add constraint seriesObPropFk foreign key (observablePropertyId) references public.observableProperty;
alter table public.series add constraint seriesProcedureFk foreign key (procedureId) references public."procedure";
alter table public.series add constraint seriesUnitFk foreign key (unitId) references public.unit;
alter table public.sweDataArrayValue add constraint observationSweDataArrayValueFk foreign key (observationId) references public.observation;
alter table public.textValue add constraint observationTextValueFk foreign key (observationId) references public.observation;
alter table public.validProcedureTime add constraint validProcedureTimeProcedureFk foreign key (procedureId) references public."procedure";
alter table public.validProcedureTime add constraint validProcProcDescFormatFk foreign key (procedureDescriptionFormatId) references public.procedureDescriptionFormat;
create sequence public.codespaceId_seq;
create sequence public.featureOfInterestId_seq;
create sequence public.featureOfInterestTypeId_seq;
create sequence public.i18nObsPropId_seq;
create sequence public.i18nOfferingId_seq;
create sequence public.i18nProcedureId_seq;
create sequence public.i18nfeatureOfInterestId_seq;
create sequence public.observablePropertyId_seq;
create sequence public.observationConstellationId_seq;
create sequence public.observationId_seq;
create sequence public.observationTypeId_seq;
create sequence public.offeringId_seq;
create sequence public.parameterId_seq;
create sequence public.procDescFormatId_seq;
create sequence public.procedureId_seq;
create sequence public.relatedFeatureId_seq;
create sequence public.relatedFeatureRoleId_seq;
create sequence public.resultTemplateId_seq;
create sequence public.seriesId_seq;
create sequence public.unitId_seq;
create sequence public.validProcedureTimeId_seq;
