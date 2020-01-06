--
-- Copyright (C) 2012-2020 52°North Initiative for Geospatial Open Source
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
create table dbo.[procedure] (procedureId bigint identity not null, hibernateDiscriminator char(1) not null, procedureDescriptionFormatId bigint not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), deleted char(1) default 'F' not null check (deleted in ('T','F')), disabled char(1) default 'F' not null check (disabled in ('T','F')), descriptionFile varchar(MAX), referenceFlag char(1) default 'F' check (referenceFlag in ('T','F')), typeOf bigint, isType char(1) default 'F' check (isType in ('T','F')), isAggregation char(1) default 'T' check (isAggregation in ('T','F')), mobile char(1) default 'F' check (mobile in ('T','F')), insitu char(1) default 'T' check (insitu in ('T','F')), primary key (procedureId));
create table dbo.blobvalue (observationId bigint not null, value varbinary(MAX), primary key (observationId));
create table dbo.booleanfeatparamvalue (parameterId bigint not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table dbo.booleanparametervalue (parameterId bigint not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table dbo.booleanseriesparamvalue (parameterId bigint not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table dbo.booleanvalue (observationId bigint not null, value char(1), primary key (observationId), check (value in ('T','F')), check (value in ('T','F')));
create table dbo.categoryfeatparamvalue (parameterId bigint not null, value varchar(255), unitId bigint, primary key (parameterId));
create table dbo.categoryparametervalue (parameterId bigint not null, value varchar(255), unitId bigint, primary key (parameterId));
create table dbo.categoryseriesparamvalue (parameterId bigint not null, value varchar(255), unitId bigint, primary key (parameterId));
create table dbo.categoryvalue (observationId bigint not null, value varchar(255), identifier varchar(255), name varchar(255), description varchar(255), primary key (observationId));
create table dbo.codespace (codespaceId bigint identity not null, codespace varchar(255) not null, primary key (codespaceId));
create table dbo.complexvalue (observationId bigint not null, primary key (observationId));
create table dbo.compositeobservation (observationId bigint not null, childObservationId bigint not null, primary key (observationId, childObservationId));
create table dbo.compositephenomenon (parentObservablePropertyId bigint not null, childObservablePropertyId bigint not null, primary key (childObservablePropertyId, parentObservablePropertyId));
create table dbo.countfeatparamvalue (parameterId bigint not null, value int, primary key (parameterId));
create table dbo.countparametervalue (parameterId bigint not null, value int, primary key (parameterId));
create table dbo.countseriesparamvalue (parameterId bigint not null, value int, primary key (parameterId));
create table dbo.countvalue (observationId bigint not null, value int, primary key (observationId));
create table dbo.featureofinterest (featureOfInterestId bigint identity not null, hibernatediscriminator varchar(255), featureOfInterestTypeId bigint not null, identifier varchar(255), codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), geom GEOMETRY, descriptionXml varchar(MAX), url varchar(255), primary key (featureOfInterestId));
create table dbo.featureofinteresttype (featureOfInterestTypeId bigint identity not null, featureOfInterestType varchar(255) not null, primary key (featureOfInterestTypeId));
create table dbo.featureparameter (parameterId bigint identity not null, featureOfInterestId bigint not null, name varchar(255) not null, primary key (parameterId));
create table dbo.featurerelation (parentFeatureId bigint not null, childFeatureId bigint not null, primary key (childFeatureId, parentFeatureId));
create table dbo.geometryvalue (observationId bigint not null, value GEOMETRY, primary key (observationId));
create table dbo.numericfeatparamvalue (parameterId bigint not null, value double precision, unitId bigint, primary key (parameterId));
create table dbo.numericparametervalue (parameterId bigint not null, value double precision, unitId bigint, primary key (parameterId));
create table dbo.numericseriesparamvalue (parameterId bigint not null, value double precision, unitId bigint, primary key (parameterId));
create table dbo.numericvalue (observationId bigint not null, value double precision, primary key (observationId));
create table dbo.observableproperty (observablePropertyId bigint identity not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), disabled char(1) default 'F' not null check (disabled in ('T','F')), hiddenChild char(1) default 'F' not null check (hiddenChild in ('T','F')), primary key (observablePropertyId));
create table dbo.observation (observationId bigint identity not null, seriesId bigint not null, phenomenonTimeStart datetime2 not null, phenomenonTimeEnd datetime2 not null, resultTime datetime2 not null, identifier varchar(255), codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), deleted char(1) default 'F' not null check (deleted in ('T','F')), child char(1) default 'F' not null check (child in ('T','F')), parent char(1) default 'F' not null check (parent in ('T','F')), validTimeStart datetime2 default NULL, validTimeEnd datetime2 default NULL, samplingGeometry GEOMETRY, unitId bigint, primary key (observationId));
create table dbo.observationconstellation (observationConstellationId bigint identity not null, observablePropertyId bigint not null, procedureId bigint not null, observationTypeId bigint, offeringId bigint not null, deleted char(1) default 'F' not null check (deleted in ('T','F')), hiddenChild char(1) default 'F' not null check (hiddenChild in ('T','F')), disabled char(1) default 'F' not null check (disabled in ('T','F')), primary key (observationConstellationId));
create table dbo.observationtype (observationTypeId bigint identity not null, observationType varchar(255) not null, primary key (observationTypeId));
create table dbo.offering (offeringId bigint identity not null, hibernateDiscriminator char(1) not null, identifier varchar(255) not null, codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), disabled char(1) default 'F' not null check (disabled in ('T','F')), primary key (offeringId));
create table dbo.offeringallowedfeaturetype (offeringId bigint not null, featureOfInterestTypeId bigint not null, primary key (offeringId, featureOfInterestTypeId));
create table dbo.offeringallowedobservationtype (offeringId bigint not null, observationTypeId bigint not null, primary key (offeringId, observationTypeId));
create table dbo.offeringhasrelatedfeature (relatedFeatureId bigint not null, offeringId bigint not null, primary key (offeringId, relatedFeatureId));
create table dbo.offeringrelation (parentOfferingId bigint not null, childOfferingId bigint not null, primary key (childOfferingId, parentOfferingId));
create table dbo.parameter (parameterId bigint identity not null, observationId bigint not null, name varchar(255) not null, primary key (parameterId));
create table dbo.proceduredescriptionformat (procedureDescriptionFormatId bigint identity not null, procedureDescriptionFormat varchar(255) not null, primary key (procedureDescriptionFormatId));
create table dbo.profileobservation (observationId bigint not null, childObservationId bigint not null, primary key (observationId, childObservationId));
create table dbo.profilevalue (observationId bigint not null, fromlevel double precision, tolevel double precision, levelunitid bigint, primary key (observationId));
create table dbo.referencevalue (observationId bigint not null, href varchar(255), title varchar(255), role varchar(255), primary key (observationId));
create table dbo.relatedfeature (relatedFeatureId bigint identity not null, featureOfInterestId bigint not null, primary key (relatedFeatureId));
create table dbo.relatedfeaturehasrole (relatedFeatureId bigint not null, relatedFeatureRoleId bigint not null, primary key (relatedFeatureId, relatedFeatureRoleId));
create table dbo.relatedfeaturerole (relatedFeatureRoleId bigint identity not null, relatedFeatureRole varchar(255) not null, primary key (relatedFeatureRoleId));
create table dbo.relatedobservation (relatedObservationId bigint identity not null, observationId bigint, relatedObservation bigint, role varchar(255), relatedUrl varchar(255), primary key (relatedObservationId));
create table dbo.relatedseries (relationId bigint identity not null, seriesId bigint not null, relatedSeries bigint, role varchar(255), relatedUrl varchar(255), primary key (relationId));
create table dbo.resulttemplate (resultTemplateId bigint identity not null, offeringId bigint not null, observablePropertyId bigint not null, procedureId bigint, featureOfInterestId bigint, identifier varchar(255) not null, resultStructure varchar(MAX) not null, resultEncoding varchar(MAX) not null, primary key (resultTemplateId));
create table dbo.sensorsystem (parentSensorId bigint not null, childSensorId bigint not null, primary key (childSensorId, parentSensorId));
create table dbo.series (seriesId bigint identity not null, featureOfInterestId bigint not null, observablePropertyId bigint not null, procedureId bigint not null, offeringId bigint not null, deleted char(1) default 'F' not null check (deleted in ('T','F')), published char(1) default 'T' not null check (published in ('T','F')), hiddenChild char(1) default 'F' not null check (hiddenChild in ('T','F')), firstTimeStamp datetime2 default NULL, lastTimeStamp datetime2 default NULL, firstNumericValue double precision, lastNumericValue double precision, unitId bigint, identifier varchar(255), codespace bigint, name varchar(255), codespaceName bigint, description varchar(255), seriesType varchar(255), primary key (seriesId));
create table dbo.seriesmetadata (metadataId bigint identity not null, seriesId bigint not null, identifier varchar(255) not null, value varchar(255) not null, domain varchar(255) not null, primary key (metadataId));
create table dbo.seriesparameter (parameterId bigint identity not null, seriesId bigint not null, name varchar(255) not null, primary key (parameterId));
create table dbo.seriesreference (seriesid bigint not null, referenceseriesid bigint not null, sortorder int not null, primary key (seriesid, sortorder));
create table dbo.swedataarrayvalue (observationId bigint not null, value varchar(MAX), primary key (observationId));
create table dbo.textfeatparamvalue (parameterId bigint not null, value varchar(255), primary key (parameterId));
create table dbo.textparametervalue (parameterId bigint not null, value varchar(255), primary key (parameterId));
create table dbo.textseriesparamvalue (parameterId bigint not null, value varchar(255), primary key (parameterId));
create table dbo.textvalue (observationId bigint not null, value varchar(MAX), identifier varchar(255), name varchar(255), description varchar(255), primary key (observationId));
create table dbo.unit (unitId bigint identity not null, unit varchar(255) not null, name varchar(255), link varchar(255), primary key (unitId));
create table dbo.validproceduretime (validProcedureTimeId bigint identity not null, procedureId bigint not null, procedureDescriptionFormatId bigint not null, startTime datetime2 not null, endTime datetime2 default NULL, descriptionXml varchar(MAX) not null, primary key (validProcedureTimeId));
create table dbo.xmlfeatparamvalue (parameterId bigint not null, value varchar(MAX), primary key (parameterId));
create table dbo.xmlparametervalue (parameterId bigint not null, value varchar(MAX), primary key (parameterId));
create table dbo.xmlseriesparamvalue (parameterId bigint not null, value varchar(MAX), primary key (parameterId));
alter table dbo.[procedure] add constraint procIdentifierUK unique (identifier);
create index blobvalueobsididx on dbo.blobvalue (observationId);
create index booleanFeatParamIdx on dbo.booleanfeatparamvalue (value);
create index booleanparamididx on dbo.booleanparametervalue (parameterId);
create index booleanParamIdx on dbo.booleanparametervalue (value);
create index booleanseriesparamididx on dbo.booleanseriesparamvalue (parameterId);
create index seriesBooleanParamIdx on dbo.booleanseriesparamvalue (value);
create index booleanvalueobsididx on dbo.booleanvalue (observationId);
create index booleanvalueidx on dbo.booleanvalue (value);
create index categoryFeatParamIdx on dbo.categoryfeatparamvalue (value);
create index categoryparamididx on dbo.categoryparametervalue (parameterId);
create index categoryParamIdx on dbo.categoryparametervalue (value);
create index categoryseriesparamididx on dbo.categoryseriesparamvalue (parameterId);
create index seriesCategoryParamIdx on dbo.categoryseriesparamvalue (value);
create index categoryvalueobsididx on dbo.categoryvalue (observationId);
create index categoryvalueidx on dbo.categoryvalue (value);
alter table dbo.codespace add constraint codespaceUK unique (codespace);
create index complexvalueobsididx on dbo.complexvalue (observationId);
create index complexobsididx on dbo.compositeobservation (observationId);
create index complexchildobsididx on dbo.compositeobservation (childObservationId);
create index countFeatParamIdx on dbo.countfeatparamvalue (value);
create index countparamididx on dbo.countparametervalue (parameterId);
create index countParamIdx on dbo.countparametervalue (value);
create index countseriesparamididx on dbo.countseriesparamvalue (parameterId);
create index seriesCountParamIdx on dbo.countseriesparamvalue (value);
create index countvalueobsididx on dbo.countvalue (observationId);
create index countvalueidx on dbo.countvalue (value);
alter table dbo.featureofinterest add constraint foiIdentifierUK unique (identifier);
alter table dbo.featureofinterest add constraint featureUrl unique (url);
alter table dbo.featureofinteresttype add constraint featureTypeUK unique (featureOfInterestType);
create index featParamNameIdx on dbo.featureparameter (name);
create index geometryvalueobsididx on dbo.geometryvalue (observationId);
create index quantityFeatParamIdx on dbo.numericfeatparamvalue (value);
create index numericparamididx on dbo.numericparametervalue (parameterId);
create index quantityParamIdx on dbo.numericparametervalue (value);
create index numericseriesparamididx on dbo.numericseriesparamvalue (parameterId);
create index seriesQuantityParamIdx on dbo.numericseriesparamvalue (value);
create index numericvalueobsididx on dbo.numericvalue (observationId);
create index numericvalueidx on dbo.numericvalue (value);
alter table dbo.observableproperty add constraint obsPropIdentifierUK unique (identifier);
create index obsSeriesIdx on dbo.observation (seriesId);
create index obsPhenTimeStartIdx on dbo.observation (phenomenonTimeStart);
create index obsPhenTimeEndIdx on dbo.observation (phenomenonTimeEnd);
create index obsResultTimeIdx on dbo.observation (resultTime);
alter table dbo.observationconstellation add constraint obsnConstellationIdentity unique (observablePropertyId, procedureId, offeringId);
create index obsConstObsPropIdx on dbo.observationconstellation (observablePropertyId);
create index obsConstProcedureIdx on dbo.observationconstellation (procedureId);
create index obsConstOfferingIdx on dbo.observationconstellation (offeringId);
alter table dbo.observationtype add constraint observationTypeUK unique (observationType);
alter table dbo.offering add constraint offIdentifierUK unique (identifier);
create index paramNameIdx on dbo.parameter (name);
alter table dbo.proceduredescriptionformat add constraint procDescFormatUK unique (procedureDescriptionFormat);
create index profileobsididx on dbo.profileobservation (observationId, childObservationId);
create index profvalueobsididx on dbo.profilevalue (observationId);
create index referencevalueobsididx on dbo.referencevalue (observationId);
alter table dbo.relatedfeaturerole add constraint relFeatRoleUK unique (relatedFeatureRole);
create index relobsobsididx on dbo.relatedobservation (observationId);
create index relobsrelobsididx on dbo.relatedobservation (relatedObservation);
create index relatedObsObsIdx on dbo.relatedobservation (observationId);
create index seriesRelationIdx on dbo.relatedseries (seriesId);
create index resultTempOfferingIdx on dbo.resulttemplate (offeringId);
create index resultTempeObsPropIdx on dbo.resulttemplate (observablePropertyId);
create index resultTempProcedureIdx on dbo.resulttemplate (procedureId);
create index resultTempIdentifierIdx on dbo.resulttemplate (identifier);
alter table dbo.series add constraint seriesIdentity unique (featureOfInterestId, observablePropertyId, procedureId, offeringId);
alter table dbo.series add constraint seriesIdentifierUK unique (identifier);
create index seriesFeatureIdx on dbo.series (featureOfInterestId);
create index seriesObsPropIdx on dbo.series (observablePropertyId);
create index seriesProcedureIdx on dbo.series (procedureId);
create index seriesOfferingIdx on dbo.series (offeringId);
create index seriesmetadataseriesididx on dbo.seriesmetadata (seriesId);
create index seriesParamNameIdx on dbo.seriesparameter (name);
create index seriesididx on dbo.seriesreference (seriesid);
create index referenceseriesididx on dbo.seriesreference (referenceseriesid);
create index swedataarryvalueobsididx on dbo.swedataarrayvalue (observationId);
create index textFeatParamIdx on dbo.textfeatparamvalue (value);
create index textparamididx on dbo.textparametervalue (parameterId);
create index textParamIdx on dbo.textparametervalue (value);
create index textseriesparamididx on dbo.textseriesparamvalue (parameterId);
create index seriesTextParamIdx on dbo.textseriesparamvalue (value);
create index textvalueobsididx on dbo.textvalue (observationId);
create index textvalueidx on dbo.textvalue (value);
alter table dbo.unit add constraint unitUK unique (unit);
create index validProcedureTimeStartTimeIdx on dbo.validproceduretime (startTime);
create index validProcedureTimeEndTimeIdx on dbo.validproceduretime (endTime);
create index xmlparamididx on dbo.xmlparametervalue (parameterId);
create index xmlseriesparamididx on dbo.xmlseriesparamvalue (parameterId);
create index seriesXmlParamIdx on dbo.xmlseriesparamvalue (value);
alter table dbo.[procedure] add constraint procProcDescFormatFk foreign key (procedureDescriptionFormatId) references dbo.proceduredescriptionformat;
alter table dbo.[procedure] add constraint procCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo.[procedure] add constraint procCodespaceNameFk foreign key (codespaceName) references dbo.codespace;
alter table dbo.[procedure] add constraint typeOfFk foreign key (typeOf) references dbo.[procedure];
alter table dbo.blobvalue add constraint observationBlobValueFk foreign key (observationId) references dbo.observation;
alter table dbo.booleanfeatparamvalue add constraint featParamBooleanValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.booleanparametervalue add constraint parameterBooleanValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.booleanseriesparamvalue add constraint seriesParamBooleanValueFk foreign key (parameterId) references dbo.seriesparameter;
alter table dbo.booleanvalue add constraint observationBooleanValueFk foreign key (observationId) references dbo.observation;
alter table dbo.categoryfeatparamvalue add constraint featParamCategoryValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.categoryfeatparamvalue add constraint catfeatparamvalueUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.categoryparametervalue add constraint parameterCategoryValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.categoryparametervalue add constraint catParamValueUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.categoryseriesparamvalue add constraint seriesParamCategoryValueFk foreign key (parameterId) references dbo.seriesparameter;
alter table dbo.categoryseriesparamvalue add constraint seriesCatParamValueUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.categoryvalue add constraint observationCategoryValueFk foreign key (observationId) references dbo.observation;
alter table dbo.complexvalue add constraint observationComplexValueFk foreign key (observationId) references dbo.observation;
alter table dbo.compositeobservation add constraint observationChildFk foreign key (childObservationId) references dbo.observation;
alter table dbo.compositeobservation add constraint observationParentFK foreign key (observationId) references dbo.complexvalue;
alter table dbo.compositephenomenon add constraint observablePropertyChildFk foreign key (childObservablePropertyId) references dbo.observableproperty;
alter table dbo.compositephenomenon add constraint observablePropertyParentFk foreign key (parentObservablePropertyId) references dbo.observableproperty;
alter table dbo.countfeatparamvalue add constraint featParamCountValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.countparametervalue add constraint parameterCountValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.countseriesparamvalue add constraint seriesParamCountValueFk foreign key (parameterId) references dbo.seriesparameter;
alter table dbo.countvalue add constraint observationCountValueFk foreign key (observationId) references dbo.observation;
alter table dbo.featureofinterest add constraint featureFeatureTypeFk foreign key (featureOfInterestTypeId) references dbo.featureofinteresttype;
alter table dbo.featureofinterest add constraint featureCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo.featureofinterest add constraint featureCodespaceNameFk foreign key (codespaceName) references dbo.codespace;
alter table dbo.featureparameter add constraint FK_4ps6yv41rwnbu3q0let2v7772 foreign key (featureOfInterestId) references dbo.featureofinterest;
alter table dbo.featurerelation add constraint featureOfInterestChildFk foreign key (childFeatureId) references dbo.featureofinterest;
alter table dbo.featurerelation add constraint featureOfInterestParentFk foreign key (parentFeatureId) references dbo.featureofinterest;
alter table dbo.geometryvalue add constraint observationGeometryValueFk foreign key (observationId) references dbo.observation;
alter table dbo.numericfeatparamvalue add constraint featParamNumericValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.numericfeatparamvalue add constraint quanfeatparamvalueUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.numericparametervalue add constraint parameterNumericValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.numericparametervalue add constraint quanParamValueUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.numericseriesparamvalue add constraint seriesParamNumericValueFk foreign key (parameterId) references dbo.seriesparameter;
alter table dbo.numericseriesparamvalue add constraint seriesQuanParamValueUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.numericvalue add constraint observationNumericValueFk foreign key (observationId) references dbo.observation;
alter table dbo.observableproperty add constraint obsPropCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo.observableproperty add constraint obsPropCodespaceNameFk foreign key (codespaceName) references dbo.codespace;
alter table dbo.observation add constraint observationSeriesFk foreign key (seriesId) references dbo.series;
alter table dbo.observation add constraint obsCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo.observation add constraint obsCodespaceNameFk foreign key (codespaceName) references dbo.codespace;
alter table dbo.observation add constraint observationUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.observationconstellation add constraint obsConstObsPropFk foreign key (observablePropertyId) references dbo.observableproperty;
alter table dbo.observationconstellation add constraint obsnConstProcedureFk foreign key (procedureId) references dbo.[procedure];
alter table dbo.observationconstellation add constraint obsConstObservationIypeFk foreign key (observationTypeId) references dbo.observationtype;
alter table dbo.observationconstellation add constraint obsConstOfferingFk foreign key (offeringId) references dbo.offering;
alter table dbo.offering add constraint offCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo.offering add constraint offCodespaceNameFk foreign key (codespaceName) references dbo.codespace;
alter table dbo.offeringallowedfeaturetype add constraint offeringFeatureTypeFk foreign key (featureOfInterestTypeId) references dbo.featureofinteresttype;
alter table dbo.offeringallowedfeaturetype add constraint FK_cu8nfsf9q5vsn070o2d3u6chg foreign key (offeringId) references dbo.offering;
alter table dbo.offeringallowedobservationtype add constraint offeringObservationTypeFk foreign key (observationTypeId) references dbo.observationtype;
alter table dbo.offeringallowedobservationtype add constraint FK_jehw0637hllvta9ao1tqdhrtm foreign key (offeringId) references dbo.offering;
alter table dbo.offeringhasrelatedfeature add constraint relatedFeatureOfferingFk foreign key (offeringId) references dbo.offering;
alter table dbo.offeringhasrelatedfeature add constraint offeringRelatedFeatureFk foreign key (relatedFeatureId) references dbo.relatedfeature;
alter table dbo.offeringrelation add constraint offeringChildFk foreign key (childOfferingId) references dbo.offering;
alter table dbo.offeringrelation add constraint offeringParenfFk foreign key (parentOfferingId) references dbo.offering;
alter table dbo.parameter add constraint FK_3v5iovcndi9w0hgh827hcvivw foreign key (observationId) references dbo.observation;
alter table dbo.profileobservation add constraint profileObsChildFk foreign key (childObservationId) references dbo.observation;
alter table dbo.profileobservation add constraint profileObsParentFK foreign key (observationId) references dbo.profilevalue;
alter table dbo.profilevalue add constraint observationProfileValueFk foreign key (observationId) references dbo.observation;
alter table dbo.profilevalue add constraint profileUnitFk foreign key (levelunitid) references dbo.unit;
alter table dbo.referencevalue add constraint observationRefValueFk foreign key (observationId) references dbo.observation;
alter table dbo.relatedfeature add constraint relatedFeatureFeatureFk foreign key (featureOfInterestId) references dbo.featureofinterest;
alter table dbo.relatedfeaturehasrole add constraint relatedFeatRelatedFeatRoleFk foreign key (relatedFeatureRoleId) references dbo.relatedfeaturerole;
alter table dbo.relatedfeaturehasrole add constraint FK_5fd921q6mnbkc57mgm5g4uyyn foreign key (relatedFeatureId) references dbo.relatedfeature;
alter table dbo.relatedobservation add constraint FK_g0f0mpuxn3co65uwud4pwxh4q foreign key (observationId) references dbo.observation;
alter table dbo.relatedobservation add constraint FK_m4nuof4x6w253biuu1r6ttnqc foreign key (relatedObservation) references dbo.observation;
alter table dbo.relatedseries add constraint relatedSeriesFk foreign key (relatedSeries) references dbo.series;
alter table dbo.resulttemplate add constraint resultTemplateOfferingIdx foreign key (offeringId) references dbo.offering;
alter table dbo.resulttemplate add constraint resultTemplateObsPropFk foreign key (observablePropertyId) references dbo.observableproperty;
alter table dbo.resulttemplate add constraint resultTemplateProcedureFk foreign key (procedureId) references dbo.[procedure];
alter table dbo.resulttemplate add constraint resultTemplateFeatureIdx foreign key (featureOfInterestId) references dbo.featureofinterest;
alter table dbo.sensorsystem add constraint procedureChildFk foreign key (childSensorId) references dbo.[procedure];
alter table dbo.sensorsystem add constraint procedureParenfFk foreign key (parentSensorId) references dbo.[procedure];
alter table dbo.series add constraint seriesFeatureFk foreign key (featureOfInterestId) references dbo.featureofinterest;
alter table dbo.series add constraint seriesObPropFk foreign key (observablePropertyId) references dbo.observableproperty;
alter table dbo.series add constraint seriesProcedureFk foreign key (procedureId) references dbo.[procedure];
alter table dbo.series add constraint seriesOfferingFk foreign key (offeringId) references dbo.offering;
alter table dbo.series add constraint seriesUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.series add constraint seriesCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo.series add constraint seriesCodespaceNameFk foreign key (codespaceName) references dbo.codespace;
alter table dbo.seriesreference add constraint seriesrefreffk foreign key (referenceseriesid) references dbo.series;
alter table dbo.seriesreference add constraint seriesrefseriesfk foreign key (seriesid) references dbo.series;
alter table dbo.swedataarrayvalue add constraint observationSweDataArrayValueFk foreign key (observationId) references dbo.observation;
alter table dbo.textfeatparamvalue add constraint featParamTextValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.textparametervalue add constraint parameterTextValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.textseriesparamvalue add constraint seriesParamTextValueFk foreign key (parameterId) references dbo.seriesparameter;
alter table dbo.textvalue add constraint observationTextValueFk foreign key (observationId) references dbo.observation;
alter table dbo.validproceduretime add constraint validProcedureTimeProcedureFk foreign key (procedureId) references dbo.[procedure];
alter table dbo.validproceduretime add constraint validProcProcDescFormatFk foreign key (procedureDescriptionFormatId) references dbo.proceduredescriptionformat;
alter table dbo.xmlfeatparamvalue add constraint featParamXmlValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.xmlparametervalue add constraint parameterXmlValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.xmlseriesparamvalue add constraint seriesParamXmlValueFk foreign key (parameterId) references dbo.seriesparameter;

