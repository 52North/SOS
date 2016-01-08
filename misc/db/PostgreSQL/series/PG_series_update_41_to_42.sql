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

-- Can also be required in existing SOS 4.2.0-SNAPSHOT versions

-- update numeric columns to double
ALTER TABLE public.numericvalue ALTER COLUMN value TYPE double precision;
ALTER TABLE public.series ALTER COLUMN firstnumericvalue TYPE double precision;
ALTER TABLE public.series ALTER COLUMN lastnumericvalue TYPE double precision;

-- add published flag to series table
ALTER TABLE public.series ADD published char(1) NOT NULL default 'T' check (published in ('T','F'));

-- Required to update from SOS 4.1 to 4.2

-- update observation table
ALTER TABLE public.observation RENAME COLUMN codespaceid TO codespace;
ALTER TABLE public.observation ADD COLUMN name varchar(255);
ALTER TABLE public.observation ADD COLUMN codespacename int8;
ALTER TABLE public.observation add constraint obsCodespaceNameFk foreign key (codespacename) references public.codespace;

-- update offering table
ALTER TABLE public.offering ADD COLUMN codespace int8;
ALTER TABLE public.offering ADD COLUMN codespacename int8;
ALTER TABLE public.offering ADD COLUMN description varchar(255);
ALTER TABLE public.offering add constraint offCodespaceIdentifierFk foreign key (codespace) references public.codespace;
ALTER TABLE public.offering add constraint offCodespaceNameFk foreign key (codespacename) references public.codespace;

-- update procedure table
ALTER TABLE public."procedure" ADD COLUMN codespace int8;
ALTER TABLE public."procedure" ADD COLUMN name varchar(255);
ALTER TABLE public."procedure" ADD COLUMN codespacename int8;
ALTER TABLE public."procedure" ADD COLUMN description varchar(255);
ALTER TABLE public."procedure" add constraint procCodespaceIdentifierFk foreign key (codespace) references public.codespace;
ALTER TABLE public."procedure" add constraint procCodespaceNameFk foreign key (codespacename) references public.codespace;

-- update observaleProperty table
ALTER TABLE public.observableproperty ADD COLUMN codespace int8;
ALTER TABLE public.observableproperty ADD COLUMN name varchar(255);
ALTER TABLE public.observableproperty ADD COLUMN codespacename int8;
ALTER TABLE public.observableproperty add constraint obsPropCodespaceIdentifierFk foreign key (codespace) references public.codespace;
ALTER TABLE public.observableproperty add constraint obsPropCodespaceNameFk foreign key (codespacename) references public.codespace;

-- update featureOfInterest table
ALTER TABLE public.featureofinterest RENAME COLUMN codespaceid TO codespace;
ALTER TABLE public.featureofinterest ALTER COLUMN name TYPE varchar(255);
ALTER TABLE public.featureofinterest ADD COLUMN codespacename int8;
ALTER TABLE public.featureofinterest ADD COLUMN description varchar(255);
ALTER TABLE public.featureofinterest add constraint featureCodespaceNameFk foreign key (codespacename) references public.codespace;

-- create multiligualism tables (optional, required for multiligualism support)

-- i18n featureOfInterest
create table public.i18nfeatureOfInterest (id int8 not null, objectId int8 not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
ALTER TABLE public.i18nfeatureOfInterest add constraint i18nFeatureIdentity unique (objectId, locale);
create index i18nFeatureIdx on public.i18nfeatureOfInterest (objectId);
ALTER TABLE public.i18nfeatureOfInterest add constraint i18nFeatureFeatureFk foreign key (objectId) references public.featureOfInterest;
create sequence public.i18nfeatureOfInterestId_seq;

-- i18n observableProperty
create table public.i18nobservableProperty (id int8 not null, objectId int8 not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
ALTER TABLE public.i18nobservableProperty add constraint i18nobsPropIdentity unique (objectId, locale);
create index i18nObsPropIdx on public.i18nobservableProperty (objectId);
ALTER TABLE public.i18nobservableProperty add constraint i18nObsPropObsPropFk foreign key (objectId) references observableProperty;
create sequence public.i18nObsPropId_seq;

-- i18n offering
create table public.i18noffering (id int8 not null, objectId int8 not null, locale varchar(255) not null, name varchar(255), description varchar(255), primary key (id));
alter table public.i18noffering add constraint i18nOfferingIdentity unique (objectId, locale);
create index i18nOfferingIdx on public.i18noffering (objectId);
alter table public.i18noffering add constraint i18nOfferingOfferingFk foreign key (objectId) references public.offering;
create sequence public.i18nOfferingId_seq;

-- i18n procedure
create table public.i18nprocedure (id int8 not null, objectId int8 not null, locale varchar(255) not null, name varchar(255), description varchar(255), shortname varchar(255), longname varchar(255), primary key (id));
alter table public.i18nprocedure add constraint i18nProcedureIdentity unique (objectId, locale);
create index i18nProcedureIdx on public.i18nprocedure (objectId);
alter table public.i18nprocedure add constraint i18nProcedureProcedureFk foreign key (objectId) references public."procedure";
create sequence public.i18nProcedureId_seq;