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
alter table public."procedure" add column typeOf int8;
alter table public."procedure" add column isType char(1) default 'F' check(isType in ('T','F'));
alter table public."procedure" add column isAggregation char(1) default 'T' check(isAggregation in ('T','F'));
alter table public."procedure" add column mobile char(1) default 'F' check(mobile in ('T','F'));
alter table public."procedure" add column insitu char(1) default 'T' check(insitu in ('T','F'));
create table public.booleanfeatparamvalue (parameterId int8 not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table public.booleanparametervalue (parameterId int8 not null, value char(1), primary key (parameterId), check (value in ('T','F')));
create table public.categoryfeatparamvalue (parameterId int8 not null, value varchar(255), unitId int8, primary key (parameterId));
create table public.categoryparametervalue (parameterId int8 not null, value varchar(255), unitId int8, primary key (parameterId));
alter table public.categoryvalue add column identifier varchar(255);
alter table public.categoryvalue add column name varchar(255);
alter table public.categoryvalue add column description varchar(255);
create table public.complexvalue (observationId int8 not null, primary key (observationId));
create table public.compositeobservation (observationId int8 not null, childObservationId int8 not null, primary key (observationId, childObservationId));
create table public.countfeatparamvalue (parameterId int8 not null, value int4, primary key (parameterId));
create table public.countparametervalue (parameterId int8 not null, value int4, primary key (parameterId));
create table public.featureparameter (parameterId int8 not null, featureOfInterestId int8 not null, name varchar(255) not null, primary key (parameterId));
create table public.numericfeatparamvalue (parameterId int8 not null, value float8, unitId int8, primary key (parameterId));
create table public.numericparametervalue (parameterId int8 not null, value float8, unitId int8, primary key (parameterId));
alter table public.observableproperty add column hiddenChild char(1) default 'F' not null check(hiddenChild in ('T','F'));
alter table public.observation add column child char(1) default 'F' not null check(child in ('T','F'));
alter table public.observation add column parent char(1) default 'F' not null check(parent in ('T','F'));
alter table public.observationconstellation add column disabled char(1) default 'F' not null check(disabled in ('T','F'));
create table public.offeringrelation (parentOfferingId int8 not null, childOfferingId int8 not null, primary key (childOfferingId, parentOfferingId));
alter table public.parameter add column name varchar(255) not null;
create table public.profileobservation (observationId int8 not null, childObservationId int8 not null, primary key (observationId, childObservationId));
create table public.profilevalue (observationId int8 not null, fromlevel float8, tolevel float8, levelunitid int8, primary key (observationId));
create table public.relatedobservation (relatedObservationId int8 not null, observationId int8, relatedObservation int8, role varchar(255), relatedUrl varchar(255), primary key (relatedObservationId));
alter table public.series add column hiddenChild char(1) default 'F' not null check(hiddenChild in ('T','F'));
alter table public.series add column identifier varchar(255);
alter table public.series add column codespace int8;
alter table public.series add column name varchar(255);
alter table public.series add column codespaceName int8;
alter table public.series add column description varchar(255);
alter table public.series add column seriesType varchar(255);
create table public.textfeatparamvalue (parameterId int8 not null, value varchar(255), primary key (parameterId));
create table public.textparametervalue (parameterId int8 not null, value varchar(255), primary key (parameterId));
alter table public.textvalue add column identifier varchar(255);
alter table public.textvalue add column name varchar(255);
alter table public.textvalue add column description varchar(255);
alter table public.unit add column name varchar(255);
alter table public.unit add column link varchar(255);
create table public.xmlfeatparamvalue (parameterId int8 not null, value text, primary key (parameterId));
create table public.xmlparametervalue (parameterId int8 not null, value text, primary key (parameterId));
create index booleanFeatParamIdx on public.booleanfeatparamvalue (value);
create index booleanParamIdx on public.booleanparametervalue (value);
create index categoryFeatParamIdx on public.categoryfeatparamvalue (value);
create index categoryParamIdx on public.categoryparametervalue (value);
create index countFeatParamIdx on public.countfeatparamvalue (value);
create index countParamIdx on public.countparametervalue (value);
create index featureGeomIdx on public.featureofinterest USING GIST (geom);
create index featParamNameIdx on public.featureparameter (name);
create index quantityFeatParamIdx on public.numericfeatparamvalue (value);
create index quantityParamIdx on public.numericparametervalue (value);
create index samplingGeomIdx on public.observation USING GIST (samplingGeometry);
create index paramNameIdx on public.parameter (name);
create index relatedObsObsIdx on public.relatedobservation (observationId);
alter table public.series add constraint seriesIdentifierUK unique (identifier);
create index textFeatParamIdx on public.textfeatparamvalue (value);
create index textParamIdx on public.textparametervalue (value);
alter table public."procedure" add constraint typeOfFk foreign key (typeOf) references public."procedure";
alter table public.booleanfeatparamvalue add constraint featParamBooleanValueFk foreign key (parameterId) references public.featureparameter;
alter table public.booleanparametervalue add constraint parameterBooleanValueFk foreign key (parameterId) references public.parameter;
alter table public.categoryfeatparamvalue add constraint featParamCategoryValueFk foreign key (parameterId) references public.featureparameter;
alter table public.categoryfeatparamvalue add constraint catfeatparamvalueUnitFk foreign key (unitId) references public.unit;
alter table public.categoryparametervalue add constraint parameterCategoryValueFk foreign key (parameterId) references public.parameter;
alter table public.categoryparametervalue add constraint catParamValueUnitFk foreign key (unitId) references public.unit;
alter table public.complexvalue add constraint observationComplexValueFk foreign key (observationId) references public.observation;
alter table public.compositeobservation add constraint observationChildFk foreign key (childObservationId) references public.observation;
alter table public.compositeobservation add constraint observationParentFK foreign key (observationId) references public.complexvalue;
alter table public.countfeatparamvalue add constraint featParamCountValueFk foreign key (parameterId) references public.featureparameter;
alter table public.countparametervalue add constraint parameterCountValueFk foreign key (parameterId) references public.parameter;
alter table public.featureparameter add constraint FK_4ps6yv41rwnbu3q0let2v7foreign key (featureOfInterestId) references public.featureofinterest;
alter table public.numericfeatparamvalue add constraint featParamNumericValueFk foreign key (parameterId) references public.featureparameter;
alter table public.numericfeatparamvalue add constraint quanfeatparamvalueUnitFk foreign key (unitId) references public.unit;
alter table public.numericparametervalue add constraint parameterNumericValueFk foreign key (parameterId) references public.parameter;
alter table public.numericparametervalue add constraint quanParamValueUnitFk foreign key (unitId) references public.unit;
alter table public.offeringrelation add constraint offeringChildFk foreign key (childOfferingId) references public.offering;
alter table public.offeringrelation add constraint offeringParenfFk foreign key (parentOfferingId) references public.offering;
alter table public.parameter add constraint FK_3v5iovcndi9w0hgh827hcvivw foreign key (observationId) references public.observation;
alter table public.profileobservation add constraint profileObsChildFk foreign key (childObservationId) references public.observation;
alter table public.profileobservation add constraint profileObsParentFK foreign key (observationId) references public.profilevalue;
alter table public.profilevalue add constraint observationProfileValueFk foreign key (observationId) references public.observation;
alter table public.profilevalue add constraint profileUnitFk foreign key (levelunitid) references public.unit;
alter table public.relatedobservation add constraint FK_g0f0mpuxn3co65uwud4pwxh4q foreign key (observationId) references public.observation;
alter table public.relatedobservation add constraint FK_m4nuof4x6w253biuu1r6ttnqc foreign key (relatedObservation) references public.observation;
alter table public.series add constraint seriesCodespaceIdentifierFk foreign key (codespace) references public.codespace;
alter table public.series add constraint seriesCodespaceNameFk foreign key (codespaceName) references public.codespace;
alter table public.textfeatparamvalue add constraint featParamTextValueFk foreign key (parameterId) references public.featureparameter;
alter table public.textparametervalue add constraint parameterTextValueFk foreign key (parameterId) references public.parameter;
alter table public.xmlfeatparamvalue add constraint featParamXmlValueFk foreign key (parameterId) references public.featureparameter;
alter table public.xmlparametervalue add constraint parameterXmlValueFk foreign key (parameterId) references public.parameter;
ALTER TABLE public.featureofinterest ALTER hibernatediscriminator TYPE character varying(255);
ALTER TABLE public.featureofinterest ALTER hibernatediscriminator DROP NOT NULL;
UPDATE public.featureofinterest SET hibernatediscriminator = null;
ALTER TABLE public.observableproperty DROP COLUMN hibernatediscriminator;
create sequence public.relatedObservationId_seq