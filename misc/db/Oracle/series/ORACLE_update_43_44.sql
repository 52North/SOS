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
alter table oracle."procedure" add column typeOf int8;
alter table oracle."procedure" add column isType char(1) default 'F' check(isType in ('T','F'));
alter table oracle."procedure" add column isAggregation char(1) default 'T' check(isAggregation in ('T','F'));
alter table oracle."procedure" add column mobile char(1) default 'F' check(mobile in ('T','F'));
alter table oracle."procedure" add column insitu char(1) default 'T' check(insitu in ('T','F'));
create table oracle.booleanfeatparamvalue (parameterId number(19) not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table oracle.booleanparametervalue (parameterId number(19) not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table oracle.categoryfeatparamvalue (parameterId number(19) not null, value varchar2(255), unitId number(19), primary key (parameterId));
create table oracle.categoryparametervalue (parameterId number(19) not null, value varchar2(255), unitId number(19), primary key (parameterId));
alter table oracle.categoryvalue add column identifier varchar(255);
alter table oracle.categoryvalue add column name varchar(255);
alter table oracle.categoryvalue add column description varchar(255);
create table oracle.complexvalue (observationId number(19) not null, primary key (observationId));
create table oracle.compositeobservation (observationId number(19) not null, childObservationId number(19) not null, primary key (observationId, childObservationId));
create table oracle.countfeatparamvalue (parameterId number(19) not null, value number(10), primary key (parameterId));
create table oracle.countparametervalue (parameterId number(19) not null, value number(10), primary key (parameterId));
create table oracle.featureparameter (parameterId number(19) not null, featureOfInterestId number(19) not null, name varchar2(255) not null, primary key (parameterId));
create table oracle.numericfeatparamvalue (parameterId number(19) not null, value binary_double, unitId number(19), primary key (parameterId));
create table oracle.numericparametervalue (parameterId number(19) not null, value binary_double, unitId number(19), primary key (parameterId));
alter table oracle.observableproperty add column hiddenChild char(1) default 'F' not null check(hiddenChild in ('T','F'));
alter table oracle.observation add column child char(1) default 'F' not null check(child in ('T','F'));
alter table oracle.observation add column parent char(1) default 'F' not null check(parent in ('T','F'));
alter table oracle.observationconstellation add column disabled char(1) default 'F' not null check(disabled in ('T','F'));
create table oracle.offeringrelation (parentOfferingId number(19) not null, childOfferingId number(19) not null, primary key (childOfferingId, parentOfferingId));
alter table oracle.parameter add column name varchar(255) not null;
create table oracle.profileobservation (observationId number(19) not null, childObservationId number(19) not null, primary key (observationId, childObservationId));
create table oracle.profilevalue (observationId number(19) not null, fromlevel binary_double, tolevel binary_double, levelunitid number(19), primary key (observationId));
create table oracle.relatedobservation (relatedObservationId number(19) not null, observationId number(19), relatedObservation number(19), role varchar2(255), relatedUrl varchar2(255), primary key (relatedObservationId));
alter table oracle.series add column hiddenChild char(1) default 'F' not null check(hiddenChild in ('T','F'));
alter table oracle.series add column identifier varchar(255);
alter table oracle.series add column codespace int8;
alter table oracle.series add column name varchar(255);
alter table oracle.series add column codespaceName int8;
alter table oracle.series add column description varchar(255);
alter table oracle.series add column seriesType varchar(255);
create table oracle.textfeatparamvalue (parameterId number(19) not null, value varchar2(255), primary key (parameterId));
create table oracle.textparametervalue (parameterId number(19) not null, value varchar2(255), primary key (parameterId));
alter table oracle.textvalue add column identifier varchar(255);
alter table oracle.textvalue add column name varchar(255);
alter table oracle.textvalue add column description varchar(255);
alter table oracle.unit add column name varchar(255);
alter table oracle.unit add column link varchar(255);
create table oracle.xmlfeatparamvalue (parameterId number(19) not null, value clob, primary key (parameterId));
create table oracle.xmlparametervalue (parameterId number(19) not null, value clob, primary key (parameterId));
create index booleanFeatParamIdx on oracle.booleanfeatparamvalue (value);
create index booleanParamIdx on oracle.booleanparametervalue (value);
create index categoryFeatParamIdx on oracle.categoryfeatparamvalue (value);
create index categoryParamIdx on oracle.categoryparametervalue (value);
create index countFeatParamIdx on oracle.countfeatparamvalue (value);
create index countParamIdx on oracle.countparametervalue (value);
create index featureGeomIdx on oracle.featureofinterest USING GIST (geom);
create index featParamNameIdx on oracle.featureparameter (name);
create index quantityFeatParamIdx on oracle.numericfeatparamvalue (value);
create index quantityParamIdx on oracle.numericparametervalue (value);
create index samplingGeomIdx on oracle.observation USING GIST (samplingGeometry);
create index paramNameIdx on oracle.parameter (name);
create index relatedObsObsIdx on oracle.relatedobservation (observationId);
alter table oracle.series add constraint seriesIdentifierUK unique (identifier);
create index textFeatParamIdx on oracle.textfeatparamvalue (value);
create index textParamIdx on oracle.textparametervalue (value);
alter table oracle."procedure" add constraint typeOfFk foreign key (typeOf) references oracle."procedure";
alter table oracle.booleanfeatparamvalue add constraint featParamBooleanValueFk foreign key (parameterId) references oracle.featureparameter;
alter table oracle.booleanparametervalue add constraint parameterBooleanValueFk foreign key (parameterId) references oracle.parameter;
alter table oracle.categoryfeatparamvalue add constraint featParamCategoryValueFk foreign key (parameterId) references oracle.featureparameter;
alter table oracle.categoryfeatparamvalue add constraint catfeatparamvalueUnitFk foreign key (unitId) references oracle.unit;
alter table oracle.categoryparametervalue add constraint parameterCategoryValueFk foreign key (parameterId) references oracle.parameter;
alter table oracle.categoryparametervalue add constraint catParamValueUnitFk foreign key (unitId) references oracle.unit;
alter table oracle.complexvalue add constraint observationComplexValueFk foreign key (observationId) references oracle.observation;
alter table oracle.compositeobservation add constraint observationChildFk foreign key (childObservationId) references oracle.observation;
alter table oracle.compositeobservation add constraint observationParentFK foreign key (observationId) references oracle.complexvalue;
alter table oracle.countfeatparamvalue add constraint featParamCountValueFk foreign key (parameterId) references oracle.featureparameter;
alter table oracle.countparametervalue add constraint parameterCountValueFk foreign key (parameterId) references oracle.parameter;
alter table oracle.featureparameter add constraint FK_4ps6yv41rwnbu3q0let2v7foreign create index (featureOfInterestId) references oracle.featureofinterest;
alter table oracle.numericfeatparamvalue add constraint featParamNumericValueFk foreign key (parameterId) references oracle.featureparameter;
alter table oracle.numericfeatparamvalue add constraint quanfeatparamvalueUnitFk foreign key (unitId) references oracle.unit;
alter table oracle.numericparametervalue add constraint parameterNumericValueFk foreign key (parameterId) references oracle.parameter;
alter table oracle.numericparametervalue add constraint quanParamValueUnitFk foreign key (unitId) references oracle.unit;
alter table oracle.offeringrelation add constraint offeringChildFk foreign key (childOfferingId) references oracle.offering;
alter table oracle.offeringrelation add constraint offeringParenfFk foreign key (parentOfferingId) references oracle.offering;
alter table oracle.parameter add constraint FK_3v5iovcndi9w0hgh827hcvivw foreign key (observationId) references oracle.observation;
alter table oracle.profileobservation add constraint profileObsChildFk foreign key (childObservationId) references oracle.observation;
alter table oracle.profileobservation add constraint profileObsParentFK foreign key (observationId) references oracle.profilevalue;
alter table oracle.profilevalue add constraint observationProfileValueFk foreign key (observationId) references oracle.observation;
alter table oracle.profilevalue add constraint profileUnitFk foreign key (levelunitid) references oracle.unit;
alter table oracle.relatedobservation add constraint FK_g0f0mpuxn3co65uwud4pwxh4q foreign key (observationId) references oracle.observation;
alter table oracle.relatedobservation add constraint FK_m4nuof4x6w253biuu1r6ttnqc foreign key (relatedObservation) references oracle.observation;
alter table oracle.series add constraint seriesCodespaceIdentifierFk foreign key (codespace) references oracle.codespace;
alter table oracle.series add constraint seriesCodespaceNameFk foreign key (codespaceName) references oracle.codespace;
alter table oracle.textfeatparamvalue add constraint featParamTextValueFk foreign key (parameterId) references oracle.featureparameter;
alter table oracle.textparametervalue add constraint parameterTextValueFk foreign key (parameterId) references oracle.parameter;
alter table oracle.xmlfeatparamvalue add constraint featParamXmlValueFk foreign key (parameterId) references oracle.featureparameter;
alter table oracle.xmlparametervalue add constraint parameterXmlValueFk foreign key (parameterId) references oracle.parameter;
ALTER TABLE oracle.featureofinterest ALTER hibernatediscriminator TYPE character varying(255);
ALTER TABLE oracle.featureofinterest ALTER hibernatediscriminator DROP NOT NULL;
UPDATE oracle.featureofinterest SET hibernatediscriminator = null;
ALTER TABLE oracle.observableproperty DROP COLUMN hibernatediscriminator;
create sequence oracle.relatedObservationId_seq