DECLARE @ObjectName NVARCHAR(100);SELECT @ObjectName = ccu.CONSTRAINT_NAME FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu, INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc WHERE ccu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME AND ccu.TABLE_NAME='featureOfInterest' AND ccu.COLUMN_NAME='url';IF (OBJECT_ID(@ObjectName, 'UQ') IS NOT NULL) BEGIN EXEC('ALTER TABLE dbo.featureOfInterest DROP CONSTRAINT ' + @ObjectName); END; 
CREATE UNIQUE NONCLUSTERED INDEX featureOfInterest_url ON dbo.featureOfInterest(url)WHERE url IS NOT NULL;
DECLARE @ObjectName2 NVARCHAR(100);SELECT @ObjectName2 = ccu.CONSTRAINT_NAME FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu, INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc WHERE ccu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME AND ccu.TABLE_NAME='featureOfInterest' AND ccu.COLUMN_NAME='identifier';IF (OBJECT_ID(@ObjectName2, 'UQ') IS NOT NULL) BEGIN EXEC('ALTER TABLE dbo.featureOfInterest DROP CONSTRAINT ' + @ObjectName2); END; 
CREATE UNIQUE NONCLUSTERED INDEX featureOfInterest_identifier ON dbo.featureOfInterest(identifier)WHERE identifier IS NOT NULL;
DECLARE @ObjectName3 NVARCHAR(100);SELECT @ObjectName3 = ccu.CONSTRAINT_NAME FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu, INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc WHERE ccu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME AND ccu.TABLE_NAME='observation' AND ccu.COLUMN_NAME='identifier';IF (OBJECT_ID(@ObjectName3, 'UQ') IS NOT NULL) BEGIN EXEC('ALTER TABLE dbo.observation DROP CONSTRAINT ' + @ObjectName3); END; 
CREATE UNIQUE NONCLUSTERED INDEX observation_identifier ON dbo.observation(identifier)WHERE identifier IS NOT NULL;
DECLARE @ObjectName4 NVARCHAR(100);SELECT @ObjectName4 = ccu.CONSTRAINT_NAME FROM INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu, INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc WHERE ccu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME AND ccu.TABLE_NAME='series' AND ccu.COLUMN_NAME='identifier';IF (OBJECT_ID(@ObjectName4, 'UQ') IS NOT NULL) BEGIN EXEC('ALTER TABLE dbo.series DROP CONSTRAINT ' + @ObjectName4); END; 
CREATE UNIQUE NONCLUSTERED INDEX observation_identifier ON dbo.observation(identifier)WHERE identifier IS NOT NULL;