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
alter table sos.`procedure` add column typeOf int8;
alter table sos.`procedure` add column isType char(1) default 'F' check(isType in ('T','F'));
alter table sos.`procedure` add column isAggregation char(1) default 'T' check(isAggregation in ('T','F'));
alter table sos.`procedure` add column mobile char(1) default 'F' check(mobile in ('T','F'));
alter table sos.`procedure` add column insitu char(1) default 'T' check(insitu in ('T','F'));
create table sos.booleanfeatparamvalue (parameterId int8 not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table sos.booleanparametervalue (parameterId int8 not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table sos.categoryfeatparamvalue (parameterId int8 not null, value varchar(255), unitId int8, primary key (parameterId));
create table sos.categoryparametervalue (parameterId int8 not null, value varchar(255), unitId int8, primary key (parameterId));
alter table sos.categoryvalue add column identifier varchar(255);
alter table sos.categoryvalue add column name varchar(255);
alter table sos.categoryvalue add column description varchar(255);
create table sos.complexvalue (observationId int8 not null, primary key (observationId));
create table sos.compositeobservation (observationId int8 not null, childObservationId int8 not null, primary key (observationId, childObservationId));
create table sos.countfeatparamvalue (parameterId int8 not null, value int4, primary key (parameterId));
create table sos.countparametervalue (parameterId int8 not null, value int4, primary key (parameterId));
create table sos.featureparameter (parameterId int8 not null, featureOfInterestId int8 not null, name varchar(255) not null, primary key (parameterId));
create table sos.numericfeatparamvalue (parameterId int8 not null, value float8, unitId int8, primary key (parameterId));
create table sos.numericparametervalue (parameterId int8 not null, value float8, unitId int8, primary key (parameterId));
alter table sos.observableproperty add column hiddenChild char(1) default 'F' not null check(hiddenChild in ('T','F'));
alter table sos.observation add column child char(1) default 'F' not null check(child in ('T','F'));
alter table sos.observation add column parent char(1) default 'F' not null check(parent in ('T','F'));
alter table sos.observationconstellation add column disabled char(1) default 'F' not null check(disabled in ('T','F'));
create table sos.offeringrelation (parentOfferingId int8 not null, childOfferingId int8 not null, primary key (childOfferingId, parentOfferingId));
alter table sos.parameter add column name varchar(255) not null;
create table sos.profileobservation (observationId int8 not null, childObservationId int8 not null, primary key (observationId, childObservationId));
create table sos.profilevalue (observationId int8 not null, fromlevel float8, tolevel float8, levelunitid int8, primary key (observationId));
create table sos.relatedobservation (relatedObservationId int8 not null, observationId int8, relatedObservation int8, role varchar(255), relatedUrl varchar(255), primary key (relatedObservationId));
alter table sos.series add column hiddenChild char(1) default 'F' not null check(hiddenChild in ('T','F'));
alter table sos.series add column identifier varchar(255);
alter table sos.series add column codespace int8;
alter table sos.series add column name varchar(255);
alter table sos.series add column codespaceName int8;
alter table sos.series add column description varchar(255);
alter table sos.series add column seriesType varchar(255);
create table sos.textfeatparamvalue (parameterId int8 not null, value varchar(255), primary key (parameterId));
create table sos.textparametervalue (parameterId int8 not null, value varchar(255), primary key (parameterId));
alter table sos.textvalue add column identifier varchar(255);
alter table sos.textvalue add column name varchar(255);
alter table sos.textvalue add column description varchar(255);
alter table sos.unit add column name varchar(255);
alter table sos.unit add column link varchar(255);
create table sos.xmlfeatparamvalue (parameterId int8 not null, value longtext, primary key (parameterId));
create table sos.xmlparametervalue (parameterId int8 not null, value longtext, primary key (parameterId));
create index booleanFeatParamIdx on sos.booleanfeatparamvalue (value);
create index booleanParamIdx on sos.booleanparametervalue (value);
create index categoryFeatParamIdx on sos.categoryfeatparamvalue (value);
create index categoryParamIdx on sos.categoryparametervalue (value);
create index countFeatParamIdx on sos.countfeatparamvalue (value);
create index countParamIdx on sos.countparametervalue (value);
create index featureGeomIdx on sos.featureofinterest USING GIST (geom);
create index featParamNameIdx on sos.featureparameter (name);
create index quantityFeatParamIdx on sos.numericfeatparamvalue (value);
create index quantityParamIdx on sos.numericparametervalue (value);
create index samplingGeomIdx on sos.observation USING GIST (samplingGeometry);
create index paramNameIdx on sos.parameter (name);
create index relatedObsObsIdx on sos.relatedobservation (observationId);
alter table sos.series add constraint seriesIdentifierUK unique (identifier);
create index textFeatParamIdx on sos.textfeatparamvalue (value);
create index textParamIdx on sos.textparametervalue (value);
alter table sos.`procedure` add constraint typeOfFk foreign key (typeOf) references sos.`procedure`;
alter table sos.booleanfeatparamvalue add constraint featParamBooleanValueFk foreign key (parameterId) references sos.featureparameter;
alter table sos.booleanparametervalue add constraint parameterBooleanValueFk foreign key (parameterId) references sos.parameter;
alter table sos.categoryfeatparamvalue add constraint featParamCategoryValueFk foreign key (parameterId) references sos.featureparameter;
alter table sos.categoryfeatparamvalue add constraint catfeatparamvalueUnitFk foreign key (unitId) references sos.unit;
alter table sos.categoryparametervalue add constraint parameterCategoryValueFk foreign key (parameterId) references sos.parameter;
alter table sos.categoryparametervalue add constraint catParamValueUnitFk foreign key (unitId) references sos.unit;
alter table sos.complexvalue add constraint observationComplexValueFk foreign key (observationId) references sos.observation;
alter table sos.compositeobservation add constraint observationChildFk foreign key (childObservationId) references sos.observation;
alter table sos.compositeobservation add constraint observationParentFK foreign key (observationId) references sos.complexvalue;
alter table sos.countfeatparamvalue add constraint featParamCountValueFk foreign key (parameterId) references sos.featureparameter;
alter table sos.countparametervalue add constraint parameterCountValueFk foreign key (parameterId) references sos.parameter;
alter table sos.featureparameter add constraint FK_4ps6yv41rwnbu3q0let2v7foreign key (featureOfInterestId) references sos.featureofinterest;
alter table sos.numericfeatparamvalue add constraint featParamNumericValueFk foreign key (parameterId) references sos.featureparameter;
alter table sos.numericfeatparamvalue add constraint quanfeatparamvalueUnitFk foreign key (unitId) references sos.unit;
alter table sos.numericparametervalue add constraint parameterNumericValueFk foreign key (parameterId) references sos.parameter;
alter table sos.numericparametervalue add constraint quanParamValueUnitFk foreign key (unitId) references sos.unit;
alter table sos.offeringrelation add constraint offeringChildFk foreign key (childOfferingId) references sos.offering;
alter table sos.offeringrelation add constraint offeringParenfFk foreign key (parentOfferingId) references sos.offering;
alter table sos.parameter add constraint FK_3v5iovcndi9w0hgh827hcvivw foreign key (observationId) references sos.observation;
alter table sos.profileobservation add constraint profileObsChildFk foreign key (childObservationId) references sos.observation;
alter table sos.profileobservation add constraint profileObsParentFK foreign key (observationId) references sos.profilevalue;
alter table sos.profilevalue add constraint observationProfileValueFk foreign key (observationId) references sos.observation;
alter table sos.profilevalue add constraint profileUnitFk foreign key (levelunitid) references sos.unit;
alter table sos.relatedobservation add constraint FK_g0f0mpuxn3co65uwud4pwxh4q foreign key (observationId) references sos.observation;
alter table sos.relatedobservation add constraint FK_m4nuof4x6w253biuu1r6ttnqc foreign key (relatedObservation) references sos.observation;
alter table sos.series add constraint seriesCodespaceIdentifierFk foreign key (codespace) references sos.codespace;
alter table sos.series add constraint seriesCodespaceNameFk foreign key (codespaceName) references sos.codespace;
alter table sos.textfeatparamvalue add constraint featParamTextValueFk foreign key (parameterId) references sos.featureparameter;
alter table sos.textparametervalue add constraint parameterTextValueFk foreign key (parameterId) references sos.parameter;
alter table sos.xmlfeatparamvalue add constraint featParamXmlValueFk foreign key (parameterId) references sos.featureparameter;
alter table sos.xmlparametervalue add constraint parameterXmlValueFk foreign key (parameterId) references sos.parameter;
ALTER TABLE sos.featureofinterest ALTER hibernatediscriminator TYPE character varying(255);
ALTER TABLE sos.featureofinterest ALTER hibernatediscriminator DROP NOT NULL;
UPDATE sos.featureofinterest SET hibernatediscriminator = null;
ALTER TABLE sos.observableproperty DROP COLUMN hibernatediscriminator;
call CreateSequence('sos.relatedObservationId_seq', 1, 1)