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

-- Add and update other columns
alter table dbo.[procedure] add column typeOf int8;
alter table dbo.[procedure] add column isType char(1) default 'F' check(isType in ('T','F'));
alter table dbo.[procedure] add column isAggregation char(1) default 'T' check(isAggregation in ('T','F'));
alter table dbo.[procedure] add column mobile char(1) default 'F' check(mobile in ('T','F'));
alter table dbo.[procedure] add column insitu char(1) default 'T' check(insitu in ('T','F'));
create table dbo.booleanfeatparamvalue (parameterId bigint not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table dbo.booleanparametervalue (parameterId bigint not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table dbo.categoryfeatparamvalue (parameterId bigint not null, value varchar(255), unitId bigint, primary key (parameterId));
create table dbo.categoryparametervalue (parameterId bigint not null, value varchar(255), unitId bigint, primary key (parameterId));
alter table dbo.categoryvalue add column identifier varchar(255);
alter table dbo.categoryvalue add column name varchar(255);
alter table dbo.categoryvalue add column description varchar(255);
create table dbo.complexvalue (observationId bigint not null, primary key (observationId));
create table dbo.compositeobservation (observationId bigint not null, childObservationId bigint not null, primary key (observationId, childObservationId));
create table dbo.countfeatparamvalue (parameterId bigint not null, value int, primary key (parameterId));
create table dbo.countparametervalue (parameterId bigint not null, value int, primary key (parameterId));
create table dbo.featureparameter (parameterId bigint not null, featureOfInterestId bigint not null, name varchar(255) not null, primary key (parameterId));
create table dbo.numericfeatparamvalue (parameterId bigint not null, value float, unitId bigint, primary key (parameterId));
create table dbo.numericparametervalue (parameterId bigint not null, value float, unitId bigint, primary key (parameterId));
alter table dbo.observableproperty add column hiddenChild char(1) default 'F' not null check(hiddenChild in ('T','F'));
alter table dbo.observation add column child char(1) default 'F' not null check(child in ('T','F'));
alter table dbo.observation add column parent char(1) default 'F' not null check(parent in ('T','F'));
alter table dbo.observationconstellation add column disabled char(1) default 'F' not null check(disabled in ('T','F'));
create table dbo.offeringrelation (parentOfferingId bigint not null, childOfferingId bigint not null, primary key (childOfferingId, parentOfferingId));
alter table dbo.parameter add column name varchar(255) not null;
create table dbo.profileobservation (observationId bigint not null, childObservationId bigint not null, primary key (observationId, childObservationId));
create table dbo.profilevalue (observationId bigint not null, fromlevel float, tolevel float, levelunitid bigint, primary key (observationId));
create table dbo.relatedobservation (relatedObservationId bigint not null, observationId bigint, relatedObservation bigint, role varchar(255), relatedUrl varchar(255), primary key (relatedObservationId));
alter table dbo.series add column hiddenChild char(1) default 'F' not null check(hiddenChild in ('T','F'));
alter table dbo.series add column identifier varchar(255);
alter table dbo.series add column codespace int8;
alter table dbo.series add column name varchar(255);
alter table dbo.series add column codespaceName int8;
alter table dbo.series add column description varchar(255);
alter table dbo.series add column seriesType varchar(255);
create table dbo.textfeatparamvalue (parameterId bigint not null, value varchar(255), primary key (parameterId));
create table dbo.textparametervalue (parameterId bigint not null, value varchar(255), primary key (parameterId));
alter table dbo.textvalue add column identifier varchar(255);
alter table dbo.textvalue add column name varchar(255);
alter table dbo.textvalue add column description varchar(255);
alter table dbo.unit add column name varchar(255);
alter table dbo.unit add column link varchar(255);
create table dbo.xmlfeatparamvalue (parameterId bigint not null, value varchar(max), primary key (parameterId));
create table dbo.xmlparametervalue (parameterId bigint not null, value varchar(max), primary key (parameterId));
create index booleanFeatParamIdx on dbo.booleanfeatparamvalue (value);
create index booleanParamIdx on dbo.booleanparametervalue (value);
create index categoryFeatParamIdx on dbo.categoryfeatparamvalue (value);
create index categoryParamIdx on dbo.categoryparametervalue (value);
create index countFeatParamIdx on dbo.countfeatparamvalue (value);
create index countParamIdx on dbo.countparametervalue (value);
create index featureGeomIdx on dbo.featureofinterest USING GIST (geom);
create index featParamNameIdx on dbo.featureparameter (name);
create index quantityFeatParamIdx on dbo.numericfeatparamvalue (value);
create index quantityParamIdx on dbo.numericparametervalue (value);
create index samplingGeomIdx on dbo.observation USING GIST (samplingGeometry);
create index paramNameIdx on dbo.parameter (name);
create index relatedObsObsIdx on dbo.relatedobservation (observationId);
alter table dbo.series add constraint seriesIdentifierUK unique (identifier);
create index textFeatParamIdx on dbo.textfeatparamvalue (value);
create index textParamIdx on dbo.textparametervalue (value);
alter table dbo.[procedure] add constraint typeOfFk foreign key (typeOf) references dbo.[procedure];
alter table dbo.booleanfeatparamvalue add constraint featParamBooleanValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.booleanparametervalue add constraint parameterBooleanValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.categoryfeatparamvalue add constraint featParamCategoryValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.categoryfeatparamvalue add constraint catfeatparamvalueUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.categoryparametervalue add constraint parameterCategoryValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.categoryparametervalue add constraint catParamValueUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.complexvalue add constraint observationComplexValueFk foreign key (observationId) references dbo.observation;
alter table dbo.compositeobservation add constraint observationChildFk foreign key (childObservationId) references dbo.observation;
alter table dbo.compositeobservation add constraint observationParentFK foreign key (observationId) references dbo.complexvalue;
alter table dbo.countfeatparamvalue add constraint featParamCountValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.countparametervalue add constraint parameterCountValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.featureparameter add constraint FK_4ps6yv41rwnbu3q0let2v7foreign create index (featureOfInterestId) references dbo.featureofinterest;
alter table dbo.numericfeatparamvalue add constraint featParamNumericValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.numericfeatparamvalue add constraint quanfeatparamvalueUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.numericparametervalue add constraint parameterNumericValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.numericparametervalue add constraint quanParamValueUnitFk foreign key (unitId) references dbo.unit;
alter table dbo.offeringrelation add constraint offeringChildFk foreign key (childOfferingId) references dbo.offering;
alter table dbo.offeringrelation add constraint offeringParenfFk foreign key (parentOfferingId) references dbo.offering;
alter table dbo.parameter add constraint FK_3v5iovcndi9w0hgh827hcvivw foreign key (observationId) references dbo.observation;
alter table dbo.profileobservation add constraint profileObsChildFk foreign key (childObservationId) references dbo.observation;
alter table dbo.profileobservation add constraint profileObsParentFK foreign key (observationId) references dbo.profilevalue;
alter table dbo.profilevalue add constraint observationProfileValueFk foreign key (observationId) references dbo.observation;
alter table dbo.profilevalue add constraint profileUnitFk foreign key (levelunitid) references dbo.unit;
alter table dbo.relatedobservation add constraint FK_g0f0mpuxn3co65uwud4pwxh4q foreign key (observationId) references dbo.observation;
alter table dbo.relatedobservation add constraint FK_m4nuof4x6w253biuu1r6ttnqc foreign key (relatedObservation) references dbo.observation;
alter table dbo.series add constraint seriesCodespaceIdentifierFk foreign key (codespace) references dbo.codespace;
alter table dbo.series add constraint seriesCodespaceNameFk foreign key (codespaceName) references dbo.codespace;
alter table dbo.textfeatparamvalue add constraint featParamTextValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.textparametervalue add constraint parameterTextValueFk foreign key (parameterId) references dbo.parameter;
alter table dbo.xmlfeatparamvalue add constraint featParamXmlValueFk foreign key (parameterId) references dbo.featureparameter;
alter table dbo.xmlparametervalue add constraint parameterXmlValueFk foreign key (parameterId) references dbo.parameter;
ALTER TABLE dbo.featureofinterest ALTER hibernatediscriminator TYPE character varying(255);
ALTER TABLE dbo.featureofinterest ALTER hibernatediscriminator DROP NOT NULL;
UPDATE dbo.featureofinterest SET hibernatediscriminator = null;
ALTER TABLE dbo.observableproperty DROP COLUMN hibernatediscriminator;
create sequence dbo.relatedObservationId_seq